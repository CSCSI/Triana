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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.VectorType;

/**
 * An abstract unit for exporting data.
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */

public abstract class Export extends Unit {


    // parameter data type definitions
    private String filename;
    private String export;
    private int colrownum;
    private boolean multi;
    private boolean seqnum;

    /**
     * a flag indicating whether the previous file has been finished with
     */
    private boolean finished = true;

    /**
     * an array of stored column data
     */
    private ArrayList coldata = new ArrayList();

    /**
     * the number of rows written to file
     */
    private int rowcount = 0;

    /**
     * the number appended onto the filename
     */
    private int sequencenum;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        boolean writer = false;
        Object input;
        double[] data;

        for (int count = 0; count < getInputNodeCount(); count++) {
            if (!filename.equals("")) {
                String seqfilename = getSequencedFileName(filename);
                if (finished) {
                    writer = openOutputStream(new FileOutputStream(seqfilename, false));
                    finished = false;
                } else {
                    writer = openOutputStream(new FileOutputStream(seqfilename, true));
                }
            }

            if (writer) {
                input = getInputAtNode(count);

                if (input instanceof VectorType) {
                    data = (double[]) ((VectorType) input).getGraphArrayReal(0);
                } else {
                    data = (double[]) input;
                }

                if (export.equals("Rows")) {
                    writeRow(data);
                    rowcount++;
                } else {
                    storeColumn(data);
                }

                if ((colrownum <= 0) || (coldata.size() == colrownum)) {
                    writeColumns();
                }

                checkFinished();

                closeOutputStream();
            }
        }
    }

    /**
     * Opens a output stream to wrap the specified output stream
     *
     * @return the opened writer.
     */
    protected abstract boolean openOutputStream(OutputStream outstream) throws IOException;

    /**
     * Write a row to the output stream
     */
    protected abstract void writeRow(double[] data) throws IOException;

    /**
     * Closes and disposes the output stream
     */
    protected abstract void closeOutputStream() throws IOException;


    /**
     * Appends the sequence number onto a filename (if required)
     */
    private String getSequencedFileName(String filename) {
        if (seqnum) {
            if (filename.lastIndexOf('.') == -1) {
                return filename + sequencenum;
            } else {
                return filename.substring(0, filename.lastIndexOf('.')) + sequencenum + filename
                        .substring(filename.lastIndexOf('.'));
            }
        } else {
            return filename;
        }
    }

    /**
     * Store the data in coldata
     */
    private void storeColumn(double[] input) {
        coldata.add(input);
    }

    /**
     * Write all the columns in coldata to the file writer
     */
    private void writeColumns() throws IOException {
        if (coldata.isEmpty()) {
            return;
        }

        Object[] cols = coldata.toArray(new Object[coldata.size()]);
        double[] data = new double[cols.length];
        int maxrow = 0;

        for (int count = 0; count < cols.length; count++) {
            if (maxrow < ((double[]) cols[count]).length) {
                maxrow = ((double[]) cols[count]).length;
            }
        }

        for (int rcount = 0; rcount < maxrow; rcount++) {
            for (int ccount = 0; ccount < cols.length; ccount++) {
                if (rcount < ((double[]) cols[ccount]).length) {
                    data[ccount] = (((double[]) cols[ccount])[rcount]);
                } else {
                    data[ccount] = 0;
                }
            }

            writeRow(data);
        }
    }

    /**
     * Checks whether all the rows/columns have been written and cleans-up if they have
     */
    private void checkFinished() {
        if ((export.equals("Rows")) && (colrownum > 0) && (rowcount >= colrownum)) {
            finished = true;
        } else if ((!export.equals("Rows")) && ((colrownum <= 0) || (coldata.size() >= colrownum))) {
            finished = true;
        }

        if (finished) {
            rowcount = 0;

            if (colrownum > 0) {
                coldata.clear();

                if (seqnum) {
                    sequencenum++;
                }
            }
        }
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(0);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Exports data in row/column format to an ASCII file");
        setHelpFileLocation("TableExporter.html");

        // Define initial value and type of parameters
        defineParameter("filename", "", USER_ACCESSIBLE);
        defineParameter("export", "", USER_ACCESSIBLE);
        defineParameter("colrownum", "1", USER_ACCESSIBLE);
        defineParameter("multi", "true", USER_ACCESSIBLE);
        defineParameter("seqnum", "false", USER_ACCESSIBLE);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        filename = (String) getParameter("filename");
        export = (String) getParameter("export");

        String colrowval = (String) getParameter("colrownum");
        if (colrowval.equals("")) {
            colrownum = 0;
        } else {
            colrownum = new Integer(colrowval).intValue();
        }

        multi = new Boolean((String) getParameter("multi")).booleanValue();
        seqnum = new Boolean((String) getParameter("seqnum")).booleanValue();

        resetFile();
    }

    /**
     * Resets the file
     */
    private void resetFile() {
        finished = true;
        coldata.clear();
        rowcount = 0;
        sequencenum = 1;
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up TableExporter (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        Task task = getTask();

        if (paramname.equals("multi")) {
            if (multi != new Boolean((String) value).booleanValue()) {
                multi = new Boolean((String) value).booleanValue();
                resetFile();
            }
        }

        if (paramname.equals("colrownum")) {
            int tmpnum = colrownum;

            if (value.equals("")) {
                colrownum = 0;
            } else {
                colrownum = new Integer((String) value).intValue();
            }

            if (tmpnum != colrownum) {
                resetFile();
            }
        }

        try {
            if (multi) {
                while (task.getDataInputNodeCount() < colrownum) {
                    task.addDataInputNode();
                }

                while (task.getDataInputNodeCount() > Math.max(colrownum, 1)) {
                    task.removeDataInputNode(task.getDataInputNode(Math.max(colrownum, 1)));
                }
            } else {
                while (task.getDataInputNodeCount() < 1) {
                    task.addDataInputNode();
                }

                while (task.getDataInputNodeCount() > 1) {
                    task.removeDataInputNode(task.getDataInputNode(1));
                }
            }
        } catch (NodeException except) {
            notifyError(except.getMessage());
        }

        if (paramname.equals("export")) {
            if (!export.equals(value)) {
                export = (String) value;
                resetFile();
            }
        }

        // Code to update local variables
        if (paramname.equals("filename")) {
            if (!filename.equals(value)) {
                filename = (String) value;
                resetFile();
            }
        }

        if (paramname.equals("seqnum")) {
            if (seqnum != new Boolean((String) value).booleanValue()) {
                seqnum = new Boolean((String) value).booleanValue();
                resetFile();
            }
        }
    }


    /**
     * @return an array of the input types for TableExporter
     */
    public String[] getInputTypes() {
        return new String[]{"double[]", "VectorType"};
    }

    /**
     * @return an array of the output types for TableExporter
     */
    public String[] getOutputTypes() {
        return new String[]{};
    }

}
