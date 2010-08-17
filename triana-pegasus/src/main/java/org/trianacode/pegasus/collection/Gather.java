package org.trianacode.pegasus.collection;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class Gather<C> extends CollectionMapper {

    public Gather(List<? extends CollectionElement<C>> listA, CollectionElement<C> listB) {
        super(listA, Arrays.asList(listB), 1, 1, 1);
    }
}