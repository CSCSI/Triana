package org.trianacode.shiwa.handler;

import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.shiwa.desktop.data.transfer.WorkflowEngineHandler;
import org.shiwa.fgi.iwir.AbstractDataPort;
import org.shiwa.fgi.iwir.IWIR;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public TrianaIWIRHandler(IWIR iwir, InputStream imageInputStream) {
        this.iwir = iwir;
        this.imageInputStream = imageInputStream;
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
        return "iwir";
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
    public InputStream getWorkflowDefinition() {
        try {
            File file = File.createTempFile("iwir", "tmp");
            file.deleteOnExit();
            iwir.asXMLFile(file);
            return new FileInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getWorkflowDefinitionName() {
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
}
