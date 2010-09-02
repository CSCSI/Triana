package org.trianacode.pegasus.dax;

import org.trianacode.taskgraph.annotation.Parameter;
import org.trianacode.taskgraph.annotation.Process;
import org.trianacode.taskgraph.annotation.TextFieldParameter;
import org.trianacode.taskgraph.annotation.Tool;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 19, 2010
 * Time: 11:08:09 AM
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class JobUnit{

    @Parameter
    private String programmaticParam = "This is a process";

    @TextFieldParameter
    private String name = "_";

    @TextFieldParameter
    private String args = "ls -l ";

    public void setName(String name) {
        this.name = name;
    }

    @Process(gather=true)
    public List process(List in) {
        List fileStrings = new ArrayList();
        System.out.println("\nList in is size: " + in.size() + " contains : " + in.toString() + ".\n ");
        List<DaxJobChunk> jcl = new ArrayList<DaxJobChunk>();
        
        
            List<List> inList = (List<List>)in;
            for(int i = 0; i < inList.size(); i++){
                Object o = inList.get(i);
                if(o instanceof List){
                    List<List> innerList = (List)o;
                    for(int j = 0; j < innerList.size(); j++){
                        Object o2 = innerList.get(j);
                        if(o2 instanceof DaxJobChunk){
                            System.out.println("Found a DaxJobChunk");
                            if(j == (innerList.size() - 1)){
                                DaxJobChunk jobChunk = (DaxJobChunk) o2;
                                System.out.println("This path through workflow includes " + jobChunk.getOutputFilename() + " before this job");
                                fileStrings.add(jobChunk.getOutputFilename());
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

        DaxJobChunk djc = new DaxJobChunk();

        System.out.println("Adding " + fileStrings.size() + " inputs to job.");
        for(int i = 0; i < fileStrings.size(); i++){
            djc.addInFile((String)fileStrings.get(i));
        }

        djc.setJobArgs(args);
        jcl.add(djc);

        System.out.println("\nList out is size: " + jcl.size() + " contains : " + jcl.toString() + ".\n ");

        return jcl;
    }

}