package org.trianacode.shiwaall.handler;

import org.shiwa.desktop.gui.SHIWADesktop;
import org.shiwa.desktop.gui.util.InterfaceUtils;
import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.hci.ApplicationFrame;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.DisplayDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 06/07/2011
 * Time: 14:21
 * To change this template use File | Settings | File Templates.
 */
public class DownloadWorkflow extends AbstractAction implements ActionDisplayOptions {

    /**
     * Instantiates a new download workflow.
     */
    public DownloadWorkflow() {
        this(ActionDisplayOptions.DISPLAY_BOTH);
    }

    /**
     * Instantiates a new download workflow.
     *
     * @param displayOption the display option
     */
    public DownloadWorkflow(int displayOption) {
        putValue(SHORT_DESCRIPTION, "Retrieve Bundle");
        putValue(NAME, "Retrieve Bundle");
        if ((displayOption == DISPLAY_ICON) || (displayOption == DISPLAY_BOTH)) {
//            InterfaceUtils.initImages();
            Icon icon = InterfaceUtils.X16_ICON;
//            putValue(SMALL_ICON, GUIEnv.getIcon("download_small.png"));
            putValue(SMALL_ICON, icon);
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        System.out.println("Opening SHIWADesktop");

        ApplicationFrame frame = GUIEnv.getApplicationFrame();

        DisplayDialog dialog = null;
        TrianaShiwaListener tsl = new TrianaShiwaListener(frame.getEngine());

//        SHIWADesktopOpener shiwaDesktopOpener = new SHIWADesktopOpener();
//        tsl.setReceivedListener(shiwaDesktopOpener);
//        shiwaDesktopOpener.addExecutionListener(tsl);
//        shiwaDesktopOpener.open(null);

        SHIWADesktop shiwaDesktop = new SHIWADesktop(SHIWADesktop.ButtonOption.SHOW_TOOLBAR);
        tsl.addSHIWADesktop(shiwaDesktop);
        shiwaDesktop.addExecutionListener(tsl);

        Image icon = InterfaceUtils.X16_ICON.getImage();
        dialog = new DisplayDialog(shiwaDesktop.getPanel(), "SHIWA Desktop", icon);

    }
}
