package org.trianacode.taskgraph;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Takes a class in the constructor and creates inputs to a selected method.
 * Outputs the result of invoking the method, or fi a primitive or collection or enum,
 * outputs the input.
 *
 * @author Andrew Harrison
 * @version $Revision: 1.16 $
 * @created 03 Jun 2006
 * @date $Date: 2004/06/11 15:59:20 $ modified by $Author: spxinw $
 */
public class BeanUnit extends Unit {

    private List<Method> setters = new ArrayList<Method>();
    private List<Method> getters = new ArrayList<Method>();

    private Method selectedMethod = null;
    private String[] currentInputs = new String[0];
    private String[] currentOutputs = new String[0];

    private Method[] ignores = Object.class.getMethods();

    private Object bean;
    private Class beanClass;
    private String primitiveValue = null;
    private boolean isList = false;

    public BeanUnit(Class beanClass) throws Exception {
        this.beanClass = wrapPrimitiveClass(beanClass);
        if (beanClass.isInterface() || Modifier.isAbstract(beanClass.getModifiers())) {
            throw new IllegalArgumentException("Class cannot be be an interface or abstract");
        }
        if (isArrayOrCollection(this.beanClass)) {
            if (isCollection(this.beanClass)) {
                bean = this.beanClass.newInstance();
            } else {
                bean = Array.newInstance(Object.class, 0);
            }
            if (isArray(beanClass)) {
                currentInputs = new String[]{beanClass.getComponentType().getName()};

            } else {
                currentInputs = new String[]{Object.class.getName()};
                //currentOutputs = new String[]{Collection.class.getName()};
            }
            currentOutputs = new String[]{beanClass.getName()};
            isList = true;
        } else if (!isPrimitiveOrWrapperOrEnum(this.beanClass)) {
            this.bean = this.beanClass.newInstance();
            List<Method>[] arr = extractMethods(this.beanClass);
            this.setters = arr[0];
            this.getters = arr[1];
            if (setters.size() > 0 && getters.size() > 0) {
                this.selectedMethod = setters.get(0);
                setIOTypes(selectedMethod);
            }
        } else {
            currentInputs = new String[]{this.beanClass.getName()};
            currentOutputs = new String[]{this.beanClass.getName()};
        }
    }

    /*
    * Called whenever there is data for the unit to process
    */

    public void process() {
        Method m = selectedMethod;
        if (m != null) {
            List<Object> params = new ArrayList<Object>();
            for (int count = 0; count < getInputNodeCount(); count++) {
                Object obj = getInputAtNode(count);
                params.add(obj);
            }
            try {
                m.invoke(bean, params.toArray(new Object[params.size()]));
                Method getter = getMatchingMethod(m);
                Object ret = getter.invoke(bean, null);
                if (ret != null) {
                    output(ret);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (primitiveValue != null) {
                setValue(primitiveValue);
            } else {
                if (isList) {
                    List<Object> params = new ArrayList<Object>();
                    for (int count = 0; count < getInputNodeCount(); count++) {
                        Object obj = getInputAtNode(count);
                        params.add(obj);
                    }
                    if (isArray(beanClass)) {
                        bean = Array.newInstance(Object.class, params.size());
                        for (int i = 0; i < params.size(); i++) {
                            Object o = params.get(i);
                            Array.set(bean, i, o);
                        }
                    } else {
                        for (Object param : params) {
                            ((Collection) bean).add(param);
                        }
                    }

                } else {
                    Object obj = getInputAtNode(0);
                    this.bean = obj;
                }
            }
            output(bean);
        }
    }

    private List<Method>[] extractMethods(Class o) {
        Method[] ms = o.getDeclaredMethods();
        List<Method> s = new ArrayList<Method>();
        List<Method> g = new ArrayList<Method>();
        for (Method m : ms) {
            if (!Modifier.isPublic(m.getModifiers())) {
                continue;
            }
            boolean not = false;
            for (Method ignore : ignores) {
                if (m.equals(ignore)) {
                    not = true;
                    break;
                }
            }
            if (not) {
                continue;
            }
            if (m.getName().length() > 4 && m.getName().startsWith("set")) {
                String field = m.getName().substring(3, m.getName().length());
                field = field.substring(0, 1).toLowerCase() + field.substring(1, field.length());
                Field[] fs = o.getDeclaredFields();
                for (Field f : fs) {
                    if (f.getName().equals(field)) ;
                    s.add(m);
                    break;
                }
            } else if ((m.getName().length() > 4 && m.getName().startsWith("get")) || (m.getName().length() > 4
                    && m.getName().startsWith("is"))) {
                String name = m.getName();
                int offset;
                offset = name.startsWith("get") ? 3 : 2;
                String field = name.substring(offset, name.length());
                field = field.substring(0, 1).toLowerCase() + field.substring(1, field.length());
                Field[] fs = o.getDeclaredFields();
                for (Field f : fs) {
                    if (f.getName().equals(field)) ;
                    g.add(m);
                    break;
                }
            }
        }
        return new List[]{s, g};
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and
     * parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(0);
        setMinimumInputNodes(0);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(COPY_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("Takes a bean object and invokes the chosen method on it.");
        setHelpFileLocation("BeanUnit.html");
        if (selectedMethod != null) {
            defineParameter("selectedMethod", selectedMethod.getName(), USER_ACCESSIBLE);
            StringBuilder sb = new StringBuilder();
            sb.append("Method").append(" $title ").append("selectedMethod").append(" Choice");
            for (Method value : setters) {
                sb.append(" ").append(value.getName());
            }
            setGUIBuilderV2Info(sb.toString() + "\n");
            try {
                getTask().setSubTitle(selectedMethod.getName());
                updateInputNodes();
                updateOutputNodes();
            } catch (NodeException e) {
                e.printStackTrace();
            }
        } else {
            if (!isList) {
                defineParameter("value", "", USER_ACCESSIBLE);
                setGUIBuilderV2Info("Value $title value TextField\n");
            }
        }
    }

    private Method getMatchingMethod(Method m) {
        if (m.getName().length() > 4 && m.getName().startsWith("set")) {
            String field = m.getName().substring(3, m.getName().length());
            for (Method getter : getters) {
                if (getter.getName().equals("get" + field)) {
                    return getter;
                } else if (getter.getName().equals("is" + field)) {
                    return getter;
                }
            }
        } else if ((m.getName().length() > 4 && m.getName().startsWith("get")) || (m.getName().length() > 4
                && m.getName().startsWith("is"))) {
            String name = m.getName();
            int offset;
            offset = name.startsWith("get") ? 3 : 2;
            String field = name.substring(offset, name.length());
            for (Method setter : setters) {
                if (setter.getName().equals("set" + field)) {
                    return setter;
                }
            }
        }
        return null;
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values
     * specified by the parameters.
     */
    public void reset() {
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up NewInstance (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("selectedMethod")) {
            String val = (String) value;
            System.out.println("BeanUnit.parameterUpdate value:" + val);
            for (Method setter : setters) {
                if (setter.getName().equals(val)) {
                    selectedMethod = setter;
                    setIOTypes(selectedMethod);
                    try {
                        getTask().setSubTitle(selectedMethod.getName());
                        updateInputNodes();
                        updateOutputNodes();
                    } catch (NodeException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } else if (paramname.equals("value")) {
            this.primitiveValue = (String) value;
        }
    }

    private void setIOTypes(Method selectedMethod) {
        List<String> ins = new ArrayList<String>();
        Class[] params = selectedMethod.getParameterTypes();
        for (Class param : params) {
            ins.add(param.getName());
        }
        currentInputs = ins.toArray(new String[ins.size()]);
        Method getter = getMatchingMethod(selectedMethod);

        if (getter != null) {
            Class output = getter.getReturnType();
            if (!output.equals(void.class)) {
                currentOutputs = new String[]{output.getName()};
            }
        }
    }

    /**
     * todo - mesh types
     *
     * @throws NodeException
     */
    private void updateInputNodes() throws NodeException {
        Node[] ins = getTask().getDataInputNodes();
        for (int i = ins.length - 1; i >= 0; i--) {
            Node n = ins[i];
            getTask().removeDataInputNode(n);
        }
        for (String currentInput : currentInputs) {
            getTask().addDataInputNode();
        }
    }

    private void updateOutputNodes() throws NodeException {
        Node[] ns = getTask().getDataOutputNodes();
        for (int i = ns.length - 1; i >= 0; i--) {
            Node n = ns[i];
            getTask().removeDataOutputNode(n);
        }
        for (String current : currentOutputs) {
            getTask().addDataOutputNode();
        }
    }


    /**
     * @return an array of the input types accepted by nodes not covered
     *         by getNodeInputTypes().
     */
    public String[] getInputTypes() {
        return currentInputs;
    }


    /**
     * @return an array of the input types output by nodes not covered
     *         by getNodeOutputTypes().
     */
    public String[] getOutputTypes() {
        return currentOutputs;
    }

    public boolean isArray(Class cls) {
        return cls.isArray();
    }

    public boolean isCollection(Class cls) {
        return Collection.class.isAssignableFrom(cls);
    }

    public boolean isArrayOrCollection(Class cls) {
        return isArray(cls) || isCollection(cls);
    }

    public static boolean isPrimitive(Class cls) {
        return cls == Boolean.TYPE ||
                cls == Byte.TYPE ||
                cls == Short.TYPE ||
                cls == Character.TYPE ||
                cls == Integer.TYPE ||
                cls == Long.TYPE ||
                cls == Double.TYPE ||
                cls == Float.TYPE;
    }


    public static boolean isWrapper(Class cls) {
        return cls.equals(Boolean.class) ||
                cls.equals(Byte.class) ||
                cls.equals(Short.class) ||
                cls.equals(Character.class) ||
                cls.equals(Integer.class) ||
                cls.equals(Long.class) ||
                cls.equals(Double.class) ||
                cls.equals(Float.class);
    }

    public void setValue(String value) {
        if (isWrapper(beanClass)) {
            try {
                Constructor c = beanClass.getConstructor(String.class);
                bean = c.newInstance(value);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Constructor c = beanClass.getConstructor(char.class);
                    bean = c.newInstance(value.charAt(0));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            if (beanClass.equals(String.class)) {
                bean = value;
            } else if (Enum.class.isAssignableFrom(beanClass)) {
                bean = Enum.valueOf(beanClass, value);
            }
        }
    }

    public static boolean isEnum(Class cls) {
        return Enum.class.isAssignableFrom(cls);
    }

    public static boolean isPrimitiveOrWrapperOrEnum(Class cls) {
        return isPrimitive(cls) || isWrapper(cls) || isEnum(cls);
    }

    public static Object wrapPrimitive(boolean val) {
        return new Boolean(val);
    }

    public static Object wrapPrimitive(int val) {
        return new Integer(val);
    }

    public static Object wrapPrimitive(byte val) {
        return new Byte(val);
    }

    public static Object wrapPrimitive(short val) {
        return new Short(val);
    }

    public static Object wrapPrimitive(long val) {
        return new Long(val);
    }

    public static Object wrapPrimitive(char val) {
        return new Character(val);
    }

    public static Object wrapPrimitive(double val) {
        return new Double(val);
    }

    public static Object wrapPrimitive(float val) {
        return new Float(val);
    }


    public static Object wrapPrimitive(String val) {
        if (val.equals("boolean")) {
            return wrapPrimitive(false);
        } else if (val.equals("byte")) {
            byte x = 0;
            return wrapPrimitive(x);
        } else if (val.equals("short")) {
            short x = 0;
            return wrapPrimitive(x);
        } else if (val.equals("char")) {
            char x = 0;
            return wrapPrimitive(x);
        } else if (val.equals("long")) {
            long x = 0L;
            return wrapPrimitive(x);
        } else if (val.equals("double")) {
            double x = 0.0;
            return wrapPrimitive(x);
        } else if (val.equals("float")) {
            float x = 0.0F;
            return wrapPrimitive(x);
        } else {
            int x = 0;
            return wrapPrimitive(x);
        }
    }

    public static Class wrapPrimitiveClass(Class cls) {
        if (cls.equals(boolean.class)) {
            return Boolean.class;
        } else if (cls.equals(byte.class)) {
            return Byte.class;
        } else if (cls.equals(int.class)) {
            return Integer.class;
        } else if (cls.equals(char.class)) {
            return Character.class;
        } else if (cls.equals(long.class)) {
            return Long.class;
        } else if (cls.equals(double.class)) {
            return Double.class;
        } else if (cls.equals(short.class)) {
            return Short.class;
        } else {
            return cls;
        }
    }


}
