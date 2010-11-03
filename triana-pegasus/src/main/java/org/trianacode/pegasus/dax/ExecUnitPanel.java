package org.trianacode.pegasus.dax;

import org.trianacode.gui.panels.ParameterPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 3, 2010
 * Time: 4:01:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExecUnitPanel extends ParameterPanel {

    JTextField execField = new JTextField();
    JTextField execArgsField = new JTextField();
    JTextField searchField = new JTextField();

    @Override
    public void init() {
        getParams();

        JPanel mainPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        JLabel execLabel = new JLabel("Executable : ");
        JLabel execArgsLabel = new JLabel("Executable arguments : ");
        JLabel searchLabel = new JLabel("Search string : ");

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                new helpFrame();
            }
        });

        mainPanel.add(execLabel);
        mainPanel.add(execField);
        mainPanel.add(execArgsLabel);
        mainPanel.add(execArgsField);
        mainPanel.add(searchLabel);
        mainPanel.add(searchField);
        mainPanel.add(helpButton);
        add(mainPanel);

    }

    public void applyClicked(){ apply(); }
    public void okClicked(){ apply(); }

    public void apply(){
        getTask().setParameter("executable", execField.getText());
        getTask().setParameter("executable_args", execArgsField.getText());
        getTask().setParameter("search_for", searchField.getText());
    }

    private void getParams(){
        execField.setText((String)getTask().getParameter("executable"));
        execArgsField.setText((String)getTask().getParameter("executable_args"));
        searchField.setText((String)getTask().getParameter("search_for"));

    }

    @Override
    public void reset() {
    }

    @Override
    public void dispose() {

    }

    class helpFrame extends JFrame{
        public helpFrame(){
            this.setTitle("Help");
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel helpLabel = new JLabel("This is helpful");
            JTextArea helpArea = new JTextArea("Executable refers to the program which will be run, eg python, ls " +
                    "\n\nExecutable arguments are the parameters given to that program, eg example.py, -l" +
                    "\n\nSearch text is the string Triana will look for in the executables output. " +
                    "The integer directly after the search string will be sent to the next unit in the workflow. " +
                    "If this unit is a collection FileUnit, the number will be used as the number of files in the collection.");
            helpArea.setEditable(false);
            helpArea.setLineWrap(true);
            helpArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(helpArea);

            panel.add(helpLabel);
            panel.add(scrollPane);
            JButton ok = new JButton("Ok");
            ok.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    dispose();
                }
            });
            panel.add(ok);
            this.add(panel);
            this.setSize(400,200);
            this.setVisible(true);
        }
    }
}
