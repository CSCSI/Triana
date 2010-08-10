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
package org.trianacode.gui.help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

/**
 * @version $Revision: 4048 $
 */
public class TrianaHelpHandler implements ActionListener {
    // Static variable which gets initialised the first time and instance
    // of this class is created.
    private static TrianaHelpFrame helpFrame = null;

    /* SUPERCEDED

    // Static variable which contains the image path
    private static String imagePath; */

    // The help file and title
    protected String helpFile;
    protected String title;

    // The handle of the parent frame
    protected JFrame parent;

    /**
     * The main function for the TrianaHelp application
     */
    public TrianaHelpHandler(JFrame parent, String helpFile, String title) {
        if (helpFrame == null) {
            // If we haven't a Help window we'd better create it
            try {
                helpFrame = new TrianaHelpFrame(title, helpFile);
            }
            catch (Exception ee) {
                ee.printStackTrace();
                helpFrame = null;
                return;
            }

            // And attach a close frame listener to it
            helpFrame.addCloseFrameListener(new CloseListener());
        }

        this.helpFile = helpFile;
        this.title = title;
        this.parent = parent;
    }

    /*

    ********** SUPERCEDED FUNCTIONS **********

    The images are now loaded from the resource tree using getImageResouce()
    in TrianaHelpFrame.

    public final static void setImagePath(String path) {
     if (path != null) {
        if (!path.equals("")  && !path.endsWith(File.separator)) {
          imagePath = path + File.separator;
        } else {
          imagePath = path;
        }
      } else {
        imagePath = "";
      }

      //System.out.println("[TrianaHelpHandler: imagePath = " +
      //    imagePath + "]");
    }

    public final static String getImagePath() {
      return imagePath;
    } */

    /**
     * Initialises the help window.  Sets the title and the help file.
     */
    public void initFrame(String helpFile, String title) {
        if (helpFrame == null) {
            return;
        }
        HtmlPane htmlPane = helpFrame.getHtmlPane();
        htmlPane.setIndex(helpFile);
        htmlPane.setPage(helpFile);
        helpFrame.setTitle(title);
        helpFrame.setVisible(true);
    }

    /**
     * An action listener which opens the help window, sets the title and index pages and loads the appropriate page.
     */
    public void actionPerformed(ActionEvent e) {
        initFrame(helpFile, title);
    }

    /**
     * A class which responds to the CloseFrameEvent from TrianaHelpFrame and closes the help window.
     */
    class CloseListener implements CloseFrameListener {
        public void frameClosing(CloseFrameEvent e) {
            helpFrame.setVisible(false);
        }
    }
}




