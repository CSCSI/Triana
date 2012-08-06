package org.trianacode.convert;

import org.shiwa.fgi.iwir.IWIR;
import org.trianacode.TrianaInstance;
import org.trianacode.shiwaall.iwir.importer.utils.ExportIwir;
import org.trianacode.shiwaall.iwir.importer.utils.ImportIwir;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.tool.Tool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 25/05/2012
 * Time: 03:17
 * To change this template use File | Settings | File Templates.
 */
public class ConvertLanguage {

    public static final String TASKGRAPH_FORMAT = "taskgraph";
    public static final String IWIR_FORMAT = "iwir";

    public static void main(String[] args) {
        ConvertLanguage convertLanguage = new ConvertLanguage();

        File outputFile;
        File inputFile;
        if (args.length == 2) {
            inputFile = new File(args[0]);
            outputFile = new File(args[1]);
//        } else {
//            inputFile = new File("/Users/ian/tempStuff/image-new.iwir");
//            outputFile = new File("/Users/ian/tempStuff/outputFile.xml");

            try {
                convertLanguage.doConvert(inputFile, outputFile);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.exit(1);
            }
            System.exit(0);
        } else {
            System.out.println("Usage : inputfile outputfile");
        }
    }

    public ConvertLanguage(){}

    public void doConvert(File inputFile, File outputFile) throws IOException, TaskGraphException, ProxyInstantiationException, JAXBException {
        if(inputFile.exists()){

            String type = getWorkflowType(inputFile);

            if(type.equals(IWIR_FORMAT)){
                System.out.println("Found IWIR, converting to TaskGraph");

                TrianaInstance instance = new TrianaInstance();
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                instance.init();
                System.out.println("Bootstrapping Triana");
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

                ImportIwir iwirImport = new ImportIwir();
                TaskGraph tool = iwirImport.taskFromIwir(new IWIR(inputFile), null);

                XMLWriter outWriter = new XMLWriter(new PrintWriter(System.out));
                outWriter.writeComponent(tool);
                XMLWriter fileWriter = new XMLWriter(new PrintWriter(outputFile));
                fileWriter.writeComponent(tool);
                System.out.println("File created : " + outputFile.getAbsolutePath());

            }
            else if(type.equals(TASKGRAPH_FORMAT)){
                System.out.println("Found TaskGraph, converting to IWIR");
                XMLReader reader = new XMLReader(new FileReader(inputFile));

                TrianaInstance instance = new TrianaInstance();
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                instance.init();
                System.out.println("Bootstrapping Triana");
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

                Tool tool = reader.readComponent(instance.getProperties());
                ExportIwir exportIwir = new ExportIwir();
                exportIwir.taskGraphToIWIRFile((TaskGraph) tool, outputFile);
            } else {
                System.out.println("Type not recognised " + type);
            }
        } else {
            System.out.println("Input file " + inputFile.getAbsolutePath() + " not found.");
            throw new FileNotFoundException();
        }
    }

    public static String getWorkflowType(File file) {
        String workflowType = null;
        if (file != null && file.exists()) {
            try {
                System.out.println(file.getAbsolutePath());

                DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
                Document doc = docBuilder.parse(file);
                Element i = doc.getDocumentElement();
                NamedNodeMap l = i.getAttributes();
                for (int m = 0; m < l.getLength(); m++) {
                    org.w3c.dom.Node o = l.item(m);
                    System.out.println(o.toString());
                }
                String rootName = i.getNodeName();
                System.out.println("XML root is : " + rootName);
                if (rootName.toLowerCase().equals(IWIR_FORMAT)) {
                    return IWIR_FORMAT;
                } else if (rootName.toLowerCase().equals("tool")) {
                    return TASKGRAPH_FORMAT;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return workflowType;
    }

}
