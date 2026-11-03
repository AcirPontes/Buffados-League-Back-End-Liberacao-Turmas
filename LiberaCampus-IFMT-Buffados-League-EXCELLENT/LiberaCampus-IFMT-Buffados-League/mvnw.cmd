@echo off
setlocal EnableExtensions

rem Inicializador simplificado do Maven para Windows.
set "MAVEN_VERSION=3.9.11"
set "MAVEN_BASE=%USERPROFILE%\.m2\wrapper\dists"
set "MAVEN_HOME=%MAVEN_BASE%\apache-maven-%MAVEN_VERSION%"
set "MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd"

if not exist "%MAVEN_CMD%" (
    echo.
    echo [LiberaCampus] Maven %MAVEN_VERSION% nao encontrado.
    echo [LiberaCampus] O instalador sera executado automaticamente.
    echo.

    where powershell.exe >nul 2>&1
    if errorlevel 1 (
        echo ERRO: PowerShell nao foi encontrado.
        exit /b 1
    )

    powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0tools\install-maven.ps1" -Version "%MAVEN_VERSION%" -Destination "%MAVEN_BASE%"
    if errorlevel 1 (
        echo.
        echo ERRO: nao foi possivel instalar o Maven.
        exit /b 1
    )
)

if not exist "%MAVEN_CMD%" (
    echo ERRO: o executavel do Maven nao foi localizado:
    echo %MAVEN_CMD%
    exit /b 1
)

call "%MAVEN_CMD%" %*
exit /b %ERRORLEVEL%
