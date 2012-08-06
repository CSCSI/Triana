package org.trianacode.shiwaall.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Sep 17, 2010
 * Time: 4:47:50 PM
 * To change this template use File | Settings | File Templates.
 * TODO use concurrent hashmap
 */
public class DaxRegister {
    private static DaxRegister register = new DaxRegister();
    List<String> files = new ArrayList<String>();
    List<String> jobs = new ArrayList<String>();
    List<DaxFileChunk> fileChunks = new ArrayList<DaxFileChunk>();

    List<DaxJobChunk> jobChunks = new ArrayList<DaxJobChunk>();

    Log devLog = Loggers.DEV_LOGGER;

    private DaxRegister() {
    }

    public static DaxRegister getDaxRegister() {
        return register;
    }

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

    public synchronized List<DaxJobChunk> getJobChunks() {
        return jobChunks;
    }

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

    public void clear() {
        fileChunks.clear();
        jobChunks.clear();
    }
}
