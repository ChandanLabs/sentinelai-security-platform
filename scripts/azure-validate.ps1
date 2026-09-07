# Validate Azure deployment assets without creating Azure resources.
$ErrorActionPreference = 'Stop'

az account show | Out-Null
az bicep version
az bicep build --file (Join-Path $PSScriptRoot '..\infrastructure\azure\main.bicep') --stdout | Out-Null
Write-Host 'Azure Bicep validation passed.'
