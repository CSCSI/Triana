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
package org.trianacode.gui.windows;

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.util.Env;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.logging.Logger;

/**
 * Start-up Splash screen class that also has buttons for the various peer options
 *
 * @author Matthew Shields
 * @version $Revision: 4050 $
 * @created May 21, 2003: 3:56:44 PM
 * @date $Date: 2007-10-24 13:14:13 +0100 (Wed, 24 Oct 2007) $ modified by $Author: spxmss $
 */
public class SplashScreen {

    protected static JFrame splashScreen = new JFrame();
    protected static JProgressBar progressBar = new JProgressBar();
    protected static JLabel progressLabel = new JLabel();
    static Logger logger = Logger.getLogger("org.trianacode.gui.windows.SplashScreen");

    /**
     * Display the Splash screen with a progress bar
     *
     * @param tasks the length of the progress bar (no. of items)
     */
    public void showSplashScreen(int tasks) {
        logger.finest("display splash screen");
        splashScreen.setTitle(Env.getString("launch"));

        splashScreen.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        splashScreen.getContentPane().removeAll();
        splashScreen.setUndecorated(true);
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        final SplashPanel sp = new SplashPanel(GUIEnv.getIcon("frontsplash.jpg"));
        jp.add(sp, BorderLayout.CENTER);
        sp.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                ImageIcon icon = (ImageIcon) sp.getIcon();
                Dimension size = sp.getSize();
                icon.setImage(icon.getImage().getScaledInstance(size.width, size.height, Image.SCALE_DEFAULT));
            }
        });
        progressBar.setMinimum(0);
        progressBar.setMaximum(tasks - 1);
        progressBar.setValue(0);

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BorderLayout());
        progressPanel.add(progressLabel, BorderLayout.NORTH);
        JPanel pbarPanel = new JPanel(new BorderLayout());
        pbarPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(pbarPanel, BorderLayout.SOUTH);

        jp.add(progressPanel, BorderLayout.SOUTH);
        splashScreen.setContentPane(jp);
        splashScreen.pack();
        splashScreen.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - splashScreen.getSize().width) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - splashScreen.getSize().height) / 2);
        splashScreen.setVisible(true);
        splashScreen.requestFocus();
    }

    /**
     * Increment the progress bar and display s
     *
     * @param s the string to display
     */
    public void setSplashProgress(String s) {
        logger.finest("Enter");
        progressLabel.setText(s);
        progressBar.setValue(progressBar.getValue() + 1);

    }

    /**
     * Hide the splash screen
     */
    public void hideSplashScreen() {
        splashScreen.setVisible(false);
        splashScreen.dispose();
    }


    private class SplashPanel extends JPanel {

        private ImageIcon icon;

        public SplashPanel(ImageIcon icon, JComponent config) {
            this.icon = icon;

            setLayout(null);
            add(config);

            Dimension size = config.getPreferredSize();
            config.setLocation((int) (icon.getIconWidth() - (size.width * 1.2)),
                    (int) (icon.getIconHeight() - (size.height * 1.2) - 30));
            config.setSize(size);
        }


        /**
         * Creates a new <code>JPanel</code> with a double buffer
         * and a flow layout.
         */
        public SplashPanel(ImageIcon icon) {
            this.icon = icon;
        }

        public ImageIcon getIcon() {
            return icon;
        }


        public Dimension getPreferredSize() {
            return new Dimension(icon.getIconWidth(), icon.getIconHeight());
        }


        protected void paintComponent(Graphics graphs) {
            icon.paintIcon(this, graphs, 0, 0);
            paintComponents(graphs);
        }

    }

}
