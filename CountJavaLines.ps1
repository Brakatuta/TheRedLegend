# Get all .java files recursively from the current directory
$javaFiles = Get-ChildItem -Path . -Recurse -Filter *.java -File

# Initialize counters
$totalLines = 0
$fileCount = 0

foreach ($file in $javaFiles) {
    $lineCount = (Get-Content $file.FullName | Measure-Object -Line).Lines
    $totalLines += $lineCount
    $fileCount++
    Write-Output "$($file.FullName): $lineCount lines"
}

Write-Host "`nTotal .java files: $fileCount"
Write-Host "Total lines of code: $totalLines"
