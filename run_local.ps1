# LearnLoop Backend Local Runner Script

$MavenDir = "$PSScriptRoot\maven"
$MavenZip = "$PSScriptRoot\maven.zip"
$MvnPath = "$MavenDir\apache-maven-3.9.6\bin\mvn.cmd"

# Check if global mvn command is available
if (Get-Command "mvn" -ErrorAction SilentlyContinue) {
    Write-Host "[LearnLoop] Global Maven detected. Using system Maven..." -ForegroundColor Green
    mvn clean spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=h2"
} else {
    # Check if local Maven exists
    if (-not (Test-Path $MvnPath)) {
        Write-Host "[LearnLoop] Global Maven not detected. Downloading portable Apache Maven 3.9.6..." -ForegroundColor Cyan
        New-Item -ItemType Directory -Force -Path $MavenDir | Out-Null
        
        $Uri = "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
        Write-Host "Downloading Maven from $Uri..." -ForegroundColor Gray
        Invoke-WebRequest -Uri $Uri -OutFile $MavenZip
        
        Write-Host "Extracting Maven archive..." -ForegroundColor Gray
        Expand-Archive -Path $MavenZip -DestinationPath $MavenDir -Force
        
        Remove-Item -Path $MavenZip -Force
        Write-Host "Maven set up completed locally." -ForegroundColor Green
    }
    
    Write-Host "[LearnLoop] Launching Spring Boot backend (with H2 in-memory profile)..." -ForegroundColor Green
    & $MvnPath clean spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=h2"
}
