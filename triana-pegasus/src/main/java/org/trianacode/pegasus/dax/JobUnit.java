package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.annotation.*;
import org.trianacode.taskgraph.annotation.Process;

import java.util.List;
import java.util.UUID;


/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 19, 2010
 * Time: 11:08:09 AM
 * To change this template use File | Settings | File Templates.
 */

@Tool(panelClass = "org.trianacode.pegasus.dax.JobUnitPanel", renderingHints = {"DAX_JOB_RENDERING_HINT"})
public class JobUnit{


    @Parameter
    private String programmaticParam = "This is a process";
    @Parameter
    private int numberOfJobs = 1;

    @TextFieldParameter
    private String jobName = "a_process";

    @TextFieldParameter
    private String args = "an_argument";

    @CheckboxParameter
    private boolean collection = false;

    public String getArgs(){
        return args;
    }

    public void setName(String name) {
        this.jobName = name;
    }

    @Process(gather=true)
    public UUID process(List in) {
        DaxJobChunk thisJob = new DaxJobChunk();

        thisJob.setJobName(jobName);
        thisJob.setUuid(UUID.randomUUID());
        thisJob.setCollection(collection);
        thisJob.setNumberOfJobs(numberOfJobs);

        DaxRegister register = DaxRegister.getDaxRegister();
        register.addJob(thisJob);

        log("\nList into " + jobName + " is size: " + in.size() + " contains : " + in.toString() + ".\n ");


        for(Object object : in){
            if(object instanceof DaxFileChunk){
                DaxFileChunk fileChunk = (DaxFileChunk)object;
                log("Adding : " + thisJob.getJobName() + " as an output to file : " + fileChunk.getFilename());
                fileChunk.addOutJobChunk(thisJob);
                //        fileChunk.listChunks();

                log("Adding : " + fileChunk.getFilename() + " as an input to job : " + thisJob.getJobName());
                thisJob.addInFileChunk(fileChunk);
                //        thisJob.listChunks();
            }

            else if(object instanceof UUID){
                UUID uuid = (UUID)object;
                DaxFileChunk fileChunk = register.getFileChunkFromUUID(uuid);

                if(fileChunk != null){

                    log("\nPrevious file was : " + fileChunk.getFilename() + "\n");
                    log("Adding : " + thisJob.getJobName() + " as an output to file : " + fileChunk.getFilename());
                    fileChunk.addOutJobChunk(thisJob);

                    log("Adding : " + fileChunk.getFilename() + " as an input to job : " + thisJob.getJobName());
                    thisJob.addInFileChunk(fileChunk);

                }
                else{
                    log("FileChunk not found in register");
                }
            }

            else{
                log("Cannot handle input : " + object.getClass().getName());
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
        return thisJob.getUuid();
    }
    private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        //System.out.println(s);
    }
}