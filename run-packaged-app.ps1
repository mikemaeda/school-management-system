$jarPath = Join-Path $PSScriptRoot "target\school-management-system.jar"

if (-not (Test-Path $jarPath)) {
    Write-Host "Packaged app not found. Building it now..."
    mvn clean package
}

java -jar $jarPath
