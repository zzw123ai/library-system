@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion
cd /d "%~dp0"
title 图书馆系统-后端

set "RUN_DIR=%~dp0..\.run"
set "BACKEND_LOCK=%RUN_DIR%\backend.starting.lock"
if not exist "%RUN_DIR%" mkdir "%RUN_DIR%" >nul 2>&1

if exist "%BACKEND_LOCK%" (
  echo [提示] 检测到后端正在启动或编译中，已跳过重复启动。
  echo [提示] 若长时间卡住，可先运行 停止全部服务.bat 再重试。
  pause
  exit /b 0
)
echo 1>"%BACKEND_LOCK%"

powershell -NoProfile -ExecutionPolicy Bypass -Command "try { $c = New-Object Net.Sockets.TcpClient; $c.Connect('127.0.0.1', 8080); if ($c.Connected) { $c.Close(); exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
if not errorlevel 1 (
  echo [提示] 端口 8080 已占用，后端可能已在运行。
  echo [提示] 如需重启，请先运行 停止全部服务.bat
  del /f /q "%BACKEND_LOCK%" >nul 2>&1
  pause
  exit /b 0
)

echo ========================================
echo   后端服务
echo   端口: 8080
echo   目录: %CD%
echo ========================================
echo.
mvn spring-boot:run
set "MAVEN_EXIT=%ERRORLEVEL%"
del /f /q "%BACKEND_LOCK%" >nul 2>&1
echo.
if not "%MAVEN_EXIT%"=="0" (
  echo [错误] 后端启动失败，可能是并发启动或端口冲突导致。
)
echo 后端进程已退出，按任意键关闭窗口...
pause >nul
endlocal
