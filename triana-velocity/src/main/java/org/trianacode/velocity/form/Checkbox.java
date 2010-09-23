package org.trianacode.velocity.form;

import org.trianacode.velocity.Output;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Sep 20, 2010
 */
public class Checkbox extends FormComponent {

    public static final String CHECKBOX_TEMPLATE = "checkbox.template";
    public static final String CHECKBOX_LOCATION = "/templates/checkbox.tpl";

    static {
        try {
            Output.registerTemplate(CHECKBOX_TEMPLATE, CHECKBOX_LOCATION);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String name;
    private String label;
    private boolean checked;

    public Checkbox(String name, String label, boolean checked, String... classes) {
        this.name = name;
        this.label = label;
        this.checked = checked;
        for (String aClass : classes) {
            addClass(aClass);
        }
    }

    public Checkbox(String name, String label, String... classes) {
        this(name, label, false, classes);
    }

    public Checkbox(String name, boolean checked, String... classes) {
        this(name, name, checked, classes);
    }

    public Checkbox(String name, String label) {
        this(name, label, false);
    }

    public Checkbox(String name, boolean checked) {
        this(name, name, checked);
    }


    public Checkbox(String name) {
        this(name, name, false);
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public boolean isChecked() {
        return checked;
    }

    public String render() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("checkbox", this);
        return Output.outputString(properties, CHECKBOX_TEMPLATE);
    }
}
