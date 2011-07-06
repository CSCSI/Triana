package common.processing;

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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 19, 2010
 * Time: 11:08:09 AM
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class ExecuteString implements TaskConscious {

    @TextFieldParameter
    private String executableString = "";

    @Parameter
    private Task task;

    @Process
    public int process(String executableString) {

        java.lang.Process process = null;
        BufferedReader errorreader;
        BufferedReader inreader;
        String str;
        boolean errors = false;
        String errLog = "";

        List<String> options = new ArrayList<String>();
        String[] optionsStrings = executableString.split(" ");
        for (int i = 0; i < optionsStrings.length; i++) {
            options.add(optionsStrings[i]);
        }

        StringBuilder out = new StringBuilder();


        try {

            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec(optionsStrings);  // execute command


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
            }
            inreader.close();

        } catch (Exception except) {
            except.printStackTrace();
        }

        if (!errors) {
            //        log("ExecUnit.process output:" + out.toString());
        } else {
            log("ExecUnit.process err:" + errLog);
        }


        if (process != null) {
            return process.exitValue();
        } else {
            return -1;
        }

    }

    private void log(String s) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }


    JTextField execField = new JTextField();
    private JPanel mainPanel;


    @CustomGUIComponent
    public Component getComponent() {
        mainPanel = new JPanel();

        getParams();

        mainPanel.setLayout(new BorderLayout(5, 5));

        JPanel topPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JPanel lowerPanel = new JPanel(new GridLayout(0, 2, 5, 5));


        JLabel execLabel = new JLabel("Executable string : ");

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new helpFrame();
            }
        });
        JButton okButton = new JButton("OK");
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                apply();
            }
        });

        topPanel.add(execLabel);
        topPanel.add(execField);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        lowerPanel.add(okButton);
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
    }

    private void getParams() {
        execField.setText((String) task.getParameter("executable"));
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
                    "\n\nExecutable arguments are the parameters given to that program, eg example.py, -l");
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
