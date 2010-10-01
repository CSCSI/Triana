package signalproc.converters;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.gui.windows.WindowButtonConstants;

/**
 * A RawToGenPanel UnitPanel to apply an interface for converting between the Raw data set and data analysis types
 * within OCL, e.g. SampleSet, Sepctrum and their complex counterparts.
 *
 * @author Ian Taylor
 * @version 1.0 1 August 2000
 */
public class VectToGenPanel extends UnitPanel implements ItemListener,
        ActionListener, DocumentListener {

    /**
     * The parameter name that indicates the mode of the panel
     */
    public static final String PANEL_MODE = "panelMode";

    /**
     * The panel modes
     */
    public static final String SAMP = "Samp";
    public static final String SPEC = "Spec";
    public static final String CSAMP = "CSamp";
    public static final String CSPEC = "CSpec";
    public static final String TWOD = "2D";


    /**
     * A simple textfield to edit sampling frequency of the data
     */
    JTextField sampFreq;

    /**
     * A textfield to edit the start time of the data
     */
    JLabel time;

    /**
     * A textfield to edit the description of the data
     */
    JTextArea description;

    /**
     * A simple textfield to edit the y label of the data
     */
    JTextField ylabel;

    /**
     * A simple textfield to edit the x label of the data
     */
    JTextField xlabel;

    /**
     * The order for Complex spectra data
     */
    JComboBox complexOrder;

    JButton ok;

    /**
     * The mode the panel is run in, default = SAMP
     */
    private String mode = SAMP;


    /**
     * @return false so that parameter changes are not committed automatically
     */
    public boolean isAutoCommitByDefault() {
        return false;
    }

    /**
     * Overrides UnitPanel method to return WindowConstans.OK_CANCEl_APPLY_BUTTONS.
     */
    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS;
    }


    /**
     * Creates a new RawToGenPanel for RawTo.. units.  The mode is set to SPEC, SAMP, CSPEC, CSAMP or TWOD depending on
     * what this window is getting information for.
     */
    public void setObject(Object unit) {
        super.setObject(unit);

        if (getTask().isParameterName(PANEL_MODE)) {
            mode = (String) getTask().getParameter(PANEL_MODE);
        }

        createWidgets();
        layoutPanel();
    }

    public void createWidgets() {
        if (mode.equals(TWOD)) {
            xlabel = new JTextField(20);
            ylabel = new JTextField(20);
        } else {
            sampFreq = new JTextField((String) getTask().getParameter("sampFreq"), 10);
            time = new JLabel((String) getTask().getParameter("time"), JLabel.LEFT);
            description = new JTextArea((String) getTask().getParameter("description"), 2, 30);
        }

        complexOrder = new JComboBox();

        if (mode.equals(TWOD)) {
            complexOrder.addItem("Data are Y Values");
            complexOrder.addItem("Data are X Values");
            complexOrder.addItem("X Y X Y X Y ....");
            complexOrder.addItem("X X X .. Y Y Y ..");
        } else {
            complexOrder.addItem("R I  R I  RI ..");
            complexOrder.addItem("R R R .. I I I ..");
        }

        complexOrder.setSelectedItem((String) getTask().getParameter("complex"));

        complexOrder.addItemListener(this);
        sampFreq.addActionListener(this);
//        time.addActionListener(this);
        description.getDocument().addDocumentListener(this);
    }


    /**
     * The layout of the RawToGen window.
     */
    public void layoutPanel() {
        setLayout(new BorderLayout());

        JPanel labelpanel = new JPanel();
        JPanel parampanel = new JPanel();

        if ((mode.equals(CSPEC)) || (mode.equals(CSAMP)) || (mode.equals(TWOD))) {
            labelpanel.setLayout(new GridLayout(3, 1));
            parampanel.setLayout(new GridLayout(3, 1));
        } else {
            labelpanel.setLayout(new GridLayout(2, 1));
            parampanel.setLayout(new GridLayout(2, 1));
        }

        if (mode.equals(TWOD)) {
            labelpanel.add(new JLabel("X Label", JLabel.LEFT));
            parampanel.add(xlabel);

            labelpanel.add(new JLabel("Y Label", JLabel.LEFT));
            parampanel.add(ylabel);
        } else {
            labelpanel.add(new JLabel("Sampling Frequency : ", JLabel.LEFT));
            parampanel.add(sampFreq);

            labelpanel.add(new JLabel("Time stamp : ", JLabel.LEFT));
            parampanel.add(time);
        }

        if ((mode.equals(CSPEC)) || (mode.equals(CSAMP)) || (mode.equals(TWOD))) {
            labelpanel.add(new JLabel("Order of Complex Data : ", JLabel.LEFT));
            parampanel.add(complexOrder);
        }

        JPanel contain = new JPanel(new BorderLayout());
        contain.add(labelpanel, BorderLayout.WEST);
        contain.add(parampanel, BorderLayout.CENTER);
        add(contain, BorderLayout.NORTH);

        if (!mode.equals(TWOD)) {
            JPanel descpanel = new JPanel(new BorderLayout());

            descpanel.add(new JLabel("Description:", JLabel.LEFT), BorderLayout.NORTH);
            descpanel.add(description, BorderLayout.CENTER);

            add(descpanel, BorderLayout.CENTER);
        }
    }


    /**
     * Resets the components in the panel to those specified by the task.
     */
    public void reset() {
        super.reset();

        if (getTask() != null) {
            sampFreq.setText((String) getTask().getParameter("sampFreq"));
            time.setText((String) getTask().getParameter("time"));
            description.setText((String) getTask().getParameter("description"));

            if (complexOrder != null) {
                complexOrder.setSelectedItem((String) getTask().getParameter("complex"));
            }
        }
    }


    /**
     * Called when the ok button is clicked on the parameter window. Commits any parameter changes.
     */
    public void okClicked() {
        updateParameters();
        super.okClicked();
    }

    /**
     * Called when the apply button is clicked on the parameter window. Commits any parameter changes.
     */
    public void applyClicked() {
        updateParameters();
        super.applyClicked();
    }

    /**
     * Updates the parameters
     */
    private void updateParameters() {
        setParameter("sampFreq", sampFreq.getText());
        setParameter("time", time.getText());
        setParameter("description", description.getText());

        if (complexOrder != null) {
            setParameter("complex", (String) complexOrder.getSelectedItem());
        }
    }


    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (e.getSource() == sampFreq) {
            updateParameter("sampFreq", sampFreq.getText());
        }
/*        else if (e.getSource() == time)
            updateParameter("time", time.getText());*/
    }

    public void insertUpdate(DocumentEvent d) {
        updateParameter("description", description.getText());
    }

    public void removeUpdate(DocumentEvent d) {
        updateParameter("description", description.getText());
    }

    public void changedUpdate(DocumentEvent d) {
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == complexOrder) {
            updateParameter("complex", (String) complexOrder.getSelectedItem());
        }
    }

    /**
     * @return the date stamp of the data
     */
    public Date getDate() {
        String textDate = time.getText();
        return null; // ?? how to convert ?
    }


    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Converters.html";
    }
}
















