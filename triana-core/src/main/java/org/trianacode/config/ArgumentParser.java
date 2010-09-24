package org.trianacode.config;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Simple convenience class to make command lines easier to deal with.   String is
 * converted into a hashtable of key/value pairs that contain the various command
 * line arguments that the user has supplied.  It also allows you to add prefixes for
 * identifying arguments and you can return a String[] or a List or values for each
 * parameter
 *
 *
 * Values can either be Strings (single) or lists (List) of Strings.
 *
 * User: Ian Taylor
 * 
 * Date: Sep 24, 2010
 * Time: 9:35:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArgumentParser {
    Hashtable <String,List>arguments;
    ArrayList <String>argumentPrefixes;

    String args[];

    public ArgumentParser(String[] args) {
        this.args = args;
        arguments=new Hashtable <String,List>();
        argumentPrefixes= new ArrayList <String>();

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
        int i=0;
        int valp;
        List <String>values;

        do {
            argument = args[i];

            if (!isAnArgument(argument))
                throw new ArgumentParsingException("Argument " + argument + " not valid");

            valp=i+1;

            if (valp<args.length) {
                values=new ArrayList<String>();
                boolean moreValues;

                do {
                    value = args[valp];
                    moreValues=false;
                    if (!isAnArgument(value)) {
                        values.add(value);
                        ++valp;
                        if (valp<args.length)
                            moreValues=true;
                    }
                } while (moreValues);
            }
            
            i=valp;
            
        } while (i<args.length);
    }

    // - or -- is consider a property, which might have an optional value in the following
    // array element. If no value is supplied then the value element is set to ""
    private boolean isAnArgument(String argument) {

        for (String prefix: argumentPrefixes) {
            if (argument.startsWith(prefix)) return true;
        }

        return false;
    }

    /**
     * Gets values fgor this argument
     *
     * @param argument argument to search for (containing the prefix)
     * 
     * @return a List of values for this argument or bull if not found
     */
    public List getArgumentValues(String argument) {
        return arguments.get(argument);
    }


    /**
     * Returns a list of arguments as a String[]
     * 
     * @param argument
     * @return
     */
    public String[] getArgumentAsStringValues(String argument) {
        List<String> vals = getArgumentValues(argument);
        if (vals==null) return null;

        String strvals[]=new String[vals.size()];

        int i=0;
        for (String val: vals) {
            strvals[i]=val;
            ++i;
        }
        
        return strvals;
    }

    /**
     *  Gets the arguments along with corresponding values
     *
     * @return as a hashtable of key (argument as a string) and values (as a List)
     */
    public Hashtable<String,List> getArguments() {
        return arguments;
    }
}
