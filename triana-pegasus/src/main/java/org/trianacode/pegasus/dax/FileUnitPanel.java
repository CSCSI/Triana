package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.pegasus.string.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

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
    JComboBox namingPatternBox = new JComboBox();
    private PatternCollection namingPattern = null;



    //   public boolean isAutoCommitByDefault(){return true;}

    public void applyClicked(){ apply(); }
    public void okClicked(){ apply(); }
    //  public void cancelClicked(){ reset(); }

    private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
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
        log("Setting param namingPattern : " + namingPattern.toString());
        getTask().setParameter("namingPattern", namingPattern);
        log("Checking namingPattern param : " + getNamingPattern().toString());
    }

    public void getParams(){
        collection = isCollection();
        numberOfFiles = getNumberOfFiles();
        namingPattern = getNamingPattern();
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
        namingPatternBox = new JComboBox(options);
        namingPatternBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                fillFileListArea();
            }
        });
        lowerPanel1.add(namingLabel);
        lowerPanel1.add(namingPatternBox);

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
                    namingPattern = (PatternCollection)NamingPanel.getValue(parts);

                    log("ChosenNamingPattern : " + namingPattern.toString());
                    log("Does this appear too soon?");
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

    @Override
    public void dispose() {
    }

    private PatternCollection getNamingPattern(){
        Object o = getParameter("namingPattern");
        //     System.out.println("Returned object from param *numberOfFiles* : " + o.getClass().getCanonicalName() + " : " + o.toString());
        if(o instanceof PatternCollection){
            return (PatternCollection)o;
        }
        return null;
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
        String selected = (String)namingPatternBox.getSelectedItem();
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
}

class NamingPanel extends JDialog{
    JPanel mainPanel = new JPanel();
    JLabel hi = new JLabel();
    JTextField name = new JTextField("");
    JComboBox separatorBox;
    String separator;
    int parts = 1;
    Vector<String> nameParts;
    String[] patternOptions = {"words", "numbers", "dates", "letters"};
    String[] numberArray = {"1", "01", "001", "0001"};
    String[] dateArray = {"", "dd-mm-yy", "yy-mm-dd", "hh-mm-ss"};
    String[] letterArray = {"A", "AA", "AAA", "AAAA", "AAAAA"};
    PatternCollection chosenNamingPattern = null;

    public static Object getValue(int p){
        NamingPanel np = new NamingPanel(p);
        return np.getReturnValue();
    }

    private Object getReturnValue(){
        return chosenNamingPattern;
    }

    private void prepNameParts(){
        for(int i = 0; i < parts; i++){
            nameParts.add("");
        }
    }

    private void refresh(){
        mainPanel.revalidate();
        this.pack();
    }

    public NamingPanel(int p){
        this.setModal(true);
        this.setLocationRelativeTo(this.getOwner());
        this.parts = p;
        nameParts = new Vector<String>();
        prepNameParts();

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File name pattern"));

        JPanel setNumParts = new JPanel(new GridLayout(1, 3, 5, 5));
        setNumParts.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JPanel midPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        midPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        final JPanel lowerPanel = new JPanel();
        lowerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));


        JLabel label = new JLabel("Change number of parts");
        setNumParts.add(label);
        String numbers[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        final JComboBox numsBox = new JComboBox(numbers);
        numsBox.setSelectedItem((String)"" + p);
        setNumParts.add(numsBox);

        JButton setButton = new JButton("Set");
        setButton.addActionListener(new ActionListener(){
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
        separatorBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent){
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
        ok.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    PatternCollection pc = new PatternCollection(getSeparator());
                    for(int i = 0; i < parts; i++){
                        pc.add(new CharSequencePattern(nameParts.get(i)));
                    }
                    for (int i = 0; i < 5; i++) {
                        System.out.println(pc.next());
                    }

                    log(checkNameVaries(pc) ? "Name iterates ok." :  "Name does not iterate");

                    setNamingPattern(pc);
                }catch(Exception ex){}
                finally{
                    dispose();
                }
            }
        });
        mainPanel.add(ok);

        add(mainPanel);
        this.setTitle("File naming pattern");
        this.pack();
        this.setVisible(true);

    }

    private void addChoosers(JPanel lowerPanel){
        lowerPanel.removeAll();
        lowerPanel.setLayout(new GridLayout(parts, 4, 5, 5));

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
                    setNameLabel(buildName(getSeparator()));
                    fillDetailCombo(detailChooser, selected);
                }
            });

            detailChooser.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent actionEvent){
                    setSection(finalI, (String)detailChooser.getSelectedItem());
                    setNameLabel(buildName(getSeparator()));
                }
            });

            fillDetailCombo(detailChooser, section.getSelectedItem());

            JLabel lx = new JLabel("Pattern " + (i+1) + " : ");


            JButton helpButton = new JButton("Help");
            helpButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    new helpFrame();
                }
            });

            lowerPanel.add(lx);
            lowerPanel.add(section);
            lowerPanel.add(detailChooser);
            lowerPanel.add(helpButton);
        }
     //   lowerPanel.revalidate();
    }

    private void setNamingPattern(PatternCollection pc){
        log("chosenNamingPattern : " + pc.toString());
        chosenNamingPattern = pc;
    }

    private boolean checkNameVaries(PatternCollection pc){
        boolean varies = false;
        List l = pc.getStringPatternList();
        for(Iterator i = l.iterator(); i.hasNext();){
            Object o = i.next();
            if(!(o instanceof CharSequencePattern)){
                varies = true;
            }
        }
        return varies;
    }

    private void fillDetailCombo(JComboBox detail, Object patternSelection){
        detail.removeAllItems();
        detail.setEditable(false);

        if(patternSelection.equals("numbers")){
            addArrayToCombo(detail, numberArray);
        }
        if(patternSelection.equals("dates")){
            addArrayToCombo(detail, dateArray);
            detail.setEditable(true);
        }
        if(patternSelection.equals("words")){
            String[] array = {""};
            addArrayToCombo(detail, array);
            detail.setEditable(true);
        }
        if(patternSelection.equals("letters")){
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
        if(i >= nameParts.size()){
            log("trying to set " + i + " nameParts is : " + nameParts.size());
            for(int j = nameParts.size(); j < (i+1); j++){
                nameParts.add("");
            }
            log("nameParts is now : " + nameParts.size());
        }
        log("Setting namePart " + i + " as : "+ s);        
        nameParts.setElementAt(s,i);
    }

    private String getSeparator(){
        String s = (String)separatorBox.getSelectedItem();
        s = s.substring(0,1);
        if(s.equals("(")){ s = "";}
        return s;
    }

    public String buildName(String sep){
        String name = "";
        int size = nameParts.size();
        if(size > 1){
            for (int i = 0; i < (size - 1); i++){
                String bit = "XXX";
                if(nameParts.get(i) != null){
                    bit = nameParts.get(i);
                }
                name += bit + sep;
            }
        }
        if(nameParts.get(size - 1) != null){
            name += nameParts.get(size - 1);
        }else{
            name += "XXX";
        }

        return name;
    }

    public String getName(){
        return name.getText();
    }

    private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
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
        ok.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });
        panel.add(ok);
        this.add(panel);
        this.setSize(600,400);
        this.setVisible(true);
        this.setTitle("Help");
    }
}

