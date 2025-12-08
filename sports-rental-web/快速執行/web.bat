@echo off
chcp 65001
title 體育器材租借系統
echo ==========================================
echo      正在啟動體育器材租借系統...
echo ==========================================
echo.
echo 請稍候，等待下方出現 "Started SportRentalApplication..." 字樣
echo 然後請打開瀏覽器，輸入 http://localhost:8080
echo.
java -jar sports-rental-web.jar
pause