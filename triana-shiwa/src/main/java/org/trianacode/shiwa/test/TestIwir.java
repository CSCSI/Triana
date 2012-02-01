package org.trianacode.shiwa.test;

import org.shiwa.fgi.iwir.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 20/07/2011
 * Time: 13:14
 * To change this template use File | Settings | File Templates.
 */
public class TestIwir {

    public static void main(String[] args) {
        TestIwir testIwir = new TestIwir();
        testIwir.testIwir();
    }


    private IWIR testIwir() {

        IWIR crossProduct = null;
        try {
            crossProduct = build();

            // to stdout
            System.out.println(crossProduct.asXMLString());

            // to file
            crossProduct.asXMLFile(new File("crossProduct.xml"));

            // form file
            crossProduct = new IWIR(new File("crossProduct.xml"));

            // to stdout
            System.out.println(crossProduct.asXMLString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return crossProduct;

    }

    private IWIR build() {
        IWIR i = new IWIR("crossProduct");

        ParallelForEachTask forEach1 = new ParallelForEachTask("foreach1");
        forEach1.addInputPort(new InputPort("collB", new CollectionType(
                SimpleType.FILE)));
        forEach1.addLoopElement(new LoopElement("collA", new CollectionType(
                SimpleType.FILE)));

        ParallelForEachTask forEach2 = new ParallelForEachTask("foreach2");
        forEach2.addInputPort(new InputPort("elementA", SimpleType.FILE));
        forEach2.addLoopElement(new LoopElement("collB", new CollectionType(
                SimpleType.FILE)));

        org.shiwa.fgi.iwir.Task a = new org.shiwa.fgi.iwir.Task("A", "consumer");
        a.addInputPort(new InputPort("elementA", SimpleType.FILE));
        a.addInputPort(new InputPort("elementB", SimpleType.FILE));
        a.addOutputPort(new OutputPort("res", SimpleType.FILE));

        forEach2.addTask(a);
        forEach2.addOutputPort(new OutputPort("res", new CollectionType(
                SimpleType.FILE)));
        forEach2.addLink(forEach2.getPort("elementA"), a.getPort("elementA"));
        forEach2.addLink(forEach2.getPort("collB"), a.getPort("elementB"));
        forEach2.addLink(a.getPort("res"), forEach2.getPort("res"));

        forEach1.addTask(forEach2);
        forEach1.addOutputPort(new OutputPort("res", new CollectionType(
                new CollectionType(SimpleType.FILE))));
        forEach1.addLink(forEach1.getPort("collA"),
                forEach2.getPort("elementA"));
        forEach1.addLink(forEach1.getPort("collB"), forEach2.getPort("collB"));
        forEach1.addLink(forEach2.getPort("res"), forEach1.getPort("res"));

        i.setTask(forEach1);

        return i;
    }
}
