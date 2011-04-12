package org.trianacode.shiwa.xslt;

import org.trianacode.shiwa.IwirWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 04/04/2011
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public class xsltTransformer {

    public xsltTransformer(String in, String out, String transformer) {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transform = tFactory.newTransformer(new StreamSource(new File(transformer)));

            transform.transform(new StreamSource(new File(in)), new StreamResult(new File(out)));
            System.out.println("Done");
        } catch (Exception e) {
            System.out.println("Fail");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String root = "triana-shiwa/src/main/java/org/trianacode/shiwa/xslt/";

        new IwirWriter();
        new xsltTransformer(root + "iwir/iwir.xml", root + "iwir/outputTemp.xml", root + "iwir/removeNamespace.xsl");
        new xsltTransformer(root + "iwir/outputTemp.xml", root + "iwir/output.xml", root + "iwir/iwir.xsl");

    }
}
