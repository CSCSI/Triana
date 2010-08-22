package math.calculator;

/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import triana.types.util.Str;
import triana.types.util.StringSplitter;
import triana.types.util.TrianaSort;

//import triana.tools.Compute;
//import Compute;

/**
 * ComputeCalc is an extension of Compute which implements some functions which TrianaCalc needs to call to access
 * certain information within Compute which may change e.g. contant names, these could be extended later on to include
 * new constants.
 *
 * @author Ian Taylor, Bernard Schutz
 * @version 1.1 10 june 1998
 */
public class ComputeCalc extends Compute {

    public static final byte ABSENT = -1;
    public static final int RADIANS = 1;
    public static final int DEGREES = 2;
    public static final int GRADS = 3;

    public ComputeCalc(ComputeManager trianaUnit) {
        super(trianaUnit);
        diagnosisToFile = true;
        setAngleMeasure(RADIANS);
    }

    /**
     * @return a list of constant names already defined within Compute.
     */

    public Vector<String> getConstantNames() {
        /* Melanie: ConstantNames is an array in Compute that
       starts with 1 and not 0, holding names of built-in constants.
       mergeSort() is our utility (a method in Compute) for
       alphabetization of a list. It is overloaded to take a
       number of different arguments -- see the file.
       */
        String[] con = new String[NumberOfConstants];
        for (int i = 0; i < NumberOfConstants; ++i) {
            con[i] = ConstantNames[i + 1];
        }
        String[] names = mergeSort(con);
        Vector<String> constants = new Vector<String>(200);
        for (int i = 0; i < NumberOfConstants; ++i) {
            constants.addElement(names[i]);
        }
        return constants;
    }

    /**
     * @return a list of function names already defined within Compute.
     */
    public Vector<String> getFunctionNames() {
        /* Melanie -- same as the list of constants. But here, I
       add brackets to indicate how many arguments, and that
       information is available because the functions are held in
       separarate lists in Compute.  One function called Control
       is not shown to users because it is for us to control
       the operation of Compute for debugging purposes. It is
       documented in Compute.
         */
        String funct;
        String[] fun = new String[NumberOfFunctionNames];
        for (int i = 0; i < NumberOfFunctionNames; ++i) {
            fun[i] = FunctionNames[i + 1];
        }
        String[] names = mergeSort(fun);
        Vector<String> functions = new Vector<String>(200);
        for (int i = 0; i < NumberOfFunctionNames; ++i) {
            funct = names[i];
            if (!(funct.equals("Control"))) {
                if (FunctionOneTypeDictionary.containsKey(funct)) {
                    funct = funct + "( )";
                    functions.addElement(funct);
                } else if (FunctionTwoTypeDictionary.containsKey(funct)) {
                    funct = funct + "( , )";
                    functions.addElement(funct);
                } else if (FunctionThreeTypeDictionary.containsKey(funct)) {
                    funct = funct + "( , , )";
                    functions.addElement(funct);
                } else if (FunctionListTypeDictionary.containsKey(funct)) {
                    funct = funct + "( .. )";
                    functions.addElement(funct);
                } else {
                    functions.addElement(funct);
                }
            }
        }
        return functions;
    }

    public String returnStringResult() {
        InitializeInputNodeValues();
        return EvaluateStringBracketPair(2, NumberOfTokens - 1);
    }

    public String EvaluateStringBracketPair(int FirstToken, int LastToken) {
        return Token[FirstToken];
    }


    public void setAngleMeasure(int angleMeasure) {
        //
        // The variable angleScale is defined so that if it multiplies
        // an angle in the current angle measure, then the result will be
        // an angle in radians. inverseAngleScale does the inverse.
        //
//    System.out.println("Set angle measure to " + String.valueOf( angleMeasure ));
        if (angleMeasure == RADIANS) {
            angleScale = 1.0;
        } else if (angleMeasure == DEGREES) {
            angleScale = Math.PI / 180.;
        } else if (angleMeasure == GRADS) {
            angleScale = Math.PI / 50.;
        }
        inverseAngleScale = 1.0 / angleScale;
    }


// Functions needed for User Defined Constants

    /**
     * This adds the specific variable definition to the list of user-defined variables within Compute if the variable
     * is not already defined or replaces the existing variable within this one if it does. Variables can be single
     * values of sequences of values. Both are input as Vector<String>s i.e. if the Vector<String>'s size is 1 then the
     * variable is a single value if it is greater than 1 then it is assumed to be a sequence. The values are stored in
     * the hashtables as arrays of doubles.
     */
    public void updateUserVar(String name, Vector<String> value) {
        /* Melanie -- Here is where user constant information is passed
       from the interface into Compute. It is an overloaded name,
       this version taking a Vector<String> containing all the
       values of the sequence defined by the user and putting
       them into a double array store.  This is then added to
       Compute's list of user-defined variables by calling
       its synonym with an array argument.
       If the value is a single constant, then
       this method calls its synonym with a single string argument.
       */
        int length = value.size();
        if (length == 1) {
            updateUserVar(name, Str.strToDouble(value.get(0)));
        } else {
            double[] store = new double[length];
            for (int j = 0; j < length; j++) {
                store[j] = Str.strToDouble((String) value.elementAt(j));
            }
            updateUserVar(name, store);
        }
//	  userVariableValueDictionary.put( name, store );
//	  if (length == 1)	userVariableTypeDictionary.put( name, new Byte( SCALAR ) );
//	  else userVariableTypeDictionary.put( name, new Byte( VECTOR ) );
    }

    /**
     * This adds the specific variable and value to the list of user-defined variables within Compute.
     */
    public void updateUserVar(String name, double value) {
        /*Melanie -- here is where the work is done for putting
      a single constant into the Compute lists.  The value is
      stored into a Hashtable called userVariableValueDictionary
      keyed by the constant name. The Hashtable needs an Object,
      so the scalar value must be stored in an array of length one
      first. The type (scalar in this case)
      is also stored.  If a new value is defined for an old name,
      the Hashtable simply overwrites the old one.  If we want
      a list of user constant names, we can get the list of
      keys from the Hashtable.  See the method below called
      getUserVariableNames(). The next method does the same for
      arrays (sequences).
      */
        double[] store = new double[1];
        store[0] = value;
        userVariableValueDictionary.put(name, store);
        userVariableTypeDictionary.put(name, new Byte(SCALAR));
//        Vector<String> a = new Vector<String>();
//        a.addElement(String.valueOf(value));
//        updateUserVar(name, a);
    }

    /**
     * This adds the specific variable and sequence of values to the list of user-defined variables within Compute.
     */
    public void updateUserVar(String name, double values[]) {
        userVariableValueDictionary.put(name, values);
        userVariableTypeDictionary.put(name, new Byte(VECTOR));
//        Vector<String> a = new Vector<String>();
//        for (int i=0; i<values.length;++i)
//           a.addElement(String.valueOf(values[i]));
//        updateUserVar(name, a);
    }

    /**
     * This adds the specific variable definition to the list of user-defined variables within Compute. Variables can be
     * single values of sequences of values. Both are stored as Vector<String>s i.e. if the Vector<String>'s size is 1
     * then the variable is a single value if it is greater than 1 then it is assumed to be a sequence.
     */
    public void addUserVar(String name, Vector<String> value) {
        /* Melanie -- this is legacy, allowing the use of a different
       name for updateUserVar().
       */
        updateUserVar(name, value);
    }

    /**
     * This adds the specific variable and value to the list of user-defined variables within Compute.
     */
    public void addUserVar(String name, double value) {
        updateUserVar(name, value);
    }

    /**
     * This adds the specific variable and sequence of values to the list of user-defined variables within Compute.
     */
    public void addUserVar(String name, double values[]) {
        updateUserVar(name, values);
    }

    /**
     * This function deletes the specified user variable from the internal list of user variables.
     */
    public void deleteUserVar(String name) {
        userVariableValueDictionary.remove(name);
    }

    /**
     * @return a Hashtable containing the list and values of user-defined variables currently defined within Compute.
     */
    public Hashtable getUserVars() {
        return userVariableValueDictionary;
    }

    /**
     * @return a sorted Vector<String> containing the names of user-defined variables currently defined within Compute.
     */
    public Vector<String> getUserVariableNames() {
        Enumeration names = userVariableValueDictionary.keys();
        Vector<String> keyNames = new Vector<String>(100);
        while (names.hasMoreElements()) {
            keyNames.addElement((String) names.nextElement());
        }
        return mergeSort(keyNames);
    }

    /**
     * @return a Vector<String> containing the answer from the evaluated <i>expression </i>. The answer is wrapped in a
     *         Vector<String> and is a scalar if the Vector<String> size is 1 or a sequence if the Vector<String>'s size
     *         is > 1
     */
    public Vector<String> parse(String expression) throws ComputeExpressionException {
        /* Melanie -- this is where the interface gets the Compute
       engine to do a calculation.  The input is the string
       of the expression.  The return value is a Vector<String>
       containing the value(s) in the answer as strings. It
       is processed further in the next method.
       */
        setExpression(expression);
        boolean success = parseAndCheck();
        if (success) {
            Vector<String> a = new Vector<String>();

            if (endType == Compute.SCALAR) {
                a.addElement(String.valueOf(returnScalarResult()));
            } else {
                double[] d = returnVectorResult();
                for (int i = 0; i < d.length; ++i) {
                    a.addElement(String.valueOf(d[i]));
                }
            }
            return a;
        } else {
            return null;
        }
    }

    /**
     * @return a String containing the answer from the evaluated <i>expression </i>.
     */
    public String parseIntoString(String expression) throws ComputeExpressionException {
        Vector<String> a = parse(expression);
        String s = "";
        if (a != null) {
            for (int i = 0; i < a.size(); ++i) {
                s = s + a.get(i) + " ";
            }
        } else {
            s = "0.0";
        }
        return s.trim();
    }


    public void diagnosticPrintln(String line) {
        /* Melanie -- this and all subsequent methods are
           called by Control; see its definition in Compute.
           */
        /* if (diagnosisToFile) TcalcEnv.diagnosticPrintln( line );
        else */
        diagnosticPrint(line + "\n");
    }

    public void diagnosticPrint(String phrase) {
        userInterface.print(phrase);
    }

    public void openDiagnosisFile() {
        /* TcalcEnv.openDiagnosticFile(); */
    }

    public void closeDiagnosisFile() {
        /* TcalcEnv.closeDiagnosticFile(); */
    }


    public void printFunctionAliases(double choice) {
        /* Melanie -- this and the next method are used to
           change the documentation files when new functions and
           constants are added.
           */
        //
        // choice = 0 prints: function name (tab)  aliases (newline)
        // choice = 1 prints HTML: name linked to bookmark of same name, then alias list, then newline.
        // choice = 2 prints HTML: alias name (tab) alias name linked to bookmark of associated function name.  To use this in the help file, get rid of first part of each line, only there to get alphabetization right.
        //
        int I, J;
        Vector<String> aliases = new Vector<String>(NumberOfFunctionNames);
        if (choice == 0) {
            for (I = 1; I <= NumberOfFunctionNames; I++) {
                if (!FunctionNames[I].equals("Control")) {
                    aliases.addElement(FunctionNames[I] + "\t" + FunctionAliases[I]);
                }
            }
        } else if (choice == 1) {
            for (I = 1; I <= NumberOfFunctionNames; I++) {
                if (!FunctionNames[I].equals("Control")) {
                    aliases.addElement("\n<tr> \n<td>&nbsp;<a href=\"#" + FunctionNames[I] + "\">" + FunctionNames[I]
                            + "</a></td> \n\n<td>" + FunctionAliases[I] +
                            "</td>\n</tr>");
                }
            }
        } else if (choice == 2) {
            StringSplitter aliasesEachName;
            Vector<String> aliasVector;
            for (I = 1; I <= NumberOfFunctionNames; I++) {
                if (!FunctionNames[I].equals("Control")) {
                    aliasesEachName = new StringSplitter(FunctionAliases[I]);
                    for (J = 0; J < aliasesEachName.size(); J++) {
                        aliases.addElement(aliasesEachName.elementAt(J) + "\t<a href=\"#" + FunctionNames[I] + "\">"
                                + aliasesEachName.elementAt(J) + "</a>");
                    }
                }
            }
            aliases = TrianaSort.mergeSort(aliases);
        }
        /* TcalcEnv.printFunctionAliases( aliases ); */
    }


    public void printConstantAliases(double choice) {
        //
        // choice = 0 prints: constant name (tab) value (tab) aliases (newline)
        // choice = 1 prints HTML: name linked to bookmark of same name, then alias list, then newline.
        // choice = 2 prints HTML: alias name (tab) alias name linked to bookmark of associated constant name.  To use this in the help file, get rid of first part of each line, only there to get alphabetization right.
        //
        int I, J;
        Vector<String> aliases = new Vector<String>(NumberOfConstants);
        if (choice == 0) {
            for (I = 1; I <= NumberOfConstants; I++) {
                aliases.addElement(ConstantNames[I] + "\t"
                        + String.valueOf(((double[]) (ConstantValueDictionary.get(ConstantNames[I])))[0]) + "\t"
                        + ConstantAliases[I]);
            }
        } else if (choice == 1) {
            for (I = 1; I <= NumberOfConstants; I++) {
                aliases.addElement("\n<tr> \n<td>&nbsp;<a href=\"#" + ConstantNames[I] + "\">" + ConstantNames[I]
                        + "</a></td> \n\n<td>" + ConstantAliases[I] +
                        "</td>\n</tr>");
            }
        } else if (choice == 2) {
            StringSplitter aliasesEachName;
            Vector<String> aliasVector;
            for (I = 1; I <= NumberOfConstants; I++) {
                aliasesEachName = new StringSplitter(ConstantAliases[I]);
                for (J = 0; J < aliasesEachName.size(); J++) {
                    aliases.addElement(aliasesEachName.elementAt(J) + "\t<a href=\"#" + ConstantNames[I] + "\">"
                            + aliasesEachName.elementAt(J) + "</a>");
                }
            }
            aliases = TrianaSort.mergeSort(aliases);
        }

        /* TcalcEnv.printConstantAliases( aliases ); */
    }


}













