package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.griphyn.vdl.dax.ADAG;
import org.griphyn.vdl.dax.Filename;
import org.griphyn.vdl.dax.Job;
import org.griphyn.vdl.dax.PseudoText;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.annotation.CheckboxParameter;
import org.trianacode.taskgraph.annotation.Process;
import org.trianacode.taskgraph.annotation.TextFieldParameter;
import org.trianacode.taskgraph.annotation.Tool;

import javax.swing.*;
import java.awt.*;
import java.io.File;
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

    @CheckboxParameter
    private boolean demo = false;

    @TextFieldParameter
    private String fileName = "output.dax";

    @Process(gather=true)
    public void process(List in){
        log("\nList in is size: " + in.size() + " contains : " + in.toString() + ".\n ");

        DaxRegister register = DaxRegister.getDaxRegister();

        try{
            GUIEnv.getApplicationFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));            
            //  daxFromInList(in);
            daxFromRegister(register);
        }catch(Exception e){
            System.out.println("Failed at something : " + e + "\n\n");
            e.printStackTrace();
        }finally{
            register.clear();
            System.out.println("Cleared register");
            GUIEnv.getApplicationFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
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

            log("\nJob : " + jobChunk.getJobName());
            if(pattern == AUTO_CONNECT){
                log("auto_connect");

                autoConnect(dax, jobChunk);
            }
            if(pattern == SCATTER_CONNECT){
                log("scatter_connect");

                scatterConnect(dax, jobChunk);
            }
            if(pattern == ONE2ONE_CONNECT){
                log("one2one_connect");

                one2oneConnect(dax, jobChunk);
            }
            if(pattern == SPREAD_CONNECT){
                log("spread_connect");

                spreadConnect(dax, jobChunk);
            }

            for(DaxFileChunk fc : jobChunk.getOutFileChunks()){
                fc.resetNextCounter();
            }
            for(DaxFileChunk fc : jobChunk.getInFileChunks()){
                fc.resetNextCounter();
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
                chunk.resetNextCounter();
                if(chunk.isCollection()){
                    for(int m = 0 ; m < chunk.getNumberOfFiles(); m++){
                        log("Job " + job.getID() + " named : "  + job.getName() + " has input : " + chunk.getFilename() + "-" + m);

                        if(chunk.getNamePattern() != null){
                            log("Collection has a naming pattern");
                        }else{
                            log("Collection has no naming pattern, using *append int*");
                        }

                        String filename = chunk.getFilename() + "-" + m;
                        filename = chunk.getNextFilename();
                        job.addUses(new Filename(filename, 1));
                    }
                }
                else{
                    log("Job " + job.getID() + " named : " + job.getName() + " has input : " + chunk.getFilename());
                    job.addUses(new Filename(chunk.getFilename(), 1));
                }
            }

            addOutputs(job, jobChunk);

//            List outFiles = jobChunk.getOutFileChunks();
//            for(int i = 0; i < outFiles.size(); i++){
//                DaxFileChunk chunk = (DaxFileChunk)outFiles.get(i);
//                if(chunk.isCollection()){
//                    for(int m = 0 ; m < chunk.getNumberOfFiles(); m++){
//                        log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + "-" + m);
//
//                        if(chunk.getNamePattern() != null){
//                            log("Collection has a naming pattern");
//                        }else{
//                            log("Collection has no naming pattern, using *append int*");
//                        }
//
//                        job.addUses(new Filename(chunk.getFilename() + "-" + m, 2));
//                    }
//                }
//                else{
//                    log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
//                    job.addUses(new Filename(chunk.getFilename(), 2));
//                }
//            }
            dax.addJob(job);
            log("Added job : " + job.getName() + " to ADAG.");

        }
    }

    private void one2oneOutput(ADAG dax, DaxJobChunk jobChunk){

    }

    private int[] sortSpread(int files, int jobs){
        double numberOfFiles = (double)files;
        double numberOfJobs = (double)jobs;

        double filesLeft = numberOfFiles;
        double jobsLeft = numberOfJobs;

        int[] filesPerJob = new int[(int)jobs];
        for(int i = 0; i < jobs; i++){
            double num =  Math.floor(filesLeft / jobsLeft);
            filesPerJob[i] = (int)num;
            filesLeft = filesLeft - num;
            jobsLeft = jobsLeft -1;
        }

        int count = 0;
        for(int j = 0; j < filesPerJob.length; j++){
            count = count + filesPerJob[j];
            System.out.println("Job : " + j + " will have : " + filesPerJob[j] + " connected.");
        }
        System.out.println("Should be total : " + files + " files. Assigned : " + count);

        return filesPerJob;
    }


    public void spreadConnect(ADAG dax, DaxJobChunk jobChunk){
        int n = 0;
        int numberJobs = jobChunk.getNumberOfJobs();
        Vector<DaxFileChunk> fcs = new Vector<DaxFileChunk>();
        for(DaxFileChunk fc : jobChunk.getInFileChunks()){
            for(int i = 0; i < fc.getNumberOfFiles() ; i++){
                fcs.add(fc);
            }
            fc.resetNextCounter();
        }

        int[] filesPerJob = sortSpread(fcs.size(), jobChunk.getNumberOfJobs());


        //     double numberInputFiles = fcs.size();
        //     double filesPerJob = numberInputFiles/numberJobs;
        //     System.out.println("Files : " + numberInputFiles +
        //             " Jobs : " + numberJobs + ". "
        //             + filesPerJob + " files / job ");

        for (int i = 0; i< numberJobs; i++){

            //  System.out.println("Sorting out job : " + i);
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


            for(int j = 0; j < filesPerJob[i] ; j++){
                if(fcs.size() > 0){
                    java.util.Random rand = new java.util.Random();
                    int r = rand.nextInt(fcs.size());

                    //      System.out.println("Files left : " + fcs.size() + " Getting file : " + r);
                    DaxFileChunk fc = fcs.get(r);
                    fcs.remove(r);
                    //    System.out.println("Got : " + r + " filename : " + fc.getFilename());
                    job.addUses(new Filename(fc.getNextFilename(), 1));
                }
            }

            addOutputs(job, jobChunk);
//
//            List outFiles = jobChunk.getOutFileChunks();
//            for(int j = 0; j < outFiles.size(); j++){
//                DaxFileChunk chunk = (DaxFileChunk)outFiles.get(j);
//                if(chunk.isCollection()){
//                    for(int k = 0 ; k < chunk.getNumberOfFiles(); k++){
//                        log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + "-" + k);
//
//                        if(chunk.getNamePattern() != null){
//                            log("Collection has a naming pattern");
//                        }else{
//                            log("Collection has no naming pattern, using *append int*");
//                        }
//
//                        job.addUses(new Filename(chunk.getFilename() + "-" + k, 2));
//                    }
//                }
//                else{
//                    log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
//                    job.addUses(new Filename(chunk.getFilename(), 2));
//                }
//            }

            System.out.println("Adding job : " + job.getName() + " to dax");
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
            fc.resetNextCounter();
        }
        double numberInputFiles = fcs.size();

        double number = Math.ceil(numberInputFiles / numberInputsPerJob);
        numberJobs = (int)number;
        jobChunk.setNumberOfJobs(numberJobs);
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

            addOutputs(job, jobChunk);

//            List outFiles = jobChunk.getOutFileChunks();
//            for(int j = 0; j < outFiles.size(); j++){
//                DaxFileChunk chunk = (DaxFileChunk)outFiles.get(j);
//                if(chunk.isCollection()){
//                    for(int k = 0 ; k < chunk.getNumberOfFiles(); k++){
//                        log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + "-" + k);
//
//                        if(chunk.getNamePattern() != null){
//                            log("Collection has a naming pattern");
//                        }else{
//                            log("Collection has no naming pattern, using *append int*");
//                        }
//
//                        job.addUses(new Filename(chunk.getFilename() + "-" + k, 2));
//                    }
//                }
//                else{
//                    log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
//                    job.addUses(new Filename(chunk.getFilename(), 2));
//                }
//            }

            dax.addJob(job);
        }

    }

    private void one2oneConnect(ADAG dax, DaxJobChunk jobChunk){
        int n = 0;
        int m = 0;
        for(DaxFileChunk fc : jobChunk.getInFileChunks()){
            fc.resetNextCounter();

            log("Job has " + fc.getNumberOfFiles() + " inputs.");
            if(fc.getNumberOfFiles() > jobChunk.getNumberOfJobs()){
                jobChunk.setNumberOfJobs(fc.getNumberOfFiles());
            }


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
                fileName = fc.getNextFilename();
                log("Job has " + fileName + " as an input.");
                job.addUses(new Filename(fileName, 1));

                addOutputs(job, jobChunk);

//                List outFiles = jobChunk.getOutFileChunks();
//                for(int j = 0; j < outFiles.size(); j++){
//                    DaxFileChunk chunk = (DaxFileChunk)outFiles.get(j);
//                    if(chunk.isCollection()){
//                        for(int k = 0 ; k < chunk.getNumberOfFiles(); k++){
//                            log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + "-" + k);
//
//                            if(chunk.getNamePattern() != null){
//                                log("Collection has a naming pattern");
//                            }else{
//                                log("Collection has no naming pattern, using *append int*");
//                            }
//
//                            job.addUses(new Filename(chunk.getFilename() + "-" + k, 2));
//                        }
//                    }
//                    else{
//                        log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
//                        job.addUses(new Filename(chunk.getFilename(), 2));
//                    }
//                }
                dax.addJob(job);
                log("Added job : " + job.getName() + " to ADAG.");
            }
        }
    }

    private void addOutputs(Job job, DaxJobChunk jobChunk){
        List outFiles = jobChunk.getOutFileChunks();
        for(int j = 0; j < outFiles.size(); j++){
            DaxFileChunk chunk = (DaxFileChunk)outFiles.get(j);
            log("Job has " + chunk.getNumberOfFiles() + " outputs.");
            if(chunk.isCollection()){
                log("Jobs output file is a collection");
                if(chunk.isOne2one()){

                    log("Building one2one output");
                    //                   chunk.setOne2one(true);
                    chunk.setNumberOfFiles(jobChunk.getNumberOfJobs());

                    log("File " + chunk.getFilename() + " duplication set to " + chunk.getNumberOfFiles());
                    if(chunk.getNamePattern() != null){
                        log("Collection has a naming pattern");
                    }else{
                        log("Collection has no naming pattern, using *append int*");
                    }

                    String filename = chunk.getNextFilename();
                    log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + filename);
                    job.addUses(new Filename(filename, 2));
                }
                else{
                    log("Not a one2one");
                    for(int k = 0 ; k < chunk.getNumberOfFiles(); k++){

//                        if(chunk.getNamePattern() != null){
//                            log("Collection has a naming pattern");
//                        }else{
//                            log("Collection has no naming pattern, using *append int*");
//                        }
                        String filename = chunk.getNextFilename();
                        log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + filename);

                        job.addUses(new Filename(filename, 2));

                    }
                }
            }
            else{
                log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
                job.addUses(new Filename(chunk.getFilename(), 2));
            }
        }
    }



    private void writeDax(ADAG dax){
        log("\nADAG has " + dax.getJobCount() + " jobs in.");

        if(dax.getJobCount() > 0 ){

            FileWriter fw = null;
            try {
                fw = new FileWriter(fileName);
                dax.toXML(fw, "", null );
                log("File " + fileName + " saved.\n");
            } catch (IOException e){
                e.printStackTrace();
            } finally{
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else{
            log("No jobs in DAX, will not create file. (Avoids overwrite)");
        }

        if(demo){
            log("Displaying demo");
            DaxReader dr = new DaxReader();
            try {
                TaskGraph t = dr.importWorkflow(new File(fileName));
                TaskGraph tg = GUIEnv.getApplicationFrame().addParentTaskGraphPanel(t);
            } catch (Exception e) {
                log("Error opening *" + fileName + "* demo taskgraph : " + e);
                e.printStackTrace();
            }
        }else{
            log("Not displaying demo");
        }
        GUIEnv.getApplicationFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));        
        JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(), "Dax saved : " + fileName);

    }


    private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }
}
