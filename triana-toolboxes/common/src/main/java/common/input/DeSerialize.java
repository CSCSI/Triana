package common.input;


import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.Tool;

import java.io.*;


/**
 * Output the data from a serialized file
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class DeSerialize extends Unit {

    private static final String INTERNAL = "Internal Data";

    // parameter data type definitions
    private String filename;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        if (filename.equals(INTERNAL) || (filename.equals(""))) {
            outputInternalData();
        } else {
            outputFileData();
        }
    }

    /**
     * Deserialize and output data stored in the file
     */
    private void outputFileData() throws IOException {
        // try to deserialize as a Triana tool
        try {
            XMLReader xmlreader = new XMLReader(new FileReader(filename));
            Tool tool = xmlreader.readComponent(getTask().getProperties());
            xmlreader.close();

            int datalen = 1;

            if (isParameter("dataLength")) {
                datalen = Integer.parseInt((String) getParameter("dataLength"));
            }

            for (int count = 0; count < datalen; count++) {
                if (tool.isParameterName("data" + count)) {
                    output(tool.getParameter("data" + count));
                } else {
                    if (datalen == 1) {
                        notifyError("Error deserializing " + tool.getToolName() + ": No internal data specified");
                    } else {
                        notifyError("Error deserializing " + tool.getToolName() + ": Incomplete sequence data");
                    }

                    return;
                }
            }
        } catch (Exception except) {
            try {
                ObjectInputStream instream = new ObjectInputStream(new FileInputStream(filename));

                try {
                    do {
                        output(instream.readObject());
                    } while (true);
                } catch (EOFException eof) {
                }

                instream.close();
            } catch (Exception except2) {
                notifyError("Error deserializing " + new File(filename).getName() + ": " + except2.getMessage());
            }
        }
    }

    /**
     * Output data stored in the internal data parameter
     */
    private void outputInternalData() {
        int datalen = 1;

        if (isParameter("dataLength")) {
            datalen = Integer.parseInt((String) getParameter("dataLength"));
        }

        for (int count = 0; count < datalen; count++) {
            if (isParameter("data" + count)) {
                output(getParameter("data" + count));
            } else {
                if (count == 0) {
                    notifyError("Error deserializing data: No internal data specified");
                } else {
                    notifyError("Error deserializing data: Incomplete sequenc data");
                }

                return;
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
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Output the data from a serialized file");
        setHelpFileLocation("DeSerialize.html");

        // Define initial value and type of parameters
        defineParameter("filename", "", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "File Name $title filename File null *.*\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        filename = (String) getParameter("filename");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up DeSerialize (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("filename")) {
            filename = (String) value;
        }
    }


    /**
     * @return an array of the input types for DeSerialize
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for DeSerialize
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

}



