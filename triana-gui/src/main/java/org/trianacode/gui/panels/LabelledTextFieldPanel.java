package org.trianacode.gui.panels;

import javax.swing.*;
import java.awt.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 29, 2010
 */
public class LabelledTextFieldPanel extends JPanel {


    public LabelledTextFieldPanel(String[] labels, JTextField[] fields) {
        super(new GridBagLayout());
        if (labels.length < fields.length) {
            throw new IllegalArgumentException("not enough labels for text fields");
        }
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < fields.length; i++) {
            JTextField field = fields[i];
            String lab = labels[i];
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 0.0;
            add(new JLabel(lab), c);
            c.gridx = 1;
            c.gridy = i;
            c.weightx = 1.0;
            add(field, c);
        }
    }
}
