# Picture Viewer Windows Release Builder
# Runs on Windows to create Windows native installers

Write-Host "Picture Viewer Windows Release Builder" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan

# Clean previous builds
Write-Host "Cleaning previous builds..." -ForegroundColor Yellow
mvn clean

# Create fat JAR
Write-Host "Creating fat JAR..." -ForegroundColor Yellow
mvn package

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Failed to create JAR" -ForegroundColor Red
    exit 1
}

# Create releases directory
if (!(Test-Path "releases")) {
    New-Item -ItemType Directory -Path "releases"
}

# Create native Windows packages with jpackage
Write-Host "Creating native Windows packages with jpackage..." -ForegroundColor Yellow

if (Get-Command jpackage -ErrorAction SilentlyContinue) {
    
    # Clean previous native packages
    if (Test-Path "releases\Picture Viewer") {
        Remove-Item -Path "releases\Picture Viewer" -Recurse -Force
    }
    Remove-Item -Path "releases\*.exe" -Force -ErrorAction SilentlyContinue
    Remove-Item -Path "releases\*.msi" -Force -ErrorAction SilentlyContinue
    
    # Create app-image (portable)
    Write-Host "Creating portable app-image..." -ForegroundColor Yellow
    $appImageResult = & jpackage `
        --input target `
        --main-jar picture-viewer-fat.jar `
        --main-class com.angeasla.pictureviewer.Main `
        --name "Picture Viewer" `
        --app-version 1.0 `
        --description "A simple picture viewer application" `
        --vendor "Angeasla" `
        --dest releases `
        --type app-image
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "SUCCESS: Portable app-image created" -ForegroundColor Green
    } else {
        Write-Host "ERROR: Portable app-image failed" -ForegroundColor Red
    }
    
    # Create .exe installer
    Write-Host "Creating .exe installer..." -ForegroundColor Yellow
    $exeResult = & jpackage `
        --input target `
        --main-jar picture-viewer-fat.jar `
        --main-class com.angeasla.pictureviewer.Main `
        --name "Picture Viewer" `
        --app-version 1.0 `
        --description "A simple picture viewer application" `
        --vendor "Angeasla" `
        --dest releases `
        --type exe `
        --win-menu `
        --win-shortcut `
        --win-dir-chooser
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "SUCCESS: .exe installer created" -ForegroundColor Green
    } else {
        Write-Host "ERROR: .exe installer failed" -ForegroundColor Red
    }
    
    # Create .msi installer
    Write-Host "Creating .msi installer..." -ForegroundColor Yellow
    $msiResult = & jpackage `
        --input target `
        --main-jar picture-viewer-fat.jar `
        --main-class com.angeasla.pictureviewer.Main `
        --name "Picture Viewer" `
        --app-version 1.0 `
        --description "A simple picture viewer application" `
        --vendor "Angeasla" `
        --dest releases `
        --type msi `
        --win-menu `
        --win-shortcut `
        --win-dir-chooser
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "SUCCESS: .msi installer created" -ForegroundColor Green
    } else {
        Write-Host "ERROR: .msi installer failed" -ForegroundColor Red
    }
    
    Write-Host ""
    Write-Host "Native Windows packages created:" -ForegroundColor Cyan
    Get-ChildItem -Path "releases" -Include "*.exe", "*.msi", "Picture Viewer" | ForEach-Object {
        Write-Host "  $($_.Name)" -ForegroundColor White
    }
    
} else {
    Write-Host "WARNING: jpackage not found, continuing with JAR-only" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "SUCCESS: Windows build completed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Test the native installers" -ForegroundColor White
Write-Host "2. Distribute the .exe/.msi files for easy installation" -ForegroundColor White
Write-Host "3. The portable app can run without installation" -ForegroundColor White