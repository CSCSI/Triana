package org.trianacode.pegasus.converter;

import org.apache.commons.lang.ArrayUtils;
import org.trianacode.enactment.Exec;
import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.hci.ApplicationFrame;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.pegasus.execution.CommandLinePegasus;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.taskgraph.ser.XMLWriter;
import org.trianacode.taskgraph.service.ClientException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 28/10/2011
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class ConvertWorkflowToDax extends AbstractAction implements ActionDisplayOptions {

    public ConvertWorkflowToDax() {
        this(DISPLAY_BOTH);
    }

    public ConvertWorkflowToDax(int displayOption) {
        putValue(SHORT_DESCRIPTION, "Convert");
        putValue(NAME, "Convert to Dax");
        if ((displayOption == DISPLAY_ICON) || (displayOption == DISPLAY_BOTH)) {
            putValue(SMALL_ICON, GUIEnv.getIcon("cog.png"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        final ApplicationFrame frame = GUIEnv.getApplicationFrame();
        final TaskGraph tg = frame.getSelectedTaskgraph();
        if (tg == null || tg.getTasks(false).length == 0) {
            JOptionPane.showMessageDialog(frame, "No taskgraph selected," +
                    " or currently selected taskgraph has no tasks");
        } else {
            convert(tg);
        }
    }

    public void convert(final TaskGraph tg) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    DaxifyTaskGraph converter = new DaxifyTaskGraph();
                    TaskGraph daxifiedTaskGraph = converter.convert(tg);
                    CommandLinePegasus.initTaskgraph(daxifiedTaskGraph, false);

                    GUIEnv.getApplicationFrame().addParentTaskGraphPanel((TaskGraph) daxifiedTaskGraph);
                    try {
                        GUIEnv.getApplicationFrame().getSelectedTrianaClient().run();
                    } catch (ClientException e) {
                        e.printStackTrace();
                    }

                    File file = saveTaskGraph(daxifiedTaskGraph);

                    String[] args = new String[5];
                    args[0] = "-n";
                    args[1] = "-w";
                    args[2] = file.getAbsolutePath();
                    args[3] = "-d";
                    args[4] = "config.dat";

                    System.out.println(ArrayUtils.toString(args));
                    int errorNumber = Exec.exec(args);
                    System.out.println(errorNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.run();

    }

    private File saveTaskGraph(TaskGraph daxifiedTaskGraph) {
        File file = null;
        try {
            file = File.createTempFile(daxifiedTaskGraph.getToolName(), ".txt");
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
            XMLWriter writer = new XMLWriter(fileWriter);
            writer.writeComponent(daxifiedTaskGraph);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
