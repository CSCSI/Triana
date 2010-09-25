package org.trianacode.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple convenience class to make command lines easier to deal with.   String is
 * converted into a hashtable of key/value pairs that contain the various command
 * line arguments that the user has supplied.  It also allows you to add prefixes for
 * identifying arguments and you can return a String[] or a List or values for each
 * parameter
 * <p/>
 * <p/>
 * Values can either be Strings (single) or lists (List) of Strings.
 * <p/>
 * User: Ian Taylor
 * <p/>
 * Date: Sep 24, 2010
 * Time: 9:35:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArgumentParser {
    private static final String NL = System.getProperty("line.separator");
    Map<String, List<String>> arguments;
    ArrayList<String> argumentPrefixes;

    String args[];

    public ArgumentParser(String[] args) {
        this.args = args;
        arguments = new HashMap<String, List<String>>();
        argumentPrefixes = new ArrayList<String>();

        argumentPrefixes.add("-");
        argumentPrefixes.add("--");
    }

    /**
     * Adds an argument prefix to the list of prefixes that are used to identify arguments
     *
     * @param prefix
     */
    public void addArgumentPrefix(String prefix) {
        argumentPrefixes.add(prefix);
    }

    public void parse() throws ArgumentParsingException {
        String argument;
        String value;
        int i = 0;

        int valp;

        for (;i < args.length;) {
            List<String> values = new ArrayList<String>();
            argument = args[i];
            System.out.println("ArgumentParser.parse arg:" + argument);
            if (!isAnArgument(argument))
                throw new ArgumentParsingException("Argument " + argument + " not valid");

            valp = i + 1;
            if (valp < args.length) {

                boolean moreValues;
                do {
                    value = args[valp];
                    values.add(value);
                    moreValues = false;
                    if (!isAnArgument(value)) {
                        values.add(value);
                        ++valp;
                        if (valp < args.length)
                            moreValues = true;
                    }
                } while (moreValues);
            }

            arguments.put(argument, values);

            i = valp;
        }

    }

    // - or -- is consider a property, which might have an optional value in the following
    // array element. If no value is supplied then the value element is set to ""

    private boolean isAnArgument(String argument) {

        for (String prefix : argumentPrefixes) {
            if (argument.startsWith(prefix)) return true;
        }

        return false;
    }

    /**
     * Gets values fgor this argument
     *
     * @param argument argument to search for (containing the prefix)
     * @return a List of values for this argument or bull if not found
     */
    public List<String> getArgumentValues(String argument) {
        return arguments.get(argument);
    }

    /**
     * returns the first value or null;
     *
     * @param argument
     * @return
     */
    public String getArgumentValue(String argument) {
        List<String> vals = arguments.get(argument);
        if (vals == null) {
            return null;
        }
        for (String val : vals) {
            if(val.length() > 0) {
                return val;
            }
        }
        return null;
    }


    /**
     * Returns a list of arguments as a String[]
     *
     * @param argument
     * @return
     */
    public String[] getArgumentAsStringValues(String argument) {
        List<String> vals = getArgumentValues(argument);
        if (vals == null) return null;

        String strvals[] = new String[vals.size()];

        int i = 0;
        for (String val : vals) {
            strvals[i] = val;
            ++i;
        }

        return strvals;
    }

    /**
     * Gets the arguments along with corresponding values
     *
     * @return as a hashtable of key (argument as a string) and values (as a List)
     */
    public Map<String, List<String>> getArguments() {
        return arguments;
    }

    public void listArguments() {
        System.out.println(toString());

    }

    public String toString() {

        StringBuffer allargs = new StringBuffer();

        for (String arg : arguments.keySet()) {
            StringBuffer onearg = new StringBuffer();
            List<String> vals = arguments.get(arg);

            onearg.append(arg);
            onearg.append(" ");
            for (String val : vals) {
                onearg.append(val);
                onearg.append(" ");
            }

            allargs.append(onearg.toString().trim());
            allargs.append(NL);
        }
        return allargs.toString();
    }
}

