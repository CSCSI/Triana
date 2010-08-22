package common.output;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.trianacode.gui.panels.FormLayout;
import org.trianacode.gui.panels.ParameterPanel;


/**
 * The panel for the serialize tool
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */
public class SerializePanel extends ParameterPanel
        implements FocusListener, ItemListener {

    private static final String TRIANA_SERIALIZE = "Triana Serialize";
    private static final String JAVA_SERIALIZE = "Java Serialize";

    private static final String XML_SUFFIX = ".xml";


    private JComboBox typecombo = new JComboBox(new DefaultComboBoxModel());
    private JTextField toolname = new JTextField(10);
    private JTextField pack = new JTextField(15);
    private JComboBox toolboxes = new JComboBox(new DefaultComboBoxModel());
    private JTextField filename = new JTextField(25);
    private JCheckBox append = new JCheckBox();
    private JCheckBox seq = new JCheckBox();
    private JTextField length = new JTextField(3);

    private String tmp;


    /**
     * This method is called before the panel is displayed. It should initialise the panel layout.
     */
    public void init() {
        initPanel();
    }

    public void initPanel() {
        setLayout(new BorderLayout());

        JPanel typepanel = new JPanel(new BorderLayout(3, 0));
        typepanel.add(new JLabel("Serialization Method"), BorderLayout.WEST);
        typepanel.add(typecombo, BorderLayout.CENTER);
        typepanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        DefaultComboBoxModel model = (DefaultComboBoxModel) typecombo.getModel();
        model.addElement(TRIANA_SERIALIZE);
        model.addElement(JAVA_SERIALIZE);

        JPanel typemain = new JPanel(new BorderLayout());
        typemain.add(typepanel, BorderLayout.WEST);

        JPanel formpanel = new JPanel(new FormLayout(3, 3));

        JPanel toolpanel = new JPanel(new BorderLayout());
        toolpanel.add(toolname, BorderLayout.WEST);

        formpanel.add(new JLabel("Tool Name"));
        formpanel.add(toolpanel);

        JPanel packpanel = new JPanel(new BorderLayout());
        packpanel.add(pack, BorderLayout.WEST);

        formpanel.add(new JLabel("Package"));
        formpanel.add(packpanel, BorderLayout.NORTH);

        JPanel toolboxpanel = new JPanel(new BorderLayout());
        toolboxpanel.add(toolboxes, BorderLayout.WEST);

        formpanel.add(new JLabel("Tool Box"));
        formpanel.add(toolboxpanel);

        JPanel filepanel = new JPanel(new BorderLayout());
        filepanel.add(filename, BorderLayout.WEST);

        formpanel.add(new JLabel("File Name"));
        formpanel.add(filepanel);

        JPanel appendpanel = new JPanel(new BorderLayout(3, 0));
        appendpanel.add(new JLabel("Append Sequence Number"), BorderLayout.WEST);
        appendpanel.add(append, BorderLayout.CENTER);
        appendpanel.setBorder(new EmptyBorder(3, 0, 0, 0));

        JPanel seqpanel = new JPanel(new BorderLayout(3, 0));
        seqpanel.add(new JLabel("Serialize Sequence"), BorderLayout.WEST);
        seqpanel.add(seq, BorderLayout.CENTER);

        JPanel lengthpanel = new JPanel(new BorderLayout(3, 0));
        lengthpanel.add(new JLabel("Length"), BorderLayout.WEST);
        lengthpanel.add(length, BorderLayout.CENTER);

        JPanel seqcont1 = new JPanel(new BorderLayout(10, 0));
        seqcont1.add(seqpanel, BorderLayout.WEST);
        seqcont1.add(lengthpanel, BorderLayout.CENTER);

        JPanel seqcont2 = new JPanel(new BorderLayout());
        seqcont2.add(seqcont1, BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.add(typemain, BorderLayout.NORTH);
        main.add(formpanel, BorderLayout.CENTER);
        main.add(appendpanel, BorderLayout.SOUTH);

        add(main, BorderLayout.NORTH);
        add(seqcont2, BorderLayout.CENTER);

        toolname.addFocusListener(this);
        pack.addFocusListener(this);
        toolboxes.addFocusListener(this);
        filename.addFocusListener(this);
        length.addFocusListener(this);

        typecombo.addItemListener(this);
        append.addItemListener(this);
        seq.addItemListener(this);
    }


    /**
     * This method is called when cancel is clicked on the parameter window. It should synchronize the GUI components
     * with the task parameter values
     */
    public void reset() {
        if (isParameterName("type")) {
            typecombo.setSelectedItem(getParameter("type"));

            if (getParameter("type").equals(TRIANA_SERIALIZE)) {
                updateFileName();
            }

            updateEnabled();
        }

        if (isParameterName("filename")) {
            filename.setText((String) getParameter("filename"));
        }

        if (isParameterName("toolname")) {
            toolname.setText((String) getParameter("toolname"));
        }

        if (isParameterName("package")) {
            pack.setText((String) getParameter("package"));
        }

        if (isParameterName("toolboxes")) {
            updateToolboxes();
        }

        if (isParameterName("toolbox")) {
            toolboxes.setSelectedItem(getParameter("toolbox"));
        }

        if (isParameterName("append")) {
            append.setSelected(new Boolean((String) getParameter("append")).booleanValue());
        }

        if (isParameterName("sequence")) {
            seq.setSelected(new Boolean((String) getParameter("sequence")).booleanValue());
            updateEnabled();
        }

        if (isParameterName("sequenceLength")) {
            length.setText((String) getParameter("sequenceLength"));
        }
    }

    /**
     * This method is called when a parameter in the task is updated. It should update the GUI in response to the
     * parameter update
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("type")) {
            typecombo.setSelectedItem(value);

            if (value.equals(TRIANA_SERIALIZE)) {
                updateFileName();
            }

            updateEnabled();
        }

        if (paramname.equals("filename")) {
            filename.setText((String) value);
        }

        if (paramname.equals("toolname")) {
            toolname.setText((String) value);
        }

        if (paramname.equals("package")) {
            pack.setText((String) value);
        }

        if (paramname.equals("toolbox")) {
            toolboxes.setSelectedItem(value);
        }

        if (paramname.equals("toolboxes")) {
            updateToolboxes();
        }

        if (paramname.equals("append")) {
            append.setSelected(new Boolean((String) value).booleanValue());
        }

        if (paramname.equals("sequence")) {
            seq.setSelected(new Boolean((String) value).booleanValue());
            updateEnabled();
        }

        if (paramname.equals("sequenceLength")) {
            length.setText((String) value);
        }
    }


    /**
     * This method is called when the panel is being disposed off. It should clean-up subwindows, open files etc.
     */
    public void dispose() {
        // Insert code to clean-up panel here
    }


    private void updateToolboxes() {
        String[] boxes = (String[]) getParameter("toolboxes");
        DefaultComboBoxModel model = (DefaultComboBoxModel) toolboxes.getModel();
        model.removeAllElements();

        for (int count = 0; count < boxes.length; count++) {
            model.addElement(boxes[count]);
        }

        if (typecombo.getSelectedItem().equals(TRIANA_SERIALIZE)) {
            updateFileName();
        }

        getWindowInterface().getWindow().pack();
    }

    private void updateFileName() {
        String filename;

        if ((toolboxes.getSelectedItem() == null) || (toolname.getText().equals(""))) {
            filename = "";
        } else {
            filename = ((String) toolboxes.getSelectedItem()) +
                    File.separatorChar + pack.getText().replace('.', File.separatorChar) +
                    File.separatorChar + toolname.getText() + XML_SUFFIX;
        }

        this.filename.setText(filename);
        this.filename.setCaretPosition(0);
        setParameter("filename", filename);
    }

    private void updateEnabled() {
        boolean trianaserialize = typecombo.getSelectedItem().equals(TRIANA_SERIALIZE);

        toolname.setEnabled(trianaserialize);
        pack.setEnabled(trianaserialize);
        toolboxes.setEnabled(trianaserialize);

        length.setEnabled(seq.isSelected());
    }

    /**
     * Invoked when a component gains the keyboard focus.
     */
    public void focusGained(FocusEvent event) {
        if (event.getSource() == toolname) {
            tmp = toolname.getText();
        }

        if (event.getSource() == pack) {
            tmp = pack.getText();
        }

        if (event.getSource() == toolboxes) {
            tmp = (String) toolboxes.getSelectedItem();
        }
    }

    /**
     * Invoked when a component loses the keyboard focus.
     */
    public void focusLost(FocusEvent event) {
        if (event.getSource() == filename) {
            setParameter("filename", filename.getText());
        }

        if ((event.getSource() == toolname) && (!tmp.equals(toolname.getText()))) {
            setParameter("toolname", toolname.getText());
            updateFileName();
        }

        if ((event.getSource() == pack) && (!tmp.equals(pack.getText()))) {
            setParameter("package", pack.getText());
            updateFileName();
        }

        if ((event.getSource() == toolboxes) && (!tmp.equals(toolboxes.getSelectedItem()))) {
            setParameter("toolbox", toolboxes.getSelectedItem());
            updateFileName();
        }

        if (event.getSource() == length) {
            try {
                if (Integer.parseInt(length.getText()) <= 0) {
                    length.setText("1");
                }
            } catch (NumberFormatException except) {
                length.setText("1");
            }

            setParameter("sequenceLength", length.getText());
        }
    }

    /**
     * Invoked when an item has been selected or deselected by the user. The code written for this method performs the
     * operations that need to occur when an item is selected (or deselected).
     */
    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == typecombo) {
            setParameter("type", typecombo.getSelectedItem());
            updateEnabled();
        }

        if (event.getSource() == append) {
            setParameter("append", String.valueOf(append.isSelected()));
        }

        if (event.getSource() == seq) {
            setParameter("sequence", String.valueOf(seq.isSelected()));
            updateEnabled();
        }
    }

}
