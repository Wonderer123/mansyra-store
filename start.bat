@echo off
title Mansyra Project Launcher

echo ================================
echo     Mansyra Ecommerce App
echo ================================

echo.
echo Compiling Java backend...

javac -cp .;json-20240303.jar app\*.java

echo.
echo Starting server...

java -cp .;json-20240303.jar app.Main

timeout /t 2 >nul

echo.
echo Opening login page...

start http://localhost:8080/login.html

pause