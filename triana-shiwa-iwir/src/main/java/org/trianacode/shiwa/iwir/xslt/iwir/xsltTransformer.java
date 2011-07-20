package org.trianacode.shiwa.iwir.xslt.iwir;

import org.trianacode.shiwa.iwir.test.IwirWriter;

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

    public xsltTransformer() {
    }

    public static void doTransform(String in, String out, String transformer) {
        StreamSource streamSource = new StreamSource(new File(in));
        StreamSource transformerSource = new StreamSource(new File(transformer));
        StreamResult streamResult = new StreamResult(new File(out));

        doTransform(streamSource, transformerSource, streamResult);
    }

    public static void doTransform(StreamSource streamSource, StreamSource transformerSource, StreamResult streamResult) {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transform = tFactory.newTransformer(transformerSource);

            transform.transform(streamSource, streamResult);
            System.out.println("Success with transform : " + transformerSource.getSystemId());
        } catch (Exception e) {
            System.out.println("Fail : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String root = "triana-shiwa/src/main/java/org/trianacode/shiwa/xslt/";

        new IwirWriter();
        xsltTransformer.doTransform(root + "iwir/iwir.xml", root + "iwir/outputTemp.xml", root + "iwir/removeNamespace.xsl");
        xsltTransformer.doTransform(root + "iwir/outputTemp.xml", root + "iwir/output.xml", root + "iwir/iwir.xsl");

    }
}
