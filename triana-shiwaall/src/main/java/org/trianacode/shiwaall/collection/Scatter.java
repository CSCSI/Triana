package org.trianacode.shiwaall.collection;

import java.util.Arrays;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Scatter.
 *
 * @param <C> the generic type
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class Scatter<C> extends CollectionMapper {

    /**
     * Instantiates a new scatter.
     *
     * @param listA the list a
     * @param listB the list b
     */
    public Scatter(CollectionElement<C> listA, List<? extends CollectionElement<C>> listB) {
        super(Arrays.asList(listA), listB, 1, 1, 0);
    }
}