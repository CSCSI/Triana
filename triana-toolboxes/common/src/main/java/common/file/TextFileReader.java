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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Jan 24, 2011
 * Time: 8:59:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class TextFileReader implements TaskConscious {

    private String filePath = ""; ///Users/ian/Work/testBundles/DART/DART Commands 1.txt";
    private Task task;

    @Process(gather = true)
    public String process(java.util.List list) {
        if (list.size() > 0) {
            Object object = list.get(0);
            if (object instanceof File) {
                File file = (File) object;
                if (file.exists() && file.length() < -1) {
                    filePath = file.getAbsolutePath();
                }
            }
            if (object instanceof String) {
                filePath = (String) object;
            }
        }

        if (!filePath.equals("")) {
            File file = new File(filePath);
            if (file.exists()) {
                try {
                    BufferedReader reader = new BufferedReader(new java.io.FileReader(file));
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    return sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            }

        }
        return "";
    }

    @CustomGUIComponent
    public Component getGUI() {
        loadParams();

        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        JPanel locationPanel = new JPanel(new BorderLayout());
        JLabel locationLabel = new JLabel("File Path : ");
        final JTextField locationField = new JTextField(20);
        locationField.setText(filePath);
        JButton locationButton = new JButton("...");
        locationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                File file = new File(filePath);
                if (file.exists()) {
                    chooser.setCurrentDirectory(file.getParentFile());
                }
                int returnVal = chooser.showDialog(GUIEnv.getApplicationFrame(), "File");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    if (f != null) {
                        filePath = f.getAbsolutePath();
                        locationField.setText(filePath);
                        task.setParameter("filePath", filePath);
                    }
                }
            }
        });
        locationPanel.add(locationLabel, BorderLayout.WEST);
        locationPanel.add(locationField, BorderLayout.CENTER);
        locationPanel.add(locationButton, BorderLayout.EAST);

        mainPane.add(locationPanel);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                locationField.setText("");
                task.setParameter("filePath", "");
            }
        });
        mainPane.add(clearButton);
        return mainPane;
    }

    private void loadParams() {
        String fileString = (String) task.getParameter("filePath");
        if (fileString != null && new File(fileString).exists()) {
            filePath = fileString;
        }
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }
}

