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

// TODO: Auto-generated Javadoc
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

    /** The executable string. */
    @TextFieldParameter
    private String executableString = "";

    /** The runtime directory. */
    @TextFieldParameter
    private String runtimeDirectory = "";

    /** The concurrent processes. */
    @TextFieldParameter
    private String concurrentProcesses = "";

    /** The running processes. */
    private int runningProcesses = 0;

    /** The return codes. */
    ConcurrentHashMap<Integer, returnCode> returnCodes;// = new ConcurrentHashMap<Integer, returnCode>();
    
    /** The Constant EXIT_CODE. */
    private static final String EXIT_CODE = "exitCode";

    /**
     * The Enum returnCode.
     */
    enum returnCode {
        
        /** The success. */
        SUCCESS,
        
        /** The fail. */
        FAIL
    }

    /** The task. */
    private Task task;

    /**
     * Process.
     *
     * @param list the list
     * @return the concurrent hash map
     * @throws Exception the exception
     */
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

    /**
     * The Class StringExecutor.
     */
    class StringExecutor extends SwingWorker<Void, Void> {
        
        /** The exec string. */
        private String execString;
        
        /** The exec location. */
        private File execLocation;
        
        /** The run id. */
        private int runID;

        /**
         * Instantiates a new string executor.
         *
         * @param execString the exec string
         * @param execLocation the exec location
         * @param runID the run id
         */
        public StringExecutor(String execString, File execLocation, int runID) {
            this.execString = execString;
            this.execLocation = execLocation;
            this.runID = runID;
        }

        /* (non-Javadoc)
         * @see javax.swing.SwingWorker#doInBackground()
         */
        @Override
        protected Void doInBackground() throws Exception {
            runningProcesses++;
            String exitCode = String.valueOf(executeProcess(execString, execLocation));

            this.getPropertyChangeSupport().firePropertyChange(EXIT_CODE, null, exitCode);
            return null;
        }

        /* (non-Javadoc)
         * @see javax.swing.SwingWorker#done()
         */
        public void done() {
            System.out.println("Finished " + this.runID);
            runningProcesses--;
        }

        /**
         * Execute process.
         *
         * @param executableString the executable string
         * @param execLocation the exec location
         * @return the int
         */
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

    /**
     * Log.
     *
     * @param s the s
     */
    private void log(String s) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }


    /** The exec field. */
    JTextField execField = new JTextField("");
    
    /** The runtime field. */
    JTextField runtimeField = new JTextField(""); ///Users/ian/Work/testBundles/DART");
    
    /** The concurrent field. */
    JTextField concurrentField = new JTextField("");
    
    /** The main panel. */
    private JPanel mainPanel;


    /**
     * Gets the component.
     *
     * @return the component
     */
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

    /**
     * Apply clicked.
     */
    public void applyClicked() {
        apply();
    }

    /**
     * Ok clicked.
     */
    public void okClicked() {
        apply();
    }

    /**
     * Apply.
     */
    public void apply() {
        task.setParameter("executable", execField.getText() == null ? "" : execField.getText());
        task.setParameter("runtimeDirectory", runtimeField.getText() == null ? "" : runtimeField.getText());
        task.setParameter("concurrentProcesses", concurrentField.getText());
    }

    /**
     * Gets the params.
     *
     * @return the params
     */
    private void getParams() {
        executableString = (String) task.getParameter("executable");
        execField.setText(executableString);
        runtimeDirectory = (String) task.getParameter("runtimeDirectory");
        runtimeField.setText(runtimeDirectory);
        concurrentProcesses = (String) task.getParameter("concurrentProcesses");
        concurrentField.setText(concurrentProcesses);
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.annotation.TaskConscious#setTask(org.trianacode.taskgraph.Task)
     */
    @Override
    public void setTask(Task task) {
        this.task = task;
        task.setParameter(StampedeLog.STAMPEDE_TASK_TYPE, StampedeLog.JobType.dax.desc);
        getParams();
    }

    /**
     * The Class helpFrame.
     */
    class helpFrame extends JFrame {
        
        /**
         * Instantiates a new help frame.
         */
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

    /**
     * The listener interface for receiving exitCode events.
     * The class that is interested in processing a exitCode
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addExitCodeListener<code> method. When
     * the exitCode event occurs, that object's appropriate
     * method is invoked.
     *
     * @see ExitCodeEvent
     */
    private class ExitCodeListener implements PropertyChangeListener {

        /** The run id. */
        private Integer runID;

        /**
         * Instantiates a new exit code listener.
         *
         * @param runID the run id
         */
        public ExitCodeListener(int runID) {
            this.runID = runID;
        }

        /* (non-Javadoc)
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
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
