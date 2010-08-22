package common.string;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.windows.WindowButtonConstants;


/**
 * $POPUP_DESCRIPTION
 *
 * @author $AUTHOR
 * @version $Revision: 2921 $
 */

public class StringViewPanel extends ParameterPanel implements ItemListener {

    private JTextArea textarea = new JTextArea(10, 30);
    private JCheckBox append = new JCheckBox("Append values", false);


    /**
     * This method returns WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS by default. It should be overridden if the
     * panel has different preferred set of buttons.
     *
     * @return the panels preferred button combination (as defined in Windows Constants).
     * @see WindowButtonConstants
     */
    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_BUTTON;
    }

    /**
     * This method returns true by default. It should be overridden if the panel does not want the user to be able to
     * change the auto commit state
     */
    public boolean isAutoCommitVisible() {
        return false;
    }

    /**
     * Commit append value immediately
     */
    public boolean isAutoCommitByDefault() {
        return true;
    }


    /**
     * This method is called before the panel is displayed. It should initialise the panel layout.
     */
    public void init() {
        setLayout(new BorderLayout());

        textarea.setEditable(false);
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);

        setText(getParameter("str").toString());

        append.setSelected(new Boolean((String) getParameter("append")).booleanValue());
        append.addItemListener(this);

        JPanel appendpanel = new JPanel(new BorderLayout());
        appendpanel.add(new JLabel("Value = "), BorderLayout.WEST);
        appendpanel.add(append, BorderLayout.EAST);
        appendpanel.setBorder(new EmptyBorder(0, 0, 3, 0));
        add(appendpanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(textarea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scroll, BorderLayout.CENTER);
    }

    private void setText(String str) {
        textarea.setText(str);
    }


    /**
     * This method is called when cancel is clicked on the parameter window. It should synchronize the GUI components
     * with the task parameter values
     */
    public void reset() {
        setText(getParameter("str").toString());
        append.setSelected(new Boolean((String) getParameter("append")).booleanValue());
    }


    /**
     * This method is called when a parameter in the task is updated. It should update the GUI in response to the
     * parameter update
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("str")) {
            setText(value.toString());
        }

        if (paramname.equals("append")) {
            append.setSelected(new Boolean((String) value).booleanValue());
        }
    }


    /**
     * This method is called when the panel is being disposed off. It should clean-up subwindows, open files etc.
     */
    public void dispose() {
    }


    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == append) {
            setParameter("append", String.valueOf(append.isSelected()));
        }
    }

}
