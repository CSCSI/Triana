/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.gui.service;

import org.trianacode.enactment.io.IoConfiguration;
import org.trianacode.enactment.io.IoHandler;
import org.trianacode.enactment.io.NodeMappings;
import org.trianacode.enactment.logging.ExecutionStateLogger;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.clipin.HistoryClipIn;
import org.trianacode.taskgraph.imp.ToolImp;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.proxy.java.JavaProxy;
import org.trianacode.taskgraph.service.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class LocalServer implements TrianaClient, TrianaServer {


    /**
     * the taskgraph this client handles
     */
    private TaskGraph taskgraph;

    /**
     * the scheduler responsible for running this taskgraph
     */
    private SchedulerInterface scheduler;

    /**
     * a list of the processes awaiting execution when the task is fully instantiated
     */
    private Vector processWaiting = new Vector();

    /**
     * Constructs a local server.
     */
    public LocalServer(TaskGraph taskgraph) {
        this.taskgraph = taskgraph;
        this.scheduler = new Scheduler(taskgraph);
        this.scheduler.addExecutionListener(new ExecutionStateLogger());
    }

    public LocalServer(TaskGraph taskgraph, SchedulerInterface scheduler) {
        this.taskgraph = taskgraph;
        this.scheduler = scheduler;
    }


    /**
     * @return the taskgraph handled by this local server
     */
    public TaskGraph getTaskGraph() {
        return taskgraph;
    }


    /**
     * Sends a message to the sever to run the taskgraph.
     */
    public void run() throws ClientException {
        taskgraph.getProperties().getEngine().execute(new Runnable() {
            public void run() {
                int result = WorkflowActionManager.CANCEL;

                try {
                    result = WorkflowActionManager.authorizeWorkflowAction(WorkflowActions.RUN_ACTION, taskgraph,
                            scheduler.getExecutionState());
                } catch (WorkflowException except) {
                    except.printStackTrace();
                }

                try {
                    if (result == WorkflowActionManager.AUTHORIZE) {
                        scheduler.runTaskGraph();
                    } else {
                        handleCancel(result, null);
                    }
                } catch (SchedulerException except) {
                    except.printStackTrace();
                }
            }
        });
    }

    /**
     * Sends a message to the sever to run the taskgraph. The specfied history clip-ins is attached to every input task
     */
    public void run(final HistoryClipIn history) throws ClientException {
        taskgraph.getProperties().getEngine().execute(new Runnable() {
            public void run() {
                int result = WorkflowActionManager.CANCEL;

                try {
                    result = WorkflowActionManager.authorizeWorkflowAction(WorkflowActions.RUN_ACTION, taskgraph,
                            scheduler.getExecutionState());
                } catch (WorkflowException except) {
                    except.printStackTrace();
                }

                try {
                    if (result == WorkflowActionManager.AUTHORIZE) {
                        scheduler.runTaskGraph(history);
                    } else {
                        handleCancel(result, history);
                    }
                } catch (SchedulerException except) {
                    except.printStackTrace();
                }
            }
        });
    }

    @Override
    public void run(final IoConfiguration ioConfiguration) throws ClientException {
        taskgraph.getProperties().getEngine().execute(new Runnable() {
            public void run() {
                ArrayList<Task> dummyTasks = new ArrayList<Task>();
                try {
                    IoHandler handler = new IoHandler();
                    NodeMappings mappings = handler.map(ioConfiguration, taskgraph);

                    if (mappings != null) {
                        Node[] inputNodes = new Node[taskgraph.getInputNodeCount()];
                        for (int i = 0; i < taskgraph.getInputNodes().length; i++) {
                            Node inputNode = taskgraph.getInputNode(i);
                            inputNodes[i] = inputNode.getTopLevelNode();
                        }
                        System.out.println("Data mappings size : " + mappings.getMap().size());
                        Iterator<Integer> it = mappings.iterator();
                        while (it.hasNext()) {
                            Integer integer = it.next();
                            Object val = mappings.getValue(integer);
                            System.out.println("Data : " + val.toString() + " will be sent to input number " + integer);
                            Task varTask = getVarTask(taskgraph, val);
                            Node taskNode = getNodeInScope(inputNodes[integer], varTask.getParent());

                            taskgraph.connect(varTask.getDataOutputNode(0), taskNode);
                            dummyTasks.add(varTask);
                        }
                    } else {
                        System.out.println("Mappings was null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//        run();

                int result = WorkflowActionManager.CANCEL;
                try {
                    result = WorkflowActionManager.authorizeWorkflowAction(WorkflowActions.RUN_ACTION, taskgraph,
                            scheduler.getExecutionState());
                } catch (WorkflowException except) {
                    except.printStackTrace();
                }
                try {
                    if (result == WorkflowActionManager.AUTHORIZE) {
                        scheduler.runTaskGraph();
                    } else {
                        handleCancel(result, null);
                    }
                } catch (SchedulerException except) {
                    except.printStackTrace();
                }


                while (!isFinished(taskgraph)) {
                    synchronized (this) {
                        try {
                            wait(100);
                        } catch (InterruptedException e) {

                        }
                    }
                }

                for (Task dummyTask : dummyTasks) {
                    taskgraph.removeTask(dummyTask);
                }
                try {
                    TaskLayoutUtils.resolveGroupNodes(taskgraph);
                } catch (TaskGraphException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Node getNodeInScope(Node inputNode, TaskGraph taskGraph) {
        Node scopeNode = inputNode.getTopLevelNode();
        while (scopeNode.getTask().getParent() != taskGraph && scopeNode != null) {
            scopeNode = scopeNode.getChildNode();
        }
        return scopeNode;
    }

    private Task getVarTask(TaskGraph taskgraph, Object variable) throws TaskException, ProxyInstantiationException {
        ToolImp varTool = new ToolImp(taskgraph.getProperties());
        varTool.setDataOutputNodeCount(1);
        varTool.setToolPackage(VariableDummyUnit.class.getPackage().getName());
        varTool.setProxy(new JavaProxy(VariableDummyUnit.class.getSimpleName(), VariableDummyUnit.class.getPackage().getName()));
        varTool.setParameter("variable", variable);
        varTool.setToolName("DummyTool");
        return taskgraph.createTask(varTool);
    }

    private boolean isFinished(TaskGraph taskgraph) {
        Task[] tasks = taskgraph.getTasks(true);
        boolean finished = true;

        for (int count = 0; (count < tasks.length) && (finished); count++) {
            if (tasks[count] instanceof RunnableInstance) {
                finished = finished && ((tasks[count]).getExecutionState()
                        == ExecutionState.COMPLETE);
            }

            if (tasks[count] instanceof TaskGraph) {
                finished = finished && isFinished((TaskGraph) tasks[count]);
            }
        }

        return finished;
    }

    public SchedulerInterface getSchedulerInterface() {
        return scheduler;
    }

    /**
     * Sends a message to the sever to run the specified task within a running taskgraph
     */
    public void runTask(Task task) throws SchedulerException {
        scheduler.runTask(task);
    }


    /**
     * Sends a message to the server to stop running the taskgraph.
     */
    public void pause() throws ClientException {
        taskgraph.getProperties().getEngine().execute(new Runnable() {
            public void run() {
                int result = WorkflowActionManager.CANCEL;

                try {
                    result = WorkflowActionManager.authorizeWorkflowAction(WorkflowActions.PAUSE_ACTION, taskgraph,
                            scheduler.getExecutionState());
                } catch (WorkflowException except) {
                    except.printStackTrace();
                }

                try {
                    if (result == WorkflowActionManager.AUTHORIZE) {
                        scheduler.pauseTaskGraph();
                    } else {
                        handleCancel(result, null);
                    }
                } catch (SchedulerException except) {
                    except.printStackTrace();
                }
            }
        });
    }

    /**
     * Sends a message to the server to reset the taskgraph.
     */
    public void reset() throws ClientException {
        taskgraph.getProperties().getEngine().execute(new Runnable() {
            public void run() {
                int result = WorkflowActionManager.CANCEL;

                try {
                    result = WorkflowActionManager.authorizeWorkflowAction(WorkflowActions.RESET_ACTION, taskgraph,
                            scheduler.getExecutionState());
                } catch (WorkflowException except) {
                    except.printStackTrace();
                }

                try {
                    if (result == WorkflowActionManager.AUTHORIZE) {
                        scheduler.resetTaskGraph();
                    } else {
                        handleCancel(result, null);
                    }
                } catch (SchedulerException except) {
                    except.printStackTrace();
                }
            }
        });
    }

    /**
     * Sends a message to the server to flush the taskgraph
     */
    public void flush() throws ClientException {
        try {
            scheduler.flushTaskGraph();
        } catch (SchedulerException except) {
            throw (new ClientException(except));
        }
    }


    /**
     * Handles the various cancel results from the WorkflowActionManager
     */
    private void handleCancel(int result, HistoryClipIn history) throws SchedulerException {
        if (result == WorkflowActionManager.CANCEL) {
            return;
        } else if (result == WorkflowActionManager.RESET) {
            handleResetAfterCancel();
        } else if (result == WorkflowActionManager.RESET_AND_RUN) {
            handleResetRunAfterCancel(history);
        }
    }

    private void handleResetAfterCancel() throws SchedulerException {
        try {
            int newresult = WorkflowActionManager
                    .authorizeWorkflowAction(WorkflowActions.RESET_ACTION, taskgraph, scheduler.getExecutionState());

            if (newresult == WorkflowActionManager.AUTHORIZE) {
                scheduler.resetTaskGraph();
            }
        } catch (WorkflowException except) {

        }
    }

    private void handleResetRunAfterCancel(HistoryClipIn history) throws SchedulerException {
        int newresult = WorkflowActionManager.CANCEL;

        try {
            newresult = WorkflowActionManager
                    .authorizeWorkflowAction(WorkflowActions.RESET_ACTION, taskgraph, scheduler.getExecutionState());
        } catch (WorkflowException except) {
            except.printStackTrace();
        }

        try {
            if (newresult == WorkflowActionManager.AUTHORIZE) {
                scheduler.resetTaskGraph();

                newresult = WorkflowActionManager
                        .authorizeWorkflowAction(WorkflowActions.RUN_ACTION, taskgraph, scheduler.getExecutionState());

                if (newresult == WorkflowActionManager.AUTHORIZE) {
                    if (history != null) {
                        scheduler.runTaskGraph(history);
                    } else {
                        scheduler.runTaskGraph();
                    }
                }
            }
        } catch (WorkflowException except) {
            except.printStackTrace();
        }
    }


    /**
     * Disposes of the server
     */
    public void dispose() throws ClientException {
        try {
            scheduler.pauseTaskGraph();
            scheduler.flushTaskGraph();
            taskgraph = null;
            processWaiting.setSize(0);
        } catch (SchedulerException except) {
            throw (new ClientException(except));
        }
    }


    /**
     * Called be a runnable instance to notify that an error has occured
     */
    public void notifyError(RunnableInstance runnable, String message) {
        scheduler.notifyError(runnable, message);
    }


}
