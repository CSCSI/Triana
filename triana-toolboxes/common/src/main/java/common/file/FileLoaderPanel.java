package common.file;

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.ParameterPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 25, 2010
 * Time: 12:42:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileLoaderPanel extends ParameterPanel {

    String filePath = "";

    @Override
    public void init() {

        JPanel locationPanel = new JPanel(new BorderLayout());
        JLabel locationLabel = new JLabel("File Path : ");
        final JTextField locationField = new JTextField(getPath());
        JButton locationButton = new JButton("...");
        locationButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal = chooser.showDialog(GUIEnv.getApplicationFrame(), "File");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    if (f != null) {
                        filePath = f.getAbsolutePath();
                        locationField.setText(filePath);
                    }
                }
            }

        });
        locationPanel.add(locationLabel, BorderLayout.WEST);
        locationPanel.add(locationField, BorderLayout.CENTER);
        locationPanel.add(locationButton, BorderLayout.EAST);

        add(locationPanel);
    }


    public void okClicked(){
        update();
    }
    public void applyClicked(){
        update();
    }

    private void update(){
        getTask().setParameter("filePath", filePath);
    }

    private String getPath(){
        String pathParameter = "";
        Object o = getParameter("filePath");
        if(o instanceof String){
            pathParameter = (String)o;
        }
        return "";
    }
    @Override
    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
