@echo off
echo =========================================================
echo   Starting PASSAGE Event Ticket Booking Website...
echo   URL: http://localhost:8080/
echo =========================================================
echo.
mvn compile exec:java
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Trying direct java compilation and run...
    javac -d bin -sourcepath src/main/java src/main/java/com/ticketing/Main.java
    java -cp bin com.ticketing.Main
)
pause
