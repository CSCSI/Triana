package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.pegasus.string.PatternCollection;
import org.trianacode.taskgraph.annotation.*;
import org.trianacode.taskgraph.annotation.Process;

import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 19, 2010
 * Time: 11:08:09 AM
 * To change this template use File | Settings | File Templates.
 */

@Tool(panelClass = "org.trianacode.pegasus.dax.FileUnitPanel", renderingHints = {"DAX_FILE_RENDERING_HINT"})
public class FileUnit {

    @Parameter
    private String programmaticParam = "This is a file";
    @Parameter
    private int numberOfFiles = 1;
    @Parameter
    private PatternCollection namingPattern = null;

    @TextFieldParameter
    private String fileName = "a.txt";

    @CheckboxParameter
    private boolean collection = false;

    @Process(gather = true)
    public UUID fileUnitProcess(List in){

        

        log("File : " + fileName + " Collection = " + collection + " Number of files : " + numberOfFiles);
        DaxFileChunk thisFile = new DaxFileChunk();

        thisFile.setFilename(fileName);
        thisFile.setUuid(UUID.randomUUID());
        thisFile.setCollection(collection);
        thisFile.setNumberOfFiles(numberOfFiles);
        thisFile.setNamePattern(namingPattern);

        DaxRegister register = DaxRegister.getDaxRegister();
        register.addFile(thisFile);

        log("\nList in is size: " + in.size() + " contains : " + in.toString() + ".\n ");

        for(Object object : in){
            if(object instanceof DaxSettingObject){
                log("Found settings object");
                DaxSettingObject dso = (DaxSettingObject)object;
                int number = dso.getNumberFiles();
                log("Found number of files from settings object : " + number);
                thisFile.setNumberOfFiles(number);
            }

            if(object instanceof DaxJobChunk){
                DaxJobChunk jobChunk = (DaxJobChunk)object;

                log("Previous job was : " + jobChunk.getJobName());

                log("Adding : " + thisFile.getFilename() + " as an output to job : " + jobChunk.getJobName());
                jobChunk.addOutFileChunk(thisFile);

                log("Adding : " + jobChunk.getJobName() + " as an input to file : " + thisFile.getFilename());
                thisFile.addInJobChunk(jobChunk);

            }

            else if(object instanceof UUID){
                UUID uuid = (UUID)object;
                DaxJobChunk jobChunk = register.getJobChunkFromUUID(uuid);

                if(jobChunk != null){

                    log("\nPrevious job was : " + jobChunk.getJobName() + "\n");

                    log("Adding : " + thisFile.getFilename() + " as an output to job : " + jobChunk.getJobName());
                    jobChunk.addOutFileChunk(thisFile);

                    log("Adding : " + jobChunk.getJobName() + " as an input to file : " + thisFile.getFilename());
                    thisFile.addInJobChunk(jobChunk);
                }
                else{
                    log("jobChunk not found in register");
                }
            }

            else{
                log("Cannot handle input : " + object.getClass().getName());
            }

        }

        if(in.size() == 0){
            log("No jobs enter fileUnit : " + thisFile.getFilename());
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
        return thisFile.getUuid();
    }

    private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        //System.out.println(s);
    }
}