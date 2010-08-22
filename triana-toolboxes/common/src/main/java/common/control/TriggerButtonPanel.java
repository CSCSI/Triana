package common.control;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.windows.WindowButtonConstants;


/**
 * $POPUP_DESCRIPTION
 *
 * @author $AUTHOR
 * @version $Revision: 2921 $
 */
public class TriggerButtonPanel extends ParameterPanel implements ActionListener {

    private JButton trigger = new JButton("Trigger");
    private JButton stop = new JButton("Stop");

    private JLabel count = new JLabel("Stopped");

    private int trigid = 0;


    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_BUTTON;
    }

    public boolean isAutoCommitByDefault() {
        return true;
    }

    public boolean isAutoCommitVisible() {
        return false;
    }


    /**
     * This method is called before the panel is displayed. It should initialise the panel layout.
     */
    public void init() {
        setLayout(new BorderLayout(0, 5));

        Font font = getFont();
        trigger.setFont(font.deriveFont(font.getSize2D() * 2));

        JPanel trigpanel = new JPanel();
        trigpanel.add(trigger);

        JPanel triglabel = new JPanel();
        triglabel.add(new JLabel("Trigger Count :"));
        triglabel.add(count);

        JPanel trigcont = new JPanel(new BorderLayout());
        trigcont.add(trigpanel, BorderLayout.CENTER);
        trigcont.add(triglabel, BorderLayout.SOUTH);

        JPanel stoppanel = new JPanel();
        stoppanel.add(stop);

        trigger.addActionListener(this);
        stop.addActionListener(this);

        add(trigcont, BorderLayout.CENTER);
        add(stoppanel, BorderLayout.SOUTH);
    }


    /**
     * This method is called when cancel is clicked on the parameter window. It should synchronize the GUI components
     * with the task parameter values
     */
    public void reset() {
        // Insert code to synchronise the GUI with the task parameters here, e.g.
        //
        // namelabel.setText(getParameter("name"));         
    }


    /**
     * This method is called when a parameter in the task is updated. It should update the GUI in response to the
     * parameter update
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("triggercount")) {
            if (Integer.parseInt((String) value) == -1) {
                count.setText("Stopped");
            } else {
                count.setText(value.toString());
            }
        }
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == trigger) {
            setParameter("buttonevent", String.valueOf(++trigid));
        }

        if (event.getSource() == stop) {
            setParameter("buttonevent", "STOP");
        }
    }


    /**
     * This method is called when the panel is being disposed off. It should clean-up subwindows, open files etc.
     */
    public void dispose() {
        // Insert code to clean-up panel here
    }

}
