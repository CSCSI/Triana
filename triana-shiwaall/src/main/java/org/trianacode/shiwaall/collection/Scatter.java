package org.trianacode.shiwaall.collection;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class Scatter<C> extends CollectionMapper {

    public Scatter(CollectionElement<C> listA, List<? extends CollectionElement<C>> listB) {
        super(Arrays.asList(listA), listB, 1, 1, 0);
    }
}