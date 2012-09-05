package org.trianacode.shiwaall.converter;

import org.apache.commons.lang.ArrayUtils;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.enactment.addon.ConversionAddon;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.shiwaall.dax.FileUnit;
import org.trianacode.shiwaall.dax.JobUnit;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.imp.TaskFactoryImp;
import org.trianacode.taskgraph.imp.TaskImp;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.tool.Tool;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 28/10/2011
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
public class DaxifyTaskGraph implements ConversionAddon {

    /** The file unit class. */
    private Class fileUnitClass;
    
    /** The job unit class. */
    private Class jobUnitClass;

    /** The file iterator. */
    int fileIterator = 0;

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getServiceName();
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getUsageString()
     */
    @Override
    public String getUsageString() {
        return "";
    }

    /**
     * Convert.
     *
     * @param fileUnitClass the file unit class
     * @param jobUnitClass the job unit class
     * @param taskGraph the task graph
     * @return the task graph
     */
    public TaskGraph convert(Class fileUnitClass, Class jobUnitClass, TaskGraph taskGraph) {
        this.fileUnitClass = fileUnitClass;
        this.jobUnitClass = jobUnitClass;
        try {
            return daxifyTaskGraph(taskGraph, TaskFactory.DEFAULT_FACTORY_NAME, false, false, false);
        } catch (TaskGraphException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convert.
     *
     * @param taskGraph the task graph
     * @return the task graph
     */
    public TaskGraph convert(TaskGraph taskGraph) {
        fileUnitClass = FileUnit.class;
        jobUnitClass = JobUnit.class;
        try {
            return daxifyTaskGraph(taskGraph, TaskFactory.DEFAULT_FACTORY_NAME, false, false, false);
        } catch (TaskGraphException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ungroup all.
     *
     * @param taskGraph the task graph
     */
    private void ungroupAll(TaskGraph taskGraph) {
        for (Task task : taskGraph.getTasks(false)) {
            if (task instanceof TaskGraph) {
                TaskGraph inner = (TaskGraph) task;
                ungroupAll(inner);
                try {
                    taskGraph.unGroupTask(inner.getToolName());
                } catch (TaskGraphException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Daxify task graph.
     *
     * @param taskgraph the taskgraph
     * @param factorytype the factorytype
     * @param presclone the presclone
     * @param prestasks the prestasks
     * @param clonecontrol the clonecontrol
     * @return the task graph
     * @throws TaskGraphException the task graph exception
     */
    private TaskGraph daxifyTaskGraph(TaskGraph taskgraph, String factorytype, boolean presclone,
                                      boolean prestasks, boolean clonecontrol) throws TaskGraphException {

        ungroupAll(taskgraph);

        try {
            TaskGraph clone = TaskGraphManager.createTaskGraph(taskgraph, factorytype, presclone);
            if (taskgraph.getToolName() != null) {
                clone.setToolName(taskgraph.getToolName());
            }

            Task[] tasks = taskgraph.getTasks(false);
            for (Task task1 : tasks) {
                if (task1 instanceof TaskGraph) {
                    clone.createTask(daxifyTaskGraph((TaskGraph) task1, TaskFactory.DEFAULT_FACTORY_NAME, false, false, false));
                } else {
                    Task daxTask = initDaxTask(task1, jobUnitClass);
                    clone.createTask(daxTask, prestasks);
                }
            }

            System.out.println("Daxified taskgraph has " + clone.getTasks(false).length + " tasks.");

            Cable[] cables = TaskGraphUtils.getInternalCables(tasks);

            System.out.println("Cables " + ArrayUtils.toString(cables));

            Task task;
            Node sendnode;
            Node recnode;

            try {
                for (int count = 0; count < cables.length; count++) {
                    task = clone.getTask(taskgraph.getTask(cables[count].getSendingNode()).getToolName());

                    if (cables[count].getSendingNode().isParameterNode()) {
                        sendnode = task.getParameterOutputNode(cables[count].getSendingNode().getNodeIndex());
                    } else {
                        sendnode = task.getDataOutputNode(cables[count].getSendingNode().getNodeIndex());
                    }

                    task = clone.getTask(taskgraph.getTask(cables[count].getReceivingNode()).getToolName());

                    if (cables[count].getReceivingNode().isParameterNode()) {
                        recnode = task.getParameterInputNode(cables[count].getReceivingNode().getNodeIndex());
                    } else {
                        recnode = task.getDataInputNode(cables[count].getReceivingNode().getNodeIndex());
                    }

                    addFileUnit(sendnode, recnode, clone);

//                    clone.connect(sendnode, recnode);
                }
            } catch (CableException e) {
                e.printStackTrace();
            }

            TaskGraph group = taskgraph;
            Node nodes[] = group.getDataInputNodes();
            Node node;
            Node clonenode;

            try {
                for (int count = 0; count < nodes.length; count++) {
//                    if (taskgraph.isControlTaskConnected()) {
//                        node = TaskGraphUtils.getControlNode(nodes[count]).getCable().getReceivingNode();
//                    } else {
                    node = nodes[count].getParentNode();
//                    }

                    if (node.isParameterNode()) {
                        clonenode = clone.getTask(taskgraph.getTask(node).getToolName())
                                .getParameterInputNode(node.getNodeIndex());
                    } else {
                        clonenode = clone.getTask(taskgraph.getTask(node).getToolName())
                                .getDataInputNode(node.getNodeIndex());
                    }

                    clone.setGroupNodeParent(clone.getDataInputNode(count), clonenode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            nodes = group.getDataOutputNodes();

            try {
                for (int count = 0; count < nodes.length; count++) {
//                    if (taskgraph.isControlTaskConnected()) {
//                        node = TaskGraphUtils.getControlNode(nodes[count]).getCable().getSendingNode();
//                    } else {
                    node = nodes[count].getParentNode();
//                    }

                    if (node.isParameterNode()) {
                        clonenode = clone.getTask(taskgraph.getTask(node).getToolName())
                                .getParameterOutputNode(node.getNodeIndex());
                    } else {
                        clonenode = clone.getTask(taskgraph.getTask(node).getToolName())
                                .getDataOutputNode(node.getNodeIndex());
                    }

                    clone.setGroupNodeParent(clone.getDataOutputNode(count), clonenode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (taskgraph.isControlTask() && clonecontrol) {
                clone.createControlTask(taskgraph.getControlTask(), prestasks);
//
//                if (taskgraph.isControlTaskConnected()) {
//                    connectControlTask(clone);
//                }
            }
            TaskGraphContext context = taskgraph.getContext();
            Collection<String> keys = context.getKeys();
            for (String key : keys) {
                clone.setContextProperty(key, context.getProperty(key));
            }


            if (clone.getInputNodeCount() > 0) {
                for (Node inputNode : clone.getInputNodes()) {
                    addFileUnit(null, inputNode.getTopLevelNode(), clone);
                }
            }

            if (clone.getOutputNodeCount() > 0) {
                for (Node outputNode : clone.getOutputNodes()) {
                    addFileUnit(outputNode.getTopLevelNode(), null, clone);
                }
            }

            Node childNode = getTaskgraphChildNode(clone);
            if (childNode != null) {
//
                ToolImp creatorTool = new ToolImp(clone.getProperties());
                initTool(creatorTool, "DaxCreator", "org.trianacode.shiwaall.gui.guiUnits", 1, 0);
//                if (daxFilePath != null) {
//                    creatorTool.setParameter("fileName", daxFilePath);
//                }
                Task creatorTask = clone.createTask(creatorTool);

                //sensible location on the taskgraph
                TPoint childPoint = TaskLayoutUtils.getPosition(childNode.getTask());
                TPoint creatorPoint = new TPoint(childPoint.getX() + 1.5, childPoint.getY());
                TaskLayoutUtils.setPosition(creatorTask, creatorPoint);

                clone.connect(childNode, creatorTask.getDataInputNode(0));
            }

            return clone;
        } catch (ClassCastException except) {
            except.printStackTrace();
            throw (new TaskGraphException("cloningError" + ": " + "NodeError", except));
        } catch (TaskGraphException except) {
            except.printStackTrace();
            throw (new TaskGraphException("cloningError" + ": " + except.getMessage(), except));
        }
    }

    /**
     * Inits the tool.
     *
     * @param tool the tool
     * @param unitName the unit name
     * @param unitPackage the unit package
     * @param inNodes the in nodes
     * @param outNodes the out nodes
     */
    private static void initTool(ToolImp tool, String unitName, String unitPackage, int inNodes, int outNodes) {
        tool.setToolName(unitName);
        try {
            tool.setDataInputNodeCount(inNodes);
            tool.setDataOutputNodeCount(outNodes);
            tool.setToolPackage(unitPackage);
            tool.setProxy(new JavaProxy(unitName, unitPackage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the file unit.
     *
     * @param sendnode the sendnode
     * @param recnode the recnode
     * @param clone the clone
     * @throws CableException the cable exception
     */
    private void addFileUnit(Node sendnode, Node recnode, TaskGraph clone) throws CableException {
        try {
            Task fileTask = new TaskImp(
                    AddonUtils.makeTool(fileUnitClass, "Interim_" + fileIterator, clone.getProperties()),
                    new TaskFactoryImp(),
                    false
            );

            Task task = clone.createTask(fileTask, false);

            if (sendnode != null) {
                Node inNode = task.addDataInputNode();
                clone.connect(sendnode, inNode);
            }

            if (recnode != null) {
                Node outNode = task.addDataOutputNode();
                clone.connect(outNode, recnode);
            }

            TPoint taskPoint = null;
            if(sendnode != null){
                //Place interim file task after producing executable task
                TPoint sendingpoint = TaskLayoutUtils.getPosition(sendnode.getTask());
//                taskPoint = new TPoint(sendingpoint.getX() + 1.5, sendingpoint.getY());

                int outputNodeCount = sendnode.getTask().getOutputNodeCount();
                int thisNodeNumber = sendnode.getTask().getAbsoluteNodeIndex(sendnode);

                double y = thisNodeNumber - (outputNodeCount/2);
                taskPoint = new TPoint(sendingpoint.getX() + 1.5, sendingpoint.getY() + y);

            } else if( recnode != null) {
                // If no producing task (ie its an input file) place it before the consuming task.
                //Put it either in front of it, or above if there's no space
                TPoint recpoint = TaskLayoutUtils.getPosition(recnode.getTask());
                if(recpoint.getX() - 1.5 > 0){
                    taskPoint = new TPoint(recpoint.getX() - 1.5, recpoint.getY());
                } else {
                    taskPoint = new TPoint(recpoint.getX(), recpoint.getY() - 1);
                }
            }
            if(taskPoint != null){

                TaskGraphPanel panel = GUIEnv.getApplicationFrame().getSelectedDesktopView().getTaskgraphPanel();
                TaskLayoutUtils.setPosition(task, taskPoint);

//                moveTask(panel, task, taskPoint);
            }

            fileIterator++;
        } catch (TaskException e) {
            e.printStackTrace();
        } catch (ProxyInstantiationException e) {
            e.printStackTrace();
        }
    }

//    private void moveTask(TaskGraphPanel panel, Task task, TPoint taskPoint){
//        Rectangle taskBounds = panel.getTaskComponent(task).getComponent().getBounds();
//        for(TaskComponent overlap : panel.getTaskComponents()) {
//            Rectangle overlapRect = overlap.getComponent().getBounds();
//            if(taskBounds.intersects(overlapRect)){
//                taskPoint.setY(taskPoint.getY() + 1);
//                TaskLayoutUtils.setPosition(task, taskPoint);
//                moveTask(panel, task, taskPoint);
//            }
//        }
//    }

    /**
 * Inits the dax task.
 *
 * @param task the task
 * @param clazz the clazz
 * @return the task
 */
private Task initDaxTask(Task task, Class clazz) {
        Tool tool = null;
        try {
            tool = AddonUtils.makeTool(clazz, task.getToolName(), task.getProperties());
        } catch (ProxyInstantiationException e) {
            e.printStackTrace();
        } catch (TaskException e) {
            e.printStackTrace();
        }
        try {
            Task daxTask = new TaskImp(tool, new TaskFactoryImp(), false);
            setParameters(task, daxTask);
            return daxTask;
        } catch (TaskException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the parameters.
     *
     * @param task the task
     * @param daxTask the dax task
     */
    private void setParameters(Task task, Task daxTask) {
        int count = 0;
        while (count < task.getInputNodeCount()) {
            try {
                daxTask.addDataInputNode();
            } catch (NodeException e) {
                e.printStackTrace();
            }
            count++;
        }
        count = 0;

        while (count < task.getOutputNodeCount()) {
            try {
                daxTask.addDataOutputNode();
            } catch (NodeException e) {
                e.printStackTrace();
            }
            count++;
        }

        daxTask.setParameter(JobUnit.TRIANA_TOOL, task.getQualifiedToolName());
        daxTask.setParameter("jobName", ((JavaProxy) task.getProxy()).getFullUnitName());

        for (String paramName : task.getParameterNames()) {
            Object value = task.getParameter(paramName);
            daxTask.setParameter(paramName, value);
        }
        //Todo - probably something technical and frustrating...
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getServiceName()
     */
    @Override
    public String getServiceName() {
        return "Triana Dax Template";
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getLongOption()
     */
    @Override
    public String getLongOption() {
        return "taskgraph-to-daxJobs";
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getShortOption()
     */
    @Override
    public String getShortOption() {
        return "dax";
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.CLIaddon#getDescription()
     */
    @Override
    public String getDescription() {
        return "An intermediary stage between a taskgraph and a dax, where units must input/output files.";
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#toolToWorkflow(org.trianacode.taskgraph.tool.Tool)
     */
    @Override
    public Object toolToWorkflow(Tool tool) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#workflowToTool(java.lang.Object)
     */
    @Override
    public Tool workflowToTool(Object workflowObject) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#processWorkflow(org.trianacode.taskgraph.tool.Tool)
     */
    @Override
    public Tool processWorkflow(Tool workflow) {
        return convert((TaskGraph) workflow);
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#toolToWorkflowFile(org.trianacode.taskgraph.tool.Tool, java.io.File, java.lang.String)
     */
    @Override
    public File toolToWorkflowFile(Tool tool, File configFile, String filePath) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.trianacode.enactment.addon.ConversionAddon#toolToWorkflowFileInputStream(org.trianacode.taskgraph.tool.Tool)
     */
    @Override
    public InputStream toolToWorkflowFileInputStream(Tool tool) {
        return null;
    }

    /**
     * Gets the taskgraph child node.
     *
     * @param taskGraph the task graph
     * @return the taskgraph child node
     */
    private static Node getTaskgraphChildNode(TaskGraph taskGraph) {
// Find a child task on the taskgraph to attach the daxCreator to, and connect it

        Node childNode = null;
        try {
            Task[] tasks = taskGraph.getTasks(false);
            ArrayList<Task> childTasks = new ArrayList<Task>();
            for (Task task : tasks) {
                if (task.getDataOutputNodeCount() == 0) {
                    childTasks.add(task);
                }
            }
            System.out.println("These are the child tasks of the taskgraph (will use the first discovered): ");
            for (Task task : childTasks) {
                System.out.println(task.getToolName());
            }

            if (childTasks.size() > 0) {
                childNode = childTasks.get(0).addDataOutputNode();
            } else {
                if (taskGraph.getOutputNodeCount() > 0) {
                    childNode = taskGraph.getOutputNode(0).getTopLevelTask().addDataOutputNode();
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to add node to child leaf of taskgraph");
        }
        System.out.println("Child node " + childNode);
        return childNode;
    }
}
