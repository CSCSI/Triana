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


import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * TrianaType is the root class for data types in Triana. It
 * is an abstract class that provides the basic data structures
 * but does not fill them. All working Triana data types must
 * be derived from TrianaType.
 * </p><p>
 * The main data objects in TrianaType are:<UL>
 * <LI> A Hashtable <i>dataContainer</i> which should be
 * used in derived classes to hold the main data, which could
 * consist of numerical arrays representing function values,
 * geometrical objects, images, and so on, or which could
 * consist of a text string (as in the Document data type).
 * <LI> A Vector of Strings (in a StringVector object)
 * called <i>description</i> that can carry information about the
 * data object, for example the time the object was created and
 * the OldUnit that created it, including the values of the OldUnit's
 * parameters when it processed the data.
 * <LI> A Journal (called <i>journal</i>) that records the <i>description</i> of the
 * current data object and the <i>journal</i>s
 * of all the data objects that were used to
 * create it. Thus, <i>journal</i> is a tree of the <i>description</i>s of all
 * the data objects whose data went into the present one.
 * In this way data objects can include their entire history
 * of generation, which can be useful when exchanging data between
 * collaborators or in verifying important results.
 * <LI> A String called <i>legend</i>, which can be used to identify the
 * data object in displays, such as in titles on graphs.
 * </UL>
 * </p><p>
 * TrianaType implements a number of methods for setting and examining
 * these data. In addition, the class defines two groups of methods
 * that <b>must</b>  be
 * implemented for each Triana data type. These include <UL>
 * <LI> Methods <i>copyMe</i>,
 * <i>copyData</i>, and <i>copyParameters</i>, which are vital for
 * passing data from one OldUnit to another, and
 * <LI> methods <i>inputFromStream</i> and <i>outputToStream</i>,
 * which perform ascii i/o that permits units to display their contents
 * and pass data to non-Java programs and between computing platforms that
 * have different binary representations. </UL>
 * </p><p>
 * TrianaType also implements a mechanism for allowing Units to attach data
 * to TrianaType data objects. These attachments are called ClipIns, and
 * the attachment holder is called <i>theClip</i>. See the ClipIn interface
 * for details of how these optional attachments work. TrianaType includes
 * a number of methods for attaching, inspecting, and deleting ClipIns.
 * </p><p>
 * <b>Type programmers note:</b> Please take note of this class and
 * implement the functions within it when creating new Triana types. For
 * example, the <i>copyMe</i> group of methods is essential
 * to the running of the units, for the reason given in the next paragraph.
 * </p><p>
 * <b>OldUnit programmers note:</b> To avoid memory problems, units should
 * normally modify an input data object and output it, rather than copy it.
 * If an output data object is to be
 * of the same type as an input object, then programmers can usually use
 * the <i>copyMe</i> method of the input data to create the new object and
 * then modify its data appropriately. This is easier than instantiating
 * a new object with its Constructors.
 *
 * @see GraphType
 * @see TrianaImage
 * @see Document
 * @see triana.types.util.Journal
 *
 * @author      Ian Taylor
 * @author      Bernard Schutz
 * @created     20 August 2000
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public abstract class TrianaType implements Serializable, SequenceInterface {

    /**
     * All triana types can be part of an ordered sequence.
     */
    private int sequenceNumber = -1;

    /**
     * A Hashtable called <i>dataContainer</i> is defined here
     * for holding any kind of data. This is declared here
     * but not allocated. Data types derived from TrianaType can
     * use this container or they can add their own specific data
     * objects. It is recommended to use the container and the
     * methods defined here in order to access and manipulate data
     * defined in derived data types.
     *
     * Normally types derived directly from TrianaType should allocate
     * <i>dataContainer</i>. If it is not allocated, then on the first
     * attempt to store data in it, it will be allocated a size of 6.
     * (See method <i>insertIntoContainer</i> below.)
     */
    private Hashtable dataContainer;


    /*
     * Basic Constructor.
     */

    /**
     * Creates an empty TrianaType, adds a message to the description
     * saying when it was created, and constructs the Journal from this
     * description. Most Units automatically add the <i>journal</i>s of input
     * data sets to the <i>journal</i> of an object they create, provided
     * the user elects this option.
     */
    protected TrianaType() {
    }

    /*
     * Methods to access and modify the Hashtable <i>dataContainer</i>.
     */

    /**
     * Initializes the <i>dataContainer</i> Hashtable.
     *
     * @param h Any Hashtable that may contain suitable data.
     */
    public void setDataContainer(Hashtable h) {
        dataContainer = h;
    }

    /**
     * Returns the <i>dataContainer</i> Hashtable.
     *
     * @return Hashtable containing the data.
     */
    public Hashtable getDataContainer() {
        return dataContainer;
    }

    /**
     * Returns an Enumeration containing
     * the keys identifying the contents of the <i>dataContainer</i> Hashtable.
     *
     * @return Enumeration containing the keys
     */
    public Enumeration inspectDataContainer() {
        return dataContainer.keys();
    }

    /**
     * Inserts the given Object <i>d</i> (second argument)
     * into the <i>dataContainer</i> Hashtable with
     * the argument Object <i>k</i> (first argument)
     * as a key. Normally this key will be
     * a String identifier, but it can in principle be any Object.
     *
     * @param k The key to the item being stored in the Hashtable
     * @param d The data item being stored
     */
    public void insertIntoContainer(Object k, Object d) {
        if (dataContainer == null) dataContainer = new Hashtable(6);
        if (dataContainer.containsKey(k)) // IT FIX : January 28th 001
            dataContainer.remove(k);
        dataContainer.put(k, d);
    }

    /**
     * Deletes the  element
     * from the <i>dataContainer</i> Hashtable that is associated
     * with the given key <i>k</i>.
     *
     * @param k The key of the object being deleted
     * @return boolean <i>True</i> if delete successful, <i>false</i> if container or key does not exit
     */
    public boolean deleteFromContainer(Object k) {
        if (dataContainer != null) {
            if (dataContainer.containsKey(k)) {
                dataContainer.remove(k);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the actual number of elements
     * stored in the <i>dataContainer</i> Hashtable.
     *
     * @return int The number of objects stored or 0 if <i>dataContainer</i> does not exist
     */
    public int containerSize() {
        if (dataContainer == null) return 0;
        if (dataContainer.isEmpty()) return 0;
        return dataContainer.size();
    }

    /**
     * Returns <i>true</i> if
     * the <i>dataContainer</i> Hashtable contains an object with the given
     * argument <i>k</i> as a key.
     *
     * @param k The key
     * @return <i>True</i> if the key exists, <i>false</i> if not
     */
    public boolean dataExists(Object k) {
        if (dataContainer == null) return false;
        return dataContainer.containsKey(k);
    }

    /**
     * Returns the Object from
     * the <i>dataContainer</i> associated with
     * the given argument key <i>k</i>.
     *
     * @param k The key to the element being sought
     * @return The data Object stored under that key, or <i>null</i> if none
     */
    public Object getFromContainer(Object k) {
        if (dataContainer == null) return null;
        Object o;
        if (dataContainer.containsKey(k))
            o = dataContainer.get(k);
        else
            o = null;
        return o;
    }


    /*
     * Now define a number of methods that must be over-ridden in
     * classes derived from this one.
     */

    /**
     * This is one of the most important methods of Triana data.
     * types. It returns a copy of the type invoking it. This <b>must</b>
     * be overridden for every derived data type derived. If not, the data
     * cannot be copied to be given to other units. Copying must be done by
     * value, not by reference.
     * </p><p>
     * To override, the programmer should not invoke the <i>super.copyMe</i> method.
     * Instead, create an object of the current type and call methods
     * <i>copyData</i> and <i>copyParameters</i>. If these have been written correctly,
     * then they will do the copying.  The code should read, for type YourType:
     * <PRE>
     *        YourType y = null;
     *        try {
     *            y = (YourType)getClass().newInstance();
     *	          y.copyData( this );
     *	          y.copyParameters( this );
     *            y.setLegend( this.getLegend() );
     *            }
     *        catch (IllegalAccessException ee) {
     *            System.out.println("Illegal Access: " + ee.getMessage());
     *            }
     *        catch (InstantiationException ee) {
     *            System.out.println("Couldn't be instantiated: " + ee.getMessage());
     *            }
     *        return y;
     * </PRE>
     * </p><p>
     * The copied object's data should be identical to the original. The
     * method here modifies only one item: a String indicating that the
     * object was created as a copy is added to the <i>description</i>
     * StringVector.
     *
     * @return TrianaType Copy by value of the current Object except for an
     updated <i>description</i>
     */
    public abstract TrianaType copyMe();

    /**
     * Copies data held in <i>dataContainer</i> from the argument object
     * to the current object. The copy must be by value, not by
     * reference.
     * </p><p>
     * This method must be overridden if
     * subclasses add more data to <i>dataContainer</i>, and the
     * overriding method should invoke its <i>super</i> method.
     * This method is called by <i>copyMe()</i>.
     *
     * @param source Object that contains the data to be copied.
     */
    protected void copyData(TrianaType source) {
    }

    /**
     * Copies modifiable parameters from the argument object
     * to the current object. The copying is by value, not by
     * reference. Parameters are defined as data not held in
     * <i>dataContainer</i>. They are modifiable if they have
     * <i>set...</i> methods. Parameters that cannot be modified, but
     * which are set by constructors, should be placed correctly
     * into the copied object when it is constructed.
     * </p><p>
     * In this method, the old description is not copied, since
     * it will turn up in the journal list (if history tracking
     * is turned on). A new description line is added to note
     * that this is a copy. Then the legend is copied.
     * </p><p>
     * This must be overridden by any subclass that defines new parameters.
     * The overriding method should invoke its super method. It should use
     * the </i>set...</i> and <i>get...</i> methods for the parameters in question.
     * This method is protected so that it cannot be called except by
     * objects that inherit from this one. It is called by <i>copyMe()</i>.
     *
     * @param source Object that contains the data to be copied.
     */
    protected void copyParameters(TrianaType source) {
    }

    /*
     * Used to make the new
     * types derived from TrianaType backward-compatible with
     * older types. It must be called by any method that
     * modifies data in <i>dataContainer</i> or in any other variable
     * that replaces a storage location used previously by any
     * type. It must be implemented (over-ridden) in any type
     * that re-defines storage or access methods to any
     * variable. The implementation should assign the
     * new variables to the obsolete ones, and ensure that
     * obsolete access methods retrieve data from the new
     * locations. Any over-riding method should finish
     * with the line<PRE>
     *       super.updateObsoletePointers;
     * </PRE>
     */
    protected void updateObsoletePointers() {
        // does nothing here
    }

    /**
     * @return the sequence number of this data type in the set. The default
     * value of -1 infers that this data set is not part of a sequence.
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @param sequenceNumber the identifying number of this data type in the sequence.
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

}



