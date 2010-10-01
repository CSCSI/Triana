package signalproc.input;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;
import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskDisposedEvent;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;
import triana.types.VectorType;


/**
 * Imports columns of data from an ASCII file
 *
 * @author Ian Wang
 * @version $Revision $
 */


public class TableImporter extends Unit implements TaskListener {

    public static final String COLUMNS = "Columns";
    public static final String ROWS = "Rows";

    // parameter data type definitions
    private String filename;
    private String columns;
    private String rows;
    private String extract;
    private boolean multi;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        File file = new File(filename);

        if (file.exists() && (!file.isDirectory())) {
            StreamTokenizer tokenizer = new StreamTokenizer(new FileReader(file));
            DataSchema colarray = DataSchema.getDataSchema(columns);
            DataSchema rowarray = DataSchema.getDataSchema(rows);
            ArrayList linedata = new ArrayList();
            int rowcount = 0;

            ArrayList[] dataarray = initialiseDataArray(colarray, rowarray);

            tokenizer.wordChars('+', '+');
            tokenizer.eolIsSignificant(true);
            int token = tokenizer.nextToken();

            while (token != tokenizer.TT_EOF) {
                rowcount++;

                token = readLine(linedata, tokenizer, token);

                // extract data to columns or rows
                if (extract.equals(COLUMNS)) {
                    handleColumnExtract(linedata, dataarray, colarray, rowarray, rowcount);
                } else {
                    handleRowExtract(linedata, dataarray, colarray, rowarray, rowcount);
                }
            }

            if (extract.equals(COLUMNS) || (!rowarray.isInOrder())) {
                outputData(dataarray);
            }
        } else if (!filename.equals("")) {
            JOptionPane.showMessageDialog(null, filename + " not Found", getToolName(), JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "File not specified", getToolName(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Initialises the data arrays
     */
    private ArrayList[] initialiseDataArray(DataSchema colarray, DataSchema rowarray) {
        ArrayList[] dataarray;
        int datalength;

        if (extract.equals(COLUMNS)) {
            dataarray = new ArrayList[colarray.getSchema().length];
            datalength = rowarray.getSchema().length;
        } else if (rowarray.isInOrder()) {
            // set-up data array for immediate output
            dataarray = new ArrayList[1];
            datalength = 0;
        } else {
            dataarray = new ArrayList[rowarray.getSchema().length];
            datalength = colarray.getSchema().length;
        }

        // init data array
        for (int count = 0; count < dataarray.length; count++) {
            dataarray[count] = new ArrayList();

            for (int dcount = 0; dcount < datalength; dcount++) {
                dataarray[count].add(dcount, new Double(0));
            }
        }

        return dataarray;
    }

    /**
     * Outputs the extracted data
     */
    private void outputData(ArrayList[] dataarray) {
        // Construct and output vector type for each column

        for (int ccount = 0; ccount < dataarray.length; ccount++) {
            outputData(dataarray[ccount], ccount);
        }
    }

    /**
     * Outputs the extracted data
     */
    private void outputData(ArrayList dataarray, int ccount) {
        double[] coldata = new double[dataarray.size()];
        Iterator iter = dataarray.iterator();

        for (int dcount = 0; dcount < coldata.length; dcount++) {
            coldata[dcount] = ((Double) iter.next()).doubleValue();
        }

        if (multi) {
            if (ccount < getTask().getDataOutputNodeCount()) {
                outputAtNode(ccount, new VectorType(coldata));
            }
        } else {
            output(new VectorType(coldata));
        }
    }

    /**
     * Reads a line from the stream tokenizer into linedata
     */
    private int readLine(ArrayList linedata, StreamTokenizer tokenizer, int token) throws IOException {
        double val;
        linedata.clear();

        // Read in values for a single line
        while ((token != tokenizer.TT_EOL) && (token != tokenizer.TT_EOF)) {
            if (token == tokenizer.TT_NUMBER) {
                val = tokenizer.nval;
                token = tokenizer.nextToken();

                if ((token == tokenizer.TT_WORD) && (tokenizer.sval.startsWith("e") || tokenizer.sval
                        .startsWith("E"))) {
                    linedata.add(new Double(String.valueOf(val) + tokenizer.sval));
                } else {
                    tokenizer.pushBack();
                    linedata.add(new Double(val));
                }
            } else if (token == tokenizer.TT_WORD) {
                linedata.add(new Double(Double.NaN));
            }

            token = tokenizer.nextToken();
        }

        return tokenizer.nextToken();
    }

    /**
     * Handles a line of data for column extraction
     */
    private void handleColumnExtract(ArrayList linedata, ArrayList[] dataarray, DataSchema colarray,
                                     DataSchema rowarray, int rowcount) {
        // put values into dataarray according to the schema given in colarray
        for (int ccount = 0; ccount < dataarray.length; ccount++) {
            if ((linedata.size() >= colarray.getSchema()[ccount]) && (linedata
                    .get(colarray.getSchema()[ccount] - 1) instanceof Double)) {
                for (int rcount = 0; rcount < rowarray.getSchema().length; rcount++) {
                    if (rowarray.getSchema()[rcount] == rowcount) {
                        dataarray[ccount].set(rcount, linedata.get(colarray.getSchema()[ccount] - 1));
                    }
                }

                if (rowarray.getCutOff() > -1) {
                    if (rowcount >= rowarray.getCutOff()) {
                        dataarray[ccount].add(linedata.get(colarray.getSchema()[ccount] - 1));
                    }
                }
            }
        }
    }

    /**
     * Handles a line of data for row extraction, and outputs it if the output rows are in order
     */
    private void handleRowExtract(ArrayList linedata, ArrayList[] dataarray, DataSchema colarray, DataSchema rowarray,
                                  int rowcount) {
        // if the values into the data array according to the schema given in rowarray
        for (int rcount = 0; rcount < rowarray.getSchema().length; rcount++) {
            if (rowarray.getSchema()[rcount] == rowcount) {
                if (rowarray.isInOrder()) {
                    // handle immediate output if output rows in order
                    initRow(dataarray[0], colarray.getSchema().length);
                    extractRow(linedata, dataarray[0], colarray);
                    outputData(dataarray[0], 0);
                } else {
                    extractRow(linedata, dataarray[rcount], colarray);
                }
            }
        }

// handle immediate output above cutoff if output rows in order
        if (rowarray.isInOrder() && (rowarray.getCutOff() > -1) && (rowcount >= rowarray.getCutOff())) {
            initRow(dataarray[0], colarray.getSchema().length);
            extractRow(linedata, dataarray[0], colarray);
            outputData(dataarray[0], 0);
        }
    }

    /**
     * Initialises a data array for immediate output
     */
    private void initRow(ArrayList dataarray, int collength) {
        dataarray.clear();

        for (int dcount = 0; dcount < collength; dcount++) {
            dataarray.add(dcount, new Double(0));
        }
    }

    /**
     * Extracts the current line into the specified dataarray
     */
    private void extractRow(ArrayList linedata, ArrayList dataarray, DataSchema colarray) {
        for (int ccount = 0; ccount < colarray.getSchema().length; ccount++) {
            if ((linedata.size() >= colarray.getSchema()[ccount]) && (linedata
                    .get(colarray.getSchema()[ccount] - 1) instanceof Double)) {
                dataarray.set(ccount, linedata.get(colarray.getSchema()[ccount] - 1));
            }
        }

        if (colarray.getCutOff() > -1) {
            int ccount = colarray.getCutOff();

            while (ccount <= linedata.size()) {
                if (linedata.get(ccount - 1) instanceof Double) {
                    dataarray.add(linedata.get(ccount - 1));
                } else {
                    dataarray.add(new Double(0));
                }

                ccount++;
            }
        }
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

// Initialise node properties
        setDefaultInputNodes(0);
        setMinimumInputNodes(0);
        setMaximumInputNodes(0);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(1);

// Initialise parameter update policy
        setParameterUpdatePolicy(Task.PROCESS_UPDATE);

        setHelpFileLocation("TableImporter.html");

// Initialise task parameters with default values (if not already initialised)
        Task task = getTask();

        if (!task.isParameterName("filename")) {
            task.setParameter("filename", "");
        }

        if (!task.isParameterName("columns")) {
            task.setParameter("columns", "");
        }

        if (!task.isParameterName("rows")) {
            task.setParameter("rows", "");
        }

        if (!task.isParameterName("extract")) {
            task.setParameter("extract", COLUMNS);
        }

        if (!task.isParameterName("multi")) {
            task.setParameter("multi", "false");
        }

// Initialise GUI builder interface
        String guilines = "";
        guilines += "File Name $title filename File null *.*\n";
        guilines += "Columns (e.g. 3,6-12,15+) $title columns TextField \n";
        guilines += "Rows (e.g. 3,6-12,15+) $title rows TextField \n";
        guilines += "Extract $title extract Choice " + COLUMNS + " " + ROWS + " \n";
        guilines += "Output on multiple nodes $title multi Checkbox \n";
        setGUIBuilderV2Info(guilines);

// Add ascii importer as task listener so it can update nodes immediately
        getTask().addTaskListener(this);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        Task task = getTask();
        filename = (String) task.getParameter("filename");
        columns = (String) task.getParameter("columns");
        rows = (String) task.getParameter("rows");
        extract = (String) task.getParameter("extract");
        multi = new Boolean((String) task.getParameter("multi")).booleanValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up TableImporter (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("extract")) {
            extract = (String) value;
        }

        if (paramname.equals("filename")) {
            filename = (String) value;
        }

        if (paramname.equals("columns")) {
            columns = (String) value;
        }

        if (paramname.equals("rows")) {
            rows = (String) value;
        }

        if (paramname.equals("multi")) {
            multi = new Boolean((String) value).booleanValue();
        }

        if (extract.equals(COLUMNS) && (columns.indexOf('+') > -1)) {
            JOptionPane.showMessageDialog(null, "Cannot use + in columns when using column extract");
            setParameter("columns", (Object) columns.replace('+', ' '));
        } else if ((columns.indexOf('+') > -1) && (!columns.endsWith("+"))) {
            setParameter("columns", (Object) columns.substring(0, columns.indexOf('+') + 1));
        } else if ((rows.indexOf('+') > -1) && (!rows.endsWith("+"))) {
            setParameter("rows", (Object) rows.substring(0, rows.indexOf('+') + 1));
        }
    }


    /**
     * @return an array of the input types for TableImporter
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for TableImporter
     */
    public String[] getOutputTypes() {
        return new String[]{"VectorType"};
    }


    /**
     * @return a <b>brief!</b> description of what the unit does
     */
    public String getPopUpDescription() {
        return "Imports columns/rows of data from an ASCII file";
    }

    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
        try {
            Task task = event.getTask();
            String paramname = event.getParameterName();

            boolean reextract = paramname.equals("columns") || paramname.equals("rows") ||
                    paramname.equals("extract") || paramname.equals("multi");

            if (reextract) {
                if (new Boolean((String) task.getParameter("multi")).booleanValue()) {
                    int nodecount;

                    if (task.getParameter("extract").equals(COLUMNS)) {
                        nodecount = DataSchema.getDataSchema((String) getTask().getParameter("columns"))
                                .getSchema().length;
                    } else {
                        nodecount = DataSchema.getDataSchema((String) getTask().getParameter("rows"))
                                .getSchema().length;
                    }

                    while (getTask().getDataOutputNodeCount() < nodecount) {
                        getTask().addDataOutputNode();
                    }

                    while (getTask().getDataOutputNodeCount() > Math.max(nodecount, 1)) {
                        getTask().removeDataOutputNode(getTask().getDataOutputNode(nodecount));
                    }
                } else if (paramname.equals("multi")) {
                    while (getTask().getDataOutputNodeCount() > 1) {
                        getTask().removeDataOutputNode(getTask().getDataOutputNode(1));
                    }
                }
            }
        } catch (NodeException except) {
            notifyError(except.getMessage());
        }
    }

    /**
     * Called when the core properties of a task change i.e. its name, whether it is running continuously etc.
     */
    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }

    /**
     * Called before a data input node is removed.
     */
    public void nodeRemoved(TaskNodeEvent event) {
    }

    /**
     * Called when a data input node is added.
     */
    public void nodeAdded(TaskNodeEvent event) {
    }

    /**
     * Called before the task is disposed
     */
    public void taskDisposed(TaskDisposedEvent event) {
    }

}



