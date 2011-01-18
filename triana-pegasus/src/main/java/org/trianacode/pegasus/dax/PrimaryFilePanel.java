package org.trianacode.pegasus.dax;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Jan 17, 2011
 * Time: 9:30:10 PM
 * To change this template use File | Settings | File Templates.
 */

class PrimaryFilePanel extends JDialog implements ActionListener {

    JPanel mainPanel = new JPanel();
    ButtonGroup bg = new ButtonGroup();

    List<DaxFileChunk> chunks = new ArrayList();
    String jobName = "";
    DaxFileChunk returnChunk = null;

    public static Object getValue(String jobName, List list){
        PrimaryFilePanel pfp = new PrimaryFilePanel(jobName, list);
        return pfp.getReturnValue();
    }

    private Object getReturnValue(){
        return returnChunk;
    }

    public PrimaryFilePanel(String jobName, List l){
        this.chunks = l;
        this.jobName = jobName;
        this.setSize(400,400);
        this.setModal(true);
        this.setLocationRelativeTo(this.getOwner());

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Job duplication selection"));

        JTextArea text = new JTextArea("Job " + jobName + " is set to duplicate in a one-2-one pattern.\n\n" +
                "It currently has more than one input file.\n\n" +
                "Please select the input file this jobs duplication should match the number of.\n\n" +
                "Select primary file : ");
        text.setWrapStyleWord(true);
        mainPanel.add(text);

        JPanel radioPanel = new JPanel(new GridLayout(2,0));
        returnChunk = chunks.get(0);
        for(int i = 0; i < chunks.size(); i++){
            DaxFileChunk fc = chunks.get(i);
            JRadioButton radio = new JRadioButton(fc.getFilename() +
                    " (" + fc.getNumberOfFiles() + " job" + ((fc.getNumberOfFiles() > 1) ? "s" : "" ) + " will be created)");
            radio.setActionCommand("" + i);
            radio.addActionListener(this);
            radioPanel.add(radio);
            bg.add(radio);
        }
        mainPanel.add(radioPanel);

        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                okPressed();
            }
        });
        mainPanel.add(ok);

        this.add(mainPanel);
        this.setTitle("Select primary file");
        this.pack();
        this.setVisible(true);

    }

    private void okPressed(){
        if(returnChunk == null){
            returnChunk = chunks.get(0);
        }
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int i = Integer.parseInt(e.getActionCommand());
        returnChunk = chunks.get(i);
        System.out.println("Setting " + returnChunk.getFilename() + " as primary file");
    }
}

