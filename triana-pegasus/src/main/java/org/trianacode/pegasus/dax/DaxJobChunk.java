package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 24, 2010
 * Time: 1:14:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxJobChunk implements Serializable {

    private String jobName = "";
    private String jobArgs = "";
    private String jobID = "";
    private String outputFilename = "";
    private List<String> inFiles = new ArrayList();
    private List<String> outFiles = new ArrayList();
    private Vector<DaxFileChunk> inFileChunks = new Vector();
    private Vector<DaxFileChunk> outFileChunks = new Vector();
    private boolean isStub = false;
    private boolean isCollection = false;
    private DaxFileChunk outputFileChunk;
    private UUID uuid;
    private int numberOfJobs = 1;
    private int connectPattern = 0;
    private int fileInputsPerJob = 1;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobArgs() {
        return jobArgs;
    }

    public void setJobArgs(String jobArgs) {
        this.jobArgs = jobArgs;
    }

    public void addJobArg(String jobArg){
        jobArgs += jobArg;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public List getInFiles() {
        return inFiles;
    }

    public void addInFile(String file) {
        inFiles.add(file);
    }

    public List getOutFiles() {
        return outFiles;
    }

    public void addOutFile(String file) {
        outFiles.add(file);
    }

    public boolean isStub() {
        return isStub;
    }

    public void setStub(boolean stub) {
        isStub = stub;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public List<DaxFileChunk> getInFileChunks() {
        return inFileChunks;
    }

    public void addInFileChunk(DaxFileChunk chunk) {
        inFileChunks.add(chunk);
    }

    public void setInFileChunks(Vector<DaxFileChunk> inFileChunks) {
        this.inFileChunks = inFileChunks;
    }

    public List<DaxFileChunk> getOutFileChunks() {
        return outFileChunks;
    }

    public void addOutFileChunk(DaxFileChunk chunk) {
        outFileChunks.add(chunk);
    }
    public void setOutFileChunks(Vector<DaxFileChunk> outFileChunks) {
        this.outFileChunks = outFileChunks;
    }

    public void setOutputFileChunk(DaxFileChunk outputFileChunk) {
        this.outputFileChunk = outputFileChunk;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }


    public void listChunks(){
        for(DaxFileChunk c : inFileChunks){
            log("Job : " + getJobName() + " has input : " + c.getFilename());
        }
        for(DaxFileChunk c : outFileChunks){
            log("Job : " + getJobName() + " has output : " + c.getFilename());
        }
    }

    public int getNumberOfJobs() {
        if(isCollection){
            return numberOfJobs;
        }
        else{
            return 1;
        }
    }

    public void setNumberOfJobs(int numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
    }

    private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        //System.out.println(s);
    }

    public void setConnectPattern(int connectPattern) {
        this.connectPattern = connectPattern;
    }

    public int getConnectPattern() {
        return connectPattern;
    }

    public int getFileInputsPerJob() {
        return fileInputsPerJob;
    }

    public void setFileInputsPerJob(int fileInputsPerJob) {
        this.fileInputsPerJob = fileInputsPerJob;
    }
}
