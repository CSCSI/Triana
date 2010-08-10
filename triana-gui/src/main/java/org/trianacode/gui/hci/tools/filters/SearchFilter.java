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
package org.trianacode.gui.hci.tools.filters;

import org.trianacode.gui.hci.ToolFilter;
import org.trianacode.taskgraph.TaskGraphUtils;
import org.trianacode.taskgraph.tool.Tool;

/**
 * A filter that matches  a search string to tool names, packages, input and output types and pop-up descriptions.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */
public class SearchFilter implements ToolFilter {

    private String searchstr;


    public SearchFilter(String searchstr) {
        this.searchstr = searchstr.toLowerCase();
    }

    /**
     * @return the root for the tool tree
     */
    public String getRoot() {
        if ((searchstr == null) || (searchstr.equals(""))) {
            return DEFAULT_ROOT;
        } else {
            return searchstr;
        }
    }

    /**
     * @return the name of this filter
     */
    public String getName() {
        return searchstr;
    }

    /**
     * @return the name of this filter
     */
    public String toString() {
        return getName();
    }


    /**
     * @return the filtered packages for the tool, empty array if the tool is ignored. (e.g. a tool in SignalPro.Input
     *         could become Input.SignalProc)
     */
    public String[] getFilteredPackage(Tool tool) {
        if ((searchstr == null) || (searchstr.equals(""))) {
            return new String[]{tool.getToolPackage()};
        } else {
            String tooldef = getToolDefAsString(tool);

            if (tooldef.indexOf(searchstr) > -1) {
                return new String[]{""};
            } else {
                return new String[]{};
            }
        }
    }


    /**
     * @return a string containing all input and output types and the pop-up description.
     */
    public static String getToolDefAsString(Tool tool) {
        StringBuffer sb = new StringBuffer(tool.getQualifiedToolName());
        sb.append(" ");

        String[] strings = TaskGraphUtils.getAllDataInputTypes(tool);
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]);
            sb.append(" ");
        }
        strings = TaskGraphUtils.getAllDataOutputTypes(tool);
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]);
            sb.append(" ");
        }
        strings = tool.getParameterNames();
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]);
            sb.append(" ");
        }
        sb.append(tool.getPopUpDescription());
        return sb.toString().toLowerCase();
    }


    /**
     * This method is called when the filter is choosen. The initialisation of the filter should be implemented here
     */
    public void init() {
    }

    /**
     * This method is called when the filter is unchoosen. Any disposal related to the filter should be implemented
     * here
     */
    public void dispose() {
    }


}
