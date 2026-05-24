$tools = Join-Path $PSScriptRoot "..\java-tools"
$localJava = Join-Path $tools "jdk-17"
$localMaven = Join-Path $tools "apache-maven-3.9.11"

if (Test-Path -LiteralPath $localJava) {
    $env:JAVA_HOME = (Resolve-Path $localJava).Path
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"
}

if (Test-Path -LiteralPath $localMaven) {
    $env:Path = "$(Resolve-Path $localMaven)\bin;$env:Path"
}

mvn exec:java
