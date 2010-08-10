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
package triana.types.image;

import java.awt.Image;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.Serializable;

/**
 * PixelMap is a class which represents the pixels within an image.</p><p> A pixel map may be created from an image, as
 * a blank map, as a copy of an existing pixel map, or from input related to a particular color image encoding.  Allowed
 * encodings are defined in this class, and are used for, eg, choosing the particular colors used in a color map
 * representation of numerical data. The class contains methods which allow setting the value of a pixel, retrieving its
 * value and flood filling areas of pixels.</p><p> Images are stored in an integer array. Each element (pixel) of the
 * array stores RGB information in different groups of bits: the lowest 8 bits contains blue, the next highest 8 bits
 * holds green, and the next highest 8 bits red. The highest 8 bits of each int are ignored.</p><p> The Triana Color Map
 * model is a one-dimensional list of 256 colors running from red (lowest) to blue (highest). They run through the
 * visible spectrum using rgb colors that are fully saturated and of medium brightness. The color model is designated by
 * the static integer TRIANA_COLOR_MAP and is defined by the static array of rgb values called
 * <i>trianaColorMapTable</i>.</p>
 *
 * @version $Revision: 4048 $
 */
public class PixelMap implements Serializable {

    /*
     * Public parameters of the class
     */

    public int width;
    public int height;
    public int[] pixels;

    /*
     * Define color map models with static initializers. These
     * are models that allow one to associate numerical data values
     * with colors.
     */

    /**
     * Defining index for the Triana Color Map model.
     */
    public static int TRIANA_COLOR_MAP = 0;

    /**
     * Table that defines the 256 rgb values of the Triana Color Map.
     */
    public static int[] trianaColorMapTable = new int[256];

    /*
     * Static initializer to set up the Triana Color Map table.
     */

    static {
        int j, red, green, blue, rgb, rgb0;
        //
        // The first 64 values have constant red and blue, increasing green.
        //
        red = 255;
        green = 0;
        blue = 0;
        rgb0 = 0xff000000 | (red << 16) | blue;
        for (j = 0; j < 64; j++) {
            rgb = rgb0 | (green << 8);
            trianaColorMapTable[j] = rgb;
            green += 4;
        }
        //
        // The next 64 values have constant green and blue, decreasing red.
        //
        red = 255;
        green = 255;
        blue = 0;
        rgb0 = 0xff000000 | (green << 8) | blue;
        for (j = 64; j < 128; j++) {
            rgb = rgb0 | (red << 16);
            trianaColorMapTable[j] = rgb;
            red -= 4;
        }
        //
        // The next 64 values have constant red and green, increasing blue.
        //
        red = 0;
        green = 255;
        blue = 0;
        rgb0 = 0xff000000 | (red << 16) | (green << 8);
        for (j = 128; j < 192; j++) {
            rgb = rgb0 | blue;
            trianaColorMapTable[j] = rgb;
            blue += 4;
        }
        //
        // The final 64 values have constant red and blue, decreasing green.
        //
        red = 0;
        green = 255;
        blue = 255;
        rgb0 = 0xff000000 | (red << 16) | blue;
        for (j = 192; j < 256; j++) {
            rgb = rgb0 | (green << 8);
            trianaColorMapTable[j] = rgb;
            green -= 4;
        }
    }

    public PixelMap() {
    }

    /**
     * Creates a PixelMap object of the given dimensions with the option of clearing the pixel map to all black pixels.
     * If it is not cleared, the initial map will be transparent black.
     *
     * @param width  The width of the map
     * @param height The height of the map
     * @param clear  <i>true</i> if the map is to be cleared to black.
     */

    public PixelMap(int width, int height, boolean clear) {
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];

        // System.out.println("CONSTRUCTOR: Width = " + width + " height " + height + " pixels " + pixels.length);

        if (clear) {
            clear();
            System.out.println("Clearing pixelmap...");
        }
    }

    /**
     * Creates a PixelMap object of the given dimensions and clears the pixel map to all black pixels.
     *
     * @param width  The width of the map
     * @param height The height of the map
     */

    public PixelMap(int width, int height) {
        this(width, height, true);
    }

    /**
     * Creates a PixelMap object of the same dimensions as the given image and tries to include the pixel information of
     * the image.  If unsuccessful the pixel map contains black pixels.
     *
     * @param image The given image to be made into the pixel map
     */

    public PixelMap(Image image) {
        this(image.getWidth(null), image.getHeight(null), false);

        PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0,
                width, height, pixels, 0, width);

        try {
            pg.grabPixels();
        }
        catch (Exception e) {
            clear();
        }
    }

    /**
     * Creates a PixelMap object which is a copy of the PixelMap object supplied.
     *
     * @param pixelmap The given PixelMap objet.
     */
    public PixelMap(PixelMap pixelmap) {
        this(pixelmap.width, pixelmap.height, false);
        System.arraycopy(pixelmap.pixels, 0, pixels, 0, pixels.length);
    }

    /**
     * Creates a PixelMap object from an input matrix of color values according to the given color map model, as defined
     * in this class.
     *
     * @param colorMatrix A matrix containing the color values of the model
     * @param model       An integer that designates which model is being used
     */
    public PixelMap(int[][] colorMatrix, int model) {
        this(colorMatrix[0].length, colorMatrix.length, false);
        if (model == TRIANA_COLOR_MAP) {
            int j, k, c, rgb;
            for (j = 0; j < height; j++) {
                for (k = 0; k < width; k++) {
                    c = colorMatrix[j][k];
                    if (c < 0) {
                        rgb = 0xff000000;
                    } else if (c > 255) {
                        rgb = 0xffffffff;
                    } else {
                        rgb = trianaColorMapTable[c];
                    }
                    pixels[k + j * width] = rgb;
                }
            }
        }
    }

    /**
     * uses the default writer to output this object to the Object
     * output stream.
     */
//    public void writeObject(ObjectOutputStream out) throws IOException {
//        out.defaultWriteObject();
//    }

    /**
     * uses the default writer to input and create an object from the Object
     * input stream.
     */
//    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//    }

    /**
     * Returns an ImageProducer for the data contained within the PixelMap.
     */

    public ImageProducer getImageProducer() {
        if (pixels.length != (width * height)) {
            int[] tmp = pixels;
            pixels = new int[width * height];
            clear();
            System.arraycopy(tmp, 0, pixels, 0, tmp.length);
        }
        MemoryImageSource mis = new MemoryImageSource(width, height, pixels, 0, width);
        return mis;
    }

    /**
     * Clears the pixel map to all black pixels.
     */

    public void clear() {
        int i;

        for (i = 0; i < pixels.length; i++) {
            pixels[i] = 0xff000000;
        }
    }

    /**
     * Returns the width of the pixel map in pixels.
     */

    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the pixel map in pixels.
     */

    public int getHeight() {
        return height;
    }

    /**
     * Returns the array of pixels.
     */

    public int[] getPixels() {
        return pixels;
    }

    /**
     * Returns the size of the pixel map.
     */

    public int getLength() {
        return pixels.length;
    }

    public int getPixel(int x, int y) {
        if ((x < 0) || (x >= width) || (y < 0) || (y > height)) {
            return -1;
        }

        return pixels[x + y * width];
    }

    public void setPixel(int x, int y, int value) {
        if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {
            pixels[x + y * width] = value;
        }
    }

    /**
     * Returns the rgb value of a given color in the Triana Color Map model.
     *
     * @param c The given color value on the Triana Color Map scale
     * @return int The rgb value that the given color value is mapped to
     */
    public int rgbOfTrianaColorMapColor(int c) {
        int rgb;
        if (c < 0) {
            rgb = 0xff000000;
        } else if (c > 255) {
            rgb = 0xffffffff;
        } else {
            rgb = trianaColorMapTable[c];
        }
        return rgb;
    }

    /**
     * Returns the Triana Color Map color value associated with a given rgb pixel value. If the rgb value is not
     * associated with any color value then this returns -2. If the rgb value is black, the method returns -1. If the
     * color is white, the method returns 256.
     *
     * @param p The rgb value of the given pixel
     * @return int The Triana Color Map color value
     */
    public int trianaColorMapColorOf(int p) {
        if (p == 0xffffffff) {
            return 256;
        }
        if (p == 0xff000000) {
            return -1;
        }
        int k = 0;
        try {
            while (trianaColorMapTable[k] != p) {
                k++;
            }
            return k;
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return -2;
        }
    }

    /**
     * Flood fills an area of <I>oldpixel</I> values with <I>pixel</I> values starting at coordinate <I>(x, y)</I>.<P>
     * If uses a scanline method to reduce recursion and increase speed and efficiency.
     */

    public void fillColour(int x, int y, int pixel, int oldpixel) {
        if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {
            fillScanLineColour(x, y, pixel, oldpixel);
        }
    }

    /**
     * The fillScanLineColour method is a private function which fills a row of pixels to the first non old pixel colour
     * in either direction.  It then scans the rows above and below and if a black pixel is found then the area is
     * filled.  This means that the function only recurses once per scan line and so the maximum possible recursion is
     * the height of the image.
     */

    void fillScanLineColour(int xstart, int ystart, int pixel, int oldpixel) {
        int minx, maxx, y;
        int x = xstart;
        int line = ystart * width;

        // fill the pixel and to the left

        while (x >= 0) {
            if ((pixels[line + x] & 0xffffff) != oldpixel) {
                break;
            }
            pixels[line + x] = pixel | (pixels[line + x] & 0xff000000);
            x--;
        }

        minx = x >= 0 ? x : 0;

        // System.err.println("x=" + x + ", minx=" + minx + ", line=" + line);

        x = xstart + 1;

        // fill to the right of the pixel

        while (x < width) {
            if ((pixels[line + x] & 0xffffff) != oldpixel) {
                break;
            }
            pixels[line + x] = pixel | (pixels[line + x] & 0xff000000);
            x++;
        }

        maxx = x < width ? x : width - 1;

        // System.err.println("x=" + x + ", maxx=" + maxx + ", line=" + line);

        // check to see whether we have reached the top of the image

        y = ystart - 1;

        // System.err.println("y=" + y);

        if (y >= 0) {
            // scan the line above filling in any empty pixels
            // if we find a pixel in the old colour then fill recursively

            line = y * width;

            for (x = minx; x < maxx; ++x) {
                if ((pixels[x + line] & 0xffffff) == oldpixel) {
                    fillScanLineColour(x, y, pixel, oldpixel);
                }
            }
        }

        // check to see whether we are at the bottom of the image

        y = ystart + 1;

        // System.err.println("y=" + y);

        if (y >= height) {
            return;
        }

        // if we find a pixel in the old colour then fill recursively

        line = y * width;

        for (x = minx; x < maxx; x++) {
            if ((pixels[x + line] & 0xffffff) == oldpixel) {
                fillScanLineColour(x, y, pixel, oldpixel);
            }
        }
    }

    /**
     * Fills a black area to the given colour
     */

    public void fillBlack(int x, int y, int pixel) {
        fillColour(x, y, pixel, 0);
    }

    /**
     * Fills a non black area to black, ie erases the colour
     */

    public void fillErase(int x, int y) {
        if ((x >= 0) && (x < width) && (y >= 0) && (y < height)) {
            fillScanLineErase(x, y);
        }
    }

    /**
     * The fillScanLineErase method is a private function which fills a row of pixels non black pixels to black in
     * either direction.  It then scans the rows above and below and if a black pixel is found then the area is filled.
     * This means that the function only recurses once per scan line and so the maximum possible recursion is the height
     * of the image.
     */

    void fillScanLineErase(int xstart, int ystart) {
        int minx, maxx, y;
        int x = xstart;
        int line = ystart * width;

        // fill the pixel and to the left

        while (x >= 0) {
            if ((pixels[line + x] & 0xffffff) == 0) {
                break;
            }
            pixels[line + x] = pixels[line + x] & 0xff000000;
            x--;
        }

        minx = x >= 0 ? x : 0;

        // System.err.println("x=" + x + ", minx=" + minx + ", line=" + line);

        x = xstart + 1;

        // fill to the right of the pixel

        while (x < width) {
            if ((pixels[line + x] & 0xffffff) == 0) {
                break;
            }
            pixels[line + x] = pixels[line + x] & 0xff000000;
            x++;
        }

        maxx = x < width ? x : width - 1;

        // System.err.println("x=" + x + ", maxx=" + maxx + ", line=" + line);

        // check to see whether we have reached the top of the image

        y = ystart - 1;

        // System.err.println("y=" + y);

        if (y >= 0) {
            // scan the line above filling in any empty pixels
            // if we find a pixel in the old colour then fill recursively

            line = y * width;

            for (x = minx; x < maxx; ++x) {
                if ((pixels[x + line] & 0xffffff) != 0) {
                    fillScanLineErase(x, y);
                }
            }
        }

        // check to see whether we are at the bottom of the image

        y = ystart + 1;

        // System.err.println("y=" + y);

        if (y >= height) {
            return;
        }

        // if we find a pixel in the old colour then fill recursively

        line = y * width;

        for (x = minx; x < maxx; x++) {
            if ((pixels[x + line] & 0xffffff) != 0) {
                fillScanLineErase(x, y);
            }
        }
    }

    /**
     * Returns a Histogram object containing the histogram for the values in the red channel of the image.
     */

    public ImageHistogram getRedHistogram() {
        int[] table = new int[256];
        int i;

        for (i = 0; i < pixels.length; i++) {
            table[(pixels[i] >> 16) & 0xff]++;
        }

        return new ImageHistogram(table);
    }

    /**
     * Returns a Histogram object containing the histogram for the values in the green channel of the image.
     */

    public ImageHistogram getGreenHistogram() {
        int[] table = new int[256];
        int i;

        for (i = 0; i < pixels.length; i++) {
            table[(pixels[i] >> 8) & 0xff]++;
        }

        return new ImageHistogram(table);
    }

    /**
     * Returns a Histogram object containing the histogram of the values in the blue channel of the image.
     */

    public ImageHistogram getBlueHistogram() {
        int[] table = new int[256];
        int i;

        for (i = 0; i < pixels.length; i++) {
            table[pixels[i] & 0xff]++;
        }

        return new ImageHistogram(table);
    }

    /**
     * Returns a Histogram object containing the histogram of the intensities of the pixels in the image.
     */

    public ImageHistogram getIntensityHistogram() {
        int[] table = new int[256];
        int i, pixel;

        for (i = 0; i < pixels.length; i++) {
            pixel = pixels[i];
            table[(((pixel >> 16) & 0xff) + ((pixel >> 8) & 0xff) + (pixel & 0xff)) / 3]++;
        }

        return new ImageHistogram(table);
    }
}













