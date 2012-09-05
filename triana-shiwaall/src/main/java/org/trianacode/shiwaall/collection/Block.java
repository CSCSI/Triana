package org.trianacode.shiwaall.collection;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Block.
 *
 * @param <C> the generic type
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class Block<C> extends CollectionMapper {

    /**
     * Instantiates a new block.
     *
     * @param listA the list a
     * @param listB the list b
     */
    public Block(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB) {
        super(listA, listB, 1, listA.size() / listB.size() <= 0 ? listA.size() : listA.size() / listB.size(), 0);
    }

    /**
     * Instantiates a new block.
     *
     * @param listA the list a
     * @param listB the list b
     * @param iteration the iteration
     */
    public Block(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB, int iteration) {
        super(listA, listB, 1, iteration, 0);
    }

    /**
     * Instantiates a new block.
     *
     * @param listA the list a
     * @param listB the list b
     * @param iteration the iteration
     * @param overlap the overlap
     */
    public Block(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB, int iteration, int overlap) {
        super(listA, listB, 1, iteration, overlap);
    }
}