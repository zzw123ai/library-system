@echo off
chcp 65001 >nul
cd /d "%~dp0"
title 图书馆系统-前端

powershell -NoProfile -ExecutionPolicy Bypass -Command "try { $c = New-Object Net.Sockets.TcpClient; $c.Connect('127.0.0.1', 5173); if ($c.Connected) { $c.Close(); exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
if not errorlevel 1 (
  echo [提示] 端口 5173 已占用，前端可能已在运行。
  echo [提示] 如需重启，请先运行 停止全部服务.bat
  pause
  exit /b 0
)

echo ========================================
echo   前端服务
echo   端口: 5173
echo   目录: %CD%
echo ========================================
echo.
if not exist "node_modules" (
  echo [提示] 首次启动，正在安装依赖 npm install ...
  call npm install
  if errorlevel 1 (
    echo [错误] npm install 失败
    pause
    exit /b 1
  )
)
call npm run dev
echo.
echo 前端进程已退出，按任意键关闭窗口...
pause >nul
