package org.trianacode.shiwaall.collection;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class CollectionMapper.
 *
 * @param <C> the generic type
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public abstract class CollectionMapper<C> {

    /** The list a. */
    private List<? extends CollectionElement<C>> listA;
    
    /** The list b. */
    private List<? extends CollectionElement<C>> listB;

    /** The increment. */
    private int increment = 1;
    
    /** The iteration. */
    private int iteration = 1;
    
    /** The overlap. */
    private int overlap = 0;

    /**
     * Instantiates a new collection mapper.
     *
     * @param listA the list a
     * @param listB the list b
     * @param increment the increment
     * @param iteration the iteration
     * @param overlap the overlap
     */
    public CollectionMapper(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB,
                            int increment,
                            int iteration, int overlap) {
        this.listA = listA;
        this.listB = listB;
        this.increment = increment;
        this.iteration = iteration;
        this.overlap = overlap;
    }

    /**
     * Map.
     */
    public void map() {
        map(0);
    }

    /**
     * Map.
     *
     * @param offset the offset
     */
    public void map(int offset) {
        int currB = offset;
        int initialB = currB;
        for (int i = 0; i < listA.size(); i += increment) {
            CollectionElement elementA = listA.get(i);
            int count = 0;
            for (int j = 0; j < iteration; j++, count++) {
                int curr = currB + j;
                if (curr < listB.size()) {
                    CollectionElement elementB = listB.get(curr);
                    elementB.setContent(elementA.getContent());
                }
            }
            currB += (count - overlap);
        }
        if(currB < listB.size() - 1 && currB > initialB) {
            map(currB);
        }
    }

    /**
     * Gets the list a.
     *
     * @return the list a
     */
    public List<? extends CollectionElement<C>> getListA() {
        return listA;
    }

    /**
     * Gets the list b.
     *
     * @return the list b
     */
    public List<? extends CollectionElement<C>> getListB() {
        return listB;
    }

    /**
     * Gets the increment.
     *
     * @return the increment
     */
    public int getIncrement() {
        return increment;
    }

    /**
     * Gets the iteration.
     *
     * @return the iteration
     */
    public int getIteration() {
        return iteration;
    }

    /**
     * Gets the overlap.
     *
     * @return the overlap
     */
    public int getOverlap() {
        return overlap;
    }
}
