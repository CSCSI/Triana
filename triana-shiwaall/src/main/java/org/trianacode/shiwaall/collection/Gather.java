package org.trianacode.shiwaall.collection;

import java.util.Arrays;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Gather.
 *
 * @param <C> the generic type
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class Gather<C> extends CollectionMapper {

    /**
     * Instantiates a new gather.
     *
     * @param listA the list a
     * @param listB the list b
     */
    public Gather(List<? extends CollectionElement<C>> listA, CollectionElement<C> listB) {
        super(listA, Arrays.asList(listB), 1, 1, 1);
    }
}