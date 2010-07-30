package org.trianacode.toolloading.protocols;

/**
 * Created by IntelliJ IDEA.
 * User: scmijt
 * Date: Jul 27, 2010
 * Time: 3:34:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class TypeMetadata {
    String description;
    Class inputForm=null;
    String commandLine=null;

    TypeMetadata(String description) {
        this.description = description;
    }

    public TypeMetadata(String description, Class inputForm) {
        this.description = description;
        this.inputForm = inputForm;
    }

    public TypeMetadata(String description, Class inputForm, String commandLine) {
        this.description = description;
        this.inputForm = inputForm;
        this.commandLine = commandLine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Class getInputForm() {
        return inputForm;
    }

    public void setInputForm(Class inputForm) {
        this.inputForm = inputForm;
    }

    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }
}