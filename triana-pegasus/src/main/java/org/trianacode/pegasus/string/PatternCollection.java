package org.trianacode.pegasus.string;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class PatternCollection implements StringPattern, Serializable {

    private static long serialVersionUID = -1;


    private String link = "";
    private List<StringPattern> patterns = new ArrayList<StringPattern>();

    public PatternCollection(String link) {
        this.link = link;
    }

    public void add(StringPattern pattern) {
        patterns.add(pattern);
    }

    public List getStringPatternList() {
        return patterns;
    }

    public int getPatternCollectionSize() {
        return patterns.size();
    }

    public String next() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < patterns.size(); i++) {
            StringPattern pattern = patterns.get(i);
            sb.append(pattern.next());
            if (i < patterns.size() - 1) {
                sb.append(link);
            }
        }
        return sb.toString();
    }

    public void resetCount() {
        for (StringPattern sp : patterns) {
            sp.resetCount();
        }
    }

    public String toString() {
        String concat = "";
        for (StringPattern string : patterns) {
            concat.concat(string.toString());
        }
        return patterns.toString();
    }

    public boolean varies() {
        boolean varies = false;
        for (Iterator i = patterns.iterator(); i.hasNext(); ) {
            Object o = i.next();
            System.out.println(o.getClass().getCanonicalName());
            if (!(o instanceof CharSequencePattern)) {
                varies = true;
            }
        }
        return varies;
    }
}
