package org.trianacode.pegasus;

import org.apache.commons.logging.Log;
import org.trianacode.TrianaInstance;
import org.trianacode.discovery.ResolverRegistry;
import org.trianacode.discovery.ToolMetadataResolver;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.module.ModuleClassLoader;
import org.trianacode.taskgraph.CableException;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.ClassLoaders;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolClassLoader;
import org.trianacode.taskgraph.tool.Toolbox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 13/05/2011
 * Time: 10:38
 * To change this template use File | Settings | File Templates.
 */
public class CommandLinePegasus {

    private static String pegasusPackage = "org.trianacode.pegasus.dax";

    public static Tool initTaskgraph(TrianaInstance engine, TaskGraph taskGraph) {
        log("\nBegin init taskgraph for dax create/ submit.");

        //    investigateClassLoaders(engine);
//
//        Class creatorClass = null;
//        Class submitClass = null;
//        try {
//            log("Trying to find " + pegasusPackage + ".DaxCreatorV3");
//            creatorClass = ClassLoaders.forName(pegasusPackage + ".DaxCreatorV3");
//            submitClass = ClassLoaders.forName(pegasusPackage + ".DaxToPegasusUnit");
//        } catch (Exception e) {
//            log("Exception : " + pegasusPackage + " package not found");
//            e.printStackTrace();
//        } catch (Error err) {
//            log("Error : " + pegasusPackage + " package not found");
//            err.printStackTrace();
//        }

        Task creatorTask = null;
        Task submitTask = null;
//        if(creatorClass != null && submitClass != null){
        try{
            ToolImp creatorTool = new ToolImp(engine.getProperties());
            //            initTool(creatorTool, creatorClass.getCanonicalName(), creatorClass.getPackage().getName(), 0, 1);
            initTool(creatorTool, "DaxCreatorV3", pegasusPackage, 1, 1);
            creatorTask = taskGraph.createTask(creatorTool);

            ToolImp submitTool = new ToolImp(engine.getProperties());
            //            initTool(submitTool, submitClass.getCanonicalName(), submitClass.getPackage().getName(), 1, 0);
            initTool(submitTool, "DaxToPegasusUnit", pegasusPackage, 1, 0);
            submitTool.setParameter("locationService", "AUTO");

            submitTask = taskGraph.createTask(submitTool);
            submitTask.addParameterInputNode("manualURL");



//      If daxCreator and daxSubmit tasks were able to be instatiated, connect them together.
            if(creatorTask != null && submitTask != null){
                try {

                    taskGraph.connect(creatorTask.getDataOutputNode(0), submitTask.getDataInputNode(0));
                    log("Connected added tasks");
                } catch (CableException e) {
                    log("Failed to connect task cables");
                    e.printStackTrace();
                }
            }else {
                log("Tasks were null, not connected.");
            }

            Node childNode = getTaskgraphChildNode(taskGraph);
            if(childNode != null){
                taskGraph.connect(childNode, creatorTask.getDataInputNode(0));
            }else{
                log("No child node available to attach daxCreator to.");
            }


        }catch (Exception e){
            e.printStackTrace();
        }
//        }else {
//            log("Class loaders failed to load dax classes");
//        }

        try{
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter("/Users/ian/pegterm.xml"));
            XMLWriter writer = new XMLWriter(fileWriter);
            writer.writeComponent(taskGraph);
        }catch (Exception e){
            log("Failed to write modified xml file");
            e.printStackTrace();
        }
        System.out.println("Taskgraph initialised");
        return (Tool) taskGraph;
    }

    private static void initTool(ToolImp tool, String unitName, String unitPackage, int inNodes, int outNodes) {
        tool.setToolName(unitName);
        try {
            tool.setDataInputNodeCount(inNodes);
            tool.setDataOutputNodeCount(outNodes);
            tool.setToolPackage(unitPackage);
            tool.setProxy(new JavaProxy(unitName, unitPackage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Node getTaskgraphChildNode(TaskGraph taskGraph){
// Find a child task on the taskgraph to attach the daxCreator to, and connect it
        Node childNode = null;
        try{
            Task[] tasks = taskGraph.getTasks(false);
            ArrayList<Task> childTasks = new ArrayList<Task>();
            for (Task task : tasks) {
                if (task.getDataOutputNodeCount() == 0) {
                    childTasks.add(task);
                }
            }
            log("These are the child tasks of the taskgraph (will use the first discovered): ");
            for(Task task : childTasks){
                log(task.getToolName());
            }

            if(childTasks.size() > 0){
                childNode = childTasks.get(0).addDataOutputNode();
            }
        }catch (Exception e){
            log("Failed to add node to child leaf of taskgraph");
        }

        return childNode;
    }

    private static void investigateClassLoaders(TrianaInstance engine){
        log("\nClass loaders");
        List<ToolClassLoader> toolClasses = ClassLoaders.getToolClassLoaders();
        for (ToolClassLoader next : toolClasses) {
            log(next.getClassPath());

            for (String s : next.getClassPathList()) {
                log(s);
            }
            for (String s : next.getLibPaths()){
                log(s);
            }

            URL[] urls = next.getURLs();
            for (URL url : urls) {
                log(url.toString());
            }
        }

        log("\nModule class loaders");
        List<ModuleClassLoader> modulesClasses = ClassLoaders.getModuleClassLoaders();
        for (ModuleClassLoader next : modulesClasses) {
            log(next.getClassPath());
            for(String s : next.getClassPathList()){
                log(s);
            }
        }

        log("Local tools from resolver");
        for(Tool tool : engine.getToolResolver().getTools()){
            log(tool.getQualifiedToolName());
        }

        log("Resolver registy contents");
        Collection<ToolMetadataResolver> resolvers = ResolverRegistry.getResolvers();
        for(ToolMetadataResolver resolver : resolvers){
            log(resolver.getName());
        }

        log("Toolboxes from ToolTable");
        for(Toolbox toolbox : engine.getToolTable().getToolBoxes()){
            log("Toolbox : " + toolbox.getPath());
            for(Tool tool : toolbox.getTools()){
                log(tool.getQualifiedToolName());
            }
        }

        log("Toolboxes from ToolResolver");
        for(Toolbox toolbox : engine.getToolResolver().getToolboxes()){
            log("Toolbox : " + toolbox.getPath());
            for(Tool tool : toolbox.getTools()){
                log(tool.getQualifiedToolName());
            }
        }

        try{
            log("Messing with system classloader");
            Enumeration urls = ClassLoader.getSystemClassLoader().getResources(pegasusPackage + ".DaxCreatorV3");
            while(urls.hasMoreElements()){
                log(urls.nextElement().toString());
            }
        }catch(Exception e){
            log("Error screwing around with system classloader");
        }

    }

    private static void log(String s) {
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }
}
