package org.trianacode.shiwaall.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.shiwaall.string.PatternCollection;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Sep 20, 2010
 * Time: 1:43:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxFileChunk implements Serializable {
    
    /** The filename. */
    private String filename = "";
    
    /** The is collection. */
    private boolean isCollection = false;
    
    /** The one2one. */
    private boolean one2one = false;
    
    /** The uuid. */
    private UUID uuid = null;
    
    /** The in job chunks. */
    private Vector<DaxJobChunk> inJobChunks = new Vector<DaxJobChunk>();
    
    /** The out job chunks. */
    private Vector<DaxJobChunk> outJobChunks = new Vector<DaxJobChunk>();
    
    /** The number of files. */
    private int numberOfFiles = 1;
    
    /** The name pattern. */
    private PatternCollection namePattern = null;
    
    /** The counter. */
    private int counter = 0;
    
    /** The file location. */
    private String fileLocation = "";
    
    /** The file protocol. */
    private String fileProtocol = "";
    
    /** The physical file. */
    private boolean physicalFile = false;

    /** The dev log. */
    Log devLog = Loggers.DEV_LOGGER;


    // Use Carefully!! This method has the potential to never return the same filename twice.
    // Call reset once you're done listing auto-generated names, to begin the pattern again.
    /**
     * Gets the filename.
     *
     * @return the filename
     */
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

    /**
     * Gets the next filename.
     *
     * @return the next filename
     */
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

    /**
     * Reset next counter.
     */
    public void resetNextCounter() {
        counter = 0;
        if (namePattern != null) {
            namePattern.resetCount();
        }
        devLog.debug("Reset counter for " + filename);
    }

    /**
     * Sets the filename.
     *
     * @param filename the new filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
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
     * Gets the uuid.
     *
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
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
     * Gets the in job chunks.
     *
     * @return the in job chunks
     */
    public List getInJobChunks() {
        return inJobChunks;
    }

    /**
     * Adds the in job chunk.
     *
     * @param chunk the chunk
     */
    public void addInJobChunk(DaxJobChunk chunk) {
        inJobChunks.add(chunk);
    }

    /**
     * Sets the in job chunks.
     *
     * @param inJobChunks the new in job chunks
     */
    public void setInJobChunks(Vector inJobChunks) {
        this.inJobChunks = inJobChunks;
    }

    /**
     * Gets the out job chunks.
     *
     * @return the out job chunks
     */
    public List getOutJobChunks() {
        return outJobChunks;
    }

    /**
     * Adds the out job chunk.
     *
     * @param chunk the chunk
     */
    public void addOutJobChunk(DaxJobChunk chunk) {
        outJobChunks.add(chunk);
    }

    /**
     * Sets the out job chunks.
     *
     * @param outJobChunks the new out job chunks
     */
    public void setOutJobChunks(Vector outJobChunks) {
        this.outJobChunks = outJobChunks;
    }

    /**
     * List chunks.
     */
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

    /**
     * Sets the number of files.
     *
     * @param numberOfFiles the new number of files
     */
    public void setNumberOfFiles(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    /**
     * Gets the number of files.
     *
     * @return the number of files
     */
    public int getNumberOfFiles() {
        if (isCollection()) {
            return numberOfFiles;
        } else {
            return 1;
        }
    }

    /**
     * Gets the name pattern.
     *
     * @return the name pattern
     */
    public PatternCollection getNamePattern() {
        return namePattern;
    }

    /**
     * Sets the name pattern.
     *
     * @param namePattern the new name pattern
     */
    public void setNamePattern(PatternCollection namePattern) {
        this.namePattern = namePattern;
    }

    /**
     * Checks if is one2one.
     *
     * @return true, if is one2one
     */
    public boolean isOne2one() {
        return one2one;
    }

    /**
     * Sets the one2one.
     *
     * @param one2one the new one2one
     */
    public void setOne2one(boolean one2one) {
        this.one2one = one2one;
    }

    /**
     * Sets the file location.
     *
     * @param fileLocation the new file location
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    /**
     * Gets the file location.
     *
     * @return the file location
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /**
     * Gets the file protocol.
     *
     * @return the file protocol
     */
    public String getFileProtocol() {
        return fileProtocol;
    }

    /**
     * Sets the file protocol.
     *
     * @param fileProtocol the new file protocol
     */
    public void setFileProtocol(String fileProtocol) {
        this.fileProtocol = fileProtocol;
    }

    /**
     * Checks if is physical file.
     *
     * @return true, if is physical file
     */
    public boolean isPhysicalFile() {
        return physicalFile;
    }

    /**
     * Sets the physical file.
     *
     * @param physicalFile the new physical file
     */
    public void setPhysicalFile(boolean physicalFile) {
        this.physicalFile = physicalFile;
    }
}
