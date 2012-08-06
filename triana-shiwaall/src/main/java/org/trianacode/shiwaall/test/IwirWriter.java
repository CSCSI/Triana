package org.trianacode.shiwaall.test;


import org.shiwa.fgi.iwir.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 22/02/2011
 * Time: 17:08
 * To change this template use File | Settings | File Templates.
 */
public class IwirWriter {
    public IwirWriter() {
        IWIR iwir = new IWIR();
        iwir.setWfname("test");

        BlockScope mainBlock = new BlockScope("mainBlock");
        mainBlock.addInputPort(new InputPort("mainIn", SimpleType.FILE));
        mainBlock.addOutputPort(new OutputPort("mainOut", SimpleType.FILE));

        Task t1 = new Task("t1", "consumer");
        t1.addInputPort(new InputPort("t1i1", SimpleType.FILE));
        t1.addOutputPort(new OutputPort("t1o1", SimpleType.FILE));
        t1.addOutputPort(new OutputPort("t1o2", SimpleType.FILE));

        Task t2 = new Task("t2", "consumer");
        t2.addInputPort(new InputPort("t2i1", SimpleType.FILE));
        t2.addOutputPort(new OutputPort("t2o1", SimpleType.FILE));

        Task t3 = new Task("t3", "consumer");
        t3.addInputPort(new InputPort("t3i1", SimpleType.FILE));
        t3.addOutputPort(new OutputPort("t3o1", SimpleType.FILE));

        Task t4 = new Task("t4", "consumer");
        t4.addInputPort(new InputPort("t4i1", SimpleType.FILE));
        t4.addInputPort(new InputPort("t4i2", SimpleType.FILE));
        t4.addOutputPort(new OutputPort("t4o1", SimpleType.FILE));

        mainBlock.addTask(t1);
        mainBlock.addTask(t2);
        mainBlock.addTask(t3);
        mainBlock.addTask(t4);

        mainBlock.addLink(t1.getPort("t1o1"), t2.getPort("t2i1"));
        mainBlock.addLink(t1.getPort("t1o2"), t3.getPort("t3i1"));
        mainBlock.addLink(t2.getPort("t2o1"), t4.getPort("t4i1"));
        mainBlock.addLink(t3.getPort("t3o1"), t4.getPort("t4i2"));

        mainBlock.addLink(mainBlock.getPort("mainIn"), t1.getPort("t1i1"));
        mainBlock.addLink(t4.getPort("t4o1"), mainBlock.getPort("mainOut"));

        iwir.setTask(mainBlock);
        System.out.println(iwir.asXMLString());

        String root = "triana-shiwa-iwir/src/main/java/org/trianacode/shiwa/iwir/xslt/";
        File output = new File(root + "iwir/iwir.xml");
        try {
            iwir.asXMLFile(output);
            System.out.println("Wrote iwir to " + output.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Failed to write file.");
            e.printStackTrace();
        }

//           org.shiwa.fgi.iwir.examples.CrossProduct.main(new String[0]);
    }

    public static void main(String[] args) {
        new IwirWriter();
    }
}
