package org.trianacode.pegasus.collection;

import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class Block<C> extends CollectionMapper {

    public Block(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB) {
        super(listA, listB, 1, listA.size() / listB.size() <= 0 ? listA.size() : listA.size() / listB.size(), 0);
    }

    public Block(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB, int iteration) {
        super(listA, listB, 1, iteration, 0);
    }

    public Block(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB, int iteration, int overlap) {
        super(listA, listB, 1, iteration, overlap);
    }
}