package org.trianacode.shiwaall.dax;

import org.apache.commons.logging.Log;
import org.trianacode.config.TrianaProperties;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.Proxy;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.tool.Tool;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Aug 18, 2010
 * Time: 2:07:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class DaxReader {

    /** The file. */
    private File file = null;
    
    /** The doc. */
    private Document doc = null;
    
    /** The tool vector. */
    private ArrayList<DaxJobHolder> toolVector = new ArrayList<DaxJobHolder>();
    
    /** The files. */
    private ArrayList<DaxFileHolder> files = new ArrayList<DaxFileHolder>();
    
    /** The dax package. */
    private String daxPackage = "org.trianacode.shiwaall.gui.guiUnits";
    
    /** The dax file unit name. */
    private String daxFileUnitName = "DaxFile";
    
    /** The dax job unit name. */
    private String daxJobUnitName = "DaxJob";

    /** The dev log. */
    private static Log devLog = Loggers.DEV_LOGGER;
    
    /** The Constant ORIGINAL_DAX_FILE. */
    public static final String ORIGINAL_DAX_FILE = "originalDaxFile";

    /**
     * Instantiates a new dax reader.
     */
    public DaxReader() {
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        String filename = "";
        if (args.length > 0) {
            filename = args[0];
            new DaxReader(filename);
        } else {
            devLog.debug("Set an XML file for input.");
        }
    }

    /**
     * Instantiates a new dax reader.
     *
     * @param filename the filename
     */
    public DaxReader(String filename) {
        setFile(new File(filename));
        getJobInfo();
    }

    /**
     * Instantiates a new dax reader.
     *
     * @param daxPackage the dax package
     * @param file the file
     * @param job the job
     */
    public DaxReader(String daxPackage, String file, String job) {
        this.daxPackage = daxPackage;
        this.daxFileUnitName = file;
        this.daxJobUnitName = job;
    }

    /**
     * Reset.
     */
    private void reset() {
        file = null;
        doc = null;
        toolVector = new ArrayList<DaxJobHolder>();
        files = new ArrayList<DaxFileHolder>();
    }

    /**
     * Import workflow.
     *
     * @param file the file
     * @param properties the properties
     * @return the task graph
     * @throws TaskGraphException the task graph exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public TaskGraph importWorkflow(File file, TrianaProperties properties) throws TaskGraphException, IOException {
        devLog.debug("importWorkflow called.");
        setFile(file);
        NodeList jobList = getJobInfo();
        if (jobList != null) {

            Vector<DaxJobHolder> jobHolders = getJobHoldersFromNodeList(jobList);
            listAllJobs(jobHolders);

            TaskGraph tg = null;
            if (jobList != null) {
                tg = createTaskGraph(jobList, properties);
                tg.setToolName(file.getName());
            }
            tg.setParameter(DaxReader.ORIGINAL_DAX_FILE, file.getAbsolutePath());
            return tg;
        } else {
            return null;
        }
    }

    /**
     * List all jobs.
     *
     * @param jobs the jobs
     */
    private void listAllJobs(Vector<DaxJobHolder> jobs) {
        for (DaxJobHolder job : jobs) {
            devLog.debug("Found job : " + job.getToolname());
            if (job.isCollection()) {
                devLog.debug("Is collection");
            } else {
                devLog.debug("Is not a collection");
            }
        }
    }

    /**
     * set the DAX file to create the workflow from.
     *
     * @param file the new file
     */

    public void setFile(File file) {
        reset();
        this.file = file;
        devLog.debug("Set dax input file as : " + getFileName());
        setDoc();
    }

    /**
     * Returns a list of all the nodes labelled "job".
     *
     * @return the job info
     */
    private NodeList getJobInfo() {
        NodeList jobList = null;
        if (isValidDax()) {
            jobList = getNodeListFromTag("job");
            devLog.debug("There are " + jobList.getLength() + " jobs listed in the DAX");

        } else {
            devLog.debug("XML does not contain job items, is this a valid dax?");
            return null;
        }
        return jobList;
    }

    /**
     * Gets the job holders from node list.
     *
     * @param jobList the job list
     * @return the job holders from node list
     */
    public Vector<DaxJobHolder> getJobHoldersFromNodeList(NodeList jobList) {
        Vector<DaxJobHolder> sortedJobs = new Vector<DaxJobHolder>();
        for (int i = 0; i < jobList.getLength(); i++) {
            Node node = jobList.item(i);
            NamedNodeMap map = node.getAttributes();
            devLog.debug("Job " + i + " has " + map.getLength() + " attributes : " + listAllAttributes(map));

            DaxJobHolder newJob = new DaxJobHolder();
            String name = getNodeAttributeValue(node, "name");
            newJob.setToolname(name);

            boolean repeatedJob = false;
            for (DaxJobHolder holder : sortedJobs) {
                if (holder.getToolname().equals(name)) {
                    devLog.debug("SortedJobs already contains : " + name + ", so it will be a collection.");
                    repeatedJob = true;
                    holder.setCollection(true);
                }
            }

            if (!repeatedJob) {
                sortedJobs.add(newJob);
                devLog.debug("Added : " + name);
            }


        }

        return sortedJobs;
    }

    /**
     * Creates the task graph.
     *
     * @param jobList the job list
     * @param properties the properties
     * @return the task graph
     */
    private TaskGraph createTaskGraph(NodeList jobList, TrianaProperties properties) {
        TaskGraph tg = null;

        try {
            //       tg = GUIEnv.getApplicationFrame().addParentTaskGraphPanel(TaskGraphManager.createTaskGraph());
            tg = TaskGraphManager.createTaskGraph();
        } catch (TaskException e) {
            e.printStackTrace();
        }

        findFiles();
        for (Iterator iter = files.iterator(); iter.hasNext(); ) {
            DaxFileHolder dfh = (DaxFileHolder) (iter.next());
            ToolImp tool = new ToolImp(properties);
            initFileTool(tool, dfh);
            try {
                tg.createTask(tool);
            } catch (TaskException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < jobList.getLength(); i++) {
            Node node = jobList.item(i);
            NamedNodeMap map = node.getAttributes();
            devLog.debug("Job " + i + " has " + map.getLength() + " attributes : " + listAllAttributes(map));

            ToolImp tool = new ToolImp(properties);
            initJobTool(tool, node);

            try {
                Task task = tg.createTask(tool);

                JavaProxy jp = (JavaProxy) tool.getProxy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        for (Iterator iter = toolVector.iterator(); iter.hasNext(); ) {
            DaxJobHolder djh = (DaxJobHolder) (iter.next());
            for (int i = 0; i < djh.getNumInputNodes(); i++) {
                devLog.debug("Job : " + djh.getToolname() + " has file : " + djh.getLinkAtInNode(i) +
                        " at input node " + i);
            }
            for (int i = 0; i < djh.getNumOutputNodes(); i++) {
                devLog.debug("Job : " + djh.getToolname() + " has file : " + djh.getLinkAtOutNode(i) + " at output node " + i);

            }
        }

        attachCables(tg);
        devLog.debug("Attached Cables. Trying to organize taskgraph.");
        //    tg = combineUnits(tg);


        return tg;

    }

    /**
     * Combine units.
     *
     * @param tg the tg
     * @return the task graph
     */
    private TaskGraph combineUnits(TaskGraph tg) {

        Task[] tasks = tg.getTasks(false);
        for (Task task : tasks) {
            if (task.getDataInputNodeCount() > 3) {
                devLog.debug("Task : " + task.getToolName() + " has : " + task.getDataInputNodeCount() + " input nodes.");
            }
            if (task.getDataOutputNodeCount() > 3) {
                devLog.debug("Task : " + task.getToolName() + " has : " + task.getDataOutputNodeCount() + " output nodes.");

            }
        }
        for (DaxJobHolder djh : toolVector) {
            if (djh.getNumInputNodes() > 3) {
                devLog.debug("Job : " + djh.getToolname() + " has : " + djh.getNumInputNodes() + " input nodes.");
                HashMap inHash = djh.getFilesIn();
                devLog.debug("Hash has : " + inHash.toString());
//                for(int i = 0; i < inHash.size(); i++){
//                    for(int j = 0; j < inHash.size(); j++){
//                        devLog.debug("******* Found + " + lcs((String)inHash.get(i), (String)inHash.get(j)));
//                    }
//                }
            }
            if (djh.getNumOutputNodes() > 3) {
                devLog.debug("Job : " + djh.getToolname() + " has : " + djh.getNumOutputNodes() + " output nodes.");
                HashMap outHash = djh.getFilesOut();
                devLog.debug("Hash has : " + outHash.toString());
//                for(int i = 0; i < outHash.size(); i++){
//                    for(int j = 0; j < outHash.size(); j++){
//                        devLog.debug(lcs((String)outHash.get(i), (String)outHash.get(j)));
//                    }
//                }
            }
        }

        return tg;
    }

    /**
     * Lcs.
     *
     * @param a the a
     * @param b the b
     * @return the string
     */
    public static String lcs(String a, String b) {

        devLog.debug("s.a : " + a + " s.b : " + b);

        int aLen = a.length();
        int bLen = b.length();
        if (aLen == 0 || bLen == 0) {
            return "";
        } else if (a.charAt(aLen - 1) == b.charAt(bLen - 1)) {
            return lcs(a.substring(0, aLen - 1), b.substring(0, bLen - 1))
                    + a.charAt(aLen - 1);
        } else {
            String x = lcs(a, b.substring(0, bLen - 1));
            String y = lcs(a.substring(0, aLen - 1), b);
            return (x.length() > y.length()) ? x : y;
        }
    }

    /**
     * Gets the task from tool.
     *
     * @param tool the tool
     * @return the task from tool
     */
    private Task getTaskFromTool(Tool tool) {
        Task task = null;
        /*
        try {
            Proxy p = tool.getProxy();
            devLog.debug("Got Proxy from Tool : " + p.toString());


            if(p instanceof JavaProxy){
                JavaProxy jp = (JavaProxy)p;
                devLog.debug("Cast Proxy to JavaProxy : " + jp.toString());
                Unit unit = jp.getUnit();
                devLog.debug("Got unit from proxy : " + unit.toString());
                task = unit.getTask();
                devLog.debug("Got task from unit." + task.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        return task;
    }

    /**
     * Takes an (largely unconnected) taskgraph and loops through the Vector of tools (JobUnits)
     * Loops through jobs input nodes, then output nodes.
     * Attaches jobs input and output nodes to the next available node from a files "DaxFileHolder" getUnconnected(In-Out)Node()
     *
     * @param tg the tg
     */
    private void attachCables(TaskGraph tg) {
        for (Iterator iter = toolVector.iterator(); iter.hasNext(); ) {
            DaxJobHolder djh = (DaxJobHolder) iter.next();
            Task jobTask = getTaskFromTool(djh.getTool());
            if (jobTask == null) {
                jobTask = tg.getTask(djh.getJobID());
            }

            for (int i = 0; i < djh.getNumInputNodes(); i++) {
                String link = djh.getLinkAtInNode(i);
                org.trianacode.taskgraph.Node fileNode = null;
                for (Iterator iter2 = files.iterator(); iter2.hasNext(); ) {
                    DaxFileHolder dfh = (DaxFileHolder) iter2.next();
                    if (dfh.getFilename().equals(link)) {
                        //  String filesJobID = dfh.getJobAtOutNode(0);
                        //  String jobsID = djh.getJobID();
                        //  if(!(filesJobID == null) && filesJobID.equals(jobsID)){
                        Task fileTask = tg.getTask(dfh.getFilename());
                        fileNode = fileTask.getOutputNode(dfh.getUnconnectedOutNode());
                        devLog.debug("Job : " + djh.getJobID() + " will connect input node " + i + " to file " + dfh.getFilename());
                        break;
                    }
                }
                try {
                    tg.connect(fileNode, jobTask.getDataInputNode(i));
                } catch (CableException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < djh.getNumOutputNodes(); i++) {
                String link = djh.getLinkAtOutNode(i);
                org.trianacode.taskgraph.Node fileNode = null;
                for (Iterator iter2 = files.iterator(); iter2.hasNext(); ) {
                    DaxFileHolder dfh = (DaxFileHolder) iter2.next();
                    if (dfh.getFilename().equals(link)) {
                        //  String filesJobID = dfh.getJobAtInNode(0);
                        //  String jobsID = djh.getJobID();
                        //  if(!(filesJobID == null) && filesJobID.equals(jobsID)){
                        Task fileTask = tg.getTask(dfh.getFilename());
                        fileNode = fileTask.getInputNode(dfh.getUnconnectedInNode());
                        devLog.debug("Job : " + djh.getJobID() + " will connect output node " + i + " to file " + dfh.getFilename());
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

    /**
     * Finds all nodes labelled "job" in DAX,.
     */
    private void findFiles() {
        NodeList jobLst = doc.getElementsByTagName("job");
        Vector<String> filenames = new Vector<String>();

        for (int j = 0; j < jobLst.getLength(); j++) {
            Node jobNode = jobLst.item(j);
            NodeList nodeLst = jobNode.getChildNodes();

            for (int i = 0; i < nodeLst.getLength(); i++) {
                Node node = nodeLst.item(i);
                if (node.getNodeName().equals("uses")) {
                    //    NamedNodeMap map = node.getAttributes();
                    //    String fileName = map.getNamedItem("file").getNodeValue();
                    String fileName = getNodeAttributeValue(node, "file");
                    if (fileName == null) {
                        fileName = getNodeAttributeValue(node, "name");
                    }
                    //      devLog.debug("Getting info for file : " + fileName);

                    if (!filenames.contains(fileName)) {
                        filenames.add(fileName);
                        DaxFileHolder dfh = new DaxFileHolder();
                        dfh.setFilename(fileName);
                        String parentID = getNodeAttributeValue(jobNode, "id");
                        //devLog.debug("Files parent is job : " + parentID);
                        if (getNodeAttributeValue(node, "link").equals("input")) {
                            int nodeNumber = dfh.getFreeOutNode();
                            dfh.addJobOut(nodeNumber, parentID);
                            devLog.debug("File " + fileName + " has job " + parentID + " attached to output node : " + nodeNumber);
                        }
                        if (getNodeAttributeValue(node, "link").equals("output")) {
                            int nodeNumber = dfh.getFreeInNode();
                            dfh.addJobIn(nodeNumber, parentID);
                            devLog.debug("File " + fileName + " has job " + parentID + " attached to input node : " + nodeNumber);

                        }

                        files.add(dfh);
                    } else {
                        for (Iterator iter = files.iterator(); iter.hasNext(); ) {
                            DaxFileHolder dfh = (DaxFileHolder) iter.next();
                            if (dfh.getFilename().equals(fileName)) {
                                String parentID = getNodeAttributeValue(jobNode, "id");
                                devLog.debug("Files parent is job : " + parentID);
                                if (getNodeAttributeValue(node, "link").equals("input")) {
                                    int nodeNumber = dfh.getFreeOutNode();
                                    dfh.addJobOut(nodeNumber, parentID);
                                    devLog.debug("File " + fileName + " has job " + parentID + " attached to output node : " + nodeNumber);
                                }
                                if (getNodeAttributeValue(node, "link").equals("output")) {
                                    int nodeNumber = dfh.getFreeInNode();
                                    dfh.addJobIn(nodeNumber, parentID);
                                    devLog.debug("File " + fileName + " has job " + parentID + " attached to input node : " + nodeNumber);
                                }

                            }
                        }
                    }
                }
            }
        }

        listKnownFileData();
    }

    /**
     * Method to list the jobs associated with certain nodes of each file found so far.
     */
    private void listKnownFileData() {
        devLog.debug("\n**** System knows this about files:");
        for (Iterator i = files.iterator(); i.hasNext(); ) {
            DaxFileHolder dfh = (DaxFileHolder) (i.next());
            for (int j = 0; j < dfh.getNumInputNodes(); j++) {
                devLog.debug("File : " + dfh.getFilename() + " has job : " + dfh.getJobAtInNode(j) + " at input node " + j);
            }
            for (int j = 0; j < dfh.getNumOutputNodes(); j++) {
                devLog.debug("File : " + dfh.getFilename() + " has job : " + dfh.getJobAtOutNode(j) + " at output node " + j);
            }
        }
    }

    /**
     * Fills the tool with information taken from the job node of a DAX
     * All tools created are of type "JobUnit".
     * Created tool should have the correct number of input and output nodes
     *
     * @param tool the tool
     * @param node the node
     */
    private void initJobTool(ToolImp tool, Node node) {
        DaxJobHolder djh = new DaxJobHolder();

        String toolname = getNodeAttributeValue(node, "id");
        tool.setParameter("name", getNodeAttributeValue(node, "name"));
        tool.setToolName(toolname);
        tool.setSubTitle((String) tool.getParameter("name"));
        devLog.debug("Subtitle set to : " + tool.getSubTitle());


        int inputNodes = 0;
        int outputNodes = 0;

        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node childNode = nl.item(i);
            if (childNode.getNodeName().equals("uses")) {
                NamedNodeMap map = childNode.getAttributes();
                //      devLog.debug("Job uses : "  + listAllAttributes(map));
                if (map.getNamedItem("link").getNodeValue().equals("input")) {
                    String fileName;
                    if (map.getNamedItem("file") != null) {
                        fileName = map.getNamedItem("file").getNodeValue();
                    } else {
                        fileName = map.getNamedItem("name").getNodeValue();
                    }
                    djh.addFileIn(inputNodes, fileName);
                    inputNodes++;
                }
                if (map.getNamedItem("link").getNodeValue().equals("output")) {
                    String fileName;
                    if (map.getNamedItem("file") != null) {
                        fileName = map.getNamedItem("file").getNodeValue();
                    } else {
                        fileName = map.getNamedItem("name").getNodeValue();
                    }
                    djh.addFileOut(outputNodes, fileName);
                    outputNodes++;
                }
            }
            if (childNode.getNodeName().equals("argument")) {
                tool.setParameter("args", childNode.getTextContent().trim());
                devLog.debug("Job tool will have args : " + childNode.getTextContent());
            }
        }

        try {
            tool.setDataInputNodeCount(inputNodes);
            tool.setDataOutputNodeCount(outputNodes);
            tool.setToolPackage(daxPackage);
            tool.setProxy(makeProxy(daxJobUnitName, daxPackage));
        } catch (Exception e) {
            e.printStackTrace();
        }


        djh.setToolname(toolname);
        djh.setJobID(getNodeAttributeValue(node, "id"));
        djh.setTrianaToolName("T-" + toolname);

        djh.setNumInputNodes(inputNodes);
        djh.setNumOutputNodes(outputNodes);
        djh.setTool(tool);


        devLog.debug("Job " + toolname + " has " + inputNodes + " inputNodes, and " + outputNodes + " outputNodes.");
        String inputs = "";
        String outputs = "";

        for (int i = 0; i < djh.getNumInputNodes(); i++) {
            inputs += "Node " + i + " : " + djh.getLinkAtInNode(i) + " ";
        }

        for (int i = 0; i < djh.getNumOutputNodes(); i++) {
            outputs += "Node " + i + " : " + djh.getLinkAtOutNode(i) + " ";
        }

        devLog.debug("It has " + inputs + " as input, and " + outputs + " as output.\n");

        toolVector.add(djh);

    }

    /**
     * Inits the file tool.
     *
     * @param tool the tool
     * @param dfh the dfh
     */
    private void initFileTool(ToolImp tool, DaxFileHolder dfh) {

        tool.setToolName(dfh.getFilename());
        tool.setParameter("fileName", dfh.getFilename());


        int inputNodes = dfh.getNumInputNodes();
        int outputNodes = dfh.getNumOutputNodes();

        devLog.debug("Creating file : " + dfh.getFilename() + " with " + inputNodes + " inputnodes and " + outputNodes + " outputnodes.");

        try {
            tool.setDataInputNodeCount(inputNodes);
            tool.setDataOutputNodeCount(outputNodes);
            tool.setToolPackage(daxPackage);
            tool.setProxy(makeProxy(daxFileUnitName, daxPackage));
        } catch (TaskException e) {
            e.printStackTrace();
        }

    }

    /**
     * Make proxy.
     *
     * @param filename the filename
     * @param packageLocation the package location
     * @return the proxy
     */
    private Proxy makeProxy(String filename, String packageLocation) {
        HashMap details = new HashMap();
        details.put("unitName", (filename));
        details.put("unitPackage", (packageLocation));
        Proxy proxy = null;
        try {
            proxy = ProxyFactory.createProxy("Java", details);
        } catch (ProxyInstantiationException e) {
            e.printStackTrace();
        }
        return proxy;
    }


    /**
     * Gets the node attribute value.
     *
     * @param node the node
     * @param name the name
     * @return the node attribute value
     */
    private String getNodeAttributeValue(Node node, String name) {
        Node child = node.getAttributes().getNamedItem(name);
        if (child != null) {
            return node.getAttributes().getNamedItem(name).getNodeValue();
        } else {
            return null;
        }
    }

    /**
     * Gets the file.
     *
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return file.getName();
    }

    /**
     * Sets the doc variable which will be used as the root node when building tools from the DAX.
     */
    private void setDoc() {
        try {
            BufferedInputStream bufferedInput = new BufferedInputStream(new FileInputStream(file));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(bufferedInput);
            doc.getDocumentElement().normalize();

        } catch (Exception e) {

        }
    }

    /**
     * Checks if the first string in the file is "adag".
     *
     * @return true, if is valid dax
     */
    private boolean isValidDax() {
        String rootString = doc.getDocumentElement().getNodeName();
        devLog.debug("Root element : " + rootString);
        if (rootString.equals("adag")) {
            devLog.debug("File " + getFileName() + " is a valid DAX");
            return true;
        } else {
            devLog.debug("File " + getFileName() + " is not a valid DAX");
            return false;
        }
    }

    /**
     * Gets the node list from tag.
     *
     * @param tagName the tag name
     * @return the node list from tag
     */
    private NodeList getNodeListFromTag(String tagName) {
        NodeList nodeLst = doc.getElementsByTagName(tagName);
        return nodeLst;
    }

    /**
     * Lists all the attributes in the NamedNodeMap.
     *
     * @param map the map
     * @return the string
     */
    private String listAllAttributes(NamedNodeMap map) {
        String listString = "";
        for (int i = 0; i < map.getLength(); i++) {
            Node node = map.item(i);
            listString += node.getNodeName();
            listString += " = " + node.getNodeValue() + " : ";

        }
        return listString;
    }
}
