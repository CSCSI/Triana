/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2003 University of Wales, Cardiff. All rights reserved.
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
 */
package imageproc.output;

import java.awt.Image;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.TrianaPixelMap;

/**
 * A ImageView unit to display an Triana Image
 *
 * @author Ian Taylor
 * @version $Revision: 2921 $
 */
public class ImageView extends Unit {

    public static final String IMAGE_DATA = "imageData";

    /**
     * ********************************************* ** USER CODE of ImageView goes here    ***
     * *********************************************
     */
    public void process() throws Exception {
        Object image = getInputAtNode(0);

        getTask().setParameterType(IMAGE_DATA, Task.TRANSIENT);

        if (image instanceof TrianaPixelMap) {
            setParameter(IMAGE_DATA, ((TrianaPixelMap) image).getTrianaImage().getImage());
        } else if (image instanceof Image) {
            setParameter(IMAGE_DATA, image);
        }
    }

    /**
     * Initialses information specific to ImageView.
     */
    public void init() {
        super.init();

        setParameterPanelClass("imageproc.output.ImagePanel");
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);
        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(0);
        setParameterUpdatePolicy(Task.IMMEDIATE_UPDATE);
        setPopUpDescription("A unit to display an image");
        setHelpFileLocation("ImageView.html");

        defineParameter(IMAGE_DATA, null, TRANSIENT);
    }


    /**
     * Reset's ImageView
     */
    public void reset() {
        super.reset();
    }

    /**
     * This method should return an array of the data input types accepted by this unit (e.g. triana.types.VectorType).
     * If no package is specified then triana.types is assumed.
     *
     * @return an array of the input types for this unit
     */
    public String[] getInputTypes() {
        return new String[]{"java.awt.Image", "TrianaPixelMap"};
    }

    /**
     * This method should return an array of the data input types output by this unit (e.g. triana.types.VectorType). If
     * no package is specified then triana.types is assumed.
     *
     * @return an array of the output types for this unit
     */
    public String[] getOutputTypes() {
        return new String[]{"none"};
    }

    /**
     * Called to update the unit's internal variables when parameters change
     */
    public void parameterUpdate(String name, Object value) {
    }
}













