package org.trianacode.shiwaall.handler;

import hu.sztaki.lpds.exceptions.FileImTeaPotException;
import hu.sztaki.lpds.jsdl_lib.Parser;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.shiwa.desktop.data.description.workflow.Author;
import org.shiwa.desktop.data.transfer.FGITaskHandler;
import org.shiwa.desktop.data.transfer.FGIWorkflowEngineHandler;
import org.shiwa.desktop.data.transfer.IWIRTaskHandler;
import org.shiwa.fgi.iwir.*;
import org.shiwa.fgi.iwir.Task;
import org.trianacode.TrianaInstance;
import org.trianacode.config.Locations;
import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoHandler;
import org.trianacode.enactment.io.IoMapping;
import org.trianacode.enactment.io.IoType;
import org.trianacode.shiwaall.extras.FileBuilder;
import org.trianacode.shiwaall.iwir.execute.Executable;
import org.trianacode.shiwaall.iwir.execute.ExecutableNode;
import org.trianacode.shiwaall.iwir.importer.utils.ExportIwir;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.ser.Base64;
import org.trianacode.taskgraph.ser.DocumentHandler;
import org.trianacode.taskgraph.ser.TrianaObjectInputStream;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 15/12/2011
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public class TrianaIWIRHandler implements FGIWorkflowEngineHandler {

    private static final String TRIANA_RUN_SCRIPT = "unzip-triana.sh";

    /** The iwir. */
    private IWIR iwir;

    /** The image input stream. */
    private InputStream imageInputStream;
    /** The jsdls. */
    private Map<String, FGITaskHandler> jsdls = null;
    private TaskGraph taskgraph = null;
    private HashMap<org.trianacode.taskgraph.Task, AbstractTask> taskHashMap = null;


    //For internal testing
    /**
     * Instantiates a new triana iwir handler.
     */
    private TrianaIWIRHandler(){}

    /**
     * Instantiates a new triana iwir handler.
     *
     * @param taskGraph the task graph
     * @param trianaImage the triana image
     */
    public TrianaIWIRHandler(TaskGraph taskGraph, InputStream trianaImage) {
        try {
            init(taskGraph);
            this.taskgraph = taskGraph;
            this.imageInputStream = getImageInputStream(trianaImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the image input stream.
     *
     * @param trianaImage the triana image
     * @return the image input stream
     */
    private InputStream getImageInputStream(InputStream trianaImage){
        InputStream image;
        try{

            // if dot isn't in default unix location, use the triana image.
            File file = new File("/usr/local/bin/dot");
            if(file.exists()){
                File imageFile = iwir.getImage(file.getAbsolutePath(), "png", "");
//                System.out.println("dot image " + imageFile.getAbsolutePath());
                image = new FileInputStream(imageFile);
            } else{
                System.out.println("/usr/local/bin/dot not found");
                throw new FileNotFoundException();
            }
        } catch (Exception e){
            e.printStackTrace();
            image = trianaImage;
        }
        return image;
    }

    /**
     * Inits the IWIR Exporter, creates the IWIR from the taskgraph, and prepares JSDLs
     *
     * @param taskGraph the task graph
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void init(TaskGraph taskGraph) throws IOException {
        ExportIwir exportIwir = new ExportIwir();
        BlockScope blockscope = exportIwir.taskGraphToBlockScope(taskGraph);
        taskHashMap = exportIwir.getTaskHashMap();
        iwir = new IWIR(taskGraph.getToolName());
        iwir.setTask(blockscope);

        System.out.println(iwir.asXMLString());

//        try {
//            prepareJSDLs(iwir);
//        } catch (JAXBException e) {
//            e.printStackTrace();
//            jsdls = null;
//        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws JAXBException the jAXB exception
     * @throws FileImTeaPotException the file im tea pot exception
     */
    public static void main(String[] args) throws IOException, JAXBException, FileImTeaPotException, TaskGraphException {
        TrianaIWIRHandler trianaIWIRHandler = new TrianaIWIRHandler();

        File file = new File("triana-app/dist/input_0");
//        Object obj = new TrianaPixelMap(new ImageIcon("output.gif").getImage());
//        trianaIWIRHandler.serial(obj, new FileOutputStream(file));
        System.out.println(file.getAbsolutePath());
        Serializable o = trianaIWIRHandler.unserial(new FileInputStream(file));
        System.out.println(o.getClass().getCanonicalName());



    }

    private Serializable unserial(FileInputStream fileInputStream) throws TaskGraphException {
        try {
            byte[] bytes = Base64.decode(readAsString(fileInputStream));
            ObjectInputStream in = new TrianaObjectInputStream(new ByteArrayInputStream(bytes));
            return (Serializable) in.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new TaskGraphException(e);
        }
    }

    public String readAsString(InputStream in) throws TaskGraphException {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] bytes = new byte[8192];
            int c;
            while ((c = in.read(bytes)) != -1) {
                bout.write(bytes, 0, c);
            }
            bout.flush();
            bout.close();
            return new String(bout.toByteArray());
        } catch (IOException e) {
            throw new TaskGraphException(e);
        }
    }

    private void serial(Object serializable, OutputStream sink) throws TaskGraphException {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream marshall = new ObjectOutputStream(bout);
            marshall.writeObject(serializable);
            String ret = Base64.encode(bout.toByteArray());
            sink.write(ret.getBytes("UTF-8"));

            marshall.close();
            bout.close();
        } catch (IOException e) {
            throw new TaskGraphException(e);
        }


    }


    /**
     * An attempt to find the triana jars and libraries to add to a tasktype
     * @return
     */
    private ArrayList<File> findTrianaFiles() {
        ArrayList<File> files = new ArrayList<File>();
        File dist = null;

        if(Locations.isJarred()){

            try {
                File jarFile = new File( TrianaInstance.class.getProtectionDomain()
                        .getCodeSource().getLocation().toURI());
                System.out.println("triana jar : " + jarFile.getAbsolutePath());

//            File trianaJar = new File(Locations.runHome().getAbsolutePath());
                dist = new File(jarFile.getParent());

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            File trianaFolder = new File(Locations.getHomeProper());
            if(trianaFolder.exists() && trianaFolder.isDirectory()){
                File trianaApp = new File(trianaFolder, "triana-app");
                if(trianaApp.exists() && trianaApp.isDirectory()){
                    dist = new File(trianaApp, "dist");

//                        File trianaJar = new File(dist, TRIANA_JAR_NAME);
//                        if(trianaJar.exists()){
//                            files.add(trianaJar);
//                            trianaRootFolder = new File(trianaJar.getParent());
//                        }

                }
            }
        }
        if(dist != null && dist.exists() && dist.isDirectory()){
            File untarTriana = new File(dist, "unzip-triana.sh");
            if(untarTriana.exists() && untarTriana.isFile()){
                files.add(untarTriana);
            }

            File disttar = new File(dist, "dist-tar");
            if(disttar.exists() && disttar.isDirectory()){
                File disttarFile = new File(disttar, "disttar.tar.bz2");
                files.add(disttarFile);
            }
        } else {
            return null;
        }


//        File singleFolderTrianaScript = new File(trianaRootFolder, TRIANA_RUN_SCRIPT);
//        if(singleFolderTrianaScript.exists()){
//            files.add(singleFolderTrianaScript);
//        }
//
//        File libFolder = new File(trianaRootFolder, "lib");
//        if(libFolder.exists() && libFolder.isDirectory()){
////            files.add(trianaRootFolder);
//            for(File lib : libFolder.listFiles()){
//                File libWithParent = new File(libFolder, lib.getName());
//                files.add(libWithParent);
//            }
//        }

//        HashSet<File> taskFiles = new HashSet<File>();
//        for(org.trianacode.taskgraph.Task task : TaskGraphUtils.getAllTasksRecursive(taskgraph, false)){
//
//            JavaProxy javaProxy = (JavaProxy) task.getProxy();
//            Class unitClass = javaProxy.getUnit().getClass();
//
//            File clazzFile = null;
//            try {
//                clazzFile = new File( unitClass.getProtectionDomain()
//                        .getCodeSource().getLocation().toURI());
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//            if(clazzFile != null) {
//                if(clazzFile.isFile()){
//
//                    System.out.println("task " + task.getToolName() +
//                            " tool " + task.getClass().getCanonicalName() +
//                            "triana toolbox jar : " + clazzFile.getAbsolutePath());
//                    taskFiles.add(clazzFile);
//                } else {
//                    //TODO iterate through and grab all sub-folders/files and flatten
//                }
//            }
//            files.addAll(taskFiles);



//        System.out.println(Locations.getHomeProper()
//                + "\n" + Locations.isJarred() + "\n" + Locations.runHome().getAbsolutePath()  + "\n"
//        );

//        }

        for (File file : files){
            System.out.println(file.getAbsolutePath() + " " + file.exists() + "\n");
        }

        return files;
    }


    /**
     * Prepare jsdls.
     *
     * @param iwir the iwir
     * @throws JAXBException the jAXB exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void prepareJSDLs(IWIR iwir) throws JAXBException, IOException {
        try {
            jsdls = new HashMap<String, FGITaskHandler>();

            ArrayList<File> trianaFiles = findTrianaFiles();

            Set<String> tasktypes = iwir.getAtomicTaskTypes();
            System.out.println(tasktypes);

            for(String tasktype : tasktypes){

                //for each tasktype, sort through the atomic tasks and create a jsdl for each
                //TODO this is wrong, as need to check for similar signatures.
                for(Task task : iwir.getAtomicTasks()){
                    if(task.getTasktype().equals(tasktype)){

                        HashSet<File> tasksFiles = new HashSet<File>();
                        Executable exec = null;
                        org.trianacode.taskgraph.Task trianaTask = getTaskForAtomicTask(task);

                        if (trianaTask.isParameterName(Executable.EXECUTABLE)) {
                            exec = (Executable) trianaTask.getParameter(Executable.EXECUTABLE);
                            File dir = exec.getWorkingDir();
                            if(dir.exists() && dir.isDirectory()) {
                                tasksFiles.addAll(exec.getWorkingDirFileWhichAreNotInputsOrOutputs());
                            }
                        } else {
                            tasksFiles.addAll(trianaFiles);
                        }

                        TaskGraph taskGraph = null;
                        taskGraph = TaskGraphManager.createTaskGraph();
                        taskGraph.setToolName(trianaTask.getToolName() + ".graph");
                        TaskGraphUtils.createTasks(new Tool[]{trianaTask}, taskGraph, false);
                        TaskLayoutUtils.resolveGroupNodes(taskGraph);


                        File taskFile = new File(taskGraph.getQualifiedToolName() + ".xml");
                        XMLWriter xmlWriter = new XMLWriter(new FileWriter(taskFile));
                        xmlWriter.writeComponent(taskGraph);

                        System.out.println("Wrote " + taskFile.getAbsolutePath());
                        tasksFiles.add(taskFile);

                        File ioConfig = createIOConfig(taskGraph);
                        tasksFiles.add(ioConfig);


                        String jobName = "triana.sh";
                        String executableFileName = TRIANA_RUN_SCRIPT;
                        String arguments = "-n -w " + taskFile.getName() + " -d " + ioConfig.getName();

                        if(exec != null){
                            arguments = exec.getArgsAsString();
                            executableFileName = exec.getPrimaryExec();
                            jobName = exec.getTaskName();
                        }


//                        TaskTypeToolDescriptor taskTypeToolDescriptor;
//                        if( (taskTypeToolDescriptor = TaskTypeRepo.getDescriptorFromType(tasktype)) != null){
//                            if((executable = taskTypeToolDescriptor.getExecutable()) != null){
//                                Collections.addAll(tasksFiles, executable.getWorkingDir().listFiles());
//                            }
//                        } else {
//                        }

                        IWIRTaskHandler iwirTaskHandler = new IWIRTaskHandler(task);
                        iwirTaskHandler.setFiles(tasksFiles);

                        Parser parser = new Parser();
                        JobDefinitionType jobDefinitionType = parser.readJSDLFromString(
                                createJSDLString(task, tasksFiles, jobName, executableFileName, arguments));

                        String jsdlXML = parser.getJSDLXML(jobDefinitionType);
                        System.out.println("\n\n" + jsdlXML);

                        File jsdlFile = new File(tasktype + ".jdsl");
                        new FileBuilder(jsdlFile.getAbsolutePath(), jsdlXML);
                        System.out.println("Created jsdl at " + jsdlFile.getAbsolutePath());

                        iwirTaskHandler.setDefinitionFile(jsdlFile);

                        jsdls.put(tasktype, iwirTaskHandler);

                    }
                }
            }
            System.out.println(jsdls.keySet());
        } catch (TaskGraphException e) {
            e.printStackTrace();
            jsdls = null;
        }
    }

    private File createIOConfig(TaskGraph taskgraph) throws IOException {

        ArrayList<IoMapping> inputMappings = new ArrayList<IoMapping>();

        Node[] inputPorts = taskgraph.getDataInputNodes();
        for (Node inputPort : inputPorts) {

            boolean reference;
            String type = "string";

            String[] inputTypes = taskgraph.getDataInputTypes(inputPort.getAbsoluteNodeIndex());
            if(inputTypes == null){
                inputTypes = taskgraph.getDataInputTypes();
            }

            //TODO figure out references and types
            if (Arrays.asList(inputTypes).contains("java.lang.String")) {
                type = "string";
                reference = false;
            } else {
                type = "java64";
                reference = true;
            }

//            System.out.println("\n" +
//                    " to port " + inputPort.getName() +
//                    " portNumber " + inputPort.getAbsoluteNodeIndex() +
//                    " reference " + reference
//            );

            IoMapping ioMapping = new IoMapping(
                    new IoType("input_" + inputPort.getAbsoluteNodeIndex()
                            , type, reference),
                    "" + inputPort.getAbsoluteNodeIndex());
            inputMappings.add(ioMapping);
        }


        Node[] outputNodes = taskgraph.getDataOutputNodes();
        List<IoMapping> outputMappings = new ArrayList<IoMapping>();

        for (Node outputNode : outputNodes) {

            boolean reference;
            String type = "string";

            String[] outputTypes = taskgraph.getDataOutputTypes(outputNode.getAbsoluteNodeIndex());
            if(outputTypes == null){
                outputTypes = taskgraph.getDataOutputTypes();
            }

            //TODO figure out references and types
            if (Arrays.asList(outputTypes).contains("java.lang.String")) {
                type = "string";
                reference = true;
            } else {
                type = "java64";
                reference = true;
            }

            System.out.println("\n" +
                    " to port " + outputNode.getName() +
                    " portNumber " + outputNode.getAbsoluteNodeIndex() +
                    " reference " + reference
            );

            IoMapping ioMapping = new IoMapping(
                    new IoType("output_" + outputNode.getAbsoluteNodeIndex()
                            , type, reference),
                    "" + outputNode.getAbsoluteNodeIndex());
            outputMappings.add(ioMapping);
        }


        IoConfiguration conf = new IoConfiguration(taskgraph.getQualifiedToolName(), "0.1", inputMappings, outputMappings);

        DocumentHandler documentHandler = new DocumentHandler();
        new IoHandler().serialize(documentHandler, conf);
        File tempConfFile = File.createTempFile(conf.getToolName() + ".io_conf", ".dat");
        documentHandler.output(new FileWriter(tempConfFile), true);
        return tempConfFile;
    }

    private org.trianacode.taskgraph.Task getTaskForAtomicTask(Task task) {
        for(org.trianacode.taskgraph.Task trianaTask : taskHashMap.keySet()){
            AbstractTask atomicTask = taskHashMap.get(trianaTask);
//            System.out.println("Comparing " + atomicTask.getUniqueId() +
//                    " with " + task.getUniqueId());
            if(atomicTask.getUniqueId().equals(task.getUniqueId())){
                return trianaTask;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.WorkflowEngineHandler#getEngineName(java.util.Set)
     */
    @Override
    public String getEngineName(Set<String> strings) {
        return "Triana";
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.WorkflowEngineHandler#getEngineVersion()
     */
    @Override
    public String getEngineVersion() {
        return "4.0";
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.WorkflowEngineHandler#getWorkflowLanguage(java.util.Set)
     */
    @Override
    public String getWorkflowLanguage(Set<String> strings) {
        return "IWIR";
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.TaskHandler#getSignature()
     */
    @Override
    public TransferSignature getSignature() {
        TransferSignature signature = new TransferSignature();
        signature.setName(iwir.getWfname());

        for (AbstractDataPort i : iwir.getTask().getAllInputPorts()) {
            signature.addInput(i.getName(), i.getType().toString());
        }
        for (AbstractDataPort j : iwir.getTask().getAllOutputPorts()) {
            signature.addInput(j.getName(), j.getType().toString());
        }
        return signature;
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.TaskHandler#getDefinition()
     */
    @Override
    public InputStream getDefinition() {
        try {
            return new ByteArrayInputStream(iwir.asXMLString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.TaskHandler#getDefinitionName()
     */
    @Override
    public String getDefinitionName() {
        return iwir.getWfname();
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.WorkflowEngineHandler#getDisplayImage()
     */
    @Override
    public InputStream getDisplayImage() {
        return imageInputStream;
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.WorkflowEngineHandler#getDisplayImageName()
     */
    @Override
    public String getDisplayImageName() {
        return iwir.getWfname() + "-image.jpg";
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.TaskHandler#getAuthors()
     */
    @Override
    public List<Author> getAuthors() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.TaskHandler#getDescription()
     */
    @Override
    public String getDescription() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.TaskHandler#getVersion()
     */
    @Override
    public String getVersion() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.shiwa.desktop.data.transfer.FGIWorkflowEngineHandler#getTaskHandlers()
     */
    @Override
    public Map<String, FGITaskHandler> getTaskHandlers() {
        try {
            prepareJSDLs(iwir);
        } catch (Exception e) {
            e.printStackTrace();
            jsdls = null;
        }
        return jsdls;
    }

    private String createJSDLString(Task task, HashSet<File> files, String jobName, String executableFileName, String arguments){
        StringBuilder jsdlBuilder = new StringBuilder();

        jsdlBuilder.append(HEADER);
        jsdlBuilder.append(createJobIdentification(jobName));
        jsdlBuilder.append(createApplication(task.getTasktype(), executableFileName, arguments));
        jsdlBuilder.append(RESOURCES);
        jsdlBuilder.append(createAllInputDataStaging(task));

        for(File file : files){
            jsdlBuilder.append(createInputDataStaging(null, file.getName()));
        }

        jsdlBuilder.append(createAllOutputDataStaging(task));

        jsdlBuilder.append(FOOTER);

        return jsdlBuilder.toString();
    }

    private static String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<JobDefinition xmlns=\"http://schemas.ggf.org/jsdl/2005/11/jsdl\" " +
            "xmlns:ns2=\"http://schemas.ggf.org/jsdl/2005/11/jsdl-posix\" " +
            "xmlns:ns3=\"extension.dci\" xmlns:ns4=\"uri:MBSchedulingDescriptionLanguage\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xsi:schemaLocation=\"http://schemas.ggf.org/jsdl/2005/11/jsdl\">\n" +
            "    <JobDescription>\n";

    private String createJobIdentification(String jobName){
        return "        <JobIdentification>\n" +
                "            <JobName>" + jobName + "</JobName>\n" +
                "            <JobAnnotation>_PLACEHOLDER_JOBANNOTATION_</JobAnnotation>\n" +
                "        </JobIdentification>\n";
    }

    private String createApplication(String applicationName, String executableFileName, String arguments){
        return "        <Application>\n" +
                "            <ApplicationName>" + applicationName +"</ApplicationName>\n" +
                "            <ns2:POSIXApplication_Type>\n" +
                "                <ns2:Executable>" + executableFileName +"</ns2:Executable>\n" +
                "                <ns2:Argument>" + arguments + "</ns2:Argument>\n" +
                "                <ns2:Output>stdout.log</ns2:Output>\n" +
                "                <ns2:Error>stderr.log</ns2:Error>\n" +
                "                <ns2:UserName>_PLACEHOLDER_USERNAME_</ns2:UserName>\n" +
                "                <ns2:GroupName>_PLACEHOLDER_GROUP_</ns2:GroupName>\n" +
                "            </ns2:POSIXApplication_Type>\n" +
                "        </Application>\n";
    }

    private static String RESOURCES = "        <Resources>\n" +
            "            <CandidateHosts>\n" +
            "                <HostName>_PLACEHOLDER_HOSTNAME_</HostName>\n" +
            "                <OperatingSystem>\n" +
            "                    <OperatingSystemType>\n" +
            "                        <OperatingSystemName>LINUX</OperatingSystemName>\n" +
            "                    </OperatingSystemType>\n" +
            "                </OperatingSystem>" +
            "            </CandidateHosts>\n" +
            "        </Resources>\n";

    private String createAllInputDataStaging(Task task){
        StringBuilder inputDataStagings = new StringBuilder();


        org.trianacode.taskgraph.Task trianaTask = getTaskForAtomicTask(task);
        if (trianaTask.isParameterName(Executable.EXECUTABLE)) {
            Executable exec = (Executable) trianaTask.getParameter(Executable.EXECUTABLE);

            for(ExecutableNode executableNode : exec.getInputNodes()) {
                 executableNode.getFilename();
            }
        } else {
            List<InputPort> inputPorts = task.getInputPorts();
            for(int in = 0; in < inputPorts.size(); in++){
                InputPort inputPort = inputPorts.get(in);
                String portName = inputPort.getName();
                String filename = "input_" + in;
                inputDataStagings.append(createInputDataStaging(portName, filename));
            }
        }
        return inputDataStagings.toString();
    }

    private String createInputDataStaging(String inputPortName, String inputFileName){
        StringBuilder dataStaging = new StringBuilder();
        dataStaging.append("        <DataStaging");

        if(inputPortName != null){
            dataStaging.append(" name=\"" + inputPortName +"\"");
        }
        dataStaging.append(">\n" +
                "            <FileName>" + inputFileName +"</FileName>\n" +
                "            <CreationFlag>overwrite</CreationFlag>\n" +
                "            <DeleteOnTermination>true</DeleteOnTermination>\n" +
                "            <Source>\n" +
                "                <URI>_PLACEHOLDER_SOURCEFILESERVER_</URI>\n" +
                "            </Source>\n" +
                "        </DataStaging>\n");
        return dataStaging.toString();
    }

    private String createAllOutputDataStaging(Task task){
        StringBuilder allOutputDataStagings = new StringBuilder();

        List<OutputPort> outputPorts = task.getOutputPorts();
        for(int out = 0; out < outputPorts.size(); out++){
            OutputPort outputPort = outputPorts.get(out);
            String portName = outputPort.getName();

            String filename = "output_" + out;
//            try {
//                parser.AddOutputDataStaging(filename, Parser.PLACEHOLDER_TARGETURI);
//            } catch (FileImTeaPotException e) {
//                System.out.println("teapot error with " + filename);
//                e.printStackTrace();
//            }
//            parser.ModifyDatastagingName(filename, portName);
            allOutputDataStagings.append(createOutputDataStaging(portName, filename));
        }
        return allOutputDataStagings.toString();
    }

    private String createOutputDataStaging(String outputPortName, String outputFileName){
        return "<DataStaging name=\"" + outputPortName + "\">\n" +
                "            <FileName>" + outputFileName + "</FileName>\n" +
                "            <CreationFlag>overwrite</CreationFlag>\n" +
                "            <DeleteOnTermination>true</DeleteOnTermination>\n" +
                "            <Target>\n" +
                "                <URI>_PLACEHOLDER_TARGETFILESERVER_</URI>\n" +
                "            </Target>\n" +
                "        </DataStaging>";
    }

    private static String FOOTER = "    </JobDescription>\n" +
            "    <ns4:SDL_Type>\n" +
            "        <ns4:Constraints>\n" +
            "            <ns4:Middleware>\n" +
            "                <ns4:DCIName>_PLACEHOLDER_DCINAME_</ns4:DCIName>\n" +
            "                <ns4:MyProxy/>\n" +
            "                <ns4:ManagedResource>_PLACEHOLDER_RESOURCE_</ns4:ManagedResource>\n" +
            "            </ns4:Middleware>\n" +
            "            <ns4:Budget>0</ns4:Budget>\n" +
            "        </ns4:Constraints>\n" +
            "    </ns4:SDL_Type>\n" +
            "    <ns3:extension_type>\n" +
            "        <ns3:wfiservice>_PLACEHOLDER_STATUSSERVICE_</ns3:wfiservice>\n" +
            "        <ns3:proxyservice>_PLACEHOLDER_CREDENTIALPROVIDER_</ns3:proxyservice>\n" +
            "    </ns3:extension_type>\n" +
            "</JobDefinition>";

    /** The horrible jsd lhack. */
    private static String horribleJSDLhack = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<JobDefinition xmlns=\"http://schemas.ggf.org/jsdl/2005/11/jsdl\" xmlns:ns2=\"http://schemas.ggf.org/jsdl/2005/11/jsdl-posix\" xmlns:ns3=\"extension.dci\" xmlns:ns4=\"uri:MBSchedulingDescriptionLanguage\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://schemas.ggf.org/jsdl/2005/11/jsdl\">\n" +
            "    <JobDescription>\n" +
            "        <JobIdentification>\n" +
            "            <JobName></JobName>\n" +
            "            <JobAnnotation>_PLACEHOLDER_JOBANNOTATION_</JobAnnotation>\n" +
            "        </JobIdentification>\n" +
            "        <Application>\n" +
            "            <ApplicationName></ApplicationName>\n" +
            "            <ns2:POSIXApplication_Type>\n" +
            "                <ns2:Executable></ns2:Executable>\n" +
            "                <ns2:Argument>-n -w</ns2:Argument>\n" +
            "                <ns2:Output>stdout.log</ns2:Output>\n" +
            "                <ns2:Error>stderr.log</ns2:Error>\n" +
            "                <ns2:UserName>_PLACEHOLDER_USERNAME_</ns2:UserName>\n" +
            "                <ns2:GroupName>_PLACEHOLDER_GROUP_</ns2:GroupName>\n" +
            "            </ns2:POSIXApplication_Type>\n" +
            "        </Application>\n" +
            "        <Resources>\n" +
            "            <CandidateHosts>\n" +
            "                <HostName>_PLACEHOLDER_HOSTNAME_</HostName>\n" +
            "                <OperatingSystem>\n" +
            "                    <OperatingSystemType>\n" +
            "                        <OperatingSystemName>LINUX</OperatingSystemName>\n" +
            "                    </OperatingSystemType>\n" +
            "                </OperatingSystem>" +
            "            </CandidateHosts>\n" +
            "        </Resources>\n" +
            "        <DataStaging>\n" +
            "            <FileName>mirror.sh</FileName>\n" +
            "            <CreationFlag>overwrite</CreationFlag>\n" +
            "            <DeleteOnTermination>true</DeleteOnTermination>\n" +
            "            <Source>\n" +
            "                <URI>_PLACEHOLDER_SOURCEFILESERVER_</URI>\n" +
            "            </Source>\n" +
            "        </DataStaging>\n" +
            "        <DataStaging>\n" +
            "            <FileName>triana.jar</FileName>\n" +
            "            <CreationFlag>overwrite</CreationFlag>\n" +
            "            <DeleteOnTermination>true</DeleteOnTermination>\n" +
            "            <Source>\n" +
            "                <URI>_PLACEHOLDER_SOURCEFILESERVER_</URI>\n" +
            "            </Source>\n" +
            "        </DataStaging>\n" +
            "    </JobDescription>\n" +
            "    <ns4:SDL_Type>\n" +
            "        <ns4:Constraints>\n" +
            "            <ns4:Middleware>\n" +
            "                <ns4:DCIName>_PLACEHOLDER_DCINAME_</ns4:DCIName>\n" +
            "                <ns4:MyProxy/>\n" +
            "                <ns4:ManagedResource>_PLACEHOLDER_RESOURCE_</ns4:ManagedResource>\n" +
            "            </ns4:Middleware>\n" +
            "            <ns4:Budget>0</ns4:Budget>\n" +
            "        </ns4:Constraints>\n" +
            "    </ns4:SDL_Type>\n" +
            "    <ns3:extension_type>\n" +
            "        <ns3:wfiservice>_PLACEHOLDER_STATUSSERVICE_</ns3:wfiservice>\n" +
            "        <ns3:proxyservice>_PLACEHOLDER_CREDENTIALPROVIDER_</ns3:proxyservice>\n" +
            "    </ns3:extension_type>\n" +
            "</JobDefinition>";
}
