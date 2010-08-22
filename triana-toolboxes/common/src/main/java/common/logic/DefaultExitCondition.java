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

package common.logic;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.TaskGraph;
import triana.types.Arithmetic;
import triana.types.Const;

/**
 * The default exit condition used by the loop unit.
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */

public class DefaultExitCondition implements ExitCondition {

    private final static int VARIABLE_COUNT = 4;

    private final static int EQUAL = 0;
    private final static int NOT_EQUAL = 1;
    private final static int GREATER_THAN = 2;
    private final static int GREATER_THAN_EQUAL = 3;
    private final static int LESS_THAN = 4;
    private final static int LESS_THAN_EQUAL = 5;

    private final static int PLUS = 10;
    private final static int MINUS = 11;
    private final static int MULTIPLY = 12;
    private final static int DIVIDE = 13;
    private final static int MOD = 14;

    private final static int NOT = 20;
    private final static int AND = 21;
    private final static int OR = 22;

    private final static int OPEN_BRACKET = 30;
    private final static int CLOSE_BRACKET = 31;

    private final static int LOGIC_PRIORITY = 5;
    private final static int NOT_PRIORITY = 4;
    private final static int COMP_PRIORITY = 3;
    private final static int PLUS_MINUS_PRIORITY = 2;
    private final static int MULTIPLY_DIVIDE_PRIORITY = 1;

    private final static int OPEN_BRACKET_INIT_PRIORITY = 0;
    private final static int OPEN_BRACKET_STACK_PRIORITY = Integer.MAX_VALUE;
    private final static int CLOSE_BRACKET_PRIORITY = Integer.MAX_VALUE - 1;

    private static Hashtable optable = new Hashtable();

    private Task looptask;


    public DefaultExitCondition() {
        optable.put("==", new Operator(EQUAL, COMP_PRIORITY));
        optable.put("=", new Operator(EQUAL, COMP_PRIORITY));
        optable.put("!=", new Operator(NOT_EQUAL, COMP_PRIORITY));
        optable.put("<>", new Operator(NOT_EQUAL, COMP_PRIORITY));
        optable.put(">", new Operator(GREATER_THAN, COMP_PRIORITY));
        optable.put(">=", new Operator(GREATER_THAN_EQUAL, COMP_PRIORITY));
        optable.put("<", new Operator(LESS_THAN, COMP_PRIORITY));
        optable.put("<=", new Operator(LESS_THAN_EQUAL, COMP_PRIORITY));

        optable.put("!", new Operator(NOT, NOT_PRIORITY));
        optable.put("&", new Operator(AND, LOGIC_PRIORITY));
        optable.put("&&", new Operator(AND, LOGIC_PRIORITY));
        optable.put("|", new Operator(OR, LOGIC_PRIORITY));
        optable.put("||", new Operator(OR, LOGIC_PRIORITY));

        optable.put("+", new Operator(PLUS, PLUS_MINUS_PRIORITY));
        optable.put("-", new Operator(MINUS, PLUS_MINUS_PRIORITY));

        optable.put("*", new Operator(MULTIPLY, MULTIPLY_DIVIDE_PRIORITY));
        optable.put("/", new Operator(DIVIDE, MULTIPLY_DIVIDE_PRIORITY));
        optable.put("@", new Operator(DIVIDE, MULTIPLY_DIVIDE_PRIORITY));
        optable.put("%", new Operator(MOD, MULTIPLY_DIVIDE_PRIORITY));

        optable.put("(", new Operator(OPEN_BRACKET, OPEN_BRACKET_INIT_PRIORITY));
        optable.put(")", new Operator(CLOSE_BRACKET, CLOSE_BRACKET_PRIORITY));
    }

    /**
     * Sets an interface to the loop task
     */
    public void setTask(Task task) {
        this.looptask = task;
    }

    /**
     * @return an interface to the loop task
     */
    public Task getTask() {
        return looptask;
    }


    /**
     * Called when the loop is initialised (before any iterations are run)
     */
    public void init() throws InvalidEquationException {
        Task task = getTask();
        Object val;

        for (int count = 0; count < VARIABLE_COUNT; count++) {
            if (task.isParameterName("init$" + count)) {
                val = calculateEquation((String) task.getParameter("init$" + count), null);

                if (val != null) {
                    task.setParameter("$var" + count, val);
                }
            }
        }
    }

    /**
     * Called when an iteration is run
     */
    public void iteration() throws InvalidEquationException {
        Task task = getTask();
        Object val;

        for (int count = 0; count < VARIABLE_COUNT; count++) {
            if (task.isParameterName("iter$" + count)) {
                val = calculateEquation((String) task.getParameter("iter$" + count), null);

                if (val != null) {
                    task.setParameter("$var" + count, val);
                }
            }
        }
    }


    /**
     * Calculates whether the loop should exit. This decision can be based on a any factors, including the parameters of
     * the loop task and other tasks.
     *
     * @return true if the loop should exit
     */
    public boolean isExitLoop(Object[] data) throws InvalidEquationException {
        if (looptask.isParameterName("exitCondition")) {
            Object result = calculateEquation((String) looptask.getParameter("exitCondition"), data);

            if (result instanceof Boolean) {
                return ((Boolean) result).booleanValue();
            } else {
                throw (new InvalidEquationException("Invalid condition: Non boolean result"));
            }
        } else {
            throw (new InvalidEquationException("Invalid condition: Condition not specified"));
        }
    }


    /**
     * Calculates the equation, returning either a Double or a Boolean
     */
    private Object calculateEquation(String equation, Object[] data) throws InvalidEquationException {
        if (equation.equals("")) {
            return null;
        }

        try {
            String modequation = modifyEquation(equation);

            StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(modequation));
            CalculateInfo info = new CalculateInfo();

            tokenizer.quoteChar('"');
            tokenizer.wordChars('$', '$');
            int token = tokenizer.nextToken();

            while (token != StreamTokenizer.TT_EOF) {
                if (token == StreamTokenizer.TT_NUMBER) {
                    handleOperator(info);
                    info.pushValue(new Double(tokenizer.nval));
                } else if (token == StreamTokenizer.TT_WORD) {
                    handleOperator(info);
                    info.pushValue(parseString(tokenizer.sval, data));
                } else if ((char) token == '\"') {
                    handleOperator(info);
                    info.pushValue(tokenizer.sval);
                } else {
                    handleOperator(info, (char) token);
                }

                token = tokenizer.nextToken();
            }

            handleOperator(info);
            calculate(info, new Operator(CLOSE_BRACKET, CLOSE_BRACKET_PRIORITY));

            return info.popValue();
        } catch (IOException except) {
            throw (new InvalidEquationException("IOException: " + except.getMessage()));
        }
    }

    private String modifyEquation(String equation) {
        String modequation = equation.replace('/', '@');
        return modequation;
    }

    private Object parseString(String str, Object[] data) throws InvalidEquationException {
        if (str.equalsIgnoreCase("false")) {
            return new Boolean(false);
        } else if (str.equalsIgnoreCase("true")) {
            return new Boolean(true);
        } else if (str.startsWith("$data")) {
            int index = Integer.parseInt(str.substring(5));

            if ((data != null) && (index < data.length)) {
                return convert(data[index]);
            } else {
                return new Const(Double.NaN);
            }
        } else {
            return convert(getParameter(str));
        }
    }

    private Object convert(Object obj) {
        if (obj instanceof String) {
            try {
                return new Const((String) obj);
            } catch (NumberFormatException except) {
                return obj;
            }
        } else if (obj instanceof Number) {
            return new Const((Number) obj);
        } else {
            return obj;
        }
    }

    /**
     * @return the specified parameter value, where paramname is a mapped paramname or of the form
     *         groupname.taskname.paramname (returns null if parameter not set). Note that if the paramname is mapped to
     *         multiple parameters then the value of one is returned (but it is not defined which one!).
     */
    public Object getParameter(String paramname) throws InvalidEquationException {
        Task subtask = getTask(paramname);
        String param = getParameterName(paramname);

        if ((subtask != null) && (subtask.isParameterName(param))) {
            return subtask.getParameter(param);
        } else {
            throw (new InvalidEquationException("Invalid parameter: " + paramname));
        }
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
        if ((paramname.indexOf('.') == -1) || (looptask.getParent() == null)) {
            return looptask;
        } else {
            String[] tasknames = paramname.split("\\.");
            Task subtask = looptask.getParent();

            for (int count = 0; count < tasknames.length - 1; count++) {
                if ((subtask == null) || (!(subtask instanceof TaskGraph))) {
                    return null;
                }

                subtask = ((TaskGraph) subtask).getTask(tasknames[count]);
            }

            return subtask;
        }
    }


    private void handleOperator(CalculateInfo info, char c) throws InvalidEquationException {
        if (info.getOperator() == null) {
            info.setOperator(String.valueOf(c));
        } else if (optable.containsKey(info.getOperator()) && (!optable.containsKey(info.getOperator() + c))) {
            handleOperator(info);
            info.setOperator(String.valueOf(c));
        } else {
            info.setOperator(info.getOperator() + c);
        }
    }

    private void handleOperator(CalculateInfo info) throws InvalidEquationException {
        if (info.getOperator() == null) {
            return;
        }

        if (!optable.containsKey(info.getOperator())) {
            throw (new InvalidEquationException("Invalid operator: " + info.getOperator()));
        }

        Operator operator = (Operator) optable.get(info.getOperator());
        Operator peekop = info.peekOperator();

        if ((peekop == null) || (operator.priority < peekop.priority)) {
            info.pushOperator(operator);
        } else {
            calculate(info, operator);

            if (operator.operator != CLOSE_BRACKET) {
                info.pushOperator(operator);
            }
        }

        info.setOperator(null);
    }

    private void calculate(CalculateInfo info, Operator nextop) throws InvalidEquationException {
        Operator popop = info.popOperator();

        while ((popop != null) && (popop.priority <= nextop.priority)) {
            if (popop.operator == EQUAL) {
                calculateEqual(info);
            } else if (popop.operator == NOT_EQUAL) {
                calculateNotEqual(info);
            } else if (popop.operator == GREATER_THAN) {
                calculateGreaterThan(info);
            } else if (popop.operator == GREATER_THAN_EQUAL) {
                calculateGreaterThanEqual(info);
            } else if (popop.operator == LESS_THAN) {
                calculateLessThan(info);
            } else if (popop.operator == LESS_THAN_EQUAL) {
                calculateLessThanEqual(info);
            } else if (popop.operator == NOT) {
                calculateNot(info);
            } else if (popop.operator == AND) {
                calculateAnd(info);
            } else if (popop.operator == OR) {
                calculateOr(info);
            } else if (popop.operator == PLUS) {
                calculatePlus(info);
            } else if (popop.operator == MINUS) {
                calculateMinus(info);
            } else if (popop.operator == MULTIPLY) {
                calculateMultiply(info);
            } else if (popop.operator == DIVIDE) {
                calculateDivide(info);
            } else if (popop.operator == MOD) {
                calculateMod(info);
            }

            popop = info.popOperator();
        }

        if ((popop != null) && (nextop.operator != CLOSE_BRACKET)) {
            info.pushOperator(popop);
        }
    }

    private void calculateEqual(CalculateInfo info) throws InvalidEquationException {
        Object val2 = info.popValue();
        Object val1 = info.popValue();

        try {
            if ((val1 instanceof Number) && (val2 instanceof Number)) {
                info.pushValue(new Boolean(new Const((Number) val1).equals(new Const((Number) val2))));
            } else {
                info.pushValue(new Boolean(val1.equals(val2)));
            }
        } catch (ClassCastException except) {
            throw (new InvalidEquationException(
                    "Invalid comparison: Cannot compare " + val1.getClass().getName() + " with " + val2.getClass()
                            .getName()));
        }
    }

    private void calculateNotEqual(CalculateInfo info) throws InvalidEquationException {
        Object val2 = info.popValue();
        Object val1 = info.popValue();

        try {
            if ((val1 instanceof Number) && (val2 instanceof Number)) {
                info.pushValue(new Boolean(!new Const((Number) val1).equals(new Const((Number) val2))));
            } else {
                info.pushValue(new Boolean(!val1.equals(val2)));
            }
        } catch (ClassCastException except) {
            throw (new InvalidEquationException(
                    "Invalid comparison: Cannot compare " + val1.getClass().getName() + " with " + val2.getClass()
                            .getName()));
        }
    }

    private void calculateGreaterThan(CalculateInfo info) throws InvalidEquationException {
        Object val2 = info.popValue();
        Object val1 = info.popValue();

        try {
            if ((val1 instanceof Comparable) && (val2 instanceof Comparable)) {
                info.pushValue(new Boolean(((Comparable) val1).compareTo(val2) > 0));
            }
        } catch (ClassCastException except) {
            throw (new InvalidEquationException(
                    "Invalid comparison: Cannot compare " + val1.getClass().getName() + " with " + val2.getClass()
                            .getName()));
        }
    }

    private void calculateGreaterThanEqual(CalculateInfo info) throws InvalidEquationException {
        Object val2 = info.popValue();
        Object val1 = info.popValue();

        try {
            if ((val1 instanceof Comparable) && (val2 instanceof Comparable)) {
                info.pushValue(new Boolean(((Comparable) val1).compareTo(val2) >= 0));
            }
        } catch (ClassCastException except) {
            throw (new InvalidEquationException(
                    "Invalid comparison: Cannot compare " + val1.getClass().getName() + " with " + val2.getClass()
                            .getName()));
        }
    }

    private void calculateLessThan(CalculateInfo info) throws InvalidEquationException {
        Object val2 = info.popValue();
        Object val1 = info.popValue();

        try {
            if ((val1 instanceof Comparable) && (val2 instanceof Comparable)) {
                info.pushValue(new Boolean(((Comparable) val1).compareTo(val2) < 0));
            }
        } catch (ClassCastException except) {
            throw (new InvalidEquationException(
                    "Invalid comparison: Cannot compare " + val1.getClass().getName() + " with " + val2.getClass()
                            .getName()));
        }
    }

    private void calculateLessThanEqual(CalculateInfo info) throws InvalidEquationException {
        Object val2 = info.popValue();
        Object val1 = info.popValue();

        try {
            if ((val1 instanceof Comparable) && (val2 instanceof Comparable)) {
                info.pushValue(new Boolean(((Comparable) val1).compareTo(val2) <= 0));
            }
        } catch (ClassCastException except) {
            throw (new InvalidEquationException(
                    "Invalid comparison: Cannot compare " + val1.getClass().getName() + " with " + val2.getClass()
                            .getName()));
        }
    }

    private void calculateNot(CalculateInfo info) throws InvalidEquationException {
        try {
            Boolean val1 = (Boolean) info.popValue();
            info.pushValue(new Boolean(!val1.booleanValue()));
        } catch (ClassCastException except) {
            throw (new InvalidEquationException("Invalid logic: Cannot negate non-boolean value"));
        }
    }

    private void calculateAnd(CalculateInfo info) throws InvalidEquationException {
        try {
            Boolean val2 = (Boolean) info.popValue();
            Boolean val1 = (Boolean) info.popValue();
            info.pushValue(new Boolean(val1.booleanValue() && val2.booleanValue()));
        } catch (ClassCastException except) {
            throw (new InvalidEquationException("Invalid logic: Cannot AND non-boolean values"));
        }
    }

    private void calculateOr(CalculateInfo info) throws InvalidEquationException {
        try {
            Boolean val2 = (Boolean) info.popValue();
            Boolean val1 = (Boolean) info.popValue();
            info.pushValue(new Boolean(val1.booleanValue() || val2.booleanValue()));
        } catch (ClassCastException except) {
            throw (new InvalidEquationException("Invalid logic: Cannot OR non-boolean values"));
        }
    }

    private void calculatePlus(CalculateInfo info) throws InvalidEquationException {
        Object val2 = info.popValue();
        Object val1 = info.popValue();

        if (val1 instanceof Number) {
            val1 = new Const((Number) val1);
        }

        try {
            info.pushValue(((Arithmetic) val1).add(val2));
        } catch (ClassCastException except) {
            throw (new InvalidEquationException("Invalid calculation: Cannot subtract non-numeric values"));

        }
    }

    private void calculateMinus(CalculateInfo info) throws InvalidEquationException {
        Object val2 = info.popValue();
        Object val1 = info.popValue();

        if (val1 instanceof Number) {
            val1 = new Const((Number) val1);
        }

        try {
            info.pushValue(((Arithmetic) val1).subtract(val2));
        } catch (ClassCastException except) {
            throw (new InvalidEquationException("Invalid calculation: Cannot subtract non-numeric values"));
        }
    }

    private void calculateMultiply(CalculateInfo info) throws InvalidEquationException {
        Object val2 = info.popValue();
        Object val1 = info.popValue();

        if (val1 instanceof Number) {
            val1 = new Const((Number) val1);
        }

        try {
            info.pushValue(((Arithmetic) val1).multiply(val2));
        } catch (ClassCastException except) {
            throw (new InvalidEquationException("Invalid calculation: Cannot multiply non-numeric values"));
        }
    }

    private void calculateDivide(CalculateInfo info) throws InvalidEquationException {
        Object val2 = info.popValue();
        Object val1 = info.popValue();

        if (val1 instanceof Number) {
            val1 = new Const((Number) val1);
        }

        try {
            info.pushValue(((Arithmetic) val1).divide(val2));
        } catch (ClassCastException except) {
            throw (new InvalidEquationException("Invalid calculation: Cannot divide non-numeric values"));
        }
    }

    private void calculateMod(CalculateInfo info) throws InvalidEquationException {
        try {
            Number val2 = (Number) info.popValue();
            Number val1 = (Number) info.popValue();
            info.pushValue(new Const(val1.doubleValue() % val2.doubleValue()));
        } catch (ClassCastException except) {
            throw (new InvalidEquationException("Invalid calculation: Cannot mod non-numeric values"));
        }
    }


    public static String getStack(ArrayList list) {
        String stack = "";

        for (int count = list.size() - 1; count >= 0; count--) {
            stack += list.get(count) + " ";
        }

        return stack;
    }


    private class CalculateInfo {

        private String operator;

        private ArrayList valstack = new ArrayList();
        private ArrayList opstack = new ArrayList();


        public void setOperator(String op) {
            operator = op;
        }

        public String getOperator() {
            return operator;
        }


        public void pushValue(Object value) {
            push(value, valstack);
        }

        public Object popValue() {
            return pop(valstack);
        }

        public Object peekValue() {
            return peek(valstack);
        }


        public void pushOperator(Operator op) {
            if (op.operator == OPEN_BRACKET) {
                push(new Operator(OPEN_BRACKET, OPEN_BRACKET_STACK_PRIORITY), opstack);
            } else {
                push(op, opstack);
            }
        }

        public Operator popOperator() {
            return (Operator) pop(opstack);
        }

        public Operator peekOperator() {
            return (Operator) peek(opstack);
        }


        private void push(Object val, ArrayList stack) {
            stack.add(0, val);
        }

        private Object pop(ArrayList stack) {
            if (stack.isEmpty()) {
                return null;
            } else {
                Object obj = stack.get(0);
                stack.remove(0);
                return obj;
            }
        }

        private Object peek(ArrayList stack) {
            if (stack.isEmpty()) {
                return null;
            } else {
                return stack.get(0);
            }
        }

    }

    private class Operator {

        public int operator;
        public int priority;

        public Operator(int operator, int priority) {
            this.operator = operator;
            this.priority = priority;
        }

    }

}
