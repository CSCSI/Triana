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


import java.awt.image.ImageProducer;

/**
 * @author      Ian Taylor
 * @created     Wed May 15 16:43:30 BST 2002
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class Convolution {
    PixelMap sourceMap, destMap;
    int w00, w01, w02, w10, w11, w12, w20, w21, w22, norm;

    public Convolution(PixelMap pixelmap, int[] matrix) {
        // Get the pixel map and make a copy.

        sourceMap = pixelmap;
        destMap = new PixelMap(pixelmap);

        // Copy the weights for the individual cells.

        w00 = matrix[0];
        w01 = matrix[1];
        w02 = matrix[2];
        w10 = matrix[3];
        w11 = matrix[4];
        w12 = matrix[5];
        w20 = matrix[6];
        w21 = matrix[7];
        w22 = matrix[8];

        // Calculate the sum for normalisation purposes.

        norm = w00 + w01 + w02 + w10 + w11 + w12 + w20 + w21 + w22;
    }

    public ImageProducer getImageProducer() {
        return destMap.getImageProducer();
    }

    public PixelMap getResult() {
        int x, y;
        int i00, i01, i02, i10, i11, i12, i20, i21, i22;
        int p00, p01, p02, p10, p11, p12, p20, p21, p22;
        int red, green, blue;
        int width = sourceMap.getWidth();
        int[] source = sourceMap.getPixels();
        int[] dest = destMap.getPixels();
        double total;

        System.out.println("Processing...");

        for (y = 1; y < sourceMap.getHeight() - 1; y++) {
            i11 = 1 + y * width;
            i10 = i11 - 1;
            i12 = i11 + 1;
            i00 = i10 - width;
            i01 = i11 - width;
            i02 = i12 - width;
            i20 = i10 + width;
            i21 = i11 + width;
            i22 = i12 + width;

            for (x = 1; x < width - 1; x++) {
                p00 = source[i00];
                p01 = source[i01];
                p02 = source[i02];
                p10 = source[i10];
                p11 = source[i11];
                p12 = source[i12];
                p20 = source[i20];
                p21 = source[i21];
                p22 = source[i22];

                total = ((p00 >> 16) & 0xff) * w00;
                total += ((p01 >> 16) & 0xff) * w01;
                total += ((p02 >> 16) & 0xff) * w02;
                total += ((p10 >> 16) & 0xff) * w10;
                total += ((p11 >> 16) & 0xff) * w11;
                total += ((p12 >> 16) & 0xff) * w12;
                total += ((p20 >> 16) & 0xff) * w20;
                total += ((p21 >> 16) & 0xff) * w21;
                total += ((p22 >> 16) & 0xff) * w22;
                red = (int) (total / norm) << 16;

                total = ((p00 >> 8) & 0xff) * w00;
                total += ((p01 >> 8) & 0xff) * w01;
                total += ((p02 >> 8) & 0xff) * w02;
                total += ((p10 >> 8) & 0xff) * w10;
                total += ((p11 >> 8) & 0xff) * w11;
                total += ((p12 >> 8) & 0xff) * w12;
                total += ((p20 >> 8) & 0xff) * w20;
                total += ((p21 >> 8) & 0xff) * w21;
                total += ((p22 >> 8) & 0xff) * w22;
                green = (int) (total / norm) << 8;

                total = (p00 & 0xff) * w00;
                total += (p01 & 0xff) * w01;
                total += (p02 & 0xff) * w02;
                total += (p10 & 0xff) * w10;
                total += (p11 & 0xff) * w11;
                total += (p12 & 0xff) * w12;
                total += (p20 & 0xff) * w20;
                total += (p21 & 0xff) * w21;
                total += (p22 & 0xff) * w22;
                blue = (int) (total / norm);

                dest[i11] = (p11 & 0xff000000) | red | green | blue;

                i00++;
                i01++;
                i02++;
                i10++;
                i11++;
                i12++;
                i20++;
                i21++;
                i22++;
            }
        }

        return destMap;
    }
}














