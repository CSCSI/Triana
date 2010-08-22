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


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Window;
import java.awt.image.ImageObserver;

import javax.swing.JPanel;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;

/**
 * A window which displays Triana Images.
 *
 * @author Ian Taylor
 * @version $Revision: 2921 $
 */
public class ImagePanel extends ParameterPanel implements TaskListener {

    public static final String IMAGE_DATA = "imageData";


    /**
     * The canvas that the image is drawn on
     */
    private ImageCanvas canvas = new ImageCanvas(this);

    /**
     * flag indicating there is an image
     */
    private boolean initimage = false;


    /**
     * @return false so that the auto commit box is not shown
     */
    public boolean isAutoCommitVisible() {
        return false;
    }

    /**
     * Overridden to return WindowButtonConstants.OK_BUTTON only.
     */
    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_BUTTON;
    }

    /**
     * Overridden to return false, suggesting that the panel prefers to be allowed to be hidden behind the main Triana
     * window.
     */
    public boolean isAlwaysOnTopPreferred() {
        return false;
    }


    /**
     * @return the canvas used to draw the image
     */
    protected ImageCanvas getImageCanvas() {
        return canvas;
    }


    /**
     * Sets the panel up as a task listener and calls initLayout().
     */
    public void init() {
        getTask().addTaskListener(this);

        initLayout();
    }

    /**
     * Initialises the layout, can be overridden to change the layout
     */
    protected void initLayout() {
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);

    }


    public void reset() {
    }

    public void dispose() {
        getTask().removeTaskListener(this);
    }


    /**
     * Called when the value of a parameter is changed, including when a parameter is removed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
        Task task = event.getTask();
        String paramname = event.getParameterName();

        try {
            if (paramname.equals(IMAGE_DATA) && (task.getParameter(paramname) != null)) {
                Image image = (Image) task.getParameter(paramname);

                prepareImage(image, this);
                canvas.setImage(image, this);

                popUpWindow();

                initimage = true;
            }
        } catch (Exception except) {
            System.out.println("ImagePanel Error: " + except.getMessage());
        }
    }

    private void popUpWindow() {
        Component parent = this;

        while ((parent != null) && (!(parent instanceof Window))) {
            parent = parent.getParent();
        }

        if (parent != null) {
            if (!parent.isVisible()) {
                parent.setVisible(true);
            }

            if (!initimage) {
                ((Window) parent).pack();
            }
        }

        repaint();
    }

    /**
     * Called when the core properties of a task change i.e. its name, whether it is running continuously etc.
     */
    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }

    /**
     * Called when a data input node is added.
     */
    public void nodeAdded(TaskNodeEvent event) {
    }


    protected class ImageCanvas extends JPanel {

        private Image image;
        private ImageObserver imageob;

        private MediaTracker mediaTracker;
        private Container container;

        //       private Image scaledImage;
        //       private int scaleheight;
        //       private int scalewidth;

        /**
         * Construct an ImageCanvas that uses double buffering
         */
        public ImageCanvas(Container container) {
            super(true);
            this.container = container;
            mediaTracker = new MediaTracker(this);
        }

        /**
         * Sets the image displayed in the canvas
         */
        public void setImage(Image image, ImageObserver imageob) {
            this.image = image;
            this.imageob = imageob;
            mediaTracker.addImage(image, 0);
            try {
                mediaTracker.waitForID(0);
            } catch (InterruptedException e) {
            }
            repaint();
        }

        /**
         * @return the image displayed in the canvas
         */
        public Image getImage() {
            return image;
        }

        /**
         * @return the image observer for the image
         */
        public Image getImageObserver() {
            return image;
        }


        public Dimension getPreferredSize() {
            if (image != null) {
                return new Dimension(image.getWidth(imageob), image.getHeight(imageob));
            } else {
                return new Dimension(0, 0);
            }
        }


        public void paint(Graphics g) {
            if (image != null) {
                if ((image.getHeight(null) == -1) || (image.getWidth(null) == -1)) {
                    g.drawImage(image, 0, 0, imageob);
                } else {
                    super.paint(g);
                    Dimension dim = getSize();

                    int height = image.getHeight(null);
                    int width = image.getWidth(null);
                    //scale the image to smallest of the two dimension scales so it still fits in the window
                    double scale;

                    if ((((double) dim.width) / width) < (((double) dim.height) / height)) {
                        scale = ((double) dim.width) / width;
                    } else {
                        scale = ((double) dim.height) / height;
                    }


                    /*                  if ((scaledImage == null) || (height != scaleheight) || (width != scalewidth)) {
                    scaleheight = height;
                    scalewidth = width;
                    scaledImage = image.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);

                    mediaTracker.addImage(scaledImage, 1);

                    try {
                        mediaTracker.waitForID(1);
                    } catch (InterruptedException e) {
                    }
                }    */
                    g.drawImage(image, 0, 0, (int) (width * scale), (int) (height * scale), imageob);
                }
            }
        }

        /**
         * Scale the image to match the panels dimensions, keeping the aspect ratio of the origonal image
         */
        public void scale() {
        }
    }

}



