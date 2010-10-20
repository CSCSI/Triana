package org.trianacode.velocity.form;

import org.trianacode.config.TrianaProperties;
import org.trianacode.velocity.Output;

import java.io.IOException;
import java.util.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Sep 20, 2010
 */
public class Form extends FormComponent {

    public static final String FORM_TEMPLATE = "form.template";


    private String name;
    private String action;
    private String method;
    private List<FormComponent> components = new ArrayList<FormComponent>();

    public Form(String name, String action, String method, Properties properties) {
        this.name = name;
        this.action = action;
        this.method = method;
        try {
            Output.registerTemplate(FORM_TEMPLATE, properties.getProperty(TrianaProperties.FORM_TEMPLATE_PROPERTY));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        Properties props = TrianaProperties.getDefaultConfiguration();
        Form form = new Form("myform", "cgi-bin", "post", props);
        form.addComponent(new Checkbox("skiing", true, props, "myClass", "myOtherClass"));
        form.addComponent(new Checkbox("snowboarding", "snow boarding", props));
        form.addComponent(new Checkbox("surfing", props));
        form.addClass("cls1");
        form.addClass("cls2");
        System.out.println(form.render());


    }
}
