param([string]$Url, [int]$Concurrency = 10, [int]$Seconds = 60)
Add-Type -AssemblyName System.Net.Http
$lat = New-Object 'System.Collections.Concurrent.ConcurrentBag[double]'
$err = New-Object 'System.Collections.Concurrent.ConcurrentBag[int]'
$byt = New-Object 'System.Collections.Concurrent.ConcurrentBag[long]'
$worker = {
  param($Url, $End, $lat, $err, $byt)
  Add-Type -AssemblyName System.Net.Http
  $c = New-Object System.Net.Http.HttpClient
  $c.Timeout = [TimeSpan]::FromSeconds(30)
  $sw = New-Object System.Diagnostics.Stopwatch
  while ([DateTime]::UtcNow -lt $End) {
    $sw.Restart()
    try {
      $r = $c.GetAsync($Url).Result
      $b = $r.Content.ReadAsByteArrayAsync().Result
      $sw.Stop()
      if ($r.IsSuccessStatusCode) { $lat.Add($sw.Elapsed.TotalMilliseconds); $byt.Add([long]$b.Length) } else { $err.Add(1) }
    } catch { $err.Add(1) }
  }
  $c.Dispose()
}
Write-Host "Testing $Url with $Concurrency users for $Seconds seconds..." -ForegroundColor Cyan
$total = [System.Diagnostics.Stopwatch]::StartNew()
$end = [DateTime]::UtcNow.AddSeconds($Seconds)
$pool = [runspacefactory]::CreateRunspacePool(1, $Concurrency)
$pool.Open()
$jobs = @()
for ($i = 0; $i -lt $Concurrency; $i++) {
  $ps = [powershell]::Create()
  $ps.RunspacePool = $pool
  [void]$ps.AddScript($worker).AddArgument($Url).AddArgument($end).AddArgument($lat).AddArgument($err).AddArgument($byt)
  $jobs += [pscustomobject]@{ PS = $ps; Handle = $ps.BeginInvoke() }
}
foreach ($j in $jobs) { $j.PS.EndInvoke($j.Handle); $j.PS.Dispose() }
$total.Stop()
$pool.Close()
$s = $lat.ToArray()
[Array]::Sort($s)
$n = $s.Length
if ($n -eq 0) { Write-Host "No successful requests. Is Tomcat running?" -ForegroundColor Red; exit }
function Pct($p) { $s[[Math]::Min($n - 1, [int][Math]::Ceiling($n * $p / 100) - 1)] }
$secs = $total.Elapsed.TotalSeconds
$mean = ($s | Measure-Object -Average).Average
$kb = (($byt.ToArray() | Measure-Object -Sum).Sum) / 1024
Write-Host ""
Write-Host "Concurrency Level            : $Concurrency"
Write-Host ("Time taken for tests (s)     : {0:N2}" -f $secs)
Write-Host "Complete requests            : $n"
Write-Host "Failed requests              : $($err.Count)"
Write-Host ("Requests per second          : {0:N2}" -f ($n / $secs))
Write-Host ("Time per request mean (ms)   : {0:N2}" -f $mean)
Write-Host ("50th percentile (ms)         : {0:N2}" -f (Pct 50))
Write-Host ("95th percentile (ms)         : {0:N2}" -f (Pct 95))
Write-Host ("99th percentile (ms)         : {0:N2}" -f (Pct 99))
Write-Host ("Transfer rate (Kbytes/sec)   : {0:N2}" -f ($kb / $secs))
