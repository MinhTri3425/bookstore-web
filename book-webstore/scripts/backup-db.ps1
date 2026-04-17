param(
    [string]$OutputDir = "backups",
    [string]$DbName = "bookstore",
    [string]$DbUser = "root",
    [string]$DbPassword = "1234",
    [string]$ComposeFile = "docker-compose.yml",
    [string]$MysqlService = "mysql",
    [switch]$UseLocalClient
)

$ErrorActionPreference = "Stop"

$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Resolve-Path (Join-Path $scriptRoot "..")
$outputPath = Join-Path $projectRoot $OutputDir

if (-not (Test-Path $outputPath)) {
    New-Item -ItemType Directory -Path $outputPath | Out-Null
}

$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$backupFile = Join-Path $outputPath ("{0}-{1}.sql" -f $DbName, $timestamp)

Write-Host "[backup] Exporting database '$DbName' to '$backupFile' ..."

Push-Location $projectRoot
try {
    if ($UseLocalClient) {
        $localDump = Get-Command mysqldump -ErrorAction SilentlyContinue
        if (-not $localDump) {
            throw "Local mysqldump not found. Install MySQL client or run without -UseLocalClient to use Docker."
        }

        & $localDump.Source "-u$DbUser" "-p$DbPassword" "--databases" "$DbName" "--routines" "--events" "--triggers" "--single-transaction" "--quick" "--lock-tables=false" |
            Out-File -FilePath $backupFile -Encoding utf8
    }
    else {
        docker compose -f $ComposeFile exec -T $MysqlService sh -c "mysqldump -u$DbUser -p$DbPassword --databases $DbName --routines --events --triggers --single-transaction --quick --lock-tables=false" |
            Out-File -FilePath $backupFile -Encoding utf8
    }
}
finally {
    Pop-Location
}

if ((Get-Item $backupFile).Length -le 0) {
    Remove-Item -Path $backupFile -ErrorAction SilentlyContinue
    throw "Backup file is empty. Please check whether the MySQL service is running and has data."
}

Write-Host "[backup] Done: $backupFile"
