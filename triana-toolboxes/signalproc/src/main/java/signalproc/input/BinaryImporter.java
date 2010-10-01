package signalproc.input;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
 * Imports raw data from a binary file
 *
 * @author Ian Wang
 * @version $Revision $
 */


public class BinaryImporter extends Unit implements TaskListener {

    public static final String DOUBLE = "Double (8bytes)";
    public static final String FLOAT = "Float (4bytes)";
    public static final String LONG = "Long (8bytes)";
    public static final String INT = "Int (4bytes)";
    public static final String SHORT = "Short (2bytes)";
    public static final String BYTE = "Byte (1byte)";

    public static final String ONE_BYTE_PER_COLUMN = "One byte per column";
    public static final String SAME_AS_FOR_DATA_TYPE = "Same as for data type";

    public static final String COLUMNS = "Columns";
    public static final String ROWS = "Rows";

    public static final String NEVER_REWIND = "Never";
    public static final String AUTOMATIC = "Automatic";
    public static final String EVERY_RUN = "Every run";

    // parameter data type definitions
    private String datatype;
    private int columns;
    private int rows;
    private String columnbytes;
    private String rowschema;
    private String columnschema;
    private boolean reversebytes;
    private String filename;
    private int offset;
    private String extract;
    private boolean multi;
    private String rewind;
    private boolean iteroffset;

    /**
     * the binary importer utility class
     */
    private ImportBinary imp;

    /**
     * a flag indicating one data set has been successfully imported from the input stream
     */
    private boolean onesuccess = false;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        if ((imp == null) || rewind.equals(EVERY_RUN)) {
            openImport();
        } else if (iteroffset) {
            imp.offset(offset);
        }

        initImport();
        importData();
    }

    /**
     * Open the import binary utility and skips the header
     */
    private void openImport() throws IOException, FileNotFoundException {
        if (imp != null) {
            imp.close();
        }

        imp = new ImportBinary(new FileInputStream(filename));
        imp.offset(offset);

        onesuccess = false;
    }

    /**
     * Initialise the parameters on the import binary utility
     */
    private void initImport() throws IOException {
        imp.setColumns(columns);
        imp.setRows(rows);

        imp.setOneBytePerColumn(columnbytes.equals(ONE_BYTE_PER_COLUMN));
        imp.setReverseByteOrder(reversebytes);

        if (datatype.equals(DOUBLE)) {
            imp.setDataType(ImportBinary.DOUBLE);
        } else if (datatype.equals(FLOAT)) {
            imp.setDataType(ImportBinary.FLOAT);
        } else if (datatype.equals(LONG)) {
            imp.setDataType(ImportBinary.LONG);
        } else if (datatype.equals(INT)) {
            imp.setDataType(ImportBinary.INT);
        } else if (datatype.equals(SHORT)) {
            imp.setDataType(ImportBinary.SHORT);
        } else if (datatype.equals(BYTE)) {
            imp.setDataType(ImportBinary.BYTE);
        }

        imp.nextDataSet();
    }

    /**
     * Imports data from the input stream using the import binary utility
     */
    private void importData() throws IOException {
        DataSchema colscheme = DataSchema.getDataSchema(columnschema);
        DataSchema rowscheme = DataSchema.getDataSchema(rowschema);

        if (extract.equals(ROWS)) {
            importRows(colscheme, rowscheme);
        } else {
            importColumns(colscheme, rowscheme);
        }
    }

    /**
     * Imports columns from the input stream
     */
    private void importColumns(DataSchema colscheme, DataSchema rowscheme) throws IOException {
        VectorType[] dataarray = imp.readColumns(colscheme, rowscheme);

        if ((dataarray == null) && (rewind.equals(AUTOMATIC))) {
            openImport();
            initImport();
            dataarray = imp.readColumns(colscheme, rowscheme);
        }

        if (dataarray != null) {
            for (int count = 0; count < dataarray.length; count++) {
                if (multi) {
                    outputAtNode(count, dataarray[count], true);
                } else {
                    output(dataarray[count]);
                }
            }
        } else if (!rewind.equals(NEVER_REWIND)) {
            throw (new RuntimeException("Data set exceeds input stream length"));
        }
    }

    /**
     * Import rows from the input stream
     */
    private void importRows(DataSchema colscheme, DataSchema rowscheme) throws IOException {
        VectorType data;
        int count = 0;

        int rowcount = rowscheme.getSchema().length;

        if ((rowscheme.getCutOff() != -1) && (rows > 0)) {
            rowcount += rows - rowscheme.getCutOff() + 1;
        }

        do {
            data = imp.readRow(colscheme, rowscheme);

            if ((((rows > 0) && (count < rowcount)) || (count == 0)) && (data == null) && (rewind.equals(AUTOMATIC))) {
                openImport();
                initImport();
                count = 0;

                data = imp.readRow(colscheme, rowscheme);
            }

            if (data != null) {
                if (multi) {
                    outputAtNode(Math.min(count, getTask().getDataOutputNodeCount()), data, true);
                } else {
                    output(data);
                }

                count++;
            }
        } while (data != null);

        if ((!onesuccess) && (count < rowcount)) {
            throw (new RuntimeException("Data set exceeds input stream size"));
        } else {
            onesuccess = true;
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
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(Task.PROCESS_UPDATE);

        setPopUpDescription("Imports raw data from a binary file");
        setHelpFileLocation("BinaryImporter.html");

        // Initialise task parameters with default values (if not already initialised)
        Task task = getTask();

        if (!task.isParameterName("datatype")) {
            task.setParameter("datatype", DOUBLE);
        }

        if (!task.isParameterName("columns")) {
            task.setParameter("columns", "1");
        }

        if (!task.isParameterName("rows")) {
            task.setParameter("rows", "1");
        }

        if (!task.isParameterName("columnbytes")) {
            task.setParameter("columnbytes", SAME_AS_FOR_DATA_TYPE);
        }

        if (!task.isParameterName("rowschema")) {
            task.setParameter("rowschema", "");
        }

        if (!task.isParameterName("columnschema")) {
            task.setParameter("columnschema", "");
        }

        if (!task.isParameterName("reversebytes")) {
            task.setParameter("reversebytes", "false");
        }

        if (!task.isParameterName("filename")) {
            task.setParameter("filename", "");
        }

        if (!task.isParameterName("offset")) {
            task.setParameter("offset", "0");
        }

        if (!task.isParameterName("extract")) {
            task.setParameter("extract", COLUMNS);
        }

        if (!task.isParameterName("multi")) {
            task.setParameter("multi", "false");
        }

        if (!task.isParameterName("rewind")) {
            task.setParameter("rewind", EVERY_RUN);
        }

        if (!task.isParameterName("iteroffset")) {
            task.setParameter("iteroffset", "false");
        }

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Filename $title filename File null *.*\n";
        guilines += "Data type $title datatype Choice [" + DOUBLE + "] [" + FLOAT + "] [" + LONG + "] [" + INT + "] ["
                + SHORT + "] [" + BYTE + "]\n";
        guilines += "Bytes per column $title columnbytes Choice [" + ONE_BYTE_PER_COLUMN + "] [" + SAME_AS_FOR_DATA_TYPE
                + "]\n";
        guilines += "Extract $title extract Choice [" + COLUMNS + "] [" + ROWS + "]\n";
        guilines += "Header offset (bytes) $title offset TextField 0\n";
        guilines += "Number of columns $title columns TextField 1\n";
        guilines += "Number of rows $title rows TextField 1\n";
        guilines += "Extract columns (e.g. 1,3-12,15+) $title columnschema TextField \n";
        guilines += "Extract rows (e.g. 1,3-12,15+) $title rowschema TextField \n";
        guilines += "Reverse byte order $title reversebytes Checkbox false\n";
        guilines += "Output on multiple nodes $title multi Checkbox false\n";
        guilines += "Header offset every iteration $title iteroffset Checkbox false\n";
        guilines += "Rewind input stream $title rewind Choice [" + NEVER_REWIND + "] [" + AUTOMATIC + "] [" + EVERY_RUN
                + "]\n";
        setGUIBuilderV2Info(guilines);

        task.addTaskListener(this);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        Task task = getTask();
        datatype = (String) task.getParameter("datatype");
        columns = new Integer((String) task.getParameter("columns")).intValue();
        rows = new Integer((String) task.getParameter("rows")).intValue();
        columnbytes = (String) task.getParameter("columnbytes");
        rowschema = (String) task.getParameter("rowschema");
        columnschema = (String) task.getParameter("columnschema");
        reversebytes = new Boolean((String) task.getParameter("reversebytes")).booleanValue();
        filename = (String) task.getParameter("filename");
        offset = new Integer((String) task.getParameter("offset")).intValue();
        extract = (String) task.getParameter("extract");
        multi = new Boolean((String) task.getParameter("multi")).booleanValue();
        rewind = (String) task.getParameter("rewind");
        iteroffset = new Boolean((String) task.getParameter("iteroffset")).booleanValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up BinaryImporter (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("datatype")) {
            datatype = (String) value;
        }

        if (paramname.equals("columns")) {
            columns = new Integer((String) value).intValue();
        }

        if (paramname.equals("rows")) {
            if (((String) value).trim().equals("")) {
                rows = 0;
            } else {
                rows = new Integer((String) value).intValue();
            }
        }

        if (paramname.equals("columnbytes")) {
            columnbytes = (String) value;
        }

        if (paramname.equals("rowschema")) {
            rowschema = (String) value;
        }

        if (paramname.equals("columnschema")) {
            columnschema = (String) value;
        }

        if (paramname.equals("reversebytes")) {
            reversebytes = new Boolean((String) value).booleanValue();
        }

        if (paramname.equals("filename")) {
            filename = (String) value;
        }

        if (paramname.equals("offset")) {
            offset = new Integer((String) value).intValue();
        }

        if (paramname.equals("extract")) {
            extract = (String) value;
        }

        if (paramname.equals("multi")) {
            multi = new Boolean((String) value).booleanValue();
        }

        if (paramname.equals("rewind")) {
            rewind = (String) value;
        }

        if (paramname.equals("iteroffset")) {
            iteroffset = new Boolean((String) value).booleanValue();
        }

        if ((columnschema.indexOf('+') > -1) && (!columnschema.endsWith("+"))) {
            setParameter("columnschema", columnschema.substring(0, columnschema.indexOf('+') + 1));
        } else if ((rowschema.indexOf('+') > -1) && (!rowschema.endsWith("+"))) {
            setParameter("rowschema", rowschema.substring(0, rowschema.indexOf('+') + 1));
        }
    }


    /**
     * @return an array of the input types for BinaryImporter
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for BinaryImporter
     */
    public String[] getOutputTypes() {
        return new String[]{"VectorType"};
    }


    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
        String paramname = event.getParameterName();
        Task task = event.getTask();

        try {
            boolean reextract = paramname.equals("columnschema") || paramname.equals("rowschema") ||
                    paramname.equals("columns") || paramname.equals("rows") ||
                    paramname.equals("extract") || paramname.equals("multi");

            if (reextract) {
                if (new Boolean((String) task.getParameter("multi")).booleanValue()) {
                    DataSchema schema;
                    int len;
                    int nodecount;

                    if (task.getParameter("extract").equals(COLUMNS)) {
                        schema = DataSchema.getDataSchema((String) getTask().getParameter("columnschema"));
                        len = new Integer((String) getTask().getParameter("columns")).intValue();
                    } else {
                        schema = DataSchema.getDataSchema((String) getTask().getParameter("rowschema"));
                        len = new Integer((String) getTask().getParameter("rows")).intValue();
                    }

                    if (schema.getCutOff() > -1) {
                        nodecount = schema.getSchema().length + len - schema.getCutOff() + 1;
                    } else {
                        nodecount = schema.getSchema().length;
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
        } catch (NumberFormatException except) {
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
     * Called when a data input node is added.
     */
    public void nodeAdded(TaskNodeEvent event) {
    }

    /**
     * Called before a data input node is removed.
     */
    public void nodeRemoved(TaskNodeEvent event) {
    }

    /**
     * Called before the task is disposed
     */
    public void taskDisposed(TaskDisposedEvent event) {
    }

}



