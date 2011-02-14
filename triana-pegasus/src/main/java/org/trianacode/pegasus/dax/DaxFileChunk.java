package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
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
    private boolean one2one = false;
    private UUID uuid = null;
    private Vector<DaxJobChunk> inJobChunks = new Vector();
    private Vector<DaxJobChunk> outJobChunks = new Vector();
    private int numberOfFiles = 1;
    private PatternCollection namePattern = null;
    private int counter = 0;
    private String fileLocation = "";
    private String fileProtocol;

    public String getFilename() {
        if (namePattern == null || !isCollection) {
            log("returning filename");
            return filename;
        } else {
            log("returning next patterned name");
            return namePattern.next();
        }
    }

    @Deprecated
    public String getNextFilename() {
//       if(counter < numberOfFiles){
        String returnName = getFilename() + "-" + counter;
        counter++;
        return returnName;

//        }
//        else{
//            return null;
//        }
    }

    public void resetNextCounter() {
        counter = 0;
        if (namePattern != null) {
            namePattern.resetCount();
        }
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

    public void listChunks() {
        for (DaxJobChunk c : inJobChunks) {
            log(" ******* File : " + getFilename() + " has input : " + c.getJobName());
        }
        for (DaxJobChunk c : outJobChunks) {
            log(" ******* File : " + getFilename() + " has output : " + c.getJobName());
        }
    }

    public void setNumberOfFiles(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public int getNumberOfFiles() {
        if (isCollection()) {
            return numberOfFiles;
        } else {
            return 1;
        }
    }

    public PatternCollection getNamePattern() {
        return namePattern;
    }

    public void setNamePattern(PatternCollection namePattern) {
        log("Setting pattern in fileChunk to : " + namePattern);
        this.namePattern = namePattern;
    }

    private void log(String s) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        //     System.out.println(s);
    }

    public boolean isOne2one() {
        return one2one;
    }

    public void setOne2one(boolean one2one) {
        this.one2one = one2one;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public String getFileProtocol() {
        return fileProtocol;
    }

    public void setFileProtocol(String fileProtocol) {
        this.fileProtocol = fileProtocol;
    }
}
