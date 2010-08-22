package math.calculator;

import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Compute performs stream-oriented arithmetic, interpreting an input arithmetic expression that can contain numbers,
 * built-in constants, built-in functions, the usual arithmetic operations, and references to data arriving at an input
 * node. Input data is passed to Compute via the UserInterfaceManager class.
 *
 * @author Bernard Schutz
 *         <p/>
 *         Longer description:
 *         <p/>
 *         Compute parses an arithmetic expression into a form suitable for evaluation by a recursive algorithm.  The
 *         expression is first split into tokens, which are logical building blocks of the expression (numbers, names of
 *         functions, names of operators, etc).  These are then grouped, by means of brackets, into simple evaluation
 *         units: either arguments of functions or simple binary operator groups in the form (argument_1 operator
 *         argument_2), which is the normal arithmetic form, e.g. ( a + b ).  If the arguments in turn contain
 *         expressions in brackets, these will be evaluated recursively.  Binary operators follow the rules of
 *         stream-oriented arithmetic: if the arguments are simple numbers, the usual rules apply.  But if one or both
 *         arguments are vectors (sequences of numbers) then the result is a vector obtained by applying the operation
 *         sequentially to each element of the agument(s) in turn.  Most built-in functions behave in the same way, but
 *         some are special and are described separately.
 *         <p/>
 *         Numerical values can be held as numbers (scalar numbers that are explicitly given in the arithmetic
 *         expression), constants (scalar numbers with pre-defined names that must be looked up in an internal table),
 *         and variables ( scalars or vectors that are obtained from input to the Triana routine).  Variables can be
 *         either constant (double) values, which are called scalars in this program (denoted by #1c, #2c, ... in an
 *         expression); or data sets (1-dimensional arrays of doubles), which are called vectors in this program
 *         (denoted by #1s, #2s, ...).  The variable name begins with #, is followed by a postive integer showing which
 *         input stream the data comes from, and is finished by a c or s to denote constant or data set (vector). Dummy
 *         variables are allowed in certain expressions, and their names must begin with @.  Currently they are allowed
 *         only in the "for" construction, which implements a limited kind of loop.
 *         <p/>
 *         An effort has been made to accommodate a variety of common formats in the arithmetic expression (e.g.
 *         exponentiation is recognized if the user types ^, ^^, or **) and to convert them to a standard internal form.
 *         A number of built-in functions of one, two, or three variables, and of pre-defined constants with reserved
 *         names are available.  They often also can be invoked by the user with several names, called aliases (e.g.
 *         arccos and acos).
 *         <p/>
 *         An important feature of stream-oriented arithmetic when applied to data sets (vectors) is that the number of
 *         operations is sensitive to the order in which operations are evaluated.  Compute therefore makes an effort to
 *         optimize the expression before it is evaluated. This involves re-ordering commuting operations, grouping
 *         repeated divisions into a single divide, replacing products of exponentials with a single exponential, and a
 *         few other steps.  The user is allowed to turn optimization off, but this is not recommended.
 * @version 2.0 15 December 1998
 */
//
// Improvements in the programming that should be made are:
// 1. Convert a few variables and functions to boolean, such as DidSomething
// 	and the return values of functions containing it.
// 2. Make all function lists into Hashtables directly.
// 3. Make tokens into proper objects containing all the auxilliary
// 	variables, and make the list of tokens a java Vector.
// 	This will save time in optimization steps.
// 4. Put named constants into a file that can be edited.  Implement = to
// 	store results to named constants, including temporary memories.
// 5. Add more aliases.  Improve internal names: use _ for multiple word
// 	names, each word caps, taken from first alias always.
// 6. Do all the JavaDoc stuff.
// 7. Make this into a package with lots of individually compiled programs.
// 8. Bug: checking array bounds not done well enough.  Internally generated ones not checked.
// 9. Bug: if input vectors genuinely have length 1 they are assumed scalars and program fails.
// 10. Reduce functions to just one type: arguments are a list in all cases.
//
// Things that are missing so far are:
// 1. Complex arithmetic.
// 2. Functions:  sequential trig etc functions (quicker),
//      stats functions like t-test, chisquare, regression
// 3. Use Bignum to do arbitrary-precision real arithmetic, Bigint
// 	to do unlimited-precision integer arithmetic --  for this
// 	need to implement integer versions of functions.
//
// Test strings that have been parsed correctly are:
//
// Exp(5)**4+sin(#12)+cos(-tan(cosh(4.0e-2)/45+Pi/3))*8.4*((7.8))
// cos(min(1e-3,4.*sin(atan(PI/2))))
// Exp(sin(-cos(33E-1)))*32e-15/Exp(#2)*5/#1*exp(3.2+5*#1*exp(3.4)*Pi)
// e^#8/#1*(2^(3+#1+6))^4/1e-6/e^4*#1/7*#2/9/#3
// e^4/3/#2/6/#1*exp(15*#2)*(#3^2)^#4
// exp(use(1,2,sequence(3),use(4,5)*3))
//
// Define class-wide variables and arrays.
//

public class Compute {
    String Expression;
    public int NumberOfTokens;
    public int NumberOfConstants, NumberOfUnaryOperators, NumberOfFunctionsOne, NumberOfFunctionsTwo,
            NumberOfFunctionsThree, NumberOfFunctionsList, NumberOfFunctionNames;
    public String[] Token, ConstantAliases, ConstantNames, FunctionNames;
    byte[] Properties, Type, BinaryOperatorSecondArgumentType, ConstantTypes, FunctionOneTypes, FunctionTwoTypes,
            FunctionThreeTypes, FunctionListTypes, ArgumentType;
    int[] Level, ArgumentStart, ArgumentEnd, ExponentialFunctionArgument;
    boolean[] ExponentialFunction, HasBeenCopied, HasBeenOptimized, InputNodeAccessed, InputNodeIsScalar;
    public Hashtable BuiltInConstantDictionary, FunctionDictionary, ConstantValueDictionary, ConstantTypeDictionary,
            FunctionOneTypeDictionary, FunctionTwoTypeDictionary, FunctionThreeTypeDictionary,
            FunctionListTypeDictionary;
    public Hashtable userVariableValueDictionary, userVariableTypeDictionary;
    public Hashtable temporaryVariableValueDictionary;
    String[] UnaryNames, UnaryValues, FunctionAliases, FunctionOneNames, FunctionTwoNames, FunctionThreeNames,
            FunctionListNames, typeName;
    public StringTokenizer Aliases;
    int SequenceNumber, SequenceStart, SequenceEnd, NumberOfArgumentsCopied;
    int NumberScalarMults, NumberVectorMults, NumberScalarDivides, NumberVectorDivides, NumberScalars, NumberVectors,
            NumberExponentialFunctionScalars, NumberExponentialFunctionVectors, NumberScalarAdds, NumberVectorAdds;
    boolean UnwrappedArgumentOfNegate;
    boolean InputNodesAccessed;
    boolean InputVectorsExist;
    public Hashtable InputNodeList = new Hashtable(5);
    String argumentTypeString;
    boolean trivialExpression;
    long lastRandomSeed = System.currentTimeMillis();
//
//int InputVectorLength = 0;
//
    public boolean displayProgress = false;
    public boolean optimise = true;
    public boolean diagnosisToFile = false;
//public UserInterfaceManager userInterface;
    public ComputeManager userInterface;
    public byte endType;
//
// Codes for the different types of tokens: these are the possible values
// for the elements of the array Properties.
//
    public static final byte Remove = 0;
    public static final byte BuiltInName = 1;
    public static final byte Comma = 2;
    public static final byte OpenBracket = 3;
    public static final byte CloseBracket = 4;
    public static final byte OpenSquareBracket = 5;
    public static final byte CloseSquareBracket = 6;
    public static final byte Number = 7;
    public static final byte ConstantScalar = 8;
    public static final byte ConstantVector = 9;
    public static final byte ConstantString = 10; // this is used only in the calculator
    public static final byte Variable = 11;  // this is used during tokenization
    public static final byte VariableScalar = 12;  // these two are assigned during tokenization
    public static final byte VariableVector = 13;
    public static final byte UnaryOperator = 14;
    public static final byte BinaryOperator = 15;
    public static final byte Function = 16; // this is used during tokenization
    public static final byte FunctionOfOneVariable = 17;  // these 3 are assigned after tokenization
    public static final byte FunctionOfTwoVariables = 18;
    public static final byte FunctionOfThreeVariables = 19;
    public static final byte FunctionOfListVariables = 20;
    public static final byte AssignmentOperator = 21; // used during tokenization
    public static final byte Assignment = 22;
    public static final byte IncrementOperator = 23; // used during tokenization
    public static final byte TemporaryVariable = 24;
    public static final byte ForLoop = 25;
//
// Codes for numerical types
//
    public static final byte ILLEGAL = -1;
    public static final byte SCALAR = 0;
    public static final byte VECTOR = 1;
    public static final byte STRING = 2;
//
// Codes for function types, assigned by function definitions, used by TypeBracketPair
//
    public static final byte ScalarToScalar = 0;
    public static final byte VectorToScalar = 1;
    public static final byte ScalarToVector = 2;
    public static final byte VectorToVector = 3;
    public static final byte AnyToScalar = 4;
    public static final byte AnyToVector = 5;
    public static final byte AnyToSame = 6;
    public static final byte AnyToOpposite = 7;
//
// Codes for optimizing sequences of operations, held in array
// ArgumentType
//
    public static final byte AddOrSubtractScalar = 1;
    public static final byte AddOrSubtractVector = 2;
    public static final byte MultiplyByScalar = 3;
    public static final byte MultiplyByVector = 4;
    public static final byte DivideByScalar = 5;
    public static final byte DivideByVector = 6;
    public static final byte MultOrDivByExponential = 7;
//
// The variable angleScale is defined so that if it multiplies
// an angle in the current angle measure, then the result will be
// an angle in radians.  inverseAngleScale does the inverse.
//
    public double angleScale, inverseAngleScale;
//
// Main procedure creates an instance of the Compute class and invokes the
// method Parse to do the work.  Parse produces the token list, optimized, but
// does not do the calculation. That will be done by the method Evaluate.
//
// public static void main(String argv[]) {
//	String InputString = argv[0];
//	for (int I = 1; I < argv.length; I++ ) {
//		if (argv[I].equals("-display") ) display = true;
//		else if (argv[I].equals("-no-optimize") ) optimise = false;
//	}
//	Compute Example = new Compute(inputString);
//	Compute example = new Compute();
//	example.parse();
//	if ( optimise ) example.optimize();
//	example.evaluate();
// 	}

//
// Constructor uses the data source
//
//public Compute( UserInterfaceManager userInterface) {

    public Compute(ComputeManager userInterface) {
        this.userInterface = userInterface;
        this.Initialize();
    }

    public void setExpression(String inputString) {
        Expression = inputString.toLowerCase();
    }

    public boolean parseAndCheck() {
        try {
            parse();
        } catch (ComputeExpressionException e) {
            userInterface.println(e.getMessage());
            return false;
        }
        return true;
    }


    public void parse() throws ComputeExpressionException, ComputeSyntaxException {
//	Initialize();
        Token = new String[300];
        Properties = new byte[300];
        Level = new int[300];
        Type = new byte[300];
        BinaryOperatorSecondArgumentType = new byte[300];
        ExponentialFunction = new boolean[300];
        ArgumentStart = new int[100];
        ArgumentEnd = new int[100];
        ArgumentType = new byte[100];
        ExponentialFunctionArgument = new int[100];
        HasBeenCopied = new boolean[100];
        HasBeenOptimized = new boolean[300];
        trivialExpression = false;
//
//	 Now start processing the string.
//
//	 Use the function ReplaceInString to convert to standard form:
//	 trim whitespace, remove all blanks, change "^^" and "**" to "^",
//	 but don't temporarily replace
//	 the power-of-ten notation "e" or "E" by unique internal
//	 symbol "@" until the string is tokenized.  This avoids confusion
//	 with the transcendental number e while string is parsed.
//
        String WorkingString = ReplaceInString(Expression.trim(), " ", "");
        CheckParens(WorkingString);
        WorkingString = ReplaceInString(WorkingString, "^^", "^");
        WorkingString = ReplaceInString(WorkingString, "**", "^");
        //WorkingString = ReplacePowerOfTen( WorkingString );
        if (!WorkingString.startsWith("for(")) {
            Expression = "(" + WorkingString + ")";
        } else {
            Expression = WorkingString;
        }
        if (displayProgress) {
            diagnosticPrintln(Expression);
        }
        TokenizeString();
        //
        // If there are only 3 tokens, including the outer brackets, then
        // there is only a single token inside, and it does not need to be
        // parsed, or optimised.  Set the trivialExpression flag and return.
        //
        if (NumberOfTokens == 3) {
            trivialExpression = true;
        }
        if (trivialExpression) {
            switch (Properties[2]) {
                case ConstantScalar:
                case VariableScalar:
                case Number:
                    endType = SCALAR;
                    break;
                case ConstantVector:
                case VariableVector:
                    endType = VECTOR;
                    break;
                case ConstantString:
                    endType = STRING;
                    break;
                default:
                    userInterface.println(
                            "Error: a single-term expression can only be evaluated if it is a constant, variable, or pure number.");
                    return;
            }
            Type[1] = endType;
            Type[2] = endType;
            Type[3] = endType;
            DisplayTokens("Now the tokens have been typed, and the overall type = " + String.valueOf(endType));
            return;
        }
        ClassifyFunctions();
        while (ConvertUnaryOperators() == 1) {
            RemoveMarkedTokens(1, NumberOfTokens);
            SetBracketLevels(" SBL Invocation 1 ");
            RemoveUselessBrackets();
            DisplayTokens("Converted Unary Operators.");
        }
        //
        // Test again to see if there are only 3 tokens including outer
        // brackets.  IF so, set the trivialExpression flag and return.
        //
        if (NumberOfTokens == 3) {
            trivialExpression = true;
        }
        if (trivialExpression) {
            switch (Properties[2]) {
                case ConstantScalar:
                case VariableScalar:
                case Number:
                    endType = SCALAR;
                    break;
                case ConstantVector:
                case VariableVector:
                    endType = VECTOR;
                    break;
                case ConstantString:
                    endType = STRING;
                    break;
                default:
                    userInterface.println(
                            "Error: a single-term expression can only be evaluated if it is a constant, variable, or pure number.");
                    return;
            }
            Type[1] = endType;
            Type[3] = endType;
            DisplayTokens("Now the tokens have been typed, and the overall type = " + String.valueOf(endType));
            return;
        }

//
//	 Now insert brackets around functions, in order to establish
//	 the precedence of these evaluations over binary operators,
//	 which will be placed inside brackets later.
//
        SurroundFunctionsWithBrackets();
        DisplayTokens("Surrounded functions with brackets.");
//
//	 Now begin to insert brackets that will tell us where to use recursion
//	 to evaluate binary operators of different precedence.  Assume here that
//	 the only binary operators are + - * / ^.  (If there are others, then
//	 this section must be changed.)  Do this operation repeatedly according
//	 to the precedence of the operators: ^ first, then */ together, then +- together.
//
        SurroundBinOpsWithBrackets("^", "^", 1, NumberOfTokens);
        DisplayTokens("Surrounded power operations by brackets");
        SurroundBinOpsWithBrackets("*", "/", 1, NumberOfTokens);
        DisplayTokens("Surrounded multiplications by brackets");
        SurroundBinOpsWithBrackets("+", "-", 1, NumberOfTokens);
        DisplayTokens("Surrounded binary operators with brackets");
//
//	 Now change round brackets to square ones for those pairs that
//	 delineate the argument field of functions.  These serve purely
//	 a place-keeping function in the expression, while we want to
//	 use the round brackets to trigger the recursive evaluation of
//	 sub-expressions.  Arguments of functions do not always need recursion,
//	 since they could be simple constants.  If the arguments do need
//	 recursive evaluation themselves, then this will be forced by
//	 round brackets inserted later.
//
        ConvertToSquareBrackets();
        SetBracketLevels(" SBL Invocation 2 ");
        RemoveUselessBrackets();
        DisplayTokens("Replaced round brackets denoting function arguments with square ones.");
//
//	Change uses of the Pow function into ^
//
        while (ReplacePowByHat() == 1) {
            RemoveMarkedTokens(1, NumberOfTokens);
            SetBracketLevels(" SBL Invocation 3 ");
        }
        RemoveUselessBrackets();
        DisplayTokens("Changed Pow(a,b) to a^b");
//
//	 Replace any incidence of the constant E raised to a power
//	 by the exponential function.
//
        while (ReplaceEPower() == 1) {
            RemoveMarkedTokens(1, NumberOfTokens);
            SetBracketLevels(" SBL Invocation 4 ");
        }
        RemoveUselessBrackets();
        DisplayTokens("Changed E^(...) to (exp[(...)])");
//
//	 Now pay attention to whether operations will be on vectors or scalars.
//	 Run through the string determining whether a datum is a vector or
//	 scalar, whether an operator or function needs to perform a vector or
//	 scalar operation, and whether a nested bracket returns a vector or
//	 scalar.  These values are held in an array Type which was
//	 declared at the start.
//
        if (Properties[1] == OpenBracket) {
            endType = TypeBracketPair(2, NumberOfTokens - 1);
        } else if (Properties[1] == ForLoop) {
            endType = TypeForLoop(3, NumberOfTokens - 1);
        }
        Type[1] = endType;
        Type[NumberOfTokens] = endType;
        DisplayTokens("Now the tokens have been typed, and the overall type = " + String.valueOf(endType));
        if (optimise) {
            optimize();
        }
        return;
    } // end Parse()


    public void optimize() throws ComputeExpressionException, ComputeSyntaxException {
//
// Now begin to optimize arrangement of tokens in string
//
// First optimization is to replace instances of the exponential function
// raised to a power by bringing the power inside the argument as a
// multiplier of the original argument of the function, and to do
// a similar thing with powers of powers: ((a^b)^c) -> (a^(b*c)).
//
        if (trivialExpression) {
            return;
        }
        int RefurbishArgument;
        while ((RefurbishArgument = OptimizeExpToPower()) >= 0) {
            RemoveMarkedTokens(1, NumberOfTokens);
            SetBracketLevels(" SBL Invocation 5 ");
            if (RefurbishArgument > 0) {
                SurroundBinOpsWithBrackets("*", "/", RefurbishArgument - 1,
                        FindPairedBracket(RefurbishArgument - 1, 1, NumberOfTokens));
            }
            RemoveUselessBrackets();
            DisplayTokens("Eliminated powers of the exponential function");
        }
        while ((RefurbishArgument = OptimizePowerOfPower()) >= 0) {
            RemoveMarkedTokens(1, NumberOfTokens);
            SetBracketLevels(" SBL Invocation 6 ");
            if (RefurbishArgument > 0) {
                SurroundBinOpsWithBrackets("*", "/", RefurbishArgument - 1,
                        FindPairedBracket(RefurbishArgument - 1, 1, NumberOfTokens));
            }
            RemoveUselessBrackets();
            DisplayTokens("Eliminated powers of powers");
        }
//
// Now do the main sorting of a multiplication sequence.
// First set the optimization flag array to false (not optimized).
// Then find and sort sequences of multiplications, divisions,
// and exponentials, ready for further optimization steps.
//
        for (int I = 1; I <= NumberOfTokens; I++) {
            HasBeenOptimized[I] = false;
        }
        while (FindSequence(1, '*') > 0) {
            SortScalarVectorExpFactors();
            RemoveMarkedTokens(1, NumberOfTokens);
            DisplayTokens("Look at what has happened to a multiplication sequence.");
            SetBracketLevels(" SBL Invocation 7 ");
            RemoveUselessBrackets();
        }
        DisplayTokens("All the multiplication sequences have been sorted.");
//
// Combine products of exponentials into a single exponential of the sum of the arguments.
//
        while ((RefurbishArgument = ConsolidateExponentials()) >= 0) {
            DisplayTokens("After exponential consolidation, but before removing marked tokens");
            RemoveMarkedTokens(1, NumberOfTokens);
            DisplayTokens("After exponential consolidation, and after removing marked tokens");
            SetBracketLevels(" SBL Invocation 8 ");
            DisplayTokens(
                    "After exponential consolidation, and after setting levels.  Next step will use RefurbishArgument = "
                            + String.valueOf(RefurbishArgument));
            if (RefurbishArgument > 0) {
                SurroundBinOpsWithBrackets("+", "-", RefurbishArgument,
                        FindPairedBracket(RefurbishArgument, 1, NumberOfTokens));
            }
            DisplayTokens("After exponential consolidation, but before removing useless brackets");
            RemoveUselessBrackets();
            DisplayTokens("An exponential sequence has been consolidated.");
        }
        DisplayTokens("Now all the exponential sequences have been consolidated.");
//
// Combine successive divisions into a single division by the product of the divisors.
// Then re-do the assignment of scalar and vector operations.
//
        while ((RefurbishArgument = ConsolidateDivisions()) >= 0) {
            DisplayTokens("After division consolidation, but before removing marked tokens");
            RemoveMarkedTokens(1, NumberOfTokens);
            SetBracketLevels(" SBL Invocation 9 ");
            if (RefurbishArgument > 0) {
                SurroundBinOpsWithBrackets("*", "/", RefurbishArgument + 1,
                        FindPairedBracket(RefurbishArgument + 1, 1, NumberOfTokens));
            }
            DisplayTokens("After division consolidation, but before removing useless brackets");
            RemoveUselessBrackets();
            DisplayTokens("A division sequence has been consolidated");
        }
        DisplayTokens("Now all the division sequences have been consolidated.");
        if (Properties[1] == OpenBracket) {
            endType = TypeBracketPair(2, NumberOfTokens - 1);
        } else if (Properties[1] == ForLoop) {
            endType = TypeForLoop(3, NumberOfTokens - 1);
        }
        Type[1] = endType;
        Type[NumberOfTokens] = endType;
        DisplayTokens("The types of the tokens have been re-computed.");
//
// ConvertScalarMultVectorExponential(); -- not yet written: not clear that it is useful enough.
// DisplayTokens( "Now a product of a scalar and the exponentiation of a vector have been combined");
//
// Now optimize addition sequences.  Reset optimization flags for safety.
//
        for (int I = 1; I <= NumberOfTokens; I++) {
            HasBeenOptimized[I] = false;
        }
//
// Now find addition sequences, then sort.
// The first step is to find any instances of the function "Negate" and distribute it over
// its argument as far as possible.  This must  be repeated each time, since the function
// SortSummationTerms() might introduce new instances of Negate.
//
        UnwrappedArgumentOfNegate = false;
        while (DistributeNegates(true) > 0) {
            DisplayTokens("Look at the current distribution of Negate.");
        }
        if (UnwrappedArgumentOfNegate) {
            SurroundBinOpsWithBrackets("+", "-", 1, NumberOfTokens);
            RemoveUselessBrackets();
        }
        DisplayTokens("Look at the final distribution of Negate.");
        if (Properties[1] == OpenBracket) {
            endType = TypeBracketPair(2, NumberOfTokens - 1);
        } else if (Properties[1] == ForLoop) {
            endType = TypeForLoop(3, NumberOfTokens - 1);
        }
        Type[1] = endType;
        Type[NumberOfTokens] = endType;
        DisplayTokens("The types of the tokens have been re-computed.");
        while (FindSequence(1, '+') > 0) {
            SortSummationTerms();
            RemoveMarkedTokens(1, NumberOfTokens);
            SetBracketLevels(" SBL Invocation 10 ");
            DisplayTokens("Look at what has happened to an addition sequence.");
            RemoveUselessBrackets();
            UnwrappedArgumentOfNegate = false;
            while (DistributeNegates(true) > 0) {
                DisplayTokens("Look at the current distribution of Negate.");
            }
            DisplayTokens("Look at the final distribution of Negate.");
            if (UnwrappedArgumentOfNegate) {
                SurroundBinOpsWithBrackets("+", "-", 1, NumberOfTokens);
                RemoveUselessBrackets();
            }
            DisplayTokens("Look at the final distribution of Negate.");
        }
        DisplayTokens("Now the addition sequences have been sorted.");
        if (Properties[1] == OpenBracket) {
            endType = TypeBracketPair(2, NumberOfTokens - 1);
        } else if (Properties[1] == ForLoop) {
            endType = TypeForLoop(3, NumberOfTokens - 1);
        }
        Type[1] = endType;
        Type[NumberOfTokens] = endType;
        DisplayTokens("The types of the tokens have been re-computed.");
        return;
    } // end of optimization

    public String getOptimisedString() {
        String optimisedString = new String("");
        for (int I = 1; I <= NumberOfTokens; I++) {
            optimisedString += Token[I];
        }
        return optimisedString;
    }

/*
public void evaluate() {
	double ScalarResult;
	int InputLength = 2;
	double InputData[][] = {{2.3, 3.6}};
	double VectorResult[] = new double[1];
	int NumberOfInputVectors = CountNumberOfInputVectors();
	InputVectorsExist = (NumberOfInputVectors > 0);
//	if (InputVectorsExist) {
//		InputLength = GetInputVectorLength();
//		InputData = new double[NumberOfInputVectors][InputLength];
//		VectorResult = new double[InputLength];
//	}
	if (Type[1] == SCALAR) {
		ScalarResult = EvaluateScalarBracketPair(2, NumberOfTokens - 1 );
		userInterface.println( "Result = " + String.valueOf(ScalarResult) );
	}
	else {
 		VectorResult = EvaluateVectorBracketPair(2, NumberOfTokens - 1 );
		userInterface.println("Result =");
		for (int I = 0; I < VectorResult.length ; I++) userInterface.println( String.valueOf(VectorResult[I]) );
	}
	return;
} // end evaluation method
*/

    public double returnScalarResult() throws ComputeExpressionException {
        double value = 0.0;
        InitializeInputNodeValues();
        if (Properties[1] == OpenBracket) {
            value = EvaluateScalarBracketPair(2, NumberOfTokens - 1);
        } else if (Properties[1] == ForLoop) {
            value = EvaluateScalarForLoop(3, NumberOfTokens - 1);
        }
        return value;
    }

    public double[] returnVectorResult() throws ComputeExpressionException {
        double[] value = new double[1];
        value[0] = 0.0;
        InitializeInputNodeValues();
        if (Properties[1] == OpenBracket) {
            value = EvaluateVectorBracketPair(2, NumberOfTokens - 1);
        } else if (Properties[1] == ForLoop) {
            value = EvaluateVectorForLoop(3, NumberOfTokens - 1);
        }
        return value;
    }


    public String returnScalarResultString() throws ComputeExpressionException {
        return String.valueOf(returnScalarResult());
    }

    public String returnVectorResultString() throws ComputeExpressionException {
        double[] vectorResult;
        String returnString = new String("");
        vectorResult = returnVectorResult();
        for (int I = 0; I < vectorResult.length; I++) {
            returnString =
                    returnString + String.valueOf(vectorResult[I]) + "\n";
        }
        return returnString;
    }


    public void InitializeInputNodeValues() {
        InputNodeAccessed = new boolean[50];
        InputNodeIsScalar = new boolean[50];
        InputNodesAccessed = false;
        InputNodeList.clear();
    }

/*
//dummy routine until we begin inputting vector data
public int CountNumberOfInputVectors() {
	int NumberOfInputVectors = 1;
	return NumberOfInputVectors;
}

//dummy routine until we begin inputting vector data
public int GetInputVectorLength() {
	int InputVectorLength = 4;
	return InputVectorLength;
}
*/

    public double[] VectorOf(String TokenString) throws ComputeExpressionException {
        InputVectorsExist = true;
        return GetInputData(TokenString);
    }

    public double ScalarOf(String TokenString) throws ComputeExpressionException {
        return GetInputData(TokenString)[0];
    }

    public double[] GetInputData(String TokenString) throws ComputeExpressionException {
        boolean VariableIsScalar = false;
        String Key;
        int InputNodeNumber = 0;
        double InputArrayTemp[] = new double[2];
        if (!TokenString.startsWith("#")) {
            userInterface
                    .println("Variable token string " + TokenString + " does not begin with #."); // trap this error!!
            return null;
        }
        if (TokenString.endsWith("c")) {
            VariableIsScalar = true;
            InputNodeNumber = Integer.parseInt(TokenString.substring(1, TokenString.length() - 1));
        } else if (TokenString.endsWith("s")) {
            InputNodeNumber = Integer.parseInt(TokenString.substring(1, TokenString.length() - 1));
        } else if (Character.isDigit(TokenString.charAt(TokenString.length() - 1))) {
            InputNodeNumber = Integer.parseInt(TokenString.substring(1, TokenString.length()));
        } else {
            throw new ComputeExpressionException("variable name  " + TokenString + " does not end in digit, c, or s.");
        }
        Key = "#" + String.valueOf(InputNodeNumber);
        if (InputNodeAccessed[InputNodeNumber]) {
            if (InputNodeIsScalar[InputNodeNumber] == VariableIsScalar) {
                return (double[]) InputNodeList.get(Key);
            } else if (InputNodeIsScalar[InputNodeNumber]) {
                throw new ComputeExpressionException("Input NodeCable " + String.valueOf(InputNodeNumber)
                        + " was first accessed as a constant, but later as a sequence.");
            } else {
                throw new ComputeExpressionException("Input NodeCable " + String.valueOf(InputNodeNumber)
                        + " was first accessed as a sequence, but later as a scalar.");
            }
        } else {
            InputNodeAccessed[InputNodeNumber] = true;
            InputArrayTemp = userInterface.getInputData(InputNodeNumber);
            if (InputArrayTemp == null) {
                userInterface.println("No input node with number " + String.valueOf(InputNodeNumber));
            } else if (InputArrayTemp.length == 1) {
                InputNodeList.put(Key, InputArrayTemp);
                InputNodeIsScalar[InputNodeNumber] = true;
                if (!VariableIsScalar) {
                    userInterface.println("Input NodeCable " + String.valueOf(InputNodeNumber)
                            + " referenced as a sequence in the expression, but the data coming in is a constant.   It will be treated as a sequence of length 1.");
                    InputNodeIsScalar[InputNodeNumber] = false;
                }
            } else {
                InputNodeList.put(Key, InputArrayTemp);
                InputNodeIsScalar[InputNodeNumber] = false;
                if (VariableIsScalar) {
                    userInterface.println("Input NodeCable " + String.valueOf(InputNodeNumber)
                            + " referenced as a constant but the data coming in is a data set.  Only the first element of the set will be used.");
                    InputNodeIsScalar[InputNodeNumber] = true;
                } else if (!InputNodesAccessed) {
                    InputNodesAccessed = true;
                }

            }
        }
        return InputArrayTemp;
    }


    public void CheckParens(String checkString) throws ComputeExpressionException {

        int index;
        int numberOfOpenParens = 0;
        int numberOfClosedParens = 0;
        int excess;

        index = 0;
        while ((index < checkString.length()) && ((index = checkString.indexOf("(", index) + 1) > 0)) {
            numberOfOpenParens++;
        }
        index = 0;
        while ((index < checkString.length()) && ((index = checkString.indexOf(")", index) + 1) > 0)) {
            numberOfClosedParens++;
        }
        excess = numberOfOpenParens - numberOfClosedParens;

        if (excess > 1) {
            throw new ComputeExpressionException("non-matching round brackets.  There are "
                    + String.valueOf(numberOfOpenParens - numberOfClosedParens)
                    + " extra open brackets in the expression.");
        } else if (excess == 1) {
            throw new ComputeExpressionException(
                    "non-matching round brackets.  There is one extra open bracket in the expression.");
        } else if (excess == -1) {
            throw new ComputeExpressionException(
                    "non-matching round brackets.  There is one extra closed bracket in the expression.");
        } else if (excess < -1) {
            throw new ComputeExpressionException("non-matching round brackets.  There are "
                    + String.valueOf(numberOfClosedParens - numberOfOpenParens)
                    + " extra closed brackets in the expression.");
        }

        return;
    }


    public String ReplaceInString(String InString, String FindString, String ReplacementString) {
        String Temp = new String(InString);
        int FindLength = FindString.length();
        int FindPosition = Temp.indexOf(FindString);
        while (FindPosition >= 0) {
            if (FindPosition + FindLength < Temp.length()) {
                Temp = Temp.substring(0, FindPosition) + ReplacementString + Temp.substring(FindPosition + FindLength);
            } else {
                Temp = Temp.substring(0, FindPosition) + ReplacementString;
            }
            FindPosition = Temp.indexOf(FindString);
        }
        return Temp;
    }

    public String ReplacePowerOfTen(String InString) {
//
// This is too simplistic.  It will replace any "e" that is preceded by a digit (incl. decimal).
// This could lead to a bug if a function or constant name were of the form "...1e..".
// The easiest way to avoid this bug is not to allow such function names to be defined:
// keep all digits in function names at the end, for example.
//
        char TestChar;
        String Temp = new String(InString);
        int FindPosition = Temp.indexOf("e", 1);
        while (FindPosition > 0) {
            TestChar = Temp.charAt(FindPosition - 1);
            if (Character.isDigit(TestChar) || (TestChar == '.')) {
                Temp = Temp.substring(0, FindPosition) + "@" + Temp.substring(FindPosition + 1);
            }
            FindPosition = Temp.indexOf("e", FindPosition + 2);
        }
        FindPosition = Temp.indexOf("E");
        while (FindPosition > 0) {
            TestChar = Temp.charAt(FindPosition - 1);
            if (Character.isDigit(TestChar) || (TestChar == '.')) {
                Temp = Temp.substring(0, FindPosition) + "@" + Temp.substring(FindPosition + 1);
            }
            FindPosition = Temp.indexOf("E", FindPosition + 2);
        }
        return Temp;
    }


    public byte TokenType(char c, int PrevProp, int position) throws ComputeSyntaxException {
//
// This function classifies tokens according to the first character of the
// token ( c ) and the type of the previous token.  It checks for errors, ie
// token sequences that are not sensible for arithmetic.  If there is
// an error it throws an appropriate exception.
//
// TokenType values are listed at beginning of the macro.
// Type value BuiltInName is used as temporary type at
// this stage for all types whose names have yet to be recognized.  Once
// this function returns, the type of a BuiltInName variable is
// determined, so BuiltInName is never found as a value of PrevProp.
// Square brackets also cannot occur as values of PrevProp because
// they have not yet been introduced.  The input string must always
// follow a function name with an opening bracket to delimit the
// argument.  If not, there can be an error here, since a number or
// variable or constant following a function name is an error.
//
// This program is somewhat inefficient in that it tests for illegal
// values of PrevProp first, only assigning a value to Test if
// there is nothing illegal.  In most arithmetic expressions, there
// will be nothing illegal, so it would be quicker to test for allowed
// values of PrevProp before assigning a value to Test.
//
        byte Test = -1;
        if (Character.isDigit(c) || (c == '.')) {
            if ((PrevProp == Number) || (PrevProp == ConstantScalar) || (PrevProp == ConstantVector)
                    || (PrevProp == VariableScalar) || (PrevProp == VariableVector) || (PrevProp == TemporaryVariable)
                    || (PrevProp == Function) || (PrevProp == CloseBracket)) {
                throwSyntaxException(position, "A digit or decimal point cannot follow a " + typeName[PrevProp] + ".");
            } else {
                Test = Number;
            }
        } else if (c == '#') {
            if ((PrevProp == Number) || (PrevProp == ConstantScalar) || (PrevProp == ConstantVector)
                    || (PrevProp == VariableScalar) || (PrevProp == VariableVector) || (PrevProp == TemporaryVariable)
                    || (PrevProp == Function) || (PrevProp == CloseBracket)) {
                throwSyntaxException(position,
                        "A variable (a name beginning with #) cannot follow a " + typeName[PrevProp] + ".");
            } else {
                Test = Variable;
            }
        } else if ((c == '+') || (c == '-') || (c == '*') || (c == '/') || (c == '^')) {
            if (((c == '+') || (c == '-')) && ((PrevProp == -1) || (PrevProp == OpenBracket) || (PrevProp == Comma))) {
                Test = UnaryOperator;
            } else if ((PrevProp == -1) || (PrevProp == UnaryOperator) || (PrevProp == BinaryOperator)
                    || (PrevProp == Comma) || (PrevProp == OpenBracket) || (PrevProp == Function)) {
                if (PrevProp == -1) {
                    throwSyntaxException(position,
                            "The arithmetic operator " + String.valueOf(c) + " cannot begin the expression.");
                } else {
                    throwSyntaxException(position,
                            "The arithmetic operator " + String.valueOf(c) + " cannot follow a " + typeName[PrevProp]
                                    + ".");
                }
            } else {
                Test = BinaryOperator;
            }
        } else if (c == '(') {
            if ((PrevProp == Number) || (PrevProp == ConstantScalar) || (PrevProp == ConstantVector)
                    || (PrevProp == VariableScalar) || (PrevProp == VariableVector) || (PrevProp == TemporaryVariable)
                    || (PrevProp == CloseBracket)) {
                throwSyntaxException(position, "An open round bracket cannot follow a " + typeName[PrevProp] + ".");
            } else {
                Test = OpenBracket;
            }
        } else if (c == ')') {
            if ((PrevProp == -1) || (PrevProp == BinaryOperator) || (PrevProp == UnaryOperator)
                    || (PrevProp == Function) || (PrevProp == Comma) || (PrevProp == OpenBracket)) {
                if (PrevProp == -1) {
                    throwSyntaxException(position, "A closed round bracket cannot begin the expression.");
                } else {
                    throwSyntaxException(position,
                            "A closed round bracket cannot follow a " + typeName[PrevProp] + ".");
                }
            } else {
                Test = CloseBracket;
            }
        } else if (c == ',') {
            if ((PrevProp == BinaryOperator) || (PrevProp == Comma) || (PrevProp == OpenBracket)
                    || (PrevProp == UnaryOperator) || (PrevProp == Function) || (PrevProp == -1)) {
                if (PrevProp == -1) {
                    throwSyntaxException(position, "A comma cannot begin the expression.");
                } else {
                    throwSyntaxException(position, "A comma cannot follow a " + typeName[PrevProp] + ".");
                }
            } else {
                Test = Comma;
            }
        } else if (c == '@') {
            if ((PrevProp == -1) || (PrevProp == Number) || (PrevProp == VariableScalar) || (PrevProp == VariableVector)
                    || (PrevProp == ConstantScalar) || (PrevProp == ConstantVector) || (PrevProp == TemporaryVariable)
                    || (PrevProp == Function) || (PrevProp == CloseBracket)) {
                if (PrevProp == -1) {
                    throwSyntaxException(position,
                            "A dummy variable (a name beginning with @) cannot begin the expression.");
                } else {
                    throwSyntaxException(position,
                            "A dummy variable (a name beginning with @) cannot follow a " + typeName[PrevProp] + ".");
                }
            } else {
                Test = TemporaryVariable;
            }
        } else {
            if ((PrevProp == Number) || (PrevProp == VariableScalar) || (PrevProp == VariableVector)
                    || (PrevProp == ConstantScalar) || (PrevProp == ConstantVector) || (PrevProp == TemporaryVariable)
                    || (PrevProp == Function) || (PrevProp == CloseBracket)) {
                throwSyntaxException(position,
                        "A variable or function name cannot follow a " + typeName[PrevProp] + ".");
            } else {
                Test = BuiltInName;
            }
        }
        return Test;
    }

    public void throwSyntaxException(int position, String advice) throws ComputeSyntaxException {
        String prefix, error, suffix;
        int realBeginning, realEnd;
        int endOfExpression = Expression.length() - 1;
        realBeginning = (Expression.startsWith("(")) ? 1 : 0;
        realEnd = (Expression.endsWith(")")) ? endOfExpression - 1 : endOfExpression;
        if (position == realBeginning) {
            prefix = "";
        } else {
            prefix = (position <= realBeginning + 2) ? Expression.substring(realBeginning, position)
                    : Expression.substring(position - 3, position);
        }
        error = (position == realEnd) ? error = Expression.substring(position)
                : (error = Expression.substring(position, position + 1));
        if (position == realEnd) {
            suffix = "";
        } else {
            suffix = (position >= realEnd - 3) ? Expression.substring(position + 1, endOfExpression)
                    : Expression.substring(position + 1, position + 4);
        }
        throw new ComputeSyntaxException(prefix, error, suffix, advice);
    }


    public void SetBracketLevels(String Indicator) throws ComputeSyntaxException {
        int CurrentLevel = 0;
        for (int I = 1; I <= NumberOfTokens; I++) {
            if (Properties[I] == OpenBracket) {
                CurrentLevel = CurrentLevel + 1;
                Level[I] = CurrentLevel;
                if ((I > 2) && (Properties[I - 1] == ForLoop)) {
                    Level[I - 1] = CurrentLevel;
                }
            } else if (Properties[I] == CloseBracket) {
                Level[I] = CurrentLevel;
                CurrentLevel = CurrentLevel - 1;
            } else {
                Level[I] = CurrentLevel;
            }
            if (CurrentLevel < 0) {
                throwSyntaxException(I, "Too many closing brackets at this point.");
            }
        }
        if (CurrentLevel > 0) {
            throw new ComputeSyntaxException(" there are unmatched open round brackets.");
        }
        if (CurrentLevel < 0) {
            throw new ComputeSyntaxException(" there are unmatched closed round brackets.");
        }
        for (int I = 1; I <= NumberOfTokens; I++) {
            if (Properties[I] == OpenSquareBracket) {
                if (Level[FindPairedSquareBracket(I)] != Level[I]) {
                    throwSyntaxException(I,
                            "There are unmatched round brackets somewhere within the argument of this function.");
                }
            }
        }
        return;
    }


    public void RemoveMarkedTokens(int StartToken, int EndToken) {
        int ReadFrom = StartToken;
        while ((ReadFrom <= NumberOfTokens) && (Properties[ReadFrom] != Remove)) {
            ReadFrom++;
        }
        int WriteTo = ReadFrom;
        while (ReadFrom <= NumberOfTokens) {
            if (Properties[ReadFrom] != Remove) {
                Token[WriteTo] = new String(Token[ReadFrom]);
                Properties[WriteTo] = Properties[ReadFrom];
                Level[WriteTo] = Level[ReadFrom];
                Type[WriteTo] = Type[ReadFrom];
                HasBeenOptimized[WriteTo] = HasBeenOptimized[ReadFrom];
                ExponentialFunction[WriteTo] = ExponentialFunction[ReadFrom];
                WriteTo++;
            }
            ReadFrom++;
        }
        NumberOfTokens = WriteTo - 1;
        return;
    }

    public void InsertTokenBefore(int InsertionPoint, String InsertionToken, byte InsertionProperty, byte InsertionType,
                                  boolean InsertionOptimized, boolean InsertionExponential) {
        int NewPos;
        for (int I = NumberOfTokens; I >= InsertionPoint; I--) {
            NewPos = I + 1;
            Token[NewPos] = new String(Token[I]);
            Properties[NewPos] = Properties[I];
            Type[NewPos] = Type[I];
            HasBeenOptimized[NewPos] = HasBeenOptimized[I];
            ExponentialFunction[NewPos] = ExponentialFunction[I];
        }
        NewPos = InsertionPoint;
        Token[NewPos] = new String(InsertionToken);
        Properties[NewPos] = InsertionProperty;
        Type[NewPos] = InsertionType;
        HasBeenOptimized[NewPos] = InsertionOptimized;
        ExponentialFunction[NewPos] = InsertionExponential;
        NumberOfTokens++;
        return;
    }

    public void InsertTokenAfter(int InsertionPoint, String InsertionToken, byte InsertionProperty, byte InsertionType,
                                 boolean InsertionOptimized, boolean InsertionExponential) {
        int NewPos;
        if (InsertionPoint < NumberOfTokens) {
            for (int I = NumberOfTokens; I > InsertionPoint; I--) {
                NewPos = I + 1;
                Token[NewPos] = new String(Token[I]);
                Properties[NewPos] = Properties[I];
                Type[NewPos] = Type[I];
                HasBeenOptimized[NewPos] = HasBeenOptimized[I];
                ExponentialFunction[NewPos] = ExponentialFunction[I];
            }
        }
        NewPos = InsertionPoint + 1;
        Token[NewPos] = new String(InsertionToken);
        Properties[NewPos] = InsertionProperty;
        Type[NewPos] = InsertionType;
        HasBeenOptimized[NewPos] = InsertionOptimized;
        ExponentialFunction[NewPos] = InsertionExponential;
        NumberOfTokens++;
        return;
    }

    public void ShiftTokens(int StartShiftAt, int SizeOfShift) {
        int NewPos = NumberOfTokens + SizeOfShift + 1;
        for (int I = NumberOfTokens; I >= StartShiftAt; I--) {
            NewPos--;
            Token[NewPos] = Token[I];
            Properties[NewPos] = Properties[I];
            Type[NewPos] = Type[I];
            HasBeenOptimized[NewPos] = HasBeenOptimized[I];
            ExponentialFunction[NewPos] = ExponentialFunction[I];
        }
        NumberOfTokens += SizeOfShift;
        return;
    }

    public void WriteTokenTo(int InsertionPoint, String InsertionToken, byte InsertionProperty, byte InsertionType,
                             boolean InsertionOptimized, boolean InsertionExponential) {
        Token[InsertionPoint] = new String(InsertionToken);
        Properties[InsertionPoint] = InsertionProperty;
        Type[InsertionPoint] = InsertionType;
        HasBeenOptimized[InsertionPoint] = InsertionOptimized;
        ExponentialFunction[InsertionPoint] = InsertionExponential;
        return;
    }

    public void DisplayTokens(String Message) {
        if (displayProgress) {
            diagnosticPrintln("Display Tokens:");
            diagnosticPrintln(
                    "I" + "\t" + "Token" + "\t" + "Prop" + "\t" + "Level" + "\t" + "Type" + "\t" + "2ndArgType" + "\t"
                            + "Opt'd" + "\t" + "Exp'l?");
            for (int I = 1; I <= NumberOfTokens; I++) {
                diagnosticPrintln(String.valueOf(I) + "\t" + Token[I] + "\t" + String.valueOf(Properties[I]) + "\t"
                        + String.valueOf(Level[I]) + "\t" + String.valueOf(Type[I]) + "\t"
                        + String.valueOf(BinaryOperatorSecondArgumentType[I]) + "\t" + "\t"
                        + String.valueOf(HasBeenOptimized[I]) + "\t" + String.valueOf(ExponentialFunction[I]));
            }
            for (int I = 1; I <= NumberOfTokens; I++) {
                diagnosticPrint(Token[I]);
            }
            diagnosticPrint("\n");
            diagnosticPrintln("Number of Tokens = " + String.valueOf(NumberOfTokens));
            diagnosticPrintln(Message);
        }
        return;
    }


    public void Initialize() {
//
// Names for the various type variables, for use in error messages.
//

        typeName = new String[26];

        typeName[0] = "remove";
        typeName[1] = "builtInName";
        typeName[2] = "comma";
        typeName[3] = "open round bracket";
        typeName[4] = "close round bracket";
        typeName[5] = "open square bracket";
        typeName[6] = "close square bracket";
        typeName[7] = "number";
        typeName[8] = "constant";
        typeName[9] = "sequence constant";
        typeName[10] = "character string constant";
        typeName[11] = "variable";
        typeName[12] = "numerical variable";
        typeName[13] = "sequence variable";
        typeName[14] = "unary operator";
        typeName[15] = "binary operator";
        typeName[16] = "function";
        typeName[17] = "function of one variable";
        typeName[18] = "function of two variables";
        typeName[19] = "function of three variables";
        typeName[20] = "function of a list of variables";
        typeName[21] = "assignment operator";
        typeName[22] = "assignment";
        typeName[23] = "increment operator";
        typeName[24] = "dummy variable";
        typeName[25] = "keyword for";


//
// Numbers of constants and functions that are recognized.
//
        NumberOfConstants = 52;
        NumberOfUnaryOperators = 1;
        NumberOfFunctionNames = 91;
        NumberOfFunctionsOne = 70;
        NumberOfFunctionsTwo = 27;
        NumberOfFunctionsThree = 21;
        NumberOfFunctionsList = 4;
//
// Store information about constants and functions that
// will be recognized.  Aliases that may be used in expressions
// are listed in ConstantAliases, while the unique name used
// internally is listed in ConstantNames.  The type of the constant
// (either SCALAR or VECTOR) is in ConstantType.  This name will be used
// as the key to the hashtables for constants such as ConstantValueDictionary
// and ConstantTypeDictionary.
//
        BuiltInConstantDictionary = new Hashtable(1000);
        FunctionDictionary = new Hashtable(1000);
        ConstantValueDictionary = new Hashtable(100);
        userVariableValueDictionary = new Hashtable(1000);
        temporaryVariableValueDictionary = new Hashtable(10);
        ConstantTypeDictionary = new Hashtable(100);
        userVariableTypeDictionary = new Hashtable(1000);
        FunctionOneTypeDictionary = new Hashtable(100);
        FunctionTwoTypeDictionary = new Hashtable(100);
        FunctionThreeTypeDictionary = new Hashtable(100);
        FunctionListTypeDictionary = new Hashtable(100);
        ConstantAliases = new String[200];
        ConstantNames = new String[200];
        ConstantTypes = new byte[200];
        UnaryNames = new String[10];
        UnaryValues = new String[10];
        FunctionAliases = new String[200];
        FunctionNames = new String[200];
        FunctionOneNames = new String[100];
        FunctionOneTypes = new byte[100];
        FunctionTwoNames = new String[50];
        FunctionTwoTypes = new byte[50];
        FunctionThreeNames = new String[30];
        FunctionThreeTypes = new byte[30];
        FunctionListNames = new String[10];
        FunctionListTypes = new byte[10];
//
//
        ConstantAliases[1] = "pi";
        ConstantNames[1] = "Pi";
        ConstantTypes[1] = SCALAR;
        ConstantAliases[2] = "e";
        ConstantNames[2] = "E";
        ConstantTypes[2] = SCALAR;
        ConstantAliases[3] = "gamma eulerconstant euler";
        ConstantNames[3] = "Gamma";
        ConstantTypes[3] = SCALAR;
        ConstantAliases[4] = "piby2 piover2 pibytwo piovertwo";
        ConstantNames[4] = "PiBy2";
        ConstantTypes[4] = SCALAR;
        ConstantAliases[5] = "twopi";
        ConstantNames[5] = "TwoPi";
        ConstantTypes[5] = SCALAR;
        ConstantAliases[6] = "c";
        ConstantNames[6] = "C";
        ConstantTypes[6] = SCALAR;
        ConstantAliases[7] = "qe q_e qelectron q_electron ";
        ConstantNames[7] = "Qe";
        ConstantTypes[7] = SCALAR;
        ConstantAliases[8] = "h hpl h_pl hplanck h_planck";
        ConstantNames[8] = "H";
        ConstantTypes[8] = SCALAR;
        ConstantAliases[9] = "hbar h_bar";
        ConstantNames[9] = "Hbar";
        ConstantTypes[9] = SCALAR;
        ConstantAliases[10] = "me m_e m_electron melectron";
        ConstantNames[10] = "Me";
        ConstantTypes[10] = SCALAR;
        ConstantAliases[11] = "mp m_p m_proton mproton";
        ConstantNames[11] = "Mp";
        ConstantTypes[11] = SCALAR;
        ConstantAliases[12] = "mn m_n m_neutron mneutron";
        ConstantNames[12] = "Mn";
        ConstantTypes[12] = SCALAR;
        ConstantAliases[13] = "kb k k_b k_boltzmann kboltzmann boltzmann";
        ConstantNames[13] = "K";
        ConstantTypes[13] = SCALAR;
        ConstantAliases[14] = "g gn g_n gnewton g_newton";
        ConstantNames[14] = "G";
        ConstantTypes[14] = SCALAR;
        ConstantAliases[15] = "alpha finestructure";
        ConstantNames[15] = "Alpha";
        ConstantTypes[15] = SCALAR;
        ConstantAliases[16] = "sigma stefan_boltzmann stefanboltzmann";
        ConstantNames[16] = "Sigma";
        ConstantTypes[16] = SCALAR;
        ConstantAliases[17] = "na n_a navogadro n_avogadro avogadro";
        ConstantNames[17] = "NA";
        ConstantTypes[17] = SCALAR;
        ConstantAliases[18] = "year yr";
        ConstantNames[18] = "Year";
        ConstantTypes[18] = SCALAR;
        ConstantAliases[19] = "msolar m_solar msun m_sun solarmass solar_mass";
        ConstantNames[19] = "MSolar";
        ConstantTypes[19] = SCALAR;
        ConstantAliases[20] = "au astronomicalunit astronomical_unit";
        ConstantNames[20] = "AU";
        ConstantTypes[20] = SCALAR;
        ConstantAliases[21] = "parsec pc";
        ConstantNames[21] = "Parsec";
        ConstantTypes[21] = SCALAR;
        ConstantAliases[22] = "epsilon0 epsilon_0 epsilonzero epsilonnought epsilon_zero epsilon_nought";
        ConstantNames[22] = "Epsilon0";
        ConstantTypes[22] = SCALAR;
        ConstantAliases[23] = "mu0 mu_0 muzero munought mu_zero mu_nought";
        ConstantNames[23] = "Mu0";
        ConstantTypes[23] = SCALAR;
        ConstantAliases[24] = "acresperm2 acrespersquaremetre acrespersquaremeter squaremetretoacre squaremetertoacre";
        ConstantNames[24] = "AcresPerM2";
        ConstantTypes[24] = SCALAR;
        ConstantAliases[25] = "btusperj btusperjoule jouletobtu";
        ConstantNames[25] = "BtusPerJ";
        ConstantTypes[25] = SCALAR;
        ConstantAliases[26]
                = "kcalperj kilocalperj kilocalorieperjoule kcalperjoule kcalsperj kilocalsperj kilocaloriesperjoule kcalsperjoule jtokcal jouletokcal jouletokilocalorie";
        ConstantNames[26] = "KCalPerJ";
        ConstantTypes[26] = SCALAR;
        ConstantAliases[27]
                = "picaperm picapermetre picapermeter picasperm picaspermetre picaspermeter metretopica metertopica mtopica";
        ConstantNames[27] = "PicaPerM";
        ConstantTypes[27] = SCALAR;
        ConstantAliases[28]
                = "pointperm pointpermeter pointpermetre pointsperm pointspermeter pointspermetre mtopoint metertopoint metretopoint";
        ConstantNames[28] = "PointPerM";
        ConstantTypes[28] = SCALAR;
        ConstantAliases[29]
                = "amuperkg amuperkilogram amusperkg amusperkilogram atomicmassunitsperkilogram kgtoamu kilogramtoamu kilogramtoatomicmassunit";
        ConstantNames[29] = "AmuPerKg";
        ConstantTypes[29] = SCALAR;
        ConstantAliases[30] = "lbperkg poundperkilogram poundperkilogramme kgtolb kilogramtopound kilogrammetopound";
        ConstantNames[30] = "LbPerKg";
        ConstantTypes[30] = SCALAR;
        ConstantAliases[31] = "hpperw horsepowerperwatt wtohp watttohorsepower";
        ConstantNames[31] = "HpPerW";
        ConstantTypes[31] = SCALAR;
        ConstantAliases[32] = "atmperpa atmospheresperpascal pascaltoatmospheres pascaltoatmosphere patoatm";
        ConstantNames[32] = "AtmPerPa";
        ConstantTypes[32] = SCALAR;
        ConstantAliases[33]
                = "flozusperm3 usflozperm3 usozperm3 fluidounceuspercubicmeter fluidounceuspercubicmetre usfluidouncepercubicmeter usfluidouncepercubicmetre usouncepercubicmeter usouncepercubicmetre fluidouncesuspercubicmeter fluidouncesuspercubicmetre usfluidouncespercubicmeter usfluidouncespercubicmetre usouncespercubicmeter usouncespercubicmetre cubicmetertousfluidounce cubicmetretousfluidounce cubicmetertousounce cubicmetretousounce fluidounceuspermetercubed fluidounceuspermetrecubed usfluidouncepermetercubed usfluidouncepermetrecubed usouncepermetercubed usouncepermetrecubed fluidouncesuspermetercubed fluidouncesuspermetrecubed usfluidouncespermetercubed usfluidouncespermetrecubed usouncespermetercubed usouncespermetrecubed metercubedtousfluidounce metrecubedtousfluidounce metercubedtousounce metrecubedtousounce fluidounceuspermeterscubed fluidounceuspermetrescubed usfluidouncepermeterscubed usfluidouncepermetrescubed usouncepermeterscubed usouncepermetrescubed fluidouncesuspermeterscubed fluidouncesuspermetrescubed usfluidouncespermeterscubed usfluidouncespermetrescubed usouncespermeterscubed usouncespermetrescubed meterscubedtousfluidounce metrescubedtousfluidounce meterscubedtousounce metrescubedtousounce";
        ConstantNames[33] = "FlOzUSPerM3";
        ConstantTypes[33] = SCALAR;
        ConstantAliases[34]
                = "flozukperm3 ukflozperm3 ukozperm3 fluidounceukpercubicmeter fluidounceukpercubicmetre ukfluidouncepercubicmeter ukfluidouncepercubicmetre ukouncepercubicmeter ukouncepercubicmetre fluidouncesukpercubicmeter fluidouncesukpercubicmetre ukfluidouncespercubicmeter ukfluidouncespercubicmetre ukouncespercubicmeter ukouncespercubicmetre cubicmetertoukfluidounce cubicmetretoukfluidounce cubicmetertoukounce cubicmetretoukounce fluidounceukpermetercubed fluidounceukpermetrecubed ukfluidouncepermetercubed ukfluidouncepermetrecubed ukouncepermetercubed ukouncepermetrecubed fluidouncesukpermetercubed fluidouncesukpermetrecubed ukfluidouncespermetercubed ukfluidouncespermetrecubed ukouncespermetercubed ukouncespermetrecubed metercubedtoukfluidounce metrecubedtoukfluidounce metercubedtoukounce metrecubedtoukounce fluidounceukpermeterscubed fluidounceukpermetrescubed ukfluidouncepermeterscubed ukfluidouncepermetrescubed ukouncepermeterscubed ukouncepermetrescubed fluidouncesukpermeterscubed fluidouncesukpermetrescubed ukfluidouncespermeterscubed ukfluidouncespermetrescubed ukouncespermeterscubed ukouncespermetrescubed meterscubedtoukfluidounce metrescubedtoukfluidounce meterscubedtoukounce metrescubedtoukounce";
        ConstantNames[34] = "FlOzUKPerM3";
        ConstantTypes[34] = SCALAR;
        ConstantAliases[35]
                = "evperj evperjoule electronvoltperjoule electronvoltsperjoule jtoev jouletoev jouletoelectronvolt joulestoelectronvolts";
        ConstantNames[35] = "eVPerJ";
        ConstantTypes[35] = SCALAR;
        ConstantAliases[36]
                = "ftperm feetpermeter feetpermetre footpermeter footpermetre mtoft meterstofeet metrestofeet metertofoot metretofoot metertofeet metretofeet meterstofoot metrestofoot";
        ConstantNames[36] = "FtPerM";
        ConstantTypes[36] = SCALAR;
        ConstantAliases[37]
                = "degperrad degreeperradian degreesperradian radtodeg radianstodegrees radiantodegree radiantodegrees radianstodegree";
        ConstantNames[37] = "DegPerRad";
        ConstantTypes[37] = SCALAR;
        ConstantAliases[38] = "gradperrad gradperradian gradsperradian radtograd radianstograd radiantograd";
        ConstantNames[38] = "GradPerRad";
        ConstantTypes[38] = SCALAR;
        ConstantAliases[39] = "rydberg ryd";
        ConstantNames[39] = "Rydberg";
        ConstantTypes[39] = SCALAR;
        ConstantAliases[40] = "rmolar r_molar molargas molar_gas molargasconstant molar_gas_constant";
        ConstantNames[40] = "RMolar";
        ConstantTypes[40] = SCALAR;
        ConstantAliases[41] = "day";
        ConstantNames[41] = "Day";
        ConstantTypes[41] = SCALAR;
        ConstantAliases[42] = "daysidereal day_sidereal siderealday sidereal_day";
        ConstantNames[42] = "DaySidereal";
        ConstantTypes[42] = SCALAR;
        ConstantAliases[43] = "hour hr";
        ConstantNames[43] = "Hour";
        ConstantTypes[43] = SCALAR;
        ConstantAliases[44] = "hoursidereal hour_sidereal siderealhour sidereal_hour";
        ConstantNames[44] = "HourSidereal";
        ConstantTypes[44] = SCALAR;
        ConstantAliases[45]
                = "torrperpa torr_per_pa torrperpascal torr_per_pascal patotorr pa_to_torr pascaltotorr pascal_to_torr";
        ConstantNames[45] = "TorrPerPa";
        ConstantTypes[45] = SCALAR;
        ConstantAliases[46] = "rsolar r_solar solarradius solar_radius";
        ConstantNames[46] = "RSolar";
        ConstantTypes[46] = SCALAR;
        ConstantAliases[47] = "lsolar l_solar solarluminosity solar_luminosity";
        ConstantNames[47] = "LSolar";
        ConstantTypes[47] = SCALAR;
        ConstantAliases[48] = "mearth m_earth earthmass earth_mass";
        ConstantNames[48] = "MEarth";
        ConstantTypes[48] = SCALAR;
        ConstantAliases[49] = "rearth r_earth earthradius earth_radius";
        ConstantNames[49] = "REarth";
        ConstantTypes[49] = SCALAR;
        ConstantAliases[50] = "hubble100 h100 hubblestandard hubble_standard";
        ConstantNames[50] = "Hubble100";
        ConstantTypes[50] = SCALAR;
        ConstantAliases[51] = "cubit egyptiancubit egyptian_cubit";
        ConstantNames[51] = "Cubit";
        ConstantTypes[51] = SCALAR;
        ConstantAliases[52] = "jansky";
        ConstantNames[52] = "Jansky";
        ConstantTypes[52] = SCALAR;
//
// Now store the names into a hashtable whose keys are the aliases
//
        for (int I = 1; I <= NumberOfConstants; I++) {
            Aliases = new StringTokenizer(ConstantAliases[I]);
            while (Aliases.hasMoreTokens()) {
                BuiltInConstantDictionary.put(Aliases.nextToken(), ConstantNames[I]);
            }
        }

//
// Now store the types into a hashtable whose keys are the names
//
        for (int I = 1; I <= NumberOfConstants; I++) {
            ConstantTypeDictionary.put(ConstantNames[I], new Byte(ConstantTypes[I]));
        }

//
// Now put values of constants into a hashtable whose keys are the names
//
        InsertScalarIntoConstantValueDictionary("Pi", Math.PI);
        InsertScalarIntoConstantValueDictionary("E", Math.E);
        InsertScalarIntoConstantValueDictionary("Gamma", 0.5772156649015);
        InsertScalarIntoConstantValueDictionary("PiBy2", 1.5707963267949);
        InsertScalarIntoConstantValueDictionary("TwoPi", Math.PI * 2);
        InsertScalarIntoConstantValueDictionary("C", 299792458);
        InsertScalarIntoConstantValueDictionary("Qe", 1.6021892E-19);
        InsertScalarIntoConstantValueDictionary("H", 6.626176E-34);
        InsertScalarIntoConstantValueDictionary("Hbar", 1.0545887E-34);
        InsertScalarIntoConstantValueDictionary("Me", 9.109534E-31);
        InsertScalarIntoConstantValueDictionary("Mp", 1.6726485E-27);
        InsertScalarIntoConstantValueDictionary("Mn", 1.6749543E-27);
        InsertScalarIntoConstantValueDictionary("K", 1.380662E-23);
        InsertScalarIntoConstantValueDictionary("G", 6.672E-11);
        InsertScalarIntoConstantValueDictionary("Alpha", 0.007297350390452);
        InsertScalarIntoConstantValueDictionary("Sigma", 0.0000000567032);
        InsertScalarIntoConstantValueDictionary("NA", 6.0220450000000E+23);
        InsertScalarIntoConstantValueDictionary("Year", 31558000);
        InsertScalarIntoConstantValueDictionary("MSolar", 1.9890000000000E+30);
        InsertScalarIntoConstantValueDictionary("AU", 1.4959787E11);
        InsertScalarIntoConstantValueDictionary("Parsec", 3.0856780000000E+16);
        InsertScalarIntoConstantValueDictionary("eVPerJ", 6.2414847E18);
        InsertScalarIntoConstantValueDictionary("FtPerM", 3.2808);
        InsertScalarIntoConstantValueDictionary("AcresPerM2", 2.4716E-4);
        InsertScalarIntoConstantValueDictionary("BtusPerJ", 9.4841E-4);
        InsertScalarIntoConstantValueDictionary("KCalPerJ", 2.0773E-4);
        InsertScalarIntoConstantValueDictionary("PicaPerM", 237.1);
        InsertScalarIntoConstantValueDictionary("PointPerM", 2847.7);
        InsertScalarIntoConstantValueDictionary("AmuPerKg", 6.02203E26);
        InsertScalarIntoConstantValueDictionary("LbPerKg", 2.2046);
        InsertScalarIntoConstantValueDictionary("HpPerW", 1.341E-3);
        InsertScalarIntoConstantValueDictionary("AtmPerPa", 9.869232E-6);
        InsertScalarIntoConstantValueDictionary("FlOzUSPerM3", 33813.48);
        InsertScalarIntoConstantValueDictionary("FlOzUKPerM3", 35195.16);
        InsertScalarIntoConstantValueDictionary("Epsilon0", 8.8542E-12);
        InsertScalarIntoConstantValueDictionary("Mu0", 1.0151000000000E+28);
        InsertScalarIntoConstantValueDictionary("DegPerRad", 180 / Math.PI);
        InsertScalarIntoConstantValueDictionary("GradPerRad", 50 / Math.PI);
        InsertScalarIntoConstantValueDictionary("Rydberg", 1.097373E7);
        InsertScalarIntoConstantValueDictionary("RMolar", 8.31441);
        InsertScalarIntoConstantValueDictionary("Day", 86400);
        InsertScalarIntoConstantValueDictionary("DaySidereal", 86164);
        InsertScalarIntoConstantValueDictionary("Hour", 3600);
        InsertScalarIntoConstantValueDictionary("HourSidereal", 3590.2);
        InsertScalarIntoConstantValueDictionary("TorrPerPa", 0.00750075);
        InsertScalarIntoConstantValueDictionary("RSolar", 6.9599E8);
        InsertScalarIntoConstantValueDictionary("MEarth", 5.976E24);
        InsertScalarIntoConstantValueDictionary("REarth", 6.378164E6);
        InsertScalarIntoConstantValueDictionary("LSolar", 3.826E26);
        InsertScalarIntoConstantValueDictionary("Hubble100", 3.240779E-18);
        InsertScalarIntoConstantValueDictionary("Cubit", 0.523);
        InsertScalarIntoConstantValueDictionary("Jansky", 1e-26);
//
// Next define unary operators.
//
        UnaryNames[1] = "-";
        UnaryValues[1] = "-";
//
// Now go on to do for functions what we did for constants.  The function names go into the
// FunctionDictionary hashtable, one entry per name regardless of how many overloaded
// methods (with different numbers of arguments) there will be for this name.
//
        FunctionAliases[1] = "negate - negative changesign change_sign chs";
        FunctionNames[1] = "Negate";
        FunctionAliases[2] = "abs absolutevalue absolute_value";
        FunctionNames[2] = "Abs";
        FunctionAliases[3] = "acos arccos invcos inversecos cosinv cosinverse";
        FunctionNames[3] = "Acos";
        FunctionAliases[4] = "if condition conditional";
        FunctionNames[4] = "If";
        FunctionAliases[5] = "asin arcsin invsin inversesin sininv sininverse";
        FunctionNames[5] = "Asin";
        FunctionAliases[6] = "grads converttograds convert_to_grads";
        FunctionNames[6] = "Grads";
        FunctionAliases[7] = "atan arctan invtan inversetan taninv taninverse";
        FunctionNames[7] = "Atan";
        FunctionAliases[8] = "scalarproduct innerproduct dotproduct scalar_product inner_product dot_product";
        FunctionNames[8] = "ScalarProduct";
        FunctionAliases[9] = "ceil ceiling roundup round_up";
        FunctionNames[9] = "Ceil";
        FunctionAliases[10] = "cos";
        FunctionNames[10] = "Cos";
        FunctionAliases[11] = "cosh";
        FunctionNames[11] = "Cosh";
        FunctionAliases[12] = "exp";
        FunctionNames[12] = "Exp";
        FunctionAliases[13] = "floor rounddown round_down";
        FunctionNames[13] = "Floor";
        FunctionAliases[14] = "log ln loge log_e naturallogarithm natural_logarithm";
        FunctionNames[14] = "Log";
        FunctionAliases[15] = "convolve convolution";
        FunctionNames[15] = "Convolve";
        FunctionAliases[16] = "log10 logten logbase10 log_base_10 decimallogarithm decimal_logarithm";
        FunctionNames[16] = "Log10";
        FunctionAliases[17] = "round rint round_nearest roundnearest roundoff round_off";
        FunctionNames[17] = "Round";
        FunctionAliases[18] = "shift cyclicshift cyclic_shift cycle";
        FunctionNames[18] = "Shift";
        FunctionAliases[19] = "sin";
        FunctionNames[19] = "Sin";
        FunctionAliases[20] = "sinh";
        FunctionNames[20] = "Sinh";
        FunctionAliases[21] = "sqrt sqr squareroot square_root";
        FunctionNames[21] = "Sqrt";
        FunctionAliases[22] = "pow power exponentiation exponent";
        FunctionNames[22] = "Pow";
        FunctionAliases[23] = "tan";
        FunctionNames[23] = "Tan";
        FunctionAliases[24] = "tanh";
        FunctionNames[24] = "Tanh";
        FunctionAliases[25] = "reverse invertorder invert_order reflect";
        FunctionNames[25] = "Reverse";
        FunctionAliases[26] = "sign sgn";
        FunctionNames[26] = "Sign";
        FunctionAliases[27] = "sum summation";
        FunctionNames[27] = "Sum";
        FunctionAliases[28] = "count length sequence_length";
        FunctionNames[28] = "Count";
        FunctionAliases[29] = "sumofsquares sum_of_squares";
        FunctionNames[29] = "SumOfSquares";
        FunctionAliases[30] = "atanh arctanh invtanh tanhinv tanhinverse inversetanh";
        FunctionNames[30] = "Atanh";
        FunctionAliases[31] = "mod modulo positiveremainder positive_remainder posmod posmodulo";
        FunctionNames[31] = "Mod";
        FunctionAliases[32] = "asinh arcsinh invsinh sinhinv sinhinverse inversesinh";
        FunctionNames[32] = "Asinh";
        FunctionAliases[33] = "atan2 arctan2 phase";
        FunctionNames[33] = "Atan2";
        FunctionAliases[34] = "acosh arccosh invcosh coshinv coshinverse inversecosh";
        FunctionNames[34] = "Acosh";
        FunctionAliases[35] = "sequence sequencegenerator sequence_generator seq seqgen seq_gen";
        FunctionNames[35] = "Sequence";
        FunctionAliases[36] = "degrees converttodegrees convert_to_degrees ";
        FunctionNames[36] = "Degrees";
        FunctionAliases[37] = "radians converttoradians convert_to_radians ";
        FunctionNames[37] = "Radians";
        FunctionAliases[38]
                = "ftoc f_to_c fahrenheittocentigrade fahrenheittocelsius centigradefromfahrenheit celsiusfromfahrenheit fahrenheit_to_centigrade fahrenheit_to_celsius centigrade_from_fahrenheit celsius_from_fahrenheit ";
        FunctionNames[38] = "FToC";
        FunctionAliases[39]
                = "ftok c_to_k fahrenheittokelvin fahrenheittoabsolute kelvinfromfahrenheit absolutefromfahrenheit fahrenheit_to_kelvin fahrenheit_to_absolute kelvin_from_fahrenheit absolute_from_fahrenheit ";
        FunctionNames[39] = "FToK";
        FunctionAliases[40]
                = "ctof c_to_f centigradetofahrenheit celsiustofahrenheit fahrenheitfromcelsius fahrenheitfromcentigrade centigrade_to_fahrenheit celsius_to_fahrenheit fahrenheit_from_celsius fahrenheit_from_centigrade ";
        FunctionNames[40] = "CToF";
        FunctionAliases[41]
                = "ctok c_to_k centigradetokelvin celsiustokelvin kelvinfromcentigrade kelvinfromcelsius centigradetoabsolute celsiustoabsolute absolutefromcentigrade absolutefromcelsius centigrade_to_kelvin celsius_to_kelvin kelvin_from_centigrade kelvin_from_celsius centigrade_to_absolute celsius_to_absolute absolute_from_centigrade absolute_from_celsius";
        FunctionNames[41] = "CToK";
        FunctionAliases[42]
                = "ktof k_to_f kelvintofahrenheit absolutetofahrenheit fahrenheitfromabsolute fahrenheitfromkelvin kelvin_to_fahrenheit absolute_to_fahrenheit fahrenheit_from_absolute fahrenheit_from_kelvin ";
        FunctionNames[42] = "KToF";
        FunctionAliases[43]
                = "ktoc k_to_c kelvintocentigrade absolutetocentigrade centigradefromabsolute centigradefromkelvin kelvintocelsius absolutetocelsius celsiusfromabsolute celsiusfromkelvin kelvin_to_centigrade absolute_to_centigrade centigrade_from_absolute centigrade_from_kelvin kelvin_to_celsius absolute_to_celsius celsius_from_absolute celsius_from_kelvin ";
        FunctionNames[43] = "KToC";
        FunctionAliases[44] = "product";
        FunctionNames[44] = "Product";
        FunctionAliases[45] = "max maximum sup supremum";
        FunctionNames[45] = "Max";
        FunctionAliases[46] = "min minimum inf infemum";
        FunctionNames[46] = "Min";
        FunctionAliases[47] = "uniformrandom random randomuniform uniform_random random_uniform";
        FunctionNames[47] = "UniformRandom";
        FunctionAliases[48]
                = "gaussianrandom gaussian gaussian_random randomgaussian random_gaussian normalrandom normal normal_random randomnormal random_normal ";
        FunctionNames[48] = "GaussianRandom";
        FunctionAliases[49] = "frac fraction fractionalpart fractional_part";
        FunctionNames[49] = "Frac";
        FunctionAliases[50] = "int integer integerpart integer_part aint";
        FunctionNames[50] = "Int";
        FunctionAliases[51] = "log2 logtwo logbase2 log_base_2 binarylogarithm binary_logarithm";
        FunctionNames[51] = "Log2";
        FunctionAliases[52]
                = "astroangle anglefromhourangle hourangletoangle angle_from_hour_angle hour_angle_to_angle ";
        FunctionNames[52] = "AstroAngle";
        FunctionAliases[53] = "accumulate integrate accum integ";
        FunctionNames[53] = "Accumulate";
        FunctionAliases[54] = "use give_sequence define_sequence compose_sequence concatenate";
        FunctionNames[54] = "Use";
        FunctionAliases[55] = "sort";
        FunctionNames[55] = "Sort";
        FunctionAliases[56] = "randomize randomise sort_random random_sort rearrange_at_random";
        FunctionNames[56] = "Randomize";
        FunctionAliases[57] = "average mean avg";
        FunctionNames[57] = "Average";
        FunctionAliases[58] = "geometric_mean geometricmean geo_mean geomean";
        FunctionNames[58] = "GeometricMean";
        FunctionAliases[59] = "factorial fact fac";
        FunctionNames[59] = "Factorial";
        FunctionAliases[60] = "combinations comb combs binomial binom";
        FunctionNames[60] = "Combinations";
        FunctionAliases[61] = "dms degminsec deg_min_sec d.m.s. deg.min.sec";
        FunctionNames[61] = "DMS";
        FunctionAliases[62] = "astrohms astro_hms astro_hour_min_sec right_ascension ra r_a";
        FunctionNames[62] = "AstroHMS";
        FunctionAliases[63] = "hms hour_min_sec h.m.s hour.min.sec";
        FunctionNames[63] = "HMS";
        FunctionAliases[64] = "subsequence subset subseq";
        FunctionNames[64] = "Subsequence";
        FunctionAliases[65] = "select nonzero";
        FunctionNames[65] = "Select";
        FunctionAliases[66] = "extract choose";
        FunctionNames[66] = "Extract";
        FunctionAliases[67] = "variance var centralmoment2 cm2 second_central_moment secondcentralmoment";
        FunctionNames[67] = "Variance";
        FunctionAliases[68]
                = "samplevariance sample_variance s_variance svariance s_var svar samplecentralmoment2 sample_central_moment_2 scm2 sample_second_central_moment samplesecondcentralmoment";
        FunctionNames[68] = "SampleVariance";
        FunctionAliases[69] = "diff differentiate differences derivative";
        FunctionNames[69] = "Diff";
        FunctionAliases[70] = "regression regress linear_regression fit_to_line least_squares_line";
        FunctionNames[70] = "Regression";
        FunctionAliases[71]
                = "elementat element_at extractone extract_one selectone select_one chooseone choose_one selectat select_at chooseat choose_at";
        FunctionNames[71] = "ElementAt";
        FunctionAliases[72] = "centralmoment3 central_moment_3 cm3 third_central_moment thirdcentralmoment";
        FunctionNames[72] = "CentralMoment3";
        FunctionAliases[73] = "skewness";
        FunctionNames[73] = "Skewness";
        FunctionAliases[74]
                = "samplecentralmoment3 sample_central_moment_3 scm3 sample_third_central_moment samplethirdcentralmoment";
        FunctionNames[74] = "SampleCentralMoment3";
        FunctionAliases[75]
                = "samplecentralmoment4 sample_central_moment_4 scm4 sample_fourth_central_moment samplefourthcentralmoment";
        FunctionNames[75] = "SampleCentralMoment4";
        FunctionAliases[76] = "kurtosis";
        FunctionNames[76] = "Kurtosis";
        FunctionAliases[77] = "centralmoment4 central_moment_4 cm4 fourth_central_moment fourthcentralmoment";
        FunctionNames[77] = "CentralMoment4";
        FunctionAliases[78] = "correlate correlation";
        FunctionNames[78] = "Correlate";
        FunctionAliases[79] = "covariance covar";
        FunctionNames[79] = "Covariance";
        FunctionAliases[80] = "samplecovariance sample_covariance s_covariance svariance s_covar scovar";
        FunctionNames[80] = "SampleCovariance";
        FunctionAliases[81] = "index indexof firstnonzero first_nonzero";
        FunctionNames[81] = "Index";
        FunctionAliases[82] = "absmag absolutemagnitude absolute_magnitude";
        FunctionNames[82] = "AbsMag";
        FunctionAliases[83] = "luminosity abslum absoluteluminosity absolute_luminosity";
        FunctionNames[83] = "Luminosity";
        FunctionAliases[84] = "appmag apparentmagnitude apparent_magnitude";
        FunctionNames[84] = "AppMag";
        FunctionAliases[85] = "brightness flux applum apparentluminosity apparent_luminosity";
        FunctionNames[85] = "Brightness";
        FunctionAliases[86]
                = "weekday dayofweek day_of_week weekdaygregorian weekday_gregorian dayofweekgregorian day_of_week_gregorian";
        FunctionNames[86] = "Weekday";
        FunctionAliases[87] = "solvequadratic solve_quadratic quadratic quad";
        FunctionNames[87] = "SolveQuadratic";
        FunctionAliases[88] = "solvecubic solve_cubic cubic";
        FunctionNames[88] = "SolveCubic";
        FunctionAliases[89] = "weekdayjulian weekday_julian dayofweekjulian day_of_week_julian";
        FunctionNames[89] = "WeekdayJulian";
        FunctionAliases[90]
                = "ieeeremainder iremainder imod imodulo ieeemod ieeemodulo smallestremainder smallest_remainder smallmod smallmodulo";
        FunctionNames[90] = "IEEERemainder";
        FunctionAliases[91] = "control";
        FunctionNames[91] = "Control";

//
// Now store these into a hashtable whose keys are the aliases
//
        for (int I = 1; I <= NumberOfFunctionNames; I++) {
            Aliases = new StringTokenizer(FunctionAliases[I]);
            while (Aliases.hasMoreTokens()) {
                FunctionDictionary.put(Aliases.nextToken(), FunctionNames[I]);
            }
        }


//
// Next define how many arguments each function has (names can be used for more than
// one set of arguments) and what the return type is for that set of arguments.
//
        FunctionOneNames[1] = "Negate";
        FunctionOneTypes[1] = AnyToSame;
        FunctionOneNames[2] = "Abs";
        FunctionOneTypes[2] = AnyToSame;
        FunctionOneNames[3] = "Acos";
        FunctionOneTypes[3] = AnyToSame;
        FunctionOneNames[4] = "Grads";
        FunctionOneTypes[4] = AnyToSame;
        FunctionOneNames[5] = "Asin";
        FunctionOneTypes[5] = AnyToSame;
        FunctionOneNames[6] = "Asin";
        FunctionOneTypes[6] = AnyToSame;
        FunctionOneNames[7] = "Atan";
        FunctionOneTypes[7] = AnyToSame;
        FunctionOneNames[8] = "Atan";
        FunctionOneTypes[8] = AnyToSame;
        FunctionOneNames[9] = "Ceil";
        FunctionOneTypes[9] = AnyToSame;
        FunctionOneNames[10] = "Cos";
        FunctionOneTypes[10] = AnyToSame;
        FunctionOneNames[11] = "Cosh";
        FunctionOneTypes[11] = AnyToSame;
        FunctionOneNames[12] = "Exp";
        FunctionOneTypes[12] = AnyToSame;
        FunctionOneNames[13] = "Floor";
        FunctionOneTypes[13] = AnyToSame;
        FunctionOneNames[14] = "Log";
        FunctionOneTypes[14] = AnyToSame;
        FunctionOneNames[15] = "SampleCentralMoment4";
        FunctionOneTypes[15] = VectorToScalar;
        FunctionOneNames[16] = "Log10";
        FunctionOneTypes[16] = AnyToSame;
        FunctionOneNames[17] = "SampleCentralMoment3";
        FunctionOneTypes[17] = VectorToScalar;
        FunctionOneNames[18] = "Round";
        FunctionOneTypes[18] = AnyToSame;
        FunctionOneNames[19] = "Sin";
        FunctionOneTypes[19] = AnyToSame;
        FunctionOneNames[20] = "Sinh";
        FunctionOneTypes[20] = AnyToSame;
        FunctionOneNames[21] = "Sqrt";
        FunctionOneTypes[21] = AnyToSame;
        FunctionOneNames[22] = "Accumulate";
        FunctionOneTypes[22] = VectorToVector;
        FunctionOneNames[23] = "Tan";
        FunctionOneTypes[23] = AnyToSame;
        FunctionOneNames[24] = "Tanh";
        FunctionOneTypes[24] = AnyToSame;
        FunctionOneNames[25] = "Reverse";
        FunctionOneTypes[25] = VectorToVector;
        FunctionOneNames[26] = "Sign";
        FunctionOneTypes[26] = AnyToSame;
        FunctionOneNames[27] = "Sum";
        FunctionOneTypes[27] = VectorToScalar;
        FunctionOneNames[28] = "Count";
        FunctionOneTypes[28] = VectorToScalar;
        FunctionOneNames[29] = "SumOfSquares";
        FunctionOneTypes[29] = VectorToScalar;
        FunctionOneNames[30] = "Atanh";
        FunctionOneTypes[30] = AnyToSame;
        FunctionOneNames[31] = "Atanh";
        FunctionOneTypes[31] = AnyToSame;
        FunctionOneNames[32] = "Asinh";
        FunctionOneTypes[32] = AnyToSame;
        FunctionOneNames[33] = "Asinh";
        FunctionOneTypes[33] = AnyToSame;
        FunctionOneNames[34] = "Acosh";
        FunctionOneTypes[34] = AnyToSame;
        FunctionOneNames[35] = "Acosh";
        FunctionOneTypes[35] = AnyToSame;
        FunctionOneNames[36] = "Degrees";
        FunctionOneTypes[36] = AnyToSame;
        FunctionOneNames[37] = "Radians";
        FunctionOneTypes[37] = AnyToSame;
        FunctionOneNames[38] = "FToC";
        FunctionOneTypes[38] = AnyToSame;
        FunctionOneNames[39] = "FToK";
        FunctionOneTypes[39] = AnyToSame;
        FunctionOneNames[40] = "CToF";
        FunctionOneTypes[40] = AnyToSame;
        FunctionOneNames[41] = "CToK";
        FunctionOneTypes[41] = AnyToSame;
        FunctionOneNames[42] = "KToF";
        FunctionOneTypes[42] = AnyToSame;
        FunctionOneNames[43] = "KToC";
        FunctionOneTypes[43] = AnyToSame;
        FunctionOneNames[44] = "Product";
        FunctionOneTypes[44] = VectorToScalar;
        FunctionOneNames[45] = "Max";
        FunctionOneTypes[45] = VectorToScalar;
        FunctionOneNames[46] = "Min";
        FunctionOneTypes[46] = VectorToScalar;
        FunctionOneNames[47] = "UniformRandom";
        FunctionOneTypes[47] = AnyToVector;
        FunctionOneNames[48] = "GaussianRandom";
        FunctionOneTypes[48] = AnyToVector;
        FunctionOneNames[49] = "Sequence";
        FunctionOneTypes[49] = AnyToVector;
        FunctionOneNames[50] = "Frac";
        FunctionOneTypes[50] = AnyToSame;
        FunctionOneNames[51] = "Int";
        FunctionOneTypes[51] = AnyToSame;
        FunctionOneNames[52] = "Log2";
        FunctionOneTypes[52] = AnyToSame;
        FunctionOneNames[53] = "Sort";
        FunctionOneTypes[53] = VectorToVector;
        FunctionOneNames[54] = "Randomize";
        FunctionOneTypes[54] = VectorToVector;
        FunctionOneNames[55] = "Average";
        FunctionOneTypes[55] = VectorToScalar;
        FunctionOneNames[56] = "GeometricMean";
        FunctionOneTypes[56] = VectorToScalar;
        FunctionOneNames[57] = "Factorial";
        FunctionOneTypes[57] = AnyToSame;
        FunctionOneNames[58] = "DMS";
        FunctionOneTypes[58] = ScalarToVector;
        FunctionOneNames[59] = "AstroHMS";
        FunctionOneTypes[59] = ScalarToVector;
        FunctionOneNames[60] = "HMS";
        FunctionOneTypes[60] = ScalarToVector;
        FunctionOneNames[61] = "Select";
        FunctionOneTypes[61] = VectorToVector;
        FunctionOneNames[62] = "SampleVariance";
        FunctionOneTypes[62] = VectorToScalar;
        FunctionOneNames[63] = "Diff";
        FunctionOneTypes[63] = VectorToVector;
        FunctionOneNames[64] = "Regression";
        FunctionOneTypes[64] = VectorToVector;
        FunctionOneNames[65] = "Grad";
        FunctionOneTypes[65] = AnyToSame;
        FunctionOneNames[66] = "Index";
        FunctionOneTypes[66] = AnyToScalar;
        FunctionOneNames[67] = "AbsMag";
        FunctionOneTypes[67] = AnyToSame;
        FunctionOneNames[68] = "Luminosity";
        FunctionOneTypes[68] = AnyToSame;
        FunctionOneNames[69] = "AppMag";
        FunctionOneTypes[69] = AnyToSame;
        FunctionOneNames[70] = "Brightness";
        FunctionOneTypes[70] = AnyToSame;
        FunctionTwoNames[1] = "Mod";
        FunctionTwoTypes[1] = AnyToSame;
        FunctionTwoNames[2] = "Atan2";
        FunctionTwoTypes[2] = AnyToSame;
        FunctionTwoNames[3] = "Atan2";
        FunctionTwoTypes[3] = AnyToSame;
        FunctionTwoNames[4] = "Max";
        FunctionTwoTypes[4] = AnyToSame;
        FunctionTwoNames[5] = "Min";
        FunctionTwoTypes[5] = AnyToSame;
        FunctionTwoNames[6] = "Pow";
        FunctionTwoTypes[6] = AnyToSame;
        FunctionTwoNames[7] = "Sequence";
        FunctionTwoTypes[7] = AnyToVector;
        FunctionTwoNames[8] = "ScalarProduct";
        FunctionTwoTypes[8] = VectorToScalar;
        FunctionTwoNames[9] = "Convolve";
        FunctionTwoTypes[9] = VectorToVector;
        FunctionTwoNames[10] = "Shift";
        FunctionTwoTypes[10] = VectorToVector;
        FunctionTwoNames[11] = "UniformRandom";
        FunctionTwoTypes[11] = AnyToVector;
        FunctionTwoNames[12] = "GaussianRandom";
        FunctionTwoTypes[12] = AnyToVector;
        FunctionTwoNames[13] = "Sort";
        FunctionTwoTypes[13] = AnyToVector;
        FunctionTwoNames[14] = "Randomize";
        FunctionTwoTypes[14] = AnyToVector;
        FunctionTwoNames[15] = "Combinations";
        FunctionTwoTypes[15] = AnyToSame;
        FunctionTwoNames[16] = "Subsequence";
        FunctionTwoTypes[16] = AnyToVector;
        FunctionTwoNames[17] = "Select";
        FunctionTwoTypes[17] = AnyToVector;
        FunctionTwoNames[18] = "Extract";
        FunctionTwoTypes[18] = VectorToVector;
        FunctionTwoNames[19] = "Variance";
        FunctionTwoTypes[19] = AnyToScalar;
        FunctionTwoNames[20] = "Regression";
        FunctionTwoTypes[20] = VectorToVector;
        FunctionTwoNames[21] = "ElementAt";
        FunctionTwoTypes[21] = VectorToScalar;
        FunctionTwoNames[22] = "CentralMoment3";
        FunctionTwoTypes[22] = VectorToScalar;
        FunctionTwoNames[23] = "CentralMoment4";
        FunctionTwoTypes[23] = VectorToScalar;
        FunctionTwoNames[24] = "Correlate";
        FunctionTwoTypes[24] = VectorToVector;
        FunctionTwoNames[25] = "SampleCovariance";
        FunctionTwoTypes[25] = VectorToScalar;
        FunctionTwoNames[26] = "Index";
        FunctionTwoTypes[26] = AnyToScalar;
        FunctionTwoNames[27] = "IEEERemainder";
        FunctionTwoTypes[27] = AnyToSame;
        FunctionThreeNames[1] = "If";
        FunctionThreeTypes[1] = AnyToSame;
        FunctionThreeNames[2] = "Radians";
        FunctionThreeTypes[2] = AnyToSame;
        FunctionThreeNames[3] = "Max";
        FunctionThreeTypes[3] = AnyToSame;
        FunctionThreeNames[4] = "Min";
        FunctionThreeTypes[4] = AnyToSame;
        FunctionThreeNames[5] = "UniformRandom";
        FunctionThreeTypes[5] = AnyToVector;
        FunctionThreeNames[6] = "GaussianRandom";
        FunctionThreeTypes[6] = AnyToVector;
        FunctionThreeNames[7] = "Sequence";
        FunctionThreeTypes[7] = AnyToVector;
        FunctionThreeNames[8] = "AstroAngle";
        FunctionThreeTypes[8] = AnyToSame;
        FunctionThreeNames[9] = "Subsequence";
        FunctionThreeTypes[9] = AnyToVector;
        FunctionThreeNames[10] = "Select";
        FunctionThreeTypes[10] = AnyToVector;
        FunctionThreeNames[11] = "Grads";
        FunctionThreeTypes[11] = AnyToSame;
        FunctionThreeNames[12] = "Degrees";
        FunctionThreeTypes[12] = AnyToSame;
        FunctionThreeNames[13] = "Sin";
        FunctionThreeTypes[13] = AnyToSame;
        FunctionThreeNames[14] = "Cos";
        FunctionThreeTypes[14] = AnyToSame;
        FunctionThreeNames[15] = "Tan";
        FunctionThreeTypes[15] = AnyToSame;
        FunctionThreeNames[16] = "Skewness";
        FunctionThreeTypes[16] = VectorToScalar;
        FunctionThreeNames[17] = "Kurtosis";
        FunctionThreeTypes[17] = VectorToScalar;
        FunctionThreeNames[18] = "Index";
        FunctionThreeTypes[18] = AnyToScalar;
        FunctionThreeNames[19] = "Weekday";
        FunctionThreeTypes[19] = AnyToSame;
        FunctionThreeNames[20] = "SolveQuadratic";
        FunctionThreeTypes[20] = AnyToVector;
        FunctionThreeNames[21] = "WeekdayJulian";
        FunctionThreeTypes[21] = AnyToSame;
        FunctionListNames[1] = "Use";
        FunctionListTypes[1] = AnyToVector;
        FunctionListNames[2] = "Covariance";
        FunctionListTypes[2] = AnyToVector;
        FunctionListNames[3] = "SolveCubic";
        FunctionListTypes[3] = AnyToVector;
        FunctionListNames[4] = "Control";
        FunctionListTypes[4] = AnyToScalar;

//
// Now store the function-one types into a hashtable
//
        for (int I = 1; I <= NumberOfFunctionsOne; I++) {
            FunctionOneTypeDictionary.put(FunctionOneNames[I], new Integer((int) FunctionOneTypes[I]));
        }
//
// Now store the function-two types into a hashtable
//
        for (int I = 1; I <= NumberOfFunctionsTwo; I++) {
            FunctionTwoTypeDictionary.put(FunctionTwoNames[I], new Integer((int) FunctionTwoTypes[I]));
        }
//
// Now store the function-three types into a hashtable
//
        for (int I = 1; I <= NumberOfFunctionsThree; I++) {
            FunctionThreeTypeDictionary.put(FunctionThreeNames[I], new Integer((int) FunctionThreeTypes[I]));
        }
//
//
//
// Now store the function-list types into a hashtable
//
        for (int I = 1; I <= NumberOfFunctionsList; I++) {
            FunctionListTypeDictionary.put(FunctionListNames[I], new Integer((int) FunctionListTypes[I]));
        }
//
//
        return;
    }


    public double EvaluateScalarFunctionOfOneVariable(String FunctionToken, double ArgumentValue) {
//
// Returns the value of a scalar-valued function of one variable on a
// scalar-valued argument.  The functions evauated here are:
//
// Negate, Abs, Acos, Asin, Atan, Ceil, Cos, Cosh, Exp, Floor, Log, Log10,
// Log2, Round, Sin, Sinh, Sqrt, Tan, Tanh, Sign, Atanh, Asinh, Acosh,
// Degrees, Radians, Grads, FToC, FToK, CToF, CToK, KToC, KToF, Frac,
// Int, Factorial, AbsMag, Luminosity, AppMag, Brightness
//
// Improvement:
// The simpler functions here (non-transcendental, non-built-in-to-java)
// should be in-lined by the interpreter, ie substituted into the token
// list before optimization, so that their calculations can be optimized.
//
        double Value = 0.0;
        if (FunctionToken.equals("Negate")) {
            Value = -ArgumentValue;
        } else if (FunctionToken.equals("Abs")) {
            Value = Math.abs(ArgumentValue);
        } else if (FunctionToken.equals("Acos")) {
            Value = inverseAngleScale * Math.acos(ArgumentValue);
        } else if (FunctionToken.equals("Asin")) {
            Value = inverseAngleScale * Math.asin(ArgumentValue);
        } else if (FunctionToken.equals("Atan")) {
            Value = inverseAngleScale * Math.atan(ArgumentValue);
        } else if (FunctionToken.equals("Ceil")) {
            Value = Math.ceil(ArgumentValue);
        } else if (FunctionToken.equals("Int")) {
            Value = (ArgumentValue >= 0) ? Math.floor(ArgumentValue) : Math.ceil(ArgumentValue);
        } else if (FunctionToken.equals("Cos")) {
            Value = Math.cos(angleScale * ArgumentValue);
        } else if (FunctionToken.equals("Cosh")) {
            double Eplus = Math.exp(ArgumentValue);
            Value = (Eplus + 1. / Eplus) / 2;
        } else if (FunctionToken.equals("Exp")) {
            Value = Math.exp(ArgumentValue);
        } else if (FunctionToken.equals("Floor")) {
            Value = Math.floor(ArgumentValue);
        } else if (FunctionToken.equals("Log")) {
            Value = Math.log(ArgumentValue);
        } else if (FunctionToken.equals("Log10")) {
            Value = Math.log(ArgumentValue) * 0.43429448190325176;
        } else if (FunctionToken.equals("Log2")) {
            Value = Math.log(ArgumentValue) * 1.44269504088896438539;
        } else if (FunctionToken.equals("Round")) {
            Value = Math.round(ArgumentValue);
        } else if (FunctionToken.equals("Sin")) {
            Value = Math.sin(angleScale * ArgumentValue);
        } else if (FunctionToken.equals("Sinh")) {
            double Eplus = Math.exp(ArgumentValue);
            Value = (Eplus - 1. / Eplus) / 2;
        } else if (FunctionToken.equals("Sqrt")) {
            Value = Math.sqrt(ArgumentValue);
        } else if (FunctionToken.equals("Tan")) {
            Value = Math.tan(angleScale * ArgumentValue);
        } else if (FunctionToken.equals("Tanh")) {
            double e2 = Math.exp(ArgumentValue * 2);
            Value = (e2 - 1.) / (e2 + 1.);
        } else if (FunctionToken.equals("Sign")) {
            if (ArgumentValue < 0) {
                Value = -1.;
            } else if (ArgumentValue == 0) {
                Value = 0;
            } else {
                Value = 1.;
            }
        } else if (FunctionToken.equals("Atanh")) {
// throw out-of-bounds exception if ArgumentValue is not between -1 and +1 !!
            Value = Math.log((1 + ArgumentValue) / (1 - ArgumentValue)) / 2;
        } else if (FunctionToken.equals("Asinh")) {
            Value = Math.log(ArgumentValue + Math.sqrt(ArgumentValue * ArgumentValue + 1));
        } else if (FunctionToken.equals("Acosh")) {
// throw exception if ArgumentValue is less than 1. !!
            Value = Math.log(ArgumentValue + Math.sqrt(ArgumentValue * ArgumentValue - 1));
        } else if (FunctionToken.equals("Degrees")) {
            Value = 180.0 / Math.PI * angleScale * ArgumentValue;
        } else if (FunctionToken.equals("Radians")) {
            Value = angleScale * ArgumentValue;
        } else if (FunctionToken.equals("Grads")) {
            Value = 50.0 / Math.PI * angleScale * ArgumentValue;
        } else if (FunctionToken.equals("FToC")) {
            Value = 5.0 / 9.0 * (ArgumentValue - 32);
        } else if (FunctionToken.equals("FToK")) {
            Value = 5.0 / 9.0 * (ArgumentValue + 459.67);
        } else if (FunctionToken.equals("CToF")) {
            Value = 1.8 * ArgumentValue + 32;
        } else if (FunctionToken.equals("CToK")) {
            Value = ArgumentValue + 273.15;
        } else if (FunctionToken.equals("KToF")) {
            Value = 1.8 * ArgumentValue - 459.67;
        } else if (FunctionToken.equals("KToC")) {
            Value = ArgumentValue - 273.15;
        } else if (FunctionToken.equals("Frac")) {
            Value = (ArgumentValue >= 0) ? ArgumentValue - Math.floor(ArgumentValue)
                    : ArgumentValue - Math.ceil(ArgumentValue);
        } else if (FunctionToken.equals("Factorial")) {
            if (ArgumentValue < -0.5) {
                userInterface.println(
                        "Error: Factorial should not be evaluated on a negative argument.  Returning a value of 1.0.");
                Value = 1;
            } else {
                int k = (int) Math.round(ArgumentValue);
                Value = 1;
                while (k > 0) {
                    Value *= k;
                    k--;
                }
            }
        } else if (FunctionToken.equals("AbsMag")) {
            Value = 1.085736204758 * (65.56 - Math.log(ArgumentValue));
        } else if (FunctionToken.equals("Luminosity")) {
            Value = Math.exp(65.56 - ArgumentValue / 1.085736204758);
        } else if (FunctionToken.equals("AppMag")) {
            Value = 1.085736204758 * (-17.41 - Math.log(ArgumentValue));
        } else if (FunctionToken.equals("Brightness")) {
            Value = Math.exp(-17.41 - ArgumentValue / 1.085736204758);
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of one scalar variable.");
            Value = 0;
        }
        return Value;
    }

    public double EvaluateScalarFunctionOfOneVariable(String FunctionToken, double[] ArgumentValue) {
//
// Returns the value of a scalar-valued function of one variable on a
// vector-valued argument.  The functions evauated here are:
//
// Sum, Average, Product, GeometricMean, Count, SumOfSquares, Max, Min,
// SampleVariance, SampleCentralMoment3, SampleCentralMoment4, Index
//
// Improvement:
// The simpler functions here (non-transcendental, non-built-in-to-java)
// should be in-lined by the interpreter, ie substituted into the token
// list before optimization, so that their calculations can be optimized.
//
        double Value = 0.0;
        int ArgLength = ArgumentValue.length;
        int I;
        if (FunctionToken.equals("Sum")) {
            Value = 0;
            for (I = 0; I < ArgLength; I++) {
                Value += ArgumentValue[I];
            }
        } else if (FunctionToken.equals("Average")) {
            Value = 0;
            for (I = 0; I < ArgLength; I++) {
                Value += ArgumentValue[I];
            }
            Value = Value / ArgLength;
        } else if (FunctionToken.equals("Product")) {
            Value = ArgumentValue[0];
            if (ArgLength > 1) {
                for (I = 1; I < ArgLength; I++) {
                    Value *= ArgumentValue[I];
                }
            }
        } else if (FunctionToken.equals("GeometricMean")) {
            Value = ArgumentValue[0];
            if (ArgLength > 1) {
                for (I = 1; I < ArgLength; I++) {
                    Value *= ArgumentValue[I];
                }
            }
            if (Value < 0) {
                Value = -1.0 * Value;
            }
            Value = Math.pow(Value, 1.0 / ArgLength);
        } else if (FunctionToken.equals("Count")) {
            Value = (double) ArgLength;
        } else if (FunctionToken.equals("SumOfSquares")) {
            Value = 0;
            for (I = 0; I < ArgLength; I++) {
                Value += ArgumentValue[I] * ArgumentValue[I];
            }
        } else if (FunctionToken.equals("Max")) {
            Value = ArgumentValue[0];
            if (ArgLength > 1) {
                for (I = 1; I < ArgLength; I++) {
                    Value = Math.max(Value, ArgumentValue[I]);
                }
            }
        } else if (FunctionToken.equals("Min")) {
            Value = ArgumentValue[0];
            if (ArgLength > 1) {
                for (I = 1; I < ArgLength; I++) {
                    Value = Math.min(Value, ArgumentValue[I]);
                }
            }
        } else if (FunctionToken.equals("SampleVariance")) {
            double temp;
            double meanEst = 0.0;
            if (ArgLength <= 1) {
                Value = 0.0;
            } else {
                for (I = 0; I < ArgLength; I++) {
                    meanEst += ArgumentValue[I];
                }
                meanEst = meanEst / ArgLength;
                for (I = 0; I < ArgLength; I++) {
                    temp = ArgumentValue[I] - meanEst;
                    Value += temp * temp;
                }
                Value = Value / (ArgLength - 1);
            }
        } else if (FunctionToken.equals("SampleCentralMoment3")) {
            double temp;
            double meanEst = 0.0;
            if (ArgLength <= 2) {
                Value = 0.0;
            } else {
                for (I = 0; I < ArgLength; I++) {
                    meanEst += ArgumentValue[I];
                }
                meanEst = meanEst / ArgLength;
                for (I = 0; I < ArgLength; I++) {
                    temp = ArgumentValue[I] - meanEst;
                    Value += temp * temp * temp;
                }
                Value = Value * ArgLength / (ArgLength - 1) / (ArgLength - 2);
            }
        } else if (FunctionToken.equals("SampleCentralMoment4")) {
            double temp1, temp2;
            double meanEst = 0.0;
            double varEst = 0.0;
            if (ArgLength <= 3) {
                Value = 0.0;
            } else {
                for (I = 0; I < ArgLength; I++) {
                    meanEst += ArgumentValue[I];
                }
                meanEst = meanEst / ArgLength;
                for (I = 0; I < ArgLength; I++) {
                    temp1 = ArgumentValue[I] - meanEst;
                    temp2 = temp1 * temp1;
                    varEst += temp2;
                    Value += temp2 * temp2;
                }
                varEst = varEst / (ArgLength - 1);
                Value = (ArgLength * ArgLength / (ArgLength - 1) * Value - 3 * (2 * ArgLength - 3) * varEst * varEst)
                        / (ArgLength * (ArgLength - 3) - 3);
            }
        } else if (FunctionToken.equals("Index")) {
            Value = -1.;
            for (I = 0; I < ArgLength; I++) {
                if (ArgumentValue[I] != 0.0) {
                    Value = I;
                    break;
                }
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of one vector variable.");
            Value = 0;
        }
        return Value;
    }

    public double[] EvaluateVectorFunctionOfOneVariable(String FunctionToken, double ArgumentValue) {
//
// Returns the value of a vector-valued function of one variable on a
// scalar-valued argument.  The functions evauated here are:
//
// UniformRandom, GaussianRandom, Sequence, DMS, AstroHMS, HMS.
//
// Improvement:
// The simpler functions here (non-transcendental, non-built-in-to-java)
// should be in-lined by the interpreter, ie substituted into the token
// list before optimization, so that their calculations can be optimized.
//
// The scalar ArgumentValue for many functions is the length of the vector,
// input as a double.
//
        double[] Value;
        int I, OutputLength;
        long seed;
        if (FunctionToken.equals("UniformRandom")) {
//	The output vector's elements are uniformly distributed on (0,1).
            OutputLength = (int) Math.round(ArgumentValue);
            if (OutputLength < 1) {
                userInterface.println("Error: Function " + FunctionToken
                        + " cannot take a single argument that rounds to a non-positive integer.");
            }
            Value = new double[OutputLength];
            seed = System.currentTimeMillis();
            if (seed == lastRandomSeed) {
                seed = seed + 1;
            }
            lastRandomSeed = seed;
            Random Uniform = new Random(seed);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Uniform.nextDouble();
            }
        } else if (FunctionToken.equals("GaussianRandom")) {
//	The output vector's elements are distributed as a Gaussian with zero mean and
// 	unit standard deviation.
            OutputLength = (int) Math.round(ArgumentValue);
            if (OutputLength < 1) {
                userInterface.println("Error: Function " + FunctionToken
                        + " cannot take a single argument that rounds to a non-positive integer.");
            }
            Value = new double[OutputLength];
            seed = System.currentTimeMillis();
            if (seed == lastRandomSeed) {
                seed = seed + 1;
            }
            lastRandomSeed = seed;
            Random Gaussian = new Random(seed);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Gaussian.nextGaussian();
            }
        } else if (FunctionToken.equals("Sequence")) {
//	The output vector's elements are (0, 1, 2, ..., OutputLength - 1), as doubles.
            OutputLength = (int) Math.round(ArgumentValue);
            if (OutputLength < 1) {
                userInterface.println("Error: Function " + FunctionToken
                        + " cannot take a single argument that rounds to a non-positive integer.");
            }
            Value = new double[OutputLength];
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (double) I;
            }
        } else if (FunctionToken.equals("DMS")) {
            int sign = 1;
            double degrees, minutes;
            Value = new double[3];
            if (ArgumentValue == 0) {
                Value[0] = 0;
                Value[1] = 0;
                Value[2] = 0;
            } else {
                if (ArgumentValue < 0) {
                    sign = -1;
                    ArgumentValue = -ArgumentValue;
                }
                degrees = 180.0 / Math.PI * angleScale * ArgumentValue;
                Value[0] = sign * Math.floor(degrees);
                minutes = (degrees - sign * Value[0]) * 60.0;
                Value[1] = sign * Math.floor(minutes);
                Value[2] = sign * (minutes - sign * Value[1]) * 60.0;
            }
        } else if (FunctionToken.equals("AstroHMS")) {
            int sign = 1;
            double hourAngle, minutes;
            Value = new double[3];
            if (ArgumentValue == 0) {
                Value[0] = 0;
                Value[1] = 0;
                Value[2] = 0;
            } else {
                if (ArgumentValue < 0) {
                    sign = -1;
                    ArgumentValue = -ArgumentValue;
                }
                hourAngle = 12.0 / Math.PI * angleScale * ArgumentValue;
                Value[0] = sign * Math.floor(hourAngle);
                minutes = (hourAngle - sign * Value[0]) * 60.0;
                Value[1] = sign * Math.floor(minutes);
                Value[2] = sign * (minutes - sign * Value[1]) * 60.0;
            }
        } else if (FunctionToken.equals("HMS")) {
            int sign = 1;
            double minutes;
            Value = new double[3];
            if (ArgumentValue == 0) {
                Value[0] = 0;
                Value[1] = 0;
                Value[2] = 0;
            } else {
                if (ArgumentValue < 0) {
                    sign = -1;
                    ArgumentValue = -ArgumentValue;
                }
                Value[0] = sign * Math.floor(ArgumentValue);
                minutes = (ArgumentValue - sign * Value[0]) * 60.0;
                Value[1] = sign * Math.floor(minutes);
                Value[2] = sign * (minutes - sign * Value[1]) * 60.0;
            }
        } else {
            OutputLength = (int) Math.round(ArgumentValue);
            if (OutputLength < 1) {
                userInterface.println("Error: Function " + FunctionToken
                        + " cannot take a single argument that rounds to a non-positive integer.");
            }
            Value = new double[OutputLength];
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of one scalar variable.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }


    public double[] EvaluateVectorFunctionOfOneVariable(String FunctionToken, double[] ArgumentValue) {
//
// Returns the value of a vector-valued function of one variable on a
// vector-valued argument.  The functions evauated here are:
//
// Reverse, UniformRandom, GaussianRandom, Sequence, Accumulate, Sort, Randomize,
// Negate, Abs, Acos, Asin, Atan, Ceil, Cos, Cosh, Exp, Floor, Log, Log10,
// Log2, Round, Sin, Sinh, Sqrt, Tan, Tanh, Sign, Atanh, Asinh, Acosh,
// Degrees, Radians, Grads, FToC, FToK, CToF, CToK, KToC, KToF, Frac, Int,
// Factorial, Select, Diff, Regression, AbsMag, Luminosity, AppMag, Brightness.
//
// Improvement:
// The simpler functions here (non-transcendental, non-built-in-to-java)
// should be in-lined by the interpreter, ie substituted into the token
// list before optimization, so that their calculations can be optimized.
//
// The input vector is used to get the length of the output vector.
//
        int OutputLength = ArgumentValue.length;
        if (OutputLength < 1) {
            userInterface.println("Error: Function " + FunctionToken
                    + " cannot take a single argument that rounds to a non-positive integer.");
        }
        double Value[] = new double[OutputLength];
        int I;
        long seed;
        if (FunctionToken.equals("Reverse")) {
            int J = OutputLength - 1;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = ArgumentValue[J--];
            }
        } else if (FunctionToken.equals("Accumulate")) {
//	The output vector's elements are partial sums of the input vector's elements.
            Value[0] = ArgumentValue[0];
            for (I = 1; I < OutputLength; I++) {
                Value[I] = Value[I - 1] + ArgumentValue[I];
            }
        } else if (FunctionToken.equals("UniformRandom")) {
//	The output vector's elements are uniformly distributed on (0,1).
            seed = System.currentTimeMillis();
            if (seed == lastRandomSeed) {
                seed = seed + 1;
            }
            lastRandomSeed = seed;
            Random Uniform = new Random(seed);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Uniform.nextDouble();
            }
        } else if (FunctionToken.equals("Sort")) {
//	The output vector's elements are arranged in ascending order.
            Value = mergeSort(ArgumentValue, 1.0);
        } else if (FunctionToken.equals("Randomize")) {
//
//      The vector argument's elements are rearranged in a random order by
//      generating a random sequence of doubles, associating them with the
//      elements of the input (using a Hashtable), sorting the random doubles
//      into ascending order, and retrieving the elements of the hashtable in
//      the new order.
//
            double key;
            double[] indices = new double[OutputLength];
            seed = System.currentTimeMillis();
            if (seed == lastRandomSeed) {
                seed = seed + 1;
            }
            lastRandomSeed = seed;
            Random Uniformizer = new Random(seed);
            Hashtable Rearranger = new Hashtable(2 * OutputLength);
            for (I = 0; I < OutputLength; I++) {
                key = Uniformizer.nextDouble();
                Rearranger.put(new Double(key), new Double(ArgumentValue[I]));
                indices[I] = key;
            }
            indices = mergeSort(indices, 1.0);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = ((Double) Rearranger.get(new Double(indices[I]))).doubleValue();
            }
        } else if (FunctionToken.equals("GaussianRandom")) {
//	The output vector's elements are distributed as a Gaussian with zero mean and
// 	unit standard deviation.
            seed = System.currentTimeMillis();
            if (seed == lastRandomSeed) {
                seed = seed + 1;
            }
            lastRandomSeed = seed;
            Random Gaussian = new Random(seed);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Gaussian.nextGaussian();
            }
        } else if (FunctionToken.equals("Sequence")) {
//	The output vector's elements are (0, 1, 2, ..., OutputLength - 1), as doubles.
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (double) I;
            }
        } else if (FunctionToken.equals("Negate")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = -ArgumentValue[I];
            }
        } else if (FunctionToken.equals("Abs")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.abs(ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Acos")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = inverseAngleScale * Math.acos(ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Asin")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = inverseAngleScale * Math.asin(ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Atan")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = inverseAngleScale * Math.atan(ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Ceil")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.ceil(ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Int")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (ArgumentValue[I] >= 0) ? Math.floor(ArgumentValue[I]) : Math.ceil(ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Cos")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.cos(angleScale * ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Cosh")) {
            double Eplus;
            for (I = 0; I < OutputLength; I++) {
                Eplus = Math.exp(ArgumentValue[I]);
                Value[I] = (Eplus + 1. / Eplus) / 2;
            }
        } else if (FunctionToken.equals("Exp")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.exp(ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Floor")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.floor(ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Log")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.log(ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Log10")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.log(ArgumentValue[I]) * 0.43429448190325176;
            }
        } else if (FunctionToken.equals("Log2")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.log(ArgumentValue[I]) * 1.44269504088896438539;
            }
        } else if (FunctionToken.equals("Round")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.round(ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Sin")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.sin(angleScale * ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Sinh")) {
            double Eplus;
            for (I = 0; I < OutputLength; I++) {
                Eplus = Math.exp(ArgumentValue[I]);
                Value[I] = (Eplus - 1. / Eplus) / 2;
            }
        } else if (FunctionToken.equals("Sqrt")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.sqrt(ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Tan")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.tan(angleScale * ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Tanh")) {
            for (I = 0; I < OutputLength; I++) {
                double e2 = Math.exp(ArgumentValue[I] * 2);
                Value[I] = (e2 - 1.) / (e2 + 1.);
            }
        } else if (FunctionToken.equals("Sign")) {
            for (I = 0; I < OutputLength; I++) {
                if (ArgumentValue[I] < 0) {
                    Value[I] = -1.;
                } else if (ArgumentValue[I] == 0) {
                    Value[I] = 0;
                } else {
                    Value[I] = 1.;
                }
            }
        } else if (FunctionToken.equals("Atanh")) {
// throw out-of-bounds exception if ArgumentValue is not between -1 and +1 !!
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.log((1. + ArgumentValue[I]) / (1. - ArgumentValue[I])) / 2;
            }
        } else if (FunctionToken.equals("Asinh")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.log(ArgumentValue[I] + Math.sqrt(ArgumentValue[I] * ArgumentValue[I] + 1));
            }
        } else if (FunctionToken.equals("Acosh")) {
// throw exception if ArgumentValue is less than 1. !!
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.log(ArgumentValue[I] + Math.sqrt(ArgumentValue[I] * ArgumentValue[I] - 1));
            }
        } else if (FunctionToken.equals("Degrees")) {
            double convert = 180.0 / Math.PI * angleScale;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = convert * ArgumentValue[I];
            }
        } else if (FunctionToken.equals("Radians")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = angleScale * ArgumentValue[I];
            }
        } else if (FunctionToken.equals("Grads")) {
            double convert = 50.0 / Math.PI * angleScale;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = convert * ArgumentValue[I];
            }
        } else if (FunctionToken.equals("FToC")) {
            double convert = 5.0 / 9.0;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = convert * (ArgumentValue[I] - 32);
            }
        } else if (FunctionToken.equals("FToK")) {
            double convert = 5.0 / 9.0;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = convert * (ArgumentValue[I] + 459.67);
            }
        } else if (FunctionToken.equals("CToF")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 1.8 * ArgumentValue[I] + 32;
            }
        } else if (FunctionToken.equals("CToK")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = ArgumentValue[I] + 273.15;
            }
        } else if (FunctionToken.equals("KToF")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 1.8 * ArgumentValue[I] - 459.67;
            }
        } else if (FunctionToken.equals("KToC")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = ArgumentValue[I] - 273.15;
            }
        } else if (FunctionToken.equals("Frac")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (ArgumentValue[I] >= 0) ? ArgumentValue[I] - Math.floor(ArgumentValue[I])
                        : ArgumentValue[I] - Math.ceil(ArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Factorial")) {
            int k;
            for (I = 0; I < OutputLength; I++) {
                if (ArgumentValue[I] < -0.5) {
                    userInterface.println(
                            "Error: Factorial should not be evaluated on a negative argument.  Returning a value of 1.0.");
                    Value[I] = 1;
                } else {
                    k = (int) Math.round(ArgumentValue[I]);
                    Value[I] = 1;
                    while (k > 0) {
                        Value[I] *= k;
                        k--;
                    }
                }
            }
        } else if (FunctionToken.equals("Select")) {
            Vector nonZero = new Vector(OutputLength);
            for (I = 0; I < OutputLength; I++) {
                if (ArgumentValue[I] != 0.0) {
                    nonZero.addElement(new Double(ArgumentValue[I]));
                }
            }
            int size = nonZero.size();
            if (size == 0) {
                Value = new double[1];
                Value[0] = 0.0;
            } else {
                Value = new double[size];
                for (I = 0; I < size; I++) {
                    Value[I] = ((Double) nonZero.elementAt(I)).doubleValue();
                }
            }
        } else if (FunctionToken.equals("Diff")) {
            Value = new double[OutputLength - 1];
            for (I = 0; I < OutputLength - 1; I++) {
                Value[I] = ArgumentValue[I + 1] - ArgumentValue[I];
            }
        } else if (FunctionToken.equals("Regression")) {
            // define variables to be means of sums of x[i], y[i], x[i]*y[i], etc
            double Y = 0.0;
            double X = (OutputLength - 1) / 2.;
            double XY = 0.0;
            double XX = (OutputLength - 1) * (2 * OutputLength - 1) / 6.;
            double YY = 0.0;
            double temp, a, b, c;
            for (I = 0; I < OutputLength; I++) {
                temp = ArgumentValue[I];
                Y += temp;
                XY += I * temp;
                YY += temp * temp;
            }
            Y /= OutputLength;
            XY /= OutputLength;
            YY /= OutputLength;
            temp = XX - X * X;
            Value = new double[3];
            a = (XY - X * Y) / temp; // slope
            b = (XX * Y - XY * X) / temp; // intercept
            c = Math.sqrt(YY - 2 * a * XY + a * a * XX - 2 * b * Y + 2 * a * b * X + b * b); // rms of residuals
            Value[0] = a;
            Value[1] = b;
            Value[2] = c;
        } else if (FunctionToken.equals("AbsMag")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 1.085736204758 * (65.56 - Math.log(ArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Luminosity")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.exp(65.56 - ArgumentValue[I] / 1.085736204758);
            }
        } else if (FunctionToken.equals("AppMag")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 1.085736204758 * (-17.41 - Math.log(ArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Brightness")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.exp(-17.41 - ArgumentValue[I] / 1.085736204758);
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of one vector variable.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }


    public double EvaluateScalarFunctionOfTwoVariables(String FunctionToken, double FirstArgumentValue,
                                                       double SecondArgumentValue) {
//
// Returns the value of a scalar-valued function of two variables, both scalar-valued.
// The functions evaluated here are:
//
// Mod, IEEERemainder, Atan2, Max, Min, Pow, Combinations
//
        double Value = 0.0;
        if (FunctionToken.equals("IEEERemainder")) {
            Value = Math.IEEEremainder(FirstArgumentValue, SecondArgumentValue);
        } else if (FunctionToken.equals("Mod")) {
            Value = Math.IEEEremainder(FirstArgumentValue, SecondArgumentValue);
            if (Value < 0) {
                Value += SecondArgumentValue;
            }
        } else if (FunctionToken.equals("Atan2")) {
            Value = inverseAngleScale * Math.atan2(FirstArgumentValue, SecondArgumentValue);
        } else if (FunctionToken.equals("Max")) {
            Value = Math.max(FirstArgumentValue, SecondArgumentValue);
        } else if (FunctionToken.equals("Min")) {
            Value = Math.min(FirstArgumentValue, SecondArgumentValue);
        } else if (FunctionToken.equals("Pow")) {
            Value = Math.pow(FirstArgumentValue, SecondArgumentValue);
        } else if (FunctionToken.equals("Combinations")) {
            Value = 1.0;
            int n = (int) Math.round(FirstArgumentValue);
            int m = (int) Math.round(SecondArgumentValue);
            int k;
            if ((m < 0) || (n < m)) {
                userInterface.println("Function Combinations: arguments out of range: (" + String.valueOf(n) + ", "
                        + String.valueOf(m) + ")");
            } else {
                for (k = 1; k <= m; k++) {
                    Value *= (n - k + 1.0) / k;
                }
            }
            Value = Math.round(Value);
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of two scalar variables.");
            Value = 0;
        }
        return Value;
    }


    public double EvaluateScalarFunctionOfTwoVariables(String FunctionToken, double[] FirstArgumentValue,
                                                       double SecondArgumentValue) {
//
// Returns the value of a scalar-valued function of one vector-valued and one scalar-valued variable.
// The functions evaluated here are:
//
// ElementAt, Variance, CentralMoment3, CentralMoment4, Index
//
// The length of the vector always comes from the first argument.
//
        double Value = 0.0;
        int I;
        int firstLength = FirstArgumentValue.length;
        if (FunctionToken.equals("ElementAt")) {
            int index1 = (int) Math.round(SecondArgumentValue);
            if ((index1 < 0) || (index1 > firstLength)) {
                userInterface.println("Function ElementAt: index argument out of range: " + String.valueOf(index1));
                Value = 0.0;
            } else {
                Value = FirstArgumentValue[index1];
            }
        } else if (FunctionToken.equals("Variance")) {
            double temp;
            for (I = 0; I < firstLength; I++) {
                temp = FirstArgumentValue[I] - SecondArgumentValue;
                Value += temp * temp;
            }
            Value = Value / firstLength;
        } else if (FunctionToken.equals("CentralMoment3")) {
            double temp;
            for (I = 0; I < firstLength; I++) {
                temp = FirstArgumentValue[I] - SecondArgumentValue;
                Value += temp * temp * temp;
            }
            Value = Value / firstLength;
        } else if (FunctionToken.equals("CentralMoment4")) {
            double temp;
            for (I = 0; I < firstLength; I++) {
                Value += Math.pow(FirstArgumentValue[I] - SecondArgumentValue, 4);
            }
            Value = Value / firstLength;
        } else if (FunctionToken.equals("Index")) {
            Value = -1.;
            int index1 = (int) Math.round(SecondArgumentValue);
            if (index1 >= firstLength) {
                index1 = firstLength - 1;
            }
            for (I = index1; I < firstLength; I++) {
                if (FirstArgumentValue[I] != 0.0) {
                    Value = I;
                    break;
                }
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of one vector and one scalar variable.");
            Value = 0;
        }
        return Value;
    }


    public double EvaluateScalarFunctionOfTwoVariables(String FunctionToken, double FirstArgumentValue,
                                                       double[] SecondArgumentValue) {
//
// Returns the value of a scalar-valued function of one scalar-valued and one vector-valued variable.
// The functions evaluated here are:
//
// (None)
//
// The length of the vector always comes from the first argument.
//
        double Value = 0.0;
        int OutputLength = SecondArgumentValue.length;
        if (false) {
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of one scalar and one vector variable.");
            Value = 0;
        }
        return Value;
    }

    public double EvaluateScalarFunctionOfTwoVariables(String FunctionToken, double[] FirstArgumentValue,
                                                       double[] SecondArgumentValue) {
//
// Returns the value of a scalar-valued function of two vector-valued variables.
// The functions evaluated here are:
//
// ScalarProduct, SampleCovariance
//
// The length of the vector always comes from the first argument.  An error
// is usually generated if the two arguments do not have the same length.
//
        int I;
        double Value = 0.0;
        int firstArgLength = FirstArgumentValue.length;
        if (FunctionToken.equals("ScalarProduct")) {
            if (SecondArgumentValue.length != firstArgLength) {
                userInterface.println("Error: In ScalarProduct, the two sequences must have the same length.");
                Value = 0;
            } else {
                Value = 0;
                for (I = 0; I < firstArgLength; I++) {
                    Value += FirstArgumentValue[I] * SecondArgumentValue[I];
                }
            }
        } else if (FunctionToken.equals("SampleCovariance")) {
            double temp;
            double meanEst1 = 0.0;
            double meanEst2 = 0.0;
            if (firstArgLength <= 1) {
                Value = 0.0;
            } else if (SecondArgumentValue.length != firstArgLength) {
                userInterface.println("Error: In SampleCovariance, the two sequences must have the same length.");
                Value = 0;
            } else {
                for (I = 0; I < firstArgLength; I++) {
                    meanEst1 += FirstArgumentValue[I];
                    meanEst2 += SecondArgumentValue[2];
                }
                meanEst1 = meanEst1 / firstArgLength;
                meanEst2 = meanEst2 / firstArgLength;
                for (I = 0; I < firstArgLength; I++) {
                    Value = (FirstArgumentValue[I] - meanEst1) * (SecondArgumentValue[I] - meanEst2);
                }
                Value = Value / (firstArgLength - 1);
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of two vector variables.");
            Value = 0;
        }
        return Value;
    }


    public double[] EvaluateVectorFunctionOfTwoVariables(String FunctionToken, double FirstArgumentValue,
                                                         double SecondArgumentValue) {
//
// Returns the value of a vector-valued function of two scalar-valued variables.
// The functions evaluated here are:
//
// UniformRandom, GaussianRandom, Sequence
//
// The length of the vector always comes from the first argument.
//
        int I;
        long seed;
        int OutputLength = (int) Math.round(FirstArgumentValue);
        if (OutputLength < 1) {
            userInterface.println("Error: Function " + FunctionToken
                    + " cannot take a first argument that rounds to a non-positive integer.");
        }
        double Value[] = new double[OutputLength];
        if (FunctionToken.equals("UniformRandom")) {
//	The output vector's elements are uniformly distributed on (0, SecondArgumentValue).
            seed = System.currentTimeMillis();
            if (seed == lastRandomSeed) {
                seed = seed + 1;
            }
            lastRandomSeed = seed;
            Random Uniform = new Random(seed);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = SecondArgumentValue * Uniform.nextDouble();
            }
        } else if (FunctionToken.equals("GaussianRandom")) {
//	The output vector's elements are distributed as a Gaussian with zero mean and
// 	standard deviation = SecondArgumentValue.
            seed = System.currentTimeMillis();
            if (seed == lastRandomSeed) {
                seed = seed + 1;
            }
            lastRandomSeed = seed;
            Random Gaussian = new Random(seed);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = SecondArgumentValue * Gaussian.nextGaussian();
            }
        } else if (FunctionToken.equals("Sequence")) {
//	The output vector's elements are (0, SecondArgumentValue, 2*SecondArgumentValue,
//	3*SecondArgumentValue, ..., SecondArgumentValue * (OutputLength - 1) ), as doubles.
            for (I = 0; I < OutputLength; I++) {
                Value[I] = I * SecondArgumentValue;
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of two scalar variables.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }


    public double[] EvaluateVectorFunctionOfTwoVariables(String FunctionToken, double[] FirstArgumentValue,
                                                         double SecondArgumentValue) {
//
// Returns the value of a vector-valued function of one vector-valued and one scalar-valued variable.
// The functions evaluated here are:
//
// UniformRandom, GaussianRandom, Sequence, Sort, Randomize
// Mod, IEEERemainder, Atan2, Max, Min, Pow, Shift, Combinations,
// Subsequence, Select
//
// In many functions, the input vector is used to get the length of
// the output vector.
//
        int I;
        long seed;
        int OutputLength = FirstArgumentValue.length;
        double Value[] = new double[OutputLength];
        if (FunctionToken.equals("UniformRandom")) {
//	The output vector's elements are uniformly distributed on (0, SecondArgumentValue).
            seed = System.currentTimeMillis();
            if (seed == lastRandomSeed) {
                seed = seed + 1;
            }
            lastRandomSeed = seed;
            Random Uniform = new Random(seed);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = SecondArgumentValue * Uniform.nextDouble();
            }
        } else if (FunctionToken.equals("GaussianRandom")) {
//	The output vector's elements are distributed as a Gaussian with zero mean and
// 	standard deviation = SecondArgumentValue.
            seed = System.currentTimeMillis();
            if (seed == lastRandomSeed) {
                seed = seed + 1;
            }
            lastRandomSeed = seed;
            Random Gaussian = new Random(seed);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = SecondArgumentValue * Gaussian.nextGaussian();
            }
        } else if (FunctionToken.equals("Sort")) {
//	The output vector's elements are arranged in ascending order if the second
// argument is non-negative, and in descending order if it is negative.
            Value = mergeSort(FirstArgumentValue, SecondArgumentValue);
        } else if (FunctionToken.equals("Randomize")) {
//
//      The vector argument's elements are rearranged in a random order by
//      generating a random sequence of doubles, associating them with the
//      elements of the input (using a Hashtable), sorting the random doubles
//      into ascending order, and retrieving the elements of the hashtable in
//      the new order.  The scalar argument is the seed for the random number
//      generator.
//
            double key;
            double[] indices = new double[OutputLength];
            long Seed = Math.round(SecondArgumentValue);
            Random Uniformizer = new Random(Seed);
            Hashtable Rearranger = new Hashtable(2 * OutputLength);
            for (I = 0; I < OutputLength; I++) {
                key = Uniformizer.nextDouble();
                Rearranger.put(new Double(key), new Double(FirstArgumentValue[I]));
                indices[I] = key;
            }
            indices = mergeSort(indices, 1.0);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = ((Double) Rearranger.get(new Double(indices[I]))).doubleValue();
            }
        } else if (FunctionToken.equals("Sequence")) {
//	The output vector's elements are (0, SecondArgumentValue, 2*SecondArgumentValue,
//	3*SecondArgumentValue, ..., SecondArgumentValue * (OutputLength - 1) ), as doubles.
            for (I = 0; I < OutputLength; I++) {
                Value[I] = I * SecondArgumentValue;
            }
        } else if (FunctionToken.equals("Shift")) {
//	The output vector consists of the elements of the input vector shifted by an amount
//	given by the second argument.  It is a cyclic shift.  Reduce the shift modulo the length.
            int Shift = (int) Math.round(Math.IEEEremainder(Math.round(SecondArgumentValue), OutputLength));
            if (Shift < 0) {
                for (I = -Shift; I < OutputLength; I++) {
                    Value[I + Shift] = FirstArgumentValue[I];
                }
                for (I = 0; I < -Shift; I++) {
                    Value[I + Shift + OutputLength] = FirstArgumentValue[I];
                }
            } else if (Shift > 0) {
                for (I = 0; I < OutputLength - Shift; I++) {
                    Value[I + Shift] = FirstArgumentValue[I];
                }
                for (I = OutputLength - Shift; I < OutputLength; I++) {
                    Value[I + Shift - OutputLength] = FirstArgumentValue[I];
                }
            } else {
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue[I];
                }
            }
        } else if (FunctionToken.equals("IEEERemainder")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.IEEEremainder(FirstArgumentValue[I], SecondArgumentValue);
            }
        } else if (FunctionToken.equals("Mod")) {
            double temp;
            for (I = 0; I < OutputLength; I++) {
                temp = Math.IEEEremainder(FirstArgumentValue[I], SecondArgumentValue);
                if (temp < 0) {
                    temp += SecondArgumentValue;
                }
                Value[I] = temp;
            }
        } else if (FunctionToken.equals("Atan2")) {
            double v, vNext, test;
            int cycle = 0;
            v = Math.atan2(FirstArgumentValue[0], SecondArgumentValue);
            Value[0] = inverseAngleScale * v;
            if (OutputLength > 1) {
                for (I = 0; I < OutputLength; I++) {
                    vNext = Math.atan2(FirstArgumentValue[I], SecondArgumentValue);
                    test = vNext - v;
                    if (test > Math.PI) {
                        cycle--;
                    } else if (test < -Math.PI) {
                        cycle++;
                    }
                    Value[I] = inverseAngleScale * (vNext + 2 * Math.PI * cycle);
                    v = vNext;
                }
            }
        } else if (FunctionToken.equals("Max")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.max(FirstArgumentValue[I], SecondArgumentValue);
            }
        } else if (FunctionToken.equals("Min")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.min(FirstArgumentValue[I], SecondArgumentValue);
            }
        } else if (FunctionToken.equals("Pow")) {
            if (SecondArgumentValue == 2.) {
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue[I] * FirstArgumentValue[I];
                }
            } else if (SecondArgumentValue == 3.) {
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue[I] * FirstArgumentValue[I] * FirstArgumentValue[I];
                }
            } else if (SecondArgumentValue == 0.5) {
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = Math.sqrt(FirstArgumentValue[I]);
                }
            } else {
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = Math.pow(FirstArgumentValue[I], SecondArgumentValue);
                }
            }
        } else if (FunctionToken.equals("Combinations")) {
            int k, n;
            int m = (int) Math.round(SecondArgumentValue);
            if (m < 0) {
                userInterface.println("Function Combinations: second argument is negative: " + String.valueOf(m));
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = 1.0;
                }
            } else {
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = 1.0;
                    n = (int) Math.round(FirstArgumentValue[I]);
                    if (n < m) {
                        userInterface.println(
                                "Function Combinations: arguments out of range: (" + String.valueOf(n) + ", "
                                        + String.valueOf(m) + ")");
                    } else {
                        for (k = 1; k <= m; k++) {
                            Value[I] *= (n - k + 1) / k;
                        }
                        Value[I] = Math.round(Value[I]);
                    }
                }
            }
        } else if (FunctionToken.equals("Subsequence")) {
            int index1 = (int) Math.round(SecondArgumentValue);
            if (index1 >= FirstArgumentValue.length) {
                index1 = FirstArgumentValue.length - 1;
            }
            if (index1 < 0) {
                userInterface.println("Function Subsequence: second argument is negative: " + String.valueOf(index1));
                Value = FirstArgumentValue;
            } else {
                int newLength = FirstArgumentValue.length - index1;
                Value = new double[newLength];
                for (I = index1; I < FirstArgumentValue.length; I++) {
                    Value[I - index1] = FirstArgumentValue[I];
                }
            }
        } else if (FunctionToken.equals("Select")) {
            Vector nonZero = new Vector(OutputLength);
            int index1 = (int) Math.round(SecondArgumentValue);
            if (index1 >= OutputLength) {
                index1 = OutputLength - 1;
            }
            for (I = index1; I < OutputLength; I++) {
                if (FirstArgumentValue[I] != 0.0) {
                    nonZero.addElement(new Double(FirstArgumentValue[I]));
                }
            }
            int size = nonZero.size();
            if (size == 0) {
                Value = new double[1];
                Value[0] = 0.0;
            } else {
                Value = new double[size];
                for (I = 0; I < size; I++) {
                    Value[I] = ((Double) nonZero.elementAt(I)).doubleValue();
                }
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of one vector and one scalar variable.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }

    public double[] EvaluateVectorFunctionOfTwoVariables(String FunctionToken, double FirstArgumentValue,
                                                         double[] SecondArgumentValue) {
//
// Returns the value of a vector-valued function of one scalar-valued and one vector-valued variable.
// The functions evaluated here are:
//
// Mod, IEEERemainder, Atan2, Max, Min, Pow, Combinations
//
// The input vector is used to get the length of the output vector.
//
        int I;
        int OutputLength = SecondArgumentValue.length;
        double Value[] = new double[OutputLength];
        if (FunctionToken.equals("IEEERemainder")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.IEEEremainder(FirstArgumentValue, SecondArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Mod")) {
            double temp;
            for (I = 0; I < OutputLength; I++) {
                temp = Math.IEEEremainder(FirstArgumentValue, SecondArgumentValue[I]);
                if (temp < 0) {
                    temp += SecondArgumentValue[I];
                }
                Value[I] = temp;
            }
        } else if (FunctionToken.equals("Atan2")) {
            double v, vNext, test;
            int cycle = 0;
            v = Math.atan2(FirstArgumentValue, SecondArgumentValue[0]);
            Value[0] = inverseAngleScale * v;
            if (OutputLength > 1) {
                for (I = 0; I < OutputLength; I++) {
                    vNext = Math.atan2(FirstArgumentValue, SecondArgumentValue[I]);
                    test = vNext - v;
                    if (test > Math.PI) {
                        cycle--;
                    } else if (test < -Math.PI) {
                        cycle++;
                    }
                    Value[I] = inverseAngleScale * (vNext + 2 * Math.PI * cycle);
                    v = vNext;
                }
            }
        } else if (FunctionToken.equals("Max")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.max(FirstArgumentValue, SecondArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Min")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.min(FirstArgumentValue, SecondArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Pow")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.pow(FirstArgumentValue, SecondArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Combinations")) {
            int k, m;
            int n = (int) Math.round(FirstArgumentValue);
            if (n < 0) {
                userInterface.println("Function Combinations: first argument is negative: " + String.valueOf(n));
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = 1.0;
                }
            } else {
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = 1.0;
                    m = (int) Math.round(SecondArgumentValue[I]);
                    if (n < m) {
                        userInterface.println(
                                "Function Combinations: arguments out of range: (" + String.valueOf(n) + ", "
                                        + String.valueOf(m) + ")");
                    } else {
                        for (k = 1; k <= m; k++) {
                            Value[I] *= (n - k + 1) / k;
                        }
                        Value[I] = Math.round(Value[I]);
                    }
                }
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of one scalar and one vector variable.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }


    public double[] EvaluateVectorFunctionOfTwoVariables(String FunctionToken, double[] FirstArgumentValue,
                                                         double[] SecondArgumentValue) {
//
// Returns the value of a vector-valued function of two vector-valued variables.
// The functions evaluated here are:
//
// Convolve, Mod, IEEERemainder, Atan2, Max, Min, Pow, Extract, Combinations,
// Regression, Correlate
//
// The first input vector is used to get the length of the output vector.
// The second vector must be at least as long as the first; extra elements
// will be ignored.  Some functions may demand that the second vector be the
// same length as the first.
//
        int I;
        int OutputLength = FirstArgumentValue.length;
        double Value[] = new double[OutputLength];
        if ((SecondArgumentValue.length < OutputLength) && !(FunctionToken.equals("Extract"))) {
            userInterface.println("Error: In " + FunctionToken
                    + ", the second sequence argument must be at least as long as the first.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
            return Value;
        }
        if (FunctionToken.equals("Convolve")) {
            if (SecondArgumentValue.length > OutputLength) {
                userInterface.println("Error: In " + FunctionToken
                        + ", the second sequence argument must be exactly as long as the first.");
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = 0;
                }
                return Value;
            }
            int J, K;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
                for (J = 0, K = I; J <= I; J++, K--) {
                    Value[I] += FirstArgumentValue[J] * SecondArgumentValue[K];
                }
                for (J = I + 1, K = OutputLength - 1; J < OutputLength; J++, K--) {
                    Value[I] += FirstArgumentValue[J] * SecondArgumentValue[K];
                }
            }
        } else if (FunctionToken.equals("Correlate")) {
            if (SecondArgumentValue.length > OutputLength) {
                userInterface.println("Error: In " + FunctionToken
                        + ", the second sequence argument must be exactly as long as the first.");
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = 0;
                }
                return Value;
            }
            int J, K;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
                for (J = 0, K = I; J < OutputLength - I; J++, K++) {
                    Value[I] += FirstArgumentValue[J] * SecondArgumentValue[K];
                }
                for (J = OutputLength - I, K = 0; J < OutputLength; J++, K++) {
                    Value[I] += FirstArgumentValue[J] * SecondArgumentValue[K];
                }
            }
        } else if (FunctionToken.equals("IEEERemainder")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.IEEEremainder(FirstArgumentValue[I], SecondArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Mod")) {
            double temp;
            for (I = 0; I < OutputLength; I++) {
                temp = Math.IEEEremainder(FirstArgumentValue[I], SecondArgumentValue[I]);
                if (temp < 0) {
                    temp += SecondArgumentValue[I];
                }
                Value[I] = temp;
            }
        } else if (FunctionToken.equals("Atan2")) {
            double v, vNext, test;
            int cycle = 0;
            v = Math.atan2(FirstArgumentValue[0], SecondArgumentValue[0]);
            Value[0] = inverseAngleScale * v;
            if (OutputLength > 1) {
                for (I = 0; I < OutputLength; I++) {
                    vNext = Math.atan2(FirstArgumentValue[I], SecondArgumentValue[I]);
                    test = vNext - v;
                    if (test > Math.PI) {
                        cycle--;
                    } else if (test < -Math.PI) {
                        cycle++;
                    }
                    Value[I] = inverseAngleScale * (vNext + 2 * Math.PI * cycle);
                    v = vNext;
                }
            }
        } else if (FunctionToken.equals("Max")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.max(FirstArgumentValue[I], SecondArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Min")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.min(FirstArgumentValue[I], SecondArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Pow")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.pow(FirstArgumentValue[I], SecondArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Combinations")) {
            int k, m, n;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 1.0;
                n = (int) Math.round(FirstArgumentValue[I]);
                m = (int) Math.round(SecondArgumentValue[I]);
                if ((m < 0) || (n < m)) {
                    userInterface.println("Function Combinations: arguments out of range: (" + String.valueOf(n) + ", "
                            + String.valueOf(m) + ")");
                } else {
                    for (k = 1; k <= m; k++) {
                        Value[I] *= (n - k + 1) / k;
                    }
                    Value[I] = Math.round(Value[I]);
                }
            }
        } else if (FunctionToken.equals("Extract")) {
            Vector extracted = new Vector(OutputLength);
            int index1, indexOfIndex;
            for (indexOfIndex = 0; indexOfIndex < SecondArgumentValue.length; indexOfIndex++) {
                index1 = (int) Math.round(SecondArgumentValue[indexOfIndex]);
                if ((index1 >= 0) && (index1 < OutputLength)) {
                    extracted.addElement(new Double(FirstArgumentValue[index1]));
                }
            }
            int size = extracted.size();
            if (size == 0) {
                Value = new double[1];
                Value[0] = 0.0;
            } else {
                Value = new double[size];
                for (I = 0; I < size; I++) {
                    Value[I] = ((Double) extracted.elementAt(I)).doubleValue();
                }
            }
        } else if (FunctionToken.equals("Regression")) {
            // define variables to be means of sums of x[i], y[i], x[i]*y[i], etc
            double Y = 0.0;
            double X = 0.0;
            double XY = 0.0;
            double XX = 0.0;
            double YY = 0.0;
            double temp1, temp2, a, b, c;
            for (I = 0; I < OutputLength; I++) {
                temp1 = FirstArgumentValue[I];
                temp2 = SecondArgumentValue[I];
                Y += temp1;
                X += temp2;
                XY += temp2 * temp1;
                YY += temp1 * temp1;
                XX += temp2 * temp2;
            }
            Y /= OutputLength;
            X /= OutputLength;
            XY /= OutputLength;
            YY /= OutputLength;
            XX /= OutputLength;
            temp1 = XX - X * X;
            Value = new double[3];
            a = (XY - X * Y) / temp1; // slope
            b = (XX * Y - XY * X) / temp1; // intercept
            c = Math.sqrt(YY - 2 * a * XY + a * a * XX - 2 * b * Y + 2 * a * b * X + b * b); // rms of residuals
            Value[0] = a;
            Value[1] = b;
            Value[2] = c;
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of two vector variables.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }


    public double EvaluateScalarFunctionOfThreeVariables(String FunctionToken, double FirstArgumentValue,
                                                         double SecondArgumentValue, double ThirdArgumentValue) {
//
// Returns the value of a scalar-valued function of three scalar-valued variables.
// The functions evaluated here are:
//
// If, Radians, Degrees, Grads, Max, Min, AstroAngle, Sin, Cos, Tan,
// Weekday, WeekdayJulian
//
        double Value = 0.0;
        if (FunctionToken.equals("If")) {
            Value = (FirstArgumentValue >= 0) ? SecondArgumentValue : ThirdArgumentValue;
        } else if (FunctionToken.equals("Radians")) {
            Value = Math.PI / 648000. * ((60. * FirstArgumentValue + SecondArgumentValue) * 60. + ThirdArgumentValue);
        } else if (FunctionToken.equals("Degrees")) {
            Value = (FirstArgumentValue + (SecondArgumentValue + ThirdArgumentValue / 60.) / 60.);
        } else if (FunctionToken.equals("Grads")) {
            Value = ((60. * FirstArgumentValue + SecondArgumentValue) * 60. + ThirdArgumentValue) / 12960.;
        } else if (FunctionToken.equals("AstroAngle")) {
            Value = inverseAngleScale * Math.PI / 43200. * ((60. * FirstArgumentValue + SecondArgumentValue) * 60.
                    + ThirdArgumentValue);
        } else if (FunctionToken.equals("Max")) {
            Value = Math.max(FirstArgumentValue, Math.max(SecondArgumentValue, ThirdArgumentValue));
        } else if (FunctionToken.equals("Min")) {
            Value = Math.min(FirstArgumentValue, Math.min(SecondArgumentValue, ThirdArgumentValue));
        } else if (FunctionToken.equals("Sin")) {
            Value = Math.sin(Math.PI / 648000. * ((60. * FirstArgumentValue + SecondArgumentValue) * 60.
                    + ThirdArgumentValue));
        } else if (FunctionToken.equals("Cos")) {
            Value = Math.cos(Math.PI / 648000. * ((60. * FirstArgumentValue + SecondArgumentValue) * 60.
                    + ThirdArgumentValue));
        } else if (FunctionToken.equals("Tan")) {
            Value = Math.tan(Math.PI / 648000. * ((60. * FirstArgumentValue + SecondArgumentValue) * 60.
                    + ThirdArgumentValue));
        } else if (FunctionToken.equals("Weekday")) {
            int dayNumber = (int) Math.round(FirstArgumentValue);
            int month = (int) Math.round(SecondArgumentValue);
            if (month > 12) {
                userInterface.println(
                        "You have entered too large a month.  The first argument should be the day, then the month.");
                return 0.;
            }
            int year = (int) Math.round(ThirdArgumentValue);
            double mprime, yprime;
            mprime = Math.IEEEremainder(9 + month, 12);
            mprime = (mprime >= 0) ? mprime : mprime + 12;
            yprime = year - Math.floor(mprime / 10);
            if (yprime < 0) {
                yprime = yprime + 400 * (1 - Math.floor(yprime / 400));
            }
            Value = Math.IEEEremainder(
                    2 + dayNumber + Math.floor((13 * mprime + 2) / 5) + yprime + Math.floor(yprime / 4) - Math
                            .floor(yprime / 100) + Math.floor(yprime / 400), 7);
            Value = 1 + ((Value >= 0) ? Value : Value + 7);
        } else if (FunctionToken.equals("WeekdayJulian")) {
            int dayNumber = (int) Math.round(FirstArgumentValue);
            int month = (int) Math.round(SecondArgumentValue);
            if (month > 12) {
                userInterface.println(
                        "You have entered too large a month.  The first argument should be the day, then the month.");
                return 0.;
            }
            int year = (int) Math.round(ThirdArgumentValue);
            double mprime, yprime;
            mprime = Math.IEEEremainder(9 + month, 12);
            mprime = (mprime >= 0) ? mprime : mprime + 12;
            yprime = year - Math.floor(mprime / 10);
            if (yprime < 0) {
                yprime = yprime + 28 * (1 - Math.floor(yprime / 28));
            }
            Value = Math
                    .IEEEremainder(dayNumber + Math.floor((13 * mprime + 2) / 5) + yprime + Math.floor(yprime / 4), 7);
            Value = 1 + ((Value >= 0) ? Value : Value + 7);
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of three scalar variables.");
            Value = 0;
        }
        return Value;
    }


    public double EvaluateScalarFunctionOfThreeVariables(String FunctionToken, double[] FirstArgumentValue,
                                                         double SecondArgumentValue, double ThirdArgumentValue) {
//
// Returns the value of a scalar-valued function of one vector and two scalar-valued variables.
// The functions evaluated here are:
//
// Skewness, Kurtosis, Index
//
// The length of the vector always comes from the first argument.
//
        double Value = 0.0;
        int I;
        int firstLength = FirstArgumentValue.length;
        if (FunctionToken.equals("Skewness")) {
            double temp;
            for (I = 0; I < firstLength; I++) {
                temp = FirstArgumentValue[I] - SecondArgumentValue;
                Value += temp * temp * temp;
            }
            Value = Value / firstLength / Math.pow(ThirdArgumentValue, 3);
        } else if (FunctionToken.equals("Kurtosis")) {
            double temp;
            for (I = 0; I < firstLength; I++) {
                Value += Math.pow(FirstArgumentValue[I] - SecondArgumentValue, 4);
            }
            Value = Value / firstLength / Math.pow(ThirdArgumentValue, 4) - 3.0;
        } else if (FunctionToken.equals("Index")) {
            Value = -1.;
            int index1 = (int) Math.round(SecondArgumentValue);
            if (index1 >= firstLength) {
                index1 = firstLength - 1;
            }
            int index2 = (int) Math.round(ThirdArgumentValue);
            if (index2 >= firstLength) {
                index2 = firstLength - 1;
            }
            if ((index1 < 0) || (index2 < index1)) {
                userInterface.println(
                        "Function Index: second and/or third arguments are not valid: (" + String.valueOf(index1) + ", "
                                + String.valueOf(index2) + ") for a sequence of length" + String.valueOf(firstLength));
                Value = -1.;
            } else {
                for (I = index1; I < index2; I++) {
                    if (FirstArgumentValue[I] != 0.0) {
                        Value = I;
                        break;
                    }
                }
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of one vector and two scalar variables.");
            Value = 0;
        }
        return Value;
    }


    public double EvaluateScalarFunctionOfThreeVariables(String FunctionToken, double FirstArgumentValue,
                                                         double[] SecondArgumentValue, double ThirdArgumentValue) {
//
// Returns the value of a scalar-valued function of one scalar, one vector, and one scalar variable.
// The functions evaluated here are:
//
// (none)
//
// The length of the vector always comes from the second argument.
//
        double Value = 0.0;
        int I;
        int secondLength = SecondArgumentValue.length;
        if (false) {
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of one scalar, one vector and a further scalar variable.");
            Value = 0;
        }
        return Value;
    }


    public double EvaluateScalarFunctionOfThreeVariables(String FunctionToken, double[] FirstArgumentValue,
                                                         double[] SecondArgumentValue, double ThirdArgumentValue) {
//
// Returns the value of a scalar-valued function of two vector and one scalar variable.
// The functions evaluated here are:
//
// (none)
//
// The length of the vector always comes from the first argument.
//
        double Value = 0.0;
        int I;
        int OutputLength = FirstArgumentValue.length;
        if (false) {
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of two vector and one scalar variable.");
            Value = 0;
        }
        return Value;
    }


    public double EvaluateScalarFunctionOfThreeVariables(String FunctionToken, double FirstArgumentValue,
                                                         double SecondArgumentValue, double[] ThirdArgumentValue) {
//
// Returns the value of a scalar-valued function of two scalar-valued and one vector variable.
// The functions evaluated here are:
//
// (none)
//
// The length of the vector always comes from the third argument.
//
        double Value = 0.0;
        int OutputLength = ThirdArgumentValue.length;
        if (false) {
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of two scalar and one vector variable.");
            Value = 0;
        }
        return Value;
    }


    public double EvaluateScalarFunctionOfThreeVariables(String FunctionToken, double[] FirstArgumentValue,
                                                         double SecondArgumentValue, double[] ThirdArgumentValue) {
//
// Returns the value of a scalar-valued function of one vector, one scalar, and a further
// vector variable.
// The functions evaluated here are:
//
// (none)
//
// The length of the vector always comes from the first argument.
//
        double Value = 0.0;
        int OutputLength = FirstArgumentValue.length;
        if (false) {
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of one vector, one scalar, and a further vector variable.");
            Value = 0;
        }
        return Value;
    }


    public double EvaluateScalarFunctionOfThreeVariables(String FunctionToken, double FirstArgumentValue,
                                                         double[] SecondArgumentValue, double[] ThirdArgumentValue) {
//
// Returns the value of a scalar-valued function of one scalar and two vector-valued variables.
// The functions evaluated here are:
//
// (none)
//
// The length of the vector always comes from the second argument.
//
        double Value = 0.0;
        int OutputLength = SecondArgumentValue.length;
        if (false) {
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of one scalar and two vector variables.");
            Value = 0;
        }
        return Value;
    }


    public double EvaluateScalarFunctionOfThreeVariables(String FunctionToken, double[] FirstArgumentValue,
                                                         double[] SecondArgumentValue, double[] ThirdArgumentValue) {
//
// Returns the value of a scalar-valued function of three vector-valued variables.
// The functions evaluated here are:
//
// (none)
//
// The length of the vector always comes from the first argument.
//
        double Value = 0.0;
        int OutputLength = FirstArgumentValue.length;
        if (false) {
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known scalar-valued function of three vector variables.");
            Value = 0;
        }
        return Value;
    }


    public double[] EvaluateVectorFunctionOfThreeVariables(String FunctionToken, double FirstArgumentValue,
                                                           double SecondArgumentValue, double ThirdArgumentValue) {
//
// Returns the value of a vector-valued function of three scalar-valued variables.
// The functions evaluated here are:
//
// UniformRandom, GaussianRandom, Sequence, SolveQuadratic
//
// The length of the vector comes from the first argument, except in the case
// of SolveQuadratic, where the output length is always 4.
//
        int I;
        int OutputLength = (int) Math.round(FirstArgumentValue);
        if (FunctionToken.equals("SolveQuadratic")) {
            OutputLength = 4;
        } else if (OutputLength < 1) {
            userInterface.println("Error: Function " + FunctionToken
                    + " cannot take a first argument that rounds to a non-positive integer.");
        }
        double Value[] = new double[OutputLength];
        if (FunctionToken.equals("SolveQuadratic")) {
            if (FirstArgumentValue == 0) {
                if (SecondArgumentValue == 0) {
                    userInterface.println(
                            "Error: the first and second arguments of SolveQuadratic are zero.  There is no equation to solve!  Returning with zeros.");
                    for (I = 0; I < 4; I++) {
                        Value[I] = 0;
                    }
                } else {
                    userInterface.println(
                            "Warning: in SolveQuadratic, the first argument is zero.  Equation is not a quadratic. Returning the root of the linear equation.");
                    Value[0] = -ThirdArgumentValue / SecondArgumentValue;
                    for (I = 1; I < 4; I++) {
                        Value[I] = 0;
                    }
                }
            } else {
                double ratio = SecondArgumentValue / FirstArgumentValue / (-2.0);
                double discriminant = ratio * ratio - ThirdArgumentValue / FirstArgumentValue;
                if (discriminant >= 0) {
                    discriminant = Math.sqrt(discriminant);
                    Value[0] = ratio + discriminant;
                    Value[1] = 0;
                    Value[2] = ratio - discriminant;
                    Value[3] = 0;
                } else {
                    discriminant = Math.sqrt(-discriminant);
                    Value[0] = ratio;
                    Value[1] = discriminant;
                    Value[2] = ratio;
                    Value[3] = -discriminant;
                }
            }
        } else if (FunctionToken.equals("UniformRandom")) {
//	The output vector's elements are uniformly distributed on ( 0, SecondArgumentValue),
// 	and the seed is determined by the ThirdArgumentValue.
            long Seed = Math.round(ThirdArgumentValue);
            Random Uniform = new Random(Seed);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = SecondArgumentValue * Uniform.nextDouble();
            }
        } else if (FunctionToken.equals("GaussianRandom")) {
//	The output vector's elements are distributed as a Gaussian with zero mean and
// 	standard deviation = SecondArgumentValue.
// 	The seed is determined by the ThirdArgumentValue.
            long Seed = Math.round(ThirdArgumentValue);
            Random Gaussian = new Random(Seed);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = SecondArgumentValue * Gaussian.nextGaussian();
            }
        } else if (FunctionToken.equals("Sequence")) {
//	The output vector's elements are (ThirdArgumentValue, ThirdArgumentValue +
// 	SecondArgumentValue, ThirdArgumentValue + 2 * SecondArgumentValue, ...,
// 	ThirdArgumentValue + (OutputLength - 1) * SecondArgumentValue), as doubles.
            for (I = 0; I < OutputLength; I++) {
                Value[I] = I * SecondArgumentValue + ThirdArgumentValue;
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of three scalar variables.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }

    public double[] EvaluateVectorFunctionOfThreeVariables(String FunctionToken, double[] FirstArgumentValue,
                                                           double SecondArgumentValue, double ThirdArgumentValue) {
//
// Returns the value of a vector-valued function of one vector and two scalar-valued variables.
// The functions evaluated here are:
//
// UniformRandom, GaussianRandom, Sequence, Subsequence, Select
// If, Radians, Degress, Grads, Max, Min, AstroAngle, Sin, Cos,
// Tan, Weekday, WeekdayJulian
//
// The length of the vector always comes from the first argument.
//
        int I;
        int OutputLength = FirstArgumentValue.length;
        double Value[] = new double[OutputLength];
        if (FunctionToken.equals("UniformRandom")) {
//	The output vector's elements are uniformly distributed on (0, SecondArgumentValue).
// 	The seed comes from ThirdArgumentValue
            long Seed = Math.round(ThirdArgumentValue);
            Random Uniform = new Random(Seed);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = SecondArgumentValue * Uniform.nextDouble();
            }
        } else if (FunctionToken.equals("GaussianRandom")) {
//	The output vector's elements are distributed as a Gaussian with zero mean and
// 	standard deviation = SecondArgumentValue.
// 	The seed comes from ThirdArgumentValue
            long Seed = Math.round(ThirdArgumentValue);
            Random Gaussian = new Random(Seed);
            for (I = 0; I < OutputLength; I++) {
                Value[I] = SecondArgumentValue * Gaussian.nextGaussian();
            }
        } else if (FunctionToken.equals("Sequence")) {
//	The output vector's elements are (ThirdArgumentValue, ThirdArgumentValue +
// 	SecondArgumentValue, ThirdArgumentValue + 2 * SecondArgumentValue, ...,
// 	ThirdArgumentValue + (OutputLength - 1) * SecondArgumentValue), as doubles.
            for (I = 0; I < OutputLength; I++) {
                Value[I] = I * SecondArgumentValue + ThirdArgumentValue;
            }
        } else if (FunctionToken.equals("If")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue[I] >= 0) ? SecondArgumentValue : ThirdArgumentValue;
            }
        } else if (FunctionToken.equals("Radians")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue) * 60. + ThirdArgumentValue);
            }
        } else if (FunctionToken.equals("Degrees")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue[I] + (SecondArgumentValue + ThirdArgumentValue / 60.) / 60.);
            }
        } else if (FunctionToken.equals("Grads")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = ((60. * FirstArgumentValue[I] + SecondArgumentValue) * 60. + ThirdArgumentValue) / 12960.;
            }
        } else if (FunctionToken.equals("AstroAngle")) {
            double Coeff = inverseAngleScale * Math.PI / 43200.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue) * 60. + ThirdArgumentValue);
            }
        } else if (FunctionToken.equals("Sin")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math
                        .sin(Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue) * 60. + ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Cos")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math
                        .cos(Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue) * 60. + ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Tan")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math
                        .tan(Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue) * 60. + ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Max")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.max(FirstArgumentValue[I], Math.max(SecondArgumentValue, ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Min")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.min(FirstArgumentValue[I], Math.min(SecondArgumentValue, ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Subsequence")) {
            int index1 = (int) Math.round(SecondArgumentValue);
            if (index1 >= FirstArgumentValue.length) {
                index1 = FirstArgumentValue.length - 1;
            }
            int index2 = (int) Math.round(ThirdArgumentValue);
            if (index2 >= FirstArgumentValue.length) {
                index2 = FirstArgumentValue.length - 1;
            }
            if ((index1 < 0) || (index2 < index1)) {
                userInterface.println(
                        "Function Subsequence: second and third arguments are not valid: (" + String.valueOf(index1)
                                + ", " + String.valueOf(index2) + ")");
                Value = FirstArgumentValue;
            } else {
                int newLength = index2 - index1;
                Value = new double[newLength];
                for (I = 0; I < newLength; I++) {
                    Value[I] = FirstArgumentValue[I + index1];
                }
            }
        } else if (FunctionToken.equals("Select")) {
            Vector nonZero = new Vector(OutputLength);
            int index1 = (int) Math.round(SecondArgumentValue);
            if (index1 >= OutputLength) {
                index1 = OutputLength - 1;
            }
            int index2 = (int) Math.round(ThirdArgumentValue);
            if (index2 >= OutputLength) {
                index2 = OutputLength - 1;
            }
            if ((index1 < 0) || (index2 < index1)) {
                userInterface.println(
                        "Function Select: second and third arguments are not valid: (" + String.valueOf(index1) + ", "
                                + String.valueOf(index2) + ")");
                Value = FirstArgumentValue;
            } else {
                for (I = index1; I < index2; I++) {
                    if (FirstArgumentValue[I] != 0.0) {
                        nonZero.addElement(new Double(FirstArgumentValue[I]));
                    }
                }
            }
            int size = nonZero.size();
            if (size == 0) {
                Value = new double[1];
                Value[0] = 0.0;
            } else {
                Value = new double[size];
                for (I = 0; I < size; I++) {
                    Value[I] = ((Double) nonZero.elementAt(I)).doubleValue();
                }
            }
        } else if (FunctionToken.equals("Weekday")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, continuity, val;
            month = (int) Math.round(SecondArgumentValue);
            if (month > 12) {
                userInterface.println(
                        "You have entered too large a month.  The first argument should be the day, then the month.");
                Value = new double[1];
                Value[0] = 0.;
                return Value;
            }
            year = (int) Math.round(ThirdArgumentValue);
            mprime = Math.IEEEremainder(9 + month, 12);
            mprime = (mprime >= 0) ? mprime : mprime + 12;
            mprimecomb = Math.floor((13 * mprime + 2) / 5);
            yprime = year - Math.floor(mprime / 10);
            if (yprime < 0) {
                yprime = yprime + 400 * (1 - Math.floor(yprime / 400));
            }
            yprimecomb = yprime + Math.floor(yprime / 4) - Math.floor(yprime / 100) + Math.floor(yprime / 400);
            continuity = 2 + mprimecomb + yprimecomb;
            for (I = 0; I < OutputLength; I++) {
                dayNumber = (int) Math.round(FirstArgumentValue[I]);
                val = Math.IEEEremainder(dayNumber + continuity, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else if (FunctionToken.equals("WeekdayJulian")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, continuity, val;
            month = (int) Math.round(SecondArgumentValue);
            if (month > 12) {
                userInterface.println(
                        "You have entered too large a month.  The first argument should be the day, then the month.");
                Value = new double[1];
                Value[0] = 0.;
                return Value;
            }
            year = (int) Math.round(ThirdArgumentValue);
            mprime = Math.IEEEremainder(9 + month, 12);
            mprime = (mprime >= 0) ? mprime : mprime + 12;
            mprimecomb = Math.floor((13 * mprime + 2) / 5);
            yprime = year - Math.floor(mprime / 10);
            if (yprime < 0) {
                yprime = yprime + 28 * (1 - Math.floor(yprime / 28));
            }
            yprimecomb = yprime + Math.floor(yprime / 4);
            continuity = 2 + mprimecomb + yprimecomb;
            for (I = 0; I < OutputLength; I++) {
                dayNumber = (int) Math.round(FirstArgumentValue[I]);
                val = Math.IEEEremainder(dayNumber + continuity, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of one vector and two scalar variables.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }

    public double[] EvaluateVectorFunctionOfThreeVariables(String FunctionToken, double FirstArgumentValue,
                                                           double[] SecondArgumentValue, double ThirdArgumentValue) {
//
// Returns the value of a vector-valued function of one scalar, one vector, and
// a further scalar-valued variable.
// The functions evaluated here are:
//
// If, Sequence, Radians, Degress, Grads, Max, Min, AstroAngle,
// Sin, Cos, Tan, Weekday, WeekdayJulian
//
// The length of the vector always comes from the first argument.
//
        int I;
        int OutputLength = SecondArgumentValue.length;
        double[] Value = new double[OutputLength];
        if (FunctionToken.equals("If")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue >= 0) ? SecondArgumentValue[I] : ThirdArgumentValue;
            }
        } else if (FunctionToken.equals("Sequence")) {
//	The output vector's elements are (ThirdArgumentValue, ThirdArgumentValue +
// 	SecondArgumentValue[0], ThirdArgumentValue + SecondArgumentValue[0] +
//	SecondArgumentValue[1], ..., ThirdArgumentValue + Sum(SecondArgumentValue)
//	- SecondArgumentValue[last].  The output length equals one more than that
//	of the input vector of intervals (SecondArgumentValue).
//	The value of FirstArgumentValue is ignored.
            OutputLength = SecondArgumentValue.length + 1;
            Value = new double[OutputLength];
            Value[0] = ThirdArgumentValue;
            for (I = 1; I < OutputLength; I++) {
                Value[I] = Value[I - 1] + SecondArgumentValue[I - 1];
            }
        } else if (FunctionToken.equals("Radians")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue + SecondArgumentValue[I]) * 60. + ThirdArgumentValue);
            }
        } else if (FunctionToken.equals("Degrees")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue + (SecondArgumentValue[I] + ThirdArgumentValue / 60.) / 60.);
            }
        } else if (FunctionToken.equals("Grads")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = ((60. * FirstArgumentValue + SecondArgumentValue[I]) * 60. + ThirdArgumentValue) / 12960.;
            }
        } else if (FunctionToken.equals("AstroAngle")) {
            double Coeff = inverseAngleScale * Math.PI / 43200.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue + SecondArgumentValue[I]) * 60. + ThirdArgumentValue);
            }
        } else if (FunctionToken.equals("Sin")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math
                        .sin(Coeff * ((60. * FirstArgumentValue + SecondArgumentValue[I]) * 60. + ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Cos")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math
                        .cos(Coeff * ((60. * FirstArgumentValue + SecondArgumentValue[I]) * 60. + ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Tan")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math
                        .tan(Coeff * ((60. * FirstArgumentValue + SecondArgumentValue[I]) * 60. + ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Max")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.max(FirstArgumentValue, Math.max(SecondArgumentValue[I], ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Min")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.min(FirstArgumentValue, Math.min(SecondArgumentValue[I], ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Weekday")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, continuity, val;
            dayNumber = (int) Math.round(FirstArgumentValue);
            year = (int) Math.round(ThirdArgumentValue);
            for (I = 0; I < OutputLength; I++) {
                month = (int) Math.round(SecondArgumentValue[I]);
                if (month > 12) {
                    userInterface.println(
                            "You have entered too large a month.  The first argument should be the day, then the month.");
                    Value = new double[1];
                    Value[0] = 0.;
                    return Value;
                }
                mprime = Math.IEEEremainder(9 + month, 12);
                mprime = (mprime >= 0) ? mprime : mprime + 12;
                mprimecomb = Math.floor((13 * mprime + 2) / 5);
                yprime = year - Math.floor(mprime / 10);
                if (yprime < 0) {
                    yprime = yprime + 400 * (1 - Math.floor(yprime / 400));
                }
                yprimecomb = yprime + Math.floor(yprime / 4) - Math.floor(yprime / 100) + Math.floor(yprime / 400);
                continuity = 2 + mprimecomb + yprimecomb;
                val = Math.IEEEremainder(dayNumber + continuity, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else if (FunctionToken.equals("WeekdayJulian")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, continuity, val;
            dayNumber = (int) Math.round(FirstArgumentValue);
            year = (int) Math.round(ThirdArgumentValue);
            for (I = 0; I < OutputLength; I++) {
                month = (int) Math.round(SecondArgumentValue[I]);
                if (month > 12) {
                    userInterface.println(
                            "You have entered too large a month.  The first argument should be the day, then the month.");
                    Value = new double[1];
                    Value[0] = 0.;
                    return Value;
                }
                mprime = Math.IEEEremainder(9 + month, 12);
                mprime = (mprime >= 0) ? mprime : mprime + 12;
                mprimecomb = Math.floor((13 * mprime + 2) / 5);
                yprime = year - Math.floor(mprime / 10);
                if (yprime < 0) {
                    yprime = yprime + 28 * (1 - Math.floor(yprime / 28));
                }
                yprimecomb = yprime + Math.floor(yprime / 4);
                continuity = 2 + mprimecomb + yprimecomb;
                val = Math.IEEEremainder(dayNumber + continuity, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of one scalar, one vector, and a further scalar variable.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }

    public double[] EvaluateVectorFunctionOfThreeVariables(String FunctionToken, double[] FirstArgumentValue,
                                                           double[] SecondArgumentValue, double ThirdArgumentValue) {
//
// Returns the value of a vector-valued function of two vector and one scalar-valued variable.
// The functions evaluated here are:
//
// If, Sequence, Radians, Degrees, Grads, Max, Min, AstroAngle,
// Sin, Cos, Tan, Weekday, WeekdayJulian
//
// The length of the vector always comes from the first argument.
//
        int I;
        int OutputLength = FirstArgumentValue.length;
        double Value[] = new double[OutputLength];
        if ((SecondArgumentValue.length != OutputLength) && !(FunctionToken.equals("Sequence"))) {
            userInterface
                    .println("Error: In " + FunctionToken + ", the two sequence arguments must have the same length.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
            return Value;
        }
        if (FunctionToken.equals("If")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue[I] >= 0) ? SecondArgumentValue[I] : ThirdArgumentValue;
            }
        } else if (FunctionToken.equals("Sequence")) {
//	The output vector's elements are (ThirdArgumentValue, ThirdArgumentValue +
// 	SecondArgumentValue[0], ThirdArgumentValue + SecondArgumentValue[0] +
//	SecondArgumentValue[1], ..., ThirdArgumentValue + Sum(SecondArgumentValue)
//	- SecondArgumentValue[last].  The output length equals that of the input vector
//	of intervals (SecondArgumentValue), so the last value of that vector is not used.
//	The value of FirstArgumentValue is ignored.
            OutputLength = SecondArgumentValue.length + 1;
            Value = new double[OutputLength];
            Value[0] = ThirdArgumentValue;
            for (I = 1; I < OutputLength; I++) {
                Value[I] = Value[I - 1] + SecondArgumentValue[I - 1];
            }
        } else if (FunctionToken.equals("Radians")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue[I]) * 60. + ThirdArgumentValue);
            }
        } else if (FunctionToken.equals("Degrees")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue[I] + (SecondArgumentValue[I] + ThirdArgumentValue / 60.) / 60.);
            }
        } else if (FunctionToken.equals("Grads")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = ((60. * FirstArgumentValue[I] + SecondArgumentValue[I]) * 60. + ThirdArgumentValue) / 12960.;
            }
        } else if (FunctionToken.equals("AstroAngle")) {
            double Coeff = inverseAngleScale * Math.PI / 43200.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue[I]) * 60. + ThirdArgumentValue);
            }
        } else if (FunctionToken.equals("Sin")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.sin(Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue[I]) * 60.
                        + ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Cos")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.cos(Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue[I]) * 60.
                        + ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Tan")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.tan(Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue[I]) * 60.
                        + ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Max")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.max(FirstArgumentValue[I], Math.max(SecondArgumentValue[I], ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Min")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.min(FirstArgumentValue[I], Math.min(SecondArgumentValue[I], ThirdArgumentValue));
            }
        } else if (FunctionToken.equals("Weekday")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, continuity, val;
            year = (int) Math.round(ThirdArgumentValue);
            for (I = 0; I < OutputLength; I++) {
                dayNumber = (int) Math.round(FirstArgumentValue[I]);
                month = (int) Math.round(SecondArgumentValue[I]);
                if (month > 12) {
                    userInterface.println(
                            "You have entered too large a month.  The first argument should be the day, then the month.");
                    Value = new double[1];
                    Value[0] = 0.;
                    return Value;
                }
                mprime = Math.IEEEremainder(9 + month, 12);
                mprime = (mprime >= 0) ? mprime : mprime + 12;
                mprimecomb = Math.floor((13 * mprime + 2) / 5);
                yprime = year - Math.floor(mprime / 10);
                if (yprime < 0) {
                    yprime = yprime + 400 * (1 - Math.floor(yprime / 400));
                }
                yprimecomb = yprime + Math.floor(yprime / 4) - Math.floor(yprime / 100) + Math.floor(yprime / 400);
                continuity = 2 + mprimecomb + yprimecomb;
                val = Math.IEEEremainder(dayNumber + continuity, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else if (FunctionToken.equals("WeekdayJulian")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, continuity, val;
            year = (int) Math.round(ThirdArgumentValue);
            for (I = 0; I < OutputLength; I++) {
                dayNumber = (int) Math.round(FirstArgumentValue[I]);
                month = (int) Math.round(SecondArgumentValue[I]);
                if (month > 12) {
                    userInterface.println(
                            "You have entered too large a month.  The first argument should be the day, then the month.");
                    Value = new double[1];
                    Value[0] = 0.;
                    return Value;
                }
                mprime = Math.IEEEremainder(9 + month, 12);
                mprime = (mprime >= 0) ? mprime : mprime + 12;
                mprimecomb = Math.floor((13 * mprime + 2) / 5);
                yprime = year - Math.floor(mprime / 10);
                if (yprime < 0) {
                    yprime = yprime + 28 * (1 - Math.floor(yprime / 28));
                }
                yprimecomb = yprime + Math.floor(yprime / 4);
                continuity = 2 + mprimecomb + yprimecomb;
                val = Math.IEEEremainder(dayNumber + continuity, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of two vector and one scalar variable.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }

    public double[] EvaluateVectorFunctionOfThreeVariables(String FunctionToken, double FirstArgumentValue,
                                                           double SecondArgumentValue, double[] ThirdArgumentValue) {
//
// Returns the value of a vector-valued function of two scalar-valued and one vector variable.
// The functions evaluated here are:
//
// If, Radians, Degrees, Grads , Max, Min, AstroAngle, Sin, Cos,
// Tan, Weekday, WeekdayJulian
//
// The length of the vector always comes from the third argument.
//
        int I;
        int OutputLength = ThirdArgumentValue.length;
        double Value[] = new double[OutputLength];
        if (FunctionToken.equals("If")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue >= 0) ? SecondArgumentValue : ThirdArgumentValue[I];
            }
        } else if (FunctionToken.equals("Radians")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue + SecondArgumentValue) * 60. + ThirdArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Degrees")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue + (SecondArgumentValue + ThirdArgumentValue[I] / 60.) / 60.);
            }
        } else if (FunctionToken.equals("Grads")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = ((60. * FirstArgumentValue + SecondArgumentValue) * 60. + ThirdArgumentValue[I]) / 12960.;
            }
        } else if (FunctionToken.equals("AstroAngle")) {
            double Coeff = inverseAngleScale * Math.PI / 43200.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue + SecondArgumentValue) * 60. + ThirdArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Sin")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math
                        .sin(Coeff * ((60. * FirstArgumentValue + SecondArgumentValue) * 60. + ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Cos")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math
                        .cos(Coeff * ((60. * FirstArgumentValue + SecondArgumentValue) * 60. + ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Tan")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math
                        .tan(Coeff * ((60. * FirstArgumentValue + SecondArgumentValue) * 60. + ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Max")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.max(FirstArgumentValue, Math.max(SecondArgumentValue, ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Min")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.min(FirstArgumentValue, Math.min(SecondArgumentValue, ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Weekday")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, continuity, val;
            dayNumber = (int) Math.round(FirstArgumentValue);
            month = (int) Math.round(SecondArgumentValue);
            if (month > 12) {
                userInterface.println(
                        "You have entered too large a month.  The first argument should be the day, then the month.");
                Value = new double[1];
                Value[0] = 0.;
                return Value;
            }
            mprime = Math.IEEEremainder(9 + month, 12);
            mprime = (mprime >= 0) ? mprime : mprime + 12;
            mprimecomb = Math.floor((13 * mprime + 2) / 5);
            continuity = mprimecomb + 2 + dayNumber;
            for (I = 0; I < OutputLength; I++) {
                year = (int) Math.round(ThirdArgumentValue[I]);
                yprime = year - Math.floor(mprime / 10);
                if (yprime < 0) {
                    yprime = yprime + 400 * (1 - Math.floor(yprime / 400));
                }
                yprimecomb = yprime + Math.floor(yprime / 4) - Math.floor(yprime / 100) + Math.floor(yprime / 400);
                val = Math.IEEEremainder(yprimecomb + continuity, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else if (FunctionToken.equals("WeekdayJulian")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, continuity, val;
            dayNumber = (int) Math.round(FirstArgumentValue);
            month = (int) Math.round(SecondArgumentValue);
            if (month > 12) {
                userInterface.println(
                        "You have entered too large a month.  The first argument should be the day, then the month.");
                Value = new double[1];
                Value[0] = 0.;
                return Value;
            }
            mprime = Math.IEEEremainder(9 + month, 12);
            mprime = (mprime >= 0) ? mprime : mprime + 12;
            mprimecomb = Math.floor((13 * mprime + 2) / 5);
            continuity = mprimecomb + 2 + dayNumber;
            for (I = 0; I < OutputLength; I++) {
                year = (int) Math.round(ThirdArgumentValue[I]);
                yprime = year - Math.floor(mprime / 10);
                if (yprime < 0) {
                    yprime = yprime + 28 * (1 - Math.floor(yprime / 28));
                }
                yprimecomb = yprime + Math.floor(yprime / 4);
                val = Math.IEEEremainder(yprimecomb + continuity, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of two scalar and one vector variable.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }

    public double[] EvaluateVectorFunctionOfThreeVariables(String FunctionToken, double[] FirstArgumentValue,
                                                           double SecondArgumentValue, double[] ThirdArgumentValue) {
//
// Returns the value of a vector-valued function of one vector, one scalar, and one further
// vector variable.
// The functions evaluated here are:
//
// If, Radians, Degrees, Grads, Max, Min, AstroAngle, Sin, Cos,
// Tan, Weekday, WeekdayJulian
//
// The length of the vector always comes from the first argument.
//
        int I;
        int OutputLength = FirstArgumentValue.length;
        double Value[] = new double[OutputLength];
        if (ThirdArgumentValue.length != OutputLength) {
            userInterface
                    .println("Error: In " + FunctionToken + ", the two sequence arguments must have the same length.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
            return Value;
        }
        if (FunctionToken.equals("If")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue[I] >= 0) ? SecondArgumentValue : ThirdArgumentValue[I];
            }
        } else if (FunctionToken.equals("Radians")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue) * 60. + ThirdArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Degrees")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue[I] + (SecondArgumentValue + ThirdArgumentValue[I] / 60.) / 60.);
            }
        } else if (FunctionToken.equals("Grads")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = ((60. * FirstArgumentValue[I] + SecondArgumentValue) * 60. + ThirdArgumentValue[I]) / 12960.;
            }
        } else if (FunctionToken.equals("AstroAngle")) {
            double Coeff = inverseAngleScale * Math.PI / 43200.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue) * 60. + ThirdArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Sin")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.sin(Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue) * 60.
                        + ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Cos")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.cos(Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue) * 60.
                        + ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Tan")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.tan(Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue) * 60.
                        + ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Max")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.max(FirstArgumentValue[I], Math.max(SecondArgumentValue, ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Min")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.min(FirstArgumentValue[I], Math.min(SecondArgumentValue, ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Weekday")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, continuity, val;
            month = (int) Math.round(SecondArgumentValue);
            if (month > 12) {
                userInterface.println(
                        "You have entered too large a month.  The first argument should be the day, then the month.");
                Value = new double[1];
                Value[0] = 0.;
                return Value;
            }
            mprime = Math.IEEEremainder(9 + month, 12);
            mprime = (mprime >= 0) ? mprime : mprime + 12;
            mprimecomb = Math.floor((13 * mprime + 2) / 5);
            continuity = mprimecomb + 2;
            for (I = 0; I < OutputLength; I++) {
                dayNumber = (int) Math.round(FirstArgumentValue[I]);
                year = (int) Math.round(ThirdArgumentValue[I]);
                yprime = year - Math.floor(mprime / 10);
                if (yprime < 0) {
                    yprime = yprime + 400 * (1 - Math.floor(yprime / 400));
                }
                yprimecomb = yprime + Math.floor(yprime / 4) - Math.floor(yprime / 100) + Math.floor(yprime / 400);
                val = Math.IEEEremainder(yprimecomb + continuity + dayNumber, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else if (FunctionToken.equals("WeekdayJulian")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, continuity, val;
            month = (int) Math.round(SecondArgumentValue);
            if (month > 12) {
                userInterface.println(
                        "You have entered too large a month.  The first argument should be the day, then the month.");
                Value = new double[1];
                Value[0] = 0.;
                return Value;
            }
            mprime = Math.IEEEremainder(9 + month, 12);
            mprime = (mprime >= 0) ? mprime : mprime + 12;
            mprimecomb = Math.floor((13 * mprime + 2) / 5);
            continuity = mprimecomb + 2;
            for (I = 0; I < OutputLength; I++) {
                dayNumber = (int) Math.round(FirstArgumentValue[I]);
                year = (int) Math.round(ThirdArgumentValue[I]);
                yprime = year - Math.floor(mprime / 10);
                if (yprime < 0) {
                    yprime = yprime + 28 * (1 - Math.floor(yprime / 28));
                }
                yprimecomb = yprime + Math.floor(yprime / 4);
                val = Math.IEEEremainder(yprimecomb + continuity + dayNumber, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of one vector, one scalar, and one further vector variable.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }

    public double[] EvaluateVectorFunctionOfThreeVariables(String FunctionToken, double FirstArgumentValue,
                                                           double[] SecondArgumentValue, double[] ThirdArgumentValue) {
//
// Returns the value of a vector-valued function of one scalar and two vector variables.
// The functions evaluated here are:
//
// If, Radians, Degrees, Grads, Max, Min, AstroAngle, Sin, Cos,
// Tan, Weekday, WeekdayJulian
//
// The length of the vector always comes from the second argument.
//
        int I;
        int OutputLength = SecondArgumentValue.length;
        double Value[] = new double[OutputLength];
        if (ThirdArgumentValue.length != OutputLength) {
            userInterface
                    .println("Error: In " + FunctionToken + ", the two sequence arguments must have the same length.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
            return Value;
        }
        if (FunctionToken.equals("If")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue >= 0) ? SecondArgumentValue[I] : ThirdArgumentValue[I];
            }
        } else if (FunctionToken.equals("Radians")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue + SecondArgumentValue[I]) * 60. + ThirdArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Degrees")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue + (SecondArgumentValue[I] + ThirdArgumentValue[I] / 60.) / 60.);
            }
        } else if (FunctionToken.equals("Grads")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = ((60. * FirstArgumentValue + SecondArgumentValue[I]) * 60. + ThirdArgumentValue[I]) / 12960.;
            }
        } else if (FunctionToken.equals("AstroAngle")) {
            double Coeff = inverseAngleScale * Math.PI / 43200.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue + SecondArgumentValue[I]) * 60. + ThirdArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Sin")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.sin(Coeff * ((60. * FirstArgumentValue + SecondArgumentValue[I]) * 60.
                        + ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Cos")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.cos(Coeff * ((60. * FirstArgumentValue + SecondArgumentValue[I]) * 60.
                        + ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Tan")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.tan(Coeff * ((60. * FirstArgumentValue + SecondArgumentValue[I]) * 60.
                        + ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Max")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.max(FirstArgumentValue, Math.max(SecondArgumentValue[I], ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Min")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.min(FirstArgumentValue, Math.min(SecondArgumentValue[I], ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Weekday")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, continuity, val;
            dayNumber = (int) Math.round(FirstArgumentValue);
            continuity = dayNumber + 2;
            for (I = 0; I < OutputLength; I++) {
                month = (int) Math.round(SecondArgumentValue[I]);
                if (month > 12) {
                    userInterface.println(
                            "You have entered too large a month.  The first argument should be the day, then the month.");
                    Value = new double[1];
                    Value[0] = 0.;
                    return Value;
                }
                mprime = Math.IEEEremainder(9 + month, 12);
                mprime = (mprime >= 0) ? mprime : mprime + 12;
                mprimecomb = Math.floor((13 * mprime + 2) / 5);
                year = (int) Math.round(ThirdArgumentValue[I]);
                yprime = year - Math.floor(mprime / 10);
                if (yprime < 0) {
                    yprime = yprime + 400 * (1 - Math.floor(yprime / 400));
                }
                yprimecomb = yprime + Math.floor(yprime / 4) - Math.floor(yprime / 100) + Math.floor(yprime / 400);
                val = Math.IEEEremainder(yprimecomb + mprimecomb + continuity, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else if (FunctionToken.equals("WeekdayJulian")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, continuity, val;
            dayNumber = (int) Math.round(FirstArgumentValue);
            continuity = dayNumber + 2;
            for (I = 0; I < OutputLength; I++) {
                month = (int) Math.round(SecondArgumentValue[I]);
                if (month > 12) {
                    userInterface.println(
                            "You have entered too large a month.  The first argument should be the day, then the month.");
                    Value = new double[1];
                    Value[0] = 0.;
                    return Value;
                }
                mprime = Math.IEEEremainder(9 + month, 12);
                mprime = (mprime >= 0) ? mprime : mprime + 12;
                mprimecomb = Math.floor((13 * mprime + 2) / 5);
                year = (int) Math.round(ThirdArgumentValue[I]);
                yprime = year - Math.floor(mprime / 10);
                if (yprime < 0) {
                    yprime = yprime + 28 * (1 - Math.floor(yprime / 28));
                }
                yprimecomb = yprime + Math.floor(yprime / 4);
                val = Math.IEEEremainder(yprimecomb + mprimecomb + continuity, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of one scalar and two vector variables.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }

    public double[] EvaluateVectorFunctionOfThreeVariables(String FunctionToken, double[] FirstArgumentValue,
                                                           double[] SecondArgumentValue, double[] ThirdArgumentValue) {
//
// Returns the value of a vector-valued function of three vector-valued variables.
// The functions evaluated here are:
//
// If, Radians, Degrees, Grads, Max, Min, AstroAngle, Sin, Cos,
// Tan, Weekday, WeekdayJulian
//
// The length of the vector always comes from the first argument.
//
        int I;
        int OutputLength = FirstArgumentValue.length;
        double Value[] = new double[OutputLength];
        if ((SecondArgumentValue.length != OutputLength) || (ThirdArgumentValue.length != OutputLength)) {
            userInterface.println(
                    "Error: In " + FunctionToken + ", the three sequence arguments must have the same length.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
            return Value;
        }
        if (FunctionToken.equals("If")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue[I] >= 0) ? SecondArgumentValue[I] : ThirdArgumentValue[I];
            }
        } else if (FunctionToken.equals("Radians")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue[I]) * 60.
                        + ThirdArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Degrees")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = (FirstArgumentValue[I] + (SecondArgumentValue[I] + ThirdArgumentValue[I] / 60.) / 60.);
            }
        } else if (FunctionToken.equals("Grads")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = ((60. * FirstArgumentValue[I] + SecondArgumentValue[I]) * 60. + ThirdArgumentValue[I])
                        / 12960.;
            }
        } else if (FunctionToken.equals("AstroAngle")) {
            double Coeff = inverseAngleScale * Math.PI / 43200.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue[I]) * 60.
                        + ThirdArgumentValue[I]);
            }
        } else if (FunctionToken.equals("Sin")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.sin(Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue[I]) * 60.
                        + ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Cos")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.cos(Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue[I]) * 60.
                        + ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Tan")) {
            double Coeff = Math.PI / 648000.;
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.tan(Coeff * ((60. * FirstArgumentValue[I] + SecondArgumentValue[I]) * 60.
                        + ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Max")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.max(FirstArgumentValue[I], Math.max(SecondArgumentValue[I], ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Min")) {
            for (I = 0; I < OutputLength; I++) {
                Value[I] = Math.min(FirstArgumentValue[I], Math.min(SecondArgumentValue[I], ThirdArgumentValue[I]));
            }
        } else if (FunctionToken.equals("Weekday")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, val;
            for (I = 0; I < OutputLength; I++) {
                dayNumber = (int) Math.round(FirstArgumentValue[I]);
                month = (int) Math.round(SecondArgumentValue[I]);
                if (month > 12) {
                    userInterface.println(
                            "You have entered too large a month.  The first argument should be the day, then the month.");
                    Value = new double[1];
                    Value[0] = 0.;
                    return Value;
                }
                mprime = Math.IEEEremainder(9 + month, 12);
                mprime = (mprime >= 0) ? mprime : mprime + 12;
                mprimecomb = Math.floor((13 * mprime + 2) / 5);
                year = (int) Math.round(ThirdArgumentValue[I]);
                yprime = year - Math.floor(mprime / 10);
                if (yprime < 0) {
                    yprime = yprime + 400 * (1 - Math.floor(yprime / 400));
                }
                yprimecomb = yprime + Math.floor(yprime / 4) - Math.floor(yprime / 100) + Math.floor(yprime / 400);
                val = Math.IEEEremainder(yprimecomb + mprimecomb + dayNumber + 2, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else if (FunctionToken.equals("WeekdayJulian")) {
            int dayNumber, month, year;
            double mprime, yprime, mprimecomb, yprimecomb, val;
            for (I = 0; I < OutputLength; I++) {
                dayNumber = (int) Math.round(FirstArgumentValue[I]);
                month = (int) Math.round(SecondArgumentValue[I]);
                if (month > 12) {
                    userInterface.println(
                            "You have entered too large a month.  The first argument should be the day, then the month.");
                    Value = new double[1];
                    Value[0] = 0.;
                    return Value;
                }
                mprime = Math.IEEEremainder(9 + month, 12);
                mprime = (mprime >= 0) ? mprime : mprime + 12;
                mprimecomb = Math.floor((13 * mprime + 2) / 5);
                year = (int) Math.round(ThirdArgumentValue[I]);
                yprime = year - Math.floor(mprime / 10);
                if (yprime < 0) {
                    yprime = yprime + 28 * (1 - Math.floor(yprime / 28));
                }
                yprimecomb = yprime + Math.floor(yprime / 4);
                val = Math.IEEEremainder(yprimecomb + mprimecomb + dayNumber + 2, 7);
                Value[I] = 1 + ((val >= 0) ? val : val + 7);
            }
        } else {
            userInterface.println("Error: Function " + FunctionToken
                    + " is not a known vector-valued function of three vector variables.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
        }
        return Value;
    }


    public double EvaluateScalarBinaryOperator(String BinaryOperatorToken, double FirstArgumentValue,
                                               double SecondArgumentValue) throws ComputeExpressionException {
        double Value = 0.0;
        char Operator = BinaryOperatorToken.charAt(0);
        switch (Operator) {
            case '+':
                Value = FirstArgumentValue + SecondArgumentValue;
                break;
            case '-':
                Value = FirstArgumentValue - SecondArgumentValue;
                break;
            case '*':
                Value = FirstArgumentValue * SecondArgumentValue;
                break;
            case '/':
                Value = FirstArgumentValue / SecondArgumentValue;
                break;
            case '^':
                Value = Math.pow(FirstArgumentValue, SecondArgumentValue);
                break;
            default:
                throw new ComputeExpressionException(
                        "binary operator \"" + BinaryOperatorToken + "\" is not recognized.");
        }
        return Value;
    }


    public double[] EvaluateVectorBinaryOperator(String BinaryOperatorToken, double[] FirstArgumentValue,
                                                 double SecondArgumentValue) throws ComputeExpressionException {
        int I;
        int OutputLength = FirstArgumentValue.length;
        double Value[] = new double[OutputLength];
        char Operator = BinaryOperatorToken.charAt(0);
        switch (Operator) {
            case '+':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue[I] + SecondArgumentValue;
                }
                break;
            case '-':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue[I] - SecondArgumentValue;
                }
                break;
            case '*':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue[I] * SecondArgumentValue;
                }
                break;
            case '/':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue[I] / SecondArgumentValue;
                }
                break;
            case '^':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = Math.pow(FirstArgumentValue[I], SecondArgumentValue);
                }
                break;
            default:
                throw new ComputeExpressionException(
                        "binary operator \"" + BinaryOperatorToken + "\" is not recognized.");
        }
        return Value;
    }


    public double[] EvaluateVectorBinaryOperator(String BinaryOperatorToken, double FirstArgumentValue,
                                                 double[] SecondArgumentValue) throws ComputeExpressionException {
        int I;
        int OutputLength = SecondArgumentValue.length;
        double Value[] = new double[OutputLength];
        char Operator = BinaryOperatorToken.charAt(0);
        switch (Operator) {
            case '+':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue + SecondArgumentValue[I];
                }
                break;
            case '-':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue - SecondArgumentValue[I];
                }
                break;
            case '*':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue * SecondArgumentValue[I];
                }
                break;
            case '/':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue / SecondArgumentValue[I];
                }
                break;
            case '^':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = Math.pow(FirstArgumentValue, SecondArgumentValue[I]);
                }
                break;
            default:
                throw new ComputeExpressionException(
                        "binary operator \"" + BinaryOperatorToken + "\" is not recognized.");
        }
        return Value;
    }


    public double[] EvaluateVectorBinaryOperator(String BinaryOperatorToken, double[] FirstArgumentValue,
                                                 double[] SecondArgumentValue) throws ComputeExpressionException {
        int I;
        int OutputLength = FirstArgumentValue.length;
        double Value[] = new double[OutputLength];
        char Operator = BinaryOperatorToken.charAt(0);
        if (SecondArgumentValue.length != OutputLength) {
            userInterface.println("Error: In binary operation " + BinaryOperatorToken
                    + ", the two sequence operands must have the same length.");
            for (I = 0; I < OutputLength; I++) {
                Value[I] = 0;
            }
            return Value;
        }
        switch (Operator) {
            case '+':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue[I] + SecondArgumentValue[I];
                }
                break;
            case '-':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue[I] - SecondArgumentValue[I];
                }
                break;
            case '*':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue[I] * SecondArgumentValue[I];
                }
                break;
            case '/':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = FirstArgumentValue[I] / SecondArgumentValue[I];
                }
                break;
            case '^':
                for (I = 0; I < OutputLength; I++) {
                    Value[I] = Math.pow(FirstArgumentValue[I], SecondArgumentValue[I]);
                }
                break;
            default:
                throw new ComputeExpressionException(
                        "binary operator \"" + BinaryOperatorToken + "\" is not recognized.");
        }
        return Value;
    }


    public int GetUnaryOperator(String d) throws ComputeExpressionException {
        int GetU = 0;
        int I;
        for (I = 1; I <= NumberOfUnaryOperators; I++) {
            if (d.equals(UnaryNames[I])) {
                GetU = I;
                break;
            }
        }
        if (I > NumberOfUnaryOperators) {
            throw new ComputeExpressionException("unary operator \"" + d + "\" is not recognized.");
        }
        return GetU;
    }

    public void DisplayString() {
        if (displayProgress) {
            for (int I = 1; I <= NumberOfTokens; I++) {
                diagnosticPrint(Token[I]);
            }
            diagnosticPrint("\n");
        }
        return;
    }

    public void SurroundFunctionWithBrackets(int I) throws ComputeSyntaxException {
        int LastPosition = OtherEndOfArgument(I + 1);
        if ((I == 1) || (LastPosition == NumberOfTokens) || (Properties[I - 1] != OpenBracket)
                || (Properties[LastPosition + 1] != CloseBracket) || (Level[I - 1] != Level[LastPosition + 1])) {
            InsertTokenAfter(LastPosition, ")", CloseBracket, (byte) 0, false, false);
            InsertTokenBefore(I, "(", OpenBracket, (byte) 0, false, false);
        }
        return;
    }

    public int FindPairedBracket(int I, int LowerLimit, int UpperLimit) throws ComputeSyntaxException {
        int J = 0;
        int LevelOfPair = Level[I];
        if (Properties[I] == OpenBracket) {
            J = I + 1;
            while ((J <= UpperLimit) && ((Properties[J] != CloseBracket) || (Level[J] != LevelOfPair))) {
                J++;
                if (J > UpperLimit) {
                    throw new ComputeSyntaxException(Expression.substring(LowerLimit, I), Token[I],
                            Expression.substring(I + 1, UpperLimit + 1),
                            "Open round bracket is not paired properly with a closed bracket.");
                }
            }
        } else if (Properties[I] == CloseBracket) {
            J = I - 1;
            while ((J >= LowerLimit) && ((Properties[J] != OpenBracket) || (Level[J] != LevelOfPair))) {
                J--;
                if (J < LowerLimit) {
                    throw new ComputeSyntaxException(Expression.substring(LowerLimit, I), Token[I],
                            Expression.substring(I + 1, UpperLimit + 1),
                            "Closed round bracket is not paired properly with an open bracket.");
                }
            }
        } else {
            userInterface.println("Token passed to FindPairedBracket was not a bracket.  I = " + String.valueOf(I));
        }
        return J;
    }


    public int FindPairedSquareBracket(int I) throws ComputeSyntaxException {
        int J = 0;
        int LevelOfPair = Level[I];
        if (Properties[I] == OpenSquareBracket) {
            J = I + 1;
            while ((J <= NumberOfTokens) && ((Properties[J] != CloseSquareBracket) || (Level[J] != LevelOfPair))) {
                J++;
                if (J > NumberOfTokens) {
                    throw new ComputeSyntaxException(Expression.substring(0, I), Token[I], Expression.substring(I + 1),
                            "Open square bracket is not paired properly with a closed bracket.");
                }
            }
        } else if (Properties[I] == CloseSquareBracket) {
            J = I - 1;
            while ((J > 0) && ((Properties[J] != OpenSquareBracket) || (Level[J] != LevelOfPair))) {
                J--;
                if (J < 1) {
                    throw new ComputeSyntaxException(Expression.substring(0, I), Token[I], Expression.substring(I + 1),
                            "Closed square bracket is not paired properly with an open bracket.");
                }
            }
        } else {
            userInterface.println(
                    "Token passed to FindPairedSquareBracket was not a square bracket.  Token was a position " + String
                            .valueOf(I));
        }
        return J;
    }

    public byte TypeForLoop(int FirstToken, int LastToken) throws ComputeExpressionException {
        //
        // This function returns the type (SCALAR or VECTOR) of the evaluation
        // of a "for" loop.  This is the type of the second argument: if it is
        // a scalar then the loop returns a scalar, and if it is
        // a vector then the loop returns a vector of the same length.
        // The function also types the third argument
        // and checks that it is a scalar.  An error results if it is not.
        // It also checks that the first argument is a TemporaryVariable.
        // The token "for" and the opening and closing bracket delimiters are
        // not part of the list: FirstToken is the first thing inside the brackets.
        //
        byte Out = SCALAR;
        byte SecondArgumentType, ThirdArgumentType;
        int FirstArgumentToken, SecondArgumentToken, ThirdArgumentToken;
        int EndArgumentToken, InsideFirstToken, InsideLastToken;
        FirstArgumentToken = FirstToken;
        if (displayProgress) {
            diagnosticPrintln("Type for loop: first argument = " + Token[FirstToken]);
        }
        if (Properties[FirstArgumentToken] != TemporaryVariable) {
            throwSyntaxException(FirstArgumentToken,
                    "The first argument of the function \"for\" must be a temporary variable name beginning with @.");
        }
        SecondArgumentToken = FirstArgumentToken + 2;
        EndArgumentToken = SecondArgumentToken;
        if (displayProgress) {
            diagnosticPrintln("Type for loop: second argument begins with " + Token[SecondArgumentToken]);
        }
        switch (Properties[SecondArgumentToken]) {
            case Number:
            case ConstantScalar:
            case VariableScalar:
            case TemporaryVariable:
                Out = SCALAR;
                break;
            case ConstantVector:
            case VariableVector:
                Out = VECTOR;
                break;
            case OpenBracket:
                EndArgumentToken = FindPairedBracket(SecondArgumentToken, FirstToken, LastToken);
                InsideFirstToken = SecondArgumentToken + 1;
                InsideLastToken = EndArgumentToken - 1;
                SecondArgumentType = TypeBracketPair(InsideFirstToken, InsideLastToken);
                Type[SecondArgumentToken] = SecondArgumentType;
                Type[EndArgumentToken] = SecondArgumentType;
                Out = SecondArgumentType;
                break;
        }
        ThirdArgumentToken = EndArgumentToken + 2;
        if (displayProgress) {
            diagnosticPrintln("Type for loop: third argument begins with " + Token[ThirdArgumentToken]);
        }
        switch (Properties[ThirdArgumentToken]) {
            case Number:
            case ConstantScalar:
            case VariableScalar:
            case TemporaryVariable:
                break;
            case ConstantVector:
            case VariableVector:
                throw new ComputeExpressionException(
                        "The third argument of this function \"" + remakeExpression(FirstToken - 2, LastToken + 1)
                                + "\" must not evaluate to a sequence.");
            case ForLoop:
                EndArgumentToken = FindPairedBracket(ThirdArgumentToken + 1, FirstToken, LastToken);
                InsideFirstToken = ThirdArgumentToken + 2;
                InsideLastToken = EndArgumentToken - 1;
                ThirdArgumentType = TypeForLoop(InsideFirstToken, InsideLastToken);
                if (ThirdArgumentType == VECTOR) {
                    throw new ComputeExpressionException(
                            "The third argument of this function \"" + remakeExpression(FirstToken - 2, LastToken + 1)
                                    + "\" is another \"for\" that evaluates to a sequence.  Third arguments of \"for\" cannot be sequences.");
                }
                Type[ThirdArgumentToken] = ThirdArgumentType;
                Type[EndArgumentToken] = ThirdArgumentType;
                break;
            case OpenBracket:
                EndArgumentToken = FindPairedBracket(ThirdArgumentToken, FirstToken, LastToken);
                InsideFirstToken = ThirdArgumentToken + 1;
                InsideLastToken = EndArgumentToken - 1;
                ThirdArgumentType = TypeBracketPair(InsideFirstToken, InsideLastToken);
                if (ThirdArgumentType == VECTOR) {
                    throw new ComputeExpressionException(
                            "The third argument of this function \"" + remakeExpression(FirstToken - 2, LastToken + 1)
                                    + "\" is an expression that evaluates to a sequence.  Third arguments of \"for\" cannot be sequences.");
                }
                Type[ThirdArgumentToken] = ThirdArgumentType;
                Type[EndArgumentToken] = ThirdArgumentType;
                break;
        }
        return Out;
    }

    public String remakeExpression(int startToken, int endToken) {
        String fragment;
        if (Token[startToken].equals("[")) {
            fragment = "(";
        } else if (Token[startToken].equals("]")) {
            fragment = ")";
        } else {
            fragment = Token[startToken];
        }
        if (endToken > startToken) {
            for (int k = startToken + 1; k <= endToken; k++) {
                if (Token[k].equals("[")) {
                    fragment = fragment + "(";
                } else if (Token[k].equals("]")) {
                    fragment = fragment + ")";
                } else {
                    fragment = fragment + Token[k];
                }
            }
        }
        return fragment;
    }


    public byte TypeBracketPair(int FirstToken, int LastToken) throws ComputeExpressionException {
//
// This recursive function returns a type (SCALAR or VECTOR) of
// the value returned from the evaluation of this list of
// tokens, which are obtained as the contents of a bracket pair
// (which means that the brackets are not part of the list).  The
// calling program must set the type values of the enclosing
// brackets to the value returned by this function.
//
        byte Out, FType;
        byte ArgumentType, FirstArgumentType, SecondArgumentType, ThirdArgumentType;
        int ArgumentToken, EndArgumentToken, InsideFirstToken, InsideLastToken;
        int FirstArgumentToken, SecondArgumentToken, ThirdArgumentToken, OperatorToken;
        boolean moreArguments = true;
        Out = SCALAR;
        ArgumentToken = FirstToken;
        ArgumentType = SCALAR;
        FirstArgumentType = SCALAR;
        SecondArgumentType = SCALAR;
        ThirdArgumentType = SCALAR;
        //
        // If the input list is only one token, it should be a datum whose type is just to
        // be returned.
        //
        if (FirstToken == LastToken) {
            switch (Properties[FirstToken]) {
                case Number:
                case ConstantScalar:
                case VariableScalar:
                    Type[FirstToken] = SCALAR;
                    Out = SCALAR;
                    break;
                case ConstantVector:
                case VariableVector:
                    Type[FirstToken] = VECTOR;
                    Out = VECTOR;
                    break;
            }
        } else {
            switch (Properties[FirstToken]) {
                case FunctionOfOneVariable:
                    FType = (byte) ((Integer) FunctionOneTypeDictionary.get(Token[FirstToken])).intValue();
                    if (displayProgress) {
                        diagnosticPrintln("type of " + Token[FirstToken] + " = " + String.valueOf(FType));
                    }
                    ArgumentToken = FirstToken + 2;
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            ArgumentType = TypeForLoop(InsideFirstToken, InsideLastToken);
                            Type[ArgumentToken] = ArgumentType;
                            Type[ArgumentToken + 1] = ArgumentType;
                            Type[EndArgumentToken] = ArgumentType;
                            Out = ReturnType(ArgumentType, FType, Token[FirstToken], ArgumentToken, EndArgumentToken);
                            if (displayProgress) {
                                diagnosticPrintln("ReturnType for same function with ArgumentType = "
                                        + String.valueOf(ArgumentType) + " is " + String.valueOf(Out));
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            ArgumentType = TypeBracketPair(InsideFirstToken, InsideLastToken);
                            Type[ArgumentToken] = ArgumentType;
                            Type[EndArgumentToken] = ArgumentType;
                            Out = ReturnType(ArgumentType, FType, Token[FirstToken], ArgumentToken, EndArgumentToken);
                            if (displayProgress) {
                                diagnosticPrintln("ReturnType for same function with ArgumentType = "
                                        + String.valueOf(ArgumentType) + " is " + String.valueOf(Out));
                            }
                            break;
                        case Number:
                        case ConstantScalar:
                        case VariableScalar:
                        case TemporaryVariable:
                            Type[ArgumentToken] = SCALAR;
                            Out = ReturnType(SCALAR, FType, Token[FirstToken], ArgumentToken, ArgumentToken);
                            break;
                        case ConstantVector:
                        case VariableVector:
                            Type[ArgumentToken] = VECTOR;
                            Out = ReturnType(VECTOR, FType, Token[FirstToken], ArgumentToken, ArgumentToken);
                            break;
                    }
                    Type[FirstToken + 1] = Out;
                    Type[FirstToken] = Out;
                    Type[LastToken] = Out;
                    break;
                case FunctionOfTwoVariables:
                    FType = (byte) ((Integer) FunctionTwoTypeDictionary.get(Token[FirstToken])).intValue();
                    FirstArgumentToken = FirstToken + 2;
                    EndArgumentToken = FirstArgumentToken;
                    switch (Properties[FirstArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(FirstArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = FirstArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            FirstArgumentType = TypeForLoop(InsideFirstToken, InsideLastToken);
                            Type[FirstArgumentToken] = FirstArgumentType;
                            Type[FirstArgumentToken + 1] = FirstArgumentType;
                            Type[EndArgumentToken] = FirstArgumentType;
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(FirstArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = FirstArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            FirstArgumentType = TypeBracketPair(InsideFirstToken, InsideLastToken);
                            Type[FirstArgumentToken] = FirstArgumentType;
                            Type[EndArgumentToken] = FirstArgumentType;
                            break;
                        case Number:
                        case ConstantScalar:
                        case VariableScalar:
                        case TemporaryVariable:
                            FirstArgumentType = SCALAR;
                            Type[FirstArgumentToken] = SCALAR;
                            break;
                        case ConstantVector:
                        case VariableVector:
                            FirstArgumentType = VECTOR;
                            Type[FirstArgumentToken] = VECTOR;
                            break;
                    }
                    SecondArgumentToken = EndArgumentToken + 2;
                    EndArgumentToken = SecondArgumentToken;
                    switch (Properties[SecondArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(SecondArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = SecondArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            SecondArgumentType = TypeForLoop(InsideFirstToken, InsideLastToken);
                            Type[SecondArgumentToken] = SecondArgumentType;
                            Type[SecondArgumentToken + 1] = SecondArgumentType;
                            Type[EndArgumentToken] = SecondArgumentType;
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(SecondArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = SecondArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            SecondArgumentType = TypeBracketPair(InsideFirstToken, InsideLastToken);
                            Type[SecondArgumentToken] = SecondArgumentType;
                            Type[EndArgumentToken] = SecondArgumentType;
                            break;
                        case Number:
                        case ConstantScalar:
                        case VariableScalar:
                        case TemporaryVariable:
                            SecondArgumentType = SCALAR;
                            Type[SecondArgumentToken] = SCALAR;
                            break;
                        case ConstantVector:
                        case VariableVector:
                            SecondArgumentType = VECTOR;
                            Type[SecondArgumentToken] = VECTOR;
                            break;
                    }
                    if ((FirstArgumentType == SCALAR) && (SecondArgumentType == SCALAR)) {
                        ArgumentType = SCALAR;
                    } else {
                        ArgumentType = VECTOR;
                    }
                    Out = ReturnType(ArgumentType, FType, Token[FirstToken], FirstArgumentToken, EndArgumentToken);
                    Type[FirstToken + 1] = Out;
                    Type[FirstToken] = Out;
                    Type[LastToken] = Out;
                    break;
                case FunctionOfThreeVariables:
                    FType = (byte) ((Integer) FunctionThreeTypeDictionary.get(Token[FirstToken])).intValue();
                    FirstArgumentToken = FirstToken + 2;
                    EndArgumentToken = FirstArgumentToken;
                    switch (Properties[FirstArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(FirstArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = FirstArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            FirstArgumentType = TypeForLoop(InsideFirstToken, InsideLastToken);
                            Type[FirstArgumentToken] = FirstArgumentType;
                            Type[FirstArgumentToken + 1] = FirstArgumentType;
                            Type[EndArgumentToken] = FirstArgumentType;
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(FirstArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = FirstArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            FirstArgumentType = TypeBracketPair(InsideFirstToken, InsideLastToken);
                            Type[FirstArgumentToken] = FirstArgumentType;
                            Type[EndArgumentToken] = FirstArgumentType;
                            break;
                        case Number:
                        case ConstantScalar:
                        case VariableScalar:
                        case TemporaryVariable:
                            FirstArgumentType = SCALAR;
                            Type[FirstArgumentToken] = SCALAR;
                            break;
                        case ConstantVector:
                        case VariableVector:
                            FirstArgumentType = VECTOR;
                            Type[FirstArgumentToken] = VECTOR;
                            break;
                    }
                    SecondArgumentToken = EndArgumentToken + 2;
                    EndArgumentToken = SecondArgumentToken;
                    switch (Properties[SecondArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(SecondArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = SecondArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            SecondArgumentType = TypeForLoop(InsideFirstToken, InsideLastToken);
                            Type[SecondArgumentToken] = SecondArgumentType;
                            Type[SecondArgumentToken + 1] = SecondArgumentType;
                            Type[EndArgumentToken] = SecondArgumentType;
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(SecondArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = SecondArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            SecondArgumentType = TypeBracketPair(InsideFirstToken, InsideLastToken);
                            Type[SecondArgumentToken] = SecondArgumentType;
                            Type[EndArgumentToken] = SecondArgumentType;
                            break;
                        case Number:
                        case ConstantScalar:
                        case VariableScalar:
                        case TemporaryVariable:
                            SecondArgumentType = SCALAR;
                            Type[SecondArgumentToken] = SCALAR;
                            break;
                        case ConstantVector:
                        case VariableVector:
                            SecondArgumentType = VECTOR;
                            Type[SecondArgumentToken] = VECTOR;
                            break;
                    }
                    ThirdArgumentToken = EndArgumentToken + 2;
                    EndArgumentToken = ThirdArgumentToken;
                    switch (Properties[ThirdArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ThirdArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = ThirdArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            ThirdArgumentType = TypeForLoop(InsideFirstToken, InsideLastToken);
                            Type[ThirdArgumentToken] = ThirdArgumentType;
                            Type[ThirdArgumentToken + 1] = ThirdArgumentType;
                            Type[EndArgumentToken] = ThirdArgumentType;
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ThirdArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = ThirdArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            ThirdArgumentType = TypeBracketPair(InsideFirstToken, InsideLastToken);
                            Type[ThirdArgumentToken] = ThirdArgumentType;
                            break;
                        case Number:
                        case ConstantScalar:
                        case VariableScalar:
                        case TemporaryVariable:
                            ThirdArgumentType = SCALAR;
                            Type[ThirdArgumentToken] = SCALAR;
                            break;
                        case ConstantVector:
                        case VariableVector:
                            ThirdArgumentType = VECTOR;
                            Type[ThirdArgumentToken] = VECTOR;
                            break;
                    }
                    if ((FirstArgumentType == SCALAR) && (SecondArgumentType == SCALAR) && (ThirdArgumentType
                            == SCALAR)) {
                        ArgumentType = SCALAR;
                    } else {
                        ArgumentType = VECTOR;
                    }
                    Out = ReturnType(ArgumentType, FType, Token[FirstToken], FirstArgumentToken, EndArgumentToken);
                    Type[FirstToken + 1] = Out;
                    Type[FirstToken] = Out;
                    Type[LastToken] = Out;
                    break;
                case FunctionOfListVariables:
                    ArgumentToken = FirstToken + 2;
                    EndArgumentToken = ArgumentToken;
                    moreArguments = true;
                    argumentTypeString = "";
                    while (moreArguments) {
                        switch (Properties[ArgumentToken]) {
                            case ForLoop:
                                EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                                InsideFirstToken = ArgumentToken + 2;
                                InsideLastToken = EndArgumentToken - 1;
                                ArgumentType = TypeForLoop(InsideFirstToken, InsideLastToken);
                                Type[ArgumentToken] = ArgumentType;
                                Type[ArgumentToken + 1] = ArgumentType;
                                Type[EndArgumentToken] = ArgumentType;
                                break;
                            case OpenBracket:
                                EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                                InsideFirstToken = ArgumentToken + 1;
                                InsideLastToken = EndArgumentToken - 1;
                                ArgumentType = TypeBracketPair(InsideFirstToken, InsideLastToken);
                                Type[ArgumentToken] = ArgumentType;
                                Type[EndArgumentToken] = ArgumentType;
                                break;
                            case Number:
                            case ConstantScalar:
                            case VariableScalar:
                            case TemporaryVariable:
                                ArgumentType = SCALAR;
                                Type[ArgumentToken] = SCALAR;
                                break;
                            case ConstantVector:
                            case VariableVector:
                                ArgumentType = VECTOR;
                                Type[ArgumentToken] = VECTOR;
                                break;
                        }
                        argumentTypeString = argumentTypeString + ((ArgumentType == SCALAR) ? "S" : "V");
                        if (Properties[EndArgumentToken + 1] != Comma) {
                            moreArguments = false;
                        } else {
                            ArgumentToken = EndArgumentToken + 2;
                            EndArgumentToken = ArgumentToken;
                        }
                    }
                    Out = JListFunctionReturnType(argumentTypeString, Token[FirstToken], FirstToken + 2,
                            EndArgumentToken);
                    Type[FirstToken + 1] = Out;
                    Type[FirstToken] = Out;
                    Type[LastToken] = Out;
                    break;
                case Number:
                case ConstantScalar:
                case VariableScalar:
                case TemporaryVariable:
                case ConstantVector:
                case VariableVector:
                    if ((Properties[FirstToken] == VariableVector) || (Properties[FirstToken] == ConstantVector)) {
                        FirstArgumentType = VECTOR;
                    } else {
                        FirstArgumentType = SCALAR;
                    }
                    Type[FirstToken] = FirstArgumentType;
                    OperatorToken = FirstToken + 1;
                    SecondArgumentToken = OperatorToken + 1;
                    switch (Properties[SecondArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(SecondArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = SecondArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            SecondArgumentType = TypeForLoop(InsideFirstToken, InsideLastToken);
                            Type[SecondArgumentToken] = SecondArgumentType;
                            Type[SecondArgumentToken + 1] = SecondArgumentType;
                            Type[EndArgumentToken] = SecondArgumentType;
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(SecondArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = SecondArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            SecondArgumentType = TypeBracketPair(InsideFirstToken, InsideLastToken);
                            Type[SecondArgumentToken] = SecondArgumentType;
                            Type[EndArgumentToken] = SecondArgumentType;
                            break;
                        case Number:
                        case ConstantScalar:
                        case VariableScalar:
                        case TemporaryVariable:
                        case ConstantVector:
                        case VariableVector:
                            if ((Properties[SecondArgumentToken] == VariableVector) || (Properties[SecondArgumentToken]
                                    == ConstantVector)) {
                                SecondArgumentType = VECTOR;
                            } else {
                                SecondArgumentType = SCALAR;
                            }
                            Type[SecondArgumentToken] = SecondArgumentType;
                            break;
                    }
                    BinaryOperatorSecondArgumentType[OperatorToken] = SecondArgumentType;
                    if ((FirstArgumentType == SCALAR) && (SecondArgumentType == SCALAR)) {
                        Out = SCALAR;
                    } else {
                        Out = VECTOR;
                    }
                    Type[OperatorToken] = Out;
                    break;
                case ForLoop:
                    EndArgumentToken = FindPairedBracket(FirstToken + 1, FirstToken, LastToken);
                    InsideFirstToken = FirstToken + 2;
                    InsideLastToken = EndArgumentToken - 1;
                    FirstArgumentType = TypeForLoop(InsideFirstToken, InsideLastToken);
                    Type[FirstToken] = FirstArgumentType;
                    Type[FirstToken + 1] = FirstArgumentType;
                    Type[EndArgumentToken] = FirstArgumentType;
                    OperatorToken = EndArgumentToken + 1;
                    SecondArgumentToken = OperatorToken + 1;
                    switch (Properties[SecondArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(SecondArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = SecondArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            SecondArgumentType = TypeForLoop(InsideFirstToken, InsideLastToken);
                            Type[SecondArgumentToken] = SecondArgumentType;
                            Type[SecondArgumentToken + 1] = SecondArgumentType;
                            Type[EndArgumentToken] = SecondArgumentType;
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(SecondArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = SecondArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            SecondArgumentType = TypeBracketPair(InsideFirstToken, InsideLastToken);
                            Type[SecondArgumentToken] = SecondArgumentType;
                            Type[EndArgumentToken] = SecondArgumentType;
                            break;
                        case Number:
                        case ConstantScalar:
                        case VariableScalar:
                        case TemporaryVariable:
                        case ConstantVector:
                        case VariableVector:
                            if ((Properties[SecondArgumentToken] == VariableVector) || (Properties[SecondArgumentToken]
                                    == ConstantVector)) {
                                SecondArgumentType = VECTOR;
                            } else {
                                SecondArgumentType = SCALAR;
                            }
                            Type[SecondArgumentToken] = SecondArgumentType;
                            break;
                    }
                    BinaryOperatorSecondArgumentType[OperatorToken] = SecondArgumentType;
                    if ((FirstArgumentType == SCALAR) && (SecondArgumentType == SCALAR)) {
                        Out = SCALAR;
                    } else {
                        Out = VECTOR;
                    }
                    Type[OperatorToken] = Out;
                    break;
                case OpenBracket:
                    EndArgumentToken = FindPairedBracket(FirstToken, FirstToken, LastToken);
                    InsideFirstToken = FirstToken + 1;
                    InsideLastToken = EndArgumentToken - 1;
                    FirstArgumentType = TypeBracketPair(InsideFirstToken, InsideLastToken);
                    Type[FirstToken] = FirstArgumentType;
                    Type[EndArgumentToken] = FirstArgumentType;
                    OperatorToken = EndArgumentToken + 1;
                    SecondArgumentToken = OperatorToken + 1;
                    switch (Properties[SecondArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(SecondArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = SecondArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            SecondArgumentType = TypeForLoop(InsideFirstToken, InsideLastToken);
                            Type[SecondArgumentToken] = SecondArgumentType;
                            Type[SecondArgumentToken + 1] = SecondArgumentType;
                            Type[EndArgumentToken] = SecondArgumentType;
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(SecondArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = SecondArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            SecondArgumentType = TypeBracketPair(InsideFirstToken, InsideLastToken);
                            Type[SecondArgumentToken] = SecondArgumentType;
                            Type[EndArgumentToken] = SecondArgumentType;
                            break;
                        case Number:
                        case ConstantScalar:
                        case VariableScalar:
                        case TemporaryVariable:
                        case ConstantVector:
                        case VariableVector:
                            if ((Properties[SecondArgumentToken] == VariableVector) || (Properties[SecondArgumentToken]
                                    == ConstantVector)) {
                                SecondArgumentType = VECTOR;
                            } else {
                                SecondArgumentType = SCALAR;
                            }
                            Type[SecondArgumentToken] = SecondArgumentType;
                            break;
                    }
                    BinaryOperatorSecondArgumentType[OperatorToken] = SecondArgumentType;
                    if ((FirstArgumentType == SCALAR) && (SecondArgumentType == SCALAR)) {
                        Out = SCALAR;
                    } else {
                        Out = VECTOR;
                    }
                    Type[OperatorToken] = Out;
                    break;
            }
        }
        return Out;
    }


    public byte JListFunctionReturnType(String argumentTypeString, String FunctionName, int FirstArgumentToken,
                                        int EndArgumentToken) throws ComputeFunctionUseException {
        //
        // The arguments of this function pass the information about every argument
        // in the variable argumentTypeString.  This can be used to decide if the
        // call to the function is allowable.  The format of this argument is
        // a sequence of letters S and V in the order of the arguments of the
        // function to be called.
        //
        if (FunctionName.equals("Use")) {
            return VECTOR; // takes any arguments.
        } else if (FunctionName.equals("Covariance")) {
            if (argumentTypeString.equals("VSVS")) {
                return SCALAR;
            }
        } else if (FunctionName.equals("SolveCubic")) {
            if (argumentTypeString.equals("SSSS")) {
                return VECTOR;
            }
        } else if (FunctionName.equals("Control")) {
            return SCALAR;
        } // any args
        throw new ComputeFunctionUseException(FunctionName, remakeExpression(FirstArgumentToken, EndArgumentToken));
    }


    /*
  public void throwFunctionNameException(int namePosition, String name) throws ComputeFunctionNameException {
      String prefix, suffix;
      int realBeginning, realEnd, position;
      int endOfExpression = Expression.length() - 1;
      realBeginning = ( Expression.startsWith("(") ) ? 1 : 0;
      realEnd = ( Expression.endsWith(")") ) ? endOfExpression - 1 : endOfExpression;
      if (namePosition == realBeginning ) prefix = "";
      else prefix = (namePosition <= realBeginning + 2) ? Expression.substring(realBeginning, namePosition) : Expression.substring(namePosition - 3, namePosition);
      position = namePosition + name.length();
      if (position == realEnd) suffix = "";
      else suffix = (position >= realEnd - 3) ? Expression.substring(position + 1, endOfExpression) : Expression.substring(position + 1, position + 4);
      throw new ComputeFunctionNameException(prefix, name, suffix);
  }

  public void throwConstantNameException(int namePosition, String name) throws ComputeConstantNameException {
      String prefix, suffix;
      int realBeginning, realEnd, position;
      int endOfExpression = Expression.length() - 1;
      realBeginning = ( Expression.startsWith("(") ) ? 1 : 0;
      realEnd = ( Expression.endsWith(")") ) ? endOfExpression - 1 : endOfExpression;
      if (namePosition == realBeginning ) prefix = "";
      else prefix = (namePosition <= realBeginning + 2) ? Expression.substring(realBeginning, namePosition) : Expression.substring(namePosition - 3, namePosition);
      position = namePosition + name.length();
      if (position == realEnd) suffix = "";
      else suffix = (position >= realEnd - 3) ? Expression.substring(position + 1, endOfExpression) : Expression.substring(position + 1, position + 4);
      throw new ComputeConstantNameException(prefix, name, suffix);
  }
  */


    public byte ReturnType(byte ArgumentType, byte FunctionType, String FunctionName, int FirstArgumentToken,
                           int EndArgumentToken) throws ComputeFunctionUseException {

        byte Out = SCALAR;
        switch (FunctionType) {
            case AnyToSame:
                Out = ArgumentType;
                break;
            case AnyToScalar:
                Out = SCALAR;
                break;
            case AnyToVector:
                Out = VECTOR;
                break;
            case AnyToOpposite:
                Out = (ArgumentType == SCALAR) ? VECTOR : SCALAR;
                break;
            case ScalarToScalar:
                if (ArgumentType == SCALAR) {
                    Out = SCALAR;
                } else {
                    throw new ComputeFunctionUseException(FunctionName,
                            remakeExpression(FirstArgumentToken, EndArgumentToken),
                            "This function requires only scalar arguments.");
                }
                break;
            case VectorToScalar:
                if (ArgumentType == SCALAR) {
                    throw new ComputeFunctionUseException(FunctionName,
                            remakeExpression(FirstArgumentToken, EndArgumentToken),
                            "This function requires vector arguments.");
                } else {
                    Out = SCALAR;
                }
                break;
            case ScalarToVector:
                if (ArgumentType == SCALAR) {
                    Out = VECTOR;
                } else {
                    throw new ComputeFunctionUseException(FunctionName,
                            remakeExpression(FirstArgumentToken, EndArgumentToken),
                            "This function requires only scalar arguments.");
                }
                break;
            case VectorToVector:
                if (ArgumentType == SCALAR) {
                    throw new ComputeFunctionUseException(FunctionName,
                            remakeExpression(FirstArgumentToken, EndArgumentToken),
                            "This function requires vector arguments.");
                } else {
                    Out = VECTOR;
                }
                break;
        }
        return Out;
    }

    public int FindSequence(int StartToken, char Key) throws ComputeSyntaxException {
        int I, StartExtendedArgument, EndExtendedArgument, Go;
        String Test1 = new String();
        String Test2 = new String();
        NumberScalarMults = 0;
        NumberVectorMults = 0;
        NumberScalarDivides = 0;
        NumberVectorDivides = 0;
        NumberScalarAdds = 0;
        NumberVectorAdds = 0;
        NumberExponentialFunctionScalars = 0;
        NumberExponentialFunctionVectors = 0;
        switch (Key) {
            case '*':
                Test1 = "*";
                Test2 = "/";
                break;
            case '+':
                Test1 = "+";
                Test2 = "-";
                break;
            case '/':
                Test1 = "/";
                Test2 = "/";
                break;
            case '^':
                Test1 = "^";
                Test2 = "^";
                break;
        }
        I = StartToken;
        while ((I < NumberOfTokens) && (HasBeenOptimized[I] || (!Token[I].equals(Test1) && !Token[I].equals(Test2)))) {
            I++;
        }
        if (I == NumberOfTokens) {
            SequenceEnd = 0;
            if (displayProgress) {
                diagnosticPrintln("SequenceEnd value at position 1 = 0: no sequence found.");
            }
        } else {
            HasBeenOptimized[I] = true;
            ArgumentType[0] = Type[I - 1];
            ExponentialFunctionArgument[0] = 0;
            EndExtendedArgument = I - 1;
            ArgumentEnd[0] = EndExtendedArgument;
            if (Properties[I - 1] == CloseBracket) {
                StartExtendedArgument = FindPairedBracket(I - 1, 1, NumberOfTokens);
                ArgumentStart[0] = StartExtendedArgument;
                if ((Key == '*') && Token[StartExtendedArgument + 1].equals("Exp")) {
                    ExponentialFunctionArgument[0] = 1;
                    ExponentialFunction[StartExtendedArgument] = true;
                    ExponentialFunction[EndExtendedArgument] = true;
                    if (Type[StartExtendedArgument] == SCALAR) {
                        NumberExponentialFunctionScalars++;
                    } else {
                        NumberExponentialFunctionVectors++;
                    }
                }
            } else {
                ArgumentStart[0] = I - 1;
            }
            switch (Key) {
                case '*':
                    if (Type[ArgumentStart[0]] == SCALAR) {
                        NumberScalarMults++;
                    } else {
                        NumberVectorMults++;
                    }
                    break;
                case '+':
                    if (Type[ArgumentStart[0]] == SCALAR) {
                        NumberScalarAdds++;
                    } else {
                        NumberVectorAdds++;
                    }
                    break;
                case '/':
                case '^':
                    break;
            }
            SequenceStart = ArgumentStart[0] - 1;
            if (SequenceStart < 0) {
                SequenceStart = 0;
            }
            DisplayTokens("Temp display as Argument 0 is determined at location " + String.valueOf(ArgumentStart[0])
                    + " with type = " + String.valueOf(Type[ArgumentStart[0]]));
            SequenceNumber = 1;
            Go = 1;
            while ((I < NumberOfTokens) && (Go == 1)) {
                ArgumentStart[SequenceNumber] = I + 1;
                if (Token[I].equals("*")) {
                    if (Type[I + 1] == SCALAR) {
                        ArgumentType[SequenceNumber] = MultiplyByScalar;
                        NumberScalarMults++;
                    } else {
                        ArgumentType[SequenceNumber] = MultiplyByVector;
                        NumberVectorMults++;
                    }
                } else if (Token[I].equals("/")) {
                    if (Type[I + 1] == SCALAR) {
                        ArgumentType[SequenceNumber] = DivideByScalar;
                        NumberScalarDivides++;
                    } else {
                        ArgumentType[SequenceNumber] = DivideByVector;
                        NumberVectorDivides++;
                    }
                } else if (Token[I].equals("+") || Token[I].equals("-")) {
                    if (Type[I + 1] == SCALAR) {
                        ArgumentType[SequenceNumber] = AddOrSubtractScalar;
                        NumberScalarAdds++;
                    } else {
                        ArgumentType[SequenceNumber] = AddOrSubtractVector;
                        NumberVectorAdds++;
                    }
                }
                ExponentialFunctionArgument[SequenceNumber] = 0;
                if (Properties[I + 1] == OpenBracket) {
                    StartExtendedArgument = I + 1;
                    EndExtendedArgument = FindPairedBracket(StartExtendedArgument, StartExtendedArgument,
                            NumberOfTokens);
                    if ((Key == '*') && Token[StartExtendedArgument + 1].equals("Exp")) {
                        ExponentialFunctionArgument[SequenceNumber] = 1;
                        ExponentialFunction[StartExtendedArgument] = true;
                        ExponentialFunction[EndExtendedArgument] = true;
                        if (Type[StartExtendedArgument] == SCALAR) {
                            NumberExponentialFunctionScalars++;
                        } else {
                            NumberExponentialFunctionVectors++;
                        }
                    }
                    I = EndExtendedArgument;
                } else {
                    I++;
                }
                ArgumentEnd[SequenceNumber] = I;
                DisplayTokens("Display as argument " + String.valueOf(SequenceNumber) + " is found, beginning at "
                        + String.valueOf(ArgumentStart[SequenceNumber]) + " and ending at location I = " + String
                        .valueOf(I));
                I = I + 2;
                if ((I < NumberOfTokens) && (Test1.equals(Token[I]) || Test2.equals(Token[I])) && (HasBeenOptimized[I]
                        == false)) {
                    HasBeenOptimized[I] = true;
                    SequenceNumber++;
                    SequenceStart--;
                } else {
                    Go = 0;
                }
                if (displayProgress) {
                    diagnosticPrintln("Here are the sequence search values just before testing while loop: SqNum = "
                            + String.valueOf(SequenceNumber) + ", I = " + String.valueOf(I));
                }
            }
            SequenceEnd = ArgumentEnd[SequenceNumber] + 1;
            if (SequenceEnd > NumberOfTokens) {
                SequenceEnd = NumberOfTokens;
            }
            if (displayProgress) {
                diagnosticPrintln("SequenceEnd value at position 2 = " + String.valueOf(SequenceEnd));
            }
        }
        NumberScalars = NumberScalarMults + NumberScalarDivides;
        NumberVectors = NumberVectorMults + NumberVectorDivides;
        return SequenceEnd;
    }

    public void CopyAndRemoveTokens(int StartToken, int EndToken, int CopyAfter) {
        int CopyTo = CopyAfter + 1;
        ShiftTokens(CopyTo, EndToken - StartToken + 1);
        for (int I = StartToken; I <= EndToken; I++) {
            WriteTokenTo(CopyTo, Token[I], Properties[I], Type[I], HasBeenOptimized[I], ExponentialFunction[I]);
            Properties[I] = Remove;
            CopyTo++;
        }
        if (displayProgress) {
            diagnosticPrintln(
                    "CopyAndRemove( " + String.valueOf(StartToken) + " , " + String.valueOf(EndToken) + " , " + String
                            .valueOf(CopyAfter));
        }
        DisplayString();
        return;
    }

    public void SortScalarVectorExpFactors() {
        int ExponentialsDone;
        if ((NumberExponentialFunctionVectors + NumberExponentialFunctionScalars > 1)
                || ((NumberScalars > 1) && (NumberVectors > 0)) || ((NumberScalars > 0) && (NumberVectors > 1))
                || (NumberScalarDivides > 1) || (NumberVectorDivides > 1)) {
            NumberOfArgumentsCopied = 0;
            for (int J = 0; J <= SequenceNumber; J++) {
                HasBeenCopied[J] = false;
            }
            if ((NumberExponentialFunctionVectors > 1) || ((NumberExponentialFunctionVectors == 1) && (
                    NumberExponentialFunctionScalars > 0))) {
                Segregate("exp");
                ExponentialsDone = 1;
            } else {
                ExponentialsDone = 0;
            }
            if (NumberVectorDivides > 0) {
                Segregate("/v");
            }
            if (NumberVectorMults > 0) {
                Segregate("*v");
            }
            if ((ExponentialsDone == 0) && (NumberExponentialFunctionScalars > 1)) {
                Segregate("exp");
            }
            if (NumberScalarDivides > 0) {
                Segregate("/s");
            }
            if (NumberScalarMults > 0) {
                Segregate("*s");
            }
        }
        return;
    }


    public void Segregate(String Key) {
        boolean Test;
        String AddBefore = new String();
        if (displayProgress) {
            diagnosticPrintln("Segregate: Key string is..." + Key + "...");
        }
        if (displayProgress) {
            diagnosticPrintln(String.valueOf(SequenceNumber));
        }
        for (int J = SequenceNumber; J >= 1; J--) {
            if (Key.equals("exp")) {
                Test = (ExponentialFunctionArgument[J] == 1);
            } else if (Key.equals("/v")) {
                Test = (ArgumentType[J] == DivideByVector);
            } else if (Key.equals("*v")) {
                Test = (ArgumentType[J] == MultiplyByVector);
            } else if (Key.equals("/s")) {
                Test = (ArgumentType[J] == DivideByScalar);
            } else if (Key.equals("*s")) {
                Test = (ArgumentType[J] == MultiplyByScalar);
            } else if (Key.equals("+-v")) {
                Test = (ArgumentType[J] == AddOrSubtractVector);
            } else if (Key.equals("+-s")) {
                Test = (ArgumentType[J] == AddOrSubtractScalar);
            } else {
                Test = false;
            } // trap error here?
            if (Test && !HasBeenCopied[J]) {
                if (NumberOfArgumentsCopied < SequenceNumber) {
                    CopyAndRemoveTokens(ArgumentStart[J] - 1, ArgumentEnd[J] + 1, SequenceEnd);
                } else {
                    Properties[ArgumentStart[J] - 1] = Remove;
                    Properties[ArgumentEnd[J] + 1] = Remove;
                    if (Token[ArgumentStart[J] - 1].equals("+") || Token[ArgumentStart[J] - 1].equals("*")) {
                        CopyAndRemoveTokens(ArgumentStart[J], ArgumentEnd[J], SequenceEnd);
                    } else if (Token[ArgumentStart[J] - 1].equals("-")) {
                        InsertTokenAfter(SequenceEnd, ")", CloseBracket, (byte) 0, false, false);
                        InsertTokenAfter(SequenceEnd, "]", CloseSquareBracket, (byte) 0, false, false);
                        CopyAndRemoveTokens(ArgumentStart[J], ArgumentEnd[J], SequenceEnd);
                        InsertTokenAfter(SequenceEnd, "[", OpenSquareBracket, (byte) 0, false, false);
                        InsertTokenAfter(SequenceEnd, "Negate", FunctionOfOneVariable, (byte) 0, false, false);
                        InsertTokenAfter(SequenceEnd, "(", OpenBracket, (byte) 0, false, false);
                    } else if (Token[ArgumentStart[J] - 1].equals("/")) {
                        InsertTokenAfter(SequenceEnd, ")", CloseBracket, (byte) 0, false, false);
                        CopyAndRemoveTokens(ArgumentStart[J], ArgumentEnd[J], SequenceEnd);
                        InsertTokenAfter(SequenceEnd, "/", BinaryOperator, (byte) 0, false, false);
                        InsertTokenAfter(SequenceEnd, "1.0", Number, (byte) 0, false, false);
                        InsertTokenAfter(SequenceEnd, "(", OpenBracket, (byte) 0, false, false);
                    }
                }
                HasBeenCopied[J] = true;
                NumberOfArgumentsCopied++;
            }
        }
        if (!HasBeenCopied[0]) {
            if (Key.equals("exp")) {
                Test = (ExponentialFunctionArgument[0] == 1);
                AddBefore = "*";
            } else if (Key.equals("/v") || Key.equals("/s")) {
                AddBefore = "No";
                Test = false;
            } else if (Key.equals("*v")) {
                Test = (Type[ArgumentStart[0]] == VECTOR);
                AddBefore = "*";
            } else if (Key.equals("+-v")) {
                Test = (Type[ArgumentStart[0]] == VECTOR);
                AddBefore = "+";
            } else if (Key.equals("*s")) {
                Test = (Type[ArgumentStart[0]] == SCALAR);
                AddBefore = "*";
            } else if (Key.equals("+-s")) {
                Test = (Type[ArgumentStart[0]] == SCALAR);
                AddBefore = "+";
            } else {
                Test = false;  // trap error here?
                AddBefore = "";
            }
            if (Test) {
                if (NumberOfArgumentsCopied == SequenceNumber) {
                    CopyAndRemoveTokens(ArgumentStart[0], ArgumentEnd[0], SequenceEnd);
                } else {
                    InsertTokenAfter(SequenceEnd, ")", CloseBracket, (byte) 0, false, false);
                    CopyAndRemoveTokens(ArgumentStart[0], ArgumentEnd[0], SequenceEnd);
                    if (!AddBefore.equals("No")) {
                        InsertTokenAfter(SequenceEnd, AddBefore, BinaryOperator, (byte) 0, true, false);
                    }
                }
                NumberOfArgumentsCopied++;
                HasBeenCopied[0] = true;
            }
        }
        return;
    }

    public int ConsolidateExponentials() throws ComputeSyntaxException {
        int ExpEnd, PreviousOperator, ArgumentFirstToken, InsertPosition;
        int StartFirstExpArg, EndFirstExpArg, NumberOfExpCombined, InterveningBracket;
        int OperatorToken, TestNextArgument, CurrentExpStart, CurrentExpEnd;
        int PreviousEnd, NumberNestedBrackets, EndCombinedExpArg;
        int CurrentArgEnd, CurrentArgStart, BeginFirstArgInside, EndFirstArgInside;
        int OperatorInside;
        int ExponentialTreated = -1;
        int MoveStartBack = 0;
        int ExpStart = 1;
        int PairedBkt = 0;
        while ((ExpStart < NumberOfTokens) && (ExponentialTreated == -1)) {
            if ((Properties[ExpStart] == OpenBracket) && ExponentialFunction[ExpStart]) {
                ExpEnd = FindPairedBracket(ExpStart, ExpStart, NumberOfTokens);
                PreviousOperator = ExpStart - 1;
                if ((PreviousOperator > 0) && Token[PreviousOperator].equals("/")) {
                    if (displayProgress) {
                        diagnosticPrintln("division by exponential recognized at first program position");
                    }
                    ExponentialTreated = 0;
                    Token[PreviousOperator] = "*";
                    ArgumentFirstToken = ExpStart + 3;
                    if (Properties[ArgumentFirstToken] == Number) {
                        if (Token[ArgumentFirstToken].startsWith("-")) {
                            Token[ArgumentFirstToken] = new String(Token[ArgumentFirstToken].substring(1));
                        } else {
                            Token[ArgumentFirstToken] = "-" + Token[ArgumentFirstToken];
                        }
                    } else {
                        DisplayTokens("just before inserting Negate in 1st prog pos ");
                        InsertPosition = ExpEnd - 2;
                        InsertTokenAfter(InsertPosition, ")", CloseBracket, (byte) 0, false, false);
                        InsertTokenAfter(InsertPosition, "]", CloseSquareBracket, (byte) 0, false, false);
                        InsertPosition = ArgumentFirstToken - 1;
                        InsertTokenAfter(InsertPosition, "[", OpenSquareBracket, (byte) 0, false, false);
                        InsertTokenAfter(InsertPosition, "Negate", FunctionOfOneVariable, (byte) 0, false, false);
                        InsertTokenAfter(InsertPosition, "(", OpenBracket, (byte) 0, false, false);
                        ExpEnd = ExpEnd + 5;
                        DisplayTokens("just after inserting Negate in 1st prog pos");
                        SetBracketLevels(" SBL Invocation 11 ");
                    }
                }
                EndFirstExpArg = ExpEnd - 2;
                StartFirstExpArg = OtherEndOfArgument(EndFirstExpArg);
                NumberOfExpCombined = 1;
                if (Properties[ExpEnd + 1] == CloseBracket) {
                    InterveningBracket = 1;
                    PairedBkt = FindPairedBracket(ExpEnd + 1, 1, ExpEnd + 1);
                    OperatorToken = ExpEnd + 2;
                    TestNextArgument = ExpEnd + 3;
                } else {
                    InterveningBracket = 0;
                    OperatorToken = ExpEnd + 1;
                    TestNextArgument = ExpEnd + 2;
                }
                CurrentExpEnd = ExpEnd;
                while ((TestNextArgument < NumberOfTokens) && ExponentialFunction[TestNextArgument]) {
                    ExponentialTreated = 0;
                    NumberOfExpCombined++;
                    PreviousEnd = CurrentExpEnd;
                    CurrentExpStart = TestNextArgument;
                    CurrentExpEnd = FindPairedBracket(CurrentExpStart, CurrentExpStart, NumberOfTokens);
                    for (int I = -1; I <= 4 + InterveningBracket; I++) {
                        Properties[PreviousEnd + I] = Remove;
                        if (PreviousEnd + I < ExpStart) {
                            MoveStartBack++;
                        }
                    }
                    if (InterveningBracket == 1) {
                        Properties[PairedBkt] = Remove;
                        if (PairedBkt < ExpStart) {
                            MoveStartBack++;
                        }
                    }
                    CurrentArgEnd = CurrentExpEnd - 2;
                    CurrentArgStart = OtherEndOfArgument(CurrentArgEnd);
                    if ((Token[OperatorToken].equals("*")) && (CurrentArgEnd > CurrentArgStart)) {
                        DisplayTokens("Look at tokens before unwrapping additions.");
                        UnwrapAdditionBrackets(CurrentArgStart, CurrentArgEnd);
                        DisplayTokens("Look at tokens after unwrapping additions.");
                        ExponentialTreated = ExpStart;
                    } else if (Token[OperatorToken].equals("/")) {
                        DisplayTokens("just before inserting Negate in 2nd prog pos ");
                        InsertTokenAfter(CurrentArgEnd, ")", CloseBracket, (byte) 0, false, false);
                        InsertTokenAfter(CurrentArgEnd, "]", CloseSquareBracket, (byte) 0, false, false);
                        InsertPosition = CurrentArgStart - 1;
                        InsertTokenAfter(InsertPosition, "[", OpenSquareBracket, (byte) 0, false, false);
                        InsertTokenAfter(InsertPosition, "Negate", FunctionOfOneVariable, (byte) 0, false, false);
                        InsertTokenAfter(InsertPosition, "(", OpenBracket, (byte) 0, false, false);
                        CurrentArgEnd = CurrentArgEnd + 5;
                        DisplayTokens("just after inserting Negate in 2nd prog pos ");
                        SetBracketLevels(" SBL Invocation 12 ");
                    }
                    CurrentExpEnd = CurrentArgEnd + 2;
                    Token[OperatorToken] = "+";
                    Properties[OperatorToken] = BinaryOperator;
                    if (NumberOfExpCombined > 2) {
                        Token[OperatorToken - 1] = ")";
                        Properties[OperatorToken - 1] = CloseBracket;
                    }
                    if (Properties[CurrentExpEnd + 1] == CloseBracket) {
                        InterveningBracket = 1;
                        PairedBkt = FindPairedBracket(CurrentExpEnd + 1, 1, ExpEnd + 1);
                        OperatorToken = CurrentExpEnd + 2;
                        TestNextArgument = CurrentExpEnd + 3;
                    } else {
                        InterveningBracket = 0;
                        OperatorToken = CurrentExpEnd + 1;
                        TestNextArgument = CurrentExpEnd + 2;
                    }
                }
                if (NumberOfExpCombined > 1) {
                    EndCombinedExpArg = CurrentExpEnd - 2;
                    InsertTokenAfter(EndCombinedExpArg, ")", CloseBracket, (byte) 0, false, false);
                    if (displayProgress) {
                        diagnosticPrintln("Inserted ) after " + String.valueOf(EndCombinedExpArg));
                    }
                    NumberNestedBrackets = NumberOfExpCombined - 1;
                    for (int I = 1; I <= NumberNestedBrackets; I++) {
                        InsertTokenBefore(StartFirstExpArg, "(", OpenBracket, (byte) 0, false, false);
                    }
                    if (displayProgress) {
                        diagnosticPrintln("Inserted ( before " + String.valueOf(StartFirstExpArg));
                    }
                }
            }
            ExpStart++;
        }
        return (ExponentialTreated > 0) ? ExponentialTreated - MoveStartBack : ExponentialTreated;
    }


    public int ReplaceEPower() throws ComputeSyntaxException {
        int ArgumentStart, ArgumentEnd;
        int DidSomething = 0;
        int I = 1;
        while ((I < NumberOfTokens) && (DidSomething == 0)) {
            if ((Properties[I] == ConstantScalar) && (Token[I].equals("E")) && (Token[I + 1].equals("^"))) {
                DidSomething = 1;
                ArgumentStart = I + 2;
                ArgumentEnd = OtherEndOfArgument(ArgumentStart);
                InsertTokenAfter(ArgumentEnd, ")", CloseBracket, (byte) 0, false, false);
                InsertTokenAfter(ArgumentEnd, "]", CloseSquareBracket, (byte) 0, false, false);
                InsertTokenBefore(ArgumentStart, "[", OpenSquareBracket, (byte) 0, false, false);
                Token[I + 1] = "Exp";
                Properties[I + 1] = FunctionOfOneVariable;
                Token[I] = "(";
                Properties[I] = OpenBracket;
            }
            I++;
        }
        return DidSomething;
    }


    public int OptimizeExpToPower() throws ComputeSyntaxException {
        int BeginSurround, EndSurround, BeginPower, EndPower, ArgumentOpenSqBkt;
        int BeginFirstArgInsideExponent, EndFirstArgInsideExponent, OperatorInsideExponent;
        int ExponentialTreated = -1;
        int I = 1;
        while ((I < NumberOfTokens) && (ExponentialTreated == -1)) {
            if (Properties[I] == FunctionOfOneVariable) {
                if (Token[I].equals("Exp")) {
                    BeginSurround = I - 1;
                    EndSurround = FindPairedBracket(BeginSurround, 1, NumberOfTokens);
                    if ((EndSurround < NumberOfTokens) && Token[EndSurround + 1].equals("^")) {
                        ExponentialTreated = 0;
                        ArgumentOpenSqBkt = I + 1;
                        BeginPower = EndSurround + 2;
                        EndPower = OtherEndOfArgument(BeginPower);
                        if (EndPower > BeginPower) {
                            DisplayTokens("Look at tokens before unwrapping multiplications.");
                            UnwrapMultiplicationBrackets(EndPower, BeginPower);
                            DisplayTokens("Look at tokens after unwrapping multiplications.");
                            ExponentialTreated = I;
                        }
                        Properties[EndSurround - 1] = Remove;
                        Token[EndSurround] = "*";
                        Properties[EndSurround] = BinaryOperator;
                        Properties[EndSurround + 1] = Remove;
                        InsertTokenAfter(EndPower, ")", CloseBracket, (byte) 0, false, false);
                        InsertTokenAfter(EndPower, "]", CloseSquareBracket, (byte) 0, false, false);
                        InsertTokenAfter(EndPower, ")", CloseBracket, (byte) 0, false, false);
                        InsertTokenAfter(ArgumentOpenSqBkt, "(", OpenBracket, (byte) 0, false, false);
                    }
                }
            }
            I++;
        }
        return ExponentialTreated;
    }

    public int OptimizePowerOfPower() throws ComputeSyntaxException {
        int BaseArgumentEnd, BaseArgumentStart, BeginSurround, EndSurround, FirstPowerStart;
        int FirstPowerEnd, SecondPowerStart, SecondPowerEnd, BeginFirstArgInsideExponent;
        int EndFirstArgInsideExponent, OperatorInsideExponent;
        int BaseArgumentTreated = -1;
        int I = 1;
        while ((I < NumberOfTokens) && (BaseArgumentTreated == -1)) {
            if (Properties[I] == BinaryOperator) {
                if (Token[I].equals("^")) {
                    BaseArgumentEnd = I - 1;
                    BaseArgumentStart = OtherEndOfArgument(BaseArgumentEnd);
                    BeginSurround = BaseArgumentStart - 1;
                    EndSurround = FindPairedBracket(BeginSurround, 1, NumberOfTokens);
                    if ((EndSurround < NumberOfTokens) && Token[EndSurround + 1].equals("^")) {
                        BaseArgumentTreated = 0;
                        FirstPowerStart = I + 1;
                        FirstPowerEnd = OtherEndOfArgument(FirstPowerStart);
                        SecondPowerStart = EndSurround + 2;
                        SecondPowerEnd = OtherEndOfArgument(SecondPowerStart);
                        if (SecondPowerEnd > SecondPowerStart) {
                            DisplayTokens("Look at tokens before unwrapping multiplications.");
                            UnwrapMultiplicationBrackets(SecondPowerStart, SecondPowerEnd);
                            DisplayTokens("Look at tokens after unwrapping multiplications.");
                            BaseArgumentTreated = BaseArgumentStart - 1;
                        }
                        Properties[BeginSurround] = Remove;
                        Properties[EndSurround] = Remove;
                        Token[EndSurround + 1] = "*";
                        InsertTokenAfter(SecondPowerEnd, ")", CloseBracket, (byte) 0, false, false);
                        InsertTokenAfter(I, "(", OpenBracket, (byte) 0, false, false);
                    }
                }
            }
            I++;
        }
        return BaseArgumentTreated;
    }


    public int OtherEndOfArgument(int GivenEnd) throws ComputeSyntaxException {
        int first;
        if (Properties[GivenEnd] == OpenBracket) {
            return FindPairedBracket(GivenEnd, 1, NumberOfTokens);
        } else if (Properties[GivenEnd] == ForLoop) {
            return FindPairedBracket(GivenEnd + 1, 1, NumberOfTokens);
        } else if (Properties[GivenEnd] == CloseBracket) {
            first = FindPairedBracket(GivenEnd, 1, NumberOfTokens);
            if ((first > 1) && (Properties[first - 1] == ForLoop)) {
                return first - 1;
            } else {
                return first;
            }
        } else {
            return GivenEnd;
        }
    }

    public int ConsolidateDivisions() throws ComputeSyntaxException {
        int DivisorBegin, DivisorEnd, NumberOfDivisionsChanged, TestNextOperator;
        int InterveningCloseBkt, PairedOpenBkt, DivToken;
        byte DivType;
        int ShiftFirstDivTokenBack = 0;
        int DivisionTreated = -1;
        int FirstDivToken = 1;
        while ((FirstDivToken < NumberOfTokens) && (DivisionTreated == -1)) {
            if ((Properties[FirstDivToken] == BinaryOperator) && Token[FirstDivToken].equals("/")) {
                DivType = Type[FirstDivToken];
                DivisorBegin = FirstDivToken + 1;
                DivisorEnd = OtherEndOfArgument(DivisorBegin);
                NumberOfDivisionsChanged = 0;
                TestNextOperator = DivisorEnd + 2;
                while ((TestNextOperator < NumberOfTokens) && Token[TestNextOperator].equals("/") && (
                        Type[TestNextOperator] == DivType)) {
                    DivisionTreated = 0;
                    DivToken = TestNextOperator;
                    Token[DivToken] = "*";
                    NumberOfDivisionsChanged = NumberOfDivisionsChanged + 1;
                    InterveningCloseBkt = DivToken - 1;
                    PairedOpenBkt = FindPairedBracket(InterveningCloseBkt, 1, NumberOfTokens);
                    if (NumberOfDivisionsChanged == 1) {
                        Properties[InterveningCloseBkt] = Remove;
                    }
                    Properties[PairedOpenBkt] = Remove;
                    if (PairedOpenBkt < FirstDivToken) {
                        ShiftFirstDivTokenBack++;
                    }
                    DivisorBegin = DivToken + 1;
                    DivisorEnd = OtherEndOfArgument(DivisorBegin);
                    if (DivisorEnd > DivisorBegin) {
                        DisplayTokens("Look at tokens before unwrapping multiplications.");
                        UnwrapMultiplicationBrackets(DivisorBegin, DivisorEnd);
                        DisplayTokens("Look at tokens after unwrapping multiplications.");
                        DivisionTreated = FirstDivToken;
                    }
                    TestNextOperator = DivisorEnd + 2;
                }
                if (DivisionTreated > -1) {
                    InsertTokenAfter(DivisorEnd, ")", CloseBracket, (byte) 0, false, false);
                    for (int I = 1; I <= NumberOfDivisionsChanged; I++) {
                        InsertTokenAfter(FirstDivToken, "(", OpenBracket, (byte) 0, false, false);
                    }
                }
            }
            FirstDivToken++;
        }
        return (DivisionTreated > 0) ? DivisionTreated - ShiftFirstDivTokenBack : DivisionTreated;
    }


    public void RemoveUselessBrackets() throws ComputeSyntaxException {
//
// First find doubled pairs of brackets and eliminate them.
//
        int MateOfFirst, I;
        int DidSomething = 0;
        for (I = 1; I <= NumberOfTokens - 3; I++) {
            if ((Properties[I] == OpenBracket) && (Properties[I + 1] == OpenBracket) && (
                    (MateOfFirst = FindPairedBracket(I, 1, NumberOfTokens))
                            == FindPairedBracket(I + 1, 1, NumberOfTokens) + 1)) {
                Properties[I] = Remove;
                Properties[MateOfFirst] = Remove;
                DidSomething = 1;
            }
        }
        if (DidSomething == 1) {
            RemoveMarkedTokens(1, NumberOfTokens);
            SetBracketLevels(" SBL Invocation 13 ");
            DisplayTokens("Eliminated redundant pairs of brackets");
        }
//
// Next find brackets around for loops and remove them.
//
        DidSomething = 0;
        for (I = 1; I <= NumberOfTokens - 8; I++) {
            if ((Properties[I] == OpenBracket) && (Properties[I + 1] == ForLoop) && (
                    (MateOfFirst = FindPairedBracket(I, 1, NumberOfTokens))
                            == FindPairedBracket(I + 2, 1, NumberOfTokens) + 1)) {
                Properties[I] = Remove;
                Properties[MateOfFirst] = Remove;
                DidSomething = 1;
            }
        }
        if (DidSomething == 1) {
            RemoveMarkedTokens(1, NumberOfTokens);
            SetBracketLevels(" SBL Invocation 13a ");
            DisplayTokens("Eliminated redundant pairs of brackets");
        }

//
// Finally look for numbers, constants, or variables enclosed within brackets, so
// that no arithmetic needs to be done.  Remove them unless the brackets denote
// a function argument or unless there are only 3 tokens (so a single constant can
// be "evaluated").
//
        DidSomething = 0;
        if (NumberOfTokens > 3) {
            for (I = 1; I <= NumberOfTokens - 2; I++) {
                if ((Properties[I] == OpenBracket) && (Properties[I + 2] == CloseBracket) && (
                        (Properties[I + 1] == Number) || (Properties[I + 1] == ConstantScalar)
                                || (Properties[I + 1] == ConstantVector) || (Properties[I + 1] == VariableScalar)
                                || (Properties[I + 1] == VariableVector) || (Properties[I + 1] == TemporaryVariable))) {
                    if ((I == 1) || ((Properties[I - 1] != FunctionOfOneVariable)
                            && (Properties[I - 1] != FunctionOfTwoVariables)
                            && (Properties[I - 1] != FunctionOfThreeVariables) && (Properties[I - 1]
                            != FunctionOfListVariables))) {
                        Properties[I] = Remove;
                        Properties[I + 2] = Remove;
                        DidSomething = 1;
                    }
                }
            }
        }
        if (DidSomething == 1) {
            RemoveMarkedTokens(1, NumberOfTokens);
            SetBracketLevels(" SBL Invocation 14 ");
            DisplayTokens("Eliminated useless brackets around single variables.");
        }
        return;
    }

    public void SortSummationTerms() {
        if (((NumberScalarAdds > 1) && (NumberVectorAdds > 0)) || ((NumberScalarAdds > 0) && (NumberVectorAdds > 1))) {
            NumberOfArgumentsCopied = 0;
            for (int J = 0; J <= SequenceNumber; J++) {
                HasBeenCopied[J] = false;
            }
            Segregate("+-v");
            Segregate("+-s");
        }
        return;
    }

    public int ReplacePowByHat() throws ComputeSyntaxException {
        int I, BeginSurround, EndSurround, BeginSqBkt, EndSqBkt, BeginFirstArgument, EndFirstArgument, CommaToken,
                BeginSecondArgument, EndSecondArgument;
        int DidSomething = 0;
        I = 1;
        while ((I < NumberOfTokens) && (DidSomething == 0)) {
            if ((Properties[I] == FunctionOfTwoVariables) && (Token[I].equals("Pow"))) {
                DidSomething = 1;
                BeginSurround = I - 1;
                EndSurround = FindPairedBracket(BeginSurround, BeginSurround, NumberOfTokens);
                BeginSqBkt = I + 1;
                EndSqBkt = EndSurround - 1;
                BeginFirstArgument = I + 2;
                EndFirstArgument = OtherEndOfArgument(BeginFirstArgument);
                CommaToken = EndFirstArgument + 1;
//			BeginSecondArgument = CommaToken + 1;
//			EndSecondArgument = OtherEndOfArgument(BeginSecondArgument);
                Properties[BeginSqBkt] = Remove;
                Properties[EndSqBkt] = Remove;
                Properties[I] = Remove;
                Token[CommaToken] = "^";
                Properties[CommaToken] = BinaryOperator;
            }
            I++;
        }
        return DidSomething;
    }


    public void TokenizeString() throws ComputeExpressionException {
// Separate string into successive tokens. Tokens are strings
// representing numbers, operators, functions, or brackets.
// Properties (of tokens) are integers: values are in the range of 0 to 11
// (see subroutine Initialize for meanings of code values).  These
// values are assigned in Initialize to integer variables whose names
// are transparent, so there is no need to remember code values.  The
// variable names are Remove, Number, Constant, VariableScalar, VariableVector,
// BinaryOperator, UnaryOperator, Assignment, FunctionOfOneVariable,
// FunctionOfTwoVariables, FunctionOfThreeVariables, FunctionOfListVariables,
// OpenBracket, CloseBracket, OpenSquareBracket, CloseSquareBracket,
// Comma, Function, Variable, AssignmentOperator, IncrementOperator,
// and BuiltInName.  There are round and square
// brackets as an internal convenience.  All input is in round brackets, but then
// the pairs of brackets that delineate the argument field of a
// function are changed to square brackets.  This allows us to use
// round brackets to denote groups of variables that are to be
// evaluated (essentially the nodes of the evaluation tree if we
// were parsing the structure into a hierarchical tree).  The name Remove
// is a temporary value to denote tokens about to be cut out; the name
// BuiltInName is also temporary, to denote a token that has to be looked
// up in a table before it can be interpreted.  The name Function is
// temporary as well, to be replaced later when functions are
// classified according to the number of variables given to them.
// Finally, the name Variable is also temporary, replaced when the variables
// are classifield into scalars and vectors.
// The Level (of a token) is an integer giving the nesting level of the
// token (number of unclosed brackets "(" preceding it in the expression).
//
        int UNumber;
        String Cname, Fname;
        byte Typ;
        char CurrentCharacter, NextCharacter;
        StringBuffer TokenString;
        int TokenNumber = 0;
        int I = 0;
        CurrentCharacter = Expression.charAt(0);
        NextCharacter = (0 < Expression.length()) ? Expression.charAt(1) : '\\';
        byte PreviousProperty = -1;
        while (I < Expression.length()) {
            TokenNumber = TokenNumber + 1;
            if (displayProgress) {
                diagnosticPrintln("Char = " + String.valueOf(CurrentCharacter) + ",  PreviousProperty = "
                        + String.valueOf(PreviousProperty) + ", NextCharacter = " + String.valueOf(NextCharacter));
            }
            Typ = TokenType(CurrentCharacter, PreviousProperty, I);
            if (displayProgress) {
                diagnosticPrintln(String.valueOf(Typ));
            }
            /* this code should never be needed now
            if ( Typ == -1 ) {
                userInterface.println( "TokenType could not cope with this token and first character:" );
                userInterface.println( String.valueOf(I) + (new Character(CurrentCharacter)).toString() );
                return; // Want to throw an exception here: traps badly formed input expressions!!
            }
            */
            Properties[TokenNumber] = Typ;
            switch (Typ) {
                case Number:
                    TokenString = new StringBuffer();
                    TokenString.append(CurrentCharacter);
                    while ((NextCharacter != '*') && (NextCharacter != '/') && (NextCharacter != '^')
                            && (NextCharacter != '(') && (NextCharacter != ')') && (NextCharacter != ',')
                            && ((NextCharacter != '+') || (CurrentCharacter == 'e') || (CurrentCharacter == 'E'))
                            && ((NextCharacter != '-') || (CurrentCharacter == 'e') || (CurrentCharacter == 'E')) && (
                            NextCharacter != '\\')) {
                        TokenString.append(NextCharacter);
                        I++;
                        CurrentCharacter = NextCharacter;
                        NextCharacter = (I < Expression.length() - 1) ? Expression.charAt(I + 1) : '\\';
                    }
                    Token[TokenNumber] = TokenString.toString();
                    break;
                case Variable:
                    TokenString = new StringBuffer();
                    TokenString.append(CurrentCharacter);
                    while (Character.isDigit(NextCharacter)) {
                        TokenString.append(NextCharacter);
                        I++;
                        CurrentCharacter = NextCharacter;
                        NextCharacter = (I < Expression.length() - 1) ? Expression.charAt(I + 1) : '\\';
                    }
                    Properties[TokenNumber] = VariableVector;
                    if (NextCharacter == 'c') {
                        TokenString.append(NextCharacter);
                        I++;
                        CurrentCharacter = NextCharacter;
                        NextCharacter = (I < Expression.length() - 1) ? Expression.charAt(I + 1) : '\\';
                        Properties[TokenNumber] = VariableScalar;
                    } else if (NextCharacter == 's') {
                        TokenString.append(NextCharacter);
                        I++;
                        CurrentCharacter = NextCharacter;
                        NextCharacter = (I < Expression.length() - 1) ? Expression.charAt(I + 1) : '\\';
                    }
                    Token[TokenNumber] = TokenString.toString();
                    break;
                case BinaryOperator:
                    Token[TokenNumber] = String.valueOf(CurrentCharacter);
                    break;
                case UnaryOperator:
                    if (CurrentCharacter == '+') {
                        TokenNumber--;
                    } else if ((UNumber = GetUnaryOperator(new Character(CurrentCharacter).toString())) > 0) {
                        Token[TokenNumber] = UnaryValues[UNumber];
                    } else {
                        throwSyntaxException(TokenNumber,
                                "This is being interpreted as a unary prefix operator, but no such operator is defined.");
                    }
                    break;
                case OpenBracket:
                    Token[TokenNumber] = "(";
                    break;
                case CloseBracket:
                    Token[TokenNumber] = ")";
                    break;
                case Comma:
                    Token[TokenNumber] = ",";
                    break;
                case BuiltInName:
                case TemporaryVariable:
                    TokenString = new StringBuffer();
                    TokenString.append(CurrentCharacter);
                    // Built-in names, including any user-defined
                    // constants, cannot contain the characters
                    // (, ), +, -, *, /, ^, \, or comma.  They
                    // cannot begin with a number, but are otherwise
                    // unrestricted.
                    while ((NextCharacter != '(') && (NextCharacter != ')') && (NextCharacter != '+')
                            && (NextCharacter != '-') && (NextCharacter != '*') && (NextCharacter != '/')
                            && (NextCharacter != '^') && (NextCharacter != ',') && (NextCharacter != '\\')) {
                        TokenString.append(NextCharacter);
                        I++;
                        CurrentCharacter = NextCharacter;
                        NextCharacter = (I < Expression.length() - 1) ? Expression.charAt(I + 1) : '\\';
                    }
                    String TokenName = TokenString.toString();
                    if (Typ == TemporaryVariable) {
                        Token[TokenNumber] = TokenName;
                        Properties[TokenNumber] = TemporaryVariable;
                    } else if (NextCharacter != '(') {
                        Token[TokenNumber] = InterpretConstantName(TokenName);
                        if (Token[TokenNumber].equals("")) {
                            throw new ComputeConstantNameException(TokenName);
                        }
                        Properties[TokenNumber] = getConstantType(Token[TokenNumber]);
                    } else if (TokenName.equals("for")) {
                        Token[TokenNumber] = "For";
                        Properties[TokenNumber] = ForLoop;
                    } else {
                        Token[TokenNumber] = InterpretFunctionName(TokenName);
                        if (Token[TokenNumber].equals("")) {
                            throw new ComputeFunctionNameException(TokenName);
                        }
                        Properties[TokenNumber] = Function;
                        // We cannot resolve the return type
                        // of the function until we look at
                        // the arguments.
                    }
                    break;
                default:
                    throwSyntaxException(TokenNumber, "This is an unexpected character sequence.");
            }
            PreviousProperty = Properties[TokenNumber];
            I++;
            CurrentCharacter = NextCharacter;
            NextCharacter = (I < Expression.length() - 1) ? Expression.charAt(I + 1) : '\\';
        }
        NumberOfTokens = TokenNumber;
        SetBracketLevels(" SBL Invocation 15 ");
        DisplayTokens("First Tokenization");
        return;
    }

    public String InterpretConstantName(String InputName) {

        String constantName = new String();
        if (BuiltInConstantDictionary.containsKey(InputName)) {
            constantName = (String) BuiltInConstantDictionary.get(InputName);
        } else if (userVariableValueDictionary.containsKey(InputName)) {
            constantName = InputName;
        }
        return constantName;

    }

    public byte getConstantType(String InputName) {

        byte type;
        byte result = ConstantScalar;
        if (displayProgress) {
            diagnosticPrintln("In getConstantType for " + InputName + ":");
        }
        if (ConstantTypeDictionary.containsKey(InputName)) {
            type = ((Byte) ConstantTypeDictionary.get(InputName)).byteValue();
            if (type == VECTOR) {
                result = ConstantVector;
            }
            if (displayProgress) {
                diagnosticPrintln("    internal constant, type dictionary gives type = " + String.valueOf(type));
            }
        } else {
            if (userVariableTypeDictionary.containsKey(InputName)) {
                if (displayProgress) {
                    diagnosticPrintln("Type as Byte is " + (userVariableTypeDictionary.get(InputName)).toString());
                }
                type = ((Byte) userVariableTypeDictionary.get(InputName)).byteValue();
                if (type == VECTOR) {
                    result = ConstantVector;
                }
                if (displayProgress) {
                    diagnosticPrintln("    user variable, type dictionary gives type = " + String.valueOf(type));
                }
            } else {
                result = Remove;
                if (displayProgress) {
                    diagnosticPrintln("    type dictionaries do not contain the constant.");
                }
            }
        }
        if (displayProgress) {
            diagnosticPrintln("getConstantType returns " + String.valueOf(result));
        }
        return result;

    }

    public String InterpretFunctionName(String InputName) {
        String function = new String();
        //
        // If the alias is in the dictionary use the internal name stored there.
        //
        if (FunctionDictionary.containsKey(InputName)) {
            function =
                    (String) FunctionDictionary.get(InputName);
        }
        return function;
    }


    public void ClassifyFunctions() throws ComputeFunctionUseException, ComputeSyntaxException {
// Finds function tokens, counts the number of arguments they have, and changes
// Properties to the appropriate value for the number of arguments.
//
        int I, J, ArgumentBegin, ArgumentEnd, NumberOfCommas, LevelOfFunction;
        for (I = 1; I < NumberOfTokens; I++) {
            if (Properties[I] == Function) {
                if (FunctionListTypeDictionary.containsKey(Token[I])) {
                    Properties[I] = FunctionOfListVariables;
                } else {
                    ArgumentBegin = I + 1;
                    ArgumentEnd = FindPairedBracket(ArgumentBegin, ArgumentBegin, NumberOfTokens);
                    LevelOfFunction = Level[ArgumentBegin];
                    NumberOfCommas = 0;
                    for (J = ArgumentBegin + 1; J < ArgumentEnd; J++) {
                        if ((Properties[J] == Comma) && (Level[J] == LevelOfFunction)) {
                            NumberOfCommas++;
                        }
                    }
                    if (NumberOfCommas == 0) {
                        if (FunctionOneTypeDictionary.containsKey(Token[I])) {
                            Properties[I] = FunctionOfOneVariable;
                        } else {
                            throw new ComputeFunctionUseException(Token[I],
                                    remakeExpression(ArgumentBegin + 1, ArgumentEnd - 1),
                                    "This is not a function of only one argument.");
                        }
                    } else if (NumberOfCommas == 1) {
                        if (FunctionTwoTypeDictionary.containsKey(Token[I])) {
                            Properties[I] = FunctionOfTwoVariables;
                        } else {
                            throw new ComputeFunctionUseException(Token[I],
                                    remakeExpression(ArgumentBegin + 1, ArgumentEnd - 1),
                                    "This is not a function of two arguments.");
                        }
                    } else if (NumberOfCommas == 2) {
                        if (FunctionThreeTypeDictionary.containsKey(Token[I])) {
                            Properties[I] = FunctionOfThreeVariables;
                        } else {
                            throw new ComputeFunctionUseException(Token[I],
                                    remakeExpression(ArgumentBegin + 1, ArgumentEnd - 1),
                                    "This is not a function of three arguments.");
                        }
                    } else {
                        throw new ComputeFunctionUseException(Token[I],
                                remakeExpression(ArgumentBegin + 1, ArgumentEnd - 1),
                                "This is not a function of mmore than three arguments or of an arbitrary list of arguments.");
                    }
                }
            }
        }
    }

    public int ConvertUnaryOperators() throws ComputeSyntaxException {
//
// Convert unary operators to functions of one variable, check consistency
//
        int DidSomething = 0;
        int I = 1;
        byte CurrProp, NextProp;
        NextProp = Properties[1];
        while ((I < NumberOfTokens) && (DidSomething == 0)) {
            CurrProp = NextProp;
            NextProp = Properties[I + 1];
            if (((CurrProp == FunctionOfOneVariable) || (CurrProp == FunctionOfTwoVariables)
                    || (CurrProp == FunctionOfThreeVariables) || (CurrProp == FunctionOfListVariables)) && (NextProp
                    != OpenBracket)) {
                userInterface
                        .println("Error: function name not followed by bracket at token number " + String.valueOf(I));
                return -1; // should really trap error here, or put test somewhere else and trap error !!
            }
            if (CurrProp == UnaryOperator) {
                DidSomething = 1;
                if ((Token[I].equals("-")) && (NextProp == Number)) {
                    Properties[I] = Remove;
                    Token[I + 1] = "-" + Token[I + 1];
//				RemoveMarkedTokens(1, NumberOfTokens);
//				I = I + 1;
                } else if ((I <= NumberOfTokens - 3) && (Token[I].equals("-")) && (NextProp == OpenBracket)
                        && (Properties[I + 2] == Number) && (Properties[I + 3] == CloseBracket)) {
                    Properties[I] = Remove;
                    Properties[I + 1] = Remove;
                    Token[I + 2] = "-" + Token[I + 2];
                    Properties[I + 3] = Remove;
//				RemoveMarkedTokens(1, NumberOfTokens);
//				I = I + 1;
                } else {
                    Token[I] = InterpretFunctionName(Token[I]);
                    Properties[I] = FunctionOfOneVariable;
                    if ((NextProp == ConstantScalar) || (NextProp == ConstantVector) || (NextProp == VariableScalar)
                            || (NextProp == VariableVector) || (NextProp == TemporaryVariable)) {
                        InsertTokenAfter(I + 1, ")", CloseBracket, (byte) 0, false, false);
                        InsertTokenBefore(I + 1, "(", OpenBracket, (byte) 0, false, false);
                        SetBracketLevels(" SBL Invocation 16 ");
//					I = I + 4;
                    } else if ((NextProp == FunctionOfOneVariable) || (NextProp == FunctionOfTwoVariables)
                            || (NextProp == FunctionOfThreeVariables) || (NextProp == FunctionOfListVariables)) {
                        InsertTokenAfter(OtherEndOfArgument(I + 2), ")", CloseBracket, (byte) 0, false, false);
                        InsertTokenBefore(I + 1, "(", OpenBracket, (byte) 0, false, false);
                        SetBracketLevels(" SBL Invocation 17 ");
//					I = I + 4;
                    }
//				else if (NextProp == OpenBracket) {
//					I = I + 2;
//				}
                    else {
                        userInterface
                                .println("ConvertUnaryOperator ended without a result at I = " + String.valueOf(I));
                    }
                    SurroundFunctionWithBrackets(I);
                    SetBracketLevels(" SBL Invocation 18 ");
                }
//			DisplayTokens(  "Searching for unary functions: I = " + String.valueOf(I) );
                // Stop -1
            }
//		else {
//			I = I + 1;
//		}
            I++;
        }
//	SetBracketLevels( " SBL Invocation 19 " );
//	Message$ = "Replaced Unary Operators with functions"
//	DisplayTokens( Message$)
//	 Stop -1
        return DidSomething;
    }

    public double EvaluateScalarForLoop(int FirstToken, int LastToken) throws ComputeExpressionException {
        //
        // This function returns the value of a scalar "for" loop. The token
        // limits are inside the round brackets delimiting the for arguments.
        //
        double forValue = 0.0;
        double replacementValue = 0.0;
        byte SecondArgumentType, ThirdArgumentType, replacementType;
        int FirstArgumentToken, SecondArgumentToken, ThirdArgumentToken, ReplacementToken, BeginSearchToken,
                EndSearchToken;
        int EndArgumentToken, InsideFirstToken, InsideLastToken, FindTokenNumber;
        FirstArgumentToken = FirstToken;
        if (displayProgress) {
            diagnosticPrintln("Scalar for-loop first argument = " + Token[FirstToken]);
        }
        String tempVarName = Token[FirstToken];
        SecondArgumentToken = FirstArgumentToken + 2;
        EndArgumentToken = SecondArgumentToken;
        if (displayProgress) {
            diagnosticPrintln("Scalar for-loop second argument begins with " + Token[SecondArgumentToken]);
        }
        switch (Properties[SecondArgumentToken]) {
            case Number:
                replacementValue = Double.valueOf(Token[SecondArgumentToken]).doubleValue();
                break;
            case ConstantScalar:
                if (ConstantValueDictionary.containsKey(Token[SecondArgumentToken])) {
                    replacementValue = ((double[]) (ConstantValueDictionary.get(Token[SecondArgumentToken])))[0];
                } else {
                    replacementValue = ((double[]) (userVariableValueDictionary.get(Token[SecondArgumentToken])))[0];
                }
                break;
            case VariableScalar:
                replacementValue = ScalarOf(Token[SecondArgumentToken]);
                break;
            case OpenBracket:
                EndArgumentToken = FindPairedBracket(SecondArgumentToken, FirstToken, LastToken);
                InsideFirstToken = SecondArgumentToken + 1;
                InsideLastToken = EndArgumentToken - 1;
                replacementValue = EvaluateScalarBracketPair(InsideFirstToken, InsideLastToken);
                break;
        }
        replacementType = Number;
        ThirdArgumentToken = EndArgumentToken + 2;
        EndArgumentToken = ThirdArgumentToken;
        if (displayProgress) {
            diagnosticPrintln("Scalar for-loop third argument begins with " + Token[ThirdArgumentToken]);
        }
        if (Properties[ThirdArgumentToken] == OpenBracket) {
            BeginSearchToken = ThirdArgumentToken + 1;
            EndSearchToken = FindPairedBracket(ThirdArgumentToken, FirstToken, LastToken) - 1;
            for (FindTokenNumber = BeginSearchToken; FindTokenNumber <= EndSearchToken; FindTokenNumber++) {
                if (Token[FindTokenNumber].equals(tempVarName)) {
                    Token[FindTokenNumber] = String.valueOf(replacementValue);
                    Properties[FindTokenNumber] = Number;
                }
            }
            forValue = EvaluateScalarBracketPair(BeginSearchToken, EndSearchToken);
        } else if (Properties[ThirdArgumentToken] == ForLoop) {
            BeginSearchToken = ThirdArgumentToken + 2;
            EndSearchToken = FindPairedBracket(ThirdArgumentToken + 1, FirstToken, LastToken) - 1;
            for (FindTokenNumber = BeginSearchToken; FindTokenNumber <= EndSearchToken; FindTokenNumber++) {
                if (Token[FindTokenNumber].equals(tempVarName)) {
                    Token[FindTokenNumber] = String.valueOf(replacementValue);
                    Properties[FindTokenNumber] = Number;
                }
            }
            forValue = EvaluateScalarForLoop(BeginSearchToken, EndSearchToken);
        } else {
            if (Token[ThirdArgumentToken].equals(tempVarName)) {
                Token[ThirdArgumentToken] = String.valueOf(replacementValue);
                Properties[ThirdArgumentToken] = Number;
            }
            switch (Properties[ThirdArgumentToken]) {
                case Number:
                    forValue = Double.valueOf(Token[ThirdArgumentToken]).doubleValue();
                    break;
                case ConstantScalar:
                    if (ConstantValueDictionary.containsKey(Token[ThirdArgumentToken])) {
                        forValue = ((double[]) (ConstantValueDictionary.get(Token[ThirdArgumentToken])))[0];
                    } else {
                        forValue = ((double[]) (userVariableValueDictionary.get(Token[ThirdArgumentToken])))[0];
                    }
                    break;
                case VariableScalar:
                    forValue = ScalarOf(Token[ThirdArgumentToken]);
                    break;
            }
        }
        return forValue;
    }

    public double[] EvaluateVectorForLoop(int FirstToken, int LastToken) throws ComputeExpressionException {
        //
        // This function returns the value of a vector "for" loop. The token
        // limits are inside the round brackets delimiting the for arguments.
        //
        double replacementValue;
        double[] replacementValues = new double[1];
        replacementValues[0] = 0.0;
        byte SecondArgumentType, ThirdArgumentType, replacementType;
        int FirstArgumentToken, SecondArgumentToken, ThirdArgumentToken, ReplacementToken, BeginSearchToken,
                EndSearchToken, replace, replacements, locations;
        int EndArgumentToken, InsideFirstToken, InsideLastToken, FindTokenNumber;
        Vector ReplacementLocations = new Vector(5);
        FirstArgumentToken = FirstToken;
        String tempVarName = Token[FirstToken];
        if (displayProgress) {
            diagnosticPrintln("Vector for-loop first argument = " + Token[FirstToken]);
        }
        SecondArgumentToken = FirstArgumentToken + 2;
        EndArgumentToken = SecondArgumentToken;
        if (displayProgress) {
            diagnosticPrintln("Vector for-loop second argument begins with " + Token[SecondArgumentToken]);
        }
        switch (Properties[SecondArgumentToken]) {
            case ConstantVector:
                if (ConstantValueDictionary.containsKey(Token[SecondArgumentToken])) {
                    replacementValues = (double[]) (ConstantValueDictionary.get(Token[SecondArgumentToken]));
                } else {
                    replacementValues = (double[]) (userVariableValueDictionary.get(Token[SecondArgumentToken]));
                }
                break;
            case VariableVector:
                replacementValues = VectorOf(Token[SecondArgumentToken]);
                break;
            case OpenBracket:
                EndArgumentToken = FindPairedBracket(SecondArgumentToken, FirstToken, LastToken);
                InsideFirstToken = SecondArgumentToken + 1;
                InsideLastToken = EndArgumentToken - 1;
                replacementValues = EvaluateVectorBracketPair(InsideFirstToken, InsideLastToken);
                break;
        }
        double[] forValue = new double[replacementValues.length];
        ThirdArgumentToken = EndArgumentToken + 2;
        EndArgumentToken = ThirdArgumentToken;
        BeginSearchToken = ThirdArgumentToken;
        EndSearchToken = ThirdArgumentToken;
        if (displayProgress) {
            diagnosticPrintln("Vector for-loop third argument begins with " + Token[ThirdArgumentToken]);
        }
        if (Properties[ThirdArgumentToken] == OpenBracket) {
            BeginSearchToken = ThirdArgumentToken + 1;
            EndSearchToken = FindPairedBracket(ThirdArgumentToken, FirstToken, LastToken) - 1;
            for (FindTokenNumber = BeginSearchToken; FindTokenNumber <= EndSearchToken; FindTokenNumber++) {
                if (Token[FindTokenNumber].equals(tempVarName)) {
                    ReplacementLocations.addElement(new Integer(FindTokenNumber));
                    Properties[FindTokenNumber] = Number;
                }
            }
        } else if (Properties[ThirdArgumentToken] == ForLoop) {
            BeginSearchToken = ThirdArgumentToken + 2;
            EndSearchToken = FindPairedBracket(ThirdArgumentToken + 1, FirstToken, LastToken) - 1;
            for (FindTokenNumber = BeginSearchToken; FindTokenNumber <= EndSearchToken; FindTokenNumber++) {
                if (Token[FindTokenNumber].equals(tempVarName)) {
                    ReplacementLocations.addElement(new Integer(FindTokenNumber));
                    Properties[FindTokenNumber] = Number;
                }
            }
        } else {
            if (Token[ThirdArgumentToken].equals(tempVarName)) {
                ReplacementLocations.addElement(new Integer(ThirdArgumentToken));
                Properties[ThirdArgumentToken] = Number;
            }
        }
        replacements = ReplacementLocations.size();
        for (replace = 0; replace < replacementValues.length; replace++) {
            replacementValue = replacementValues[replace];
            for (locations = 0; locations < replacements; locations++) {
                Token[((Integer) ReplacementLocations.elementAt(locations)).intValue()] = String
                        .valueOf(replacementValue);
            }
            if (Properties[ThirdArgumentToken] == OpenBracket) {
                forValue[replace] = EvaluateScalarBracketPair(BeginSearchToken, EndSearchToken);
            } else if (Properties[ThirdArgumentToken] == ForLoop) {
                forValue[replace] = EvaluateScalarForLoop(BeginSearchToken, EndSearchToken);
            } else {
                switch (Properties[ThirdArgumentToken]) {
                    case Number:
                        forValue[replace] = Double.valueOf(Token[ThirdArgumentToken]).doubleValue();
                        break;
                    case ConstantScalar:
                        if (ConstantValueDictionary.containsKey(Token[ThirdArgumentToken])) {
                            forValue[replace] = ((double[]) (ConstantValueDictionary.get(
                                    Token[ThirdArgumentToken])))[0];
                        } else {
                            forValue[replace] = ((double[]) (userVariableValueDictionary.get(
                                    Token[ThirdArgumentToken])))[0];
                        }
                        break;
                    case VariableScalar:
                        forValue[replace] = ScalarOf(Token[ThirdArgumentToken]);
                        break;
                }
            }
        }
        return forValue;
    }


    public double EvaluateScalarBracketPair(int FirstToken, int LastToken) throws ComputeExpressionException {
//
// This is an implementation of the recursive method of evaluation.
// This function returns the value of this list of
// tokens, which are obtained as the contents of a scalar-valued bracket pair
// (the brackets are not part of the list).
//
        int EndArgumentToken, InsideFirstToken, InsideLastToken, OperatorToken, ArgumentToken, InsideSecondToken,
                InsideThirdToken;
        double BracketValue = 0.0;
        boolean ArgumentIsScalar = true;
        boolean FirstArgumentIsScalar = true;
        boolean SecondArgumentIsScalar = true;
        boolean ThirdArgumentIsScalar = true;
        boolean moreArguments = true;
        double ScalarArgumentValue = 0.0;
        double ScalarFirstArgumentValue = 0.0;
        double ScalarSecondArgumentValue = 0.0;
        double ScalarThirdArgumentValue = 0.0;
        double VectorArgumentValue[] = {0.0};
        double VectorFirstArgumentValue[] = {0.0};
        double VectorSecondArgumentValue[] = {0.0};
        double VectorThirdArgumentValue[] = {0.0};
        Vector ListOfArguments = new Vector(10);
//
// If the input list is only one token, it should be a constant whose value is just to
// be returned.
//
        if (trivialExpression) {
            switch (Properties[FirstToken]) {
                case Number:
                    BracketValue = Double.valueOf(Token[FirstToken]).doubleValue();
                    break;
                case ConstantScalar:
                    if (ConstantValueDictionary.containsKey(Token[FirstToken])) {
                        BracketValue = ((double[]) (ConstantValueDictionary.get(Token[FirstToken])))[0];
                    } else {
                        BracketValue = ((double[]) (userVariableValueDictionary.get(Token[FirstToken])))[0];
                    }
                    break;
                case VariableScalar:
                    BracketValue = ScalarOf(Token[FirstToken]);
                    break;
            }
        } else {
            switch (Properties[FirstToken]) {
                case FunctionOfOneVariable:
                    ArgumentToken = FirstToken + 2;
                    if (Type[ArgumentToken] == SCALAR) {
                        FirstArgumentIsScalar = true;
                        VectorFirstArgumentValue = new double[1];
                    } else {
                        FirstArgumentIsScalar = false;
                        VectorFirstArgumentValue = new double[1];
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (FirstArgumentIsScalar) {
                                ScalarFirstArgumentValue = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorFirstArgumentValue = EvaluateVectorForLoop(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (FirstArgumentIsScalar) {
                                ScalarFirstArgumentValue = EvaluateScalarBracketPair(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorFirstArgumentValue = EvaluateVectorBracketPair(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarFirstArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarFirstArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarFirstArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarFirstArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorFirstArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorFirstArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarFirstArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorFirstArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    if (FirstArgumentIsScalar) {
                        BracketValue = EvaluateScalarFunctionOfOneVariable(Token[FirstToken], ScalarFirstArgumentValue);
                    } else {
                        BracketValue = EvaluateScalarFunctionOfOneVariable(Token[FirstToken], VectorFirstArgumentValue);
                    }
                    break;
                case FunctionOfTwoVariables:
                    ArgumentToken = FirstToken + 2;
                    EndArgumentToken = ArgumentToken;
                    if (Type[ArgumentToken] == SCALAR) {
                        FirstArgumentIsScalar = true;
                        VectorFirstArgumentValue = new double[1];
                    } else {
                        FirstArgumentIsScalar = false;
                        VectorFirstArgumentValue = new double[1];
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (FirstArgumentIsScalar) {
                                ScalarFirstArgumentValue = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorFirstArgumentValue = EvaluateVectorForLoop(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (FirstArgumentIsScalar) {
                                ScalarFirstArgumentValue = EvaluateScalarBracketPair(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorFirstArgumentValue = EvaluateVectorBracketPair(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarFirstArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarFirstArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarFirstArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarFirstArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorFirstArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorFirstArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarFirstArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorFirstArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    ArgumentToken = EndArgumentToken + 2;
                    if (Type[ArgumentToken] == SCALAR) {
                        SecondArgumentIsScalar = true;
                        VectorSecondArgumentValue = new double[1];
                    } else {
                        SecondArgumentIsScalar = false;
                        VectorSecondArgumentValue = new double[1];
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (SecondArgumentIsScalar) {
                                ScalarSecondArgumentValue = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorSecondArgumentValue = EvaluateVectorForLoop(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (SecondArgumentIsScalar) {
                                ScalarSecondArgumentValue = EvaluateScalarBracketPair(InsideFirstToken,
                                        InsideLastToken);
                            } else {
                                VectorSecondArgumentValue = EvaluateVectorBracketPair(InsideFirstToken,
                                        InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarSecondArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarSecondArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarSecondArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarSecondArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorSecondArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorSecondArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarSecondArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorSecondArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    if (FirstArgumentIsScalar) {
                        if (SecondArgumentIsScalar) {
                            BracketValue = EvaluateScalarFunctionOfTwoVariables(Token[FirstToken],
                                    ScalarFirstArgumentValue, ScalarSecondArgumentValue);
                        } else {
                            BracketValue = EvaluateScalarFunctionOfTwoVariables(Token[FirstToken],
                                    ScalarFirstArgumentValue, VectorSecondArgumentValue);
                        }
                    } else {
                        if (SecondArgumentIsScalar) {
                            BracketValue = EvaluateScalarFunctionOfTwoVariables(Token[FirstToken],
                                    VectorFirstArgumentValue, ScalarSecondArgumentValue);
                        } else {
                            BracketValue = EvaluateScalarFunctionOfTwoVariables(Token[FirstToken],
                                    VectorFirstArgumentValue, VectorSecondArgumentValue);
                        }
                    }
                    break;
                case FunctionOfThreeVariables:
                    ArgumentToken = FirstToken + 2;
                    EndArgumentToken = ArgumentToken;
                    if (Type[ArgumentToken] == SCALAR) {
                        FirstArgumentIsScalar = true;
                        VectorFirstArgumentValue = new double[1];
                    } else {
                        FirstArgumentIsScalar = false;
                        VectorFirstArgumentValue = new double[1];
                    }
                    if (!FirstArgumentIsScalar) {
                        if (Type[ArgumentToken] == SCALAR) {
                            FirstArgumentIsScalar = true;
                        } else {
                            VectorFirstArgumentValue = new double[1];
                        }
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (FirstArgumentIsScalar) {
                                ScalarFirstArgumentValue = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorFirstArgumentValue = EvaluateVectorForLoop(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (FirstArgumentIsScalar) {
                                ScalarFirstArgumentValue = EvaluateScalarBracketPair(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorFirstArgumentValue = EvaluateVectorBracketPair(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarFirstArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarFirstArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarFirstArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarFirstArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorFirstArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorFirstArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarFirstArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorFirstArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    ArgumentToken = EndArgumentToken + 2;
                    EndArgumentToken = ArgumentToken;
                    if (Type[ArgumentToken] == SCALAR) {
                        SecondArgumentIsScalar = true;
                        VectorSecondArgumentValue = new double[1];
                    } else {
                        SecondArgumentIsScalar = false;
                        VectorSecondArgumentValue = new double[1];
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideSecondToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (SecondArgumentIsScalar) {
                                ScalarSecondArgumentValue = EvaluateScalarForLoop(InsideSecondToken, InsideLastToken);
                            } else {
                                VectorFirstArgumentValue = EvaluateVectorForLoop(InsideSecondToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, ArgumentToken, LastToken);
                            InsideSecondToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (SecondArgumentIsScalar) {
                                ScalarSecondArgumentValue = EvaluateScalarBracketPair(InsideSecondToken,
                                        InsideLastToken);
                            } else {
                                VectorSecondArgumentValue = EvaluateVectorBracketPair(InsideSecondToken,
                                        InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarSecondArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarSecondArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarSecondArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarSecondArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorSecondArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorSecondArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarSecondArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorSecondArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    ArgumentToken = EndArgumentToken + 2;
                    if (Type[ArgumentToken] == SCALAR) {
                        ThirdArgumentIsScalar = true;
                        VectorThirdArgumentValue = new double[1];
                    } else {
                        ThirdArgumentIsScalar = false;
                        VectorThirdArgumentValue = new double[1];
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (ThirdArgumentIsScalar) {
                                ScalarThirdArgumentValue = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorThirdArgumentValue = EvaluateVectorForLoop(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, ArgumentToken, LastToken);
                            InsideThirdToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (ThirdArgumentIsScalar) {
                                ScalarThirdArgumentValue = EvaluateScalarBracketPair(InsideThirdToken, InsideLastToken);
                            } else {
                                VectorThirdArgumentValue = EvaluateVectorBracketPair(InsideThirdToken, InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarThirdArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarThirdArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarThirdArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarThirdArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorThirdArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorThirdArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarThirdArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorThirdArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    if (FirstArgumentIsScalar) {
                        if (SecondArgumentIsScalar) {
                            if (ThirdArgumentIsScalar) {
                                BracketValue = EvaluateScalarFunctionOfThreeVariables(Token[FirstToken],
                                        ScalarFirstArgumentValue, ScalarSecondArgumentValue, ScalarThirdArgumentValue);
                            } else {
                                BracketValue = EvaluateScalarFunctionOfThreeVariables(Token[FirstToken],
                                        ScalarFirstArgumentValue, ScalarSecondArgumentValue, VectorThirdArgumentValue);
                            }
                        } else {
                            if (ThirdArgumentIsScalar) {
                                BracketValue = EvaluateScalarFunctionOfThreeVariables(Token[FirstToken],
                                        ScalarFirstArgumentValue, VectorSecondArgumentValue, ScalarThirdArgumentValue);
                            } else {
                                BracketValue = EvaluateScalarFunctionOfThreeVariables(Token[FirstToken],
                                        ScalarFirstArgumentValue, VectorSecondArgumentValue, VectorThirdArgumentValue);
                            }
                        }
                    } else {
                        if (SecondArgumentIsScalar) {
                            if (ThirdArgumentIsScalar) {
                                BracketValue = EvaluateScalarFunctionOfThreeVariables(Token[FirstToken],
                                        VectorFirstArgumentValue, ScalarSecondArgumentValue, ScalarThirdArgumentValue);
                            } else {
                                BracketValue = EvaluateScalarFunctionOfThreeVariables(Token[FirstToken],
                                        VectorFirstArgumentValue, ScalarSecondArgumentValue, VectorThirdArgumentValue);
                            }
                        } else {
                            if (ThirdArgumentIsScalar) {
                                BracketValue = EvaluateScalarFunctionOfThreeVariables(Token[FirstToken],
                                        VectorFirstArgumentValue, VectorSecondArgumentValue, ScalarThirdArgumentValue);
                            } else {
                                BracketValue = EvaluateScalarFunctionOfThreeVariables(Token[FirstToken],
                                        VectorFirstArgumentValue, VectorSecondArgumentValue, VectorThirdArgumentValue);
                            }
                        }
                    }
                    break;
                case FunctionOfListVariables:
                    ArgumentToken = FirstToken + 2;
                    EndArgumentToken = ArgumentToken;
                    moreArguments = true;
                    while (moreArguments) {
                        if (Type[ArgumentToken] == SCALAR) {
                            ArgumentIsScalar = true;
                            VectorArgumentValue = new double[1];
                        } else {
                            ArgumentIsScalar = false;
                            VectorArgumentValue = new double[1];
                        }
                        if (!ArgumentIsScalar) {
                            if (Type[ArgumentToken] == SCALAR) {
                                ArgumentIsScalar = true;
                            } else {
                                VectorArgumentValue = new double[1];
                            }
                        }
                        switch (Properties[ArgumentToken]) {
                            case ForLoop:
                                EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                                InsideFirstToken = ArgumentToken + 2;
                                InsideLastToken = EndArgumentToken - 1;
                                if (ArgumentIsScalar) {
                                    VectorArgumentValue[0] = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                                } else {
                                    VectorArgumentValue = EvaluateVectorForLoop(InsideFirstToken, InsideLastToken);
                                }
                                break;
                            case OpenBracket:
                                EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                                InsideFirstToken = ArgumentToken + 1;
                                InsideLastToken = EndArgumentToken - 1;
                                if (ArgumentIsScalar) {
                                    VectorArgumentValue[0] = EvaluateScalarBracketPair(InsideFirstToken,
                                            InsideLastToken);
                                } else {
                                    VectorArgumentValue = EvaluateVectorBracketPair(InsideFirstToken, InsideLastToken);
                                }
                                break;
                            case Number:
                                VectorArgumentValue[0] = Double.valueOf(Token[ArgumentToken]).doubleValue();
                                break;
                            case ConstantScalar:
                                if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                    VectorArgumentValue = (double[]) (ConstantValueDictionary.get(
                                            Token[ArgumentToken]));
                                } else {
                                    VectorArgumentValue = (double[]) (userVariableValueDictionary.get(
                                            Token[ArgumentToken]));
                                }
                                break;
                            case TemporaryVariable:
                                VectorArgumentValue = (double[]) (temporaryVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            case ConstantVector:
                                if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                    VectorArgumentValue = (double[]) (ConstantValueDictionary.get(
                                            Token[ArgumentToken]));
                                } else {
                                    VectorArgumentValue = (double[]) (userVariableValueDictionary.get(
                                            Token[ArgumentToken]));
                                }
                                break;
                            case VariableScalar:
                                VectorArgumentValue[0] = ScalarOf(Token[ArgumentToken]);
                                break;
                            case VariableVector:
                                VectorArgumentValue = VectorOf(Token[ArgumentToken]);
                                break;
                        }
                        ListOfArguments.addElement(VectorArgumentValue);
                        if (Properties[EndArgumentToken + 1] != Comma) {
                            moreArguments = false;
                        } else {
                            ArgumentToken = EndArgumentToken + 2;
                            EndArgumentToken = ArgumentToken;
                        }
                    }
                    BracketValue = EvaluateScalarFunctionOfListVariables(Token[FirstToken], ListOfArguments);
                    break;
                case Number:
                case ConstantScalar:
                case TemporaryVariable:
                case VariableScalar:
                case ConstantVector:
                case VariableVector:
                    switch (Properties[FirstToken]) {
                        case Number:
                            ScalarFirstArgumentValue = Double.valueOf(Token[FirstToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[FirstToken])) {
                                ScalarFirstArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[FirstToken])))[0];
                            } else {
                                ScalarFirstArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[FirstToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarFirstArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[FirstToken])))[0];
                        case ConstantVector:
                            userInterface.println(
                                    "Constant vector argument in scalar-valued binary op. Loc1"); // trap this error!!
                            break;
                        case VariableScalar:
                            ScalarFirstArgumentValue = ScalarOf(Token[FirstToken]);
                            break;
                        case VariableVector:
                            userInterface.println(
                                    "Variable vector argument in scalar-valued binary op. Loc1"); // trap this error!!
                            break;
                    }
                    OperatorToken = FirstToken + 1;
                    ArgumentToken = OperatorToken + 1;
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideSecondToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            ScalarSecondArgumentValue = EvaluateScalarForLoop(InsideSecondToken, InsideLastToken);
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, ArgumentToken, LastToken);
                            InsideSecondToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            ScalarSecondArgumentValue = EvaluateScalarBracketPair(InsideSecondToken, InsideLastToken);
                            break;
                        case Number:
                            ScalarSecondArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarSecondArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarSecondArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarSecondArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            userInterface.println(
                                    "Constant vector argument in scalar-valued binary op. Loc2"); // trap this error!!
                            break;
                        case VariableScalar:
                            ScalarSecondArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            userInterface.println(
                                    "Variable vector argument in scalar-valued binary op. Loc2"); // trap this error!!
                            break;
                    }
                    BracketValue = EvaluateScalarBinaryOperator(Token[OperatorToken], ScalarFirstArgumentValue,
                            ScalarSecondArgumentValue);
                    break;
                case OpenBracket:
                case ForLoop:
                    if (Properties[FirstToken] == ForLoop) {
                        EndArgumentToken = FindPairedBracket(FirstToken + 1, FirstToken, LastToken);
                        InsideFirstToken = FirstToken + 2;
                        InsideLastToken = EndArgumentToken - 1;
                        ScalarFirstArgumentValue = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                    } else {
                        EndArgumentToken = FindPairedBracket(FirstToken, FirstToken, LastToken);
                        InsideFirstToken = FirstToken + 1;
                        InsideLastToken = EndArgumentToken - 1;
                        ScalarFirstArgumentValue = EvaluateScalarBracketPair(InsideFirstToken, InsideLastToken);
                    }
                    OperatorToken = EndArgumentToken + 1;
                    ArgumentToken = OperatorToken + 1;
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            ScalarSecondArgumentValue = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            ScalarSecondArgumentValue = EvaluateScalarBracketPair(InsideFirstToken, InsideLastToken);
                            break;
                        case Number:
                            ScalarSecondArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarSecondArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarSecondArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarSecondArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            userInterface.println(
                                    "Constant vector argument in scalar-valued binary op. Loc3"); // trap this error!!
                            break;
                        case VariableScalar:
                            ScalarSecondArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            userInterface.println(
                                    "Variable vector argument in scalar-valued binary op. Loc3"); // trap this error!!
                            break;
                    }
                    BracketValue = EvaluateScalarBinaryOperator(Token[OperatorToken], ScalarFirstArgumentValue,
                            ScalarSecondArgumentValue);
                    break;
            }
        }
        return BracketValue;
    }


    public double[] EvaluateVectorBracketPair(int FirstToken, int LastToken) throws ComputeExpressionException {
//
// This is an implementation of the recursive method of evaluation.
// This function returns the value of this list of
// tokens, which are obtained as the contents of a vector-valued bracket pair
// (the brackets are not part of the list).
//
        int EndArgumentToken, InsideLastToken, OperatorToken, ArgumentToken;
        int InsideFirstToken, InsideSecondToken, InsideThirdToken;
        double[] BracketValue = new double[1];
        boolean ArgumentIsScalar = true;
        boolean FirstArgumentIsScalar = true;
        boolean SecondArgumentIsScalar = true;
        boolean ThirdArgumentIsScalar = true;
        boolean moreArguments = true;
        double ScalarFirstArgumentValue = 0.0;
        double ScalarSecondArgumentValue = 0.0;
        double ScalarThirdArgumentValue = 0.0;
        double VectorArgumentValue[] = {0.0};
        double VectorFirstArgumentValue[] = {0.0};
        double VectorSecondArgumentValue[] = {0};
        double VectorThirdArgumentValue[] = {0};
        Vector ListOfArguments = new Vector(10);
//
// If the input list is only one token, it should be a constant or variable
// vector whose value is just to be returned.
//
        if (trivialExpression) {
            switch (Properties[FirstToken]) {
                case ConstantVector:
                    if (ConstantValueDictionary.containsKey(Token[FirstToken])) {
                        BracketValue = (double[]) (ConstantValueDictionary.get(Token[FirstToken]));
                    } else {
                        BracketValue = (double[]) (userVariableValueDictionary.get(Token[FirstToken]));
                    }
                    break;
                case VariableVector:
                    BracketValue = VectorOf(Token[FirstToken]);
                    break;
            }

        } else {
            switch (Properties[FirstToken]) {
                case FunctionOfOneVariable:
                    ArgumentToken = FirstToken + 2;
                    if (Type[ArgumentToken] == SCALAR) {
                        FirstArgumentIsScalar = true;
                        VectorFirstArgumentValue = new double[1];
                    } else {
                        FirstArgumentIsScalar = false;
                        VectorFirstArgumentValue = new double[1];
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (FirstArgumentIsScalar) {
                                ScalarFirstArgumentValue = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorFirstArgumentValue = EvaluateVectorForLoop(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (FirstArgumentIsScalar) {
                                ScalarFirstArgumentValue = EvaluateScalarBracketPair(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorFirstArgumentValue = EvaluateVectorBracketPair(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarFirstArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarFirstArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarFirstArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarFirstArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorFirstArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorFirstArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarFirstArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorFirstArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    if (FirstArgumentIsScalar) {
                        BracketValue = EvaluateVectorFunctionOfOneVariable(Token[FirstToken], ScalarFirstArgumentValue);
                    } else {
                        BracketValue = EvaluateVectorFunctionOfOneVariable(Token[FirstToken], VectorFirstArgumentValue);
                    }
                    break;
                case FunctionOfTwoVariables:
                    ArgumentToken = FirstToken + 2;
                    EndArgumentToken = ArgumentToken;
                    if (Type[ArgumentToken] == SCALAR) {
                        FirstArgumentIsScalar = true;
                        VectorFirstArgumentValue = new double[1];
                    } else {
                        FirstArgumentIsScalar = false;
                        VectorFirstArgumentValue = new double[1];
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (FirstArgumentIsScalar) {
                                ScalarFirstArgumentValue = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorFirstArgumentValue = EvaluateVectorForLoop(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (FirstArgumentIsScalar) {
                                ScalarFirstArgumentValue = EvaluateScalarBracketPair(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorFirstArgumentValue = EvaluateVectorBracketPair(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarFirstArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarFirstArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarFirstArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarFirstArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorFirstArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorFirstArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarFirstArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorFirstArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    ArgumentToken = EndArgumentToken + 2;
                    if (Type[ArgumentToken] == SCALAR) {
                        SecondArgumentIsScalar = true;
                        VectorSecondArgumentValue = new double[1];
                    } else {
                        SecondArgumentIsScalar = false;
                        VectorSecondArgumentValue = new double[1];
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (SecondArgumentIsScalar) {
                                ScalarSecondArgumentValue = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorSecondArgumentValue = EvaluateVectorForLoop(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (SecondArgumentIsScalar) {
                                ScalarSecondArgumentValue = EvaluateScalarBracketPair(InsideFirstToken,
                                        InsideLastToken);
                            } else {
                                VectorSecondArgumentValue = EvaluateVectorBracketPair(InsideFirstToken,
                                        InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarSecondArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarSecondArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarSecondArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarSecondArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorSecondArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorSecondArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarSecondArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorSecondArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    if (FirstArgumentIsScalar) {
                        if (SecondArgumentIsScalar) {
                            BracketValue = EvaluateVectorFunctionOfTwoVariables(Token[FirstToken],
                                    ScalarFirstArgumentValue, ScalarSecondArgumentValue);
                        } else {
                            BracketValue = EvaluateVectorFunctionOfTwoVariables(Token[FirstToken],
                                    ScalarFirstArgumentValue, VectorSecondArgumentValue);
                        }
                    } else {
                        if (SecondArgumentIsScalar) {
                            BracketValue = EvaluateVectorFunctionOfTwoVariables(Token[FirstToken],
                                    VectorFirstArgumentValue, ScalarSecondArgumentValue);
                        } else {
                            BracketValue = EvaluateVectorFunctionOfTwoVariables(Token[FirstToken],
                                    VectorFirstArgumentValue, VectorSecondArgumentValue);
                        }
                    }
                    break;
                case FunctionOfThreeVariables:
                    ArgumentToken = FirstToken + 2;
                    EndArgumentToken = ArgumentToken;
                    if (Type[ArgumentToken] == SCALAR) {
                        FirstArgumentIsScalar = true;
                        VectorFirstArgumentValue = new double[1];
                    } else {
                        FirstArgumentIsScalar = false;
                        VectorFirstArgumentValue = new double[1];
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (FirstArgumentIsScalar) {
                                ScalarFirstArgumentValue = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorFirstArgumentValue = EvaluateVectorForLoop(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                            InsideFirstToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (FirstArgumentIsScalar) {
                                ScalarFirstArgumentValue = EvaluateScalarBracketPair(InsideFirstToken, InsideLastToken);
                            } else {
                                VectorFirstArgumentValue = EvaluateVectorBracketPair(InsideFirstToken, InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarFirstArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarFirstArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarFirstArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarFirstArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorFirstArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorFirstArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarFirstArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorFirstArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    ArgumentToken = EndArgumentToken + 2;
                    EndArgumentToken = ArgumentToken;
                    if (Type[ArgumentToken] == SCALAR) {
                        SecondArgumentIsScalar = true;
                        VectorSecondArgumentValue = new double[1];
                    } else {
                        SecondArgumentIsScalar = false;
                        VectorSecondArgumentValue = new double[1];
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideSecondToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (SecondArgumentIsScalar) {
                                ScalarSecondArgumentValue = EvaluateScalarForLoop(InsideSecondToken, InsideLastToken);
                            } else {
                                VectorSecondArgumentValue = EvaluateVectorForLoop(InsideSecondToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, ArgumentToken, LastToken);
                            InsideSecondToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (SecondArgumentIsScalar) {
                                ScalarSecondArgumentValue = EvaluateScalarBracketPair(InsideSecondToken,
                                        InsideLastToken);
                            } else {
                                VectorSecondArgumentValue = EvaluateVectorBracketPair(InsideSecondToken,
                                        InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarSecondArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarSecondArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarSecondArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarSecondArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorSecondArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorSecondArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarSecondArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorSecondArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    ArgumentToken = EndArgumentToken + 2;
                    if (Type[ArgumentToken] == SCALAR) {
                        ThirdArgumentIsScalar = true;
                        VectorThirdArgumentValue = new double[1];
                    } else {
                        ThirdArgumentIsScalar = false;
                        VectorThirdArgumentValue = new double[1];
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideThirdToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (ThirdArgumentIsScalar) {
                                ScalarThirdArgumentValue = EvaluateScalarForLoop(InsideThirdToken, InsideLastToken);
                            } else {
                                VectorThirdArgumentValue = EvaluateVectorForLoop(InsideThirdToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, ArgumentToken, LastToken);
                            InsideThirdToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (ThirdArgumentIsScalar) {
                                ScalarThirdArgumentValue = EvaluateScalarBracketPair(InsideThirdToken, InsideLastToken);
                            } else {
                                VectorThirdArgumentValue = EvaluateVectorBracketPair(InsideThirdToken, InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarThirdArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarThirdArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarThirdArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarThirdArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorThirdArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorThirdArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarThirdArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorThirdArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    if (FirstArgumentIsScalar) {
                        if (SecondArgumentIsScalar) {
                            if (ThirdArgumentIsScalar) {
                                BracketValue = EvaluateVectorFunctionOfThreeVariables(Token[FirstToken],
                                        ScalarFirstArgumentValue, ScalarSecondArgumentValue, ScalarThirdArgumentValue);
                            } else {
                                BracketValue = EvaluateVectorFunctionOfThreeVariables(Token[FirstToken],
                                        ScalarFirstArgumentValue, ScalarSecondArgumentValue, VectorThirdArgumentValue);
                            }
                        } else {
                            if (ThirdArgumentIsScalar) {
                                BracketValue = EvaluateVectorFunctionOfThreeVariables(Token[FirstToken],
                                        ScalarFirstArgumentValue, VectorSecondArgumentValue, ScalarThirdArgumentValue);
                            } else {
                                BracketValue = EvaluateVectorFunctionOfThreeVariables(Token[FirstToken],
                                        ScalarFirstArgumentValue, VectorSecondArgumentValue, VectorThirdArgumentValue);
                            }
                        }
                    } else {
                        if (SecondArgumentIsScalar) {
                            if (ThirdArgumentIsScalar) {
                                BracketValue = EvaluateVectorFunctionOfThreeVariables(Token[FirstToken],
                                        VectorFirstArgumentValue, ScalarSecondArgumentValue, ScalarThirdArgumentValue);
                            } else {
                                BracketValue = EvaluateVectorFunctionOfThreeVariables(Token[FirstToken],
                                        VectorFirstArgumentValue, ScalarSecondArgumentValue, VectorThirdArgumentValue);
                            }
                        } else {
                            if (ThirdArgumentIsScalar) {
                                BracketValue = EvaluateVectorFunctionOfThreeVariables(Token[FirstToken],
                                        VectorFirstArgumentValue, VectorSecondArgumentValue, ScalarThirdArgumentValue);
                            } else {
                                BracketValue = EvaluateVectorFunctionOfThreeVariables(Token[FirstToken],
                                        VectorFirstArgumentValue, VectorSecondArgumentValue, VectorThirdArgumentValue);
                            }
                        }
                    }
                    break;
                case FunctionOfListVariables:
                    ArgumentToken = FirstToken + 2;
                    EndArgumentToken = ArgumentToken;
                    moreArguments = true;
                    while (moreArguments) {
                        if (Type[ArgumentToken] == SCALAR) {
                            ArgumentIsScalar = true;
                            VectorArgumentValue = new double[1];
                        } else {
                            ArgumentIsScalar = false;
                            VectorArgumentValue = new double[1];
                        }
                        if (!ArgumentIsScalar) {
                            if (Type[ArgumentToken] == SCALAR) {
                                ArgumentIsScalar = true;
                            } else {
                                VectorArgumentValue = new double[1];
                            }
                        }
                        switch (Properties[ArgumentToken]) {
                            case ForLoop:
                                EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                                InsideFirstToken = ArgumentToken + 2;
                                InsideLastToken = EndArgumentToken - 1;
                                if (FirstArgumentIsScalar) {
                                    VectorArgumentValue[0] = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                                } else {
                                    VectorArgumentValue = EvaluateVectorForLoop(InsideFirstToken, InsideLastToken);
                                }
                                break;
                            case OpenBracket:
                                EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                                InsideFirstToken = ArgumentToken + 1;
                                InsideLastToken = EndArgumentToken - 1;
                                if (ArgumentIsScalar) {
                                    VectorArgumentValue[0] = EvaluateScalarBracketPair(InsideFirstToken,
                                            InsideLastToken);
                                } else {
                                    VectorArgumentValue = EvaluateVectorBracketPair(InsideFirstToken, InsideLastToken);
                                }
                                break;
                            case Number:
                                VectorArgumentValue[0] = Double.valueOf(Token[ArgumentToken]).doubleValue();
                                break;
                            case ConstantScalar:
                                if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                    VectorArgumentValue = (double[]) (ConstantValueDictionary.get(
                                            Token[ArgumentToken]));
                                } else {
                                    VectorArgumentValue = (double[]) (userVariableValueDictionary.get(
                                            Token[ArgumentToken]));
                                }
                                break;
                            case TemporaryVariable:
                                VectorArgumentValue = (double[]) (temporaryVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            case ConstantVector:
                                if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                    VectorArgumentValue = (double[]) (ConstantValueDictionary.get(
                                            Token[ArgumentToken]));
                                } else {
                                    VectorArgumentValue = (double[]) (userVariableValueDictionary.get(
                                            Token[ArgumentToken]));
                                }
                                break;
                            case VariableScalar:
                                VectorArgumentValue[0] = ScalarOf(Token[ArgumentToken]);
                                break;
                            case VariableVector:
                                VectorArgumentValue = VectorOf(Token[ArgumentToken]);
                                break;
                        }
                        ListOfArguments.addElement(VectorArgumentValue);
                        if (Properties[EndArgumentToken + 1] != Comma) {
                            moreArguments = false;
                        } else {
                            ArgumentToken = EndArgumentToken + 2;
                            EndArgumentToken = ArgumentToken;
                        }
                    }
                    BracketValue = EvaluateVectorFunctionOfListVariables(Token[FirstToken], ListOfArguments);
                    break;
                case Number:
                case ConstantScalar:
                case VariableScalar:
                case TemporaryVariable:
                case ConstantVector:
                case VariableVector:
                    switch (Properties[FirstToken]) {
                        case Number:
                            FirstArgumentIsScalar = true;
                            VectorFirstArgumentValue = new double[1];
                            ScalarFirstArgumentValue = Double.valueOf(Token[FirstToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            FirstArgumentIsScalar = true;
                            VectorFirstArgumentValue = new double[1];
                            if (ConstantValueDictionary.containsKey(Token[FirstToken])) {
                                ScalarFirstArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[FirstToken])))[0];
                            } else {
                                ScalarFirstArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[FirstToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            FirstArgumentIsScalar = true;
                            VectorFirstArgumentValue = new double[1];
                            ScalarFirstArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[FirstToken])))[0];
                        case ConstantVector:
                            FirstArgumentIsScalar = false;
                            if (ConstantValueDictionary.containsKey(Token[FirstToken])) {
                                VectorFirstArgumentValue = (double[]) (ConstantValueDictionary.get(Token[FirstToken]));
                            } else {
                                VectorFirstArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[FirstToken]));
                            }
                            break;
                        case VariableScalar:
                            FirstArgumentIsScalar = true;
                            VectorFirstArgumentValue = new double[1];
                            ScalarFirstArgumentValue = ScalarOf(Token[FirstToken]);
                            break;
                        case VariableVector:
                            FirstArgumentIsScalar = false;
                            VectorFirstArgumentValue = VectorOf(Token[FirstToken]);
                            break;
                    }
                    OperatorToken = FirstToken + 1;
                    ArgumentToken = OperatorToken + 1;
                    if (Type[ArgumentToken] == SCALAR) {
                        SecondArgumentIsScalar = true;
                        VectorSecondArgumentValue = new double[1];
                    } else {
                        SecondArgumentIsScalar = false;
                        VectorSecondArgumentValue = new double[1];
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideSecondToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (SecondArgumentIsScalar) {
                                ScalarSecondArgumentValue = EvaluateScalarForLoop(InsideSecondToken, InsideLastToken);
                            } else {
                                VectorSecondArgumentValue = EvaluateVectorForLoop(InsideSecondToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, ArgumentToken, LastToken);
                            InsideSecondToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (SecondArgumentIsScalar) {
                                ScalarSecondArgumentValue = EvaluateScalarBracketPair(InsideSecondToken,
                                        InsideLastToken);
                            } else {
                                VectorSecondArgumentValue = EvaluateVectorBracketPair(InsideSecondToken,
                                        InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarSecondArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarSecondArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarSecondArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarSecondArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorSecondArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorSecondArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarSecondArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorSecondArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    if (FirstArgumentIsScalar) {
                        if (SecondArgumentIsScalar) {
                            userInterface.println(
                                    "Cannot evaluate a vector binary operator with two scalar arguments, at Loc1.");  // trap this error!!
                        } else {
                            BracketValue = EvaluateVectorBinaryOperator(Token[OperatorToken], ScalarFirstArgumentValue,
                                    VectorSecondArgumentValue);
                        }
                    } else {
                        if (SecondArgumentIsScalar) {
                            BracketValue = EvaluateVectorBinaryOperator(Token[OperatorToken], VectorFirstArgumentValue,
                                    ScalarSecondArgumentValue);
                        } else {
                            BracketValue = EvaluateVectorBinaryOperator(Token[OperatorToken], VectorFirstArgumentValue,
                                    VectorSecondArgumentValue);
                        }
                    }
                    break;
                case OpenBracket:
                case ForLoop:
                    if (Type[FirstToken] == SCALAR) {
                        FirstArgumentIsScalar = true;
                        VectorFirstArgumentValue = new double[1];
                    } else {
                        FirstArgumentIsScalar = false;
                        VectorFirstArgumentValue = new double[1];
                    }
                    if (Properties[FirstToken] == ForLoop) {
                        EndArgumentToken = FindPairedBracket(FirstToken + 1, FirstToken, LastToken);
                        InsideFirstToken = FirstToken + 2;
                        InsideLastToken = EndArgumentToken - 1;
                        if (FirstArgumentIsScalar) {
                            ScalarFirstArgumentValue = EvaluateScalarForLoop(InsideFirstToken, InsideLastToken);
                        } else {
                            VectorFirstArgumentValue = EvaluateVectorForLoop(InsideFirstToken, InsideLastToken);
                        }
                    } else {
                        EndArgumentToken = FindPairedBracket(FirstToken, FirstToken, LastToken);
                        InsideFirstToken = FirstToken + 1;
                        InsideLastToken = EndArgumentToken - 1;
                        if (FirstArgumentIsScalar) {
                            ScalarFirstArgumentValue = EvaluateScalarBracketPair(InsideFirstToken, InsideLastToken);
                        } else {
                            VectorFirstArgumentValue = EvaluateVectorBracketPair(InsideFirstToken, InsideLastToken);
                        }
                    }
                    OperatorToken = EndArgumentToken + 1;
                    ArgumentToken = OperatorToken + 1;
                    if (Type[ArgumentToken] == SCALAR) {
                        SecondArgumentIsScalar = true;
                        VectorSecondArgumentValue = new double[1];
                    } else {
                        SecondArgumentIsScalar = false;
                        VectorSecondArgumentValue = new double[1];
                    }
                    switch (Properties[ArgumentToken]) {
                        case ForLoop:
                            EndArgumentToken = FindPairedBracket(ArgumentToken + 1, FirstToken, LastToken);
                            InsideSecondToken = ArgumentToken + 2;
                            InsideLastToken = EndArgumentToken - 1;
                            if (SecondArgumentIsScalar) {
                                ScalarSecondArgumentValue = EvaluateScalarForLoop(InsideSecondToken, InsideLastToken);
                            } else {
                                VectorSecondArgumentValue = EvaluateVectorForLoop(InsideSecondToken, InsideLastToken);
                            }
                            break;
                        case OpenBracket:
                            EndArgumentToken = FindPairedBracket(ArgumentToken, FirstToken, LastToken);
                            InsideSecondToken = ArgumentToken + 1;
                            InsideLastToken = EndArgumentToken - 1;
                            if (SecondArgumentIsScalar) {
                                ScalarSecondArgumentValue = EvaluateScalarBracketPair(InsideSecondToken,
                                        InsideLastToken);
                            } else {
                                VectorSecondArgumentValue = EvaluateVectorBracketPair(InsideSecondToken,
                                        InsideLastToken);
                            }
                            break;
                        case Number:
                            ScalarSecondArgumentValue = Double.valueOf(Token[ArgumentToken]).doubleValue();
                            break;
                        case ConstantScalar:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                ScalarSecondArgumentValue = ((double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            } else {
                                ScalarSecondArgumentValue = ((double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken])))[0];
                            }
                            break;
                        case TemporaryVariable:
                            ScalarSecondArgumentValue = ((double[]) (temporaryVariableValueDictionary.get(
                                    Token[ArgumentToken])))[0];
                        case ConstantVector:
                            if (ConstantValueDictionary.containsKey(Token[ArgumentToken])) {
                                VectorSecondArgumentValue = (double[]) (ConstantValueDictionary.get(
                                        Token[ArgumentToken]));
                            } else {
                                VectorSecondArgumentValue = (double[]) (userVariableValueDictionary.get(
                                        Token[ArgumentToken]));
                            }
                            break;
                        case VariableScalar:
                            ScalarSecondArgumentValue = ScalarOf(Token[ArgumentToken]);
                            break;
                        case VariableVector:
                            VectorSecondArgumentValue = VectorOf(Token[ArgumentToken]);
                            break;
                    }
                    if (FirstArgumentIsScalar) {
                        if (SecondArgumentIsScalar) {
                            userInterface.println(
                                    "Cannot evaluate a vector binary operator with two scalar arguments, at Loc2.");  // trap this error!!
                        } else {
                            BracketValue = EvaluateVectorBinaryOperator(Token[OperatorToken], ScalarFirstArgumentValue,
                                    VectorSecondArgumentValue);
                        }
                    } else {
                        if (SecondArgumentIsScalar) {
                            BracketValue = EvaluateVectorBinaryOperator(Token[OperatorToken], VectorFirstArgumentValue,
                                    ScalarSecondArgumentValue);
                        } else {
                            BracketValue = EvaluateVectorBinaryOperator(Token[OperatorToken], VectorFirstArgumentValue,
                                    VectorSecondArgumentValue);
                        }
                    }
                    break;
            }
        }
        return BracketValue;
    }

    public double EvaluateScalarFunctionOfListVariables(String FunctionName, Vector ListOfArguments) {
        //
        // Functions here: Covariance, Control (private function)
        //
        int list;
        double result = 0.0;
        if (FunctionName.equals("Covariance")) {
            double[] firstArgumentValue, secondArgumentValue;
            double firstMean, secondMean;
            int I, length;
            firstArgumentValue = (double[]) ListOfArguments.elementAt(0);
            length = firstArgumentValue.length;
            firstMean = ((double[]) ListOfArguments.elementAt(1))[0];
            secondArgumentValue = (double[]) ListOfArguments.elementAt(2);
            if (secondArgumentValue.length != length) {
                userInterface.println(
                        "Error in function Covariance: third argument is not the same length as first."); // trap this runtime error
                return result;
            }
            secondMean = ((double[]) ListOfArguments.elementAt(3))[0];
            for (I = 0; I < length; I++) {
                result += (firstArgumentValue[I] - firstMean) * (secondArgumentValue[I] - secondMean);
            }
            result = result / length;
            return result;
        } else if (FunctionName.equals("Control")) {
            //
            // Control is a private function, not documented in the public help files.
            // It allows one to get Compute to do things that are useful for
            // diagnosis, debugging, documentation development, or other purposes.
            // Its format is that it can have any number of arguments, but it must
            // have at least one: the first argument is an integer code for the
            // function that Control will perform.  These are defined in this routine.
            // The function returns a value to the interface window that is a
            // completion code.  A return of 0 means the operation was successful.
            // A return of -1 means the operation failed.  This should be accompanied
            // by an error message.
            //
            double code;
            if (ListOfArguments.size() > 0) {
                code = ((double[]) ListOfArguments.elementAt(0))[0];
            } else {
                code = 0;
            }
            //
            // code = 0 means display the current values of the switches
            // displayProgress, optimise, and diagnosisToFile to the screen,
            // plus write to the console the documentation about the meanings
            // of the various control codes.
            //
            if (code == 0) {
                //
                String message = "Current switch settings: displayProgress = " + String.valueOf(displayProgress)
                        + ",  optimise = " + String.valueOf(optimise) + ", diagnosisToFile = "
                        + String.valueOf(diagnosisToFile) + ".\n  See console for documentation of function Control.";
                userInterface.println(message);
                //
                System.out.println("Control arguments:");
                System.out.println("(1) = toggle diagnostics (debugging)");
                System.out.println("(2) = toggle optimization ");
                System.out.println("(3) = toggle file I/O for diagnostics");
                System.out.println("(4, .. ) = create function alias file, all output lists being alphabetical:");
                System.out.println("\t(4,0) -- format is text file name (tab) value (tab) alias list (CR)");
                System.out.println(
                        "\t(4,1) -- format is HTML File, name (hyperlinked to bookmark of same name) (tab) alias list (CR)");
                System.out.println(
                        "\t(4,2) -- format is HTML File, alias (hyperlinked to bookmark of name of corresponding function)");
                System.out.println("(5, .. ) = create constant value and alias files, again alphabetical:");
                System.out.println("\t(5,0) -- format is text file name (tab) value (tab) alias list (CR)");
                System.out.println(
                        "\t(5,1) -- format is HTML File, name (hyperlinked to bookmark of same name) (tab) alias list (CR)");
                System.out.println(
                        "\t(5,2) -- format is HTML File, alias (hyperlinked to bookmark of name of corresponding constant)");
            }
            //
            // code = 1 means toggle the logical switch displayProgress, which is
            // used for diagnostic output.  Its default value on startup is false.
            //
            else if (code == 1) {
                displayProgress = (displayProgress) ? false : true;
            }
            //
            // code = 2 means toggle the logical swith optimise, which controls
            // optimization switch.  Its default on startup is true.
            //
            else if (code == 2) {
                optimise = (optimise) ? false : true;
            }
            //
            // code = 3 means toggle the logical switch diagnosisToFile that
            // controls the direction of diagnostic output produced when
            // displayProgress=true.  Its default on startup is false, which means
            // that the output goes to the console.
            //
            else if (code == 3) {
                if (diagnosisToFile) {
                    diagnosisToFile = false;
                    closeDiagnosisFile();
                } else {
                    diagnosisToFile = true;
                    openDiagnosisFile();
                }
            }
            //
            // code = 4 means print out a file of built-in function names and aliases.
            // This looks for a second argument.  If it is not there, then it takes
            // the default value of 0.  The second argument controls the output format:
            //   If 0 then the format is -- name (tab) value (tab) alias list (CR);
            //   If 1 then the format is -- HTML File, name (hyperlinked to bookmark of same name) (tab) alias list (CR);
            //   If 2 then the format is -- HTML File, alias (hyperlinked to bookmark of name of corresponding function).
            // The list is alphabetical on the names.
            //
            else if (code == 4) {
                double choice;
                if (ListOfArguments.size() > 1) {
                    choice = ((double[]) ListOfArguments.elementAt(1))[0];
                } else {
                    choice = 0;
                }
                printFunctionAliases(choice);
            }
            //
            // code = 5 means print out a file of built-in constant names and aliases.
            // This looks for a second argument.  If it is not there, then it takes
            // the default value of 0.  The second argument controls the output format:
            //   If 0 then the format is -- name (tab) value (tab) alias list (CR);
            //   If 1 then the format is -- HTML File, name (hyperlinked to bookmark of same name) (tab) alias list (CR);
            //   If 2 then the format is -- HTML File, alias (hyperlinked to bookmark of name of corresponding constant).
            // The list is alphabetical on the names.
            //
            else if (code == 5) {
                double choice;
                if (ListOfArguments.size() > 1) {
                    choice = ((double[]) ListOfArguments.elementAt(1))[0];
                } else {
                    choice = 0;
                }
                printConstantAliases(choice);
            }
            //
            // code = 6 means print out a file giving all the uses of every function,
            // except the function Control.  This gives, for each function, a list
            // of the acceptable arguments, both in number and type. (Not yet implemented)
            //
            //
            // If no function exists for the given value of code, then return an error.
            //
            else {
                userInterface.println("Argument of Control not recognized.  No action taken.");
                result = -1;
            }
            return result;


        } else {
            userInterface.println(
                    "Error: there is no scalar-valued function of a list of variables with the name " + FunctionName
                            + ".");
        }
        return result;
    }

    public double[] EvaluateVectorFunctionOfListVariables(String FunctionName, Vector ListOfArguments) {
        //
        // Functions here: Use, SolveCubic
        //
        int list;
        int result_index = 0;
        double[] temp;
        double[] result = {0.0};
        if (FunctionName.equals("Use")) {
            int numberOfDoubles = 0;
            for (list = 0; list < ListOfArguments.size(); list++) {
                numberOfDoubles += ((double[]) (ListOfArguments.elementAt(list))).length;
            }
            result = new double[numberOfDoubles];
            for (list = 0; list < ListOfArguments.size(); list++) {
                temp = (double[]) (ListOfArguments.elementAt(list));
                if (temp.length == 1) {
                    result[result_index] = temp[0];
                } else {
                    System.arraycopy(temp, 0, result, result_index, temp.length);
                }
                result_index += temp.length;
            }
            return result;
        } else if (FunctionName.equals("SolveCubic")) {
            result = new double[6];
            for (list = 0; list < 6; list++) {
                result[list] = 0.;
            }
            double a = ((double[]) ListOfArguments.elementAt(0))[0];
            double b = ((double[]) ListOfArguments.elementAt(1))[0];
            double c = ((double[]) ListOfArguments.elementAt(2))[0];
            double d = ((double[]) ListOfArguments.elementAt(3))[0];
            if (a == 0) {
                if (b == 0) {
                    if (c == 0) {
                        userInterface.println(
                                "Error: SolveCubic's first 3 arguments are zero.  There is no equation to solve.  Return zero.");
                        return result;
                    }
                    userInterface.println(
                            "Warning: First 2 arguments to SolveCubic are zero.  Equation reduces to a linear equation.  This solution is returned.");
                    result[0] = -d / c;
                    return result;
                }
                userInterface.println(
                        "Warning: First argument to SolveCubic is zero.  Equation reduces to a quadratic equation.  This solution is returned.");
                temp = EvaluateVectorFunctionOfThreeVariables("SolveQuadratic", b, c, d);
                for (list = 0; list < 4; list++) {
                    result[list] = temp[list];
                }
                return result;
            }
            double temp1, temp2, s1, s2, phi;
            double alpha = b / a;
            double beta = c / a;
            double gamma = d / a;
            double q = beta / 3 - alpha * alpha / 9;
            double r = (alpha * beta - 3 * gamma) / 6 - alpha * alpha * alpha / 27;
            double discrim = q * q * q + r * r;
            if (discrim > 0) {
                temp1 = Math.sqrt(discrim);
                temp2 = r + temp1;
                if (temp2 > 0) {
                    s1 = Math.pow(temp2, 1. / 3.);
                } else if (temp2 == 0) {
                    s1 = 0;
                } else {
                    s1 = -Math.pow(-temp2, 1. / 3.);
                }
                temp2 = r - temp1;
                if (temp2 > 0) {
                    s2 = Math.pow(temp2, 1. / 3.);
                } else if (temp2 == 0) {
                    s2 = 0;
                } else {
                    s2 = -Math.pow(-temp2, 1. / 3.);
                }
                temp2 = s1 + s2;
                temp1 = alpha / 3.;
                result[0] = temp2 - temp1;
                result[2] = -temp2 / 2. - temp1;
                result[3] = Math.sqrt(3.) * (s1 - s2) / 2.;
                result[4] = result[2];
                result[5] = -result[3];
            } else if (discrim == 0) {
                if (r > 0) {
                    s1 = Math.pow(r, 1. / 3.);
                } else if (r == 0) {
                    s1 = 0;
                } else {
                    s1 = -Math.pow(-r, 1. / 3.);
                }
                temp2 = s1 + s1;
                temp1 = alpha / 3.;
                result[0] = temp2 - temp1;
                result[2] = -temp2 / 2. - temp1;
                result[4] = result[2];
            } else {
                phi = Math.atan2(Math.sqrt(-discrim), r) / 3.;
                temp1 = Math.sqrt(-q);
                temp2 = 2. * temp1 * Math.cos(phi);
                temp1 = Math.sqrt(3.) * temp1 * Math.sin(phi);
                result[0] = -alpha / 3.;
                result[2] = result[0];
                result[4] = result[0];
                result[0] += temp2;
                result[2] += -temp2 / 2. - temp1;
                result[4] += -temp2 / 2. + temp1;
            }
            return result;
        } else {
            userInterface.println(
                    "Error: there is no vector-valued function of a list of variables with the name " + FunctionName
                            + ".");
            return null;
        }
    }

    public void SurroundBinOpsWithBrackets(String Operator1, String Operator2, int FirstToken, int LastToken)
            throws ComputeSyntaxException {
        int FirstPosition, LastPosition;
        int I = FirstToken;
        int UpperLimit = LastToken;
        if (displayProgress) {
            diagnosticPrintln("Entered SurroundBinOpsWithBrackets, with first op = " + Operator1 + ", FT = "
                    + String.valueOf(FirstToken) + " and LT = " + String.valueOf(LastToken));
        }
        while (I <= UpperLimit) {
            if ((Token[I].equals(Operator1)) || (Token[I].equals(Operator2))) {
                FirstPosition = OtherEndOfArgument(I - 1);
                LastPosition = OtherEndOfArgument(I + 1);
                if ((FirstPosition == 1) || (LastPosition == NumberOfTokens)
                        || (Properties[FirstPosition - 1] != OpenBracket)
                        || (Properties[LastPosition + 1] != CloseBracket)
                        || (Level[FirstPosition - 1] != Level[LastPosition + 1]) || ((FirstPosition > 2) && (
                        (Properties[FirstPosition - 2] == FunctionOfOneVariable)
                                || (Properties[FirstPosition - 2] == FunctionOfTwoVariables)
                                || (Properties[FirstPosition - 2] == FunctionOfThreeVariables) || (
                                Properties[FirstPosition - 2] == FunctionOfListVariables)))) {
                    InsertTokenAfter(LastPosition, ")", CloseBracket, (byte) 0, false, false);
                    InsertTokenBefore(FirstPosition, "(", OpenBracket, (byte) 0, false, false);
                    SetBracketLevels(" SBL Invocation 20 ");
                    UpperLimit += 2;
                    I += 2;
                } else {
                    I++;
                }
            } else {
                I++;
            }
        }
        return;
    }

    public void SurroundFunctionsWithBrackets() throws ComputeSyntaxException {
        int LastPosition;
        int I = 1;
        while (I <= NumberOfTokens) {
            if ((Properties[I] == FunctionOfOneVariable) || (Properties[I] == FunctionOfTwoVariables)
                    || (Properties[I] == FunctionOfThreeVariables) || (Properties[I] == FunctionOfListVariables)) {
                LastPosition = OtherEndOfArgument(I + 1);
                if ((I == 1) || (LastPosition == NumberOfTokens) || (Properties[I - 1] != OpenBracket)
                        || (Properties[LastPosition + 1] != CloseBracket) || (Level[I - 1] != Level[LastPosition + 1])
                        || ((I > 2) && ((Properties[I - 2] == FunctionOfOneVariable)
                        || (Properties[I - 2] == FunctionOfTwoVariables)
                        || (Properties[I - 2] == FunctionOfThreeVariables) || (Properties[I - 2]
                        == FunctionOfListVariables)))) {
                    InsertTokenAfter(OtherEndOfArgument(I + 1), ")", CloseBracket, (byte) 0, false, false);
                    InsertTokenBefore(I, "(", OpenBracket, (byte) 0, false, false);
                    SetBracketLevels(" SBL Invocation 21 ");
                    I = I + 2;
                } else {
                    I++;
                }
            } else {
                I++;
            }
        }
        return;
    }

    public void ConvertToSquareBrackets() throws ComputeSyntaxException {
        int OpenToken, CloseToken;
        for (int I = 1; I <= NumberOfTokens - 3; I++) {
            if ((Properties[I] == FunctionOfOneVariable) || (Properties[I] == FunctionOfTwoVariables)
                    || (Properties[I] == FunctionOfThreeVariables) || (Properties[I] == FunctionOfListVariables)) {
                OpenToken = I + 1;
                if (Properties[OpenToken] != OpenBracket) {
                    userInterface.println("Error in ConvertToSquareBrackets: Token " + String.valueOf(OpenToken)
                            + " is not an open bracket.");  // trap this error!!
                    return;
                }
                CloseToken = FindPairedBracket(OpenToken, OpenToken, NumberOfTokens);
                Token[OpenToken] = "[";
                Properties[OpenToken] = OpenSquareBracket;
                Token[CloseToken] = "]";
                Properties[CloseToken] = CloseSquareBracket;
            }
        }
        return;
    }

    public int DistributeNegates(boolean DoUnwrap) throws ComputeSyntaxException {
        int ArgumentStart, ArgumentEnd, BeginSurround, EndSurround;
        int PositionBeforeNegate, PositionAfterNegate, OpeningSqBkt, ClosingSqBkt;
        int NegatePosition = 1;
        boolean AdditionTerm = false;
        boolean MultiplicationTerm = false;
        boolean KeepSurround = false;
        boolean NextDoUnwrap = false;
        boolean NegateApplied = false;
        while (NegatePosition < NumberOfTokens) {
            if ((Properties[NegatePosition] == FunctionOfOneVariable) && Token[NegatePosition].equals("Negate")) {
                OpeningSqBkt = NegatePosition + 1;
                ArgumentStart = NegatePosition + 2;
                BeginSurround = NegatePosition - 1;
                EndSurround = OtherEndOfArgument(BeginSurround);
                ClosingSqBkt = EndSurround - 1;
                ArgumentEnd = ClosingSqBkt - 1;
                PositionBeforeNegate = BeginSurround - 1;
                PositionAfterNegate = EndSurround + 1;
                if (Token[PositionBeforeNegate].equals("-")) {
                    if (DoUnwrap) {
                        UnwrapAdditionBrackets(ArgumentStart, ArgumentEnd);
                        RemoveMarkedTokens(1, NumberOfTokens);
                        SetBracketLevels(" SBL Invocation 22 ");
                    }
                    RemoveNegate(BeginSurround, KeepSurround);
                    Token[PositionBeforeNegate] = "+";
                    return 1;
                } else if (Token[PositionBeforeNegate].equals("+")) {
                    AdditionTerm = true;
                    NextDoUnwrap = DoUnwrap;
                } else if (Token[PositionBeforeNegate].equals("*")) {
                    MultiplicationTerm = true;
                    NextDoUnwrap = DoUnwrap;
                } else if (Token[PositionBeforeNegate].equals("/")) {
                    MultiplicationTerm = true;
                    NextDoUnwrap = false;
                    KeepSurround = true;
                } else if ((Properties[PositionBeforeNegate] == OpenBracket) && (Token[PositionAfterNegate].equals("-")
                        || Token[PositionAfterNegate].equals("+"))) {
                    AdditionTerm = true;
                    NextDoUnwrap = false;
                } else if ((Properties[PositionBeforeNegate] == OpenBracket) && (Token[PositionAfterNegate].equals("/")
                        || Token[PositionAfterNegate].equals("*"))) {
                    MultiplicationTerm = true;
                    NextDoUnwrap = false;
                } else if (Properties[PositionBeforeNegate] == OpenSquareBracket) {
                    NextDoUnwrap = false;
                }
                NegateApplied = ApplyNegate(ArgumentStart, ArgumentEnd);
                if (NegateApplied) {
                    if (displayProgress) {
                        diagnosticPrintln("Negate applied to give value " + String.valueOf(NegateApplied));
                    }
                    RemoveUselessBrackets();
                    if (NextDoUnwrap) {
                        if (displayProgress) {
                            diagnosticPrintln("Unwrapping at " + String.valueOf(ArgumentStart));
                        }
                        ArgumentEnd = FindPairedBracket(ArgumentStart, ArgumentStart, NumberOfTokens);
                        if (AdditionTerm) {
                            UnwrapAdditionBrackets(ArgumentStart, ArgumentEnd);
                        } else if (MultiplicationTerm) {
                            UnwrapMultiplicationBrackets(ArgumentStart, ArgumentEnd);
                        }
                        UnwrappedArgumentOfNegate = true;
                        RemoveMarkedTokens(1, NumberOfTokens);
                        SetBracketLevels(" SBL Invocation 23 ");
                    }
                    RemoveNegate(BeginSurround, KeepSurround);
                    DisplayTokens("After removing negate - prog pos 1");
                    return 1;
                }
            }
            NegatePosition++;
        }
        return 0;
    }

    public boolean ApplyNegate(int ArgumentStart, int ArgumentEnd) throws ComputeSyntaxException {
        int FirstInsideArgumentBegin, FirstInsideArgumentEnd, InsideOperator;
        int SecondInsideArgumentBegin, SecondInsideArgumentEnd, FirstInsideToken;
        boolean NegateApplied;
        DisplayTokens("Entered ApplyNegate for range " + String.valueOf(ArgumentStart) + " to " + String
                .valueOf(ArgumentEnd));
        if (ArgumentStart == ArgumentEnd) {
            if (Properties[ArgumentStart] == Number) {
                if (Token[ArgumentStart].startsWith("-")) {
                    Token[ArgumentStart] = new String(Token[ArgumentStart].substring(1));
                } else {
                    Token[ArgumentStart] = "-" + Token[ArgumentStart];
                }
                return true;
            } else {
                return false;
            }
        } else if (Properties[ArgumentStart] == OpenBracket) {
            FirstInsideToken = ArgumentStart + 1;
            if (Token[FirstInsideToken].equals("Negate")) {
                RemoveNegate(ArgumentStart, false);
                return true;
            } else if ((Properties[FirstInsideToken] == FunctionOfOneVariable)
                    || (Properties[FirstInsideToken] == FunctionOfTwoVariables)
                    || (Properties[FirstInsideToken] == FunctionOfThreeVariables) || (Properties[FirstInsideToken]
                    == FunctionOfListVariables)) {
                return false;
            } else {
                FirstInsideArgumentBegin = FirstInsideToken;
                FirstInsideArgumentEnd = OtherEndOfArgument(FirstInsideArgumentBegin);
                InsideOperator = FirstInsideArgumentEnd + 1;
                if (Token[InsideOperator].equals("+")) {
                    SecondInsideArgumentBegin = InsideOperator + 1;
                    SecondInsideArgumentEnd = OtherEndOfArgument(SecondInsideArgumentBegin);
                    NegateApplied = ApplyNegate(SecondInsideArgumentBegin, SecondInsideArgumentEnd);
                    if (NegateApplied) {
                        NegateApplied = ApplyNegate(FirstInsideArgumentBegin, FirstInsideArgumentEnd);
                        if (!NegateApplied) {
                            EncloseInNegate(FirstInsideArgumentBegin, FirstInsideArgumentEnd);
                        }
                        return true;
                    } else {
                        NegateApplied = ApplyNegate(FirstInsideArgumentBegin, FirstInsideArgumentEnd);
                        if (NegateApplied) {
                            EncloseInNegate(SecondInsideArgumentBegin, SecondInsideArgumentEnd);
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else if (Token[InsideOperator].equals("-")) {
                    Token[InsideOperator] = "+";
                    NegateApplied = ApplyNegate(FirstInsideArgumentBegin, FirstInsideArgumentEnd);
                    if (!NegateApplied) {
                        EncloseInNegate(FirstInsideArgumentBegin, FirstInsideArgumentEnd);
                    }
                    return true;
                } else if (Token[InsideOperator].equals("*") || Token[InsideOperator].equals("/")) {
                    SecondInsideArgumentBegin = InsideOperator + 1;
                    SecondInsideArgumentEnd = OtherEndOfArgument(SecondInsideArgumentBegin);
                    NegateApplied = ApplyNegate(SecondInsideArgumentBegin, SecondInsideArgumentEnd);
                    if (NegateApplied) {
                        return true;
                    } else {
                        NegateApplied = ApplyNegate(FirstInsideArgumentBegin, FirstInsideArgumentEnd);
                        if (NegateApplied) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void UnwrapAdditionBrackets(int OpeningBkt, int ClosingBkt) throws ComputeSyntaxException {
        int InsideFirstArgBeginToken = OpeningBkt + 1;
        int InsideFirstArgEndToken = OtherEndOfArgument(InsideFirstArgBeginToken);
        int OperatorToken = InsideFirstArgEndToken + 1;
        if (displayProgress) {
            diagnosticPrintln("Entering UnwrapAddition at " + String.valueOf(OpeningBkt));
        }
        if (Token[OperatorToken].equals("+") || Token[OperatorToken].equals("-")) {
            Properties[OpeningBkt] = Remove;
            Properties[ClosingBkt] = Remove;
            if (InsideFirstArgBeginToken < InsideFirstArgEndToken) {
                UnwrapAdditionBrackets(InsideFirstArgBeginToken, InsideFirstArgEndToken);
            }
        }
        return;
    }

    public void UnwrapMultiplicationBrackets(int OpeningBkt, int ClosingBkt) throws ComputeSyntaxException {
        int InsideFirstArgBeginToken = OpeningBkt + 1;
        int InsideFirstArgEndToken = OtherEndOfArgument(InsideFirstArgBeginToken);
        int OperatorToken = InsideFirstArgEndToken + 1;
        if (displayProgress) {
            diagnosticPrintln("Entering UnwrapMultiplication at " + String.valueOf(OpeningBkt));
        }
        if (Token[OperatorToken].equals("*") || Token[OperatorToken].equals("/")) {
            Properties[OpeningBkt] = Remove;
            Properties[ClosingBkt] = Remove;
            if (InsideFirstArgBeginToken < InsideFirstArgEndToken) {
                UnwrapMultiplicationBrackets(InsideFirstArgBeginToken, InsideFirstArgEndToken);
            }
        }
        return;
    }

    public void RemoveNegate(int BeginSurround, boolean KeepSurround) throws ComputeSyntaxException {
        int NegatePosition, OpeningSqBkt, ClosingSqBkt, EndSurround;
        if (displayProgress) {
            diagnosticPrintln("Entering RemoveNegate at " + String.valueOf(BeginSurround));
        }
        EndSurround = FindPairedBracket(BeginSurround, BeginSurround, NumberOfTokens);
        NegatePosition = BeginSurround + 1;
        OpeningSqBkt = NegatePosition + 1;
        ClosingSqBkt = EndSurround - 1;
        Properties[NegatePosition] = Remove;
        Properties[OpeningSqBkt] = Remove;
        Properties[ClosingSqBkt] = Remove;
        if (!KeepSurround) {
            Properties[BeginSurround] = Remove;
            Properties[EndSurround] = Remove;
        }
        RemoveMarkedTokens(1, NumberOfTokens);
        SetBracketLevels(" SBL Invocation 24 ");
        return;
    }

    public void EncloseInNegate(int BeginArgument, int EndArgument) throws ComputeSyntaxException {
        int BeforeArgument = BeginArgument - 1;
        InsertTokenAfter(EndArgument, ")", CloseBracket, (byte) 0, false, false);
        InsertTokenAfter(EndArgument, "]", CloseSquareBracket, (byte) 0, false, false);
        InsertTokenAfter(BeforeArgument, "[", OpenSquareBracket, (byte) 0, false, false);
        InsertTokenAfter(BeforeArgument, "Negate", FunctionOfOneVariable, (byte) 0, false, false);
        InsertTokenAfter(BeforeArgument, "(", OpenBracket, (byte) 0, false, false);
        SetBracketLevels(" SBL Invocation 25 ");
        return;
    }

    public double[] mergeSort(double[] input, double direction) {
        //
        //  sort into an order given by sign of direction: non-negative = ascending
        //

        if (input.length < 2) {
            return input;
        } else if (input.length == 2) {
            if (direction >= 0) {
                if (input[0] > input[1]) {
                    double a = input[0];
                    input[0] = input[1];
                    input[1] = a;
                }
            } else if (input[0] < input[1]) {
                double a = input[0];
                input[0] = input[1];
                input[1] = a;
            }

            return input;
        } else {

            int frontLength = (int) Math.floor(input.length / 2);
            int rearLength = input.length - frontLength;
            double[] front = new double[frontLength];
            double[] rear = new double[rearLength];
            System.arraycopy(input, 0, front, 0, frontLength);
            System.arraycopy(input, frontLength, rear, 0, rearLength);

            front = mergeSort(front, direction);
            rear = mergeSort(rear, direction);

            int j = 0;
            int k = 0;
            int l = 0;

            if (direction >= 0) {
                while ((j < frontLength) && (k < rearLength)) {
                    if (front[j] <= rear[k]) {
                        input[l++] = front[j++];
                    } else {
                        input[l++] = rear[k++];
                    }
                }
            } else {
                while ((j < frontLength) && (k < rearLength)) {
                    if (front[j] >= rear[k]) {
                        input[l++] = front[j++];
                    } else {
                        input[l++] = rear[k++];
                    }
                }
            }

            if (j == frontLength) {
                System.arraycopy(rear, k, input, l, rearLength - k);
            } else {
                System.arraycopy(front, j, input, l, frontLength - j);
            }

            return input;
        }

    }

    public double[] mergeSort(double[] input) {
        return mergeSort(input, 1.0);
    }


    public String[] mergeSort(String[] input, double direction) {
        //
        //  sort into an order given by sign of direction: non-negative = ascending
        //

        if (input.length < 2) {
            return input;
        } else if (input.length == 2) {
            if (direction >= 0) {
                if (input[0].compareTo(input[1]) > 0) {
                    String a = input[0];
                    input[0] = input[1];
                    input[1] = a;
                }
            } else if (input[0].compareTo(input[1]) < 0) {
                String a = input[0];
                input[0] = input[1];
                input[1] = a;
            }
            return input;
        } else {
            int frontLength = (int) Math.floor(input.length / 2);
            int rearLength = input.length - frontLength;
            String[] front = new String[frontLength];
            String[] rear = new String[rearLength];

            System.arraycopy(input, 0, front, 0, frontLength);
            System.arraycopy(input, frontLength, rear, 0, rearLength);

            front = mergeSort(front, direction);
            rear = mergeSort(rear, direction);

            int j = 0;
            int k = 0;
            int l = 0;

            if (direction >= 0) {
                while ((j < frontLength) && (k < rearLength)) {
                    if (front[j].compareTo(rear[k]) <= 0) {
                        input[l++] = front[j++];
                    } else {
                        input[l++] = rear[k++];
                    }
                }
            } else {
                while ((j < frontLength) && (k < rearLength)) {
                    if (front[j].compareTo(rear[k]) >= 0) {
                        input[l++] = front[j++];
                    } else {
                        input[l++] = rear[k++];
                    }
                }
            }

            if (j == frontLength) {
                System.arraycopy(rear, k, input, l, rearLength - k);
            } else {
                System.arraycopy(front, j, input, l, frontLength - j);
            }

            return input;
        }

    }

    public String[] mergeSort(String[] input) {
        return mergeSort(input, 1.0);
    }

    public Vector<String> mergeSort(Vector<String> input, double direction) {
        int length = input.size();
        String[] buffer = new String[length];
        input.copyInto(buffer);
        buffer = mergeSort(buffer, direction);
        Vector<String> output = new Vector<String>(length);
        for (int i = 0; i < length; i++) {
            output.addElement(buffer[i]);
        }
        return output;
    }

    public Vector<String> mergeSort(Vector<String> input) {
        return mergeSort(input, 1.0);
    }


    public void InsertScalarIntoConstantValueDictionary(String Key, double value) {
        double[] arrayValue = new double[1];
        arrayValue[0] = value;
        ConstantValueDictionary.put(Key, arrayValue);
    }

    public void InsertScalarIntoTemporaryVariableValueDictionary(String Key, double value) {
        double[] arrayValue = new double[1];
        arrayValue[0] = value;
        temporaryVariableValueDictionary.put(Key, arrayValue);
    }


    public void diagnosticPrintln(String line) {
        if (diagnosisToFile) {
            ;
        } else {
            System.out.println(line);
        }
    }

    public void diagnosticPrint(String phrase) {
        if (diagnosisToFile) {
            ;
        } else {
            System.out.print(phrase);
        }
    }

    public void openDiagnosisFile() {
        //
        // overridden in ComputeCalc
        //
    }

    public void closeDiagnosisFile() {
        //
        // overridden in ComputeCalc
        //
    }

    public void printFunctionAliases(double choice) {
        //
        // overridden in ComputeCalc
        //
    }

    public void printConstantAliases(double choice) {
        //
        // overridden in ComputeCalc
        //
    }

}  //end of class
















