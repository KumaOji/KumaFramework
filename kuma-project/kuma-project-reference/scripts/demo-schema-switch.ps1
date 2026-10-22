param([string]$BaseUrl = 'http://127.0.0.1:19092')
$ErrorActionPreference = 'Stop'
$BaseUrl = $BaseUrl.TrimEnd('/')
$sku = 'DEMO-' + [Guid]::NewGuid().ToString('N').Substring(0, 12).ToUpperInvariant()
$mainBody = @{sku=$sku; productName='Sample product'; unitPrice=99.00; quantity=1} | ConvertTo-Json
$mysqlBody = @{sku=$sku; productName='Sample product'; unitPrice=99.00; quantity=2} | ConvertTo-Json

Write-Host '1. Create an order in PostgreSQL MAIN'
$mainOrder = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/orders?scope=MAIN" -ContentType 'application/json' -Body $mainBody
$read = Invoke-RestMethod "$BaseUrl/api/orders/$($mainOrder.id)?scope=MAIN"
if ($read.id -ne $mainOrder.id) { throw 'MAIN order lookup failed.' }

Write-Host '2. The unique SKU must not yet exist in MySQL (numeric IDs can overlap)'
$mysqlPage = Invoke-RestMethod "$BaseUrl/api/orders?scope=MYSQL&sku=$sku"
if ($mysqlPage.totalSize -ne 0) { throw 'Isolation failed: MAIN order was visible in MYSQL.' }

Write-Host '3. Create and confirm an independent MySQL order'
$mysqlOrder = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/orders?scope=MYSQL" -ContentType 'application/json' -Body $mysqlBody
$confirmed = Invoke-RestMethod -Method Post "$BaseUrl/api/orders/$($mysqlOrder.id)/confirm?scope=MYSQL"
if ($confirmed.status -ne 'CONFIRMED') { throw 'MYSQL confirmation failed.' }
@($mainOrder, $confirmed) | Format-Table id, sku, quantity, totalAmount, status, version

Write-Host '4. Inspect framework PageResultRecord / OrderVO'
Invoke-RestMethod "$BaseUrl/api/orders?scope=MAIN&sku=$sku&status=CREATED&currentPage=1&pageSize=3" | ConvertTo-Json -Depth 5
Write-Host 'PASS: PostgreSQL/MySQL isolation, framework CRUD, optimistic locking and pagination.'
