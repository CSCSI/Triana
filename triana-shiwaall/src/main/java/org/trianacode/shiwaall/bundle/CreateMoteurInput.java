package org.trianacode.shiwaall.bundle;

import org.trianacode.annotation.Process;
import org.trianacode.annotation.Tool;
import org.trianacode.shiwaall.utils.DomPain;
import org.trianacode.shiwaall.utils.SimpleXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Arrays;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 10/05/2012
 * Time: 18:41
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class CreateMoteurInput {

    /**
     * Process.
     *
     * @param list the list
     * @return the file
     */
    @Process(gather = true)
    public File process(List list) {

        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element inputData = doc.createElement("inputData");

            for (Object object : list) {
                if (object instanceof String) {
                    cutString((String) object, doc, inputData);
                }
            }
            doc.appendChild(inputData);

            File temp = File.createTempFile("moteurInput", "tmp");
            SimpleXML.writeXmlFile(doc, temp, true);

            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cut string.
     *
     * @param all the all
     * @param doc the doc
     * @param root the root
     */
    private static void cutString(String all, Document doc, Element root) {
        String[] inputs = (all).split(";");

        for (String input : inputs) {
            String[] inputString = (input).split(",");

            if (inputString.length == 4) {
                if (inputString[0].equals("input")) {
                    System.out.println(Arrays.toString(inputString));
                    addSource(doc, root, inputString[1], inputString[2], inputString[3]);
                }
            }
        }
    }

    /**
     * Adds the source.
     *
     * @param doc the doc
     * @param inputData the input data
     * @param name the name
     * @param type the type
     * @param value the value
     */
    private static void addSource(Document doc, Element inputData, String name, String type, String value) {
        Element source = doc.createElement("source");
        DomPain.attribute(source, "name", name);
        DomPain.attribute(source, "type", type);
        Element array = SimpleXML.createElement(doc, source, "array", "");
        Element item = SimpleXML.createElement(doc, array, "item", value);
        source.appendChild(array);
        inputData.appendChild(source);
    }

    /**
     * Make doc.
     *
     * @return the file
     */
    private static File makeDoc() {
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element inputData = doc.createElement("inputData");

            String test = "input,harmonics_max,Integer,32;" +
                    "input,freqpoints_max,Integer,250;" +
                    "input,audio_file,String,http://i9.cscloud.cf.ac.uk/webbed-nfs/nfs/dart/DARTAcousticG.wav";

            cutString(test, doc, inputData);
            doc.appendChild(inputData);

            System.out.println(SimpleXML.getXMLasString(doc));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        makeDoc();
    }
}
