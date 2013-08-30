@echo off
jar cfm bin\ClientGUI.jar ClientManifest.txt bin\org\pavlinic\chat\*.class bin\org\pavlinic\chat\client\*.class
echo JAR created in bin directory
pause