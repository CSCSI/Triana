package org.trianacode.pegasus.dax;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Sep 9, 2010
 * Time: 1:41:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxJobSubtitle extends JPanel {

        protected void paintComponent(Graphics graphs) {
            super.paintComponent(graphs);
            Color c = graphs.getColor();

            graphs.setColor(Color.black);
            graphs.drawLine(50, 50, 30, 30);
            graphs.setColor(c);
        }
}
