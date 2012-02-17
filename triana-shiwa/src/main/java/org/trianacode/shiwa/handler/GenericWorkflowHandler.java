package org.trianacode.shiwa.handler;

import org.shiwa.desktop.data.description.handler.TransferSignature;
import org.shiwa.desktop.data.transfer.WorkflowEngineHandler;

import java.io.InputStream;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 16/12/2011
 * Time: 11:44
 * To change this template use File | Settings | File Templates.
 */
public class GenericWorkflowHandler implements WorkflowEngineHandler {

    private InputStream definitionStream;
    private InputStream imageStream;
    private String definitionName;
    private String language;
    private TransferSignature signature;

    public GenericWorkflowHandler(InputStream definitionStream, InputStream imageStream) {
        this.definitionStream = definitionStream;

        this.imageStream = imageStream;
    }

    @Override
    public String getEngineName(Set<String> strings) {
        return "triana";
    }

    @Override
    public String getEngineVersion() {
        return "4.0";
    }

    @Override
    public String getWorkflowLanguage(Set<String> strings) {
        return language;
    }

    @Override
    public TransferSignature getSignature() {
        return signature;
    }

    @Override
    public InputStream getWorkflowDefinition() {
        return definitionStream;
    }

    @Override
    public String getWorkflowDefinitionName() {
        return definitionName;
    }

    @Override
    public InputStream getDisplayImage() {
        return imageStream;
    }

    @Override
    public String getDisplayImageName() {
        return definitionName + "-image";
    }

    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setSignature(TransferSignature signature) {
        this.signature = signature;
    }
}
