package common.file;

import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;
import org.trianacode.gui.hci.GUIEnv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Jan 24, 2011
 * Time: 8:59:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Tool
public class FileReader {

    //   @TextFieldParameter
    private String filePath = "";

    @Process(gather = true)
    public byte[] process(List list) {
        if (list.size() > 0) {
            if (list.get(0) instanceof String) {
                filePath = (String) list.get(0);
            }
        }

        if (!filePath.equals("")) {
            File file = new File(filePath);
            if (file.exists()) {
                try {
                    FileInputStream in = new FileInputStream(file);
                    byte[] buff = new byte[16384];
                    int c;
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    while ((c = in.read(buff)) != -1) {
                        bout.write(buff, 0, c);
                    }
                    return bout.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new byte[0];
            }

        }
        return new byte[0];
    }

    @CustomGUIComponent
    public Component getGUI() {
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
}

