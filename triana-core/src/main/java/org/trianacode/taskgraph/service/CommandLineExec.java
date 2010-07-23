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

package org.trianacode.taskgraph.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.TaskGraphException;
import org.trianacode.taskgraph.ser.XMLReader;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.util.EngineInit;


/**
 * A standalone class for executing a taskgraph using specified input data.
 *
 * @author Ian Wang
 * @version $Revision: 4048 $
 */

public class CommandLineExec extends TrianaExec {

    public static int MAX_LENGTH = 20;

    private Hashtable maptable = new Hashtable();
    private ArrayList desclist = new ArrayList();

    private String appname;
    private int argnum;


    /**
     * Constructs a TrianaExec to execute a clone of the specified taskgraph. Uses a default tool table
     */
    public CommandLineExec(String appname, TaskGraph taskgraph) throws TaskGraphException {
        super(taskgraph);
        this.appname = appname;
    }

    /**
     * Constructs a TrianaExec to execute a clone of the specified tool. Uses a default tool table.
     */
    public CommandLineExec(String appname, Tool tool) throws TaskGraphException {
        super(tool);
        this.appname = appname;
    }


    /**
     * Sets the number of required arguments
     */
    public void setNumberOfRequiredArguments(int argnum) {
        this.argnum = argnum;
    }

    /**
     * @return the number of required arguments
     */
    public int getNumberOfRequiredArguments() {
        return argnum;
    }

    /**
     * Sets the specified parameter, where paramname is a mapped paramname or of the form groupname.taskname.paramname
     */
    public void setParameter(String paramname, Object value) {
        String[] paramnames;
        Task subtask;

        if (isMapped(paramname)) {
            paramnames = getMappedParameters(paramname);
        } else {
            paramnames = new String[]{paramname};
        }

        for (int count = 0; count < paramnames.length; count++) {
            subtask = getTask(paramnames[count]);

            if (subtask != null) {
                subtask.setParameter(getParameterName(paramnames[count]), value);
            }
        }
    }

    /**
     * @return the specified parameter value, where paramname is a mapped paramname or of the form
     *         groupname.taskname.paramname (returns null if parameter not set). Note that if the paramname is mapped to
     *         multiple parameters then the value of one is returned (but it is not defined which one!).
     */
    public Object getParameter(String paramname) {
        Task subtask;

        if (isMapped(paramname)) {
            paramname = getMappedParameters(paramname)[0];
        }

        subtask = getTask(paramname);

        if (subtask != null) {
            return subtask.getParameter(getParameterName(paramname));
        } else {
            return null;
        }
    }


    /**
     * Maps a parameter of the form groupname.taskname.paramname to a simple string (e.g. file). At the commandline the
     * application user can write java TrianaExec -file afile.dat taskgraph.xml, as opposed to the full parameter name.
     * Note that a string can be mapped to multiple parameter names. Also note that the preceeding - should not be
     * included in the map string.
     */
    public void mapParameter(String map, String paramname) {
        mapParameter(map, paramname, null);
    }

    /**
     * Maps a parameter of the form groupname.taskname.paramname to a simple string (e.g. verbose), along with the value
     * that the parameter is set to (e.g. new Boolean(true)). At the commandline the application user can write java
     * TrianaExec -verbose taskgraph.xml. Note that a string can be mapped to multiple parameter names+values.
     */
    public void mapParameter(String map, String paramname, Object value) {
        if (map.startsWith("-")) {
            map = map.substring(1);
        }

        if (!maptable.containsKey(map)) {
            maptable.put(map, new ArrayList());
        }

        ArrayList maplist = (ArrayList) maptable.get(map);

        if (!maplist.contains(paramname)) {
            maplist.add(new Mapping(paramname, value));
        }
    }

    /**
     * Unmaps all the mappings from a string.
     */
    public void unmap(String map) {
        maptable.remove(map);
    }

    /**
     * @return true if the specified string is a map
     */
    public boolean isMapped(String map) {
        return maptable.containsKey(map);
    }

    /**
     * @return the parameter names mapped to the specified map
     */
    public String[] getMappedParameters(String map) {
        if (!maptable.containsKey(map)) {
            return new String[0];
        }

        ArrayList mappings = (ArrayList) maptable.get(map);
        String[] paramnames = new String[mappings.size()];

        for (int count = 0; count < paramnames.length; count++) {
            paramnames[count] = ((Mapping) mappings.get(count)).getParameterName();
        }

        return paramnames;
    }

    /**
     * @return the value mapped to the specified map/paramname pair (or null if not specified)
     */
    public boolean isMappedValue(String map, String paramname) {
        if (!maptable.containsKey(map)) {
            return false;
        }

        ArrayList mappings = (ArrayList) maptable.get(map);
        Mapping mapping;

        for (int count = 0; count < mappings.size(); count++) {
            mapping = (Mapping) mappings.get(count);

            if (mapping.getParameterName().equals(paramname)) {
                return mapping.getValue() != null;
            }
        }

        return false;

    }

    /**
     * @return the value mapped to the specified map/paramname pair (or null if not specified)
     */
    public Object getMappedValue(String map, String paramname) {
        if (!maptable.containsKey(map)) {
            return null;
        }

        ArrayList mappings = (ArrayList) maptable.get(map);
        Mapping mapping;

        for (int count = 0; count < mappings.size(); count++) {
            mapping = (Mapping) mappings.get(count);

            if (mapping.getParameterName().equals(paramname)) {
                return mapping.getValue();
            }
        }

        return null;
    }


    /**
     * Sets a description for the specified map string. This description can take the form "<item tag> main description"
     * if required.
     */
    public void setDescription(String map, String description) {
        removeDescription(map);
        desclist.add(new Description(map, description));
    }

    /**
     * Removes the description for the specified map
     */
    public void removeDescription(String map) {
        Description desc = getDescriptionItem(map);

        if (desc != null) {
            desclist.remove(desc);
        }
    }

    /**
     * @return the full description string for the specified map
     */
    public String getFullDescription(String map) {
        Description desc = getDescriptionItem(map);

        if (desc != null) {
            return desc.getDescription();
        } else {
            return null;
        }
    }

    /**
     * @return the description string for the specified map. If the description string was specified as "<item tag> main
     *         description" then only the main description is returned.
     */
    public String getDescription(String map) {
        Description desc = getDescriptionItem(map);

        if (desc != null) {
            String dstr = desc.getDescription();

            if (dstr.startsWith("<") && (dstr.indexOf('>') > -1)) {
                return dstr.substring(dstr.indexOf('>') + 1).trim();
            } else {
                return desc.getDescription().trim();
            }
        } else {
            return null;
        }
    }

    /**
     * @return the item tag for the specified map if the description was specified as "<item tag> main description".
     *         Otherwise null is returned.
     */
    public String getItemTag(String map) {
        Description desc = getDescriptionItem(map);

        if (desc != null) {
            String dstr = desc.getDescription();

            if (dstr.startsWith("<") && (dstr.indexOf('>') > -1)) {
                return dstr.substring(1, dstr.indexOf('>'));
            }
        }

        return null;
    }


    /**
     * @return the description instance for the specified map
     */
    private Description getDescriptionItem(String map) {
        Iterator desciter = desclist.iterator();
        Description desc;

        while (desciter.hasNext()) {
            desc = (Description) desciter.next();

            if (desc.getMap().equals(map)) {
                return desc;
            }
        }

        return null;
    }


    /**
     * @return the paramname for the specified groupname.taskname.paramname
     */
    private String getParameterName(String paramname) {
        if (paramname.indexOf('.') == -1) {
            return paramname;
        } else {
            return paramname.substring(paramname.indexOf('.') + 1);
        }
    }

    /**
     * @return the task for the specified groupname.taskname.paramname string
     */
    private Task getTask(String paramname) {
        if (paramname.indexOf('.') == -1) {
            return getTask();
        } else {
            String[] tasknames = paramname.split("\\.");
            Task subtask = getTask();

            for (int count = 0; count < tasknames.length - 1; count++) {
                if ((subtask == null) || (!(subtask instanceof TaskGraph))) {
                    return null;
                }

                subtask = ((TaskGraph) subtask).getTask(tasknames[count]);
            }

            return subtask;
        }
    }


    /**
     * Initialises the parameters for the specified command line arguments.
     *
     * @return either a help message or error message, or null if no message is needed.
     */
    public String initParameters(String[] args) {
        String maparg;
        String val;
        boolean map;
        boolean valused;
        int hashptr = 1;
        int ptr = 0;

        while (ptr < args.length) {
            maparg = args[ptr].substring(1);
            map = args[ptr].charAt(0) == '-';
            valused = false;

            if (ptr + 1 < args.length) {
                val = args[ptr + 1];
            } else {
                val = null;
            }

            if (map && maparg.equals("?")) {
                return getHelpMessage();
            } else if (map && isMapped(maparg)) {
                String[] params = getMappedParameters(maparg);

                for (int count = 0; count < params.length; count++) {
                    if (isMappedValue(maparg, params[count])) {
                        setParameter(params[count], getMappedValue(maparg, params[count]));
                    } else {
                        setParameter(params[count], val);
                        valused = true;
                    }
                }
            } else if (map && (getTask(maparg) != null)) {
                setParameter(maparg, val);
                valused = true;
            } else if (isMapped("#" + hashptr)) {
                setParameter("#" + hashptr, args[ptr]);
                hashptr++;
            } else {
                return "Error: Unknown argument/option " + args[ptr] + "\n\n" + getHelpMessage();
            }

            if (valused) {
                ptr++;
            }

            ptr++;
        }

        if (hashptr <= getNumberOfRequiredArguments()) {
            return "Error: Incomplete arguments\n\n" + getHelpMessage();
        }

        return null;
    }

    public String getHelpMessage() {
        Iterator iter = desclist.iterator();
        String mess = "";
        String tmp;
        Description desc;

        int optionlen = getOptionLength();

        mess += "Usage: java " + appname + " [options]";

        for (int count = 1; count <= getNumberOfRequiredArguments(); count++) {
            if (getItemTag("#" + count) != null) {
                mess += " <" + getItemTag("#" + count) + ">";
            } else {
                mess += " <#" + count + ">";
            }
        }

        mess += "\n";

        for (int count = 1; count <= getNumberOfRequiredArguments(); count++) {
            if ((getDescription("#" + count) != null) && (!getDescription("#" + count).equals(""))) {
                if (getItemTag("#" + count) != null) {
                    tmp = "    <" + getItemTag("#" + count) + ">";
                } else {
                    tmp = "    <#" + count + ">";
                }

                while (tmp.length() < optionlen) {
                    tmp += " ";
                }

                mess += tmp + getDescription("#" + count) + "\n";
            }
        }

        mess += "\n";

        if (desclist.size() > 0) {
            mess += "Where options include:\n";
        }

        while (iter.hasNext()) {
            desc = (Description) iter.next();

            if (!desc.getMap().startsWith("#")) {
                tmp = "    -" + desc.map + " ";

                if (getItemTag(desc.getMap()) != null) {
                    tmp += "<" + getItemTag(desc.getMap()) + ">  ";
                } else {
                    tmp += " ";
                }

                while (tmp.length() < optionlen) {
                    tmp += " ";
                }

                mess += tmp + getDescription(desc.getMap()) + "\n";
            }
        }

        return mess;
    }

    private int getOptionLength() {
        Iterator iter = desclist.iterator();
        int maxlength = 0;
        int length;
        Description desc;

        while (iter.hasNext()) {
            desc = (Description) iter.next();
            length = 5 + desc.getMap().length() + 2;

            if (getItemTag(desc.getMap()) != null) {
                length += 3 + getItemTag(desc.getMap()).length();
            }

            maxlength = Math.max(maxlength, length);
        }

        return Math.min(maxlength, MAX_LENGTH);
    }


    public static void main(String[] args) {
        System.out.println("CommandLineExec.main CALLED");
        if (args.length == 0) {
            System.out.println("Error: Taskgraph xml file not specified");
            System.out.println("Usage: java CommandLineExec <xmlfile>");
            return;
        }

        File file = new File(args[0]);

        if (!file.exists()) {
            System.out.println("File not found: " + file.getAbsolutePath());
            return;
        }

        try {
            EngineInit.init();

            XMLReader reader = new XMLReader(new FileReader(file));
            CommandLineExec exec = new CommandLineExec("CommandLineExec <xmlfile> ", reader.readComponent());

            String[] argsub = new String[args.length - 1];
            System.arraycopy(args, 1, argsub, 0, argsub.length);
            exec.initParameters(argsub);

            exec.run(new Object[0]);
            while (!exec.isFinished()) {
                System.out.println("CommandLineExec.main WAITING FOR TASKGRAPH TO FINISH");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
            exec.dispose();
        } catch (FileNotFoundException except) {
            System.out.println("File not found: " + file.getAbsolutePath());
        } catch (TaskGraphException except) {
            System.out.println("Invalid taskgraph file: " + file.getAbsolutePath());
        } catch (SchedulerException except) {
            System.out.println("Error running taskgraph: " + except.getMessage());
        } catch (IOException except) {
            except.printStackTrace();
        }
        System.exit(0);
    }


    private class Mapping {

        private String paramname;
        private Object value;


        public Mapping(String paramname) {
            this.paramname = paramname;
        }

        public Mapping(String paramname, Object value) {
            this.paramname = paramname;
            this.value = value;
        }


        public String getParameterName() {
            return paramname;
        }

        public Object getValue() {
            return value;
        }

    }


    private class Description {

        private String map;
        private String desc;

        public Description(String map, String desc) {
            this.map = map;
            this.desc = desc;
        }


        public String getMap() {
            return map;
        }

        public String getDescription() {
            return desc;
        }

    }

}
