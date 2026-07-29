$res = Invoke-RestMethod -Method GET -Uri 'https://api.render.com/v1/services' -Headers @{ Authorization = 'Bearer rnd_DLMpqCyWZZkPxfIa9UE1yVtUkvC2' }
$service = $res | Where-Object { $_.service.name -eq 'hedge-fund-prod-node' } | Select-Object -ExpandProperty service
$deploys = Invoke-RestMethod -Method GET -Uri "https://api.render.com/v1/services/$($service.id)/deploys" -Headers @{ Authorization = 'Bearer rnd_DLMpqCyWZZkPxfIa9UE1yVtUkvC2' }
$deploys[0].deploy | Select-Object id, status | ConvertTo-Json
