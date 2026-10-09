param(
    [ValidateSet('blog', 'uaa')][string[]]$Services = @('uaa', 'blog'),
    [string]$Distribution = 'Ubuntu-24.04',
    [switch]$Build,
    [switch]$Restart,
    [switch]$WithoutObservability,
    [ValidateRange(0, 1)][double]$Sampling = 0.1,
    [int]$BlogDebugPort = 5005,
    [int]$UaaDebugPort = 5006
)
$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
if ($Build) {
    $tasks = @($Services | ForEach-Object { ":kuma-project:kuma-project-${_}:bootJar" })
    & (Join-Path $repoRoot 'gradlew.bat') -p $repoRoot @tasks --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Service build failed.' }
}
$routeJson = & wsl -d $Distribution -- ip -j -4 route show default
if ($LASTEXITCODE -ne 0) { throw 'Cannot read WSL route.' }
$windowsAddress = @($routeJson | ConvertFrom-Json)[0].gateway
$addressJson = & wsl -d $Distribution -- ip -j -4 addr show eth0
if ($LASTEXITCODE -ne 0) { throw 'Cannot read WSL middleware address.' }
$wslAddress = @((@($addressJson | ConvertFrom-Json)[0].addr_info) | Where-Object scope -eq 'global')[0].local
if (!$windowsAddress -or !$wslAddress) { throw 'No WSL NAT addresses found.' }

foreach ($service in $Services) {
    $port = if ($service -eq 'blog') { 9000 } else { 33336 }
    $managementPort = if ($service -eq 'blog') { 19001 } else { 19002 }
    $debugPort = if ($service -eq 'blog') { $BlogDebugPort } else { $UaaDebugPort }
    $database = if ($service -eq 'blog') { 'base' } else { 'kuma_uaa' }
    $serviceRoot = Join-Path $repoRoot "kuma-project/kuma-project-$service"
    $jarPath = Join-Path $serviceRoot "build/libs/$service-2026.10.jar"
    $localConfig = Join-Path $serviceRoot 'src/main/resources/bootstrap-local.yml'
    $logDirectory = Join-Path $repoRoot "logs/$service-local"
    $pidPath = Join-Path $logDirectory 'process-id.txt'
    if (!(Test-Path -LiteralPath $jarPath)) { throw "Build $service first, or pass -Build." }
    if (!(Test-Path -LiteralPath $localConfig)) { throw "Configure $service bootstrap-local.yml first." }
    if ($Restart -and (Test-Path -LiteralPath $pidPath)) {
        $previousId = [int](Get-Content -LiteralPath $pidPath)
        $previous = Get-CimInstance Win32_Process -Filter "ProcessId=$previousId"
        if ($previous -and $previous.Name -eq 'java.exe' -and
            ($previous.CommandLine -like "*$service-2026.10.jar*" -or $previous.CommandLine -like "*$service-local*runtime*")) {
            Stop-Process -Id $previousId
            Wait-Process -Id $previousId -Timeout 15 -ErrorAction SilentlyContinue
        } elseif ($previous) { throw "PID $previousId belongs to a different process; refusing to stop it." }
    }
    $requiredPorts = @($port)
    if (!$WithoutObservability) { $requiredPorts += $managementPort }
    if ($debugPort -gt 0) { $requiredPorts += $debugPort }
    foreach ($requiredPort in $requiredPorts) {
        if (Get-NetTCPConnection -State Listen -LocalPort $requiredPort -ErrorAction SilentlyContinue) {
            throw "Port $requiredPort is in use. Stop that instance or use -Restart for a script-managed instance."
        }
    }
    New-Item -ItemType Directory -Path $logDirectory -Force | Out-Null
    $runtimeDirectory = Join-Path $logDirectory 'runtime'
    New-Item -ItemType Directory -Path $runtimeDirectory -Force | Out-Null
    $jarHash = (Get-FileHash -LiteralPath $jarPath -Algorithm SHA256).Hash
    $runtimeJar = Join-Path $runtimeDirectory "$jarHash.jar"
    if (!(Test-Path -LiteralPath $runtimeJar)) { Copy-Item -LiteralPath $jarPath -Destination $runtimeJar }
    if ((Get-FileHash -LiteralPath $runtimeJar -Algorithm SHA256).Hash -ne $jarHash) {
        throw 'Jar changed while copying. Finish the build and retry.'
    }
    $arguments = @('--enable-preview', '--enable-native-access=ALL-UNNAMED', '-Xmx768m')
    if ($debugPort -gt 0) { $arguments += "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:$debugPort" }
    if (!$WithoutObservability) { $arguments += '-Dlogging.config=classpath:logback-observability.xml' }
    $configUri = 'file:' + ($localConfig -replace '\\', '/')
    $profile = if ($WithoutObservability) { 'dev' } else { 'dev,observability' }
    $arguments += @('-jar', ('"' + $runtimeJar + '"'), "--spring.profiles.active=$profile",
        '--KUMA_CONFIG_PROFILE=dev', ('"--spring.config.additional-location=' + $configUri + '"'),
        "--server.port=$port", '--server.address=0.0.0.0',
        "--SPRING_CLOUD_NACOS_SERVER_ADDR=${wslAddress}:8848", "--spring.cloud.nacos.discovery.ip=$windowsAddress",
        "--spring.data.redis.host=$wslAddress",
        "--spring.datasource.dynamic.datasource.mysql.url=jdbc:mysql://${wslAddress}:3306/${database}?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true&preserveInstants=true&connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true")
    if ($service -eq 'blog') { $arguments += "--kuma.boot.ai.qdrant.host=$wslAddress" }
    if (!$WithoutObservability) {
        # CLI takes precedence over Nacos settings, including logging.config and tracing.enabled.
        $arguments += @('--logging.config=classpath:logback-observability.xml',
            "--management.server.port=$managementPort", "--management.server.address=$windowsAddress",
            '--management.endpoints.web.exposure.include=health,info,prometheus',
            '--management.endpoint.health.show-details=never', '--management.tracing.enabled=true',
            "--management.tracing.sampling.probability=$Sampling",
            '--management.opentelemetry.tracing.export.otlp.endpoint=http://localhost:4318/v1/traces',
            '--management.otlp.metrics.export.enabled=false', '--observability.environment=wsl',
            '--management.metrics.tags.environment=wsl',
            '--kuma.boot.monitor.enabled=false', '--kuma.boot.web.request.enabled=false',
            '--atlas.log.enabled=false', '--logging.access.enabled=false',
            '--kuma.boot.logger.logging.files.enabled=false')
    }
    $process = Start-Process -FilePath (Get-Command java).Source -ArgumentList $arguments `
        -WorkingDirectory $repoRoot -WindowStyle Hidden -PassThru `
        -RedirectStandardOutput (Join-Path $logDirectory 'stdout.log') `
        -RedirectStandardError (Join-Path $logDirectory 'stderr.log')
    $process.Id | Set-Content -LiteralPath $pidPath
    Write-Output "$service PID: $($process.Id); discovery: ${windowsAddress}:$port; debug: localhost:$debugPort"
    if (!$WithoutObservability) { Write-Output "Metrics: http://${windowsAddress}:$managementPort/actuator/prometheus" }
    Write-Output "Logs: $logDirectory"
}
if (!$WithoutObservability) {
    $driveLetter = $repoRoot.Substring(0, 1).ToLowerInvariant()
    $linuxScript = "/mnt/$driveLetter" + ($repoRoot.Substring(2) -replace '\\', '/')
    & wsl -d $Distribution -u root -- python3 "$linuxScript/scripts/register-services-metrics-wsl.py" $windowsAddress
    if ($LASTEXITCODE -ne 0) { throw 'Services started, but metrics registration failed.' }
}
