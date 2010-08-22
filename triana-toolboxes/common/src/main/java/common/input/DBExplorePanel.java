package common.input;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.panels.ParameterPanelImp;
import org.trianacode.gui.windows.ParameterWindow;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;
import triana.types.util.StringVector;


public class DBExplorePanel extends ParameterPanel implements
        ActionListener, FocusListener, TaskListener, ListSelectionListener {

    /**
     * Input components
     */
    private JTextField username;
    private JPasswordField password;
    JComboBox hostname, databases, tables;

    private JButton queryButton = new JButton("Submit Query");
    private JButton countButton = new JButton("Count Results");

    private JTextArea display = new JTextArea();
    private JList listField;
    private JScrollPane listScrollPane, tableScrollPane;
    private JTable resultsTable;
    private JTextArea criteria;
    private JTextArea generalQuery;
    private ParameterWindow window;
    private JTextField resultsCount;

    DefaultListModel listModel;
    String fields;
    String[][] data;
    String[] headers;
    StringVector headersVec;
    QueryTableModel qtm;
    int numberOfCols;
    int numberOfRows;
    int rowCounter;
    int queryButtonCounter = 0;
    int countButtonCounter = 0;

    /**
     * This method returns true by default. It should be overridden if the panel does not want the user to be able to
     * change the auto commit state
     */
    public boolean isAutoCommitVisible() {
        return false;
    }

    /**
     * This method returns false by default. It should be overridden if the panel wants parameter changes to be commited
     * automatically
     */
    public boolean isAutoCommitByDefault() {
        return true;
    }

    /**
     * Layout Panel
     */
    public void init() {
        // listens for paramater update events on the task

        getTask().addTaskListener(this);


        setLayout(new BorderLayout());

        JPanel labelpanel = new JPanel(new GridLayout(5, 1));
        labelpanel.add(new JLabel("Username"));
        labelpanel.add(new JLabel("Password"));
        labelpanel.add(new JLabel("Hostname"));
        labelpanel.add(new JLabel("databases"));
        labelpanel.add(new JLabel("tables"));
        labelpanel.setBorder(new EmptyBorder(0, 0, 0, 3));

        JPanel usercont = new JPanel(new BorderLayout());
        username = new JTextField(20);
        username.addFocusListener(this);
        usercont.add(username, BorderLayout.WEST);
        usercont.setBorder(new EmptyBorder(0, 0, 3, 0));

        JPanel passcont = new JPanel(new BorderLayout());
        password = new JPasswordField(20);
        password.addFocusListener(this);
        passcont.add(password, BorderLayout.WEST);
        passcont.setBorder(new EmptyBorder(0, 0, 3, 0));


        JPanel hostcont = new JPanel(new BorderLayout());
        hostname = new JComboBox();
        hostname.addItem("localhost");
        hostname.addItem("mini.astro.cf.ac.uk");
        hostname.addItem("weber.astro.cf.ac.uk");
        hostname.addItem("130.75.117.76");
        hostname.addItem("130.75.117.164");
        hostname.setSelectedIndex(0);
        hostname.setEditable(true);
        hostname.addActionListener(this);
        hostcont.add(hostname, BorderLayout.CENTER);
        hostcont.setBorder(new EmptyBorder(0, 0, 3, 0));


        JPanel databasescont = new JPanel(new BorderLayout());
        databases = new JComboBox();
        databases.addActionListener(this);
        databasescont.add(databases, BorderLayout.CENTER);
        databasescont.setBorder(new EmptyBorder(0, 0, 3, 0));

        JPanel tablescont = new JPanel(new BorderLayout());
        tables = new JComboBox();
        tables.addActionListener(this);
        tablescont.add(tables, BorderLayout.CENTER);
        tablescont.setBorder(new EmptyBorder(0, 0, 3, 0));

        JPanel itempanel = new JPanel(new GridLayout(5, 1));
        itempanel.add(usercont);
        itempanel.add(passcont);
        itempanel.add(hostcont);
        itempanel.add(databasescont);
        itempanel.add(tablescont);

        JPanel top = new JPanel(new BorderLayout());
        top.add(labelpanel, BorderLayout.WEST);
        top.add(itempanel, BorderLayout.EAST);
        top.setBorder(new EmptyBorder(0, 0, 3, 0));


        JPanel labelpanel2 = new JPanel(new GridLayout(2, 1));
        labelpanel2.add(new JLabel("criteria"));
        labelpanel2.add(new JLabel("general query"));
        labelpanel2.setBorder(new EmptyBorder(0, 0, 0, 3));


        JPanel criteriacont = new JPanel(new BorderLayout());
        criteria = new JTextArea(3, 20);
        criteria.setLineWrap(true);
        criteria.setWrapStyleWord(true);
        criteria.addFocusListener(this);
        criteriacont.add(criteria, BorderLayout.WEST);
        criteriacont.setBorder(new EmptyBorder(0, 0, 3, 0));
        //criteriacont.setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel generalquerycont = new JPanel(new BorderLayout());
        generalQuery = new JTextArea(3, 20);
        generalQuery.setLineWrap(true);
        generalQuery.setWrapStyleWord(true);
        generalQuery.addFocusListener(this);
        generalquerycont.add(generalQuery, BorderLayout.WEST);
        generalquerycont.setBorder(new EmptyBorder(0, 0, 3, 0));
        //generalquerycont.setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel itempanel2 = new JPanel(new GridLayout(1, 1));
        itempanel2.add(criteriacont);

        JPanel itempanel3 = new JPanel(new GridLayout(1, 1));
        itempanel3.add(generalquerycont);

        JPanel areas = new JPanel(new BorderLayout());
        areas.add(itempanel2, BorderLayout.NORTH);
        areas.add(itempanel3, BorderLayout.SOUTH);

        JPanel middle = new JPanel(new BorderLayout());
        middle.add(labelpanel2, BorderLayout.WEST);
        middle.add(areas, BorderLayout.EAST);
        middle.setBorder(new EmptyBorder(0, 0, 3, 0));

        JPanel topAndMiddle = new JPanel(new BorderLayout());
        topAndMiddle.add(top, BorderLayout.NORTH);
        topAndMiddle.add(middle, BorderLayout.SOUTH);
        topAndMiddle.setBorder(new EmptyBorder(0, 0, 3, 0));

        JPanel listcont = new JPanel(new BorderLayout());
        listModel = new DefaultListModel();
        listField = new JList(listModel);
        listField.addListSelectionListener(this);
        listScrollPane = new JScrollPane(listField);
        listcont.add(listScrollPane, BorderLayout.WEST);
        listcont.setBorder(new EmptyBorder(0, 0, 3, 0));

        JPanel buttonpanel = new JPanel(new GridLayout(2, 1));
        JPanel queryButtonPanel = new JPanel(new BorderLayout());
        queryButtonPanel.add(queryButton, BorderLayout.NORTH);
        JPanel countButtonPanel = new JPanel(new BorderLayout());
        countButtonPanel.add(countButton, BorderLayout.NORTH);
        buttonpanel.add(queryButtonPanel);
        buttonpanel.add(countButtonPanel);
        queryButton.addActionListener(this);
        countButton.addActionListener(this);

        JPanel countResultsPanel = new JPanel(new GridLayout(1, 2));
        countResultsPanel.add(new JLabel("Number of results from Query"));
        resultsCount = new JTextField(10);
        resultsCount.setEditable(false);
        countResultsPanel.add(resultsCount);

        qtm = new QueryTableModel();
        resultsTable = new JTable(qtm);
        //resultsTable.setPreferredScrollableViewportSize(new Dimension(500, 100));
        tableScrollPane = new JScrollPane(resultsTable);

        JPanel maincont = new JPanel(new BorderLayout());
        maincont.add(topAndMiddle, BorderLayout.NORTH);
        maincont.add(listScrollPane, BorderLayout.CENTER);
        maincont.add(buttonpanel, BorderLayout.EAST);
        maincont.add(countResultsPanel, BorderLayout.SOUTH);

        add(maincont, BorderLayout.CENTER);
    }


    private void showTable() {
        ParameterPanelImp resultsPanel = new ParameterPanelImp();
        resultsPanel.setLayout(new BorderLayout());

        JPanel tablecont = new JPanel(new BorderLayout());
        tablecont.add(tableScrollPane, BorderLayout.CENTER);

        resultsPanel.add(tablecont, BorderLayout.NORTH);


        if (getWindowInterface().getWindow() instanceof Frame) {
            window = new ParameterWindow((Frame) getWindowInterface().getWindow(),
                    WindowButtonConstants.OK_CANCEL_BUTTONS, false);
        } else {
            window = new ParameterWindow((Dialog) getWindowInterface().getWindow(),
                    WindowButtonConstants.OK_CANCEL_BUTTONS, false);
        }

        Point loc = getLocationOnScreen();
        window.setTitle("Results of query...");
        window.setParameterPanel(resultsPanel);
        window.setLocation(loc.x + 100, loc.y + 100);
        window.setVisible(true);
    }


    /**
     * Clean-up Panel
     */
    public void dispose() {
    }

    /**
     * Reset Panel
     */
    public void reset() {
        //System.out.println("inside reset() of DBExplorePanel:");
        username.setText((String) getTask().getParameter("username"));
        //System.out.println("inside reset() of DBExplorePanel: got username");
        password.setText((String) getTask().getParameter("password"));
        //System.out.println("inside reset() of DBExplorePanel: got password");
        hostname.setSelectedItem((Object) getTask().getParameter("hostname"));
        databases.setSelectedItem((Object) getTask().getParameter("database"));
        tables.setSelectedItem((Object) getTask().getParameter("table"));
        criteria.setText((String) getTask().getParameter("criteria"));
        generalQuery.setText((String) getTask().getParameter("generalQuery"));


    }


    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
        String paramname = event.getParameterName();
        Task task = event.getTask();
        if (paramname.equals("countResults")) {
            resultsCount.setText((String) task.getParameter(paramname));
        }
        if (paramname.equals("dbnames")) {
            //System.out.println("inside parameterUpdated() of DBExplorePanel: got dbnames");
            databases.removeAllItems();

            String paramstr = (String) task.getParameter(paramname);
            while (paramstr.indexOf('\0') > -1) {
                databases.addItem(paramstr.substring(0, paramstr.indexOf('\0')));
                paramstr = paramstr.substring(paramstr.indexOf('\0') + 1);
            }
            databases.setSelectedIndex(0);
        }

        if (paramname.equals("tbnames")) {
            //System.out.println("inside parameterUpdated() of DBExplorePanel: got tbnames");
            tables.removeAllItems();

            String paramstr = (String) task.getParameter(paramname);
            while (paramstr.indexOf('\0') > -1) {
                tables.addItem(paramstr.substring(0, paramstr.indexOf('\0')));
                paramstr = paramstr.substring(paramstr.indexOf('\0') + 1);
            }
            tables.setSelectedIndex(0);
        }

        if (paramname.equals("fieldnames")) {
            listModel.removeAllElements();
            //System.out.println("inside parameterUpdated() of DBExplorePanel: got fieldnames");

            String paramstr = (String) task.getParameter(paramname);
            while (paramstr.indexOf('\0') > -1) {
                listModel.addElement(paramstr.substring(0, paramstr.indexOf('\0')));
                paramstr = paramstr.substring(paramstr.indexOf('\0') + 1);
            }
        }

        if (paramname.equals("numberOfCols")) {
            //System.out.println("inside parameterUpdated() of DBExplorePanel: got numberOfCols");
            numberOfCols = Integer.parseInt((String) task.getParameter(paramname));
        }

        if (paramname.equals("numberOfRows")) {
            //System.out.println("inside parameterUpdated() of DBExplorePanel: got numberOfRows");
            numberOfRows = Integer.parseInt((String) task.getParameter(paramname));
        }

        if (paramname.equals("results")) {
            //System.out.println("inside parameterUpdated() of DBExplorePanel: got results");
            if ((task.getParameter(paramname)).equals("empty")) {
            } else {
                String paramstr = (String) task.getParameter(paramname);

                if (rowCounter == 0) {
                    data = new String[numberOfRows][numberOfCols];
                }

                int i = 0;
                while (paramstr.indexOf('\0') > -1) {
                    data[rowCounter][i] = paramstr.substring(0, paramstr.indexOf('\0'));
                    i = i + 1;
                    paramstr = paramstr.substring(paramstr.indexOf('\0') + 1);
                }
                rowCounter++;

                if (rowCounter == numberOfRows) {
                    qtm.setData(data, headers);
                }
            }
        }
        if (paramname.equals("showTable")) {
            //System.out.println("inside parameterUpdated() of DBExplorePanel: got showTable");
            showTable();
        }
        if (paramname.equals("pressedQuery")) {
            //System.out.println("inside parameterUpdated() of DBExplorePanel: got pressedQuery");
            if ((task.getParameter(paramname)).equals("false")) {
            }
            if ((task.getParameter(paramname)).equals("true")) {
            }
        }
        if (paramname.equals("headersVec")) {
            //System.out.println("inside parameterUpdated() of DBExplorePanel: got headersVec");
            StringVector headersVec = (StringVector) task.getParameter(paramname);
            //System.out.println("Panel recieved column headers, there are " + headersVec.size() + " columns");
            headers = new String[headersVec.size()];
            for (int i = 0; i < headersVec.size(); ++i) {
                headers[i] = (String) headersVec.elementAt(i);
            }
        }
    }

    /**
     * Called before a data input node is removed.
     */
    public void nodeRemoved(TaskNodeEvent event) {
    }

    /**
     * Called when a data input node is added.
     */
    public void nodeAdded(TaskNodeEvent event) {
    }

    /**
     * Called when the core properties of a task change i.e. its name, whether it is running continuously etc.
     */
    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }


    public void valueChanged(ListSelectionEvent e) {

        if (e.getSource() == listField) {
            JList source = (JList) e.getSource();
            Object[] values = source.getSelectedValues();

            //headers = new String[values.length];

            if (values.length != 0) {
                fields = "";
                for (int i = 0; i < (values.length - 1); ++i) {
                    String word = (String) values[i];
                    fields += word + ", ";
                    //headers[i] = word;
                }
                for (int i = values.length - 1; i < values.length; ++i) {
                    String word = (String) values[i];
                    fields += word;
                    //headers[i] = word;
                }

                getTask().setParameter("fields", fields);
                /*
                   Do the following line of you want the number of output nodes to update
               automatically as the number of selected fields changes - as opposed to
               waiting for the query to br run (which takes time)
            */
                getTask().setParameter("numberOfNodes", String.valueOf(values.length));
            }

        }

    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == queryButton) {
            rowCounter = 0;
            applyClicked();
            getTask().setParameter("pressedQuery", String.valueOf(queryButtonCounter));
            ++queryButtonCounter;
        }
        if (event.getSource() == countButton) {
            applyClicked();
            getTask().setParameter("pressedCount", String.valueOf(countButtonCounter));
            ++countButtonCounter;
        }
        if (event.getSource() == hostname) {
            if (hostname.getItemCount() != 0) {
                setParameter("hostname", (String) hostname.getSelectedItem());
                getTask().setParameter("connect", String.valueOf(true));
            }
        }
        if (event.getSource() == databases) {
            if (databases.getItemCount() != 0) {
                getTask().setParameter("database", (String) (((JComboBox) event.getSource()).getSelectedItem()));
            }
        }
        if (event.getSource() == tables) {
            if (tables.getItemCount() != 0) {
                getTask().setParameter("table", (String) (((JComboBox) event.getSource()).getSelectedItem()));
            }
        }
    }

    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        // setParameter puts the value in a buffer that is committed to the task
        // when apply/ok is clicked

        if (event.getSource() == username) {
            //System.out.println("focus lost on username field on DBExplorePanel");
            setParameter("username", username.getText());
        }
        if (event.getSource() == password) {
            //System.out.println("focus lost on password field on DBExplorePanel");
            setParameter("password", new String(password.getPassword()));
        }
        if (event.getSource() == criteria) {
            //System.out.println("focus lost on criteria field on DBExplorePanel");
            setParameter("criteria", new String(criteria.getText()));
        }
        if (event.getSource() == generalQuery) {
            //System.out.println("focus lost on generalQuery field on DBExplorePanel");
            setParameter("generalQuery", new String(generalQuery.getText()));
        }
    }


}
