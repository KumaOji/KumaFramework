param(
    [ValidateSet('blog', 'uaa', 'gateway')][string[]]$Services = @('uaa', 'blog'),
    [string]$Distribution = 'Ubuntu-24.04',
    [switch]$Build,
    [switch]$Restart,
    [switch]$WithoutObservability,
    [switch]$WithoutSkywalking,
    [string]$SkywalkingAgent = $env:SKYWALKING_AGENT_JAR,
    [string]$SkywalkingBackend = 'localhost:11800',
    [ValidateRange(0, 1)][double]$Sampling = 0.1,
    [int]$BlogDebugPort = 5005,
    [int]$UaaDebugPort = 5006,
    [int]$GatewayDebugPort = 0
)
$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
. (Join-Path $PSScriptRoot 'service-tracing.ps1')
. (Join-Path $PSScriptRoot 'service-wsl.ps1')
$tracingArguments = @{}
if (!$WithoutObservability -and !$WithoutSkywalking) {
    foreach ($service in $Services) {
        $tracingArguments[$service] = @(Get-KumaServiceTracingJvmArguments -RepoRoot $repoRoot -Service $service `
            -AgentJar $SkywalkingAgent -Backend $SkywalkingBackend)
    }
}
if ($Build) {
    $tasks = @($Services | ForEach-Object { ":kuma-project:kuma-project-${_}:bootJar" })
    & (Join-Path $repoRoot 'gradlew.bat') -p $repoRoot @tasks --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Service build failed.' }
}
$routeJson = Invoke-KumaServiceWsl -Arguments @('-d', $Distribution, '--exec', 'ip', '-j', '-4', 'route', 'show', 'default')
$windowsAddress = @($routeJson | ConvertFrom-Json)[0].gateway
$addressJson = Invoke-KumaServiceWsl -Arguments @('-d', $Distribution, '--exec', 'ip', '-j', '-4', 'addr', 'show', 'eth0')
$wslAddress = @((@($addressJson | ConvertFrom-Json)[0].addr_info) | Where-Object scope -eq 'global')[0].local
if (!$windowsAddress -or !$wslAddress) { throw 'No WSL NAT addresses found.' }

foreach ($service in $Services) {
    $port = switch ($service) { 'blog' { 9000 } 'uaa' { 33336 } 'gateway' { 18080 } }
    $managementPort = switch ($service) { 'blog' { 19001 } 'uaa' { 19002 } 'gateway' { 18081 } }
    $debugPort = switch ($service) { 'blog' { $BlogDebugPort } 'uaa' { $UaaDebugPort } 'gateway' { $GatewayDebugPort } }
    $loggingConfig = if ($service -eq 'gateway') { 'classpath:logback-gateway.xml' } else { 'classpath:logback-observability.xml' }
    $managementAddress = if ($service -eq 'gateway') { '0.0.0.0' } else { $windowsAddress }
    $database = if ($service -eq 'blog') { 'base' } else { 'kuma_uaa' }
    $serviceRoot = Join-Path $repoRoot "kuma-project/kuma-project-$service"
    $jarDirectory = Join-Path $serviceRoot 'build/libs'
    $jarFile = if (Test-Path -LiteralPath $jarDirectory) {
        Get-ChildItem -LiteralPath $jarDirectory -Filter "$service-*.jar" -File |
            Where-Object { $_.Name -notmatch '-(plain|sources)\.jar$' } |
            Sort-Object LastWriteTime -Descending | Select-Object -First 1
    } else { $null }
    $jarPath = if ($jarFile) { $jarFile.FullName } else { Join-Path $jarDirectory "$service.jar" }
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
    $arguments = @('--enable-preview', '--enable-native-access=ALL-UNNAMED', '-Xmx768m',
        '-Dfile.encoding=UTF-8', '-Duser.timezone=Asia/Hong_Kong')
    if ($tracingArguments.ContainsKey($service)) { $arguments += $tracingArguments[$service] }
    if ($debugPort -gt 0) { $arguments += "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:$debugPort" }
    if (!$WithoutObservability) { $arguments += "-Dlogging.config=$loggingConfig" }
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
        $arguments += @("--logging.config=$loggingConfig",
            "--management.server.port=$managementPort", "--management.server.address=$managementAddress",
            '--management.endpoints.web.exposure.include=health,info,prometheus',
            '--management.endpoint.health.show-details=never', '--management.tracing.enabled=true',
            "--management.tracing.sampling.probability=$Sampling",
            '--management.opentelemetry.tracing.export.otlp.endpoint=http://localhost:4318/v1/traces',
            '--management.otlp.metrics.export.enabled=false', '--observability.environment=wsl',
            '--management.metrics.tags.environment=wsl',
            '--kuma.boot.monitor.enabled=false', '--kuma.boot.web.request.enabled=false',
            '--atlas.log.enabled=false', '--logging.access.enabled=false',
            '--kuma.boot.web.filter.trace=true', '--kuma.boot.web.filter.tenant=true', '--kuma.boot.web.filter.version=true',
            '--kuma.boot.logger.logging.files.enabled=false')
    }
    if ($service -eq 'gateway') {
        $arguments += @('--kuma.boot.ip2region.enabled=false', '--management.health.redis.enabled=false',
            '--gateway.environment=local', '--management.metrics.tags.environment=local',
            '--gateway.loki-url=http://localhost:3100/loki/api/v1/push')
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
    Invoke-KumaServiceWsl -Arguments @('-d', $Distribution, '-u', 'root', '--exec', 'python3',
        "$linuxScript/scripts/register-services-metrics-wsl.py", $windowsAddress)
}
