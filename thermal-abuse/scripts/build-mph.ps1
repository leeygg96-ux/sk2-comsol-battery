param(
    [string]$Output = "",
    [switch]$Solve
)

$ErrorActionPreference = "Stop"
$ModuleDir = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$BuildDir = Join-Path $ModuleDir "outputs\build"
$LogDir = Join-Path $ModuleDir "outputs\logs"
$ModelDir = Join-Path $ModuleDir "outputs\models"

if ([string]::IsNullOrWhiteSpace($Output)) {
    $Output = Join-Path $ModelDir "SOC100_3cell_offset_v02.mph"
}

& (Join-Path $PSScriptRoot "compile-model.ps1") -BuildDir $BuildDir

$Batch = Get-Command comsolbatch -ErrorAction SilentlyContinue
if (-not $Batch) {
    throw "comsolbatch was not found on PATH. Add the COMSOL 6.4 bin\win64 directory to PATH."
}

New-Item -ItemType Directory -Force -Path $LogDir, $ModelDir | Out-Null
$ClassFile = Join-Path $BuildDir "SOC100_3cell_offset_v02.class"
$LogFile = Join-Path $LogDir "SOC100_3cell_offset_v02.log"

$Arguments = @("-inputfile", $ClassFile, "-outputfile", $Output, "-batchlog", $LogFile)
if ($Solve) {
    $Arguments += @("-study", "std1")
}
else {
    $Arguments += "-norun"
}

& $Batch.Source @Arguments
if ($LASTEXITCODE -ne 0) {
    throw "COMSOL batch failed with exit code $LASTEXITCODE. See $LogFile"
}

Write-Host "MPH output: $Output"
Write-Host "Batch log: $LogFile"
