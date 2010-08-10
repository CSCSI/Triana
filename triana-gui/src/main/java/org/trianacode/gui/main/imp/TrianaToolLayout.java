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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import org.trianacode.gui.main.NodeComponent;

/**
 * The layout for a triana tool. This places the input/output nodes and plus/ minus signs in their correct places.
 *
 * @author Ian Wang <<<<<<< TrianaToolLayout.java
 * @version $Revision: 4048 $ >>>>>>> 1.2.4.1
 */

public class TrianaToolLayout implements LayoutManager {

    public static final String MAIN = "main";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";

    public static final String INPUT_NODE = "inputNode";
    public static final String OUTPUT_NODE = "outputNode";

    public static final String ADD_INPUT = "addInput";
    public static final String ADD_OUTPUT = "addOutput";
    public static final String REMOVE_INPUT = "removeInput";
    public static final String REMOVE_OUTPUT = "removeOutput";

    /**
     * the default node space at zoom 1
     */
    public static double DEFAULT_NODE_SPACE = 4;

    /**
     * the default distance above/below nodes relative to the node space
     */
    public static double NODE_BORDER_FACTOR = 1.5;

    /**
     * the default distance above/below the main component relative to the node space
     */
    public static double MAIN_TOP_BORDER_FACTOR = 2.0;

    /**
     * the default distance above/below the main component relative to the node space
     */
    public static double MAIN_SIDE_BORDER_FACTOR = 2.0;

    /**
     * the border size for top/bottom attachments
     */
    public static int ATTACHMENT_BORDER = 1;


    private Component main;
    private ArrayList topcomps = new ArrayList();
    private ArrayList bottomcomps = new ArrayList();

    private ArrayList inputnodes = new ArrayList();
    private ArrayList outputnodes = new ArrayList();

    private Component addinput;
    private Component addoutput;
    private Component removeinput;
    private Component removeoutput;

    private double nodespace = DEFAULT_NODE_SPACE;
    private double nodeborder = nodespace * NODE_BORDER_FACTOR;
    private double maintopborder = nodespace * MAIN_TOP_BORDER_FACTOR;
    private double mainsideborder = nodespace * MAIN_SIDE_BORDER_FACTOR;


    /**
     * Construct a new triana tool layout
     */
    public TrianaToolLayout() {
    }

    /**
     * Construct a new triana tool layout
     */
    public TrianaToolLayout(double nodespace) {
        this.nodespace = nodespace;
        this.nodeborder = nodespace * NODE_BORDER_FACTOR;
        this.maintopborder = nodespace * MAIN_TOP_BORDER_FACTOR;
        this.mainsideborder = nodespace * MAIN_SIDE_BORDER_FACTOR;
    }

    /**
     * Construct a new triana tool layout
     */
    public TrianaToolLayout(double nodespace, double nodeborder, double maintopborder, double mainsideborder) {
        this.nodespace = nodespace;
        this.nodeborder = nodeborder;
        this.maintopborder = maintopborder;
        this.mainsideborder = mainsideborder;
    }


    /**
     * If the layout manager uses a per-component string, adds the component <code>comp</code> to the layout,
     * associating it with the string specified by <code>name</code>.
     *
     * @param name the string to be associated with the component
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
        if (name == null) {
            return;
        }

        removeLayoutComponent(comp);

        if (name.equals(MAIN)) {
            main = comp;
        } else if (name.equals(TOP)) {
            topcomps.add(comp);
        } else if (name.startsWith(TOP)) {
            topcomps.add(Integer.parseInt(name.substring(3)), comp);
        } else if (name.equals(BOTTOM)) {
            bottomcomps.add(comp);
        } else if (name.startsWith(BOTTOM)) {
            bottomcomps.add(Integer.parseInt(name.substring(3)), comp);
        } else if (name.equals(INPUT_NODE)) {
            if (!(comp instanceof NodeComponent)) {
                throw (new RuntimeException("Illegal add: Component not instance of NodeComponent"));
            }

            inputnodes.add(comp);
        } else if (name.equals(OUTPUT_NODE)) {
            if (!(comp instanceof NodeComponent)) {
                throw (new RuntimeException("Illegal add: Component not instance of NodeComponent"));
            }

            outputnodes.add(comp);
        } else if (name.equals(ADD_INPUT)) {
            addinput = comp;
        } else if (name.equals(ADD_OUTPUT)) {
            addoutput = comp;
        } else if (name.equals(REMOVE_INPUT)) {
            removeinput = comp;
        } else if (name.equals(REMOVE_OUTPUT)) {
            removeoutput = comp;
        }
    }

    /**
     * Removes the specified component from the layout.
     *
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp) {
        if (comp == main) {
            main = null;
        }

        topcomps.remove(comp);
        bottomcomps.remove(comp);

        inputnodes.remove(comp);
        outputnodes.remove(comp);

        if (comp == addinput) {
            addinput = null;
        }

        if (comp == addoutput) {
            addoutput = null;
        }

        if (comp == removeinput) {
            removeinput = null;
        }

        if (comp == removeoutput) {
            removeoutput = null;
        }
    }


    /**
     * Calculates the minimum size dimensions for the specified container, given the components it contains.
     *
     * @param parent the component to be laid out
     * @see #preferredLayoutSize
     */
    public Dimension minimumLayoutSize(Container parent) {
        Dimension mainsize = getMinimumMainSize();
        Dimension topsize = getMinimumSize(topcomps);
        Dimension bottomsize = getMinimumSize(bottomcomps);
        Dimension inputsize = getMinimumNodeDimensions(inputnodes);
        Dimension outputsize = getMinimumNodeDimensions(outputnodes);

        int width = (int) Math.max(inputsize.width + mainsize.width + outputsize.width + (mainsideborder * 2),
                Math.max(topsize.width, bottomsize.width) + (ATTACHMENT_BORDER * 2));
        int height = (int) Math.max(mainsize.height + (2 * maintopborder),
                Math.max(inputsize.height + ((inputnodes.size() - 1) * nodespace) + (nodeborder * 2),
                        outputsize.height + ((outputnodes.size() - 1) * nodespace) + (nodeborder * 2))) +
                topsize.height + bottomsize.height + (ATTACHMENT_BORDER * 2);

        return new Dimension(width, height);
    }

    /**
     * @return the minimum height to the main component
     */
    private Dimension getMinimumMainSize() {
        if (main != null) {
            return main.getMinimumSize();
        } else {
            return new Dimension(0, 0);
        }
    }

    /**
     * @return the minimum height to the components in the list
     */
    private Dimension getMinimumSize(List list) {
        Component[] comps = (Component[]) list.toArray(new Component[list.size()]);
        Dimension compsize;
        int width = 0;
        int height = 0;

        for (int count = 0; count < comps.length; count++) {
            compsize = comps[count].getMinimumSize();
            width = Math.max(compsize.width, width);
            height += compsize.height;
        }

        return new Dimension(width, height);
    }

    /**
     * @return the minimum dimensions of the node components contained in the list
     */
    private Dimension getMinimumNodeDimensions(ArrayList list) {
        Component[] comps = (Component[]) list.toArray(new Component[list.size()]);
        Dimension compsize;
        int height = 0;
        int width = 0;

        for (int count = 0; count < comps.length; count++) {
            compsize = comps[count].getMinimumSize();

            height += compsize.height;

            if (compsize.width > width) {
                width = compsize.width;
            }
        }

        return new Dimension(width, height);
    }


    /**
     * Calculates the preferred size dimensions for the specified container, given the components it contains.
     *
     * @param parent the container to be laid out
     * @see #minimumLayoutSize
     */
    public Dimension preferredLayoutSize(Container parent) {
        Dimension mainsize = getPreferredMainSize();
        Dimension topsize = getPreferredSize(topcomps);
        Dimension bottomsize = getPreferredSize(bottomcomps);
        Dimension inputsize = getPreferredNodeDimensions(inputnodes);
        Dimension outputsize = getPreferredNodeDimensions(outputnodes);

        int width = (int) Math.max(inputsize.width + mainsize.width + outputsize.width + (mainsideborder * 2),
                Math.max(topsize.width, bottomsize.width) + (ATTACHMENT_BORDER * 2));
        int height = (int) Math.max(mainsize.height + (2 * maintopborder),
                Math.max(inputsize.height + ((inputnodes.size() - 1) * nodespace) + (nodeborder * 2),
                        outputsize.height + ((outputnodes.size() - 1) * nodespace) + (nodeborder * 2))) +
                topsize.height + bottomsize.height + (ATTACHMENT_BORDER * 2);

        return new Dimension(width, height);
    }

    /**
     * @return the minimum height to the main component
     */
    private Dimension getPreferredMainSize() {
        if (main != null) {
            return main.getPreferredSize();
        } else {
            return new Dimension(0, 0);
        }
    }

    /**
     * @return the preferred size of the components in the list
     */
    private Dimension getPreferredSize(List list) {
        Component[] comps = (Component[]) list.toArray(new Component[list.size()]);
        Dimension compsize;
        int width = 0;
        int height = 0;

        for (int count = 0; count < comps.length; count++) {
            compsize = comps[count].getPreferredSize();
            width = Math.max(compsize.width, width);
            height += compsize.height;
        }

        return new Dimension(width, height);
    }


    /**
     * @return the dimensions of the node components contained in the list
     */
    private Dimension getPreferredNodeDimensions(ArrayList list) {
        Component[] comps = (Component[]) list.toArray(new Component[list.size()]);
        Dimension compsize;
        int height = 0;
        int width = 0;

        for (int count = 0; count < comps.length; count++) {
            compsize = comps[count].getPreferredSize();

            height += compsize.height;

            if (compsize.width > width) {
                width = compsize.width;
            }
        }

        return new Dimension(width, height);
    }

    /**
     * Lays out the specified container.
     *
     * @param parent the container to be laid out
     */
    public void layoutContainer(Container parent) {
        Dimension parentsize = parent.getSize();
        Dimension prefsize = preferredLayoutSize(parent);
        Dimension compsize;

        double wfactor = ((double) parentsize.width) / prefsize.width;
        double hfactor = ((double) parentsize.width) / prefsize.width;

        Component[] comps = (Component[]) topcomps.toArray(new Component[topcomps.size()]);
        int toptop = ATTACHMENT_BORDER;

        for (int count = 0; count < comps.length; count++) {
            compsize = getSize(comps[count], wfactor, hfactor);
            comps[count].setLocation(ATTACHMENT_BORDER, toptop);
            comps[count].setSize(parentsize.width - (ATTACHMENT_BORDER * 2), compsize.height);
            toptop += compsize.height;
        }

        comps = (Component[]) bottomcomps.toArray(new Component[bottomcomps.size()]);
        int bottomtop = parentsize.height - ATTACHMENT_BORDER;

        for (int count = 0; count < comps.length; count++) {
            compsize = getSize(comps[count], wfactor, hfactor);
            comps[count].setLocation(ATTACHMENT_BORDER, bottomtop - compsize.height);
            comps[count].setSize(parentsize.width - (ATTACHMENT_BORDER * 2), compsize.height);
            bottomtop -= compsize.height;
        }

        int nodespace = (int) (this.nodespace * hfactor);
        int nodeleftborder = 0;
        int noderightborder = 0;

        comps = (Component[]) inputnodes.toArray(new Component[inputnodes.size()]);
        sort(comps);

        int nodetop = toptop + ((bottomtop - toptop) / 2) - (getSize(comps, nodespace, wfactor, hfactor).height / 2);

        for (int count = 0; count < comps.length; count++) {
            compsize = getSize(comps[count], wfactor, hfactor);
            comps[count].setLocation(0, nodetop);
            comps[count].setSize(compsize);

            nodetop += compsize.height + nodespace;

            if (compsize.width > nodeleftborder) {
                nodeleftborder = compsize.width;
            }
        }

        comps = (Component[]) outputnodes.toArray(new Component[outputnodes.size()]);
        sort(comps);

        nodetop = toptop + ((bottomtop - toptop) / 2) - (getSize(comps, nodespace, wfactor, hfactor).height / 2);

        for (int count = 0; count < comps.length; count++) {
            compsize = getSize(comps[count], wfactor, hfactor);
            comps[count].setLocation(parentsize.width - compsize.width, nodetop);
            comps[count].setSize(compsize);

            nodetop += compsize.height + nodespace;

            if (compsize.width > noderightborder) {
                noderightborder = compsize.width;
            }
        }

        int wceil = (int) Math.ceil(wfactor) * 2;
        int hceil = (int) Math.ceil(hfactor) * 2;

        if (addinput != null) {
            compsize = getSize(addinput, wfactor, hfactor);
            addinput.setLocation(nodeleftborder + wceil, bottomtop - hceil - compsize.height);
            addinput.setSize(compsize);
        }

        if (addoutput != null) {
            compsize = getSize(addoutput, wfactor, hfactor);
            addoutput.setLocation(parentsize.width - noderightborder - wceil - compsize.width,
                    bottomtop - hceil - compsize.height);
            addoutput.setSize(compsize);
        }

        if (removeinput != null) {
            compsize = getSize(removeinput, wfactor, hfactor);
            removeinput.setLocation(nodeleftborder + wceil, toptop + hceil);
            removeinput.setSize(compsize);
        }

        if (removeoutput != null) {
            compsize = getSize(removeoutput, wfactor, hfactor);
            removeoutput.setLocation(parentsize.width - noderightborder - wceil - compsize.width, toptop + hceil);
            removeoutput.setSize(compsize);
        }

        if (main != null) {
            compsize = getSize(main, wfactor, hfactor);
            main.setLocation(((parentsize.width + nodeleftborder - noderightborder) / 2) - (compsize.width / 2),
                    toptop + ((bottomtop - toptop) / 2) - (compsize.height / 2));
            main.setSize(compsize);
        }
    }


    /**
     * @return the size for the specified componenta taking into account the hfactor and the wfactor
     */
    private Dimension getSize(Component[] comps, double nodespace, double wfactor, double hfactor) {
        Dimension size;
        int width = 0;
        int height = 0;

        for (int count = 0; count < comps.length; count++) {
            size = comps[count].getPreferredSize();
            size = new Dimension((int) Math.ceil(size.width * wfactor), (int) Math.ceil(size.height * hfactor));

            if (size.width > width) {
                width = size.width;
            }

            height += size.height;
        }

        return new Dimension((int) (width), (int) (height + (nodespace * (comps.length - 1))));
    }

    /**
     * @return the size for the specified component taking into account the hfactor and the wfactor
     */
    private Dimension getSize(Component comp, double wfactor, double hfactor) {
        Dimension size = comp.getPreferredSize();
        return new Dimension((int) Math.ceil(size.width * wfactor), (int) Math.ceil(size.height * hfactor));
    }


    /**
     * Sort the specified components according to the node index
     *
     * @param comps
     */
    private void sort(Component[] comps) {
        Component temp;
        int index;

        for (int count = 0; count < comps.length; count++) {
            if ((comps[count] instanceof NodeComponent) && (((NodeComponent) comps[count]).getNode() != null)) {
                index = ((NodeComponent) comps[count]).getNode().getAbsoluteNodeIndex();

                if ((index > -1) && (index != count) && (index < comps.length)) {
                    temp = comps[index];
                    comps[index] = comps[count];
                    comps[count] = temp;
                }
            }
        }
    }

}
