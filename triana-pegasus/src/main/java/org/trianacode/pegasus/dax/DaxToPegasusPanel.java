package org.trianacode.pegasus.dax;

import org.trianacode.gui.panels.ParameterPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 30, 2010
 * Time: 2:37:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxToPegasusPanel extends ParameterPanel {
    @Override
    public void init() {
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        JPanel selectionsPane = new JPanel(new GridLayout(3,0));

        JPanel rcFieldPane = new JPanel(new BorderLayout());
        JLabel rcLabel = new JLabel("Replica Catalog :");
        JTextField rcField = new JTextField("");
        JButton rcSelectButton = new JButton("Find");
        rcFieldPane.add(rcLabel, BorderLayout.WEST);
        rcFieldPane.add(rcField, BorderLayout.CENTER);
        rcFieldPane.add(rcSelectButton, BorderLayout.EAST);


        JPanel scFieldPane = new JPanel(new BorderLayout());
        JLabel scLabel = new JLabel("Site Catalog :");
        JTextField scField = new JTextField("");
        JButton scSelectButton = new JButton("Find");
        scFieldPane.add(scLabel, BorderLayout.WEST);
        scFieldPane.add(scField, BorderLayout.CENTER);
        scFieldPane.add(scSelectButton, BorderLayout.EAST);


        JPanel tcFieldPane = new JPanel(new BorderLayout());
        JLabel tcLabel = new JLabel("Transformation Catalog :");
        JTextField tcField = new JTextField("");
        JButton tcSelectButton = new JButton("Find");
        tcFieldPane.add(tcLabel, BorderLayout.WEST);
        tcFieldPane.add(tcField, BorderLayout.CENTER);
        tcFieldPane.add(tcSelectButton, BorderLayout.EAST);

        selectionsPane.add(rcFieldPane);
        selectionsPane.add(scFieldPane);
        selectionsPane.add(tcFieldPane);

        mainPane.add(selectionsPane);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(mainPane);
    }

    @Override
    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
