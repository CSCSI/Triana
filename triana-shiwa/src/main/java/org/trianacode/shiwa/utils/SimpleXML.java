package org.trianacode.shiwa.utils;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 23/12/2011
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
public class SimpleXML {

    public static Document makeXMLDocument(String rootName) throws Exception {

        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        ////////////////////////
        //Creating the XML tree

        //create the root element and add it to the document
        Element root = doc.createElement(rootName);
        doc.appendChild(root);

//
//        Element input = doc.createElement("input");
//        input.setAttribute("node_name", cable.getToBox().getName());
//        input.setAttribute("bundle_name", cable.getToBox().getBundleName());
//
//        Element output = doc.createElement("output");
//        output.setAttribute("node_name", cable.getFromBox().getName());
//        output.setAttribute("bundle_name", cable.getFromBox().getBundleName());

//
//        root.appendChild(input);
//        root.appendChild(output);

        return doc;
    }

    public static Document addRootComment(Document document, String commentText) {
        Comment comment = document.createComment(commentText);
        document.getDocumentElement().appendChild(comment);
        return document;
    }

    public static Element createElement(Document document, Element parent, String key, String value) {
        Element newElement = document.createElement(key);
        newElement.setTextContent(value);
        parent.appendChild(newElement);
        return newElement;
    }

    public static String getXMLasString(Document document) throws TransformerException {
        Source source = new DOMSource(document);
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.setParameter(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        Writer outputWriter = new StringWriter();
        Result stringOut = new StreamResult(outputWriter);
        xformer.transform(source, stringOut);

        return outputWriter.toString();
    }

    public static void writeXmlFile(Document doc, File saveFile, boolean systemOut) {
        try {

            Source source = new DOMSource(doc);
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setParameter(OutputKeys.INDENT, "yes");
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            if (saveFile != null) {
                Result result = new StreamResult(saveFile);
                xformer.transform(source, result);
            }

            Writer outputWriter = new StringWriter();
            Result stringOut = new StreamResult(outputWriter);
            xformer.transform(source, stringOut);

//            new SinglePanelPopup(new TextAreaPane(outputWriter.toString()));

            if (systemOut) {
                Result system = new StreamResult(System.out);
                xformer.transform(source, system);
            }

        } catch (TransformerConfigurationException e) {
        } catch (TransformerException e) {
        }
    }

    public static Document documentFromString(String xmlString) throws IOException, SAXException {
        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new java.io.StringReader(xmlString)));
        return parser.getDocument();
    }

    public static String flattenString(String output) {
        return output.replaceAll("(\\r|\\n)", "");
    }
}
