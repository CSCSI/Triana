package org.trianacode.pegasus.dax;

import org.griphyn.vdl.dax.ADAG;
import org.griphyn.vdl.dax.Filename;
import org.griphyn.vdl.dax.Job;
import org.griphyn.vdl.dax.PseudoText;
import org.trianacode.gui.extensions.AbstractFormatFilter;
import org.trianacode.gui.extensions.TaskGraphExporterInterface;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.annotation.AnnotatedUnitWrapper;
import org.trianacode.taskgraph.proxy.java.JavaProxy;

import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 23, 2010
 * Time: 10:33:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxSaver extends AbstractFormatFilter implements TaskGraphExporterInterface {

    public DaxSaver(){
    }

    public String toString(){
        return "DaxSaver";
    }

    @Override
    public void exportWorkflow(TaskGraph taskgraph, File file, boolean appendSuffix) throws IOException, TaskGraphException {
        System.out.println("Beginning save to DAX");
        ADAG dax = new ADAG();

        Task[] tasks = taskgraph.getTasks(false);
        int i = 0;
        for(Task task : tasks){
            System.out.println("\nTaskGraph contains task : " + task.toString() + " (" + task.getToolName() + ")");

            JavaProxy proxy = (JavaProxy)task.getProxy();
            Unit unit = proxy.getUnit();
            if(unit instanceof AnnotatedUnitWrapper){
                AnnotatedUnitWrapper auw = (AnnotatedUnitWrapper)unit;
                System.out.println("It's a auw");
            }
            Object param = task.getParameter("args");
            System.out.println("Unitname from proxy : " + proxy.getUnitName() + " Unit : " + unit.toString());
            System.out.println("*args* : " + param);

            if(proxy.getUnitName().equals("JobUnit")){
                AnnotatedUnitWrapper au = (AnnotatedUnitWrapper)unit;
                System.out.println(">>" + au.toString());
                
                i++;
                Job job = new Job();

                String args = (String)task.getParameter("args");
                
                job.addArgument(new PseudoText(args));

                String name = "getName()";
                job.setName(name);

                String id = "0000000" + i;
                job.setID("ID" + id.substring(id.length() - 7));

                Node[] inNodes = task.getInputNodes();
                for(Node inNode : inNodes){
                    if(inNode.isConnected()){
                        String unitOut = inNode.getCable().getSendingTask().toString();
                        System.out.println("    Input node is connected : " + unitOut);
                        job.addUses(new Filename(unitOut, 1));
                    }
                }

                Node[] outNodes = task.getOutputNodes();
                for(Node outNode : outNodes){
                    if(outNode.isConnected()){
                        String unitIn = outNode.getCable().getReceivingTask().toString();
                        System.out.println("    Output node is connected : " + unitIn);
                        job.addUses(new Filename(unitIn, 2));
                    }
                }

                dax.addJob(job);
            }
        }

        System.out.println("ADAG has " + dax.getJobCount() + " jobs in.");

        try {
            FileWriter fw = new FileWriter(file);
            dax.toXML(fw, "", null );
            fw.close();
            System.out.println("File " + file + " saved.\n");
        } catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("Saved to " + file);
    }

    @Override
    public String getFilterDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FileFilter[] getChoosableFileFilters() {
        return new FileFilter[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FileFilter getDefaultFileFilter() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasOptions() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int showOptionsDialog(Component parent) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
