# SentinelAI staged Azure deployment helper.
# Prerequisites: Azure CLI, Bicep, Docker.
# This script provisions resources only when you explicitly run it.
# It never contains or commits credentials.

$ErrorActionPreference = 'Stop'

$ResourceGroup = if ($env:SENTINEL_AZURE_RG) { $env:SENTINEL_AZURE_RG } else { 'sentinelai-rg' }
$Location = if ($env:SENTINEL_AZURE_LOCATION) { $env:SENTINEL_AZURE_LOCATION } else { 'centralindia' }
$Suffix = if ($env:SENTINEL_AZURE_SUFFIX) { $env:SENTINEL_AZURE_SUFFIX.ToLower() } else { (Get-Random -Minimum 10000 -Maximum 99999).ToString() }
$Suffix = $Suffix -replace '[^a-z0-9]', ''
if ($Suffix.Length -lt 4) { throw 'SENTINEL_AZURE_SUFFIX must contain at least 4 lowercase letters/numbers.' }
$DeploymentName = "sentinelai-$Suffix"
$Template = Join-Path $PSScriptRoot '..\infrastructure\azure\main.bicep'
$RegistryName = "sentinelacr$Suffix"
$RegistryServer = "$RegistryName.azurecr.io"

if (-not $env:SENTINEL_POSTGRES_PASSWORD) { throw 'Set SENTINEL_POSTGRES_PASSWORD in the current shell.' }
if (-not $env:SENTINEL_JWT_SECRET) { throw 'Set SENTINEL_JWT_SECRET in the current shell.' }

Write-Host 'Checking Azure login...'
az account show | Out-Null

Write-Host "Creating resource group: $ResourceGroup"
az group create --name $ResourceGroup --location $Location | Out-Null

Write-Host "Creating or reusing private Azure Container Registry: $RegistryName"
az acr create --resource-group $ResourceGroup --name $RegistryName --sku Basic --admin-enabled false | Out-Null
az acr login --name $RegistryName

$BackendImage = if ($env:SENTINEL_BACKEND_IMAGE) { $env:SENTINEL_BACKEND_IMAGE } else { "$RegistryServer/sentinel-backend:phase12" }
$AiImage = if ($env:SENTINEL_AI_IMAGE) { $env:SENTINEL_AI_IMAGE } else { "$RegistryServer/sentinel-ai:phase12" }

if (-not $env:SENTINEL_SKIP_IMAGE_BUILD) {
  Write-Host 'Building and pushing backend image...'
  docker build -t $BackendImage (Join-Path $PSScriptRoot '..\backend')
  docker push $BackendImage

  Write-Host 'Building and pushing AI service image...'
  docker build -t $AiImage (Join-Path $PSScriptRoot '..\ai-service')
  docker push $AiImage
}

Write-Host 'Deploying PostgreSQL, Container Apps environment, backend and internal AI service...'
az deployment group create `
  --resource-group $ResourceGroup `
  --name $DeploymentName `
  --template-file $Template `
  --parameters location=$Location nameSuffix=$Suffix postgresAdminPassword=$env:SENTINEL_POSTGRES_PASSWORD jwtSecret=$env:SENTINEL_JWT_SECRET backendImage=$BackendImage aiImage=$AiImage frontendImage='' frontendOrigin='http://localhost:3000' | Out-Null

$BackendUrl = az deployment group show --resource-group $ResourceGroup --name $DeploymentName --query properties.outputs.backendUrl.value -o tsv
Write-Host "Backend URL: $BackendUrl"

$FrontendImage = if ($env:SENTINEL_FRONTEND_IMAGE) { $env:SENTINEL_FRONTEND_IMAGE } else { "$RegistryServer/sentinel-frontend:phase12" }

if (-not $env:SENTINEL_SKIP_IMAGE_BUILD) {
  Write-Host 'Building and pushing frontend image with the deployed backend URL...'
  docker build --build-arg NEXT_PUBLIC_API_URL="$BackendUrl/api/v1" -t $FrontendImage (Join-Path $PSScriptRoot '..\frontend')
  docker push $FrontendImage
}

Write-Host 'Deploying frontend...'
az deployment group create `
  --resource-group $ResourceGroup `
  --name "$DeploymentName-frontend" `
  --template-file $Template `
  --parameters location=$Location nameSuffix=$Suffix postgresAdminPassword=$env:SENTINEL_POSTGRES_PASSWORD jwtSecret=$env:SENTINEL_JWT_SECRET backendImage=$BackendImage aiImage=$AiImage frontendImage=$FrontendImage frontendOrigin='https://placeholder.invalid' | Out-Null

$FrontendOrigin = az deployment group show --resource-group $ResourceGroup --name "$DeploymentName-frontend" --query properties.outputs.frontendUrl.value -o tsv
Write-Host "Frontend URL: $FrontendOrigin"

Write-Host 'Applying exact frontend CORS origin to the backend deployment...'
az deployment group create `
  --resource-group $ResourceGroup `
  --name "$DeploymentName-final" `
  --template-file $Template `
  --parameters location=$Location nameSuffix=$Suffix postgresAdminPassword=$env:SENTINEL_POSTGRES_PASSWORD jwtSecret=$env:SENTINEL_JWT_SECRET backendImage=$BackendImage aiImage=$AiImage frontendImage=$FrontendImage frontendOrigin=$FrontendOrigin | Out-Null

Write-Host ''
Write-Host 'SentinelAI Azure deployment resources are ready.'
Write-Host "Frontend: $FrontendOrigin"
Write-Host "Backend:  $BackendUrl"
Write-Host 'Do not call the deployment verified until the final smoke/security checks pass.'
