package org.trianacode.taskgraph.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.trianacode.taskgraph.Unit;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 11, 2010
 */

public class AnnotatedUnitWrapper extends Unit {

    private Object annotated;
    private Method process;
    private String[] inputs;
    private String[] outputs;
    private Map<String, Method[]> params = new HashMap<String, Method[]>();

    public AnnotatedUnitWrapper(String name, String pkge, Object annotated, Method process, String[] inputs,
                                String[] outputs) {
        setToolName(annotated.getClass().getSimpleName());
        setToolPackage(getPackageName(annotated.getClass().getName()));
        if (name != null) {
            setDisplayName(name);
        } else {
            setDisplayName(getToolName());
        }
        if (pkge != null) {
            setDisplayPackage(pkge);
        } else {
            setDisplayPackage(getToolPackage());
        }
        this.annotated = annotated;
        this.process = process;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public void addAnnotatedParameter(String name, Method[] m) {
        params.put(name, m);
    }

    public void init() {
        setDefaultInputNodes(inputs.length);
        setMinimumInputNodes(inputs.length);
        setMaximumInputNodes(inputs.length);

        setDefaultOutputNodes(outputs.length);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);
        try {
            for (String s : params.keySet()) {
                Method getter = params.get(s)[0];
                defineParameter(s, getter.invoke(annotated, null), USER_ACCESSIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parameterUpdate(String paramname, Object value) {
        Method[] ms = params.get(paramname);
        if (ms != null) {
            if (ms[1].getParameterTypes()[0].isAssignableFrom(value.getClass())) {
                try {
                    ms[1].invoke(annotated, new Object[]{value});
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String[] getInputTypes() {
        return inputs;
    }

    @Override
    public String[] getOutputTypes() {
        return outputs;
    }

    @Override
    public void process() throws Exception {
        List<Object> objects = new ArrayList<Object>();
        for (int count = 0; count < getInputNodeCount(); count++) {
            objects.add(getInputAtNode(count));
        }

        Object ret = process.invoke(annotated, objects.toArray(new Object[objects.size()]));
        output(ret);
    }
}
