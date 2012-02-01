package org.trianacode.shiwa.iwir.importer.utils;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 02/12/2011
 * Time: 18:17
 * To change this template use File | Settings | File Templates.
 */
public class TaskTypeRepo {
    public TaskTypeRepo() {

    }

    public File getConcreteDescriptor(String taskType) throws ParserConfigurationException, IOException, TransformerException {
        File file = null;

        Document doc = makeXMLRequest(taskType);

        File requestFile = writeXmlFile(doc);

        file = sendRequest(requestFile, new URL("http://www.test.com"));

        return file;
    }

    private File sendRequest(File requestFile, URL url) {
        return null;
    }

    private Document makeXMLRequest(String taskTypeString) throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        //create the root element and add it to the document
        Element root = doc.createElement("TaskType");
        doc.appendChild(root);

        //create a comment and put it in the root element
        Comment comment = doc.createComment("Request for concrete binary to run task");
        root.appendChild(comment);


        Element tasktype = doc.createElement("request");
        tasktype.setAttribute("tasktype", taskTypeString);
        tasktype.setAttribute("engine", "Triana");

        Element env = doc.createElement("env");
        env.setAttribute("arch", System.getProperty("os.arch"));
        env.setAttribute("os", System.getProperty("os.name"));
        env.setAttribute("os.verson", System.getProperty("os.version"));

        Element java = doc.createElement("java");
        java.setAttribute("jvm", System.getProperty("java.vendor"));
        java.setAttribute("jvm.version", System.getProperty("java.version"));

        Element limits = doc.createElement("limits");
        limits.setAttribute("size.max", "50000000");
        limits.setAttribute("accepted.fileTypes", "bin,zip,sh,");

        root.appendChild(tasktype);
        root.appendChild(env);
        root.appendChild(java);
        root.appendChild(limits);

        return doc;
    }

    public File writeXmlFile(Document doc) throws IOException, TransformerException {
        File saveFile = File.createTempFile("tasktyperequest", "");
        Source source = new DOMSource(doc);
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setParameter(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        if (saveFile != null) {
            Result result = new StreamResult(saveFile);
            xformer.transform(source, result);
        }
//        Result systemOut = new StreamResult(System.out);
//        xformer.transform(source, systemOut);

        Writer outputWriter = new StringWriter();
        Result stringOut = new StreamResult(outputWriter);
        xformer.transform(source, stringOut);

        return saveFile;
    }
}
