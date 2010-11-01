package org.trianacode.velocity.form;

import org.trianacode.config.TrianaProperties;
import org.trianacode.http.Output;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Sep 20, 2010
 */
public class Checkbox extends FormComponent {

    public static final String CHECKBOX_TEMPLATE = "checkbox.template";

    private String name;
    private String label;
    private boolean checked;

    public Checkbox(String name, String label, boolean checked, Properties properties, String... classes) {
        this.name = name;
        this.label = label;
        this.checked = checked;
        try {
            Output.registerTemplate(CHECKBOX_TEMPLATE, properties.getProperty(TrianaProperties.CHECKBOX_TEMPLATE_PROPERTY));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String aClass : classes) {
            addClass(aClass);
        }
    }

    public Checkbox(String name, String label, Properties properties, String... classes) {
        this(name, label, false, properties, classes);
    }

    public Checkbox(String name, boolean checked, Properties properties, String... classes) {
        this(name, name, checked, properties, classes);
    }

    public Checkbox(String name, String label, Properties properties) {
        this(name, label, false, properties);
    }

    public Checkbox(String name, boolean checked, Properties properties) {
        this(name, name, checked, properties);
    }


    public Checkbox(String name, Properties properties) {
        this(name, name, false, properties);
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
