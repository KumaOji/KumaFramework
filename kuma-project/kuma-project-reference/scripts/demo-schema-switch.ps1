param([string]$BaseUrl = 'http://127.0.0.1:19092')
$ErrorActionPreference = 'Stop'
$BaseUrl = $BaseUrl.TrimEnd('/')

Write-Host '1. Inspect actual PostgreSQL connection state'
$main = Invoke-RestMethod "$BaseUrl/api/datasources/current?scope=MAIN"
$sandbox = Invoke-RestMethod "$BaseUrl/api/datasources/current?scope=SANDBOX"
@($main, $sandbox) | Format-Table scope, dataSource, database, schema, databaseUser, orderCount
if ($main.schema -ne 'kuma_reference_main' -or $sandbox.schema -ne 'kuma_reference_sandbox') { throw 'Unexpected schema routing.' }

Write-Host '2. Create an order in MAIN'
$mainOrder = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/orders?scope=MAIN" -ContentType 'application/json' -Body '{"sku":"BOOK-001","productName":"Sample product","unitPrice":99.00,"quantity":1}'
$read = Invoke-RestMethod "$BaseUrl/api/orders/$($mainOrder.id)?scope=MAIN"
if ($read.id -ne $mainOrder.id) { throw 'MAIN order lookup failed.' }

Write-Host '3. The same order ID must return 404 in SANDBOX'
try {
    Invoke-RestMethod "$BaseUrl/api/orders/$($mainOrder.id)?scope=SANDBOX" | Out-Null
    throw 'Isolation failed: MAIN order was visible in SANDBOX.'
} catch {
    if ($null -eq $_.Exception.Response -or [int]$_.Exception.Response.StatusCode -ne 404) { throw }
    Write-Host 'PASS: SANDBOX returned 404 for the MAIN order.'
}

Write-Host '4. Create and confirm an independent SANDBOX order'
$sandboxOrder = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/orders?scope=SANDBOX" -ContentType 'application/json' -Body '{"sku":"BOOK-001","productName":"Sample product","unitPrice":99.00,"quantity":2}'
$confirmed = Invoke-RestMethod -Method Post "$BaseUrl/api/orders/$($sandboxOrder.id)/confirm?scope=SANDBOX"
if ($confirmed.status -ne 'CONFIRMED') { throw 'SANDBOX confirmation failed.' }
@($mainOrder, $confirmed) | Format-Table id, sku, quantity, totalAmount, status, version

Write-Host '5. Query with QO parameters and inspect PageVO / OrderVO'
Invoke-RestMethod "$BaseUrl/api/orders?scope=MAIN&sku=BOOK-001&status=CREATED&page=1&size=3" | ConvertTo-Json -Depth 5
Write-Host 'PASS: real connection schema selection, isolated orders, command/query flow and QO/VO pagination.'
