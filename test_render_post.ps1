$RenderToken = 'rnd_DLMpqCyWZZkPxfIa9UE1yVtUkvC2'
$serviceBody = @{
    name = "hedge-fund-prod-2"
    ownerId = "tea-d9ktdarl550s73f7901g"
    type = "web_service"
    repo = "https://github.com/vishnuramireddy1/Hedge-fund"
    autoDeploy = "yes"
    serviceDetails = @{
        env = "docker"
        region = "oregon"
        plan = "free"
        envSpecificDetails = @{
            dockerfilePath = "Dockerfile"
            dockerCommand = "npm start"
        }
        envVars = @(
            @{ key = "PORT"; value = "3000" }
        )
    }
} | ConvertTo-Json -Depth 10

try {
    $serviceResponse = Invoke-RestMethod -Method POST -Uri 'https://api.render.com/v1/services' -Headers @{ Authorization = "Bearer $RenderToken" } -ContentType 'application/json' -Body $serviceBody
    Write-Host "RESPONSE JSON:"
    $serviceResponse | ConvertTo-Json -Depth 5
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.ErrorDetails) {
        Write-Host "Details: $($_.ErrorDetails.Message)"
    }
}
