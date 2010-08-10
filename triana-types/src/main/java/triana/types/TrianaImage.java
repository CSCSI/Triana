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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * TrianaImage is a TrianaType wrapper for the Java image type.  It contains a reference to a Java Image object (called
 * <i>image</i>) and implements things which allow it to be a TrianaType. TrianaImage also subclasses JPanel so that the
 * image can be prepared before it is used. Images in Java have to be put on a JComponent before you can find out their
 * actual size. To display a TrianaImage then you just add it to your container and <i>pack</i> it in.
 *
 * @author Ian Taylor
 * @author Bernard Schutz
 * @version $Revision: 4048 $
 */
public class TrianaImage extends TrianaType {

    /**
     * String parameter to act as the key for storing this image in <i>dataContainer</i>.
     */
    String imageName = "TrianaImage";

    /*
     * Begin with Constructors.
     */

    /**
     * Default constructor.
     */
    public TrianaImage() {
        super();
    }

    /**
     * Constructor for given image.
     *
     * @param image The image to be held in the new TrianaImage
     */
    public TrianaImage(Image image) {
        insertIntoContainer(imageName, new Im(image));
    }

    /**
     * Put an image into the current object.
     *
     * @param image The image to be placed into this object
     */
    public void setImage(Image image) {
        insertIntoContainer(imageName, new Im(image));
    }

    /**
     * @return the Java Image which this TrianaImage represents
     */
    public Image getImage() {
        return ((Im) getFromContainer(imageName)).getImage();
    }

    /**
     * @return the Image observer for this TrianaImage
     */
    public JComponent getImageObs() {
        return (Im) getFromContainer(imageName);
    }

    /**
     * @return the dimensions of the image
     */
    public Dimension getImageDimensions() {
        return ((Im) getFromContainer(imageName)).getImageDimensions(getImage());
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
        TrianaImage t = null;
        try {
            t = (TrianaImage) getClass().newInstance();
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
     * present one. (There is no need to over-ride method <i>copyParameters</i> here, since there are no variable
     * parameters in this class.)
     *
     * @param source The TrianaImage whose image is being copied
     */
    protected void copyData(TrianaType source) {
        if (source instanceof TrianaImage) {
            setImage(((TrianaImage) source).getImage());
        }
    }


    /**
     * Uses the default writer to output this object to the given Object output stream.
     *
     * @param out The stream to write to
     */
    public void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(new TrianaPixelMap(getImage()));
    }

    /**
     * Uses the default writer to input and create an object from the Object input stream.
     *
     * @param in The stream to createTool from
     */
    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        TrianaPixelMap trianaPixelMap = (TrianaPixelMap) in.readObject();
        in.registerValidation((ObjectInputValidation) trianaPixelMap.getTrianaImage(), 1);
    }
}


/**
 * A class that subclasses JPanel to provide an Image Observer for the image, so we can check when its loaded in!
 */
class Im extends JPanel {

    /**
     * Parameters of this class include a MediaTracker and an Image
     */
    MediaTracker tracker;
    Image image;

    /**
     * Constructor
     *
     * @param image The image being observed
     */
    public Im(Image image) {
        super();

        this.image = image;
        tracker = new MediaTracker(this);
        tracker.addImage(this.image, 0);
        try {
            tracker.waitForID(0);
        }
        catch (InterruptedException e) {
        }

        setSize(200, 200);

        setVisible(true);  // show it so that the image can be prepared

        Dimension d = getImageDimensions(this.image); // find actual size

        setSize(d.width, d.height);
        repaint();
    }

    /**
     * @return the image held here
     */
    public Image getImage() {
        return image;
    }

    /**
     * Get the dimensions of an image.
     *
     * @return the image's dimensions.
     */
    Dimension getImageDimensions(Image image) {
        return new Dimension(image.getWidth(this), image.getHeight(this));
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        if ((tracker.statusID(0, true) &
                MediaTracker.COMPLETE) != 0) {
            g.drawImage(this.image, 0, 0, this);
        }
    }
}















