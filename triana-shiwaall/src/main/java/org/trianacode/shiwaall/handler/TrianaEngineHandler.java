package org.trianacode.shiwaall.handler;

import org.apache.commons.logging.Log;
import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.shiwa.desktop.data.description.workflow.Author;
import org.shiwa.desktop.data.transfer.WorkflowEngineHandler;
import org.shiwa.desktop.gui.SHIWADesktop;
import org.trianacode.TrianaInstance;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;

import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
* Created by IntelliJ IDEA.
* User: Ian Harvey
* Date: 24/02/2011
* Time: 12:56
* To change this template use File | Settings | File Templates.
*/
public class TrianaEngineHandler implements WorkflowEngineHandler {

    /** The dev log. */
    private static Log devLog = Loggers.DEV_LOGGER;

    /** The task. */
    private Task task;
    
    /** The display image. */
    private InputStream displayImage;
    
    /** The triana instance. */
    private TrianaInstance trianaInstance;

    /**
     * Instantiates a new triana engine handler.
     *
     * @param task the task
     * @param trianaInstance the triana instance
     * @param displayImage the display image
     */
    public TrianaEngineHandler(Task task, TrianaInstance trianaInstance, InputStream displayImage) {
        this.task = task;
        this.trianaInstance = trianaInstance;
        this.displayImage = displayImage;
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.WorkflowEngineHandler#getEngineName(java.util.Set)
     */
    @Override
    public String getEngineName(Set<String> engines) {
        return "Triana";
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.WorkflowEngineHandler#getEngineVersion()
     */
    @Override
    public String getEngineVersion() {
        return (String) task.getProperties().get(TrianaProperties.VERSION);
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.WorkflowEngineHandler#getWorkflowLanguage(java.util.Set)
     */
    @Override
    public String getWorkflowLanguage(Set<String> languages) {
        return "triana-taskgraph";
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.TaskHandler#getSignature()
     */
    @Override
    public TransferSignature getSignature() {
        TransferSignature signature = new TransferSignature();
        signature.setName(task.getToolName());
        setInputPorts(signature);
        setOutputPorts(signature);

        return signature;
    }

    /**
     * Sets the output ports.
     *
     * @param signature the new output ports
     */
    private void setOutputPorts(TransferSignature signature) {
        for (int i = 0; i < task.getDataOutputNodeCount(); i++) {
            String s = task.getDataOutputTypes(i)[0];
            signature.addOutput("OutputPort_" + i, ObjectToSchema.getSchemaURIString(s));
        }
    }

    /**
     * Sets the input ports.
     *
     * @param signature the new input ports
     */
    private void setInputPorts(TransferSignature signature) {
        for (int i = 0; i < task.getDataInputNodeCount(); i++) {
            String s = task.getDataInputTypes(i)[0];
            signature.addInput("InputPort_" + i, s);
        }
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.TaskHandler#getDefinition()
     */
    @Override
    public InputStream getDefinition() {
        try {
            File temp = File.createTempFile("publishedTaskgraphTemp", ".xml");
            temp.deleteOnExit();
            XMLWriter writer = new XMLWriter(new BufferedWriter(new FileWriter(temp)));
            writer.writeComponent(task);
            writer.close();
            devLog.debug("Sending taskgraph to ShiwaDesktop.");
            return new FileInputStream(temp);

        } catch (Exception e) {
            devLog.debug("Failed to create temp xml for output to shiwa-desktop : ");
            e.printStackTrace();
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.TaskHandler#getDefinitionName()
     */
    @Override
    public String getDefinitionName() {
        return task.getQualifiedTaskName();
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.WorkflowEngineHandler#getDisplayImage()
     */
    @Override
    public InputStream getDisplayImage() {
        devLog.debug("Sending display image to ShiwaDesktop");
        return displayImage;
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.WorkflowEngineHandler#getDisplayImageName()
     */
    @Override
    public String getDisplayImageName() {
        return task.getQualifiedTaskName() + "-image.jpg";
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.TaskHandler#getAuthors()
     */
    @Override
    public List<Author> getAuthors() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.TaskHandler#getVersion()
     */
    @Override
    public String getVersion() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.TaskHandler#getDescription()
     */
    @Override
    public String getDescription() {
        return null;
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws TaskGraphException the task graph exception
     */
    public static void main(String[] args) throws IOException, TaskGraphException {
        String wf = args[0];
        File f = new File(wf);

        if (!f.exists()) {
            devLog.debug("Cannot find workflow file:" + wf);
            System.exit(1);
        }

        String[] engineArgs = new String[args.length - 1];
        System.arraycopy(args, 1, engineArgs, 0, args.length - 1);

        TrianaInstance engine = new TrianaInstance(engineArgs);
        engine.init();
        XMLReader reader = new XMLReader(new FileReader(f));
        Tool tool = reader.readComponent(engine.getProperties());

//        JPanel jPanel = new SHIWADesktopPanel(
//                new TrianaEngineHandler((Task) tool, engine, null),
//                SHIWADesktopPanel.ButtonOption.SHOW_TOOLBAR
//        );
        SHIWADesktop shiwaDesktop = new SHIWADesktop(
                new TrianaEngineHandler((Task) tool, engine, null),
                SHIWADesktop.ButtonOption.SHOW_TOOLBAR
        );
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(shiwaDesktop.getPanel());
        jFrame.pack();
        jFrame.setVisible(true);
    }


}
