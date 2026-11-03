@echo off
setlocal EnableExtensions
cd /d "%~dp0"
title Diagnostico LiberaCampus IFMT

echo ============================================================
echo          DIAGNOSTICO DO LIBERACAMPUS IFMT
echo ============================================================
echo Pasta atual:
cd
echo.

echo [1] Verificando pom.xml...
if exist "pom.xml" (echo OK - pom.xml encontrado.) else (echo FALHA - pom.xml ausente.)
echo.

echo [2] Verificando mvnw.cmd...
if exist "mvnw.cmd" (echo OK - mvnw.cmd encontrado.) else (echo FALHA - mvnw.cmd ausente.)
echo.

echo [3] Verificando instalador PowerShell...
if exist "tools\install-maven.ps1" (echo OK - instalador encontrado.) else (echo FALHA - instalador ausente.)
echo.

echo [4] Verificando Java...
where java
if errorlevel 1 (echo FALHA - Java nao esta no PATH.) else (java -version)
echo.

echo [5] Verificando PowerShell...
where powershell.exe
if errorlevel 1 (echo FALHA - PowerShell nao encontrado.) else (echo OK - PowerShell encontrado.)
echo.

echo Envie uma captura desta janela caso alguma verificacao falhe.
echo ============================================================
pause
