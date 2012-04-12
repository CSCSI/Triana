package common.output;


import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphManager;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.FileToolboxLoader;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolListener;
import org.trianacode.taskgraph.tool.Toolbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Serialize data to a file
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class Serialize extends Unit implements ToolListener {

    private static final String TRIANA_SERIALIZE = "Triana Serialize";
    private static final String JAVA_SERIALIZE = "Java Serialize";

    private static final String INTERNAL = "Internal Data";
    private static final String DEFAULT_DESERIALIZE_TOOL = "Common.Input.DeSerialize";


    private Object[] data;
    private int ptr;

    private String filename;

    private String type;
    private String toolname;
    private String pack;

    private boolean append;
    private int appendcount = 0;
    private boolean seq;
    private int seqlength = 1;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        Object input = getInputAtNode(0);

        data[ptr++] = input;

        if (ptr >= data.length) {
            serialize();
        }
    }

    private void serialize() {
        if (filename.equals("")) {
            notifyError("Error Serializing Data: Filename not set");
            return;
        }

        if (type.equals(TRIANA_SERIALIZE)) {
            trianaSerialize();
        } else {
            javaSerialize();
        }

        increaseAppendCount();
        resetData();
    }

    /**
     * Serialize the input data as a Triana tool
     */
    private void trianaSerialize() {
        String deserialize;

        if (isParameter("deserializeTool")) {
            deserialize = (String) getParameter("deserializeTool");
        } else {
            deserialize = DEFAULT_DESERIALIZE_TOOL;
        }

        if (!getToolTable().isTool(deserialize)) {
            notifyError("Error Serializing Data: Deserialize tool not found: " + deserialize);
            return;
        }

        if (toolname.equals("")) {
            notifyError("Error Serializing Data: Tool name not set");
            return;
        }

        try {
            Tool tool = getToolTable().getTool(deserialize);

            TaskGraph tgraph = TaskGraphManager.createTaskGraph();
            Task task = tgraph.createTask(tool);

            task.setToolName(getAppendToolName());
            ((Tool) task).setToolPackage(pack);

            for (int count = 0; count < data.length; count++) {
                task.setParameter("data" + count, data[count]);
            }

            task.setParameter("dataLength", String.valueOf(data.length));
            task.setParameter("filename", INTERNAL);

            String appendfile = getAppendedFileName();
            File file = new File(appendfile);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            XMLWriter writer = new XMLWriter(new FileWriter(file));
            writer.writeComponent(task);
            writer.close();
        } catch (Exception except) {
            notifyError("Error Serializing Data: " + except.getMessage());
        }
    }

    /**
     * Serialize the input data using Java's object serialization mechanism
     */
    private void javaSerialize() {
        try {
            String appendfile = getAppendedFileName();
            System.out.println(appendfile);
            ObjectOutputStream outstream = new ObjectOutputStream(new FileOutputStream(appendfile));

            for (int count = 0; count < data.length; count++) {
                outstream.writeObject(data[count]);
            }

            outstream.close();
        } catch (Exception except) {
            notifyError("Error Serializing Data: " + except.getMessage());
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
        setPopUpDescription("Serialize data to a file");
        setHelpFileLocation("Serialize.html");

        // Define initial value and type of parameters
        defineParameter("type", "Triana Serialize", USER_ACCESSIBLE);
        defineParameter("filename", "", USER_ACCESSIBLE);
        defineParameter("toolname", "", USER_ACCESSIBLE);
        defineParameter("package", "Data", USER_ACCESSIBLE);
        defineParameter("toolbox", "", USER_ACCESSIBLE);
        defineParameter("toolboxes", new String[0], TRANSIENT);
        defineParameter("append", "false", USER_ACCESSIBLE);
        defineParameter("appendCount", "0", USER_ACCESSIBLE);
        defineParameter("sequence", "false", USER_ACCESSIBLE);
        defineParameter("sequenceLength", "1", USER_ACCESSIBLE);

        defineParameter("deserializeTool", DEFAULT_DESERIALIZE_TOOL, USER_ACCESSIBLE);

        // Initialise custom panel interface
        setParameterPanelClass("common.output.SerializePanel");

        // Initialise the toolboxes parameter for serialize panel
        updateToolBoxes();
        getToolTable().addToolTableListener(this);

        resetData();
    }


    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        filename = (String) getParameter("filename");
        type = (String) getParameter("type");
        toolname = (String) getParameter("toolname");
        pack = (String) getParameter("package");
        append = new Boolean((String) getParameter("append")).booleanValue();
        seq = new Boolean((String) getParameter("sequence")).booleanValue();

        appendcount = 0;
        setParameter("appendCount", String.valueOf(0));

        try {
            seqlength = Integer.parseInt((String) getParameter("sequenceLength"));
        } catch (NumberFormatException except) {
            setParameter("sequenceLength", "1");
        }

        resetData();
    }

    private void resetData() {
        if (seq) {
            data = new Object[seqlength];
        } else {
            data = new Object[1];
        }

        ptr = 0;
    }


    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Serialize (e.g. close open files)
    }


    private String getAppendedFileName() {
        if (append) {
            String appfile;

            if (filename.lastIndexOf('.') > -1) {
                appfile = filename.substring(0, filename.lastIndexOf('.')) + appendcount +
                        filename.substring(filename.lastIndexOf('.'));
            } else {
                appfile = filename + appendcount;
            }

            setParameter("appendCount", String.valueOf(appendcount));
            return appfile;
        } else {
            return filename;
        }
    }

    private String getAppendToolName() {
        if (append) {
            return toolname + appendcount;
        } else {
            return toolname;
        }
    }

    private void increaseAppendCount() {
        if (append) {
            appendcount++;
            setParameter("appendCount", String.valueOf(appendcount));
        }
    }


    private void updateToolBoxes() {
        Toolbox[] toolboxes = getToolTable().getToolBoxes(FileToolboxLoader.LOCAL_TYPE);

        ArrayList<String> list = new ArrayList();
        for (int count = 0; count < toolboxes.length; count++) {
            list.add(toolboxes[count].getPath());
        }

        setParameter("toolboxes", list.toArray(new String[list.size()]));
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("filename")) {
            filename = (String) value;
        }

        if (paramname.equals("type")) {
            type = (String) value;
        }

        if (paramname.equals("toolname")) {
            toolname = (String) value;
        }

        if (paramname.equals("package")) {
            pack = (String) value;
        }

        if (paramname.equals("append")) {
            append = new Boolean((String) value).booleanValue();
        }

        if (paramname.equals("appendCount")) {
            appendcount = new Integer((String) value).intValue();
        }

        if (paramname.equals("sequence")) {
            seq = new Boolean((String) value).booleanValue();
            resetData();
        }

        if (paramname.equals("sequenceLength")) {
            try {
                seqlength = Integer.parseInt((String) value);
            } catch (NumberFormatException except) {
                setParameter("sequenceLength", "1");
            }

            resetData();
        }
    }


    /**
     * @return an array of the input types for Serialize
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * @return an array of the output types for Serialize
     */
    public String[] getOutputTypes() {
        return new String[0];
    }


    @Override
    public void toolsAdded(List<Tool> tools) {
    }

    @Override
    public void toolsRemoved(List<Tool> tools) {
    }

    /**
     * Called when a new tool is added
     */
    public void toolAdded(Tool tool) {
    }

    /**
     * Called when a tool is removed
     */
    public void toolRemoved(Tool tool) {
    }

    @Override
    public void toolBoxAdded(Toolbox toolbox) {
    }

    @Override
    public void toolBoxRemoved(Toolbox toolbox) {
    }

    @Override
    public void toolboxNameChanging(Toolbox toolbox, String newName) {
    }

    @Override
    public void toolboxNameChanged(Toolbox toolbox, String newName) {
    }

}



