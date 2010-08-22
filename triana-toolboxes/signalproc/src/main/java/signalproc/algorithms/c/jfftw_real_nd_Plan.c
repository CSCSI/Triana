#include "jfftw_real_nd_Plan.h"
#include <rfftw.h>

/*
 * Class:     jfftw_real_nd_Plan
 * Method:    createPlan
 * Signature: ([III)V
 */
JNIEXPORT void JNICALL Java_jfftw_real_nd_Plan_createPlan( JNIEnv *env, jobject obj, jintArray dim, jint dir, jint flags )
{
	jclass clazz;
	jfieldID id;
	jbyteArray arr;
	unsigned char* carr;
	int rank;
	int *cdim;

	if( sizeof( jdouble ) != sizeof( fftw_real ) )
	{
		(*env)->ThrowNew( env, (*env)->FindClass( env, "java/lang/RuntimeException" ), "jdouble and fftw_real are incompatible" );
		return;
	}

	clazz = (*env)->GetObjectClass( env, obj );
	id    = (*env)->GetFieldID( env, clazz, "plan", "[B" );
	arr   = (*env)->NewByteArray( env, sizeof( rfftwnd_plan ) );
	carr  = (*env)->GetByteArrayElements( env, arr, 0 );
	rank  = (*env)->GetArrayLength( env, dim );
	cdim  = (*env)->GetIntArrayElements( env, dim, 0 );

	(*env)->MonitorEnter( env, (*env)->FindClass( env, "jfftw/Plan" ) );

	*(rfftwnd_plan*)carr = rfftwnd_create_plan( rank, cdim, dir, flags );

	(*env)->MonitorExit( env, (*env)->FindClass( env, "jfftw/Plan" ) );

	(*env)->ReleaseIntArrayElements( env, dim, cdim, 0 );
	(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
	(*env)->SetObjectField( env, obj, id, arr );
}
/*
 * Class:     jfftw_real_nd_Plan
 * Method:    destroyPlan
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_jfftw_real_nd_Plan_destroyPlan( JNIEnv* env, jobject obj )
{
	jclass clazz = (*env)->GetObjectClass( env, obj );
	jfieldID id = (*env)->GetFieldID( env, clazz, "plan", "[B" );
	jbyteArray arr = (jbyteArray)(*env)->GetObjectField( env, obj, id );
	unsigned char* carr = (*env)->GetByteArrayElements( env, arr, 0 );

	rfftwnd_destroy_plan( *(rfftwnd_plan*)carr );

	(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
	(*env)->SetObjectField( env, obj, id, NULL );
}
/*
 * Class:     jfftw_real_nd_Plan
 * Method:    transform
 * Signature: ([D)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_jfftw_real_nd_Plan_transform___3D( JNIEnv* env, jobject obj, jdoubleArray in )
{
	jdouble *cin, *cout;
	jdoubleArray out;
	int i;

	jclass clazz = (*env)->GetObjectClass( env, obj );
	jfieldID id = (*env)->GetFieldID( env, clazz, "plan", "[B" );
	jbyteArray arr = (jbyteArray)(*env)->GetObjectField( env, obj, id );
	unsigned char* carr = (*env)->GetByteArrayElements( env, arr, 0 );
	rfftwnd_plan plan = *(rfftwnd_plan*)carr;
	int length = 1;
	int clength = 2;
	for( i = 0; i < plan->rank; ++i ) length *= plan->plans[i]->n;
	for( i = 0; i < plan->rank; ++i ) clength *= plan->n[i];
	if( (plan->dir==FFTW_REAL_TO_COMPLEX?length:clength) != (*env)->GetArrayLength( env, in ) )
	{
		(*env)->ThrowNew( env, (*env)->FindClass( env, "java/lang/IndexOutOfBoundsException" ), "the Plan was created for a different length" );
		(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
		return NULL;
	}

	cin = (*env)->GetDoubleArrayElements( env, in, 0 );
	if( plan->is_in_place )
	{
		out = in;
		cout = NULL;
	}
	else
	{
		out = (*env)->NewDoubleArray( env, (plan->dir==FFTW_REAL_TO_COMPLEX?clength:length) );
		cout = (*env)->GetDoubleArrayElements( env, out, 0 );
	}

	if( plan->rank > 0 && ! plan->plans[0]->flags & FFTW_THREADSAFE )
	{
		// synchronization
		(*env)->MonitorEnter( env, obj );
	}

	if( plan->dir == FFTW_REAL_TO_COMPLEX )
	{
		rfftwnd_one_real_to_complex( plan, cin, (fftw_complex*)cout );
	}
	else
	{
		rfftwnd_one_complex_to_real( plan, (fftw_complex*)cin, cout );
	}

	if( plan->rank > 0 && ! plan->plans[0]->flags & FFTW_THREADSAFE )
	{
		// synchronization
		(*env)->MonitorExit( env, obj );
	}

	(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
	(*env)->ReleaseDoubleArrayElements( env, in, cin, 0 );
	if( plan->is_in_place )
	{
		(*env)->ReleaseDoubleArrayElements( env, out, cout, 0 );
	}
	return out;
}
/*
 * Class:     jfftw_real_nd_Plan
 * Method:    transform
 * Signature: (I[DII[DII)V
 */
JNIEXPORT void JNICALL Java_jfftw_real_nd_Plan_transform__I_3DII_3DII( JNIEnv *env, jobject obj, jint howmany, jdoubleArray in, jint istride, jint idist, jdoubleArray out, jint ostride, jint odist )
{
	jdouble *cin, *cout;
	int i;

	jclass clazz = (*env)->GetObjectClass( env, obj );
	jfieldID id = (*env)->GetFieldID( env, clazz, "plan", "[B" );
	jbyteArray arr = (jbyteArray)(*env)->GetObjectField( env, obj, id );
	unsigned char* carr = (*env)->GetByteArrayElements( env, arr, 0 );
	rfftwnd_plan plan = *(rfftwnd_plan*)carr;
	int length = 1;
	int clength = 2;
	for( i = 0; i < plan->rank; ++i ) length *= plan->plans[i]->n;
	for( i = 0; i < plan->rank; ++i ) clength *= plan->n[i];
	if( (howmany - 1) * idist + (plan->dir==FFTW_REAL_TO_COMPLEX?length:clength) != (*env)->GetArrayLength( env, in ) )
	if( (plan->dir==FFTW_REAL_TO_COMPLEX?length:clength) != (*env)->GetArrayLength( env, in ) )
	{
		(*env)->ThrowNew( env, (*env)->FindClass( env, "java/lang/IndexOutOfBoundsException" ), "the Plan was created for a different length (in)" );
		(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
		return;
	}
	if( (howmany - 1) * odist + (plan->dir==FFTW_REAL_TO_COMPLEX?clength:length) != (*env)->GetArrayLength( env, out ) )
	{
		(*env)->ThrowNew( env, (*env)->FindClass( env, "java/lang/IndexOutOfBoundsException" ), "the Plan was created for a different length (out)" );
		(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
		return;
	}

	cin = (*env)->GetDoubleArrayElements( env, in, 0 );
	cout = (*env)->GetDoubleArrayElements( env, out, 0 );

	if( plan->rank > 0 && ! plan->plans[0]->flags & FFTW_THREADSAFE )
	{
		// synchronization
		(*env)->MonitorEnter( env, obj );
	}
	if( plan->dir == FFTW_REAL_TO_COMPLEX )
	{
		rfftwnd_real_to_complex( plan, howmany, cin, istride, idist, (fftw_complex*)cout, ostride, odist );
	}
	else
	{
		rfftwnd_complex_to_real( plan, howmany, (fftw_complex*)cin, istride, idist, cout, ostride, odist );
	}
	if( plan->rank > 0 && ! plan->plans[0]->flags & FFTW_THREADSAFE )
	{
		// synchronization
		(*env)->MonitorExit( env, obj );
	}

	(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
	(*env)->ReleaseDoubleArrayElements( env, in, cin, 0 );
	(*env)->ReleaseDoubleArrayElements( env, out, cout, 0 );
}


