package org.trianacode.pegasus.dax;

import org.griphyn.vdl.dax.Job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 24, 2010
 * Time: 1:14:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxJobChunk implements Serializable {

    private Job job = null;
    private String jobName = "";
    private String jobArgs = "";
    private String outputFilename = "";
    private List<String> inFiles = new ArrayList();
    private List<String> outFiles = new ArrayList();
    private boolean isStub = false;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

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
}
