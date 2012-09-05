package org.trianacode.shiwaall.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Aug 24, 2010
 * Time: 1:14:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxJobChunk implements Serializable {

    /** The job name. */
    private String jobName = "";
    
    /** The job args. */
    private String jobArgs = "";
    
    /** The job id. */
    private String jobID = "";
    
    /** The output filename. */
    private String outputFilename = "";
    
    /** The in files. */
    private List<String> inFiles = new ArrayList();
    
    /** The out files. */
    private List<String> outFiles = new ArrayList();
    
    /** The in file chunks. */
    private Vector<DaxFileChunk> inFileChunks = new Vector();
    
    /** The out file chunks. */
    private Vector<DaxFileChunk> outFileChunks = new Vector();
    
    /** The is stub. */
    private boolean isStub = false;
    
    /** The is collection. */
    private boolean isCollection = false;
    
    /** The output file chunk. */
    private DaxFileChunk outputFileChunk;
    
    /** The uuid. */
    private UUID uuid;
    
    /** The number of jobs. */
    private int numberOfJobs = 1;
    
    /** The connect pattern. */
    private int connectPattern = 0;
    
    /** The file inputs per job. */
    private int fileInputsPerJob = 1;
    
    /** The arg builder. */
    private ArgBuilder argBuilder;

    /** The dev log. */
    Log devLog = Loggers.DEV_LOGGER;
    
    /** The args string array. */
    private ArrayList<String> argsStringArray = null;
    
    /** The exec location. */
    private String execLocation;

    /**
     * Gets the job name.
     *
     * @return the job name
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Sets the job name.
     *
     * @param jobName the new job name
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Gets the job args.
     *
     * @return the job args
     */
    public String getJobArgs() {
        return jobArgs;
    }

    /**
     * Sets the job args.
     *
     * @param jobArgs the new job args
     */
    public void setJobArgs(String jobArgs) {
        this.jobArgs = jobArgs;
    }

    /**
     * Adds the job arg.
     *
     * @param jobArg the job arg
     */
    public void addJobArg(String jobArg) {
        jobArgs += jobArg;
    }

    /**
     * Gets the output filename.
     *
     * @return the output filename
     */
    public String getOutputFilename() {
        return outputFilename;
    }

    /**
     * Sets the output filename.
     *
     * @param outputFilename the new output filename
     */
    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    /**
     * Gets the in files.
     *
     * @return the in files
     */
    public List getInFiles() {
        return inFiles;
    }

    /**
     * Adds the in file.
     *
     * @param file the file
     */
    public void addInFile(String file) {
        inFiles.add(file);
    }

    /**
     * Gets the out files.
     *
     * @return the out files
     */
    public List getOutFiles() {
        return outFiles;
    }

    /**
     * Adds the out file.
     *
     * @param file the file
     */
    public void addOutFile(String file) {
        outFiles.add(file);
    }

    /**
     * Checks if is stub.
     *
     * @return true, if is stub
     */
    public boolean isStub() {
        return isStub;
    }

    /**
     * Sets the stub.
     *
     * @param stub the new stub
     */
    public void setStub(boolean stub) {
        isStub = stub;
    }

    /**
     * Checks if is collection.
     *
     * @return true, if is collection
     */
    public boolean isCollection() {
        return isCollection;
    }

    /**
     * Sets the collection.
     *
     * @param collection the new collection
     */
    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    /**
     * Gets the in file chunks.
     *
     * @return the in file chunks
     */
    public List<DaxFileChunk> getInFileChunks() {
        return inFileChunks;
    }

    /**
     * Adds the in file chunk.
     *
     * @param chunk the chunk
     */
    public void addInFileChunk(DaxFileChunk chunk) {
        inFileChunks.add(chunk);
    }

    /**
     * Sets the in file chunks.
     *
     * @param inFileChunks the new in file chunks
     */
    public void setInFileChunks(Vector<DaxFileChunk> inFileChunks) {
        this.inFileChunks = inFileChunks;
    }

    /**
     * Gets the out file chunks.
     *
     * @return the out file chunks
     */
    public List<DaxFileChunk> getOutFileChunks() {
        return outFileChunks;
    }

    /**
     * Adds the out file chunk.
     *
     * @param chunk the chunk
     */
    public void addOutFileChunk(DaxFileChunk chunk) {
        outFileChunks.add(chunk);
    }

    /**
     * Sets the out file chunks.
     *
     * @param outFileChunks the new out file chunks
     */
    public void setOutFileChunks(Vector<DaxFileChunk> outFileChunks) {
        this.outFileChunks = outFileChunks;
    }

    /**
     * Sets the output file chunk.
     *
     * @param outputFileChunk the new output file chunk
     */
    public void setOutputFileChunk(DaxFileChunk outputFileChunk) {
        this.outputFileChunk = outputFileChunk;
    }

    /**
     * Sets the uuid.
     *
     * @param uuid the new uuid
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the job id.
     *
     * @return the job id
     */
    public String getJobID() {
        return jobID;
    }

    /**
     * Sets the job id.
     *
     * @param jobID the new job id
     */
    public void setJobID(String jobID) {
        this.jobID = jobID;
    }


    /**
     * List chunks.
     */
    public void listChunks() {
        for (DaxFileChunk c : inFileChunks) {
//            devLog.debug("Job : " + getJobName() + " has input : " + c.getFilename());
        }
        for (DaxFileChunk c : outFileChunks) {
//            devLog.debug("Job : " + getJobName() + " has output : " + c.getFilename());
        }
    }

    /**
     * Gets the number of jobs.
     *
     * @return the number of jobs
     */
    public int getNumberOfJobs() {
        if (isCollection) {
            return numberOfJobs;
        } else {
            return 1;
        }
    }

    /**
     * Sets the number of jobs.
     *
     * @param numberOfJobs the new number of jobs
     */
    public void setNumberOfJobs(int numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
    }

    /**
     * Sets the connect pattern.
     *
     * @param connectPattern the new connect pattern
     */
    public void setConnectPattern(int connectPattern) {
        this.connectPattern = connectPattern;
    }

    /**
     * Gets the connect pattern.
     *
     * @return the connect pattern
     */
    public int getConnectPattern() {
        return connectPattern;
    }

    /**
     * Gets the file inputs per job.
     *
     * @return the file inputs per job
     */
    public int getFileInputsPerJob() {
        return fileInputsPerJob;
    }

    /**
     * Sets the file inputs per job.
     *
     * @param fileInputsPerJob the new file inputs per job
     */
    public void setFileInputsPerJob(int fileInputsPerJob) {
        this.fileInputsPerJob = fileInputsPerJob;
    }

    /**
     * Adds the arg builder.
     *
     * @param ab the ab
     */
    public void addArgBuilder(ArgBuilder ab) {
        this.argBuilder = ab;
    }

    /**
     * Gets the arg builder.
     *
     * @return the arg builder
     */
    public ArgBuilder getArgBuilder() {
        return argBuilder;
    }

    /**
     * Sets the job args.
     *
     * @param argsStringArray the new job args
     */
    public void setJobArgs(ArrayList<String> argsStringArray) {
        this.argsStringArray = argsStringArray;
    }

    /**
     * Gets the args string array.
     *
     * @return the args string array
     */
    public ArrayList<String> getArgsStringArray() {
        return argsStringArray;
    }

    /**
     * Sets the exec location.
     *
     * @param execLocation the new exec location
     */
    public void setExecLocation(String execLocation) {
        this.execLocation = execLocation;
    }

    /**
     * Gets the exec location.
     *
     * @return the exec location
     */
    public String getExecLocation() {
        return execLocation;
    }
}
