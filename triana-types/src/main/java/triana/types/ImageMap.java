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


/**
 * ImageMap contains a PixelMap image and a Key table that describes how a matrix of numbers was converted into the
 * image color values. See class PixelMap for details of the Triana color scale for these maps. ImageMap also contains
 * various parameters that govern how values from a matrix are mapped into colors.
 *
 * @author Bernard Schutz
 * @version $Revision: 4048 $
 */
public class ImageMap extends TrianaPixelMap {

    /*
     * Parameters defined for ImageMap
     */

    /*
     * Determines if values of the data higher than the value mapped
     * to blue will be displayed as white (if <i>true</i>) or black
     * (if <i>false</i>).
     */
    private boolean highWhite = false;

    /*
     * Determines if values of the data lower than the value mapped
     * to red will be displayed as white (if <i>true</i>) or black
     * (if <i>false</i>).
     */
    private boolean lowWhite = false;

    /*
     * Determines if values of the data are mapped to colors by
     * a logarithmic relation (if <i>true</i>) or linearly
     * (if <i>false</i>).
     */
    private boolean logScale = false;

    /*
     * Provides MatrixType information if the current ImageMap
     * came from a full MatrixType data set.
     */
    private MatrixType matrixTypeParent = null;


    /*
     * The name used by the Key table in the data hashtable of TrianaType.
     */
    private String keyTableName = "ImageMapKey";

    /**
     * Default constructor.
     */
    public ImageMap() {
        super();
    }

    /**
     * Constuctor using a matrix and parameters that govern the map.
     *
     * @param matrix      The input data matrix
     * @param toRed       The data value mapped to the lowest color (0)
     * @param toGreen     The data value mapped to the middle color (127)
     * @param toBlue      The data value mapped to the highest color (255)
     * @param loWhite     <i>true</i> if values below <i>toRed</i> are white, <i>false</i> if black
     * @param hiWhite     <i>true</i> if values above <i>toBlue</i> are white, <i>false</i> if black
     * @param logarithmic <i>true</i> if the scaling to color values is logarithmic, <i>false</i> if linear
     */
    public ImageMap(double[][] matrix, double toRed, double toGreen, double toBlue, boolean loWhite, boolean hiWhite,
                    boolean logarithmic) {
        // create empty ImageMap
        super();
        // check validity of logarithmic mapping option
        highWhite = hiWhite;
        lowWhite = loWhite;
        logScale = logarithmic;
        if ((toRed < 0) && logarithmic) {
            logScale = false;
            System.out.println(
                    "Tried to create ImageMap with logarithmic scaling, but lowest data value (for red) was negative. Use linear scaling.");
        }
        // set up boundary values between low and high parts of the map, and the slopes in this region
        double rs, gs, bs;
        if (logScale) {
            rs = Math.log(toRed);
            gs = Math.log(toGreen);
            bs = Math.log(toBlue);
        } else {
            rs = toRed;
            gs = toGreen;
            bs = toBlue;
        }
        double lowSlope = 127 / (gs - rs);
        double highSlope = 128 / (bs - gs);
        // create the matrix of Triana color values associated with the given data matrix
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] colors = new int[rows][cols];
        int lowColor = (lowWhite) ? 256 : -1;
        int highColor = (highWhite) ? 256 : -1;
        double x, xs;
        int c, j, k;
        for (j = 0; j < rows; j++) {
            for (k = 0; k < cols; k++) {
                x = matrix[j][k];
                if (x < toRed) {
                    c = lowColor;
                } else if (x > toBlue) {
                    c = highColor;
                } else {
                    xs = (logScale) ? Math.log(x) : x;
                    if (x < toGreen) {
                        c = (int) Math.round(lowSlope * (xs - rs));
                    } else {
                        c = 127 + (int) Math.round(highSlope * (xs - gs));
                    }
                }
                colors[j][k] = c;
            }
        }
        // create the image from the color values and put it into the container
        setPixelMap(new PixelMap(colors, PixelMap.TRIANA_COLOR_MAP));
        // create the key table
        double[] keyTable = new double[256];
        if (logScale) {
            double lowColorFactor = Math.exp(1. / lowSlope);
            double dataValue = toRed;
            for (j = 0; j < 128; j++) {
                keyTable[j] = dataValue;
                dataValue *= lowColorFactor;
            }
            double highColorFactor = Math.exp(1. / highSlope);
            dataValue = toGreen;
            for (j = 128; j < 256; j++) {
                dataValue *= highColorFactor;
                keyTable[j] = dataValue;
            }
        } else {
            double lowColorStep = 1. / lowSlope;
            double dataValue = toRed;
            for (j = 0; j < 128; j++) {
                keyTable[j] = dataValue;
                dataValue += lowColorStep;
            }
            double highColorStep = 1. / highSlope;
            dataValue = toGreen;
            for (j = 128; j < 256; j++) {
                dataValue += highColorStep;
                keyTable[j] = dataValue;
            }
        }
        // put the key table into the data container
        insertIntoContainer(keyTableName, keyTable);
    }

    /**
     * Constuctor using a MatrixType and parameters that govern the map. This should be the normal way to do create an
     * ImageMap in Triana. Only the real part of the data held in the MatrixType parent is used.
     *
     * @param parent      The input MatrixType whose data is used
     * @param toRed       The data value mapped to the lowest color (0)
     * @param toGreen     The data value mapped to the middle color (127)
     * @param toBlue      The data value mapped to the highest color (255)
     * @param loWhite     <i>true</i> if values below <i>toRed</i> are white, <i>false</i> if black
     * @param hiWhite     <i>true</i> if values above <i>toBlue</i> are white, <i>false</i> if black
     * @param logarithmic <i>true</i> if the scaling to color values is logarithmic, <i>false</i> if linear
     */
    public ImageMap(MatrixType parent, double toRed, double toGreen, double toBlue, boolean loWhite, boolean hiWhite,
                    boolean logarithmic) {
        this(parent.getDataReal(), toRed, toGreen, toBlue, loWhite, hiWhite, logarithmic);
        matrixTypeParent = parent;
    }


    /*
     * Various get and set methods
     */

    /**
     * Returns the key table that lists the values of the input data associated with each color
     *
     * @return double[] The values of the input data for each color
     */
    public double[] getColorKeyTable() {
        return (double[]) getFromContainer(keyTableName);
    }

    /**
     * Returns <i>true</i> if the data values above that mapped to Blue are mapped to white, <i>false</i> if black
     *
     * @return boolean <i>true</i> if the data values above Blue are White
     */
    public boolean isHighWhite() {
        return highWhite;
    }

    /**
     * Returns <i>true</i> if the data values below that mapped to Red are mapped to white, <i>false</i> if black
     *
     * @return boolean <i>true</i> if the data values below Red are White
     */
    public boolean isLowWhite() {
        return lowWhite;
    }

    /**
     * Returns <i>true</i> if the data values are mapped to colors logarithmically, <i>false</i> if linear
     *
     * @return boolean <i>true</i> if the data values are mapped to colors logarithmically
     */
    public boolean isLogScale() {
        return logScale;
    }

    /**
     * Sets the key table that lists the values of the input data associated with each color
     *
     * @param newKeyTable The values of the input data for each color
     */
    public void setColorKeyTable(double[] newKeyTable) {
        insertIntoContainer(keyTableName, newKeyTable);
    }

    /**
     * Return a reference to the MatrixType parent so that its graphing information can be used.
     *
     * @return MatrixType The parent object that provided the data
     */
    public MatrixType getParent() {
        return matrixTypeParent;
    }

    /**
     * Return information as to whether a parent MatrixType exists.
     *
     * @return boolean True if the present object was derived from a MatrixType
     */
    public boolean hasParent() {
        return (matrixTypeParent != null);
    }

    /*
     * Methods to inspect color key table
     */

    /**
     * Returns the Triana Color Map color value associated with a given data value. To convert this to rgb values, use
     * the class methods of class PixelMap, such as PixelMap.rgbOf(). The Triana Color Map is defined in PixelMap.
     *
     * @param data The data value
     * @return The Triana Color Map color value associated with the given data value
     */
    public int colorOf(double data) {
        double[] keyTable = getColorKeyTable();
        if (keyTable[0] > data) {
            return -1;
        }
        if (keyTable[0] == data) {
            return 0;
        }
        if (keyTable[255] < data) {
            return 256;
        }
        int j = 0;
        while (keyTable[j] < data) {
            j++;
        }
        return j - (int) Math.round((keyTable[j] - data) / (keyTable[j] - keyTable[j - 1]));
    }

    /**
     * Returns the data value associated with a particular color value. To obtain the color value from an rgb int, use
     * the class methods of class PixelMap, such as PixelMap.colorOf(). The Triana Color Map is defined in PixelMap.
     *
     * @param color The Triana Color Map color value
     * @return The data value associated with the given color value
     */
    public double dataFrom(int color) {
        double[] keyTable = getColorKeyTable();
        if (color < 0) {
            return keyTable[0];
        }
        if (color > 255) {
            return keyTable[255];
        }
        return keyTable[color];
    }


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
    public TrianaType copyMe() {
        ImageMap t = null;
        try {
            t = (ImageMap) getClass().newInstance();
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
     * Implement the abstract <i>copyData</i> method of TrianaType. This copies an image from the given object to the
     * present one.
     *
     * @param source The TrianaImage whose image is being copied
     */
    protected void copyData(TrianaType source) {
        super.copyData(source);
        if (source instanceof ImageMap) {
            setColorKeyTable(((ImageMap) source).getColorKeyTable());
        }
    }

    /**
     * Over-ride the <i>copyParameters</i> method to copy the parameter <i>valid</i>.
     *
     * @param source The object from which the copy will be made
     */
    protected void copyParameters(TrianaType source) {
        super.copyParameters(source);
        highWhite = ((ImageMap) source).isHighWhite();
        lowWhite = ((ImageMap) source).isLowWhite();
        logScale = ((ImageMap) source).isLogScale();
    }


}



