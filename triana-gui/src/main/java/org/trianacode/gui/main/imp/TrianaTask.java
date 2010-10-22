/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2007 University of Wales, Cardiff. All rights reserved.
 *
 * Redistribution and use of the software in source and binary forms, with
 * or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment: "This product includes
 *    software developed by the University of Wales, Cardiff for the Triana
 *    Project (http://www.trianacode.org)." Alternately, this
 *    acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Triana" and "University of Wales, Cardiff" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact triana@trianacode.org.
 *
 * 5. Products derived from this software may not be called "Triana," nor
 *    may Triana appear in their name, without prior written permission of
 *    the University of Wales, Cardiff.
 *
 * 6. This software may not be sold, used or incorporated into any product
 *    for sale to third parties.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 * NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Triana Project. For more information on the
 * Triana Project, please see. http://www.trianacode.org.
 *
 * This license is based on the BSD license as adopted by the Apache
 * Foundation and is governed by the laws of England and Wales.
 *
 */
package org.trianacode.gui.main.imp;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.hci.color.ColorManager;
import org.trianacode.gui.hci.tools.TaskGraphViewManager;
import org.trianacode.gui.main.TaskComponent;
import org.trianacode.gui.panels.ParameterPanelManager;
import org.trianacode.taskgraph.*;
import org.trianacode.taskgraph.event.*;

import javax.swing.*;
import java.awt.*;


/**
 * TrianaTask is an abstract class which defines a methods which apply to all TrianaTasks. They all have a name, a
 * colour and they are all double clickable. These events associated with TrianaTasks are handled in ToolHandler
 *
 * @author Ian Taylor <<<<<<< TrianaTask.java
 * @version $Revision: 4048 $ >>>>>>> 1.9.2.1
 */

public class TrianaTask extends TrianaTool implements TaskListener, TaskComponent {

    private static Log log = Loggers.PROCESS_LOGGER;


    public static double PROCESS_LED_WIDTH_FACTOR = 0.15;
    public static double PROCESS_LED_HEIGHT_FACTOR = 0.15;


    /**
     * A a flag indicating whether the processing led is shown in the middle of the icon. When it is on a red light is
     * shown on the icon which means that the unit is processing the data. It goes off when the data has been
     * processed.
     */
    protected boolean processled = false;

    /**
     * Counts of the number of start/stop process parameter updates received
     */
    private int executeCount = 0;
    private int requestCount = 0;


    /**
     * Constructs a new TrianaTool for a task
     */
    public TrianaTask(Task task) {
        super(task);
        Component main = null;
        String s = task.getToolName();
        if (task.getSubTitle() != null) {
            String value = task.getSubTitle();
            if (value != null && value.length() > 0) {
                main = new MultiTextSubComponent(s, value, this);
            }
        }
        if (main == null) {
            main = new TextSubComponent(s, this);

        }
        setMainComponent(main);
        initNodes();
        task.addTaskListener(this);
    }


    /**
     * initialises the input/output nodes
     */
    protected void initNodes() {
        Task task = getTaskInterface();
        if (TaskGraphUtils.getConnectedCables(task).length == 0) {
            try {
                int minIn = task.getMinDataInputNodes();
                int diff = minIn - task.getDataInputNodeCount();
                for (int i = 0; i < diff; i++) {
                    task.addDataInputNode();
                }
                int minOut = task.getMinDataOutputNodes();
                diff = minOut - task.getDataOutputNodeCount();
                for (int i = 0; i < diff; i++) {
                    task.addDataOutputNode();
                }
            } catch (NodeException e) {
                log.warn(e);
            }
        }

        Node[] nodes = task.getInputNodes();

        for (int count = 0; count < nodes.length; count++) {
            setNodeComponent(nodes[count], new TrianaNode(nodes[count], true));
        }

        nodes = task.getOutputNodes();

        for (int count = 0; count < nodes.length; count++) {
            setNodeComponent(nodes[count], new TrianaNode(nodes[count], true));
        }
    }


    /**
     * @return the tool tip that appears when the mouse hovers over this tool
     */
    public String getToolTipText() {
        return TaskGraphViewManager.getWorkspaceToolTip(getTaskInterface(), GUIEnv.showExtendedDescriptions());
    }


    /**
     * @return this component
     */
    public Component getComponent() {
        return this;
    }

    /**
     * @return the Task which this triana task represents.
     */
    public Task getTaskInterface() {
        return (Task) getTool();
    }


    /**
     * Paints the tool by placing the correct number of input and output nodes on the icon and putting the tool's name
     * on it.
     */
    public void paintComponent(Graphics graphs) {
        super.paintComponent(graphs);
        paintProcessProgress(graphs);

    }

    protected void paintProcessProgress(Graphics g) {
        if (processled) {
            Color col = ColorManager.getColor(PROGRESS_ELEMENT, getTaskInterface());

            if (col.getAlpha() > 0) {
                Dimension size = getSize();

                int width = (int) (size.width * PROCESS_LED_WIDTH_FACTOR);
                int height = (int) (size.height * PROCESS_LED_HEIGHT_FACTOR);
                int left = (size.width / 2) - (width / 2);
                int top = 0;

                if (getTaskInterface() instanceof TaskGraph) {
                    int processCount = (getStartProcessCount() % 3);
                    left = (size.width / 2) - (width / 2 * 3) + (processCount * width);
                }

                g.setColor(col);
                g.fillRect(left, top, width, height);

                left += width + 1;
                int offset = height / 2;

                for (int count = 0;
                     (count < getStartProcessCount() - getStopProcessCount() - 1) && (left + height < size.width);
                     count++) {
                    g.drawLine(left, top + offset, left + height, top + offset);
                    g.drawLine(left + offset, top, left + offset, top + height);

                    left += height + 1;
                }
            }
        }
    }


    /**
     * Sets the state of the processing LED
     */
    public void setProcessingLED(boolean state) {
        if (state != processled) {
            processled = state;
            repaint();
        }
    }

    /**
     * @return the state of the processing LED
     */
    public boolean getProcessingLED() {
        return processled;
    }

    /**
     * @return the start process count
     */
    public int getStartProcessCount() {
        return requestCount;
    }

    /**
     * @return the stop process count
     */
    public int getStopProcessCount() {
        return executeCount;
    }


    /**
     * Called when the core properties of a task change i.e. its name, whether it is running continuously etc.
     */
    public void taskPropertyUpdate(TaskPropertyEvent event) {
        if (SwingUtilities.isEventDispatchThread()) {
            handleTaskPropertyUpdated(event);
        } else {
            final TaskPropertyEvent evt = event;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    handleTaskPropertyUpdated(evt);
                }
            });
        }
    }


    /**
     * Called when a data input node is added.
     */
    public void nodeAdded(TaskNodeEvent event) {
        if (SwingUtilities.isEventDispatchThread()) {
            handleNodeAdded(event);
        } else {
            final TaskNodeEvent evt = event;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    handleNodeAdded(evt);
                }
            });
        }
    }

    /**
     * Called before a data input node is removed.
     */
    public void nodeRemoved(TaskNodeEvent event) {
        if (SwingUtilities.isEventDispatchThread()) {
            handleNodeRemoved(event);
        } else {
            final TaskNodeEvent evt = event;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    handleNodeRemoved(evt);
                }
            });
        }
    }

    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
        if (SwingUtilities.isEventDispatchThread()) {
            handleParameterUpdated(event);
        } else {
            final ParameterUpdateEvent evt = event;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    handleParameterUpdated(evt);
                }
            });
        }
    }


    /**
     * Handle a task property updated event (must be called in event dispatch thread)
     */
    private void handleTaskPropertyUpdated(final TaskPropertyEvent evt) {
        if ((evt.getTask() == getTaskInterface()) && (evt.getUpdatedProperty() == TaskPropertyEvent.TASK_NAME_UPDATE)) {
            Component comp = getMainComponent();

            if (comp instanceof TextIcon) {
                ((TextIcon) comp).setText(getToolName());
            }

            if (getParent() != null) {
                invalidate();
                getParent().validate();
                getParent().repaint();
            }
        }
        if ((evt.getTask() == getTaskInterface()) && (evt.getUpdatedProperty()
                == TaskPropertyEvent.TASK_SUBNAME_UPDATE)) {
            Component comp = getMainComponent();

            if (comp instanceof MultiTextSubComponent) {
                ((MultiTextSubComponent) comp).updateSubText((String) evt.getNewValue());
            }

            if (getParent() != null) {
                invalidate();
                getParent().validate();
                getParent().repaint();
            }
        }
    }

    /**
     * Handles a node added event (must be called in the event dispatch thread)
     */
    private void handleNodeAdded(TaskNodeEvent event) {
        if (event.getTask() == getTaskInterface()) {
            setNodeComponent(event.getNode(), new TrianaNode(event.getNode(), true));
            invalidateSize();
        }
    }

    /**
     * Handles a node removed event (must be called in event dispatch thread)
     */
    private void handleNodeRemoved(TaskNodeEvent event) {
        if (event.getTask() == getTaskInterface()) {
            removeNodeComponent(event.getNode());
            invalidateSize();
        }
    }

    /**
     * Handle a parameter updated event (must be called in event dispatch thread)
     */
    private void handleParameterUpdated(final ParameterUpdateEvent evt) {
        String paramname = evt.getParameterName();

        if (paramname.equals(Task.EXECUTION_REQUEST_COUNT) || paramname.equals(Task.EXECUTION_COUNT)) {
            if (paramname.equals(Task.EXECUTION_REQUEST_COUNT)) {
                if (Integer.parseInt((String) evt.getNewValue()) == 0) {
                    requestCount = 0;
                } else {
                    requestCount++;
                }
            } else if (paramname.equals(Task.EXECUTION_COUNT)) {
                if (Integer.parseInt((String) evt.getNewValue()) == 0) {
                    executeCount = 0;
                } else {
                    executeCount++;
                }
            }

            setProcessingLED(requestCount > executeCount);
            repaint();
        } else if (paramname.equals(Task.ERROR_MESSAGE)) {
            if ((evt.getNewValue() == null) || (evt.getNewValue().equals(""))) {
                clearError();
            } else {
                setError((String) evt.getNewValue());
            }
        } else if (paramname.equals(Task.GUI_X) || paramname.equals(Task.GUI_Y)) {
            if (getParent() != null) {
                invalidate();
                getParent().validate();
                getParent().repaint();
            }
        } else if (paramname.equals(Task.PARAM_PANEL_SHOW)) {
            ParameterPanelManager.showParameterWindowFor(getTaskInterface(), this);
        } else if (paramname.equals(Task.PARAM_PANEL_HIDE)) {
            ParameterPanelManager.hideParameterWindowFor(getTaskInterface());
        } else {
            processParameterUpdate(evt);
        }
    }

    protected void processParameterUpdate(final ParameterUpdateEvent evt) {

    }


    /**
     * Called before the task is disposed
     */
    public void taskDisposed(TaskDisposedEvent event) {
    }


    /**
     * Disposes of the tool and its associated windows
     */
    public void dispose() {
        getTaskInterface().removeTaskListener(this);

        super.dispose();
    }

}















