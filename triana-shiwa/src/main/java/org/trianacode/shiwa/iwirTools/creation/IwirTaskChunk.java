package org.trianacode.shiwa.iwirTools.creation;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 14/04/2011
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */

public class IwirTaskChunk implements Serializable {
    private String taskName = "";
    private boolean isCollection = false;
    private boolean one2one = false;
    private UUID uuid = null;
    private Vector<IwirTaskChunk> inTaskChunks = new Vector();
    private Vector<IwirTaskChunk> outTaskChunks = new Vector();

    private int numberOfFiles = 1;
    private int counter = 0;

    public String getTaskName() {
        return taskName;
    }

    @Deprecated
    public String getNextTaskName() {
//       if(counter < numberOfFiles){
        String returnName = getTaskName() + "-" + counter;
        counter++;
        return returnName;

//        }
//        else{
//            return null;
//        }
    }


    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

    public List getInTaskChunks() {
        return inTaskChunks;
    }

    public void addInTaskChunk(IwirTaskChunk chunk) {
        inTaskChunks.add(chunk);
    }

    public void setInTaskChunks(Vector inTaskChunks) {
        this.inTaskChunks = inTaskChunks;
    }

    public List getOutTaskChunks() {
        return outTaskChunks;
    }

    public void addOutTaskChunk(IwirTaskChunk chunk) {
        outTaskChunks.add(chunk);
    }

    public void setOutTaskChunks(Vector outJobChunks) {
        this.outTaskChunks = outJobChunks;
    }

    public void listChunks() {
        for (IwirTaskChunk c : inTaskChunks) {
            log(" ******* File : " + getTaskName() + " has input : " + c.getTaskName());
        }
    }

    public void setNumberOfTasks(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public int getNumberOfTasks() {
        if (isCollection()) {
            return numberOfFiles;
        } else {
            return 1;
        }
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


}
