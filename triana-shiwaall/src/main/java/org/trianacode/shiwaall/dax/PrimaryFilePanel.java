package org.trianacode.shiwaall.dax;

import org.trianacode.enactment.logging.Loggers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Jan 17, 2011
 * Time: 9:30:10 PM
 * To change this template use File | Settings | File Templates.
 */

class PrimaryFilePanel extends JDialog implements ActionListener {

    /** The main panel. */
    JPanel mainPanel = new JPanel();
    
    /** The bg. */
    ButtonGroup bg = new ButtonGroup();

    /** The chunks. */
    List<DaxFileChunk> chunks = new ArrayList();
    
    /** The job name. */
    String jobName = "";
    
    /** The return chunk. */
    DaxFileChunk returnChunk = null;

    /**
     * Gets the value.
     *
     * @param jobName the job name
     * @param list the list
     * @return the value
     */
    public static Object getValue(String jobName, List list) {
        PrimaryFilePanel pfp = new PrimaryFilePanel(jobName, list);
        return pfp.getReturnValue();
    }

    /**
     * Gets the return value.
     *
     * @return the return value
     */
    private Object getReturnValue() {
        return returnChunk;
    }

    /**
     * Instantiates a new primary file panel.
     *
     * @param jobName the job name
     * @param l the l
     */
    public PrimaryFilePanel(String jobName, List l) {
        this.chunks = l;
        this.jobName = jobName;
        this.setSize(400, 400);
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

        JPanel radioPanel = new JPanel(new GridLayout(2, 0));
        returnChunk = chunks.get(0);
        for (int i = 0; i < chunks.size(); i++) {
            DaxFileChunk fc = chunks.get(i);
            JRadioButton radio = new JRadioButton(fc.getFilename() +
                    " (" + fc.getNumberOfFiles() + " job" + ((fc.getNumberOfFiles() > 1) ? "s" : "") + " will be created)");
            radio.setActionCommand("" + i);
            radio.addActionListener(this);
            radioPanel.add(radio);
            bg.add(radio);
        }
        mainPanel.add(radioPanel);

        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
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

    /**
     * Ok pressed.
     */
    private void okPressed() {
        if (returnChunk == null) {
            returnChunk = chunks.get(0);
        }
        dispose();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int i = Integer.parseInt(e.getActionCommand());
        returnChunk = chunks.get(i);
        Loggers.DEV_LOGGER.debug("Setting " + returnChunk.getFilename() + " as primary file");
    }
}

