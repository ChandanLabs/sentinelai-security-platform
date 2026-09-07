param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$AdminEmail
)

$ErrorActionPreference = 'Stop'

function Read-Required([string]$Prompt) {
    $value = Read-Host $Prompt
    if ([string]::IsNullOrWhiteSpace($value)) { throw "$Prompt is required." }
    return $value
}

function Invoke-Api([string]$Method, [string]$Uri, [object]$Body = $null, [string]$Token = $null) {
    $headers = @{}
    if ($Token) { $headers.Authorization = "Bearer $Token" }
    $params = @{ Method = $Method; Uri = $Uri; Headers = $headers; ContentType = 'application/json' }
    if ($null -ne $Body) { $params.Body = ($Body | ConvertTo-Json -Depth 10) }
    return Invoke-RestMethod @params
}

if (-not $AdminEmail) { $AdminEmail = Read-Required 'Admin email' }
$adminPassword = Read-Required 'Admin password'

Write-Host "SentinelAI demo seed - synthetic defensive data only" -ForegroundColor Cyan
Write-Host "API: $BaseUrl"

$login = Invoke-Api POST "$BaseUrl/api/v1/auth/login" @{ email = $AdminEmail; password = $adminPassword }
$token = $login.accessToken

$asset = Invoke-Api POST "$BaseUrl/api/v1/assets" @{
    name = 'Demo Production Web Server'; hostname = 'demo-web-01'; ipAddress = '10.10.20.15'; environment = 'demo'; active = $true
} $token

$app = Invoke-Api POST "$BaseUrl/api/v1/applications" @{
    name = 'Sentinel Demo API'; version = '1.0.0'; environment = 'demo'; assetId = $asset.id; active = $true
} $token

$source = Invoke-Api POST "$BaseUrl/api/v1/event-sources" @{
    name = 'Demo Authentication Logs'; type = 'AUTHENTICATION'; applicationId = $app.id; active = $true
} $token

$base = [DateTime]::UtcNow.AddMinutes(-30)
for ($i = 0; $i -lt 25; $i++) {
    $eventTime = $base.AddMinutes($i)
    $event = @{
        eventSourceId = $source.id
        eventType = 'login_success'
        severity = 'INFO'
        timestamp = $eventTime.ToString('o')
        sourceIp = "10.10.1.$(($i % 20) + 10)"
        username = "demo-user-$($i % 5)"
        message = 'Normal successful authentication for demo baseline.'
        metadata = (@{ result = 'success'; demo = $true; sequence = $i } | ConvertTo-Json -Compress)
    }
    Invoke-Api POST "$BaseUrl/api/v1/events" $event $token | Out-Null
}

Write-Host 'Baseline events created: 25' -ForegroundColor Green

$train = Invoke-Api POST "$BaseUrl/api/v1/detections/ai/train" @{ sampleSize = 25 } $token
Write-Host "AI model trained: $($train.modelVersion), samples=$($train.samples)" -ForegroundColor Green

# Five failed logins from one source deliberately trigger AUTH_BRUTE_FORCE.
for ($i = 0; $i -lt 5; $i++) {
    $failed = @{
        eventSourceId = $source.id
        eventType = 'login_failed'
        severity = 'HIGH'
        timestamp = [DateTime]::UtcNow.AddSeconds($i).ToString('o')
        sourceIp = '203.0.113.77'
        username = 'admin'
        message = 'Failed authentication attempt against privileged account.'
        metadata = (@{ result = 'failure'; demo = $true; sequence = $i } | ConvertTo-Json -Compress)
    }
    Invoke-Api POST "$BaseUrl/api/v1/events" $failed $token | Out-Null
}
Write-Host 'Brute-force trigger events created: 5' -ForegroundColor Yellow

# Critical privilege event deliberately matches PRIVILEGE_ESCALATION and creates the incident path.
$suspicious = @{
    eventSourceId = $source.id
    eventType = 'privilege_escalation'
    severity = 'CRITICAL'
    timestamp = [DateTime]::UtcNow.ToString('o')
    sourceIp = '203.0.113.77'
    username = 'admin'
    message = 'User added to administrator group after repeated failed authentication attempts.'
    metadata = (@{ failedAttempts = 5; privileged = $true; unusualSource = $true; demo = $true } | ConvertTo-Json -Compress)
}
$incidentEvent = Invoke-Api POST "$BaseUrl/api/v1/events" $suspicious $token

Write-Host "Suspicious event created: $($incidentEvent.id)" -ForegroundColor Yellow

$alerts = Invoke-Api GET "$BaseUrl/api/v1/alerts?page=0&size=10" $null $token
$alert = $alerts.content | Where-Object { $_.eventId -eq $incidentEvent.id } | Select-Object -First 1

if ($alert) {
    Write-Host "Alert created: $($alert.id) risk=$($alert.riskLevel) score=$($alert.riskScore) status=$($alert.status)" -ForegroundColor Yellow
    if ($alert.incidentId) {
        Write-Host "Incident auto-created: $($alert.incidentId)" -ForegroundColor Magenta
    }
}
else {
    Write-Warning 'No alert was found for the suspicious event. Inspect backend logs before the demo.'
}

Write-Host ''
Write-Host 'Demo seed completed. All generated records are synthetic.' -ForegroundColor Green
Write-Host "Dashboard: http://localhost:3000"
