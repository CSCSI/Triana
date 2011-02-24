package org.trianacode.shiwa;

import org.trianacode.TrianaInstance;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.TaskGraph;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 22/02/2011
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
public class PublishWorkflow extends AbstractAction {

    public PublishWorkflow(){
        putValue(SHORT_DESCRIPTION, "Publish");
        putValue(NAME, "Publish");
    }

    public void actionPerformed(ActionEvent actionEvent) {
        System.out.println("Hello World");

        TaskGraph tg = GUIEnv.getApplicationFrame().getSelectedTaskgraph();
        TrianaInstance instance = GUIEnv.getApplicationFrame().getEngine();
        String name = tg.getToolName();


        System.out.println("Taskgraph " + name + " has " + tg.getDataInputNodeCount() + " input nodes.");
        for(int i = 0; i < tg.getInputNodeCount(); i++){
            Node node = tg.getDataInputNode(i);
            for (String s : node.getTask().getDataInputTypes()){
                System.out.println("Taskgraph input node " + i + " can accept : " + s);
            }
        }

        System.out.println("Taskgraph " + name + " has " + tg.getDataOutputNodeCount() + " output nodes.");
        for(int i = 0; i < tg.getOutputNodeCount(); i++){
            Node node = tg.getDataOutputNode(i);
            for (String s : node.getTask().getDataOutputTypes()){
                System.out.println("Taskgraph output node " + i + " may produce : " + s);
            }
        }

//        TrianaEngineHandler teh = new TrianaEngineHandler(instance, tg);
//
//        JPanel popup = new SHIWADesktopPanel(teh);
//
//        JDialog dialog = new JDialog();
//        dialog.add(popup);
//        dialog.setVisible(true);

    }
}
