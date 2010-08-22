package common.array;

import java.awt.BorderLayout;

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
public class ArrayViewerPanel extends ParameterPanel {

    private JTextArea textarea = new JTextArea(10, 30);


    /**
     * This method returns WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS by default. It should be overridden if the
     * panel has different preferred set of buttons.
     *
     * @return the panels preferred button combination (as defined in Windows Constants).
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
     * This method is called before the panel is displayed. It should initialise the panel layout.
     */
    public void init() {
        setLayout(new BorderLayout());

        textarea.setEditable(false);
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);

        JPanel labelpanel = new JPanel(new BorderLayout());
        labelpanel.add(new JLabel("Value = "), BorderLayout.WEST);
        labelpanel.setBorder(new EmptyBorder(0, 0, 3, 0));
        add(labelpanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(textarea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scroll, BorderLayout.CENTER);
    }


    /**
     * This method is called when cancel is clicked on the parameter window. It should synchronize the GUI components
     * with the task parameter values
     */
    public void reset() {
        setText((Object[]) getParameter("array"));
    }

    /**
     * This method is called when the panel is being disposed off. It should clean-up subwindows, open files etc.
     */
    public void dispose() {
    }


    /**
     * This method is called when a parameter in the task is updated. It should update the GUI in response to the
     * parameter update
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("array")) {
            setText((Object[]) value);
        }
    }


    private void setText(Object[] array) {
        StringBuffer str = new StringBuffer();
        str.append("Array Length = " + array.length + "\n");

        for (int count = 0; count < array.length; count++) {
            str.append("[").append(count).append("] = ").append(array[count].toString()).append("\n");
        }

        textarea.setText(str.toString());
    }

}
