package audio.input;

import java.io.File;

import javax.swing.JFileChooser;
import org.trianacode.gui.panels.ParameterPanel;

/**
 * Created by IntelliJ IDEA. User: eddie Date: Sep 7, 2010 Time: 7:39:25 PM To change this template use File | Settings
 * | File Templates.
 */
public class ImportGSMPanel extends ParameterPanel {

        String lastDir = null;

        public void init() {
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

        public void dispose() {
        // Insert code to clean-up panel here
    }

        public void reset() {
        // Insert code to synchronise the GUI with the task parameters here, e.g.
        //
        // namelabel.setText(getParameter("name"));
    }
}
