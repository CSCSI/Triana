package org.trianacode.taskgraph.annotation;

import org.trianacode.annotation.OutputPolicy;
import org.trianacode.annotation.TaskAware;

import java.lang.reflect.Method;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Feb 13, 2011
 */
public class ToolDescriptor {

    private Object annotated;
    private String name;
    private String pckge;
    private int minimumInputs;
    private int minimumOutputs;
    private OutputPolicy outputPolicy;
    private String[] renderingHints;
    private Method customGuiComponent;
    private String panelClass;
    private boolean taskAware = false;

    public Object getAnnotated() {
        return annotated;
    }

    public void setAnnotated(Object annotated) {
        this.annotated = annotated;
        if (annotated instanceof TaskAware) {
            taskAware = true;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPckge() {
        return pckge;
    }

    public void setPckge(String pckge) {
        this.pckge = pckge;
    }

    public int getMinimumInputs() {
        return minimumInputs;
    }

    public void setMinimumInputs(int minimumInputs) {
        this.minimumInputs = minimumInputs;
    }

    public int getMinimumOutputs() {
        return minimumOutputs;
    }

    public void setMinimumOutputs(int minimumOutputs) {
        this.minimumOutputs = minimumOutputs;
    }

    public OutputPolicy getOutputPolicy() {
        return outputPolicy;
    }

    public void setOutputPolicy(OutputPolicy outputPolicy) {
        this.outputPolicy = outputPolicy;
    }

    public String[] getRenderingHints() {
        return renderingHints;
    }

    public void setRenderingHints(String[] renderingHints) {
        this.renderingHints = renderingHints;
    }

    public Method getCustomGuiComponent() {
        return customGuiComponent;
    }

    public void setCustomGuiComponent(Method customGuiComponent) {
        this.customGuiComponent = customGuiComponent;
    }

    public String getPanelClass() {
        return panelClass;
    }

    public void setPanelClass(String panelClass) {
        this.panelClass = panelClass;
    }

    public boolean isTaskAware() {
        return taskAware;
    }

}
