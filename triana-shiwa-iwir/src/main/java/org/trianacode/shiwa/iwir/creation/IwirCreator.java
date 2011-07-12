package org.trianacode.shiwa.iwir.creation;

import org.shiwa.fgi.iwir.*;
import org.trianacode.annotation.CheckboxParameter;
import org.trianacode.annotation.TextFieldParameter;
import org.trianacode.annotation.Tool;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.shiwa.iwir.importer.IwirReader;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 14/04/2011
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */

@Tool(panelClass = "org.trianacode.shiwa.iwirTools.creation.IwirCreatorPanel")
public class IwirCreator implements TaskConscious {
    private static final int AUTO_CONNECT = 0;
    private static final int SCATTER_CONNECT = 1;
    private static final int ONE2ONE_CONNECT = 2;
    private static final int SPREAD_CONNECT = 3;
    public int idNumber = 0;

    private ArrayList<String> PFLarray = new ArrayList<String>();

    @CheckboxParameter
    private boolean demo = false;

    @TextFieldParameter
    private String fileName = "output_iwir.xml";

    private org.trianacode.taskgraph.Task task;


    @org.trianacode.annotation.Process(gather = true)
    public java.io.File process(List in) {
        log("IwirCreator");
        log("\nList in is size: " + in.size() + " contains : " + in.toString() + ".\n ");

        IwirRegister register = IwirRegister.getIwirRegister();

        java.io.File iwirFile = null;
        try {
            GUIEnv.getApplicationFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            iwirFile = iwirFromTaskGraph(task.getParent(), fileName);
        } catch (Exception e) {
            log("Failed at something : " + e + "\n\n");
            e.printStackTrace();
        } finally {
            register.clear();
            log("Cleared register");
            GUIEnv.getApplicationFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        if (iwirFile != null) {
            if (demo && iwirFile.exists() && iwirFile.canRead()) {
                log("Displaying demo of " + iwirFile.getAbsolutePath());
                IwirReader dr = new IwirReader();
                try {
                    TaskGraph t = dr.importWorkflow(iwirFile, GUIEnv.getApplicationFrame().getEngine().getProperties());
                    TaskGraph tg = GUIEnv.getApplicationFrame().addParentTaskGraphPanel(t);
                } catch (Exception e) {
                    log("Error opening *" + this.fileName + "* demo taskgraph : " + e);
                    e.printStackTrace();
                }
            } else {
                log("Not displaying demo, or file not found/accessible : " + iwirFile.getAbsolutePath());
            }
        }
        GUIEnv.getApplicationFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(), "IWIR saved : " + this.fileName);

        return iwirFile;
    }

    private void log(String s) {
//        Log log = Loggers.DEV_LOGGER;
//        log.debug(s);
        System.out.println(s);
    }

    public static java.io.File iwirFromTaskGraph(TaskGraph taskGraph, String fileName) {
        IWIR iwir = new IWIR(fileName);
        BlockScope rootTask = new BlockScope("FIXMETODO");
        iwir.setTask(rootTask);

        addGenericTrianaTasks(rootTask, taskGraph);
        System.out.println(iwir.asXMLString());
        return writeIwir(iwir, fileName);
    }

    private static java.io.File writeIwir(IWIR iwir, String fileName) {
        java.io.File iwirFile = new java.io.File(fileName + ".xml");
        try {
            iwir.asXMLFile(iwirFile);
            System.out.println("Wrote " + iwirFile.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return iwirFile;
    }

    private static void addGenericTrianaTasks(AbstractCompoundLoopTask rootTask, TaskGraph taskGraph) {
        org.trianacode.taskgraph.Task[] allTrianaTasks = taskGraph.getTasks(true);
        for (org.trianacode.taskgraph.Task task : allTrianaTasks) {
            if (AbstractTask.class.isAssignableFrom(task.getClass())) {
                System.out.println("Oh crap, this is more complicated than I thought...");
            } else {

                AbstractTask addedTask = null;
                if (task instanceof TaskGraph) {
                    addedTask = new BlockScope(task.getQualifiedToolName());
                    initIWIRTask(addedTask, task);
                    rootTask.addTask(addedTask);
                    addGenericTrianaTasks((BlockScope) addedTask, (TaskGraph) task);
                } else {
                    addedTask = new Task(task.getQualifiedToolName(), "hey look, its a string");
                    initIWIRTask(addedTask, task);
                    rootTask.addTask(addedTask);
                }

            }
        }
    }

    private static void initIWIRTask(AbstractTask addedTask, org.trianacode.taskgraph.Task task) {
//TODO - more fucking node connecting!!!!!

//        for(Node inNode : task.getDataInputNodes()){
//            addedTask.addInputPort(new InputPort(inNode.getName(), "string"));
//        }
//        for(Node outNode : task.getDataOutputNodes()){
//            addedTask.addOutputPort(new OutputPort(outNode.getName(), "string"));
//        }
    }

//    private void addIWIRTasks(IWIR iwir, IwirRegister register) {
//
//        //baseline blockscope for taskgraph
//        String taskGraphName = GUIEnv.getApplicationFrame().getSelectedTaskgraph().getToolName();
//        BlockScope rootBlockScope = new BlockScope("rootBlockScope" + taskGraphName);
//
//        // Set up tasks and ports. Port names are input/output + -taskname- +
//        Collection<AbstractTask> taskChunks = register.getRegisteredTasks();
//        int iter = 0;
//        for (Object next : taskChunks) {
//            IwirTaskChunk chunk = (IwirTaskChunk) next;
//            Task task = new Task(chunk.getTaskName() + "-" + iter, "Consumer");
//
//            int numInputs = chunk.getInTaskChunks().size();
//            System.out.println("Chunk " + task.getName() + " should have " + numInputs + " inputs.");
//            for (int i = 0; i < numInputs; i++) {
//                InputPort inputPort = new InputPort("input-" + task.getName() + "-" + i, SimpleType.FILE);
//                task.addInputPort(inputPort);
////                System.out.println(task.getOutputPorts().size() + " inputs.");
//            }
//
//            int numOutputs = chunk.getOutTaskChunks().size();
//            System.out.println("Chunk " + task.getName() + " should have " + numOutputs + " outputs.");
//            for (int i = 0; i < numOutputs; i++) {
//                OutputPort outputPort = new OutputPort("output-" + task.getName() + "-" + i, SimpleType.FILE);
//                task.addOutputPort(outputPort);
////                System.out.println(task.getOutputPorts().size() + " outputs.");
//            }
//
//            rootBlockScope.addTask(task);
//            iter++;
//        }
//
//        for (Object next : taskChunks) {
//            IwirTaskChunk chunk = (IwirTaskChunk) next;
//            Task task = new Task(chunk.getTaskName() + "-" + iter, "Consumer");
//
//            int numInputs = chunk.getInTaskChunks().size();
//            for (int i = 0; i < numInputs; i++) {
//                InputPort inputPort = new InputPort("input-" + task.getName() + "-" + i, SimpleType.FILE);
//                task.addInputPort(inputPort);
//            }
//
//            int numOutputs = chunk.getOutTaskChunks().size();
//            for (int i = 0; i < numOutputs; i++) {
//                OutputPort outputPort = new OutputPort("output-" + task.getName() + "-" + i, SimpleType.FILE);
//                task.addOutputPort(outputPort);
//            }
//
//            rootBlockScope.addTask(task);
//            iter++;
//        }
//
//
//        iwir.setTask(rootBlockScope);
//    }

    //        HashMap<String, Executable> execs = new HashMap<String, Executable>();
//
//        List<IwirTaskChunk> jobChunks = register.getJobChunks();
//        int idNumber = 0;
//        for (int j = 0; j < jobChunks.size(); j++) {
//            DaxJobChunk jobChunk = jobChunks.get(j);
//            System.out.println("******** " + jobChunk.getArgBuilder().getArgString());
//            int pattern = jobChunk.getConnectPattern();
//
//            log("\nJob : " + jobChunk.getJobName());
//            if (pattern == AUTO_CONNECT) {
//                log("auto_connect");
//
//                autoConnect(dax, jobChunk);
//            }
//            if (pattern == SCATTER_CONNECT) {
//                log("scatter_connect");
//
//                scatterConnect(dax, jobChunk);
//            }
//            if (pattern == ONE2ONE_CONNECT) {
//                log("one2one_connect");
//
//                one2oneConnect(dax, jobChunk);
//            }
//            if (pattern == SPREAD_CONNECT) {
//                log("spread_connect");
//
//                spreadConnect(dax, jobChunk);
//            }
//
//            for (IwirTaskChunk fc : jobChunk.getOutFileChunks()) {
//                fc.resetNextCounter();
//            }
//            for (IwirTaskChunk fc : jobChunk.getInFileChunks()) {
//                fc.resetNextCounter();
//            }
//
//            Executable exec = new Executable(fileName, jobChunk.getJobName(), "1.0");
//            exec.setArchitecture(Executable.ARCH.X86).setOS(Executable.OS.LINUX);
//            exec.setInstalled(true);
//            exec.addPhysicalFile("file:///home/triana-org.trianacode.pegasus.gui/org.trianacode.pegasus.gui-wms-3.0.1/bin/keg", "condorpool");
//            execs.put(jobChunk.getJobName(), exec);
//
//        }
//        for (Executable ex : execs.values()) {
//            dax.addExecutable(ex);
//        }
//
//        log("\nFound files : " + PFLarray.toString());
//
//        return writeDax(dax);
//
//    }
//
//    private void autoConnect(ADAG dax, DaxJobChunk jobChunk) {
//        log("Job " + jobChunk.getJobName() + " has " + jobChunk.getNumberOfJobs() + " jobs.");
//
//        /**
//         * Create a number of dax job objects, adding -xx if there is > 1 required.
//         */
//        for (int n = 0; n < jobChunk.getNumberOfJobs(); n++) {
//            String jobName = jobChunk.getJobName();
//            if (jobChunk.getNumberOfJobs() > 1) {
//                jobName = (jobName + "-" + n);
//            }
//            idNumber++;
//            String id = "0000000" + (idNumber);
//            id = ("ID" + id.substring(id.length() - 7));
//
//            Job job = new Job(id, fileName, jobName, "1.0");
//            System.out.println("args :" + jobChunk.getJobArgs());
//            job.addArgument(jobChunk.getJobArgs());
//
//
//            jobChunk.listChunks();
//            List inFiles = jobChunk.getInFileChunks();
//            for (int i = 0; i < inFiles.size(); i++) {
//                IwirTaskChunk chunk = (IwirTaskChunk) inFiles.get(i);
//                chunk.resetNextCounter();
//                if (chunk.isCollection()) {
//                    for (int m = 0; m < chunk.getNumberOfFiles(); m++) {
//                        //                   log("Job " + job.getId() + " named : "  + job.getName() + " has input : " + chunk.getFilename() + "-" + m);
//
//                        if (chunk.getNamePattern() != null) {
//                            log("Collection has a naming pattern");
//                        } else {
//                            log("Collection has no naming pattern, using *append int*");
//                        }
//
//                        //    String filename = chunk.getNextFilename();
//                        String filename = chunk.getFilename();
//                        String fileLocation = chunk.getFileLocation();
//                        String fileProtocol = chunk.getFileProtocol();
//                        File file = new File(filename);
//
//                        if (!fileLocation.equals("") && !fileProtocol.equals("")) {
//                            PFLarray.add(fileLocation + java.io.File.separator + filename);
//                            file.addPhysicalFile(fileProtocol + fileLocation + java.io.File.separator + filename, "condorpool");
//                            dax.addFile(file);
//
//                        }
//                        job.uses(file, File.LINK.input);
//                        job.addArgument("-i ").addArgument(file);
//
//                    }
//                    chunk.resetNextCounter();
//
//                } else {
////                   log("Job " + job.getId() + " named : " + job.getName() + " has input : " + chunk.getFilename());
////                    job.uses(new File(chunk.getFilename()), File.LINK.input);
////                    String fileLocation = chunk.getFileLocation();
////                    if(!fileLocation.equals("")){
////                        PFLarray.add(fileLocation + java.io.File.separator + chunk.getFilename());
////                    }
//
//                    String filename = chunk.getFilename();
//                    String fileLocation = chunk.getFileLocation();
//                    String fileProtocol = chunk.getFileProtocol();
//                    File file = new File(filename);
//
//                    if (!fileLocation.equals("") && !fileProtocol.equals("")) {
//                        PFLarray.add(fileLocation + java.io.File.separator + filename);
//                        file.addPhysicalFile(fileProtocol + fileLocation + java.io.File.separator + filename, "condorpool");
//                        dax.addFile(file);
//
//                    }
//                    job.uses(file, File.LINK.input);
//                    job.addArgument("-i ").addArgument(file);
//
//                }
//            }
//
//            addOutputs(job, jobChunk);
//
////            List outFiles = jobChunk.getOutFileChunks();
////            for(int i = 0; i < outFiles.size(); i++){
////                DaxFileChunk chunk = (DaxFileChunk)outFiles.get(i);
////                if(chunk.isCollection()){
////                    for(int m = 0 ; m < chunk.getNumberOfFiles(); m++){
////                        log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + "-" + m);
////
////                        if(chunk.getNamePattern() != null){
////                            log("Collection has a naming pattern");
////                        }else{
////                            log("Collection has no naming pattern, using *append int*");
////                        }
////
////                        job.addUses(new Filename(chunk.getFilename() + "-" + m, 2));
////                    }
////                }
////                else{
////                    log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
////                    job.addUses(new Filename(chunk.getFilename(), 2));
////                }
////            }
//            dax.addJob(job);
//            log("Added job : " + job.getName() + " to ADAG.");
//
//        }
//    }
//
//    private void one2oneOutput(ADAG dax, DaxJobChunk jobChunk) {
//
//    }
//
//    private int[] sortSpread(int files, int jobs) {
//        double numberOfFiles = (double) files;
//        double numberOfJobs = (double) jobs;
//
//        double filesLeft = numberOfFiles;
//        double jobsLeft = numberOfJobs;
//
//        int[] filesPerJob = new int[(int) jobs];
//        for (int i = 0; i < jobs; i++) {
//            double num = Math.floor(filesLeft / jobsLeft);
//            filesPerJob[i] = (int) num;
//            filesLeft = filesLeft - num;
//            jobsLeft = jobsLeft - 1;
//        }
//
//        int count = 0;
//        for (int j = 0; j < filesPerJob.length; j++) {
//            count = count + filesPerJob[j];
//            log("Job : " + j + " will have : " + filesPerJob[j] + " connected.");
//        }
//        log("Should be total : " + files + " files. Assigned : " + count);
//
//        return filesPerJob;
//    }
//
//
//    public void spreadConnect(ADAG dax, DaxJobChunk jobChunk) {
//        int n = 0;
//        int numberJobs = jobChunk.getNumberOfJobs();
//        Vector<IwirTaskChunk> fcs = new Vector<IwirTaskChunk>();
//        for (IwirTaskChunk fc : jobChunk.getInFileChunks()) {
//            for (int i = 0; i < fc.getNumberOfFiles(); i++) {
//                fcs.add(fc);
//            }
//            fc.resetNextCounter();
//        }
//
//        int[] filesPerJob = sortSpread(fcs.size(), jobChunk.getNumberOfJobs());
//
//
//        //     double numberInputFiles = fcs.size();
//        //     double filesPerJob = numberInputFiles/numberJobs;
//        //     System.out.println("Files : " + numberInputFiles +
//        //             " Jobs : " + numberJobs + ". "
//        //             + filesPerJob + " files / job ");
//
//        for (int i = 0; i < numberJobs; i++) {
//
//            //  System.out.println("Sorting out job : " + i);
//            idNumber++;
//
//            String jobName = jobChunk.getJobName();
//            if (numberJobs > 1) {
//                jobName = (jobName + "-" + n);
//                log("Jobs name is : " + jobName);
//                n++;
//            }
//            String id = "0000000" + (idNumber);
//            id = ("ID" + id.substring(id.length() - 7));
//            Job job = new Job(id, jobName);
//            job.addArgument(jobChunk.getJobArgs());
//
//
//            for (int j = 0; j < filesPerJob[i]; j++) {
//                if (fcs.size() > 0) {
//                    java.util.Random rand = new java.util.Random();
//                    int r = rand.nextInt(fcs.size());
//
//                    IwirTaskChunk fc = fcs.get(r);
//                    fcs.remove(r);
////                    job.uses(new File(fc.getNextFilename()), File.LINK.input);
//
//                    job.uses(new File(fc.getFilename()), File.LINK.input);
//                }
//            }
//
//            addOutputs(job, jobChunk);
////
////            List outFiles = jobChunk.getOutFileChunks();
////            for(int j = 0; j < outFiles.size(); j++){
////                DaxFileChunk chunk = (DaxFileChunk)outFiles.get(j);
////                if(chunk.isCollection()){
////                    for(int k = 0 ; k < chunk.getNumberOfFiles(); k++){
////                        log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + "-" + k);
////
////                        if(chunk.getNamePattern() != null){
////                            log("Collection has a naming pattern");
////                        }else{
////                            log("Collection has no naming pattern, using *append int*");
////                        }
////
////                        job.addUses(new Filename(chunk.getFilename() + "-" + k, 2));
////                    }
////                }
////                else{
////                    log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
////                    job.addUses(new Filename(chunk.getFilename(), 2));
////                }
////            }
//
//            log("Adding job : " + job.getName() + " to dax");
//            dax.addJob(job);
//        }
//
//    }
//
//
//    private void scatterConnect(ADAG dax, DaxJobChunk jobChunk) {
//        int n = 0;
//        int numberJobs = 0;
//        double numberInputsPerJob = jobChunk.getFileInputsPerJob();
//        Vector<IwirTaskChunk> fcs = new Vector<IwirTaskChunk>();
//        for (IwirTaskChunk fc : jobChunk.getInFileChunks()) {
//            for (int i = 0; i < fc.getNumberOfFiles(); i++) {
//                fcs.add(fc);
//            }
//            fc.resetNextCounter();
//        }
//        double numberInputFiles = fcs.size();
//
//        double number = Math.ceil(numberInputFiles / numberInputsPerJob);
//        numberJobs = (int) number;
//        jobChunk.setNumberOfJobs(numberJobs);
//        log("Double is : " + number + " Number is : " + numberJobs);
//        log("Files : " + numberInputFiles +
//                " Files/job : " + numberInputsPerJob + ". "
//                + numberJobs + " duplicates of " + jobChunk.getJobName());
//
//        int offset = 0;
//        for (int i = 0; i < numberJobs; i++) {
//            log("Sorting out job : " + i);
//            idNumber++;
//
//            String jobName = jobChunk.getJobName();
//            if (numberJobs > 1) {
//                jobName = (jobName + "-" + n);
//                log("Jobs name is : " + jobName);
//                n++;
//            }
//            String id = "0000000" + (idNumber);
//            id = ("ID" + id.substring(id.length() - 7));
//            Job job = new Job(id, jobName);
//            job.addArgument(jobChunk.getJobArgs());
//
//            for (int j = offset; j < (offset + numberInputsPerJob); j++) {
//                if (j < numberInputFiles) {
//                    IwirTaskChunk fc = fcs.get(j);
////                    String filename = fc.getNextFilename();
//                    String filename = fc.getFilename();
//
//                    log("Adding file : " + filename + " to job : " + i +
//                            " (Job : " + j + " of " + numberInputFiles + ")");
//
//                    job.uses(new File(filename), File.LINK.input);
//                }
//            }
//            offset = offset + (int) numberInputsPerJob;
//
//            addOutputs(job, jobChunk);
//
////            List outFiles = jobChunk.getOutFileChunks();
////            for(int j = 0; j < outFiles.size(); j++){
////                DaxFileChunk chunk = (DaxFileChunk)outFiles.get(j);
////                if(chunk.isCollection()){
////                    for(int k = 0 ; k < chunk.getNumberOfFiles(); k++){
////                        log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename() + "-" + k);
////
////                        if(chunk.getNamePattern() != null){
////                            log("Collection has a naming pattern");
////                        }else{
////                            log("Collection has no naming pattern, using *append int*");
////                        }
////
////                        job.addUses(new Filename(chunk.getFilename() + "-" + k, 2));
////                    }
////                }
////                else{
////                    log("Job " + job.getID() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
////                    job.addUses(new Filename(chunk.getFilename(), 2));
////                }
////            }
//
//            dax.addJob(job);
//        }
//
//    }
//
//    private void one2oneConnect(ADAG dax, DaxJobChunk jobChunk) {
//        int n = 0;
//        int m = 0;
//        List<IwirTaskChunk> fcs = jobChunk.getInFileChunks();
//        IwirTaskChunk pfc = null;
//        if (fcs.size() > 1) {
//            pfc = (IwirTaskChunk) PrimaryFilePanel.getValue(jobChunk.getJobName(), fcs);
//        } else {
//            pfc = fcs.get(0);
//        }
//
//        IwirTaskChunk fc = pfc;
//        fc.resetNextCounter();
//
//        log("Job has " + fc.getNumberOfFiles() + " inputs.");
//        if (fc.getNumberOfFiles() > jobChunk.getNumberOfJobs()) {
//            jobChunk.setNumberOfJobs(fc.getNumberOfFiles());
//        }
//
//        for (int i = 0; i < fc.getNumberOfFiles(); i++) {
//            idNumber++;
//
//            String jobName = jobChunk.getJobName();
//            if (jobChunk.getNumberOfJobs() > 1) {
//                jobName = (jobName + "-" + n);
//                n++;
//            }
//            String id = "0000000" + (idNumber);
//            id = ("ID" + id.substring(id.length() - 7));
//            Job job = new Job(id, jobName);
//            job.addArgument(jobChunk.getJobArgs());
//
////            String fileName = fc.getFilename();
////            if(fc.getNumberOfFiles() > 1){
////                fileName = (fileName + "-" + m);
////                m++;
////            }
//            //       fileName = fc.getNextFilename();
//            String fileName = fc.getFilename();
//
//            log("Job has " + fileName + " as an input.");
//            job.uses(new File(fileName), File.LINK.input);
//
//            for (IwirTaskChunk dfc : fcs) {
//                if (dfc != pfc) {
//                    if (dfc.isCollection()) {
//                        for (int j = 0; j < dfc.getNumberOfFiles(); j++) {
////                            job.uses(new File(dfc.getNextFilename()), File.LINK.input);
//                            job.uses(new File(dfc.getFilename()), File.LINK.input);
//
//                        }
//                        dfc.resetNextCounter();
//                    } else {
//                        job.uses(new File(dfc.getFilename()), File.LINK.input);
//                    }
//                }
//            }
//
//            addOutputs(job, jobChunk);
//
//            dax.addJob(job);
//            log("Added job : " + job.getName() + " to ADAG.");
//        }
//
//    }
//
//    private void addOutputs(Job job, DaxJobChunk jobChunk) {
//        List outFiles = jobChunk.getOutFileChunks();
//        for (int j = 0; j < outFiles.size(); j++) {
//            IwirTaskChunk chunk = (IwirTaskChunk) outFiles.get(j);
//            log("Job has " + chunk.getNumberOfFiles() + " outputs.");
//            if (chunk.isCollection()) {
//                chunk.resetNextCounter();
//                log("Jobs output file is a collection");
//                if (chunk.isOne2one()) {
//
//                    log("Building one2one output");
//                    //                   chunk.setOne2one(true);
//                    chunk.setNumberOfFiles(jobChunk.getNumberOfJobs());
//
//                    //                log("File " + chunk.getFilename() + " duplication set to " + chunk.getNumberOfFiles());
//                    if (chunk.getNamePattern() != null) {
//                        log("Collection has a naming pattern");
//                    } else {
//                        log("Collection has no naming pattern, using *append int*");
//                    }
////                    String filename = chunk.getNextFilename();
//
//                    String filename = chunk.getFilename();
//                    log("Job " + job.getId() + " named : " + job.getName() + " has output : " + filename);
//                    job.uses(new File(filename), File.LINK.output);
//                } else {
//                    log("Not a one2one");
//                    for (int k = 0; k < chunk.getNumberOfFiles(); k++) {
//
////                        if(chunk.getNamePattern() != null){
////                            log("Collection has a naming pattern");
////                        }else{
////                            log("Collection has no naming pattern, using *append int*");
////                        }
//
////                        String filename = chunk.getNextFilename();
//
//                        String filename = chunk.getFilename();
//                        log("Job " + job.getId() + " named : " + job.getName() + " has output : " + filename);
//
//                        job.uses(new File(filename), File.LINK.output);
//
//                    }
//                }
//            } else {
//                //           log("Job " + job.getId() + " named : "  + job.getName() + " has output : " + chunk.getFilename());
//                File outputFile = new File(chunk.getFilename());
//                job.uses(outputFile, File.LINK.output);
//                job.addArgument("-o ").addArgument(outputFile);
//            }
//        }
//    }
//
//

    @Override
    public void setTask(org.trianacode.taskgraph.Task task) {
        this.task = task;
    }
}
