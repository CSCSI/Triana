package org.trianacode.shiwaall.iwir.holders;

import org.trianacode.shiwaall.iwir.factory.AbstractTaskHolder;
import org.trianacode.shiwaall.iwir.factory.models.IWIRControlComponentModel;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.imp.RenderingHintImp;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyFactory;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.tool.Tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 09/03/2011
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class ParallelForEachTaskHolder extends AbstractTaskHolder {
    private Tool parallelTask;
    ArrayList<Object> allInputs;
    HashMap<Task, Node> attachments;
    TaskGraph parent;

    //add arraylists to hold new nodes

    public void init() {
        super.init();
        defineParameter("count", "0", Unit.USER_ACCESSIBLE);
        setGUIBuilderV2Info("Loops $title count IntScroller 0 100 0");
        getTask().addRenderingHint(
                new RenderingHintImp(
                        IWIRControlComponentModel.IWIR_CONTROL_RENDERING_HINT, false
                )
        );
    }

    @Override
    public void process() throws Exception {
        parent = this.getTask().getParent();

        ArrayList<Task> addedTasks = new ArrayList<Task>();

        //assuming the parallel task is atomic, not something loopy and horrible.
        Unit parallelUnit = new AtomicTaskHolder();

        //assuming theres an executable which can do something.

//        ((AtomicTaskHolder) parallelUnit).setExecutable(new Executable());


        int parallelNumber = 0;
        allInputs = new ArrayList<Object>();
        System.out.println("Connected nodes : " + this.getInputNodeCount());
        for (int i = 0; i < this.getInputNodeCount(); i++) {
            Object inputObject = getInputAtNode(i);
            allInputs.add(inputObject);
            if (inputObject instanceof List) {
                int thisSize = ((List) inputObject).size();
                if (thisSize > parallelNumber) {
                    parallelNumber = thisSize;
                }
            }
        }

        attachments = new HashMap<Task, Node>();
        for (int i = 0; i < allInputs.size(); i++) {
            Object inputData = allInputs.get(i);
            if (inputData instanceof List) {
                System.out.println("\nRuntime task creator will attempt to add : " + ((List) inputData).size());

                for (Object inputFromList : (List) inputData) {

                    Unit unit = parallelUnit.getClass().newInstance();
                    Task addedTask = createTask(unit);
                    connectTask(addedTask);
                    addedTasks.add(addedTask);
                }
            } else {
                Unit unit = parallelUnit.getClass().newInstance();
                Task addedTask = createTask(unit);
                connectTask(addedTask);
                addedTasks.add(addedTask);
            }
        }

        //TODO currently organising before connecting and creating cycles - check for cycles
//        DaxOrganize daxOrganize = new DaxOrganize(this.getTask().getParent());

        addLoopbackCable(addedTasks);

        sendData(addedTasks);
    }

    private void sendData(ArrayList<Task> addedTasks) {
        for (int i = 0; i < allInputs.size(); i++) {
            Object input = allInputs.get(0);
            if (input instanceof List) {
                List inputList = (List) input;
                for (Task task : addedTasks) {
                    Node node = attachments.get(task);
                    if (node != null) {
                        int outputNodeNumber = node.getAbsoluteNodeIndex();
                        if (outputNodeNumber <= i && inputList.size() >= i) {
                            outputAtNode(attachments.get(task).getAbsoluteNodeIndex(), inputList.get(i));
                            System.out.println("Output " +
                                    inputList.get(i) +
                                    " to node : " +
                                    outputNodeNumber);
                        }
                    }
                }
            } else {
                for (Task task : addedTasks) {
                    Node node = attachments.get(task);
                    if (node != null) {
                        int outputNodeNumber = node.getAbsoluteNodeIndex();
                        if (outputNodeNumber <= i) {
                            outputAtNode(attachments.get(task).getAbsoluteNodeIndex(), input);
                        }
                    }
                }
            }
        }

    }

    private void addLoopbackCable(ArrayList<Task> addedTasks) {
        for (Task newTask : addedTasks) {
            try {
                Node thisTaskInputNode = this.getTask().addDataInputNode();
                System.out.println("Added input node : " + thisTaskInputNode.getNodeIndex());
                Node nextTaskOutputNode = newTask.addDataOutputNode();
                Cable loopBackCable = parent.connect(nextTaskOutputNode, thisTaskInputNode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Task createTask(Unit unit) {
        return createTask(createTool(unit));
    }

    private Task createTask(Tool tool) {
        Task newTask = null;
        try {
            newTask = parent.createTask(tool);
            newTask.setParameter(Task.GUI_X, this.getParameter(Task.GUI_X));
            newTask.setParameter(Task.GUI_Y, this.getParameter(Task.GUI_Y));
        } catch (TaskException e) {
            e.printStackTrace();
        }
        return newTask;
    }

    private void connectTask(Task newTask) {
        try {
            Node thisTaskOutputNode = this.getTask().addDataOutputNode();
            System.out.println("Added output node : " + thisTaskOutputNode.getNodeIndex());
            Node nextTaskInputNode = newTask.addDataInputNode();
            Cable dataOutCable = parent.connect(thisTaskOutputNode, nextTaskInputNode);

            if (dataOutCable.isConnected()) {
                attachments.put(newTask, thisTaskOutputNode);
            }
        } catch (CableException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        }
    }

    private Task addAndConnectTool(Tool tool, Object data) {
        TaskGraph parent = this.getTask().getParent();

        Task newTask = null;
        try {
            Tool clonedTool = TaskGraphUtils.cloneTool(tool);
            newTask = parent.createTask(clonedTool, true);

            Node thisTaskOutputNode = this.getTask().addDataOutputNode();
            System.out.println("Added output node : " + thisTaskOutputNode.getNodeIndex());
            Node nextTaskInputNode = newTask.addDataInputNode();
            Cable dataOutCable = parent.connect(thisTaskOutputNode, nextTaskInputNode);

            if (dataOutCable.isConnected() && data != null) {
                System.out.println("Output " + data.toString() + " to node : " + thisTaskOutputNode.getNodeIndex());
                outputAtNode(thisTaskOutputNode.getNodeIndex(), data);
            }

        } catch (TaskException e) {
            e.printStackTrace();
        } catch (CableException e) {
            System.out.println("Failed to add cable");
            e.printStackTrace();
        }

        return newTask;
    }

    private ToolImp createTool(Unit unit) {
        ToolImp tool = null;
        ProxyFactory.initProxyFactory();
        try {
            tool = new ToolImp(this.getTask().getProperties());

            tool.setProxy(new JavaProxy(unit, unit.getClass().getSimpleName(), unit.getClass().getPackage().getName()));

            tool.setToolName(unit.getClass().getSimpleName());
            tool.setToolPackage(unit.getClass().getPackage().getName());
        } catch (Exception e) {
            System.out.println("Failed to initialise tool from Unit.");
            e.printStackTrace();
        }
        return tool;
    }

    public void setParallelTask(Task parallelTask) {
        this.parallelTask = parallelTask;
    }
}
