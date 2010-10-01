package audio.output;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.audio.AudioChannelFormat;
import triana.types.audio.MultipleAudio;
import triana.types.audio.gsm.encoder.Encoder;


/**
 * A WriteGSM unit to write GSM format to an output file
 *
 * @author ian
 * @version 2.0 03 Jan 2001
 */
public class WriteGSM extends Unit {

    Encoder gsm = new Encoder();
    String fileName = null;
    String lastDir = null;

    /**
     * ********************************************* ** USER CODE of WriteGSM goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        if (fileName == null) {
            doubleClick();
        }

        MultipleAudio audio = (MultipleAudio) getInputAtNode(0);

        AudioChannelFormat format = audio.getAudioChannelFormat(0);

        byte[] bytedata = null;

        if ((format.getEncoding() == AudioChannelFormat.PCM) &&
                (format.getSampleSize() == 16) &&
                (format.getSamplingRate() == 8000)) { // OK to convert
            bytedata = gsm.process((short[]) audio.getChannel(0));
        } else if (format.getEncoding() == AudioChannelFormat.GSM) // formatted
        {
            bytedata = (byte[]) audio.getChannel(0);
        }

        if (bytedata == null) {
            ErrorDialog.show("Error in " + getToolName() + " Cannot convert given format to GSM\n"
                    + "Data MUST be 16-bit and have a sampling rate of 8KHz");
            stop();
            return;
        }

        File fi = new File(fileName);

        FileOutputStream to = null;
        try {
            to = new FileOutputStream(fi);
            to.write(bytedata);
            to.flush();
            to.close();
        } catch (Exception e) {
            throw new Exception("Encoder: " + e.getMessage());
        }
    }


    /**
     * Initialses information specific to WriteGSM.
     */
    public void init() {
        super.init();

//        setResizableInputs(false);
//        setResizableOutputs(true);

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);
    }

    /**
     * Called when the reset button is pressed within the MainTriana Window
     */
    public void reset() {
        super.reset();
    }

    /**
     * Called when the stop button is pressed within the MainTriana Window
     */
    public void stopping() {
        super.stopping();
    }

    /**
     * Called when the start button is pressed within the MainTriana Window
     */
//    public void starting() {
//        super.starting();
//    }

    public void doubleClick() {
        try {
            File file;
            if (lastDir == null) {
                file = new File(System.getProperty("user.dir"));
            } else {
                file = new File(lastDir);
            }

            JFileChooser fc = new JFileChooser(file);
            fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    String name = f.getName();
                    if (name.endsWith(".gsm") || name.endsWith(".GSM")) {
                        return true;
                    }
                    return false;
                }

                public String getDescription() {
                    return ".gsm, .GSM";
                }
            });

            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                parameterUpdate("fileName", fc.getSelectedFile().getAbsolutePath());
                lastDir = fc.getSelectedFile().getPath();
            }
        } catch (SecurityException ex) {
            // JavaSound.showInfoDialog();
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Saves WriteGSM's parameters.
     */
//    public void saveParameters() {
//        saveParameter("fileName", FileUtils.convertToVirtualName(fileName));
//    }

    /**
     * Used to set each of WriteGSM's parameters. This should NOT be used to update this unit's user interface
     */
    public void parameterUpdate(String name, Object value) {
        if (name.equals("fileName")) {
            fileName = FileUtils.convertFromVirtualName((String) value);
            System.out.println(fileName);
        }
    }

    /**
     * Used to update the widget in this unit's user interface that is used to control the given parameter name.
     */
    public void updateWidgetFor(String name) {
    }

    /**
     * @return a string containing the names of the types allowed to be input to WriteGSM, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.audio.MultipleAudio"};
    }

    /**
     * @return a string containing the names of the types output from WriteGSM, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "WriteGSM writes a 16-bit 8KHz signal to a GSM File Format";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "WriteGSM.html";
    }
}



