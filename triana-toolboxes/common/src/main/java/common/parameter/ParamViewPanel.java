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

package common.parameter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;

import javax.swing.JLabel;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.windows.WindowButtonConstants;
import org.trianacode.taskgraph.event.ParameterUpdateEvent;
import org.trianacode.taskgraph.event.TaskListener;
import org.trianacode.taskgraph.event.TaskNodeEvent;
import org.trianacode.taskgraph.event.TaskPropertyEvent;

/**
 * A simple panel for displaying constants
 *
 * @author Ian Wang
 * @version $Revision $
 */

public class ParamViewPanel extends ParameterPanel implements TaskListener {

    public static String PARAM_VALUE = "paramValue";

    private JLabel val;


    /**
     * @return false so that the auto commit box is not shown
     */
    public boolean isAutoCommitVisible() {
        return false;
    }

    /**
     * Overridden to return WindowButtonConstants.OK_BUTTON only.
     */
    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_BUTTON;
    }

    /**
     * Overridden to return false, suggesting that the panel prefers to be allowed to be hidden behind the main Triana
     * window.
     */
    public boolean isAlwaysOnTopPreferred() {
        return false;
    }


    /**
     * Initialises the panel.
     */
    public void init() {
        setLayout(new BorderLayout());

        val = new JLabel("Param = 0", JLabel.CENTER);
        add(val, BorderLayout.CENTER);

        getTask().addTaskListener(this);
    }

    /**
     * A empty method as the graph is updated through its task listener interface.
     */
    public void run() {
    }

    /**
     * A empty method as the graph is updated through its task listener interface.
     */
    public void reset() {
    }

    /**
     * Disposes of the graph window and removes this panel as a task listener.
     */
    public void dispose() {
        getTask().removeTaskListener(this);
    }


    /**
     * Updates the graph when the SGTGraphData parameter is changed.
     */
    public void parameterUpdated(ParameterUpdateEvent event) {
        String paramname = event.getParameterName();

        if (paramname.equals(PARAM_VALUE) && (getTask().getParameter(PARAM_VALUE) != null)) {
            Component parent = this;

            while ((parent != null) && (!(parent instanceof Window))) {
                parent = parent.getParent();
            }

            if (!parent.isVisible()) {
                parent.setVisible(true);
            }

            val.setText("Param = " + (String) getTask().getParameter(PARAM_VALUE));
        }
    }

    public void taskPropertyUpdate(TaskPropertyEvent event) {
    }

    public void nodeAdded(TaskNodeEvent event) {
    }

    public void nodeRemoved(TaskNodeEvent event) {
    }

}
