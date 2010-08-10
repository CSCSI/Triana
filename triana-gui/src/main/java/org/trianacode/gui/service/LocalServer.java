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

import java.util.Vector;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.clipin.HistoryClipIn;
import org.trianacode.taskgraph.service.ClientException;
import org.trianacode.taskgraph.service.RunnableInstance;
import org.trianacode.taskgraph.service.Scheduler;
import org.trianacode.taskgraph.service.SchedulerException;
import org.trianacode.taskgraph.service.SchedulerInterface;
import org.trianacode.taskgraph.service.TrianaClient;
import org.trianacode.taskgraph.service.TrianaServer;
import org.trianacode.taskgraph.tool.ToolTable;

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
     * the local tool table
     */
    private ToolTable tools;


    /**
     * Constructs a local server.
     */
    public LocalServer(TaskGraph taskgraph, ToolTable tools) {
        this.taskgraph = taskgraph;
        this.tools = tools;
        this.scheduler = new Scheduler(taskgraph);
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
        Thread thread = new Thread() {
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
                }
                catch (SchedulerException except) {
                    except.printStackTrace();
                }
            }
        };

        thread.setName("RunTaskGraphThread");
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }

    /**
     * Sends a message to the sever to run the taskgraph. The specfied history clip-ins is attached to every input task
     */
    public void run(final HistoryClipIn history) throws ClientException {
        Thread thread = new Thread() {
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
        };

        thread.setName("RunTaskGraphThread");
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
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
        Thread thread = new Thread() {
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
        };

        thread.setName("PauseTaskGraphThread");
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }

    /**
     * Sends a message to the server to reset the taskgraph.
     */
    public void reset() throws ClientException {
        Thread thread = new Thread() {
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
        };

        thread.setName("ResetTaskGraphThread");
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
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
     * @return the a table of the currently loaded tools
     */
    public ToolTable getToolTable() {
        return tools;
    }


    /**
     * Called be a runnable instance to notify that an error has occured
     */
    public void notifyError(RunnableInstance runnable, String message) {
        scheduler.notifyError(runnable);
    }


}
