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
import org.trianacode.gui.help.TrianaHelpHandler;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.taskgraph.util.Listing;
import org.trianacode.util.Env;

import java.io.File;
import java.util.Vector;


/**
 * This pulls up a Ved - a Triana Editor and puts the help information
 * in it. The user gives the filename of the helpfile and Ved displays
 * it.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @created 14 April 1999
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class Help {

//    static Ved editor=null;

    static TrianaHelpHandler editor = null;

    static Vector helpPaths;

    static String title = "";
    static String file = "";

    /**
     * TODO Make into tool box listener so is notified when path added
     */
    public static void updatePaths(Vector toolBoxPaths) {
        helpPaths = new Vector();

        Listing l;
        String[] s;
        Object obj;

        for (int i = 0; i < toolBoxPaths.size(); ++i) {
            obj = toolBoxPaths.elementAt(i);

            l = FileUtils.listDirNames(obj.toString(), "help", true);
            if (!obj.toString().endsWith("GroupTools")) {
                if (l == null) {
                    String path = "";

                    new ErrorDialog(null, "The " + obj + " toolbox directory doesn't exist ! \n" +
                            "It is defined in the " + path + " file under the [TOOLBOXES] \n" +
                            "section. Please edit this file by hand or use the 'Edit ToolBox \n" +
                            "Paths' option of the SetUp JMenu (in the MainTriana Window)\n" +
                            "to correct the " + obj + " invalid path name");
                } else {
                    s = l.convertToStrings();

                    for (int k = 0; k < s.length; ++k)
                        helpPaths.addElement(s[k]);
                }
            }
        }

        obj = Env.home() + File.separator + "help";

        helpPaths.addElement(obj);
    }

    public static void setTitle(String ti) {
        if (editor == null)
            return;
        title = ti;
    }

    public static int tryActualFile(String fi) {
        String fn = fi;

        title = "Please Wait. Loading .....";

        if ((fn == null) || (!FileUtils.fileExists(fn))) {
            title = "Error!!!  File " + fn + " Not Found!";
            fn = "file:///" + Env.home() + "help" + File.separator + "helpError.html";
        } else {
            title = file.substring(file.indexOf(java.io.File.separator) + 1);
            if ((fn.indexOf("http://") == -1) && (fn.indexOf("file://") == -1)) {
                fn = "file:///" + fn;
            }
        }

        System.out.println("File to load = " + fn);

        editor = new TrianaHelpHandler(null, fn, title);
        editor.actionPerformed(null);

        return 1;
    }

    public static void setFile(String fi) {
        file = fi;

        String fn = getFullName(fi);

        title = "Please Wait. Loading .....";

        if (fn == null) {
            title = "Error!!!  File " + file + " Not Found!";
            return;
        } else {
            title = file.substring(file.indexOf(java.io.File.separator) + 1);
            if (fn.indexOf("http://") == -1)
                fn = "file:///" + fn;
        }

        System.out.println("File to load = " + fn);

        String viewer = GUIEnv.getHTMLViewerCommand();

        if (viewer.equals(Env.getString("defaultEditor"))) {
            editor = new TrianaHelpHandler(null, fn, title);
            editor.actionPerformed(null);
        } else
            GUIEnv.showEditorFor(viewer, fn);
    }

    public static void setContents(String text) {
        file = Env.home() + File.separator + "tmp";
        FileUtils.writeToFile(file, text);
        String viewer = GUIEnv.getHTMLViewerCommand();

        if (viewer.equals(Env.getString("defaultEditor"))) {
            editor = new TrianaHelpHandler(null, file, title);
            editor.actionPerformed(null);
        } else
            GUIEnv.showEditorFor(viewer, file);
    }


    public static String getFullName(String file) {
        // try just file :-
        if (FileUtils.fileExists(file))
            return file;

        String sep, name, nPath;

        for (int i = 0; i < helpPaths.size(); ++i) {
            Object nextPath = helpPaths.elementAt(i);

            if (nextPath instanceof String)
                sep = File.separator;
            else
                sep = "/";

            nPath = nextPath.toString();

            if (!nPath.endsWith(sep))
                nPath = nPath + sep;

            name = nPath + file;
            System.out.println("Trying file :- " + name);
            if (FileUtils.fileExists(name)) {
                System.out.println("File OK .. loading " + name);
                return name;
            }
        }

        // search user_home/help directory :-
        sep = File.separator;
        name = Env.userHome() + sep + "help" + sep + file;
        System.out.println("Tried file :- " + name);
        if (FileUtils.fileExists(name))
            return name;

        QuestionWindow con = new QuestionWindow(null,
                "File Not Found.  Goto Triana Web on-line-help Files ?");
        if (con.reply == con.YES)
            return "http://www.astro.cf.ac.uk/Triana/help/index.html";

        return null;  // give up finally!
    }
}













