package org.trianacode.pegasus.collection;

import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class Cross<C> extends CollectionMapper {

    public Cross(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB) {
        super(listA, listB, 1, listB.size(), listB.size());
    }
}