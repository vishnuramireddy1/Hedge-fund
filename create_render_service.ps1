$RenderToken = 'rnd_DLMpqCyWZZkPxfIa9UE1yVtUkvC2'
$serviceBody = @{
    name = "hedge-fund-prod-node"
    ownerId = "tea-d9ktdarl550s73f7901g"
    type = "web_service"
    repo = "https://github.com/vishnuramireddy1/Hedge-fund"
    autoDeploy = "yes"
    serviceDetails = @{
        env = "node"
        region = "oregon"
        plan = "free"
        envSpecificDetails = @{
            buildCommand = "npm install"
            startCommand = "npm start"
        }
        envVars = @(
            @{ key = "PORT"; value = "3000" }
            @{ key = "FIREBASE_API_KEY"; value = "AQ.Ab8RN6KR4abc2Y64cD6gkVvuNZYaOS4vXI9Z4BJsIySbjHTF1Q" }
            @{ key = "ALPHA_VANTAGE_KEY"; value = "DUMMY" }
            @{ key = "TOOL_FRAMEWORK"; value = "google_adk" }
            @{ key = "SYNTHESIZE_PARENT"; value = "true" }
        )
    }
} | ConvertTo-Json -Depth 10

Write-Host "Creating Render service..."
try {
    $serviceResponse = Invoke-RestMethod -Method POST -Uri 'https://api.render.com/v1/services' -Headers @{ Authorization = "Bearer $RenderToken" } -ContentType 'application/json' -Body $serviceBody
    $serviceId = $serviceResponse.service.id
    Write-Host "Success! ID: $serviceId"
    
    $publicUrl = $serviceResponse.service.serviceDetails.url
    Write-Host "URL: $publicUrl"

    Write-Host "Waiting for build... this will take a few minutes."
    $deployUrl = "https://api.render.com/v1/services/$serviceId/deploys"
    
    # Render automatically triggers deploy on creation, so we just get the latest deploy
    $deployId = $serviceResponse.deployId

    while ($true) {
        $status = Invoke-RestMethod -Method GET -Uri "$deployUrl/$deployId" -Headers @{ Authorization = "Bearer $RenderToken" }
        if ($status.status -eq "failed") {
            Write-Error "Deploy failed."
            exit 1
        }
        if ($status.status -eq "live" -or $status.status -eq "succeeded") { break }
        Write-Host "Status: $($status.status)..."
        Start-Sleep -Seconds 15
    }
    Write-Host "Deployment is Live! $publicUrl"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    if ($_.ErrorDetails) {
        Write-Host "Details: $($_.ErrorDetails.Message)"
    }
}
