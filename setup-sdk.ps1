$ErrorActionPreference = 'Stop'
$proj = $PSScriptRoot
Write-Host "== Configuración del SDK U.are.U ==" -ForegroundColor Cyan

Write-Host "Buscando dpuareu.jar..." -ForegroundColor Yellow
$roots = @(
  "C:\Program Files\DigitalPersona",
  "C:\Program Files (x86)\DigitalPersona",
  "C:\Program Files\HID Global",
  "C:\Program Files (x86)\HID Global",
  "C:\Program Files",
  "C:\Program Files (x86)"
)
$jar = $null
foreach ($r in $roots) {
  if (Test-Path $r) {
    $hit = Get-ChildItem -Path $r -Recurse -Filter "dpuareu.jar" -File -ErrorAction SilentlyContinue |
           Select-Object -First 1 -ExpandProperty FullName
    if ($hit) { $jar = $hit; break }
  }
}

if (-not $jar) {
  Write-Host "No se encontró dpuareu.jar." -ForegroundColor Red
  Write-Host "Verifica que instalaste el componente Java del 'U.are.U SDK'." -ForegroundColor Red
  Write-Host "Si conoces la ruta, cópialo manualmente a: $proj\lib\dpuareu.jar"
  exit 1
}
Write-Host "Encontrado: $jar" -ForegroundColor Green

$libDir = Join-Path $proj "lib"
if (-not (Test-Path $libDir)) { New-Item -ItemType Directory -Path $libDir | Out-Null }
Copy-Item -Path $jar -Destination (Join-Path $libDir "dpuareu.jar") -Force
Write-Host "Copiado a lib\dpuareu.jar" -ForegroundColor Green

$sdkRoot = Split-Path (Split-Path (Split-Path $jar))
$nativeDir = $null
$dll = Get-ChildItem -Path $sdkRoot -Recurse -Filter "dpuareu.dll" -File -ErrorAction SilentlyContinue |
       Select-Object -First 1 -ExpandProperty FullName
if ($dll) { $nativeDir = Split-Path $dll }
if ($nativeDir) {
  Write-Host "DLLs nativas en: $nativeDir" -ForegroundColor Green
  Write-Host "Para EJECUTAR usa: -Djava.library.path=`"$nativeDir`"" -ForegroundColor Cyan
} else {
  Write-Host "No se localizaron las DLLs nativas automáticamente (revisa la carpeta Bin del SDK)." -ForegroundColor Yellow
}

$jdk = "C:\Program Files\Java\jdk-17"
$mvn = "C:\Program Files\NetBeans-25\netbeans\java\maven\bin\mvn.cmd"
if (Test-Path $jdk) { $env:JAVA_HOME = $jdk }
if (Test-Path $mvn) {
  Write-Host "Compilando el proyecto..." -ForegroundColor Yellow
  & $mvn -f (Join-Path $proj "pom.xml") clean compile
} else {
  Write-Host "No se encontró el Maven de NetBeans; compila desde tu IDE." -ForegroundColor Yellow
}
