package triana.types.util;

import java.util.Enumeration;
import java.util.Vector;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 4, 2010
 */

public class TrianaSort {


    /**
     * Sort the given numbers into ascending order if the <i>direction</i> argument is non-negative, descending order
     * otherwise.
     *
     * @param input     The input numbers to be sorted
     * @param direction If non-negative, sort will be ascending
     * @return The sorted array of numbers
     */
    public static double[] mergeSort(double[] input, double direction) {

        if (input.length < 2) {
            return input;
        } else if (input.length == 2) {
            if (direction >= 0) {
                if (input[0] > input[1]) {
                    double a = input[0];
                    input[0] = input[1];
                    input[1] = a;
                }
            } else if (input[0] < input[1]) {
                double a = input[0];
                input[0] = input[1];
                input[1] = a;
            }

            return input;
        } else {

            int frontLength = (int) Math.floor(input.length / 2);
            int rearLength = input.length - frontLength;
            double[] front = new double[frontLength];
            double[] rear = new double[rearLength];
            System.arraycopy(input, 0, front, 0, frontLength);
            System.arraycopy(input, frontLength, rear, 0, rearLength);

            front = mergeSort(front, direction);
            rear = mergeSort(rear, direction);

            int j = 0;
            int k = 0;
            int l = 0;

            if (direction >= 0) {
                while ((j < frontLength) && (k < rearLength)) {
                    if (front[j] <= rear[k]) {
                        input[l++] = front[j++];
                    } else {
                        input[l++] = rear[k++];
                    }
                }
            } else {
                while ((j < frontLength) && (k < rearLength)) {
                    if (front[j] >= rear[k]) {
                        input[l++] = front[j++];
                    } else {
                        input[l++] = rear[k++];
                    }
                }
            }

            if (j == frontLength) {
                System.arraycopy(rear, k, input, l, rearLength - k);
            } else {
                System.arraycopy(front, j, input, l, frontLength - j);
            }

            return input;
        }

    }

    /**
     * Sort the given numbers into ascending order.
     *
     * @param input The input numbers to be sorted
     * @return The sorted array of numbers
     */
    public static double[] mergeSort(double[] input) {
        return mergeSort(input, 1.0);
    }

    /**
     * Sort the given Strings lexically in ascending order if the <i>direction</i> argument is non-negative, descending
     * order otherwise. The sorting is done by using Java's String compare method.
     *
     * @param input     The array of input Strings to be sorted
     * @param direction If non-negative, sort will be ascending
     * @return The sorted array of Strings
     */
    public static String[] mergeSort(String[] input, double direction) {

        if (input.length < 2) {
            return input;
        } else if (input.length == 2) {
            if (direction >= 0) {
                if (input[0].compareTo(input[1]) > 0) {
                    String a = input[0];
                    input[0] = input[1];
                    input[1] = a;
                }
            } else if (input[0].compareTo(input[1]) < 0) {
                String a = input[0];
                input[0] = input[1];
                input[1] = a;
            }
            return input;
        } else {
            int frontLength = (int) Math.floor(input.length / 2);
            int rearLength = input.length - frontLength;
            String[] front = new String[frontLength];
            String[] rear = new String[rearLength];

            System.arraycopy(input, 0, front, 0, frontLength);
            System.arraycopy(input, frontLength, rear, 0, rearLength);

            front = mergeSort(front, direction);
            rear = mergeSort(rear, direction);

            int j = 0;
            int k = 0;
            int l = 0;

            if (direction >= 0) {
                while ((j < frontLength) && (k < rearLength)) {
                    if (front[j].compareTo(rear[k]) <= 0) {
                        input[l++] = front[j++];
                    } else {
                        input[l++] = rear[k++];
                    }
                }
            } else {
                while ((j < frontLength) && (k < rearLength)) {
                    if (front[j].compareTo(rear[k]) >= 0) {
                        input[l++] = front[j++];
                    } else {
                        input[l++] = rear[k++];
                    }
                }
            }

            if (j == frontLength) {
                System.arraycopy(rear, k, input, l, rearLength - k);
            } else {
                System.arraycopy(front, j, input, l, frontLength - j);
            }

            return input;
        }

    }

    /**
     * Sort the given Strings lexically in ascending order. The sorting is done by using Java's String compare method.
     *
     * @param input The array of input Strings to be sorted
     * @return The sorted array of Strings
     */
    public static String[] mergeSort(String[] input) {
        return mergeSort(input, 1.0);
    }


    /**
     * Sort the Strings held in the input StringVector lexically in ascending order if the <i>direction</i> argument is
     * non-negative, descending order otherwise. The sorting is done by using Java's String compare method.
     *
     * @param input     The list of input Strings to be sorted
     * @param direction If non-negative, sort will be ascending
     * @return The sorted list of Strings
     */
    public static Vector<String> mergeSort(Vector<String> input, double direction) {
        int length = input.size();
        String[] buffer = new String[length];
        input.copyInto(buffer);
        buffer = mergeSort(buffer, direction);
        Vector<String> output = new Vector<String>(length);
        for (int i = 0; i < length; i++) {
            output.addElement(buffer[i]);
        }
        return output;
    }


    /**
     * Sort the Strings held in the input StringVector lexically in ascending order. The sorting is done by using Java's
     * String compare method.
     *
     * @param input The list of input Strings to be sorted
     * @return The sorted list of Strings
     */
    public static Vector<String> mergeSort(Vector<String> input) {
        return mergeSort(input, 1.0);
    }


    /**
     * Sort the Strings held in the list referred to by the input Enumeration lexically in ascending order if the
     * <i>direction</i> argument is non-negative, descending order otherwise. The sorting is done by using Java's String
     * compare method. A StringVector is returned.
     *
     * @param e         The Enumeration of the list of input Strings to be sorted
     * @param direction If non-negative, sort will be ascending
     * @return The sorted list of Strings
     */
    public static Vector<String> mergeSort(Enumeration<String> e, double direction) {

        Vector<String> converted = new Vector<String>(20);
        String monitor;

        while (e.hasMoreElements()) {
            monitor = e.nextElement();
            if (monitor instanceof String) {
                converted.addElement(monitor);
            } else {
                return null;
            }
        }
        return mergeSort(converted, direction);
    }

    /**
     * Sort the Strings held in the list referred to by the input Enumeration lexically in ascending order. A
     * StringVector is returned.
     *
     * @param e The Enumeration of the list of input Strings to be sorted
     * @return The sorted list of Strings
     */
    public static Vector<String> mergeSort(Enumeration e) {
        return mergeSort(e, 1.0);
    }


    /**
     * Sorts the tools into alphabetical order but makes sure that the Demos toolbox is at the front and that the Common
     * toolbox is appropriately placed.
     *
     * @param s The input array of Strings containing the toolbox names
     * @return The returned array of sorted toolbox names
     */
    public static String[] sortToolboxes(String[] s) {
        int pos = -1;
        int posCom = -1;

        String[] sorted = mergeSort(s, 1);

        for (int i = 0; i < sorted.length; ++i) {
            if (sorted[i].indexOf("Demo") != -1) {
                pos = i;
            }
            if (sorted[i].indexOf("Common") != -1) {
                posCom = i;
            }
        }

        if (posCom != -1) {
            int j = 0;

            String s2[] = new String[s.length];

            for (int i = 0; i < s2.length; ++i) {
                if (i != posCom) {
                    s2[j] = sorted[i];
                    ++j;
                }
            }
            s2[s2.length - 1] = sorted[posCom];
            sorted = s2;
            if (posCom < pos) {
                --pos;
            }
        }

        if (pos == -1) {
            return sorted;
        }

        int j = 1;

        String s2[] = new String[sorted.length];

        for (int i = 0; i < s2.length; ++i) {
            if (i != pos) {
                s2[j] = sorted[i];
                ++j;
            }
        }

        s2[0] = sorted[pos];

        //       for (int i=0; i< s2.length; ++i)
        //         System.out.println(s2[i]);

        return s2;
    }
}
