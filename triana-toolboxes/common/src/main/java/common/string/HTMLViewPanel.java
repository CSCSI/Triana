package common.string;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.windows.WindowButtonConstants;

/**
 * $POPUP_DESCRIPTION
 *
 * @author $AUTHOR
 * @version $Revision: 2921 $
 */

public class HTMLViewPanel extends ParameterPanel implements ItemListener, HyperlinkListener {

    private JEditorPane editpane = new JEditorPane();


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

        editpane.setEditable(false);
        editpane.setContentType("text/html");
        editpane.addHyperlinkListener(this);

        setText(getParameter("str").toString());

        JScrollPane scroll = new JScrollPane(editpane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scroll, BorderLayout.CENTER);
    }

    private void setText(String str) {
        if (str.startsWith("http://")) {
            try {
                editpane.setPage(str.trim());
            } catch (IOException except) {
                editpane.setText("Error: " + except.getMessage());
            }
        } else {
            editpane.setText(str);
        }
    }


    /**
     * This method is called when cancel is clicked on the parameter window. It should synchronize the GUI components
     * with the task parameter values
     */
    public void reset() {
        setText(getParameter("str").toString());
    }


    /**
     * This method is called when a parameter in the task is updated. It should update the GUI in response to the
     * parameter update
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("str")) {
            setText(value.toString());
        }
    }


    /**
     * This method is called when the panel is being disposed off. It should clean-up subwindows, open files etc.
     */
    public void dispose() {
    }


    public void itemStateChanged(ItemEvent event) {
    }


    /**
     * Called when a hypertext link is updated.
     *
     * @param event the event responsible for the update
     */
    public void hyperlinkUpdate(HyperlinkEvent event) {
        if ((event.getSource() == editpane) && (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)) {
            handleLink(event);
        }
    }

    private void handleLink(HyperlinkEvent event) {
        try {
            editpane.setPage(event.getURL());
        } catch (IOException except) {
            JOptionPane.showMessageDialog(this, "Invalid URL: " + event.getURL(), getTask().getToolName(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
