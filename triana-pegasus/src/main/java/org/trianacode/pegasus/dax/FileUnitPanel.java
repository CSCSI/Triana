package org.trianacode.pegasus.dax;

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

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Sep 7, 2010
 * Time: 1:22:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileUnitPanel extends ParameterPanel {

    private int numberOfFiles = 1;

    JLabel collect = new JLabel("Not a collection");
    JTextField nameField = new JTextField("");
    JTextArea fileListArea = new JTextArea("Example filenames here..");
    JPanel upperPanel = new JPanel(new GridLayout(2,2));
    JPanel lowerPanel = new JPanel(new GridLayout(2,3,5,5));
    JComboBox namingPattern = new JComboBox();


    public boolean isAutoCommitByDefault(){return true;}

    public void parameterUpdate(String param, Object value){

    }

    public void applyClicked(){ apply(); }
    public void okClicked(){ apply(); }
    //  public void cancelClicked(){ reset(); }

    private void apply(){
        setParameter("fileName", nameField.getText());
        getTask().setToolName(nameField.getText());
        fillFileListArea();
    }

    @Override
    public void reset() {
        nameField.setText((String)getParameter("fileName"));
    }

    @Override
    public void init() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        upperPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File"));
        JLabel nameLabel = new JLabel("File Name :");
        upperPanel.add(nameLabel);

        nameField.setText((String) getParameter("fileName"));
        nameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //             setParameter("fileName", nameField.getText());
                apply();
            }
        });
        upperPanel.add(nameField);

        final JCheckBox collection = new JCheckBox("Collection", isCollection());
        collection.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (collection.isSelected()) {
                    collect.setText("Collection of files.");
                    setParameter("collection", true);
                    setEnabling(lowerPanel, true);
                } else {
                    collect.setText("Not a collection");
                    setParameter("collection", false);
                    setEnabling(lowerPanel, false);
                }
            }
        });
        upperPanel.add(collection);
        upperPanel.add(collect);

        add(upperPanel);

        //Lower Panel

        lowerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File Collection Options"));
        JPanel lowerPanel1 = new JPanel(new GridLayout(3, 2, 5, 5));
        final JLabel numberLabel = new JLabel("No. files : 1");

        final JSlider slide = new JSlider(1, 999, 1);
        slide.setMajorTickSpacing(100);
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
                    System.out.println("Selected number : " + segments);
                    int parts = Integer.parseInt(segments);
                    NamingPanel np = new NamingPanel(parts);
                    int answer = JOptionPane.showOptionDialog(
                            lowerPanel, np, "Create naming strategy", JOptionPane.DEFAULT_OPTION,
                            0, null, null, null
                    );
                    if (answer == JOptionPane.OK_OPTION)
                    {
                        System.out.println("Selected ok from naming dialog, typed : " + np.getName());
                        // do stuff with the panel
                    }
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

        setEnabling(lowerPanel, isCollection());
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private boolean isCollection(){
        return (Boolean)getParameter("collection");
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

    class NamingPanel extends JPanel{
        JLabel hi = new JLabel();
        JTextField name = new JTextField("");
        JComboBox separator;
        int parts = 1;
        String[] nameParts;

        public NamingPanel(int p){
            this.parts = p;
            nameParts = new String[parts];

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            JPanel lowerPanel = new JPanel(new GridLayout(parts + 1, 2, 5, 5));
            hi.setText("File will have " + parts + " parts.");
            topPanel.add(hi);
            name.setText(buildName("-", parts));
            topPanel.add(name);
            add(topPanel);

            JLabel l1 = new JLabel("Seperator : ");
            String[] seperatorOptions = {"- (hyphen)", " (space)", "_ (underscore)", ". (period)", "(no seperator)"};
            separator = new JComboBox(seperatorOptions);
            separator.setEditable(true);
            separator.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent actionEvent){
                    setNameLabel(buildName(getSeparator(), parts));
                }
            });
            lowerPanel.add(l1);
            lowerPanel.add(separator);

            String[] patternOptions = {"0001", "dd-MMM-yy", "A", "ABC"};
            for(int i = 0; i < parts; i++){
                final JComboBox section = new JComboBox(patternOptions);
                section.setEditable(true);
                final int finalI = i;
                section.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent actionEvent){
                        setSection(finalI, (String)section.getSelectedItem());
                        setNameLabel(buildName(getSeparator(), parts));
                    }
                });
                JLabel lx = new JLabel("Pattern " + (i+1) + " : ");
                lowerPanel.add(lx);
                lowerPanel.add(section);
            }
            add(lowerPanel);

        }
        private void setNameLabel(String n){
            name.setText(n);
        }


        private void setSection(int i, String s){
            nameParts[i] = s;
            System.out.println("Setting namePart " + i + " as : "+ s);
        }

        private String getSeparator(){
            String s = (String)separator.getSelectedItem();
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
}
