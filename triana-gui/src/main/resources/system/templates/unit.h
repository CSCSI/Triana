#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*@@
  @author Ian Taylor
  @version 0.4
  @desc
  This structure stores the data which is input from the Grid
  unit by using one of the methods described in this file.
  @enddesc
  @par
  samples the number of samples within the data
  @endpar
  @par
  frequency the sampling frequency
  @endpar
  @par
  data a pointer to the actual data
  @endpar
  @par
  dataReal a pointer to the real components of the data if
  the data is complex
  @endpar
  @par
  dataImag a pointer to the imaginary components of the data if
  the data is complex
  @endpar
  @par
  id a string representing the identification of this type of data.
  e.g. if the data is a SampleSet then this identifier would contain
  the string "SampleSet"
  @endpar
*/
typedef struct sig {     /* structure input data for signal Analysis  */
    int nodenumber; /* number of input or output node this data is */
    jsize samples;
    double frequency;
    jdouble* data;
    jdouble* real;
    jdouble* imag;
    char* input_id;
    char* output_id;
    struct sig *next;  /* Next signal in linked list */
    struct sig *last;  /* Last signal in linked list */
} signal;

typedef struct {
   signal* head;
   signal* tail;
   signal* cursor;
   int outcount;
} signalList; 



/*  Each C unit ******* MUST ******** implement these */

/**
 * Implement what the unit does here
 */
void process(int argc, char *argv[]);

/**
 * This function should retun a string comtaining the help for this
 * unit.
 */
char* getHelp(void);

/**
 * This function should retun a string comtaining the interface for this
 * unit.
 */
char* getInterfaceInfo(void);

/**
 * This function should retun a string comtaining the mode for this
 * unit.
 */
char* getMode(void);

/**
 * This should return the input type which this unit can accept
 */
char* inputType(void);

/**
 * This should return the output type which this unit outputs
 */
char* outputType(void);

/*  End of function which have to be implemented */


/** User functions */

/**
 * gets the input signal
 */
signal* getSignal(void);

/**
 * outputs the given signal
 */
void output(signal* s);

/**
 * gets the input signal from the ith input node
 */
signal* getInputNode(int i);



