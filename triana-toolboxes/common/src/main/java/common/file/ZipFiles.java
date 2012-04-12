package common.file;

import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Tool;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 11/04/2012
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 * <p/>
 * <p/>
 * This Tool creates a zip file called output.zip in the current runtime folder.
 * The zip contains any files/ folders listed within the parameter panel, and
 * also any files passed in at runtime.
 * <p/>
 * Files can be passed in as File objects or as string paths.
 * If a directory is added, it is recursed.
 * Folders are searched at runtime, not at the time they are added
 * If the zip file already exists, the next integer is appended to the filename eg output9.zip.
 */

@Tool
public class ZipFiles implements TaskConscious, ListSelectionListener {

    private Task task;

    HashSet<File> files = new HashSet<File>();
    private String zipName = "output";
    private JList fileList;
    private DefaultListModel listModel;

    @org.trianacode.annotation.Process(gather = true)
    public File process(List list) {
        files.clear();

        for (Object object : list) {
            File file = null;
            if (object instanceof String) {
                file = new File((String) object);
            }
            if (object instanceof File) {
                file = (File) object;
            }
            if (file != null && file.exists()) {
                if (file.isDirectory()) {
                    Collections.addAll(files, file.listFiles());
                } else {
                    files.add(file);
                }
            }
        }

        try {
            return writeZip();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File writeZip() throws IOException {


        for (Object object : listModel.toArray()) {
            if (object instanceof File) {
                File file = ((File) object);

                if (file.exists()) {
                    if (file.isDirectory()) {
                        Collections.addAll(files, file.listFiles());
                    } else {
                        files.add(file);
                    }
                }
            }
        }
        if (files.size() > 0) {

            File zip = new File(getName() + ".zip");

            System.out.println("Writing " + files.size() + " files to zip " + zip.getAbsolutePath());

            ZipOutputStream jos = new ZipOutputStream(new FileOutputStream(zip));

            for (File file : files) {
                writeEntry(file, jos);
            }

            jos.flush();
            jos.close();

            System.out.println("Created " + zip.getAbsolutePath());

            return zip;
        } else {
            System.out.println("No files to zip");
        }
        return null;
    }

    int itr = 0;

    private String getName() {
        String name;
        if (itr > 0) {
            name = zipName + itr;
        } else {
            name = zipName;
        }
        File file = new File(name + ".zip");

        if (file.exists()) {
            itr++;
            return getName();
        }
        return name;
    }

    private static void writeEntry(File f, ZipOutputStream zos) {

        try {
            String path = f.getName();

            if (f.isDirectory()) {
                zos.putNextEntry(new ZipEntry(path));
                zos.closeEntry();
                File[] childers = f.listFiles();
                for (File childer : childers) {
                    writeEntry(childer, zos);
                }

            } else {
                FileInputStream in = new FileInputStream(f);
                byte[] bytes = new byte[(int) f.length()];
                in.read(bytes);
                zos.putNextEntry(new ZipEntry(path));
                zos.write(bytes, 0, bytes.length);
                zos.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @CustomGUIComponent
    public Component getGUI() {
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        listModel = new DefaultListModel();

        fileList = new JList(listModel);
        fileList.setVisibleRowCount(10);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.addListSelectionListener(this);


        JScrollPane scroll = new JScrollPane(fileList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainPane.add(scroll);

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.Y_AXIS));
        buttonpanel.setBorder(new EmptyBorder(3, 3, 3, 3));
        buttonpanel.add(Box.createVerticalGlue());

        JButton addBtn = new JButton("Add ");
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = chooser.showDialog(GUIEnv.getApplicationFrame(), "File");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    if (f != null) {

                        if (f.isDirectory()) {
                            int num = f.listFiles().length;
                            int choice = JOptionPane.showConfirmDialog(null,
                                    "Are you sure you want to add directory\n" +
                                            f.getAbsolutePath() + " which currently contains \n" +
                                            num + " files?\n" +
                                            "This number may increase at runtime.",
                                    "Add directory?",
                                    JOptionPane.YES_NO_OPTION);

                            if (choice == JOptionPane.OK_OPTION) {
                                for (File file : f.listFiles()) {
                                    listModel.addElement(file);
                                }
                                listModel.addElement(f);
                            } else {

                            }
                        } else {
                            listModel.addElement(f);
                        }
                    }
                }

            }
        });
        buttonpanel.add(addBtn);

        JButton remBtn = new JButton("Remove");
        remBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int selected = fileList.getSelectedIndex();
                if (selected > -1) {
                    listModel.remove(selected);
                }
            }
        });
        buttonpanel.add(remBtn);

        mainPane.add(buttonpanel);
        return mainPane;
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
//        task.setDataInputTypes(new String[]{"java.io.File", "java.lang.String", "Ljava.lang.String;"});
    }


    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
    }

}
