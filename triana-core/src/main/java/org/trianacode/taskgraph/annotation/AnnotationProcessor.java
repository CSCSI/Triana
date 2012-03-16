package org.trianacode.taskgraph.annotation;

import org.apache.commons.logging.Log;
import org.trianacode.annotation.*;
import org.trianacode.annotation.Process;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.proxy.ProxyInstantiationException;
import org.trianacode.taskgraph.tool.ClassLoaders;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 10, 2010
 */

public class AnnotationProcessor {

    private static Log log = Loggers.TOOL_LOGGER;


    public static AnnotatedUnitWrapper createUnit(Object annotatedObject) throws ProxyInstantiationException {
        Class annotated = annotatedObject.getClass();
        Tool t = (Tool) annotated.getAnnotation(Tool.class);
        if (t == null) {
            throw new ProxyInstantiationException("Could not get the annotation for the class " + annotatedObject);
        }
        ToolDescriptor td = processTool(t, annotatedObject);

        Method[] methods = annotated.getDeclaredMethods();
        MethodDescriptor md = new MethodDescriptor();
        md = processMethods(td, md, methods);
        Class superClass = annotated.getSuperclass();
        while (md.getMethod() == null && superClass != null && !(superClass.equals(Object.class))) {
            methods = superClass.getDeclaredMethods();
            md = processMethods(td, md, methods);
            superClass = superClass.getSuperclass();
        }
        if (md.getMethod() == null) {
            log.info("no @Process method found for " + annotated);
            return null;
        }
        if (md.isGather()) {
            Class[] clss = md.getMethod().getParameterTypes();
            if (clss.length == 1) {
                Class cls = clss[0];
                if (cls.isArray()) {
                    md.setArray(true);
                }
            }
        }
        Field[] fields = annotated.getDeclaredFields();
        Map<String, FieldDescriptor> fds = processFields(fields);
        superClass = annotated.getSuperclass();
        while (superClass != null && !(superClass.equals(Object.class))) {
            fields = superClass.getDeclaredFields();
            Map<String, FieldDescriptor> next = processFields(fields);
            for (String s : next.keySet()) {
                if (!fds.containsKey(s)) {
                    fds.put(s, next.get(s));
                }
            }
            superClass = superClass.getSuperclass();
        }
        AnnotatedUnitWrapper wrapper = new AnnotatedUnitWrapper(td, md, fds);

        return wrapper;
    }

    private static FieldDescriptor createFieldDescriptor(Field f, String guiLines) {
        FieldDescriptor fd = new FieldDescriptor();
        fd.setName(f.getName());
        fd.setField(f);
        fd.setGuiline(guiLines);
        addRenderingHintDetail(fd);
        return fd;
    }

    private static Map<String, FieldDescriptor> processFields(Field[] fields) {
        Map<String, FieldDescriptor> ret = new HashMap<String, FieldDescriptor>();
        for (Field field : fields) {
            Parameter param = field.getAnnotation(Parameter.class);
            if (param != null) {
                FieldDescriptor fd = new FieldDescriptor();
                fd.setName(field.getName());
                fd.setField(field);
                addRenderingHintDetail(fd);
                ret.put(field.getName(), fd);
                continue;
            }
            TextFieldParameter textField = field.getAnnotation(TextFieldParameter.class);
            if (textField != null) {
                String txt = processGuiTextField(textField, field);
                if (txt != null) {
                    FieldDescriptor fd = createFieldDescriptor(field, txt);
                    ret.put(field.getName(), fd);
                    continue;
                }
            }
            TextAreaParameter textArea = field.getAnnotation(TextAreaParameter.class);
            if (textArea != null) {
                String txt = processGuiTextArea(textArea, field);
                if (txt != null) {
                    FieldDescriptor fd = createFieldDescriptor(field, txt);
                    ret.put(field.getName(), fd);
                    continue;
                }
            }
            ChoiceParameter choice = field.getAnnotation(ChoiceParameter.class);
            if (choice != null) {
                String txt = processGuiChoice(choice, field);
                if (txt != null) {
                    FieldDescriptor fd = createFieldDescriptor(field, txt);
                    ret.put(field.getName(), fd);
                    continue;
                }
            }
            SliderParameter scroll = field.getAnnotation(SliderParameter.class);
            if (scroll != null) {
                String txt = processGuiScroller(scroll, field);
                if (txt != null) {
                    FieldDescriptor fd = createFieldDescriptor(field, txt);
                    ret.put(field.getName(), fd);
                    continue;
                }
            }
            FileParameter file = field.getAnnotation(FileParameter.class);
            if (file != null) {
                String txt = processGuiFile(file, field);
                if (txt != null) {
                    FieldDescriptor fd = createFieldDescriptor(field, txt);
                    ret.put(field.getName(), fd);
                    continue;
                }
            }
            LabelParameter label = field.getAnnotation(LabelParameter.class);
            if (label != null) {
                String txt = processGuiLabel(label, field);
                if (txt != null) {
                    FieldDescriptor fd = createFieldDescriptor(field, txt);
                    ret.put(field.getName(), fd);
                    continue;
                }
            }
            CheckboxParameter check = field.getAnnotation(CheckboxParameter.class);
            if (check != null) {
                String txt = processGuiBoolean(check, field);
                if (txt != null) {
                    FieldDescriptor fd = createFieldDescriptor(field, txt);
                    ret.put(field.getName(), fd);
                    continue;
                }
            }
        }
        return ret;
    }

    private static void addRenderingHintDetail(FieldDescriptor fd) {
        Field field = fd.getField();
        RenderingHintDetail detail = field.getAnnotation(RenderingHintDetail.class);
        if (detail != null) {
            String hint = detail.hint();
            String d = detail.detail();
            if (hint != null && d != null) {
                fd.setRenderingDetails(new String[]{hint, d});
            }
        }
    }

    private static ToolDescriptor processTool(Tool tool, Object annotated) {
        ToolDescriptor td = new ToolDescriptor();
        td.setAnnotated(annotated);
        td.setMinimumInputs(tool.minimumInputs());
        td.setMinimumOutputs(tool.minimumOutputs());
        td.setOutputPolicy(tool.outputPolicy());
        if (tool.displayName() != null && tool.displayName().length() > 0) {
            td.setName(tool.displayName());
        }
        if (tool.displayPackage() != null && tool.displayPackage().length() > 0) {
            td.setPckge(tool.displayPackage());
        }
        if (tool.panelClass() != null && tool.panelClass().length() > 0) {
            td.setPanelClass(tool.panelClass());
        }
        if (tool.renderingHints() != null && tool.renderingHints().length > 0) {
            td.setRenderingHints(tool.renderingHints());
        }
        return td;
    }

    private static MethodDescriptor processMethods(ToolDescriptor td, MethodDescriptor md, Method[] methods) {

        for (Method method : methods) {
            if (md.getMethod() == null) {
                Process p = method.getAnnotation(Process.class);
                if (p != null) {
                    boolean aggr = p.gather();
                    boolean flatten = p.flatten();
                    boolean willAggr = false;
                    boolean willFlatten = false;
                    Class[] params = method.getParameterTypes();
                    String[] inputs = null;
                    if (aggr && params.length == 1) {
                        Class coll = params[0];
                        if (coll.isArray()) {
                            inputs = new String[]{convert(coll.getComponentType())};
                            willAggr = true;
                        } else if (Collection.class.isAssignableFrom(coll) || coll.equals(List.class)) {
                            inputs = new String[]{"java.lang.Object"};
                            willAggr = true;
                        }
                    }
                    if (inputs == null) {
                        inputs = new String[params.length];
                        for (int i = 0; i < params.length; i++) {
                            Class param = params[i];
                            inputs[i] = convert(param);
                        }
                    }
                    String[] outputs;
                    Class ret = method.getReturnType();
                    if (ret.equals(void.class)) {
                        outputs = new String[0];
                    } else {
                        outputs = new String[]{convert(ret)};
                    }
                    if (willAggr && flatten) {
                        willFlatten = true;
                    }
                    md.setMethod(method);
                    md.setGather(willAggr);
                    md.setFlatten(willFlatten);
                    md.setInputs(inputs);
                    md.setOutputs(outputs);
                    md.setMultipleOutputNodes(p.multipleOutputNodes());

                }
            }
            if (td.getCustomGuiComponent() == null) {
                CustomGUIComponent component = method.getAnnotation(CustomGUIComponent.class);
                if (component != null) {
                    Class ret = method.getReturnType();
                    try {
                        if (ClassLoaders.forName("java.awt.Component").isAssignableFrom(ret)) {
                            Class[] ins = method.getParameterTypes();
                            if (ins.length == 0) {
                                td.setCustomGuiComponent(method);
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return md;
    }


    private static String processGuiTextField(TextFieldParameter field, Field f) {
        String title = field.title();
        String value = field.value();
        StringBuilder sb = new StringBuilder();
        if (title.isEmpty()) {
            title = f.getName();
        }
        sb.append(title).append(" $title ").append(f.getName()).append(" TextField ").append(value);
        return sb.toString();

    }

    private static String processGuiTextArea(TextAreaParameter field, Field f) {
        String title = field.title();
        String value = field.value();
        StringBuilder sb = new StringBuilder();
        if (title.isEmpty()) {
            title = f.getName();
        }
        sb.append(title).append(" $title ").append(f.getName()).append(" TextArea ").append(value);
        return sb.toString();

    }

    private static String processGuiChoice(ChoiceParameter field, Field f) {
        String title = field.title();
        String[] values = field.values();
        if (values.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (title.isEmpty()) {
            title = f.getName();
        }
        sb.append(title).append(" $title ").append(f.getName()).append(" Choice ");
        for (String value : values) {
            sb.append(" ").append(value);
        }
        return sb.toString();

    }

    private static String processGuiFile(FileParameter field, Field f) {
        String title = field.title();
        String filename = field.filename();
        String[] exts = field.extensions();
        StringBuilder sb = new StringBuilder();
        if (title.isEmpty()) {
            title = f.getName();
        }
        sb.append(title).append(" $title ").append(f.getName()).append(" File ").append(filename);
        for (String ext : exts) {
            if (!ext.startsWith("*.")) {
                ext = "*." + ext;
            }
            sb.append(" ").append(ext);
        }
        return sb.toString();

    }

    private static String processGuiScroller(SliderParameter field, Field f) {
        String title = field.title();
        String type = field.integer() == true ? " IntScroller " : " Scroller ";
        StringBuilder sb = new StringBuilder();
        if (title.isEmpty()) {
            title = f.getName();
        }
        sb.append(title).append(" $title ").append(f.getName()).append(type).append(field.min()).append(" ")
                .append(field.max()).append(" ").append(field.current());
        return sb.toString();

    }

    private static String processGuiLabel(LabelParameter field, Field f) {
        String title = field.title();
        StringBuilder sb = new StringBuilder();
        if (title.isEmpty()) {
            title = f.getName();
        }
        sb.append(title).append(" $title ").append(f.getName()).append(" Label ");
        return sb.toString();

    }

    private static String processGuiBoolean(CheckboxParameter field, Field f) {
        String title = field.title();
        StringBuilder sb = new StringBuilder();
        if (title.isEmpty()) {
            title = f.getName();
        }
        sb.append(title).append(" $title ").append(f.getName()).append(" Checkbox ").append(field.value());
        return sb.toString();

    }

    private static String convert(Class cls) {
        if (cls.equals(boolean.class)) {
            return "java.lang.Boolean";
        } else if (cls.equals(short.class)) {
            return "java.lang.Short";
        } else if (cls.equals(long.class)) {
            return "java.lang.Long";
        } else if (cls.equals(int.class)) {
            return "java.lang.Integer";
        } else if (cls.equals(float.class)) {
            return "java.lang.Float";
        } else if (cls.equals(double.class)) {
            return "java.lang.Double";
        }
        return cls.getName();
    }
}
