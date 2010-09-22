package org.trianacode.pegasus.dax;

import org.trianacode.pegasus.string.PatternCollection;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Sep 20, 2010
 * Time: 1:43:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxFileChunk implements Serializable {
    private String filename = "";
    private boolean isCollection = false;
    private UUID uuid = null;
    private Vector<DaxJobChunk> inJobChunks = new Vector();
    private Vector<DaxJobChunk> outJobChunks = new Vector();
    private int numberOfFiles = 1;
    private PatternCollection namePattern = null;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public List getInJobChunks() {
        return inJobChunks;
    }

    public void addInJobChunk(DaxJobChunk chunk) {
        inJobChunks.add(chunk);
    }
    public void setInJobChunks(Vector inJobChunks) {
        this.inJobChunks = inJobChunks;
    }

    public List getOutJobChunks() {
        return outJobChunks;
    }

    public void addOutJobChunk(DaxJobChunk chunk) {
        outJobChunks.add(chunk);
    }

    public void setOutJobChunks(Vector outJobChunks) {
        this.outJobChunks = outJobChunks;
    }

    public void listChunks(){
        for(DaxJobChunk c : inJobChunks){
            System.out.println(" ******* File : " + getFilename() + " has input : " + c.getJobName());
        }
        for(DaxJobChunk c : outJobChunks){
            System.out.println(" ******* File : " + getFilename() + " has output : " + c.getJobName());
        }
    }

    public void setNumberOfFiles(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    public PatternCollection getNamePattern() {
        return namePattern;
    }

    public void setNamePattern(PatternCollection namePattern) {
        this.namePattern = namePattern;
    }
}
