/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2003 University of Wales, Cardiff. All rights reserved.
 *
 * Redistribution and use of the software in source and binary forms, with
 * or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment: "This product includes
 *    software developed by the University of Wales, Cardiff for the Triana
 *    Project (http://www.trianacode.org)." Alternately, this
 *    acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Triana" and "University of Wales, Cardiff" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact triana@trianacode.org.
 *
 * 5. Products derived from this software may not be called "Triana," nor
 *    may Triana appear in their name, without prior written permission of
 *    the University of Wales, Cardiff.
 *
 * 6. This software may not be sold, used or incorporated into any product
 *    for sale to third parties.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 * NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Triana Project. For more information on the
 * Triana Project, please see. http://www.trianacode.org.
 *
 * This license is based on the BSD license as adopted by the Apache
 * Foundation and is governed by the laws of England and Wales.
 */

package signalproc.output;

import java.io.IOException;
import java.io.OutputStream;

import org.trianacode.taskgraph.Task;

/**
 * Exports data in row/column format to an Binary file
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */


public class BinaryExporter extends Export {

    public static final String DOUBLE = "Double (8bytes)";
    public static final String FLOAT = "Float (4bytes)";
    public static final String LONG = "Long (8bytes)";
    public static final String INT = "Int (4bytes)";
    public static final String SHORT = "Short (2bytes)";
    public static final String BYTE = "Byte (1byte)";

    private static final int SHORT_BYTES = 2;
    private static final int INT_BYTES = 4;
    private static final int LONG_BYTES = 8;
    private static final int FLOAT_BYTES = 4;
    private static final int DOUBLE_BYTES = 8;

    private OutputStream outstream;

    /**
     * the type of data
     */
    private String datatype;

    /**
     * a flag indicating whether byte order is reversed
     */
    private boolean reversebytes;


    /**
     * This function is called when the unit is first created. It should be over-ridden to initialise the tool
     * properties (e.g. default number of nodes) and tool parameters.
     */
    public void init() {
        super.init();

        Task task = getTask();

        if (!task.isParameterName("reversebytes")) {
            task.setParameter("reversebytes", "false");
        }

        if (!task.isParameterName("datatype")) {
            task.setParameter("datatype", DOUBLE);
        }

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "File Name $title filename File null *.*\n";
        guilines += "Data Type $title datatype Choice [" + DOUBLE + "] [" + FLOAT + "] [" + LONG + "] [" + INT + "] ["
                + SHORT + "] [" + BYTE + "]\n";
        guilines += "Export $title export Choice [Columns] [Rows]\n";
        guilines += "Number of Columns/Rows $title colrownum TextField 1\n";
        guilines += "Reverse byte order $title reversebytes Checkbox false\n";
        guilines += "Input from multiple nodes $title multi Checkbox true\n";
        guilines += "Append sequence number (file1, file2...) $title seqnum Checkbox false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        super.reset();

        datatype = (String) getTask().getParameter("datatype");
        reversebytes = new Boolean((String) getTask().getParameter("reversebytes")).booleanValue();
    }

    /**
     * Opens a output stream to wrap the specified output stream
     *
     * @return the opened output stream
     */
    protected boolean openOutputStream(OutputStream outstream) {
        this.outstream = outstream;
        return true;
    }

    /**
     * Closes and disposes the output stream
     */
    protected void closeOutputStream() throws IOException {
        outstream.close();
        outstream = null;
    }


    /**
     * Write a row to the output stream
     */
    protected void writeRow(double[] data) throws IOException {
        byte[] bytes;

        for (int ccount = 0; ccount < data.length; ccount++) {
            bytes = toByteArray(data[ccount]);

            for (int bcount = 0; bcount < bytes.length; bcount++) {
                outstream.write(bytes[bcount]);
            }
        }
    }


    private byte[] toByteArray(double val) {
        if (datatype.equals(FLOAT)) {
            val = Math.max(Math.min(Float.MAX_VALUE, val), Float.MIN_VALUE);
            return toByteArray(Float.floatToIntBits((float) val), FLOAT_BYTES, reversebytes);
        } else if (datatype.equals(LONG)) {
            val = Math.max(Math.min(Long.MAX_VALUE, val), Long.MIN_VALUE);
            return toByteArray((long) val, LONG_BYTES, reversebytes);
        } else if (datatype.equals(INT)) {
            val = Math.max(Math.min(Integer.MAX_VALUE, val), Integer.MIN_VALUE);
            return toByteArray((int) val, INT_BYTES, reversebytes);
        } else if (datatype.equals(SHORT)) {
            val = Math.max(Math.min(Short.MAX_VALUE, val), Short.MIN_VALUE);
            return toByteArray((short) val, SHORT_BYTES, reversebytes);
        } else if (datatype.equals(BYTE)) {
            val = Math.max(Math.min(Byte.MAX_VALUE, val), Byte.MIN_VALUE);
            return toByteArray((byte) val, 1, reversebytes);
        } else {
            return toByteArray(Double.doubleToLongBits(val), DOUBLE_BYTES, reversebytes);
        }
    }

    private byte[] toByteArray(long val, int bytecount, boolean reverse) {
        byte[] bytes = new byte[bytecount];

        for (int count = 0; count < bytecount; ++count) {
            if (reverse) {
                bytes[count] = (byte) (val & 0xFFL);
            } else {
                bytes[bytecount - count - 1] = (byte) (val & 0xFFL);
            }

            val = val >> 8;
        }

        return bytes;
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        super.parameterUpdate(paramname, value);

        if (paramname.equals("datatype")) {
            datatype = (String) value;
        }

        if (paramname.equals("reversebytes")) {
            reversebytes = new Boolean((String) value).booleanValue();
        }
    }

}



