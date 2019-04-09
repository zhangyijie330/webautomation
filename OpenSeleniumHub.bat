@echo off
java -jar %~dp0\lib\selenium-server-standalone-2.53.0.jar -role hub -maxSession 40 -port 4444 -timeout 0
pause