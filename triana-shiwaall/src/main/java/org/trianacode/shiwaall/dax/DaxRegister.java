package org.trianacode.shiwaall.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Sep 17, 2010
 * Time: 4:47:50 PM
 * To change this template use File | Settings | File Templates.
 * TODO use concurrent hashmap
 */
public class DaxRegister {
    
    /** The register. */
    private static DaxRegister register = new DaxRegister();
    
    /** The files. */
    List<String> files = new ArrayList<String>();
    
    /** The jobs. */
    List<String> jobs = new ArrayList<String>();
    
    /** The file chunks. */
    List<DaxFileChunk> fileChunks = new ArrayList<DaxFileChunk>();

    /** The job chunks. */
    List<DaxJobChunk> jobChunks = new ArrayList<DaxJobChunk>();

    /** The dev log. */
    Log devLog = Loggers.DEV_LOGGER;

    /**
     * Instantiates a new dax register.
     */
    private DaxRegister() {
    }

    /**
     * Gets the dax register.
     *
     * @return the dax register
     */
    public static DaxRegister getDaxRegister() {
        return register;
    }

    /**
     * List all.
     */
    public synchronized void listAll() {
        devLog.debug("+++++++++++++++++++++++++++++++LIST ALL +++++++++++++++++++++++++");
        for (DaxJobChunk chunk : jobChunks) {
            chunk.listChunks();
        }
        for (DaxFileChunk chunk : fileChunks) {
            chunk.listChunks();
        }
        devLog.debug("------------------------------END LIST ALL -----------------------");

    }

    /**
     * Adds the file.
     *
     * @param thisFile the this file
     */
    public synchronized void addFile(DaxFileChunk thisFile) {
        boolean duplicate = false;
        for (DaxFileChunk chunk : fileChunks) {
            if (chunk == thisFile) {
                duplicate = true;
                devLog.debug("Found a duplicate");
            }
        }
        if (!duplicate) {
            fileChunks.add(thisFile);
        }
        //    listAll();
    }

    /**
     * Adds the job.
     *
     * @param thisJob the this job
     */
    public synchronized void addJob(DaxJobChunk thisJob) {
        boolean duplicate = false;
        for (DaxJobChunk chunk : jobChunks) {
            if (chunk == thisJob) {
                duplicate = true;
                devLog.debug("Found a duplicate");
            }
        }
        if (!duplicate) {
            jobChunks.add(thisJob);
        }
        //   listAll();
    }

    /**
     * Gets the job chunks.
     *
     * @return the job chunks
     */
    public synchronized List<DaxJobChunk> getJobChunks() {
        return jobChunks;
    }

    /**
     * Gets the job chunk from uuid.
     *
     * @param uuid the uuid
     * @return the job chunk from uuid
     */
    public synchronized DaxJobChunk getJobChunkFromUUID(UUID uuid) {
        for (DaxJobChunk chunk : jobChunks) {
            UUID toCheck = chunk.getUuid();
            //      devLog.debug"Checking : " + uuid + " with : " + toCheck );
            if (uuid.equals(toCheck)) {
//                devLog.debug("Register returning : " + chunk.getJobName());
                return chunk;
            }
        }
        return null;
    }

    /**
     * Gets the file chunk from uuid.
     *
     * @param uuid the uuid
     * @return the file chunk from uuid
     */
    public synchronized DaxFileChunk getFileChunkFromUUID(UUID uuid) {
        for (DaxFileChunk chunk : fileChunks) {
            UUID toCheck = chunk.getUuid();
            //    devLog.debug"Checking : " + uuid + " with : " + toCheck );
            if (uuid.equals(toCheck)) {
//                devLog.debug("Register returning : " + chunk.getFilename());
                return chunk;
            }
        }
        return null;
    }

    /**
     * Clear.
     */
    public void clear() {
        fileChunks.clear();
        jobChunks.clear();
    }
}
