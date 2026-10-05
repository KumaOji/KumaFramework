param(
    [ValidateSet('all','8','9','10','11','12','14','15','16','17','18','21','22','24','25')]
    [string]$Version = 'all',
    [switch]$IncludePreview
)
$ErrorActionPreference = 'Stop'
if ($IncludePreview -and $Version -notin @('all', '25')) {
    throw 'IncludePreview applies to JDK25; select all or 25.'
}
$labRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$classes = Join-Path $labRoot 'build/jdk-version-lab/classes'
New-Item -ItemType Directory -Force -Path $classes | Out-Null
$sources = Get-ChildItem -LiteralPath (Join-Path $labRoot 'src/main/java/com/kuma/cloud/lab/jdk') -Filter '*.java' |
    ForEach-Object FullName
& javac --release 25 -encoding UTF-8 -Xlint:all -d $classes @sources
if ($LASTEXITCODE -ne 0) { throw 'JDK25 compilation failed.' }
& java -cp $classes com.kuma.cloud.lab.jdk.JdkVersionLearningDemo $Version
if ($LASTEXITCODE -ne 0) { throw 'JDK feature experiment failed.' }
if ($Version -in @('all', '25')) {
    & java --source 25 (Join-Path $labRoot 'examples/jdk25/CompactMain.java')
    if ($LASTEXITCODE -ne 0) { throw 'Compact source experiment failed.' }
}
if ($IncludePreview) {
    $previewClasses = Join-Path $labRoot 'build/jdk-version-lab/preview'
    New-Item -ItemType Directory -Force -Path $previewClasses | Out-Null
    & javac --enable-preview --release 25 -encoding UTF-8 -Xlint:preview -d $previewClasses `
        (Join-Path $labRoot 'examples/jdk25-preview/PrimitivePatterns.java')
    if ($LASTEXITCODE -ne 0) { throw 'JDK25 preview compilation failed.' }
    & java --enable-preview -cp $previewClasses PrimitivePatterns
    if ($LASTEXITCODE -ne 0) { throw 'JDK25 preview experiment failed.' }
}
