@echo off
chcp 65001 > nul
echo 正在启动前端服务 (Vue3)...
cd /d "%~dp0"
npm run dev
