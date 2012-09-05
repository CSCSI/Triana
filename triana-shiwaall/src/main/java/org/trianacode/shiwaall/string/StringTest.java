package org.trianacode.shiwaall.string;

// TODO: Auto-generated Javadoc
/**
 * The Class StringTest.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 15, 2010
 */

public class StringTest {


    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        System.out.println("ABC");

        AlphabetPattern ap = new AlphabetPattern(true, 3);
        for (int i = 0; i < 100; i++) {
            System.out.println(ap.next());
        }

        System.out.println("Counter");

        CounterPattern cp = new CounterPattern(10, 5, 2, 2);
        for (int i = 0; i < 100; i++) {
            System.out.println(cp.next());
        }

        System.out.println("Date");

        DatePattern dp = new DatePattern("yyyy-MMM-dd");
        for (int i = 0; i < 100; i++) {
            System.out.println(dp.next());
        }

        CharSequencePattern csp = new CharSequencePattern("NNN");

        PatternCollection col = new PatternCollection("-");
        col.add(ap);
        col.add(cp);
        col.add(dp);
        col.add(csp);
        for (int i = 0; i < 100; i++) {
            System.out.println(col.next());
        }

    }
}
