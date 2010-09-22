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

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.hci.color.ColorManager;
import org.trianacode.gui.hci.color.TrianaColorConstants;
import org.trianacode.gui.main.NodeComponent;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.constants.StripeToolConstants;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.util.Env;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

/**
 * The graphical representation of a tool.
 *
 * @author Ian Wang <<<<<<< TrianaTool.java
 * @version $Revision: 4048 $ >>>>>>> 1.5.2.1
 */

public abstract class TrianaTool extends JPanel implements TrianaColorConstants {


    /**
     * the width of the graphical component in terms of the number of letters displayed by default
     */
    public static int DEFAULT_NAME_LENGTH = 10;

    public static int THICK_STRIPE_WIDTH = 15;
    public static int THICK_STRIPE_OFFSET = 30;
    public static int THIN_STRIPE_WIDTH = 2;
    public static int THIN_STRIPE_OFFSET = 10;
    public static int STRIPE_ANGLE_OFFSET = -30;

    public static final String SUB_NAME = "trianatool-sub-name";
    public static final String SUB_NAME_VALUE = "trianatool-sub-name-value";


    /**
     * A reference to the task this tool is representing
     */
    private Tool tool;

    /**
     * the text component
     */
    private Component mainicon;

    /**
     * An ArrayList storing the Triana input NodeCable objects
     */
    private Hashtable nodes = new Hashtable();

    /**
     * a flag indicating whether this triana tool is selected
     */
    private boolean selected = false;

    /**
     * an error message that is set when the tool enters an error state (null indicates no error)
     */
    private String errorstate = null;


    public TrianaTool(Tool tool) {
        this(tool, new TrianaToolLayout());
    }

    public TrianaTool(Tool tool, LayoutManager layout) {
        super();

        this.tool = tool;
        setLayout(layout);
    }


    public void addNotify() {
        ToolTipManager.sharedInstance().registerComponent(this);
        super.addNotify();
    }

    public void removeNotify() {
        ToolTipManager.sharedInstance().unregisterComponent(this);
        super.removeNotify();
    }


    /**
     * @return the Task which this triana task represents.
     */
    public Tool getTool() {
        return tool;
    }


    /**
     * Initialises the main icon
     */
    protected void setMainComponent(Component comp) {
        mainicon = comp;
        add(mainicon, TrianaToolLayout.MAIN);
    }

    /**
     * Initialises the main icon
     */
    protected Component getMainComponent() {
        return mainicon;
    }


    /**
     * Inserts an input node component at the specified index
     */
    protected void setNodeComponent(Node node, NodeComponent comp) {
        nodes.put(node, comp);

        if (node.isInputNode()) {
            add(comp.getComponent(), TrianaToolLayout.INPUT_NODE);
        } else {
            add(comp.getComponent(), TrianaToolLayout.OUTPUT_NODE);
        }
    }

    /**
     * Removes the input node at the specified index
     */
    protected void removeNodeComponent(Node node) {
        Component comp = (Component) nodes.remove(node);

        if (comp != null) {
            remove(comp);
        }
    }

    /**
     * @return the input node component at the specified index
     */
    public NodeComponent getNodeComponent(Node node) {
        if (nodes.containsKey(node)) {
            return (NodeComponent) nodes.get(node);
        } else {
            return null;
        }
    }


    /**
     * Gets the name of the task
     */
    public String getToolName() {
        return tool.getToolName();
    }


    /**
     * Sets the tool into an error state with the specified message
     */
    public void setError(String message) {
        if (!message.equals(errorstate)) {
            errorstate = message;

            repaint();

            JOptionPane.showMessageDialog(this, errorstate, getToolName() + " " + Env.getString("Error"),
                    JOptionPane.ERROR_MESSAGE,
                    GUIEnv.getTrianaIcon());
        }
    }

    /**
     * Clears the current error state
     */
    public void clearError() {
        errorstate = null;
        repaint();
    }

    /**
     * @return true if the tool is currently in an error state
     */
    public boolean isErrorState() {
        return errorstate != null;
    }

    /**
     * @return the error message for the error state (null if not in error state)
     */
    public String getErrorMessage() {
        return errorstate;
    }


    /**
     * Notifies that the size of the tool should be changed when the tool is repainted
     */
    public void invalidateSize() {
        invalidate();

        if (getParent() != null) {
            getParent().validate();
            getParent().repaint();
        }
    }


    /**
     * @return the unselected color for this tool (mainTrianaNormal by default). Should be overridden to change to tool
     *         color.
     */
    public Color getToolColor() {
        return ColorManager.getColor(TOOL_ELEMENT, tool);
    }

    /**
     * @return the color of the stripe on the tool, or the standard tool color if no stipes
     */
    public Color getStripeColor() {
        if (tool.isRenderingHint(StripeToolConstants.STRIPE_TOOL_RENDERING_HINT)) {
            return ColorManager.getColor(STRIPE_ELEMENT, tool);
        } else {
            return null;
        }
    }

    /**
     * @return the color of the stripe on the tool, or the standard tool color if no stipes
     */
    public int getStripeWidth() {
        return THICK_STRIPE_WIDTH;
    }

    /**
     * @return the color of the stripe on the tool, or the standard tool color if no stipes
     */
    public int getStripeOffset() {
        return THICK_STRIPE_OFFSET;
    }


    /**
     * Sets the triana tool as selected (i.e. it appears depressed)
     */
    public void setSelected(boolean state) {
        selected = state;
        repaint();
    }

    /**
     * Returns true if the particular tool is selected
     */
    public boolean isSelected() {
        return selected;
    }


    /**
     * Paints the Triana Tool by rendering its name in the middle of the icon. Note that the size of the icon is not
     * determined until this class is extended to produce a ToolImp, ToolBox or a TrianaTool.
     */
    public void paintComponent(Graphics graphs) {
        drawRectangle(graphs, getToolColor(), graphs.getColor());
        drawStripes(graphs, getStripeColor(), getStripeWidth(), getStripeOffset());
    }

    /**
     * Paints the main tool rectangle
     */
    protected void drawRectangle(Graphics graphs, Color color, Color orig) {
        graphs.setColor(color);
        graphs.fill3DRect(0, 0, getSize().width, getSize().height, !isSelected());
        graphs.setColor(orig);
    }

    /**
     * Paints stripes on the main tool rectangle of a certain color, width and slant (offset)
     */
    protected void drawStripes(Graphics graphs, Color col, int width, int offset) {
        if ((width > 0) && (offset != 0) && (col != null) && (!col.equals(getToolColor()))) {
            if (isSelected()) {
                graphs.setColor(col.darker());
            } else {
                graphs.setColor(col);
            }

            Dimension size = getSize();
            int across = Math.min(0, -Math.abs(TrianaTool.STRIPE_ANGLE_OFFSET));
            int[] ypoints = new int[]{1, 1, size.height - 1, size.height - 1};
            int[] xpoints;

            while ((across < size.width) || (across + TrianaTool.STRIPE_ANGLE_OFFSET < size.width)) {
                xpoints = new int[]{across, across + width, across + TrianaTool.STRIPE_ANGLE_OFFSET + width,
                        across + TrianaTool.STRIPE_ANGLE_OFFSET};

                graphs.fillPolygon(xpoints, ypoints, 4);

                across += Math.abs(offset);
            }
        }
    }


    /**
     * Disposes of the tool and its associated windows
     */
    public void dispose() {
        tool = null;
        nodes.clear();
    }

}
