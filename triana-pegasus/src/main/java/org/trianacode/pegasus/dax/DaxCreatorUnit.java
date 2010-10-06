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
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 24, 2010
 * Time: 1:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

@Tool(panelClass="org.trianacode.pegasus.dax.DaxCreatorPanel")
public class DaxCreatorUnit {
    private static final int AUTO_CONNECT = 0;
    private static final int SCATTER_CONNECT = 1;
    private static final int ONE2ONE_CONNECT = 2;
    private static final int SPREAD_CONNECT = 3;
    public int idNumber = 0;

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
        idNumber = 0;
        ADAG dax = new ADAG();

        List<DaxJobChunk> jobChunks = register.getJobChunks();
        int idNumber = 0;
        for(int j = 0; j < jobChunks.size(); j++){
            DaxJobChunk jobChunk = jobChunks.get(j);
            int pattern = jobChunk.getConnectPattern();

            if(pattern == AUTO_CONNECT){
                autoConnect(dax, jobChunk);
            }
            if(pattern == SCATTER_CONNECT){
                scatterConnect(dax, jobChunk);
            }
            if(pattern == ONE2ONE_CONNECT){
                one2oneConnect(dax, jobChunk);
            }
            if(pattern == SPREAD_CONNECT){
                spreadConnect(dax, jobChunk);
            }

        }
        writeDax(dax);

    }

    private void autoConnect(ADAG dax, DaxJobChunk jobChunk){
        log("Job " + jobChunk.getJobName() + " has " + jobChunk.getNumberOfJobs() + " jobs.");
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
    }

    public void spreadConnect(ADAG dax, DaxJobChunk jobChunk){
        int n = 0;
        int numberJobs = jobChunk.getNumberOfJobs();
        Vector<DaxFileChunk> fcs = new Vector<DaxFileChunk>();
        for(DaxFileChunk fc : jobChunk.getInFileChunks()){
            for(int i = 0; i < fc.getNumberOfFiles() ; i++){
                fcs.add(fc);
            }
        }
        double numberInputFiles = fcs.size();
        double filesPerJob = numberInputFiles/numberJobs;

        for (int i = 0; i< numberJobs; i++){

            System.out.println("Sorting out job : " + i);
            Job job = new Job();
            idNumber ++;
            job.addArgument(new PseudoText(jobChunk.getJobArgs()));

            String jobName = jobChunk.getJobName();
            if(numberJobs > 1){
                jobName = (jobName + "-" + n);
                System.out.println("Jobs name is : " + jobName);
                n++;
            }
            job.setName(jobName);
            String id = "0000000" + (idNumber);
            job.setID("ID" + id.substring(id.length() - 7));


            for(int j = 0; j < filesPerJob ; j++){
                int r = (int)(Math.random() * fcs.size());
                DaxFileChunk fc = fcs.get(r);
                fcs.remove(r);
                System.out.println("Got : " + r + " filename : " + fc.getFilename());
                job.addUses(new Filename(fc.getNextFilename(), 1));
            }

            List outFiles = jobChunk.getOutFileChunks();
            for(int j = 0; j < outFiles.size(); j++){
                DaxFileChunk chunk = (DaxFileChunk)outFiles.get(j);
                if(chunk.isCollection()){
                    for(int k = 0 ; k < chunk.getNumberOfFiles(); k++){
                        log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + "-" + k);

                        if(chunk.getNamePattern() != null){
                            log("Collection has a naming pattern");
                        }else{
                            log("Collection has no naming pattern, using *append int*");
                        }

                        job.addUses(new Filename(chunk.getFilename() + "-" + k, 2));
                    }
                }
                else{
                    log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
                    job.addUses(new Filename(chunk.getFilename(), 2));
                }
            }

            dax.addJob(job);
        }

    }


    private void scatterConnect(ADAG dax, DaxJobChunk jobChunk){
        int n = 0;
        int numberJobs = 0;
        double numberInputsPerJob = jobChunk.getFileInputsPerJob();
        Vector<DaxFileChunk> fcs = new Vector<DaxFileChunk>();
        for(DaxFileChunk fc : jobChunk.getInFileChunks()){
            for(int i = 0; i < fc.getNumberOfFiles() ; i++){
                fcs.add(fc);
            }
        }
        double numberInputFiles = fcs.size();

        double number = Math.ceil(numberInputFiles / numberInputsPerJob);
        numberJobs = (int)number;
        System.out.println("Double is : " + number  + " Number is : " + numberJobs);
        System.out.println("Files : " + numberInputFiles +
                " Files/job : " + numberInputsPerJob + ". "
                + numberJobs + " duplicates of " + jobChunk.getJobName());

        int offset = 0;
        for(int i = 0; i < numberJobs ; i++){
            System.out.println("Sorting out job : " + i);
            Job job = new Job();
            idNumber ++;
            job.addArgument(new PseudoText(jobChunk.getJobArgs()));

            String jobName = jobChunk.getJobName();
            if(numberJobs > 1){
                jobName = (jobName + "-" + n);
                System.out.println("Jobs name is : " + jobName);
                n++;
            }
            job.setName(jobName);
            String id = "0000000" + (idNumber);
            job.setID("ID" + id.substring(id.length() - 7));

            for(int j = offset; j < (offset + numberInputsPerJob); j++){
                if(j < numberInputFiles){
                    DaxFileChunk fc = fcs.get(j);
                    String filename = fc.getNextFilename();
                    System.out.println("Adding file : " + filename + " to job : " + i +
                            " (Job : " + j + " of " + numberInputFiles + ")");

                    job.addUses(new Filename(filename,1));
                }
            }
            offset = offset + (int)numberInputsPerJob;

            List outFiles = jobChunk.getOutFileChunks();
            for(int j = 0; j < outFiles.size(); j++){
                DaxFileChunk chunk = (DaxFileChunk)outFiles.get(j);
                if(chunk.isCollection()){
                    for(int k = 0 ; k < chunk.getNumberOfFiles(); k++){
                        log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + "-" + k);

                        if(chunk.getNamePattern() != null){
                            log("Collection has a naming pattern");
                        }else{
                            log("Collection has no naming pattern, using *append int*");
                        }

                        job.addUses(new Filename(chunk.getFilename() + "-" + k, 2));
                    }
                }
                else{
                    log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
                    job.addUses(new Filename(chunk.getFilename(), 2));
                }
            }

            dax.addJob(job);
        }

    }

    private void one2oneConnect(ADAG dax, DaxJobChunk jobChunk){
        int n = 0;
        int m = 0;
        for(DaxFileChunk fc : jobChunk.getInFileChunks()){
            for(int i = 0; i < fc.getNumberOfFiles(); i++){
                Job job = new Job();
                idNumber ++;
                job.addArgument(new PseudoText(jobChunk.getJobArgs()));

                String jobName = jobChunk.getJobName();
                if(jobChunk.getNumberOfJobs() > 1){
                    jobName = (jobName + "-" + n);
                    n++;
                }
                job.setName(jobName);
                String id = "0000000" + (idNumber);
                job.setID("ID" + id.substring(id.length() - 7));

                String fileName = fc.getFilename();
                if(fc.getNumberOfFiles() > 1){
                    fileName = (fileName + "-" + m);
                    m++;
                }

                job.addUses(new Filename(fileName, 1));

                List outFiles = jobChunk.getOutFileChunks();
                for(int j = 0; j < outFiles.size(); j++){
                    DaxFileChunk chunk = (DaxFileChunk)outFiles.get(j);
                    if(chunk.isCollection()){
                        for(int k = 0 ; k < chunk.getNumberOfFiles(); k++){
                            log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + "-" + k);

                            if(chunk.getNamePattern() != null){
                                log("Collection has a naming pattern");
                            }else{
                                log("Collection has no naming pattern, using *append int*");
                            }

                            job.addUses(new Filename(chunk.getFilename() + "-" + k, 2));
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
        }
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
