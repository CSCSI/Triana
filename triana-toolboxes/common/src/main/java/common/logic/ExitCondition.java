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

import org.trianacode.taskgraph.Task;

/**
 * An interface that is implemented by classes that control when the loop unit exits.
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */

public interface ExitCondition {

    /**
     * Sets an interface to the loop task
     */
    public void setTask(Task task);

    /**
     * @return an interface to the loop task
     */
    public Task getTask();


    /**
     * Called when the loop is initialised (before any iterations are run)
     */
    public void init() throws InvalidEquationException;

    /**
     * Called when an iteration is run
     */
    public void iteration() throws InvalidEquationException;


    /**
     * Calculates whether the loop should exit. This decision can be based on a any factors, including the input data
     * and task parameters.
     *
     * @return true if the loop should exit
     */
    public boolean isExitLoop(Object[] data) throws InvalidEquationException;

}
