package common.logic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.event.ControlTaskStateEvent;
import org.trianacode.taskgraph.event.TaskGraphCableEvent;
import org.trianacode.taskgraph.event.TaskGraphListener;
import org.trianacode.taskgraph.event.TaskGraphTaskEvent;
import org.trianacode.taskgraph.tool.Tool;


/**
 * A panel for specifying the exit condition on a loop.
 *
 * @author $AUTHOR
 * @version $Revision: 2921 $
 */

public class LoopPanel extends ParameterPanel
        implements TaskGraphListener, ItemListener, FocusListener, ActionListener {

    public static final int PRE_DATA_COUNT = 3;
    public static final int VARIABLE_COUNT = 4;

    private int nodecount = 0;

    private ArrayList paramlist = new ArrayList();
    private ArrayList complist = new ArrayList();
    private ArrayList eqlist = new ArrayList();

    private ArrayList iterlist = new ArrayList();
    private ArrayList initlist = new ArrayList();

    private JCheckBox basiccheck = new JCheckBox();
    private JLabel iterlabel = new JLabel("0");
    private JPanel conditionpanel = new JPanel();
    private JButton add = new JButton("Add");

    private JCheckBox advancedcheck = new JCheckBox();
    private JTextArea advancedcond = new JTextArea(8, 25);


    /**
     * This method is called before the panel is displayed. It should initialise the panel layout.
     */
    public void init() {
        initParameters();

        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Basic", null, getBasicPanel(), "Set basic exit conditions");
        tabs.addTab("Advanced", null, getAdvancedPanel(), "Set advanced exit conditions");
        tabs.addTab("Variables", null, getVariablesPanel(), "Set variable assignments");

        add(tabs, BorderLayout.CENTER);

        reset();
    }


    private void initParameters() {
        Task task = getTask();

        for (int count = 0; count < VARIABLE_COUNT; count++) {
            if (!task.isParameterName("$var" + count)) {
                task.setParameterType("$var" + count, Tool.USER_ACCESSIBLE);
                task.setParameter("$var" + count, "0");
            }
        }
    }


    private JPanel getBasicPanel() {
        JPanel basicpanel = new JPanel(new BorderLayout(0, 5));

        JPanel iterpanel = new JPanel(new BorderLayout(3, 0));
        iterpanel.add(new JLabel("Iterations :", JLabel.RIGHT), BorderLayout.WEST);
        iterpanel.add(iterlabel, BorderLayout.CENTER);

        JPanel checkpanel = new JPanel(new BorderLayout(3, 0));
        checkpanel.add(basiccheck, BorderLayout.WEST);
        checkpanel.add(new JLabel("Enable Conditional Looping"), BorderLayout.CENTER);
        checkpanel.add(iterpanel, BorderLayout.EAST);
        basiccheck.addItemListener(this);

        JPanel condpanel = new JPanel(new BorderLayout());
        condpanel.add(new JLabel("Exit Conditions"), BorderLayout.NORTH);
        condpanel.add(conditionpanel, BorderLayout.CENTER);

        for (int count = 0; count < 4; count++) {
            addCondition();
        }

        JPanel condcont = new JPanel(new BorderLayout());
        condcont.add(condpanel, BorderLayout.NORTH);

        JPanel addpanel = new JPanel(new BorderLayout());
        addpanel.add(add, BorderLayout.EAST);
        add.addActionListener(this);

        basicpanel.add(checkpanel, BorderLayout.NORTH);
        basicpanel.add(condcont, BorderLayout.CENTER);
        basicpanel.add(addpanel, BorderLayout.SOUTH);

        return basicpanel;
    }

    private JPanel getAdvancedPanel() {
        JPanel advancedpanel = new JPanel(new BorderLayout(0, 5));

        JPanel checkpanel = new JPanel(new BorderLayout(3, 0));
        checkpanel.add(advancedcheck, BorderLayout.WEST);
        checkpanel.add(new JLabel("Enable Advanced Conditions"), BorderLayout.CENTER);
        advancedcheck.addItemListener(this);

        JPanel condpanel = new JPanel(new BorderLayout());
        condpanel.add(new JLabel("Exit Conditions"), BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(advancedcond, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        condpanel.add(scroll, BorderLayout.CENTER);
        advancedcond.addFocusListener(this);

        advancedpanel.add(checkpanel, BorderLayout.NORTH);
        advancedpanel.add(condpanel, BorderLayout.CENTER);

        return advancedpanel;
    }

    private JPanel getVariablesPanel() {
        JPanel varpanel = new JPanel(new GridLayout(VARIABLE_COUNT + 1, 1, 0, 3));
        JPanel initpanel = new JPanel(new GridLayout(VARIABLE_COUNT + 1, 1, 0, 3));
        JPanel iterpanel = new JPanel(new GridLayout(VARIABLE_COUNT + 1, 1, 0, 3));

        JTextField initfield;
        JTextField iterfield;

        JLabel varlabel = new JLabel("");
        JLabel initlabel = new JLabel("Init");
        JLabel iterlabel = new JLabel("Iteration");

        varlabel.setVerticalAlignment(JLabel.BOTTOM);
        initlabel.setVerticalAlignment(JLabel.BOTTOM);
        iterlabel.setVerticalAlignment(JLabel.BOTTOM);

        varpanel.add(varlabel);
        initpanel.add(initlabel);
        iterpanel.add(iterlabel);

        for (int count = 0; count < VARIABLE_COUNT; count++) {
            varpanel.add(new JLabel("$var" + count + " ="));

            initfield = new JTextField(10);
            iterfield = new JTextField(12);

            initlist.add(initfield);
            iterlist.add(iterfield);

            initpanel.add(initfield);
            iterpanel.add(iterfield);

            initfield.addFocusListener(this);
            iterfield.addFocusListener(this);
        }

        JPanel variablepanel = new JPanel(new BorderLayout(3, 0));
        variablepanel.add(varpanel, BorderLayout.WEST);
        variablepanel.add(initpanel, BorderLayout.CENTER);

        JPanel variablepanel1 = new JPanel(new BorderLayout(3, 0));
        variablepanel1.add(variablepanel, BorderLayout.WEST);
        variablepanel1.add(iterpanel, BorderLayout.CENTER);

        JPanel variablepanel2 = new JPanel(new BorderLayout());
        variablepanel2.add(variablepanel1, BorderLayout.NORTH);

        return variablepanel2;
    }


    private void addCondition() {
        JComboBox paramcombo = getParamCombo();
        JComboBox compcombo = getCompCombo();
        JTextField eqfield = new JTextField(18);

        paramlist.add(paramcombo);
        complist.add(compcombo);
        eqlist.add(eqfield);

        paramcombo.addFocusListener(this);
        compcombo.addFocusListener(this);
        eqfield.addFocusListener(this);

        conditionpanel.setLayout(new GridLayout(paramlist.size(), 1, 0, 3));

        JPanel condpanel1 = new JPanel(new BorderLayout(3, 0));
        condpanel1.add(paramcombo, BorderLayout.WEST);
        condpanel1.add(compcombo, BorderLayout.CENTER);

        JPanel eqpanel = new JPanel(new BorderLayout());
        eqpanel.add(eqfield, BorderLayout.SOUTH);

        JPanel condpanel2 = new JPanel(new BorderLayout(3, 0));
        condpanel2.add(condpanel1, BorderLayout.WEST);
        condpanel2.add(eqpanel, BorderLayout.CENTER);

        conditionpanel.add(condpanel2);

        repack();
    }

    private JComboBox getParamCombo() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        JComboBox combo = new JComboBox(model);

        populateParamCombo(combo);

        combo.addFocusListener(this);
        combo.setSelectedItem(null);
        return combo;
    }

    private void populateParamCombo(JComboBox combo) {
        Object selected = combo.getSelectedItem();

        DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();
        model.removeAllElements();

        model.addElement("");
        model.addElement("iterations");
        model.addElement("total iterations");

        for (int count = 0; count < VARIABLE_COUNT; count++) {
            model.addElement("$var" + count);
        }

        if (getTask().getParent() != null) {
            ArrayList params = new ArrayList();
            makeParameterList(getTask().getParent(), params, "");

            Iterator iter = params.iterator();
            while (iter.hasNext()) {
                model.addElement(iter.next());
            }

            combo.setSelectedItem(selected);
        }
    }

    private void updateDataVariables(JComboBox combo) {
        DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();

        while (((String) model.getElementAt(PRE_DATA_COUNT)).startsWith("$data")) {
            model.removeElementAt(PRE_DATA_COUNT);
        }

        if (isControlTask()) {
            Task parent = getTask().getParent();
            nodecount = Math.max(parent.getDataInputNodeCount(), parent.getDataOutputNodeCount());
        } else {
            nodecount = (getTask().getDataInputNodeCount() / 2) + (getTask().getDataInputNodeCount() % 2);
        }

        for (int count = 0; count < nodecount; count++) {
            model.insertElementAt("$data" + count, PRE_DATA_COUNT + count);
        }
    }

    /**
     * @return true if the task is acting a group control task
     */
    private boolean isControlTask() {
        Task task = getTask();
        TaskGraph parent = task.getParent();

        if (parent != null) {
            return parent.getControlTask() == task;
        } else {
            return false;
        }
    }


    private JComboBox getCompCombo() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        JComboBox combo = new JComboBox(model);

        model.addElement("=");
        model.addElement("!=");
        model.addElement(">");
        model.addElement("<");
        model.addElement(">=");
        model.addElement("<=");

        return combo;
    }


    private void makeParameterList(Tool tool, ArrayList list, String base) {
        if (tool != getTask()) {
            String[] paramnames = tool.getParameterNames();

            for (int count = 0; count < paramnames.length; count++) {
                if ((tool.getParameterType(paramnames[count]).equals(Tool.USER_ACCESSIBLE)) && (!paramnames[count]
                        .equals("TRIGGER"))) {
                    list.add(base + paramnames[count]);
                }
            }
        }

        if (tool instanceof TaskGraph) {
            Task[] tasks = ((TaskGraph) tool).getTasks(true);

            for (int count = 0; count < tasks.length; count++) {
                makeParameterList(tasks[count], list, base + tasks[count] + '.');
            }

            ((TaskGraph) tool).addTaskGraphListener(this);
        }
    }


    private void updateEnabled() {
        advancedcond.setEnabled(advancedcheck.isSelected());

        Iterator iter = paramlist.iterator();
        while (iter.hasNext()) {
            ((JComboBox) iter.next()).setEnabled(basiccheck.isSelected() && (!advancedcheck.isSelected()));
        }

        iter = complist.iterator();
        while (iter.hasNext()) {
            ((JComboBox) iter.next()).setEnabled(basiccheck.isSelected() && (!advancedcheck.isSelected()));
        }

        iter = eqlist.iterator();
        while (iter.hasNext()) {
            ((JTextField) iter.next()).setEnabled(basiccheck.isSelected() && (!advancedcheck.isSelected()));
        }
    }

    private void updateEquation() {
        if (!advancedcheck.isSelected()) {
            String equation = "";

            Iterator paramiter = paramlist.iterator();
            Iterator compiter = complist.iterator();
            Iterator eqiter = eqlist.iterator();
            JComboBox param;
            JComboBox comp;
            JTextField eq;
            boolean flag = false;

            while (paramiter.hasNext()) {
                param = (JComboBox) paramiter.next();
                comp = (JComboBox) compiter.next();
                eq = (JTextField) eqiter.next();

                if ((param.getSelectedItem() != null) && (!param.getSelectedItem().equals("")) && (!eq.getText()
                        .equals(""))) {
                    if (flag) {
                        equation += "\n&& ";
                    }

                    if (param.getSelectedItem().equals("total iterations")) {
                        equation += "(totalIterations)";
                    } else {
                        equation += "(" + ((String) param.getSelectedItem());
                    }

                    equation += " " + ((String) comp.getSelectedItem()) + " ";
                    equation += " " + eq.getText() + ")";

                    flag = true;
                }
            }

            advancedcond.setText(equation);
            setParameter("exitCondition", equation);
        } else {
            setParameter("exitCondition", advancedcond.getText());
        }
    }


    /**
     * This method is called when cancel is clicked on the parameter window. It should synchronize the GUI components
     * with the task parameter values
     */
    public void reset() {
        iterlabel.setText((String) getParameter("iterations"));
        basiccheck.setSelected(new Boolean((String) getParameter("enabled")).booleanValue());

        if (!isParameterName("exitCondition")) {
            getTask().setParameterType("exitCondition", Tool.USER_ACCESSIBLE);
            getTask().setParameter("exitCondition", "");
        }

        advancedcond.setText((String) getParameter("exitCondition"));

        if (isParameterName("advanced")) {
            advancedcheck.setSelected(new Boolean((String) getParameter("advanced")).booleanValue());
        }

        boolean flag = true;
        int count = 0;

        while (flag) {
            flag = false;

            if (isParameterName("param" + count)) {
                parameterUpdate("param" + count, getParameter("param" + count));
                flag = true;
            }

            if (isParameterName("comp" + count)) {
                parameterUpdate("comp" + count, getParameter("comp" + count));
                flag = true;
            }

            if (isParameterName("eq" + count)) {
                parameterUpdate("eq" + count, getParameter("eq" + count));
                flag = true;
            }

            if (isParameterName("init$" + count)) {
                parameterUpdate("init$" + count, getParameter("init$" + count));
                flag = true;
            }

            if (isParameterName("iter$" + count)) {
                parameterUpdate("iter$" + count, getParameter("init$" + count));
                flag = true;
            }

            count++;
        }

        updateEnabled();
        updateEquation();
    }


    /**
     * This method is called when a parameter in the task is updated. It should update the GUI in response to the
     * parameter update
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("iterations")) {
            iterlabel.setText((String) value);
        }

        if (paramname.equals("enabled")) {
            basiccheck.setSelected(new Boolean((String) value).booleanValue());
        }

        if (paramname.equals("advanced")) {
            advancedcheck.setSelected(new Boolean((String) value).booleanValue());
        }

        if (paramname.equals("exitCondition")) {
            advancedcond.setText(value.toString());
        }

        if (paramname.startsWith("param")) {
            try {
                int index = Integer.parseInt(paramname.substring(5));

                while (index >= paramlist.size()) {
                    addCondition();
                }

                JComboBox combo = ((JComboBox) paramlist.get(index));
                updateDataVariables(combo);
                combo.setSelectedItem(value);

                updateEquation();
            } catch (NumberFormatException except) {
            }
        }

        if (paramname.startsWith("comp")) {
            try {
                int index = Integer.parseInt(paramname.substring(4));

                while (index >= complist.size()) {
                    addCondition();
                }

                ((JComboBox) complist.get(index)).setSelectedItem(value);
                updateEquation();
            } catch (NumberFormatException except) {
            }
        }

        if (paramname.startsWith("eq")) {
            try {
                int index = Integer.parseInt(paramname.substring(2));

                while (index >= eqlist.size()) {
                    addCondition();
                }

                ((JTextField) eqlist.get(index)).setText(value.toString());
                updateEquation();
            } catch (NumberFormatException except) {
            }
        }

        if (paramname.startsWith("init$")) {
            int index = Integer.parseInt(paramname.substring(5));
            ((JTextField) initlist.get(index)).setText(value.toString());
        }

        if (paramname.startsWith("iter$")) {
            int index = Integer.parseInt(paramname.substring(5));
            ((JTextField) iterlist.get(index)).setText(value.toString());
        }
    }


    /**
     * This method is called when the panel is being disposed off. It should clean-up subwindows, open files etc.
     */
    public void dispose() {
        // Insert code to clean-up panel here
    }


    /**
     * repacks the parameter window to preferred size;
     */
    private void repack() {
        Component comp = getParent();

        while ((comp != null) && (!(comp instanceof Window))) {
            comp = comp.getParent();
        }

        if (comp != null) {
            ((Window) comp).pack();
        }
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == add) {
            addCondition();
        }
    }

    /**
     * Invoked when an item has been selected or deselected by the user.
     */
    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == basiccheck) {
            setParameter("enabled", String.valueOf(basiccheck.isSelected()));

            if (!basiccheck.isSelected()) {
                advancedcheck.setSelected(false);
            } else if (!advancedcheck.isSelected()) {
                updateEquation();
            }

            updateEnabled();
        } else if (event.getSource() == advancedcheck) {
            getTask().setParameterType("advanced", Tool.INTERNAL);
            setParameter("advanced", String.valueOf(advancedcheck.isSelected()));

            if (advancedcheck.isSelected()) {
                basiccheck.setSelected(true);
            } else {
                updateEquation();
            }

            updateEnabled();
        }
    }

    /**
     * Invoked when a component loses the keyboard focus.
     */
    public void focusLost(FocusEvent event) {
        if (paramlist.contains(event.getSource())) {
            String paramname = "param" + paramlist.indexOf(event.getSource());

            if (((JComboBox) event.getSource()).getSelectedItem() != null) {
                getTask().setParameterType(paramname, Tool.INTERNAL);
                setParameter(paramname, ((JComboBox) event.getSource()).getSelectedItem());
            }
        } else if (complist.contains(event.getSource())) {
            String paramname = "comp" + complist.indexOf(event.getSource());

            if (((JComboBox) event.getSource()).getSelectedItem() != null) {
                getTask().setParameterType(paramname, Tool.INTERNAL);
                setParameter(paramname, ((JComboBox) event.getSource()).getSelectedItem());
            }
        } else if (eqlist.contains(event.getSource())) {
            String paramname = "eq" + eqlist.indexOf(event.getSource());

            getTask().setParameterType(paramname, Tool.INTERNAL);
            setParameter(paramname, ((JTextField) event.getSource()).getText());
        } else if (initlist.contains(event.getSource())) {
            String paramname = "init$" + initlist.indexOf(event.getSource());

            getTask().setParameterType(paramname, Tool.INTERNAL);
            setParameter(paramname, ((JTextField) event.getSource()).getText());
        } else if (iterlist.contains(event.getSource())) {
            String paramname = "iter$" + iterlist.indexOf(event.getSource());

            getTask().setParameterType(paramname, Tool.INTERNAL);
            setParameter(paramname, ((JTextField) event.getSource()).getText());
        }

        updateEquation();
    }

    /**
     * Invoked when a component gains the keyboard focus.
     */
    public void focusGained(FocusEvent event) {
        if (paramlist.contains(event.getSource())) {
            updateDataVariables((JComboBox) event.getSource());
        }
    }


    /**
     * Called when a new task is created in a taskgraph.
     */
    public void taskCreated(TaskGraphTaskEvent event) {
        Iterator iter = paramlist.iterator();

        while (iter.hasNext()) {
            populateParamCombo((JComboBox) iter.next());
        }

        repack();
    }

    /**
     * Called when a task is removed from a taskgraph. Note that this method is called when tasks are removed from a
     * taskgraph due to being grouped (they are place in the groups taskgraph).
     */
    public void taskRemoved(TaskGraphTaskEvent event) {
        Iterator iter = paramlist.iterator();

        while (iter.hasNext()) {
            populateParamCombo((JComboBox) iter.next());
        }

        if (event.getTask() == getTask()) {
            event.getTaskGraph().removeTaskGraphListener(this);
        }

        repack();
    }

    /**
     * Called when a new connection is made between two tasks.
     */
    public void cableConnected(TaskGraphCableEvent event) {
    }

    /**
     * Called when a connection is reconnected to a different task.
     */
    public void cableReconnected(TaskGraphCableEvent event) {
    }

    /**
     * Called before a connection between two tasks is removed.
     */
    public void cableDisconnected(TaskGraphCableEvent event) {
    }

    /**
     * Called when the control task is connected/disconnected or unstable
     */
    public void controlTaskStateChanged(ControlTaskStateEvent event) {
    }

}
