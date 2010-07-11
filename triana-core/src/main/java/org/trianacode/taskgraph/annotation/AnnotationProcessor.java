package org.trianacode.taskgraph.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 10, 2010
 */

public class AnnotationProcessor {

    public static AnnotatedUnitWrapper createUnit(Object annotatedObject) {
        Class annotated = annotatedObject.getClass();
        String name = null;
        String pkge = null;
        Tool t = (Tool) annotated.getAnnotation(Tool.class);
        if (t == null) {
            return null;
        }
        name = t.displayName();
        pkge = t.displayPackage();
        AnnotatedUnitWrapper wrapper = null;
        Method[] methods = annotated.getDeclaredMethods();
        Map<String, Method[]> ps = new HashMap<String, Method[]>();
        for (Method method : methods) {
            Process p = method.getAnnotation(Process.class);
            if (p != null) {
                Class[] params = method.getParameterTypes();
                String[] inputs = new String[params.length];
                for (int i = 0; i < params.length; i++) {
                    Class param = params[i];
                    inputs[i] = convert(param);
                }
                String[] outputs;
                Class ret = method.getReturnType();
                if (ret.equals(void.class)) {
                    outputs = new String[0];
                } else {
                    outputs = new String[]{convert(ret)};
                }
                wrapper = new AnnotatedUnitWrapper(name, pkge, annotatedObject, method, inputs,
                        outputs);
            }
        }
        Field[] fields = annotated.getFields();
        for (Field field : fields) {
            Parameter param = field.getAnnotation(Parameter.class);
            if (param != null) {
                String paramName = param.name();
                if (paramName.isEmpty()) {
                    paramName = field.getName();
                }
                Method[] beans = getBeanMethods(field, annotated);
                if (beans != null) {
                    ps.put(paramName, beans);
                }
            }
        }
        if (wrapper != null) {
            for (String s : ps.keySet()) {
                wrapper.addAnnotatedParameter(s, ps.get(s));
            }
        }
        return wrapper;

    }

    private static Method[] getBeanMethods(Field field, Class cls) {
        Class val = field.getType();
        String name = field.getName();
        String methodField = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
        Method getter = null;
        try {
            getter = cls.getMethod("get" + methodField);
        } catch (NoSuchMethodException e) {
        }
        Method setter = null;
        if (val.equals(boolean.class) || val.equals(Boolean.class)) {
            try {
                setter = cls.getMethod("is" + methodField);
            } catch (NoSuchMethodException e) {
                try {
                    setter = cls.getMethod("set" + methodField);
                } catch (NoSuchMethodException e1) {

                }
            }
        } else {
            try {
                setter = cls.getMethod("set" + methodField);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        if (setter == null || getter == null) {
            return null;
        }
        if (getter.getParameterTypes().length != 0 || !getter.getReturnType().equals(val)) {
            return null;
        }
        if (setter.getParameterTypes().length != 1
                || !setter.getParameterTypes()[0].equals(val)
                || !setter.getReturnType().equals(void.class)) {
            return null;
        }
        return new Method[]{getter, setter};


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
