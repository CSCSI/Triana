#include "jfftw_real_Plan.h"
#include <rfftw.h>

/*
 * Class:     jfftw_real_Plan
 * Method:    createPlan
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_jfftw_real_Plan_createPlan( JNIEnv *env, jobject obj, jint n, jint dir, jint flags )
{
	jclass clazz;
	jfieldID id;
	jbyteArray arr;
	unsigned char* carr;

	if( sizeof( jdouble ) != sizeof( fftw_real ) )
	{
		(*env)->ThrowNew( env, (*env)->FindClass( env, "java/lang/RuntimeException" ), "jdouble and fftw_real are incompatible" );
		return;
	}

	clazz = (*env)->GetObjectClass( env, obj );
	id    = (*env)->GetFieldID( env, clazz, "plan", "[B" );
	arr   = (*env)->NewByteArray( env, sizeof( rfftw_plan ) );
	carr  = (*env)->GetByteArrayElements( env, arr, 0 );

	(*env)->MonitorEnter( env, (*env)->FindClass( env, "jfftw/Plan" ) );

	*(rfftw_plan*)carr = rfftw_create_plan( n, dir, flags );

	(*env)->MonitorExit( env, (*env)->FindClass( env, "jfftw/Plan" ) );

	(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
	(*env)->SetObjectField( env, obj, id, arr );
}
/*
 * Class:     jfftw_real_Plan
 * Method:    createPlanSpecific
 * Signature: (III[DI[DI)V
 */
JNIEXPORT void JNICALL Java_jfftw_real_Plan_createPlanSpecific( JNIEnv *env, jobject obj, jint n, jint dir, jint flags, jdoubleArray in, jint idist, jdoubleArray out, jint odist)
{
	jclass clazz;
	jfieldID id;
	jbyteArray arr;
	unsigned char* carr;
	double *cin, *cout;

	if( sizeof( jdouble ) != sizeof( fftw_real ) )
	{
		(*env)->ThrowNew( env, (*env)->FindClass( env, "java/lang/RuntimeException" ), "jdouble and fftw_real are incompatible" );
		return;
	}

	clazz = (*env)->GetObjectClass( env, obj );
	id    = (*env)->GetFieldID( env, clazz, "plan", "[B" );
	arr   = (*env)->NewByteArray( env, sizeof( rfftw_plan ) );
	carr  = (*env)->GetByteArrayElements( env, arr, 0 );
	cin   = (*env)->GetDoubleArrayElements( env, in, 0 );
	cout  = (*env)->GetDoubleArrayElements( env, out, 0 );

	(*env)->MonitorEnter( env, (*env)->FindClass( env, "jfftw/Plan" ) );

	*(rfftw_plan*)carr = rfftw_create_plan_specific( n, dir, flags, cin, idist, cout, odist );

	(*env)->MonitorExit( env, (*env)->FindClass( env, "jfftw/Plan" ) );

	(*env)->ReleaseDoubleArrayElements( env, in, cin, 0 );
	(*env)->ReleaseDoubleArrayElements( env, out, cout, 0 );
	(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
	(*env)->SetObjectField( env, obj, id, arr );
}
/*
 * Class:     jfftw_real_Plan
 * Method:    destroyPlan
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_jfftw_real_Plan_destroyPlan( JNIEnv* env, jobject obj )
{
	jclass clazz = (*env)->GetObjectClass( env, obj );
	jfieldID id = (*env)->GetFieldID( env, clazz, "plan", "[B" );
	jbyteArray arr = (jbyteArray)(*env)->GetObjectField( env, obj, id );
	unsigned char* carr = (*env)->GetByteArrayElements( env, arr, 0 );

	rfftw_destroy_plan( *(rfftw_plan*)carr );

	(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
	(*env)->SetObjectField( env, obj, id, NULL );
}
/*
 * Class:     jfftw_real_Plan
 * Method:    transform
 * Signature: ([D)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_jfftw_real_Plan_transform___3D( JNIEnv* env, jobject obj, jdoubleArray in )
{
	jdouble *cin, *cout;
	jdoubleArray out;
	int i;

	jclass clazz = (*env)->GetObjectClass( env, obj );
	jfieldID id = (*env)->GetFieldID( env, clazz, "plan", "[B" );
	jbyteArray arr = (jbyteArray)(*env)->GetObjectField( env, obj, id );
	unsigned char* carr = (*env)->GetByteArrayElements( env, arr, 0 );
	rfftw_plan plan = *(rfftw_plan*)carr;
	if( plan->n != (*env)->GetArrayLength( env, in ) )
	{
		(*env)->ThrowNew( env, (*env)->FindClass( env, "java/lang/IndexOutOfBoundsException" ), "the Plan was created for a different length" );
		(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
		return NULL;
	}

	cin = (*env)->GetDoubleArrayElements( env, in, 0 );

	if( ! plan->flags & FFTW_THREADSAFE )
	{
		// synchronization
		(*env)->MonitorEnter( env, obj );
	}

	if( plan->flags & FFTW_IN_PLACE )
	{
		out = in;
		rfftw_one( plan, cin, NULL );
	}
	else
	{
		out = (*env)->NewDoubleArray( env, plan->n );
		cout = (*env)->GetDoubleArrayElements( env, out, 0 );

		rfftw_one( plan, cin, cout );

		(*env)->ReleaseDoubleArrayElements( env, out, cout, 0 );
	}

	if( ! plan->flags & FFTW_THREADSAFE )
	{
		// synchronization
		(*env)->MonitorExit( env, obj );
	}

	(*env)->ReleaseDoubleArrayElements( env, in, cin, 0 );
	(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
	return out;
}
/*
 * Class:     jfftw_real_Plan
 * Method:    transform
 * Signature: (I[DII[DII)V
 */
JNIEXPORT void JNICALL Java_jfftw_real_Plan_transform__I_3DII_3DII( JNIEnv *env, jobject obj, jint howmany, jdoubleArray in, jint istride, jint idist, jdoubleArray out, jint ostride, jint odist )
{
	jdouble *cin, *cout;
	int i;

	jclass clazz = (*env)->GetObjectClass( env, obj );
	jfieldID id = (*env)->GetFieldID( env, clazz, "plan", "[B" );
	jbyteArray arr = (jbyteArray)(*env)->GetObjectField( env, obj, id );
	unsigned char* carr = (*env)->GetByteArrayElements( env, arr, 0 );
	rfftw_plan plan = *(rfftw_plan*)carr;
	if( (howmany - 1) * idist + plan->n * istride != (*env)->GetArrayLength( env, in ) )
	{
		(*env)->ThrowNew( env, (*env)->FindClass( env, "java/lang/IndexOutOfBoundsException" ), "the Plan was created for a different length (in)" );
		(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
		return;
	}
	if( (howmany - 1) * odist + plan->n * ostride != (*env)->GetArrayLength( env, out ) )
	{
		(*env)->ThrowNew( env, (*env)->FindClass( env, "java/lang/IndexOutOfBoundsException" ), "the Plan was created for a different length (out)" );
		(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
		return;
	}

	cin = (*env)->GetDoubleArrayElements( env, in, 0 );
	cout = (*env)->GetDoubleArrayElements( env, out, 0 );

	if( ! plan->flags & FFTW_THREADSAFE )
	{
		// synchronization
		(*env)->MonitorEnter( env, obj );
	}
	rfftw( plan, howmany, cin, istride, idist, cout, ostride, odist );
	if( ! plan->flags & FFTW_THREADSAFE )
	{
		// synchronization
		(*env)->MonitorExit( env, obj );
	}

	(*env)->ReleaseByteArrayElements( env, arr, carr, 0 );
	(*env)->ReleaseDoubleArrayElements( env, in, cin, 0 );
	(*env)->ReleaseDoubleArrayElements( env, out, cout, 0 );
}


