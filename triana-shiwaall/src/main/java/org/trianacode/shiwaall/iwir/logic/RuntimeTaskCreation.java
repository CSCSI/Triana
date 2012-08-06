package org.trianacode.shiwaall.iwir.logic;

import org.trianacode.gui.main.organize.DaxOrganize;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.proxy.java.JavaProxy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 13/07/2011
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */

public class RuntimeTaskCreation extends Unit {

    @Override
    public void process() throws Exception {
        ArrayList<Object> allInputs = new ArrayList<Object>();
        System.out.println("Connected nodes : " + this.getInputNodeCount());
        for (int i = 0; i < this.getInputNodeCount(); i++) {
            allInputs.add(this.getInputAtNode(i));
        }

        for (Object inputData : allInputs) {
            if (inputData instanceof List) {
                System.out.println("\nRuntime task creator will attempt to add : " + ((List) inputData).size());

                for (Object inputFromList : (List) inputData) {
                    EmptyStringCreator unit = new EmptyStringCreator();
                    Task addedTask = addAndConnectUnit(unit, inputFromList);
                }
            } else {
                EmptyStringCreator unit = new EmptyStringCreator();
                Task addedTask = addAndConnectUnit(unit, inputData);
            }
        }

        //    output("Some text");
    }

    private Task addAndConnectUnit(Unit unit, Object data) {
        TaskGraph parent = this.getTask().getParent();

        org.trianacode.taskgraph.tool.Tool tool = initTool(unit);
        Task newTask = null;
        try {
            newTask = parent.createTask(tool);

            Node outputNode = this.getTask().addDataOutputNode();
            System.out.println("Added node : " + outputNode.getNodeIndex());
            Node inputNode = newTask.addDataInputNode();

            Cable cable = parent.connect(outputNode, inputNode);

            if (cable.isConnected() && data != null) {

                System.out.println("Output " + data.toString() + " to node : " + outputNode.getNodeIndex());
                outputAtNode(outputNode.getNodeIndex(), data);
            }

        } catch (TaskException e) {
            e.printStackTrace();
        } catch (CableException e) {
            System.out.println("Failed to add cable");
            e.printStackTrace();
        }
        DaxOrganize daxOrganize = new DaxOrganize(parent);
        return newTask;
    }

    private ToolImp initTool(Unit unit) {
        ToolImp tool = null;
        ProxyFactory.initProxyFactory();
        try {
            tool = new ToolImp(this.getTask().getProperties());

            tool.setProxy(new JavaProxy(unit, unit.getClass().getSimpleName(), unit.getClass().getPackage().getName()));

            tool.setToolName(unit.getClass().getSimpleName());
            tool.setToolPackage(unit.getClass().getPackage().getName());
//            tool.setDataInputNodeCount(taskHolder.getIWIRTask().getInputPorts().size());
//            tool.setDataOutputNodeCount(taskHolder.getIWIRTask().getOutputPorts().size());
        } catch (Exception e) {
            System.out.println("Failed to initialise tool from Unit.");
            e.printStackTrace();
        }
        return tool;
    }

    @Override
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    @Override
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

}
