@echo off
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0stop-console.ps1"
if errorlevel 1 pause
