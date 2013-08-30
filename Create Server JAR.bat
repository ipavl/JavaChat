@echo off
jar cfm bin\ServerGUI.jar ServerManifest.txt bin\org\pavlinic\chat\*.class bin\org\pavlinic\chat\server\*.class
echo JAR created in bin directory
pause