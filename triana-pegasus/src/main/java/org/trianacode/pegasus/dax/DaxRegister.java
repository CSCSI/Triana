package org.trianacode.pegasus.dax;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Sep 17, 2010
 * Time: 4:47:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxRegister {
    private static DaxRegister register = new DaxRegister();
    List<String> files = new ArrayList();
    List<String> jobs = new ArrayList();
    List<DaxFileChunk> fileChunks = new ArrayList();

    List<DaxJobChunk> jobChunks = new ArrayList();

    private DaxRegister(){}

    public static DaxRegister getDaxRegister(){
        return register;
    }

    public synchronized void listAll(){
        System.out.println("+++++++++++++++++++++++++++++++LIST ALL +++++++++++++++++++++++++");
        for(DaxJobChunk chunk : jobChunks){
            chunk.listChunks();
        }
        for(DaxFileChunk chunk : fileChunks){
            chunk.listChunks();
        }
        System.out.println("------------------------------END LIST ALL -----------------------");

    }

    public synchronized void addFile(DaxFileChunk thisFile) {
        boolean duplicate = false;
        for(DaxFileChunk chunk : fileChunks){
            if(chunk == thisFile){
                duplicate = true;
                System.out.println("Found a duplicate");
            }
        }
        if(!duplicate){
            fileChunks.add(thisFile);
        }
    //    listAll();
    }

    public synchronized void addJob(DaxJobChunk thisJob) {
        boolean duplicate = false;
        for(DaxJobChunk chunk : jobChunks){
            if(chunk == thisJob){
                duplicate = true;
                System.out.println("Found a duplicate");
            }
        }
        if(!duplicate){
            jobChunks.add(thisJob);
        }
     //   listAll();
    }

    public synchronized List<DaxJobChunk> getJobChunks() {
        return jobChunks;
    }

    public synchronized DaxJobChunk getJobChunkFromUUID(UUID uuid){
        for(DaxJobChunk chunk : jobChunks){
            UUID toCheck = chunk.getUuid();
      //      System.out.println("Checking : " + uuid + " with : " + toCheck );
            if(uuid.equals(toCheck)){
                System.out.println("Register returning : " + chunk.getJobName());
                return chunk;
            }
        }
        return null;
    }

    public synchronized DaxFileChunk getFileChunkFromUUID(UUID uuid){
        for(DaxFileChunk chunk : fileChunks){
            UUID toCheck = chunk.getUuid();
        //    System.out.println("Checking : " + uuid + " with : " + toCheck );
            if(uuid.equals(toCheck)){
                System.out.println("Register returning : " + chunk.getFilename());
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
