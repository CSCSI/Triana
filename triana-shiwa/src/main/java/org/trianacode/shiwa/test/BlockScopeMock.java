package org.trianacode.shiwa.test;

import org.shiwa.fgi.iwir.*;

public class BlockScopeMock {
    // a dummy example with new blockscope construct

    public static void main(String[] args) {
        IWIR i = new BlockScopeMock().build();

        System.out.println(i.asXMLString());
    }

    public IWIR build() {

        BlockScope bs = new BlockScope("topLevel");
        bs.addInputPort(new InputPort("in1", SimpleType.INTEGER));
        bs.addInputPort(new InputPort("in2", new CollectionType(
                SimpleType.INTEGER)));
        // ip s

        Task a = new Task("A", "calc1");
        a.addInputPort(new InputPort("in1", SimpleType.INTEGER));
        a.addOutputPort(new OutputPort("out1", SimpleType.INTEGER));

        Task b = new Task("B", "calc2");
        b.addInputPort(new InputPort("in1", SimpleType.INTEGER));
        b.addInputPort(new InputPort("in2", SimpleType.INTEGER));
        b.addOutputPort(new OutputPort("out1", SimpleType.INTEGER));

        IfTask ite = new IfTask("ITE");
        ite.addInputPort(new InputPort("in1", SimpleType.INTEGER));
        ite.addInputPort(new InputPort("in2", new CollectionType(
                SimpleType.INTEGER)));
        ite.setCondition(new ConditionExpression("in1 = 1"));

        // then
        ParallelForEachTask foreach1 = new ParallelForEachTask("foreach1");
        foreach1.addInputPort(new InputPort("in1", SimpleType.INTEGER));
        foreach1.addLoopElement(new LoopElement("lp1", new CollectionType(
                SimpleType.INTEGER)));

        Task c = new Task("C", "consumer");
        c.addInputPort(new InputPort("in1", SimpleType.INTEGER));
        c.addInputPort(new InputPort("in2", SimpleType.INTEGER));
        c.addOutputPort(new OutputPort("out1", SimpleType.INTEGER));

        foreach1.addTask(c);
        foreach1.addOutputPort(new OutputPort("out1", new CollectionType(
                SimpleType.INTEGER)));
        foreach1.addLink(foreach1.getPort("in1"), c.getPort("in1"));
        foreach1.addLink(foreach1.getPort("lp1"), c.getPort("in2"));
        foreach1.addLink(c.getPort("out1"), foreach1.getPort("out1"));

        ite.addTaskToThenBlock(foreach1);

        // else
        Task d = new Task("D", "consumer");
        d.addInputPort(new InputPort("in1", SimpleType.INTEGER));
        d.addOutputPort(new OutputPort("out1", SimpleType.INTEGER));

        ite.addTaskToElseBlock(d);
        ite.addOutputPort(new OutputPort("out1", new CollectionType(
                SimpleType.INTEGER)));
        ite.addLink(ite.getPort("in1"), foreach1.getPort("in1"));
        ite.addLink(ite.getPort("in2"), foreach1.getPort("lp1"));
        ite.addLink(foreach1.getPort("out1"), ite.getPort("out1"));
        ite.addLink(ite.getPort("in1"), d.getPort("in1"));
        ite.addLink(d.getPort("out1"), ite.getPort("out1"));

        bs.addTask(a);
        bs.addTask(b);
        bs.addLink(bs.getPort("in1"), a.getPort("in1"));
        bs.addLink(bs.getPort("in1"), b.getPort("in1"));
        bs.addLink(a.getPort("out1"), b.getPort("in2"));

        bs.addTask(ite);
        bs.addLink(b.getPort("out1"), ite.getPort("in1"));
        bs.addLink(bs.getPort("in2"), ite.getPort("in2"));

        bs.addOutputPort(new OutputPort("out1", new CollectionType(
                SimpleType.INTEGER)));
        bs.addLink(ite.getPort("out1"), bs.getPort("out1"));

        IWIR dummy = new IWIR("blockScope");

        dummy.setTask(bs);
        return dummy;

    }

}