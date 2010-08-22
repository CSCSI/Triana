package common.output;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import org.trianacode.gui.panels.ParameterPanel;

public class CondorPanel extends ParameterPanel {

    // Define GUI components here, e.g.
    //
    // private JTextField namelabel = new JTextField();
    private JTextField schedd_name;
    private JCheckBox verbose;
    private JCheckBox remote;
    private JCheckBox nopermchk;
    private JTable command;
    private JTextArea output_submit;
    private JTextArea qoutput;
    private JTextArea st_output;

    private MyCellEditorInfo cellEditorInfo;

    public CondorPanel() {

        // cell editor
        cellEditorInfo = new MyCellEditorInfo();
        cellEditorInfo.addSimple("universe",
                new DefaultCellEditor(new JComboBox(new String[]{
                        "", "vanilla", "standard", "pvm", "scheduler", "globus", "mpi", "java"
                })));
        cellEditorInfo.addChooser("executable");
// 	"requirements",

        cellEditorInfo.addChooser("input");
        cellEditorInfo.addChooser("output");
        cellEditorInfo.addChooser("error");
        cellEditorInfo.addChooser("initialdir");
        cellEditorInfo.addSimple("should_transfer_files",
                new DefaultCellEditor(new JComboBox(new String[]{
                        "", "yes", "no", "if_needed"})));
        cellEditorInfo.addSimple("when_to_transfer_output",
                new DefaultCellEditor(new JComboBox(new String[]{
                        "", "on_exit", "on_exit_or_evict"})));
        cellEditorInfo.addList("transfer_input_files");
        cellEditorInfo.addList("transfer_output_files");
// 	    "rank",
// 	    "on_exit_remove",
// 	    "on_exit_hold",
// 	    "periodic_remove",
// 	    "periodic_hold",
// 	    "periodic_release",
// 	    "priority",
        cellEditorInfo.addSimple("notification",
                new DefaultCellEditor(new JComboBox(new String[]{
                        "", "always", "complete", "error", "never"})));
// 	    "notify_user",
        cellEditorInfo.addSimple("copy_to_spool",
                new DefaultCellEditor(new JComboBox(new String[]{"", "true", "false"})));
        cellEditorInfo.addSimple("getenv",
                new DefaultCellEditor(new JComboBox(new String[]{"", "true", "false"})));
        cellEditorInfo.addSimple("hold",
                new DefaultCellEditor(new JComboBox(new String[]{"", "true", "false"})));
//?? 	    "environment",
        cellEditorInfo.addChooser("log");
        cellEditorInfo.addSimple("log_xml",
                new DefaultCellEditor(new JComboBox(new String[]{"", "true", "false"})));
        cellEditorInfo.addList("jar_files");
// 	    "image_size",
// 	    "machine_count",
// 	    "coresize",
        cellEditorInfo.addSimple("nice_user",
                new DefaultCellEditor(new JComboBox(new String[]{"", "true", "false"})));
// 	    "kill_sig",
        cellEditorInfo.addList("compress_files");
        cellEditorInfo.addList("fetch_files");
        cellEditorInfo.addList("append_files");
        cellEditorInfo.addList("local_files");
//?? 	    "file_remaps",
//?? 	    "buffer_files",
// 	    "buffer_size",
// 	    "buffer_block_size",
        cellEditorInfo.addChooser("rendezvousdir");
        cellEditorInfo.addChooser("x509directory");
        cellEditorInfo.addChooser("x509userproxy");
// 	    "globusscheduler",
// 	    "globusrsl",
// 	    "globus_resubmit",
// 	    "globus_rematch",
// 	    "leave_in_queue",
// 	    "match_list_length",
        cellEditorInfo.addSimple("transfer_output",
                new DefaultCellEditor(new JComboBox(new String[]{"", "true", "false"})));
        cellEditorInfo.addSimple("transfer_input",
                new DefaultCellEditor(new JComboBox(new String[]{"", "true", "false"})));
        cellEditorInfo.addSimple("transfer_error",
                new DefaultCellEditor(new JComboBox(new String[]{"", "true", "false"})));
        cellEditorInfo.addSimple("transfer_executable",
                new DefaultCellEditor(new JComboBox(new String[]{"", "true", "false"})));
        cellEditorInfo.addSimple("stream_output",
                new DefaultCellEditor(new JComboBox(new String[]{"", "true", "false"})));
        cellEditorInfo.addSimple("stream_error",
                new DefaultCellEditor(new JComboBox(new String[]{"", "true", "false"})));
//?? 	    "+",
        cellEditorInfo.addSpinner("QUEUE");
        cellEditorInfo.addSimple("allow_startup_script",
                new DefaultCellEditor(new JComboBox(new String[]{"", "true", "false"})));
    }

    /**
     * This method is called before the panel is displayed. It should initialise the panel layout.
     */
    public void init() {
        // Insert code to layout GUI here, e.g.
        //
        // add(namelabel);
//         getTask().addTaskListener(this);

        setLayout(new BorderLayout());

        JPanel pn = new JPanel(new BorderLayout());
        add(pn, BorderLayout.CENTER);
        //pn.setBorder(new BevelBorder(BevelBorder.RAISED));

        JTabbedPane tabs = new JTabbedPane();
        pn.add(tabs, BorderLayout.CENTER);

        // tab submit
        Box submit = new Box(BoxLayout.Y_AXIS);

        Box schedd = new Box(BoxLayout.X_AXIS);
        schedd.setAlignmentX(Component.LEFT_ALIGNMENT);
        schedd.add(new JLabel("Schedd Name: "));
        this.schedd_name = new JTextField((String) getParameter("submit_schedd_name"));
        schedd_name.setMaximumSize(new Dimension(Short.MAX_VALUE, schedd_name.getPreferredSize().height));
        schedd.add(schedd_name);
        submit.add(schedd);

        Box cks = new Box(BoxLayout.X_AXIS);
        cks.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.verbose = new JCheckBox("Verbose", ((Boolean) getParameter("submit_verbose")).booleanValue());
        cks.add(verbose);
        cks.add(Box.createHorizontalGlue());
        this.remote = new JCheckBox("Remote Schedd", ((Boolean) getParameter("submit_remote_schedd")).booleanValue());
        cks.add(remote);
        cks.add(Box.createHorizontalGlue());
        this.nopermchk = new JCheckBox("Disable File Permission Checks",
                ((Boolean) getParameter("submit_disable_permchk")).booleanValue());
        cks.add(nopermchk);
        cks.add(Box.createHorizontalGlue());
        submit.add(cks);

        submit.add(Box.createVerticalGlue());

        submit.add(new JLabel("Additional Commands: "));
        String[] header = {"Command", "Value"};
        this.command =
                new MyTable(new MyTableModel((Object[][]) getParameter("submit_additional_commands"), header));
        JScrollPane tblpn = new JScrollPane(command);
        tblpn.setAlignmentX(Component.LEFT_ALIGNMENT);
        tblpn.setPreferredSize(new Dimension(command.getPreferredSize().width, command.getRowHeight() * 5));
        submit.add(tblpn);
        Box adddel = new Box(BoxLayout.X_AXIS);
        adddel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComboBox addcmd = new JComboBox(new Object[]{"Additional Commands ...",
                "input",
                "output",
                "error",
                "arguments",
                "initialdir",
                "should_transfer_files",
                "when_to_transfer_output",
                "transfer_input_files",
                "transfer_output_files",
                "rank",
                "on_exit_remove",
                "on_exit_hold",
                "periodic_remove",
                "periodic_hold",
                "periodic_release",
                "priority",
                "notification",
                "notify_user",
                "copy_to_spool",
                "getenv",
                "hold",
                "environment",
                "log",
                "log_xml",
                "jar_files",
                "image_size",
                "machine_count",
                "coresize",
                "nice_user",
                "kill_sig",
                "compress_files",
                "fetch_files",
                "append_files",
                "local_files",
                "file_remaps",
                "buffer_files",
                "buffer_size",
                "buffer_block_size",
                "rendezvousdir",
                "x509directory",
                "x509userproxy",
                "globusscheduler",
                "globusrsl",
                "globus_resubmit",
                "globus_rematch",
                "leave_in_queue",
                "match_list_length",
                "transfer_output",
                "transfer_input",
                "transfer_error",
                "transfer_executable",
                "stream_output",
                "stream_error",
                "+",
                "allow_startup_script"
        });
        addcmd.setMaximumSize(addcmd.getPreferredSize());
        addcmd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MyTableModel m = (MyTableModel) command.getModel();
                JComboBox cb = (JComboBox) e.getSource();
                int row = m.getRowCount() - 1; // before the last row, which is blank place holder
                if (cb.getSelectedIndex() > 0) {
                    m.insertRows(row, new Object[][]{{cb.getSelectedItem(), ""}});
                    command.setRowSelectionInterval(row, row);
                }
                cb.setSelectedIndex(0);
            }
        });
        JButton delcmd = new JButton("Delete");
        delcmd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] idx = command.getSelectedRows();
                MyTableModel m = (MyTableModel) command.getModel();

                int n = 0;
                for (int i = 0; i < idx.length; i++) {
                    if (idx[i] == m.getRowCount() - 1) {
                        idx[i] = -1;
                    } // not remove
                    if (idx[i] != -1) {
                        n++;
                    }
                }
                int[] newidx = new int[n];
                for (int i = 0, j = 0; i < idx.length; i++) {
                    if (idx[i] == -1) {
                        continue;
                    }
                    newidx[j] = idx[i];
                    j++;
                }
                m.removeRows(newidx);
            }
        });
        adddel.add(Box.createHorizontalGlue());
        adddel.add(addcmd);
        adddel.add(delcmd);
        submit.add(adddel);

        submit.add(Box.createVerticalGlue());

        submit.add(new JLabel("Submit Output: "));
        output_submit = new JTextArea((String) getParameter("output_submit"));
        output_submit.setRows(5);
        output_submit.setEditable(false);
        JScrollPane outputS = new JScrollPane(output_submit);
        outputS.setAlignmentX(Component.LEFT_ALIGNMENT);
        submit.add(outputS);

        tabs.addTab("Submit", submit);

        // tab queue
        JPanel queue = new JPanel(new BorderLayout());

        qoutput = new JTextArea();
        qoutput.setRows(5);
        qoutput.setEditable(false);
        JScrollPane qpn = new JScrollPane(qoutput);
        queue.add(qpn, BorderLayout.CENTER);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setParameter("queue_refresh", new Boolean(true));
            }
        });
        Box ref = new Box(BoxLayout.X_AXIS);
        ref.add(Box.createHorizontalGlue());
        ref.add(refresh);
        queue.add(ref, BorderLayout.SOUTH);
        tabs.addTab("Queue", queue);

        // tab status
        JPanel status = new JPanel(new BorderLayout());

        st_output = new JTextArea();
        st_output.setRows(5);
        st_output.setEditable(false);
        JScrollPane st_pn = new JScrollPane(st_output);
        status.add(st_pn, BorderLayout.CENTER);

        JButton refresh2 = new JButton("Refresh");
        refresh2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setParameter("status_refresh", new Boolean(true));
            }
        });
        Box ref2 = new Box(BoxLayout.X_AXIS);
        ref2.add(Box.createHorizontalGlue());
        ref2.add(refresh2);
        status.add(ref2, BorderLayout.SOUTH);
        tabs.addTab("Status", status);
    }


    /**
     * This method is called when cancel is clicked on the parameter window. It should synchronize the GUI components
     * with the task parameter values
     */
    public void reset() {
        // Insert code to synchronise the GUI with the task parameters here, e.g.
        //
        // namelabel.setText(getParameter("name"));
        System.err.println("CondorPanel reset");

        schedd_name.setText((String) getParameter("submit_schedd_name"));
        verbose.setSelected(((Boolean) getParameter("submit_verbose")).booleanValue());
        remote.setSelected(((Boolean) getParameter("submit_remote_schedd")).booleanValue());
        nopermchk.setSelected(((Boolean) getParameter("submit_disable_permchk")).booleanValue());
        ((MyTableModel) command.getModel()).setData((Object[][]) getParameter("submit_additional_commands"));
        output_submit.setText((String) getParameter("output_submit"));
    }

    /**
     * Called when the ok button is clicked on the parameter window. Calls applyClicked by default to commit any
     * parameter changes.
     */
    public void okClicked() {
        applyClicked();
    }

    /**
     * Called when the apply button is clicked on the parameter window. Commits any parameter changes.
     */
    public void applyClicked() {
        setParameter("submit_schedd_name", schedd_name.getText());
        setParameter("submit_verbose", new Boolean(verbose.getModel().isSelected()));
        setParameter("submit_remote_schedd", new Boolean(remote.getModel().isSelected()));
        setParameter("submit_disable_permchk", new Boolean(nopermchk.getModel().isSelected()));
        setParameter("submit_additional_commands", ((MyTableModel) command.getModel()).getData());

        super.applyClicked();    // it commits
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
        System.err.println("parameterUpdate (task -> GUI), paramname: " + paramname);
        if (paramname.equals("output_submit")) {
            output_submit.append((String) value);
        }
        if (paramname.equals("output_queue")) {
            qoutput.setText((String) value);
        }
        if (paramname.equals("output_status")) {
            st_output.setText((String) value);
        }
    }


    /**
     * This method is called when the panel is being disposed off. It should clean-up subwindows, open files etc.
     */
    public void dispose() {
        // Insert code to clean-up panel here
    }

    public void setParameter(String name, Object value) {
        System.err.println("setParameter");
        super.setParameter(name, value);
    }

    //
    // helper classes
    //

    // table

    private final class MyTable extends JTable {
        public MyTable(TableModel tm) {
            super(tm);
        }

        public TableCellEditor getCellEditor(int row, int col) {
            TableCellEditor res = super.getCellEditor(row, col);
            TableCellEditor tmp;

            if (col == 1) {    // value column
                tmp = cellEditorInfo.getEditor(getModel().getValueAt(row, 0));
                if (tmp != null) {
                    res = tmp;
                }
            }
            return res;
        }
    }

    private final class MyTableModel extends AbstractTableModel {
        private Object[][] data;
        private String[] columnNames;

        public MyTableModel(Object[][] data, String[] columnNames) {
            this.data = copyData(data);
            this.columnNames = columnNames;
        }

        public Object[][] getData() {
            return data;
        }

        public void setData(Object[][] data) {
            this.data = copyData(data);
            fireTableStructureChanged();
        }

        private Object[][] insertData(Object[][] first, Object[][] second, int point) {
            int row1 = first.length;
            int p = point < 0 ? row1 + point : point;
            if (row1 - 1 < p) {
                return appendData(first, second);
            }

            int col = first[0].length;
            int row2 = second == null ? 0 : second.length;
            Object[][] res = new Object[row1 + row2][col];

            for (int i = 0; i < p; i++) {
                for (int j = 0; j < col; j++) {
                    res[i][j] = first[i][j];
                }
            }
            for (int i = 0; i < row2; i++) {
                for (int j = 0; j < col; j++) {
                    res[i + p][j] = second[i][j];
                }
            }
            for (int i = 0; i < row1 - p; i++) {
                for (int j = 0; j < col; j++) {
                    res[i + p + row2][j] = first[i + p][j];
                }
            }

            return res;
        }

        private Object[][] appendData(Object[][] first, Object[][] second) {
            int row1 = first.length;
            int col = first[0].length;
            int row2 = second == null ? 0 : second.length;
            Object[][] res = new Object[row1 + row2][col];
            for (int i = 0; i < row1; i++) {
                for (int j = 0; j < col; j++) {
                    res[i][j] = first[i][j];
                }
            }
            for (int i = 0; i < row2; i++) {
                for (int j = 0; j < col; j++) {
                    res[i + row1][j] = second[i][j];
                }
            }
            return res;
        }

        private Object[][] copyData(Object[][] data) {
            return appendData(data, null);
        }

        public void addRows(Object[][] rows) {
            this.data = appendData(this.data, rows);
            fireTableStructureChanged();
        }

        public void insertRows(int row, Object[][] rows) {
            this.data = insertData(this.data, rows, row);
            fireTableStructureChanged();
        }

        private Object[][] removeData(Object[][] orig, int[] points) {
            int col = orig[0].length;
            int row = orig.length - points.length;
            Object[][] res = new Object[row][col];

            for (int i = 0, ii = 0; i < orig.length; i++) {
                int n;
                for (n = 0; n < points.length; n++) {
                    if (i == points[n]) {
                        break;
                    }
                }
                if (n < points.length) // found, so remove
                {
                    continue;
                }
                for (int j = 0; j < col; j++) {
                    res[ii][j] = orig[i][j];
                }
                ii++;
            }

            return res;
        }

        public void removeRows(int[] index) {
            this.data = removeData(this.data, index);
            fireTableStructureChanged();
        }

        public int getRowCount() {
            return data.length;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int row, int column) {
            return data[row][column];
        }

        public void setValueAt(Object value, int row, int column) {
            data[row][column] = value;
            fireTableCellUpdated(row, column);
        }

        public String getColumnName(int column) {
            return columnNames[column];
        }

        public boolean isCellEditable(int row, int column) {
            if (column == 0) {
                if (((String) getValueAt(row, 0)).startsWith("+")) {
                    return true;
                } else {
                    return false;
                }
            } else if (column == 1) {
                if (((String) getValueAt(row, 0)).equals("")) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    private final class MyCellEditor extends DefaultCellEditor {

        private Object currentValue = null;

        public MyCellEditor(final JButton button) {
            super(new JCheckBox());
            setClickCountToStart(1);
            editorComponent = button;
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public MyCellEditor(final JSpinner spinner) {
            super(new JCheckBox());
            setClickCountToStart(1);
            editorComponent = spinner;
        }

        public Object getCellEditorValue() {
            return currentValue;
        }

        public void setCellEditorValue(Object value) {
            currentValue = value;
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column) {
            if (editorComponent instanceof JButton) {
                ((JButton) editorComponent).setText(value.toString());
            } else if (editorComponent instanceof JSpinner && value instanceof Integer) {
                ((JSpinner) editorComponent).setValue(value);
            }

            currentValue = value;
            return editorComponent;
        }

    }

    private final class MyCellEditorInfo {
        Hashtable info = new Hashtable();

        public TableCellEditor getEditor(Object key) {
            return (TableCellEditor) info.get(key);
        }

        public void addSimple(String key, DefaultCellEditor editor) {
            info.put(key, editor);
        }

        public void addChooser(final String key) {
            final JButton button = new JButton();
            button.setHorizontalAlignment(JButton.LEFT);

            final MyCellEditor editor = new MyCellEditor(button);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("Choose Value for \"" + key + "\"");

                    int result = chooser.showOpenDialog(button);

                    if (result == JFileChooser.APPROVE_OPTION) {
                        editor.setCellEditorValue(chooser.getSelectedFile().getAbsolutePath());
                    }
                }
            });
            info.put(key, editor);
        }

        public void addList(final String key) {
            final JButton button = new JButton();
            button.setHorizontalAlignment(JButton.LEFT);

            final MyCellEditor editor = new MyCellEditor(button);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane opt = new JOptionPane(null,
                            JOptionPane.PLAIN_MESSAGE,
                            JOptionPane.OK_CANCEL_OPTION);
                    //JDialog diag = opt.createDialog(button, key);
                    JPanel pane = new JPanel(new BorderLayout());
                    //diag.setContentPane(pane);
                    //pane.add(opt, BorderLayout.SOUTH);

                    final JList list = new JList();
                    Object ini = editor.getCellEditorValue();
                    if (ini instanceof MyListModel) {
                        list.setModel(new MyListModel((MyListModel) ini));
                    } else {
                        list.setModel(new MyListModel());
                    }

                    JScrollPane slist = new JScrollPane(list);
                    pane.add(slist, BorderLayout.CENTER);

                    final JButton bAdd = new JButton("Add");
                    final JButton bDel = new JButton("Delete");
                    bAdd.setMaximumSize(bDel.getMaximumSize());
                    Box ctl = new Box(BoxLayout.Y_AXIS);
                    ctl.add(Box.createVerticalGlue());
                    ctl.add(bAdd);
                    ctl.add(bDel);
                    pane.add(ctl, BorderLayout.EAST);

                    bAdd.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            JFileChooser chooser = new JFileChooser();
                            chooser.setDialogTitle("Choose Value for \"" + key + "\"");

                            int result = chooser.showOpenDialog(button);

                            if (result == JFileChooser.APPROVE_OPTION) {
                                MyListModel model = (MyListModel) list.getModel();
                                model.addElement(chooser.getSelectedFile().getAbsolutePath());
                            }
                        }
                    });
                    bDel.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            MyListModel model = (MyListModel) list.getModel();
                            Object[] o = list.getSelectedValues();
                            for (int i = 0; i < o.length; i++) {
                                model.removeElement(o[i]);
                            }
                        }
                    });

                    int ans = opt.showOptionDialog(button, pane, key,
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE, null, null, null);

                    if (ans == JOptionPane.OK_OPTION) {
                        editor.setCellEditorValue(list.getModel());
                    }
                }
            });
            info.put(key, editor);
        }

        public void addSpinner(final String key) {
            final JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

            final MyCellEditor editor = new MyCellEditor(spinner);
            spinner.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    editor.currentValue = spinner.getValue();
                }
            });
            info.put(key, editor);
        }
    }

    // list

    private final class MyListModel extends DefaultListModel {
        public MyListModel() {
        }

        public MyListModel(MyListModel model) {
            for (int i = 0; i < model.size(); i++) {
                addElement(model.get(i));
            }
        }

        public String toString() {
            String res = size() > 0 ? (String) get(0) : "";
            for (int i = 1; i < size(); i++) {
                res += ", " + (String) get(i);
            }

            return res;
        }
    }

}
