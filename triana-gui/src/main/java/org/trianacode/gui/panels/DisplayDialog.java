package org.trianacode.gui.panels;

import org.trianacode.gui.hci.GUIEnv;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 06/07/2011
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class DisplayDialog extends JDialog {
    public DisplayDialog(JPanel panel, String title, Image icon) {
//             this.setModal(true);
        this.setTitle(title);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        this.add(panel);
        this.pack();
        if(icon != null){
            this.setIconImage(icon);
        }
        this.setLocationRelativeTo(GUIEnv.getApplicationFrame());
        //final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //final int x = (screenSize.width - this.getWidth()) / 2;
        //final int y = (screenSize.height - this.getHeight()) / 2;
        //this.setLocation(x, y);
        this.setVisible(true);
    }
}
