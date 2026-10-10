$ErrorActionPreference = "Stop"
$project = "R:\kavimart"
$tomcat  = "C:\Users\chinn\Downloads\apache-tomcat-9.0.122"

Write-Host "1. Checking Java and Maven..." -ForegroundColor Cyan
java -version
mvn -v

Write-Host "2. Checking ports 8080 and 8005..." -ForegroundColor Cyan
$busy = Get-NetTCPConnection -LocalPort 8080,8005 -ErrorAction SilentlyContinue
if ($busy) {
    Write-Host "Port is busy. Close the old Tomcat window first, then run this script again." -ForegroundColor Red
    $busy | Select-Object LocalPort, State, OwningProcess
    exit
}

Write-Host "3. Building the WAR (this takes 1-3 minutes)..." -ForegroundColor Cyan
Set-Location $project
mvn clean package -DskipTests

$war = Get-ChildItem "$project\target\*.war" | Select-Object -First 1
if (-not $war) { Write-Host "WAR file not found in target folder." -ForegroundColor Red; exit }
Write-Host "WAR built: $($war.FullName)" -ForegroundColor Green

Write-Host "4. Cleaning old deployment..." -ForegroundColor Cyan
Remove-Item "$tomcat\webapps\kavimart" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item "$tomcat\webapps\kavimart.war" -Force -ErrorAction SilentlyContinue
Remove-Item "$tomcat\work\Catalina\localhost\kavimart" -Recurse -Force -ErrorAction SilentlyContinue

Write-Host "5. Copying WAR to Tomcat..." -ForegroundColor Cyan
Copy-Item $war.FullName "$tomcat\webapps\kavimart.war"

Write-Host "6. Starting Tomcat..." -ForegroundColor Cyan
Start-Process "$tomcat\bin\startup.bat" -WorkingDirectory "$tomcat\bin"

Write-Host "7. Waiting 30 seconds for the app to start..." -ForegroundColor Cyan
Start-Sleep -Seconds 30
try {
    $r = Invoke-WebRequest "http://localhost:8080/kavimart/" -UseBasicParsing
    Write-Host "SUCCESS. Status code: $($r.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "App did not respond yet. Check the Tomcat window for errors." -ForegroundColor Yellow
}
