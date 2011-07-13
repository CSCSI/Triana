package org.trianacode.iwir;

import org.trianacode.annotation.CustomGUIComponent;
import org.trianacode.annotation.Tool;
import org.trianacode.gui.main.organize.DaxOrganize;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.annotation.TaskConscious;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.proxy.java.JavaProxy;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 13/07/2011
 * Time: 14:14
 * To change this template use File | Settings | File Templates.
 */

@Tool
public class RuntimeTaskCreation implements TaskConscious {

    private Task task;

    @org.trianacode.annotation.Process(gather = true)
    public String process(List in) {
        System.out.println("\nRuntime task creator will attempt to add : " + in.size());

        TaskGraph parent = task.getParent();

        for (Object input : in) {

            EmptyStringCreator unit = new EmptyStringCreator();

            org.trianacode.taskgraph.tool.Tool tool = initTool(unit);
            try {
                Task newTask = parent.createTask(tool);
                parent.connect(task.addDataOutputNode(), newTask.addDataInputNode());
            } catch (TaskException e) {
                e.printStackTrace();
            } catch (CableException e) {
                System.out.println("Screwed up adding cable");
                e.printStackTrace();
            }

        }

        DaxOrganize daxOrganize = new DaxOrganize(parent);

        return "Some text";
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }

    private ToolImp initTool(Unit unit) {
        ToolImp tool = null;
        ProxyFactory.initProxyFactory();
        try {
            tool = new ToolImp(task.getProperties());

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

    @CustomGUIComponent
    public Component gui() {
        return new JPanel();
    }
}
