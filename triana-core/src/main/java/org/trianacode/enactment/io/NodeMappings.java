package org.trianacode.enactment.io;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 3, 2010
 */
public class NodeMappings {

    private Map<Integer, Object> map = new HashMap<Integer, Object>();

    public void addMapping(int index, Object value) {
        map.put(index, value);
    }

    public Object getValue(int index) {
        return map.get(index);
    }


}
