@ECHO OFF

set JAVA=java

if "%JAVA_HOME%" NOT == "" goto setjava

goto run

:setjava

set JAVA=%JAVA_HOME%

:run

set CP=.;triana-app-@version@.jar

%JAVA% -classpath %CP% -Djava.util.logging.config.file=logging.properties Triana
