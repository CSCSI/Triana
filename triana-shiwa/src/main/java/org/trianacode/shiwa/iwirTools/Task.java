package org.trianacode.shiwa.iwirTools;

import org.apache.commons.logging.Log;
import org.trianacode.annotation.*;
import org.trianacode.annotation.Process;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.shiwa.iwirTools.creation.IwirRegister;
import org.trianacode.shiwa.iwirTools.creation.IwirTaskChunk;

import java.util.List;
import java.util.UUID;


/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 09/03/2011
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
@Tool(panelClass = "org.trianacode.shiwa.iwirTools.TaskPanel")
public class Task {

    @SliderParameter
    String slide = "0";
    @Parameter
    private int numberOfFiles = 1;
    @TextFieldParameter
    private String taskName = "bin";
    @CheckboxParameter
    private boolean collection = false;
    @CheckboxParameter
    private boolean one2one = false;


    @Process(gather = true)
    public Object process(List in) {

        log("\nFile : " + taskName + " Collection = " + collection + " Number of files : " + numberOfFiles);
        IwirTaskChunk thisTask = new IwirTaskChunk();

        thisTask.setTaskName(taskName);
        thisTask.setUuid(UUID.randomUUID());
        thisTask.setCollection(collection);
        thisTask.setNumberOfTasks(numberOfFiles);
        thisTask.setOne2one(one2one);

        //    GUIEnv.getApplicationFrame().getSelectedTaskgraph().getToolName();
        IwirRegister register = IwirRegister.getDaxRegister();
        register.addTask(thisTask);

        //   log("\nList in is size: " + in.size() + " contains : " + in.toString() + ".\n ");

        System.out.println("Running iwir task");
        for (Object object : in) {
            System.out.println("Incoming object");
            if (object instanceof UUID) {
                UUID uuid = (UUID) object;
                IwirTaskChunk taskChunk = register.getTaskChunkFromUUID(uuid);

                if (taskChunk != null) {

                    log("\nPrevious job was : " + taskChunk.getTaskName() + "\n");

                    log("Adding : " + thisTask.getTaskName() + " as an output to job : " + taskChunk.getTaskName());
                    taskChunk.addOutTaskChunk(thisTask);

                    log("Adding : " + taskChunk.getTaskName() + " as an input to file : " + thisTask.getTaskName());
                    thisTask.addInTaskChunk(taskChunk);
                } else {
                    log("jobChunk not found in register");
                }
            } else {
                log("Cannot handle input : " + object.getClass().getName());
            }
        }
        return thisTask.getUuid();
    }

    private void log(String s) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }
}
