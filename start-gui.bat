@echo off
chcp 65001 >nul
rem 編譯 Java 檔案
echo 正在編譯 Java 檔案...
javac -encoding UTF-8 -d . src\main\java\com\sportrental\*.java
if errorlevel 1 (
    echo 編譯失敗！
    pause
    exit /b 1
)
echo 編譯完成！

rem 執行 Java 應用程式的 GUI 版本
echo 啟動 GUI 租借系統...
java com.sportrental.RentalGUI
rem 暫停，以便在執行完成後檢視輸出 (可選)
pause