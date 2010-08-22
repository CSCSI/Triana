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

package imageproc.input;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.trianacode.gui.panels.FilePanel;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.panels.ParameterWindowInterface;
import org.trianacode.taskgraph.Task;

/**
 * A panel for loading a sequence of gif/jpeg images.
 *
 * @author Ian Wang
 * @version $Revision $
 */

public class LoadMoviePanel extends ParameterPanel implements FocusListener {

    /**
     * the parameter indicating the filename of the first frame
     */
    public static String FILE_NAME = FilePanel.FILE_NAME;

    /**
     * the parameter indicating the frame count
     */
    public static String FRAME_COUNT = "frameCount";


    /**
     * the file panel encapsulated in this panel
     */
    private FilePanel filepanel = new FilePanel();

    /**
     * the text field indicating the number of frames
     */
    private JTextField frames = new JTextField(10);


    /**
     * Sets the task in the file panel
     */
    public void setTask(Task task) {
        super.setTask(task);
        filepanel.setTask(task);
    }

    /**
     * Sets the auto commit interfacew for the file panel
     */
    public void setWindowInterface(ParameterWindowInterface comp) {
        super.setWindowInterface(comp);
        filepanel.setWindowInterface(comp);
    }

    /**
     * Initialises the file panel and the display
     */
    public void init() {
        if (!getTask().isParameterName(FilePanel.FILE_NAME_TEXT)) {
            getTask().setParameter(FilePanel.FILE_NAME_TEXT, "Initial Frame");
        }

        filepanel.init();

        setLayout(new BorderLayout());

        JPanel framecontain = new JPanel(new BorderLayout());
        framecontain.add(frames, BorderLayout.WEST);
        framecontain.setBorder(new EmptyBorder(0, 3, 0, 0));
        frames.addFocusListener(this);

        JPanel labelcontain = new JPanel(new BorderLayout());
        labelcontain.setBorder(new EmptyBorder(3, 3, 3, 3));
        labelcontain.add(new JLabel("Frame Count"), BorderLayout.WEST);
        labelcontain.add(framecontain, BorderLayout.CENTER);

        JPanel subcontain = new JPanel(new BorderLayout());
        subcontain.add(labelcontain, BorderLayout.NORTH);

        add(filepanel, BorderLayout.NORTH);
        add(subcontain, BorderLayout.CENTER);

        reset();
    }

    /**
     * Resets the file panel and the display
     */
    public void reset() {
        if (!getTask().isParameterName(FRAME_COUNT)) {
            getTask().setParameter(FRAME_COUNT, "1");
        }

        frames.setText((String) getTask().getParameter(FRAME_COUNT));

        filepanel.reset();
    }


    public void dispose() {
        filepanel.disable();
    }


    /**
     * Calls ok clicked in the file panel
     */
    public void okClicked() {
        super.okClicked();
        filepanel.okClicked();
    }

    /**
     * Calls cancel clicked in the file panel
     */
    public void cancelClicked() {
        super.cancelClicked();
        filepanel.cancelClicked();
    }

    /**
     * Calls apply clicked in the file panel
     */
    public void applyClicked() {
        super.applyClicked();
        filepanel.applyClicked();
    }


    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        if (event.getSource() == frames) {
            setParameter(FRAME_COUNT, frames.getText());
        }
    }

}
