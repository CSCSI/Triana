package org.trianacode.shiwaall.dax;


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
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Jan 17, 2011
 * Time: 9:27:14 PM
 * To change this template use File | Settings | File Templates.
 * <p/>
 */

@Tool //(panelClass = "org.trianacode.org.trianacode.shiwaall.gui.dax.DaxCreatorV3Panel")
public class DaxCreatorV3 implements TaskConscious {
    private static final int AUTO_CONNECT = 0;
    private static final int SCATTER_CONNECT = 1;
    private static final int ONE2ONE_CONNECT = 2;
    private static final int SPREAD_CONNECT = 3;
    public int idNumber = 0;
    public Task task;

    String namespace = "";

    Log devLog = Loggers.DEV_LOGGER;

//    private ArrayList<String> PFLarray;

    @CheckboxParameter
    public boolean demo = false;

    @CheckboxParameter
    public boolean publish = true;

    @TextFieldParameter
    public String fileName = "output.xml";

    private HashSet<File> filesForDax = new HashSet<File>();
    private HashMap<String, Job> jobHashMap = new HashMap<String, Job>();

    @Process(gather = true)
    public java.io.File process(List in) {
//        PFLarray = new ArrayList<String>();
        devLog.debug("\nList in is size: " + in.size() + " contains : " + in.toString() + ".\n ");

        if(task.getParameter("fileName") != null){
            fileName = (String) task.getParameter("fileName");
        }

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
            devLog.debug("Failed at something : " + e + "\n\n");
            e.printStackTrace();
        } finally {
            register.clear();
            devLog.debug("Cleared register");
            //         if(frame != null){
            //             frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            //         }
        }
        return daxFile;
    }

    @CustomGUIComponent
    public Component getComponent() {
        return new JLabel("This is a non-gui tool. Use the triana-pegasus-gui toolbox for more options.");
    }

    private void daxFromInList(List in) {


        ADAG dax = new ADAG(fileName);

        for (int j = 0; j < in.size(); j++) {
            Object o = in.get(j);
            if (o instanceof DaxJobChunk) {
                DaxJobChunk inChunk = (DaxJobChunk) o;

                if (!inChunk.isStub()) {
                    String jobName = "Process";
                    String id = "0000000" + (j + 1);
                    id = ("ID" + id.substring(id.length() - 7));
                    Job job = new Job(id, namespace, jobName, "1.0");
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
                    devLog.debug("Added a job to ADAG.");
                } else {
                    devLog.debug("Found a job stub, ignoring..");

                }
            } else {
                devLog.debug("*** Found something that wasn't a DaxJobChunk in input List ***");
            }
        }

        writeDax(dax);
    }

    private java.io.File daxFromRegister(DaxRegister register) {

        //      register.listAll();

        idNumber = 0;
        java.io.File filenameFile = new java.io.File(fileName);
        if(task.getParameter("namespace") != null){
            namespace = (String) task.getParameter("namespace");
        } else {
            namespace = filenameFile.getName();
        }
        ADAG dax = new ADAG(filenameFile.getName());

        HashMap<String, Executable> execs = new HashMap<String, Executable>();

        List<DaxJobChunk> jobChunks = register.getJobChunks();
        int idNumber = 0;
        for (int j = 0; j < jobChunks.size(); j++) {
            DaxJobChunk jobChunk = jobChunks.get(j);
            devLog.debug("******** " + jobChunk.getArgBuilder().getArgString());
            int pattern = jobChunk.getConnectPattern();

            for (DaxFileChunk fc : jobChunk.getOutFileChunks()) {
                fc.resetNextCounter();
            }
            for (DaxFileChunk fc : jobChunk.getInFileChunks()) {
                fc.resetNextCounter();
            }

            devLog.debug("\nJob : " + jobChunk.getJobName());
            if (pattern == AUTO_CONNECT) {
                devLog.debug("auto_connect");

                autoConnect(dax, jobChunk);
            }
            if (pattern == SCATTER_CONNECT) {
                devLog.debug("scatter_connect");

                scatterConnect(dax, jobChunk);
            }
            if (pattern == ONE2ONE_CONNECT) {
                devLog.debug("one2one_connect");

                one2oneConnect(dax, jobChunk);
            }
            if (pattern == SPREAD_CONNECT) {
                devLog.debug("spread_connect");

                spreadConnect(dax, jobChunk);
            }

            for (DaxFileChunk fc : jobChunk.getOutFileChunks()) {
                fc.resetNextCounter();
            }
            for (DaxFileChunk fc : jobChunk.getInFileChunks()) {
                fc.resetNextCounter();
            }

            Executable exec = new Executable(namespace, jobChunk.getJobName(), "1.0");
            exec.setArchitecture(Executable.ARCH.X86).setOS(Executable.OS.LINUX);
            exec.setInstalled(true);
            exec.addPhysicalFile(jobChunk.getExecLocation(), "local");
            execs.put(jobChunk.getJobName(), exec);

        }


        for (Executable ex : execs.values()) {
            dax.addExecutable(ex);
        }

        for(File file : filesForDax){
            dax.addFile(file);
        }

        writeDependencies(dax);
//        devLog.debug("\nFound files : " + PFLarray.toString());

        return writeDax(dax);

    }

    private void writeDependencies(ADAG dax) {

        for(Job job : dax.getJobs()){

//            System.out.println("Job " + job.getId());

            ArrayList<File> inputFiles = new ArrayList<File>();
            for(File file : job.getUses()){
//                System.out.println("  Uses file " + file.getName() + " + " + file.getLink().name());
                if(file.getLink() == File.LINK.input){
                    inputFiles.add(file);
//                    System.out.println("Input file " + file.getName() + " into job " + job.getId());
                }
            }

            for(Job job1 : dax.getJobs()){
                for(File file1 : job1.getUses()){
                    if(file1.getLink() == File.LINK.output){
//                        System.out.println("Output file " + file1.getName() + " out of job " + job1.getId());
                        if(inputFiles.contains(file1)){
                            dax.addDependency(job1, job);
                        }
                    }
                }
            }
        }

    }

    private void autoConnect(ADAG dax, DaxJobChunk jobChunk) {
        devLog.debug("Job " + jobChunk.getJobName() + " has " + jobChunk.getNumberOfJobs() + " jobs.");

        /**
         * Create a number of dax job objects, adding -xx if there is > 1 required.
         */
        for (int n = 0; n < jobChunk.getNumberOfJobs(); n++) {
            String jobName = jobChunk.getJobName();
//            if (jobChunk.getNumberOfJobs() > 1) {
//                jobName = (jobName + "-" + n);
//            }
            idNumber++;
            String id = "0000000" + (idNumber);
            id = ("ID" + id.substring(id.length() - 7));

            Job job = new Job(id, namespace, jobName, "1.0");
            devLog.debug("args :" + jobChunk.getJobArgs());

            if(jobChunk.getArgsStringArray() != null){
                job.addArgument(jobChunk.getArgsStringArray().get(n));
            } else {
                job.addArgument(jobChunk.getJobArgs());
            }

//            jobChunk.listChunks();
            List inFiles = jobChunk.getInFileChunks();
            for (int i = 0; i < inFiles.size(); i++) {
                DaxFileChunk chunk = (DaxFileChunk) inFiles.get(i);
                chunk.resetNextCounter();
                if (chunk.isCollection()) {
                    for (int m = 0; m < chunk.getNumberOfFiles(); m++) {
                        //                   devLog.debug("Job " + job.getId() + " named : "  + job.getName() + " has input : " + chunk.getFilename() + "-" + m);

                        if (chunk.getNamePattern() != null) {
                            devLog.debug("Collection has a naming pattern");
                        } else {
                            devLog.debug("Collection has no naming pattern, using *append int*");
                        }

                        addFileToJob(dax, jobChunk, job, chunk, File.LINK.input);
//                        //    String filename = chunk.getNextFilename();
//                        String filename = chunk.getFilename();
//                        String fileLocation = chunk.getFileLocation();
//                        String fileProtocol = chunk.getFileProtocol();
//                        File file = new File(filename);
//
//                        if (chunk.isPhysicalFile()) {
//                            PFLarray.add(fileLocation);
//                            devLog.debug(fileLocation + " added to dax");
//                            file.addPhysicalFile(fileLocation, "condorpool");
//                            dax.addFile(file);
//                        }
//                        job.uses(file, File.LINK.input);
//                        job.addArgument(jobChunk.getArgBuilder().inputSwitch).addArgument(file);

                    }
                    chunk.resetNextCounter();

                } else {
//                   devLog.debug("Job " + job.getId() + " named : " + job.getName() + " has input : " + chunk.getFilename());
//                    job.uses(new File(chunk.getFilename()), File.LINK.input);
//                    String fileLocation = chunk.getFileLocation();
//                    if(!fileLocation.equals("")){
//                        PFLarray.add(fileLocation + java.io.File.separator + chunk.getFilename());
//                    }
                    addFileToJob(dax, jobChunk, job, chunk, File.LINK.input);

//                    String filename = chunk.getFilename();
//                    String fileLocation = chunk.getFileLocation();
//                    String fileProtocol = chunk.getFileProtocol();
//                    File file = new File(filename);
//
//                    if (chunk.isPhysicalFile()) {
//                        PFLarray.add(fileLocation);
//                        devLog.debug(fileLocation + " added to dax");
//                        file.addPhysicalFile(fileLocation, "condorpool");
//                        dax.addFile(file);
//                    }
//                    job.uses(file, File.LINK.input);
////                    job.addArgument("-i ").addArgument(file);
//                    job.addArgument(jobChunk.getArgBuilder().inputSwitch).addArgument(file);

                }
            }

            addOutputs(job, jobChunk);
            dax.addJob(job);
            devLog.debug("Added job : " + job.getName() + " to ADAG.");

        }
    }

    private void addFileToJob(ADAG dax, DaxJobChunk jobChunk, Job job, DaxFileChunk fileChunk, File.LINK link) {
        //    String filename = chunk.getNextFilename();
        String filename = fileChunk.getFilename();
        String fileLocation = fileChunk.getFileLocation();
        String fileProtocol = fileChunk.getFileProtocol();
        File file = new File(filename);

        if (fileChunk.isPhysicalFile()) {
//            PFLarray.add(fileProtocol + fileLocation);
            devLog.debug(fileLocation + " added to dax");
            file.addPhysicalFile(fileProtocol + fileLocation, "local");
            filesForDax.add(file);
//            dax.addFile(file);
        }
        file.setRegister(false);
        file.setTransfer(File.TRANSFER.OPTIONAL);
        job.uses(file, File.LINK.input);
        job.addArgument(jobChunk.getArgBuilder().inputSwitch).addArgument(file);
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
            devLog.debug("Job : " + j + " will have : " + filesPerJob[j] + " connected.");
        }
        devLog.debug("Should be total : " + files + " files. Assigned : " + count);

        return filesPerJob;
    }


    public void spreadConnect(ADAG dax, DaxJobChunk jobChunk) {
//        int n = 0;
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
        //     devLog.debug("Files : " + numberInputFiles +
        //             " Jobs : " + numberJobs + ". "
        //             + filesPerJob + " files / job ");

        for (int i = 0; i < numberJobs; i++) {

            idNumber++;

            String jobName = jobChunk.getJobName();
//            if (numberJobs > 1) {
//                jobName = (jobName + "-" + n);
//                devLog.debug("Jobs name is : " + jobName);
//                n++;
//            }
            String id = "0000000" + (idNumber);
            id = ("ID" + id.substring(id.length() - 7));
            Job job = new Job(id, namespace, jobName, "1.0");
            job.addArgument(jobChunk.getJobArgs());


            for (int j = 0; j < filesPerJob[i]; j++) {
                if (fcs.size() > 0) {

//                    java.util.Random rand = new java.util.Random();
//                    int r = rand.nextInt(fcs.size());

                    DaxFileChunk chunk = fcs.get(0);
                    fcs.remove(0);
//                    job.uses(new File(fc.getNextFilename()), File.LINK.input);
//                    job.uses(new File(fc.getFilename()), File.LINK.input);

                    addFileToJob(dax, jobChunk, job, chunk, File.LINK.input);
//                    String filename = chunk.getFilename();
//                    String fileLocation = chunk.getFileLocation();
//                    String fileProtocol = chunk.getFileProtocol();
//                    File file = new File(filename);
//
//                    if (chunk.isPhysicalFile()) {
//                        PFLarray.add(fileLocation);
//                        devLog.debug(fileLocation + " added to dax");
//                        file.addPhysicalFile(fileLocation, "condorpool");
//                        dax.addFile(file);
//                    }
                }
            }

            addOutputs(job, jobChunk);
//
//            List outFiles = jobChunk.getOutFileChunks();
//            for(int j = 0; j < outFiles.size(); j++){
//                DaxFileChunk chunk = (DaxFileChunk)outFiles.get(j);
//                if(chunk.isCollection()){
//                    for(int k = 0 ; k < chunk.getNumberOfFiles(); k++){
//                        devLog.debug("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + "-" + k);
//
//                        if(chunk.getNamePattern() != null){
//                            devLog.debug("Collection has a naming pattern");
//                        }else{
//                            devLog.debug("Collection has no naming pattern, using *append int*");
//                        }
//
//                        job.addUses(new Filename(chunk.getFilename() + "-" + k, 2));
//                    }
//                }
//                else{
//                    devLog.debug("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
//                    job.addUses(new Filename(chunk.getFilename(), 2));
//                }
//            }

            devLog.debug("Adding job : " + job.getName() + " to dax");
            jobHashMap.put(job.getId(), job);
            dax.addJob(job);
        }

    }


    private void scatterConnect(ADAG dax, DaxJobChunk jobChunk) {
//        int n = 0;
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
        devLog.debug("Double is : " + number + " Number is : " + numberJobs);
        devLog.debug("Files : " + numberInputFiles +
                " Files/job : " + numberInputsPerJob + ". "
                + numberJobs + " duplicates of " + jobChunk.getJobName());

        int offset = 0;
        for (int i = 0; i < numberJobs; i++) {
            devLog.debug("Sorting out job : " + i);
            idNumber++;

            String jobName = jobChunk.getJobName();
//            if (numberJobs > 1) {
//                jobName = (jobName + "-" + n);
//                devLog.debug("Jobs name is : " + jobName);
//                n++;
//            }
            String id = "0000000" + (idNumber);
            id = ("ID" + id.substring(id.length() - 7));
            Job job = new Job(id, namespace, jobName, "1.0");
            job.addArgument(jobChunk.getJobArgs());

            for (int j = offset; j < (offset + numberInputsPerJob); j++) {
                if (j < numberInputFiles) {
                    DaxFileChunk chunk = fcs.get(j);
//                    String filename = fc.getNextFilename();
//                    String filename = fc.getFilename();
//                    devLog.debug("Adding file : " + filename + " to job : " + i + " (Job : " + j + " of " + numberInputFiles + ")");

                    addFileToJob(dax, jobChunk, job, chunk, File.LINK.input);
//                    String filename = chunk.getFilename();
//                    String fileLocation = chunk.getFileLocation();
//                    String fileProtocol = chunk.getFileProtocol();
//                    File file = new File(filename);
//
//                    if (chunk.isPhysicalFile()) {
//                        PFLarray.add(fileLocation);
//                        devLog.debug(fileLocation + " added to dax");
//                        file.addPhysicalFile(fileLocation, "condorpool");
//                        dax.addFile(file);
//                    }
//                    job.uses(file, File.LINK.input);
                }
            }
            offset = offset + (int) numberInputsPerJob;

            addOutputs(job, jobChunk);

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

        devLog.debug("Job has " + fc.getNumberOfFiles() + " inputs.");
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
            Job job = new Job(id, namespace, jobName, "1.0");
            job.addArgument(jobChunk.getJobArgs());

//            String fileName = fc.getFilename();
//            if(fc.getNumberOfFiles() > 1){
//                fileName = (fileName + "-" + m);
//                m++;
//            }
            //       fileName = fc.getNextFilename();
            String fileName = fc.getFilename();

            devLog.debug("Job has " + fileName + " as an input.");
            job.uses(new File(fileName), File.LINK.input);

            for (DaxFileChunk dfc : fcs) {
                if (dfc != pfc) {
                    if (dfc.isCollection()) {
                        for (int j = 0; j < dfc.getNumberOfFiles(); j++) {
//                            job.uses(new File(dfc.getNextFilename()), File.LINK.input);

                            addFileToJob(dax, jobChunk, job, dfc, File.LINK.input);

//                            String filename = dfc.getFilename();
//                            String fileLocation = dfc.getFileLocation();
//                            String fileProtocol = dfc.getFileProtocol();
//                            File file = new File(filename);
//
//                            if (dfc.isPhysicalFile()) {
//                                PFLarray.add(fileLocation);
//                                devLog.debug(fileLocation + " added to dax");
//                                file.addPhysicalFile(fileLocation, "condorpool");
//
//                                dax.addFile(file);
//                            }
//                            job.uses(file, File.LINK.input);

                        }
                        dfc.resetNextCounter();
                    } else {
                        addFileToJob(dax, jobChunk, job, dfc, File.LINK.input);

//                        String filename = dfc.getFilename();
//                        String fileLocation = dfc.getFileLocation();
//                        String fileProtocol = dfc.getFileProtocol();
//                        File file = new File(filename);
//
//                        if (dfc.isPhysicalFile()) {
//                            PFLarray.add(fileLocation);
//                            devLog.debug(fileLocation + " added to dax");
//                            file.addPhysicalFile(fileLocation, "condorpool");
//                            dax.addFile(file);
//                        }
//                        job.uses(file, File.LINK.input);
                    }
                }
            }

            addOutputs(job, jobChunk);

            dax.addJob(job);
            devLog.debug("Added job : " + job.getName() + " to ADAG.");
        }

    }

    private void addOutputs(Job job, DaxJobChunk jobChunk) {
        List outFiles = jobChunk.getOutFileChunks();
        devLog.debug("\nAdding outputs to job : " + jobChunk.getJobName() + " with " + jobChunk.getOutFileChunks().size() + " outputs.");
        for (int j = 0; j < outFiles.size(); j++) {
            devLog.debug("Output " + j + " :");
            DaxFileChunk chunk = (DaxFileChunk) outFiles.get(j);
            if (chunk.isCollection()) {

                //TODO fix file naming on collections
//                chunk.resetNextCounter();
                devLog.debug("Jobs output file is a collection");
                if (chunk.isOne2one()) {

                    devLog.debug("Building one2one output");
                    //                   chunk.setOne2one(true);
                    chunk.setNumberOfFiles(jobChunk.getNumberOfJobs());

                    //                devLog.debug("File " + chunk.getFilename() + " duplication set to " + chunk.getNumberOfFiles());
                    if (chunk.getNamePattern() != null) {
                        devLog.debug("Collection has a naming pattern");
                    } else {
                        devLog.debug("Collection has no naming pattern, using *append int*");
                    }
//                    String filename = chunk.getNextFilename();

                    String filename = chunk.getFilename();
                    devLog.debug("Job " + job.getId() + " named : " + job.getName() + " has output : " + filename);

                    File outputFile = new File(filename);
                    outputFile.setTransfer(File.TRANSFER.OPTIONAL);
                    outputFile.setRegister(false);
                    job.uses(outputFile, File.LINK.output);
                    job.addArgument(jobChunk.getArgBuilder().outputSwitch).addArgument(outputFile);
                } else {
                    devLog.debug("Not a one2one");
                    for (int k = 0; k < chunk.getNumberOfFiles(); k++) {

//                        if(chunk.getNamePattern() != null){
//                            devLog.debug("Collection has a naming pattern");
//                        }else{
//                            devLog.debug("Collection has no naming pattern, using *append int*");
//                        }

//                        String filename = chunk.getNextFilename();

                        String filename = chunk.getFilename();
                        devLog.debug("Job " + job.getId() + " named : " + job.getName() + " has output : " + filename);
                        File outputFile = new File(filename);
                        outputFile.setRegister(false);
                        outputFile.setTransfer(File.TRANSFER.OPTIONAL);
                        job.uses(outputFile, File.LINK.output);
                        job.addArgument(jobChunk.getArgBuilder().outputSwitch).addArgument(outputFile);

                    }
                }
            } else {
                //           devLog.debug("Job " + job.getId() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
                File outputFile = new File(chunk.getFilename());
                outputFile.setTransfer(File.TRANSFER.OPTIONAL);
                outputFile.setRegister(false);
                job.uses(outputFile, File.LINK.output);
//                job.addArgument("-o ").addArgument(outputFile);
                job.addArgument(jobChunk.getArgBuilder().outputSwitch).addArgument(outputFile);
            }
            devLog.debug("Finished with job " + job.getId() + " named : " + job.getName() + "\n");
        }
    }


    private java.io.File writeDax(ADAG dax) {

        dax.writeToFile(fileName);
        devLog.debug("File " + fileName + " saved.\n");
        System.out.println(fileName + " saved.");

        java.io.File daxFile = new java.io.File(fileName);

        return daxFile;
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
        this.demo = getDemoParam();
    }

    private boolean getDemoParam() {
        Object o = task.getParameter("demo");
        if (o != null) {
            return o.equals(true);
        }
        return false;
    }
}
