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

import triana.types.image.PixelMap;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author      Melanie Rhianna Lewis
 * @author      Bernard Schutz
 * @created     28 August 2000
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class TrianaPixelMap extends TrianaType {

    /*
     * Local parameters
     */

    private boolean valid = false;
    private String mapName = "TrianaPixelMap";

    /**
     * Obsolete parameter kept for compatibility with older version
     * of this class. It is obsolete because the main data is now
     * stored in dataContainer, not as a public object.
     * Constructors and data setting methods keep this pointer
     * up to date with changes in the object held in dataContainer.
     */
    public PixelMap pixelMap;

    /**
     * Default constructor.
     */
    public TrianaPixelMap() {
        super();
    }

    /**
     * Constuctor using a PixelMap
     *
     * @param pixelMap The input PixelMap
     */
    public TrianaPixelMap(PixelMap pixelMap) {
        super();
        insertIntoContainer(mapName, pixelMap);
        valid = true;
        updateObsoletePointers();
    }

    /**
     * Constuctor using a TrianaPixelMap
     *
     * @param trianaPixelMap The input TrianaPixelMap
     */
    public TrianaPixelMap(TrianaPixelMap trianaPixelMap) {
        this(new PixelMap(trianaPixelMap.getPixelMap()));
    }

    /**
     * Constuctor using an Image
     *
     * @param image The input Image
     */
    public TrianaPixelMap(Image image) {
        this(new PixelMap(image));
    }

    /**
     * Constuctor using a TrianaImage
     *
     * @param trianaImage The input TrianaImage
     */
    public TrianaPixelMap(TrianaImage trianaImage) {
        this(trianaImage.getImage());
    }

    /**
     * This function is used by the Op class to check if the data
     * contained within the particular Triana Type is valid or not.
     * This check is dependant on the type of data the Triana type is
     * representing so it must be implemented within
     * each individual type.
     */
    public boolean isValid() {
        // don't care for now!
        return valid;
    }

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
    public TrianaType copyMe() {
        TrianaPixelMap t = null;
        try {
            t = (TrianaPixelMap) getClass().newInstance();
            t.copyData(this);
            t.copyParameters(this);
        }
        catch (IllegalAccessException ee) {
            System.out.println("Illegal Access: " + ee.getMessage());
        }
        catch (InstantiationException ee) {
            System.out.println("Couldn't be instantiated: " + ee.getMessage());
        }
        return t;
    }

    /**
     * Implement the abstract <i>copyData</i> method of TrianaType. This copies
     * an image from the given object to the present one.
     *
     * @param source The TrianaImage whose image is being copied
     */
    protected void copyData(TrianaType source) {
        setPixelMap(((TrianaPixelMap) source).getPixelMap());
    }

    /**
     * Over-ride the <i>copyParameters</i> method to copy the parameter <i>valid</i>.
     *
     * @param source The object from which the copy will be made
     */
    protected void copyParameters(TrianaType source) {
        super.copyParameters(source);
        valid = ((TrianaPixelMap) source).isValid();
    }

    /**
     * @return the PixelMap Object contained within this type.
     */
    public PixelMap getPixelMap() {
        return (PixelMap) getFromContainer(mapName);
    }

    /**
     * @return TrianaImage an Object containing the image described by this type.
     * @see TrianaImage
     */
    public TrianaImage getTrianaImage() {
        JPanel canvas = new JPanel();
        Image image = canvas.createImage(getPixelMap().getImageProducer());
        return new TrianaImage(image);
    }

    /**
     * Sets the given PixelMap into the current Object
     *
     * @param px the object to be inserted
     */
    public void setPixelMap(PixelMap px) {
        insertIntoContainer(mapName, px);
        updateObsoletePointers();
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
        pixelMap = (PixelMap) getFromContainer(mapName);
        super.updateObsoletePointers();
    }


}



