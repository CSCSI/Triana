package org.trianacode.taskgraph.annotation;

import org.trianacode.annotation.OutputPolicy;
import org.trianacode.annotation.TaskAware;
import org.trianacode.taskgraph.Node;
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


    private ToolDescriptor toolDesc;
    private MethodDescriptor methodDesc;
    private Map<String, FieldDescriptor> fieldDescs = new HashMap<String, FieldDescriptor>();

    private Map<String, Object> originalValues = new HashMap<String, Object>();

    public AnnotatedUnitWrapper(ToolDescriptor tooldesc, MethodDescriptor methoddesc, Map<String, FieldDescriptor> fielddescs) {
        this.toolDesc = tooldesc;
        this.methodDesc = methoddesc;
        this.fieldDescs = fielddescs;
        Object annotated = tooldesc.getAnnotated();
        setToolName(annotated.getClass().getSimpleName());
        setToolPackage(getPackageName(annotated.getClass().getName()));
        if (tooldesc.getName() != null) {
            setDisplayName(tooldesc.getName());
        } else {
            setDisplayName(getToolName());
        }
        if (tooldesc.getPckge() != null) {
            setDisplayPackage(tooldesc.getPckge());
        } else {
            setDisplayPackage(getToolPackage());
        }
    }

    public void init() {
        setDefaultInputNodes(0);
        setMinimumInputNodes(toolDesc.getMinimumInputs());
        if (methodDesc.isGather()) {
            setMaximumInputNodes(Integer.MAX_VALUE);
        } else {
            setMaximumInputNodes(methodDesc.getInputs().length);
        }
        setDefaultOutputNodes(methodDesc.getOutputs().length);
        setMinimumOutputNodes(toolDesc.getMinimumOutputs());
        if (methodDesc.getOutputs().length == 0) {
            setMaximumOutputNodes(0);
        } else {
            setMaximumOutputNodes(Integer.MAX_VALUE);
        }
        StringBuilder sb = new StringBuilder();
        Object annotated = toolDesc.getAnnotated();
        try {
            for (String s : fieldDescs.keySet()) {
                FieldDescriptor fd = fieldDescs.get(s);
                Field f = fd.getField();
                Object o = f.get(annotated);
                if (o != null) {
                    originalValues.put(s, o);
                    defineParameter(s, f.get(annotated), USER_ACCESSIBLE);
                    String guiLine = fd.getGuiline();
                    if (guiLine != null) {
                        sb.append(guiLine).append("\n");
                    }
                    String[] rhd = fd.getRenderingDetails();
                    if (rhd != null) {
                        RenderingHint rh = getTask().getRenderingHint(rhd[0]);
                        if (rh != null && rh instanceof RenderingHintImp) {
                            RenderingHintImp imp = (RenderingHintImp) rh;
                            imp.setRenderingDetail(rhd[1], o);
                        }
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
        if (toolDesc.getPanelClass() != null) {
            setParameterPanelClass(toolDesc.getPanelClass());
            setParameterPanelInstantiate(Unit.ON_USER_ACCESS);

        } else {
            if (toolDesc.getCustomGuiComponent() != null) {
                Object gui = null;
                try {
                    gui = toolDesc.getCustomGuiComponent().invoke(toolDesc.getAnnotated(), new Object[0]);
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
        if (toolDesc.getRenderingHints() != null) {
            for (String s : toolDesc.getRenderingHints()) {
                RenderingHintImp hint = new RenderingHintImp(s, false);
                getTask().addRenderingHint(hint);
            }
        }

        if (toolDesc.getOutputPolicy() != null) {
            if (toolDesc.getOutputPolicy() == OutputPolicy.CLONE_MULTIPLE) {
                setOutputPolicy(CLONE_MULTIPLE_OUTPUT);
            } else if (toolDesc.getOutputPolicy() == OutputPolicy.CLONE_MULTIPLE) {
                setOutputPolicy(CLONE_ALL_OUTPUT);
            } else if (toolDesc.getOutputPolicy() == OutputPolicy.CLONE_NONE) {
                setOutputPolicy(COPY_OUTPUT);
            }
        }
    }

    public void reset() {
        for (String s : fieldDescs.keySet()) {
            Object value = originalValues.get(s);
            if (value != null) {
                parameterUpdate(s, value);
            }
        }
    }

    public void parameterUpdate(String paramname, Object value) {
        FieldDescriptor fd = fieldDescs.get(paramname);
        if (fd != null) {
            Field f = fd.getField();
            if (value instanceof String) {
                value = fromString((String) value, f);
            }
            try {
                f.set(toolDesc.getAnnotated(), value);
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
            return Boolean.class;
        } else if (cls.equals(short.class)) {
            return Short.class;
        } else if (cls.equals(long.class)) {
            return Long.class;
        } else if (cls.equals(int.class)) {
            return Integer.class;
        } else if (cls.equals(float.class)) {
            return Float.class;
        } else if (cls.equals(double.class)) {
            return Double.class;
        } else if (cls.equals(byte.class)) {
            return Byte.class;
        }
        return cls;
    }

    @Override
    public String[] getInputTypes() {
        return methodDesc.getInputs();
    }

    @Override
    public String[] getOutputTypes() {
        return methodDesc.getOutputs();
    }

    @Override
    public void process() throws Exception {
        log("ENTER for task " + getTask().getQualifiedToolName());
        Method process = methodDesc.getMethod();
        Object annotated = toolDesc.getAnnotated();
        Class[] clss = process.getParameterTypes();

        if (toolDesc.isTaskAware()) {
            int connectedIns = 0;
            Node[] inNodes = getTask().getDataInputNodes();
            for (int i = 0; i < inNodes.length; i++) {
                Node inNode = inNodes[i];
                if (inNode.isConnected()) {
                    connectedIns++;
                }
            }
            int connectedOuts = 0;
            Node[] outNodes = getTask().getDataOutputNodes();
            for (int i = 0; i < inNodes.length; i++) {
                Node node = outNodes[i];
                if (node.isConnected()) {
                    connectedOuts++;
                }
            }
            TaskAware na = (TaskAware) annotated;
            na.setInputNodeCount(connectedIns);
            na.setOutputNodeCount(connectedOuts);
            na.setTaskName(getTask().getQualifiedTaskName());
            na.setTaskSubtitle(getTask().getSubTitle());
        }

        List<Object> ins = new ArrayList<Object>();
        if (methodDesc.isGather()) {
            for (int count = 0; count < getInputNodeCount(); count++) {
                log("next input node:" + count);
                log("next input node is connected:" + getTask().getInputNode(count).isConnected());
                Object o = getInputAtNode(count);
                log("next input node object:" + o);
                if (o != null) {
                    if (methodDesc.isFlatten()) {
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
        if (methodDesc.isGather()) {
            if (methodDesc.isArray()) {
                input = new Object[]{ins.toArray(new Object[ins.size()])};
            } else { // it's a list or a collection
                input = new Object[]{ins};
            }
        } else {
            input = ins.toArray(new Object[ins.size()]);
        }

        Map<String, Object> currentParams = new HashMap<String, Object>();
        for (String s : fieldDescs.keySet()) {
            currentParams.put(s, fieldDescs.get(s).getField().get(annotated));
        }
        Object ret = process.invoke(annotated, input);
        log("return value is " + ret);
        for (String s : currentParams.keySet()) {
            Object param = currentParams.get(annotated);
            Object now = fieldDescs.get(s).getField().get(annotated);
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
