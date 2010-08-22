/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */

package signalproc.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import triana.types.VectorType;

/**
 * An utility class for importing binary data input streams
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */

public class ImportBinary {

    public static final int BYTE = 0;
    public static final int SHORT = 1;
    public static final int INT = 2;
    public static final int LONG = 3;
    public static final int FLOAT = 4;
    public static final int DOUBLE = 5;

    private static final int SHORT_BYTES = 2;
    private static final int INT_BYTES = 4;
    private static final int LONG_BYTES = 8;
    private static final int FLOAT_BYTES = 4;
    private static final int DOUBLE_BYTES = 8;


    /**
     * the stream data is being input from
     */
    protected InputStream instream;

    /**
     * the number of rows and columns in a data set
     */
    private int rows = -1;
    private int columns = -1;

    /**
     * the actual number of rows read in for the last data set
     */
    private int actualrows = 0;

    /**
     * the data type for each data field
     */
    private int datatype = BYTE;

    /**
     * a flag indicating whether it is one byte per column (or whether the number of bytes in a column is the same as
     * the data type bytes)
     */
    private boolean onebyte = false;

    /**
     * a flag indicating whether the data bytes are in reverse order
     */
    private boolean reverse = false;

    /**
     * the number of bytes read in for each each column field
     */
    private int colbytes = 1;

    /**
     * the index of the row in the row schema that is currently being read
     */
    private int rowindex = 0;

    /**
     * the offset of the current row
     */
    private int rowoffset = 0;

    /**
     * the data array rows are read into
     */
    private byte[] bytearray = new byte[0];


    public ImportBinary(InputStream instream) {
        this.instream = instream;
    }


    /**
     * @return the type of data being read in
     */
    public int getDataType() {
        return datatype;
    }

    /**
     * Sets the type of data being read in
     */
    public void setDataType(int datatype) {
        this.datatype = datatype;

        if (!onebyte) {
            colbytes = getDataBytes();
        }
    }

    /**
     * @return true is the number of bytes for each column field is the same as the number for the data type (false if
     *         it is one byte per column)
     */
    public boolean isOneBytePerColumn() {
        return onebyte;
    }

    /**
     * Sets whether each column is one byte (or alternatively whether the number of bytes in a field is the same as the
     * data type
     */
    public void setOneBytePerColumn(boolean state) {
        this.onebyte = state;

        if (onebyte) {
            colbytes = 1;
        } else {
            colbytes = getDataBytes();
        }
    }

    /**
     * @return true if the data is in reverse byte order
     */
    public boolean isReverseByteOrder() {
        return reverse;
    }

    /**
     * Sets whether the data is in reverse byte order
     */
    public void setReverseByteOrder(boolean reverse) {
        this.reverse = reverse;
    }


    /**
     * @return the number of rows in each data set, or -1 if unknown
     */
    public int getRows() {
        return rows;
    }


    /**
     * @return the actual number of rows read in for the last data set
     */
    public int getActualRows() {
        return actualrows;
    }

    /**
     * Sets the number of rows in each data set, or -1 if unknown
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * Sets the actual number of rows read in for the last data set
     */
    protected void setActualRows(int actualrows) {
        this.actualrows = actualrows;
    }

    /**
     * @return the number of columns in each data set
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Sets the number of columns in each data set
     */
    public void setColumns(int columns) {
        this.columns = columns;
    }


    /**
     * @return the number of bytes in the data type
     */
    private int getDataBytes() {
        if (datatype == BYTE) {
            return 1;
        } else if (datatype == SHORT) {
            return SHORT_BYTES;
        } else if (datatype == INT) {
            return INT_BYTES;
        } else if (datatype == LONG) {
            return LONG_BYTES;
        } else if (datatype == FLOAT) {
            return FLOAT_BYTES;
        } else if (datatype == DOUBLE) {
            ;
        }
        return DOUBLE_BYTES;
    }


    /**
     * Moves to the next data set in the input stream
     */
    public void nextDataSet() throws IOException {
        rowindex = 0;
        rowoffset = 0;
    }

    /**
     * Offsets the data by the specified number of bytes/characters (returns fales if the end of input stream is
     * reached)
     */
    public boolean offset(int amount) throws IOException {
        int read = instream.read(new byte[amount]);
        return read != -1;
    }

    /**
     * Closes the import binary and the underlying data stream
     */
    public void close() throws IOException {
        instream.close();
    }

    /**
     * @param colschema the columns that are to be read in (e.g. 2,3,6-10)
     * @param rowschema the rows that are to be included in the column (e.g. 2,3,8-10,15+)
     * @return an array of columns read from the input stream according to the given schema (returns null if the end of
     *         input stream is reached)
     */
    public VectorType[] readColumns(DataSchema colschema, DataSchema rowschema) throws IOException {
        boolean readok = readColumnData(rowschema);

        if (!readok) {
            return null;
        }

        int[] cols = colschema.getSchema();
        int[] rows = rowschema.getSchema();
        int colcount;
        int rowcount;

        if (colschema.getCutOff() > -1) {
            colcount = cols.length + (getColumns() - colschema.getCutOff() + 1);
        } else {
            colcount = cols.length;
        }

        if (rowschema.getCutOff() > -1) {
            rowcount = rows.length + (getActualRows() - rowschema.getCutOff() + 1);
        } else {
            rowcount = rows.length;
        }

        double[][] dataarray = new double[colcount][rowcount];

        while (nextRow(rowschema, false)) {
            for (int count = 0; count < cols.length; count++) {
                dataarray[count][rowindex - 1] = readColumn(cols[count]);
            }

            if (colschema.getCutOff() > -1) {
                for (int col = 0; col <= (getColumns() - colschema.getCutOff()); col++) {
                    dataarray[cols.length + col][rowindex - 1] = readColumn(colschema.getCutOff() + col);
                }
            }
        }

        VectorType[] data = new VectorType[colcount];

        for (int count = 0; count < colcount; count++) {
            data[count] = new VectorType(dataarray[count]);
        }

        return data;
    }

    /**
     * Reads in a data set when importing columns.
     *
     * @return true if the data set has been fully read
     */
    protected boolean readColumnData(DataSchema rowschema) throws IOException {
        if (rowindex == 0) {
            byte[] mainarray = new byte[getMaxRow(rowschema) * getColumns() * colbytes];
            int read = instream.read(mainarray);

            if ((getRows() <= 0) && (rowschema.getCutOff() != -1)) {
                ArrayList addrows = new ArrayList();
                byte[] addarray = new byte[getColumns() * colbytes];
                int addread = instream.read(addarray);

                while (addread == (getColumns() * colbytes)) {
                    addrows.add(addarray);
                    read += addread;

                    addarray = new byte[getColumns() * colbytes];
                    addread = instream.read(addarray);
                }

                bytearray = new byte[mainarray.length + (addrows.size() * getColumns() * colbytes)];
                System.arraycopy(mainarray, 0, bytearray, 0, mainarray.length);
                int ptr = mainarray.length;

                for (Iterator iter = addrows.iterator(); iter.hasNext();) {
                    System.arraycopy(iter.next(), 0, bytearray, ptr, getColumns() * colbytes);
                    ptr += (getColumns() * colbytes);
                }
            } else {
                bytearray = mainarray;
            }

            setActualRows(read / (getColumns() * colbytes));

            return (read >= mainarray.length);
        } else {
            return true;
        }
    }

    /**
     * @return the maximum index in a rowschema
     */
    private int getMaxRow(DataSchema rowschema) {
        if (getRows() > 0) {
            return getRows();
        } else {
            int[] schema = rowschema.getSchema();
            int max = 0;

            for (int count = 0; count < schema.length; count++) {
                if (schema[count] > max) {
                    max = schema[count];
                }
            }

            return max;
        }
    }


    /**
     * @param colschema the columns that are to be included in the row (e.g. 2,3,6-10,15+)
     * @param rowschema the rows that are to be read in (e.g. 2,3,8-10,15+)
     * @return a single row read from the input stream according to the given schemas (returns null if the end of input
     *         stream/end of data set is reached)
     */
    public VectorType readRow(DataSchema colschema, DataSchema rowschema) throws IOException {
        boolean readok = readRowData(rowschema);
        int[] cols = colschema.getSchema();

        if ((!readok) || (!nextRow(rowschema, true))) {
            return null;
        } else {
            double[] dataarray;

            if (colschema.getCutOff() > -1) {
                dataarray = new double[cols.length + (getColumns() - colschema.getCutOff() + 1)];
            } else {
                dataarray = new double[cols.length];
            }

            for (int count = 0; count < cols.length; count++) {
                dataarray[count] = readColumn(cols[count]);
            }

            if (colschema.getCutOff() > -1) {
                for (int col = 0; col <= (getColumns() - colschema.getCutOff()); col++) {
                    dataarray[cols.length + col] = readColumn(colschema.getCutOff() + col);
                }
            }

            return new VectorType(dataarray);
        }
    }

    /**
     * Reads in a data set when importing rows (only if at the start of the set)
     *
     * @return true if the data set was fully read
     */
    protected boolean readRowData(DataSchema rowschema) throws IOException {
        // read in when at start of data set
        if (rowindex == 0) {
            if (bytearray.length != getMaxRow(rowschema) * getColumns() * colbytes) {
                bytearray = new byte[getMaxRow(rowschema) * getColumns() * colbytes];
            }

            int read = instream.read(bytearray);
            setActualRows(read / (getColumns() * colbytes));

            return (read == bytearray.length);
        } else {
            return true;
        }
    }

    /**
     * Reads in an additional row when the number of rows is unspecified
     *
     * @return true if the additional row was fully read
     */
    protected boolean readAdditionalRow() throws IOException {
        if (bytearray.length != getColumns() * colbytes) {
            bytearray = new byte[getColumns() * colbytes];
        }

        int read = instream.read(bytearray);

        setActualRows(getActualRows() + (read / bytearray.length));

        return (read == bytearray.length);
    }

    /**
     * Move to the next rowindex and rowoffset
     *
     * @return true if there is a next row, false if the end of the data set is reached
     */
    private boolean nextRow(DataSchema rowschema, boolean rows) throws IOException {
        rowindex++;

        if ((rowindex - 1) < rowschema.getSchema().length) {
            // offset to next row in schema
            rowoffset = getColumns() * colbytes * (rowschema.getSchema()[rowindex - 1] - 1);
            return true;
        } else if (rowschema.getCutOff() == -1) {
            // no cutoff so no next row
            return false;
        } else {
            boolean adflag = true;

            if (rowindex == rowschema.getSchema().length)
            // offset to start of cut-off
            {
                rowoffset = getColumns() * colbytes * (rowschema.getCutOff() - 1);
            } else
            // offset to next row after cut-off
            {
                rowoffset += getColumns() * colbytes;
            }

            if (rows && (rowoffset >= bytearray.length) && (getRows() <= 0)) {
                // Advance to start of cut-off/next row in cutoff
                for (int count = 0; (count <= (rowoffset - bytearray.length) / (getColumns() * colbytes)) && (adflag);
                     count++) {
                    adflag = readAdditionalRow();
                }

                rowoffset = 0;
            }

            return (rowoffset < bytearray.length) && (adflag);
        }
    }

    /**
     * @return a column from the current row (stored in bytearray)
     */
    private double readColumn(int col) {
        try {
            if (col > getColumns()) {
                return 0;
            }

            if (!reverse) {
                if (datatype == BYTE) {
                    return byteToDouble(bytearray, rowoffset + ((col - 1) * colbytes));
                } else if (datatype == SHORT) {
                    return shortBytesToDouble(bytearray, rowoffset + ((col - 1) * colbytes));
                } else if (datatype == INT) {
                    return intBytesToDouble(bytearray, rowoffset + ((col - 1) * colbytes));
                } else if (datatype == LONG) {
                    return longBytesToDouble(bytearray, rowoffset + ((col - 1) * colbytes));
                } else if (datatype == FLOAT) {
                    return floatBytesToDouble(bytearray, rowoffset + ((col - 1) * colbytes));
                } else if (datatype == DOUBLE) {
                    return doubleBytesToDouble(bytearray, rowoffset + ((col - 1) * colbytes));
                }
            } else {
                if (datatype == BYTE) {
                    return byteToDouble(bytearray, rowoffset + ((col - 1) * colbytes));
                } else if (datatype == SHORT) {
                    return reverseShortBytesToDouble(bytearray, rowoffset + ((col - 1) * colbytes));
                } else if (datatype == INT) {
                    return reverseIntBytesToDouble(bytearray, rowoffset + ((col - 1) * colbytes));
                } else if (datatype == LONG) {
                    return reverseLongBytesToDouble(bytearray, rowoffset + ((col - 1) * colbytes));
                } else if (datatype == FLOAT) {
                    return reverseFloatBytesToDouble(bytearray, rowoffset + ((col - 1) * colbytes));
                } else if (datatype == DOUBLE) {
                    return reverseDoubleBytesToDouble(bytearray, rowoffset + ((col - 1) * colbytes));
                }
            }

            throw (new RuntimeException("Invalid import data type"));
        } catch (ArrayIndexOutOfBoundsException except) {
            return 0;
        }
    }


    /**
     * Converts the short bytes at the given offset into a double by packing the bytes in the correct order.
     */
    public final static double byteToDouble(byte[] bytes, int offset) {
        return bytes[offset];
    }

    /**
     * Converts the short bytes at the given offset into a double by packing the bytes in the correct order.
     */
    public final static double shortBytesToDouble(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 8) |
                ((bytes[offset + 1] & 0xFF));
    }

    /**
     * Converts the int bytes at the given offset into a double by packing the bytes in the correct order.
     */
    public final static double intBytesToDouble(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 24) |
                ((bytes[offset + 1] & 0xFF) << 16) |
                ((bytes[offset + 2] & 0xFF) << 8) |
                ((bytes[offset + 3] & 0xFF));
    }

    /**
     * Converts the float bytes at the given offset into a double by packing the bytes in the correct order.
     */
    public final static double floatBytesToDouble(byte[] bytes, int offset) {
        return Float.intBitsToFloat(
                ((bytes[offset] & 0xFF) << 24) |
                        ((bytes[offset + 1] & 0xFF) << 16) |
                        ((bytes[offset + 2] & 0xFF) << 8) |
                        ((bytes[offset + 3] & 0xFF)));
    }

    /**
     * Converts the long bytes at the given offset into a double by packing the bytes in the correct order.
     */
    public final static double longBytesToDouble(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFFL) << 56) |
                ((bytes[offset + 1] & 0xFFL) << 48) |
                ((bytes[offset + 2] & 0xFFL) << 40) |
                ((bytes[offset + 3] & 0xFFL) << 32) |
                ((bytes[offset + 4] & 0xFFL) << 24) |
                ((bytes[offset + 5] & 0xFFL) << 16) |
                ((bytes[offset + 6] & 0xFFL) << 8) |
                ((bytes[offset + 7] & 0xFFL));
    }

    /**
     * Converts the double bytes at the given offset into a double by packing the bytes in the correct order.
     */
    public final static double doubleBytesToDouble(byte[] bytes, int offset) {
        return Double.longBitsToDouble(
                ((bytes[offset] & 0xFFL) << 56) |
                        ((bytes[offset + 1] & 0xFFL) << 48) |
                        ((bytes[offset + 2] & 0xFFL) << 40) |
                        ((bytes[offset + 3] & 0xFFL) << 32) |
                        ((bytes[offset + 4] & 0xFFL) << 24) |
                        ((bytes[offset + 5] & 0xFFL) << 16) |
                        ((bytes[offset + 6] & 0xFFL) << 8) |
                        ((bytes[offset + 7] & 0xFFL)));
    }


    /**
     * Converts the short bytes at the given offset into a double by packing the bytes in reverse order
     */
    public final static double reverseShortBytesToDouble(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF)) |
                ((bytes[offset + 1] & 0xFF) << 8);
    }


    /**
     * Converts the int bytes at the given offset into a double by packing the bytes in reverse order
     */
    public final static double reverseIntBytesToDouble(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF)) |
                ((bytes[offset + 1] & 0xFF) << 8) |
                ((bytes[offset + 2] & 0xFF) << 16) |
                ((bytes[offset + 3] & 0xFF) << 24);
    }

    /**
     * Converts the float bytes at the given offset into a double by packing the bytes in reverse order
     */
    public final static double reverseFloatBytesToDouble(byte[] bytes, int offset) {
        return Float.intBitsToFloat(
                ((bytes[offset] & 0xFF)) |
                        ((bytes[offset + 1] & 0xFF) << 8) |
                        ((bytes[offset + 2] & 0xFF) << 16) |
                        ((bytes[offset + 3] & 0xFF) << 24));
    }

    /**
     * Converts the long bytes at the given offset into a double by packing the bytes in reverse order
     */
    public final static double reverseLongBytesToDouble(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFFL)) |
                ((bytes[offset + 1] & 0xFFL) << 8) |
                ((bytes[offset + 2] & 0xFFL) << 16) |
                ((bytes[offset + 3] & 0xFFL) << 24) |
                ((bytes[offset + 4] & 0xFFL) << 32) |
                ((bytes[offset + 5] & 0xFFL) << 40) |
                ((bytes[offset + 6] & 0xFFL) << 48) |
                ((bytes[offset + 7] & 0xFFL) << 56);
    }

    /**
     * Converts the double bytes at the given offset into a double by packing the bytes in reverse order
     */
    public final static double reverseDoubleBytesToDouble(byte[] bytes, int offset) {
        return Double.longBitsToDouble(
                ((bytes[offset] & 0xFFL)) |
                        ((bytes[offset + 1] & 0xFFL) << 8) |
                        ((bytes[offset + 2] & 0xFFL) << 16) |
                        ((bytes[offset + 3] & 0xFFL) << 24) |
                        ((bytes[offset + 4] & 0xFFL) << 32) |
                        ((bytes[offset + 5] & 0xFFL) << 40) |
                        ((bytes[offset + 6] & 0xFFL) << 48) |
                        ((bytes[offset + 7] & 0xFFL) << 56));
    }

}
