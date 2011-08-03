package org.trianacode.shiwa;

import org.shiwa.desktop.gui.SHIWADesktopPanel;
import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.hci.ApplicationFrame;
import org.trianacode.gui.hci.GUIEnv;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 06/07/2011
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class DownloadWorkflow extends AbstractAction implements ActionDisplayOptions {

    public DownloadWorkflow() {
        this(ActionDisplayOptions.DISPLAY_BOTH);
    }

    public DownloadWorkflow(int displayOption) {
        putValue(SHORT_DESCRIPTION, "Download Workflow");
        putValue(NAME, "Download Workflow");
        if ((displayOption == DISPLAY_ICON) || (displayOption == DISPLAY_BOTH)) {
            putValue(SMALL_ICON, GUIEnv.getIcon("download_small.png"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        System.out.println("Downloading Workflow");

        ApplicationFrame frame = GUIEnv.getApplicationFrame();

        TrianaShiwaListener tsl = new TrianaShiwaListener(frame.getEngine());

        JPanel popup = new SHIWADesktopPanel();
        ((SHIWADesktopPanel) popup).addSHIWADesktopListener(tsl);

        DisplayDialog dialog = new DisplayDialog(popup);

    }
}
