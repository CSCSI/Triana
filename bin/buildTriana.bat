@ECHO OFF

cd ..
mvn install
cd triana-app\dist
triana.bat
