function Invoke-ReferenceWsl {
    param([string[]]$Arguments)
    # WSL may print a harmless proxy warning to stderr; Windows PowerShell 5
    # otherwise treats that warning as a terminating NativeCommandError.
    foreach ($argument in $Arguments) {
        if ($argument.Contains('"') -or $argument.EndsWith('\')) { throw 'Unsupported WSL argument quoting.' }
    }
    $info = New-Object System.Diagnostics.ProcessStartInfo
    $info.FileName = 'wsl.exe'
    $info.Arguments = ($Arguments | ForEach-Object { if ($_ -match '\s') { '"' + $_ + '"' } else { $_ } }) -join ' '
    $info.UseShellExecute = $false
    $info.CreateNoWindow = $true
    $info.RedirectStandardOutput = $true
    $info.RedirectStandardError = $true
    $info.StandardOutputEncoding = [Text.Encoding]::UTF8
    $process = New-Object System.Diagnostics.Process
    $process.StartInfo = $info
    try {
        $process.Start() | Out-Null
        $errorRead = $process.StandardError.ReadToEndAsync()
        $outputText = $process.StandardOutput.ReadToEnd()
        $process.WaitForExit()
        $errorRead.GetAwaiter().GetResult() | Out-Null
        if ($process.ExitCode -ne 0) { throw "WSL command failed (exit $($process.ExitCode)); credentials and command output omitted." }
        return $outputText.Trim()
    } finally { $process.Dispose() }
}
