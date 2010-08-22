package audio.input;

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


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.OldUnit;
import triana.types.audio.AudioChannelFormat;
import triana.types.audio.MultipleAudio;

/**
 * A ImportGSM unit to ..
 *
 * @author ian
 * @version 2.0 03 Jan 2001
 */
public class ImportGSM extends OldUnit {

    String fileName = null;
    String lastDir = null;

    /**
     * ********************************************* ** USER CODE of ImportGSM goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        if (fileName == null) {
            doubleClick();
        }

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
        ma.setChannel(0, b,
                new AudioChannelFormat(8000, (short) 16, AudioChannelFormat.GSM));
        output(ma);
    }


    /**
     * Initialses information specific to ImportGSM.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(true);
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
    public void starting() {
        super.starting();
    }

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

            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                updateParameter("fileName", fc.getSelectedFile().getAbsolutePath());
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
     * Saves ImportGSM's parameters.
     */
    public void saveParameters() {
        saveParameter("fileName", FileUtils.convertToVirtualName(fileName));
    }

    /**
     * Used to set each of ImportGSM's parameters. This should NOT be used to update this unit's user interface
     */
    public void setParameter(String name, String value) {
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
    public String inputTypes() {
        return "none";
    }

    /**
     * @return a string containing the names of the types output from ImportGSM, each separated by a white space.
     */
    public String outputTypes() {
        return "triana.types.audio.MultipleAudio";
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



