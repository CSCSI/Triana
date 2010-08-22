jfftw : jfftw_real_Plan.o jfftw_real_nd_Plan.o jfftw_complex_Plan.o jfftw_complex_nd_Plan.o jfftw_Wisdom.o
	gcc -shared -fPIC -o libjfftw.so jfftw_real_Plan.o jfftw_real_nd_Plan.o jfftw_complex_Plan.o jfftw_complex_nd_Plan.o jfftw_Wisdom.o -L${FFTW}/lib -lm -lfftw -lrfftw

jfftw_real_Plan.o :
	gcc -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux -I${FFTW}/include -c jfftw_real_Plan.c

jfftw_real_nd_Plan.o :
	gcc -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux -I${FFTW}/include -c jfftw_real_nd_Plan.c

jfftw_complex_Plan.o :
	gcc -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux -I${FFTW}/include -c jfftw_complex_Plan.c

jfftw_complex_nd_Plan.o :
	gcc -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux -I${FFTW}/include -c jfftw_complex_nd_Plan.c

jfftw_Wisdom.o :
	gcc -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux -I${FFTW}/include -c jfftw_Wisdom.c

clean : cleano cleanso

cleano :
	rm -f *.o

cleanso :
	rm -f libjfftw.so
