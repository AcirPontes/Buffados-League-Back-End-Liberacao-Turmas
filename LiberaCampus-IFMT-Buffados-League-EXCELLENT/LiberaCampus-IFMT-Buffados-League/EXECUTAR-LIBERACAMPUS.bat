@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title LiberaCampus IFMT - Inicializador

cls
echo ============================================================
echo                 LIBERACAMPUS IFMT
echo              Inicializacao do sistema
echo ============================================================
echo.

if not exist "pom.xml" (
    echo ERRO: pom.xml nao encontrado.
    echo Extraia todo o ZIP e execute este arquivo na pasta raiz.
    echo.
    pause
    exit /b 1
)

if not exist "mvnw.cmd" (
    echo ERRO: mvnw.cmd nao encontrado na pasta do projeto.
    echo Extraia novamente o ZIP completo.
    echo.
    pause
    exit /b 1
)

where java >nul 2>&1
if errorlevel 1 (
    echo ERRO: Java nao foi encontrado no PATH.
    echo Instale o JDK 21 e configure JAVA_HOME e PATH.
    echo.
    pause
    exit /b 1
)

echo Java detectado:
java -version
echo.
echo Na primeira execucao, o Maven e as dependencias serao baixados.
echo Quando aparecer Started LiberaCampusApplication, abra:
echo http://localhost:8080
echo.
echo Mantenha esta janela aberta enquanto utilizar o sistema.
echo Para encerrar, pressione CTRL+C.
echo ============================================================
echo.

call "%~dp0mvnw.cmd" spring-boot:run
set "APP_EXIT_CODE=%ERRORLEVEL%"

if not "%APP_EXIT_CODE%"=="0" (
    echo.
    echo A aplicacao terminou com erro. Envie uma captura desta janela.
    pause
)

exit /b %APP_EXIT_CODE%
