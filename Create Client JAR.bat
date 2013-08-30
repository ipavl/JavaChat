@echo off
cd bin
jar cfm ClientGUI.jar ..\ClientManifest.txt org\pavlinic\chat\*.class org\pavlinic\chat\client\*.class
echo JAR created in bin directory
pause