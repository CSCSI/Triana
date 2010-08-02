package org.trianacode.taskgraph.annotation;

import java.lang.reflect.Field;
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
    private Map<String, String> guiLines = new HashMap<String, String>();
    private String panelClass = null;

    private Map<String, Field> params = new HashMap<String, Field>();

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

    public void setPanelClass(String panelClass) {
        this.panelClass = panelClass;
    }

    public void setGuiLines(Map<String, String> map) {
        this.guiLines = map;
    }

    public void addGuiLine(String field, String guiLine) {
        this.guiLines.put(field, guiLine);
    }

    public void addAnnotatedParameter(String name, Field f) {
        f.setAccessible(true);
        params.put(name, f);
    }

    public void init() {
        setDefaultInputNodes(inputs.length);
        setMinimumInputNodes(inputs.length);
        setMaximumInputNodes(inputs.length);

        setDefaultOutputNodes(outputs.length);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);
        StringBuilder sb = new StringBuilder();
        try {
            for (String s : params.keySet()) {
                Field f = params.get(s);
                Object o = f.get(annotated);
                if (o != null) {
                    defineParameter(s, f.get(annotated), USER_ACCESSIBLE);
                    String guiLine = guiLines.get(s);
                    if (guiLine != null) {
                        sb.append(guiLines.get(s)).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String gl = sb.toString();
        if (gl.length() > 0) {
            System.out.println("AnnotatedUnitWrapper.init guiLines:" + gl);
            setGUIBuilderV2Info(gl);
        }
        if (panelClass != null && panelClass.length() > 0) {
            setParameterPanelClass(panelClass);
            //setParameterPanelInstantiate(Unit.ON_USER_ACCESS);
            //setParameterUpdatePolicy(Unit.IMMEDIATE_UPDATE);
        }
    }

    public void parameterUpdate(String paramname, Object value) {
        Field f = params.get(paramname);
        if (f != null) {
            if (value instanceof String) {
                value = fromString((String) value, f);
            }
            try {
                f.set(annotated, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * best effort...
     *
     * @param value
     * @param field
     * @return
     */
    private Object fromString(String value, Field field) {
        Class type = convert(field.getType());
        if (type.equals(Boolean.class)) {
            return Boolean.valueOf(value);
        } else if (type.equals(Integer.class)) {
            return Integer.parseInt(trim(value));
        } else if (type.equals(Float.class)) {
            return Float.parseFloat(value);
        } else if (type.equals(Double.class)) {
            return Double.parseDouble(value);
        } else if (type.equals(Long.class)) {
            return Long.parseLong(trim(value));
        } else if (type.equals(Short.class)) {
            return Short.parseShort(value);
        } else if (type.equals(Byte.class)) {
            return Byte.parseByte(trim(value));
        }
        return value;
    }

    private static String trim(String real) {
        if (real.indexOf(".") > -1) {
            return real.substring(0, real.indexOf("."));
        }
        return real;
    }

    private static Class convert(Class cls) {
        if (cls.equals(boolean.class)) {
            return java.lang.Boolean.class;
        } else if (cls.equals(short.class)) {
            return java.lang.Short.class;
        } else if (cls.equals(long.class)) {
            return java.lang.Long.class;
        } else if (cls.equals(int.class)) {
            return java.lang.Integer.class;
        } else if (cls.equals(float.class)) {
            return java.lang.Float.class;
        } else if (cls.equals(double.class)) {
            return java.lang.Double.class;
        } else if (cls.equals(byte.class)) {
            return java.lang.Byte.class;
        }
        return cls;
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
        if (ret != null) {
            output(ret);
        }
    }
}
