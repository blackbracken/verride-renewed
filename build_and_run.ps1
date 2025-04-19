$ErrorActionPreference = "Stop"

$runningProcessId = Get-Content "./playground/.playground_pid" -ErrorAction Ignore
if ($runningProcessId) {
    Stop-Process -Id $runningProcessId -Force -ErrorAction Ignore
    Remove-Item -Path "./playground/.playground_pid"
    Start-Sleep -Seconds 3
}

Start-Process ./gradlew jar -PassThru -NoNewWindow -Wait

$pluginsPath = "./playground/plugins"
if (-not(Test-Path -Path $pluginsPath))
{
    New-Item -Path $pluginsPath -ItemType Directory
}

Copy-Item -Path ./build/libs/*.jar -Destination $pluginsPath

if (-not(Test-Path -Path ./playground/paper.jar))
{
    $paperUrl = 'https://api.papermc.io/v2/projects/paper'
    $version = "1.21.1"
#     (Invoke-WebRequest -Headers @{ "Content-type" = "application/json" } -Method GET -Uri $paperUrl).Content `
#             | ConvertFrom-Json `
#             | Select-Object -ExpandProperty versions `
#             | Select-Object -Last 1

    $build = (Invoke-WebRequest -Headers @{ "Content-type" = "application/json" } -Method GET -Uri "$paperUrl/versions/$version").Content `
        | ConvertFrom-Json `
        | Select-Object -ExpandProperty builds `
        | Select-Object -Last 1

    Invoke-WebRequest -Uri "$paperUrl/versions/$version/builds/$build/downloads/paper-$version-$build.jar" -OutFile "./playground/paper.jar"
    ICACLS "./playground/paper.jar" /grant:r "users:(RX)" /C
}

$playgroundPath = "./playground"

Push-Location
Set-Location $playgroundPath
$serverProcess = Start-Process java -ArgumentList "-jar", "paper.jar", "--nogui" -NoNewWindow -PassThru
$serverProcess.Id | Out-File ".playground_pid"
$serverProcess.WaitForExit()
Pop-Location