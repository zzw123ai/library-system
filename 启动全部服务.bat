@echo off
chcp 65001 > nul
echo ========================================
echo    图书馆管理系统 - 一键启动脚本
echo ========================================
echo.

REM 启动后端服务
echo [1/2] 正在启动后端服务 (Spring Boot)...
cd /d "%~dp0Backend"
start "Backend-SpringBoot" cmd /k "mvn spring-boot:run"

echo 等待后端启动 (约10秒)...
timeout /t 10 /nobreak > nul

REM 启动前端服务
echo [2/2] 正在启动前端服务 (Vue3)...
cd /d "%~dp0frontend"
start "Frontend-Vue" cmd /k "npm run dev"

echo.
echo ========================================
echo    启动完成！
echo ========================================
echo    后端地址: http://localhost:8080
echo    前端地址: http://localhost:5173
echo ========================================
echo.
echo 按任意键打开浏览器访问...
pause > nul
start http://localhost:5173
