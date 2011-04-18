package org.trianacode.shiwa.iwirTools.creation;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 14/04/2011
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */

public class IwirRegister {
    private static IwirRegister register = new IwirRegister();
    List<String> files = new ArrayList();
    List<String> jobs = new ArrayList();
    List<IwirTaskChunk> taskChunks = new ArrayList();

    private IwirRegister() {
    }

    public static IwirRegister getDaxRegister() {
        return register;
    }

    public synchronized void listAll() {
        log("+++++++++++++++++++++++++++++++LIST ALL +++++++++++++++++++++++++");
        for (IwirTaskChunk chunk : taskChunks) {
            chunk.listChunks();
        }
    }

    public synchronized void addTask(IwirTaskChunk thisFile) {
        boolean duplicate = false;
        for (IwirTaskChunk chunk : taskChunks) {
            if (chunk == thisFile) {
                duplicate = true;
                log("Found a duplicate");
            }
        }
        if (!duplicate) {
            taskChunks.add(thisFile);
        }
        //    listAll();
    }

    public synchronized List<IwirTaskChunk> getTaskChunks() {
        return taskChunks;
    }

    public synchronized IwirTaskChunk getTaskChunkFromUUID(UUID uuid) {
        for (IwirTaskChunk chunk : taskChunks) {
            UUID toCheck = chunk.getUuid();
            //      log("Checking : " + uuid + " with : " + toCheck );
            if (uuid.equals(toCheck)) {
                log("Register returning : " + chunk.getTaskName());
                return chunk;
            }
        }
        return null;
    }

    public void clear() {
        taskChunks.clear();
    }

    private void log(String s) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        //System.out.println(s);
    }
}
