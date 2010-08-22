@ECHO OFF

set JAVA=java

if "%JAVA_HOME%" NOT == "" goto setjava

goto run

:setjava

set JAVA=%JAVA_HOME%

:run

%JAVA% -Djava.util.logging.config.file=logging.properties -jar triana-app-@version@.jar
