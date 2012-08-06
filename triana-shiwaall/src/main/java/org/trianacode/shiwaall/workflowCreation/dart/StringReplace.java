package org.trianacode.shiwaall.workflowCreation.dart;

import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 30/07/2012
 * Time: 12:37
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class StringReplace {

    JTextField startField = new JTextField("");
    JTextField replaceField = new JTextField("");

    @Process
    public String process(Collection strings){

        String initialString = startField.getText();
        String[] replaces = replaceField.getText().split(":");

        for(String replace : replaces){
            String[] thisWithThat = replace.split("/");

            initialString.replace(thisWithThat[0], thisWithThat[1]);
        }

        return initialString;
    }

    @CustomGUIComponent
    public Component getGui(){
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(2,2));

        JLabel initialLabel = new JLabel("Initial String:");
        jPanel.add(initialLabel);
        jPanel.add(startField);

        JLabel replaceLabel = new JLabel("Replace x/y:");
        jPanel.add(replaceLabel);
        jPanel.add(replaceField);
        return jPanel;

    }

}
