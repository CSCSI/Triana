package org.trianacode.shiwa;

import org.shiwa.desktop.gui.SHIWADesktopPanel;
import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.files.ImageAction;
import org.trianacode.gui.hci.ApplicationFrame;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.TaskGraph;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 22/02/2011
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
public class PublishWorkflow extends AbstractAction implements ActionDisplayOptions {

    public PublishWorkflow() {
        this(DISPLAY_BOTH);
    }

    public PublishWorkflow(int displayOption) {
        putValue(SHORT_DESCRIPTION, "Publish");
        putValue(NAME, "Publish");
        if ((displayOption == DISPLAY_ICON) || (displayOption == DISPLAY_BOTH)) {
            putValue(SMALL_ICON, GUIEnv.getIcon("upload_small.png"));
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        System.out.println("Publishing Workflow");

        ApplicationFrame frame = GUIEnv.getApplicationFrame();
        TaskGraph tg = frame.getSelectedTaskgraph();

        if (tg != null) {

            InputStream displayStream = null;
            try {
                File imageFile = File.createTempFile("image", ".jpg");
                ImageAction.save(imageFile, 1, "jpg");
                displayStream = new FileInputStream(imageFile);
                System.out.println("Display image created : " + imageFile.toURI());

            } catch (IOException e) {
                e.printStackTrace();
            }
            TrianaEngineHandler teh = new TrianaEngineHandler(tg, frame.getEngine(), displayStream);


            JPanel popup = new SHIWADesktopPanel(teh);
            ((SHIWADesktopPanel) popup).addSHIWADesktopListener(new TrianaShiwaListener(frame.getEngine()));
            DisplayDialog dialog = new DisplayDialog(popup);

        } else {
            JOptionPane.showMessageDialog(frame, "No taskgraph selected");
        }
    }
}
