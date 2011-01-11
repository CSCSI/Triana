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

    JTextField daxField = new JTextField("");
    JTextField propField = new JTextField("");
    JTextField rcField = new JTextField("");
    JTextField scField = new JTextField("");
    JTextField tcField = new JTextField("");

    private void setParams(){
        getTask().setParameter("daxLocation", daxField.getText());
        getTask().setParameter("propLocation", propField.getText());
        getTask().setParameter("sitesLocation", scField.getText());
        getTask().setParameter("tcLocation", tcField.getText());
        getTask().setParameter("rcLocation", rcField.getText());
    }

    private void getParams(){
        daxField.setText((String)getParameter("daxLocation"));
        propField.setText((String)getParameter("propLocation"));        
        scField.setText((String)getParameter("sitesLocation"));
        tcField.setText((String)getParameter("tcLocation"));
        rcField.setText((String)getParameter("rcLocation"));
    }

    private void apply(){
        setParams();
    }

    public void applyClicked(){apply();}
    public void okClicked(){apply();}

    @Override
    public void init() {
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        JPanel selectionsPane = new JPanel(new GridLayout(5,3));

        JPanel daxFieldPane = new JPanel(new BorderLayout());
        JLabel daxLabel = new JLabel("Dax File:");
        JButton daxSelectButton = new JButton("Find");
        daxFieldPane.add(daxLabel, BorderLayout.WEST);
        daxFieldPane.add(daxField, BorderLayout.CENTER);
        daxFieldPane.add(daxSelectButton, BorderLayout.EAST);

        JPanel propFieldPane = new JPanel(new BorderLayout());
        JLabel propLabel = new JLabel("Properties File :");
        JButton propSelectButton = new JButton("Find");
        propFieldPane.add(propLabel, BorderLayout.WEST);
        propFieldPane.add(propField, BorderLayout.CENTER);
        propFieldPane.add(propSelectButton, BorderLayout.EAST);

        JPanel rcFieldPane = new JPanel(new BorderLayout());
        JLabel rcLabel = new JLabel("Replica Catalog :");
        JButton rcSelectButton = new JButton("Find");
        rcFieldPane.add(rcLabel, BorderLayout.WEST);
        rcFieldPane.add(rcField, BorderLayout.CENTER);
        rcFieldPane.add(rcSelectButton, BorderLayout.EAST);


        JPanel scFieldPane = new JPanel(new BorderLayout());
        JLabel scLabel = new JLabel("Site Catalog :");
        JButton scSelectButton = new JButton("Find");
        scFieldPane.add(scLabel, BorderLayout.WEST);
        scFieldPane.add(scField, BorderLayout.CENTER);
        scFieldPane.add(scSelectButton, BorderLayout.EAST);


        JPanel tcFieldPane = new JPanel(new BorderLayout());
        JLabel tcLabel = new JLabel("Transformation Catalog :");
        JButton tcSelectButton = new JButton("Find");
        tcFieldPane.add(tcLabel, BorderLayout.WEST);
        tcFieldPane.add(tcField, BorderLayout.CENTER);
        tcFieldPane.add(tcSelectButton, BorderLayout.EAST);

        selectionsPane.add(daxFieldPane);
        selectionsPane.add(propFieldPane);
        selectionsPane.add(rcFieldPane);
        selectionsPane.add(scFieldPane);
        selectionsPane.add(tcFieldPane);

        mainPane.add(selectionsPane);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(mainPane);
        getParams();
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
