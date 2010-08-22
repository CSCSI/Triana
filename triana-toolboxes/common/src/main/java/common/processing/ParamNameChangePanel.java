package common.processing;


import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.trianacode.gui.panels.ParameterPanel;

/**
 * $POPUP_DESCRIPTION
 *
 * @author $AUTHOR
 * @version $Revision: 2921 $
 */
public class ParamNameChangePanel extends ParameterPanel implements FocusListener {

    JTextField inputParamName;
    JTextField outputParamName;

    public void init() {

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Input Parameter Name"), BorderLayout.WEST);
        inputParamName = new JTextField(10);
        inputPanel.add(inputParamName, BorderLayout.EAST);
        inputParamName.addFocusListener(this);

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(new JLabel("Output Parameter Name"), BorderLayout.WEST);
        outputParamName = new JTextField(10);
        outputPanel.add(outputParamName, BorderLayout.EAST);
        outputParamName.addFocusListener(this);

        setLayout(new BorderLayout());

        JPanel together = new JPanel(new BorderLayout());
        together.add(inputPanel, BorderLayout.NORTH);
        together.add(outputPanel, BorderLayout.SOUTH);

        add(together, BorderLayout.CENTER);
    }


    /**
     * This method is called when cancel is clicked on the parameter window. It should synchronize the GUI components
     * with the task parameter values
     */
    public void reset() {
        inputParamName.setText((String) getParameter("IParam"));
        outputParamName.setText((String) getParameter("OParam"));
    }


    /**
     * This method is called when a parameter in the task is updated. It should update the GUI in response to the
     * parameter update
     */
    public void parameterUpdate(String paramname, Object value) {
        // Insert code to update GUI in response to parameter changes here, e.g.
        // 
        // if (paramname.equals("name"))
        //     namelabel.setText(value);
    }

    public void dispose() {
        // Insert code to clean-up panel here
    }

    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        if ((event.getSource() == inputParamName) && (!inputParamName.getText().equals(""))) {
            setParameter("IParam", inputParamName.getText());
        }
        if ((event.getSource() == outputParamName) && (!outputParamName.getText().equals(""))) {
            setParameter("OParam", outputParamName.getText());
        }
    }

}
