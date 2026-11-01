@echo off
echo =========================================================
echo   Starting Event Ticket Booking Website (PASSAGE)...
echo   Hosting at: http://localhost:8080/
echo =========================================================
echo.
echo Launching Web Server...
mvn compile exec:java
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Maven command encountered an issue. Compiling and running with Java directly...
    javac -d bin -sourcepath src/main/java src/main/java/com/ticketing/Main.java
    java -cp bin com.ticketing.Main
)
pause
