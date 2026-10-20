function Get-KumaServiceTracingJvmArguments {
    param(
        [Parameter(Mandatory)][string]$RepoRoot,
        [Parameter(Mandatory)][ValidateSet('blog', 'uaa', 'gateway')][string]$Service,
        [string]$AgentJar = $env:SKYWALKING_AGENT_JAR,
        [string]$Backend = 'localhost:11800'
    )
    if (-not $AgentJar) {
        $AgentJar = Join-Path $RepoRoot 'kuma-project/kuma-project-lab/build/tools/skywalking-agent/skywalking-agent.jar'
    } elseif (-not [IO.Path]::IsPathRooted($AgentJar)) {
        $AgentJar = Join-Path $RepoRoot $AgentJar
    }
    if (-not (Test-Path -LiteralPath $AgentJar -PathType Leaf)) {
        throw 'SkyWalking Agent missing. Set SKYWALKING_AGENT_JAR or -SkywalkingAgent to an extracted Agent JAR.'
    }
    $AgentJar = (Resolve-Path -LiteralPath $AgentJar).Path
    $agentDirectory = Split-Path -Parent $AgentJar
    foreach ($directory in @('config', 'plugins', 'activations')) {
        if (-not (Test-Path -LiteralPath (Join-Path $agentDirectory $directory) -PathType Container)) {
            throw "SkyWalking Agent installation is incomplete: $directory is missing."
        }
    }
    if ($AgentJar.Contains('"') -or $Backend -notmatch '^[A-Za-z0-9._:-]+(,[A-Za-z0-9._:-]+)*$') {
        throw 'Invalid SkyWalking Agent path or backend address.'
    }
    $loggingDirectory = Join-Path $RepoRoot "logs/$Service-local/skywalking"
    @(
        ('"-javaagent:' + $AgentJar + '"'),
        "-Dskywalking.agent.service_name=kuma-cloud-$Service",
        "-Dskywalking.collector.backend_service=$Backend",
        '-Dskywalking.logging.level=INFO',
        ('"-Dskywalking.logging.dir=' + $loggingDirectory + '"')
    )
}
