package org.trianacode.shiwaall.handler;

import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 04/05/2011
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class ObjectToSchema {

    /**
     * Gets the schema uri string.
     *
     * @param object the object
     * @return the schema uri string
     */
    public static String getSchemaURIString(Object object) {
        String canonicalName = object.getClass().getCanonicalName();
        return getSchemaURIString(canonicalName);
    }

    /**
     * Gets the schema uri string.
     *
     * @param canonicalName the canonical name
     * @return the schema uri string
     */
    public static String getSchemaURIString(String canonicalName) {
        HashMap<String, String> knownObjects = new HashMap<String, String>();
        populateMap(knownObjects);

        if (knownObjects.keySet().contains(canonicalName)) {
            return knownObjects.get(canonicalName);
        }

        return "http://www.w3.org/2001/XMLSchema#anySimpleType";
    }

    /**
     * Populate map.
     *
     * @param knownObjects the known objects
     */
    private static void populateMap(HashMap<String, String> knownObjects) {
        knownObjects.put("java.lang.String", "http://www.w3.org/2001/XMLSchema#string");
        knownObjects.put("java.math.BigInteger", "http://www.w3.org/2001/XMLSchema#integer");
        knownObjects.put("java.math.BigDecimal", "http://www.w3.org/2001/XMLSchema#decimal");
        knownObjects.put("java.lang.Object", "http://www.w3.org/2001/XMLSchema#anyType");

    }
}
