package org.trianacode.shiwaall.handler;

import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.shiwa.desktop.data.description.workflow.Author;
import org.shiwa.desktop.data.transfer.WorkflowEngineHandler;
import org.shiwa.fgi.iwir.AbstractDataPort;
import org.shiwa.fgi.iwir.BlockScope;
import org.shiwa.fgi.iwir.IWIR;
import org.trianacode.shiwaall.iwir.importer.utils.ExportIwir;
import org.trianacode.taskgraph.TaskGraph;

import java.io.*;
import java.util.List;
import java.util.Set;

/**
* Created by IntelliJ IDEA.
* User: Ian Harvey
* Date: 15/12/2011
* Time: 14:26
* To change this template use File | Settings | File Templates.
*/
public class TrianaIWIRHandler implements WorkflowEngineHandler {

    private IWIR iwir;
    private InputStream imageInputStream;

    public TrianaIWIRHandler(TaskGraph taskGraph, InputStream trianaImage) {
        try {
            init(taskGraph);
            this.imageInputStream = getImageInputStream(trianaImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream getImageInputStream(InputStream trianaImage){
        InputStream image;
        try{

            // if dot isn't in default unix location, use the triana image.
            File file = new File("/usr/local/bin/dot");
            if(file.exists()){
                File imageFile = iwir.getImage(file.getAbsolutePath(), "png", "");
                System.out.println("dot image " + imageFile.getAbsolutePath());
                image = new FileInputStream(imageFile);
            } else{
                System.out.println("/usr/local/bin/dot not found");
                throw new FileNotFoundException();
            }
        } catch (Exception e){
            e.printStackTrace();
            image = trianaImage;
        }
        return image;
    }

    private void init(TaskGraph taskGraph) throws IOException {
        ExportIwir exportIwir = new ExportIwir();
        BlockScope blockscope = exportIwir.taskGraphToBlockScope(taskGraph);
        iwir = new IWIR(taskGraph.getToolName());
        iwir.setTask(blockscope);
    }

    @Override
    public String getEngineName(Set<String> strings) {
        return "Triana";
    }

    @Override
    public String getEngineVersion() {
        return "4.0";
    }

    @Override
    public String getWorkflowLanguage(Set<String> strings) {
        return "IWIR";
    }

    @Override
    public TransferSignature getSignature() {
        TransferSignature signature = new TransferSignature();
        signature.setName(iwir.getWfname());

        for (AbstractDataPort i : iwir.getTask().getAllInputPorts()) {
            signature.addInput(i.getName(), i.getType().toString());
        }
        for (AbstractDataPort j : iwir.getTask().getAllOutputPorts()) {
            signature.addInput(j.getName(), j.getType().toString());
        }
        return signature;
    }

    @Override
    public InputStream getDefinition() {
        try {
            return new ByteArrayInputStream(iwir.asXMLString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDefinitionName() {
        return iwir.getWfname();
    }

    @Override
    public InputStream getDisplayImage() {
        return imageInputStream;
    }

    @Override
    public String getDisplayImageName() {
        return iwir.getWfname() + "-image.jpg";
    }

    @Override
    public List<Author> getAuthors() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }
}
