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
package org.trianacode.util;


import org.trianacode.config.Locations;
import org.trianacode.gui.action.files.TaskGraphFileHandler;
import org.trianacode.gui.desktop.TrianaDesktopView;
import org.trianacode.gui.desktop.TrianaDesktopViewManager;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.hci.color.ColorTableEntry;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.clipin.HistoryClipIn;
import org.trianacode.taskgraph.ser.DocumentHandler;
import org.trianacode.taskgraph.ser.ObjectMarshaller;
import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.util.FileUtils;
import org.trianacode.taskgraph.util.Listing;
import org.w3c.dom.Element;

import java.applet.Applet;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Env allows a way of accessing various environment and system variables within Triana. Some methods convert from the
 * Java system properties to suitable labels which are of more use within Triana. To get the particular variable you
 * want just call any of these functions from within any unit e.g. :-</p> <p/> <center> Env.trianahome() </center> <p>
 * returns triana's getApplicationDataDir directory.</p><p>
 * <p/>
 * The toString() method is also useful for identifying all the system properties at run-time to make sure your system
 * is set up correctly. This message is printed out Triana or Triana is used.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 */
public final class Env {

    static Logger logger = Logger.getLogger("org.trianacode.util.Env");

    public static String TAB = "    ";

    public static final String DEFAULT_HISTORY_CLIPIN = "triana.types.clipins.DefaultHistoryClipIn";

    public static int BIG = 0;
    public static int LITTLE = 1;
    public static int endian = LITTLE;// windows machine by default ....

    private static boolean passwordOK = false;
    private static boolean runOut = false;
    private static final String version = "3.2.2";
    private static final String verName = "TrianaV4";
    public static String CONFIG_VERSION = "1.6.4";

    private static Vector classpaths;
    private static Vector helpdirs;

    //property names and values
    public static String CONFIG_STR = "config";
    public static String CONFIG_VERSION_STR = "version";
    public static String TOOLBOXES_STR = "toolboxes";
    public static String TOOLBOX_STR = "toolbox";
    public static String TOOLBOX_TYPE_STR = "type";
    public static final String TOOLBOX_VIRTUAL = "virtual";
    public static final String TOOLBOX_NAME = "name";
    public static String COLORS_STR = "colors";
    public static String COLOR_STR = "color";
    public static String TYPE_STR = "type";
    public static String NAME_STR = "name";
    public static String VALUE_STR = "value";
    public static String RED_STR = "red";
    public static String GREEN_STR = "green";
    public static String BLUE_STR = "blue";
    public static String COMPILER_STR = "compiler";
    public static String CLASSPATH_STR = "classpath";
    public static String OPTIONS_STR = "options";
    public static String EXCLUDED_TOOLS = "excluded_tools";
    public static String OPTION_STR = "option";
    public static String CODE_EDITOR_STR = "code_editor";
    public static String HELP_EDITOR_STR = "help_editor";
    public static String HELP_VIEWER_STR = "help_viewer";
    public static String HISTORY_TRACK = "history_track";
    public static String POPUP_DESC_STR = "popup_desc";
    public static String EXTENDED_POPUP = "extended_popup";
    public static String NODE_EDIT_ICONS = "node_edit_icons";
    public static String DEBUG_STR = "debug";
    public static String CONVERT_TO_DOUBLE_STR = "convert_to_double";
    public static String NONBLOCKING_OUT_STR = "nonblocking_out";
    public static String TIP_STR = "show_tip";
    public static String TIP_NUM_STR = "tip_number";
    public static String STATE_STR = "state_files";
    public static String OPEN_GROUPS = "open_groups";
    public static String FILES_STR = "files";
    public static String RECENT_STR = "recent";
    public static String FILE_STR = "file";
    public static String PARENT_STR = "parent";
    public static String CHILD_STR = "child";
    public static String DIRECTORY_STR = "directory";
    public static String WINDOW_POSITION_STR = "window_position";
    public static String WINDOW_SIZE_STR = "window_size";
    public static String DEBUG_VISIBLE_STR = "debug_visible";
    public static String DEBUG_POSITION_STR = "debug_position";
    public static String DEBUG_SIZE_STR = "debug_size";
    public static String ZOOM_FACTOR_STR = "zoom_factor";
    public static String FILE_READERS_STR = "file_readers";
    public static String FILE_WRITERS_STR = "file_writers";
    public static String READER_STR = "reader";
    public static String WRITER_STR = "writer";
    public static String FILE_EXTENSION_STR = "file_ext";
    public static String TOOL_NAME_STR = "tool_name";

    // directory types
    public static String DATA_DIRECTORY = "data";
    public static String TASKGRAPH_DIRECTORY = "taskgraph";
    public static String TOOL_DIRECTORY = "tool";
    public static String TOOLBOX_DIRECTORY = "toolbox";
    public static String UNIT_DIRECTORY = "unit";
    public static String COMPILER_DIRECTORY = "compiler";

    // default triana application size
    public static Dimension defaultsize = new Dimension(800, 600);

    /**
     * the default number of recent items to remember
     */
    private static int RECENT_ITEM_COUNT = 10;

    private static Hashtable options = new Hashtable();// name, value pairs
    private static Vector recentFileItems = new Vector(10);
    private static Vector savedTaskgraphs = new Vector();

    /**
     * Color Table Definitions
     * <p/>
     * this should replace the defunct cable colour stuff unless we are going to reinstate
     */
    private static Vector colorTableEntries = new Vector();
    public static final String COLOR_TABLE_STR = "colorTable";
    public static final String COLOR_TABLE_ENTRY_STR = "colorTableEntry";


    /**
     * hashtables of qualified unit names for file readers/writers, keyed by file type
     */
    private static Hashtable filereaders = new Hashtable();
    private static Hashtable filewriters = new Hashtable();
    private static Set<String> excludedTools = new HashSet<String>();

    private static WriteConfigThread writeConfigThread = null;
    private static WriteStateThread writeStateThread = null;

    private static boolean restoredFromDisk = false;

    public static String appletHome = null;


    /**
     * The resource bundle to store the messages to display within triana. These are taken from the
     * system/locale/triana_.._...properties file depending on which locale you are running in
     */
    static ResourceBundle messages = null;

    static ResourceBundle tips = null;

    /**
     * A list of all of the Triana Types.
     */
    public static Vector<String> allTypes = null;

    /**
     * Reference to the Triana getApplicationDataDir directory : Calculated in static {}
     */
    public static String home = null;

    public static String userHome = null;


    public static Applet applet = null;

    /**
     * Files and String names used in the config files and the locking mechanism
     */
    private static final String CONFIG_FILE = "triana.config";
    private static final String CONFIG_FILE_BAK = "triana_bak.config";
    private static File configFile;
    private static File configBakFile;
    private static final String LOCK_PREFIX = "config";
    private static final String LOCK_SUFFIX = "lock";

    private static final FileFilter lockFilter = new FileFilter() {
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(LOCK_SUFFIX);
        }
    };

    private static final long TIMEOUT = 20000;
    private static String resourceDir = null;
    private static String tempDir = "";


    /**
     * A private list of the user property change listeners
     */
    private static ArrayList listeners = new ArrayList();

    /**
     * A hashtable of the peer configuration states, keyed by peer type
     */
    private static Hashtable peerconfig = new Hashtable();

    /**
     * Initializes the Triana getApplicationDataDir
     */
    static {
        home = Locations.getApplicationDataDir();
    }

    /**
     * Adds a user property listener to Env
     */
    public static void addUserPropertyListener(UserPropertyListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a user property listener from Env
     */
    public static void removeUserPropertyListener(UserPropertyListener listener) {
        listeners.remove(listener);
    }

    public static void removeExcludedTool(String definitionPath) {
        excludedTools.remove(definitionPath);
    }

    public static void addExcludedTool(String definitionPath) {
        logger.fine("adding excluded tool:" + definitionPath);
        excludedTools.add(definitionPath);
    }

    public static boolean isExcludedTool(String definitionPath) {

        boolean b = excludedTools.contains(definitionPath);
        logger.fine("tool is excluded:" + definitionPath + "? " + b);
        return b;
    }

    /**
     * Notifies the property listeners when a user property value is updated
     */
    private static void notifyPropertyListeners(String propName, Object propValue) {
        UserPropertyListener[] copy = (UserPropertyListener[]) listeners.toArray(
                new UserPropertyListener[listeners.size()]);
        UserPropertyEvent event = new UserPropertyEvent(Env.class, propName, propValue);

        for (int count = 0; count < copy.length; count++) {
            copy[count].userPropertyChanged(event);
        }
    }


    /**
     * Initialise the user config
     *
     * @param write a flag indicating whether changes to the config are written back to the config file
     */
    public static void initConfig(boolean write) {
        logger.info("Configuring environment");

        configFile = new File(Env.getResourceDir() + Env.separator() + CONFIG_FILE);
        configBakFile = new File(Env.getResourceDir() + Env.separator() + CONFIG_FILE_BAK);

        if (!configFile.exists()) {// First time run so need to set up paths
            restoreDefaultConfig();
        } else {// createTool from the file
            GUIEnv.loadDefaultColours();
            try {
                Env.readConfig(configFile);
            }
            catch (Exception e) {
                logger.warning("Corrupt config file, restoring from backup");
                e.printStackTrace();
                try {
                    Env.readConfig(configBakFile);
                }
                catch (Exception e1) {
                    logger.warning("Corrupt backup file, restoring defaults");
                    restoreDefaultConfig();
                }
            }
        }
        if (write) {
            writeConfigThread = new WriteConfigThread();
            writeConfig();
            writeStateThread = new WriteStateThread();
        }
    }

    public static String getPath(String cls) throws IOException {
        return getPath(cls, Env.class.getClassLoader());
    }

    public static String getToolboxPath() throws IOException {
        return getPath("org.trianacode.toolbox.FindMe", Env.class.getClassLoader());
    }

    public static String getPath() throws IOException {
        return getPath("org.trianacode.util.Env", Env.class.getClassLoader());

    }

    public static String getPath(String cls, ClassLoader loader) throws IOException {
        URL u;

        String clsRes = cls.replace('.', '/') + ".class";
        u = loader.getResource(clsRes);

        if (u == null) {
            throw new IOException("no resource found with name " + cls);
        }
        String url = u.toString();
        System.out.println("Env.getPath MY RESOURCE:" + url);
        String delim = "jar!/";
        int jarIdx = url.indexOf(delim);
        if (jarIdx != -1) {

            String jar = url.substring(0, jarIdx + 3);
            if (jar.startsWith("jar:file:")) {
                jar = jar.substring(9, jar.length());
            }
            try {
                jar = URLDecoder.decode(jar, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String os = System.getProperty("os.name").toLowerCase(Locale.US);
            if (os.indexOf("windows") > -1) {
                jar = jar.substring(1, jar.length());
            }
            return jar;
        } else {

            int ind = url.indexOf(clsRes);
            if (ind > -1) {
                return url.substring(0, ind);
            }
            return null;
            // not in jar. need to find root directory we are in.
        }
    }


    /**
     * Restore the default user settings
     */
    private static void restoreDefaultConfig() {
        // This is crap code .... If not used then fine otherwise we need to fix it
        // it we restore to defaults, then we shoudl do this property in ApplicationFrame
        // not as a static method ....

        //throw new RuntimeException("Crap code alert - fix me");

        /// IAN T - BAD - need to fix this static reference - its the only one that causes issues.
        // created a throw away instance for now but needs fixing properly.

        /*       new TrianaInstance().getToolResolver().loadToolboxes();
 GUIEnv.loadDefaultColours();
 String defaultEditor = Env.getString("defaultEditor");
 setUserProperty(CODE_EDITOR_STR, defaultEditor);
 setUserProperty(HELP_EDITOR_STR, defaultEditor);
 setUserProperty(HELP_VIEWER_STR, Env.getString("defaultViewer"));      */
    }


    /**
     * Turns the debug information printed out to the MSDOS window on or off. This works in Applets or Applications
     */
    public static void setDebug(String deb) {
        String oldVal;
        if (deb.equals("on")) {
            oldVal = (String) setUserProperty(DEBUG_STR, "true");
        } else {
            oldVal = (String) setUserProperty(DEBUG_STR, "false");
        }
        if ((oldVal == null) || (!oldVal.equals(deb))) {
            writeConfig();
        }
    }


    /**
     * @return true if triana should convert to doubles whenever possible
     */
    public static boolean getConvertToDouble() {
        String convert = (String) getUserProperty(CONVERT_TO_DOUBLE_STR);
        if (convert == null) {
            setConvertToDouble(false);
            return false;
        }
        return (new Boolean(convert)).booleanValue();
    }

    /**
     * @return true if triana should use non-blocking output nodes
     */
    public static boolean isNonBlockingOutputNodes() {
        String nonBlock = (String) getUserProperty(NONBLOCKING_OUT_STR);
        if (nonBlock == null) {
            setNonBlockingOutputNodes(false);
            return false;
        }
        return (new Boolean(nonBlock)).booleanValue();
    }

    /**
     * Enables/disables triana should convert to doubles whenever possible
     */
    public static void setNonBlockingOutputNodes(boolean state) {
        setUserProperty(NONBLOCKING_OUT_STR, String.valueOf(state));
    }

    /**
     * Enables/disables triana should convert to doubles whenever possible
     */
    public static void setConvertToDouble(boolean state) {
        setUserProperty(CONVERT_TO_DOUBLE_STR, String.valueOf(state));
    }

    /**
     * Adds a name, value pair to the user configuration properties. The propValue object has to correctly override the
     * equals() method otherwise the property file will be written every time this method is called, even if the new
     * value is supposed to be the same as the old value.
     *
     * @return the old value or null if there was none.
     */
    public static Object setUserProperty(String propName, Object propValue) {
        Object oldVal = options.put(propName, propValue);
        if ((oldVal == null) || (!oldVal.equals(propValue))) {
            notifyPropertyListeners(propName, propValue);
            writeConfig();
        }
        return oldVal;
    }


    /**
     * Returns the user property, null if not found.
     */
    public static Object getUserProperty(String propName) {
        if (options.containsKey(propName)) {
            return options.get(propName);
        } else {
            return null;
        }
    }

    /**
     * @return the last toolbox to be used by the unit wizard or compile tool
     */
    public static String getLastWorkingToolbox() {
        String toolbox = (String) getUserProperty(DIRECTORY_STR + ":" + TOOLBOX_DIRECTORY);

        if (toolbox != null) {
            return toolbox;
        } else {
            setLastWorkingToolbox(Env.home() + "toolboxes");
            return Env.home() + "toolboxes";
        }
    }

    /**
     * Sets the last toolbox to be used by the unit wizard or compile tool
     *
     * @param toolbox
     */
    public static void setLastWorkingToolbox(String toolbox) {
        setDirectory(TOOLBOX_DIRECTORY, toolbox);
    }

    /**
     * Wrapper for (@link #getUserProperty}
     *
     * @return true if and only if the property exists and it has the value "true"
     */
    public static boolean getBooleanUserProperty(String propName) {
        Boolean prop = getBoolean(propName);
        if (prop != null) {
            return prop.booleanValue();
        }
        return false;
    }

    /**
     * Method to sort out the difference between storing our boolean value as a string or a boolean.
     */
    private static Boolean getBoolean(String propName) {
        Object value = getUserProperty(propName);
        if ((value == null) || (value instanceof Boolean)) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return new Boolean((String) value);
        } else {
            return null;
        }
    }

    /**
     * Wraper for {@link #getUserProperty} if the property does not exist then it is set to the defaultValue and that is
     * returned.
     */
    public static boolean getBooleanUserProperty(String propName, boolean defaultValue) {
        Boolean prop = getBoolean(propName);
        if (prop != null) {
            return prop.booleanValue();
        }
        setUserProperty(propName, new Boolean(defaultValue));
        return defaultValue;
    }

    /**
     * Returns Java's getApplicationDataDir directory
     */
    public final static String javaHome() {
        String java = System.getProperty("java.getApplicationDataDir");

        if (java == null) {
            return "";
        } else if (java.endsWith(File.separator + "jre")) {
            return java.substring(0, java.lastIndexOf(File.separator + "jre"));
        } else {
            return java;
        }
    }

    /**
     * Returns the user's getApplicationDataDir directory
     */
    public final static String userHome() {
        if (userHome == null) {
            userHome = System.getProperty("user.getApplicationDataDir");
            if (userHome == null || userHome.equals("") || userHome.equals("$")) {
                userHome = File.separator;
            }

        }
        return userHome;
    }

    /**
     * sets the compiler command to the specified path
     */
    public static void setCompilerCommand(String cmd) {
        setUserProperty(COMPILER_STR, cmd);
    }

    /**
     * gets the compiler command
     */
    public static String getCompilerCommand() {
        String compilerCmd = (String) getUserProperty(COMPILER_STR);
        if ((compilerCmd != null) && (!compilerCmd.equals(""))) {
            return compilerCmd;
        }
        return getDefaultCompilerCommand();
    }

    /**
     * @return the default compiler (from java.getApplicationDataDir)
     */
    public static String getDefaultCompilerCommand() {
        String home = Env.javaHome();

        File file = new File(home + Env.separator() + "bin" + Env.separator() + "javac");
        if (file.exists()) {
            return file.getAbsolutePath();
        }

        file = new File(home + Env.separator() + "bin" + Env.separator() + "javac.exe");
        if (file.exists()) {
            return file.getAbsolutePath();
        }

        if (home.lastIndexOf(Env.separator()) > -1) {
            home = home.substring(0, home.lastIndexOf(Env.separator()));

            file = new File(home + Env.separator() + "bin" + Env.separator() + "javac");
            if (file.exists()) {
                return file.getAbsolutePath();
            }

            file = new File(home + Env.separator() + "bin" + Env.separator() + "javac.exe");
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        }

        return "";
    }

    public static String getJavacArgs() {
        return "";
    }


    public final static String separator() {
        return File.separator;
    }

    /**
     * Returns the machines operating system. This returns a single word which identifies the particular operating
     * system. The identifier is returned in lower case so it can be used to identify directories of where various
     * things are stored for different platforms e.g. </p><p> <ol> <li> Windows 95/NT - <i>windows</i> is returned <li>
     * Solaris - <i>solaris</i> is returned <li> IRIX - <i>irix</i> is returned <li> DEC - <i>dec</i> is returned <li>
     * LINUX  - <i>linux</i> is returned </li>
     */
    public final static String os() {
        return Locations.os();
    }

    public int getEndian() {
        String os = os();
        if ((os.equals("windows")) || (os.equals("linux")) || (os.equals("dec"))) {
            endian = LITTLE;
        } else {
            endian = BIG;
        }
        return endian;
    }

    /**
     * The ending for the native libraries i.e. .so on unix and .dll on window 95/NT, .jnilib for Mac OS X
     */
    public final static String getSharedLibSuffix() {
        String os = os();
        if (os.equals("windows")) {
            return ".dll";
        } else if (os.equals("osx")) {
            return ".jnilib";
        } else {
            return ".so";
        }
    }

    /**
     * The prefix for native libraries, nothing for windows, lib for *nix systems.
     */
    public final static String getSharedLibPrefix() {
        String os = os();
        if (os.equals("windows")) {
            return "";
        } else {
            return "lib";
        }
    }

    /**
     * Triana's platform specific directory for native shared libraries.
     */
    public final static String getSharedLibPath() {
        return home() + "lib" + separator() + os() + separator();
    }

    /**
     * Returns the location of the plugin directory used to store project specific plugins to be loaded at run time, for
     * example filters and import/export tools.
     *
     * @return the plugin directory location
     */
    public final static String getPluginDir() {
        return home() + "plugins";
    }

    /**
     * Returns the machine architecture. This returns a single word which identifies the particular platform e.g.
     * </p><p> <ol> <li> Windows 95/NT - <i>Windows</i> is returned <li> Solaris - <i>Solaris</i> is returned <li> IRIX
     * - <i>Irix</i> is returned <li> DEC - <i>Dec</i> is returned <li> LINUX  - <i>Linux</i> is returned </li>
     */
    public final static String arch() {
        return System.getProperty("os.arch");
    }

    /**
     * Returns the operating system version number
     */
    public final static String osVer() {
        return System.getProperty("os.version");
    }

    /**
     * Returns the Triana's version number
     */
    public final static String getVersion() {
        return version;
    }

    /**
     * Returns the java version number
     */
    public final static String javaVersion() {
        return System.getProperty("java.version");
    }

    /**
     * Returns the TRIANA environment variable.
     */
    public final static String home() {
        return Locations.getApplicationDataDir();
    }

    /**
     * Returns Java's CLASSPATH variable. This is the actual CLASSPATH environment variable.
     */
    public final static String getClasspath() {
        String saved = (String) getUserProperty(CLASSPATH_STR);
        if ((saved == null) || saved.equals("")) {
            return System.getProperty("java.class.path");
        }
        return saved;
    }

    public final static String getSystemClasspath() {
        return System.getProperty("java.class.path");
    }

    /**
     * @param path the classpath to save.
     */
    public final static void setClasspath(String path) {
        setUserProperty(CLASSPATH_STR, path);
    }


    /**
     * Returns the Windows getApplicationDataDir directory. Obviously just works for windows.
     */
    public final static String windir() {
        return System.getProperty("windir").trim();
    }

    /**
     * @return The user name from the JDK "user.name"
     */
    public static String getUserName() {
        return System.getProperty("user.name");
    }


    /**
     * @return the absolute path to the user's temp directory
     */
    public static final String getTempDir() {
        if (tempDir.equals("")) {
            tempDir = getResourceDir() + separator() + "temp";
            File dir = new File(tempDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
        return tempDir;
    }

    /**
     * @return the absolute path to the user resource directory.
     */
    public final static synchronized String getResourceDir() {
        return home();
    }


    /**
     * @return the last directory used to access the specified directory type (@see triana.util.TFileChooser)
     */
    public final static String getDirectory(String dirtype) {
        String lastdir = (String) getUserProperty(DIRECTORY_STR + ":" + dirtype);

        if (lastdir != null) {
            return lastdir;
        } else {
            File f = new File(dirtype);
            if (f.exists()) {
                return dirtype;
            }
            return userHome();
        }
    }

    /**
     * Sets the last directory used to access the specified directory type (@see triana.util.TFileChooser)
     */
    public final static void setDirectory(String dirtype, String dir) {
        setUserProperty(DIRECTORY_STR + ":" + dirtype, dir);
    }


    /**
     * Sets the last screen position of the Triana application window
     */
    public final static void setWindowPosition(Point position) {
        setUserProperty(WINDOW_POSITION_STR, position);
    }

    /**
     * @return the last screen position of the Triana window
     */
    public final static Point getWindowPosition() {
        Object position = getUserProperty(WINDOW_POSITION_STR);

        if (position == null) {
            return new Point(0, 0);
        } else {
            return (Point) position;
        }
    }

    /**
     * Sets the last dimension of the Triana application window
     */
    public final static void setWindowSize(Dimension size) {
        setUserProperty(WINDOW_SIZE_STR, size);
    }

    /**
     * @return the last dimension of the Triana window
     */
    public final static Dimension getWindowSize() {
        Object size = getUserProperty(WINDOW_SIZE_STR);

        if (size == null) {
            return defaultsize;
        } else {
            return (Dimension) size;
        }
    }


    /**
     * Sets whether the debug console is visible
     */
    public final static void setDebugVisible(boolean state) {
        setUserProperty(DEBUG_VISIBLE_STR, String.valueOf(state));
    }

    /**
     * @return true if the debug console is visible
     */
    public final static boolean isDebugVisible() {
        Object visible = getUserProperty(DEBUG_VISIBLE_STR);

        if (visible == null) {
            return false;
        } else {
            return new Boolean((String) visible).booleanValue();
        }
    }

    public final static void setTabbedView(boolean tabbed){
        setUserProperty("TabbedView", String.valueOf(tabbed));
    }

    public final static boolean isTabbedView() {
        Object tabbed = getUserProperty("TabbedView");

        if (tabbed == null) {
            return false;
        } else {
            return new Boolean((String) tabbed).booleanValue();
        }
    }

    /**
     * Sets the last screen position of the debug console
     */
    public final static void setDebugPosition(Point position) {
        setUserProperty(DEBUG_POSITION_STR, position);
    }

    /**
     * @return the last screen position of the debug console
     */
    public final static Point getDebugPosition() {
        Object position = getUserProperty(DEBUG_POSITION_STR);

        if (position == null) {
            return new Point(0, 0);
        } else {
            return (Point) position;
        }
    }

    /**
     * Sets the last dimension of the debug console
     */
    public final static void setDebugSize(Dimension size) {
        setUserProperty(DEBUG_SIZE_STR, size);
    }

    /**
     * @return the last dimension of the debug console
     */
    public final static Dimension getDebugSize() {
        Object size = getUserProperty(DEBUG_SIZE_STR);

        if ((size == null) || (!(size instanceof Dimension))) {
            return defaultsize;
        } else {
            return (Dimension) size;
        }
    }

    /**
     * Sets the current gui zoom factor
     */
    public final static void setZoomFactor(double zoom) {
        setUserProperty(ZOOM_FACTOR_STR, new Double(zoom));
    }

    /**
     * @return the current gui zoom factor
     */
    public final static double getZoomFactor() {
        Object zoom = getUserProperty(ZOOM_FACTOR_STR);

        if ((zoom == null) || (!(zoom instanceof Double))) {
            return 1;
        } else {
            return ((Double) zoom).doubleValue();
        }
    }


    /**
     * Sets the file reader for the specified file type
     *
     * @param fileext  the file extension
     * @param toolname the qualified tool name of the reader
     * @return the old value (or null if not set)
     */
    public final static String setFileReader(String fileext, String toolname) {
        String oldVal = (String) filereaders.put(fileext.toLowerCase(), toolname);
        if ((oldVal == null) || (!oldVal.equals(fileext.toLowerCase()))) {
            writeConfig();
        }

        return oldVal;
    }

    /**
     * Sets the file reader for the specified file type
     *
     * @param fileext the file extension
     * @return the qualified tool name of the removed reader (or null if not set)
     */
    public final static String removeFileReader(String fileext) {
        return (String) filereaders.remove(fileext.toLowerCase());
    }

    /**
     * @return the file reader for the specified file type (or null if not set)
     */
    public final static String getFileReader(String fileext) {
        if (filereaders.containsKey(fileext.toLowerCase())) {
            return (String) filereaders.get(fileext.toLowerCase());
        } else {
            return null;
        }
    }

    /**
     * @return an array of the file extensions for which readers are set
     */
    public final static String[] getFileReaderExtensions() {
        return (String[]) filereaders.keySet().toArray(new String[filereaders.keySet().size()]);
    }

    /**
     * @param fileext the file extension
     * @return true if a file reader is set for the specified extension
     */
    public final static boolean isFileReader(String fileext) {
        return filereaders.containsKey(fileext.toLowerCase());
    }


    /**
     * Sets the file writer for the specified file type
     *
     * @param fileext  the file extension
     * @param toolname the qualified tool name of the writer
     * @return the old value (or null if not set)
     */
    public final static String setFileWriter(String fileext, String toolname) {
        String oldVal = (String) filewriters.put(fileext.toLowerCase(), toolname);
        if ((oldVal == null) || (!oldVal.equals(fileext.toLowerCase()))) {
            writeConfig();
        }

        return oldVal;
    }

    /**
     * Sets the file writer for the specified file type
     *
     * @param fileext the file extension
     * @return the qualified tool name of the removed writer (or null if not set)
     */
    public final static String removeFileWriter(String fileext) {
        return (String) filewriters.remove(fileext.toLowerCase());
    }

    /**
     * @return the file writer for the specified file type (or null if not set)
     */
    public final static String getFileWriter(String fileext) {
        if (filewriters.containsKey(fileext.toLowerCase())) {
            return (String) filewriters.get(fileext.toLowerCase());
        } else {
            return null;
        }
    }

    /**
     * @return an array of the file extensions for which writers are set
     */
    public final static String[] getFileWriterExtensions() {
        return (String[]) filewriters.keySet().toArray(new String[filewriters.keySet().size()]);
    }

    /**
     * @param fileext the file extension
     * @return true if a file writer is set for the specified extension
     */
    public final static boolean isFileWriter(String fileext) {
        return filewriters.containsKey(fileext.toLowerCase());
    }


    /**
     * updates the initialisation file with the new variables i.e. does a save state.
     */
    public static void writeConfig() {
        if (writeConfigThread != null) {
            writeConfigThread.write();
        }
    }

    public static boolean stopConfigWriters() {
        if (writeStateThread != null) {
            writeStateThread.stopThread();
            while (writeStateThread.isAlive()) {
                try {
                    Thread.sleep(200);
                }
                catch (InterruptedException except) {
                }
            }
        }

        if (writeConfigThread != null) {
            writeConfigThread.stopThread();
            while (writeConfigThread.isAlive()) {
                try {
                    Thread.sleep(200);
                }
                catch (InterruptedException except) {
                }
            }
        }

        return true;
    }

    /**
     * Write the state of the desk top to disk for auto loading on restart
     */
    public static void saveWorkingState() {
        if (writeStateThread == null) {
            writeStateThread = new WriteStateThread();
        }
        writeStateThread.write();
    }

    /**
     * createTool in the last worked on task graphs from disk
     *
     * @return true if task graphs are loaded, false otherwise
     */
    public static boolean readStateFiles() {
        if (savedTaskgraphs.size() > 0) {
            for (Iterator iterator = savedTaskgraphs.iterator(); iterator.hasNext();) {
                OpenTaskGraph info = (OpenTaskGraph) iterator.next();
                String fullpath = Env.getTempDir() + Env.separator() + info.getRootFileName();
                if (!FileUtils.fileExists(fullpath)) {
                    logger.warning("Error loading previous work, file doesn't exist: " + fullpath);
                    restoredFromDisk = true;
                    return false;
                }
                TaskGraph taskgraph = TaskGraphFileHandler.openTaskgraph(new File(fullpath), false);
                if (taskgraph != null) {
                    OpenTaskGraph[] openChildren = info.getChildren();
                    for (int i = 0; i < openChildren.length; i++) {
                        recursivelyOpenChildWindows(openChildren[i], taskgraph);
                    }
                }
            }
            restoredFromDisk = true;
            return true;
        } else {
            restoredFromDisk = true;
            return false;
        }
    }

    private static void recursivelyOpenChildWindows(OpenTaskGraph toOpen, TaskGraph parent) {
        Task task = parent.getTask(toOpen.getName());
        if ((task != null) && (task instanceof TaskGraph)) {
            TrianaDesktopView panel = GUIEnv.getDesktopViewFor(parent);
            GUIEnv.getApplicationFrame().addChildTaskGraphPanel((TaskGraph) task, panel.getTaskgraphPanel().getTrianaClient());
        }
        OpenTaskGraph[] openChildren = toOpen.getChildren();
        for (int i = 0; i < openChildren.length; i++) {
            recursivelyOpenChildWindows(openChildren[i], (TaskGraph) task);
        }
    }

    /**
     * Write all current open taskgraphs in the workspace to disc
     */
    private static void writeStateFiles() {
        if (GUIEnv.restoreLast()) {
            if (restoredFromDisk) {
                clearStateFiles();
                TaskGraphPanel[] windows = GUIEnv.getApplicationFrame().getRootTaskGraphPanels();
                // Add the parent task graphs
                for (int i = 0; i < windows.length; i++) {
                    if (windows[i].getTaskComponentCount() > 0) {
                        String savedPath = saveTaskGraphContainer(windows[i]);
                        OpenTaskGraph info = new OpenTaskGraph();
                        info.setRootFileName(savedPath);
                        savedTaskgraphs.add(info);
                        TaskGraphPanel[] childwindows = GUIEnv.getApplicationFrame().getChildTaskGraphPanels(
                                windows[i]);
                        for (int j = 0; j < childwindows.length; j++) {
                            recursivelySaveChildWindows(childwindows[j], info);

                        }
                    }
                }
            }
        } else {
            clearStateFiles();
        }
    }

    private static void recursivelySaveChildWindows(TaskGraphPanel window, OpenTaskGraph parent) {
        OpenTaskGraph child = new OpenTaskGraph();
        child.setName(window.getTaskGraph().getToolName());
        parent.addChild(child);
        TaskGraphPanel[] childwindows = GUIEnv.getApplicationFrame().getChildTaskGraphPanels(window);
        for (int i = 0; i < childwindows.length; i++) {
            recursivelySaveChildWindows(childwindows[i], child);
        }
    }

    /**
     * remove all the old state files
     */
    private static void clearStateFiles() {
        savedTaskgraphs.clear();
        File[] oldFiles = (new File(Env.getTempDir())).listFiles();
        for (int i = 0; i < oldFiles.length; i++) {
            oldFiles[i].delete();
        }
    }

    /**
     * Save any open MainTriana window taskgraphs for reopening next time Triana is run. Only save a window if it is a
     * top level taskgraph, i.e. don't save the taskgraphs for any windows that are displaying a sub group. If a group
     * window is open then set the property that so that it will be reopened at start up once it's parent taskgraph is
     * loaded.
     *
     * @param cont the MainTriana we are testing to save
     * @return The name of the tempory file to save to or null if nothing has been saved.
     */
    private static String saveTaskGraphContainer(TaskGraphPanel cont) {
        TrianaDesktopViewManager manager = GUIEnv.getApplicationFrame().getDesktopViewManager();
        String windowName = manager.getTitle(manager.getDesktopViewFor(cont));
        StringBuffer name = new StringBuffer(windowName);
        name.append("_");
        name.append(System.currentTimeMillis());
        name.append(".taskgraph");
        String fileName = Env.getTempDir() + Env.separator() + name.toString();
        TaskGraphFileHandler.saveTaskGraphAs((Tool) cont.getTaskGraph(), fileName, null, false);
        return name.toString();
    }


    /**
     * @return true if the specified peer type is enabled
     */
    public static boolean isPeerEnabled(String type) {
        if (peerconfig.containsKey(type)) {
            return new Boolean((String) peerconfig.get(type)).booleanValue();
        } else {
            return true;
        }
    }

    /**
     * Write a marker file to indicate we are writing to the config file, this is a blocking call that is only called
     * from with the write config file thread. Calling from anywhere else may have a detrimental effect on performance.
     *
     * @return the name of the temporary marker file.
     */
    private static File aquireConfigFileLock() {
        File lockFile = null;
        File resourceDir = new File(Env.getResourceDir());
        File[] locks = resourceDir.listFiles(lockFilter);
        boolean lockExists = locks.length > 0;
        while (lockExists) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
            }
            lockExists = false;
            for (int i = 0; i < locks.length; i++) {
                lockExists = locks[i].exists();
                if (lockExists && ((System.currentTimeMillis() - locks[i].lastModified()) > TIMEOUT)) {
                    locks[i].delete();
                    lockExists = false;
                }
            }
        }
        try {
            lockFile = File.createTempFile(LOCK_PREFIX, LOCK_SUFFIX, resourceDir);
            lockFile.deleteOnExit();
        }
        catch (IOException e) {
        }
        return lockFile;
    }

    /**
     * Release the marker file.
     *
     * @param marker
     */
    private static void releaseConfigFileLock(File marker) {
        marker.delete();
    }

    /**
     * Write the triana configuration files to disk
     */
    private static void writeConfigFile() {
        File marker = aquireConfigFileLock();

        PrintWriter bw = null;
        try {

            bw = new PrintWriter(new BufferedWriter(new FileWriter(configFile)));
            DocumentHandler handler = new DocumentHandler();

            // create the root element with the file format version
            Element root = handler.element(CONFIG_STR);
            root.setAttribute(CONFIG_VERSION_STR, CONFIG_VERSION);
            handler.setRoot(root);

            // add tool colors
            Element colorsElem = handler.element(COLORS_STR);
            handler.add(colorsElem, root);
            TreeMap colourMap = GUIEnv.getCableColours();
            Object[] keys = colourMap.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                String tooltype = (String) keys[i];
                Color color = (Color) colourMap.get(tooltype);
                Element colorElem = handler.element(COLOR_STR);
                handler.add(colorElem, colorsElem);
                Element typeElem = handler.element(TYPE_STR);
                typeElem.setAttribute(NAME_STR, tooltype);
                handler.add(typeElem, colorElem);
                Element valueElem = handler.element(VALUE_STR);
                valueElem.setAttribute(RED_STR, String.valueOf(color.getRed()));
                valueElem.setAttribute(GREEN_STR, String.valueOf(color.getGreen()));
                valueElem.setAttribute(BLUE_STR, String.valueOf(color.getBlue()));
                handler.add(valueElem, colorElem);
            }

            // options
            Element optionsElem = handler.element(OPTIONS_STR);
            handler.add(optionsElem, root);

            keys = options.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                String key = (String) keys[i];
                Element optionElem = handler.element(OPTION_STR);

                optionElem.setAttribute(NAME_STR, key);
                Element val = handler.element(VALUE_STR);
                handler.add(ObjectMarshaller.marshallJavaToElement(val, options.get(key)), optionElem);
                handler.add(optionElem, optionsElem);
            }
            Element excludes = handler.element(EXCLUDED_TOOLS);
            handler.add(excludes, root);
            for (String excludedTool : excludedTools) {
                Element excl = handler.element(NAME_STR);
                handler.add(excludedTool, excl);
                handler.add(excl, excludes);
            }
            //color table entries
            if (!colorTableEntries.isEmpty()) {
                Element colorTableElem = handler.element(COLOR_TABLE_STR);
                handler.add(colorTableElem, root);

                for (int i = 0; i < colorTableEntries.size(); i++) {
                    ColorTableEntry colorTableEntry = (ColorTableEntry) colorTableEntries.elementAt(i);
                    Element ctEntryElem = handler.element(COLOR_TABLE_ENTRY_STR);
                    handler.add(ctEntryElem, colorTableElem);
                    ctEntryElem.setAttribute(NAME_STR, colorTableEntry.getColorname());
                    Element val = handler.element(VALUE_STR);
                    handler.add(ObjectMarshaller.marshallJavaToElement(val, colorTableEntry.getColor()), ctEntryElem);
                }
            }

            // files
            Element fileElem = null;
            String[] recent = getRecentFilePaths();

            if (recent.length > 0) {
                fileElem = handler.element(FILES_STR);
                handler.add(fileElem, root);

                for (int i = 0; i < recent.length; i++) {
                    if (!recent[i].equals("")) {
                        Element recentElem = handler.element(RECENT_STR);
                        handler.add(recentElem, fileElem);
                        Element elem = handler.element(FILE_STR);
                        elem.setAttribute(VALUE_STR, recent[i]);
                        handler.add(elem, recentElem);
                    }
                }
            }

            if (savedTaskgraphs.size() > 0) {
                if (fileElem == null) {
                    fileElem = handler.element(FILES_STR);
                    handler.add(fileElem, root);
                }
                for (Iterator iterator = savedTaskgraphs.iterator(); iterator.hasNext();) {
                    OpenTaskGraph info = (OpenTaskGraph) iterator.next();
                    Element openElem = handler.element(OPEN_GROUPS);
                    handler.add(openElem, fileElem);
                    Element elem = handler.element(FILE_STR);
                    elem.setAttribute(VALUE_STR, info.getRootFileName());
                    handler.add(elem, openElem);
                    OpenTaskGraph[] openChildren = info.getChildren();
                    for (int i = 0; i < openChildren.length; i++) {
                        recursivelySaveChildTaskGraphElems(openChildren[i], openElem, handler);
                    }
                }
            }

            if (filereaders.size() > 0) {
                Element readersElem = handler.element(FILE_READERS_STR);
                handler.add(readersElem, root);

                Enumeration en = filereaders.keys();
                Element readerElem;
                String ext;

                while (en.hasMoreElements()) {
                    ext = (String) en.nextElement();

                    readerElem = handler.element(READER_STR);
                    readerElem.setAttribute(FILE_EXTENSION_STR, ext);
                    readerElem.setAttribute(TOOL_NAME_STR, (String) filereaders.get(ext));
                    handler.add(readerElem, readersElem);
                }
            }

            if (filewriters.size() > 0) {
                Element writersElem = handler.element(FILE_WRITERS_STR);
                handler.add(writersElem, root);

                Enumeration en = filewriters.keys();
                Element writerElem;
                String ext;

                while (en.hasMoreElements()) {
                    ext = (String) en.nextElement();

                    writerElem = handler.element(WRITER_STR);
                    writerElem.setAttribute(FILE_EXTENSION_STR, ext);
                    writerElem.setAttribute(TOOL_NAME_STR, (String) filewriters.get(ext));
                    handler.add(writerElem, writersElem);
                }
            }

            handler.output(bw, true);
            FileUtils.copyFile(configFile.getAbsolutePath(), configBakFile.getAbsolutePath(), FileUtils.ASCII);

        }
        catch (IOException e) {
            logger.severe("Error writing to user config file: " + configFile.getAbsolutePath() + ":" + FileUtils
                    .formatThrowable(e));
        }
        finally {
            FileUtils.closeWriter(bw);
            releaseConfigFileLock(marker);
        }
    }


    private static void recursivelySaveChildTaskGraphElems(OpenTaskGraph childTG, Element parent,
                                                           DocumentHandler handler) {
        Element childElem = handler.element(CHILD_STR);
        handler.add(childElem, parent);
        Element nameElem = handler.element(NAME_STR);
        nameElem.setAttribute(VALUE_STR, childTG.getName());
        handler.add(nameElem, childElem);
        OpenTaskGraph[] children = childTG.getChildren();
        for (int i = 0; i < children.length; i++) {
            recursivelySaveChildTaskGraphElems(children[i], childElem, handler);
        }
    }

    /**
     * Loads in the initialisation file and sets the relevant new variables i.e. does a save state.
     */
    private static void readConfig(File file) throws Exception {
        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(file));
            logger.info("Restoring From Triana Property File : " + file.getAbsolutePath());
            DocumentHandler handler = new DocumentHandler(br);

            Element root = handler.root();

            if (!root.getLocalName().equals(CONFIG_STR) || (root.getAttribute(CONFIG_VERSION_STR) == null)) {
                throw (new Exception("Corrupt config file: " + file.getAbsolutePath()));
            }

            String versionStr = root.getAttribute(CONFIG_VERSION_STR);
            if (!versionStr.equals(CONFIG_VERSION)) {
                logger.warning(
                        "Current config version is " + CONFIG_VERSION + " Attempting to load your config version "
                                + versionStr);
            }

            List elementList = handler.getChildren(handler.getChild(root, EXCLUDED_TOOLS), NAME_STR);
            Iterator iter = elementList.iterator();
            while (iter.hasNext()) {
                Element name = (Element) iter.next();
                String txt = name.getTextContent().trim();
                addExcludedTool(txt);
            }

            elementList = handler.getChildren(handler.getChild(root, COLORS_STR));
            iter = elementList.iterator();
            while (iter.hasNext()) {
                Element colorElem = (Element) iter.next();
                String typeStr = handler.getChild(colorElem, TYPE_STR).getAttribute(NAME_STR);
                Element valElem = handler.getChild(colorElem, VALUE_STR);
                int redVal = Integer.parseInt(valElem.getAttribute(RED_STR));
                int greenVal = Integer.parseInt(valElem.getAttribute(GREEN_STR));
                int blueVal = Integer.parseInt(valElem.getAttribute(BLUE_STR));
                Color c = new Color(redVal, greenVal, blueVal);
                GUIEnv.setCableColor(typeStr, c);
            }

            elementList = handler.getChildren(handler.getChild(root, OPTIONS_STR));
            iter = elementList.iterator();
            while (iter.hasNext()) {
                Element optionElem = (Element) iter.next();
                String nameStr = optionElem.getAttribute(NAME_STR);

                Object value = ObjectMarshaller.marshallElementToJava(optionElem);
                setUserProperty(nameStr, value);
            }


            // color table elements
            Element colorTableElem = handler.getChild(root, COLOR_TABLE_STR);
            if (colorTableElem != null) {
                elementList = handler.getChildren(colorTableElem);
                iter = elementList.iterator();
                while (iter.hasNext()) {
                    Element ctEntryElem = (Element) iter.next();
                    String colorName = ctEntryElem.getAttribute(NAME_STR);
                    Color value = (Color) ObjectMarshaller.marshallElementToJava(ctEntryElem);
                    ColorTableEntry ctEntry = new ColorTableEntry(colorName, value);
                    colorTableEntries.add(ctEntry);
                }
            }

            Element filesElem = handler.getChild(root, FILES_STR);
            if (filesElem != null) {
                elementList = handler.getChildren(filesElem, RECENT_STR);
                iter = elementList.iterator();
                while (iter.hasNext()) {
                    Element recent = (Element) iter.next();
                    addRecentFilePath(handler.getChild(recent, FILE_STR).getAttribute(VALUE_STR));
                }

                elementList = handler.getChildren(filesElem, OPEN_GROUPS);
                iter = elementList.iterator();
                while (iter.hasNext()) {
                    Element open = (Element) iter.next();
                    Element fileElem = handler.getChild(open, FILE_STR);
                    OpenTaskGraph info = new OpenTaskGraph();
                    info.setRootFileName(fileElem.getAttribute(VALUE_STR));
                    List childElemList = handler.getChildren(open, CHILD_STR);
                    for (Iterator iterator = childElemList.iterator(); iterator.hasNext();) {
                        recursivelyLoadChildTaskGraphElements(info, (Element) iterator.next(), handler);
                    }
                    savedTaskgraphs.add(info);
                }
            }

            Element readersElem = handler.getChild(root, FILE_READERS_STR);
            Element reader;

            if (readersElem != null) {
                elementList = handler.getChildren(readersElem, READER_STR);
                iter = elementList.iterator();
                while (iter.hasNext()) {
                    reader = (Element) iter.next();
                    filereaders.put(reader.getAttribute(FILE_EXTENSION_STR),
                            reader.getAttribute(TOOL_NAME_STR));
                }
            }

            Element writersElem = handler.getChild(root, FILE_WRITERS_STR);
            Element writer;

            if (writersElem != null) {
                elementList = handler.getChildren(writersElem, WRITER_STR);
                iter = elementList.iterator();
                while (iter.hasNext()) {
                    writer = (Element) iter.next();
                    filewriters.put(writer.getAttribute(FILE_EXTENSION_STR),
                            writer.getAttribute(TOOL_NAME_STR));
                }
            }

            // fix an old bug in some old util files
            if (getUserProperty(HELP_VIEWER_STR) != null && getUserProperty(HELP_VIEWER_STR).equals(Env.getString("defaultEditor"))) {
                setUserProperty(HELP_VIEWER_STR, Env.getString("defaultViewer"));
            }

        }
        catch (Exception e) {
            throw (e);
        }
        finally {
            FileUtils.closeReader(br);
        }
    }

    private static void recursivelyLoadChildTaskGraphElements(OpenTaskGraph parent, Element childElem,
                                                              DocumentHandler handler) {
        Element nameElem = handler.getChild(childElem, NAME_STR);
        OpenTaskGraph info = new OpenTaskGraph();
        info.setName(nameElem.getAttribute(VALUE_STR));
        parent.addChild(info);
        List childElemList = handler.getChildren(childElem, CHILD_STR);
        for (Iterator iterator = childElemList.iterator(); iterator.hasNext();) {
            recursivelyLoadChildTaskGraphElements(info, (Element) iterator.next(), handler);
        }
    }

    /**
     * Returns all of the class paths Triana searches through.
     */
    public final static Vector allClasspaths() {
        return classpaths;
    }

    /**
     * adds an item to the classpath list
     *
     * @return -1 if a invalid URL path is given
     */
    public final static int addClasspath(Object classpath) {
        if (classpaths == null) {
            classpaths = new Vector(10);
        }
        return addToVar(classpaths, classpath);
    }

    /**
     * Removes the classpath at the specified position within the classpaths Vector
     */
    public final static void removeClasspath(int classpathNo) {
        classpaths.removeElementAt(classpathNo);
    }


    /**
     * @return the default clip-in used for history tracking
     */
    public final static HistoryClipIn getDefaultHistoryClipIn() {
        try {
            Class cls = Class.forName(DEFAULT_HISTORY_CLIPIN);
            return (HistoryClipIn) cls.newInstance();
        }
        catch (Exception except) {
            throw (new RuntimeException(
                    "Error instantiating default history tracking clip-in: " + DEFAULT_HISTORY_CLIPIN));
        }
    }


    /**
     * Returns the Triana GRID_HELP environmental variable.
     */
    public final static Vector helpdirs() {
        return helpdirs;
    }

    /**
     * adds an item to the help directory list
     *
     * @return -1 if a invalid URL path is given
     */
    public final static int addHelpdir(String helpDir) {
        if (helpdirs == null) {
            helpdirs = new Vector(10);
        }
        return addToVar(helpdirs, helpDir);
    }

    /**
     * Removes the help directory at the specified position within the helpDir Vector
     */
    public final static void removeHelpdir(int helpDirNo) {
        helpdirs.removeElementAt(helpDirNo);
    }

    /**
     * Adds the specific object to the given vector.  The vectors can be Classpaths, toolboxes or help files.  If the
     * argument is a String then we check to see if its a network or a local file. It is stored as a URL if its networks
     * or a String if its local. The CLASSPATH is already split up into the various objects
     */
    private final static int addToVar(Vector toAddTo, Object item) {
        try {
            if (item instanceof String) {// unparsed string
                if ((((String) item).indexOf("http:") != -1) || (toAddTo.indexOf("ftp:") != -1)) {
                    toAddTo.addElement(new java.net.URL((String) item));
                } else {
                    toAddTo.addElement(item);
                }
            } else {
                toAddTo.addElement(item);// already parsed
            }
            return 1;
        }
        catch (java.net.MalformedURLException ee) {
            new ErrorDialog(null, item + " not a URL !! ");
            return -1;
        }
    }


    /**
     * Gets all the types compiled in the $TRIANA/classes/triana/types
     *
     * @return a StringVector containing a Vector of every TrianaType (i.e. each type stored as a String).
     */
    public static Vector<String> getAllTrianaTypes() {
        if (allTypes == null) {
            allTypes = new Vector<String>();
        } else {
            return new Vector<String>(allTypes);
        }

        String sep = File.separator;


        String typePath;
        typePath = Env.home() + sep + "classes" + sep + "triana" + sep + "types" + sep;
        Listing listing = FileUtils.listAllFiles(typePath, "*.class", false);
        String[] l = new String[0];

        if (listing != null) {
            l = listing.justFileList().convertToStrings();
            for (int i = 0; i < l.length; ++i) {
                String typ = l[i].substring(l[i].lastIndexOf(sep) + 1, l[i].length() - 6);
                allTypes.add(typ);
            }
        }
        return new Vector<String>(allTypes);

    }

    /**
     * Parse the system default tools and colors file in $TRIANA_V3/SYSTEM/TYPES/TrianaTypes
     */
    public static Vector<String> getTrianaTypesAndDefaultColors() {
        Vector<String> defaults = FileUtils.readAndSplitFile(typesFile());
        Vector<String> compiled = getAllTrianaTypes();
        for (int j = 0; j < compiled.size(); j++) {
            String temp = new String(compiled.get(j));
            temp = temp + " " + defaults.get(j);
            compiled.setElementAt(temp, j);
        }
        return compiled;
    }

    private static String templateDirectory() {
        return Env.home() + "resources" + File.separator + "system" + File.separator + "templates" + File.separator;
    }

    private static String typesFile() {
        return Env.home() + "resources" + File.separator + "system" + File.separator + "types" + File.separator
                + "TrianaTypes";
    }

    /**
     * Returns UWCC's copyright found in $TRIANA/system/templates/UWCCCopyright
     *
     * @return UWCC's copyright
     */
    public static String getCopyright() {
        try {
            String template = templateDirectory() + "Copyright";
            return FileUtils.readFile(FileUtils.createReader(template));
        }
        catch (IOException except) {
            System.err.println("Error Reading Copyright: " + except.getMessage());
            return "";
        }
    }

    /**
     * Loads in a template found in $TRIANA/system/templates/ i.e. BasicWindow, UserWindow, WindowUnit etc.
     *
     * @return a string containing the contents of the template file
     */
    public static String getTemplate(String template) {
        try {
            template = templateDirectory() + template;
            return FileUtils.readFile(FileUtils.createReader(template));
        }
        catch (IOException except) {
            System.err.println("Error Reading Template: " + except.getMessage());
            return "";
        }
    }

    /**
     * Gets the ResourceBundle, which store the internationalized messages to display within triana. These are taken
     * from the system/internationalization/triana_.._...properties file depending on which locale you are running in.
     */
    public static ResourceBundle getResourceBundle() throws IOException {
        if (messages == null) {
            logger.info("Getting Locale Settings ....");
            String path = "system/locale/";
            String settings = path + "settings";
            logger.fine("resource path = " + path);
            InputStream localeSettings = Thread.currentThread().getContextClassLoader().getResourceAsStream(settings);
            Vector<String> locale = FileUtils
                    .readAndSplitFile(new BufferedReader(new InputStreamReader(localeSettings)));
            logger.info("Language = " + locale.get(0));
            logger.info("Country = " + locale.get(1));

            String file = path + "triana_" + locale.get(0) + "_" + locale.get(1) + ".properties";

            logger.info("Internationalization Bundle File = " + file);

            // Can't use the standard resource bundle searching scheme for 2 reasons
            // 1. It ONLY searches the classpath
            // 2. Does'nt allow them to work over a network
            // so I wrote a direct method which works with http or local files :-

            try {
                messages = new PropertyResourceBundle(
                        Thread.currentThread().getContextClassLoader().getResourceAsStream(file));
            }
            catch (IOException ee) {
                logger.severe("Couldn't get Resources from " + file + ":" + FileUtils.formatThrowable(ee));
                throw ee;
            }
        }

        return messages;
    }

    /**
     * Gets the ResourceBundle, which store the internationalized messages to display within triana. These are taken
     * from the system/internationalization/triana_.._...properties file depending on which locale you are running in.
     */
    public static ResourceBundle getTips() {
        String sep = separator();
        if (tips == null) {
            logger.info("Getting Local Tips Settings ....");
            String file = home() + "system" + sep + "tips" + sep + "tips.properties";

            logger.info("Tips Bundle File = " + file);

            try {
                InputStream is = FileUtils.createInputStream(file);
                tips = new PropertyResourceBundle(is);
                is.close();
            }
            catch (IOException ee) {
                logger.severe("Couldn't get Resources from " + file + ":" + FileUtils.formatThrowable(ee));
            }
        }
        return tips;
    }

    /**
     * @return the system dependant path separator
     */
    public static String getPathSeparator() {
        return System.getProperty("path.separator");
    }

    /**
     * Gets the string fromm the resource bundle which store the messages to display within triana. These are taken from
     * the system/locale/triana_.._...properties file depending on which locale you are running in
     */
    public static String getString(String word) {
        if (messages == null) {
            try {
                getResourceBundle();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            return messages.getString(word);
        }
        catch (MissingResourceException except) {
            except.printStackTrace();
            return word;
        }
    }

    /**
     * Gets the next tip from the tip resource file
     */
    public static String getNextTip() {
        if (!GUIEnv.getTipOfTheDay()) {
            return null;// return null if they are disabled
        }

        if (tips == null) {
            getTips();
        }
        String t = null;
        try {
            t = tips.getString((String) getUserProperty(TIP_NUM_STR));
        }
        catch (Exception e) {// must be at the end of tips
            setUserProperty(TIP_NUM_STR, "0");
            t = tips.getString("0");
        }

        int i = Integer.parseInt((String) getUserProperty(TIP_NUM_STR));
        GUIEnv.setTipOfTheDay(i++);

        return t;
    }

    final static boolean passwordOK() {
        return passwordOK;
    }

    final static boolean notRunOut() {
        return !runOut;
    }


    public final static void verifyPassword(String p) {
        if (!p.equals("objectcon")) {
            System.exit(0);
        } else {
            passwordOK = true;
        }
    }

    /**
     * The base for all Triana getApplicationDataDir page.
     */
    public static String homePage() {
        return "http://www.trianacode.org";
    }


    /**
     * Add a recently accessed file path to the set of recent items
     *
     * @param path full path to the file accessed
     */
    public final static void addRecentFilePath(String path) {
        if (recentFileItems.size() >= RECENT_ITEM_COUNT) {
            recentFileItems.remove(0);
        }
        if (!recentFileItems.contains(path)) {
            recentFileItems.add(path);
        }
        writeConfig();
    }

    /**
     * @return the recent list of files accessed
     */
    public final static String[] getRecentFilePaths() {
        if (restoredFromDisk) {
            Vector validatedItems = new Vector();
            for (Iterator iterator = recentFileItems.iterator(); iterator.hasNext();) {
                String s = (String) iterator.next();
                if (FileUtils.fileExists(s)) {
                    validatedItems.add(s);
                }
            }
            recentFileItems = validatedItems;

            return (String[]) validatedItems.toArray(new String[validatedItems.size()]);
        } else {
            return new String[0];
        }
    }

    /**
     * Adds or replaces a color table entry
     */
    public static void setColorTableEntry(ColorTableEntry entry) {
        for (Iterator iterator = colorTableEntries.iterator(); iterator.hasNext();) {
            ColorTableEntry colorTableEntry = (ColorTableEntry) iterator.next();
            if (colorTableEntry.getColorname().equals(entry.getColorname())) {
                iterator.remove();
            }
        }
        colorTableEntries.add(entry);
    }

    /**
     * @return an array of the currently set colour table entries
     */
    public static ColorTableEntry[] getColorTableEntries() {
        return (ColorTableEntry[]) colorTableEntries.toArray(new ColorTableEntry[colorTableEntries.size()]);
    }

    /**
     * Returns the default toolbox directory
     *
     * @return File the default directory for tools
     */
    public static File getDefaultToolBoxDir() {
        String pathname = home() + File.separatorChar + "toolboxes" + File.separatorChar;
        File defaultToolBox = new File(pathname);
        if (!defaultToolBox.isDirectory()) {
            defaultToolBox.mkdir();
        }
        return defaultToolBox;
    }

    /**
     * Inner class that acts as a timer for writing out the currently open taskgraphs
     */
    private static class WriteStateThread extends Thread {

        private boolean stop = false;
        protected int SLEEP_DELAY = 30000;

        public WriteStateThread() {
            this.setName("Triana State Saving");
            this.setPriority(Thread.MIN_PRIORITY);
            this.start();
        }

        /**
         * Stop the thread
         */
        public void stopThread() {
            if (!stop) {
                setPriority(Thread.NORM_PRIORITY);
            }

            stop = true;
            interrupt();
        }

        /**
         * @return true if the thread has been stopped
         */
        public boolean isStopped() {
            return stop;
        }

        /**
         * Trigger the thread to (asynchronsly)write out the config file
         */
        public void write() {
            interrupt();
        }

        public void run() {
            while (!isStopped()) {
                writeStateFiles();
                try {
                    sleep(SLEEP_DELAY);
                }
                catch (InterruptedException except) {
                }
            }

            writeStateFiles();
        }
    }

    /**
     * Inner class that acts as a timer to write the config files
     */
    private static class WriteConfigThread extends Thread {

        private boolean stop = false;
        protected int SLEEP_DELAY = 30000;

        public WriteConfigThread() {
            this.setName("Triana Config Writer");
            this.setPriority(Thread.MIN_PRIORITY);
            this.start();
        }

        /**
         * Stop the thread
         */
        public void stopThread() {
            if (!stop) {
                setPriority(Thread.NORM_PRIORITY);
            }

            stop = true;
            interrupt();
        }

        /**
         * @return true if the thread has been stopped
         */
        public boolean isStopped() {
            return stop;
        }

        /**
         * Trigger the thread to (asynchronsly)write out the config file
         */
        public void write() {
            interrupt();
        }

        public void run() {
            while (!isStopped()) {
                writeConfigFile();
                try {
                    sleep(SLEEP_DELAY);
                }
                catch (InterruptedException except) {
                }
            }

            writeConfigFile();
        }
    }
}












