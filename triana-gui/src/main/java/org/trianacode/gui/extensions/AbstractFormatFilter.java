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
package org.trianacode.gui.extensions;

import java.awt.Component;

import javax.swing.filechooser.FileFilter;

/**
 * Common methods used by all format filters in Triana. A format filter is a mechanism by which Triana is able to import
 * other workflow or taskgraph formats into the internal format, export the internal format to other external formats or
 * import tools to the component library.
 * <p/>
 * Format filters are used as extensions to a JFileDialog to automatically provide extensions to filter files and handle
 * their import or export.
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 */
public abstract class AbstractFormatFilter {

    /**
     * Short description of the filter that will appear as the selection item in the list of filters.
     */
    public abstract String getFilterDescription();

    /**
     * Gets the list of choosable file filters.
     *
     * @return A <code>FileFilter</code> array containing all choosable file filters.
     */
    public abstract FileFilter[] getChoosableFileFilters();

    /**
     * Returns the default choosable file filter.
     *
     * @return the default file filter
     */
    public abstract FileFilter getDefaultFileFilter();

    /**
     * Returns <b>true</b> if this filter has user configurable options, <b>false</b> otherwise.
     *
     * @return true if this filter has user options
     */
    public abstract boolean hasOptions();

    /**
     * Pops up a the options dialog for this filter if there is one
     *
     * @param parent the parent component of the dialog, can be <code>null</code>; see <code>showDialog</code> for
     *               details
     * @return the return state of the file chooser on popdown: <ul> <li>JFileChooser.CANCEL_OPTION
     *         <li>JFileChooser.APPROVE_OPTION <li>JFileCHooser.ERROR_OPTION if an error occurs or the dialog is
     *         dismissed </ul>
     * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless() returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public abstract int showOptionsDialog(Component parent);

    /**
     * Overrides the default <code>toString</code> method to return the filter description
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return getFilterDescription();
    }
}

