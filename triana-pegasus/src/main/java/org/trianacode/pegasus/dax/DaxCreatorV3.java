package org.trianacode.pegasus.dax;


import edu.isi.pegasus.planner.dax.ADAG;
import edu.isi.pegasus.planner.dax.Executable;
import edu.isi.pegasus.planner.dax.File;
import edu.isi.pegasus.planner.dax.Job;
import org.apache.commons.logging.Log;
import org.trianacode.annotation.*;
import org.trianacode.annotation.Process;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Jan 17, 2011
 * Time: 9:27:14 PM
 * To change this template use File | Settings | File Templates.
 * <p/>
 * DO NOT USE org.griphyn FOR THIS CLASS
 * use isi.org.trianacode.pegasus.gui. for v3.0.1 compatibility.
 */

@Tool //(panelClass = "org.trianacode.org.trianacode.pegasus.gui.dax.DaxCreatorV3Panel")
public class DaxCreatorV3 implements TaskConscious {
    private static final int AUTO_CONNECT = 0;
    private static final int SCATTER_CONNECT = 1;
    private static final int ONE2ONE_CONNECT = 2;
    private static final int SPREAD_CONNECT = 3;
    public int idNumber = 0;
    private Task task;

    private ArrayList<String> PFLarray = new ArrayList<String>();

    @CheckboxParameter
    public boolean demo = false;

    @TextFieldParameter
    private String fileName = "output";

    @Process(gather = true)
    public java.io.File process(List in) {
        log("\nList in is size: " + in.size() + " contains : " + in.toString() + ".\n ");

        DaxRegister register = DaxRegister.getDaxRegister();

        java.io.File daxFile = null;
        //       ApplicationFrame frame = GUIEnv.getApplicationFrame();
        try {
            //         if(frame != null){
            //           frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            //     }
            //  daxFromInList(in);
            daxFile = daxFromRegister(register);
        } catch (Exception e) {
            log("Failed at something : " + e + "\n\n");
            e.printStackTrace();
        } finally {
            register.clear();
            log("Cleared register");
            //         if(frame != null){
            //             frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            //         }
        }
        return daxFile;
    }

    @CustomGUIComponent
    public Component getComponent(){
        return new JLabel("This is a non-gui tool. Use the triana-pegasus-gui toolbox for more options.");
    }

    private void daxFromInList(List in) {

        //needs to be in the new isi.org.trianacode.pegasus.gui
        // NOT org.griphyn

        ADAG dax = new ADAG(fileName);

        for (int j = 0; j < in.size(); j++) {
            Object o = in.get(j);
            if (o instanceof DaxJobChunk) {
                DaxJobChunk inChunk = (DaxJobChunk) o;

                if (!inChunk.isStub()) {
                    String jobName = "Process";
                    String id = "0000000" + (j + 1);
                    id = ("ID" + id.substring(id.length() - 7));
                    Job job = new Job(id, jobName);
                    job.addArgument(inChunk.getJobArgs());


                    List inFiles = inChunk.getInFiles();
                    for (int i = 0; i < inFiles.size(); i++) {
                        job.uses(new File((String) inFiles.get(i)), File.LINK.input);

                    }
                    List outFiles = inChunk.getOutFiles();
                    for (int i = 0; i < outFiles.size(); i++) {
                        job.uses(new File((String) outFiles.get(i)), File.LINK.output);
                    }
                    dax.addJob(job);
                    log("Added a job to ADAG.");
                } else {
                    log("Found a job stub, ignoring..");

                }
            } else {
                log("*** Found something that wasn't a DaxJobChunk in input List ***");
            }
        }

        writeDax(dax);
    }

    private java.io.File daxFromRegister(DaxRegister register) {
        //      register.listAll();
        idNumber = 0;
        ADAG dax = new ADAG(fileName);

        HashMap<String, Executable> execs = new HashMap<String, Executable>();

        List<DaxJobChunk> jobChunks = register.getJobChunks();
        int idNumber = 0;
        for (int j = 0; j < jobChunks.size(); j++) {
            DaxJobChunk jobChunk = jobChunks.get(j);
            System.out.println("******** " + jobChunk.getArgBuilder().getArgString());
            int pattern = jobChunk.getConnectPattern();

            log("\nJob : " + jobChunk.getJobName());
            if (pattern == AUTO_CONNECT) {
                log("auto_connect");

                autoConnect(dax, jobChunk);
            }
            if (pattern == SCATTER_CONNECT) {
                log("scatter_connect");

                scatterConnect(dax, jobChunk);
            }
            if (pattern == ONE2ONE_CONNECT) {
                log("one2one_connect");

                one2oneConnect(dax, jobChunk);
            }
            if (pattern == SPREAD_CONNECT) {
                log("spread_connect");

                spreadConnect(dax, jobChunk);
            }

            for (DaxFileChunk fc : jobChunk.getOutFileChunks()) {
                fc.resetNextCounter();
            }
            for (DaxFileChunk fc : jobChunk.getInFileChunks()) {
                fc.resetNextCounter();
            }

            Executable exec = new Executable(fileName, jobChunk.getJobName(), "1.0");
            exec.setArchitecture(Executable.ARCH.X86).setOS(Executable.OS.LINUX);
            exec.setInstalled(true);
            exec.addPhysicalFile("file:///home/triana-pegasus/pegasus-wms-3.0.1/bin/keg", "condorpool");
            execs.put(jobChunk.getJobName(), exec);

        }
        for (Executable ex : execs.values()) {
            dax.addExecutable(ex);
        }

        log("\nFound files : " + PFLarray.toString());

        return writeDax(dax);

    }

    private void autoConnect(ADAG dax, DaxJobChunk jobChunk) {
        log("Job " + jobChunk.getJobName() + " has " + jobChunk.getNumberOfJobs() + " jobs.");

        /**
         * Create a number of dax job objects, adding -xx if there is > 1 required.
         */
        for (int n = 0; n < jobChunk.getNumberOfJobs(); n++) {
            String jobName = jobChunk.getJobName();
            if (jobChunk.getNumberOfJobs() > 1) {
                jobName = (jobName + "-" + n);
            }
            idNumber++;
            String id = "0000000" + (idNumber);
            id = ("ID" + id.substring(id.length() - 7));

            Job job = new Job(id, fileName, jobName, "1.0");
            System.out.println("args :" + jobChunk.getJobArgs());
            job.addArgument(jobChunk.getJobArgs());


            jobChunk.listChunks();
            List inFiles = jobChunk.getInFileChunks();
            for (int i = 0; i < inFiles.size(); i++) {
                DaxFileChunk chunk = (DaxFileChunk) inFiles.get(i);
                chunk.resetNextCounter();
                if (chunk.isCollection()) {
                    for (int m = 0; m < chunk.getNumberOfFiles(); m++) {
                        //                   log("Job " + job.getId() + " named : "  + job.getName() + " has input : " + chunk.getFilename() + "-" + m);

                        if (chunk.getNamePattern() != null) {
                            log("Collection has a naming pattern");
                        } else {
                            log("Collection has no naming pattern, using *append int*");
                        }

                        //    String filename = chunk.getNextFilename();
                        String filename = chunk.getFilename();
                        String fileLocation = chunk.getFileLocation();
                        String fileProtocol = chunk.getFileProtocol();
                        File file = new File(filename);

                        if (!fileLocation.equals("") && !fileProtocol.equals("")) {
                            PFLarray.add(fileLocation + java.io.File.separator + filename);
                            file.addPhysicalFile(fileProtocol + fileLocation + java.io.File.separator + filename, "condorpool");
                            dax.addFile(file);

                        }
                        job.uses(file, File.LINK.input);
                        job.addArgument("-i ").addArgument(file);

                    }
                    chunk.resetNextCounter();

                } else {
//                   log("Job " + job.getId() + " named : " + job.getName() + " has input : " + chunk.getFilename());
//                    job.uses(new File(chunk.getFilename()), File.LINK.input);
//                    String fileLocation = chunk.getFileLocation();
//                    if(!fileLocation.equals("")){
//                        PFLarray.add(fileLocation + java.io.File.separator + chunk.getFilename());
//                    }

                    String filename = chunk.getFilename();
                    String fileLocation = chunk.getFileLocation();
                    String fileProtocol = chunk.getFileProtocol();
                    File file = new File(filename);

                    if (!fileLocation.equals("") && !fileProtocol.equals("")) {
                        PFLarray.add(fileLocation + java.io.File.separator + filename);
                        file.addPhysicalFile(fileProtocol + fileLocation + java.io.File.separator + filename, "condorpool");
                        dax.addFile(file);

                    }
                    job.uses(file, File.LINK.input);
                    job.addArgument("-i ").addArgument(file);

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

    private void one2oneOutput(ADAG dax, DaxJobChunk jobChunk) {

    }

    private int[] sortSpread(int files, int jobs) {
        double numberOfFiles = (double) files;
        double numberOfJobs = (double) jobs;

        double filesLeft = numberOfFiles;
        double jobsLeft = numberOfJobs;

        int[] filesPerJob = new int[(int) jobs];
        for (int i = 0; i < jobs; i++) {
            double num = Math.floor(filesLeft / jobsLeft);
            filesPerJob[i] = (int) num;
            filesLeft = filesLeft - num;
            jobsLeft = jobsLeft - 1;
        }

        int count = 0;
        for (int j = 0; j < filesPerJob.length; j++) {
            count = count + filesPerJob[j];
            log("Job : " + j + " will have : " + filesPerJob[j] + " connected.");
        }
        log("Should be total : " + files + " files. Assigned : " + count);

        return filesPerJob;
    }


    public void spreadConnect(ADAG dax, DaxJobChunk jobChunk) {
        int n = 0;
        int numberJobs = jobChunk.getNumberOfJobs();
        Vector<DaxFileChunk> fcs = new Vector<DaxFileChunk>();
        for (DaxFileChunk fc : jobChunk.getInFileChunks()) {
            for (int i = 0; i < fc.getNumberOfFiles(); i++) {
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

        for (int i = 0; i < numberJobs; i++) {

            //  System.out.println("Sorting out job : " + i);
            idNumber++;

            String jobName = jobChunk.getJobName();
            if (numberJobs > 1) {
                jobName = (jobName + "-" + n);
                log("Jobs name is : " + jobName);
                n++;
            }
            String id = "0000000" + (idNumber);
            id = ("ID" + id.substring(id.length() - 7));
            Job job = new Job(id, jobName);
            job.addArgument(jobChunk.getJobArgs());


            for (int j = 0; j < filesPerJob[i]; j++) {
                if (fcs.size() > 0) {
                    java.util.Random rand = new java.util.Random();
                    int r = rand.nextInt(fcs.size());

                    DaxFileChunk fc = fcs.get(r);
                    fcs.remove(r);
//                    job.uses(new File(fc.getNextFilename()), File.LINK.input);

                    job.uses(new File(fc.getFilename()), File.LINK.input);
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

            log("Adding job : " + job.getName() + " to dax");
            dax.addJob(job);
        }

    }


    private void scatterConnect(ADAG dax, DaxJobChunk jobChunk) {
        int n = 0;
        int numberJobs = 0;
        double numberInputsPerJob = jobChunk.getFileInputsPerJob();
        Vector<DaxFileChunk> fcs = new Vector<DaxFileChunk>();
        for (DaxFileChunk fc : jobChunk.getInFileChunks()) {
            for (int i = 0; i < fc.getNumberOfFiles(); i++) {
                fcs.add(fc);
            }
            fc.resetNextCounter();
        }
        double numberInputFiles = fcs.size();

        double number = Math.ceil(numberInputFiles / numberInputsPerJob);
        numberJobs = (int) number;
        jobChunk.setNumberOfJobs(numberJobs);
        log("Double is : " + number + " Number is : " + numberJobs);
        log("Files : " + numberInputFiles +
                " Files/job : " + numberInputsPerJob + ". "
                + numberJobs + " duplicates of " + jobChunk.getJobName());

        int offset = 0;
        for (int i = 0; i < numberJobs; i++) {
            log("Sorting out job : " + i);
            idNumber++;

            String jobName = jobChunk.getJobName();
            if (numberJobs > 1) {
                jobName = (jobName + "-" + n);
                log("Jobs name is : " + jobName);
                n++;
            }
            String id = "0000000" + (idNumber);
            id = ("ID" + id.substring(id.length() - 7));
            Job job = new Job(id, jobName);
            job.addArgument(jobChunk.getJobArgs());

            for (int j = offset; j < (offset + numberInputsPerJob); j++) {
                if (j < numberInputFiles) {
                    DaxFileChunk fc = fcs.get(j);
//                    String filename = fc.getNextFilename();
                    String filename = fc.getFilename();

                    log("Adding file : " + filename + " to job : " + i +
                            " (Job : " + j + " of " + numberInputFiles + ")");

                    job.uses(new File(filename), File.LINK.input);
                }
            }
            offset = offset + (int) numberInputsPerJob;

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

    private void one2oneConnect(ADAG dax, DaxJobChunk jobChunk) {
        int n = 0;
        int m = 0;
        List<DaxFileChunk> fcs = jobChunk.getInFileChunks();
        DaxFileChunk pfc = null;
        if (fcs.size() > 1) {
            pfc = (DaxFileChunk) PrimaryFilePanel.getValue(jobChunk.getJobName(), fcs);
        } else {
            pfc = fcs.get(0);
        }

        DaxFileChunk fc = pfc;
        fc.resetNextCounter();

        log("Job has " + fc.getNumberOfFiles() + " inputs.");
        if (fc.getNumberOfFiles() > jobChunk.getNumberOfJobs()) {
            jobChunk.setNumberOfJobs(fc.getNumberOfFiles());
        }

        for (int i = 0; i < fc.getNumberOfFiles(); i++) {
            idNumber++;

            String jobName = jobChunk.getJobName();
            if (jobChunk.getNumberOfJobs() > 1) {
                jobName = (jobName + "-" + n);
                n++;
            }
            String id = "0000000" + (idNumber);
            id = ("ID" + id.substring(id.length() - 7));
            Job job = new Job(id, jobName);
            job.addArgument(jobChunk.getJobArgs());

//            String fileName = fc.getFilename();
//            if(fc.getNumberOfFiles() > 1){
//                fileName = (fileName + "-" + m);
//                m++;
//            }
            //       fileName = fc.getNextFilename();
            String fileName = fc.getFilename();

            log("Job has " + fileName + " as an input.");
            job.uses(new File(fileName), File.LINK.input);

            for (DaxFileChunk dfc : fcs) {
                if (dfc != pfc) {
                    if (dfc.isCollection()) {
                        for (int j = 0; j < dfc.getNumberOfFiles(); j++) {
//                            job.uses(new File(dfc.getNextFilename()), File.LINK.input);
                            job.uses(new File(dfc.getFilename()), File.LINK.input);

                        }
                        dfc.resetNextCounter();
                    } else {
                        job.uses(new File(dfc.getFilename()), File.LINK.input);
                    }
                }
            }

            addOutputs(job, jobChunk);

            dax.addJob(job);
            log("Added job : " + job.getName() + " to ADAG.");
        }

    }

    private void addOutputs(Job job, DaxJobChunk jobChunk) {
        List outFiles = jobChunk.getOutFileChunks();
        for (int j = 0; j < outFiles.size(); j++) {
            DaxFileChunk chunk = (DaxFileChunk) outFiles.get(j);
            log("Job has " + chunk.getNumberOfFiles() + " outputs.");
            if (chunk.isCollection()) {
                chunk.resetNextCounter();
                log("Jobs output file is a collection");
                if (chunk.isOne2one()) {

                    log("Building one2one output");
                    //                   chunk.setOne2one(true);
                    chunk.setNumberOfFiles(jobChunk.getNumberOfJobs());

                    //                log("File " + chunk.getFilename() + " duplication set to " + chunk.getNumberOfFiles());
                    if (chunk.getNamePattern() != null) {
                        log("Collection has a naming pattern");
                    } else {
                        log("Collection has no naming pattern, using *append int*");
                    }
//                    String filename = chunk.getNextFilename();

                    String filename = chunk.getFilename();
                    log("Job " + job.getId() + " named : " + job.getName() + " has output : " + filename);
                    job.uses(new File(filename), File.LINK.output);
                } else {
                    log("Not a one2one");
                    for (int k = 0; k < chunk.getNumberOfFiles(); k++) {

//                        if(chunk.getNamePattern() != null){
//                            log("Collection has a naming pattern");
//                        }else{
//                            log("Collection has no naming pattern, using *append int*");
//                        }

//                        String filename = chunk.getNextFilename();

                        String filename = chunk.getFilename();
                        log("Job " + job.getId() + " named : " + job.getName() + " has output : " + filename);

                        job.uses(new File(filename), File.LINK.output);

                    }
                }
            } else {
                //           log("Job " + job.getId() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
                File outputFile = new File(chunk.getFilename());
                job.uses(outputFile, File.LINK.output);
                job.addArgument("-o ").addArgument(outputFile);
            }
        }
    }


    private java.io.File writeDax(ADAG dax) {
        dax.writeToFile(fileName + ".dax");
        log("File " + fileName + " saved.\n");

        java.io.File daxFile = new java.io.File(fileName + ".dax");





        //Cursor change
//        ApplicationFrame frame = GUIEnv.getApplicationFrame();
//        if(frame != null){
//            frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//            JOptionPane.showMessageDialog(frame, "Dax saved : " + fileName);
//        }
        return daxFile;
    }


    private void log(String s) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }

}
