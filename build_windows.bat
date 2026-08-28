@echo off
REM ============================================================
REM  FreeBox (TVBox 电脑版) 一键构建 Windows msi 安装包
REM  仓库: https://github.com/kknifer7/FreeBox  (GPL-3.0)
REM  前置依赖(需自行安装):
REM    1) JDK 17+  (推荐 Eclipse Temurin 21: https://adoptium.net)
REM    2) WiX Toolset v3 (https://wixtoolset.org)  -- jpackage 打 msi 必需
REM  用法: 双击本文件，或在 FreeBox 根目录执行 build_windows.bat
REM ============================================================
setlocal enabledelayedexpansion

REM ---- 按需修改以下两行指向你的实际安装路径 ----
if not defined JAVA_HOME set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.12.1-hotspot"
if not defined WIX set "WIX=C:\Program Files (x86)\WiX Toolset v3.11\"
REM ---------------------------------------------------------

set "PATH=%JAVA_HOME%\bin;%WIX%bin;%PATH%"

echo [check] JDK:
java -version 2>&1 | findstr /i "version" || (echo [错误] 未找到 java，请检查 JAVA_HOME & exit /b 1)

where candle >nul 2>&1
if errorlevel 1 (echo [错误] 未找到 WiX(candle.exe)，请安装 WiX Toolset v3 并配置 WIX 变量 & exit /b 1)
echo [check] WiX OK

cd /d "%~dp0"

echo.
echo [build] gradlew clean jpackage (msi) ...
call gradlew.bat clean jpackage -Pjpackage.installerType=msi --stacktrace
if errorlevel 1 (echo [失败] 构建出错，请查看上方日志 & exit /b 1)

echo.
echo [完成] 安装包位于: %~dp0build\distributions\
dir /b "%~dp0build\distributions\*.msi" 2>nul
pause
