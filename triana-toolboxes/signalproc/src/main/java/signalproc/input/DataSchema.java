package signalproc.input;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * A class representing the rows and columns that are to be imported
 *
 * @author Ian Wang
 * @version $Revision: 2921 $
 */

public class DataSchema {

    private int[] schema;
    private int cutoff = -1;
    private boolean inorder;


    /**
     * Converts a string into a schema of rows/column indexes to be read in (e.g. "3,4,6-12" becomes
     * [3,4,6,7,8,9,10,11,12])
     */
    public static DataSchema getDataSchema(String input) {
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(input));
            ArrayList cols = new ArrayList();
            int cutoff = -1;

            int token = tokenizer.nextToken();
            int curnum;
            int lastnum = 1;
            boolean range = false;
            boolean inorder = true;

            while ((token != tokenizer.TT_EOF) && (token != tokenizer.TT_EOL)) {
                if (token == tokenizer.TT_NUMBER) {
                    curnum = (int) tokenizer.nval;

                    if (curnum < 0) {
                        curnum = Math.abs(curnum);
                        range = true;
                    }

                    if (range) {
                        if (curnum > lastnum) {
                            for (int count = lastnum + 1; count < curnum; count++) {
                                cols.add(new Integer((int) count));
                            }
                        } else {
                            for (int count = lastnum - 1; count > curnum; count--) {
                                cols.add(new Integer((int) count));
                            }
                        }
                    }

                    if (curnum < lastnum) {
                        inorder = false;
                    }

                    cols.add(new Integer(curnum));
                    lastnum = (int) curnum;
                } else if (((char) token) == '+') {
                    cutoff = lastnum + 1;
                }

                range = (((char) token) == '-');
                token = tokenizer.nextToken();
            }

            int[] copy = new int[cols.size()];

            if (cols.size() > 0) {
                for (int count = 0; count < copy.length; count++) {
                    copy[count] = ((Integer) cols.get(count)).intValue();
                }

                return new DataSchema(copy, cutoff, inorder);
            } else {
                return new DataSchema(new int[]{1}, 2, true);
            }
        } catch (IOException except) {
            return new DataSchema(new int[]{1}, 2, true);
        }
    }


    public DataSchema(int[] schmea, int cutoff, boolean inorder) {
        this.schema = schmea;
        this.cutoff = cutoff;
        this.inorder = inorder;
    }


    /**
     * @return the columns/rows that are to be read from
     */
    public int[] getSchema() {
        return schema;
    }

    /**
     * @return the cutoff from where all following rows/columns are to be read (-1 if there is no cut-off)
     */
    public int getCutOff() {
        return cutoff;
    }

    /**
     * @return true if the elememts in the schema are in order (i.e. 5 does not come after 10).
     */
    public boolean isInOrder() {
        return inorder;
    }

}
