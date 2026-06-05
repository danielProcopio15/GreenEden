@echo off
cd /d "%~dp0"
echo Compilando projeto...
mvn clean package -DskipTests
echo.
echo Iniciando aplicacao...
java -jar target/calculadora-sustentavel-0.0.1-SNAPSHOT.jar
pause
