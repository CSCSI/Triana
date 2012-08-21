package org.trianacode.shiwaall.handler;

import org.shiwa.desktop.gui.SHIWADesktop;
import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.hci.ApplicationFrame;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.DisplayDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 06/07/2011
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class DownloadWorkflow extends AbstractAction implements ActionDisplayOptions {

    public DownloadWorkflow() {
        this(ActionDisplayOptions.DISPLAY_BOTH);
    }

    public DownloadWorkflow(int displayOption) {
        putValue(SHORT_DESCRIPTION, "Retrieve Bundle");
        putValue(NAME, "Retrieve Bundle");
        if ((displayOption == DISPLAY_ICON) || (displayOption == DISPLAY_BOTH)) {
            putValue(SMALL_ICON, GUIEnv.getIcon("download_small.png"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        System.out.println("Opening SHIWADesktop");

        ApplicationFrame frame = GUIEnv.getApplicationFrame();

        DisplayDialog dialog = null;
        TrianaShiwaListener tsl = new TrianaShiwaListener(frame.getEngine(), dialog);

//        SHIWADesktopOpener shiwaDesktopOpener = new SHIWADesktopOpener();
//        tsl.setReceivedListener(shiwaDesktopOpener);
//        shiwaDesktopOpener.addExecutionListener(tsl);
//        shiwaDesktopOpener.open(null);

        SHIWADesktop shiwaDesktop = new SHIWADesktop(SHIWADesktop.ButtonOption.SHOW_TOOLBAR);
        shiwaDesktop.addExecutionListener(tsl);

        dialog = new DisplayDialog(shiwaDesktop.getPanel(), "SHIWA Desktop");

    }
}
