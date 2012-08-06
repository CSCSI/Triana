package org.trianacode.shiwaall.collection;

import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class Dot<C> extends CollectionMapper {

    public Dot(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB) {
        super(listA, listB, 1, 1, 0);
    }
}
