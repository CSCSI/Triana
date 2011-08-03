package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;


/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 19, 2010
 * Time: 11:08:09 AM
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class JobUnit implements TaskConscious, Displayer {

    public static final int AUTO_CONNECT = 0;
    public static final int SCATTER_CONNECT = 1;
    public static final int ONE2ONE_CONNECT = 2;
    public static final int SPREAD_CONNECT = 3;

    //    @Parameter
    public int numberOfJobs = 1;
    //    @Parameter
    public int fileInputsPerJob = 1;
    //    @Parameter
    public int connectPattern = 0;

    //    @TextFieldParameter
    public String jobName = "a_process";

    //    @TextFieldParameter
    public String args = "an_argument";

    //    @CheckboxParameter
    public boolean collection = false;

    public boolean autoConnect = true;
    public String exec = "ls";

    public Task task;

//    public String getArgs() {
//        return args;
//    }
//
//    public void setName(String name) {
//        this.jobName = name;
//    }

    @Override
    public void setTask(Task task) {
        this.task = task;
        getParams();
    }

    @Process(gather = true)
    public UUID process(List in) {
        UUID thisUUID = UUID.randomUUID();
        log("Job : " + jobName + " Collection = " + collection + " Number of jobs : " + numberOfJobs);

        DaxJobChunk thisJob = new DaxJobChunk();

        thisJob.setJobName(jobName);
        thisJob.setJobArgs(args);
        thisJob.setUuid(thisUUID);
        thisJob.setCollection(collection);
        thisJob.setNumberOfJobs(numberOfJobs);
        thisJob.setFileInputsPerJob(fileInputsPerJob);
        thisJob.setConnectPattern(connectPattern);

        DaxRegister register = DaxRegister.getDaxRegister();
        register.addJob(thisJob);

        log("\nList into " + jobName + " is size: " + in.size() + " contains : " + in.toString() + ".\n ");

        ArgBuilder ab = new ArgBuilder();
        ab.setInputSwitch("-i");
        ab.setOutputSwitch("-o");
        ab.setArgString(args);

        for (Object object : in) {
            if (object instanceof DaxFileChunk) {
                DaxFileChunk fileChunk = (DaxFileChunk) object;
                log("Adding : " + thisJob.getJobName() + " as an output to file : " + fileChunk.getFilename());
                fileChunk.addOutJobChunk(thisJob);
                //        fileChunk.listChunks();

                log("Adding : " + fileChunk.getFilename() + " as an input to job : " + thisJob.getJobName());
                thisJob.addInFileChunk(fileChunk);
                //        thisJob.listChunks();
            } else if (object instanceof UUID) {
                UUID uuid = (UUID) object;
                DaxFileChunk fileChunk = register.getFileChunkFromUUID(uuid);

                if (fileChunk != null) {

                    log("\nPrevious file was : " + fileChunk.getFilename() + "\n");
                    log("Adding : " + thisJob.getJobName() + " as an output to file : " + fileChunk.getFilename());
                    fileChunk.addOutJobChunk(thisJob);

                    log("Adding : " + fileChunk.getFilename() + " as an input to job : " + thisJob.getJobName());
                    thisJob.addInFileChunk(fileChunk);
                    ab.addInputFile(fileChunk.getFilename());

                } else {
                    log("FileChunk not found in register");
                }
            } else {
                log("Cannot handle input : " + object.getClass().getName());
            }
        }

        thisJob.addArgBuilder(ab);
        return thisUUID;
    }

    public void getParams() {
        if (task != null) {
            jobName = getJobName();
            collection = isCollection();
            numberOfJobs = getNumberOfJobs();
            connectPattern = getConnectPattern();
            fileInputsPerJob = getFileInputsPerJob();
        }
    }

    public void setParams() {
        if (task != null) {
            task.setParameter("args", args);
            task.setParameter("numberOfJobs", numberOfJobs);
            task.setParameter("fileInputsPerJob", fileInputsPerJob);
            task.setParameter("collection", collection);
            task.setParameter("connectPattern", connectPattern);
        }
    }

    public void changeToolName(String name) {
        if (task != null) {
            log("Changing tool " + task.getToolName() + " to : " + name);
            task.setParameter("jobName", name);
            task.setToolName(name);
        }
    }

    private String getJobName() {
        Object o = task.getParameter("jobName");
        if (o != null && !((String) o).equals("")) {
            return (String) o;
        }
        return jobName;
    }

    public boolean isCollection() {
        Object o = task.getParameter("collection");
        if (o != null) {
            log("Returned object from param *collection* : " + o.getClass().getCanonicalName() + " : " + o.toString());
            return o.equals(true);
        }
        return false;
    }

    public int getNumberOfJobs() {
        Object o = task.getParameter("numberOfJobs");
        if (o != null) {
            log("Returned object from param *numberOfJobs* : " + o.getClass().getCanonicalName() + " : " + o.toString());
            int value = (Integer) o;
            if (value > 1) {
                return value;
            }
            return 1;
        }
        return 1;
    }

    public int getFileInputsPerJob() {
        Object o = task.getParameter("fileInputsPerJob");
        if (o != null) {
            log("Returned object from param *numberOfJobs* : " + o.getClass().getCanonicalName() + " : " + o.toString());
            int value = (Integer) o;
            if (value > 1) {
                return value;
            }
            return 1;
        }
        return 1;
    }

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


    @CustomGUIComponent
    public Component getComponent() {
        return new JLabel("This is a non-gui tool. Use the triana-pegasus-gui toolbox for more options.");
    }

    private void log(String s) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        //    System.out.println(s);
    }

    @Override
    public void displayMessage(String string) {
        log(string);
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
                    log("Found a DaxJobChunk");
                    if(j == (innerList.size() - 1)){
                        DaxJobChunk jobChunk = (DaxJobChunk) o2;
                        log("This path through workflow includes " + jobChunk.getOutputFilename() + " before this job");
                        fileStrings.add(jobChunk.getOutputFilename());
                    }
                    jcl.add((DaxJobChunk) o2);
                }
                else{
                    log("Found " + o2.getClass().toString() + " instead of a DaxJobChunk.");
                }
            }
        }
        else{
            log("Incoming list didn't contain a list, contains : " + o.getClass().toString());
        }
    }

log("Adding " + fileStrings.size() + " inputs to job.");
for(int i = 0; i < fileStrings.size(); i++){
    thisJob.addInFile((String)fileStrings.get(i));
}

thisJob.setJobArgs(args);
log("Is collection : " + collection);
thisJob.setCollection(collection);



jcl.add(thisJob);

log("\nList out is size: " + jcl.size() + " contains : " + jcl.toString() + ".\n ");

return jcl;
*/

//    register.listAll();