package org.trianacode.shiwaall.string;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class PatternCollection.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class PatternCollection implements StringPattern, Serializable {

    /** The serial version uid. */
    private static long serialVersionUID = -1;


    /** The link. */
    private String link = "";
    
    /** The patterns. */
    private List<StringPattern> patterns = new ArrayList<StringPattern>();

    /**
     * Instantiates a new pattern collection.
     *
     * @param link the link
     */
    public PatternCollection(String link) {
        this.link = link;
    }

    /**
     * Adds the.
     *
     * @param pattern the pattern
     */
    public void add(StringPattern pattern) {
        patterns.add(pattern);
    }

    /**
     * Gets the string pattern list.
     *
     * @return the string pattern list
     */
    public List getStringPatternList() {
        return patterns;
    }

    /**
     * Gets the pattern collection size.
     *
     * @return the pattern collection size
     */
    public int getPatternCollectionSize() {
        return patterns.size();
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.string.StringPattern#next()
     */
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

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.string.StringPattern#resetCount()
     */
    public void resetCount() {
        for (StringPattern sp : patterns) {
            sp.resetCount();
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String concat = "";
        for (StringPattern string : patterns) {
            concat.concat(string.toString());
        }
        return patterns.toString();
    }

    /**
     * Varies.
     *
     * @return true, if successful
     */
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
