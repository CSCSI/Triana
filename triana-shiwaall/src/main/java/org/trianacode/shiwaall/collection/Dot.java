package org.trianacode.shiwaall.collection;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Dot.
 *
 * @param <C> the generic type
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class Dot<C> extends CollectionMapper {

    /**
     * Instantiates a new dot.
     *
     * @param listA the list a
     * @param listB the list b
     */
    public Dot(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB) {
        super(listA, listB, 1, 1, 0);
    }
}
