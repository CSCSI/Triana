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

package org.trianacode.gui.hci.color;


import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.RenderingHint;
import org.trianacode.taskgraph.tool.Tool;

import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The ColorManager maintains a register of ColorModels for different tool
 * class types, and a default ColorModel which handles tool class types and
 * color elements not handled by a specific ColorModel. Components can query the
 * ColorManager to discover the color value for particular graphical elements
 * based on class of the tool they are visualizing.
 *
 * @author Ian Wang
<<<<<<< ColorManager.java
 * @version $Revision: 4048 $
=======
 * @version $Revision: 4048 $
>>>>>>> 1.6.2.1
 * @created 6th May 2004
<<<<<<< ColorManager.java
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
=======
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
>>>>>>> 1.6.2.1
 make a Singleton
 */

public class ColorManager {

    /**
     * A hashtable of the color models keyed by tool class
     */
    private static Hashtable modeltable = new Hashtable();

    /**
     * A hashtable to the color model for each tool/element, keyed by the
     * tool/element. This table is dynamically populated as colors are requested
     * for tools.
     */
    private static Hashtable modelcache = new Hashtable();

    /**
     * A list of the classes in order of registration
     */
    private static ArrayList classorder = new ArrayList();

    /**
     * The color model used when no color model is registered
     */
    private static ColorModel defaultmodel;


    /**
     * @return the default color model used when no color model is registered for
     *         a tool class.
     */
    public static ColorModel getDefaultColorModel() {
        return defaultmodel;
    }

    /**
     * Sets the default color model used when no color model is registered for
     * a tool class.
     */
    public static void setDefaultColorModel(ColorModel model) {
        defaultmodel = model;
        modelcache.clear();
    }


    /**
     * Registers a color model to handle the color values for a particular
     * tool class. Note that if two models are registered for a tool then
     * the later registered takes precedence.
     */
    public static void registerColorModel(String toolclass, ColorModel model) {
        modeltable.put(toolclass, model);
        classorder.remove(toolclass);
        classorder.add(toolclass);
        modelcache.clear();
    }

    /**
     * Unregisters a color model for a tool class.
     */
    public static void unregisterColorModel(String toolclass) {
        modeltable.remove(toolclass);
        classorder.remove(toolclass);
        modelcache.clear();
    }

    /**
     * @return an array of tool classes for which color models are registered
     */
    public static String[] getRegisteredToolClasses() {
        return (String[]) modeltable.keySet().toArray(new String[modeltable.keySet().size()]);
    }

    /**
     * @return the color model regiseted for the specified tool class (or null
     *         if no model is registered, i.e. the default color model is used)
     */
    public static ColorModel getRegisteredColorModel(String toolclass) {
        if (modeltable.containsKey(toolclass))
            return (ColorModel) modeltable.get(toolclass);
        else
            return null;
    }

    /**
     * @return true if a color model is registered for the specified tool class
     */
    public static boolean isRegisteredColorModel(String toolclass) {
        return modeltable.containsKey(toolclass);
    }


    /**
     * @return an array fo the registered color models (including the default
     *         color model)
     */
    public static ColorModel[] getRegisteredColorModels() {
        String[] classes = getRegisteredToolClasses();
        ArrayList models = new ArrayList();

        models.add(getDefaultColorModel());

        for (int count = 0; count < classes.length; count++)
            if (!models.contains(getRegisteredColorModel(classes[count])))
                models.add(getRegisteredColorModel(classes[count]));

        return (ColorModel[]) models.toArray(new ColorModel[models.size()]);
    }


    /**
     * @return the color for the particular graphical element of the specified
     *         tool. Note that if two models are registered for a tool then
     *         the later registered takes precedence.
     */
    public static Color getColor(String element, Tool tool) {
        ColorModel model;
        ElementKey key = new ElementKey(tool, element);

        if (modelcache.containsKey(key))
            model = (ColorModel) modelcache.get(key);
        else {
            model = getToolColorModel(tool, element);
            modelcache.put(key, model);
        }

        return model.getColor(element, tool);
    }

    /**
     * @return the color for the particular graphical element not associated
     *         withd a specific tool
     */
    public static Color getColor(String element) {
        ColorModel model;
        ElementKey key = new ElementKey(element);

        if (modelcache.containsKey(key))
            model = (ColorModel) modelcache.get(key);
        else {
            model = getElementColorModel(element);
            modelcache.put(key, model);
        }

        return model.getColor(element);
    }

    /**
     * @return the color for the particular node
     */
    public static Color getColor(Node node) {
        if (node.getTask() == null)
            return Color.black;

        ColorModel model = getNodeColorModel(node);

        if (model instanceof NodeColorModel)
            return ((NodeColorModel) model).getColor(node);
        else if (getDefaultColorModel() instanceof NodeColorModel)
            return ((NodeColorModel) getDefaultColorModel()).getColor(node);
        else
            return Color.black;
    }


    /**
     * @return the color model used for the specified tool
     */
    private static ColorModel getToolColorModel(Tool tool, String element) {
        RenderingHint[] hints = tool.getRenderingHints();
        String hint;
        ColorModel tempmodel = null;
        ColorModel model = null;
        int priority = -1;

        for (int count = 0; count < hints.length; count++) {
            hint = hints[count].getRenderingHint();

            if (modeltable.containsKey(hint)) {
                tempmodel = (ColorModel) modeltable.get(hint);

                if (isColorModelForElement(tempmodel, element)) {
                    if ((model == null) || (classorder.indexOf(hint) > priority)) {
                        model = tempmodel;
                        priority = classorder.indexOf(hint);
                    }
                }
            }
        }

        if (model == null)
            model = getElementColorModel(element);

        return model;
    }

    private static ColorModel getElementColorModel(String element) {
        if (isColorModelForElement(getDefaultColorModel(), element))
            return getDefaultColorModel();

        Enumeration enumeration = modeltable.elements();
        ColorModel model = null;
        ColorModel tempmodel = null;

        while (enumeration.hasMoreElements() && (model == null)) {
            tempmodel = (ColorModel) enumeration.nextElement();

            if (isColorModelForElement(tempmodel, element))
                model = tempmodel;
        }

        if (model == null)
            model = getDefaultColorModel();

        return model;
    }

    private static boolean isColorModelForElement(ColorModel model, String element) {
        String[] elems = model.getElementNames();

        for (int ecount = 0; ecount < elems.length; ecount++)
            if (elems[ecount].equals(element))
                return true;

        return false;
    }

    /**
     * @return the color model used for the specified tool
     */
    private static ColorModel getNodeColorModel(Node node) {
        RenderingHint[] hints = node.getTask().getRenderingHints();
        String hint;
        ColorModel model = getDefaultColorModel();
        ColorModel tempmodel = null;
        int priority = -1;

        for (int count = 0; count < hints.length; count++) {
            hint = hints[count].getRenderingHint();

            if (modeltable.containsKey(hints)) {
                tempmodel = (ColorModel) modeltable.get(hints);

                if (tempmodel instanceof NodeColorModel) {
                    if ((model == null) || (classorder.indexOf(hints) > priority)) {
                        model = tempmodel;
                        priority = classorder.indexOf(hints);
                    }
                }
            }
        }

        return model;
    }


    private static class ElementKey {

        private Tool tool;
        private String element;

        public ElementKey(String element) {
            this.element = element;
        }

        public ElementKey(Tool tool, String element) {
            this.tool = tool;
            this.element = element;
        }


        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ElementKey)) return false;

            final ElementKey elementKey = (ElementKey) o;

            if (element != null ? !element.equals(elementKey.element) : elementKey.element != null) return false;
            if (tool != null ? !tool.equals(elementKey.tool) : elementKey.tool != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (tool != null ? tool.hashCode() : 0);
            result = 29 * result + (element != null ? element.hashCode() : 0);
            return result;
        }

    }

    private static class DefaultColorModel implements NodeColorModel {

        /**
         * @return the name of this Color model
         */
        public String getModelName() {
            return "Default Color Model";
        }

        /**
         * @return the color names that this model uses
         */
        public String[] getColorNames() {
            return new String[]{"Default Color"};
        }

        /**
         * @return the element names this color model links with color names
         */
        public String[] getElementNames() {
            return new String[0];
        }


        /**
         * @return the color for the specified graphical element when representing
         *         the specified tool. If the element is unrecognized this method will return
         *         a default color.
         */
        public Color getColor(String element, Tool tool) {
            return Color.black;
        }

        /**
         * @return the color for the specified graphical element not linked to a
         *         specific tool
         */
        public Color getColor(String element) {
            return Color.black;
        }

        /**
         * @return the color for the specified node
         */
        public Color getColor(Node node) {
            return Color.black;
        }
    }

}
