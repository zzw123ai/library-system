<#
Install Apache Maven (PowerShell script)
Usage (Run as normal user):
  .\install-maven.ps1 -Version 3.9.5
This script downloads Maven from multiple mirrors, extracts to
%USERPROFILE%\AppData\Local\Programs\apache-maven-<version>
and sets user environment variables MAVEN_HOME and updates PATH.
#>
param(
    [string]$Version = "3.9.5"
)

$target = "$env:USERPROFILE\AppData\Local\Programs\apache-maven-$Version"
$bin = "$target\bin"
$zip = "$env:TEMP\apache-maven-$Version-bin.zip"
$unpack = "$env:TEMP\apache-maven-unpack"

# Mirrors to try (order is tunu/aliyun/archive)
$urls = @(
    "https://mirrors.tuna.tsinghua.edu.cn/apache/maven/maven-3/$Version/binaries/apache-maven-$Version-bin.zip",
    "https://mirrors.ustc.edu.cn/apache/maven/maven-3/$Version/binaries/apache-maven-$Version-bin.zip",
    "https://mirrors.cloud.tencent.com/apache/maven/maven-3/$Version/binaries/apache-maven-$Version-bin.zip",
    "https://mirrors.aliyun.com/apache/maven/maven-3/$Version/binaries/apache-maven-$Version-bin.zip",
    "https://repo.huaweicloud.com/apache/maven/maven-3/$Version/binaries/apache-maven-$Version-bin.zip",
    "https://archive.apache.org/dist/maven/maven-3/$Version/binaries/apache-maven-$Version-bin.zip",
    "https://downloads.apache.org/maven/maven-3/$Version/binaries/apache-maven-$Version-bin.zip"
)

if (Test-Path $zip) { Remove-Item $zip -Force }
if (!(Test-Path $target)) { New-Item -ItemType Directory -Path $target | Out-Null }

$ok = $false
foreach ($u in $urls) {
    Write-Host "Trying $u"
    try {
        if (Get-Command curl.exe -ErrorAction SilentlyContinue) {
            curl.exe -L -o $zip $u
        } else {
            Invoke-WebRequest -Uri $u -OutFile $zip -UseBasicParsing -ErrorAction Stop
        }
        if (Test-Path $zip) { $ok = $true; break }
    } catch {
        Write-Host "download failed: $u`n  $_" -ForegroundColor Yellow
    }
}

if (-not $ok) {
    Write-Error "All downloads failed. Please check your network or manually download Maven to $target."
    exit 1
}

# Verify downloaded zip size (basic sanity check)
$minSize = 200000    # 200 KB
if ((Get-Item $zip).Length -lt $minSize) {
    Write-Error "Downloaded file is too small ($(Get-Item $zip).Length bytes). Likely a failed download. Aborting."
    Remove-Item $zip -Force
    exit 1
}

Write-Host 'Extracting...'
if (Test-Path $unpack) { Remove-Item $unpack -Recurse -Force }
try {
    Expand-Archive -Path $zip -DestinationPath $unpack -Force -ErrorAction Stop
} catch {
    Write-Error "Failed to extract archive: $_"
    if (Test-Path $zip) { Remove-Item $zip -Force }
    if (Test-Path $unpack) { Remove-Item $unpack -Recurse -Force }
    exit 1
}

$extractedDir = Join-Path $unpack "apache-maven-$Version"
if (Test-Path $extractedDir) {
    if (Test-Path $target) { Remove-Item $target -Recurse -Force }
    Move-Item -Path $extractedDir -Destination $target -Force
} else {
    Write-Error "Unexpected archive structure: expected $extractedDir not found. Aborting."
    Remove-Item $zip -Force
    if (Test-Path $unpack) { Remove-Item $unpack -Recurse -Force }
    exit 1
}

# cleanup
Remove-Item $zip -Force
if (Test-Path $unpack) { Remove-Item $unpack -Recurse -Force }

# set user environment variables
Write-Host "Setting MAVEN_HOME and updating PATH (user scope)..."
[Environment]::SetEnvironmentVariable('MAVEN_HOME', $target, 'User')
$old = [Environment]::GetEnvironmentVariable('Path','User')
if ($old -notlike "*apache-maven*") {
    [Environment]::SetEnvironmentVariable('Path', $old + ';' + $bin, 'User')
}

Write-Host "Installed Maven $Version to $target"
Write-Host "Please close and reopen your terminal, then run 'mvn -v' to verify installation."
Write-Host "Or in the current session run: $bin\mvn.cmd -v"

exit 0
