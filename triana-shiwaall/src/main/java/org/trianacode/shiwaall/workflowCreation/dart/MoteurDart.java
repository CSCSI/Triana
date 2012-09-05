package org.trianacode.shiwaall.workflowCreation.dart;

import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;

// TODO: Auto-generated Javadoc
/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 30/07/2012
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class MoteurDart {

    /** The url field. */
    JTextField urlField = new JTextField("");
    
    /** The number of lines. */
    JTextField numberOfLines = new JTextField("");

    /**
     * Process.
     *
     * @param strings the strings
     * @return the file
     */
    @Process
    public File process(String[] strings){

        try {
            File file = File.createTempFile("moteurInput", ".tmp");

            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(urlField.getText() + "\n");

            if(numberOfLines.getText() != null && !numberOfLines.getText().equals("")){
                for(int i = 0 ; i < Integer.parseInt(numberOfLines.getText()); i++){
                    if(i < strings.length){
                        fileWriter.write(strings[i] + "\n");
                    }
                }
            } else {
                for(String string : strings){
                    fileWriter.write(string);
                }
            }
            fileWriter.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the gui.
     *
     * @return the gui
     */
    @CustomGUIComponent
    public Component getGui(){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(2,2));

        JLabel numberOfLinesLabel = new JLabel("Number of lines:");
        jPanel.add(numberOfLinesLabel);
        jPanel.add(numberOfLines);

        JLabel urlToAdd = new JLabel("Url to add:");
        jPanel.add(urlToAdd);
        jPanel.add(urlField);
        return jPanel;

    }

}
