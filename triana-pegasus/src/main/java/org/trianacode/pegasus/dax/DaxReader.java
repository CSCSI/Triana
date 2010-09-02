package org.trianacode.pegasus.dax;

import org.trianacode.gui.extensions.AbstractFormatFilter;
import org.trianacode.gui.extensions.TaskGraphImporterInterface;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.TaskGraphOrganize;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.ser.ObjectMarshaller;
import org.trianacode.taskgraph.tool.Tool;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Aug 18, 2010
 * Time: 2:07:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxReader extends AbstractFormatFilter implements TaskGraphImporterInterface {

    private String toolpackage = "dax";

    private File file = null;
    private Document doc = null;
    private Vector<DaxJobHolder> toolVector = new Vector<DaxJobHolder>();
    private Vector<DaxFileHolder> files = new Vector<DaxFileHolder>();

    public static void main(String[] args){
        String filename = "";
        if(args.length > 0){
            filename = args[0];
        }else{
            filename = "../pegasus/diamond.dax";
        }
        new DaxReader(filename);
    }

    public DaxReader(String filename){
        setFile(new File(filename));
        getDaxInfo();
    }

    public void DaxReaderWithChooser(){
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(GUIEnv.getApplicationFrame()) == JFileChooser.APPROVE_OPTION) {
            setFile(fc.getSelectedFile());
            NodeList jobList = getDaxInfo();
            if(jobList != null){
                createTaskGraph(jobList);
            }
        }
    }

    public DaxReader(){}

    private void reset() {
        file = null;
        doc = null;
        toolVector = new Vector();
        files = new Vector();
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

    public String toString(){
        return "DaxReader";
    }

    /**
     * Returns a list of all the nodes labelled "job"
     * @return
     */
    private NodeList getDaxInfo() {
        NodeList jobList = null;
        if (isValidDax()) {
            jobList = getNodeListFromTag("job");
            System.out.println("There are " + jobList.getLength() + " jobs listed in the DAX");

        }
        return jobList;
    }

    private TaskGraph createTaskGraph(NodeList jobList){
        TaskGraph tg = null;

        try {
     //       tg = GUIEnv.getApplicationFrame().addParentTaskGraphPanel(TaskGraphManager.createTaskGraph());
            tg = TaskGraphManager.createTaskGraph();
        } catch (TaskException e) {
            e.printStackTrace();
        }

        findFiles();
        for(Iterator iter = files.iterator(); iter.hasNext();){
            DaxFileHolder dfh = (DaxFileHolder)(iter.next());
            ToolImp tool = new ToolImp();
            initFileTool(tool, dfh);
            try {
                tg.createTask(tool);
            } catch (TaskException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < jobList.getLength(); i++){
            Node node = jobList.item(i);
            NamedNodeMap map = node.getAttributes();
            System.out.println("Job " + i + " has " + map.getLength() + " attributes : " + listAllAttributes(map));

            ToolImp tool = new ToolImp();
            initJobTool(tool, node);


            try {
                Task task = tg.createTask(tool);

                JavaProxy jp = (JavaProxy)tool.getProxy();
    //            jp.getUnit().setRunnableInterface();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        for(Iterator iter = toolVector.iterator(); iter.hasNext();){
            DaxJobHolder djh = (DaxJobHolder)(iter.next());
            for (int i = 0; i < djh.getNumInputNodes(); i++){
            System.out.println("Job : " + djh.getToolname() + " has file : " + djh.getLinkAtInNode(i) +
                    " at input node " + i);
            }
            for(int i = 0; i < djh.getNumOutputNodes(); i++){
            System.out.println("Job : " + djh.getToolname() + " has file : " + djh.getLinkAtOutNode(i) + " at output node " + i);

            }
        }

        attachCables(tg);
        TaskGraphOrganize.organizeTaskGraph(0, tg);

        return tg;

    }

    private Task getTaskFromTool(Tool tool){
        Task task = null;

        try {
            Proxy p = tool.getProxy();
            if(p instanceof JavaProxy){
                JavaProxy jp = (JavaProxy)p;
                Unit unit = jp.getUnit();
                System.out.println("Got unit from proxy.");
                task = unit.getTask();
                System.out.println("Got task from unit.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return task;
    }

    private void attachCables(TaskGraph tg){
        for(Iterator iter  =  toolVector.iterator(); iter.hasNext();){
            DaxJobHolder djh = (DaxJobHolder)iter.next();
            Task jobTask = getTaskFromTool(djh.getTool());
            if(jobTask == null){
                jobTask = tg.getTask(djh.getJobID());
            }
            
            for(int i = 0; i < djh.getNumInputNodes(); i++){
                String link = djh.getLinkAtInNode(i);
                org.trianacode.taskgraph.Node fileNode = null;
                for(Iterator iter2 = files.iterator(); iter2.hasNext();){
                    DaxFileHolder dfh = (DaxFileHolder)iter2.next();
                    if(dfh.getFilename().equals(link)){
                    String filesJobID = dfh.getJobAtOutNode(0);
                    String jobsID = djh.getJobID();
        //            if(!(filesJobID == null) && filesJobID.equals(jobsID)){
                        Task fileTask = tg.getTask(dfh.getFilename());
                        fileNode = fileTask.getOutputNode(0);
                        System.out.println("Job : " + djh.getJobID() + " will connect input node " + i + " to file " + dfh.getFilename());
                        break;
                    }
                }
                try {
                    tg.connect(fileNode, jobTask.getDataInputNode(i));
                } catch (CableException e) {
                    e.printStackTrace();
                }
            }

            for(int i = 0; i < djh.getNumOutputNodes(); i++){
                String link = djh.getLinkAtOutNode(i);
                org.trianacode.taskgraph.Node fileNode = null;
                for(Iterator iter2 = files.iterator(); iter2.hasNext();){
                    DaxFileHolder dfh = (DaxFileHolder)iter2.next();
                    if(dfh.getFilename().equals(link)){
                    String filesJobID = dfh.getJobAtInNode(0);
                    String jobsID = djh.getJobID();
        //            if(!(filesJobID == null) && filesJobID.equals(jobsID)){
                        Task fileTask = tg.getTask(dfh.getFilename());
                        fileNode = fileTask.getInputNode(0);
                        System.out.println("Job : " + djh.getJobID() + " will connect output node " + i + " to file " + dfh.getFilename());
                        break;
                    }
                }
                try {
                    tg.connect(jobTask.getDataOutputNode(i), fileNode);
                } catch (CableException e) {
                    e.printStackTrace();
                }
            }
        }
    }

/*
    public void attachCables(TaskGraph tg){
        for(Iterator fileIter = files.iterator(); fileIter.hasNext()){
            DaxFileHolder dfh = (DaxFileHolder)fileIter.next();

        }
    }
  */

    private void findFiles(){
        NodeList jobLst = doc.getElementsByTagName("job");
        Vector<String> filenames = new Vector<String>();

        for(int j = 0; j < jobLst.getLength(); j++){
            Node jobNode = jobLst.item(j);
            NodeList nodeLst = jobNode.getChildNodes();

            for(int i = 0; i < nodeLst.getLength(); i++){
                Node node = nodeLst.item(i);
                int nodeCount = 0;
                if(node.getNodeName().equals("uses")){
        //    NamedNodeMap map = node.getAttributes();
        //    String fileName = map.getNamedItem("file").getNodeValue();
                    String fileName = getNodeAttributeValue(node, "file");
                    System.out.println("Getting info for file : " + fileName);
            
                    if(!filenames.contains(fileName)){
                        filenames.add(fileName);
                        DaxFileHolder dfh = new DaxFileHolder();
                        dfh.setFilename(fileName);
                        String parentID = getNodeAttributeValue(jobNode, "id");
                        System.out.println("Files parent is job : " + parentID);
                        if(getNodeAttributeValue(node, "link").equals("input")){
                            dfh.addJobOut(nodeCount, parentID);
                            System.out.println("File " + fileName + " has job " + parentID + " attached to output node : " + nodeCount);
                        }
                        if(getNodeAttributeValue(node, "link").equals("output")){
                            dfh.addJobIn(nodeCount, parentID);
                            System.out.println("File " + fileName + " has job " + parentID + " attached to input node : " + nodeCount);

                        }

                        files.add(dfh);
                    }
                    else{
                        for(Iterator iter = files.iterator(); iter.hasNext();){
                            DaxFileHolder dfh = (DaxFileHolder)iter.next();
                            if(dfh.getFilename().equals(fileName)){
                                String parentID = getNodeAttributeValue(jobNode, "id");
                                System.out.println("Files parent is job : " + parentID);
                                if(getNodeAttributeValue(node, "link").equals("input")){
                                    dfh.addJobOut(nodeCount, parentID);
                                    System.out.println("File " + fileName + " has job " + parentID + " attached to output node : " + nodeCount);
                                }
                                if(getNodeAttributeValue(node, "link").equals("output")){
                                    dfh.addJobIn(nodeCount, parentID);
                                    System.out.println("File " + fileName + " has job " + parentID + " attached to input node : " + nodeCount);
                                }

                            }
                        }
                    }                    
                }
                nodeCount ++;

            }
        }

        for(Iterator i = files.iterator(); i.hasNext();){
            DaxFileHolder dfh = (DaxFileHolder)(i.next());
            System.out.println("File : " + dfh.getFilename() + " has job : " + dfh.getJobAtInNode(0) +
                    " at input node 0, and job " + dfh.getJobAtOutNode(0) + " at output node 0.");
        }
    }

    /**
     *    Fills the tool with information taken from the job node of a DAX
     *    All tools created are of type "JobUnit".
     *    Created tool should have the correct number of input and output nodes
     *
     * @param tool
     * @param node
     */
    private void initJobTool(ToolImp tool, Node node) {
        DaxJobHolder djh = new DaxJobHolder();

        String toolname = getNodeAttributeValue(node, "id");
        tool.setToolName(toolname);
        

        int inputNodes = 0;
        int outputNodes = 0;

        NodeList nl = node.getChildNodes();
        for(int i = 0; i < nl.getLength(); i++){
            Node childNode = nl.item(i);
            if(childNode.getNodeName().equals("uses")){
                NamedNodeMap map = childNode.getAttributes();
        //      System.out.println("Job uses : "  + listAllAttributes(map));
                if(map.getNamedItem("link").getNodeValue().equals("input")){
                    djh.addFileIn(inputNodes, map.getNamedItem("file").getNodeValue());
                    inputNodes ++;
                }
                if(map.getNamedItem("link").getNodeValue().equals("output")){
                    djh.addFileOut(outputNodes, map.getNamedItem("file").getNodeValue());
                    outputNodes ++;
                }
            }
        }

        try {
            tool.setDataInputNodeCount(inputNodes);
            tool.setDataOutputNodeCount(outputNodes);
            tool.setToolPackage("org.trianacode.pegasus.dax");
            tool.setProxy(makeProxy("JobUnit", "org.trianacode.pegasus.dax"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        djh.setToolname(toolname);
        djh.setJobID(getNodeAttributeValue(node, "id"));
        djh.setTrianaToolName("T-" + toolname);

        djh.setNumInputNodes(inputNodes);
        djh.setNumOutputNodes(outputNodes);
        djh.setTool(tool);


        System.out.println("Job " + toolname + " has " + inputNodes + " inputNodes, and " + outputNodes  + " outputNodes.");
        String inputs = "";
        String outputs = "";

        for(int i = 0; i < djh.getNumInputNodes(); i++){
            inputs += "Node " + i + " : " + djh.getLinkAtInNode(i) + " ";
        }

        for(int i = 0; i < djh.getNumOutputNodes(); i++){
            outputs += "Node " + i + " : " + djh.getLinkAtOutNode(i) + " ";
        }

        System.out.println("It has " + inputs + " as input, and " + outputs + " as output.\n");

        toolVector.add(djh);

    }

        private void initFileTool(ToolImp tool, DaxFileHolder dfh) {

            tool.setToolName(dfh.getFilename());

            int inputNodes = dfh.getNumInputNodes();
            int outputNodes = dfh.getNumOutputNodes();

            try {
                tool.setDataInputNodeCount(inputNodes);
                tool.setDataOutputNodeCount(outputNodes);
                tool.setToolPackage("org.trianacode.pegasus.dax");
                tool.setProxy(makeProxy("FileUnit", "org.trianacode.pegasus.dax"));
            } catch (TaskException e) {
                e.printStackTrace();
            }

        }

    private Proxy makeProxy(String filename, String packageLocation) {
        HashMap details = new HashMap();
        details.put("unitName", ObjectMarshaller.marshallStringToJava(filename));
        details.put("unitPackage", ObjectMarshaller.marshallStringToJava(packageLocation));
        Proxy proxy = null;
        try {
            proxy = ProxyFactory.createProxy("Java", details);
        } catch (ProxyInstantiationException e) {
            e.printStackTrace();
        }
        return proxy;
    }


    private String getNodeAttributeValue(Node node, String name){
        return node.getAttributes().getNamedItem(name).getNodeValue();
    }

    public File getFile() {
        return file;
    }

    /**
     * set the DAX file to create the workflow from
     * @param file
     */

    public void setFile(File file) {
        reset();
        this.file = file;
        System.out.println("Set dax input file as : " + getFileName());
        setDoc();
    }

    public String getFileName(){
        return file.getName();
    }

    /**
     * Sets the doc variable which will be used as the root node when building tools from the DAX
     */
    private void setDoc(){
        try {
            BufferedInputStream bufferedInput = new BufferedInputStream(new FileInputStream(file));
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(bufferedInput);
            doc.getDocumentElement().normalize();

        }
        catch (Exception e) {

        }
    }

    /**
     * Checks if the first string in the file is "adag"
     * @return
     */
    private boolean isValidDax(){
        String rootString = doc.getDocumentElement().getNodeName();
        System.out.println("Root element : " + rootString);
        if(rootString.equals("adag")){
            System.out.println("File " + getFileName() + " is a valid DAX");
            return true;
        }
        else{
            System.out.println("File " + getFileName() + " is not a valid DAX");
            return false;
        }
    }

    private NodeList getNodeListFromTag(String tagName) {
        NodeList nodeLst = doc.getElementsByTagName(tagName);
        return nodeLst;
    }

    /**
     * Lists all the attributes in the NamedNodeMap
     * @param map
     * @return
     */
    private String listAllAttributes(NamedNodeMap map){
        String listString = "";
        for(int i = 0; i < map.getLength(); i++){
            Node node = map.item(i);
            listString += node.getNodeName();
            listString += " = " + node.getNodeValue() + " : ";

        }
        return listString;
    }

    @Override
    public TaskGraph importWorkflow(File file) throws TaskGraphException, IOException {
        System.out.println("importWorkflow called.");
        setFile(file);
        NodeList jobList = getDaxInfo();
        TaskGraph tg = null;
        if (jobList != null) {
            tg = createTaskGraph(jobList);
        }

        return tg;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
