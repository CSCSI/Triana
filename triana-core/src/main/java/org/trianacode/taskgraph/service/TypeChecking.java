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
package org.trianacode.taskgraph.service;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.tool.ClassLoaders;
import org.trianacode.taskgraph.tool.Tool;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * <p>TypeChecking includes several functions which check the validity of connections <i>e.g.</i> functions that check
 * if an object is a TrianaType or not and others which check if two units are compatible.</p>
 *
 * @author Ian Taylor, Bernard Schutz
 * @version $Revision: 4048 $
 */
public final class TypeChecking {

    private static Log log = Loggers.EXECUTION_LOGGER;


    public static final String DOUBLE_ARRAY = "double[]";
    public static final String FLOAT_ARRAY = "float[]";
    public static final String LONG_ARRAY = "long[]";
    public static final String INT_ARRAY = "int[]";
    public static final String SHORT_ARRAY = "short[]";
    public static final String BYTE_ARRAY = "byte[]";

    /**
     * Returns <i>true</i> if <b>any</b> of the specified types output is compatibale with <b>any</b> of the specified
     * input types. This is used to check whether a connection between two units can be valid
     *
     * @param outTypes the types sent out from the output unit.
     * @param inTypes  the types allowed to be input to the input unit.
     * @return boolean <i>True</i> if any of the output types are consistent with any of the input types
     */
    public static boolean isCompatibility(Class[] outTypes, Class[] inTypes) {

//        log.info("TypeChecking.isCompatibility ENTER MATCH ");
        for (Class outType : outTypes) {
//            log.info("TypeChecking.isCompatibility OUTTYPE:" + outType);
        }
        for (Class inType : inTypes) {
//            log.info("TypeChecking.isCompatibility INTYPE:" + inType);
        }
        boolean match = false;

        for (int outcount = 0; (!match) && (outcount < outTypes.length); ++outcount) {
            for (int incount = 0; (!match) && (incount < inTypes.length); ++incount) {
//                log.info("TypeChecking.isCompatibility OUT:" + outTypes[outcount]);
//                log.info("TypeChecking.isCompatibility IN:" + inTypes[incount]);
                match = match || (outTypes[outcount].isAssignableFrom(inTypes[incount])) ||
                        (inTypes[incount].isAssignableFrom(outTypes[outcount]));
            }
        }

        return match;
    }

    /**
     * Returns <i>true</i> if the specified object is one of the allowed input types.
     *
     * @param sentObject the actual data object being sent.
     * @param inTypes    the types allowed to be input to the input unit.
     * @return boolean <i>True</i> if the data being sent is an allowed input type
     */
    public static boolean isCompatible(Object sentObject, Class[] inTypes) {
        if (sentObject == null) {
            return false;
        }

        Class sentType = sentObject.getClass();

        for (int count = 0; count < inTypes.length; ++count) {
//            log.info("TypeChecking.isCompatible in:" + inTypes[count] + " out:" + sentType);
            if (inTypes[count].isAssignableFrom(sentType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns <i>true</i> if the specified nodes can be legitimately be connected. This occurs when there is type
     * compatibility between the tasks, or one/both of the nodes are parameter nodes.
     */
    public static boolean isCompatibility(Node sendnode, Node recnode) {
        if (sendnode.getTopLevelNode().isParameterNode()) {
            return true;
        }

        if (recnode.getTopLevelNode().isParameterNode()) {
            return true;
        }

        Task sendtask = sendnode.getTask();
        Task rectask = recnode.getTask();

        String[] outtypes = sendtask.getDataOutputTypes(sendnode.getNodeIndex());
        String[] intypes = rectask.getDataInputTypes(recnode.getNodeIndex());

        if (outtypes == null) {
            outtypes = sendtask.getDataOutputTypes();
        }

        if (intypes == null) {
            intypes = rectask.getDataInputTypes();
        }

        try {
            boolean b = (isCompatibility(classForTrianaType(outtypes), classForTrianaType(intypes)));
            return b;
        } catch (NoClassDefFoundError except) {
            return false;
        }
    }


    public static double[] floatArrayToDouble(float[] from) {
        double[] to = new double[from.length];
        for (int i = 0; i < from.length; i++) {
            to[i] = (double) from[i];
        }
        return to;
    }

    /**
     * This function attempts to return the class representation for the the given name of the Triana type. If prepends
     * triana.types to start of the name and looks for the class representation.
     */
    public static Class classForTrianaType(String name) {
        if (name.equals(Tool.UNKNOWN_DATA_TYPE) || name.equals(Tool.ANY_DATA_TYPE)) {
            try {
                return ClassLoaders.forName("java.lang.Object");
            } catch (ClassNotFoundException except) {
                throw (new RuntimeException(except.getMessage()));
            }
        }

        boolean array = false;

        if (name.equals(DOUBLE_ARRAY)) {
            return new double[0].getClass();
        } else if (name.equals(FLOAT_ARRAY)) {
            return new float[0].getClass();
        } else if (name.equals(LONG_ARRAY)) {
            return new long[0].getClass();
        } else if (name.equals(INT_ARRAY)) {
            return new int[0].getClass();
        } else if (name.equals(SHORT_ARRAY)) {
            return new short[0].getClass();
        } else if (name.equals(BYTE_ARRAY)) {
            return new byte[0].getClass();
        } else if (name.endsWith("[]")) {
            array = true;
            name = name.substring(0, name.indexOf("[]"));
        }
        Class javaType = null;
        try {      // look for the full type Type next
            javaType = ClassLoaders.forName(name);
        } catch (ClassNotFoundException cnf) {
            try {
                javaType = ClassLoaders.forName("triana.types." + name);
            } catch (ClassNotFoundException e) {
                log.warn("Type Error: " + name + " class not found!");
                return null;
            }
        }

        if (array) {
            return Array.newInstance(javaType, 0).getClass();
        } else {
            return javaType;
        }
    }

    /**
     * Attempts to convert the array of objects (Strings actually) to TrianaTypes
     */
    public static Class[] classForTrianaType(String[] types) {

        ArrayList typeClasses = new ArrayList();
        for (int i = 0; i < types.length; i++) {
            Class type = classForTrianaType(types[i]);
            if (type != null) {
                typeClasses.add(type);
            }
        }
        return (Class[]) typeClasses.toArray(new Class[typeClasses.size()]);
    }

}















