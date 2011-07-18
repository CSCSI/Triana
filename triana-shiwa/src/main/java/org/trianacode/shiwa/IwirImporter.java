//package org.trianacode.shiwa;
//
//import org.trianacode.annotation.CustomGUIComponent;
//import org.trianacode.annotation.Process;
//import org.trianacode.annotation.Tool;
//import org.trianacode.gui.action.files.TaskGraphFileHandler;
//import org.trianacode.gui.hci.GUIEnv;
//import org.trianacode.shiwa.xslt.xsltTransformer;
//import org.trianacode.taskgraph.TaskGraph;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;
//import java.util.List;
//
//
///**
// * Created by IntelliJ IDEA.
// * User: ian
// * Date: 09/03/2011
// * Time: 14:33
// * To change this template use File | Settings | File Templates.
// */
//@Tool
//public class IwirImporter {
//
//    private String root = "triana-shiwa/src/main/java/org/trianacode/shiwa/xslt/";
//    private String filePath = root + "iwir/output.xml";
//
//    @Process(gather = true)
//    public Object process(List in) {
//        for (Object object : in) {
//            System.out.println("Incoming object");
//            if (object instanceof File) {
//                xsltTransformer.doTransform(root + "iwir/iwir.xml", root + "iwir/outputTemp.xml", root + "iwir/removeNamespace.xsl");
//                xsltTransformer.doTransform(root + "iwir/outputTemp.xml", filePath, root + "iwir/iwir.xsl");
//
//                TaskGraph t = TaskGraphFileHandler.openTaskgraph(new File(filePath), true);
//                TaskGraph tg = GUIEnv.getApplicationFrame().addParentTaskGraphPanel(t);
//
//            }
//        }
//        return null;
//    }
//
//    @CustomGUIComponent
//    public Component getGUI() {
//        JPanel mainPane = new JPanel();
//        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
//
//        JPanel locationPanel = new JPanel(new BorderLayout());
//        JLabel locationLabel = new JLabel("File Path : ");
//        final JTextField locationField = new JTextField(filePath);
//        JButton locationButton = new JButton("...");
//        locationButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                JFileChooser chooser = new JFileChooser();
//                chooser.setMultiSelectionEnabled(false);
//                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//                int returnVal = chooser.showSaveDialog(GUIEnv.getApplicationFrame());
//                if (returnVal == JFileChooser.APPROVE_OPTION) {
//                    File f = chooser.getSelectedFile();
//                    if (f != null) {
//                        filePath = f.getAbsolutePath();
//                        locationField.setText(filePath);
//                    }
//                }
//            }
//        });
//        locationPanel.add(locationLabel, BorderLayout.WEST);
//        locationPanel.add(locationField, BorderLayout.CENTER);
//        locationPanel.add(locationButton, BorderLayout.EAST);
//
//        mainPane.add(locationPanel);
//        return mainPane;
//    }
//}