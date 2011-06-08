package org.trianacode.shiwa;

import org.shiwa.desktop.gui.SHIWADesktopPanel;
import org.trianacode.TrianaInstance;
import org.trianacode.gui.hci.ApplicationFrame;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.TaskGraph;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 22/02/2011
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
public class PublishWorkflow extends AbstractAction {

    public PublishWorkflow() {
        putValue(SHORT_DESCRIPTION, "Publish");
        putValue(NAME, "Publish");
    }

    public void actionPerformed(ActionEvent actionEvent) {
        System.out.println("Publishing Workflow");

        ApplicationFrame frame = GUIEnv.getApplicationFrame();
        TaskGraph tg = frame.getSelectedTaskgraph();

        if (tg != null) {
            TrianaInstance instance = frame.getEngine();

            TrianaEngineHandler teh = new TrianaEngineHandler(instance, tg);

            JPanel popup = new SHIWADesktopPanel(teh);

            DisplayDialog dialog = new DisplayDialog(popup);

        } else {
            JOptionPane.showMessageDialog(frame, "No taskgraph selected");
        }
    }

    class DisplayDialog extends JDialog {
        public DisplayDialog(JPanel panel) {
            this.setModal(true);
            this.setTitle("Shiwa Desktop");
            this.setLocationRelativeTo(this.getOwner());
            this.add(panel);
            this.pack();
            this.setVisible(true);
        }
    }
}
