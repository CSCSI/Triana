package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Parameter;
import org.trianacode.annotation.Tool;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.pegasus.string.PatternCollection;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.annotation.TaskConscious;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 19, 2010
 * Time: 11:08:09 AM
 * To change this template use File | Settings | File Templates.
 */

@Tool //(renderingHints = {"DAX File"})
public class FileUnit implements TaskConscious, Displayer {

    @Parameter
    public String locationString = "";   //wtf?
    @Parameter
    public boolean physicalFile = false;

    @Parameter
    public String fileProtocol = "";
    @Parameter
    public int numberOfFiles = 1;
    @Parameter
    public PatternCollection namingPattern = null;


    public String fileName = "a.txt";

    public boolean collection = false;
    public boolean one2one = false;
    public Task task;

    @org.trianacode.annotation.Process(gather = true)
    public UUID fileUnitProcess(List in) {

        log("File : " + fileName + " Collection = " + collection + " Number of files : " + numberOfFiles);
        DaxFileChunk thisFile = new DaxFileChunk();

        thisFile.setFilename(fileName);
        thisFile.setPhysicalFile(physicalFile);
        thisFile.setFileLocation(locationString);
        thisFile.setFileProtocol(fileProtocol);
        thisFile.setUuid(UUID.randomUUID());
        thisFile.setCollection(collection);
        thisFile.setNumberOfFiles(numberOfFiles);
        thisFile.setNamePattern(namingPattern);
        log("setting files one2one as " + one2one);
        thisFile.setOne2one(one2one);

        DaxRegister register = DaxRegister.getDaxRegister();
        register.addFile(thisFile);

        log("\nList in is size: " + in.size() + " contains : " + in.toString() + ".\n ");

        for (Object object : in) {
            if (object instanceof DaxSettingObject) {
                log("Found settings object");
                DaxSettingObject dso = (DaxSettingObject) object;
                int number = dso.getNumberFiles();
                log("Found number of files from settings object : " + number);
                thisFile.setNumberOfFiles(number);
                numberOfFiles = number;
            } else if (object instanceof DaxJobChunk) {
                DaxJobChunk jobChunk = (DaxJobChunk) object;

                log("Previous job was : " + jobChunk.getJobName());

                log("Adding : " + thisFile.getFilename() + " as an output to job : " + jobChunk.getJobName());
                jobChunk.addOutFileChunk(thisFile);

                log("Adding : " + jobChunk.getJobName() + " as an input to file : " + thisFile.getFilename());
                thisFile.addInJobChunk(jobChunk);

            } else if (object instanceof UUID) {
                UUID uuid = (UUID) object;
                DaxJobChunk jobChunk = register.getJobChunkFromUUID(uuid);

                if (jobChunk != null) {

                    log("\nPrevious job was : " + jobChunk.getJobName() + "\n");

                    log("Adding : " + thisFile.getFilename() + " as an output to job : " + jobChunk.getJobName());
                    jobChunk.addOutFileChunk(thisFile);
                    jobChunk.getArgBuilder().addOutputFile(fileName);

                    log("Adding : " + jobChunk.getJobName() + " as an input to file : " + thisFile.getFilename());
                    thisFile.addInJobChunk(jobChunk);
                } else {
                    log("jobChunk not found in register");
                }
            } else {
                log("Cannot handle input : " + object.getClass().getName());
            }

        }

        if (in.size() == 0) {
            log("No jobs enter fileUnit : " + thisFile.getFilename());
        }

        return thisFile.getUuid();
    }

    public void setParams() {
        if (task != null) {
            task.setParameter("numberOfFiles", numberOfFiles);
            task.setParameter("collection", collection);
            task.setParameter("one2one", one2one);

            task.setParameter("physicalFile", physicalFile);
            task.setParameter("fileLocation", locationString);
            task.setParameter("fileProtocol", fileProtocol);

            if (namingPattern != null) {
                task.setParameter("namingPattern", namingPattern);
            }
        }
    }

    public void getParams() {
        if (task != null) {
            fileName = getFileName();
            collection = isCollection();
            numberOfFiles = getNumberOfFiles();
            namingPattern = getNamingPattern();
            one2one = isOne2one();
            physicalFile = isPhysicalFile();
            locationString = getFileLocation();
            fileProtocol = getFileProtocol();
        }
    }

    /**
     * Various getting and setting of parameters
     */
    public void changeToolName(String name) {
        fileName = name;
        if (task != null) {
            log("Changing tool " + task.getToolName() + " to : " + name);
            task.setParameter("fileName", name);
            task.setToolName(name);
        }
    }

    private String getFileName() {
        Object o = task.getParameter("fileName");
        if (o != null && !((String) o).equals("")) {
            return (String) o;
        }
        return fileName;
    }

    public PatternCollection getNamingPattern() {
        Object o = task.getParameter("namingPattern");
        if (o instanceof PatternCollection) {
            log("Found : " + o.toString());
            return (PatternCollection) o;
        }
        return null;
    }

    public boolean isCollection() {
        Object o = task.getParameter("collection");
        if (o != null) {
            if (o instanceof Boolean) {
                return (Boolean) o;
            }
        }
        return false;
    }

    public boolean isOne2one() {
        Object o = task.getParameter("one2one");
        if (o != null) {
            if (o instanceof Boolean) {
                return (Boolean) o;
            }
        }
        return false;
    }

    public int getNumberOfFiles() {
        Object o = task.getParameter("numberOfFiles");
        if (o != null) {
            int value = (Integer) o;
            if (value > 1) {
                return value;
            }
            return 1;
        }
        return 1;
    }

    private boolean isPhysicalFile() {
        Object o = task.getParameter("physicalFile");
        if (o != null) {
            if (o instanceof Boolean) {
                return (Boolean) o;
            }
        }
        return false;
    }

    public String getFileLocation() {
        Object o = task.getParameter("fileLocation");
        if (o != null && !((String) o).equals("")) {
            return (String) o;
        }
        return "";
    }

    public String getFileProtocol() {
        Object o = task.getParameter("fileProtocol");
        if (o != null && !((String) o).equals("")) {
            return (String) o;
        }
        return "";
    }

    private void log(String s) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }

    @CustomGUIComponent
    public Component getComponent() {
        return new JLabel("This is a non-gui tool. Use the triana-pegasus-gui toolbox for more options.");
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
        getParams();
    }

    @Override
    public void displayMessage(String string) {
        log(string);
    }
}

//
//        List<DaxJobChunk> jcl = new ArrayList<DaxJobChunk>();
//
//
//            List<List> inList = (List<List>)in;
//            for(int i = 0; i < inList.size(); i++){
//                Object o = inList.get(i);
//                if(o instanceof List){
//                    List<DaxJobChunk> innerList = (List)o;
//
//                    for(int j = 0; j < innerList.size(); j++){
//                        Object o2 = innerList.get(j);
//                        if(o2 instanceof DaxJobChunk){
//                            log("Found a DaxJobChunk");
//                            if(j == (innerList.size() - 1)){
//                                ((DaxJobChunk)o2).addOutFile(fileName);
//                                ((DaxJobChunk)o2).addOutFileChunk(thisFile);
//                                log("Added output file to job " + (i+1) + " of " + inList.size() + ".");
//                                ((DaxJobChunk) o2).setOutputFilename(fileName);
//                                ((DaxJobChunk) o2).setOutputFileChunk(thisFile);
//                                log("Telling the jobs before and after this fileUnit that this file was in between them");
//                            }
//                            jcl.add((DaxJobChunk) o2);
//                        }
//                        else{
//                            log("Found " + o2.getClass().toString() + " instead of a DaxJobChunk.");
//                        }
//                    }
//
//
//                }
//                else{
//                    log("Incoming list didn't contain a list, contains : " + o.getClass().toString());
//                }
//            }
//            if(in.size() == 0){
//                log("No jobs handed to this one. Creating job stub with this filename");
//                DaxJobChunk jc = new DaxJobChunk();
//                jc.setOutputFilename(fileName);
//                jc.setStub(true);
//                jcl.add(jc);
//            }
//        return jcl;
//
//

//     register.listAll();