@ECHO OFF

set JAVA=java

if NOT "%JAVA_HOME%" == "" goto setjava

goto run

:setjava

set JAVA="%JAVA_HOME%\bin\java"

:run

%JAVA% -Djava.util.logging.config.file=logging.properties -jar triana-app-@version@.jar %*
