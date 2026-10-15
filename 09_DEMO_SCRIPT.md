# SentinelAI live demo script

This scenario sends **synthetic** event records to the Spring Boot API. It does not collect host telemetry or perform any real privileged action. Use only the hosted demo's write key or the local demo key; never enter real company data.

## Prepare

1. Start the services using [10_DEPLOYMENT.md](./10_DEPLOYMENT.md), or set `$baseUrl` below to the public backend URL.
2. For local use, the script reads `DEMO_INGESTION_KEY` from `.env` without printing it. For a hosted demo, set `$env:SENTINEL_DEMO_WRITE_KEY` to the Render backend secret in your secure shell before running the calls below.
3. Run this PowerShell setup:

```powershell
$baseUrl = "http://localhost:8080"
if (-not $env:SENTINEL_DEMO_WRITE_KEY -and (Test-Path -LiteralPath ".env")) {
    $keyLine = Get-Content -LiteralPath ".env" |
        Where-Object { $_ -match "^DEMO_INGESTION_KEY=" } |
        Select-Object -Last 1
    if ($keyLine) {
        $env:SENTINEL_DEMO_WRITE_KEY = $keyLine.Substring("DEMO_INGESTION_KEY=".Length).Trim()
    }
}
if (-not $env:SENTINEL_DEMO_WRITE_KEY -or $env:SENTINEL_DEMO_WRITE_KEY.StartsWith("CHANGE_ME_")) {
    throw "Set a non-placeholder demo write key in .env or SENTINEL_DEMO_WRITE_KEY."
}

$writeHeaders = @{ "X-Demo-Ingestion-Key" = $env:SENTINEL_DEMO_WRITE_KEY }
function Invoke-DemoApi {
    param(
        [Parameter(Mandatory = $true)][string]$Method,
        [Parameter(Mandatory = $true)][string]$Path,
        [object]$Body = $null
    )
    $request = @{
        Uri = "$baseUrl$Path"
        Method = $Method
        Headers = $writeHeaders
        ContentType = "application/json"
        UseBasicParsing = $true
        TimeoutSec = 20
    }
    if ($null -ne $Body) {
        $request.Body = $Body | ConvertTo-Json -Depth 5 -Compress
    }
    try {
        $response = Invoke-WebRequest @request
    } catch {
        $status = "no HTTP response"
        if ($_.Exception.Response) {
            $status = "HTTP $([int]$_.Exception.Response.StatusCode)"
        }
        throw "API request failed ($status). Check the service URL, database health, and demo key; no key was printed."
    }
    if ([int]$response.StatusCode -lt 200 -or [int]$response.StatusCode -ge 300) {
        throw "API request failed with HTTP $([int]$response.StatusCode)."
    }
    if (-not $response.Content) {
        throw "API returned an empty response body for $Path."
    }
    try {
        return $response.Content | ConvertFrom-Json
    } catch {
        throw "API returned an invalid JSON response for $Path."
    }
}
```

For a hosted backend, use its URL for `$baseUrl`, for example:

```powershell
$baseUrl = "https://sentinel-backend.onrender.com"
```

## Generate and inspect events

Each request is validated by the script. The backend assigns the UUID and timestamp, evaluates the configured rules, stores the event, and returns the actual result.

```powershell
$normal = Invoke-DemoApi -Method POST -Path "/api/v1/events" -Body @{
    eventType = "login_success"
    sourceIp = "192.0.2.24"
    username = "analyst-demo"
    message = "SIMULATED DEMO EVENT — ordinary successful login"
}
if (-not $normal.id -or -not $normal.timestamp -or $normal.aiAnalysisStatus -ne "NOT_CONFIGURED") {
    throw "Normal event response did not contain the expected server ID, timestamp, and AI status."
}
Write-Host "Normal event: id=$($normal.id), severity=$($normal.severity), rule=$($normal.detectionRule)"

$lastFailure = $null
1..5 | ForEach-Object {
    $lastFailure = Invoke-DemoApi -Method POST -Path "/api/v1/events" -Body @{
        eventType = "login_failed"
        sourceIp = "198.51.100.42"
        username = "admin-demo"
        message = "SIMULATED DEMO EVENT — synthetic failed-login attempt $_ of 5"
    }
    Write-Host "Failed login $_`: severity=$($lastFailure.severity), rule=$($lastFailure.detectionRule)"
}
if ($lastFailure.detectionRule -ne "AUTH_BRUTE_FORCE" -or $lastFailure.aiAnalysisStatus -ne "NOT_CONFIGURED") {
    throw "The fifth synthetic failure did not report the expected rule and honest AI status."
}

$privilegeExample = Invoke-DemoApi -Method POST -Path "/api/v1/events" -Body @{
    eventType = "privilege_escalation"
    sourceIp = "203.0.113.19"
    username = "operator-demo"
    message = "SIMULATED DEMO EVENT — illustrative privilege escalation; no command was run"
}
if ($privilegeExample.detectionRule -ne "PRIVILEGE_ESCALATION") {
    throw "The privilege-escalation example did not report the expected rule."
}
Write-Host "Privilege example: severity=$($privilegeExample.severity), rule=$($privilegeExample.detectionRule)"
```

Now open **Events** in the dashboard. New persisted backend records appear through polling; select an event ID to inspect its message, rule, server timestamp, score, and AI status. Open **Alerts** and **Incidents** to see the linked records. Refresh the browser: the API reloads the stored history from PostgreSQL. The separate browser-only stream on the Events page is labelled **SIMULATED DATA** and is not part of this backend flow.

## Demonstrate incident lifecycle

The example below looks up the incident linked to the fifth failed login and advances it through the real, key-protected API. Each status change must advance by one lifecycle step.

```powershell
$alerts = Invoke-DemoApi -Method GET -Path "/api/v1/alerts?page=0&size=50"
$alert = $alerts.content | Where-Object { $_.eventId -eq $lastFailure.id } | Select-Object -First 1
if (-not $alert) { throw "Could not find an alert linked to the fifth failed login." }

$incidents = Invoke-DemoApi -Method GET -Path "/api/v1/incidents?page=0&size=50"
$incident = $incidents.content | Where-Object { $_.alertId -eq $alert.id } | Select-Object -First 1
if (-not $incident) { throw "Could not find an incident linked to that alert." }

foreach ($status in @("INVESTIGATING", "RESOLVED", "CLOSED")) {
    $incident = Invoke-DemoApi -Method PATCH -Path "/api/v1/incidents/$($incident.id)/status" -Body @{
        status = $status
    }
    Write-Host "Incident $($incident.id): $($incident.status)"
}
```

## Optional safe reset

Only run this when you want to remove the scenario's persisted synthetic history. It deletes records whose message starts with `SIMULATED DEMO EVENT` and the alerts/incidents linked to those events. It does not delete other rows or the PostgreSQL database. Browser-only stream data can separately be cleared with **Clear demo events**.

```powershell
$reset = Invoke-DemoApi -Method POST -Path "/api/v1/events/demo-data/reset"
Write-Host "Removed $($reset.deletedEvents) events, $($reset.deletedAlerts) alerts, and $($reset.deletedIncidents) incidents."
```

## Three-minute interview walkthrough

| Time | Show |
|---|---|
| 0:00–0:30 | Open the public dashboard; explain that records are synthetic and reads are public. |
| 0:30–0:50 | Show backend health and AI health. Be explicit that AI is health-only and inference is not configured. |
| 0:50–1:15 | Submit the ordinary successful-login request and find its server UUID/timestamp in Events. |
| 1:15–1:45 | Submit five failed-login events with the same synthetic source IP; show the fifth event's `AUTH_BRUTE_FORCE` rule and persisted alert/incident. |
| 1:45–2:05 | Submit the labelled privilege-escalation example; show its deterministic result. No real command executes. |
| 2:05–2:30 | Open event detail, alert, and incident; refresh to demonstrate database-backed history and automatic polling. |
| 2:30–2:50 | Advance the incident with the protected status endpoint and show the updated dashboard status. |
| 2:50–3:00 | Close with limitations: public read-only demo, shared write key, PostgreSQL storage, and no AI inference or production auth. |

## What this demo does not prove

The browser-only stream is simulated data, not telemetry. The backend currently implements deterministic rules only. Risk values are demo scoring values, not calibrated probabilities. There is no Isolation Forest inference, account system, RBAC, rate limiting, audit trail, or real-world security guarantee. Never describe the demonstration as live host monitoring or a production-ready detection system.
