@echo off
chcp 65001 > nul
echo 正在启动后端服务 (Spring Boot)...
cd /d "%~dp0"
mvn spring-boot:run
