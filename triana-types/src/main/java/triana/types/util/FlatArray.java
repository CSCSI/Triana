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
package triana.types.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * <i>FlatArray</i> holds the contents of a multi-dimensional array that has been flattened, i.e. whose elements have
 * been mapped onto a one-dimensional array. It contains the one-dimensional array, an integer array holding the lengths
 * of the original array dimensions, and the name of the type of the component of the flattened array. It provides a
 * method to reconstruct the original array from the flattened array and lengths. Arrays can have elements of any type.
 * </p><p> Arrays can be partially flattened, so that the highest few dimensions are mapped onto a single dimension but
 * the remaining dimensions are kept as array elements of flattened array. </p><p> <i>FlatArray</i> also contains a
 * large number of class methods as utilities for the manipulation of arrays using the flattening technique. These are
 * extensively used in <i>GraphType</i>. </p><p>
 *
 * @author Bernard Schutz
 * @version $Revision: 4048 $
 * @see triana.types.GraphType
 */
public class FlatArray extends Object implements Serializable {

    /*
     * Remembers the input object that is to be flattened
     */
    private Object inputObject = null;

    /*
     * Holds the flattened array.
     */
    private Object flatArray;

    /*
     * The lengths of the dimensions of the original array
     */
    private int[] lengths;

    /**
     * The total length of the flattened array, equal to the total number of elements of the original array.
     */
    private int totalLength = 0;

    /*
     * The name of the Type of the components of the flattened array
     */
    private String componentName;


    /**
     * Constructs an empty <i>FlatArray</i>.
     */
    public FlatArray() {
    }

    /*
     * Constructors.
     */

    /**
     * Constructs a <i>FlatArray</i> from the given array. All dimensions higher than one are flattened. If the input
     * object is a 1-dimensional array then it is not processed, and array <i>lengths</i> has only one element. If the
     * input object is not an array, then <i>FlatArray</i> creates a 1-dimensional array with the object as the single
     * element and sets the single element of <i>lengths</i> to 1.
     *
     * @param o The array to be fully flattened
     */
    public FlatArray(Object o) {
        inputObject = o;
        Class componentClass;
        if (o.getClass().isArray()) {
            lengths = findArrayDimensionLengths(o);
            int totalDims = lengths.length;
            // System.out.println("Number of array dims = " + String.valueOf(totalDims) );
            if (totalDims == 1) {
                flatArray = o;
                componentName = o.getClass().getComponentType().getName();
                totalLength = lengths[0];
                ;
            } else {
                int totalNumbers = lengths[0];
                for (int i = 1; i < totalDims; i++) {
                    totalNumbers *= lengths[i];
                }
                totalLength = totalNumbers;
                componentName = findArrayComponentName(o, totalDims - 1);
                // System.out.println("Component name = " + componentName );
                try {
                    if (componentName.equals("boolean")) {
                        componentClass = Boolean.TYPE;
                    } else if (componentName.equals("char")) {
                        componentClass = Character.TYPE;
                    } else if (componentName.equals("byte")) {
                        componentClass = Byte.TYPE;
                    } else if (componentName.equals("short")) {
                        componentClass = Short.TYPE;
                    } else if (componentName.equals("int")) {
                        componentClass = Integer.TYPE;
                    } else if (componentName.equals("long")) {
                        componentClass = Long.TYPE;
                    } else if (componentName.equals("float")) {
                        componentClass = Float.TYPE;
                    } else if (componentName.equals("double")) {
                        componentClass = Double.TYPE;
                    } else if (componentName.equals("void")) {
                        componentClass = Void.TYPE;
                    } else {
                        componentClass = Class.forName(componentName);
                    }
                    flatArray = Array.newInstance(componentClass, totalNumbers);
                    recurseArrayUnpack(o, flatArray, lengths, 0, totalDims - 1, 0);
                }
                catch (ClassNotFoundException ex) {
                    System.out.println(
                            "Class with name " + componentName + " does not exist. Construct an empty FlatArray.");
                }
            }
        } else {
            flatArray = new Object[1];
            Array.set(flatArray, 0, o);
            lengths = new int[1];
            lengths[0] = 1;
        }
    }

    /**
     * Constructs a <i>FlatArray</i> from the given array by flattening all the dimensions up to argument <i>depth</i>.
     * The array <i>lengths</i> contains the lengths of only the dimensions to be flattened. If the input object is a
     * 1-dimensional array then it is not processed, and <i>lengths</i> has only one element. If the input object has
     * fewer dimensions than depth, then it is fully flattened. If the input object is not an array, then
     * <i>FlatArray</i> creates a 1-dimensional array with the object as the single element and sets the single element
     * of <i>lengths</i> to 1.
     *
     * @param o     The array to be flattened
     * @param depth The last dimension to be flattened, counting from 0
     */
    public FlatArray(Object o, int depth) {
        inputObject = o;
        Class componentClass;
        if (o.getClass().isArray()) {
            lengths = findArrayDimensionLengths(o, depth);
            int totalDims = lengths.length;
            if (totalDims == 1) {
                flatArray = o;
                componentName = o.getClass().getComponentType().getName();
                totalLength = lengths[0];
            } else {
                int totalNumbers = lengths[0];
                for (int i = 1; i < totalDims; i++) {
                    totalNumbers *= lengths[i];
                }
                totalLength = totalNumbers;
                componentName = findArrayComponentName(o, totalDims - 1);
                try {
                    if (componentName.equals("boolean")) {
                        componentClass = Boolean.TYPE;
                    } else if (componentName.equals("char")) {
                        componentClass = Character.TYPE;
                    } else if (componentName.equals("byte")) {
                        componentClass = Byte.TYPE;
                    } else if (componentName.equals("short")) {
                        componentClass = Short.TYPE;
                    } else if (componentName.equals("int")) {
                        componentClass = Integer.TYPE;
                    } else if (componentName.equals("long")) {
                        componentClass = Long.TYPE;
                    } else if (componentName.equals("float")) {
                        componentClass = Float.TYPE;
                    } else if (componentName.equals("double")) {
                        componentClass = Double.TYPE;
                    } else if (componentName.equals("void")) {
                        componentClass = Void.TYPE;
                    } else {
                        componentClass = Class.forName(componentName);
                    }
                    flatArray = Array.newInstance(componentClass, totalNumbers);
                    recurseArrayUnpack(o, flatArray, lengths, 0, totalDims - 1, 0);
                }
                catch (ClassNotFoundException ex) {
                    System.out.println(
                            "Class with name " + componentName + " does not exist. Construct an empty FlatArray.");
                }
            }
        } else {
            flatArray = new Object[1];
            Array.set(flatArray, 0, o);
            lengths = new int[1];
            lengths[0] = 1;
        }
    }

    /**
     * Constructs a <i>FlatArray</i> from the given flattened array and the given array of integers. The user must check
     * that the integer array is consistent with the flattened array, in that the product of its elements equals the
     * length of the array.
     *
     * @param o The flattened array
     * @param l The lengths of the dimensions of the corresponding unflattened array
     */
    public FlatArray(Object o, int[] l) {
        setFlatArray(o);
        setLengths(l);
        setComponentName(o.getClass().getComponentType().getName());
    }

    /*
     * Class methods
     */

    /**
     * Class method finds the number of dimensions of a multi-dimensional array. If the object argument is not an array,
     * the method returns 0.
     *
     * @param o The multi-dimensional array to be examined
     * @return The number of dimensions of the given array
     */
    public static int findNumberOfDimensions(Object o) {
        if (!o.getClass().isArray()) {
            return 0;
        }
        String argName = o.getClass().getName();
        return argName.lastIndexOf("[") - argName.indexOf("[") + 1;
    }


    /**
     * Class method finds the size of each dimension of a multi-dimensional array. It returns an int[] that contains the
     * sizes of the dimensions. The argument can be any Object, but if the argument is not an array, then the method
     * returns <i>null</i>.
     * <p/>
     * Note that the method only looks at the length of the first row in each dimension, so it will not give sensible
     * information unless the multi-dimensional array is "rectangular", ie unless all the rows in a given dimension have
     * the same length.
     *
     * @param o The input (multi-dimensional) array
     * @return The lengths of the dimensions
     */
    public static int[] findArrayDimensionLengths(Object o) {
        int dims = findNumberOfDimensions(o);
        if (dims == 0) {
            return null;
        }
        int[] l = new int[dims];
        Object o1 = o;
        for (int level = 0; level < dims; level++) {
            l[level] = Array.getLength(o1);
            if (level < dims - 1) {
                o1 = ((Object[]) o1)[0];
            }
        }
        return l;
    }

    /**
     * Class method finds the size of each dimension of a multi-dimensional array down to a certain depth. It returns an
     * int[] that contains the sizes of the dimensions. The argument can be any Object, but if the argument is not an
     * array, then the method returns <i>null</i>. If the given depth is actually larger than the number of dimensions
     * of the array, depth is reset to the number of dimensions.
     * <p/>
     * Note that the method only looks at the length of the first row in each dimension, so it will not give sensible
     * information unless the multi-dimensional array is "rectangular", ie unless all the rows in a given dimension have
     * the same length.
     *
     * @param o     The input (multi-dimensional) array
     * @param depth The number of dimensions whose size is desired
     * @return The lengths of the dimensions down to depth
     */
    public static int[] findArrayDimensionLengths(Object o, int depth) {
        int dims = findNumberOfDimensions(o);
        if (dims == 0) {
            return null;
        }
        if (depth > dims) {
            depth = dims;
        }
        int[] l = new int[depth];
        Object o1 = o;
        for (int level = 0; level < depth; level++) {
            l[level] = Array.getLength(o1);
            if (level < depth - 1) {
                o1 = ((Object[]) o1)[0];
            }
        }
        return l;
    }

    /**
     * Class method to find the name of the type of the component of an array down to a certain depth. Since Java
     * multi-dimensional arrays are stored as arrays of arrays, one can ask for the component type at any level in the
     * hierarchy. In Triana data for dependent variables, the data arrays may have more dimensions than the number of
     * independent variables. If so, the extra dimensions are regarded as the elementary components of the data at the
     * points in independent variable space. Thus, a vector field in two dimensions is represented by a two-dimensional
     * array of vectors, and so is stored as a three-dimensional array. The present method allows the user to specify
     * the depth in this hierarchical array in which the class of the remaining levels is to be found. The parameter
     * <i>depth</i> is the number of dimensions that should be stripped away before asking for the component type. Thus,
     * <i>depth</i> = 0 returns the type of the elements of the highest dimension of the array. To discover the
     * component type in the Triana sense, use <i>depth</i> = <i>independentVariables</i> - 1. If the array has fewer
     * dimensions than <i>depth</i> - 1, the method returns null. If the argument is not an array, the method returns
     * its type name.
     *
     * @param o     The data array to be examined
     * @param depth The depth at which one wants the component type
     * @return The component type name
     */
    public static String findArrayComponentName(Object o, int depth) {
        Class argClass = o.getClass();
        String argName = argClass.getName();
        // System.out.println( "FlatArray findArrayComponentName: object name = " + argName );
        if (!argClass.isArray()) {
            return argName;
        }
        int dims = argName.lastIndexOf("[") - argName.indexOf("[") + 1;
        // System.out.println( "FlatArray findArrayComponentName: dims and depth are " + String.valueOf(dims) + " " + String.valueOf(depth) );
        if (depth >= dims) {
            return null;
        }
        try {
            if (depth == dims - 1) {
                // System.out.println( "FlatArray findArrayComponentName: returned name for depth = dims - 1 is " +  Class.forName( argName.substring( depth ) ).getComponentType().getName() );
                return Class.forName(argName.substring(depth)).getComponentType().getName();
            }
        }
        catch (ClassNotFoundException ex) {
            System.out.println("Class with name " + argName.substring(depth)
                    + " does not exist. Cannot find array component name for the given object.");
        }
        // System.out.println( "FlatArray findArrayComponentName: returned name for depth = " + String.valueOf(depth) + " is " + argName.substring(depth + 1 ) );
        return argName.substring(depth + 1);
    }

    /**
     * Class method that creates a new array with the same dimensionality and component types as the given arbitrary
     * multi-dimensional array. Arrays with primitive components are returned with with default initialization; arrays
     * with other object components are returned with null initializations.
     * <p/>
     * The argument can be any Object, but if the argument is not an array, then the method returns <i>null</i>.
     * <p/>
     * Note that the method only looks at the length of the first row of the input array in each dimension, so it will
     * not give a correct imitation of the input array unless the multi-dimensional array is "rectangular", ie unless
     * all the rows in a given dimension have the same length.
     *
     * @param o The input (multi-dimensional) array
     * @return An imitation of the input, <i>i.e.</i> an empty array of the same type
     */
    public static Object multiArrayImitate(Object o) {
        Class oClass = o.getClass();
        if (!oClass.isArray()) {
            return null;
        }
        Class componentClass;
        Object o1;
        int[] ln = findArrayDimensionLengths(o);
        String n = oClass.getName();
        if (n.endsWith("[B")) {
            componentClass = Byte.TYPE;
        } else if (n.endsWith("[D")) {
            componentClass = Double.TYPE;
        } else if (n.endsWith("[F")) {
            componentClass = Float.TYPE;
        } else if (n.endsWith("[I")) {
            componentClass = Integer.TYPE;
        } else if (n.endsWith("[J")) {
            componentClass = Long.TYPE;
        } else if (n.endsWith("[S")) {
            componentClass = Short.TYPE;
        } else if (n.endsWith("[C")) {
            componentClass = Character.TYPE;
        } else if (n.endsWith("[Z")) {
            componentClass = Boolean.TYPE;
        } else {
            int index = n.indexOf("L");
            try {
                componentClass = Class.forName(n.substring(index));
            }
            catch (ClassNotFoundException ex) {
                System.out.println("Could not find class with component name " + n.substring(index)
                        + ". Return null from FlatArray.multiArrayImitate().");
                return null;
            }
        }
        o1 = Array.newInstance(componentClass, ln);
        return o1;
    }

    /**
     * Class method that copies an arbitrary multi-dimensional array to another array of the same dimensions. Copying of
     * arrays that hold primitive data types is by value. If they are reference types, they are copied by reference.
     * <p/>
     * The method uses the private method <i>recurseCopy</i> to move through the array dimensions. The argument can be
     * any Object, but if the argument is not an array, then the method returns <i>null</i>, and if the array holds
     * reference objects at its lowest level instead of primitive data types, then an empty array of the correct size is
     * returned.
     * <p/>
     * Note that the method only looks at the length of the first row in each dimension, so it will not give sensible
     * information unless the multi-dimensional array is "rectangular", ie unless all the rows in a given dimension have
     * the same length.
     *
     * @param o The input (multi-dimensional) array
     * @return A copy of the input by value
     */
    public static Object multiArrayCopy(Object o) {
        Object o1 = multiArrayImitate(o);
        recurseCopy(o, o1);
        return o1;
    }

    /**
     * Private recursive method that copies from an arbitrary multi-dimensional array (first argument) to another of the
     * same dimensionality (second argument).
     *
     * @param o  The array to be copied
     * @param o1 The copy
     */
    private static void recurseCopy(Object o, Object o1) {
        if (o == null) {
            return;
        }
        int j;
        int ln = Array.getLength(o);
        Class oClass = o.getClass();
        Class component = oClass.getComponentType();
        if (component.isArray()) {
            for (j = 0; j < ln; j++) {
                recurseCopy(((Object[]) o)[j], ((Object[]) o1)[j]);
            }
        } else {
            System.arraycopy(o, 0, o1, 0, ln);
        }
        return;
    }


    /**
     * Private recursive method to take apart a multi-dimensional rectangular array of any Objects in order to pack them
     * into a one-dimensional array. The arrays must have compatible component types, and the receiving array must have
     * enough elements. Starting from dimension fromDim (which counts from 0) to dimension toDim, all elements are
     * written to a 1D array. If the elements at dimension toDim are themselves arrays they are written to the new 1D
     * array as references. In this way it is possible to flatten only some of the dimensions of o.
     *
     * @param o             The multi-dimensional array being flattened
     * @param out           The output array receiving the data
     * @param dims          The integer array of dimension lengths of o
     * @param fromDim       The starting dimension for this recursion
     * @param toDim         The last dimension, whose elements are just copied.
     * @param writePosition The current writing position in the the array out
     * @return The new writing position in the array out
     */
    private static int recurseArrayUnpack(Object o, Object out, int[] dims, int fromDim, int toDim, int writePosition) {
        int k;
        int currentLength = dims[fromDim];
        if (fromDim < toDim) {
            for (k = 0; k < currentLength; k++) {
                writePosition = recurseArrayUnpack(((Object[]) o)[k], out, dims, fromDim + 1, toDim, writePosition);
            }
        } else {
            System.arraycopy(o, 0, out, writePosition, currentLength);
            writePosition += currentLength;
        }
        return writePosition;
    }

    /**
     * Private recursive method to reconstruct a multi-dimensional rectangular array of objects from values that have
     * been unpacked into a one-dimensional array.
     *
     * @param o            The multi-dimensional array that receives the data
     * @param in           The 1D array that contains the data
     * @param dims         The array containing the dimension lengths of o
     * @param fromDim      The current dimension in this recursive method
     * @param toDim        The final dimensions of o that will be written to
     * @param readPosition The current reading position in the the array in
     * @return The new reading position in the array in
     */
    private static int recurseArrayPack(Object in, Object o, int[] dims, int fromDim, int toDim, int readPosition) {
        /*
        System.out.println("RecurseArrayPack at level fromDim = " + String.valueOf( fromDim ) + " and toDim = " + String.valueOf( toDim ) );
        System.out.println( "Input multi-array name " + o.getClass().getName() );
        System.out.println( "Input flat array name " + in.getClass().getName() );
        System.out.println( "Flat array length = " + String.valueOf( Array.getLength( in ) ) );
        System.out.println( "On entering, readPosition = " + String.valueOf( readPosition ) );
        */
        int k;
        int currentLength = dims[fromDim];
        // System.out.println("currentLength = " + String.valueOf( currentLength ) );
        Class component;
        if (fromDim < toDim) {
            for (k = 0; k < currentLength; k++) {
                component = o.getClass().getComponentType();
                readPosition = recurseArrayPack(in, ((Object[]) o)[k], dims, fromDim + 1, toDim, readPosition);
                // System.out.println("if-clause readPosition = " + String.valueOf(readPosition) );
            }
        } else {
            System.arraycopy(in, readPosition, o, 0, currentLength);
            readPosition += currentLength;
            // System.out.println("else-clause readPosition = " + String.valueOf(readPosition) );
        }
        return readPosition;
    }

    /**
     * Class method that returns <i>true</i> if the given multi-dimensional array is an array of primitive Java data
     * types at its lowest level. It returns <i>false</i> if the elements of the array at its lowest level are reference
     * types.
     *
     * @param o The array being inspected
     * @return True if the elements at the lowest level are primitive.
     */
    public static boolean isPrimitiveArray(Object o) {
        Class oClass = o.getClass();
        if (!oClass.isArray()) {
            return false;
        }
        String n = oClass.getName();
        boolean a;
        if (n.endsWith("[B")) {
            a = true;
        } else if (n.endsWith("[D")) {
            a = true;
        } else if (n.endsWith("[F")) {
            a = true;
        } else if (n.endsWith("[I")) {
            a = true;
        } else if (n.endsWith("[J")) {
            a = true;
        } else if (n.endsWith("[S")) {
            a = true;
        } else if (n.endsWith("[C")) {
            a = true;
        } else if (n.endsWith("[Z")) {
            a = true;
        } else {
            a = false;
        }
        return a;
    }

    /**
     * Class method that returns <i>true</i> if the given multi-dimensional array contains these primitive Java data
     * types at its lowest level: byte, short, int, long, float, or double. It returns <i>false</i> if the elements of
     * the array at its lowest level are boolean, char, or reference types.
     *
     * @param o The array being tested
     * @return True if the elements at the lowest level are arithmetic primitive types
     */
    public static boolean isArithmeticArray(Object o) {
        Class oClass = o.getClass();
        if (!oClass.isArray()) {
            return false;
        }
        String n = oClass.getName();
        boolean a;
        if (n.endsWith("[B")) {
            a = true;
        } else if (n.endsWith("[D")) {
            a = true;
        } else if (n.endsWith("[F")) {
            a = true;
        } else if (n.endsWith("[I")) {
            a = true;
        } else if (n.endsWith("[J")) {
            a = true;
        } else if (n.endsWith("[S")) {
            a = true;
        } else {
            a = false;
        }
        return a;
    }

    /**
     * Class method that converts a given input array to an array of doubles. The input array can have any
     * dimensionality, but must contain primitive arithmetic types. If its elements are boolean, char, or reference
     * types then the method returns <i>null</i>. If the input object is not an array, the method also returns
     * <i>null</i>. If the input object is an array of doubles, then it is passed directly to output.
     *
     * @param o The input array
     * @return The array converted to an array of doubles
     */
    public static Object toDoubleArray(Object o) {
        if (o == null) {
            return null;
        }
        Class oClass = o.getClass();
        if (!oClass.isArray()) {
            return null;
        }
        String n = oClass.getName();
        if (n.endsWith("[D")) {
            return o;
        }
        Object ra = null;
        double[] r;
        int j, len;
        if (findNumberOfDimensions(o) == 1) {
            len = Array.getLength(o);
            r = new double[len];
            if (n.endsWith("[B")) {
                byte[] in = (byte[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (double) in[j];
                }
                System.out.println("test 1");
            } else if (n.endsWith("[F")) {
                float[] in = (float[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (double) in[j];
                }
                System.out.println("test 2");
            } else if (n.endsWith("[I")) {
                int[] in = (int[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (double) in[j];
                }
                System.out.println("test 3");
            } else if (n.endsWith("[J")) {
                long[] in = (long[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (double) in[j];
                }
                System.out.println("test 4");
            } else if (n.endsWith("[S")) {
                short[] in = (short[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (double) in[j];
                }
                System.out.println("test 5");
            }
            ra = r;
        } else {
            FlatArray flr = new FlatArray(o);
            len = flr.getFlatLength();
            r = new double[len];
            Object ar = flr.getFlatArray();
            if (n.endsWith("[B")) {
                byte[] in = (byte[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (double) in[j];
                }
                System.out.println("test 1b");
            } else if (n.endsWith("[F")) {
                float[] in = (float[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (double) in[j];
                }
                System.out.println("test 2b");
            } else if (n.endsWith("[I")) {
                int[] in = (int[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (double) in[j];
                }
                System.out.println("test 3b");
            } else if (n.endsWith("[J")) {
                long[] in = (long[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (double) in[j];
                }
                System.out.println("test 4b");
            } else if (n.endsWith("[S")) {
                short[] in = (short[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (double) in[j];
                }
                System.out.println("test 5b");
            }
            flr.setFlatArray(r);
            ra = flr.restoreArray(true);
        }
        return r;
    }

    /**
     * Class method that converts a given input array to an array of floats. The input array can have any
     * dimensionality, but must contain primitive arithmetic types. If its elements are boolean, char, or reference
     * types then the method returns <i>null</i>. If the input object is not an array, the method also returns
     * <i>null</i>. If the input object is an array of floats, then it is passed directly to output.
     *
     * @param o The input array
     * @return The array converted to an array of floats
     */
    public static Object toFloatArray(Object o) {
        if (o == null) {
            return null;
        }
        Class oClass = o.getClass();
        if (!oClass.isArray()) {
            return null;
        }
        String n = oClass.getName();
        if (n.endsWith("[F")) {
            return o;
        }
        Object ra = null;
        float[] r;
        int j, len;
        if (findNumberOfDimensions(o) == 1) {
            len = Array.getLength(o);
            r = new float[len];
            if (n.endsWith("[B")) {
                byte[] in = (byte[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (float) in[j];
                }
            } else if (n.endsWith("[D")) {
                double[] in = (double[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (float) in[j];
                }
            } else if (n.endsWith("[I")) {
                int[] in = (int[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (float) in[j];
                }
            } else if (n.endsWith("[J")) {
                long[] in = (long[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (float) in[j];
                }
            } else if (n.endsWith("[S")) {
                short[] in = (short[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (float) in[j];
                }
            }
            ra = r;
        } else {
            FlatArray flr = new FlatArray(o);
            len = flr.getFlatLength();
            r = new float[len];
            Object ar = flr.getFlatArray();
            if (n.endsWith("[B")) {
                byte[] in = (byte[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (float) in[j];
                }
            } else if (n.endsWith("[D")) {
                double[] in = (double[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (float) in[j];
                }
            } else if (n.endsWith("[I")) {
                int[] in = (int[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (float) in[j];
                }
            } else if (n.endsWith("[J")) {
                long[] in = (long[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (float) in[j];
                }
            } else if (n.endsWith("[S")) {
                short[] in = (short[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (float) in[j];
                }
            }
            flr.setFlatArray(r);
            ra = flr.restoreArray(true);
        }
        return r;
    }

    /**
     * Class method that converts a given input array to an array of ints. The input array can have any dimensionality,
     * but must contain primitive arithmetic types. If its elements are boolean, char, or reference types then the
     * method returns <i>null</i>. If the input object is not an array, the method also returns <i>null</i>. If the
     * input object is an array of ints, then it is passed directly to output. Floats and doubles are converted to ints
     * by rounding.
     *
     * @param o The input array
     * @return The array converted to an array of ints
     */
    public static Object toIntArray(Object o) {
        if (o == null) {
            return null;
        }
        Class oClass = o.getClass();
        if (!oClass.isArray()) {
            return null;
        }
        String n = oClass.getName();
        if (n.endsWith("[I")) {
            return o;
        }
        Object ra = null;
        int[] r;
        int j, len;
        if (findNumberOfDimensions(o) == 1) {
            len = Array.getLength(o);
            r = new int[len];
            if (n.endsWith("[B")) {
                byte[] in = (byte[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (int) in[j];
                }
            } else if (n.endsWith("[D")) {
                double[] in = (double[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (int) Math.round(in[j]);
                }
            } else if (n.endsWith("[F")) {
                float[] in = (float[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = Math.round(in[j]);
                }
            } else if (n.endsWith("[J")) {
                long[] in = (long[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (int) in[j];
                }
            } else if (n.endsWith("[S")) {
                short[] in = (short[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (int) in[j];
                }
            }
            ra = r;
        } else {
            FlatArray flr = new FlatArray(o);
            len = flr.getFlatLength();
            r = new int[len];
            Object ar = flr.getFlatArray();
            if (n.endsWith("[B")) {
                byte[] in = (byte[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (int) in[j];
                }
            } else if (n.endsWith("[D")) {
                double[] in = (double[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (int) Math.round(in[j]);
                }
            } else if (n.endsWith("[F")) {
                float[] in = (float[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = Math.round(in[j]);
                }
            } else if (n.endsWith("[J")) {
                long[] in = (long[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (int) in[j];
                }
            } else if (n.endsWith("[S")) {
                short[] in = (short[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (int) in[j];
                }
            }
            flr.setFlatArray(r);
            ra = flr.restoreArray(true);
        }
        return r;
    }

    /**
     * Class method that converts a given input array to an array of longs. The input array can have any dimensionality,
     * but must contain primitive arithmetic types. If its elements are boolean, char, or reference types then the
     * method returns <i>null</i>. If the input object is not an array, the method also returns <i>null</i>. If the
     * input object is an array of longs, then it is passed directly to output. Floats and doubles are converted to
     * longs by rounding.
     *
     * @param o The input array
     * @return The array converted to an array of longs
     */
    public static Object toLongArray(Object o) {
        if (o == null) {
            return null;
        }
        Class oClass = o.getClass();
        if (!oClass.isArray()) {
            return null;
        }
        String n = oClass.getName();
        if (n.endsWith("[J")) {
            return o;
        }
        Object ra = null;
        long[] r;
        int j, len;
        if (findNumberOfDimensions(o) == 1) {
            len = Array.getLength(o);
            r = new long[len];
            if (n.endsWith("[B")) {
                byte[] in = (byte[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (long) in[j];
                }
            } else if (n.endsWith("[D")) {
                double[] in = (double[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = Math.round(in[j]);
                }
            } else if (n.endsWith("[F")) {
                float[] in = (float[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (long) Math.round(in[j]);
                }
            } else if (n.endsWith("[I")) {
                int[] in = (int[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (long) in[j];
                }
            } else if (n.endsWith("[S")) {
                short[] in = (short[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (long) in[j];
                }
            }
            ra = r;
        } else {
            FlatArray flr = new FlatArray(o);
            len = flr.getFlatLength();
            r = new long[len];
            Object ar = flr.getFlatArray();
            if (n.endsWith("[B")) {
                byte[] in = (byte[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (long) in[j];
                }
            } else if (n.endsWith("[D")) {
                double[] in = (double[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = Math.round(in[j]);
                }
            } else if (n.endsWith("[F")) {
                float[] in = (float[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (long) Math.round(in[j]);
                }
            } else if (n.endsWith("[I")) {
                int[] in = (int[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (long) in[j];
                }
            } else if (n.endsWith("[S")) {
                short[] in = (short[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (long) in[j];
                }
            }
            flr.setFlatArray(r);
            ra = flr.restoreArray(true);
        }
        return r;
    }

    /**
     * Class method that converts a given input array to an array of shorts. The input array can have any
     * dimensionality, but must contain primitive arithmetic types. If its elements are boolean, char, or reference
     * types then the method returns <i>null</i>. If the input object is not an array, the method also returns
     * <i>null</i>. If the input object is an array of doubles, then it is passed directly to output. Floats and doubles
     * are converted first to ints by rounding and then to shorts by casting.
     *
     * @param o The input array
     * @return The array converted to an array of shorts
     */
    public static Object toShortArray(Object o) {
        if (o == null) {
            return null;
        }
        Class oClass = o.getClass();
        if (!oClass.isArray()) {
            return null;
        }
        String n = oClass.getName();
        if (n.endsWith("[S")) {
            return o;
        }
        Object ra = null;
        int j, len;
        short[] r;
        if (findNumberOfDimensions(o) == 1) {
            len = Array.getLength(o);
            r = new short[len];
            if (n.endsWith("[B")) {
                byte[] in = (byte[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (short) in[j];
                }
            } else if (n.endsWith("[D")) {
                double[] in = (double[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (short) Math.round(in[j]);
                }
            } else if (n.endsWith("[F")) {
                float[] in = (float[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (short) Math.round(in[j]);
                }
            } else if (n.endsWith("[I")) {
                int[] in = (int[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (short) in[j];
                }
            } else if (n.endsWith("[J")) {
                long[] in = (long[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (short) in[j];
                }
            }
            ra = r;
        } else {
            FlatArray flr = new FlatArray(o);
            len = flr.getFlatLength();
            r = new short[len];
            Object ar = flr.getFlatArray();
            if (n.endsWith("[B")) {
                byte[] in = (byte[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (short) in[j];
                }
            } else if (n.endsWith("[D")) {
                double[] in = (double[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (short) Math.round(in[j]);
                }
            } else if (n.endsWith("[F")) {
                float[] in = (float[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (short) Math.round(in[j]);
                }
            } else if (n.endsWith("[I")) {
                int[] in = (int[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (short) in[j];
                }
            } else if (n.endsWith("[J")) {
                long[] in = (long[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (short) in[j];
                }
            }
            flr.setFlatArray(r);
            ra = flr.restoreArray(true);
        }
        return r;
    }

    /**
     * Class method that converts a given input array to an array of bytes. The input array can have any dimensionality,
     * but must contain primitive arithmetic types. If its elements are boolean, char, or reference types then the
     * method returns <i>null</i>. If the input object is not an array, the method also returns <i>null</i>. If the
     * input object is an array of bytes, then it is passed directly to output. Floats and doubles are converted first
     * to ints by rounding and then to bytes by casting.
     *
     * @param o The input array
     * @return The array converted to an array of bytes
     */
    public static Object toByteArray(Object o) {
        if (o == null) {
            return null;
        }
        Class oClass = o.getClass();
        if (!oClass.isArray()) {
            return null;
        }
        String n = oClass.getName();
        if (n.endsWith("[B")) {
            return o;
        }
        Object ra = null;
        byte[] r;
        int j, len;
        if (findNumberOfDimensions(o) == 1) {
            len = Array.getLength(o);
            r = new byte[len];
            if (n.endsWith("[D")) {
                double[] in = (double[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (byte) Math.round(in[j]);
                }
            } else if (n.endsWith("[F")) {
                float[] in = (float[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (byte) Math.round(in[j]);
                }
            } else if (n.endsWith("[I")) {
                int[] in = (int[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (byte) in[j];
                }
            } else if (n.endsWith("[J")) {
                long[] in = (long[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (byte) in[j];
                }
            } else if (n.endsWith("[S")) {
                short[] in = (short[]) o;
                for (j = 0; j < len; j++) {
                    r[j] = (byte) in[j];
                }
            }
            ra = r;
        } else {
            FlatArray flr = new FlatArray(o);
            len = flr.getFlatLength();
            r = new byte[len];
            Object ar = flr.getFlatArray();
            if (n.endsWith("[D")) {
                double[] in = (double[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (byte) Math.round(in[j]);
                }
            } else if (n.endsWith("[F")) {
                float[] in = (float[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (byte) Math.round(in[j]);
                }
            } else if (n.endsWith("[I")) {
                int[] in = (int[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (byte) in[j];
                }
            } else if (n.endsWith("[J")) {
                long[] in = (long[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (byte) in[j];
                }
            } else if (n.endsWith("[S")) {
                short[] in = (short[]) ar;
                for (j = 0; j < len; j++) {
                    r[j] = (byte) in[j];
                }
            }
            flr.setFlatArray(r);
            ra = flr.restoreArray(true);
        }
        return r;
    }

    /**
     * Class method that converts the given input array to an array whose elements are of the type given by the Class in
     * the second argument. The input array (first argument) can have any dimensionality, but must contain primitive
     * arithmetic types. If its elements are boolean, char, or reference types, then the method returns <i>null</i>. If
     * the input object is not an array, the method also returns <i>null</i>. The second argument is a Class. If it is
     * the Class of a primitive data type, then the elements of the first argument will be converted to that type,
     * provided the type is arithmetic (<i>i.e.</i> not boolean or char); if the given class is boolean or char, the
     * method returns <i>null</i>. If the given Class is that of an array, then the output type will be the type of the
     * <i>elements</i> of the array; again, if these elements are of type boolean or char, the method returns null. If
     * the given Class is not a primitive type or an array, then the method returns null. If the given object is already
     * of the same Class as the given Class, then the object is simply passed to the output. </p><p> Note that Java
     * provides Class constants for primitives, denoted by <i>Integer.Type, Double.Type, etc,</i>. These values are also
     * returned by applying the suffix .class to a primitive variable type name, <i>e.g.</i> <i>int.class</i> has the
     * value <i>Integer.Type</i>. For any Object <i>Q</i>, including arrays, the class is given by the method
     * <i>Q.getClass()</i>. Normally, the present method would be used to to convert the elements of <i>o</i> to the
     * type of the elements of object <i>q</i>, by invoking <i>convertArrayType( o, q.getClass() )</i>.
     *
     * @param o        The input array
     * @param outClass The desired type of the output elements
     * @return The array converted to an array of the desired type
     */
    public static Object convertArrayElements(Object o, Class outClass) {
        if (o == null) {
            return null;
        }
        if (outClass == null) {
            return o;
        }
        Class inClass = o.getClass();
        if (!inClass.isArray()) {
            return null;
        }
        if (outClass.isInstance(o)) {
            return o;
        }
        if (outClass.isPrimitive()) {
            if (outClass == Double.TYPE) {
                return toDoubleArray(o);
            }
            if (outClass == Float.TYPE) {
                return toFloatArray(o);
            }
            if (outClass == Integer.TYPE) {
                return toIntArray(o);
            }
            if (outClass == Short.TYPE) {
                return toShortArray(o);
            }
            if (outClass == Long.TYPE) {
                return toLongArray(o);
            }
            if (outClass == Byte.TYPE) {
                return toByteArray(o);
            } else {
                return null;
            }
        }
        if (!outClass.isArray()) {
            return null;
        }
        String n = outClass.getName();
        if (n.endsWith("[B")) {
            return toByteArray(o);
        }
        if (n.endsWith("[D")) {
            return toDoubleArray(o);
        }
        if (n.endsWith("[F")) {
            return toFloatArray(o);
        }
        if (n.endsWith("[I")) {
            return toIntArray(o);
        }
        if (n.endsWith("[J")) {
            return toLongArray(o);
        }
        if (n.endsWith("[S")) {
            return toShortArray(o);
        } else {
            return null;
        }
    }


    /**
     * Class method that adds the given complex number (supplied as two doubles in the last two arguments) to the given
     * complex data array (supplied in the first two arguments, containing any primitive arithmetic types) and returns a
     * boolean <i>true</i> value to indicate success. Arithmetic is done in-place, <i>i.e.</i> the supplied arrays are
     * simply modified. </p><p> If the data are not arithmetic data then the method returns <i>false</i>. If the input
     * data are real (second argument <i>null</i>) but the imaginary part of the increment variable (fourth argument) is
     * non-zero, then the method returns <i>false</i>.
     *
     * @param or The real part of the array data, which must contain doubles
     * @param oi The imaginary part of the array data, which must contain doubles; it may be <i>null</i>
     * @param sr The real part of the increment number
     * @param si The imaginary part of the increment number (must be zero if the second argument is <i>null</i>)
     * @return True if the method succeeds
     */
    public static boolean incrementArray(Object or, Object oi, double sr, double si) {
        int k, len;
        FlatArray flr, fli;
        Object ar, ai;
        double[] dataReal, dataImag;
        if (!isArithmeticArray(or)) {
            return false;
        }
        boolean isComplexData = (oi != null);
        boolean isComplexIncrement = (si != 0);
        if (!isComplexData && isComplexIncrement) {
            return false;
        }
        if (findNumberOfDimensions(or) == 1) {
            if (or instanceof double[]) {
                dataReal = (double[]) or;
            } else {
                System.out.println(
                        "FlatArray.incrementArray: input array does not hold doubles. Convert to doubles before supplying argument.");
                return false;
            }
            len = dataReal.length;
            for (k = 0; k < len; k++) {
                dataReal[k] += sr;
            }
            if (isComplexData) {
                if (oi instanceof double[]) {
                    dataImag = (double[]) oi;
                } else {
                    System.out.println(
                            "FlatArray.incrementArray: input array does not hold doubles. Convert to doubles before supplying argument.");
                    return false;
                }
                for (k = 0; k < len; k++) {
                    dataImag[k] += si;
                }
            }
        } else {
            flr = new FlatArray(or);
            ar = flr.getFlatArray();
            len = Array.getLength(ar);
            if (ar instanceof double[]) {
                dataReal = (double[]) ar;
            } else {
                System.out.println(
                        "FlatArray.incrementArray: input array does not hold doubles. Convert to doubles before supplying argument.");
                return false;
            }
            for (k = 0; k < len; k++) {
                dataReal[k] += sr;
            }
            flr.restoreArray();
            if (isComplexData) {
                fli = new FlatArray(oi);
                ai = fli.getFlatArray();
                if (ai instanceof double[]) {
                    dataImag = (double[]) ai;
                } else {
                    System.out.println(
                            "FlatArray.incrementArray: input array does not hold doubles. Convert to doubles before supplying argument.");
                    return false;
                }
                for (k = 0; k < len; k++) {
                    dataImag[k] = si;
                }
                fli.restoreArray();
            }
        }
        return true;
    }

    /**
     * Class method that adds the given complex number (supplied as two doubles in the last two arguments) to the given
     * complex data array (supplied in the first two arguments, containing any primitive arithmetic types) and returns
     * an ArrayList holding the real and imaginary parts of the results as new arrays of doubles of the same
     * dimensionality as the given arrays. </p><p> If the data are not arithmetic data then the method returns a null
     * ArrayList. If the input data are real (second argument <i>null</i>) and the imaginary part of the increment
     * variable (fourth argument) is zero, then the second component of the returned ArrayList will be <i>null</i>. If
     * the input data are real but the imaginary part of the increment variable is non-zero, then the output data will
     * be complex.
     *
     * @param or The real part of the array data
     * @param oi The imaginary part of the array data (<i>null</i> if data are real)
     * @param sr The real part of the increment number
     * @param si The imaginary part of the increment number (zero if increment is real)
     * @return containing the real and imaginary parts of the incremented array (second element <i>null</i> if result is
     *         real)
     */
    public static ArrayList incrementCopyOfArray(Object or, Object oi, double sr, double si) {
        ArrayList answer = null;
        Object nr = multiArrayCopy(or);
        Object ni = null;
        if (oi != null) {
            ni = multiArrayCopy(oi);
        } else if (si != 0) {
            ni = multiArrayImitate(or);
        }
        boolean result = incrementArray(nr, ni, sr, si);
        if (result) {
            answer = new ArrayList(2);
            answer.add(nr);
            answer.add(ni);
        }
        return answer;
    }


    /**
     * Class method that adds the given real number to the given data array and returns a boolean <i>true</i> value to
     * indicate success. Arithmetic is done in-place, <i>i.e.</i> the supplied array is simply modified. </p><p> If the
     * data are not arithmetic data then the method returns <i>false</i>.
     *
     * @param o The array data, which must contain doubles
     * @param s The increment number
     * @return True if the method succeeds
     */
    public static boolean incrementArray(Object o, double s) {
        return incrementArray(o, null, s, 0.0);
    }


    /**
     * Class method that multiplies the given complex data array (supplied in the first two arguments, containing any
     * primitive arithmetic types) by the given complex number (supplied as two doubles in the last two arguments) and
     * returns a  boolean <i>true</i> value to indicate success. Arithmetic is done in-place, <i>i.e.</i> the supplied
     * arrays are simply modified. </p><p> If the data are not arithmetic data then the method returns <i>false</i>. If
     * the input data are real (second argument <i>null</i>) but the imaginary part of the scale variable (fourth
     * argument) is non-zero, then the method returns <i>false</i>.
     *
     * @param or The real part of the array data
     * @param oi The imaginary part of the array data (<i>null</i> if data are real)
     * @param sr The real part of the scaling number
     * @param si The imaginary part of the scaling number (zero if scale is real)
     * @return True if the method succeeds
     */
    public static boolean scaleArray(Object or, Object oi, double sr, double si) {
        int k, len;
        FlatArray flr = null;
        FlatArray fli = null;
        Object ar, ai;
        double[] dataReal = null;
        double[] dataImag = null;
        ;
        if (!isArithmeticArray(or)) {
            return false;
        }
        boolean isComplexData = (oi != null);
        boolean isComplexScale = (si != 0);
        if (!isComplexData && isComplexScale) {
            return false;
        }
        if (findNumberOfDimensions(or) == 1) {
            if (or instanceof double[]) {
                dataReal = (double[]) or;
            } else {
                System.out.println(
                        "FlatArray.scaleArray: input array does not hold doubles. Convert to doubles before supplying argument.");
                return false;
            }
            len = dataReal.length;
            dataImag = null;
            if (isComplexData) {
                if (oi instanceof double[]) {
                    dataImag = (double[]) oi;
                } else {
                    System.out.println(
                            "FlatArray.scaleArray: input array does not hold doubles. Convert to doubles before supplying argument.");
                    return false;
                }
            } else if (isComplexScale) {
                dataImag = new double[len];
            }
            if (!isComplexScale) {
                for (k = 0; k < len; k++) {
                    dataReal[k] *= sr;
                }
                if (isComplexData) {
                    for (k = 0; k < len; k++) {
                        dataImag[k] = sr * dataImag[k];
                    }
                }
            } else {
                double scratch;
                for (k = 0; k < len; k++) {
                    scratch = dataReal[k];
                    dataReal[k] *= sr;
                    dataReal[k] -= si * dataImag[k];
                    dataImag[k] *= sr;
                    dataImag[k] += si * scratch;
                }
            }
        } else {
            flr = new FlatArray(or);
            ar = flr.getFlatArray();
            len = Array.getLength(ar);
            if (ar instanceof double[]) {
                dataReal = (double[]) ar;
            } else {
                System.out.println(
                        "FlatArray.scaleArray: input array does not hold doubles. Convert to doubles before supplying argument.");
                return false;
            }
            if (isComplexData) {
                fli = new FlatArray(oi);
                ai = fli.getFlatArray();
                if (ai instanceof double[]) {
                    dataImag = (double[]) ai;
                } else {
                    System.out.println(
                            "FlatArray.scaleArray: input array does not hold doubles. Convert to doubles before supplying argument.");
                    return false;
                }
            }
            if (!isComplexScale) {
                for (k = 0; k < len; k++) {
                    dataReal[k] *= sr;
                }
                flr.restoreArray();
                if (isComplexData) {
                    for (k = 0; k < len; k++) {
                        dataImag[k] *= sr;
                    }
                    fli.restoreArray();
                }
            } else {
                double scratch;
                for (k = 0; k < len; k++) {
                    scratch = dataReal[k];
                    dataReal[k] *= sr;
                    dataReal[k] -= si * dataImag[k];
                    dataImag[k] *= sr;
                    dataImag[k] += si * scratch;
                }
                flr.restoreArray();
                fli.restoreArray();
            }
        }
        return true;
    }

    /**
     * Class method that multiplies the given complex data array (supplied in the first two arguments, containing any
     * primitive arithmetic types) by the given complex number (supplied as two doubles in the last two arguments) and
     * returns an ArrayList holding the real and imaginary parts of the results as new arrays of doubles of the same
     * dimensionality as the given arrays. </p><p> If the data are not arithmetic data then the method returns a null
     * ArrayList. If the input data are real (second argument <i>null</i>) and the imaginary part of the scale variable
     * (fourth argument) is zero, then the second component of the returned ArrayList will be <i>null</i>. If the input
     * data are real but the imaginary part of the scale variable is non-zero, then the output data will be complex.
     *
     * @param or The real part of the array data
     * @param oi The imaginary part of the array data (<i>null</i> if data are real)
     * @param sr The real part of the scaling number
     * @param si The imaginary part of the scaling number (zero if scale is real)
     * @return containing the real and imaginary parts of the rescaled array (second element <i>null</i> if result is
     *         real)
     */
    public static ArrayList scaleCopyOfArray(Object or, Object oi, double sr, double si) {
        ArrayList answer = null;
        Object nr = multiArrayCopy(or);
        Object ni = null;
        if (oi != null) {
            ni = multiArrayCopy(oi);
        } else if (si != 0) {
            ni = multiArrayImitate(or);
        }
        boolean result = scaleArray(nr, ni, sr, si);
        if (result) {
            answer = new ArrayList(2);
            answer.add(nr);
            answer.add(ni);
        }
        return answer;
    }


    /**
     * Class method that multiplies the given real data array by the given real number  and returns a boolean
     * <i>true</i> value to indicate success. Arithmetic is done in-place, <i>i.e.</i> the supplied array is simply
     * modified. </p><p> If the data are not arithmetic data then the method returns <i>false</i>.
     *
     * @param o The array data, which must contain doubles
     * @param s The increment number
     * @return True if the method succeeds
     */
    public static boolean scaleArray(Object o, double s) {
        return scaleArray(o, null, s, 0);
    }


    /**
     * Class method maxArray returns a double containing the maximum value of all the elements of the given array, if
     * its elements are of an arithmetic data type, as determined by method <i>isArithmeticArray</i>. If the array
     * contains primitive types other than doubles, the returned value is converted to a double. If the data are not
     * arithmetic, then <i>Double.NaN</i> is returned.
     *
     * @param in The array to be tested
     * @return The maximum value of the elements of the given array
     */
    public static double maxArray(Object in) {
        int len, k;
        FlatArray fl;
        Object o;
        double d;
        int i;
        long l;
        double m;
        String name;
        if (isArithmeticArray(in)) {
            fl = new FlatArray(in);
            name = fl.getComponentName();
            o = fl.getFlatArray();
            len = Array.getLength(o);
            if ((Byte.TYPE).getName().equals(name)) {
                byte[] ia = (byte[]) o;
                i = (int) ia[0];
                for (k = 1; k < len; k++) {
                    i = Math.max(i, (int) ia[k]);
                }
                m = (double) i;
            } else if ((Double.TYPE).getName().equals(name)) {
                double[] da = (double[]) o;
                d = da[0];
                for (k = 1; k < len; k++) {
                    d = Math.max(d, da[k]);
                }
                m = d;
            } else if ((Float.TYPE).getName().equals(name)) {
                float[] fa = (float[]) o;
                d = (double) fa[0];
                for (k = 1; k < len; k++) {
                    d = Math.max(d, (double) fa[k]);
                }
                m = d;
            } else if ((Integer.TYPE).getName().equals(name)) {
                int[] ia = (int[]) o;
                i = ia[0];
                for (k = 1; k < len; k++) {
                    i = Math.max(i, ia[k]);
                }
                m = (double) i;
            } else if ((Long.TYPE).getName().equals(name)) {
                long[] la = (long[]) o;
                l = la[0];
                for (k = 1; k < len; k++) {
                    l = Math.max(l, la[k]);
                }
                m = (double) l;
            } else if ((Short.TYPE).getName().equals(name)) {
                short[] sa = (short[]) o;
                i = (int) sa[0];
                for (k = 1; k < len; k++) {
                    i = Math.max(i, (int) sa[k]);
                }
                m = (double) i;
            } else {
                m = Double.NaN;
            }
        } else {
            m = Double.NaN;
        }
        return m;
    }

    /**
     * Class method that returns a double containing the minimum value of all the elements of the given array, if its
     * elements are of an arithmetic data type, as determined by method <i>isArithmeticArray</i>. If the array contains
     * primitive types other than doubles, the returned value is converted to a double. If the data are not arithmetic,
     * then <i>Double.NaN</i> is returned.
     *
     * @param in The array to be tested
     * @return double The minimum value of the elements of the given array
     */
    public static double minArray(Object in) {
        int len, k;
        FlatArray fl;
        Object o;
        double d;
        int i;
        long l;
        double m;
        String name;
        if (isArithmeticArray(in)) {
            fl = new FlatArray(in);
            name = fl.getComponentName();
            o = fl.getFlatArray();
            len = Array.getLength(o);
            if ((Byte.TYPE).getName().equals(name)) {
                byte[] ia = (byte[]) o;
                i = (int) ia[0];
                for (k = 1; k < len; k++) {
                    i = Math.min(i, (int) ia[k]);
                }
                m = (double) i;
            } else if ((Double.TYPE).getName().equals(name)) {
                double[] da = (double[]) o;
                d = da[0];
                for (k = 1; k < len; k++) {
                    d = Math.min(d, da[k]);
                }
                m = d;
            } else if ((Float.TYPE).getName().equals(name)) {
                float[] fa = (float[]) o;
                d = (double) fa[0];
                for (k = 1; k < len; k++) {
                    d = Math.min(d, (double) fa[k]);
                }
                m = d;
            } else if ((Integer.TYPE).getName().equals(name)) {
                int[] ia = (int[]) o;
                i = ia[0];
                for (k = 1; k < len; k++) {
                    i = Math.min(i, ia[k]);
                }
                m = (double) i;
            } else if ((Long.TYPE).getName().equals(name)) {
                long[] la = (long[]) o;
                l = la[0];
                for (k = 1; k < len; k++) {
                    l = Math.min(l, la[k]);
                }
                m = (double) l;
            } else if ((Short.TYPE).getName().equals(name)) {
                short[] sa = (short[]) o;
                i = (int) sa[0];
                for (k = 1; k < len; k++) {
                    i = Math.min(i, (int) sa[k]);
                }
                m = (double) i;
            } else {
                m = Double.NaN;
            }
        } else {
            m = Double.NaN;
        }
        return m;
    }

    /**
     * Class method that tests to see if the given arrays are of a similar type: both Objects are indeed arrays, they
     * have the same dimensionality, and their sizes in the different dimensions are the same. Finally it tests that
     * their types are the same.
     *
     * @param a1 The first array to be compared
     * @param a2 The second array to be compared
     * @return <i>True</i> if the arrays are similar
     */
    public static boolean similarArrays(Object a1, Object a2) {
        if ((!a1.getClass().isArray()) || (!a2.getClass().isArray())) {
            return false;
        }
        if (!a1.getClass().getName().equals(a2.getClass().getName())) {
            return false;
        }
        int[] d1 = findArrayDimensionLengths(a1);
        int[] d2 = findArrayDimensionLengths(a2);
        ;
        for (int k = 0; k < d1.length; k++) {
            if (d1[k] != d2[k]) {
                return false;
            }
        }
        return true;
    }


    /**
     * Class method that tests the elements of the two given arrays. If the arrays have the same dimensionality and
     * size, if the elements are primitive Java data types, and if all the elements of one array equal those of the
     * other array at the same position, then the method returns <i>true</i>. Otherwise it returns <i>false</i>. If the
     * given objects are not arrays, or are arrays of non-primitive objects, then the method also returns <i>false</i>.
     *
     * @param a1 The first array to be compared
     * @param a2 The second array
     * @return <i>True</i> if the two arrays are equal element-by-element
     */
    public static boolean equalArrays(Object a1, Object a2) {
        if (!similarArrays(a1, a2)) {
            return false;
        }
        if ((!isPrimitiveArray(a1)) || (!isPrimitiveArray(a2))) {
            return false;
        }
        int k, len;
        FlatArray fl1, fl2;
        Object o1, o2;
        String name1, name2;
        fl1 = new FlatArray(a1);
        name1 = fl1.getComponentName();
        o1 = fl1.getFlatArray();
        fl2 = new FlatArray(a2);
        name2 = fl2.getComponentName();
        o2 = fl2.getFlatArray();
        len = Array.getLength(o1);
        if (!name1.equals(name2)) {
            return false;
        }
        if ((Byte.TYPE).getName().equals(name1)) {
            byte[] b1 = (byte[]) o1;
            byte[] b2 = (byte[]) o2;
            for (k = 0; k < len; k++) {
                if (b1[k] != b2[k]) {
                    return false;
                }
            }
            o1 = b1;
            o2 = b2;
        } else if ((Double.TYPE).getName().equals(name1)) {
            double[] d1 = (double[]) o1;
            double[] d2 = (double[]) o2;
            for (k = 0; k < len; k++) {
                if (d1[k] != d2[k]) {
                    return false;
                }
            }
            o1 = d1;
            o2 = d2;
        } else if ((Float.TYPE).getName().equals(name1)) {
            float[] f1 = (float[]) o1;
            float[] f2 = (float[]) o2;
            for (k = 0; k < len; k++) {
                if (f1[k] != f2[k]) {
                    return false;
                }
            }
            o1 = f1;
            o2 = f2;
        } else if ((Integer.TYPE).getName().equals(name1)) {
            int[] i1 = (int[]) o1;
            int[] i2 = (int[]) o2;
            for (k = 0; k < len; k++) {
                if (i1[k] != i2[k]) {
                    return false;
                }
            }
            o1 = i1;
            o2 = i2;
        } else if ((Long.TYPE).getName().equals(name1)) {
            long[] l1 = (long[]) o1;
            long[] l2 = (long[]) o2;
            for (k = 0; k < len; k++) {
                if (l1[k] != l2[k]) {
                    return false;
                }
            }
            o1 = l1;
            o2 = l2;
        } else if ((Short.TYPE).getName().equals(name1)) {
            short[] s1 = (short[]) o1;
            short[] s2 = (short[]) o2;
            for (k = 0; k < len; k++) {
                if (s1[k] != s2[k]) {
                    return false;
                }
            }
            o1 = s1;
            o2 = s2;
        }
        return true;
    }

    /**
     * Class method that sets the elements of the given array to zero. If the given object is not an array, or is an
     * array of non-arithmetic elements, then the method returns without doing anything.
     *
     * @param a The array to be initialized
     */
    public static void initializeArray(Object a) {
        if (!a.getClass().isArray()) {
            return;
        }
        if (!isArithmeticArray(a)) {
            return;
        }
        int k;
        FlatArray fl = new FlatArray(a);
        String name = fl.getComponentName();
        Object o = fl.getFlatArray();
        int len = Array.getLength(o);
        if ((Byte.TYPE).getName().equals(name)) {
            byte[] b = (byte[]) o;
            for (k = 0; k < len; k++) {
                b[k] = (byte) 0;
            }
            o = b;
        } else if ((Double.TYPE).getName().equals(name)) {
            double[] d = (double[]) o;
            for (k = 0; k < len; k++) {
                d[k] = 0.0;
            }
            o = d;
        } else if ((Float.TYPE).getName().equals(name)) {
            float[] f = (float[]) o;
            for (k = 0; k < len; k++) {
                f[k] = (float) 0.0;
            }
            o = f;
        } else if ((Integer.TYPE).getName().equals(name)) {
            int[] i = (int[]) o;
            for (k = 0; k < len; k++) {
                i[k] = 0;
            }
            o = i;
        } else if ((Long.TYPE).getName().equals(name)) {
            long[] l = (long[]) o;
            for (k = 0; k < len; k++) {
                l[k] = (long) 0;
            }
            o = l;
        } else if ((Short.TYPE).getName().equals(name)) {
            short[] s = (short[]) o;
            for (k = 0; k < len; k++) {
                s[k] = (short) 0;
            }
            o = s;
        }
        fl.restoreArray();
    }

    /**
     * Class method that adds the elements of the two given arrays. If the arrays have the same dimensionality and size,
     * and if the elements are arithmetic Java data types, then the method adds the two arrays and returns an Object of
     * the same type. The arithmetic is done in-place in the first argument, so the returned value is simply a reference
     * to the first argument. If a new returned array is desired, then the first argument should be a copy of the input
     * array, using method <i>multiArrayCopy</i>. </p><p> If the input arrays are not the same dimensionality and size,
     * or if at least one of them does not contain arithmetic data types, then this method returns <i>null</i>. If the
     * given objects are not arrays, or are arrays of non-primitive objects, then the method also returns <i>null</i>.
     *
     * @param a1 The first array to be added
     * @param a2 The second array to be added
     * @return The sum of the two arrays element-by-element
     */
    public static Object addArrays(Object a1, Object a2) {
        if (!similarArrays(a1, a2)) {
            return null;
        }
        if ((!isArithmeticArray(a1)) || (!isArithmeticArray(a2))) {
            return null;
        }
        int k;
        FlatArray fl1 = new FlatArray(a1);
        String name = fl1.getComponentName();
        Object o1 = fl1.getFlatArray();
        FlatArray fl2 = new FlatArray(a2);
        Object o2 = fl2.getFlatArray();
        int len = Array.getLength(o1);
        Class component = o1.getClass().getComponentType();
        if ((Byte.TYPE).getName().equals(name)) {
            byte[] b1 = (byte[]) o1;
            byte[] b2 = (byte[]) o2;
            for (k = 0; k < len; k++) {
                b1[k] += b2[k];
            }
        } else if ((Double.TYPE).getName().equals(name)) {
            System.out.println("FlatArray add: adding doubles.");
            double[] d1 = (double[]) o1;
            double[] d2 = (double[]) o2;
            for (k = 0; k < len; k++) {
                d1[k] += d2[k];
            }
        } else if ((Float.TYPE).getName().equals(name)) {
            float[] f1 = (float[]) o1;
            float[] f2 = (float[]) o2;
            for (k = 0; k < len; k++) {
                f1[k] += f2[k];
            }
        } else if ((Integer.TYPE).getName().equals(name)) {
            int[] i1 = (int[]) o1;
            int[] i2 = (int[]) o2;
            for (k = 0; k < len; k++) {
                i1[k] += i2[k];
            }
        } else if ((Long.TYPE).getName().equals(name)) {
            long[] l1 = (long[]) o1;
            long[] l2 = (long[]) o2;
            for (k = 0; k < len; k++) {
                l1[k] += l2[k];
            }
        } else if ((Short.TYPE).getName().equals(name)) {
            short[] s1 = (short[]) o1;
            short[] s2 = (short[]) o2;
            for (k = 0; k < len; k++) {
                s1[k] += s2[k];
            }
        }
        System.out.println("FlatArray add: name of returned array is " + a1.getClass().getName());
        fl1.restoreArray();
        return a1;
    }

    /**
     * Class method that subtracts the elements of the second given array from those of the first. If the arrays have
     * the same dimensionality and size, and if the elements are arithmetic Java data types, then the method subtracts
     * the second array from the first and returns an Object of the same type. The arithmetic is done in-place in the
     * first argument, so the returned value is simply a reference to the first argument. If a new returned array is
     * desired, then the first argument should be a copy of the input array, using method <i>multiArrayCopy</i>. </p><p>
     * If the input arrays are not the same dimensionality and size, or if at least one of them does not contain
     * arithmetic data types, then this method returns <i>null</i>. If the given objects are not arrays, or are arrays
     * of non-primitive objects, then the method also returns <i>null</i>.
     *
     * @param a1 The array to be subtracted from
     * @param a2 The array to be subtracted
     * @return The result of subtracting the second array from the first element-by-element
     */
    public static Object subtractArrays(Object a1, Object a2) {
        if (!similarArrays(a1, a2)) {
            return null;
        }
        if ((!isArithmeticArray(a1)) || (!isArithmeticArray(a2))) {
            return null;
        }
        int k;
        FlatArray fl1 = new FlatArray(a1);
        String name = fl1.getComponentName();
        Object o1 = fl1.getFlatArray();
        FlatArray fl2 = new FlatArray(a2);
        Object o2 = fl2.getFlatArray();
        int len = Array.getLength(o1);
        Class component = o1.getClass().getComponentType();
        if ((Byte.TYPE).getName().equals(name)) {
            byte[] b1 = (byte[]) o1;
            byte[] b2 = (byte[]) o2;
            for (k = 0; k < len; k++) {
                b1[k] -= b2[k];
            }
        } else if ((Double.TYPE).getName().equals(name)) {
            double[] d1 = (double[]) o1;
            double[] d2 = (double[]) o2;
            for (k = 0; k < len; k++) {
                d1[k] -= d2[k];
            }
        } else if ((Float.TYPE).getName().equals(name)) {
            float[] f1 = (float[]) o1;
            float[] f2 = (float[]) o2;
            for (k = 0; k < len; k++) {
                f1[k] -= f2[k];
            }
        } else if ((Integer.TYPE).getName().equals(name)) {
            int[] i1 = (int[]) o1;
            int[] i2 = (int[]) o2;
            for (k = 0; k < len; k++) {
                i1[k] -= i2[k];
            }
        } else if ((Long.TYPE).getName().equals(name)) {
            long[] l1 = (long[]) o1;
            long[] l2 = (long[]) o2;
            for (k = 0; k < len; k++) {
                l1[k] -= l2[k];
            }
        } else if ((Short.TYPE).getName().equals(name)) {
            short[] s1 = (short[]) o1;
            short[] s2 = (short[]) o2;
            for (k = 0; k < len; k++) {
                s1[k] -= s2[k];
            }
        }
        fl1.restoreArray();
        return a1;
    }


    /**
     * Class method that multiplies the individual elements of two complex arrays, whose real and imaginary parts are
     * the four given arrays. Corresponding elements are multiplied. Thus, this is <i>not</i> a version of matrix
     * multiplication. If the arrays have the same dimensionality and size, and if the elements are arithmetic Java data
     * types, then the method multiplies the arrays and returns an ArrayList containing two arrays of the same type, the
     * real and imaginary parts of the result. The arithmetic is done in-place in the first 2 arguments, so the returned
     * values are simply references to these arguments. If new returned arrays are desired, then the first 2 arguments
     * should be copies of the input arrays, using method <i>multiArrayCopy</i>. </p><p> If the input arrays are not all
     * the same dimensionality and size, or if at least one of them does not contain arithmetic data types, the this
     * method returns <i>null</i>. If the given objects are not arrays, or are arrays of non-primitive objects, then the
     * method also returns <i>null</i>.
     *
     * @param a1r The real part of the first array to be multiplied
     * @param a1i The imaginary part of the first array to be multiplied
     * @param a2r The real part of the second array to be multiplied
     * @param a2i The imaginary part of the second array to be multiplied
     * @return The product of the first 2 arrays by the second two, using element-by-element complex multiplication
     */
    public static ArrayList multiplyArrays(Object a1r, Object a1i, Object a2r, Object a2i) {
        if (!similarArrays(a1r, a2r)) {
            return null;
        }
        if ((!isArithmeticArray(a1r)) || (!isArithmeticArray(a2r))) {
            return null;
        }
        if (a1i != null) {
            if (!similarArrays(a1r, a1i)) {
                return null;
            }
            if (!isArithmeticArray(a1i)) {
                return null;
            }
        }
        if (a2i != null) {
            if (!similarArrays(a1r, a2i)) {
                return null;
            }
            if (!isArithmeticArray(a2i)) {
                return null;
            }
        }
        boolean complex = ((a1i != null) || (a2i != null));
        ArrayList answer = new ArrayList(2);
        FlatArray fl1r = new FlatArray(a1r);
        String name = fl1r.getComponentName();
        Object o1r = fl1r.getFlatArray();
        FlatArray fl2r = new FlatArray(a2r);
        Object o2r = fl2r.getFlatArray();
        FlatArray fl1i = null;
        FlatArray fl2i = null;
        Object o1i = null;
        Object o2i = null;
        Class component = o1r.getClass().getComponentType();
        int len = Array.getLength(o1r);
        if (a1i != null) {
            fl1i = new FlatArray(a1i);
            o1i = fl1i.getFlatArray();
        } else if (complex) {
            o1i = Array.newInstance(component, len);
            initializeArray(o1i);
        }
        if (a2i != null) {
            fl2i = new FlatArray(a2i);
            o2i = fl2i.getFlatArray();
        } else if (complex) {
            o2i = Array.newInstance(component, len);
            initializeArray(o2i);
        }
        int k;
        if ((Byte.TYPE).getName().equals(name)) {
            byte[] b1r = (byte[]) o1r;
            byte[] b1i = (byte[]) o1i;
            byte[] b2r = (byte[]) o2r;
            byte[] b2i = (byte[]) o2i;
            if (complex) {
                byte scratch;
                for (k = 0; k < len; k++) {
                    scratch = b1r[k];
                    b1r[k] = (byte) (b1r[k] * b2r[k] - b1i[k] * b2i[k]);
                    b1i[k] = (byte) (scratch * b2i[k] + b2r[k] * b1i[k]);
                }
            } else {
                for (k = 0; k < len; k++) {
                    b1r[k] *= b2r[k];
                }
            }
        } else if ((Double.TYPE).getName().equals(name)) {
            double[] d1r = (double[]) o1r;
            double[] d1i = (double[]) o1i;
            double[] d2r = (double[]) o2r;
            double[] d2i = (double[]) o2i;
            if (complex) {
                double scratch;
                for (k = 0; k < len; k++) {
                    scratch = d1r[k];
                    d1r[k] = d1r[k] * d2r[k] - d1i[k] * d2i[k];
                    d1i[k] = scratch * d2i[k] + d2r[k] * d1i[k];
                }
            } else {
                for (k = 0; k < len; k++) {
                    d1r[k] *= d2r[k];
                }
            }
        } else if ((Float.TYPE).getName().equals(name)) {
            float[] f1r = (float[]) o1r;
            float[] f1i = (float[]) o1i;
            float[] f2r = (float[]) o2r;
            float[] f2i = (float[]) o2i;
            if (complex) {
                float scratch;
                for (k = 0; k < len; k++) {
                    scratch = f1r[k];
                    f1r[k] = (f1r[k] * f2r[k] - f1i[k] * f2i[k]);
                    f1i[k] = (scratch * f2i[k] + f2r[k] * f1i[k]);
                }
            } else {
                for (k = 0; k < len; k++) {
                    f1r[k] *= f2r[k];
                }
            }
        } else if ((Integer.TYPE).getName().equals(name)) {
            int[] i1r = (int[]) o1r;
            int[] i1i = (int[]) o1i;
            int[] i2r = (int[]) o2r;
            int[] i2i = (int[]) o2i;
            if (complex) {
                int scratch;
                for (k = 0; k < len; k++) {
                    scratch = i1r[k];
                    i1r[k] = i1r[k] * i2r[k] - i1i[k] * i2i[k];
                    i1i[k] = scratch * i2i[k] + i2r[k] * i1i[k];
                }
            } else {
                for (k = 0; k < len; k++) {
                    i1r[k] *= i2r[k];
                }
            }
        } else if ((Long.TYPE).getName().equals(name)) {
            long[] l1r = (long[]) o1r;
            long[] l1i = (long[]) o1i;
            long[] l2r = (long[]) o2r;
            long[] l2i = (long[]) o2i;
            if (complex) {
                long scratch;
                for (k = 0; k < len; k++) {
                    scratch = l1r[k];
                    l1r[k] = (l1r[k] * l2r[k] - l1i[k] * l2i[k]);
                    l1i[k] = (scratch * l2i[k] + l2r[k] * l1i[k]);
                }
            } else {
                for (k = 0; k < len; k++) {
                    l1r[k] *= l2r[k];
                }
            }
        } else if ((Short.TYPE).getName().equals(name)) {
            short[] s1r = (short[]) o1r;
            short[] s1i = (short[]) o1i;
            short[] s2r = (short[]) o2r;
            short[] s2i = (short[]) o2i;
            if (complex) {
                short scratch;
                for (k = 0; k < len; k++) {
                    scratch = s1r[k];
                    s1r[k] = (short) (s1r[k] * s2r[k] - s1i[k] * s2i[k]);
                    s1i[k] = (short) (scratch * s2i[k] + s2r[k] * s1i[k]);
                }
            } else {
                for (k = 0; k < len; k++) {
                    s1r[k] *= s2r[k];
                }
            }
        }
        fl1r.restoreArray();
        answer.add(a1r);
        if (complex) {
            fl1i.restoreArray();
            answer.add(a1i);
        } else {
            answer.add(null);
        }
        return answer;
    }


    /**
     * Class method that  multiplies the individual elements of the two given arrays. Corresponding elements are
     * multiplied. Thus, this is <i>not</i> a version of matrix multiplication. If the arrays have the same
     * dimensionality and size, and if the elements are arithmetic Java data types, then the method multiplies the two
     * arrays and returns the result, an Object that is an array of the same type. The arithmetic is done in-place in
     * the first argument, so the returned value is simply a reference to the first argument. If  a new returned array
     * is desired, then the first argument should be a copy of the input array, using method <i>multiArrayCopy</i>.
     * </p><p> If the input arrays are not all the same dimensionality and size, or if at least one of them does not
     * contain arithmetic data types, the this method returns <i>null</i>. If the given objects are not arrays, or are
     * arrays of non-primitive objects, then the method also returns <i>null</i>.
     *
     * @param a1 The first array to be multiplied
     * @param a2 The second array to be multiplied
     * @return The product of the two arrays element-by-element
     */
    public static Object multiplyArrays(Object a1, Object a2) {
        ArrayList answer = multiplyArrays(a1, null, a2, null);
        return answer.get(0);
    }


    /**
     * Class method that divides the individual elements of two complex arrays, whose real and imaginary parts are the
     * four given arrays. Corresponding elements of the first set of arrays are divided by the elements of the second
     * set. Integer data types use integer division. If remaindered division is required, convert all arguments to a
     * floating type first, using for example the method <i>toDoubleArray</i>. If the arrays have the same
     * dimensionality and size, and if the elements are arithmetic Java data types, then the method divides the arrays
     * and returns an ArrayList containing two arrays of the same type, the real and imaginary parts of the result. The
     * arithmetic is done in-place in the first 2 arguments, so the returned values are simply references to these
     * arguments. If new returned arrays are desired, then the first 2 arguments should be copies of the input arrays,
     * using method <i>multiArrayCopy</i>. </p><p> If the input arrays are not all the same dimensionality and size, or
     * if at least one of them does not contain arithmetic data types, the this method returns <i>null</i>. If the given
     * objects are not arrays, or are arrays of non-primitive objects, then the method also returns <i>null</i>.
     *
     * @param a1r The real part of the numerator array
     * @param a1i The imaginary part of the numeratorarray
     * @param a2r The real part of the denominator array
     * @param a2i The imaginary part of the denominator array
     * @return The quotient of the first 2 arrays by the second two, using element-by-element complex division
     */
    public static ArrayList divideArrays(Object a1r, Object a1i, Object a2r, Object a2i) {
        if (!similarArrays(a1r, a2r)) {
            return null;
        }
        if ((!isArithmeticArray(a1r)) || (!isArithmeticArray(a2r))) {
            return null;
        }
        if (a1i != null) {
            if (!similarArrays(a1r, a1i)) {
                return null;
            }
            if (!isArithmeticArray(a1i)) {
                return null;
            }
        }
        if (a2i != null) {
            if (!similarArrays(a1r, a2i)) {
                return null;
            }
            if (!isArithmeticArray(a2i)) {
                return null;
            }
        }
        boolean complex = ((a1i != null) || (a2i != null));
        ArrayList answer = new ArrayList(2);
        FlatArray fl1r = new FlatArray(a1r);
        String name = fl1r.getComponentName();
        Object o1r = fl1r.getFlatArray();
        FlatArray fl2r = new FlatArray(a2r);
        Object o2r = fl2r.getFlatArray();
        FlatArray fl1i = null;
        FlatArray fl2i = null;
        Object o1i = null;
        Object o2i = null;
        Class component = o1r.getClass().getComponentType();
        int len = Array.getLength(o1r);
        if (a1i != null) {
            fl1i = new FlatArray(a1i);
            o1i = fl1i.getFlatArray();
        } else if (complex) {
            o1i = Array.newInstance(component, len);
            initializeArray(o1i);
        }
        if (a2i != null) {
            fl2i = new FlatArray(a2i);
            o2i = fl2i.getFlatArray();
        } else if (complex) {
            o2i = Array.newInstance(component, len);
            initializeArray(o2i);
        }
        int k;
        if ((Byte.TYPE).getName().equals(name)) {
            byte[] b1r = (byte[]) o1r;
            byte[] b1i = (byte[]) o1i;
            byte[] b2r = (byte[]) o2r;
            byte[] b2i = (byte[]) o2i;
            if (complex) {
                byte denom, scratch;
                for (k = 0; k < len; k++) {
                    scratch = b1r[k];
                    denom = (byte) (b2r[k] * b2r[k] + b2i[k] * b2i[k]);
                    b1r[k] = (byte) ((b1r[k] * b2r[k] + b1i[k] * b2i[k]) / denom);
                    b1i[k] = (byte) ((scratch * b2i[k] - b2r[k] * b1i[k]) / denom);
                }
            } else {
                for (k = 0; k < len; k++) {
                    b1r[k] /= b2r[k];
                }
            }
        } else if ((Double.TYPE).getName().equals(name)) {
            double[] d1r = (double[]) o1r;
            double[] d1i = (double[]) o1i;
            double[] d2r = (double[]) o2r;
            double[] d2i = (double[]) o2i;
            if (complex) {
                double denom, scratch;
                for (k = 0; k < len; k++) {
                    scratch = d1r[k];
                    denom = d2r[k] * d2r[k] + d2i[k] * d2i[k];
                    d1r[k] = (d1r[k] * d2r[k] + d1i[k] * d2i[k]) / denom;
                    d1i[k] = (scratch * d2i[k] - d2r[k] * d1i[k]) / denom;
                }
            } else {
                for (k = 0; k < len; k++) {
                    d1r[k] /= d2r[k];
                }
            }
        } else if ((Float.TYPE).getName().equals(name)) {
            float[] f1r = (float[]) o1r;
            float[] f1i = (float[]) o1i;
            float[] f2r = (float[]) o2r;
            float[] f2i = (float[]) o2i;
            if (complex) {
                float denom, scratch;
                for (k = 0; k < len; k++) {
                    scratch = f1r[k];
                    denom = (f2r[k] * f2r[k] + f2i[k] * f2i[k]);
                    f1r[k] = ((f1r[k] * f2r[k] + f1i[k] * f2i[k]) / denom);
                    f1i[k] = ((scratch * f2i[k] - f2r[k] * f1i[k]) / denom);
                }
            } else {
                for (k = 0; k < len; k++) {
                    f1r[k] /= f2r[k];
                }
            }
        } else if ((Integer.TYPE).getName().equals(name)) {
            int[] i1r = (int[]) o1r;
            int[] i1i = (int[]) o1i;
            int[] i2r = (int[]) o2r;
            int[] i2i = (int[]) o2i;
            if (complex) {
                int denom, scratch;
                for (k = 0; k < len; k++) {
                    scratch = i1r[k];
                    denom = i2r[k] * i2r[k] + i2i[k] * i2i[k];
                    i1r[k] = (i1r[k] * i2r[k] + i1i[k] * i2i[k]) / denom;
                    i1i[k] = (scratch * i2i[k] - i2r[k] * i1i[k]) / denom;
                }
            } else {
                for (k = 0; k < len; k++) {
                    i1r[k] /= i2r[k];
                }
            }
        } else if ((Long.TYPE).getName().equals(name)) {
            long[] l1r = (long[]) o1r;
            long[] l1i = (long[]) o1i;
            long[] l2r = (long[]) o2r;
            long[] l2i = (long[]) o2i;
            if (complex) {
                long denom, scratch;
                for (k = 0; k < len; k++) {
                    scratch = l1r[k];
                    denom = (l2r[k] * l2r[k] + l2i[k] * l2i[k]);
                    l1r[k] = ((l1r[k] * l2r[k] + l1i[k] * l2i[k]) / denom);
                    l1i[k] = ((scratch * l2i[k] - l2r[k] * l1i[k]) / denom);
                }
            } else {
                for (k = 0; k < len; k++) {
                    l1r[k] /= l2r[k];
                }
            }
        } else if ((Short.TYPE).getName().equals(name)) {
            short[] s1r = (short[]) o1r;
            short[] s1i = (short[]) o1i;
            short[] s2r = (short[]) o2r;
            short[] s2i = (short[]) o2i;
            if (complex) {
                short denom, scratch;
                for (k = 0; k < len; k++) {
                    scratch = s1r[k];
                    denom = (short) (s2r[k] * s2r[k] + s2i[k] * s2i[k]);
                    s1r[k] = (short) ((s1r[k] * s2r[k] + s1i[k] * s2i[k]) / denom);
                    s1i[k] = (short) ((scratch * s2i[k] - s2r[k] * s1i[k]) / denom);
                }
            } else {
                for (k = 0; k < len; k++) {
                    s1r[k] /= s2r[k];
                }
            }
        }
        fl1r.restoreArray();
        answer.add(a1r);
        if (complex) {
            fl1i.restoreArray();
            answer.add(a1i);
        } else {
            answer.add(null);
        }
        return answer;
    }


    /**
     * Class method that divides the individual elements of two arrays. Corresponding elements of the first array are
     * divided by the elements of the second. Integer data types use integer division. If remaindered division is
     * required, convert both arguments to a floating type first, using for example the method <i>toDoubleArray</i>. If
     * the arrays have the same dimensionality and size, and if the elements are arithmetic Java data types, then the
     * method divides the arrays and returns the result as an Object that is an array of the same type. The arithmetic
     * is done in-place in the first 2 arguments, so the returned value is simply a reference to the first argument. If
     * a new returned array is desired, then the first argument should be a copy of the input array, using method
     * <i>multiArrayCopy</i>. </p><p> If the input arrays are not the same dimensionality and size, or if at least one
     * of them does not contain arithmetic data types, the this method returns <i>null</i>. If the given objects are not
     * arrays, or are arrays of non-primitive objects, then the method also returns <i>null</i>.
     *
     * @param a1 The numerator array
     * @param a2 The denominator array
     * @return The quotient of the two arrays element-by-element
     */
    public static Object divideArrays(Object a1, Object a2) {
        ArrayList answer = divideArrays(a1, null, a2, null);
        return answer.get(0);
    }

    /*
     * Class methods to assist manipulating spectra. In this version of
     * FlatArray these do not deal with multi-dimensional arrays, so
     * they are not really related to the other utilities here. But
     * in future releases they will be upgraded to use the facilities
     * of FlatArray to deal with multi-dimensional spectra. The
     * upgrade should consist of new methods over-loading the same
     * name with different arguments.
     */

    /**
     * Class method that converts a full-bandwidth spectrum to a narrow-band spectrum. It returns a double[] array
     * containing only the elements of the original array that are required by the Triana data model for spectra. The
     * input array can be either the real or the imaginary part of the spectrum. The bandwidth is determined by the
     * integer indices of the edges of the band in the input array; these are the indices of the positive-frequency
     * elements in the full-bandwidth spectrum. The method assumes that the data obeys the Triana spectral data model,
     * but it does not check this.
     *
     * @param fullArray The input full-bandwidth spectrum
     * @param oneSide   <i>True</i> if the input spectrum in one-sided
     * @param low       The index of the lower edge of the returned band
     * @param high      The index of the higher edge of the returned band
     * @return The corresponding narrow-band spectrum
     * @see triana.types.Spectrum
     * @see triana.types.ComplexSpectrum
     */
    public static double[] convertToNarrowBand(double[] fullArray, boolean oneSide, int low, int high) {
        double[] narrowArray = fullArray; // fool the compiler about initalization
        int lenFull = fullArray.length;
        int bandwidth = high - low + 1;
        int topwidth = bandwidth;
        int starttop;
        boolean even = (lenFull % 2 == 0);
        if (oneSide) { // one-sided input array
            narrowArray = new double[bandwidth];
            System.arraycopy(fullArray, low, narrowArray, 0, bandwidth);
        } else { // two-sided input array
            if (even) { // input array has even number of elements
                if (low == 0) { // output band contains zero freq
                    topwidth = bandwidth - 1;
                    starttop = lenFull - topwidth;
                } else if (high == lenFull / 2) { //output band contains top freq
                    topwidth = bandwidth - 1;
                    starttop = high + 1;
                } else { // output band does not include either end of spectrum
                    topwidth = bandwidth;
                    starttop = lenFull - high;
                }
            } else { // input array has odd number of elements
                if (low == 0) { // output band contains zero freq
                    topwidth = bandwidth - 1;
                    starttop = lenFull - topwidth;
                } else if (high == (lenFull - 1) / 2) { //output band contains top freq
                    topwidth = bandwidth;
                    starttop = high + 1;
                } else { // output band does not include either end of spectrum
                    topwidth = bandwidth;
                    starttop = lenFull - high;
                }
            }
            narrowArray = new double[bandwidth + topwidth];
            System.arraycopy(fullArray, low, narrowArray, 0, bandwidth);
            System.arraycopy(fullArray, starttop, narrowArray, bandwidth, topwidth);
        }
        return narrowArray;
    }


    /**
     * Class method that takes the given array, assumed to be a two-sided spectrum (either the real or imaginary part)
     * and returns the associated one-sided spectrum. It uses the information in the given integer full (the number of
     * elements in the full two-sided spectrum, which is not the same as the number of elements of the given array if it
     * is narrow-band); the given boolean narrowband (the flag that is true if the given array is a narrow-band slice of
     * a full spectrum); and the given boolean <i>containsZeroFrequency</i> (<i>true</i> if the given array contains the
     * zero-frequency element of the full spectrum).
     * <p/>
     * The storage order is the order given by the Triana storage convention. The method extracts the one-sided spectrum
     * essentially by copying the positive-frequency elements of the two-sided spectrum. It does not check whether the
     * negative-frequency elements are appropriately related to the positive-frequency ones.
     *
     * @param twoSide               The input array containing a two-sided spectrum
     * @param full                  The number of elements in the full two-sided spectrum
     * @param narrowband            <i>True</i> if the input array is a narrow-band spectrum
     * @param containsZeroFrequency <i>True</i> if the zero-frequency element is present in the input array
     * @return The associated one-sided spectrum
     * @see triana.types.Spectrum
     * @see triana.types.ComplexSpectrum
     */
    public static double[] convertToOneSided(double[] twoSide, int full, boolean narrowband,
                                             boolean containsZeroFrequency) {
        boolean fullEven = (full % 2 == 0);
        int len2 = twoSide.length;
        boolean inputEven = (len2 % 2 == 0);
        int len1;
        double[] oneSide = twoSide; // fool compiler on intialization
        if (fullEven) { // full data set had an even number of elements
            if (!narrowband) { // complete spectrum in input data set
                len1 = len2 / 2 + 1;
            } else { // input spectrum is narrow-band
                if (containsZeroFrequency) { // zero freq is in band
                    len1 = (len2 + 1) / 2;
                } else { // zero freq missing from band
                    if (inputEven) { // the highest freq also missing
                        len1 = len2 / 2;
                    } else { // the highest freq is present in band
                        len1 = (len2 + 1) / 2;
                    }
                }
            }
        } else { // full data set had an odd number of elements
            if (!narrowband) { // complete spectrum in input data set
                len1 = (len2 + 1) / 2;
            } else { // narrow-band input data set
                if (containsZeroFrequency) { // zero frequency is in the band
                    len1 = (len2 + 1) / 2;
                } else { // zero frequency missing from band
                    len1 = len2 / 2;
                }
            }
        }
//	System.out.println("FlatArray convertToOneSided: input array of type " + twoSide.getClass().getName() + " with " + String.valueOf(full) + " elements will be reduced to length " + String.valueOf(len1) );
        oneSide = new double[len1];
        System.arraycopy(twoSide, 0, oneSide, 0, len1);
        return oneSide;
    }


    /**
     * Class method that converts takes the given spectral data array, which can be one-sided and/or narrow-band, and
     * which is assumed to be stored according to the Triana spectral data model, and returns the associated two-sided
     * full-bandwidth data array. The conversion from one-sided to two-sided assumes that the data from which the
     * spectrum was obtained are real, so that the spectrum at negative frequencies is the complex-conjugate of that at
     * positive frequencies. In this case it reads the boolean parameter realpart to determine if it is converting the
     * real part or the imaginary part of the spectrum. The conversion from narrow-band to full-band pads the remaining
     * parts of the spectrum with zeros; it uses the int parameter low to determine the lowest edge of the given band.
     * </p><p> The nature of the input array is given by the flags onesided and narrowband. The int argument full is the
     * number of elements in the returned array. </p><p> The method does not check that the input data obeys the Triana
     * spectral data model. The user is responsible for ensuring this.
     *
     * @param input      The input  array
     * @param full       The number of data points in the returned array
     * @param onesided   <i>True</i> if the input data are one-sided
     * @param realpart   <i>True</i> if the input data are the real part of a spectrum, <i>false</i> if imaginary part
     * @param narrowband <i>True</i> if the input data are narrow-band
     * @param low        The lower bound on the frequency spectrum
     * @return The data as a two-sided full-bandwidth spectrum
     */
    public static double[] convertToFullSpectrum(double[] input, int full, boolean onesided, boolean realpart,
                                                 boolean narrowband, int low) {
        if (!(onesided || narrowband)) {
            return input;
        } // no action needed
        //	System.out.println("FlatArray convertToFullSpectrum: inputLength = " + String.valueOf(input.length) + ", full length = " + String.valueOf(full) + ", low = " + String.valueOf(low) );
        //	System.out.println("onesided = " + String.valueOf(onesided) + ", realpart = " + String.valueOf(realpart) + ", narrowband = " + String.valueOf(narrowband) );
        int high = low;
        int inputLength = input.length;
        double[] fullArray = new double[full];
        boolean evenFull = (full % 2 == 0);
        if (!narrowband) { // data is full-bandwidth so it must be one-sided
            //	    System.out.println("Begin copying input array into fullArray.");
            System.arraycopy(input, 0, fullArray, 0, inputLength);
            reverseArray(input, realpart);
            if (evenFull) { // full spectrum has even no. elems
                //		System.out.println("Begin copying reverse array into fullArray for full even.");
                System.arraycopy(input, 1, fullArray, inputLength, inputLength - 2);
            } else { // full spectrum has odd no. elems
                //		System.out.println("Begin copying reverse array into fullArray for full odd.");
                System.arraycopy(input, 0, fullArray, inputLength, inputLength - 1);
            }
        } else { // data is narrow-band
            int bandwidth;
            if (onesided) { // data is one-sided and narrowband
                bandwidth = inputLength;
                high = low + bandwidth - 1;
                System.arraycopy(input, 0, fullArray, low, bandwidth);
                reverseArray(input, realpart);
                if (evenFull) { // two-sided has even no. elems
                    if (low == 0) { // bandwidth begins with zero freq
                        System.arraycopy(input, 0, fullArray, full - inputLength + 1, inputLength - 1);
                    } else if (high == full / 2) { // bandwidth goes up to top
                        System.arraycopy(input, 1, fullArray, full / 2 + 1, inputLength - 1);
                    } else { // band does not reach bottom or top of spectrum
                        System.arraycopy(input, 0, fullArray, full - high, inputLength);
                    }
                } else { // two-sided has odd no. elems
                    if (low == 0) { // bandwidth begins with zero freq
                        System.arraycopy(input, 0, fullArray, full - inputLength + 1, inputLength - 1);
                    } else if (high == (full - 1) / 2) { //band goes to top
                        System.arraycopy(input, 0, fullArray, low + inputLength, inputLength);
                    } else { // band does not reach either top or bottom
                        System.arraycopy(input, 0, fullArray, full - high, inputLength);
                    }
                }
            } else { // data is two-sided but narrowband
                boolean evenInput = ((inputLength / 2) * 2 == inputLength);
                if (evenInput) { // two-sided data are symmetrical
                    bandwidth = inputLength / 2;
                    high = low + bandwidth - 1;
                    System.arraycopy(input, 0, fullArray, low, bandwidth);
                    System.arraycopy(input, bandwidth, fullArray, full - high, bandwidth);
                } else { // first part of input has one extra val (zero freq or top)
                    bandwidth = (inputLength + 1) / 2;
                    high = low + bandwidth - 1;
                    System.arraycopy(input, 0, fullArray, low, bandwidth);
                    if (low == 0) { // input contains zero freq
                        System.arraycopy(input, bandwidth, fullArray, full - high, bandwidth - 1);
                    } else { // input contains highest freq
                        System.arraycopy(input, bandwidth, fullArray, full - high + 1, bandwidth - 1);
                    }
                }
            }
        }
        return fullArray;
    }

    /**
     * Class method to test whether a spectral data set has the symmetry property that will result in its inverse
     * transform being pure real or pure imaginary. If, for a full data set (not one-sided spectra, which will
     * automatically transform to real), x[k] = ComplexConjugate(x[N-k]), then the transform will be real. If  x[k] =
     * -ComplexConjugate(x[N-k]) then the transform will be imaginary. This test should only be applied to the full data
     * set that will be transformed.
     * <p/>
     * The return value is an int. It has value +1 if the transform will be real, -1 if imaginary, 0 if there is no
     * special symmetry.
     *
     * @param xr The real part of the input data set (can be null)
     * @param xi The imaginary part of the input data set (can be null)
     * @return Takes value 1, 0, or -1 for symmetry, no symmetry, antisymmetry
     */
    public static int testConjugateSymmetry(double[] xr, double[] xi) {
        boolean symmetric = true;
        boolean antisymmetric = true;
        int k, len, lk;
        if (xi == null) {
            if (xr == null) {
                return 0;
            } else {
                if (xr[0] != 0) {
                    antisymmetric = false;
                }
                k = 1;
                len = xr.length;
                while ((k < len / 2) && (symmetric || antisymmetric)) {
                    lk = len - k;
                    if ((symmetric) && (xr[k] != xr[lk])) {
                        symmetric = false;
                    }
                    if ((antisymmetric) && (xr[k] != -xr[lk])) {
                        antisymmetric = false;
                    }
                    k++;
                }
            }
        } else {
            if (xr == null) {
                if (xi[0] != 0) {
                    symmetric = false;
                }
                k = 1;
                len = xi.length;
                while ((k < len / 2) && (symmetric || antisymmetric)) {
                    lk = len - k;
                    if ((symmetric) && (xi[k] != -xi[lk])) {
                        symmetric = false;
                    }
                    if ((antisymmetric) && (xi[k] != xi[lk])) {
                        antisymmetric = false;
                    }
                    k++;
                }
            } else {
                if (xr[0] != 0) {
                    antisymmetric = false;
                }
                if (xi[0] != 0) {
                    symmetric = false;
                }
                k = 1;
                len = xr.length;
                while ((k < len / 2) && (symmetric || antisymmetric)) {
                    lk = len - k;
                    if ((symmetric) && ((xr[k] != xr[lk]) || (xi[k] != -xi[lk]))) {
                        symmetric = false;
                    }
                    if ((antisymmetric) && ((xr[k] != -xr[lk]) || (xi[k] != xi[lk]))) {
                        antisymmetric = false;
                    }
                    k++;
                }
            }
        }
        if (symmetric) {
            return 1;
        }
        if (antisymmetric) {
            return -1;
        }
        return 0;
    }

    /**
     * Utility method that takes an input double[] array and reverses the order of its elements. If the argument flag
     * <i>plus</i> is <i>false</i>, then it also multiplies the elements of the array by -1.
     */
    private static void reverseArray(double[] a, boolean plus) {
        int len = a.length;
        int k, last, kconj;
        double scratch;
        boolean even = (len % 2 == 0);
        last = len - 1;
        if (plus) {
            for (k = 0; k < len / 2; k++) {
                kconj = last - k;
                scratch = a[k];
                a[k] = a[kconj];
                a[kconj] = scratch;
            }
        } else {
            for (k = 0; k < len / 2; k++) {
                kconj = last - k;
                scratch = a[k];
                a[k] = -a[kconj];
                a[kconj] = -scratch;
            }
            if (!even) {
                a[len / 2] *= -1;
            }
        }
    }


    /*
     * Instance methods.
     */


    /**
     * Returns the array that holds the flattened data.
     *
     * @return Object The flattened array
     */
    public Object getFlatArray() {
        return flatArray;
    }

    /**
     * Returns the int[] that contains the lengths of the dimensions of the original array.
     *
     * @return int[] The original dimension lengths
     */
    public int[] getLengths() {
        return lengths;
    }

    /**
     * Returns the length of the flattened array, which is the total number of elements in the original array.
     *
     * @return int The total flattened length
     */
    public int getFlatLength() {
        return totalLength;
    }

    /**
     * Returns the name of the type of the components of the flattened array.
     *
     * @return String The name of the components
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * Sets the Array that holds the flattened data.
     *
     * @param newArray The new flattened array
     */
    public void setFlatArray(Object newArray) {
        if (newArray.getClass().isArray()) {
            flatArray = newArray;
            if (Array.getLength(newArray) != totalLength) {
                inputObject = null;
            }
        } else {
            flatArray = new Object[1];
            Array.set(flatArray, 0, newArray);
            lengths = new int[1];
            lengths[0] = 1;
        }
        componentName = flatArray.getClass().getComponentType().getName();
    }

    /**
     * Sets the int[] that holds the lengths of the original dimensions.
     *
     * @param newLengths The new lengths array
     */
    public void setLengths(int[] newLengths) {
        lengths = newLengths;
        int totalDims = lengths.length;
        if (totalDims == 1) {
            totalLength = lengths[0];
        } else {
            int totalNumbers = lengths[0];
            for (int i = 1; i < totalDims; i++) {
                totalNumbers *= lengths[i];
            }
            totalLength = totalNumbers;
        }
    }

    /**
     * Sets the name of the type of the component of the flat array.
     *
     * @param newComponentName The new type name
     */
    public void setComponentName(String newComponentName) {
        componentName = newComponentName;
    }

    /**
     * Returns an array of the dimensionality given by <i>lengths</i> and containing the data held in <i>flatArray</i>.
     * It will restore to the original array that was used to create <i>flatArray</i> if parameter <i>newArray</i> is
     * <i>false</i>. If the parameter is <i>true</i>, it will create a new array; if the elements of the old array are
     * primitive types, they will be copied to the new array; if they are reference types the new array will contain
     * references to them.
     *
     * @param copy True if the output should be a new object, false if the old object should be restored
     * @return The new multidimensional array
     */
    public Object restoreArray(boolean copy) {
        Object o;
        Class componentClass;
        int flattenedDims = lengths.length;
        if (flattenedDims == 1) {
            o = flatArray;
        } else if ((copy) || (inputObject == null)) {
            try {
                if (componentName.equals("boolean")) {
                    componentClass = Boolean.TYPE;
                } else if (componentName.equals("char")) {
                    componentClass = Character.TYPE;
                } else if (componentName.equals("byte")) {
                    componentClass = Byte.TYPE;
                } else if (componentName.equals("short")) {
                    componentClass = Short.TYPE;
                } else if (componentName.equals("int")) {
                    componentClass = Integer.TYPE;
                } else if (componentName.equals("long")) {
                    componentClass = Long.TYPE;
                } else if (componentName.equals("float")) {
                    componentClass = Float.TYPE;
                } else if (componentName.equals("double")) {
                    componentClass = Double.TYPE;
                } else if (componentName.equals("void")) {
                    componentClass = Void.TYPE;
                } else {
                    componentClass = Class.forName(componentName);
                }
                o = Array.newInstance(componentClass, lengths);
                recurseArrayPack(flatArray, o, lengths, 0, flattenedDims - 1, 0);
            }
            catch (ClassNotFoundException ex) {
                System.out.println("Class with name " + componentName
                        + " does not exist. Return null from FlatArray.restoreArray().");
                o = null;
            }
        } else {
            o = inputObject;
            recurseArrayPack(flatArray, o, lengths, 0, flattenedDims - 1, 0);
        }
        return o;
    }

    /**
     * Creates an array of the dimensionality given by lengths and containing the data held in <i>flatArray,/i>. It will
     * restore to the original array that was used to create <i>flatArray</i>.
     */
    public Object restoreArray() {
        return restoreArray(false);
    }
}



