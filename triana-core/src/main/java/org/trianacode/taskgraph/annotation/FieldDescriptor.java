package org.trianacode.taskgraph.annotation;

import java.lang.reflect.Field;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Feb 13, 2011
 */
public class FieldDescriptor {

    private String name;
    private Field field;
    private String[] renderingDetails;
    private String guiline;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        field.setAccessible(true);
        this.field = field;
    }

    public String[] getRenderingDetails() {
        return renderingDetails;
    }

    public void setRenderingDetails(String[] renderingDetails) {
        this.renderingDetails = renderingDetails;
    }

    public String getGuiline() {
        return guiline;
    }

    public void setGuiline(String guiline) {
        this.guiline = guiline;
    }
}
