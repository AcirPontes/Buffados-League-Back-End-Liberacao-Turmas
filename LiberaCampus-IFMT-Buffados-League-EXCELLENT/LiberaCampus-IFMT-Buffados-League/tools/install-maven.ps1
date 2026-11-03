param(
    [Parameter(Mandatory = $true)]
    [string]$Version,

    [Parameter(Mandatory = $true)]
    [string]$Destination
)

$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'

$archiveName = "apache-maven-$Version-bin.zip"
$archivePath = Join-Path $env:TEMP $archiveName
$installPath = Join-Path $Destination "apache-maven-$Version"
$expectedMaven = Join-Path $installPath 'bin\mvn.cmd'
$url = "https://archive.apache.org/dist/maven/maven-3/$Version/binaries/$archiveName"

try {
    if (Test-Path $expectedMaven) {
        Write-Host "Maven $Version ja esta instalado."
        exit 0
    }

    New-Item -ItemType Directory -Path $Destination -Force | Out-Null

    if (Test-Path $archivePath) {
        Remove-Item $archivePath -Force
    }

    Write-Host "[LiberaCampus] Baixando Apache Maven $Version..."
    Write-Host "Origem: $url"
    Invoke-WebRequest -Uri $url -OutFile $archivePath -UseBasicParsing

    if (-not (Test-Path $archivePath)) {
        throw 'O download terminou sem criar o arquivo ZIP.'
    }

    Write-Host '[LiberaCampus] Extraindo o Maven...'
    Expand-Archive -LiteralPath $archivePath -DestinationPath $Destination -Force

    if (-not (Test-Path $expectedMaven)) {
        throw "O arquivo mvn.cmd nao foi encontrado em: $expectedMaven"
    }

    Write-Host "[LiberaCampus] Maven instalado em: $installPath"
}
catch {
    Write-Host ''
    Write-Host '[ERRO] Nao foi possivel instalar o Maven automaticamente.' -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}
finally {
    if (Test-Path $archivePath) {
        Remove-Item $archivePath -Force -ErrorAction SilentlyContinue
    }
}
