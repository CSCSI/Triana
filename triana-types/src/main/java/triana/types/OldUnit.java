package triana.types;

import java.util.ArrayList;
import java.util.Vector;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.util.Str;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 4, 2010
 */

public abstract class OldUnit extends Unit {


    /**
     * Legacy flags used to set the correct max/min input/output nodes when allowZeroInput/OutputNodes is called
     */
    private boolean zeroinput = false;
    private boolean zerooutput = false;

    /**
     * Set to true if this unit requires its inputs to be in double precision format
     */
    private boolean requireDoubleInputs = false;

    /**
     * Set to true if this unit can deal with double precision input values
     */
    private boolean canProcessDoubleArrays = false;


    /**
     * @return the name of this unit/Class e.g. FFT, Wave, Grapher etc.
     */
    public String getName() {
        return getTask().getToolName();
    }


    /**
     * This function is called when the unit is first created. It should be over-ridden to initialise the tool
     * properties (e.g. default number of nodes) and tool parameters.
     */
    public void init() {
        saveParameters();
    }

    /**
     * This function is called when the reset is pressed on the gui. It restore the unit to its pre-start state.
     */
    public void reset() {
        Task task = getTask();
        String[] paramnames = task.getParameterNames();

        for (int count = 0; count < paramnames.length; count++) {
            if (task.getParameterType(paramnames[count]).equals(USER_ACCESSIBLE)) {
                setParameter(paramnames[count], (String) task.getParameter(paramnames[count]));
            }
        }
    }

    /**
     * This function is called when the unit is deleted. It should be over-ridden to clean up the unit (e.g. close open
     * files).
     */
    public void dispose() {
        cleanUp();
    }


    /**
     * ************************************************************* *********** Programmers implement their code here
     * *********** *************************************************************
     * <p/>
     * If a user wants to include an algorithm into Triana then this method must be implemented and the appropriate
     * algorithm invoked here.
     * <p/>
     * This method should always be over-ridden, as if the unit does nothing DEADLOCK occurs!.
     */
    public abstract void process() throws Exception;


    /**
     * @return true if this unit requires its inputs to be in double precision format
     */
    public boolean getRequireDoubleInputs() {
        return requireDoubleInputs;
    }

    /**
     * @return true if this unit can deal with double precision input values
     */
    public boolean getCanProcessDoubleArrays() {
        return canProcessDoubleArrays;
    }

    /**
     * Unit Programmers should set this to true if this unit requires its inputs to be in double precision format
     */
    public void setRequireDoubleInputs(boolean state) {
        requireDoubleInputs = state;
    }

    /**
     * Unit Programmers should set this to true if this unit can process input data sets in double precision format
     */
    public void setCanProcessDoubleArrays(boolean state) {
        canProcessDoubleArrays = state;
    }


    /**
     * Stops the network running
     */
    public final void stop() {
        getRunnableInterface().notifyError(null);
    }

    /**
     * Returns the data at input node <i>nodeNumber</i>. If data is not ready, NOT_READY triana type is returned. If
     * there is no cable connected to the input node the NOT_CONNECTED triana type is returned.
     *
     * @param nodeNumber the node you want to get the data from.
     * @deprecated
     */
    public TrianaType getInputNode(int nodeNumber) {
        return (TrianaType) getInputAtNode(nodeNumber);
    }


    /**
     * Sets the data at output node <i>outputNode</i>. This method should be used to set the data at each particular
     * output node (if this is necessary) This function would only be used in very few cases.  The normal default
     * function is to duplicate the data at all output nodes so hence one call to output() passing the data often
     * suffices.
     *
     * @param outputNode the output node you wish to set
     * @param data       the data to be sent
     * @deprecated
     */
    public void outputAtNode(int outputNode, TrianaType data) {
        outputAtNode(outputNode, data, true);
    }

    /**
     * Sets the data at output node <i>outputNode</i> but this call does not block. Use isOutputSent to poll to find out
     * if it has been delivered.
     *
     * @param outputNode the output node you wish to set
     * @param data       the data to be sent
     * @see public boolean isOutputSent(int outputNode)
     * @deprecated
     */
    public void outputAtNodeNonBlocking(int outputNode, TrianaType data) {
        outputAtNode(outputNode, data, false);
    }


    /**
     * This method is used to change the type of data that is currently being output from this unit and tells the GUI to
     * redraw its cable so that the new type of information can be seen by a different coloured cable leaving this
     * unit.
     *
     * @deprecated
     */
    public void setOutputType(Class trianaType) {
//        getTask().setParameter(Tool.OUTPUT_TYPE, trianaType.getName());
    }

    /**
     * This method is used to change the type of data that is currently being output from this unit and tells the GUI to
     * redraw its cable so that the new type of information can be seen by a different coloured cable leaving this unit.
     * Here give the full name of the type e.g. "triana.types.SampleSet"
     *
     * @deprecated
     */
    public void setOutputType(String name) {
//        getTask().setParameter(Tool.OUTPUT_TYPE, name);
    }


    /**
     * This method should be overridden to return an array of the data input types accepted by this unit (returns
     * triana.types.TrianaType by default).
     *
     * @return an array of the input types for this unit
     */
    public String[] getInputTypes() {
        String inlist = inputTypes();
        ArrayList inarray = new ArrayList();

        while (inlist.indexOf(" ") > -1) {
            inarray.add(inlist.substring(0, inlist.indexOf(" ")));
            inlist = inlist.substring(inlist.indexOf(" ") + 1);
        }

        if ((!inlist.equals("none")) && (!inlist.equals(""))) {
            inarray.add(inlist);
        }

        String[] copy = new String[inarray.size()];
        inarray.toArray(copy);
        return copy;
    }

    /**
     * This method should be overridden to return an array of the data output types accepted by this unit (returns
     * triana.types.TrianaType by default).
     *
     * @return an array of the output types for yhis unit
     */
    public String[] getOutputTypes() {
        String outlist = outputTypes();
        ArrayList outarray = new ArrayList();

        while (outlist.indexOf(" ") > -1) {
            outarray.add(outlist.substring(0, outlist.indexOf(" ")));
            outlist = outlist.substring(outlist.indexOf(" ") + 1);
        }

        if (!outlist.equals("none")) {
            outarray.add(outlist);
        }

        String[] copy = new String[outarray.size()];
        outarray.toArray(copy);
        return copy;
    }


    /**
     * A convienience method that sets the data input node at the specified index to optional. This means that the
     * scheduler does not wait for data from the node before continuing with processing. By default input nodes are not
     * set to optional (i.e. they are set to essential).
     */
    public void setOptional(int index) {
        getTask().setNodeRequirement(index, Task.OPTIONAL);
    }

    /**
     * A convienience method that returns whether the data input node at the specified index is optional. If a node is
     * optional this means that the scheduler does not wait for data from the node before continuing with processing. By
     * default input nodes are not set to optional (i.e. they are set to essential).
     */
    public boolean isOptional(int index) {
        return getTask().getNodeRequirement(index).equals(Task.OPTIONAL);
    }

    /**
     * A convienience method that sets the data input node at the specified index to essential. This means that the
     * scheduler does must wait for data from the node before continuing with processing. By default input nodes are not
     * set to essential (this is the opposite of optional).
     */
    public void setEssential(int index) {
        getTask().setNodeRequirement(index, Task.ESSENTIAL);
    }

    /**
     * A convienience method that returns whether the data input node at the specified index is essential. If a node is
     * essential this means that the scheduler must not wait for data from the node before continuing with processing.
     * By default input nodes are set to essential (this is the opposite of optional).
     */
    public boolean isEssential(int index) {
        return getTask().getNodeRequirement(index).equals(Task.ESSENTIAL);
    }


    /**
     * Sets the text in the main triana window to the following
     */
    public void setText(String information) {
        // TODO
    }

    /**
     * Prints the specific text to the debug window for the MainTriana which this unit is running within.
     */
    public void print(String text) {
        // TODO
    }

    /**
     * Prints the specific text to the debug window for the MainTriana which this unit is running within and adds a
     * carriage return to the end of the given string.
     */
    public void println(String text) {
        print(text + "\n");
    }


    /**
     * Gets the toolbox directory which this unit belongs to
     */
    /*public String getToolBoxPath() {
        String toolbox = getTask().getToolXMLFileName();
        return toolbox.substring(0, toolbox.lastIndexOf(Env.separator()) + 1);
    }*/

    /**
     * Gets the help directory of the toolbox which this unit belongs to
     *
     * @deprecated handled by tools rather than Units now
     */
    /*public String getHelpPath() {
        String toolbox = getTask().getToolXMLFileName();
        return toolbox.substring(0, toolbox.lastIndexOf(Env.separator()) + 1) + "help" + Env.separator();
    }*/

    /**
     * Gets the library directory of the toolbox which this unit belongs to
     */
    /*public String getLibraryPath() {
        String toolbox = getTask().getToolXMLFileName();
        return toolbox.substring(0, toolbox.lastIndexOf(Env.separator()) + 1) + "lib" + Env.separator() + Env.os() + Env
                .separator();
    }*/


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "No description for this unit";
    }

    /**
     * This function must be implemented for each Triana unit to specify the help file for this unit. We really must
     * insist that people write help files in order to keep Triana as user-friendly as possible! Help files should be
     * written in html format. If not, they will displayed in text format. You can simply enter the name of the unit
     * (.html) and then Triana looks for this help file in several places e.g. :- </p> <p/> <ol> <li> in
     * $GRIDHOME/doc/help and then within the <b>tool</b> and <b> main </b> subdirectories. <li> in the users getApplicationDataDir
     * directory within a directory called help and then within the <b>tool</b> and <b> main </b> subdirectories. <li>
     * Any path specified by the GRID_HELP environment variable and also then within the <b>tool</b> and <b> main </b>
     * subdirectories. <p/> You can alternatively enter the location as an absolute path name or a network address e.g.
     * </p><p> <center> "http:///www.astro.cf.ac.uk/pub/Ian.Taylor/Triana/Help/Wave.html"<br> or <br>
     * "/usr/local/ian/Triana/Help/Wave.html"<br> </center> <p/> <p>If you don't wan't to provide help (!!!) the this
     * function should return the String "none" or null.</p>
     *
     * @return the location of the help file for this unit.
     * @deprecated now set as a parameter in Tool
     */
    public String getHelpFile() {
        return null;
    }

    /*public void setHelpFileLocation(String location) {
        getTask().setParameter(Task.HELP_FILE_PARAM, location);
    }*/


    /**
     * Called to update internal unit variables when the value of a parameter changes. Whether this is called
     * immediately the parameter is updates, or just before the process method depends on the units param update
     * policy.
     */
    public void parameterUpdate(String paramname, Object value) {
        if (value instanceof String) {
            setParameter(paramname, (String) value);
        }
    }


    /**
     * Converts a color to a String
     */
    /*public final static String toString(Color col) {
        return toString(col.getRed()) + ' ' + toString(col.getGreen()) + ' ' + toString(col.getBlue());
    }*/

    /**
     * Converts an integer into a String
     */
    public final static String toString(int param) {
        return String.valueOf(param);
    }

    /**
     * Converts an long into a String
     */
    public final static String toString(long param) {
        return String.valueOf(param);
    }

    /**
     * Converts an float into a String
     */
    public final static String toString(float param) {
        return String.valueOf(param);
    }

    /**
     * Converts an short into a String
     */
    public final static String toString(short param) {
        return String.valueOf(param);
    }

    /**
     * Converts an double into a String
     */
    public final static String toString(double param) {
        return String.valueOf(param);
    }

    /**
     * Converts an byte into a String
     */
    public final static String toString(byte param) {
        return String.valueOf(param);
    }

    /**
     * Converts an boolean into a String
     */
    public final static String toString(boolean param) {
        return String.valueOf(param);
    }


    /**
     * Converts the given String to a float .
     */
    public final static float strToFloat(String value) {
        return Str.strToFloat(value);
    }

    /**
     * Converts the given Color String to a float .
     */
    /*public final static Color strToColor(String value) {
        StringSplitter str = new StringSplitter(value);
        return new Color((int) strToDouble(str.at(0)),
                (int) strToDouble(str.at(1)),
                (int) strToDouble(str.at(2)));
    }*/

    /**
     * Converts the given String to a double.
     */
    public final static double strToDouble(String value) {
        return Str.strToDouble(value);
    }

    /**
     * Converts the given String to a int.
     */
    public final static int strToInt(String value) {
        return (int) Str.strToDouble(value);
    }

    /**
     * Converts the given String to a short.
     */
    public final static short strToShort(String value) {
        return (short) Str.strToDouble(value);
    }

    /**
     * Converts the given String to a Long.
     */
    public final static long strToLong(String value) {
        return Str.strToLong(value);
    }

    /**
     * Converts the given String to a byte.
     */
    public final static byte strToByte(String value) {
        return Str.strToByte(value);
    }

    /**
     * Converts the given String to a boolean.
     */
    public final static boolean strToBoolean(String value) {
        return Str.strToBoolean(value);
    }


    /**
     * Converts the given String to an array of floats.
     */
    public final static float[] strToFloats(String value) {
        Vector<String> line = FileUtils.splitLine(value);
        float[] vals = new float[line.size()];
        for (int i = 0; i < line.size(); ++i) {
            vals[i] = Str.strToFloat(line.get(i));
        }
        return vals;
    }

    /**
     * Converts the given String to an array of  double.
     */
    public final static double[] strToDoubles(String value) {
        Vector<String> line = FileUtils.splitLine(value);
        double[] vals = new double[line.size()];
        for (int i = 0; i < line.size(); ++i) {
            vals[i] = Str.strToDouble(line.get(i));
        }
        return vals;
    }

    /**
     * Converts the given String  to an array of int. Each value is separated by a single white space
     */
    public final static int[] strToInts(String value) {
        Vector<String> line = FileUtils.splitLine(value);
        int[] vals = new int[line.size()];
        for (int i = 0; i < line.size(); ++i) {
            vals[i] = (int) Str.strToDouble(line.get(i));
        }
        return vals;
    }

    /**
     * Converts the given String to an array of short.
     */
    public final static short[] strToShorts(String value) {
        Vector<String> line = FileUtils.splitLine(value);
        short[] vals = new short[line.size()];
        for (int i = 0; i < line.size(); ++i) {
            vals[i] = (short) Str.strToDouble(line.get(i));
        }
        return vals;
    }

    /**
     * Converts the given String to an array of Long.
     */
    public final static long[] strToLongs(String value) {
        Vector<String> line = FileUtils.splitLine(value);
        long[] vals = new long[line.size()];
        for (int i = 0; i < line.size(); ++i) {
            vals[i] = Str.strToLong(line.get(i));
        }
        return vals;
    }

    /**
     * Converts the given String to an array of byte.
     */
    public final static byte[] strToBytes(String value) {
        Vector<String> line = FileUtils.splitLine(value);
        byte[] vals = new byte[line.size()];
        for (int i = 0; i < line.size(); ++i) {
            vals[i] = Str.strToByte(line.get(i));
        }
        return vals;
    }

    /**
     * Converts the given String to an array of shortcut.  boolean.
     */
    public final static boolean[] strToBooleans(String value) {
        Vector<String> line = FileUtils.splitLine(value);
        boolean[] vals = new boolean[line.size()];
        for (int i = 0; i < line.size(); ++i) {
            vals[i] = Str.strToBoolean(line.get(i));
        }
        return vals;
    }

    /**
     * Converts the given String to an array of String containing each string separately.
     */
    public final static String[] strToStrings(String value) {
        Vector<String> line = FileUtils.splitLine(value);
        String[] vals = new String[line.size()];
        for (int i = 0; i < line.size(); ++i) {
            vals[i] = line.get(i);
        }
        return vals;
    }

// --------------------------- DEPRECATED METHODS --------------------------

    /**
     * @deprecated
     */
    public void saveParameters() {
    }

    /**
     * Use getTask().setParameter() instead
     *
     * @deprecated
     */
    public void setParameter(String name, String value) {
        getTask().setParameter(name, value);
    }

    /**
     * @deprecated
     */
    public void updateWidgetFor(String name) {
    }


    /**
     * @deprecated
     */
    public String inputTypes() {
        return "triana.types.TrianaType";
    }

    /**
     * @deprecated
     */
    public String outputTypes() {
        return "triana.types.TrianaType";
    }


    /**
     * Add the description directly to the triana type
     *
     * @deprecated ??
     */
    public void addDescription(String text) {
        // TODO???
    }


    /**
     * Use getTask().getDataInputNodeCount() instead.
     *
     * @deprecated
     */
    public int inputNodes() {
        return getInputNodes();
    }

    /**
     * Use getTask().getDataOutputNodeCount() instead.
     *
     * @deprecated
     */
    public int outputNodes() {
        return getOutputNodes();
    }

    /**
     * Use getTask().getDataInputNodeCount() instead.
     *
     * @deprecated
     */
    public int getInputNodes() {
        return getTask().getDataInputNodeCount();
    }

    /**
     * Use getTask().getDataOutputNodeCount() instead.
     *
     * @deprecated
     */
    public int getOutputNodes() {
        return getTask().getDataOutputNodeCount();
    }


    /**
     * Use setMinimumInputNodes() and setMaximumInputNodes() instead
     *
     * @deprecated
     */
    public void setResizableInputs(boolean b) {
        int incount = getTask().getInputNodeCount();

        if (getTask().isParameterName(Task.DEFAULT_INPUT_NODES)) {
            incount = Integer.parseInt((String) getTask().getParameter(Task.DEFAULT_INPUT_NODES));
        }

        if (b) {
            getTask().setParameter(Task.MAX_INPUT_NODES, String.valueOf(Task.DEFAULT_MAX_NODES));
            getTask().setParameter(Task.DEFAULT_INPUT_NODES, String.valueOf(incount));
            getTask().setParameter(Task.MIN_INPUT_NODES, String.valueOf(Task.DEFAULT_MIN_NODES));
        } else {
            if (incount == 0) {
                incount = 1;
            }
            if (zerooutput) {
                getTask().setParameter(Task.MAX_INPUT_NODES, String.valueOf(incount));
                getTask().setParameter(Task.DEFAULT_INPUT_NODES, String.valueOf(incount));
                getTask().setParameter(Task.MIN_INPUT_NODES, String.valueOf(incount));
            } else {
                getTask().setParameter(Task.MAX_INPUT_NODES, String.valueOf(incount));
                getTask().setParameter(Task.DEFAULT_INPUT_NODES, String.valueOf(incount));
                getTask().setParameter(Task.MIN_INPUT_NODES, String.valueOf(incount));
            }
        }
    }

    /**
     * Use setMinimumOutputNodes() and setMaximumOutputNodes() instead
     *
     * @deprecated
     */
    public void setResizableOutputs(boolean b) {
        int outcount = getTask().getOutputNodeCount();
        if (getTask().isParameterName(Task.DEFAULT_OUTPUT_NODES)) {
            outcount = Integer.parseInt((String) getTask().getParameter(Task.DEFAULT_OUTPUT_NODES));
        }

        if (b) {
            getTask().setParameter(Task.MAX_OUTPUT_NODES, String.valueOf(Task.DEFAULT_MAX_NODES));
            getTask().setParameter(Task.DEFAULT_OUTPUT_NODES, String.valueOf(outcount));
            getTask().setParameter(Task.MIN_OUTPUT_NODES, String.valueOf(Task.DEFAULT_MIN_NODES));
        } else {
            if (outcount == 0) {
                outcount = 1;
            }
            if (zerooutput) {
                getTask().setParameter(Task.MAX_OUTPUT_NODES, String.valueOf(outcount));
                getTask().setParameter(Task.DEFAULT_OUTPUT_NODES, String.valueOf(outcount));
                getTask().setParameter(Task.MIN_OUTPUT_NODES, String.valueOf(outcount));
            } else {
                getTask().setParameter(Task.MAX_OUTPUT_NODES, String.valueOf(outcount));
                getTask().setParameter(Task.DEFAULT_OUTPUT_NODES, String.valueOf(outcount));
                getTask().setParameter(Task.MIN_OUTPUT_NODES, String.valueOf(outcount));
            }
        }
    }

    /**
     * @deprecated
     */
    public void stopping() {
    }

    /**
     * @deprecated
     */
    public void starting() {
    }


    /**
     * Use setGUIBuilderInformation() instead.
     *
     * @deprecated
     */
    public void setUseGUIBuilder(boolean state) {
        getTask().removeParameter(Task.GUI_BUILDER);
        getTask().removeParameter(Task.OLD_GUI_BUILDER);
        setGUIInformation();
    }

    /**
     * @deprecated
     */
    public void setGUIInformation() {
    }

    /**
     * Use setGUIBuilderInformation() instead.
     *
     * @deprecated
     */
    public void addGUILine(String line) {
        if (getTask().isParameterName(Task.GUI_BUILDER)) {
            String info = (String) getTask().getParameter(Task.GUI_BUILDER);
            getTask().setParameter(Task.GUI_BUILDER, info + '\n' + line);
        } else {
            getTask().setParameter(Task.GUI_BUILDER, line);
        }
    }


    /**
     * Use setParameterPanelClass() instead
     *
     * @deprecated
     */
    /*public ParameterPanel getParameterPanel() {
        return null;
    }*/

    /**
     * DON'T USE
     *
     * @deprecated
     */
    public void doubleClick() {
    }

    /**
     * Override dispose instead
     *
     * @deprecated
     */
    public void cleanUp() {
    }

    /**
     * Use setMinimumInputNodes() instead.
     *
     * @deprecated
     */
    public void allowZeroInputNodes() {
        getTask().setParameter(Task.MIN_INPUT_NODES, String.valueOf(0));
        zeroinput = true;
    }

    /**
     * Use setMinimumOutputNodes() instead.
     *
     * @deprecated
     */
    public void allowZeroOutputNodes() {
        getTask().setParameter(Task.MIN_OUTPUT_NODES, String.valueOf(0));
        zerooutput = true;
    }

    /**
     * @deprecated
     */
    public void saveParameter(String name, int param) {
        saveParameter(name, String.valueOf(param));
    }

    /**
     * @deprecated
     */
    public void saveParameter(String name, String param) {
        getTask().setParameter(name, param);
    }

    /**
     * @deprecated
     */
    public void saveParameter(String name, long param) {
        saveParameter(name, String.valueOf(param));
    }

    /**
     * @deprecated
     */
    public void saveParameter(String name, float param) {
        saveParameter(name, String.valueOf(param));
    }

    /**
     * @deprecated
     */
    public void saveParameter(String name, double param) {
        saveParameter(name, String.valueOf(param));
    }

    /**
     * @deprecated
     */
    public void saveParameter(String name, byte param) {
        saveParameter(name, String.valueOf(param));
    }

    /**
     * @deprecated
     */
    public void saveParameter(String name, boolean param) {
        saveParameter(name, String.valueOf(param));
    }

    /**
     * Use getTask().setParameter() instead.
     *
     * @deprecated
     */
    public void updateParameter(String name, String value) {
        getTask().setParameter(name, value);
    }

    /**
     * Use getTask().setParameter() instead.
     *
     * @deprecated
     */
    public void updateGUIParameter(String name, String value) {
        if (value == null) {
            getTask().removeParameter(name);
        } else {
            getTask().setParameter(name, value);
        }
    }


    /**
     * Use getTask().setParameter() instead.
     *
     * @deprecated
     */
    public void updateParameter(String name, int value) {
        updateParameter(name, toString(value));
    }

    /**
     * Use getTask().setParameter() instead.
     *
     * @deprecated
     */
    public void updateParameter(String name, long value) {
        updateParameter(name, toString(value));
    }

    /**
     * Use getTask().setParameter() instead.
     *
     * @deprecated
     */
    public void updateParameter(String name, short value) {
        updateParameter(name, toString(value));
    }

    /**
     * Use getTask().setParameter() instead.
     *
     * @deprecated
     */
    public void updateParameter(String name, boolean value) {
        updateParameter(name, toString(value));
    }

    /**
     * Use getTask().setParameter() instead.
     *
     * @deprecated
     */
    public void updateParameter(String name, float value) {
        updateParameter(name, toString(value));
    }

    /**
     * Use getTask().setParameter() instead.
     *
     * @deprecated
     */
    public void updateParameter(String name, double value) {
        updateParameter(name, toString(value));
    }

    /**
     * Use getTask().setParameter() instead.
     *
     * @deprecated
     */
    /*public void updateParameter(String name, Color value) {
        updateParameter(name, toString(value));
    }*/

    /**
     * Use getTask().setParameter() instead.
     *
     * @deprecated
     */
    public void updateParameter(String name, char value) {
        updateParameter(name, toString(value));
    }


    /**
     * For backwardly compatability ONLY
     *
     * @deprecated
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
    }

    /**
     * @param nameAndNumber the name and the number in one string e.g. node1
     * @return the name of the parameter which is tagged along with a number to identify different instances of it.
     * @deprecated
     */
    public final static String getName(String nameAndNumber) {
        String bit;
        boolean ok;
        int pos = -1;

        for (int i = 0; i < nameAndNumber.length(); ++i) {
            ok = true;
            bit = nameAndNumber.substring(i, i + 1);
            try {
                Str.strToInt(bit);
            }
            catch (Exception ee) {
                ok = false;
            }
            if (ok) {
                pos = i;
                break;
            }
        }

        if (pos != -1) {
            return nameAndNumber.substring(0, pos);
        } else {
            return "NONE";
        }
    }

    /**
     * @param nameAndNumber the name and the number in one string e.g. node1
     * @return the value of the integer which is tagged along at the end of given name.  Could do this yourself but this
     *         is a short cut.  Useful when loading back lists of parametes e.g. node1, node2, node3 etc .....
     * @deprecated
     */
    public final static int getNumber(String nameAndNumber) {
        String bit;
        boolean ok;
        int pos = -1;

        for (int i = 0; i < nameAndNumber.length(); ++i) {
            ok = true;
            bit = nameAndNumber.substring(i, i + 1);
            try {
                Str.strToInt(bit);
            }
            catch (Exception ee) {
                ok = false;
            }
            if (ok) {
                pos = i;
                break;
            }
        }
        // the rest SHOULD be a number so :-

        int num = -1;

        try {
            num = Str.strToInt(nameAndNumber.substring(pos));
        }
        catch (Exception ee) {
            num = -1;
        }

        return num;
    }

}

