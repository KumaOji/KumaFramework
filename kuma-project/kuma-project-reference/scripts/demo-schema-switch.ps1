param([string]$BaseUrl = 'http://127.0.0.1:19092')
$ErrorActionPreference = 'Stop'
$BaseUrl = $BaseUrl.TrimEnd('/')

Write-Host '1. Create an order in PostgreSQL MAIN'
$mainOrder = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/orders?scope=MAIN" -ContentType 'application/json' -Body '{"sku":"BOOK-001","productName":"Sample product","unitPrice":99.00,"quantity":1}'
$read = Invoke-RestMethod "$BaseUrl/api/orders/$($mainOrder.id)?scope=MAIN"
if ($read.id -ne $mainOrder.id) { throw 'MAIN order lookup failed.' }

Write-Host '2. The same order ID must return 404 in MySQL'
try {
    Invoke-RestMethod "$BaseUrl/api/orders/$($mainOrder.id)?scope=MYSQL" | Out-Null
    throw 'Isolation failed: MAIN order was visible in MYSQL.'
} catch {
    if ($null -eq $_.Exception.Response -or [int]$_.Exception.Response.StatusCode -ne 404) { throw }
    Write-Host 'PASS: MYSQL returned 404 for the MAIN order.'
}

Write-Host '3. Create and confirm an independent MySQL order'
$mysqlOrder = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/orders?scope=MYSQL" -ContentType 'application/json' -Body '{"sku":"BOOK-001","productName":"Sample product","unitPrice":99.00,"quantity":2}'
$confirmed = Invoke-RestMethod -Method Post "$BaseUrl/api/orders/$($mysqlOrder.id)/confirm?scope=MYSQL"
if ($confirmed.status -ne 'CONFIRMED') { throw 'MYSQL confirmation failed.' }
@($mainOrder, $confirmed) | Format-Table id, sku, quantity, totalAmount, status, version

Write-Host '4. Query with QO parameters and inspect PageVO / OrderVO'
Invoke-RestMethod "$BaseUrl/api/orders?scope=MAIN&sku=BOOK-001&status=CREATED&page=1&size=3" | ConvertTo-Json -Depth 5
Write-Host 'PASS: isolated PostgreSQL/MySQL routing, command/query flow and QO/VO pagination.'
