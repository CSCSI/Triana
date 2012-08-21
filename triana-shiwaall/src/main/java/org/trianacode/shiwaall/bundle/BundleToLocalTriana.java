package org.trianacode.shiwaall.bundle;

import org.apache.commons.logging.Log;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.TextFieldParameter;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.StreamToOutput;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.enactment.logging.stampede.StampedeLog;
import org.trianacode.shiwaall.utils.BrokerUtils;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//import org.shiwa.desktop.data.util.DataUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Aug 19, 2010
 * Time: 11:08:09 AM
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class BundleToLocalTriana implements TaskConscious {

    @TextFieldParameter
    private String executableString = "";

    @TextFieldParameter
    private String runtimeDirectory = "";

    @TextFieldParameter
    private String concurrentProcesses = "";

    private int runningProcesses = 0;

    ConcurrentHashMap<Integer, returnCode> returnCodes;// = new ConcurrentHashMap<Integer, returnCode>();
    private static final String EXIT_CODE = "exitCode";

    enum returnCode {
        SUCCESS,
        FAIL
    }

    private Task task;

    @Process(gather = true)
    public ConcurrentHashMap<Integer, returnCode> process(List list) throws Exception {
        ArrayList<String> executableStrings = new ArrayList<String>();

        executableString = execField.getText();
        if (!executableString.equals("")) {
            executableStrings.add(executableString);
        }

        for (Object object : list) {
            if (object instanceof String[]) {
                Collections.addAll(executableStrings, (String[]) object);
            } else if (object instanceof String) {
                executableStrings.add((String) object);
            }
        }

        runtimeDirectory = runtimeField.getText();
        if (runtimeDirectory == null || runtimeDirectory.equals("")) {
            runtimeDirectory = ".";
        }

        String concurrent = concurrentField.getText();
        int allowedProcesses;
        if (concurrent != null) {
            if (concurrent.equals("")) {
                allowedProcesses = 1;
            } else {
                allowedProcesses = Integer.parseInt(concurrent);
            }
        } else {
            allowedProcesses = 1;
        }
        int run = 0;
        System.out.println(executableStrings.size() + " processes to run.");
        System.out.println("Allowing " + allowedProcesses + " concurrent processes.");
        returnCodes = new ConcurrentHashMap<Integer, returnCode>();

        while (run < executableStrings.size()) {
            if (runningProcesses < allowedProcesses) {
                System.out.println("Beginning run " + run);
                String exec = executableStrings.get(run);
                System.out.println("Run " + run + ", executing : " + exec);
                StringExecutor executor = new StringExecutor(exec, new File(runtimeDirectory), run);
                executor.addPropertyChangeListener(new ExitCodeListener(run));
                executor.execute();

                run++;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int wait = 0;
        int time = 0;
        while (returnCodes.size() < executableStrings.size()) {
            if (wait > 10000) {
                time += 10;
                System.out.println(returnCodes.size() + "/" + executableStrings.size() + " complete. " +
                        "~" + time + " seconds past."
                );
                wait = 0;
            }
            try {
                Thread.sleep(1000);
                wait += 1000;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(returnCodes.size() + " return codes recorded " + returnCodes.values().toString());
        return returnCodes;
    }

    class StringExecutor extends SwingWorker<Void, Void> {
        private String execString;
        private File execLocation;
        private int runID;

        public StringExecutor(String execString, File execLocation, int runID) {
            this.execString = execString;
            this.execLocation = execLocation;
            this.runID = runID;
        }

        @Override
        protected Void doInBackground() throws Exception {
            runningProcesses++;
            String exitCode = String.valueOf(executeProcess(execString, execLocation));

            this.getPropertyChangeSupport().firePropertyChange(EXIT_CODE, null, exitCode);
            return null;
        }

        public void done() {
            System.out.println("Finished " + this.runID);
            runningProcesses--;
        }

        private int executeProcess(String executableString, File execLocation) {


            List<String> options = new ArrayList<String>();
            String[] optionsStrings = executableString.split(" ");
            Collections.addAll(options, optionsStrings);

            try {
                String bundlePath = optionsStrings[4];

                ShiwaBundleHelper shiwaBundleHelper = new ShiwaBundleHelper(bundlePath);

                BrokerUtils.prepareSubworkflow(
                        task, UUID.randomUUID(), shiwaBundleHelper.getWorkflowImplementation()
                );

                File temp = File.createTempFile("anything", "tmp");

                shiwaBundleHelper.bundle(temp);

//                DataUtils.bundle(
//                        temp,
//                        shiwaBundleHelper.getWorkflowImplementation());

                optionsStrings[4] = temp.getAbsolutePath();

            } catch (Exception e) {
                e.printStackTrace();
            }

            java.lang.Process process = null;
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(optionsStrings);
                processBuilder.directory(execLocation);

                System.out.println(processBuilder.directory().getAbsolutePath());
                process = processBuilder.start();

                new StreamToOutput(process.getInputStream(), "std.out").start();
                new StreamToOutput(process.getErrorStream(), "err").start();

                return process.waitFor();

            } catch (Exception except) {
                except.printStackTrace();
                this.cancel(true);
            }

            return 1;
        }

    }

    private void log(String s) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }


    JTextField execField = new JTextField("");
    JTextField runtimeField = new JTextField(""); ///Users/ian/Work/testBundles/DART");
    JTextField concurrentField = new JTextField("");
    private JPanel mainPanel;


    @CustomGUIComponent
    public Component getComponent() {
        mainPanel = new JPanel();

        getParams();

        mainPanel.setLayout(new BorderLayout(5, 5));

        JPanel topPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        JLabel execLabel = new JLabel("Executable string : ");
        JLabel folderLabel = new JLabel("Runtime folder : ");
        JLabel concurrentExecutionsLabel = new JLabel("Concurrent executions : ");

        topPanel.add(execLabel);
        topPanel.add(execField);

        topPanel.add(folderLabel);
        topPanel.add(runtimeField);

        topPanel.add(concurrentExecutionsLabel);
        topPanel.add(concurrentField);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel lowerPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new helpFrame();
            }
        });
        JButton okButton = new JButton("Set");
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                apply();
            }
        });

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
        task.setParameter("executable", execField.getText() == null ? "" : execField.getText());
        task.setParameter("runtimeDirectory", runtimeField.getText() == null ? "" : runtimeField.getText());
        task.setParameter("concurrentProcesses", concurrentField.getText());
    }

    private void getParams() {
        executableString = (String) task.getParameter("executable");
        execField.setText(executableString);
        runtimeDirectory = (String) task.getParameter("runtimeDirectory");
        runtimeField.setText(runtimeDirectory);
        concurrentProcesses = (String) task.getParameter("concurrentProcesses");
        concurrentField.setText(concurrentProcesses);
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
        task.setParameter(StampedeLog.STAMPEDE_TASK_TYPE, StampedeLog.JobType.dax.desc);
        getParams();
    }

    class helpFrame extends JFrame {
        public helpFrame() {
            this.setTitle("Help");
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel helpLabel = new JLabel("This is helpful");
            JTextArea helpArea = new JTextArea("Executable String is a line of text which will be run in a process " +
                    "\n\nRuntime folder is the location which the process will be run." +
                    "\n\nConcurrent executions is the number of processes which will run at the same time. If the " +
                    "input array has multiple strings to run, then this number dictates how many to do at the same time. " +
                    "Once a process finishes, another starts till they are all complete."
            );
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

    private class ExitCodeListener implements PropertyChangeListener {

        private Integer runID;

        public ExitCodeListener(int runID) {
            this.runID = runID;
        }

        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

            Object returned = propertyChangeEvent.getNewValue();
            if (returned instanceof String) {
                System.out.println("Run " + runID + " returned " + returned);
                String code = (String) returned;
                if (code.equals("0")) {
                    returnCodes.put(runID, returnCode.SUCCESS);
                } else {
                    returnCodes.put(runID, returnCode.FAIL);
                }
            }
        }
    }
}
