param(
    [string]$JavaFile = "",
    [string]$BuildDir = ""
)

$ErrorActionPreference = "Stop"
$ModuleDir = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
if ([string]::IsNullOrWhiteSpace($JavaFile)) {
    $JavaFile = Join-Path $ModuleDir "src\main\java\SOC100_3cell_offset_v02.java"
}
if ([string]::IsNullOrWhiteSpace($BuildDir)) {
    $BuildDir = Join-Path $ModuleDir "outputs\build"
}

$Compiler = Get-Command comsolcompile -ErrorAction SilentlyContinue
if (-not $Compiler) {
    throw "comsolcompile was not found on PATH. Add the COMSOL 6.4 bin\win64 directory to PATH."
}

New-Item -ItemType Directory -Force -Path $BuildDir | Out-Null
$SourceCopy = Join-Path $BuildDir (Split-Path $JavaFile -Leaf)
Copy-Item -Force $JavaFile $SourceCopy

Push-Location $BuildDir
try {
    & $Compiler.Source -verbose $SourceCopy
    if ($LASTEXITCODE -ne 0) {
        throw "COMSOL Java compilation failed with exit code $LASTEXITCODE."
    }
}
finally {
    Pop-Location
}

Write-Host "Compiled class files are in $BuildDir"
