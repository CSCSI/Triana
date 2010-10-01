package common.input;

import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import org.trianacode.gui.panels.ParameterPanel;

/**
 * GUI for CondorSubmitDescription
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */

public class CondorSubmitDescriptionPanel extends ParameterPanel {

    private MyTable desc;

    private MyCellEditorInfo cellEditorInfo;

    public CondorSubmitDescriptionPanel() {

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
        // Insert code to layout GUI here

        JPanel pane;        // maybe not needed
        Box conduct;
        JButton addQ = new JButton("New Queue");
        final JComboBox addcmd;
        JLabel lb = new JLabel(" for Queue Number: ");
        final JSpinner qn = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        JButton delcmd = new JButton("Delete");
        JScrollPane step1;
        Object[][] description;
        String[] descHeader = {"Command", "Value"};

        description = (Object[][]) getParameter("CondorSubmitDescription");
        System.err.println("===> \"" + description + "\"");

        // whole
        setLayout(new BorderLayout());

        pane = new JPanel(new BorderLayout());
        add(pane, BorderLayout.CENTER);
        pane.setBorder(new BevelBorder(BevelBorder.RAISED));

        // back, next buttons
        conduct = new Box(BoxLayout.X_AXIS);
        addcmd = new JComboBox(new Object[]{"More Commands ...",
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
        pane.add(conduct, BorderLayout.SOUTH);
        conduct.add(addQ);
        conduct.add(Box.createHorizontalGlue());
        conduct.add(addcmd);
        conduct.add(lb);
        conduct.add(qn);
        conduct.add(Box.createHorizontalGlue());
        conduct.add(delcmd);

        addQ.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((MyTableModel) desc.getModel()).addRows(new Object[][]{{"QUEUE", ""}});
                SpinnerNumberModel sm = (SpinnerNumberModel) qn.getModel();
                sm.setMaximum(new Integer(((Integer) sm.getMaximum()).intValue() + 1));
            }
        });
        addcmd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MyTableModel m = (MyTableModel) desc.getModel();
                JComboBox cb = (JComboBox) e.getSource();
                int q = ((Integer) ((SpinnerNumberModel) qn.getModel()).getNumber()).intValue();
                int qq = 0, row = 0;
                for (int i = 0; i < m.getRowCount(); i++) {
                    if (!((String) m.getValueAt(i, 0)).equals("QUEUE")) {
                        continue;
                    }
                    qq++;
                    if (qq == q) {
                        row = i;
                        break;
                    }
                }

                if (cb.getSelectedIndex() > 0) {
                    m.insertRows(row, new Object[][]{{cb.getSelectedItem(), ""}});
                    desc.setRowSelectionInterval(row, row);
                }
                cb.setSelectedIndex(0);
            }
        });
        delcmd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] idx = desc.getSelectedRows();
                MyTableModel m = (MyTableModel) desc.getModel();
                SpinnerNumberModel sm = (SpinnerNumberModel) qn.getModel();

                int n = 0;
                for (int i = 0; i < idx.length; i++) {
                    String s = (String) m.getValueAt(idx[i], 0);
                    if (s.equals("universe") || s.equals("executable") || s.equals("requirements")) {
                        idx[i] = -1;
                    } // not remove
                    if (s.equals("QUEUE")) {
                        if (((Integer) sm.getMaximum()).intValue() > 1) {
                            sm.setMaximum(new Integer(((Integer) sm.getMaximum()).intValue() - 1));
                        } else {
                            idx[i] = -1;
                        }
                    }
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

        step1 = new JScrollPane();
        desc = new MyTable(new MyTableModel(description, descHeader));
// 	desc.getColumnModel().getColumn(0).setPreferredWidth(100);
// 	desc.getColumnModel().getColumn(1).setPreferredWidth(500);
        step1.setViewportView(desc);
        pane.add(step1, BorderLayout.CENTER);

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
        setParameter("CondorSubmitDescription", ((MyTableModel) desc.getModel()).getData());
        super.applyClicked();    // it commits
        ((MyTableModel) desc.getModel()).setData((Object[][]) getParameter("CondorSubmitDescription"));
    }

    public void setParameter(String name, Object value) {
        System.err.println("setParameter");
        super.setParameter(name, value);
    }

    /**
     * This method is called when cancel is clicked on the parameter window. It should synchronize the GUI components
     * with the task parameter values
     */
    public void reset() {
        // Insert code to synchronise the GUI with the task parameters here, e.g.
        //
        // namelabel.setText(getParameter("name"));

        ((MyTableModel) desc.getModel()).setData((Object[][]) getParameter("CondorSubmitDescription"));
    }

    /**
     * This method is called when the panel is being disposed off. It should clean-up subwindows, open files etc.
     */
    public void dispose() {
        // Insert code to clean-up panel here
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

                            int result = chooser.showOpenDialog(bAdd);

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
