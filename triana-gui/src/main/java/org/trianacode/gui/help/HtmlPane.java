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

import org.trianacode.gui.hci.GUIEnv;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @version $Revision: 4048 $
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class HtmlPane extends JScrollPane implements HyperlinkListener {
    private JEditorPane editorPane;
    private UrlHistory urlHistory;
    private URL indexUrl;

    public HtmlPane(URL url) {
        createHtmlPane(url);
    }

    /**
     *
     */
    public HtmlPane(File file) {
        try {
            createHtmlPane(toURL(file));
        }
        catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + e);
        }
    }

    /**
     *
     */
    public HtmlPane(String string) {
        try {
            createHtmlPane(toURL(string));
        }
        catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + e);
        }
    }

    /**
     * The default constructor
     */
    public HtmlPane() {
        createHtmlPane();
    }

    public URL toURL(File file) throws MalformedURLException {
        return new URL(new URL("file:/"), file.getAbsolutePath());
    }

    public URL toURL(String string) throws MalformedURLException {
        return new URL(new URL("file:/"), string);
    }

    /**
     *
     *
     */
    private void createHtmlPane(URL url) {
        try {
            editorPane = new JEditorPane(url);
            editorPane.setEditable(false);
            editorPane.addHyperlinkListener(this);

            JViewport viewport = getViewport();
            viewport.add(editorPane);

            urlHistory = new UrlHistory();
            urlHistory.addUrl(url);

        }
        catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }

    /**
     *
     *
     */
    private void createHtmlPane() {
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(this);

        JViewport viewport = getViewport();
        viewport.add(editorPane);

        urlHistory = new UrlHistory();
    }

    /**
     *
     *
     */
    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public UrlHistory getUrlHistory() {
        return urlHistory;
    }

    /**
     * Follows the reference in an link.  This is an internal function
     * which doesn't carry out and history functions.  This is useful for
     * calling from history functions!
     *
     * @param url the URL to follow
     */
    private void setPageInternal(URL url) {
        Cursor cursor = editorPane.getCursor();
        Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        editorPane.setCursor(waitCursor);
        SwingUtilities.invokeLater(new PageLoader(url, cursor));
    }

    /**
     * Follows the reference in an link.
     *
     * @param url the URL to follow
     */
    public void setPage(URL url) {
        setPageInternal(url);
        urlHistory.addUrl(url);
    }

    /**
     * Follows the reference in an link.
     */
    public void setPage(String string) {
        try {
            URL url = new URL(string);
            setPage(url);
        }
        catch (MalformedURLException e) {
            getToolkit().beep();
            JOptionPane.showMessageDialog(editorPane, "The URL provided was not a valid",
                    "Invalid URL",
                    JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
        }
    }

    /**
     *
     */
    public void setIndex(URL indexUrl) {
        this.indexUrl = indexUrl;
    }

    /**
     *
     */
    public void setIndex(String string) {
        try {
            setIndex(toURL(string));
        }
        catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + e);
        }
    }

    /**
     *
     */
    public void goBack() {
        URL url = urlHistory.getPreviousUrl();
        setPageInternal(url);
    }

    /**
     *
     */
    public void goForwards() {
        URL url = urlHistory.getNextUrl();
        setPageInternal(url);
    }

    /**
     *
     */
    public void goIndex() {
        setPage(indexUrl);
    }

    /**
     * Notification of a change relative to a
     * hyperlink.
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            setPage(e.getURL());
        }
    }

    /**
     * temporary class that loads synchronously (although
     * later than the request so that a cursor change
     * can be done).
     */
    class PageLoader implements Runnable {
        URL url;
        Cursor cursor;

        PageLoader(URL url, Cursor cursor) {
            this.url = url;
            this.cursor = cursor;
        }

        public void run() {
            if (url == null) {

                // restore the original cursor
                editorPane.setCursor(cursor);

                Container parent = editorPane.getParent();
                parent.repaint();
            } else {
                Document doc = editorPane.getDocument();
                try {
                    editorPane.setPage(url);
                }
                catch (IOException ioe) {
                    getToolkit().beep();
                    JOptionPane.showMessageDialog(editorPane, url,
                            "Document not found", JOptionPane.ERROR_MESSAGE, GUIEnv.getTrianaIcon());
                    editorPane.setDocument(doc);
                }
                finally {

                    // schedule the cursor to revert after
                    // the paint has happended.
                    url = null;
                    SwingUtilities.invokeLater(this);
                }
            }
        }
    }
}



