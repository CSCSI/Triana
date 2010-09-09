package org.trianacode.taskgraph.annotation;

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

    public static AnnotatedUnitWrapper createUnit(Object annotatedObject) {
        Class annotated = annotatedObject.getClass();
        Map<String, String> guiLines = new HashMap<String, String>();
        String name = null;

        String pkge = null;
        String panelClass = null;
        Tool t = (Tool) annotated.getAnnotation(Tool.class);
        if (t == null) {
            return null;
        }
        name = t.displayName();
        pkge = t.displayPackage();
        panelClass = t.panelClass();
        String[] renderingHints = t.renderingHints();

        AnnotatedUnitWrapper wrapper = null;
        Method[] methods = annotated.getDeclaredMethods();
        Map<String, Field> ps = new HashMap<String, Field>();
        for (Method method : methods) {
            Process p = method.getAnnotation(Process.class);
            if (p != null) {
                boolean aggr = p.gather();
                System.out.println("AnnotationProcessor.createUnit gather:" + aggr);
                boolean willAggr = false;
                Class[] params = method.getParameterTypes();
                System.out.println("AnnotationProcessor.createUnit parameters:" + params.length);
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
                    System.out.println("AnnotationProcessor.createUnit created aggregated input type:" + inputs[0]);
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
                wrapper = new AnnotatedUnitWrapper(name, pkge, annotatedObject, method, inputs,
                        outputs, willAggr);
                if(renderingHints != null) {
                    wrapper.setRenderingHints(renderingHints);
                }
                break;
            }
        }
        Field[] fields = annotated.getDeclaredFields();
        for (Field field : fields) {
            boolean hasParam = false;
            String paramName = field.getName();

            Parameter param = field.getAnnotation(Parameter.class);
            if (param != null) {
                hasParam = true;
                ps.put(paramName, field);
            }
            if (!hasParam) {
                TextFieldParameter textField = field.getAnnotation(TextFieldParameter.class);
                if (textField != null) {
                    hasParam = true;
                    String txt = processGuiTextField(textField, field);
                    if (txt != null) {
                        ps.put(paramName, field);
                        guiLines.put(paramName, txt);
                    }
                }
            }
            if (!hasParam) {
                TextAreaParameter textArea = field.getAnnotation(TextAreaParameter.class);
                if (textArea != null) {
                    hasParam = true;
                    String txt = processGuiTextArea(textArea, field);
                    if (txt != null) {
                        ps.put(paramName, field);
                        guiLines.put(paramName, txt);
                    }
                }
            }
            if (!hasParam) {
                ChoiceParameter choice = field.getAnnotation(ChoiceParameter.class);
                if (choice != null) {
                    hasParam = true;
                    String txt = processGuiChoice(choice, field);
                    if (txt != null) {
                        ps.put(paramName, field);
                        guiLines.put(paramName, txt);
                    }
                }
            }
            if (!hasParam) {
                SliderParameter scroll = field.getAnnotation(SliderParameter.class);
                if (scroll != null) {
                    hasParam = true;
                    String txt = processGuiScroller(scroll, field);
                    if (txt != null) {
                        ps.put(paramName, field);
                        guiLines.put(paramName, txt);
                    }
                }
            }
            if (!hasParam) {
                FileParameter file = field.getAnnotation(FileParameter.class);
                if (file != null) {
                    hasParam = true;
                    String txt = processGuiFile(file, field);
                    if (txt != null) {
                        ps.put(paramName, field);
                        guiLines.put(paramName, txt);
                    }
                }
            }
            if (!hasParam) {
                LabelParameter label = field.getAnnotation(LabelParameter.class);
                if (label != null) {
                    hasParam = true;
                    String txt = processGuiLabel(label, field);
                    if (txt != null) {
                        ps.put(paramName, field);
                        guiLines.put(paramName, txt);
                    }
                }
            }
            if (!hasParam) {
                CheckboxParameter label = field.getAnnotation(CheckboxParameter.class);
                if (label != null) {
                    hasParam = true;
                    String txt = processGuiBoolean(label, field);
                    if (txt != null) {
                        ps.put(paramName, field);
                        guiLines.put(paramName, txt);
                    }
                }
            }
            if(!hasParam) {
                RenderingHintDetail detail = field.getAnnotation(RenderingHintDetail.class);
                if(detail != null) {
                    String hint = detail.hint();
                    String d = detail.detail();
                    if(hint != null && d != null) {
                        wrapper.addRenderingDetail(hint, d, field);
                    }
                }
            }
        }
        if (wrapper != null) {
            for (String s : ps.keySet()) {
                wrapper.addAnnotatedParameter(s, ps.get(s));
            }
            if (guiLines.size() > 0) {
                wrapper.setGuiLines(guiLines);
            }
            if (panelClass != null) {
                wrapper.setPanelClass(panelClass);
            }
        }

        return wrapper;

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
