package signalproc.output;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import org.trianacode.gui.panels.ParameterPanel;


/**
 * $POPUP_DESCRIPTION
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */
public class GnuplotGrapherPanel extends ParameterPanel
        implements ActionListener {

    // Define GUI components here, e.g.
    //
    // private JTextField namelabel = new JTextField();
    private JTextArea plotFeedbackText = new JTextArea(30, 60);
    private JTextField commandText = new JTextField();
    private Vector commandHistory = new Vector();
    private int commandHistoryIndex = 0;
    private JButton sendCommandButton = new JButton("Send");

    /**
     * This method is called before the panel is displayed. It should initialise the panel layout.
     */
    public void init() {
        // Insert code to layout GUI here, e.g.
        //
        // add(namelabel);
        initPanel();
    }

    void initPanel() {
        setLayout(new BorderLayout());
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ctrl L"), MyAction.FOCUS_COMMAND_TEXT);
        getActionMap().put(MyAction.FOCUS_COMMAND_TEXT, new MyAction(MyAction.FOCUS_COMMAND_TEXT));

        plotFeedbackText.setEditable(false);
        JScrollPane p1 = new JScrollPane(plotFeedbackText);
        add(p1, BorderLayout.CENTER);

        JPanel p2 = new JPanel(new BorderLayout());
        add(p2, BorderLayout.NORTH);
        p2.add(commandText, BorderLayout.CENTER);
        commandText.addActionListener(this);
        commandText.getInputMap().put(KeyStroke.getKeyStroke("UP"), MyAction.HIST_PREV);
        commandText.getActionMap().put(MyAction.HIST_PREV, new MyAction(MyAction.HIST_PREV));
        commandText.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), MyAction.HIST_NEXT);
        commandText.getActionMap().put(MyAction.HIST_NEXT, new MyAction(MyAction.HIST_NEXT));
        commandText.getInputMap().put(KeyStroke.getKeyStroke("PAGE_UP"), MyAction.OUTPUT_PAGE_UP);
        commandText.getActionMap().put(MyAction.OUTPUT_PAGE_UP, new MyAction(MyAction.OUTPUT_PAGE_UP));
        commandText.getInputMap().put(KeyStroke.getKeyStroke("PAGE_DOWN"), MyAction.OUTPUT_PAGE_DOWN);
        commandText.getActionMap().put(MyAction.OUTPUT_PAGE_DOWN, new MyAction(MyAction.OUTPUT_PAGE_DOWN));

        sendCommandButton.addActionListener(this);
        p2.add(sendCommandButton, BorderLayout.EAST);
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
        // Insert code to update GUI in response to parameter changes here, e.g.
        // 
        // if (paramname.equals("name"))
        //     namelabel.setText(value);

        if (paramname.equals("plotFeedback")) {
            plotFeedbackText.append((String) value);
            plotFeedbackText.append("\n");
        }
    }


    /**
     * This method is called when the panel is being disposed off. It should clean-up subwindows, open files etc.
     */
    public void dispose() {
        // Insert code to clean-up panel here
    }

    public boolean isAutoCommitByDefault() {
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == commandText || e.getSource() == sendCommandButton) {
            int len = commandHistory.size();
            String str = commandText.getText().trim() + " #" + len;
            commandHistory.add(str);
            commandHistoryIndex = commandHistory.size() - 1;
            setParameter("command", str);
            plotFeedbackText.setCaretPosition(plotFeedbackText.getDocument().getLength());
        }
    }

    class MyAction extends AbstractAction {
        public static final String HIST_PREV = "doHistPrev";
        public static final String HIST_NEXT = "doHistNext";
        public static final String OUTPUT_PAGE_UP = "doPageUp";
        public static final String OUTPUT_PAGE_DOWN = "doPageDown";
        public static final String FOCUS_COMMAND_TEXT = "focusCommandText";

        public MyAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            String cmd = (String) getValue(Action.NAME);
            String old;
            if (cmd.equals(HIST_PREV) && commandHistoryIndex > 0) {
                old = (String) commandHistory.get(--commandHistoryIndex);
                old = old.substring(0, old.indexOf('#')).trim();
                commandText.setText(old);
            } else if (cmd.equals(HIST_NEXT) && commandHistoryIndex < commandHistory.size() - 1) {
                old = (String) commandHistory.get(++commandHistoryIndex);
                old = old.substring(0, old.indexOf('#')).trim();
                commandText.setText(old);
            } else if (cmd.equals(OUTPUT_PAGE_UP)) {
                Rectangle view = plotFeedbackText.getVisibleRect();
                int trans = plotFeedbackText.getScrollableUnitIncrement(view, SwingConstants.VERTICAL, -1);
                view.setLocation((int) view.getX(), (int) (view.getY() - view.getHeight() + trans * 2));
                plotFeedbackText.scrollRectToVisible(view);
            } else if (cmd.equals(OUTPUT_PAGE_DOWN)) {
                Rectangle view = plotFeedbackText.getVisibleRect();
                int trans = plotFeedbackText.getScrollableUnitIncrement(view, SwingConstants.VERTICAL, +1);
                view.setLocation((int) view.getX(), (int) (view.getY() + view.getHeight() - trans * 2));
                plotFeedbackText.scrollRectToVisible(view);
            } else if (cmd.equals(FOCUS_COMMAND_TEXT)) {
                boolean succ = commandText.requestFocusInWindow();
                if (!succ) {
                    System.out.println("commandText can't get focus.");
                }
            }
        }
    }
}
