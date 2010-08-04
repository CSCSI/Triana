package triana.types;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Aug 4, 2010
 */

public interface ProcessDoublesInterface {

    /**
     * @return true if this unit requires its inputs to be in double precision format
     */
    boolean getRequireDoubleInputs();

    /**
     * @return true if this unit can deal with double precision input values
     */
    boolean getCanProcessDoubleArrays();

    /**
     * Unit Programmers should set this to true if this unit requires its inputs to be in double precision format
     */
    void setRequireDoubleInputs(boolean state);

    /**
     * Unit Programmers should set this to true if this unit can process input data sets in double precision format
     */
    void setCanProcessDoubleArrays(boolean state);
}
