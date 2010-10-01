package common.lang;


import org.trianacode.gui.panels.ParameterPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * $POPUP_DESCRIPTION
 *
 * @author $AUTHOR
 * @version $Revision: 1.4 $
 * @created $DATE
 * @date $Date: 2006/04/04 13:44:26 $ modified by $Author: spxinw $
 * @todo
 */

public class IsPanel extends ParameterPanel implements ItemListener {

    private JComboBox value;
    private JLabel type1 = new JLabel();
    private JLabel type2 = new JLabel();
    private JCheckBox box = new JCheckBox("Invert test (Not)");

    public static final String EQU = "is equal to";
    public static final String GREATER = "is greater than";
    public static final String LESS = "is less than";
    public static final String IS_A = "is a";
    public static final String HAS_NAME = "has name equal to";

    public static final String TEST_PARAM = "test";

    private String[] enums = new String[]{
            EQU,
            GREATER,
            LESS,
            IS_A,
            HAS_NAME};

    private String curr = enums[0];
    private boolean invert = false;


    /**
     * This method is called before the panel is displayed. It should initialise
     * the panel layout.
     */
    public void init() {
        setLayout(new BorderLayout());
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        value = new JComboBox(enums);
        value.addItemListener(this);
        JPanel labpane = new JPanel();
        labpane.setLayout(new BoxLayout(labpane, BoxLayout.Y_AXIS));
        type1.setText("IN 1=Object");
        type2.setText("IN 2=Object");
        JPanel one = new JPanel();
        one.setLayout(new BoxLayout(one, BoxLayout.X_AXIS));
        one.add(type1);
        one.add(Box.createHorizontalGlue());
        labpane.add(one);
        labpane.add(Box.createRigidArea(new Dimension(3, 3)));
        JPanel two = new JPanel();
        two.setLayout(new BoxLayout(two, BoxLayout.X_AXIS));
        two.add(type2);
        two.add(Box.createHorizontalGlue());
        labpane.add(two);
        JPanel check = new JPanel();
        check.setLayout(new BoxLayout(check, BoxLayout.X_AXIS));
        box.addItemListener(this);
        check.add(box);
        check.add(Box.createHorizontalGlue());
        labpane.add(check);
        pane.add(labpane);
        pane.add(value);
        pane.add(Box.createVerticalGlue());
        add(pane, BorderLayout.NORTH);
        setParameter(TEST_PARAM, curr);

    }


    /**
     * This method is called when cancel is clicked on the parameter window. It
     * should synchronize the GUI components with the task parameter values
     */
    public void reset() {
        curr = enums[0];
    }


    /**
     * This method is called when a parameter in the task is updated. It should
     * update the GUI in response to the parameter update
     */
    public void parameterUpdate(String paramname, Object value) {

    }


    /**
     * This method is called when the panel is being disposed off. It should
     * clean-up subwindows, open files etc.
     */
    public void dispose() {
    }

    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == box) {
            invert = box.isSelected();
            setParameter("invert", new Boolean(invert).toString());
        } else {
            curr = (String) value.getSelectedItem();
            if (curr.equals(GREATER) || curr.equals(LESS)) {
                type1.setText("IN 1=Comparable");
                type2.setText("IN 2=Comparable");
            } else if (curr.equals(HAS_NAME)) {
                type1.setText("IN 1=Object");
                type2.setText("IN 2=String");
            } else {
                type1.setText("IN 1=Object");
                type2.setText("IN 2=Object");
            }
            setParameter(TEST_PARAM, curr);
        }
    }


}
