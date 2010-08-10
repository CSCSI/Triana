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
package org.trianacode.taskgraph.imp;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.tool.Tool;

/**
 * Tooltip generator class, responsible for the pretty printing of tool and task tip information.
 *
 * @author Matthew Shields
 * @version $Revsion$
 */
public class ToolTipWriter {

    /**
     * Format a string containing information about this tool.
     */
    public static String getToolTip(Tool tool, boolean extended) {
        StringBuffer tip = new StringBuffer("<html>");

        if (tool instanceof TaskGraph) {
            TaskGraph tg = (TaskGraph) tool;
            tip.append("<b>");
            tip.append(tg.getToolName());
            tip.append("</b> : ");
            tip.append(tg.getPopUpDescription());
            // TODO show input and output types for a group, we are going to need to know
            // about nodes to do this.
            if (extended) {
                tip.append("<br><br><b>Group Contains</b> : ");
                Task[] tasks = tg.getTasks(false);
                for (int i = 0; i < tasks.length; i++) {
                    tip.append(tasks[i].getToolName());
                    tip.append(" ");
                }
            }

            tip.append("<br>");
            appendDataTypes(tool, tip);

            tip.append("</html>");
            return tip.toString();
        }
        if (tool instanceof Task) {
            Task task = (Task) tool;

            tip.append("<b>");
            tip.append(task.getToolName());
            tip.append("</b> : ");
            tip.append(task.getPopUpDescription());

            if (extended) {
                /*if ((task.getProxy() != null) && (task.getProxy() instanceof JavaProxy)) {
                    tip.append("<br><b>Unit</b>: ");
                    tip.append(((JavaProxy) task.getProxy()).getUnitName());
                }*/
            }

            tip.append("<br>");
            appendDataTypes(tool, tip);

            tip.append("</html>");
            return tip.toString();
        } else {
            tip.append(tool.getPopUpDescription());
            tip.append("</html>");
            return tip.toString();
        }
    }

    /**
     * Format a string containing information about this tool, suitable for the Tool Tree view.
     */
    public static String getTreeTip(Tool tool, boolean extended) {
        StringBuffer tip = new StringBuffer("<html>");
        tip.append("<b>");
        tip.append(tool.getToolName());
        tip.append("</b> : ");
        tip.append(tool.getPopUpDescription());

        if (extended) {
            tip.append("<br>");
            appendDataTypes(tool, tip);

            tip.append("<br>ToolBox location : ");
            tip.append(tool.getToolBox());
            tip.append("<br>Tool Package : ");
            tip.append(tool.getToolPackage());

            /*if (tool.getProxy() instanceof JavaProxy) {
                JavaProxy proxy = (JavaProxy) tool.getProxy();
                tip.append("<br>Unit Package : ");
                tip.append(proxy.getUnitPackage());
                tip.append("<br>Unit Name : ");
                tip.append(proxy.getUnitName());
            } else*/
            if (tool.getProxy() != null) {
                tip.append("<br>Proxy : ");
                tip.append(tool.getProxy().toString());
            }

            tip.append("<br>Definition File : ");
            tip.append(tool.getDefinitionPath().toString());

        }
        tip.append("</html>");
        return tip.toString();
    }

    /**
     * Append the data input/output types for a tool
     */
    private static void appendDataTypes(Tool tool, StringBuffer tip) {
        String[] types;
        boolean all = false;

        tip.append("<table width=100% cellspacing=0 cellpadding=0><tr>");

        tip.append("<td width=50%>");
        tip.append("<b>Input Types </b>: ");
        tip.append("</td><td width=50%>");
        tip.append("<b>Output Types </b>:");
        tip.append("</td></tr>");

        tip.append("<tr><td>");

        tip.append("<table cellspacing=0 cellpadding=0>");

        for (int count = 0; count < tool.getDataInputNodeCount() && (!all); count++) {
            tip.append("<tr><td>");
            types = tool.getDataInputTypes(count);

            if ((types == null) && (count == 0)) {
                all = true;
            }

            if (types == null) {
                types = tool.getDataInputTypes();
            }

            for (int tcount = 0; tcount < types.length; tcount++) {
                if (tcount > 0) {
                    tip.append("<br>");
                }

                tip.append(types[tcount].substring(types[tcount].lastIndexOf('.') + 1));
            }

            if (types.length == 0) {
                tip.append("None ");
            }


            tip.append("</td><td>");

            if (all) {
                tip.append("-> [ALL]");
            } else {
                tip.append("-> [" + count + "]");
            }

            tip.append("</td></tr>");
        }

        tip.append("</table>");
        tip.append("</td><td>");
        tip.append("<table cellspacing=0 cellpadding=0>");
        all = false;

        for (int count = 0; count < tool.getDataOutputNodeCount() && (!all); count++) {
            tip.append("<tr><td>");

            types = tool.getDataOutputTypes(count);

            if ((types == null) && (count == 0)) {
                tip.append("[ALL] -> ");
                all = true;
            } else {
                tip.append("[" + count + "] -> ");
            }

            tip.append("</td><td>");

            if (types == null) {
                types = tool.getDataOutputTypes();
            }

            for (int tcount = 0; tcount < types.length; tcount++) {
                if (tcount > 0) {
                    tip.append("<br>");
                }

                tip.append(types[tcount].substring(types[tcount].lastIndexOf('.') + 1));
            }

            if (types.length == 0) {
                tip.append("None ");
            }

            tip.append("</td></tr>");
        }

        tip.append("</table");
        tip.append("</td></tr></table>");
    }
}
