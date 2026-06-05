@echo off
REM Verificar se Maven está disponível
where mvn >nul 2>&1
if errorlevel 1 (
    echo Maven nao foi encontrado no PATH
    echo Por favor, instale Maven ou adicione-o ao PATH
    pause
    exit /b 1
)

cd /d "%~dp0"
echo Verificando todos os arquivos Java...
echo.

REM Verificar sintaxe dos arquivos principais
echo Compilando projeto...
mvn clean compile 2>&1 | findstr /i "ERROR" || echo Compilacao bem-sucedida

pause
