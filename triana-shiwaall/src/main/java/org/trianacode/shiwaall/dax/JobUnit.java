package org.trianacode.shiwaall.dax;

import org.apache.commons.logging.Log;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Aug 19, 2010
 * Time: 11:08:09 AM
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class JobUnit implements TaskConscious, Displayer {

    /** The Constant AUTO_CONNECT. */
    public static final int AUTO_CONNECT = 0;
    
    /** The Constant SCATTER_CONNECT. */
    public static final int SCATTER_CONNECT = 1;
    
    /** The Constant ONE2ONE_CONNECT. */
    public static final int ONE2ONE_CONNECT = 2;
    
    /** The Constant SPREAD_CONNECT. */
    public static final int SPREAD_CONNECT = 3;

    //    @Parameter
    /** The number of jobs. */
    public int numberOfJobs = 1;
    //    @Parameter
    /** The file inputs per job. */
    public int fileInputsPerJob = 1;
    //    @Parameter
    /** The connect pattern. */
    public int connectPattern = 0;

    //    @TextFieldParameter
    /** The job name. */
    public String jobName = "a_process";

    //    @TextFieldParameter
    /** The args. */
    public String args = "an_argument";

    //    @CheckboxParameter
    /** The collection. */
    public boolean collection = false;

    /** The auto connect. */
    public boolean autoConnect = true;
    
    /** The exec. */
    public String exec = "ls";

    /** The input switch. */
    public String inputSwitch = "-i";
    
    /** The output switch. */
    public String outputSwitch = "-o";

    /** The task. */
    public Task task;

    /** The dev log. */
    private static Log devLog = Loggers.DEV_LOGGER;
    
    /** The Constant TRIANA_TOOL. */
    public static final String TRIANA_TOOL = "triana_tool";

    /** The args string array. */
    public ArrayList<String> argsStringArray = null;


//    public String getArgs() {
//        return args;
//    }
//
//    public void setName(String name) {
//        this.jobName = name;
//    }

    /* (non-Javadoc)
 * @see org.trianacode.taskgraph.annotation.TaskConscious#setTask(org.trianacode.taskgraph.Task)
 */
@Override
    public void setTask(Task task) {
        this.task = task;
        getParams();
        setParams();
    }

    /**
     * Process.
     *
     * @param in the in
     * @return the uuid
     */
    @Process(gather = true)
    public UUID process(List in) {

        if (task.getParameter(TRIANA_TOOL) != null) {
            jobName = (String) task.getParameter(TRIANA_TOOL);
            args = "triana.sh -n -U " + jobName;
        }

        UUID thisUUID = UUID.randomUUID();
        devLog.debug("Job : " + jobName + " Collection = " + collection + " Number of jobs : " + numberOfJobs);

        DaxJobChunk thisJob = new DaxJobChunk();

        thisJob.setJobName(jobName);
        thisJob.setJobArgs(args);
        thisJob.setExecLocation(exec);
        thisJob.setUuid(thisUUID);
        thisJob.setCollection(collection);
        thisJob.setNumberOfJobs(numberOfJobs);

        if(argsStringArray != null){
            thisJob.setJobArgs(argsStringArray);
            thisJob.setNumberOfJobs(argsStringArray.size());
        }

        thisJob.setFileInputsPerJob(fileInputsPerJob);
        thisJob.setConnectPattern(connectPattern);

        DaxRegister register = DaxRegister.getDaxRegister();
        register.addJob(thisJob);

        devLog.debug("\nList into " + jobName + " is size: " + in.size() + " contains : " + in.toString() + ".\n ");

        ArgBuilder ab = new ArgBuilder();
        ab.setInputSwitch(inputSwitch);
        ab.setOutputSwitch(outputSwitch);
        ab.setArgString(args);

        for (Object object : in) {
            if (object instanceof DaxFileChunk) {
                DaxFileChunk fileChunk = (DaxFileChunk) object;
                devLog.debug("Adding : " + thisJob.getJobName() + " as an output to file : " + fileChunk.getFilename());
                fileChunk.addOutJobChunk(thisJob);
                //        fileChunk.listChunks();

                devLog.debug("Adding : " + fileChunk.getFilename() + " as an input to job : " + thisJob.getJobName());
                thisJob.addInFileChunk(fileChunk);
                //        thisJob.listChunks();
            } else if (object instanceof UUID) {
                UUID uuid = (UUID) object;
                DaxFileChunk fileChunk = register.getFileChunkFromUUID(uuid);

                if (fileChunk != null) {

//                    devLog.debug("\nPrevious file was : " + fileChunk.getFilename() + "\n");
//                    devLog.debug("Adding : " + thisJob.getJobName() + " as an output to file : " + fileChunk.getFilename());
                    fileChunk.addOutJobChunk(thisJob);

//                    devLog.debug("Adding : " + fileChunk.getFilename() + " as an input to job : " + thisJob.getJobName());
                    thisJob.addInFileChunk(fileChunk);
                    ab.addInputFile(fileChunk.getFilename());

                } else {
                    devLog.debug("FileChunk not found in register");
                }
            } else {
                devLog.debug("Cannot handle input : " + object.getClass().getName());
            }
        }

        thisJob.addArgBuilder(ab);
        return thisUUID;
    }

    /**
     * Gets the params.
     *
     * @return the params
     */
    public void getParams() {
        if (task != null) {
            jobName = getJobName();
            collection = isCollection();
            numberOfJobs = getNumberOfJobs();
            connectPattern = getConnectPattern();
            fileInputsPerJob = getFileInputsPerJob();
            exec = getExecLocation();
            inputSwitch = getInputSwitch();
            outputSwitch = getOutputSwitch();
        }
    }

    /**
     * Gets the output switch.
     *
     * @return the output switch
     */
    private String getOutputSwitch() {
        Object o = task.getParameter("outputSwitch");
        if (o != null && !((String) o).equals("")) {
            return (String) o;
        }
        return outputSwitch;
    }

    /**
     * Gets the input switch.
     *
     * @return the input switch
     */
    private String getInputSwitch() {
        Object o = task.getParameter("inputSwitch");
        if (o != null && !((String) o).equals("")) {
            return (String) o;
        }
        return inputSwitch;
    }



    /**
     * Gets the exec location.
     *
     * @return the exec location
     */
    private String getExecLocation() {
        Object o = task.getParameter("execLocation");
        if (o != null && !((String) o).equals("")) {
            return (String) o;
        }
        return exec;
    }

    /**
     * Sets the params.
     */
    public void setParams() {
        if (task != null) {
            task.setParameter("jobName", jobName);
            task.setParameter("args", args);
            task.setParameter("numberOfJobs", numberOfJobs);
            task.setParameter("fileInputsPerJob", fileInputsPerJob);
            task.setParameter("collection", collection);
            task.setParameter("connectPattern", connectPattern);
            task.setParameter("execLocation", exec);
            task.setParameter("inputSwitch", inputSwitch);
            task.setParameter("outputSwitch", outputSwitch);
        }
    }

    /**
     * Change tool name.
     *
     * @param name the name
     */
    public void changeToolName(String name) {
        if (task != null) {
            devLog.debug("Changing tool " + task.getToolName() + " to : " + name);
            task.setParameter("jobName", name);
            task.setToolName(name);
        }
    }

    /**
     * Gets the job name.
     *
     * @return the job name
     */
    private String getJobName() {
        Object o = task.getParameter("jobName");
        if (o != null && !((String) o).equals("")) {
            return (String) o;
        }
        return jobName;
    }

    /**
     * Checks if is collection.
     *
     * @return true, if is collection
     */
    public boolean isCollection() {
        Object o = task.getParameter("collection");
        if (o != null) {
            devLog.debug("Returned object from param *collection* : " + o.getClass().getCanonicalName() + " : " + o.toString());
            return o.equals(true);
        }
        return false;
    }

    /**
     * Gets the number of jobs.
     *
     * @return the number of jobs
     */
    public int getNumberOfJobs() {
        Object o = task.getParameter("numberOfJobs");
        if (o != null) {
            devLog.debug("Returned object from param *numberOfJobs* : " + o.getClass().getCanonicalName() + " : " + o.toString());
            int value = (Integer) o;
            if (value > 1) {
                return value;
            }
            return 1;
        }
        return 1;
    }

    /**
     * Gets the file inputs per job.
     *
     * @return the file inputs per job
     */
    public int getFileInputsPerJob() {
        Object o = task.getParameter("fileInputsPerJob");
        if (o != null) {
            devLog.debug("Returned object from param *numberOfJobs* : " + o.getClass().getCanonicalName() + " : " + o.toString());
            int value = (Integer) o;
            if (value > 1) {
                return value;
            }
            return 1;
        }
        return 1;
    }

    /**
     * Gets the connect pattern.
     *
     * @return the connect pattern
     */
    public int getConnectPattern() {
        Object o = task.getParameter("connectPattern");
        if (o != null) {
            int value = (Integer) o;
            switch (value) {
                case 0:
                    return AUTO_CONNECT;
                case 1:
                    return SCATTER_CONNECT;
                case 2:
                    return ONE2ONE_CONNECT;
                case 3:
                    return SPREAD_CONNECT;
            }
        }
        return AUTO_CONNECT;
    }


    /**
     * Gets the component.
     *
     * @return the component
     */
    @CustomGUIComponent
    public Component getComponent() {
        return new JLabel("This is a non-gui tool. Use the triana-pegasus-gui toolbox for more options.");
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.dax.Displayer#displayMessage(java.lang.String)
     */
    @Override
    public void displayMessage(String string) {
        devLog.debug(string);
    }
}


/*
List fileStrings = new ArrayList();

List<DaxJobChunk> jcl = new ArrayList<DaxJobChunk>();


    List<List> inList = (List<List>)in;
    for(int i = 0; i < inList.size(); i++){
        Object o = inList.get(i);
        if(o instanceof List){
            List<List> innerList = (List)o;

            for(int j = 0; j < innerList.size(); j++){
                Object o2 = innerList.get(j);
                if(o2 instanceof DaxJobChunk){
                    devLog.debug("Found a DaxJobChunk");
                    if(j == (innerList.size() - 1)){
                        DaxJobChunk jobChunk = (DaxJobChunk) o2;
                        devLog.debug("This path through workflow includes " + jobChunk.getOutputFilename() + " before this job");
                        fileStrings.add(jobChunk.getOutputFilename());
                    }
                    jcl.add((DaxJobChunk) o2);
                }
                else{
                    devLog.debug("Found " + o2.getClass().toString() + " instead of a DaxJobChunk.");
                }
            }
        }
        else{
            devLog.debug("Incoming list didn't contain a list, contains : " + o.getClass().toString());
        }
    }

devLog.debug("Adding " + fileStrings.size() + " inputs to job.");
for(int i = 0; i < fileStrings.size(); i++){
    thisJob.addInFile((String)fileStrings.get(i));
}

thisJob.setJobArgs(args);
devLog.debug("Is collection : " + collection);
thisJob.setCollection(collection);



jcl.add(thisJob);

devLog.debug("\nList out is size: " + jcl.size() + " contains : " + jcl.toString() + ".\n ");

return jcl;
*/

//    register.listAll();