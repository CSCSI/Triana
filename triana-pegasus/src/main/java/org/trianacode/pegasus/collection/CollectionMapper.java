package org.trianacode.pegasus.collection;

import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public abstract class CollectionMapper<C> {

    private List<? extends CollectionElement<C>> listA;
    private List<? extends CollectionElement<C>> listB;

    private int increment = 1;
    private int iteration = 1;
    private int overlap = 0;

    public CollectionMapper(List<? extends CollectionElement<C>> listA, List<? extends CollectionElement<C>> listB,
                            int increment,
                            int iteration, int overlap) {
        this.listA = listA;
        this.listB = listB;
        this.increment = increment;
        this.iteration = iteration;
        this.overlap = overlap;
    }

    public void map() {
        map(0);
    }

    public void map(int offset) {
        int currB = offset;
        int initialB = currB;
        for (int i = 0; i < listA.size(); i += increment) {
            CollectionElement elementA = listA.get(i);
            int count = 0;
            for (int j = 0; j < iteration; j++, count++) {
                int curr = currB + j;
                if (curr < listB.size()) {
                    CollectionElement elementB = listB.get(curr);
                    elementB.setContent(elementA.getContent());
                }
            }
            currB += (count - overlap);
        }
        if(currB < listB.size() - 1 && currB > initialB) {
            map(currB);
        }
    }

    public List<? extends CollectionElement<C>> getListA() {
        return listA;
    }

    public List<? extends CollectionElement<C>> getListB() {
        return listB;
    }

    public int getIncrement() {
        return increment;
    }

    public int getIteration() {
        return iteration;
    }

    public int getOverlap() {
        return overlap;
    }
}
