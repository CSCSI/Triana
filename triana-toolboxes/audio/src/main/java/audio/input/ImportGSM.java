package audio.input;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.audio.AudioChannelFormat;
import triana.types.audio.MultipleAudio;

/**
 * A ImportGSM unit to ..
 *
 * @author ian
 * @version 2.0 03 Jan 2001
 */
public class ImportGSM extends Unit {

    String fileName = null;

    public void process() throws Exception {
//        if (fileName == null) {
//            //doubleClick();
//        }

        File fi = new File(fileName);
        long llen = fi.length();
        int len = (int) (llen & 0x7FFFFFFF);
        if (len != llen) {
            System.out.println("File too big to read into a single object");
            return;
        }

        byte[] b = new byte[len];

        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(fi)));
            dis.readFully(b);
            dis.close();
        }
        catch (FileNotFoundException ee) {
            new ErrorDialog(null, "File " + fileName + " NOT FOUND !!!");
            return;
        }
        catch (IOException eee) {
            new ErrorDialog(null, "IO exception on file " + fileName);
            return;
        }

        MultipleAudio ma = new MultipleAudio(1);
        ma.setChannel(0, b, new AudioChannelFormat(8000, (short) 16, AudioChannelFormat.GSM));
        output(ma);
    }


    /**
     * Initialses information specific to ImportGSM.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(0);
        setMinimumInputNodes(0);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Imports GSM Audio Files");
        setHelpFileLocation("ImportGSM.html");

        setParameterPanelClass("audio.input.ImportGSMPanel");
        setParameterPanelInstantiate(ON_USER_ACCESS);

    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        fileName = (String) getParameter("fileName");
    }

    /**
     * Used to set each of ImportGSM's parameters. This should NOT be used to update this unit's user interface
     */

    public void parameterUpdate(String name, String value) {
        // Code to update local variables
        if (name.equals("fileName")) {
            fileName = FileUtils.convertFromVirtualName(value);
            System.out.println(fileName);
        }
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to ImportGSM, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return a string containing the names of the types output from ImportGSM, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Imports a GSM Formatted File and outputs it in GSM Format";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "ImportGSM.html";
    }
}



