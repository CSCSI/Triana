package org.trianacode.pegasus.dax;

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

    @TextFieldParameter
    private String fileName = "a.txt";

    @CheckboxParameter
    private boolean collection = false;

    @Process(gather = true)
    public UUID fileUnitProcess(List in){

        System.out.println("File : " + fileName + " Collection = " + collection + " Number of files : " + numberOfFiles);
        DaxFileChunk thisFile = new DaxFileChunk();

        thisFile.setFilename(fileName);
        thisFile.setUuid(UUID.randomUUID());
        thisFile.setCollection(collection);
        thisFile.setNumberOfFiles(numberOfFiles);

        DaxRegister register = DaxRegister.getDaxRegister();
        register.addFile(thisFile);

        System.out.println("\nList in is size: " + in.size() + " contains : " + in.toString() + ".\n ");

        for(Object object : in){
            if(object instanceof DaxJobChunk){
                DaxJobChunk jobChunk = (DaxJobChunk)object;

                System.out.println("Previous job was : " + jobChunk.getJobName());

                System.out.println("Adding : " + thisFile.getFilename() + " as an output to job : " + jobChunk.getJobName());
                jobChunk.addOutFileChunk(thisFile);

                System.out.println("Adding : " + jobChunk.getJobName() + " as an input to file : " + thisFile.getFilename());
                thisFile.addInJobChunk(jobChunk);

            }

            else if(object instanceof UUID){
                UUID uuid = (UUID)object;
                DaxJobChunk jobChunk = register.getJobChunkFromUUID(uuid);

                if(jobChunk != null){

                    System.out.println("\nPrevious job was : " + jobChunk.getJobName() + "\n");

                    System.out.println("Adding : " + thisFile.getFilename() + " as an output to job : " + jobChunk.getJobName());
                    jobChunk.addOutFileChunk(thisFile);

                    System.out.println("Adding : " + jobChunk.getJobName() + " as an input to file : " + thisFile.getFilename());
                    thisFile.addInJobChunk(jobChunk);
                }
                else{
                    System.out.println("jobChunk not found in register");
                }
            }

            else{
                System.out.println("Cannot handle input : " + object.getClass().getName());
            }

        }

        if(in.size() == 0){
            System.out.println("No jobs enter fileUnit : " + thisFile.getFilename());
        }

        /*
        List<DaxJobChunk> jcl = new ArrayList<DaxJobChunk>();


            List<List> inList = (List<List>)in;
            for(int i = 0; i < inList.size(); i++){
                Object o = inList.get(i);
                if(o instanceof List){
                    List<DaxJobChunk> innerList = (List)o;

                    for(int j = 0; j < innerList.size(); j++){
                        Object o2 = innerList.get(j);
                        if(o2 instanceof DaxJobChunk){
                            System.out.println("Found a DaxJobChunk");
                            if(j == (innerList.size() - 1)){
                                ((DaxJobChunk)o2).addOutFile(fileName);
                                ((DaxJobChunk)o2).addOutFileChunk(thisFile);
                                System.out.println("Added output file to job " + (i+1) + " of " + inList.size() + ".");
                                ((DaxJobChunk) o2).setOutputFilename(fileName);
                                ((DaxJobChunk) o2).setOutputFileChunk(thisFile);                                                                 
                                System.out.println("Telling the jobs before and after this fileUnit that this file was in between them");                               
                            }
                            jcl.add((DaxJobChunk) o2);
                        }
                        else{
                            System.out.println("Found " + o2.getClass().toString() + " instead of a DaxJobChunk.");
                        }
                    }


                }
                else{
                    System.out.println("Incoming list didn't contain a list, contains : " + o.getClass().toString());
                }
            }
            if(in.size() == 0){
                System.out.println("No jobs handed to this one. Creating job stub with this filename");
                DaxJobChunk jc = new DaxJobChunk();
                jc.setOutputFilename(fileName);
                jc.setStub(true);
                jcl.add(jc);
            }
        return jcl;

        */

        //     register.listAll();
        return thisFile.getUuid();
    }
}