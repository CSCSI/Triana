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

/**
 * ComputeFunctionUseException is an extension of ComputeExpressionException which is invoked if the user invokes an
 * existing function with an illegal combination of arguments.  It returns an error message that will be displayed to
 * the user.
 *
 * @author Bernard Schutz
 * @version 1.0 30 December 1998
 */

public class ComputeFunctionUseException extends ComputeExpressionException {
    public ComputeFunctionUseException() {
        super();
    }

    public ComputeFunctionUseException(String message) {
        super("a function has illegal types and/or numbers of arguments. " + message);
    }

    public ComputeFunctionUseException(String name, String arguments) {
        super("the function \"" + name + "\" cannot be evaluated on the arguments \"" + arguments
                + "\".  Check the help file to find what number and types of arguments are permissible. (NB: The function name given in this error message is the standard internal name for the function; any alias you may have typed has been replaced.)");
    }

    public ComputeFunctionUseException(String name, String arguments, String advice) {
        super("the function \"" + name + "\" cannot be evaluated on the arguments \"" + arguments + "\". " + advice
                + " Check the help file. (NB: The function name given in this error message is the standard internal name for the function; any alias you may have typed has been replaced.)");
    }
}













