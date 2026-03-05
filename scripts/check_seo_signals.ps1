param(
    [string]$BaseUrl = "https://automoneypit.com"
)

$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Net.Http

function Invoke-UrlCheck {
    param(
        [string]$Url
    )

    $handler = New-Object System.Net.Http.HttpClientHandler
    $handler.AllowAutoRedirect = $false
    $client = New-Object System.Net.Http.HttpClient($handler)
    $client.Timeout = [TimeSpan]::FromSeconds(20)
    $client.DefaultRequestHeaders.Add("User-Agent", "seo-signal-check/1.0")

    try {
        $response = $client.GetAsync($Url).GetAwaiter().GetResult()
        $status = [int]$response.StatusCode
        $location = ""
        if ($response.Headers.Location) {
            $location = $response.Headers.Location.ToString()
        }

        $xRobots = ""
        if ($response.Headers.Contains("X-Robots-Tag")) {
            $xRobots = ($response.Headers.GetValues("X-Robots-Tag") -join ";")
        }

        $canonical = ""
        $metaRobots = ""
        if ($status -ge 200 -and $status -lt 300) {
            $body = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
            $canonicalMatch = [regex]::Match($body, '<link rel="canonical" href="([^"]+)"', "IgnoreCase")
            if ($canonicalMatch.Success) {
                $canonical = $canonicalMatch.Groups[1].Value
            }
            $metaMatch = [regex]::Match($body, '<meta name="robots" content="([^"]+)"', "IgnoreCase")
            if ($metaMatch.Success) {
                $metaRobots = $metaMatch.Groups[1].Value
            }
        }

        [PSCustomObject]@{
            Url        = $Url
            Status     = $status
            Location   = $location
            Canonical  = $canonical
            XRobotsTag = $xRobots
            MetaRobots = $metaRobots
        }
    }
    finally {
        $client.Dispose()
        $handler.Dispose()
    }
}

$checks = @(
    "$BaseUrl/",
    "$BaseUrl/models",
    "$BaseUrl/lead?page_type=playwright&intent=SELL&verdict_state=TIME_BOMB&brand=toyota&model=camry",
    "$BaseUrl/lead-capture?verdict=TIME_BOMB&brand=toyota",
    "$BaseUrl/report?token=invalid-seo-check",
    "$BaseUrl/models/mercedesbenz",
    "$BaseUrl/models/mercedes-benz",
    "$BaseUrl/verdict/honda/crv/vtc-actuator",
    "$BaseUrl/verdict/honda/cr-v/vtc-actuator",
    "$BaseUrl/should-i-fix/2018-honda-crv",
    "$BaseUrl/should-i-fix/2018-honda-cr-v"
)

$results = $checks | ForEach-Object { Invoke-UrlCheck -Url $_ }
$results | Format-Table -AutoSize

$failures = @()

$leadRow = $results | Where-Object { $_.Url -like "*/lead?page_type=playwright*" } | Select-Object -First 1
if ($leadRow -and ($leadRow.Status -ne 302 -or $leadRow.XRobotsTag -notmatch "noindex")) {
    $failures += "Lead endpoint should be 302 + noindex."
}

$leadCaptureRow = $results | Where-Object { $_.Url -like "*/lead-capture*" } | Select-Object -First 1
if ($leadCaptureRow -and ($leadCaptureRow.Status -ne 200 -or $leadCaptureRow.XRobotsTag -notmatch "noindex" -or $leadCaptureRow.MetaRobots -notmatch "noindex")) {
    $failures += "Lead capture should be 200 + noindex meta/header."
}

$modelsRedirectRow = $results | Where-Object { $_.Url -like "*/models/mercedesbenz" } | Select-Object -First 1
if ($modelsRedirectRow -and ($modelsRedirectRow.Status -ne 301)) {
    $failures += "Non-canonical brand slug should 301 to canonical slug."
}

$verdictRedirectRow = $results | Where-Object { $_.Url -like "*/verdict/honda/crv/*" } | Select-Object -First 1
if ($verdictRedirectRow -and ($verdictRedirectRow.Status -ne 301)) {
    $failures += "Non-canonical verdict model slug should 301 to canonical slug."
}

$shouldFixRedirectRow = $results | Where-Object { $_.Url -like "*/should-i-fix/2018-honda-crv" } | Select-Object -First 1
if ($shouldFixRedirectRow -and ($shouldFixRedirectRow.Status -ne 301)) {
    $failures += "Non-canonical should-i-fix slug should 301 to canonical slug."
}

if ($failures.Count -gt 0) {
    Write-Host ""
    Write-Host "SEO signal check failed:" -ForegroundColor Red
    $failures | ForEach-Object { Write-Host "- $_" -ForegroundColor Red }
    exit 1
}

Write-Host ""
Write-Host "SEO signal check passed." -ForegroundColor Green
