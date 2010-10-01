package imageproc.processing.effects;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.taskgraph.Unit;

public class ConvolveWeights extends UnitPanel implements ActionListener {
    ActionListener listenerUnit;
    JTextField textField00;
    JTextField textField10;
    JTextField textField20;
    JTextField textField01;
    JTextField textField11;
    JTextField textField21;
    JTextField textField02;
    JTextField textField12;
    JTextField textField22;
    JButton setButton;
    int[] weights = new int[9];

    public ConvolveWeights() {
        super();
    }

    public void setObject(Unit unit) {
        super.setObject(unit);

        textField00 = new JTextField("0", 4);
        textField10 = new JTextField("0", 4);
        textField20 = new JTextField("0", 4);
        textField01 = new JTextField("0", 4);
        textField11 = new JTextField("1", 4);
        textField21 = new JTextField("0", 4);
        textField02 = new JTextField("0", 4);
        textField12 = new JTextField("0", 4);
        textField22 = new JTextField("0", 4);

        textField00.addActionListener(this);
        textField10.addActionListener(this);
        textField20.addActionListener(this);
        textField01.addActionListener(this);
        textField11.addActionListener(this);
        textField21.addActionListener(this);
        textField02.addActionListener(this);
        textField12.addActionListener(this);
        textField22.addActionListener(this);

        JPanel weightsPanel = new JPanel();
        weightsPanel.setLayout(new GridLayout(3, 3, 5, 5));
        weightsPanel.add(textField00);
        weightsPanel.add(textField10);
        weightsPanel.add(textField20);
        weightsPanel.add(textField01);
        weightsPanel.add(textField11);
        weightsPanel.add(textField21);
        weightsPanel.add(textField02);
        weightsPanel.add(textField12);
        weightsPanel.add(textField22);

        setLayout(new BorderLayout(5, 5));
        add("Center", weightsPanel);
    }


    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == setButton) {
            String wts = textField00.getText() + " " + textField10.getText() + " " +
                    textField20.getText() + " " + textField01.getText() + " " +
                    textField11.getText() + " " + textField21.getText() + " " +
                    textField02.getText() + " " + textField12.getText() + " " + textField22.getText();
            updateParameter("weights", wts);
        }
    }
}













