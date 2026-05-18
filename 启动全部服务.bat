@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

cd /d "%~dp0"
set "ROOT=%~dp0"
set "BACKEND_DIR=%ROOT%Backend"
set "FRONTEND_DIR=%ROOT%frontend"
set "RUN_DIR=%ROOT%.run"
set "LOCK_FILE=%RUN_DIR%\startup.lock"
set "BROWSER_FLAG=%RUN_DIR%\browser.opened"
set "BACKEND_START_LOCK=%RUN_DIR%\backend.starting.lock"
set "BACKEND_PORT=8080"
set "FRONTEND_PORT=5173"
set "LOGIN_URL=http://localhost:5173/login"
set "STARTED_BACKEND=0"
set "STARTED_FRONTEND=0"

if not exist "%RUN_DIR%" mkdir "%RUN_DIR%" >nul 2>&1

if exist "%LOCK_FILE%" (
  del /f /q "%LOCK_FILE%" >nul 2>&1
)
if exist "%LOCK_FILE%" (
  echo [INFO] startup lock exists, another launcher is running.
  echo [INFO] run stop script then retry.
  goto finish_fail
)
echo 1>"%LOCK_FILE%"

echo ========================================
echo   Library System Launcher
echo ========================================
echo.

where java >nul 2>&1 || (echo [ERROR] java not found. Install JDK 17. & goto cleanup_fail)
where mvn >nul 2>&1 || (echo [ERROR] mvn not found. Install Maven. & goto cleanup_fail)
where npm >nul 2>&1 || (echo [ERROR] npm not found. Install Node.js. & goto cleanup_fail)

if not exist "%BACKEND_DIR%\pom.xml" (
  echo [ERROR] Backend\pom.xml not found.
  goto cleanup_fail
)
if not exist "%FRONTEND_DIR%\package.json" (
  echo [ERROR] frontend\package.json not found.
  goto cleanup_fail
)

call :is_listening %BACKEND_PORT%
if not errorlevel 1 (
  echo [1/4] backend already running on %BACKEND_PORT%.
) else if exist "%BACKEND_START_LOCK%" (
  echo [1/4] backend lock detected, waiting 12s for ready state...
  call :wait_port %BACKEND_PORT% 12
  if not errorlevel 1 (
    echo [1/4] backend became ready, skip duplicate start.
  ) else (
    echo [1/4] stale backend lock found, clearing and restarting backend...
    del /f /q "%BACKEND_START_LOCK%" >nul 2>&1
    start "图书馆系统-后端" cmd /k call "%BACKEND_DIR%\dev-server.bat"
    set "STARTED_BACKEND=1"
  )
) else (
  echo [1/4] starting backend window...
  start "图书馆系统-后端" cmd /k call "%BACKEND_DIR%\dev-server.bat"
  set "STARTED_BACKEND=1"
)

echo [2/4] waiting backend port (max 90s)...
call :wait_port %BACKEND_PORT% 90
if errorlevel 1 (
  echo [WARN] backend wait timeout, continue.
) else (
  echo [OK] backend ready.
)

call :is_listening %FRONTEND_PORT%
if not errorlevel 1 (
  echo [3/4] frontend already running on %FRONTEND_PORT%.
) else (
  echo [3/4] starting frontend window...
  start "图书馆系统-前端" cmd /k call "%FRONTEND_DIR%\dev-server.bat"
  set "STARTED_FRONTEND=1"
)

echo [4/4] waiting frontend port (max 90s)...
call :wait_port %FRONTEND_PORT% 90
if errorlevel 1 (
  echo [WARN] frontend wait timeout. open url manually:
  echo %LOGIN_URL%
  goto summary
)
echo [OK] frontend ready.

REM 仅当“本次确实新启动了前端”时自动打开一次浏览器。
if "%STARTED_FRONTEND%"=="1" (
  if not exist "%BROWSER_FLAG%" (
    echo [INFO] opening browser once...
    echo 1>"%BROWSER_FLAG%"
    start "" "%LOGIN_URL%"
  )
) else (
  echo [INFO] frontend already running, browser will not reopen.
)

:summary
echo.
echo ----------------------------------------
echo backend: http://localhost:%BACKEND_PORT%
echo frontend: http://localhost:%FRONTEND_PORT%
echo login: %LOGIN_URL%
echo ----------------------------------------
echo for full restart run stop script first.
echo.
goto cleanup_ok

REM -------- functions --------

:is_listening
powershell -NoProfile -ExecutionPolicy Bypass -Command "try { $c = New-Object Net.Sockets.TcpClient; $c.Connect('127.0.0.1', %~1); if ($c.Connected) { $c.Close(); exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
exit /b %errorlevel%

:wait_port
set "W_PORT=%~1"
set /a "W_MAX=%~2"
set /a "W_CNT=0"
:wait_loop
call :is_listening %W_PORT%
if not errorlevel 1 exit /b 0
if %W_CNT% geq %W_MAX% exit /b 1
set /a "W_CNT+=3"
call :sleep 3
goto wait_loop

:sleep
set /a "_PING_N=%~1+1"
ping 127.0.0.1 -n %_PING_N% -w 1000 >nul
exit /b 0

:cleanup_fail
del /f /q "%LOCK_FILE%" >nul 2>&1
goto finish_fail

:cleanup_ok
del /f /q "%LOCK_FILE%" >nul 2>&1
echo %CMDCMDLINE% | find /I " /c " >nul 2>&1
if not errorlevel 1 exit /b 0
pause
exit /b 0

:finish_fail
echo %CMDCMDLINE% | find /I " /c " >nul 2>&1
if not errorlevel 1 exit /b 1
pause
exit /b 1
