package common.file;

import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Parameter;
import org.trianacode.annotation.Tool;
import org.trianacode.gui.hci.GUIEnv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 24/02/2012
 * Time: 22:04
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class StringToFile {

    @Parameter
    private String filename = "";
    private String content = "";

    @org.trianacode.annotation.Process(gather = true)
    public File process(List list) {
        if (list.size() > 0) {
            if (list.get(0) instanceof String) {
                content = (String) list.get(0);
            }
        }

        if (!filename.equals("")) {
            File file = new File(filename);
            System.out.println(file.getAbsolutePath());

            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(content);
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return file;

        }
        return null;
    }

    @CustomGUIComponent
    public Component getGUI() {
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        JPanel locationPanel = new JPanel(new BorderLayout());
        JLabel locationLabel = new JLabel("File Path : ");
        final JTextField locationField = new JTextField(20);
        locationField.setText(filename);
        JButton locationButton = new JButton("...");
        locationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal = chooser.showDialog(GUIEnv.getApplicationFrame(), "File");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    if (f != null) {
                        filename = f.getAbsolutePath();
                        locationField.setText(filename);
                    }
                }
            }
        });
        locationPanel.add(locationLabel, BorderLayout.WEST);
        locationPanel.add(locationField, BorderLayout.CENTER);
        locationPanel.add(locationButton, BorderLayout.EAST);

        mainPane.add(locationPanel);
        return mainPane;
    }

}
