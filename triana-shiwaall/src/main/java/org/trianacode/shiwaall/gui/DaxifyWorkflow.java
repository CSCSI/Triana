package org.trianacode.shiwaall.gui;

import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.hci.ApplicationFrame;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.shiwaall.converter.DaxifyTaskGraph;
import org.trianacode.shiwaall.gui.guiUnits.DaxFile;
import org.trianacode.shiwaall.gui.guiUnits.DaxJob;
import org.trianacode.taskgraph.TaskGraph;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 31/01/2012
 * Time: 17:20
 * To change this template use File | Settings | File Templates.
 */
public class DaxifyWorkflow extends AbstractAction implements ActionDisplayOptions {
    public DaxifyWorkflow() {
        this(DISPLAY_BOTH);
    }

    public DaxifyWorkflow(int displayOption) {
        putValue(SHORT_DESCRIPTION, "Convert To dax");
        putValue(NAME, "Daxify");
        if ((displayOption == DISPLAY_ICON) || (displayOption == DISPLAY_BOTH)) {
            putValue(SMALL_ICON, GUIEnv.getIcon("job.png"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        final ApplicationFrame frame = GUIEnv.getApplicationFrame();
        final TaskGraph tg = frame.getSelectedTaskgraph();

        DaxifyTaskGraph daxifyTaskGraph = new DaxifyTaskGraph();
        TaskGraph daxified = daxifyTaskGraph.convert(DaxFile.class, DaxJob.class, tg);
        frame.addParentTaskGraphPanel(daxified);
    }
}
