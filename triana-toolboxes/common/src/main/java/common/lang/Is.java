package common.lang;

import org.trianacode.taskgraph.Unit;


/**
 * checks two things against a boolean - returns a Boolean
 *
 * @author Andrew Harrison
 * @version $Revision: 1.16 $
 * @created 03 Jun 2006
 * @date $Date: 2004/06/11 15:59:20 $ modified by $Author: scmabh $
 * @todo
 */
public class Is extends Unit {

    public static final String EQU = "is equal to";
    public static final String GREATER = "is greater than";
    public static final String LESS = "is less than";
    public static final String INST = "is instance";
    public static final String IS_A = "is a";
    public static final String HAS_NAME = "has name equal to";

    public static final String TEST_PARAM = "test";
    private String test = EQU;
    private boolean invert;

    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        java.lang.Object in1 = (java.lang.Object) getInputAtNode(0);
        java.lang.Object in2 = (java.lang.Object) getInputAtNode(1);
        Boolean ret = Boolean.FALSE;
        if (in1 == null || in2 == null) {
            output(ret);
        }
        if (test.equals(EQU)) {
            ret = new Boolean(in1.equals(in2));
        } else if (test.equals(GREATER) && in1 instanceof Comparable && in2 instanceof Comparable) {
            int i = ((Comparable) in1).compareTo((Comparable) in2);
            if (i <= 0)
                ret = Boolean.FALSE;
            else
                ret = Boolean.TRUE;
        } else if (test.equals(LESS) && in1 instanceof Comparable && in2 instanceof Comparable) {
            int i = ((Comparable) in1).compareTo((Comparable) in2);
            if (i < 0)
                ret = Boolean.TRUE;
            else
                ret = Boolean.FALSE;
        } else if (test.equals(IS_A)) {
            Class in1cls = in1.getClass();
            if (in1 instanceof Class)
                in1cls = (Class) in1;
            Class in2cls = in2.getClass();
            if (in2 instanceof Class)
                in2cls = (Class) in2;
            if (in2cls.isAssignableFrom(in1cls))
                ret = Boolean.TRUE;
            else
                ret = Boolean.FALSE;
        } else if (test.equals(HAS_NAME) && in2 instanceof java.lang.String) {
            String name = getTextClassName(in1.getClass().getName());
            log("Is.process in1 name=" + name);
            int dot = name.lastIndexOf(".");
            if (dot > -1)
                name = name.substring(dot + 1, name.length());
            if (((String) in2).endsWith(name))
                ret = Boolean.TRUE;
            else
                ret = Boolean.FALSE;
        }
        if (invert) {
            if (ret.booleanValue()) {
                output(Boolean.FALSE);
            } else {
                output(Boolean.TRUE);
            }
        } else
            output(ret);
    }


    private static String getTextClassName(String text) {
        if (text == null || !(isJVMName(text)))
            return text;
        String className = "";
        int index = 0;
        while (index < text.length() && text.charAt(index) == '[') {
            index++;
            className += "[]";
        }
        if (index < text.length()) {
            if (text.charAt(index) == 'B')
                className = "byte" + className;
            else if (text.charAt(index) == 'C')
                className = "char" + className;
            else if (text.charAt(index) == 'D')
                className = "double" + className;
            else if (text.charAt(index) == 'F')
                className = "float" + className;
            else if (text.charAt(index) == 'I')
                className = "int" + className;
            else if (text.charAt(index) == 'J')
                className = "long" + className;
            else if (text.charAt(index) == 'S')
                className = "short" + className;
            else if (text.charAt(index) == 'Z')
                className = "boolean" + className;
            else {
                className = text.substring(index + 1, text.indexOf(";")) + className;
            }
        }
        return className;
    }

    private static boolean isJVMName(String text) {
        return text.startsWith("[") ||
                text.startsWith("L") ||
                text.equals("B") ||
                text.equals("C") ||
                text.equals("D") ||
                text.equals("F") ||
                text.equals("I") ||
                text.equals("J") ||
                text.equals("S") ||
                text.equals("Z");
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and
     * parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(2);
        setMinimumInputNodes(2);
        setMaximumInputNodes(2);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(1);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("checks two things against a boolean - returns a Boolean");
        setHelpFileLocation("Is.html");

        defineParameter(TEST_PARAM, "", USER_ACCESSIBLE);
        defineParameter("invert", "false", USER_ACCESSIBLE);

        // Initialise custom panel interface
        setParameterPanelClass("Common.Lang.IsPanel");
        setParameterPanelInstantiate(ON_USER_ACCESS);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values
     * specified by the parameters.
     */
    public void reset() {
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Is (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals(TEST_PARAM)) {
            test = (String) value;
        } else if (paramname.equals("invert")) {
            invert = new Boolean(((String) value)).booleanValue();
        }
    }


    /**
     * @return an array of the types accepted by each input node. For node indexes
     *         not covered the types specified by getInputTypes() are assumed.
     */
    public String[][] getNodeInputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types accepted by nodes not covered
     *         by getNodeInputTypes().
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object", "java.lang.Object"};
    }


    /**
     * @return an array of the types output by each output node. For node indexes
     *         not covered the types specified by getOutputTypes() are assumed.
     */
    public String[][] getNodeOutputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types output by nodes not covered
     *         by getNodeOutputTypes().
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Boolean"};
    }

}



