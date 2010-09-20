package org.trianacode.velocity.form;

import org.trianacode.velocity.Output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Sep 20, 2010
 */
public class Form implements FormComponent {

    public static final String FORM_TEMPLATE = "form.template";
    public static final String FORM_LOCATION = "/templates/form.tpl";
    ;

    static {
        try {
            Output.registerTemplate(FORM_TEMPLATE, FORM_LOCATION);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String name;
    private String action;
    private String method;
    private List<FormComponent> components = new ArrayList<FormComponent>();

    public Form(String name, String action, String method) {
        this.name = name;
        this.action = action;
        this.method = method;
    }


    public String getName() {
        return name;
    }

    public String getAction() {
        return action;
    }

    public String getMethod() {
        return method;
    }

    public void addComponent(FormComponent component) {
        components.add(component);
    }

    public List<FormComponent> getComponents() {
        return components;
    }

    @Override
    public String render() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("form", this);
        return Output.outputString(properties, FORM_TEMPLATE);
    }

    public static void main(String[] args) {
        Form form = new Form("myform", "cgi-bin", "post");
        form.addComponent(new Checkbox("skiing", true));
        form.addComponent(new Checkbox("snowboarding", "snow boarding"));
        form.addComponent(new Checkbox("surfing"));
        System.out.println(form.render());


    }
}
