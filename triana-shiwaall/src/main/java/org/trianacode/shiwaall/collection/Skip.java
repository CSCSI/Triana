package org.trianacode.shiwaall.collection;

import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class Skip<C> extends CollectionMapper {

    public Skip(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB, int increment) {
        super(listA, listB, increment, listA.size() / listB.size(), 0);
    }

    public Skip(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB, int increment, int iteration) {
        super(listA, listB, increment, iteration, 0);
    }

    public Skip(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB, int increment, int iteration,
                int overlap) {
        super(listA, listB, increment, iteration, overlap);
    }
}