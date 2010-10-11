package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.pegasus.string.AlphabetPattern;
import org.trianacode.pegasus.string.CounterPattern;
import org.trianacode.pegasus.string.DatePattern;
import org.trianacode.pegasus.string.PatternCollection;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Sep 7, 2010
 * Time: 1:22:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUnitPanel extends ParameterPanel {

    private int numberOfFiles = 1;
    private boolean collection = false;

    JLabel collectLabel = new JLabel("");
    JTextField nameField = new JTextField("");
    JTextArea fileListArea = new JTextArea("Example filenames here..");
    JPanel upperPanel = new JPanel(new GridLayout(2,2,5,5));
    JPanel lowerPanel = new JPanel(new GridLayout(2,3,5,5));
    JComboBox namingPattern = new JComboBox();



    //   public boolean isAutoCommitByDefault(){return true;}

    public void applyClicked(){ apply(); }
    public void okClicked(){ apply(); }
    //  public void cancelClicked(){ reset(); }

    private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        //System.out.println(s);
    }

    private void apply(){
        changeToolName(nameField.getText());
        fillFileListArea();
        setParams();
    }

    public void changeToolName(String name){
        nameField.setText(name);
        log("Changing tool " + getTask().getToolName() + " to : " + name);
        getTask().setParameter("fileName", name);
        getTask().setToolName(name);
    }

    private void setParams(){
        getTask().setParameter("numberOfFiles", numberOfFiles);
        getTask().setParameter("collection", collection);
    }

    public void getParams(){
        collection = isCollection();
        numberOfFiles = getNumberOfFiles();
    }

    @Override
    public void reset() {
        //    nameField.setText((String)getParameter("fileName"));
    }

    @Override
    public void init() {
        getParams();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        upperPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File"));
        JLabel nameLabel = new JLabel("File Name :");
        upperPanel.add(nameLabel);

        changeToolName(getTask().getToolName());
        upperPanel.add(nameField);

        collection = isCollection();
        numberOfFiles = getNumberOfFiles();

        final JCheckBox collectionBox = new JCheckBox("Collection", collection);
        collectionBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (collectionBox.isSelected()) {
                    collectLabel.setText("Collection of files.");
                    collection = true;
                    setEnabling(lowerPanel, true);
                } else {
                    collectLabel.setText("Not a collection");
                    collection = false;
                    setEnabling(lowerPanel, false);
                }
            }
        });
        upperPanel.add(collectionBox);
        if(collection){
            collectLabel.setText("Collection of files.");
        }else{
            collectLabel.setText("Not a collection");
        }
        upperPanel.add(collectLabel);

        add(upperPanel);

        //Lower Panel

        lowerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File Collection Options"));
        JPanel lowerPanel1 = new JPanel(new GridLayout(3, 2, 5, 5));
        final JLabel numberLabel = new JLabel("No. files : " + numberOfFiles);

        final JSlider slide = new JSlider(1, 999, numberOfFiles);
        slide.setMajorTickSpacing(100);
        slide.setValue(numberOfFiles);
        slide.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                numberOfFiles = slide.getValue();
                numberLabel.setText("No. files : " + numberOfFiles);
                fillFileListArea();
            }
        });
        lowerPanel1.add(numberLabel);
        lowerPanel1.add(slide);

        JLabel namingLabel = new JLabel("Naming Pattern :");
        String[] options = {"0001", "yyyy-MMM-dd", "-A"};
        namingPattern = new JComboBox(options);
        namingPattern.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                fillFileListArea();
            }
        });
        lowerPanel1.add(namingLabel);
        lowerPanel1.add(namingPattern);

        JLabel custom = new JLabel("Or create custom name..");
        JButton namingButton = new JButton("Custom pattern");
        namingButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                String numbers[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
                String segments = "" + JOptionPane.showInputDialog(
                        lowerPanel, "How many parts is the name of the file split into?\ne.g. xxx-xxx-xxx = 3", "How many segments?",
                        JOptionPane.DEFAULT_OPTION, null, numbers, numbers[0]
                );

                if (!segments.equals("-1") && segments != null){
                    log("Selected number : " + segments);
                    int parts = Integer.parseInt(segments);
                    NamingPanel np = new NamingPanel(parts);
                }
            }
        });
        lowerPanel1.add(custom);
        lowerPanel1.add(namingButton);

        lowerPanel.add(lowerPanel1);

        JPanel lowerPanel2 = new JPanel(new BorderLayout());
        fileListArea.setRows(6);
        JScrollPane sp = new JScrollPane(fileListArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        lowerPanel2.add(sp);
        lowerPanel.add(lowerPanel2, BorderLayout.CENTER);
        add(lowerPanel);

        setEnabling(lowerPanel, collection);
    }

    public void returnSomething(String thing){
        log("Returned : " + thing);
    }


    @Override
    public void dispose() {
    }

    private boolean isCollection(){
        if(getParameter("collection").equals(true)){
            return true;
        }else{
            return false;
        }
    }

    private int getNumberOfFiles(){
        Object o = getParameter("numberOfFiles");
   //     System.out.println("Returned object from param *numberOfFiles* : " + o.getClass().getCanonicalName() + " : " + o.toString());
        if(o != null){
            int value = (Integer)o;
            if(value > 1 ){
                return value;
            }
            return 1;
        }
        return 1;
    }

    public void setEnabling(Component c,boolean enable) {
        c.setEnabled(enable);
        if (c instanceof Container)
        {
            Component [] arr = ((Container) c).getComponents();
            for (int j=0;j<arr.length;j++) { setEnabling(arr[j],enable); }
        }
    }

    private void fillFileListArea(){
        String selected = (String)namingPattern.getSelectedItem();
        PatternCollection pc = new PatternCollection("-");

        if(selected.equals("0001")){
            pc.add(new CounterPattern(1, 5, 1,1));
        }

        if(selected.equals("yyyy-MMM-dd")){
            pc.add(new DatePattern("yyyy-MMM-dd"));
        }

        if(selected.equals("-A")){
            pc.add(new AlphabetPattern(true, 1));
        }


        fileListArea.setText("Files will be named : \n");
        String name = (String)getParameter("fileName");
        for(int i = 0 ; i< numberOfFiles; i++){
            fileListArea.append(name + "-" + pc.next() + "\n");
        }

    }

    class NamingPanel extends JDialog{
        JLabel hi = new JLabel();
        JTextField name = new JTextField("");
        JComboBox separatorBox;
        String separator;
        int parts = 1;
        String[] nameParts;
        String[] patternOptions = {"0001", "dd-MMM-yy", "A", "ABC"};
        String[] numberArray = {"1", "01", "001", "0001"};
        String[] dateArray = {"dd-mm-yy", "yy-mm-dd", "hh-mm-ss"};
        String[] letterArray = {"A", "AA", "AAA", "AAAA", "AAAAA"};

        public NamingPanel(int p){
            this.parts = p;
            nameParts = new String[parts];
            JPanel mainPanel = new JPanel();

            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File name pattern"));

            JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            hi.setText("Filename will have " + parts + " parts.");
            topPanel.add(hi);
            name.setText(buildName("-", parts));
            topPanel.add(name);
            mainPanel.add(topPanel);

            JPanel midPanel = new JPanel(new GridLayout(1, 2, 5, 5));
            JLabel l1 = new JLabel("Seperator : ");
            String[] seperatorOptions = {"- (hyphen)", " (space)", "_ (underscore)", ". (period)", "(no seperator)"};
            separatorBox = new JComboBox(seperatorOptions);
            separatorBox.setEditable(true);
            separatorBox.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent actionEvent){
                    separator = getSeparator();
                    setNameLabel(buildName(getSeparator(), parts));
                }
            });
            midPanel.add(l1);
            midPanel.add(separatorBox);
            mainPanel.add(midPanel);

            JPanel lowerPanel = new JPanel(new GridLayout(parts, 3, 5, 5));

            for(int i = 0; i < parts; i++){
                final int finalI = i;

                final JComboBox section = new JComboBox(patternOptions);
                section.setEditable(true);

                String[] patternDetail = {""};
                final JComboBox detailChooser = new JComboBox(patternDetail);

                section.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent actionEvent){
                        Object selected = section.getSelectedItem();
                        setSection(finalI, (String)selected);
                        setNameLabel(buildName(getSeparator(), parts));
                        fillDetailCombo(detailChooser, selected);
                    }
                });

                detailChooser.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent actionEvent){
                        setSection(finalI, (String)detailChooser.getSelectedItem());
                        setNameLabel(buildName(getSeparator(), parts));
                    }
                });

                JLabel lx = new JLabel("Pattern " + (i+1) + " : ");
                lowerPanel.add(lx);
                lowerPanel.add(section);
                lowerPanel.add(detailChooser);
            }
            mainPanel.add(lowerPanel);
            JButton ok = new JButton("Ok");
            ok.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    returnSomething("words");
                    PatternCollection pc = new PatternCollection(separator);
                    for(int i = 0; i < parts; i++){
                        if(nameParts[i].equals("")){
                            
                        }
                    }
                    dispose();
                }
            });
            mainPanel.add(ok);

            JButton helpButton = new JButton("Help");
            helpButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    new helpFrame();
                }
            });
            mainPanel.add(helpButton);

            add(mainPanel);
            this.setTitle("File naming pattern");
            this.pack();
            this.setVisible(true);

        }

        public String[] fillDateArray(){
            Date now = new Date();

            String array[] = {""};

            return array;
        }

        private void fillDetailCombo(JComboBox detail, Object patternSelection){
            detail.removeAllItems();
            detail.setEditable(false);

            if(patternSelection == patternOptions[0]){
                addArrayToCombo(detail, numberArray);
            }
            if(patternSelection == patternOptions[1]){
                addArrayToCombo(detail, dateArray);
            }
            if(patternSelection == patternOptions[2]){
                String[] array = {""};
                addArrayToCombo(detail, array);
                detail.setEditable(true);
            }
            if(patternSelection == patternOptions[3]){
                addArrayToCombo(detail, letterArray);
            }
        }

        private void addArrayToCombo(JComboBox box, String[] array){
            for(int i = 0; i < array.length ; i++){
                box.addItem(array[i]);
            }
        }


        private void setNameLabel(String n){
            name.setText(n);
        }


        private void setSection(int i, String s){
            nameParts[i] = s;
            log("Setting namePart " + i + " as : "+ s);
        }

        private String getSeparator(){
            String s = (String)separatorBox.getSelectedItem();
            s = s.substring(0,1);
            if(s.equals("(")){ s = "";}
            return s;
        }

        public String buildName(String s, int parts){
            String name = "";

            for (int i = 0; i < (parts -1); i++){
                String bit = "XXX";
                if(nameParts[i] != null){
                    bit = nameParts[i];
                }

                name += bit + s;
            }
            if(nameParts[parts-1] != null){
                name += nameParts[parts-1];
            }else{
                name += "XXX";
            }

            return name;
        }

        public String getName(){
            return name.getText();
        }
    }

    class helpFrame extends JFrame{
        public helpFrame(){
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


            };
            JTable table = new JTable(data, headings);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);
            panel.add(scrollPane);

            panel.add(helpLabel);
            JButton ok = new JButton("Ok");
            ok.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    dispose();
                }
            });
            panel.add(ok);
            this.add(panel);
            this.setSize(400,600);
            this.setVisible(true);
            this.setTitle("Help");
        }
    }
}
