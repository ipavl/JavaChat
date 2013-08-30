@echo off
cd bin
jar cfm ServerGUI.jar ..\ServerManifest.txt org\pavlinic\chat\*.class org\pavlinic\chat\server\*.class
echo JAR created in bin directory
pause