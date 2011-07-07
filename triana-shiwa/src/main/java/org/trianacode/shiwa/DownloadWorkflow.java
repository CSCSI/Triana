package org.trianacode.shiwa;

import org.shiwa.desktop.gui.SHIWADesktopPanel;
import org.trianacode.gui.hci.ApplicationFrame;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.TaskGraph;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 06/07/2011
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class DownloadWorkflow extends AbstractAction {

    public DownloadWorkflow() {
        putValue(SHORT_DESCRIPTION, "Download Workflow");
        putValue(NAME, "Download Workflow");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        System.out.println("Downloading Workflow");

        ApplicationFrame frame = GUIEnv.getApplicationFrame();
        TaskGraph tg = frame.getSelectedTaskgraph();

        if (tg != null) {

            TrianaShiwaListener tsl = new TrianaShiwaListener(frame.getEngine());

            JPanel popup = new SHIWADesktopPanel();
            ((SHIWADesktopPanel) popup).addSHIWADesktopListener(tsl);

            DisplayDialog dialog = new DisplayDialog(popup);

        } else {
            JOptionPane.showMessageDialog(frame, "No taskgraph selected");
        }
    }
}
