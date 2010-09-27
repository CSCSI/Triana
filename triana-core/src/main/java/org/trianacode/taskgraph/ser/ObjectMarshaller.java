/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.taskgraph.ser;


import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class ObjectMarshaller implements XMLConstants {

    static Log log = Loggers.TOOL_LOGGER;


    private static Base64ObjectDeserializer deser = new Base64ObjectDeserializer();


    public static Element marshallJavaToElement(Element result, Object javaObject) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        String res = serSimple(javaObject);
        if (res != null) {
            result.setTextContent(res);
        } else {
            result.setAttribute(BASE64_ENCODED, "true");
            try {
                ObjectOutputStream marshall = new ObjectOutputStream(bout);
                marshall.writeObject(javaObject);
                String enc = Base64.encode(bout.toByteArray());
                result.setTextContent(enc);
                marshall.close();
            }
            catch (IOException e) {
                log.warn("Error marshalling object " + javaObject, e);
            }
        }
        return result;
    }


    public static Object marshallElementToJava(Element toMarshall) {
        NodeList ch = toMarshall.getChildNodes();
        if (ch != null) {
            int len = ch.getLength();
            for (int i = 0; i < len; i++) {
                Node n = ch.item(i);
                if (n instanceof Element) {
                    Element e = (Element) n;
                    if (e.getLocalName().equals(VALUE_TAG)) {
                        String encoded = e.getAttribute(BASE64_ENCODED);
                        if (encoded == null || !encoded.equals("true")) {
                            return e.getTextContent();
                        }
                        return marshallStringToJava(e.getTextContent());
                    }
                }
            }
        }
        return null;
    }


    public static String marshallJavaToString(Object javaObject) throws IOException {
        if ((javaObject instanceof String)) {
            return (String) javaObject;
        }

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream marshall = new ObjectOutputStream(bout);
        marshall.writeObject(javaObject);
        return Base64.encode(bout.toByteArray());
    }

    public static Object marshallStringToJava(String str) {
        try {
            Object ret = deser.deserializeObject(str);
            return ret;
        } catch (IOException e) {
            return str;
        }
    }

    private static String serSimple(Object o) {
        if (o instanceof String) {
            return (String) o;
        } else if (o instanceof CharSequence ||
                o instanceof Boolean ||
                o instanceof Short ||
                o instanceof Long ||
                o instanceof Double ||
                o instanceof Integer ||
                o instanceof Character ||
                o instanceof CharSequence ||
                o instanceof Enum ||
                o instanceof Float) {
            return o.toString();
        }
        return null;
    }
}
