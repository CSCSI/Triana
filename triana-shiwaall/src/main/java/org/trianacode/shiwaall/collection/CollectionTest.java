package org.trianacode.shiwaall.collection;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class CollectionTest.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class CollectionTest {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        List<AddCollectionElement> listA = new ArrayList<AddCollectionElement>();
        for (int i = 0; i < 2; i++) {
            AddCollectionElement ce = new AddCollectionElement(i);
            listA.add(ce);
        }

        List<AddCollectionElement> listB = new ArrayList<AddCollectionElement>();
        for (int i = 0; i < 1; i++) {
            AddCollectionElement ce = new AddCollectionElement();
            listB.add(ce);
        }

        //=================DOT======================//
        CollectionMapper<Number> cm = new Dot<Number>(listA, listB);
        cm.map();
        System.out.println("Dot product:");
        for (CollectionElement<Number> element : listB) {
            System.out.println(element);
        }

        //=================BLOCK======================//
        listB = new ArrayList<AddCollectionElement>();
        for (int i = 0; i < 10; i++) {
            AddCollectionElement ce = new AddCollectionElement();
            listB.add(ce);
        }
        cm = new Block<Number>(listA, listB);
        cm.map();
        System.out.println("Block product:");
        for (CollectionElement<Number> element : listB) {
            System.out.println(element);
        }

        //=================CROSS======================//
        listB = new ArrayList<AddCollectionElement>();
        for (int i = 0; i < 1; i++) {
            AddCollectionElement ce = new AddCollectionElement();
            listB.add(ce);
        }
        cm = new Cross<Number>(listA, listB);
        cm.map();
        System.out.println("Cross product:");
        for (CollectionElement<Number> element : listB) {
            System.out.println(element);
        }
    }
}
