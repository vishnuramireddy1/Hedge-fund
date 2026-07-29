Write-Host 'Extracting Node.js...'
Expand-Archive -Path 'node.zip' -DestinationPath 'C:\Users\vvram\AppData\Local\Nodejs' -Force
$nodePath = 'C:\Users\vvram\AppData\Local\Nodejs\node-v20.12.2-win-x64'
Write-Host 'Adding to PATH...'
$userPath = [Environment]::GetEnvironmentVariable('Path', [EnvironmentVariableTarget]::User)
if ($userPath -notmatch [regex]::Escape($nodePath)) {
    [Environment]::SetEnvironmentVariable('Path', $userPath + ';' + $nodePath, [EnvironmentVariableTarget]::User)
}
Remove-Item 'node.zip' -Force
Write-Host 'Done!'
