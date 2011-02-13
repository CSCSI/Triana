package org.trianacode.taskgraph.annotation;

import org.trianacode.annotation.OutputPolicy;
import org.trianacode.annotation.TaskAware;
import org.trianacode.taskgraph.RenderingHint;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.imp.RenderingHintImp;
import org.trianacode.taskgraph.tool.ClassLoaders;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 11, 2010
 */

public class AnnotatedUnitWrapper extends Unit {

    private Object annotated;
    private Method process;
    private String[] inputs;
    private String[] outputs;
    private String[] renderingHints = new String[0];
    private Map<String, String> guiLines = new HashMap<String, String>();
    private String panelClass = null;
    private boolean aggregate = false;
    private boolean isArray = false;
    private int minimumInputs = 0;
    private int minimumOutputs = 0;
    private Map<String[], Field> renderingDetails = new HashMap<String[], Field>();
    private Method customGUIComponent;
    private boolean nodeAware = false;
    private boolean flatten = false;
    private OutputPolicy outputClonePolicy;


    private Map<String, Field> params = new HashMap<String, Field>();

    public AnnotatedUnitWrapper(String name, String pkge, Object annotated, Method process, String[] inputs,
                                String[] outputs, boolean aggregate) {
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
        this.aggregate = aggregate;
        if (annotated instanceof TaskAware) {
            System.out.println("AnnotatedUnitWrapper.AnnotatedUnitWrapper OBJECT IS NODE AWARE");
            nodeAware = true;
        }
        for (String input : inputs) {
            debug("AnnotatedUnitWrapper.AnnotatedUnitWrapper INPUT:" + input);
        }
        for (String output : outputs) {
            debug("AnnotatedUnitWrapper.AnnotatedUnitWrapper OUTPUT:" + output);
        }
        if (aggregate) {
            Class[] clss = process.getParameterTypes();
            if (clss.length == 1) {
                Class cls = clss[0];
                if (cls.isArray()) {
                    isArray = true;
                }
            }
        }
    }

    public OutputPolicy getOutputClonePolicy() {
        return outputClonePolicy;
    }

    public void setOutputClonePolicy(OutputPolicy outputClonePolicy) {
        this.outputClonePolicy = outputClonePolicy;
    }

    public boolean isFlatten() {
        return flatten;
    }

    public void setFlatten(boolean flatten) {
        this.flatten = flatten;
    }

    public Method getCustomGUIComponent() {
        return customGUIComponent;
    }

    public void setCustomGUIComponent(Method customGUIComponent) {
        this.customGUIComponent = customGUIComponent;
    }

    public void setPanelClass(String panelClass) {
        this.panelClass = panelClass;
    }

    public void setGuiLines(Map<String, String> map) {
        this.guiLines = map;
    }

    public void addAnnotatedParameter(String name, Field f) {
        f.setAccessible(true);
        params.put(name, f);
    }

    public void setRenderingHints(String[] renderingHints) {
        this.renderingHints = renderingHints;
    }

    public void addRenderingDetail(String hint, String name, Field f) {
        renderingDetails.put(new String[]{hint, name}, f);
    }

    public void setMinimumInputs(int minimumInputs) {
        this.minimumInputs = minimumInputs;
    }

    public void setMinimumOutputs(int minimumOutputs) {
        this.minimumOutputs = minimumOutputs;
    }

    public void init() {
        setDefaultInputNodes(0);
        setMinimumInputNodes(minimumInputs);
        if (aggregate) {
            setMaximumInputNodes(Integer.MAX_VALUE);
        } else {
            setMaximumInputNodes(inputs.length);
        }
        setDefaultOutputNodes(outputs.length);
        setMinimumOutputNodes(minimumOutputs);
        if (outputs.length == 0) {
            setMaximumOutputNodes(0);
        } else {
            setMaximumOutputNodes(Integer.MAX_VALUE);
        }
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
            debug("AnnotatedUnitWrapper.init guiLines:" + gl);
            setGUIBuilderV2Info(gl);
        }
        if (panelClass != null && panelClass.length() > 0) {
            setParameterPanelClass(panelClass);
            setParameterPanelInstantiate(Unit.ON_USER_ACCESS);

        } else {
            if (customGUIComponent != null) {
                Object gui = null;
                try {
                    gui = customGUIComponent.invoke(annotated, new Object[0]);
                    // TODO - HACK. need some sort of callback to panel manager from tool
                    Class manager = ClassLoaders.forName("org.trianacode.gui.panels.ParameterPanelManager");
                    Method setter = manager.getMethod("registerComponent", new Class[]{ClassLoaders.forName("java.awt.Component"), Task.class});
                    setter.invoke(null, new Object[]{gui, this.getTask()});
                } catch (Throwable e) {
                    debug("error creating custom panel", e);
                    e.printStackTrace();
                }
            }
        }
        if (renderingHints.length > 0) {
            for (String s : renderingHints) {
                RenderingHintImp hint = new RenderingHintImp(s, false);
                getTask().addRenderingHint(hint);
            }
        }
        if (renderingDetails.size() > 0) {
            for (String[] s : renderingDetails.keySet()) {
                try {
                    Field f = params.get(s);
                    Object o = f.get(annotated);
                    if (o != null) {
                        RenderingHint rh = getTask().getRenderingHint(s[0]);
                        if (rh != null && rh instanceof RenderingHintImp) {
                            RenderingHintImp imp = (RenderingHintImp) rh;
                            imp.setRenderingDetail(s[1], o);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if (outputClonePolicy != null) {
            if (outputClonePolicy == OutputPolicy.CLONE_MULTIPLE) {
                setOutputPolicy(CLONE_MULTIPLE_OUTPUT);
            } else if (outputClonePolicy == OutputPolicy.CLONE_MULTIPLE) {
                setOutputPolicy(CLONE_ALL_OUTPUT);
            }
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
        log("ENTER for task " + getTask().getQualifiedToolName());
        Class[] clss = process.getParameterTypes();
        int inCount = getInputNodeCount();
        log("input count:" + inCount);
        if (nodeAware) {
            TaskAware na = (TaskAware) annotated;
            na.setInputNodeCount(inCount);
            na.setOutputNodeCount(getOutputNodeCount());
            na.setTaskName(getTask().getQualifiedTaskName());
            na.setTaskSubtitle(getTask().getSubTitle());
        }

        List<Object> ins = new ArrayList<Object>();
        if (aggregate) {
            for (int count = 0; count < inCount; count++) {
                log("next input node:" + count);
                log("next input node is connected:" + getTask().getInputNode(count).isConnected());
                Object o = getInputAtNode(count);
                log("next input node object:" + o);
                if (o != null) {
                    if (flatten) {
                        if (o.getClass().isArray()) {
                            int len = Array.getLength(o);
                            for (int i = 0; i < len; i++) {
                                ins.add(Array.get(o, i));
                            }
                        } else if (o instanceof Collection) {
                            ins.addAll((Collection) o);
                        } else {
                            ins.add(o);
                        }
                    } else {
                        ins.add(o);
                    }
                }
            }
        } else {
            for (int count = 0; count < getInputNodeCount(); count++) {
                Object o = getInputAtNode(count);
                if (o != null) {
                    if (clss.length >= count) {
                        if (clss[count].isAssignableFrom(o.getClass())) {
                            ins.add(o);
                        } else {
                            throw new Exception("class is not assignable");
                        }
                    } else {
                        throw new Exception("parameter length is less than inputs");
                    }
                }
            }
        }
        Object[] input = null;
        if (aggregate) {
            if (isArray) {
                input = new Object[]{ins.toArray(new Object[ins.size()])};
            } else { // it's a list or a collection
                input = new Object[]{ins};
            }
        } else {
            input = ins.toArray(new Object[ins.size()]);
        }

        Map<String, Object> currentParams = new HashMap<String, Object>();
        for (String s : params.keySet()) {
            currentParams.put(s, params.get(s).get(annotated));
        }
        Object ret = process.invoke(annotated, input);
        log("return value is " + ret);
        for (String s : currentParams.keySet()) {
            Object param = currentParams.get(annotated);
            Object now = params.get(s).get(annotated);
            if (now != null) {
                if (!now.equals(param)) {
                    setParameter(s, now);
                }
            } else {
                if (param != null) {
                    if (!now.equals(param)) {
                        setParameter(s, now);
                    }
                }
            }
        }
        if (ret != null) {
            log("EXIT for task " + getTask().getQualifiedTaskName() + " outputted " + ret);
            output(ret);
        }
        log("EXIT for task " + getTask().getQualifiedTaskName() + " did not output ");

    }
}
