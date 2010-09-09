package org.trianacode.pegasus.dax;

import org.trianacode.taskgraph.annotation.*;
import org.trianacode.taskgraph.annotation.Process;

import java.util.ArrayList;
import java.util.List;

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

    @TextFieldParameter
    private String fileName = "a.txt";

    @CheckboxParameter
    private boolean collection = false;

    @ChoiceParameter
    private String[] pattern = {""};

    @Process(gather = true)
    public List echoWithNewLine(List in){
        String endString = fileName;
        System.out.println("\nList in is size: " + in.size() + " contains : " + in.toString() + ".\n ");
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
                                System.out.println("Added output file to job " + (i+1) + " of " + inList.size() + ".");
                                ((DaxJobChunk) o2).setOutputFilename(fileName);
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
    }

}