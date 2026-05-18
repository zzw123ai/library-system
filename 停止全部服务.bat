@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

echo ========================================
echo    停止图书馆系统服务 (8080 / 5173)
echo ========================================
echo.

call :kill_port 8080 "后端"
call :kill_port 5173 "前端"

if exist "%~dp0.run" (
    del /f /q "%~dp0.run\browser.opened" 2>nul
    del /f /q "%~dp0.run\launching.lock" 2>nul
    rmdir "%~dp0.run" 2>nul
)

echo.
echo 完成。若仍有残留窗口，请手动关闭「图书馆系统-后端」「图书馆系统-前端」。
echo.
pause
exit /b 0

:kill_port
set "PORT=%~1"
set "NAME=%~2"
set "FOUND=0"
for /f "tokens=5" %%p in ('netstat -ano 2^>nul ^| findstr /C:":%PORT%" ^| findstr /I "LISTENING"') do (
    set "FOUND=1"
    echo [停止] %NAME% 端口 %PORT% PID=%%p
    taskkill /F /PID %%p >nul 2>&1
)
if "!FOUND!"=="0" echo [跳过] %NAME% 端口 %PORT% 未在监听
exit /b 0
