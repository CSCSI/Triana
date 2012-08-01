package org.trianacode.shiwa.iwir.importer.utils;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import hu.sztaki.lpds.jsdl_lib.Parser;
import org.ggf.schemas.jsdl._2005._11.jsdl.ApplicationType;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.ArgumentType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.FileNameType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.shiwa.fgi.iwir.Task;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.shiwa.executionServices.TaskTypeToolDescriptor;
import org.trianacode.shiwa.iwir.execute.Executable;
import org.trianacode.shiwa.test.InOut;
import org.trianacode.taskgraph.TaskException;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.tool.Tool;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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

    ArrayList<TaskTypeToolDescriptor> taskTypeToolDescriptors = new ArrayList<TaskTypeToolDescriptor>();

    private static TaskTypeRepo singleInstance = null;

    private TaskTypeRepo() {
        addTaskType(InOut.class, "InOut");
    }

    private static TaskTypeRepo getTaskTypeRepo() {
        if (singleInstance == null) {
            singleInstance = new TaskTypeRepo();
        }

        return singleInstance;
    }

    public static void addTaskType(String type, Class clazz) {
        getTaskTypeRepo().addTaskType(clazz, type);
    }

    private void addTaskType(Class clazz, String type) {
        addTaskType(clazz, type, null);
    }

    private void addTaskType(Class clazz, String type, Properties properties){
        taskTypeToolDescriptors.add(new TaskTypeToolDescriptor(type, clazz, properties));
    }

    //    public static Tool getToolFromType(Task iwirTask, FGIWorkflowReader fgiWorkflowReader,
//                                       TrianaProperties properties) throws IOException, JAXBException {
    public static Tool getToolFromType(Task iwirTask,
                                       TrianaProperties properties) throws IOException, JAXBException, ProxyInstantiationException, TaskException {
        Tool tool = null;
        String taskName = iwirTask.getName();
        String type = iwirTask.getTasktype();

        TaskTypeToolDescriptor descriptor = getTaskTypeRepo().getDescriptorForType(type);

        if (descriptor != null) {
            Class clazz = descriptor.getToolClass();
            try {
                tool = AddonUtils.makeTool(clazz, taskName, properties);

                if(descriptor.getProperties() != null){
                    for(String key : descriptor.getProperties().stringPropertyNames()){
                        tool.setParameter(key, descriptor.getProperties().get(key));
                    }
                }
            } catch (Exception ignored) {
            }
        } else {
            TaskTypeRepo.addTaskType(type, null);

            if (type.contains(".")) {
                String unitName = type.substring(type.lastIndexOf(".") + 1);
                String packageName = type.substring(0, type.lastIndexOf("."));
                System.out.println(packageName + unitName);
                try {
                    tool = AddonUtils.makeTool(unitName, packageName, taskName, properties);
                } catch (Exception ignored) {
                    tool = null;
                }
            }
        }

        //FIX ME
//        if (tool == null && fgiWorkflowReader != null){
//            System.out.println("Using JSDL");
//            FGITaskReader reader = fgiWorkflowReader.getReaderByType(type);
//            File jsdlFile = reader.getJSDLFile();
//
//            Executable executable = new Executable(type);
//            populateExecutableFromJSDL(executable, jsdlFile);
//
//            Set<File> envFiles = reader.getDefinitionFiles();
//
//            for(File file : envFiles){
//                FileUtils.copyFileToDirectory(file, executable.getWorkingDir());
//            }
//
//            try {
//                tool = AddonUtils.makeTool(AtomicTaskHolder.class, iwirTask.getTasktype(), properties);
//                tool.setParameter(Executable.EXECUTABLE, executable);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            System.out.println("Tasktype " + iwirTask.getTasktype() + " has "
//                    + envFiles.size() + " files to run. The jsdl is at "
//                    + jsdlFile.getAbsolutePath());
//            for(File file : envFiles){
//                System.out.println("File " + file.getAbsolutePath());
//            }
//        }

        if (tool == null) {
            tool = AddonUtils.makeTool(InOut.class, taskName, properties);
        }
        tool.setToolName(iwirTask.getName());
        tool.setParameter(Executable.TASKTYPE, type);
        return tool;
    }

    private TaskTypeToolDescriptor getDescriptorForType(String type) {
        for(TaskTypeToolDescriptor descriptor : taskTypeToolDescriptors){
            if(descriptor.getTasktype().equals(type)){
                return descriptor;
            }
        }
        return null;
    }

    private static void populateExecutableFromJSDL(Executable executable, File jsdlFile)
            throws IOException, JAXBException {

        String j = readJSDL(jsdlFile);

        Parser parser = new Parser();
        JobDefinitionType jobDef =  parser.readJSDLFromString(j);

        String jobName = jobDef.getJobDescription().getJobIdentification().getJobName();
        ApplicationType attributes = jobDef.getJobDescription().getApplication();

        String dir = System.getProperty("user.dir");
        File workingDir = new File(dir);
        File execDir = new File(workingDir, attributes.getApplicationName());
        execDir.mkdirs();

        executable.setWorkingDir(execDir);

        String[] args = new String[0];
        for(Object obj : attributes.getAny()){

            if(obj instanceof ElementNSImpl){
                if(((ElementNSImpl) obj).getLocalName().equals("POSIXApplication_Type")){
                    JAXBElement jaxbElement =
                            parser.unMarshal((ElementNSImpl) obj, new POSIXApplicationType());

                    POSIXApplicationType posixApplicationType = (POSIXApplicationType) jaxbElement.getValue();

                    FileNameType exec = posixApplicationType.getExecutable();
                    System.out.println("Executable " + exec.getValue());
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

        System.out.println("Will execute " + Arrays.toString(args) + " in " + execDir.getAbsolutePath());

        List<DataStagingType> dataStaging = jobDef.getJobDescription().getDataStaging();
        for(DataStagingType ds : dataStaging){

        }

    }

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

    public static ArrayList<TaskTypeToolDescriptor> getAllDescriptors() {
        return getTaskTypeRepo().taskTypeToolDescriptors;
    }
}
