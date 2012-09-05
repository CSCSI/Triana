package org.trianacode.shiwaall.collection;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Skip.
 *
 * @param <C> the generic type
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class Skip<C> extends CollectionMapper {

    /**
     * Instantiates a new skip.
     *
     * @param listA the list a
     * @param listB the list b
     * @param increment the increment
     */
    public Skip(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB, int increment) {
        super(listA, listB, increment, listA.size() / listB.size(), 0);
    }

    /**
     * Instantiates a new skip.
     *
     * @param listA the list a
     * @param listB the list b
     * @param increment the increment
     * @param iteration the iteration
     */
    public Skip(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB, int increment, int iteration) {
        super(listA, listB, increment, iteration, 0);
    }

    /**
     * Instantiates a new skip.
     *
     * @param listA the list a
     * @param listB the list b
     * @param increment the increment
     * @param iteration the iteration
     * @param overlap the overlap
     */
    public Skip(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB, int increment, int iteration,
                int overlap) {
        super(listA, listB, increment, iteration, overlap);
    }
}