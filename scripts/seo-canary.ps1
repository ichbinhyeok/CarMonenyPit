param(
    [string]$BaseUrl = "https://automoneypit.com",
    [string]$CanonicalBaseUrl = "",
    [string]$OutputDirectory = "output/seo-canary"
)

$ErrorActionPreference = "Stop"
$base = $BaseUrl.TrimEnd("/")
$canonicalBase = if ([string]::IsNullOrWhiteSpace($CanonicalBaseUrl)) {
    $base
} else {
    $CanonicalBaseUrl.TrimEnd("/")
}
$resolvedOutput = [System.IO.Path]::GetFullPath((Join-Path (Get-Location) $OutputDirectory))
New-Item -ItemType Directory -Force -Path $resolvedOutput | Out-Null

$expectedPaths = @(
    "/tools/repair-or-sell-calculator",
    "/guides/car-repair-estimate-second-opinion",
    "/verdict/nissan/rogue/cvt-transmission",
    "/verdict/ford/fusion/15l20l-coolant-intrusion",
    "/verdict/ford/escape/coolant-intrusion",
    "/verdict/tesla/model-3/control-arms",
    "/verdict/ram/1500/exhaust-manifold-bolts",
    "/verdict/honda/odyssey/200000-miles"
)

function Get-PageCheck {
    param(
        [string]$Url,
        [string]$ExpectedCanonical
    )

    try {
        $response = Invoke-WebRequest -Uri $Url -MaximumRedirection 5 -UseBasicParsing -TimeoutSec 30
        $canonicalMatch = [regex]::Match(
            $response.Content,
            '<link[^>]+rel=["'']canonical["''][^>]+href=["'']([^"'']+)["'']',
            [System.Text.RegularExpressions.RegexOptions]::IgnoreCase
        )
        if (-not $canonicalMatch.Success) {
            $canonicalMatch = [regex]::Match(
                $response.Content,
                '<link[^>]+href=["'']([^"'']+)["''][^>]+rel=["'']canonical["'']',
                [System.Text.RegularExpressions.RegexOptions]::IgnoreCase
            )
        }

        [pscustomobject]@{
            Url = $Url
            ExpectedCanonical = $ExpectedCanonical
            Status = [int]$response.StatusCode
            FinalUrl = $response.BaseResponse.ResponseUri.AbsoluteUri
            Canonical = if ($canonicalMatch.Success) { $canonicalMatch.Groups[1].Value } else { "" }
            Noindex = [regex]::IsMatch(
                $response.Content,
                '<meta[^>]+name=["'']robots["''][^>]+content=["''][^"'']*noindex',
                [System.Text.RegularExpressions.RegexOptions]::IgnoreCase
            )
            Title = ([regex]::Match(
                $response.Content,
                '<title>(.*?)</title>',
                [System.Text.RegularExpressions.RegexOptions]::IgnoreCase
            )).Groups[1].Value
            Error = ""
        }
    }
    catch {
        [pscustomobject]@{
            Url = $Url
            ExpectedCanonical = $ExpectedCanonical
            Status = 0
            FinalUrl = ""
            Canonical = ""
            Noindex = $false
            Title = ""
            Error = $_.Exception.Message
        }
    }
}

$sitemapUrl = "$base/sitemap.xml"
$sitemapResponse = Invoke-WebRequest -Uri $sitemapUrl -UseBasicParsing -TimeoutSec 30
[xml]$sitemap = $sitemapResponse.Content
$sitemapUrls = @($sitemap.urlset.url | ForEach-Object { [string]$_.loc })
$duplicateUrls = @(
    $sitemapUrls |
        Group-Object |
        Where-Object { $_.Count -gt 1 } |
        ForEach-Object { $_.Name }
)
$missingExpected = @(
    $expectedPaths |
        ForEach-Object { "$canonicalBase$_" } |
        Where-Object { $_ -notin $sitemapUrls }
)

$sampleUrls = @(
    $expectedPaths | ForEach-Object {
        [pscustomobject]@{
            FetchUrl = "$base$_"
            ExpectedCanonical = "$canonicalBase$_"
        }
    }
)
$pageChecks = @(
    $sampleUrls |
        ForEach-Object {
            Get-PageCheck -Url $_.FetchUrl -ExpectedCanonical $_.ExpectedCanonical
        }
)

$failures = @(
    $pageChecks |
        Where-Object {
            $_.Status -ne 200 -or
            $_.Noindex -or
            [string]::IsNullOrWhiteSpace($_.Canonical) -or
            $_.Canonical.TrimEnd("/") -ne $_.ExpectedCanonical.TrimEnd("/")
        }
)

$summary = [ordered]@{
    checkedAtUtc = [DateTime]::UtcNow.ToString("o")
    baseUrl = $base
    canonicalBaseUrl = $canonicalBase
    sitemapStatus = [int]$sitemapResponse.StatusCode
    sitemapUrlCount = $sitemapUrls.Count
    duplicateUrlCount = $duplicateUrls.Count
    missingExpectedCount = $missingExpected.Count
    sampledPageCount = $pageChecks.Count
    failedPageCount = $failures.Count
    duplicateUrls = $duplicateUrls
    missingExpectedUrls = $missingExpected
    passed = $duplicateUrls.Count -eq 0 -and $missingExpected.Count -eq 0 -and $failures.Count -eq 0
}

$pageChecks | Export-Csv -NoTypeInformation -Encoding UTF8 -Path (Join-Path $resolvedOutput "page-checks.csv")
$summary | ConvertTo-Json -Depth 5 | Set-Content -Encoding UTF8 -Path (Join-Path $resolvedOutput "summary.json")

$summary | ConvertTo-Json -Depth 5
if (-not $summary.passed) {
    exit 1
}
