@echo off
echo ====================================================================
echo   ACTUALIZAR DRIVER DEL LECTOR DE HUELLAS U.are.U 4500 (QUITAR WBF)
echo ====================================================================
echo.
echo Este script desinstalara el driver WBF para obligar a Windows
echo a utilizar el driver clasico de DigitalPersona.
echo.
echo Presione una tecla para continuar...
pause > nul
echo.
echo Desinstalando el driver WBF (oem7.inf)...
pnputil /delete-driver oem7.inf /uninstall /force
echo.
echo ====================================================================
echo PROCESO COMPLETADO.
echo 1. Desconecte el lector de huellas USB y vuelva a conectarlo.
echo 2. Abra el Administrador de Dispositivos para verificar que ya no
echo    dice "(WBF)" en el nombre.
echo ====================================================================
echo.
pause
