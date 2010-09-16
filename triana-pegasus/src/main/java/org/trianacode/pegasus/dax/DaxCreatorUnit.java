package org.trianacode.pegasus.dax;

import org.griphyn.vdl.dax.ADAG;
import org.griphyn.vdl.dax.Filename;
import org.griphyn.vdl.dax.Job;
import org.griphyn.vdl.dax.PseudoText;
import org.trianacode.taskgraph.annotation.Process;
import org.trianacode.taskgraph.annotation.TextFieldParameter;
import org.trianacode.taskgraph.annotation.Tool;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 24, 2010
 * Time: 1:07:14 PM
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class DaxCreatorUnit {

    @TextFieldParameter
    private String fileName = "output.dax";

    @Process
    public String process(List in){
        System.out.println("\nList in is size: " + in.size() + " contains : " + in.toString() + ".\n ");

        ADAG dax = new ADAG();

        for(int j = 0; j < in.size(); j++){
            Object o = in.get(j);
            if(o instanceof DaxJobChunk){
                DaxJobChunk inChunk = (DaxJobChunk)o;

                if(!inChunk.isStub()){
                    Job job = new Job();
                    job.addArgument(new PseudoText(inChunk.getJobArgs()));
                    job.setName("Process");
                    job.setID("ID0000"+(j+1));

                    List inFiles = inChunk.getInFiles();
                    for(int i = 0; i < inFiles.size(); i++){
                        job.addUses(new Filename((String)inFiles.get(i), 1));
                    }
                    List outFiles = inChunk.getOutFiles();
                   for(int i = 0; i < outFiles.size(); i++){
                        job.addUses(new Filename((String)outFiles.get(i), 2));
                    }
                    dax.addJob(job);
                    System.out.println("Added a job to ADAG.");
                }
                else{
                    System.out.println("Found a job stub, ignoring..");

                }
            }
            else{
                System.out.println("*** Found something that wasn't a DaxJobChunk in input List ***");
            }

        }

        System.out.println("ADAG has " + dax.getJobCount() + " jobs in.");

        try {
            FileWriter fw = new FileWriter(fileName);
            dax.toXML(fw, "", null );
            fw.close();
            System.out.println("File " + fileName + " saved.\n");
        } catch (IOException e){
            e.printStackTrace();  
        }

        return "Saved to " + fileName;

    }

}
