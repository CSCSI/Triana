#include <jni.h>
#include "triana_util_LinkToNative.h"
#include "unit.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

signal* currentSignal;
signalList* nodes;

/** Linked List Stuff   */

void reset_iter(signalList* ol) { 
   ol->cursor = ol->head; 
}

void iter(signalList* ol) { 
   if (ol->cursor) ol->cursor = ol->cursor->next; 
}

void gotoend(signalList *ol) { 
   ol->cursor = ol->tail; 
}
   
signal* add(signalList *ol, signal *n) {
/* add signal to end of list */
    signal* q;

    ++ol->outcount;

    if (ol->head == 0) {
        ol->head = n;
	ol->tail = n;
        ol->head->next = 0;
        ol->head->last = 0;
        reset_iter(ol);
        return ol->head;
    }
	
    n->next = 0;
    n->last = 0;   	
	 
    q = ol->tail;

    q->next = n;
    n->last = q;
    ol->tail = n;

    iter(ol);

    return n;
}

signalList* createSignalList(void) {
   signalList *ol;

   ol = (signalList *)malloc(sizeof(signalList));

   ol->head=0; 
   ol->tail=0;
   ol->outcount=0;

   return ol;
}

void signalFree(signal *n) {
    printf("data\n");
    if (n->data != 0)
        free(n->data);
    else {
        free(n->real);
        free(n->imag);
        }

    if (n->input_id != 0)
        free(n->input_id);
    if (n->output_id != 0)
        free(n->output_id);
    free(n);
    }

void destroy(signalList *ol) {
    signal* n;
    signal* q;
	
    if (ol == 0)
        return;

    n = ol->head;
    
    while (n != 0) {
        q = n->next;
        signalFree( n );
        n = q;
    }
    ol->head = 0;
    ol->tail = 0;
}


/*
 * Outputs the given signal.
 */
void output(signal* s) {
   signalFree(currentSignal);
   currentSignal = s;
   }


/**  Implementation of user calls */


/*
 * Returns the signal taken from the first (and only) input node.
 */
signal* getSignal(void) {
   return currentSignal;
   }


signal* getInputNode(int node) {
    signal* n;
    signal* q;
    int i;

    if (nodes == 0)
        return 0;

    n = nodes->head;
    
    i=0;

    while (i<node) {
        q = n->next;
        n = q;
        ++i;
    }
    return n;
}


 
JNIEXPORT void JNICALL Java_triana_util_LinkToNative_setInputNodes
  (JNIEnv *env, jobject obj, jint nn) {
    }


JNIEXPORT void JNICALL Java_triana_util_LinkToNative_putData
  (JNIEnv *env, jobject obj, jdoubleArray data, jdouble srate) { 
    currentSignal = (signal *)malloc(sizeof(signal));

    currentSignal->frequency = srate;
    currentSignal->samples = (*env)->GetArrayLength(env, data);
    currentSignal->data = (*env)->GetDoubleArrayElements(env, data, 0);
    currentSignal->real=0;
    currentSignal->imag=0;
    currentSignal->input_id=0;
    currentSignal->output_id=0;

    if (nodes==0) 
        nodes = createSignalList();

    add(nodes, currentSignal);
    }


JNIEXPORT void JNICALL Java_triana_util_LinkToNative_putComplexData
  (JNIEnv *env, jobject obj, jdoubleArray real, jdoubleArray imag, jdouble sr) {
    currentSignal = (signal *)malloc(sizeof(signal));

    currentSignal->frequency = sr;
    currentSignal->samples = (*env)->GetArrayLength(env, real);

    currentSignal->real = (*env)->GetDoubleArrayElements(env, real, 0);
    currentSignal->imag = (*env)->GetDoubleArrayElements(env, imag, 0);
    currentSignal->data=0;
    currentSignal->input_id=0;
    currentSignal->output_id=0;

    if (nodes==0) 
        nodes = createSignalList();

    add(nodes, currentSignal);
    }

JNIEXPORT void JNICALL Java_triana_util_LinkToNative_process
  (JNIEnv *env, jobject obj, jstring str){
    int argc, i;
    char *argv[50];    /* 50 at most, should be enough for each parameter !! */
    char *tmp;
    char *lasts;
    char *strin = (char*)(*env)->GetStringUTFChars(env, str, 0);

    for (i=0; i<50; ++i)
        argv[i] = (char *)malloc(50*sizeof(char));

    tmp = (char *)malloc(50*sizeof(char));

    i=0;

    while ((tmp = (char *)strtok_r(strin, " ",(char **)&lasts)) != 0) {
        strcpy(argv[i], tmp); 
        ++i; 
        strin = 0;
        }
    
/*    printf("Before\n"); */
    process(i, argv);
    argc = i;
 /*   printf("After\n"); */

    (*env)->ReleaseStringUTFChars(env, str, strin);
    free(strin);

/*    printf("argv[]\n"); */
    for (i=0; i<argc; ++i)
        free(argv[i]);
/*    printf("argv\n"); */
    free(argv);
    free(tmp); 

/*    printf("lasts\n");
    free(lasts);
    printf("done processing\n");*/
    }



JNIEXPORT void JNICALL Java_triana_util_LinkToNative_cleanUp
  (JNIEnv *env, jobject obj) {
    
/*    printf("nodes\n");
    if (nodes != 0)
        destroy(nodes);
    printf("done\n");
*/
    free(currentSignal);
    nodes=0;
    currentSignal=0; 
/*    printf("all deallocated\n"); */
    }

JNIEXPORT jstring JNICALL Java_triana_util_LinkToNative_getInputType
  (JNIEnv *env, jobject obj) {
    currentSignal->input_id = inputType();
    return (*env)->NewStringUTF(env, currentSignal->input_id);
    }


JNIEXPORT jstring JNICALL Java_triana_util_LinkToNative_getOutputType
  (JNIEnv *env, jobject obj) {
    currentSignal->output_id = outputType();
    return (*env)->NewStringUTF(env, currentSignal->output_id);
    }

JNIEXPORT jstring JNICALL Java_triana_util_LinkToNative_getUserHelp
  (JNIEnv *env, jobject obj){
    return (*env)->NewStringUTF(env, getHelp());
    }

JNIEXPORT jstring JNICALL Java_triana_util_LinkToNative_getInterfaceInfo
  (JNIEnv *env, jobject obj) {
    return (*env)->NewStringUTF(env, getInterfaceInfo());
    }

JNIEXPORT jstring JNICALL Java_triana_util_LinkToNative_getMode
  (JNIEnv *env, jobject obj) {
    return (*env)->NewStringUTF(env, getMode());
    }


JNIEXPORT jdoubleArray JNICALL Java_triana_util_LinkToNative_getData
  (JNIEnv *env, jobject obj) {
    jdoubleArray data;    
    data = (*env)->NewDoubleArray(env, currentSignal->samples);

    (*env)->SetDoubleArrayRegion(env, data, (jsize)0, currentSignal->samples, currentSignal->data);   
    return data;  
    }


JNIEXPORT jdoubleArray JNICALL Java_triana_util_LinkToNative_getRealData
  (JNIEnv *env, jobject obj) {
    jdoubleArray data;    

    data = (*env)->NewDoubleArray(env, currentSignal->samples);

    (*env)->SetDoubleArrayRegion(env, data, (jsize)0, currentSignal->samples, currentSignal->real);   

    return data;  
    }


JNIEXPORT jdoubleArray JNICALL Java_triana_util_LinkToNative_getImagData
  (JNIEnv *env, jobject obj) {
    jdoubleArray data;    

    data = (*env)->NewDoubleArray(env, currentSignal->samples);

    (*env)->SetDoubleArrayRegion(env, data, (jsize)0, currentSignal->samples, currentSignal->imag);   

    return data;  
    }

JNIEXPORT jdouble JNICALL Java_triana_util_LinkToNative_getSamplingRate
  (JNIEnv *env, jobject obj) {
    return currentSignal->frequency;
    }




