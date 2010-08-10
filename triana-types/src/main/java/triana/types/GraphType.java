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
package triana.types;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import triana.types.util.FlatArray;
import triana.types.util.Str;
import triana.types.util.Triplet;

/**
 * GraphType is the basis for all Triana numerical data types that can be displayed graphically. <p> GraphType defines
 * Triana's most general numerical data structures along with methods for reading the data, modifying it, providing
 * information to a graphing object, restricting the data to a sub-domain, and doing element-by- element arithmetic on
 * the data (implementing the Arithmetic interface). The arithmetic methods include for example adding and multiplying
 * by other GraphType data objects and by Const type objects. GraphType implements arithmetic for data arrays of
 * arbitrary dimensionality holding arbitrary primitive arithmetic Java data types (double, int, float, etc.); the data
 * may be real or complex (in which the real and imaginary parts are held in separate arrays). The arithmetic
 * implementation relies heavily on the class methods of FlatArray, which also provides methods that graphical display
 * objects can use for rescaling and translating graphs. GraphType distinguishes between data for dependent and
 * independent variables, both of which may be complex. GraphType's data model is described below. </p><p> GraphType is
 * an abstract class, so it cannot be used directly to store data. Rather, "raw data" classes should be derived from
 * GraphType for specific values of dimensions of the data structures. Raw data types derived from GraphType include
 * VectorType (1D array) and MatrixType (2D array), and Curve (1D curve in several dimensions). These permit one to use
 * strong type checking in Units, while losing none of the functionality of GraphType. Therefore, although GraphType
 * provides methods for modifying or replacing data, it does not provide methods for changing the basic dimensionality
 * parameters that determine (i) the number of dimensions in the multi-dimensional arrays used to store data, and (ii)
 * the number of dependent variable arrays. </p><p> GraphType also does not provide parameters (auxilliary data) that
 * will be needed for some applications, nor should the "raw" types derived directly from GraphType include extra
 * parameters. It is recommended that types needing new parameters be derived as subclasses of these raw types and that
 * the methods used to manipulate parameters be implemented via Interfaces. Interfaces allow such types to be defined
 * anywhere in the inheritance tree of GraphType, <i>i.e.</i> for any kinds of data structures that are needed.
 * Interfaces already implemented include Signal, Spectral, and Histogramming. </p><p> GraphType's data model is
 * designed to allow very general representations of functions and geometical objects. The data represent the values of
 * a number <i>dependentVariables</i> of functions of another number <i>independentVariables</i> of independent
 * variables. The independent variables span the <i>domain</i> of the problem, and they are represented by a set of
 * one-dimensional arrays of doubles (or, to save storage, by Triplets, which are basically iteration counters -- see
 * below). </p><p> The data arrays (dependent variables) must each have <i>independentVariables</i> dimensions, but
 * their lengths in each of the dimensions can differ from one another. Thus, there is no requirement that they span the
 * domain. The data arrays can store any kind of data. The data could be primitive data types (such as doubles) or other
 * Java Ojbects, such as colors. The data elements could also be arrays, such as vectors; this would define a vector
 * field over the independent variables. The data can be complex (two similar arrays for each variable), and the
 * independent variables can also be complex. </p><p> To use this data model one should think of the independent
 * variables as parameters describing the dependent variables. Thus, for an ordinary graph representing a function of
 * one variable there would be one independent variable and one dependent variable. But to describe a curve that is not
 * a single-valued function, such as a circle in the <i>x-y</i> plane, one would use two dependent variables (<i>x</i>
 * and <i>y</i>) and a single independent variable (distance along the curve). The first would be an instance of
 * VectorType, the second of Curve. A sphere in 3 dimensions would need 3 dependent variables (<i>x</i>, <i>y</i>, and
 * <i>z</i>) depending on two independent variables (the spherical coordinates on the sphere, perhaps). </p><p> Often
 * the independent variable in this data model is implicit; it could be just the index of the one-dimensional array of
 * points describing the function of one variable. GraphType does not require that all the values of the independent
 * variable be stored as doubles. Instead, it allows storage of a Triplet. The Triplet class contains three numbers: an
 * integer length, a double starting value and a double step. These can be used to generate the independent variable by
 * iteration. If the independent variable data are not uniformly spaced, however, then they must be stored in GraphType
 * as a double[] array. </p><p> GraphType stores its data in the Hashtable called <i>dataContainer</i> that is defined
 * in its super-class TrianaType. The keys used for the Hashtable are defined internally in GraphType, but they do not
 * need to be known to the user. The data are accessed by methods that assign an index to each data set, as if they were
 * stored in an ordered list. If imaginary parts do not exist for certain data then the associated elements are not
 * stored. Parameters defined in this class include arrays of flags and lengths. </p><p> GraphType communicates with
 * Units by implementing TrianaType, and with other programs (or computers) by implementing AsciiComm. Types derived
 * from GraphType should call these inherited functions first before implementing their own specific data transfer. The
 * key methods are <i>CopyMe</i>, <i>outputToStream</i>, and <i>inputFromStream</i>. These and the following methods
 * will normally be over-ridden in classes derived from GraphType by methods of the same name that extend their
 * functionality: <i>testDataModel</i>, <i>copyData</i>, <i>copyParameters</i>, and the methods demanded by the
 * Arithmetic Interface -- <i>isCompatible</i>, <i>equals</i>, <i>add</i>, <i>subtract</i>, <i>multiply</i>,
 * <i>divide</i>. Note that GraphType implements the methods of Arithmetic in such a way that they work on any data
 * array that holds numerical data, regardless of its dimension or size. It does this by invoking the utilities of
 * FlatArray. If a GraphType data object contains non-numerical data, such as images, these are ignored by the
 * arithmetic operations. </p><p>
 *
 * @author Bernard Schutz
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @see TrianaType
 * @see Arithmetic
 * @see triana.types.util.Triplet
 * @see VectorType
 * @see MatrixType
 * @see Curve
 */
public abstract class GraphType extends TrianaType implements Arithmetic, AsciiComm {

    /*
     * First define parameters. It is good programming practice to
     * put all these definitions first, since these parameters may
     * need to be known to programmers of derived data types.
     */

    /**
     * Parameter <i>independentVariables</i> is an int that defines the number of independent variables that describe
     * the object. This is essentially the dimension of the graph of the object. Thus, a sphere in three dimensions has
     * <i>independentVariables</i> = 2, since the postion of any point on the sphere is a function of only two
     * variables, e.g. the latitude and longitude. When the object is drawn, these variables may or may not correspond
     * to independent dimensions in the space in which the object is portrayed. </p><p> The Hashtable
     * <i>dataContainer</i> contains <i>dependentVariables</i> arrays, each of which has <i>independentVariables</i>
     * dimensions, which contain the data that describe the dependent variables; and <i>independentVariables</i> 1-D
     * arrays of doubles or Triplets that describe the spacing of the values of the independent variables at which the
     * data are represented. The independent variables must be given by doubles. The dependent variable arrays can have
     * any Object component type. </p><p> As a special case, it is allowed to have <i>independentVariables</i> = 0. This
     * represents a constant: the dependent variables are arrays consisting of only one element each. The type of the
     * elements are still arbitrary. </p><p> It is initialised to -1 as a signal for an empty data set.
     *
     * @see triana.types.util.Triplet
     */
    private int independentVariables = -1;

    /**
     * Parameter <i>dependentVariables</i> is an int that defines the number of dependent variables represented in the
     * data set. These could, for example, be the three coordinates (x,y,z) of a point on the surface of a sphere being
     * drawn in 3 dimensions. They are functions of the independent variables, such as the latitude and longitude. There
     * must be <i>dependentVariables</i> such arrays, each with <i>independentVariables</i> dimensions, and their
     * components can be any Objects: doubles, arrays, colours, etc. Of course, only objects that a graphing unit can
     * understand will be graphed. </p><p> It is initialised to -1 as a signal for an empty data set.
     */
    private int dependentVariables = -1;

    /**
     * Parameter array <i>triplet</i> is a boolean[] that carries information about whether the data associated with a
     * particular independent variable are represented by a Triplet, which is an object that contains three numbers from
     * which one can generate a uniformly spaced one-dimensional data set: length, start, and step.  The value
     * <i>true</i> indicates that the data Object is a Triplet; <i>false</i> that it is not, ie that it is either a full
     * one-dimensional array. If the data are complex, then they cannot be represented by a Triplet. </p><p> The array
     * <i>triplet</i> must be dimensioned to have at least <i>independentVariables</i> elements in the data object
     * constructors.
     *
     * @see triana.types.util.Triplet
     */
    private boolean[] triplet;

    /**
     * Parameter array <i>independentComplex</i> is a boolean[] that carries information about whether the data
     * associated with a particular independent variable are real or complex. Complex data are represented by two real
     * arrays of identical structure. The array <i>independentComplex</i> must be dimensioned to have at least
     * <i>independentVariables</i> elements in the data object constructors. </p><p> Parameter array
     * <i>dependentComplex</i> similarly carries information about whether the data associated with a particular
     * dependent variable are real or complex. This only makes sense if the data component type is a numerical one;
     * otherwise this must be <i>false</i>. The array <i>dependentComplex</i> must be dimensioned to have at least
     * <i>dependentVariables</i> elements in the data object constructors.
     */
    private boolean[] independentComplex;
    private boolean[] dependentComplex;

    /**
     * Parameter array <i>dimensionLengths</i> is an int[] that contains the length of the dimension of each independent
     * variable (i.e. the number of data elements in that direction) in the data array. The number of elements in
     * <i>dimensionLengths</i> is <i>independentVariables</i>.
     */
    private int[] dimensionLengths;

    /**
     * Parameter array <i>dependentVariableDimensions</i> is an ArrayList that contains int[]'s that give the lengths of
     * the dimension of each independent variable (i.e. the number of data elements in that direction) in the data
     * array. The number of elements in <i>dependentVariableDimensions</i> is <i>dependentVariables</i>. The length of
     * the int[] in each element is <i>independentVariables</i>.
     */
    private ArrayList dependentVariableDimensions;

    /**
     * Parameter array <i>dataArrayTypeNames</i> is an array of Strings that give the type names of each of the
     * dependent variable arrays. This array must be dimensioned to have at least <i>dependentVariables</i> dimensions
     * in data object constructors.
     */
    private String[] dataArrayTypeNames;

    /**
     * Parameter <i>title</i> is a String that contains the title that should be displayed by a graph of the data.
     */
    private String title;

    /**
     * Parameter <i>labels</i> is a Hashtable containing text labels that describe the data held in dataContainer. They
     * should be short enough to be used for the axes of a graph. The keys to the Hashtable are the Strings in the
     * String arrays <i>independentNames</i> and <i>dependentNames</i>, which also key the data arrays in
     * <i>dataContainer</i>. However, since the real and imaginary parts of the data should have the same basic name,
     * there are only <i>independentVariables</i> + <i>dependentVariables</i> labels, and they are keyed by the Strings
     * for the real parts of the respective data variables (<i>i.e.</i> even-numbered elements of
     * <i>independentNames</i> and <i>dependentNames</i>). Graphing/display methods that present complex data should
     * append appropriate modifiers to the basic label, such as "(real)", "(imag)", "(magnitude)", or "(phase)".
     */
    private Hashtable labels;

    /**
     * Parameters <i>independentNames</i> and <i>dependentNames</i> are String[] arrays consisting of Strings that will
     * be used as keys into the Hashtables <i>dataContainer</i> and <i>labels</i>. The keys are Strings of the form  "IV
     * 0 real", "IV 0 imag", "IV 1 real", ..., "DV 0 real", etc, where "IV" denotes the independent variables and "DV"
     * the dependent variables. There are 2 * <i>independentVariables</i> + 2 * <i>dependentVariables</i> elements in
     * this array. In this way the methods that access this data can access it as if it were an array with components 0,
     * 1, 2, .., 2 * <i>independentVariables</i> + 2 * <i>dependentVariables</i> - 1, rather than a Hashtable. They use
     * the indices to get access the keys that are held in these arrays and then the access methods use these keys into
     * the Hashtable. This allows uniform data access even when the number of data sets is not known ahead of time,
     * which will be the case here, even though the number of dimensions is fixed in any data object, since data are
     * allowed to be real or complex. If the data were stored directly in a Vector or other list, then the position of,
     * say, the real part of the data for the 3rd axis would depend on whether the data for the first and second axes
     * were real or imaginary. This would complicate bookkeeping. By storing keys in these arrays, which contain a key
     * for each possible data set, and then using the keys in a Hashtable, the position of the actual data set in the
     * Hashtable is not needed.
     */
    private String[] independentNames;
    private String[] dependentNames;

    /*
     * Begin with Constructors.
     */

    /**
     * Creates a new GraphType that is empty.
     */
    public GraphType() {
        super();
    }

    /**
     * Creates a new GraphType with specified dimensions and an empty <i>dataContainer</i> hashtable. The data
     * containers are initialized with correct keys but null data. Data could be added later using the methods below.
     * Derived classes can call this Constructor and then fill in the data.
     *
     * @param iv The number of independent variables (must be positive)
     * @param dv The number of dependent variables (must be positive)
     */
    public GraphType(int iv, int dv) {
        this();
        setDimensions(iv, dv);
        setDefaultAxisLabelling();
    }


    /**
     * Added by I. Taylor, August 2001 : This function sets the default labeling scheme used for all GraphTypes. Default
     * values are 'X' for the X-axis and 'Y' for the Y axis.  The various subclasses can override this function with
     * their specific axis-labelling conventions.
     */
    public void setDefaultAxisLabelling() {
        String labelx = "X";
        String labely = "Y";
        setIndependentLabels(0, labelx);
        setDependentLabels(0, labely);
    }

    /**
     * Sets up the Graph Type to the specified dimensions and an empty <i>dataContainer</i> hashtable. The data
     * containers are initialized with correct keys but null data. Data could be added later using the methods below.
     * Derived classes can call this Constructor and then fill in the data.
     *
     * @param iv The number of independent variables (must be positive)
     * @param dv The number of dependent variables (must be positive)
     */
    public void setDimensions(int iv, int dv) {
        int j;
        independentVariables = iv;
        dependentVariables = dv;
        independentNames = new String[2 * iv];
        for (j = 0; j < iv; j++) {
            independentNames[2 * j] = "IV " + String.valueOf(j) + " real";
            independentNames[2 * j + 1] = "IV " + String.valueOf(j) + " imag";
        }
        dependentNames = new String[2 * dv];
        for (j = 0; j < dv; j++) {
            dependentNames[2 * j] = "DV " + String.valueOf(j) + " real";
            dependentNames[2 * j + 1] = "DV " + String.valueOf(j) + " imag";
        }
        independentComplex = new boolean[iv];
        dependentComplex = new boolean[dv];
        dimensionLengths = new int[iv];
        dependentVariableDimensions = new ArrayList(dv);
        triplet = new boolean[iv];
        dataArrayTypeNames = new String[dv];
        labels = new Hashtable(iv + dv);
        setDataContainer(new Hashtable(2 * iv + 2 * dv));
        for (j = 0; j < dv; ++j) {
            dependentVariableDimensions.add(null);
        }
    }


    /*
     * Instance methods.
     */

    /**
     * Tests to make sure this object obeys the GraphType data model. This should be overridden in classes derived from
     * GraphType to test for more restrictive assumptions. The overriding method should call this one <i>first</i>.
     * </p><p> This method returns <i>true</i> if the data sets exist,  if the independent variables are determined
     * either by Triplets or by one-dimensional arrays of doubles, if the dependent data arrays have
     * <i>independentVariables</i> dimensions, and if the lengths of the dimensions of the dependent variable arrays is
     * the same as the lengths of the independent variable data sets. Otherwise it returns <i>false</i>.
     *
     * @return boolean <i>True</i> if the current data satisfy the data model
     * @see triana.types.util.Triplet
     */
    public boolean testDataModel() {
        int dim, dv, j;
        int[] lr, li;
        Object or, oi;
        Class oClass;
        for (dim = 0; dim < independentVariables; dim++) {
            resetIndependent(dim);
            if (!triplet[dim]) {
                if (!(getIndependentArrayReal(dim) instanceof double[])) {
                    return false;
                }
                if (independentComplex[dim]) {
                    if (!(getIndependentArrayImag(dim) instanceof double[])) {
                        return false;
                    }
                    if (getIndependentArrayReal(dim).length != getIndependentArrayImag(dim).length) {
                        return false;
                    }
                }
            }
        }
        for (dv = 0; dv < dependentVariables; dv++) {
            resetDependent(dv);
            if (dependentComplex[dv]) {
                or = getDataArrayReal(dv);
                oClass = or.getClass();
                oi = getDataArrayImag(dv);
                if (!(oClass.isInstance(oi))) {
                    return false;
                }
                lr = FlatArray.findArrayDimensionLengths(or, independentVariables - 1);
                li = FlatArray.findArrayDimensionLengths(oi, independentVariables - 1);
                if (lr.length != li.length) {
                    return false;
                }
                for (j = 0; j < lr.length; j++) {
                    if (lr[j] != li[j]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Checks the independent data in the given dimension for consistency and resets all the boolean flags etc. This is
     * called by methods that change data sets.
     *
     * @param dim The dimension being reset
     */
    public void resetIndependent(int dim) {
        if (dataExists(independentNames[2 * dim + 1])) {
            independentComplex[dim] = true;
            triplet[dim] = false;
        } else {
            independentComplex[dim] = false;
            if (getFromContainer(independentNames[2 * dim]) instanceof Triplet) {
                triplet[dim] = true;
            } else {
                triplet[dim] = false;
            }
        }
        if (triplet[dim]) {
            dimensionLengths[dim] = getIndependentTriplet(dim).getLength();
        } else {
            dimensionLengths[dim] = getIndependentArrayReal(dim).length;
        }
        this.updateObsoletePointers();
    }

    /**
     * Checks the dependent data in the given dimension for consistency and resets all the boolean flags etc. This is
     * called by methods that change data sets. If the associated independent variables are Triplets before the call to
     * this method, and if their size is not consistent with the length of the new data, then they are automatically
     * reset to new Triplets running by default from index 0 with step 1. This is a fail-safe mechanism for programmers
     * who forget to reset the Triplet.
     *
     * @param dv The index of the dependent data being reset
     */
    public void resetDependent(int dv) {
        if (dataExists(dependentNames[2 * dv + 1])) {
            dependentComplex[dv] = true;
        } else {
            dependentComplex[dv] = false;
        }
        dataArrayTypeNames[dv] = getDataArrayReal(dv).getClass().getName();
        int[] length = FlatArray.findArrayDimensionLengths(getDataArrayReal(dv), independentVariables);
        if (dataExists(dependentNames[2 * dv])) {
            if (dependentVariableDimensions.size() > dv) {
                dependentVariableDimensions.set(dv, length);
            } else {
                dependentVariableDimensions.add(dv, length);
            }
        }
        for (int dim = 0; dim < length.length; dim++) {
            if (isTriplet(dim)) {
                if (getIndependentTriplet(dim).getLength() != length[dim]) {
                    setIndependentTriplet(length[dim], 0, 1, dim);
                }
            }
        }
        this.updateObsoletePointers();
    }


    /*
     * Now implement instance methods that test properties of the data.
     */


    /**
     * Returns <i>true</i> if the data associated with the independent variable <i>dim</i> is complex, <i>false</i>
     * otherwise. </p><p> If there is no data array (<i>i.e.</i> if the data are generated by a Triplet -- see the
     * method <i>isTriplet</i> below) then this returns <i>false</i>.
     *
     * @param dim The number indicating the variable in question
     * @return <i>True</i> if the data in this dimension are complex, <i>false</i> otherwise
     * @see triana.types.util.Triplet
     */
    public boolean isIndependentComplex(int dim) {
        return independentComplex[dim];
    }

    /**
     * Returns <i>true</i> if the data associated with the dependent variable <i>dv</i> is complex,
     * <i>false</i>otherwise.
     *
     * @param dv The number indicating the variable in question
     * @return <i>True</i> if the data in this dimension are complex, <i>false</i> otherwise
     */
    public boolean isDependentComplex(int dv) {
        return dependentComplex[dv];
    }


    /**
     * Returns <i>true</i> if (i) <i>isIndependentComplex</i> returns <i>false</i> for the same argument and (ii) the
     * independent variable numbered by the argument <i>dim</i> is generated from a Triplet (<i>length</i>,
     * <i>start</i>, <i>step</i>). The Triplet itself can be found using the method <i>getIndependentTriplet</i> below.
     *
     * @param dim The independent variable dimension
     * @return <i>True</i> if data in this dimension are represented by a Triplet
     * @see triana.types.util.Triplet
     */
    public boolean isTriplet(int dim) {
        return triplet[dim];
    }

    /**
     * Returns the type name of the data array (dependent variable) associated with the argument index <i>dv</i>. This
     * is the name of the full array.
     *
     * @param dv The dependent variable dimension
     * @return The component type name of the data in this dimension
     */
    public String getDataArrayTypeNames(int dv) {
        return dataArrayTypeNames[dv];
    }

    /**
     * Returns the Class of the data array (dependent variable) associated with the argument index <i>dv</i>.  Returns
     * null if the Class does not exist.
     *
     * @param dv The dependent variable dimension
     * @return The Class of the data in this dimension
     */
    public Class getDataArrayClass(int dv) {
        try {
            return Class.forName(dataArrayTypeNames[dv]);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    /**
     * Returns <i>true</i> if the dependent variable associated with the argument index <i>dv</i> is an array of
     * primitive Java data types. It returns <i>false</i> if the elements of the array at its <i>lowest</i> level are
     * reference types.
     *
     * @param dv The index of the dependent variable being inspected
     * @return <i>True</i> if the elements at the lowest level are primitive.
     */
    public boolean isPrimitiveArray(int dv) {
        return FlatArray.isPrimitiveArray(getDataArrayReal(dv));
    }

    /**
     * Returns <i>true</i> if the dependent variable associated with the argument index <i>dv</i> is an array of
     * primitive Java data types at the level of independentVariables. This returns <i>false</i> if the components of
     * the data array after the first <i>independentVariables</i> dimensions are reference types, even if they are
     * arrays of primitives. </p><p> This can be used to determine if the data are to be interpreted as arrays of
     * arrays, such as vector fields.
     *
     * @param dv The index of the dependent variable being inspected
     * @return <i>True</i> if the elements at the independentVariables level are primitive.
     */
    public boolean isPrimitiveData(int dv) {
        Object o = getDataArrayReal(dv);
        if (!FlatArray.isPrimitiveArray(o)) {
            return false;
        }
        if (FlatArray.findNumberOfDimensions(o) != independentVariables) {
            return false;
        }
        return true;
    }

    /**
     * Returns <i>true</i> if the dependent variable associated with the argument index <i>dv</i> is an array of
     * primitive Java data types for numerical data: byte, short, int, long, float, or double. It returns <i>false</i>
     * if the elements of the array at its <i>lowest</i> level are boolean, char, or reference types.
     */
    public boolean isArithmeticArray(int dv) {
        return FlatArray.isArithmeticArray(getDataArrayReal(dv));
    }

    /**
     * Returns <i>true</i> if the dependent variable associated with the argument index <i>dv</i> is an array of
     * primitive Java arithmetic data types at the level of <i>independentVariables</i>, i.e. not boolean or char. This
     * returns <i>false</i> if the components of the data array after the first <i>independentVariables</i> dimensions
     * are reference types or boolean or char, even if they are arrays of arithmetic primitives.
     * <p/>
     * This can be used to determine if the data are to be interpreted as arrays of arrays, such as vector fields.
     *
     * @param dv The index of the dependent variable being inspected
     * @return <i>True</i> if the elements at the independentVariables level are arithmetic primitive types.
     */
    public boolean isArithmeticData(int dv) {
        Object o = getDataArrayReal(dv);
        if (!FlatArray.isArithmeticArray(o)) {
            return false;
        }
        if (FlatArray.findNumberOfDimensions(o) != independentVariables) {
            return false;
        }
        return true;
    }

    /**
     * Tests to see if the independent variable in the given direction is uniformly sampled.
     *
     * @return boolean <i>True</i> if the given independent variable is sampled uniformly
     */
    public boolean isUniform(int dim) {
        boolean u = false;
        if (triplet[dim]) {
            u = true;
        } else {
            u = Triplet.testUniform(getIndependentArrayReal(dim));
            if (u && independentComplex[dim]) {
                u = Triplet.testUniform(getIndependentArrayImag(dim));
            }
        }
        return u;
    }

    /*
     * Implement instance methods for data retrieval.
     */

    /**
     * Returns the number of independent dimensions <i>independentVariables</i> of the data set.
     *
     * @return int Number of independent variables
     */
    public int getIndependentVariables() {
        return independentVariables;
    }

    /**
     * Returns the number of dependent dimensions <i>dependentVariables</i> of the data set.
     *
     * @return int Number of dependent variables
     */
    public int getDependentVariables() {
        return dependentVariables;
    }


    /**
     * Returns the number of domain points in the independent variable dimension given by argument <i>dim</i>.
     *
     * @param dim The number indicating the variable in question
     * @return The length of the independent variable dimension dim
     */
    public int getDimensionLengths(int dim) {
        return dimensionLengths[dim];
    }

    /**
     * Returns an integer array containing the number of points in each of the independent variable dimensions, numbered
     * 0 to <i>independentVariables</i> - 1.
     *
     * @return int[] An array containing the lengths of the independent variable dimensions
     */
    public int[] getDimensionLengths() {
        return dimensionLengths;
    }


    /**
     * Returns an int[] containing the lengths of the dimensions of the dependent variable given by the argument
     * <i>dv</i>.
     *
     * @param dv The dependent variable under consideration
     * @return The array of the lengths of the dimensions of the variable
     */
    public int[] getDependentVariableDimensions(int dv) {
        return (int[]) dependentVariableDimensions.get(dv);
    }


    /**
     * Returns the Triplet object which specifies how values of the independent variable indexed by <i>dim</i> are
     * generated. If the independent variable values are held in an array, the array is converted to a Triplet if its
     * values are uniformly spaced. If they are not uniform, then this method returns <i>null</i>.
     *
     * @param dim The spatial dimension
     * @return The Triplet Object from which uniformly spaced one-dimensional data can be generated
     * @see triana.types.util.Triplet
     */
    public Triplet getIndependentTriplet(int dim) {
        if (independentComplex[dim]) {
            return null;
        }
        if (triplet[dim]) {
            return (Triplet) getFromContainer(independentNames[2 * dim]);
        }
        if (isUniform(dim)) {
            return Triplet.convertToTriplet((double[]) getFromContainer(independentNames[2 * dim]));
        }
        return null;
    }

    /**
     * Returns the real part of the array for the independent variable indexed by <i>dim</i>. If the data are real
     * (<i>isIndependentComplex</i> returns <i>false</i>) then this returns the whole data. If method <i>isTriplet</i>
     * returns <i>true</i> (so that the data array does not exist) then this returns a data array generated by the
     * Triplet.
     *
     * @param dim The independent variable dimension
     * @return The values of (real part of) the independent variable
     * @see triana.types.util.Triplet
     */
    public double[] getIndependentArrayReal(int dim) {
        if (triplet[dim]) {
            return ((Triplet) getFromContainer(independentNames[2 * dim])).convertToArray();
        }
        return (double[]) getFromContainer(independentNames[2 * dim]);
    }

    /**
     * Returns an array containing the real part of the graphing scale associated with the independent variable indexed
     * by <i>dim</i>. By default this just returns <i>getIndependentArrayReal</i>, but it should be over-ridden in
     * derived types if the scale to be displayed on a graph is not stored in the independent variable. This can happen
     * if the "physical" values are not monotonic in the independent variable index, so they cannot be stored there.
     * <p/>
     * If the data are real (<i>isIndependentComplex</i> returns <i>false</i>) then this returns the whole scale.
     *
     * @param dim The independent variable dimension
     * @return The values of (real part of) the graphing scale for this variable
     */
    public double[] getIndependentScaleReal(int dim) {
        return getIndependentArrayReal(dim);
    }

    /**
     * Returns an array containing the logarithms (base 10) of the real part of the graphing scale values associated
     * with the dependent variable indexed by <i>dim</i>. This is an aid to producing logarithmically scaled graphs.
     * <p/>
     * Users should check to see that data are positive before calling this method (use
     * <i>minIndependentScalesReal</i>). The method uses the Java <i>log</i> function, which returns NaN on data values
     * that are negative, and negative infinity on data values that are zero.
     * <p/>
     * If the data are real (<i>isIndependentComplex</i> returns <i>false</i>) then this returns the logarithm of the
     * whole graphing data set.
     *
     * @param dim The dependent variable dimension
     * @return The logarithms (base 10) of the values of (real part of) the graphing scale associated with the given
     *         dependent variable
     */
    public double[] getIndependentScaleRealLog10(int dim) {
        double scaling = 1.0 / Math.log(10);
        double[] array = getIndependentScaleReal(dim);
        for (int j = 0; j < array.length; j++) {
            array[j] = Math.log(array[j]) * scaling;
        }
        return array;
    }

    /**
     * Returns the imaginary part of the array for the independent variable indexed by <i>dim</i>. If the data are real
     * (<i>isIndependentComplex()</i> returns <i>false</i>) then this returns <i>null</i>. If method <i>isTriplet()</i>
     * returns <i>true</i> (so that the data array does not exist) then this returns <i>null</i>.
     *
     * @param dim The independent variable dimension
     * @return The values of the imaginary part of the independent variable
     * @see triana.types.util.Triplet
     */
    public double[] getIndependentArrayImag(int dim) {
        if (triplet[dim]) {
            return null;
        }
        if (independentComplex[dim]) {
            return null;
        }
        return (double[]) getFromContainer(independentNames[2 * dim + 1]);
    }

    /**
     * Returns an array containing the imaginary part of the graphing scale associated with the independent variable
     * indexed by <i>dim</i>. By default this just returns <i>getIndependentArrayImag</i>, but it should be over-ridden
     * in derived types if the scale to be displayed on a graph is not stored in the independent variable. This can
     * happen if the "physical" values are not monotonic in the independent variable index, so they cannot be stored
     * there.
     * <p/>
     * If the data are real (<i>isIndependentComplex</i> returns <i>false</i>) then this returns <i>null</i>.
     *
     * @param dim The independent variable dimension
     * @return The values of the imaginary part of the graphing scale for this variable
     */
    public double[] getIndependentScaleImag(int dim) {
        return getIndependentArrayImag(dim);
    }

    /**
     * Returns an array containing the logarithms (base 10) of the imag part of the graphing scale values associated
     * with the dependent variable indexed by <i>dim</i>. This is an aid to producing logarithmically scaled graphs.
     * <p/>
     * Users should check to see that data are positive before calling this method (use
     * <i>minIndependentScalesImag</i>). The method uses the Java <i>log</i> function, which returns NaN on data values
     * that are negative, and negative infinity on data values that are zero.
     * <p/>
     * If the data are purely real (<i>isIndependentComplex</i> returns <i>false</i>) then this returns <i>null</i>.
     *
     * @param dim The dependent variable dimension
     * @return The logarithms (base 10) of the values of (imag part of) the graphing scale associated with the given
     *         dependent variable
     */
    public double[] getIndependentScaleImagLog10(int dim) {
        double scaling = 1.0 / Math.log(10);
        double[] array = getIndependentScaleImag(dim);
        if (array != null) {
            for (int j = 0; j < array.length; j++) {
                array[j] = Math.log(array[j]) * scaling;
            }
        }
        return array;
    }

    /**
     * Returns the real part of the array for the dependent variable indexed by <i>dv</i>. If the data are real
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data.
     *
     * @param dv The dependent variable dimension
     * @return The values of (real part of) the dependent variable
     */
    public Object getDataArrayReal(int dv) {
        return getFromContainer(dependentNames[2 * dv]);
    }

    /**
     * Returns the real part of the array for the dependent variable indexed by <i>dv</i>. If the data are real
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data. If the elements of the array
     * can be converted to doubles then they are, otherwise the method returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (real part of) the dependent variable
     */
    public Object getDataArrayRealAsDoubles(int dv) {
        return FlatArray.toDoubleArray(getFromContainer(dependentNames[2 * dv]));
    }

    /**
     * Returns the real part of the array for the dependent variable indexed by <i>dv</i>. If the data are real
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data. If the elements of the array
     * can be converted to floatss then they are, otherwise the method returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (real part of) the dependent variable
     */
    public Object getDataArrayRealAsFloats(int dv) {
        return FlatArray.toFloatArray(getFromContainer(dependentNames[2 * dv]));
    }

    /**
     * Returns the real part of the array for the dependent variable indexed by <i>dv</i>. If the data are real
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data. If the elements of the array
     * can be converted to ints then they are, otherwise the method returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (real part of) the dependent variable
     */
    public Object getDataArrayRealAsInts(int dv) {
        return FlatArray.toIntArray(getFromContainer(dependentNames[2 * dv]));
    }

    /**
     * Returns the real part of the array for the dependent variable indexed by <i>dv</i>. If the data are real
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data. If the elements of the array
     * can be converted to longs then they are, otherwise the method returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (real part of) the dependent variable
     */
    public Object getDataArrayRealAsLongs(int dv) {
        return FlatArray.toLongArray(getFromContainer(dependentNames[2 * dv]));
    }

    /**
     * Returns the real part of the array for the dependent variable indexed by <i>dv</i>. If the data are real
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data. If the elements of the array
     * can be converted to shorts then they are, otherwise the method returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (real part of) the dependent variable
     */
    public Object getDataArrayRealAsShorts(int dv) {
        return FlatArray.toShortArray(getFromContainer(dependentNames[2 * dv]));
    }

    /**
     * Returns the real part of the array for the dependent variable indexed by <i>dv</i>. If the data are real
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data. If the elements of the array
     * can be converted to bytess then they are, otherwise the method returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (real part of) the dependent variable
     */
    public Object getDataArrayRealAsBytes(int dv) {
        return FlatArray.toByteArray(getFromContainer(dependentNames[2 * dv]));
    }

    /**
     * Returns the real part of the array for the dependent variable indexed by <i>dv</i>; if the elements of the array
     * can be converted to the data type of the given Class (second argument) then they are; if the given Class is an
     * array, then the method tries to convert to the type of the elements of that array. Otherwise the method returns
     * <i>null</i>. If the data are real (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole
     * data.
     * <p/>
     * Note that Java provides Class constants for primitives, denoted by <i>Integer.Type, Double.Type, etc,</i>. These
     * values are also returned by applying the suffix .class to a primitive variable type name, <i>e.g.</i>
     * <i>int.class</i> has the value <i>Integer.Type</i>. For any Object <i>Q</i>, including arrays, the class is given
     * by the method <i>Q.getClass()</i>. Normally, the present method would be used to to get the data as an array of
     * elements of the same type as the elements of object <i>q</i> by invoking <i>getDataArrayReal( o, q.getClass()
     * )</i>.
     *
     * @param dv       The dependent variable dimension
     * @param outClass The desired type of the output elements
     * @return The values of (real part of) the dependent variable
     */
    public Object getDataArrayReal(int dv, Class outClass) {
        return FlatArray.convertArrayElements(getFromContainer(dependentNames[2 * dv]), outClass);
    }

    /**
     * Returns an array containing the real part of the graphing values for the dependent variable indexed by <i>dv</i>
     * as an array of doubles. By default this simply returns <i>getDataArrayReal</i>, but it should be over-ridden in
     * derived classes that arrange the data storage in a way that should not be graphed directly, for example if the
     * value along the graphing scale of the independent variable is not monotonic in the index of the data set.
     * <p/>
     * If the data are real (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole graphing data
     * set.
     *
     * @param dv The dependent variable dimension
     * @return The values of (real part of) the dependent variable
     */
    public Object getGraphArrayReal(int dv) {
        return getDataArrayRealAsDoubles(dv);
    }

    /**
     * Returns an array containing the logarithms (base 10) of the real part of the graphing values for the dependent
     * variable indexed by <i>dv</i>. This is an aid to producing logarithmically scaled graphs.
     * <p/>
     * Users should check to see that data are positive before calling this method (use <i>minDependentGraphReal</i>).
     * The method uses the Java <i>log</i> function, which returns NaN on data values that are negative, and negative
     * infinity on data values that are zero.
     * <p/>
     * If the data are real (<i>isDependentComplex</i> returns <i>false</i>) then this returns the logarithm of the
     * whole graphing data set.
     *
     * @param dv The dependent variable dimension
     * @return The values of (real part of) the dependent variable as an array of doubles
     */
    public Object getGraphArrayRealLog10(int dv) {
        double scaling = 1.0 / Math.log(10);
        FlatArray flat = new FlatArray(getGraphArrayReal(dv));
        double[] array = (double[]) flat.getFlatArray();
        for (int j = 0; j < flat.getFlatLength(); j++) {
            array[j] = Math.log(array[j]) * scaling;
        }
        return flat.restoreArray();
    }

    /**
     * Returns the imaginary part of the array for the dependent variable indexed by <i>dv</i>. If the data are real
     * (<i>isDependentComplex()</i> returns <i>false</i>) then this returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of the imaginary part of the dependent variable
     */
    public Object getDataArrayImag(int dv) {
        if (!dependentComplex[dv]) {
            return null;
        }
        return getFromContainer(dependentNames[2 * dv + 1]);
    }


    /**
     * Returns the imag part of the array for the dependent variable indexed by <i>dv</i>. If the data are imag
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data. If the elements of the array
     * can be converted to doubles then they are, otherwise the method returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (imag part of) the dependent variable
     */
    public Object getDataArrayImagAsDoubles(int dv) {
        return FlatArray.toDoubleArray(getFromContainer(dependentNames[2 * dv + 1]));
    }

    /**
     * Returns the imag part of the array for the dependent variable indexed by <i>dv</i>. If the data are imag
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data. If the elements of the array
     * can be converted to floatss then they are, otherwise the method returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (imag part of) the dependent variable
     */
    public Object getDataArrayImagAsFloats(int dv) {
        return FlatArray.toFloatArray(getFromContainer(dependentNames[2 * dv + 1]));
    }

    /**
     * Returns the imag part of the array for the dependent variable indexed by <i>dv</i>. If the data are imag
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data. If the elements of the array
     * can be converted to ints then they are, otherwise the method returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (imag part of) the dependent variable
     */
    public Object getDataArrayImagAsInts(int dv) {
        return FlatArray.toIntArray(getFromContainer(dependentNames[2 * dv + 1]));
    }

    /**
     * Returns the imag part of the array for the dependent variable indexed by <i>dv</i>. If the data are imag
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data. If the elements of the array
     * can be converted to longs then they are, otherwise the method returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (imag part of) the dependent variable
     */
    public Object getDataArrayImagAsLongs(int dv) {
        return FlatArray.toLongArray(getFromContainer(dependentNames[2 * dv + 1]));
    }

    /**
     * Returns the imag part of the array for the dependent variable indexed by <i>dv</i>. If the data are imag
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data. If the elements of the array
     * can be converted to shorts then they are, otherwise the method returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (imag part of) the dependent variable
     */
    public Object getDataArrayImagAsShorts(int dv) {
        return FlatArray.toShortArray(getFromContainer(dependentNames[2 * dv + 1]));
    }

    /**
     * Returns the imag part of the array for the dependent variable indexed by <i>dv</i>. If the data are imag
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data. If the elements of the array
     * can be converted to bytess then they are, otherwise the method returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (imag part of) the dependent variable
     */
    public Object getDataArrayImagAsBytes(int dv) {
        return FlatArray.toByteArray(getFromContainer(dependentNames[2 * dv + 1]));
    }

    /**
     * Returns the imag part of the array for the dependent variable indexed by <i>dv</i>. If the data are real
     * (<i>isDependentComplex</i> returns <i>false</i>) then this returns the whole data. If the elements of the array
     * can be converted to the data type of the given Class (second argument) then they are; if the given Class is an
     * array, then the method tries to convert to the type of the elements of that array. Otherwise the method returns
     * <i>null</i>.
     * <p/>
     * Note that Java provides Class constants for primitives, denoted by <i>Integer.Type, Double.Type, etc,</i>. These
     * values are also returned by applying the suffix .class to a primitive variable type name, <i>e.g.</i>
     * <i>int.class</i> has the value <i>Integer.Type</i>. For any Object <i>Q</i>, including arrays, the class is given
     * by the method <i>Q.getClass()</i>. Normally, the present method would be used to to get the data as an array of
     * elements of the same type as the elements of object <i>q</i> by invoking <i>getDataArrayImag( o, q.getClass()
     * )</i>.
     *
     * @param dv       The dependent variable dimension
     * @param outClass The desired type of the output elements
     * @return The values of (imag part of) the dependent variable
     */
    public Object getDataArrayImag(int dv, Class outClass) {
        return FlatArray.convertArrayElements(getFromContainer(dependentNames[2 * dv + 1]), outClass);
    }

    /**
     * Converts all of the arithmetic dependent data from their present format to double arrays.
     */
    public void convertDependentDataArraysToDoubles() {
        Object data;
        int dv;
        for (dv = 0; dv < dependentVariables; ++dv) {
            if (isArithmeticArray(dv)) {
                data = getDataArrayRealAsDoubles(dv);
                if (data != null) {
                    setDataArrayReal(data, dv);
                }
                if (isDependentComplex(dv)) {
                    data = getDataArrayImagAsDoubles(dv);
                    if (data != null) {
                        setDataArrayImag(data, dv);
                    }
                }
            }
        }
    }

    /**
     * Converts all of the arithmetic dependent data from their present format to float arrays.
     */
    public void convertDependentDataArraysToFloats() {
        Object data;
        int dv;
        for (dv = 0; dv < dependentVariables; ++dv) {
            if (isArithmeticArray(dv)) {
                data = getDataArrayRealAsFloats(dv);
                if (data != null) {
                    setDataArrayReal(data, dv);
                }
                if (isDependentComplex(dv)) {
                    data = getDataArrayImagAsFloats(dv);
                    if (data != null) {
                        setDataArrayImag(data, dv);
                    }
                }
            }
        }
    }

    /**
     * Converts all of the arithmetic dependent data from their present format to int arrays.
     */
    public void convertDependentDataArraysToInts() {
        Object data;
        int dv;
        for (dv = 0; dv < dependentVariables; ++dv) {
            if (isArithmeticArray(dv)) {
                data = getDataArrayRealAsInts(dv);
                if (data != null) {
                    setDataArrayReal(data, dv);
                }
                if (isDependentComplex(dv)) {
                    data = getDataArrayImagAsInts(dv);
                    if (data != null) {
                        setDataArrayImag(data, dv);
                    }
                }
            }
        }
    }

    /**
     * Converts all of the arithmetic dependent data from their present format to long arrays.
     */
    public void convertDependentDataArraysToLongs() {
        Object data;
        int dv;
        for (dv = 0; dv < dependentVariables; ++dv) {
            if (isArithmeticArray(dv)) {
                data = getDataArrayRealAsLongs(dv);
                if (data != null) {
                    setDataArrayReal(data, dv);
                }
                if (isDependentComplex(dv)) {
                    data = getDataArrayImagAsLongs(dv);
                    if (data != null) {
                        setDataArrayImag(data, dv);
                    }
                }
            }
        }
    }

    /**
     * Converts all of the arithmetic dependent data from their present format to short arrays.
     */
    public void convertDependentDataArraysToShorts() {
        Object data;
        int dv;
        for (dv = 0; dv < dependentVariables; ++dv) {
            if (isArithmeticArray(dv)) {
                data = getDataArrayRealAsShorts(dv);
                if (data != null) {
                    setDataArrayReal(data, dv);
                }
                if (isDependentComplex(dv)) {
                    data = getDataArrayImagAsShorts(dv);
                    if (data != null) {
                        setDataArrayImag(data, dv);
                    }
                }
            }
        }
    }

    /**
     * Converts all of the arithmetic dependent data from their present format to byte arrays.
     */
    public void convertDependentDataArraysToBytes() {
        Object data;
        int dv;
        for (dv = 0; dv < dependentVariables; ++dv) {
            if (isArithmeticArray(dv)) {
                data = getDataArrayRealAsBytes(dv);
                if (data != null) {
                    setDataArrayReal(data, dv);
                }
                if (isDependentComplex(dv)) {
                    data = getDataArrayImagAsBytes(dv);
                    if (data != null) {
                        setDataArrayImag(data, dv);
                    }
                }
            }
        }
    }

    /**
     * Returns an array containing the imaginary part of the graphing values for the dependent variable indexed by
     * <i>dv</i>. By default this simply returns <i>getDataArrayImag</i>, but it should be over-ridden in derived
     * classes that arrange the data storage in a way that should not be graphed directly, for example if the value
     * along the graphing scale of the independent variable is not monotonic in the index of the data set.
     * <p/>
     * If the data are real (<i>isDependentComplex</i> returns <i>false</i>) then this returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (real part of) the dependent variable
     */
    public Object getGraphArrayImag(int dv) {
        return getDataArrayImag(dv);
    }

    /**
     * Returns an array containing the logarithms (base 10) of the imag part of the graphing values for the dependent
     * variable indexed by <i>dv</i>. This is an aid to producing logarithmically scaled graphs.
     * <p/>
     * Users should check to see that data are positive before calling this method (use <i>minDependentGraphImag</i>).
     * The method uses the Java <i>log</i> function, which returns NaN on data values that are negative, and negative
     * infinity on data values that are zero.
     * <p/>
     * If the data are real (<i>isDependentComplex</i> returns <i>false</i>) then this returns <i>null</i>.
     *
     * @param dv The dependent variable dimension
     * @return The values of (imag part of) the dependent variable as an array of doubles
     */
    public Object getGraphArrayImagLog10(int dv) {
        double scaling = 1.0 / Math.log(10);
        Object graph = getGraphArrayImag(dv);
        if (graph != null) {
            FlatArray flat = new FlatArray(graph);
            double[] array = (double[]) flat.getFlatArray();
            for (int j = 0; j < flat.getFlatLength(); j++) {
                array[j] = Math.log(array[j]) * scaling;
            }
            return flat.restoreArray();
        }
        return null;
    }

    /**
     * Returns a String containing the title to be written on a displayed graph.
     *
     * @return String The title String for this data Object
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns a String containing the label to be displayed along the axis associated with the independent variable
     * <i>dim</i>. If the label has not been defined, an empty String is returned.
     *
     * @param dim The spatial dimension
     * @return The independent axis label for this dimension
     */
    public String getIndependentLabels(int dim) {
        if (labels.containsKey(independentNames[2 * dim])) {
            return (String) labels.get(independentNames[2 * dim]);
        }
        return "";
    }

    /**
     * Returns a String containing the label to be displayed along the axis associated with the dependent variable
     * <i>dv</i>. If the label has not been defined, an empty String is returned.
     *
     * @param dv The index of the dependent variable
     * @return The dependent variable axis label for this dimension
     */
    public String getDependentLabels(int dv) {
        if (labels.containsKey(dependentNames[2 * dv])) {
            return (String) labels.get(dependentNames[2 * dv]);
        }
        return "";
    }

    /**
     * Returns a Hashtable containing all the labels to be displayed along the axes of the independent and dependent
     * variables.
     *
     * @return Hashtable The axis labels
     */
    public Hashtable getLabels() {
        if (labels.isEmpty()) {
            return null;
        }
        return labels;
    }

    /**
     * Returns all the axis labels that have been defined, along with their keys. This is in the form of a String
     * consisting of <i>key \n label \n key \n label \n ...</i>: keys and labels alternating on separate lines. If no
     * labels have been defined, then the method returns an empty String.
     *
     * @return String The axis labels and keys
     */
    public String getLabelsColumn() {
        if (labels.isEmpty()) {
            return "";
        }
        Enumeration keys = labels.keys();
        String k;
        String s = "";
        while (keys.hasMoreElements()) {
            k = (String) keys.nextElement();
            s += k + "\n" + labels.get(k) + "\n";
        }
        return s;
    }

    /**
     * Returns a Hashtable copy of labels, by value, not by reference.
     *
     * @return Hashtable The copy of the axis labels
     */
    public Hashtable copyLabels() {
        int j;
        int totalDims = independentVariables + dependentVariables;
        Hashtable newlab = new Hashtable(totalDims);
        String k;
        if (labels.isEmpty()) {
            return newlab;
        }
        for (j = 0; j < independentVariables; j++) {
            k = independentNames[2 * j];
            if (labels.containsKey(k)) {
                newlab.put(k, Str.copy((String) labels.get(k)));
            }
        }
        for (j = 0; j < dependentVariables; j++) {
            k = dependentNames[2 * j];
            if (labels.containsKey(k)) {
                newlab.put(k, Str.copy((String) labels.get(k)));
            }
        }
        return newlab;
    }


    /* Now implement instance methods for data manipulation and replacement.
     * Note that there are no methods to change <i>independentVariables</i> and
     * <i>dependentVariables</i>: the data space dimensions of an instance of this
     * class or derived classes should be fixed for the lifetime of
     * that object.
     */

    /**
     * Sets the number of domain points in the independent variable dimension given by argument <i>dim</i> to the value
     * given by the argument <i>length</i>.
     *
     * @param length The number of values of this variable
     * @param dim    The number indicating the independent variable in question
     */
    public void setDimensionLengths(int length, int dim) {
        dimensionLengths[dim] = length;
    }

    /**
     * Sets the integer array containing the number of domain points in each of the independent variable dimensions to
     * the elements of the argument <i>dimLen</i>. The user is responsible for checking that it has the right number of
     * dimensions.
     *
     * @param dimLen An array containing the lengths of the independent variable dimensions
     */
    public void setDimensionLengths(int[] dimLen) {
        dimensionLengths = dimLen;
    }

    /**
     * Sets an int[] into the ArrayList containing the lengths of the dimensions of the dependent variable given by the
     * argument <i>dv</i>.
     *
     * @param size The array of the lengths of the dimensions of the variable
     * @param dv   The dependent variable under consideration
     */
    public void setDependentVariableDimensions(int[] size, int dv) {
        dependentVariableDimensions.set(dv, size);
    }


    /**
     * Sets the Triplet object which specifies how values of the independent variable indexed by <i>dim</i> to the
     * argument <i>tr</i>. If there had been a data array for this data then it is removed from the Hashtable, along
     * with its imaginary counterpart if it is complex. It sets the flag createTool by the method <i>isTriplet</i> to
     * <i>true</i> and it removes any imaginary data associated with this direction, setting the flag createTool by the
     * method <i>isIndependentComplex</i> flag to <i>false</i>.
     *
     * @param tr  The Triplet to be used to repesent data for this dimension
     * @param dim The independent variable dimension
     * @see triana.types.util.Triplet
     */
    public void setIndependentTriplet(Triplet tr, int dim) {
        boolean b;
        if (independentComplex[dim]) {
            b = deleteFromContainer(independentNames[dim * 2 + 1]);
        }
        insertIntoContainer(independentNames[dim * 2], tr);
        resetIndependent(dim);
    }

    /**
     * Sets the argument values into a new Triplet object and then sets this object to be the data object for uniformly
     * spaced values of the independent variable given by <i>dim</i>: length is given by the argument <i>len</i>,
     * starting value by the argument <i>strt</i>, and the step by the argument <i>stp</i>.  The method sets the flag
     * createTool by the method <i>isTriplet</i> to <i>true</i> and it removes any imaginary data associated with this
     * direction, setting the flag createTool by the method <i>isIndependentComplex</i> to <i>false</i>.
     *
     * @param len  The length of the data set to be represented by a Triplet
     * @param strt The starting value of the uniformly spaced data
     * @param stp  The step between the uniformly spaced data values
     * @param dim  The independent variable dimension
     * @see triana.types.util.Triplet
     */
    public void setIndependentTriplet(int len, double strt, double stp, int dim) {
        Triplet tr = new Triplet(len, strt, stp);
        boolean b;
        if (independentComplex[dim]) {
            b = deleteFromContainer(independentNames[dim * 2 + 1]);
        }
        insertIntoContainer(independentNames[dim * 2], tr);
        resetIndependent(dim);
    }

    /**
     * Assigns the argument array of doubles <i>A</i> to the storage location in this data object for the real part of
     * the data associated with independent variable <i>dim</i>. This method sets the flag createTool by the method
     * <i>isTriplet</i> for this dimension to <i>false</i>. If the array <i>A</i> is <i>null</i>, any existing data
     * array will be deleted from the Hashtable.
     *
     * @param A   The (real part of the) data to be stored in this independent variable
     * @param dim The spatial dimension
     * @see triana.types.util.Triplet
     */
    public void setIndependentArrayReal(double[] A, int dim) {
        boolean b;
        if (A == null) {
            b = deleteFromContainer(independentNames[dim * 2]);
        } else {
            insertIntoContainer(independentNames[dim * 2], A);
        }
        resetIndependent(dim);
    }

    /**
     * Assigns the argument array of doubles <i>A</i> to the storage location in this data object for the imaginary part
     * of the data associated with independent variable <i>dim</i>. This method sets the flag createTool by the method
     * <i>isTriplet</i> for this dimension to <i>false</i> and the flag createTool by <i>isIndependentComplex</i> to
     * <i>true</i>. If the array <i>A</i> is <i>null</i>, the stored data array will be deleted, losing any existing
     * data in this location.
     *
     * @param A   The (real part of the) data to be stored in this independent variable
     * @param dim The spatial dimension
     * @see triana.types.util.Triplet
     */
    public void setIndependentArrayImag(double[] A, int dim) {
        boolean b;
        if (A == null) {
            b = deleteFromContainer(independentNames[dim * 2 + 1]);
        } else {
            insertIntoContainer(independentNames[dim * 2 + 1], A);
        }
        resetIndependent(dim);
    }

    /**
     * Assigns the appropriately dimensioned argument array <i>A</i> to the storage location in this data object for the
     * real part of the data associated with dependent variable <i>dv</i>. The type specified for <i>A</i> is Object,
     * and it can be an array of any dimensionality and component type. To create the array, the user should make sure
     * it is consistent with the information supplied by methods like <i>getDimensionLengths</i> and
     * <i>getDataArrayTypeNames</i>. If the Object <i>A</i> is <i>null</i>, the data array will be deleted, losing any
     * existing data in this location. If it is not an array of some kind, then nothing is done. </p><p> Warning to
     * programmers: the independent variables associated with the array are not automatically reset when a new array is
     * stored. This is because it is not possible to know whether the independent variable should be a uniformly spaced
     * index starting at zero, or something more complicated. Therefore users should be sure that they call
     * <i>setIndependentTriplet</i> or <i>setIndependentArrayReal</i> if the new dependent array has a different size
     * from the previous one. However, to catch the most frequent case, the method <i>resetDependent</i> will reset the
     * independent variable in one situation, where the previous dependent variable was a Triplet and the new dependent
     * array has a size in that direction that is different from the length of the Triplet. Then the independent
     * variable is reset to a Triplet of the correct length, starting at zero with step 1.
     *
     * @param A  The (real part of the) data to be stored in this dependent variable
     * @param dv The dimension in the data space (dependent variable)
     */
    public void setDataArrayReal(Object A, int dv) {
        boolean b;
        if (A == null) {
            b = deleteFromContainer(dependentNames[dv * 2]);
        }
        if (A.getClass().isArray()) {
            insertIntoContainer(dependentNames[dv * 2], A);
            resetDependent(dv);
        }
    }

    /**
     * Assigns the appropriately dimensioned argument array <i>A</i> to the storage location in this data object for the
     * imaginary part of the data associated with independent variable <i>dv</i>. The type specified for <i>A</i> is
     * Object, and it can be an array of any dimensionality and component type. To create the array the user should make
     * sure it is consistent with the information supplied by  methods like <i>dimensionLengths()</i> and
     * <i>getDataArrayTypeNames()</i>. If the Object <i>A</i> is <i>null</i>, the data array will be deleted, losing any
     * existing data in this location. If it is not an array of some kind, then nothing is done. </p><p> Warning to
     * programmers: the independent variables associated with the array are not automatically reset when a new array is
     * stored. This is because it is not possible to know whether the independent variable should be a uniformly spaced
     * index starting at zero, or something more complicated. Therefore users should be sure that they call
     * <i>setIndependentTriplet</i> or <i>setIndependentArrayReal</i> if the new dependent array has a different size
     * from the previous one. However, to catch the most frequent case, the method <i>resetDependent</i> will reset the
     * independent variable in one situation, where the previous dependent variable was a Triplet and the new dependent
     * array has a size in that direction that is different from the length of the Triplet. Then the independent
     * variable is reset to a Triplet of the correct length, starting at zero with step 1.
     *
     * @param A  The imaginary part of the data to be stored in this dependent variable
     * @param dv The dimension of the data space (dependent variable)
     */
    public void setDataArrayImag(Object A, int dv) {
        boolean b;
        if (A == null) {
            b = deleteFromContainer(dependentNames[dv * 2 + 1]);
        } else if (A.getClass().isArray()) {
            insertIntoContainer(dependentNames[dv * 2 + 1], A);
            resetDependent(dv);
        }
    }


    /**
     * Sets the <i>title</i> String to the argument String <i>t</i> by reference.
     *
     * @param t The new graph title
     */
    public void setTitle(String t) {
        title = t;
    }

    /**
     * Appends the argument String <i>str</i> to the existing <i>title</i> String.
     *
     * @param str The String to be appended
     */
    public void addToTitle(String str) {
        if (title.equals("")) {
            title = str;
        } else {
            title += " | " + str;
        }
    }

    /**
     * Sets the axis label for the independent variable <i>dim</i> to the argument String <i>l</i>.
     *
     * @param dim The independent variable index
     * @param lbl The new axis label for this variable
     */
    public void setIndependentLabels(int dim, String lbl) {
        labels.put(independentNames[2 * dim], lbl);
        this.updateObsoletePointers();
    }

    /**
     * Sets the axis label for the dependent (data) variable <i>dim</i> to the argument String <i>l</i>.
     *
     * @param dv  The data variable index
     * @param lbl The new axis label for this variable
     */
    public void setDependentLabels(int dv, String lbl) {
        labels.put(dependentNames[2 * dv], lbl);
        this.updateObsoletePointers();
    }

    /**
     * Sets the axis labels Hashtable, by reference. Use method <i>copyLabels</i> to create a copy by value.
     *
     * @param newLab The new axis labels
     */
    public void setLabels(Hashtable newLab) {
        labels = newLab;
        this.updateObsoletePointers();
    }


    /* Now implement methods that should be overridden in derived
     * classes. The way in which they should be overridden in
     * indicated in the comments.
     */

    /**
     * This is one of the most important methods of Triana data. types. It returns a copy of the type invoking it. This
     * <b>must</b> be overridden for every derived data type derived. If not, the data cannot be copied to be given to
     * other units. Copying must be done by value, not by reference. </p><p> To override, the programmer should not
     * invoke the <i>super.copyMe</i> method. Instead, create an object of the current type and call methods
     * <i>copyData</i> and <i>copyParameters</i>. If these have been written correctly, then they will do the copying.
     * The code should createTool, for type YourType: <PRE> YourType y = null; try { y =
     * (YourType)getClass().newInstance(); y.copyData( this ); y.copyParameters( this ); y.setLegend( this.getLegend()
     * ); } catch (IllegalAccessException ee) { System.out.println("Illegal Access: " + ee.getMessage()); } catch
     * (InstantiationException ee) { System.out.println("Couldn't be instantiated: " + ee.getMessage()); } return y;
     * </PRE> </p><p> The copied object's data should be identical to the original. The method here modifies only one
     * item: a String indicating that the object was created as a copy is added to the <i>description</i> StringVector.
     *
     * @return TrianaType Copy by value of the current Object except for an updated <i>description</i>
     */
    public abstract TrianaType copyMe();

    /**
     * Copies data held in <i>dataContainer</i> from the argument object to the current object. The copying is by value,
     * not by reference. The implementation in GraphType copies all data arrays that are arrays of primitive Java data
     * types (doubles, ints, booleans, etc). The data representing the independent variable(s) are always of this type,
     * so they are copied by this implementation. Any derived classes that use dependent variable arrays of primitives
     * can rely on their being copied here. </p><p> However, a derived type that defines a dependent variable array of
     * reference types (eg colors, streams, hashtables, ...) MUST implement copy-by-value in its own over-riding method
     * <i>copyData</i>. The over-riding method should first invoke <PRE> super.copyData( source ) </PRE>and then proceed
     * with remaining copies.
     * <p/>
     * This method is protected so that it cannot be called except by objects that inherit from this one. It is called
     * by <i>copyMe</i>.
     *
     * @param source Object that contains the data to be copied.
     */
    protected void copyData(TrianaType source) {
        int j, len;
        double[] d, d1;
        Object o;
        Triplet t1;
        for (j = 0; j < independentVariables; j++) {
            if (((GraphType) source).isTriplet(j)) {
                t1 = ((GraphType) source).getIndependentTriplet(j).copy();
                setIndependentTriplet(t1, j);
            } else {
                d = ((GraphType) source).getIndependentArrayReal(j);
                len = d.length;
                d1 = new double[len];
                System.arraycopy(d, 0, d1, 0, len);
                setIndependentArrayReal(d1, j);
                if (((GraphType) source).isIndependentComplex(j)) {
                    d = ((GraphType) source).getIndependentArrayImag(j);
                    d1 = new double[len];
                    System.arraycopy(d, 0, d1, 0, len);
                    setIndependentArrayImag(d1, j);
                }
            }
        }
        for (j = 0; j < dependentVariables; j++) {
            if (((GraphType) source).isPrimitiveArray(j)) {
                o = ((GraphType) source).getDataArrayReal(j);
                setDataArrayReal(FlatArray.multiArrayCopy(o), j);
                if (((GraphType) source).isDependentComplex(j)) {
                    o = ((GraphType) source).getDataArrayImag(j);
                    setDataArrayImag(FlatArray.multiArrayCopy(o), j);
                }
            }
        }
    }


    /**
     * Copies modifiable parameters from the argument object to the current object. The copying is by value, not by
     * reference. Parameters are defined as data not held in <i>dataContainer</i>. They are modifiable if they have
     * <i>set...</i> methods. Parameters that cannot be modified, but which are set by constructors, should be placed
     * correctly into the copied object when it is constructed. Other parameters that do not have <i>set...</i> methods
     * are given values when data arrays are set, eg in the method <i>reset</i>.
     * <p/>
     * In GraphType, only the parameters <i>title</i> and <i>labels</i> need to be copied. The boolean arrays and the
     * arrays <i>dimensionLengths</i> and <i>dataArrayTypeNames</i> are generated automatically by <i>reset</i> when
     * data is copied by <i>copyData</i>. The dimensions and the StringVectors for dimension names are generated by the
     * default Constructor of subclass types. (Recall that GraphType is abstract so only subclass types are
     * instantiated.)
     * <p/>
     * This method must be overridden by any subclass that defines new parameters. The overriding method should invoke
     * its super method first, as below. It should use the <i>set...</i> and <i>get...</i> methods for the parameters in
     * question.
     * <p/>
     * This method is protected so that it cannot be called except by objects that inherit from this one. It is called
     * by <i>copyMe</i>.
     *
     * @param source Object that contains the data to be copied.
     */
    protected void copyParameters(TrianaType source) {
        super.copyParameters(source);
        String oldTitle = ((GraphType) source).getTitle();
        if (oldTitle != null) {
            setTitle(Str.copy(oldTitle));
        }
        setLabels(((GraphType) source).copyLabels());
    }

    /**
     * Used when Triana types want to be able to send ASCII data to other programs using strings.  This is used to
     * implement socket and to run other executables, written in C or other languages. With ASCII you don't have to
     * worry about ENDIAN'ness as the conversions are all done via text. This is obviously slower than binary
     * communication because you have to format the input and output within the other program and because ASCII data
     * representations are generally larger than binary ones. </p><p> This method takes care of the output of data held
     * in dataContainer in arrays of primitive Java data types (double, int, boolean, etc) and of parameters defined in
     * GraphType. It must be overridden in every subclass that defines new parameters or dependent variable arrays
     * holding reference types at their lowest level. The problem of representing such reference types as ASCII data is
     * left to the programmer! The overriding method should first call<PRE> super.outputToStream( dos ) </PRE>to get
     * output from superior classes, and then new parameters and/or arrays of reference values defined for the current
     * subclass must be output. </p><p> Unlike <i>copyData</i> and <i>copyParameters</i>, this method explicitly outputs
     * the boolean and other arrays that have no <i>set...</i> methods. Although this is not necessary for the arrays to
     * be reconstructed, it is useful because it allows the output stream to be written to a text editor for inspecting
     * all the contents of the object.
     * <p/>
     * Programmers must ensure that all parameters are written to output before the data arrays that they describe, so
     * that the input method can correctly dimension and createTool the data.
     *
     * @param dos The data output stream
     */
    public void outputToStream(PrintWriter dos) throws IOException {
        int dim, dv, k;
        dos.println(independentVariables);
        dos.println(dependentVariables);
        for (dim = 0; dim < independentVariables; dim++) {
            dos.println(independentComplex[dim]);
        }
        for (dv = 0; dv < dependentVariables; dv++) {
            dos.println(dependentComplex[dv]);
        }
        for (dim = 0; dim < independentVariables; dim++) {
            dos.println(triplet[dim]);
        }
        for (dim = 0; dim < independentVariables; dim++) {
            dos.println(dimensionLengths[dim]);
        }
        for (dv = 0; dv < dependentVariables; dv++) {
            dos.println(dataArrayTypeNames[dv]);
        }
        for (dim = 0; dim < 2 * independentVariables; dim++) {
            dos.println(independentNames[dim]);
        }
        for (dv = 0; dv < 2 * dependentVariables; dv++) {
            dos.println(dependentNames[dv]);
        }
        if (title != null) {
            dos.println(title);
        } else {
            dos.println("title string undefined");
        }
        dos.print(getLabelsColumn());
        dos.println("$$$");
        int[] l;
        int len;
        double[] d;
        Object o, o1;
        String type;
        FlatArray fl;
        for (dim = 0; dim < independentVariables; dim++) {
            if (triplet[dim]) {
                dos.print(getIndependentTriplet(dim).toAColumn());
            } else {
                d = getIndependentArrayReal(dim);
                dos.println(d.length);
                for (k = 0; k < d.length; k++) {
                    dos.println(d[k]);
                }
                if (independentComplex[dim]) {
                    d = getIndependentArrayImag(dim);
                    dos.println(d.length);
                    for (k = 0; k < d.length; k++) {
                        dos.println(d[k]);
                    }
                }
            }
        }
        for (dv = 0; dv < dependentVariables; dv++) {
            if (isPrimitiveArray(dv)) {
                dos.println("Primitive array");
                dos.println(dv);
                o = getDataArrayReal(dv);
                fl = new FlatArray(o);
                o1 = fl.getFlatArray();
                l = fl.getLengths();
                dos.println(l.length);
                for (k = 0; k < l.length; k++) {
                    dos.println(l[k]);
                }
                len = Array.getLength(o1);
                dos.println(len);
                type = dataArrayTypeNames[dv];
                if (type.endsWith("[B")) {
                    for (k = 0; k < len; k++) {
                        dos.println(Array.getByte(o1, k));
                    }
                } else if (type.endsWith("[C")) {
                    for (k = 0; k < len; k++) {
                        dos.println(Array.getChar(o1, k));
                    }
                } else if (type.endsWith("[D")) {
                    for (k = 0; k < len; k++) {
                        dos.println(Array.getDouble(o1, k));
                    }
                } else if (type.endsWith("[F")) {
                    for (k = 0; k < len; k++) {
                        dos.println(Array.getFloat(o1, k));
                    }
                } else if (type.endsWith("[I")) {
                    for (k = 0; k < len; k++) {
                        dos.println(Array.getInt(o1, k));
                    }
                } else if (type.endsWith("[J")) {
                    for (k = 0; k < len; k++) {
                        dos.println(Array.getLong(o1, k));
                    }
                } else if (type.endsWith("[S")) {
                    for (k = 0; k < len; k++) {
                        dos.println(Array.getShort(o1, k));
                    }
                } else if (type.endsWith("[Z")) {
                    for (k = 0; k < len; k++) {
                        dos.println(Array.getBoolean(o1, k));
                    }
                }
                if (dependentComplex[dv]) {
                    o = getDataArrayImag(dv);
                    fl = new FlatArray(o);
                    o1 = fl.getFlatArray();
                    if (type.endsWith("[B")) {
                        for (k = 0; k < len; k++) {
                            dos.println(Array.getByte(o1, k));
                        }
                    } else if (type.endsWith("[C")) {
                        for (k = 0; k < len; k++) {
                            dos.println(Array.getChar(o1, k));
                        }
                    } else if (type.endsWith("[D")) {
                        for (k = 0; k < len; k++) {
                            dos.println(Array.getDouble(o1, k));
                        }
                    } else if (type.endsWith("[F")) {
                        for (k = 0; k < len; k++) {
                            dos.println(Array.getFloat(o1, k));
                        }
                    } else if (type.endsWith("[I")) {
                        for (k = 0; k < len; k++) {
                            dos.println(Array.getInt(o1, k));
                        }
                    } else if (type.endsWith("[J")) {
                        for (k = 0; k < len; k++) {
                            dos.println(Array.getLong(o1, k));
                        }
                    } else if (type.endsWith("[S")) {
                        for (k = 0; k < len; k++) {
                            dos.println(Array.getShort(o1, k));
                        }
                    } else if (type.endsWith("[Z")) {
                        for (k = 0; k < len; k++) {
                            dos.println(Array.getBoolean(o1, k));
                        }
                    }
                }
            }
        }
        dos.println("$$$");
    }

    /**
     * Used when Triana types want to be able to create a new data object from ASCII data it receives from a stream.
     * This is used to implement socket and to run other executables, written in C or in other languages. With ASCII you
     * don't have to worry about ENDIAN'ness as the conversions are all done via text. This is obviously slower than
     * binary communication since you have to format the input and output and the ASCII representation of most data is
     * larger than the binary. </p><p> This method must be overridden in every subclass that defines new data or
     * parameters. The overriding method should first call<PRE> super.inputFromStream(dis) </PRE>to get input from
     * superior classes, and then new parameters defined for the current subclass must be input. Data arrays that
     * contain only primitive Java data types will be correctly input by the methods implemented here in GraphType, but
     * programmers who define dependent variable data arrays that contain reference types will have to take care of
     * ASCII I/O for these types themselves in the over-riding method. The over-riding method must be written to
     * createTool data that are in the format created by <i>outputToStream</i>.
     *
     * @param dis The data input stream
     */
    public void inputFromStream(BufferedReader dis) throws IOException {
        String s;

        independentVariables = Integer.valueOf(dis.readLine()).intValue();
        dependentVariables = Integer.valueOf(dis.readLine()).intValue();

        int dim, dv;
        for (dim = 0; dim < independentVariables; dim++) {
            independentComplex[dim] = Boolean.valueOf(dis.readLine()).booleanValue();
        }
        for (dv = 0; dv < dependentVariables; dv++) {
            dependentComplex[dv] = Boolean.valueOf(dis.readLine()).booleanValue();
        }
        for (dim = 0; dim < independentVariables; dim++) {
            triplet[dim] = Boolean.valueOf(dis.readLine()).booleanValue();
        }
        for (dim = 0; dim < independentVariables; dim++) {
            dimensionLengths[dim] = Integer.valueOf(dis.readLine()).intValue();
        }
        for (dv = 0; dv < dependentVariables; dv++) {
            dataArrayTypeNames[dv] = dis.readLine();
        }
        for (dim = 0; dim < 2 * independentVariables; dim++) {
            independentNames[dim] = dis.readLine();
        }
        for (dv = 0; dv < 2 * dependentVariables; dv++) {
            dependentNames[dv] = dis.readLine();
        }

        s = dis.readLine();
        if (s.equals("title string undefined")) {
            title = null;
        } else {
            title = s;
        }
        labels = new Hashtable(independentVariables + dependentVariables);
        String leg;
        String key = "";
        while (!(key = dis.readLine()).equals("$$$")) {
            leg = dis.readLine();
            labels.put(key, leg);
        }
        int k, len;
        double sta, stp;
        int[] l;
        double[] d;
        Object o1, oc1;
        FlatArray fl;
        String type;
        Class typeOfComponent;
        for (dim = 0; dim < independentVariables; dim++) {
            if (triplet[dim]) {
                len = Integer.valueOf(dis.readLine()).intValue();
                sta = Double.valueOf(dis.readLine()).doubleValue();
                stp = Double.valueOf(dis.readLine()).doubleValue();
                setIndependentTriplet(len, sta, stp, dim);
            } else {
                len = Integer.valueOf(dis.readLine()).intValue();
                d = new double[len];
                for (k = 0; k < len; k++) {
                    d[k] = Double.valueOf(dis.readLine()).doubleValue();
                }
                setIndependentArrayReal(d, dim);
                if (independentComplex[dim]) {
                    len = Integer.valueOf(dis.readLine()).intValue();
                    d = new double[len];
                    for (k = 0; k < len; k++) {
                        d[k] = Double.valueOf(dis.readLine()).doubleValue();
                    }
                    setIndependentArrayImag(d, dim);
                }
            }
        }
        while (dis.readLine().equals("Primitive array")) {
            dv = Integer.valueOf(dis.readLine()).intValue();
            len = Integer.valueOf(dis.readLine()).intValue();
            l = new int[len];
            for (k = 0; k < len; k++) {
                l[k] = Integer.valueOf(dis.readLine()).intValue();
            }
            len = Integer.valueOf(dis.readLine()).intValue();
            type = dataArrayTypeNames[dv];
            if (type.endsWith("[B")) {
                byte[] o = new byte[len];
                for (k = 0; k < len; k++) {
                    o[k] = Byte.valueOf(dis.readLine()).byteValue();
                }
                fl = new FlatArray(o, l);
                o1 = fl.restoreArray(true);
                setDataArrayReal(o1, dv);
                if (dependentComplex[dv]) {
                    byte[] oc = new byte[len];
                    for (k = 0; k < len; k++) {
                        oc[k] = Byte.valueOf(dis.readLine()).byteValue();
                    }
                    fl = new FlatArray(oc, l);
                    oc1 = fl.restoreArray(true);
                    setDataArrayImag(oc1, dv);
                }
                typeOfComponent = Byte.TYPE;
            } else if (type.endsWith("[C")) {
                char[] o = new char[len];
                for (k = 0; k < len; k++) {
                    o[k] = dis.readLine().charAt(0);
                }
                fl = new FlatArray(o, l);
                o1 = fl.restoreArray(true);
                setDataArrayReal(o1, dv);
                if (dependentComplex[dv]) {
                    char[] oc = new char[len];
                    for (k = 0; k < len; k++) {
                        oc[k] = dis.readLine().charAt(0);
                    }
                    fl = new FlatArray(oc, l);
                    oc1 = fl.restoreArray(true);
                    setDataArrayImag(oc1, dv);
                }
                typeOfComponent = Character.TYPE;
            } else if (type.endsWith("[D")) {
                double[] o = new double[len];
                for (k = 0; k < len; k++) {
                    o[k] = Double.valueOf(dis.readLine()).doubleValue();
                }
                fl = new FlatArray(o, l);
                o1 = fl.restoreArray(true);
                setDataArrayReal(o1, dv);
                if (dependentComplex[dv]) {
                    double[] oc = new double[len];
                    for (k = 0; k < len; k++) {
                        oc[k] = Double.valueOf(dis.readLine()).doubleValue();
                    }
                    fl = new FlatArray(oc, l);
                    oc1 = fl.restoreArray(true);
                    setDataArrayImag(oc1, dv);
                }
                typeOfComponent = Double.TYPE;
            } else if (type.endsWith("[F")) {
                float[] o = new float[len];
                for (k = 0; k < len; k++) {
                    o[k] = Float.valueOf(dis.readLine()).floatValue();
                }
                fl = new FlatArray(o, l);
                o1 = fl.restoreArray(true);
                setDataArrayReal(o1, dv);
                if (dependentComplex[dv]) {
                    float[] oc = new float[len];
                    for (k = 0; k < len; k++) {
                        oc[k] = Float.valueOf(dis.readLine()).floatValue();
                    }
                    fl = new FlatArray(oc, l);
                    oc1 = fl.restoreArray(true);
                    setDataArrayImag(oc1, dv);
                }
                typeOfComponent = Float.TYPE;
            } else if (type.endsWith("[I")) {
                int[] o = new int[len];
                for (k = 0; k < len; k++) {
                    o[k] = Integer.valueOf(dis.readLine()).intValue();
                }
                fl = new FlatArray(o, l);
                o1 = fl.restoreArray(true);
                setDataArrayReal(o1, dv);
                if (dependentComplex[dv]) {
                    int[] oc = new int[len];
                    for (k = 0; k < len; k++) {
                        oc[k] = Integer.valueOf(dis.readLine()).intValue();
                    }
                    fl = new FlatArray(oc, l);
                    oc1 = fl.restoreArray(true);
                    setDataArrayImag(oc1, dv);
                }
                typeOfComponent = Integer.TYPE;
            } else if (type.endsWith("[J")) {
                long[] o = new long[len];
                for (k = 0; k < len; k++) {
                    o[k] = Long.valueOf(dis.readLine()).longValue();
                }
                fl = new FlatArray(o, l);
                o1 = fl.restoreArray(true);
                setDataArrayReal(o1, dv);
                if (dependentComplex[dv]) {
                    long[] oc = new long[len];
                    for (k = 0; k < len; k++) {
                        oc[k] = Long.valueOf(dis.readLine()).longValue();
                    }
                    fl = new FlatArray(oc, l);
                    oc1 = fl.restoreArray(true);
                    setDataArrayImag(oc1, dv);
                }
                typeOfComponent = Long.TYPE;
            } else if (type.endsWith("[S")) {
                short[] o = new short[len];
                for (k = 0; k < len; k++) {
                    o[k] = Short.valueOf(dis.readLine()).shortValue();
                }
                fl = new FlatArray(o, l);
                o1 = fl.restoreArray(true);
                setDataArrayReal(o1, dv);
                if (dependentComplex[dv]) {
                    short[] oc = new short[len];
                    for (k = 0; k < len; k++) {
                        oc[k] = Short.valueOf(dis.readLine()).shortValue();
                    }
                    fl = new FlatArray(oc, l);
                    oc1 = fl.restoreArray(true);
                    setDataArrayImag(oc1, dv);
                }
                typeOfComponent = Short.TYPE;
            } else if (type.endsWith("[Z")) {
                boolean[] o = new boolean[len];
                for (k = 0; k < len; k++) {
                    o[k] = Boolean.valueOf(dis.readLine()).booleanValue();
                }
                fl = new FlatArray(o, l);
                o1 = fl.restoreArray(true);
                setDataArrayReal(o1, dv);
                if (dependentComplex[dv]) {
                    boolean[] oc = new boolean[len];
                    for (k = 0; k < len; k++) {
                        oc[k] = Boolean.valueOf(dis.readLine()).booleanValue();
                    }
                    fl = new FlatArray(oc, l);
                    oc1 = fl.restoreArray(true);
                    setDataArrayImag(oc1, dv);
                }
                typeOfComponent = Boolean.TYPE;
            }
        }
    }


    /*
    * Now give a number of methods that can be used by graphing
    * objects and other Units to do simple transformations on
    * the data: rescaling and shifting, including complex
    * transformations.
    */

    /**
     * Returns a double[] containing the largest values of the elements of the real parts of all the independent
     * variables.
     *
     * @return double[] The maximum values of the real parts of the independent variables
     */
    public double[] maxIndependentVariablesReal() {
        Triplet t;
        double[] values;
        double[] max = new double[independentVariables];
        for (int dim = 0; dim < independentVariables; dim++) {
            if (triplet[dim]) {
                t = getIndependentTriplet(dim);
                if (t.getStep() > 0) {
                    max[dim] = t.getStart() + (t.getLength() - 1) * t.getStep();
                } else {
                    max[dim] = t.getStart();
                }
            } else {
                values = getIndependentArrayReal(dim);
                double mx = values[0];
                if (dimensionLengths[dim] > 1) {
                    for (int j = 1; j < dimensionLengths[dim]; j++) {
                        mx = Math.max(mx, values[j]);
                    }
                }
                max[dim] = mx;
            }
        }
        return max;
    }

    /**
     * Returns a double[] containing the largest values of the elements of the imaginary parts of all the independent
     * variables. It returns <i>NaN</i> for any data for which the imaginary part does not exist.
     *
     * @return double[] The maximum values of the imaginary parts of the independent variables
     */
    public double[] maxIndependentVariablesImag() {
        double[] values;
        double[] max = new double[independentVariables];
        for (int dim = 0; dim < independentVariables; dim++) {
            if (!independentComplex[dim]) {
                max[dim] = Double.NaN;
            } else {
                values = getIndependentArrayImag(dim);
                double mn = values[0];
                if (dimensionLengths[dim] > 1) {
                    for (int j = 1; j < dimensionLengths[dim]; j++) {
                        mn = Math.max(mn, values[j]);
                    }
                }
                max[dim] = mn;
            }
        }
        return max;
    }

    /**
     * Returns a double[] containing the largest values on the graphing scales of all the independent variables.
     *
     * @return double[] The maximum values of the real parts of the graphing scales
     */
    public double[] maxIndependentScalesReal() {
        double[] values;
        double[] max = new double[independentVariables];
        for (int dim = 0; dim < independentVariables; dim++) {
            values = getIndependentScaleReal(dim);
            max[dim] = Math.max(values[0], values[dimensionLengths[dim] - 1]);
        }
        return max;
    }

    /**
     * Returns a double[] containing the largest values of the elements of the imaginary parts of the graphing scales of
     * all the independent variables. It returns <i>NaN</i> for any data for which the imaginary part does not exist.
     *
     * @return double[] The maximum values of the imaginary parts of the graphing scales
     */
    public double[] maxIndependentScalesImag() {
        double[] values;
        double[] max = new double[independentVariables];
        for (int dim = 0; dim < independentVariables; dim++) {
            if (!independentComplex[dim]) {
                max[dim] = Double.NaN;
            } else {
                values = getIndependentScaleImag(dim);
                max[dim] = Math.max(values[0], values[dimensionLengths[dim] - 1]);
            }
        }
        return max;
    }


    /**
     * Returns a double[] containing the smallest values of the elements of the real parts of all the independent
     * variables.
     *
     * @return double[] The minimum values of the independent variables
     */
    public double[] minIndependentVariablesReal() {
        Triplet t;
        double[] values;
        double[] min = new double[independentVariables];
        for (int dim = 0; dim < independentVariables; dim++) {
            if (triplet[dim]) {
                t = getIndependentTriplet(dim);
                if (t.getStep() < 0) {
                    min[dim] = t.getStart() + t.getLength() * t.getStep();
                } else {
                    min[dim] = t.getStart();
                }
            } else {
                values = getIndependentArrayReal(dim);
                min[dim] = Math.min(values[0], values[dimensionLengths[dim] - 1]);
            }
        }
        return min;
    }


    /**
     * Returns a double[] containing the smallest values elements of the imaginary parts of all the independent
     * variables. It returns <i>NaN</i> for any data for which the imaginary part does not exist.
     *
     * @return double[] The minimum values of the imaginary parts of the independent variables
     */
    public double[] minIndependentVariablesImag() {
        double[] values;
        double[] min = new double[independentVariables];
        for (int dim = 0; dim < independentVariables; dim++) {
            if (!independentComplex[dim]) {
                min[dim] = Double.NaN;
            } else {
                values = getIndependentArrayImag(dim);
                min[dim] = Math.min(values[0], values[dimensionLengths[dim] - 1]);
            }
        }
        return min;
    }


    /**
     * Returns a double[] containing the largest values on the graphing scales of all the independent variables.
     *
     * @return double[] The minimum values of the real parts of the graphing scales
     */
    public double[] minIndependentScalesReal() {
        double[] values;
        double[] min = new double[independentVariables];
        for (int dim = 0; dim < independentVariables; dim++) {
            values = getIndependentScaleReal(dim);
            min[dim] = Math.min(values[0], values[dimensionLengths[dim] - 1]);
        }
        return min;
    }

    /**
     * Returns a double[] containing the largest values of the elements of the imaginary parts of the graphing scales of
     * all the independent variables. It returns <i>NaN</i> for any data for which the imaginary part does not exist.
     *
     * @return double[] The minimum values of the imaginary parts of the graphing scales
     */
    public double[] minIndependentScalesImag() {
        double[] values;
        double[] min = new double[independentVariables];
        for (int dim = 0; dim < independentVariables; dim++) {
            if (!independentComplex[dim]) {
                min[dim] = Double.NaN;
            } else {
                values = getIndependentScaleImag(dim);
                min[dim] = Math.min(values[0], values[dimensionLengths[dim] - 1]);
            }
        }
        return min;
    }

    /**
     * Returns a double[] containing the maximum value of the real part of each of the dependent variables that is an
     * arithmetic data type, as determined by method <i>isArithmeticData</i>. The maximum is found over all elements of
     * the array. The numeric data are converted to doubles for output. If the data are not arithmetic, then <i>NaN</i>
     * is returned for that array.
     *
     * @return double[] The maximum values of the real parts of the arithmetic data arrays
     */
    public double[] maxDependentVariablesReal() {
        int dv;
        double[] max = new double[dependentVariables];
        for (dv = 0; dv < dependentVariables; dv++) {
            max[dv] = FlatArray.maxArray(getDataArrayReal(dv));
        }
        return max;
    }


    /**
     * Returns a double[] containing the maximum value of the imaginary part of each of the dependent variables that is
     * an arithmetic data type, as determined by method <i>isArithmeticData</i>. The maximum is found over all elements
     * of the array. The numeric data are converted to doubles for output. If the data are not arithmetic, or if the
     * data have no imaginary part, then <i>NaN</i> is returned for that array.
     *
     * @return double[] The maximum values of the imaginary parts of the arithmetic data arrays
     */
    public double[] maxDependentVariablesImag() {
        int dv;
        double[] max = new double[dependentVariables];
        for (dv = 0; dv < dependentVariables; dv++) {
            max[dv] = FlatArray.maxArray(getDataArrayImag(dv));
        }
        return max;
    }

    /**
     * Returns a double[] containing the maximum value of the real part of the graphing values associated with each of
     * the dependent variables that is an arithmetic data type, as determined by the method <i>isArithmeticData</i>. The
     * maximum is found over all elements of the array. The numeric data are converted to doubles for output. If the
     * data are not arithmetic, then <i>NaN</i> is returned for that array.
     *
     * @return double[] The maximum values of the real parts of the graphing values of the arithmetic data arrays
     */
    public double[] maxDependentGraphingValuesReal() {
        int dv;
        double[] max = new double[dependentVariables];
        for (dv = 0; dv < dependentVariables; dv++) {
            max[dv] = FlatArray.maxArray(getGraphArrayReal(dv));
        }
        return max;
    }


    /**
     * Returns a double[] containing the maximum value of the imaginary part of the graphing values associated with each
     * of the dependent variables that is an arithmetic data type, as determined by the method <i>isArithmeticData</i>.
     * The maximum is found over all elements of the array. The numeric data are converted to doubles for output. If the
     * data are not arithmetic, or if the data have no imaginary part, then <i>NaN</i> is returned for that array.
     *
     * @return double[] The maximum values of the imaginary parts of the graphing values of the arithmetic data arrays
     */
    public double[] maxDependentGraphingValuesImag() {
        int dv;
        double[] max = new double[dependentVariables];
        for (dv = 0; dv < dependentVariables; dv++) {
            max[dv] = FlatArray.maxArray(getGraphArrayImag(dv));
        }
        return max;
    }


    /**
     * Returns a double[] containing the minimum value of the real parts of each of the dependent variables that is an
     * arithmetic data type, as determined by method <i>isArithmeticData</i>. The minimum is found over all elements of
     * the array. The numeric data are converted to doubles for output. If the data are not arithmetic, then <i>NaN</i>
     * is returned for that array.
     *
     * @return double[] The minimum values of the real parts of the arithmetic data arrays
     */
    public double[] minDependentVariablesReal() {
        int dv;
        double[] min = new double[dependentVariables];
        for (dv = 0; dv < dependentVariables; dv++) {
            min[dv] = FlatArray.minArray(getDataArrayReal(dv));
        }
        return min;
    }


    /**
     * Returns a double[] containing the minimum value of the imaginary part of each of the dependent variables that is
     * an arithmetic data type, as determined by method <i>isArithmeticData</i>. The minimum is found over all elements
     * of the array. The numeric data are converted to doubles for output. If the data are not arithmetic, or if the
     * data have no imaginary part, then <i>NaN</i> is returned for that array.
     *
     * @return double[] The minimum values of the imaginary parts of the arithmetic data arrays
     */
    public double[] minDependentVariablesImag() {
        int dv;
        double[] min = new double[dependentVariables];
        for (dv = 0; dv < dependentVariables; dv++) {
            min[dv] = FlatArray.minArray(getDataArrayImag(dv));
        }
        return min;
    }


    /**
     * Returns a double[] containing the minimum value of the real part of the graphing values associated with each of
     * the dependent variables that is an arithmetic data type, as determined by the method <i>isArithmeticData</i>. The
     * minimum is found over all elements of the array. The numeric data are converted to doubles for output. If the
     * data are not arithmetic, then <i>NaN</i> is returned for that array.
     *
     * @return double[] The minimum values of the real parts of the graphing values of the arithmetic data arrays
     */
    public double[] minDependentGraphingValuesReal() {
        int dv;
        double[] min = new double[dependentVariables];
        for (dv = 0; dv < dependentVariables; dv++) {
            min[dv] = FlatArray.minArray(getGraphArrayReal(dv));
        }
        return min;
    }


    /**
     * Returns a double[] containing the minimum value of the imaginary part of the graphing values associated with each
     * of the dependent variables that is an arithmetic data type, as determined by the method <i>isArithmeticData</i>.
     * The minimum is found over all elements of the array. The numeric data are converted to doubles for output. If the
     * data are not arithmetic, or if the data have no imaginary part, then <i>NaN</i> is returned for that array.
     *
     * @return double[] The minimum values of the imaginary parts of the graphing values of the arithmetic data arrays
     */
    public double[] minDependentGraphingValuesImag() {
        int dv;
        double[] min = new double[dependentVariables];
        for (dv = 0; dv < dependentVariables; dv++) {
            min[dv] = FlatArray.minArray(getGraphArrayImag(dv));
        }
        return min;
    }

    /**
     * Returns a new GraphType object with data restricted to a subdomain of one of the independent variables.  In this
     * version of the method, the subdomain is defined by minimum and maximum index values of the given variable. The
     * dependent variable data arrays are modified so that they contain only the data on that subdomain. Data are copied
     * value to the new object using method <i>copyMe</i>, so they do not contain references to the old data.
     *
     * @param dim  The index of the independent variable being restricted
     * @param from The starting value of the new domain
     * @param to   The ending value of the new domain
     */
    public GraphType restrictToSubdomain(int dim, int from, int to) {
        int num = Math.abs(to - from + 1);
        GraphType newGraph = (GraphType) copyMe();
        if (triplet[dim]) {
            Triplet t = newGraph.getIndependentTriplet(dim);
            t.setStart(t.getStart() + from * t.getStep());
            t.setLength(num);
        } else {
            double[] var = newGraph.getIndependentArrayReal(dim);
            double[] newvar = new double[num];
            System.arraycopy(var, from, newvar, 0, num);
            newGraph.setIndependentArrayReal(newvar, dim);
            if (independentComplex[dim]) {
                var = newGraph.getIndependentArrayImag(dim);
                newvar = new double[num];
                System.arraycopy(var, from, newvar, 0, num);
                newGraph.setIndependentArrayImag(newvar, dim);
            }
        }
        Object o, o1;
        FlatArray fl;
        int dv, dimLen, dimRepeats, j, readPos, writePos;
        int[] l;
        if (dim == 0) {
            for (dv = 0; dv < dependentVariables; dv++) {
                o = newGraph.getDataArrayReal(dv);
                o1 = Array.newInstance(o.getClass().getComponentType(), Array.getLength(o));
                System.arraycopy(o, from, o1, 0, num);
                newGraph.setDataArrayReal(o1, dv);
                if (dependentComplex[dv]) {
                    o = newGraph.getDataArrayImag(dv);
                    o1 = Array.newInstance(o.getClass().getComponentType(), Array.getLength(o));
                    System.arraycopy(o, from, o1, 0, num);
                    newGraph.setDataArrayImag(o1, dv);
                }
            }
        } else {
            l = FlatArray.findArrayDimensionLengths(newGraph.getDataArrayReal(0), dim + 1);
            dimLen = l[dim];
            dimRepeats = 1;
            for (j = 0; j < l.length - 1; j++) {
                dimRepeats *= l[j];
            }
            for (dv = 0; dv < dependentVariables; dv++) {
                fl = new FlatArray(newGraph.getDataArrayReal(dv), dim + 1);
                o = fl.getFlatArray();
                o1 = Array.newInstance(o.getClass().getComponentType(), dimRepeats * num);
                writePos = 0;
                readPos = from;
                for (j = 0; j < dimRepeats; j++) {
                    System.arraycopy(o, readPos, o1, writePos, num);
                    readPos += dimLen;
                    writePos += num;
                }
                l[dim] = Array.getLength(o1);
                fl.setFlatArray(o1);
                fl.setLengths(l);
                newGraph.setDataArrayReal(fl.restoreArray(true), dv);
                if (dependentComplex[dv]) {
                    fl = new FlatArray(newGraph.getDataArrayImag(dv), dim + 1);
                    o = fl.getFlatArray();
                    o1 = Array.newInstance(o.getClass().getComponentType(), dimRepeats * num);
                    writePos = 0;
                    readPos = from;
                    for (j = 0; j < dimRepeats; j++) {
                        System.arraycopy(o, readPos, o1, writePos, num);
                        readPos += dimLen;
                        writePos += num;
                    }
                    l[dim] = Array.getLength(o1);
                    fl.setFlatArray(o1);
                    fl.setLengths(l);
                    newGraph.setDataArrayImag(fl.restoreArray(true), dv);
                }
            }
        }
        return newGraph;
    }


    /**
     * Returns a new GraphType object with data restricted to a subdomain of one of the independent variables. In this
     * version the subdomain is defined by the given minimum and maximum values of the real parts of the given
     * independent variable. In case these do not coincide with actual sampled values of the variable, the restriction
     * is performed by finding the largest subdomain of the independent variable that is contained in the given domain.
     * The dependent variable data arrays are modified so that they contain only the data on that subdomain. Data are
     * copied by value to the new object using <i>copyMe</i>, so they do not contain references to the old data. If
     * there is no value of the independent variable in the given subdomain, the method returns <i>null</i>.
     *
     * @param dim  The index of the independent variable being restricted
     * @param from The starting value of the new domain
     * @param to   The ending value of the new domain
     */
    public GraphType restrictToSubdomain(int dim, double from, double to) {
        int k;
        int newStartIndex = 0;
        int newEndIndex = 0;
        boolean ascending = true;
        double newStart, newEnd;
        double[] indVals;
        if (triplet[dim]) {
            Triplet t = getIndependentTriplet(dim);
            indVals = t.convertToArray();
            if (t.getStep() < 0) {
                ascending = false;
            }
        } else {
            indVals = getIndependentArrayReal(dim);
            if (indVals[0] > indVals[1]) {
                ascending = false;
            }
        }
        if (ascending) {
            if (indVals[0] == from) {
                newStart = from;
                newStartIndex = 0;
            } else {
                for (k = 0; indVals[k] < from; k++) {
                    ;
                }
                newStart = indVals[k + 1];
                newStartIndex = k + 1;
            }
            if (indVals[indVals.length - 1] == to) {
                newEnd = to;
                newEndIndex = indVals.length - 1;
            } else {
                for (k = indVals.length - 1; indVals[k] > to; k--) {
                    ;
                }
                newEnd = indVals[k - 1];
                newEndIndex = k - 1;
            }
        } else {
            if (indVals[indVals.length - 1] == from) {
                newEnd = from;
                newEndIndex = indVals.length - 1;
            } else {
                for (k = indVals.length - 1; indVals[k] < from; k--) {
                    ;
                }
                newEnd = indVals[k - 1];
                newEndIndex = k - 1;
            }
            if (indVals[0] == to) {
                newStart = to;
                newStartIndex = 0;
            } else {
                for (k = 0; indVals[k] > to; k++) {
                    ;
                }
                newEnd = indVals[k + 1];
                newEndIndex = k + 1;
            }
        }
        if (newStartIndex > newEndIndex) {
            return null;
        }
        return restrictToSubdomain(dim, newStartIndex, newEndIndex);
    }

    /*
     * Now implement the methods of the Arithmetic Interface.
     */


    /**
     * Tests the argument object to determine if it makes sense to perform arithmetic operations between it and the
     * current object. </p><p> In GraphType, this method tests that the given Object is either another GraphType or a
     * Const. If it is a GraphType Object, then the method tests that the number of independent dimensions and their
     * sizes are the same. It does not inspect the dependent data or check the values of the independent variables. If
     * the given Object is a Const, then it is automatically compatible. </p><p> Classes derived from this should
     * over-ride this method with further tests as appropriate. The over-riding method should normally have the first
     * lines <PRE> boolean test = super.isCompatible( obj ); </PRE>followed by other tests. If other types not
     * subclassed from GraphType or Const should be allowed to be compatible then other tests must be implemented.
     *
     * @param obj The data object to be compared with the current one
     * @return <I>True</I> if the object can be combined with the current one
     */
    public boolean isCompatible(Object obj) {
        if (obj instanceof Number) {
            return true;
        }
        if (obj instanceof GraphType) {
            if (independentVariables != ((GraphType) obj).getIndependentVariables()) {
                return false;
            }
            for (int dim = 0; dim < independentVariables; dim++) {
                if (dimensionLengths[dim] != ((GraphType) obj).getDimensionLengths(dim)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    /**
     * Tests if the argument Object is equal to the current object. </p><p> In GraphType, this method inspects the data
     * arrays and independent variables. The number of dependent variables and the values of the other parameters must
     * be equal. The independent data values must also be equal. The dependent data arrays are then inspected, and all
     * arrays of Java primitive data types are required to have equal elements. Arrays of non-primitive data types must
     * have the same type, but are not tested further for equality. </p><p> This method must be over-ridden in derived
     * types. In a derived type called xxx the method should begin <PRE> if ( !( obj instanceof xxx ) ) return false; if
     * ( !isCompatible( obj ) ) return false; </PRE>followed by tests that are specific to type xxx (testing its own
     * parameters) and then (if all tests have been passed) the last line should be <PRE> return super.equals( obj );
     * </PRE>This line invokes the other equals methods up the chain to GraphType. Each superior object tests its own
     * parameters. If subclasses implement dependent data that are not arrays of primitives, then they should implement
     * suitable tests of equality for these in this method.
     *
     * @param obj The data object to be compared with the current one
     * @return <I>True</I> if the object equals the current one
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof GraphType)) {
            return false;
        }
        GraphType other = (GraphType) obj;
        if (dependentVariables != other.getDependentVariables()) {
            return false;
        }
        int dim, dv;
        for (dim = 0; dim < independentVariables; dim++) {
            if (independentComplex[dim] != other.isIndependentComplex(dim)) {
                return false;
            }
            if (!getIndependentLabels(dim).equals(other.getIndependentLabels(dim))) {
                return false;
            }
        }
        for (dv = 0; dv < dependentVariables; dv++) {
            if (dependentComplex[dv] != other.isDependentComplex(dv)) {
                return false;
            }
            if (!dataArrayTypeNames[dv].equals(other.getDataArrayTypeNames(dv))) {
                return false;
            }
            if (!getDependentLabels(dv).equals(other.getDependentLabels(dv))) {
                return false;
            }
        }

        if ((title == null) && (other.getTitle() != null)) {
            return false;
        }
        if ((title != null) && (!title.equals(other.getTitle()))) {
            return false;
        }

        Triplet tr, to;
        double[] id, od;
        for (dim = 0; dim < independentVariables; dim++) {
            if (!independentComplex[dim]) {
                if (isUniform(dim)) {
                    if (!other.isUniform(dim)) {
                        return false;
                    }
                    tr = getIndependentTriplet(dim);
                    to = other.getIndependentTriplet(dim);
                    if (tr.getStart() != to.getStart()) {
                        return false;
                    }
                    if (tr.getStep() != to.getStep()) {
                        return false;
                    }
                    if (tr.getLength() != to.getLength()) {
                        return false;
                    }
                } else {
                    if (other.isUniform(dim)) {
                        return false;
                    }
                    id = getIndependentArrayReal(dim);
                    od = other.getIndependentArrayReal(dim);
                    if (!FlatArray.equalArrays(id, od)) {
                        return false;
                    }
                }
            } else {
                id = getIndependentArrayReal(dim);
                od = other.getIndependentArrayReal(dim);
                if (!FlatArray.equalArrays(id, od)) {
                    return false;
                }
                id = getIndependentArrayImag(dim);
                od = other.getIndependentArrayImag(dim);
                if (!FlatArray.equalArrays(id, od)) {
                    return false;
                }
            }
        }
        for (dv = 0; dv < dependentVariables; dv++) {
            if (isPrimitiveArray(dv)) {
                id = (double[]) getDataArrayReal(dv);
                od = (double[]) other.getDataArrayReal(dv);
                if (!FlatArray.equalArrays(id, od)) {
                    return false;
                }
                if (dependentComplex[dv]) {
                    id = (double[]) getDataArrayImag(dv);
                    od = (double[]) other.getDataArrayImag(dv);
                    if (!FlatArray.equalArrays(id, od)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Adds the argument TrianaType to the current object. Users should invoke method <i>isCompatible</i> to test
     * whether this operation makes sense. </p><p> In the GraphType method, addition is performed if the given Object is
     * a Const or another GraphType. In each case only data of the current object that are of an arithmetic nature are
     * added to. When the given object is a GraphType, the addition is done between corresponding data sets, <i>i.e.</i>
     * sets that have the same index when they are stored and retrieved. (If the given TrianaType passes the
     * <i>isCompatible</i> test, then these sets are guaranteed to exist.) When the given TrianaType is a Const, its
     * value is added to each element of all arithmetic data sets.
     *
     * @param obj The new data object to be added to the current one
     * @return the result
     */
    public Arithmetic add(Object obj) throws ClassCastException {
        GraphType result = (GraphType) copyMe();
        int dv;

        if (obj instanceof Number) {
            Const cst = new Const((Number) obj);

            double numR = cst.getReal();
            double numI = cst.getImag();

            for (dv = 0; dv < result.getDependentVariables(); dv++) {
                if (result.isArithmeticArray(dv)) {
                    if (result.isDependentComplex(dv)) {
                        FlatArray.incrementArray(result.getDataArrayReal(dv), result.getDataArrayImag(dv), numR, numI);
                    } else {
                        FlatArray.incrementArray(result.getDataArrayReal(dv), numR);
                    }
                }
            }
        } else if (obj instanceof GraphType) {
            for (dv = 0; dv < result.getDependentVariables(); dv++) {
                if (result.isArithmeticArray(dv)) {
                    FlatArray.addArrays(result.getDataArrayReal(dv), ((GraphType) obj).getDataArrayReal(dv));

                    if (result.isDependentComplex(dv)) {
                        if (((GraphType) obj).isDependentComplex(dv)) {
                            FlatArray.addArrays(result.getDataArrayImag(dv), ((GraphType) obj).getDataArrayImag(dv));
                        }
                    }
                }   // add more cases when we have made Spectrum and SampleSet complex types.
            }
        } else {
            throw (new ClassCastException(
                    "Invalid arithmetic: Cannot add " + getClass().getName() + " and " + obj.getClass().getName()));
        }

        return result;
    }


    /**
     * Subtracts the argument TrianaType from the current object. Users should invoke method <i>isCompatible</i> to test
     * whether this operation makes sense. </p><p> In the GraphType method, subtraction is performed if the given Object
     * is a Const or another GraphType. In each case only data of the current object that are of an arithmetic nature
     * are subtracted from. When the given object is a GraphType, the subtraction is done between corresponding data
     * sets, <i>i.e.</i> sets that have the same index when they are stored and retrieved. (If the given TrianaType
     * passes the <i>isCompatible</i> test, then these sets are guaranteed to exist.) When the given TrianaType is a
     * Const, its value is subtracted from each element of all arithmetic data sets.
     *
     * @param obj The new data object to be subtracted from the current one
     * @return the result
     */
    public Arithmetic subtract(Object obj) throws ClassCastException {
        GraphType result = (GraphType) copyMe();
        int dv;

        if (obj instanceof Number) {
            Const cst = new Const((Number) obj);

            double numR = cst.getReal();
            double numI = cst.getImag();

            for (dv = 0; dv < result.getDependentVariables(); dv++) {
                if (result.isArithmeticArray(dv)) {
                    if (result.isDependentComplex(dv)) {
                        FlatArray
                                .incrementArray(result.getDataArrayReal(dv), result.getDataArrayImag(dv), -numR, -numI);
                    } else {
                        FlatArray.incrementArray(result.getDataArrayReal(dv), -numR);
                    }
                }
            }
        } else if (obj instanceof GraphType) {
            for (dv = 0; dv < result.getDependentVariables(); dv++) {
                if (result.isArithmeticArray(dv)) {
                    FlatArray.subtractArrays(result.getDataArrayReal(dv), ((GraphType) obj).getDataArrayReal(dv));
                    if (result.isDependentComplex(dv)) {
                        if (((GraphType) obj).isDependentComplex(dv)) {
                            FlatArray.subtractArrays(result.getDataArrayImag(dv),
                                    ((GraphType) obj).getDataArrayImag(dv));
                        }
                    }
                }   // add more cases when we have made Spectrum and SampleSet complex types.
            }
        } else {
            throw (new ClassCastException(
                    "Invalid arithmetic: Cannot subtract " + getClass().getName() + " and " + obj.getClass()
                            .getName()));
        }

        return result;
    }


    /**
     * Multiplies the argument TrianaType into the current object. Users should invoke method <i>isCompatible</i> to
     * test whether this operation makes sense. </p><p> In the GraphType method, multiplication is performed if the
     * given Object is a Const or another GraphType. In each case only data of the current object that are of an
     * arithmetic nature are multiplied. When the given object is a GraphType, the multiplication is done between
     * corresponding data sets, <i>i.e.</i> sets that have the same index when they are stored and retrieved. (If the
     * given TrianaType passes the <i>isCompatible</i> test, then these sets are guaranteed to exist.) When the given
     * TrianaType is a Const, its value is multiplied into each element of all arithmetic data sets.
     *
     * @param obj The new data object to be multiplied into the current one
     * @return the result
     */
    public Arithmetic multiply(Object obj) throws ClassCastException {
        GraphType result = (GraphType) copyMe();
        int dv;

        if (obj instanceof Number) {
            Const cst = new Const((Number) obj);

            double numR = cst.getReal();
            double numI = cst.getImag();

            for (dv = 0; dv < result.getDependentVariables(); dv++) {
                if (result.isArithmeticArray(dv)) {
                    if (result.isDependentComplex(dv)) {
                        FlatArray.scaleArray(result.getDataArrayReal(dv), result.getDataArrayImag(dv), numR, numI);
                    } else {
                        FlatArray.scaleArray(result.getDataArrayReal(dv), numR);
                    }
                }
            }
        } else if (obj instanceof GraphType) {
            for (dv = 0; dv < result.getDependentVariables(); dv++) {
                if (result.isArithmeticArray(dv)) {
                    if ((result.isDependentComplex(dv)) || (((GraphType) obj).isDependentComplex(dv))) {
                        FlatArray.multiplyArrays(result.getDataArrayReal(dv), result.getDataArrayImag(dv),
                                ((GraphType) obj).getDataArrayReal(dv), ((GraphType) obj).getDataArrayImag(dv));
                    } else {
                        FlatArray.multiplyArrays(result.getDataArrayReal(dv), ((GraphType) obj).getDataArrayReal(dv));
                    }    // add more cases when we make Spectrum and SampleSet complex
                }
            }
        } else {
            throw (new ClassCastException(
                    "Invalid arithmetic: Cannot multiply " + getClass().getName() + " and " + obj.getClass()
                            .getName()));
        }

        return result;
    }


    /**
     * Divides the argument TrianaType into the current object. Users should invoke method <i>isCompatible</i> to test
     * whether this operation makes sense. </p><p> In the GraphType method, division is performed if the given Object is
     * a Const or another GraphType. In each case only data of the current object that are of an arithmetic nature are
     * divided. When the given object is a GraphType, the division is done between corresponding data sets, <i>i.e.</i>
     * sets that have the same index when they are stored and retrieved. (If the given TrianaType passes the
     * <i>isCompatible</i> test, then these sets are guaranteed to exist.) When the given TrianaType is a Const, its
     * value is divided into each element of all arithmetic data sets.
     *
     * @param obj The new data object to be divided into the current one
     * @return the result
     */
    public Arithmetic divide(Object obj) throws ClassCastException {
        GraphType result = (GraphType) copyMe();
        int dv;

        if (obj instanceof Const) {
            Const cst = new Const((Number) obj);

            double numR = cst.getReal();
            double numI = cst.getImag();

            double denom = numR * numR + numI * numI;
            double divR = numR / denom;
            double divI = -numI / denom;
            for (dv = 0; dv < result.getDependentVariables(); dv++) {
                if (result.isArithmeticArray(dv)) {
                    if (result.isDependentComplex(dv)) {
                        FlatArray.scaleArray(result.getDataArrayReal(dv), result.getDataArrayImag(dv), divR, divI);
                    } else {
                        FlatArray.scaleArray(result.getDataArrayReal(dv), 1.0 / numR);
                    }
                }
            }
        } else if (obj instanceof GraphType) {
            for (dv = 0; dv < result.getDependentVariables(); dv++) {
                if (result.isArithmeticArray(dv)) {
                    if ((result.isDependentComplex(dv)) || (((GraphType) obj).isDependentComplex(dv))) {
                        FlatArray.divideArrays(result.getDataArrayReal(dv), result.getDataArrayImag(dv),
                                ((GraphType) obj).getDataArrayReal(dv), ((GraphType) obj).getDataArrayImag(dv));
                    } else {
                        FlatArray.divideArrays(result.getDataArrayReal(dv), ((GraphType) obj).getDataArrayReal(dv));
                    }       // add more cases when we make Spectrum and SampleSet complex
                }
            }
        } else {
            throw (new ClassCastException(
                    "Invalid arithmetic: Cannot multiply " + getClass().getName() + " and " + obj.getClass()
                            .getName()));
        }

        return result;
    }


}









