package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.pegasus.string.CharSequencePattern;
import org.trianacode.pegasus.string.PatternCollection;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
    private boolean one2one = false;

    JLabel collectLabel = new JLabel("");
    JLabel iterLabel = new JLabel("");
    JTextField nameField = new JTextField("");
    JTextField extensionField = new JTextField("");
    JTextArea fileListArea = new JTextArea("Example filenames here..");
    JPanel upperPanel = new JPanel(new GridLayout(3,2,5,5));
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
        getTask().setParameter("one2one", one2one);
        if(namingPattern != null){
            log("Setting param namingPattern : " + namingPattern.toString());
            getTask().setParameter("namingPattern", namingPattern);
            log("Checking namingPattern param : " + getNamingPattern().toString());
        }
    }

    public void getParams(){
        nameField.setText(getTask().getToolName());
        collection = isCollection();
        numberOfFiles = getNumberOfFiles();
        namingPattern = getNamingPattern();
        one2one = isOne2one();
    }

    @Override
    public void reset() {
        //    nameField.setText((String)getParameter("fileName"));
    }

    @Override
    public void init() {
        getParams();

        this.getTask().setParameter(Tool.DEFAULT_INPUT_NODES, 0);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        upperPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File"));
        JLabel nameLabel = new JLabel("File Name :");
        upperPanel.add(nameLabel);

        changeToolName(getTask().getToolName());
        upperPanel.add(nameField);
        JLabel extLabel = new JLabel("File extension : *. ");
        upperPanel.add(extLabel);
        upperPanel.add(extensionField);

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

        mainPanel.add(upperPanel);


        //Lower Panel

        lowerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File Collection Options"));
        JPanel lowerPanel1 = new JPanel(new GridLayout(3, 1, 5, 5));

        JPanel lowerPanel1a = new JPanel(new GridLayout(1, 2, 5, 5));
        final JPanel lowerPanel1b = new JPanel(new GridLayout(1, 2, 5, 5));

        final JLabel one2oneLabel = new JLabel("One2one with previous jobs : ");
        final JCheckBox one2oneBox = new JCheckBox("one2one", one2one);
        one2oneBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (one2oneBox.isSelected()) {
                    one2one = true;
                    setEnabling(lowerPanel1b, false);
                } else {
                    one2one = false;
                    setEnabling(lowerPanel1b, true);
                }
            }
        });
        lowerPanel1a.add(one2oneLabel);
        lowerPanel1a.add(one2oneBox);

        final JLabel numberLabel = new JLabel("No. files : " + numberOfFiles);
        final String[] numbers = new String[100];
        for(int i = 1; i < 100; i++){
            numbers[i] = "" + i;
        }
        final JComboBox numbersCombo = new JComboBox(numbers);
        numbersCombo.setSelectedItem("" + numberOfFiles);
        numbersCombo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                numberOfFiles = Integer.parseInt((String)numbersCombo.getSelectedItem());
                numberLabel.setText("No. files : " + numberOfFiles);
                fillFileListArea();
            }
        });
        lowerPanel1b.add(numberLabel);        
        lowerPanel1b.add(numbersCombo);
        lowerPanel1.add(lowerPanel1a);
        lowerPanel1.add(lowerPanel1b);

        final JPanel lowerPanel1c = new JPanel(new GridLayout(1, 2, 5, 5));
        JLabel custom = new JLabel("Or create custom name :");
        JButton namingButton = new JButton("Custom pattern...");
        namingButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                int parts = 3;
                if(namingPattern != null){
                    parts = namingPattern.getPatternCollectionSize();
                }
                namingPattern = (PatternCollection)NamingPanel.getValue(parts);
                fillFileListArea();
                if(namingPattern != null){
                    log("ChosenNamingPattern : " + namingPattern.toString());

                }
            }
        });
        lowerPanel1c.add(custom);
        lowerPanel1c.add(namingButton);
        lowerPanel1.add(lowerPanel1c);

        JPanel lowerPanel2 = new JPanel();
        lowerPanel2.setLayout(new BoxLayout(lowerPanel2, BoxLayout.Y_AXIS));
        fileListArea.setRows(6);
        fileListArea.setEditable(false);
        JScrollPane sp = new JScrollPane(fileListArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        lowerPanel2.add(sp);

        lowerPanel2.add(iterLabel);

        lowerPanel.add(lowerPanel1);
        lowerPanel.add(lowerPanel2);
        mainPanel.add(lowerPanel);
        setEnabling(lowerPanel, collection);
        this.add(mainPanel);
    }

    @Override
    public void dispose() {
    }

    private PatternCollection getNamingPattern(){
        Object o = getParameter("namingPattern");
        //     System.out.println("Returned object from param *numberOfFiles* : " + o.getClass().getCanonicalName() + " : " + o.toString());
        if(o instanceof PatternCollection){
            log("Found : " + o.toString());
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

    private boolean isOne2one(){
        if(getParameter("one2one").equals(true)){
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
        String ext = extensionField.getText();
        if(namingPattern != null){
            iterLabel.setText("Filename " + (namingPattern.varies() ? "iterates well" : "does not vary, will +\"01\""));
            nameField.setText(namingPattern.next() + ext);
            namingPattern.resetCount();
            fileListArea.setText("Files will be named : \n");
            for(int i = 0 ; i< numberOfFiles; i++){
                log("adding a filename to fileListArea");
                fileListArea.append(namingPattern.next() + ext +"\n");
            }
        }
        else{
            fileListArea.setText("Files will be named : \n");
            String name = (String)getParameter("fileName");
            for(int i = 0 ; i< numberOfFiles; i++){
                log("adding a name to fileListArea");
                fileListArea.append(name + "." + ext +"\n");
            }
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
    Vector<JComboBox> patternDetailVector = new Vector<JComboBox>();
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
        setNumParts.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        JPanel midPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        midPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        final JPanel lowerPanel = new JPanel();
        lowerPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        JLabel exampleLabel = new JLabel("XXX-XXX-XXX");
        mainPanel.add(exampleLabel);

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
                okPressed();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
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

    private void okPressed(){
        boolean completeName = true;
        for(JComboBox jcb : patternDetailVector){
            if(jcb.getSelectedItem().equals("")){
                completeName = false;
            }
        }
        if(completeName){
            try{
                PatternCollection pc = new PatternCollection(getSeparator());
                for(int i = 0; i < parts; i++){
                    pc.add(new CharSequencePattern(nameParts.get(i)));
                }
                for (int i = 0; i < 5; i++) {
                    System.out.println(pc.next());
                }

                log((pc.varies()) ? "Name iterates ok." :  "Name does not iterate");

                setNamingPattern(pc);
            }catch(Exception ex){}
            finally{
                dispose();
            }
        }
        else{
            log("name not complete");
            JOptionPane.showMessageDialog(mainPanel,
                    "Name not complete.\nFill or remove empty part(s).",
                    "Name Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addChoosers(JPanel lowerPanel){
        lowerPanel.removeAll();
        if(patternDetailVector.size() > 0){
            patternDetailVector.removeAllElements();
        }
        lowerPanel.setLayout(new GridLayout(parts, 3, 5, 5));

        for(int i = 0; i < parts; i++){
            log("adding a line of choosers");
            final int finalI = i;

            final JComboBox section = new JComboBox(patternOptions);
            section.setEditable(true);

            String[] patternDetail = {""};
            final JComboBox detailChooser = new JComboBox(patternDetail);
            patternDetailVector.add(detailChooser);

            section.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent actionEvent){
                    Object selected = section.getSelectedItem();
                    setSection(finalI, (String)selected);
                    setNameLabel(buildName(getSeparator()));
                    fillDetailCombo(detailChooser, selected);
                }
            });

            fillDetailCombo(detailChooser, section.getSelectedItem());

            JLabel lx = new JLabel("Pattern " + (i+1) + " : ");

            lowerPanel.add(lx);
            lowerPanel.add(section);
            lowerPanel.add(detailChooser);

            JButton setButton = new JButton("Set");
            setButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    setSection(finalI, (String)detailChooser.getSelectedItem());
                    setNameLabel(buildName(getSeparator()));
                }
            });
            lowerPanel.add(setButton);
        }
        //   lowerPanel.revalidate();
    }

    private void setNamingPattern(PatternCollection pc){
        log("chosenNamingPattern : " + pc.toString());
        chosenNamingPattern = pc;
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
            log("adding array to combobox");
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

