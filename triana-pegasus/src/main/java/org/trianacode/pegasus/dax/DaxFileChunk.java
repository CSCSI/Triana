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
 * User: Ian Harvey
 * Date: Sep 20, 2010
 * Time: 1:43:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxFileChunk implements Serializable {
    private String filename = "";
    private boolean isCollection = false;
    private boolean one2one = false;
    private UUID uuid = null;
    private Vector<DaxJobChunk> inJobChunks = new Vector<DaxJobChunk>();
    private Vector<DaxJobChunk> outJobChunks = new Vector<DaxJobChunk>();
    private int numberOfFiles = 1;
    private PatternCollection namePattern = null;
    private int counter = 0;
    private String fileLocation = "";
    private String fileProtocol = "";
    private boolean physicalFile = false;

    Log devLog = Loggers.DEV_LOGGER;


    // Use Carefully!! This method has the potential to never return the same filename twice.
    // Call reset once you're done listing auto-generated names, to begin the pattern again.
    public String getFilename() {
        if (namePattern == null || !isCollection) {
            devLog.debug("returning filename from fileChunk : " + filename);
            return filename;
        } else {
            String nextFilename = namePattern.next();
            devLog.debug("returning next patterned name : " + nextFilename);
            return nextFilename;
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
            devLog.debug(" ******* File : " + getFilename() + " has input : " + c.getJobName());
        }
        resetNextCounter();
        for (DaxJobChunk c : outJobChunks) {
            devLog.debug(" ******* File : " + getFilename() + " has output : " + c.getJobName());
        }
        resetNextCounter();
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
        this.namePattern = namePattern;
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

    public boolean isPhysicalFile() {
        return physicalFile;
    }

    public void setPhysicalFile(boolean physicalFile) {
        this.physicalFile = physicalFile;
    }
}
