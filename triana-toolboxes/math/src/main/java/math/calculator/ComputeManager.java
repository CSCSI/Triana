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
 * ComputeManager outlines a set of functions which are implemented by any Triana unit/user interafce which instantiates
 * the Compute class.  Compute uses this interface to send messages back to the unit and to retireve other information
 * as and when it requires it.
 *
 * @author Ian Taylor
 * @version 1.0 2 Jan 1998
 */
public interface ComputeManager {

    /**
     * Gets the data from the <i>i</i>th input node from the unit which implements this interface.  A double array is
     * returned containing the data. If the length of the data is one then the input data must have been a scaler value,
     * otherwise an array of values is returned.
     */
    public double[] getInputData(int i);

    /**
     * Prints the text to the debug stream for the unit followed by a line feed.  This function just returns if there is
     * no debug window.
     */
    public void println(String text);

    /**
     * Prints the text to the debug stream for the unit.  This function just returns if there is no debug window.
     */
    public void print(String text);


    /**
     * Prompts the user to define the variable which is currently undefined. This allows users to not define things
     * until the equation is run.  The user just gets prompted everytime Compute doesn't recognize any constant.
     */
    public String defineVariable(String undefinedVariable);
}














