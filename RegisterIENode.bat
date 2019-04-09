@echo off
java -jar %~dp0\lib\selenium-server-standalone-2.53.0.jar -role node -hub http://localhost:4444/grid/register -maxSession 5 -browser "browserName=internet explorer,platform=XP,maxInstances=5" -port 5555
pause