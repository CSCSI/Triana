package org.trianacode.pegasus.gui.guiUnits;

import org.apache.commons.logging.Log;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.pegasus.dax.Displayer;
import org.trianacode.pegasus.dax.FileUnit;
import org.trianacode.pegasus.string.CharSequencePattern;
import org.trianacode.pegasus.string.CounterPattern;
import org.trianacode.pegasus.string.PatternCollection;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 19/05/2011
 * Time: 22:00
 * To change this template use File | Settings | File Templates.
 */
@org.trianacode.annotation.Tool(renderingHints = {"DAX File"})
public class DaxFile extends FileUnit implements TaskConscious, Displayer, ActionListener, ItemListener {

    private static Log devLog = Loggers.DEV_LOGGER;

    File location;

    JLabel collectLabel; // = new JLabel("");
    JLabel iterLabel;  //= new JLabel("");
    JTextField nameField; //= new JTextField("");
    JTextField locationField; //= new JTextField("");
    JTextArea fileListArea; //= new JTextArea("Example filenames here..\n\n");
    private JPanel namePanel; //= new JPanel();
    private JPanel collectionPanel; //= new JPanel();
    JComboBox namingPatternBox; //= new JComboBox();
//    PatternCollection namingPattern = null;

    JButton nameReset;
    JButton namingButton;
    JButton locationButton;
    JCheckBox locationCheck;
    JComboBox numbersCombo;
    JCheckBox one2oneCheck;
    JCheckBox collectionCheck;
    JComboBox locationTypeCombo;
    JLabel numberLabel;
    JPanel numberFilesPanel;
    JPanel customNamePanel;
    JPanel locationPanel;

    @org.trianacode.annotation.Process(gather = true)
    public UUID fakeProcess(List list) {
        return this.fileUnitProcess(list);
    }

    private void apply() {
        if (physicalFile) {
            fileProtocol = (String) locationTypeCombo.getSelectedItem();
            locationString = locationField.getText();
            devLog.debug("PFL : " + fileProtocol + locationString + File.separator + nameField.getText());
        } else {
            devLog.debug("File does not have a physical location");
        }
        if (!collection) {
            one2one = false;
        }
        changeToolName(nameField.getText());
        fillFileListArea();
        setParams();
    }


    @CustomGUIComponent
    public Component getComponent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

//        FileActionPerformer fileActionPerformer = new FileActionPerformer();
//        FileItemPerformer fileItemPerformer = new FileItemPerformer();

        nameField = new JTextField(fileName);
//        nameField.setText(fileName);
        namePanel = new JPanel(new BorderLayout());
        namePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File"));

        JLabel nameLabel = new JLabel("File Name : ");
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(nameField, BorderLayout.CENTER);

//          locationPanel
        JPanel locationMainPanel = new JPanel();
        locationMainPanel.setLayout(new BoxLayout(locationMainPanel, BoxLayout.Y_AXIS));
        locationMainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Location"));
        locationPanel = new JPanel(new BorderLayout());

        final JPanel locationCheckPanel = new JPanel(new BorderLayout());
        locationCheck = new JCheckBox("Set location : ", physicalFile);
        locationCheck.addItemListener(this);
        locationCheckPanel.add(locationCheck, BorderLayout.WEST);

        locationButton = new JButton("...");
        String[] locationTypes = {"file://", "http://"};
        locationTypeCombo = new JComboBox(locationTypes);
        locationTypeCombo.setActionCommand("locationTypeCombo");
        if (!fileProtocol.equals("")) {
            locationTypeCombo.setSelectedItem(fileProtocol);
        }
        locationTypeCombo.addActionListener(this);

        locationField = new JTextField("");
        locationField.setText(locationString);
        locationButton.setActionCommand("locationButton");
        locationButton.addActionListener(this);

        locationPanel.add(locationTypeCombo, BorderLayout.WEST);
        locationPanel.add(locationField, BorderLayout.CENTER);
        locationPanel.add(locationButton, BorderLayout.EAST);
        setEnabling(locationPanel, locationCheck.isSelected());

        locationMainPanel.add(locationCheckPanel);
        locationMainPanel.add(locationPanel);

        mainPanel.add(namePanel);
        mainPanel.add(locationMainPanel);

        // collectionPanel
        collectionPanel = new JPanel();
        collectionPanel.setLayout(new BoxLayout(collectionPanel, BoxLayout.Y_AXIS));
        collectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File Collection Options"));

        JPanel collectionCheckPanel = new JPanel(new BorderLayout());
        numberFilesPanel = new JPanel(new BorderLayout());
        customNamePanel = new JPanel(new GridLayout(1, 3));

        collection = isCollection();
        numberOfFiles = getNumberOfFiles();
        collectionCheck = new JCheckBox("Collection", collection);
        collectionCheck.addItemListener(this);
        collectLabel = new JLabel("");
        if (collection) {
            collectLabel.setText("Collection of files.");
        } else {
            collectLabel.setText("Not a collection");
        }
        collectionCheckPanel.add(collectionCheck, BorderLayout.WEST);
        collectionCheckPanel.add(collectLabel, BorderLayout.EAST);
        collectionPanel.add(collectionCheckPanel);

        one2oneCheck = new JCheckBox("One2one with previous jobs", one2one);
        one2oneCheck.addItemListener(this);
        JPanel one2oneCheckPanel = new JPanel(new BorderLayout());
        one2oneCheckPanel.add(one2oneCheck);
        collectionPanel.add(one2oneCheckPanel);
        setEnabling(one2oneCheckPanel, false);

        numberLabel = new JLabel("No. files : " + numberOfFiles);
        final String[] numbers = new String[100];
        for (int i = 1; i < 100; i++) {
            numbers[i] = "" + i;
        }
        numbersCombo = new JComboBox(numbers);
        numbersCombo.setActionCommand("numbersCombo");
        numbersCombo.setSelectedItem("" + numberOfFiles);
        numbersCombo.addActionListener(this);

        numberFilesPanel.add(numberLabel, BorderLayout.WEST);
        numberFilesPanel.add(numbersCombo, BorderLayout.EAST);
        collectionPanel.add(numberFilesPanel);
        setEnabling(numberFilesPanel, false);

        JLabel customLabel = new JLabel("Custom name :");
        namingButton = new JButton("Custom pattern...");
        namingButton.setActionCommand("namingButton");
        namingButton.addActionListener(this);

        nameReset = new JButton("Reset");
        nameReset.setActionCommand("nameReset");
        nameReset.addActionListener(this);

        customNamePanel.add(customLabel);
        customNamePanel.add(namingButton);
        customNamePanel.add(nameReset);
        collectionPanel.add(customNamePanel);
        setEnabling(customNamePanel, false);

        fileListArea = new JTextArea("Example filenames here..\n\n");
        fileListArea.setRows(6);
        fileListArea.setEditable(false);
        JScrollPane sp = new JScrollPane(fileListArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        collectionPanel.add(sp);
        setEnabling(fileListArea, false);

        mainPanel.add(collectionPanel);
        String word = "no";
        if (task != null) {
            word = task.toString();
        }
        mainPanel.add(new JLabel("Task? " + word));

        JButton apply = new JButton("Apply");
        apply.setActionCommand("apply");
        apply.addActionListener(this);
        mainPanel.add(apply);

        return mainPanel;
    }

    //  class FileActionPerformer implements ActionListener {
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("apply")) {
            apply();
        }
        if (actionEvent.getActionCommand().equals("nameReset")) {
            namingPattern = null;
            fillFileListArea();
        }
        if ((actionEvent.getActionCommand().equals("namingButton"))) {
            int parts = 3;
            if (namingPattern != null) {
                parts = namingPattern.getPatternCollectionSize();
            }
            namingPattern = (PatternCollection) NamingPanel.getValue(parts);
            fillFileListArea();
            if (namingPattern != null) {
                devLog.debug("ChosenNamingPattern : " + namingPattern.toString());
            }
        }
        if (actionEvent.getActionCommand().equals("numbersCombo")) {
            numberOfFiles = Integer.parseInt((String) numbersCombo.getSelectedItem());
            numberLabel.setText("No. files : " + numberOfFiles);
            fillFileListArea();
        }
        if (actionEvent.getActionCommand().equals("locationButton")) {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal = chooser.showDialog(GUIEnv.getApplicationFrame(), "Select");
            String filePath = null;
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                location = chooser.getSelectedFile();
                if (location != null) {
                    locationField.setText(location.getParent());
                    nameField.setText(location.getName());
//                        filePath = f.getAbsolutePath();
//                        locationField.setText(filePath);
//                        if(f.isFile()){
//                            nameField.setText(f.getName());
//                        }
                }
            }
        }
        if (actionEvent.getActionCommand().equals("locationTypeCombo")) {
            fileProtocol = (String) locationTypeCombo.getSelectedItem();
            if (fileProtocol.equals("file://")) {
                locationButton.setVisible(true);
            } else {
                locationButton.setVisible(false);
            }
        }
    }
    //  }

    //   class FileItemPerformer implements ItemListener{
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getSource().equals(one2oneCheck)) {
            if (one2oneCheck.isSelected()) {
                one2one = true;
                setEnabling(numberFilesPanel, false);
                setEnabling(customNamePanel, false);
                setEnabling(fileListArea, false);
            } else {
                one2one = false;
                setEnabling(numberFilesPanel, true);
                setEnabling(customNamePanel, true);
                setEnabling(fileListArea, true);
            }
        }
        if (itemEvent.getSource().equals(collectionCheck)) {
            if (collectionCheck.isSelected()) {
                collectLabel.setText("Collection of files.");
                collection = true;
                setEnabling(one2oneCheck, true);
                setEnabling(numberFilesPanel, true);
                setEnabling(customNamePanel, true);
                setEnabling(fileListArea, true);

            } else {
                collectLabel.setText("Not a collection");
                collection = false;
                setEnabling(one2oneCheck, false);
                setEnabling(numberFilesPanel, false);
                setEnabling(customNamePanel, false);
                setEnabling(fileListArea, false);
            }
        }
        if (itemEvent.getSource().equals(locationCheck)) {
            if (locationCheck.isSelected()) {
                physicalFile = true;
                setEnabling(locationPanel, true);
                locationField.requestFocus();
            } else {
                physicalFile = false;
                setEnabling(locationPanel, false);
            }
        }
    }
    //   }

    public void setEnabling(Component c, boolean enable) {
        c.setEnabled(enable);
        if (c instanceof Container) {
            Component[] arr = ((Container) c).getComponents();
            for (int j = 0; j < arr.length; j++) {
                setEnabling(arr[j], enable);
            }
        }
    }

    /**
     * Uses the currently set namingPattern object to list the names the files will take.
     */
    private void fillFileListArea() {
        if (namingPattern == null) {
            namingPattern = new PatternCollection("-");
            CharSequencePattern a = new CharSequencePattern(nameField.getText());
            CounterPattern b = new CounterPattern(0, 3, 1, 1);
            namingPattern.add(a);
            namingPattern.add(b);
        }

        namingPattern.resetCount();
        fileListArea.setText("Files will be named : \n");
        for (int i = 0; i < numberOfFiles; i++) {
            fileListArea.append(namingPattern.next() + "\n");
        }

//        iterLabel.setText("Filename " + (namingPattern.varies() ? "iterates well" : "does not vary, will +\"01\""));
//        nameField.setText(namingPattern.next());
//        namingPattern.resetCount();
//        fileListArea.setText("Files will be named : \n");
//        for(int i = 0 ; i< numberOfFiles; i++){
//            devLog.debug("adding a filename to fileListArea");
//            fileListArea.append(namingPattern.next() + "\n");
//        }
//
//        fileListArea.setText("Files will be named : \n");
//        String name = (String)getParameter("fileName");
//        for(int i = 0 ; i< numberOfFiles; i++){
//            devLog.debug("adding a name to fileListArea");
//            fileListArea.append(name + "." + "\n");
//        }

    }

    @Override
    public void displayMessage(String string) {
        devLog.debug(string);
    }
}

/**
 * JDialog to provide the formation of a naming pattern
 */

class NamingPanel extends JDialog {
    private static Log devLog = Loggers.DEV_LOGGER;

    JPanel mainPanel = new JPanel();
    JLabel hi = new JLabel();
    JTextField name = new JTextField("");
    JComboBox separatorBox;
    String separator;
    int parts = 1;
    Vector<String> nameParts;
    Vector<JComboBox> patternDetailVector = new Vector<JComboBox>();
    String[] patternOptions = {"words", "numbers", "dates", "letters"};
    String[] numberArray = {"1", "01", "001", "0001"};
    String[] dateArray = {"", "dd-mm-yy", "yy-mm-dd", "hh-mm-ss"};
    String[] letterArray = {"A", "AA", "AAA", "AAAA", "AAAAA"};
    PatternCollection chosenNamingPattern = null;

    public static Object getValue(int p) {
        NamingPanel np = new NamingPanel(p);
        return np.getReturnValue();
    }

    private Object getReturnValue() {
        return chosenNamingPattern;
    }

    private void prepNameParts() {
        for (int i = 0; i < parts; i++) {
            nameParts.add("");
        }
    }

    private void refresh() {
        mainPanel.revalidate();
        this.pack();
    }

    public NamingPanel(int p) {
        this.setModal(true);
        this.setLocationRelativeTo(this.getOwner());
        this.parts = p;
        nameParts = new Vector<String>();
        prepNameParts();

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File name pattern"));

        JPanel setNumParts = new JPanel(new GridLayout(1, 3, 5, 5));
        setNumParts.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel midPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        midPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        final JPanel lowerPanel = new JPanel();
        lowerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel exampleLabel = new JLabel("XXX-XXX-XXX");
        mainPanel.add(exampleLabel);

        JLabel label = new JLabel("Change number of parts");
        setNumParts.add(label);
        String numbers[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        final JComboBox numsBox = new JComboBox(numbers);
        numsBox.setSelectedItem((String) "" + p);
        setNumParts.add(numsBox);

        JButton setButton = new JButton("Set");
        setButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                parts = Integer.parseInt(numsBox.getSelectedItem().toString());
                hi.setText("Filename will have " + parts + " part" + ((parts > 1) ? "s." : "."));
                addChoosers(lowerPanel);
                refresh();
            }
        });
        setNumParts.add(setButton);
        mainPanel.add(setNumParts);


        hi.setText("Filename will have " + parts + " part" + ((parts > 1) ? "s." : "."));
        topPanel.add(hi);
        name.setText(buildName("-"));
        topPanel.add(name);
        mainPanel.add(topPanel);

        JLabel l1 = new JLabel("Seperator : ");
        String[] seperatorOptions = {"- (hyphen)", " (space)", "_ (underscore)", ". (period)", "(no seperator)"};
        separatorBox = new JComboBox(seperatorOptions);
        separatorBox.setSelectedIndex(0);
        separatorBox.setEditable(true);
        separatorBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                separator = getSeparator();
                setNameLabel(buildName(getSeparator()));
            }
        });
        midPanel.add(l1);
        midPanel.add(separatorBox);
        mainPanel.add(midPanel);

        addChoosers(lowerPanel);

        mainPanel.add(lowerPanel);
        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okPressed();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new helpFrame();
            }
        });

        JPanel endPanel = new JPanel();
        endPanel.add(ok);
        endPanel.add(cancelButton);
        endPanel.add(helpButton);
        mainPanel.add(endPanel);

        add(mainPanel);
        this.setTitle("File naming pattern");
        this.pack();
        this.setVisible(true);

    }

    private void okPressed() {
        boolean completeName = true;
        for (JComboBox jcb : patternDetailVector) {
            if (jcb.getSelectedItem().equals("")) {
                completeName = false;
            }
        }
        if (completeName) {
            try {
                PatternCollection pc = new PatternCollection(getSeparator());
                for (int i = 0; i < parts; i++) {
                    pc.add(new CharSequencePattern(nameParts.get(i)));
                }
                for (int i = 0; i < 5; i++) {
                    devLog.debug(pc.next());
                }

                devLog.debug((pc.varies()) ? "Name iterates ok." : "Name does not iterate");
                if (!pc.varies()) {
                    pc.add(new CounterPattern(0, 3, 1, 1));
                }

                setNamingPattern(pc);
            } catch (Exception ex) {
            } finally {
                dispose();
            }
        } else {
            devLog.debug("name not complete");
            JOptionPane.showMessageDialog(mainPanel,
                    "Name not complete.\nFill or remove empty part(s).",
                    "Name Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addChoosers(JPanel lowerPanel) {
        lowerPanel.removeAll();
        if (patternDetailVector.size() > 0) {
            patternDetailVector.removeAllElements();
        }
        lowerPanel.setLayout(new GridLayout(parts, 3, 5, 5));

        for (int i = 0; i < parts; i++) {
            devLog.debug("adding a line of choosers");
            final int finalI = i;

            final JComboBox section = new JComboBox(patternOptions);
            section.setEditable(true);

            String[] patternDetail = {""};
            final JComboBox detailChooser = new JComboBox(patternDetail);
            patternDetailVector.add(detailChooser);

            section.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    Object selected = section.getSelectedItem();
                    setSection(finalI, (String) selected);
                    setNameLabel(buildName(getSeparator()));
                    fillDetailCombo(detailChooser, selected);
                }
            });

            fillDetailCombo(detailChooser, section.getSelectedItem());

            JLabel lx = new JLabel("Pattern " + (i + 1) + " : ");

            lowerPanel.add(lx);
            lowerPanel.add(section);
            lowerPanel.add(detailChooser);

            JButton setButton = new JButton("Set");
            setButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    setSection(finalI, (String) detailChooser.getSelectedItem());
                    setNameLabel(buildName(getSeparator()));
                }
            });
            lowerPanel.add(setButton);
        }
        //   lowerPanel.revalidate();
    }

    private void setNamingPattern(PatternCollection pc) {
        devLog.debug("chosenNamingPattern : " + pc.toString());
        chosenNamingPattern = pc;
    }

    private void fillDetailCombo(JComboBox detail, Object patternSelection) {
        detail.removeAllItems();
        detail.setEditable(false);

        if (patternSelection.equals("numbers")) {
            addArrayToCombo(detail, numberArray);
        }
        if (patternSelection.equals("dates")) {
            addArrayToCombo(detail, dateArray);
            detail.setEditable(true);
        }
        if (patternSelection.equals("words")) {
            String[] array = {""};
            addArrayToCombo(detail, array);
            detail.setEditable(true);
        }
        if (patternSelection.equals("letters")) {
            addArrayToCombo(detail, letterArray);
        }
    }

    private void addArrayToCombo(JComboBox box, String[] array) {
        for (int i = 0; i < array.length; i++) {
            devLog.debug("adding array to combobox");
            box.addItem(array[i]);
        }
    }

    private void setNameLabel(String n) {
        name.setText(n);
    }

    private void setSection(int i, String s) {
        if (i >= nameParts.size()) {
            devLog.debug("trying to set " + i + " nameParts is : " + nameParts.size());
            for (int j = nameParts.size(); j < (i + 1); j++) {

                nameParts.add("");
            }
            devLog.debug("nameParts is now : " + nameParts.size());
        }
        devLog.debug("Setting namePart " + i + " as : " + s);
        nameParts.setElementAt(s, i);
    }

    private String getSeparator() {
        String s = (String) separatorBox.getSelectedItem();
        s = s.substring(0, 1);
        if (s.equals("(")) {
            s = "";
        }
        return s;
    }

    public String buildName(String sep) {
        String name = "";
        int size = nameParts.size();
        if (size > 1) {
            for (int i = 0; i < (size - 1); i++) {
                String bit = "XXX";
                if (nameParts.get(i) != null) {
                    bit = nameParts.get(i);
                }
                name += bit + sep;
            }
        }
        if (nameParts.get(size - 1) != null) {
            name += nameParts.get(size - 1);
        } else {
            name += "XXX";
        }

        return name;
    }

    public String getName() {
        return name.getText();
    }

}

class helpFrame extends JFrame {
    public helpFrame() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel helpLabel = new JLabel("This is helpful");

        String[] headings = {"Symbol", "Meaning", "Type", "Example"};
        String[][] data = {{"G", "Era", "Text", "GG -> AD"},
                {"y", "Year", "Number", "yy -> 03, yyyy -> 2003"},
                {"M", "Month", "Text or Number", "M -> 7, M -> 12, MM -> 07, MMM -> Jul, MMMM -> December"},
                {"d", "Day in month", "Number", "d -> 3, dd -> 03"},
                {"h", "Hour (1-12, AM/PM", "Number", "h -> 3, hh -> 03"},
                {"H", "Hour (0-24)", "Number", "H -> 15, HH -> 15"},
                {"k", "Hour (1-24)", "Number", "k -> 3, kk -> 03"},
                {"K", "Hour (1-11 AM/PM)", "Number", "K -> 15, KK -> 15"},
                {"m", "Minute", "Number", "m -> 7, m -> 15, mm -> 15"},
                {"s", "Second", "Number", "s -> 15, kk -> 15"},
                {"S", "Millisecond (0-999)", "Number", "SSS -> 007"},
                {"E", "Day in week", "Text", "EEE -> Tue, EEEE -> Tuesday"},
                {"D", "Day in year (1-365 or 1-364", "Number", "D -> 65, DDD -> 065"},
                {"F", "Day of week in month (1-5)", "Number", "F -> 1"},
                {"w", "Week in year (1-53)", "Number", "w -> 7"},
                {"W", "Week in month (1-5)", "Number", "W -> 3"},
                {"a", "AM/PM", "Text", "a -> AM, aa -> AM"},
                {"z", "Time zone", "Text", "z -> EST, zzz -> EST, zzzzz -> Eastern Standard Time"},
                {"'", "Escape for text", "Delimiter", "'hour' h -> hour 9"},
                {"\"", "Single quote", "Literal", "ss\"SSS -> 45'876"}
        };
        JTable table = new JTable(data, headings);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        panel.add(scrollPane);

        panel.add(helpLabel);
        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panel.add(ok);
        this.add(panel);
        this.setSize(600, 400);
        this.setVisible(true);
        this.setTitle("Help");
    }
}
