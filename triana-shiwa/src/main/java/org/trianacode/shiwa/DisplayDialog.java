package org.trianacode.shiwa;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 06/07/2011
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class DisplayDialog extends JDialog {
    public DisplayDialog(JPanel panel) {
        //     this.setModal(true);
        this.setTitle("Shiwa Desktop");
        this.setLocationRelativeTo(this.getOwner());
        this.add(panel);
        this.pack();
        this.setVisible(true);
    }
}
