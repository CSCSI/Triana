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
    JPanel lowerPanel = new JPanel(new GridLayout(2,2,5,5));
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
        JPanel lowerPanel1 = new JPanel(new GridLayout(2, 2, 5, 5));
        final JLabel numberLabel = new JLabel("No. files : 1");

        final JSlider slide = new JSlider(1, 999, 1);
        slide.setMajorTickSpacing(100);
        slide.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                numberOfFiles = slide.getValue();
                numberLabel.setText("No. files : " + numberOfFiles);
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
}
