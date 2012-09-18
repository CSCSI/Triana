package org.trianacode.shiwaall.iwir.importer.utils;

//import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
//import hu.sztaki.lpds.jsdl_lib.Parser;
//import org.ggf.schemas.jsdl._2005._11.jsdl.ApplicationType;
//import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
//import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
//import org.ggf.schemas.jsdl._2005._11.jsdl_posix.ArgumentType;
//import org.ggf.schemas.jsdl._2005._11.jsdl_posix.FileNameType;
//import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;

import hu.sztaki.lpds.jsdl_lib.Parser;
import org.apache.commons.io.FileUtils;
import org.ggf.schemas.jsdl._2005._11.jsdl.ApplicationType;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.ArgumentType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.FileNameType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.shiwa.desktop.data.transfer.FGITaskReader;
import org.shiwa.desktop.data.transfer.FGIWorkflowReader;
import org.shiwa.fgi.iwir.InputPort;
import org.shiwa.fgi.iwir.OutputPort;
import org.shiwa.fgi.iwir.Task;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.shiwaall.executionServices.TaskTypeToolDescriptor;
import org.trianacode.shiwaall.iwir.execute.Executable;
import org.trianacode.shiwaall.iwir.holders.AtomicTaskHolder;
import org.trianacode.shiwaall.test.InOut;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.tool.Tool;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;

// TODO: Auto-generated Javadoc
//import org.apache.xerces.dom.ElementNSImpl;

//FIX ME
//import org.shiwa.desktop.data.transfer.FGITaskReader;
//import org.shiwa.desktop.data.transfer.FGIWorkflowReader;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 26/09/2011
 * Time: 20:23
 * To change this template use File | Settings | File Templates.
 */

public class TaskTypeRepo {
//    private HashMap<String, Class> typeTaskMap = new HashMap<String, Class>();
//    private HashMap<Class, String> classTypeMap = new HashMap<Class, String>();

    /** The task type tool descriptors. */
    HashMap<String, TaskTypeToolDescriptor> taskTypeToolDescriptors;

    /** The single instance. */
    private static TaskTypeRepo singleInstance = null;

    /**
     * Instantiates a new task type repo.
     */
    private TaskTypeRepo() {
        taskTypeToolDescriptors = new HashMap<String, TaskTypeToolDescriptor>();
        taskTypeToolDescriptors.put("InOut", new TaskTypeToolDescriptor("InOut", InOut.class, null));
    }

    /**
     * Gets the task type repo.
     *
     * @return the task type repo
     */
    private static TaskTypeRepo getTaskTypeRepo() {
        if (singleInstance == null) {
            singleInstance = new TaskTypeRepo();
        }

        return singleInstance;
    }

    /** True if std out should be enabled. */
    private static boolean printing = false;

    /**
     * Stdout.
     *
     * @param string the string
     */
    private static void stdout(String string){
        if(printing){
            System.out.println(string);
        }
    }

    /**
     * Adds the task type.
     *
     * @param taskTypeToolDescriptor a descriptor to create a task of this tasktype
     */
    public static void addTaskType(TaskTypeToolDescriptor taskTypeToolDescriptor) {
        getTaskTypeRepo().taskTypeToolDescriptors.put(taskTypeToolDescriptor.getTasktype(), taskTypeToolDescriptor);
    }

    /**
     * Gets the tool from type.
     *
     * @param iwirTask the iwir task
     * @param fgiWorkflowReader the fgi workflow reader for the FGI bundle, can be null
     * @param taskGraph the taskgraph the task will be created within
     * @return the tool from type
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JAXBException the jAXB exception
     * @throws ProxyInstantiationException the proxy instantiation exception
     * @throws TaskException the task exception
     */
    public static org.trianacode.taskgraph.Task getTaskFromType(Task iwirTask, FGIWorkflowReader fgiWorkflowReader,
                                                                TaskGraph taskGraph, boolean inOutOnFail) throws IOException, JAXBException, ProxyInstantiationException, TaskException {
        String taskName = iwirTask.getName();
        String type = iwirTask.getTasktype();

        TaskTypeToolDescriptor descriptor = getTaskTypeRepo().getDescriptorForType(type);

        Tool tool = null;
        if (descriptor != null) {
//            tool = getToolFromDescriptor(descriptor, taskGraph.getProperties());
        } else {
            tool = getTrianaTool(type, taskGraph.getProperties());

//            if (type.contains(".")) {
//                String unitName = type.substring(type.lastIndexOf(".") + 1);
//                String packageName = type.substring(0, type.lastIndexOf("."));
//                stdout(packageName + unitName);
//                try {
//                    tool = AddonUtils.makeTool(unitName, packageName, taskName, properties);
//                } catch (Exception ignored) {
//                    tool = null;
//                }
//            }
        }

        //FIX ME
        if (tool == null && fgiWorkflowReader != null){

            stdout("Using JSDL for tasktype " + type);
            FGITaskReader reader = fgiWorkflowReader.getReaderByType(type);
            if(reader != null) {
                File jsdlFile = reader.getJSDLFile();
                if(jsdlFile != null) {
                    Executable executable = new Executable(type);
                    executable.setTaskName(iwirTask.getName());
                    populateExecutableFromJSDL(executable, jsdlFile, iwirTask);

                    Set<File> envFiles = reader.getDefinitionFiles();

                    for(File file : envFiles){
                        File destinationFile = new File(executable.getWorkingDir(), file.getName());
                        FileUtils.copyFile(file, destinationFile);
                        stdout("File " + destinationFile.getAbsolutePath() + " is exec'd " + destinationFile.setExecutable(true, false));
                        file.deleteOnExit();
                    }

                    try {
                        tool = AddonUtils.makeTool(AtomicTaskHolder.class, iwirTask.getTasktype(), taskGraph.getProperties());
                        tool.setParameter(Executable.EXECUTABLE, executable);
                        executable.setTool(tool);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    stdout("Tasktype " + iwirTask.getTasktype() + " has "
                            + envFiles.size() + " files to run. The jsdl is at "
                            + jsdlFile.getAbsolutePath());
                    jsdlFile.deleteOnExit();
                    TaskTypeRepo.addTaskType(new TaskTypeToolDescriptor(type, executable));
                } else {
                    stdout("JSDL null " + iwirTask.getTasktype());
                }
            } else {
                stdout("FGITaskReader null " + iwirTask.getTasktype());
            }
        }

        if (tool == null && inOutOnFail) {
            tool = AddonUtils.makeTool(InOut.class, taskName, taskGraph.getProperties());
        }
        tool.setToolName(iwirTask.getName());
        tool.setParameter(Executable.TASKTYPE, type);
        stdout("Returning tool " + tool.getToolName() + " " + tool.getClass().getCanonicalName());

        org.trianacode.taskgraph.Task task = taskGraph.createTask(tool);

//        if(descriptor != null){
//            Executable executable = descriptor.getExecutable();
//
//
//
//            File workingDir = executable.getWorkingDir();
//            if(workingDir != null){
//
//                // basically a clone, without the annoyance of implementing Cloneable
//                Executable thisExecutable = new Executable(type);
//
//
//                String name = task.getToolName();
//                File execDir = new File(workingDir.getParent());
//
//                File thisWorkingDir = new File(execDir, name);
//
//                FileUtils.copyDirectory(workingDir, thisWorkingDir);
//                thisExecutable.setWorkingDir(thisWorkingDir);
//
//                tool.setParameter(Executable.EXECUTABLE, thisExecutable);
//            }
//
//            tool.setParameter(Executable.TASKTYPE , descriptor.getTasktype());
//        }


        return task;
    }

    private static Tool getTrianaTool(String type, TrianaProperties properties) {
        System.out.println("From triana tool");

//        System.out.println(Arrays.toString(properties.getEngine().getToolTable().getToolNames()));

        return properties.getEngine().getToolTable().getTool(type);
    }

    public static Tool getToolFromDescriptor(TaskTypeToolDescriptor descriptor, TrianaProperties properties) {
        System.out.println("From descriptor");
        Tool tool = null;
//        Class clazz = descriptor.getToolClass();
//        String taskName = descriptor.getTasktype();

        tool = getTrianaTool(descriptor.getTasktype(), properties);
        if(tool == null){
            try {

                if(descriptor.getExecutable() != null){
                    if(descriptor.getExecutable().getTool() != null){
                        tool = descriptor.getExecutable().getTool();
//                        Executable executable = descriptor.getExecutable();
//
//                        tool.setParameter(Executable.EXECUTABLE, descriptor.getExecutable());
//                        tool.setParameter(Executable.TASKTYPE , descriptor.getTasktype());
                    }
                }

//                tool = AddonUtils.makeTool(AtomicTaskHolder.class, descriptor.getTasktype(), properties);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

        }

        if(tool != null && descriptor.getProperties() != null){
            for(String key : descriptor.getProperties().stringPropertyNames()){
                tool.setParameter(key, descriptor.getProperties().get(key));
            }
        }
        return tool;
    }

    /**
     * Gets the descriptor for type.
     *
     * @param type the type
     * @return the descriptor for type
     */
    private TaskTypeToolDescriptor getDescriptorForType(String type) {
        return taskTypeToolDescriptors.get(type);
    }

    /**
     * Populate executable from jsdl.
     *
     * @param executable the executable
     * @param jsdlFile the jsdl file
     * @param iwirTask the iwir task
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JAXBException the jAXB exception
     */
    private static void populateExecutableFromJSDL(Executable executable, File jsdlFile, Task iwirTask)
            throws IOException, JAXBException {

        String jsdlString = readJSDL(jsdlFile);
        populateExecutableFromJSDLString(executable, jsdlString, iwirTask);
    }

    public static void populateExecutableFromJSDLString(Executable executable, String j, Task iwirTask) throws JAXBException, FileNotFoundException {
        executable.setJSDLstring(j);

        Parser parser = new Parser();
        JobDefinitionType jobDef =  parser.readJSDLFromString(j);

        String jobName = jobDef.getJobDescription().getJobIdentification().getJobName();
        ApplicationType attributes = jobDef.getJobDescription().getApplication();

        String dir = System.getProperty("user.dir");
        File workingDir = new File(dir);
        File executeRoot = new File(workingDir, "executable");
        File execDir = new File(executeRoot, executable.getTaskName());
        execDir.mkdirs();

        executable.setWorkingDir(execDir);

//        for(QName name : attributes.getOtherAttributes().keySet()){
//            String att = attributes.getOtherAttributes().get(name);
//            System.out.println(name.toString() + " " + att);
//        }

        String[] args = new String[0];
        for(Object obj : attributes.getAny()){
            if(obj instanceof Element){
                if(((Element) obj).getLocalName().equals("POSIXApplication_Type")){
                    JAXBElement jaxbElement =
                            parser.unMarshal((Element) obj, new POSIXApplicationType());

                    POSIXApplicationType posixApplicationType = (POSIXApplicationType) jaxbElement.getValue();

                    FileNameType exec = posixApplicationType.getExecutable();
                    stdout("Executable " + exec.getValue());
                    executable.setPrimaryExec("./" + exec.getValue());

                    List<ArgumentType> argumentTypes = posixApplicationType.getArgument();
                    ArrayList<String> argsStrings = new ArrayList<String>();
                    argsStrings.add("./" + exec.getValue());

                    for(ArgumentType argumentType : argumentTypes){
                        argsStrings.add(argumentType.getValue());
                    }
                    args = argsStrings.toArray(new String[argsStrings.size()]);
                    executable.setArgs(args);
                }
            }
        }

        List<DataStagingType> dataStaging = jobDef.getJobDescription().getDataStaging();
        for(InputPort inputPort : iwirTask.getInputPorts()){
            String portName = inputPort.getName();

            for(DataStagingType dataStage : dataStaging){
                if(dataStage.getName() != null && dataStage.getName().equals(portName)){
                    String fileName = dataStage.getFileName();
                    File inputFile = new File(executable.getWorkingDir(), fileName);
                    executable.addInputFile(inputPort, inputFile);

                    executable.addExecutableNodeMapping(inputPort, fileName);
                }
            }
        }

        for(OutputPort outputPort : iwirTask.getOutputPorts()){
            String portName = outputPort.getName();

            for(DataStagingType dataStage : dataStaging){
                if(dataStage.getName() != null && dataStage.getName().equals(portName)){
                    String fileName = dataStage.getFileName();
                    File outputFile = new File(executable.getWorkingDir(), fileName);
                    executable.addOutputFile(outputPort, outputFile);
                }
            }
        }

        stdout("Staging binary " + Arrays.toString(args) + " in " + execDir.getAbsolutePath());

    }

    /**
     * Read jsdl.
     *
     * @param file the file
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static String readJSDL(File file) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(file));
        char[] buf = new char[1024];
        int numRead = 0;

        while ((numRead = reader.read(buf)) != -1) {

            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

    /**
     * Gets the all descriptors.
     *
     * @return the all descriptors
     */
    public static Collection<TaskTypeToolDescriptor> getAllDescriptors() {
        return getTaskTypeRepo().taskTypeToolDescriptors.values();
    }

    public static TaskTypeToolDescriptor getDescriptorFromType(String tasktype) {
        return getTaskTypeRepo().getDescriptorForType(tasktype);
    }
}
