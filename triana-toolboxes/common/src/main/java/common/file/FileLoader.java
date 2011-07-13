package common.file;

import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Jan 24, 2011
 * Time: 8:59:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class FileLoader implements TaskConscious {

    //   @TextFieldParameter
    private String filePath = "";
    private Task task;

    @Process(gather = true)
    public File process(List list) {
        if (list.size() > 0) {
            if (list.get(0) instanceof String) {
                filePath = (String) list.get(0);
            }
        }

        if (!filePath.equals("")) {
            File file = new File(filePath);
            if (file.exists()) {
                return file;
            }

        }
        return null;
    }

    @CustomGUIComponent
    public Component getGUI() {
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        JPanel locationPanel = new JPanel(new BorderLayout());
        JLabel locationLabel = new JLabel("File Path : ");
        final JTextField locationField = new JTextField(filePath);
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
                        filePath = f.getAbsolutePath();
                        locationField.setText(filePath);
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

    @Override
    public void setTask(Task task) {
        this.task = task;
    }
}

