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
import org.trianacode.shiwaall.extras.FileBuilder;
import org.trianacode.shiwaall.iwir.importer.utils.ExportIwir;
import org.trianacode.taskgraph.TaskGraph;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* Created by IntelliJ IDEA.
* User: Ian Harvey
* Date: 15/12/2011
* Time: 14:26
* To change this template use File | Settings | File Templates.
*/
public class TrianaIWIRHandler implements FGIWorkflowEngineHandler {

    private IWIR iwir;
    private InputStream imageInputStream;
    private Map<String, FGITaskHandler> jsdls = null;


    //For internal testing
    private TrianaIWIRHandler(){}

    public TrianaIWIRHandler(TaskGraph taskGraph, InputStream trianaImage) {
        try {
            init(taskGraph);
            this.imageInputStream = getImageInputStream(trianaImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream getImageInputStream(InputStream trianaImage){
        InputStream image;
        try{

            // if dot isn't in default unix location, use the triana image.
            File file = new File("/usr/local/bin/dot");
            if(file.exists()){
                File imageFile = iwir.getImage(file.getAbsolutePath(), "png", "");
                System.out.println("dot image " + imageFile.getAbsolutePath());
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

    private void init(TaskGraph taskGraph) throws IOException {
        ExportIwir exportIwir = new ExportIwir();
        BlockScope blockscope = exportIwir.taskGraphToBlockScope(taskGraph);
        iwir = new IWIR(taskGraph.getToolName());
        iwir.setTask(blockscope);

        System.out.println(iwir.asXMLString());

        try {
            prepareJSDLs(iwir);
        } catch (JAXBException e) {
            e.printStackTrace();
            jsdls = null;
        } catch (FileImTeaPotException ignored) {}

    }

    public static void main(String[] args) throws IOException, JAXBException, FileImTeaPotException {
        TrianaIWIRHandler trianaIWIRHandler = new TrianaIWIRHandler();

        IWIR iwir1 = new IWIR(new File("/Users/ian/jsdl/activity.xml"));

        trianaIWIRHandler.prepareJSDLs(iwir1);

    }

    private void prepareJSDLs(IWIR iwir) throws JAXBException, IOException, FileImTeaPotException {
        jsdls = new HashMap<String, FGITaskHandler>();
        Parser parser = new Parser();

//        List<DataStagingType> dataStaging = jobDefinitionType.getJobDescription().getDataStaging();

//        JobDescriptionType jobDescriptionType = new JobDescriptionType();

//        //JobIdentification type
//        JobIdentificationType jobIdentificationType = new JobIdentificationType();
//        jobIdentificationType.setJobName(name);
//        jobIdentificationType.setDescription("");
//        jobDescriptionType.setJobIdentification(jobIdentificationType);
//
//        ApplicationType applicationType = new ApplicationType();
//        applicationType.setApplicationName("");
//        POSIXApplicationType posixApplicationType = new POSIXApplicationType();
//        jobDescriptionType.setApplication(applicationType);
//
//        ResourcesType resourcesType = new ResourcesType();
//        CandidateHostsType candidateHostsType = new CandidateHostsType();
//        resourcesType.setCandidateHosts(candidateHostsType);
//        jobDescriptionType.setResources(resourcesType);
//
//        jobDefinitionType.setJobDescription(jobDescriptionType);

        for(String tasktype : iwir.getAtomicTaskTypes()){
            JobDefinitionType jobDefinitionType = parser.readJSDLFromString(horribleJSDLhack);

            for(Task task : iwir.getAtomicTasks()){
                if(task.getTasktype().equals(tasktype)){

                    List<InputPort> inputPorts = task.getInputPorts();
                    for(int in = 0; in < inputPorts.size(); in++){
                        InputPort inputPort = inputPorts.get(in);
                        String portName = inputPort.getName();

                        String filename = "input_" + in;

//                        parser.DeleteDataStaging(filename);
                        parser.AddInputDataStaging(filename, Parser.PLACEHOLDER_SOURCEURI);
                        parser.ModifyDatastagingName(filename, portName);

                    }

                    List<OutputPort> outputPorts = task.getOutputPorts();
                    for(int out = 0; out < outputPorts.size(); out++){
                        OutputPort outputPort = outputPorts.get(out);
                        String portName = outputPort.getName();

                        String filename = "output_" + out;

//                        parser.DeleteDataStaging(filename);
                        parser.AddOutputDataStaging(filename, Parser.PLACEHOLDER_TARGETURI);
                        parser.ModifyDatastagingName(filename, portName);

                    }

                    parser.createExtensionTaginJSDL(jobDefinitionType);

                    String jsdlXML = parser.getJSDLXML(jobDefinitionType);
                    System.out.println("\n\n" + jsdlXML);

                    File jsdlFile = new File(tasktype + ".jdsl");
                    new FileBuilder(jsdlFile.getAbsolutePath(), jsdlXML);
                    System.out.println("Created jsdl at " + jsdlFile.getAbsolutePath());

                    IWIRTaskHandler iwirTaskHandler = new IWIRTaskHandler(task);
                    iwirTaskHandler.setDefinitionFile(jsdlFile);

                    jsdls.put(tasktype, iwirTaskHandler);

                    System.out.println(jsdls.keySet());
                }
            }
        }
    }

    @Override
    public String getEngineName(Set<String> strings) {
        return "Triana";
    }

    @Override
    public String getEngineVersion() {
        return "4.0";
    }

    @Override
    public String getWorkflowLanguage(Set<String> strings) {
        return "IWIR";
    }

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

    @Override
    public InputStream getDefinition() {
        try {
            return new ByteArrayInputStream(iwir.asXMLString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDefinitionName() {
        return iwir.getWfname();
    }

    @Override
    public InputStream getDisplayImage() {
        return imageInputStream;
    }

    @Override
    public String getDisplayImageName() {
        return iwir.getWfname() + "-image.jpg";
    }

    @Override
    public List<Author> getAuthors() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public Map<String, FGITaskHandler> getTaskHandlers() {
        return jsdls;
    }

    private static String horribleJSDLhack = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<JobDefinition xmlns=\"http://schemas.ggf.org/jsdl/2005/11/jsdl\" xmlns:ns2=\"http://schemas.ggf.org/jsdl/2005/11/jsdl-posix\" xmlns:ns3=\"extension.dci\" xmlns:ns4=\"uri:MBSchedulingDescriptionLanguage\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://schemas.ggf.org/jsdl/2005/11/jsdl\">\n" +
            "    <JobDescription>\n" +
            "        <JobIdentification>\n" +
            "            <JobName></JobName>\n" +
            "            <JobAnnotation>_PLACEHOLDER_JOBANNOTATION_</JobAnnotation>\n" +
            "        </JobIdentification>\n" +
            "        <Application>\n" +
            "            <ApplicationName>mirror</ApplicationName>\n" +
            "            <ns2:POSIXApplication_Type>\n" +
            "                <ns2:Executable>triana.sh</ns2:Executable>\n" +
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
