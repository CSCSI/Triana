package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.annotation.*;
import org.trianacode.annotation.Process;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * todo - pipe input stream?
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 27, 2010
 */

@Tool //(panelClass = "org.trianacode.org.trianacode.pegasus.gui.dax.ExecUnitPanel")
public class ExecUnit implements TaskConscious {

    public static final String numberOfFiles = "Number of files";
    public static final String namesOfFiles = "Names of files";

    DaxSettingObject dso = new DaxSettingObject();

    @TextFieldParameter
    private String executable = "";
    @TextFieldParameter
    private String input_file = "";
    @TextFieldParameter
    private String executable_args = "";
    @TextFieldParameter
    private String search_for = "";
    @Parameter
    private String save_as = "";
    private Task task;

    private static Log devLog = Loggers.DEV_LOGGER;

    @Process(gather = true)
    public DaxSettingObject process(List in) {

        dso.clear();

        java.lang.Process process;
        BufferedReader errorreader;
        BufferedReader inreader;
        String str;
        boolean errors = false;
        String errLog = "";

        List<String> options = new ArrayList<String>();
        String[] optionsStrings = executable_args.split(" ");
        for (int i = 0; i < optionsStrings.length; i++) {
            options.add(optionsStrings[i]);
        }

        StringBuilder out = new StringBuilder();
        List commmandStrVector = new ArrayList();
        if (!executable.equals("")) {
            if (!error()) {
                commmandStrVector.add(executable);
                if (!input_file.equals("")) {
                    commmandStrVector.add(input_file);
                }
                commmandStrVector.addAll(options);

                StringBuilder buffer = new StringBuilder();
                for (Iterator iterator = commmandStrVector.iterator(); iterator.hasNext();) {
                    buffer.append((String) iterator.next());
                    buffer.append(" ");
                }
                devLog.info("ExecUnit.process invocation:" + buffer.toString());

                try {
                    String[] cmdarray = (String[]) commmandStrVector.toArray(new String[commmandStrVector.size()]);

                    Runtime runtime = Runtime.getRuntime();
                    process = runtime.exec(cmdarray);  // execute command


                    errorreader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    while ((str = errorreader.readLine()) != null) {
                        errors = true;
                        errLog += str + "\n";
                    }
                    errorreader.close();

                    inreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    str = "";
                    while ((str = inreader.readLine()) != null) {
                        out.append(str).append("\n");
                        checkForData(str);
                    }
                    inreader.close();

                } catch (Exception except) {
                    except.printStackTrace();
                }

                if (!errors) {
                    //        log("ExecUnit.process output:" + out.toString());
                } else {
                    devLog.info("ExecUnit.process err:" + errLog);
                }

                dso.addFullOutput(out.toString());
            }
        }
        return dso;

    }

    private boolean error() {
        if (input_file.equals("")) {
            return false;
        } else {
            if (new File(input_file).exists()) {
                return false;
            } else {
//                JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(), "Input file (" + input_file + ")for ExecUnit does not appear to exist.\n" +
//                        ".dax file will be created, but may contain errors.", "Error", JOptionPane.WARNING_MESSAGE);
                devLog.warn("Input file for exec unit does not exist.");
                return true;
            }
        }
    }

    private void checkForData(String s) {
        if (s.contains(search_for)) {
            devLog.debug("Found : " + search_for);
            String found = s.substring(search_for.length());
            devLog.debug("Adding : " + found);

            if (save_as.equals(ExecUnit.numberOfFiles)) {
                dso.addObject(ExecUnit.numberOfFiles, found);
            }
            if (save_as.equals(ExecUnit.namesOfFiles)) {
                String foundNames = dso.getFileNames();
                String names = "";
                if (foundNames.equals("")) {
                    names = found;
                } else {
                    names = foundNames + ", " + found;
                }
                dso.addObject(ExecUnit.namesOfFiles, names);
            }
        } else {
            //         log("String : *" + s + "* does not contain : " + search_for);
        }
    }

    JTextField execField = new JTextField();
    JTextField fileField = new JTextField();
    JTextField execArgsField = new JTextField();
    JTextField searchField = new JTextField();
    String[] options = {ExecUnit.numberOfFiles, ExecUnit.namesOfFiles};
    JComboBox dropDown = new JComboBox(options);


    //    private HashMap map = new HashMap();
    private JPanel mainPanel;


    @CustomGUIComponent
    public Component getComponent() {
        mainPanel = new JPanel();

        getParams();

        mainPanel.setLayout(new BorderLayout(5, 5));

        JPanel topPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        final JPanel midPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JPanel lowerPanel = new JPanel(new GridLayout(0, 2, 5, 5));


        JLabel execLabel = new JLabel("Executable : ");
        JLabel fileLabel = new JLabel("Input file : ");

        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.add(fileField, BorderLayout.CENTER);
        JButton fileButton = new JButton("...");
        fileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal = chooser.showDialog(mainPanel, "Input File");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    if (f != null) {
                        String file = f.getPath();
                        try {
                            devLog.info("File : " + f.getAbsolutePath() + f.getCanonicalPath() + f.getPath());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        fileField.setText(file);
                    }
                }
            }
        });
        filePanel.add(fileButton, BorderLayout.EAST);

        JLabel execArgsLabel = new JLabel("Executable arguments : ");

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new helpFrame();
            }
        });

        topPanel.add(execLabel);
        topPanel.add(execField);
        topPanel.add(fileLabel);
        topPanel.add(filePanel);
        topPanel.add(execArgsLabel);
        topPanel.add(execArgsField);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        midPanel.add(new JLabel("Search string : "));
        midPanel.add(searchField);
        mainPanel.add(midPanel, BorderLayout.CENTER);

        lowerPanel.add(dropDown);
        lowerPanel.add(helpButton);
        mainPanel.add(lowerPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    public void applyClicked() {
        apply();
    }

    public void okClicked() {
        apply();
    }

    public void apply() {

        task.setParameter("executable", execField.getText());
        task.setParameter("input_file", fileField.getText());
        task.setParameter("executable_args", execArgsField.getText());
        task.setParameter("search_for", searchField.getText());
        task.setParameter("save_as", (String) dropDown.getSelectedItem());
    }

    private void getParams() {
        execField.setText((String) task.getParameter("executable"));
        fileField.setText((String) task.getParameter("input_file"));
        execArgsField.setText((String) task.getParameter("executable_args"));
        searchField.setText((String) task.getParameter("search_for"));
        dropDown.setSelectedItem((String) task.getParameter("save_as"));
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }

    class helpFrame extends JFrame {
        public helpFrame() {
            this.setTitle("Help");
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel helpLabel = new JLabel("This is helpful");
            JTextArea helpArea = new JTextArea("Executable refers to the program which will be run, eg python, ls " +
                    "\n\nInput file is the file, or files, to be read by the running program." +
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
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            panel.add(ok);
            this.add(panel);
            this.setSize(400, 200);
            this.setVisible(true);
        }
    }
}
