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

import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @version $Revision: 4048 $
 */
public class UrlHistory extends Vector {
    private int urlIndex;
    private int urlCount;

    // A vector which contains the UrlEventListeners
    protected Vector urlEventVector;

    public UrlHistory() {
        super();

        urlEventVector = new Vector();
        urlIndex = -1;
        urlCount = 0;

        // System.out.println("*** Creating UrlHistory ***");
    }

    public void addUrl(URL url) {
        urlIndex++;
        urlCount = urlIndex + 1;

        if (urlCount > elementCount) {
            setSize(urlCount + 10);
        }

        setElementAt(url, urlIndex);
        // System.out.println("*** Adding URL ***");
        processUrlEvent(new UrlEvent(this));
    }

    public URL getPreviousUrl() {
        if (urlIndex <= 0) {
            return null;
        }

        urlIndex--;
        processUrlEvent(new UrlEvent(this));
        return (URL) elementAt(urlIndex);
    }

    public URL getNextUrl() {
        if (urlIndex >= (urlCount - 1)) {
            return null;
        }

        urlIndex++;
        processUrlEvent(new UrlEvent(this));
        return (URL) elementAt(urlIndex);
    }

    public int getIndex() {
        return urlIndex;
    }

    public int countUrls() {
        return urlCount;
    }

    public void addUrlEventListener(UrlEventListener urlListener) {
        urlEventVector.addElement(urlListener);
    }

    public void removeUrlEventListener(UrlEventListener urlListener) {
        urlEventVector.removeElement(urlListener);
    }

    private void processUrlEvent(UrlEvent event) {
        UrlEventListener listener;
        Enumeration e = urlEventVector.elements();

        while (e.hasMoreElements()) {
            // System.out.println("*** Throwing UrlEvent ***");
            listener = (UrlEventListener) e.nextElement();
            listener.indexChanged(event);
        }
    }
}



