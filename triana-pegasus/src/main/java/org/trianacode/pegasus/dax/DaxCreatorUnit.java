package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.griphyn.vdl.dax.ADAG;
import org.griphyn.vdl.dax.Filename;
import org.griphyn.vdl.dax.Job;
import org.griphyn.vdl.dax.PseudoText;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.annotation.Process;
import org.trianacode.taskgraph.annotation.TextFieldParameter;
import org.trianacode.taskgraph.annotation.Tool;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 24, 2010
 * Time: 1:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

@Tool(panelClass="org.trianacode.pegasus.dax.DaxCreatorPanel")
public class DaxCreatorUnit {

    @TextFieldParameter
    private String fileName = "output.dax";

    @Process(gather=true)
    public void process(List in){
        log("\nList in is size: " + in.size() + " contains : " + in.toString() + ".\n ");

        DaxRegister register = DaxRegister.getDaxRegister();

        //  daxFromInList(in);
        daxFromRegister(register);

        register.clear();

    }

    private void daxFromInList(List in){
        ADAG dax = new ADAG();

        for(int j = 0; j < in.size(); j++){
            Object o = in.get(j);
            if(o instanceof DaxJobChunk){
                DaxJobChunk inChunk = (DaxJobChunk)o;

                if(!inChunk.isStub()){
                    Job job = new Job();
                    job.addArgument(new PseudoText(inChunk.getJobArgs()));
                    job.setName("Process");
                    String id = "0000000" + (j+1);
                    job.setID("ID" + id.substring(id.length() - 7));

                    List inFiles = inChunk.getInFiles();
                    for(int i = 0; i < inFiles.size(); i++){
                        job.addUses(new Filename((String)inFiles.get(i), 1));
                    }
                    List outFiles = inChunk.getOutFiles();
                    for(int i = 0; i < outFiles.size(); i++){
                        job.addUses(new Filename((String)outFiles.get(i), 2));
                    }
                    dax.addJob(job);
                    log("Added a job to ADAG.");
                }
                else{
                    log("Found a job stub, ignoring..");

                }
            }
            else{
                log("*** Found something that wasn't a DaxJobChunk in input List ***");
            }
        }

        writeDax(dax);
    }

    private void daxFromRegister(DaxRegister register){
        //      register.listAll();

        ADAG dax = new ADAG();

        List<DaxJobChunk> jobChunks = register.getJobChunks();
        int idNumber = 0;
        for(int j = 0; j < jobChunks.size(); j++){
            DaxJobChunk jobChunk = jobChunks.get(j);

     //       if(jobChunk.isCollection()){
            System.out.println("Job " + jobChunk.getJobName() + " has " + jobChunk.getNumberOfJobs() + " jobs.");
                for(int n = 0; n < jobChunk.getNumberOfJobs(); n++){
                    Job job = new Job();
                    idNumber ++;
                    job.addArgument(new PseudoText(jobChunk.getJobArgs()));
                    String jobName = jobChunk.getJobName();
                    if(jobChunk.getNumberOfJobs() > 1){
                        jobName = (jobName + "-" + n);
                    }
                    job.setName(jobName);
                    String id = "0000000" + (idNumber);
                    job.setID("ID" + id.substring(id.length() - 7));

                    jobChunk.listChunks();

                    List inFiles = jobChunk.getInFileChunks();
                    for(int i = 0; i < inFiles.size(); i++){
                        DaxFileChunk chunk = (DaxFileChunk)inFiles.get(i);
                        if(chunk.isCollection()){
                            for(int m = 0 ; m < chunk.getNumberOfFiles(); m++){
                                log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + "-" + m);

                                if(chunk.getNamePattern() != null){
                                    log("Collection has a naming pattern");
                                }else{
                                    log("Collection has no naming pattern, using *append int*");
                                }

                                job.addUses(new Filename(chunk.getFilename() + "-" + m, 1));
                            }
                        }
                        else{
                            log("Job " + job.getID() + " named : " + job.getName() + " has input : " + chunk.getFilename());
                            job.addUses(new Filename(chunk.getFilename(), 1));
                        }
                    }

                    List outFiles = jobChunk.getOutFileChunks();
                    for(int i = 0; i < outFiles.size(); i++){
                        DaxFileChunk chunk = (DaxFileChunk)outFiles.get(i);
                        if(chunk.isCollection()){
                            for(int m = 0 ; m < chunk.getNumberOfFiles(); m++){
                                log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + "-" + m);

                                if(chunk.getNamePattern() != null){
                                    log("Collection has a naming pattern");
                                }else{
                                    log("Collection has no naming pattern, using *append int*");
                                }

                                job.addUses(new Filename(chunk.getFilename() + "-" + m, 2));
                            }
                        }
                        else{
                            log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
                            job.addUses(new Filename(chunk.getFilename(), 2));
                        }
                    }
                    dax.addJob(job);
                    log("Added job : " + job.getName() + " to ADAG.");

                }

 //           }
 //           else{

//                Job job = new Job();
//                job.addArgument(new PseudoText(jobChunk.getJobArgs()));
//                job.setName(jobChunk.getJobName());
//                String id = "0000000" + (j+1);
//                job.setID("ID" + id.substring(id.length() - 7));
//
//                jobChunk.listChunks();
//
//                List inFiles = jobChunk.getInFileChunks();
//                for(int i = 0; i < inFiles.size(); i++){
//                    DaxFileChunk chunk = (DaxFileChunk)inFiles.get(i);
//                    if(chunk.isCollection()){
//                        for(int m = 0 ; m < chunk.getNumberOfFiles(); m++){
//                            log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + m);
//
//                            if(chunk.getNamePattern() != null){
//                                log("Collection has a naming pattern");
//                            }else{
//                                log("Collection has no naming pattern, using *append int*");
//                            }
//
//                            job.addUses(new Filename(chunk.getFilename() + "-" + m, 1));
//                        }
//                    }
//                    else{
//                        log("Job " + job.getID() + " named : " + job.getName() + " has input : " + chunk.getFilename());
//                        job.addUses(new Filename(chunk.getFilename(), 1));
//                    }
//                }
//
//                List outFiles = jobChunk.getOutFileChunks();
//                for(int i = 0; i < outFiles.size(); i++){
//                    DaxFileChunk chunk = (DaxFileChunk)outFiles.get(i);
//                    if(chunk.isCollection()){
//                        for(int m = 0 ; m < chunk.getNumberOfFiles(); m++){
//                            log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + m);
//
//                            if(chunk.getNamePattern() != null){
//                                log("Collection has a naming pattern");
//                            }else{
//                                log("Collection has no naming pattern, using *append int*");
//                            }
//
//                            job.addUses(new Filename(chunk.getFilename() + "-" + m, 2));
//                        }
//                    }
//                    else{
//                        log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
//                        job.addUses(new Filename(chunk.getFilename(), 2));
//                    }
//                }
//                dax.addJob(job);
//                log("Added job : " + jobChunk.getJobName() + " to ADAG.");
//            }
        }

        writeDax(dax);

    }

    private void writeDax(ADAG dax){
        log("ADAG has " + dax.getJobCount() + " jobs in.");

        if(dax.getJobCount() > 0 ){

            try {
                FileWriter fw = new FileWriter(fileName);
                dax.toXML(fw, "", null );
                fw.close();
                log("File " + fileName + " saved.\n");
            } catch (IOException e){
                e.printStackTrace();
            }

        }else{
            log("No jobs in DAX, will not create file. (Avoids overwrite)");
        }
    }


    private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }
}
