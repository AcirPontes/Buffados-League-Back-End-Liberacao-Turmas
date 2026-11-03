@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title LiberaCampus IFMT - Gerar JAR

if not exist "pom.xml" (
    echo ERRO: execute este arquivo na pasta raiz do projeto.
    pause
    exit /b 1
)

call "%~dp0mvnw.cmd" clean package
if errorlevel 1 (
    echo.
    echo ERRO: a compilacao ou os testes falharam.
    pause
    exit /b 1
)

echo.
echo Compilacao concluida. Iniciando o sistema...
echo Acesse http://localhost:8080
java -jar "target\liberacampus-1.0.0.jar"
set "APP_EXIT_CODE=%ERRORLEVEL%"
if not "%APP_EXIT_CODE%"=="0" pause
exit /b %APP_EXIT_CODE%
