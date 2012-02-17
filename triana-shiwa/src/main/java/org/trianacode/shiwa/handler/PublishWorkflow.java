package org.trianacode.shiwa.handler;

import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.shiwa.desktop.data.transfer.WorkflowEngineHandler;
import org.shiwa.desktop.gui.SHIWADesktop;
import org.shiwa.fgi.iwir.IWIR;
import org.trianacode.TrianaInstance;
import org.trianacode.enactment.AddonUtils;
import org.trianacode.enactment.addon.ConversionAddon;
import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.action.files.ImageAction;
import org.trianacode.gui.hci.ApplicationFrame;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.panels.DisplayDialog;
import org.trianacode.shiwa.iwir.importer.utils.ExportIwir;
import org.trianacode.taskgraph.TaskGraph;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;


/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 22/02/2011
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
public class PublishWorkflow extends AbstractAction implements ActionDisplayOptions {

    public PublishWorkflow() {
        this(DISPLAY_BOTH);
    }

    public PublishWorkflow(int displayOption) {
        putValue(SHORT_DESCRIPTION, "Publish");
        putValue(NAME, "Publish");
        if ((displayOption == DISPLAY_ICON) || (displayOption == DISPLAY_BOTH)) {
            putValue(SMALL_ICON, GUIEnv.getIcon("upload_small.png"));
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {


        final ApplicationFrame frame = GUIEnv.getApplicationFrame();
        final TaskGraph tg = frame.getSelectedTaskgraph();

        WorkflowEngineHandler handler = buildHandler(tg);
        if (handler != null) {
            publish(handler, frame.getEngine());
        }
    }

    public static WorkflowEngineHandler buildHandler(TaskGraph tg) {
        System.out.println("Publishing Workflow");

        if (tg == null || tg.getTasks(false).length == 0) {
            JOptionPane.showMessageDialog(null, "No taskgraph selected," +
                    " or currently selected taskgraph has no tasks");
            return null;
        } else {
            System.out.println(tg.getQualifiedTaskName());

            InputStream displayStream = null;
            try {
                File imageFile = File.createTempFile("image", ".jpg");
                ImageAction.save(imageFile, 1, "jpg");
                if (imageFile.length() > 0) {
                    displayStream = new FileInputStream(imageFile);
                    System.out.println("Display image created : " + imageFile.toURI());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<ConversionAddon> converters = new ArrayList<ConversionAddon>();
            Set<Object> addons = AddonUtils.getCLIaddons(GUIEnv.getApplicationFrame().getEngine());
            for (Object addon : addons) {
                if (addon instanceof ConversionAddon) {
                    converters.add((ConversionAddon) addon);
                }
            }

            Object[] choices = new Object[converters.size() + 1];
            choices[0] = "taskgraph";
            for (int i = 0; i < converters.size(); i++) {
                ConversionAddon addon = converters.get(i);
//                choices[i] = addon.getShortOption();
                choices[i + 1] = addon;
            }
            Object addon = JOptionPane.showInputDialog(GUIEnv.getApplicationFrame(),
                    "Please select the format for the workflow definition to be submitted in.",
                    "Select Definition Type",
                    JOptionPane.OK_OPTION,
                    null,
                    choices,
                    choices[0]);

            if (addon != null) {
                TransferSignature signature = new TransferSignature();
                if (!(addon instanceof ConversionAddon)) {
                    return new TrianaEngineHandler(tg, tg.getProperties().getEngine(), displayStream);
                } else if (addon.toString().equals("iwir")) {
                    ExportIwir exportIwir = new ExportIwir();
                    //BlockScope blockscope = exportIwir.taskGraphToBlockScope(tg);
                    IWIR iwir = new IWIR(tg.getToolName());
                    //iwir.setTask(blockscope);
                    return new TrianaIWIRHandler(iwir, displayStream);

                } else {
                    InputStream definitionStream = ((ConversionAddon) addon).toolToWorkflowFileInputStream(tg);
                    GenericWorkflowHandler handler = new GenericWorkflowHandler(definitionStream, displayStream);
                    handler.setSignature(signature);
                    handler.setLanguage(addon.toString());
                    handler.setDefinitionName(tg.getToolName());

                    return handler;
                }
            } else {
                return null;
            }
        }
    }

    public static void publish(WorkflowEngineHandler handler, TrianaInstance engine) {
        SHIWADesktop shiwaDesktop = new SHIWADesktop(handler, SHIWADesktop.ButtonOption.SHOW_TOOLBAR);
        DisplayDialog dialog = null;
        shiwaDesktop.addExecutionListener(new TrianaShiwaListener(engine, dialog));
        dialog = new DisplayDialog(shiwaDesktop.getPanel(), "SHIWA Desktop");


    }
}
