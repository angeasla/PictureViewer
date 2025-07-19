#!/bin/bash

# Picture Viewer Release Builder
# Creates releases for Linux and Windows

echo "Picture Viewer Release Builder"
echo "=============================="

# Clean previous builds
echo "Cleaning previous builds..."
mvn clean

# Create fat JAR
echo "Creating fat JAR..."
mvn package

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to create JAR"
    exit 1
fi

# Create native packages with jpackage
echo "Creating native packages with jpackage..."
if command -v jpackage &> /dev/null; then
    
    # Clean previous native packages
    rm -rf "releases/Picture Viewer"
    rm -f releases/*.deb releases/*.rpm
    
    # Create app-image (portable)
    echo "Creating portable app-image..."
    if jpackage \
        --input target \
        --main-jar picture-viewer-fat.jar \
        --main-class com.angeasla.pictureviewer.Main \
        --name "Picture Viewer" \
        --app-version 1.0 \
        --description "A simple picture viewer application" \
        --vendor "Angeasla" \
        --dest releases \
        --type app-image; then
        echo "SUCCESS: Portable app-image created"
    else
        echo "ERROR: Portable app-image failed"
    fi
    
    # Create .deb installer for Debian/Ubuntu
    echo "Creating .deb installer..."
    if jpackage \
        --input target \
        --main-jar picture-viewer-fat.jar \
        --main-class com.angeasla.pictureviewer.Main \
        --name "picture-viewer" \
        --app-version 1.0 \
        --description "A simple picture viewer application" \
        --vendor "Angeasla" \
        --dest releases \
        --type deb \
        --linux-menu-group "Graphics" \
        --linux-shortcut; then
        echo "SUCCESS: .deb installer created"
    else
        echo "ERROR: .deb installer failed"
    fi
    
    # Create .rpm installer for Fedora/RHEL (only if supported)
    echo "Creating .rpm installer..."
    if jpackage \
        --input target \
        --main-jar picture-viewer-fat.jar \
        --main-class com.angeasla.pictureviewer.Main \
        --name "picture-viewer" \
        --app-version 1.0 \
        --description "A simple picture viewer application" \
        --vendor "Angeasla" \
        --dest releases \
        --type rpm \
        --linux-menu-group "Graphics" \
        --linux-shortcut 2>/dev/null; then
        echo "SUCCESS: .rpm installer created"
    else
        echo "WARNING: .rpm installer not supported on this system"
    fi
    
    echo ""
    echo "Native packages created:"
    ls -lh releases/Picture\ Viewer/ releases/*.deb releases/*.rpm 2>/dev/null | sed 's/^/  /'
    
else
    echo "WARNING: jpackage not found, continuing with JAR-only"
fi

# Create releases directory
mkdir -p releases

# Copy fat JAR
echo "Copying fat JAR..."
cp target/picture-viewer-fat.jar releases/

# Copy jlink image
echo "Copying jlink runtime..."
if [ -d "target/picture-viewer" ]; then
    cp -r target/picture-viewer releases/picture-viewer-linux
fi

# Create launch scripts
echo "Creating launch scripts..."

# Linux launch script
cat > releases/run-picture-viewer-linux.sh << 'EOF'
#!/bin/bash
# Picture Viewer Launcher for Linux

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ -d "$SCRIPT_DIR/Picture Viewer" ]; then
    echo "Starting Picture Viewer with native runtime..."
    "$SCRIPT_DIR/Picture Viewer/bin/Picture Viewer"
elif [ -d "$SCRIPT_DIR/picture-viewer-linux" ]; then
    echo "Starting Picture Viewer with custom runtime..."
    "$SCRIPT_DIR/picture-viewer-linux/bin/picture-viewer"
else
    echo "ERROR: Runtime directory not found"
    echo "Make sure one of these directories exists:"
    echo "  - 'Picture Viewer' (native runtime)"
    echo "  - 'picture-viewer-linux' (jlink runtime)"
    echo ""
    echo "Alternatively, use: ./run-with-java.sh"
    exit 1
fi
EOF

# Windows batch script
cat > releases/run-picture-viewer-windows.bat << 'EOF'
@echo off
REM Picture Viewer Launcher for Windows

set SCRIPT_DIR=%~dp0

if exist "%SCRIPT_DIR%picture-viewer-windows\bin\picture-viewer.exe" (
    echo Starting Picture Viewer with custom runtime...
    "%SCRIPT_DIR%picture-viewer-windows\bin\picture-viewer.exe"
) else (
    echo ERROR: Runtime directory not found
    echo Make sure the picture-viewer-windows directory exists
    pause
    exit /b 1
)
EOF

# Fallback JAR launcher for systems with Java
cat > releases/run-with-java.sh << 'EOF'
#!/bin/bash
# Fallback launcher for systems with Java installed

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if command -v java &> /dev/null; then
    echo "Starting Picture Viewer with system Java..."
    java -jar "$SCRIPT_DIR/picture-viewer-fat.jar"
else
    echo "ERROR: Java not found on system"
    echo "Install Java or use the native runtime"
    exit 1
fi
EOF

# Windows JAR launcher
cat > releases/run-with-java.bat << 'EOF'
@echo off
REM Fallback launcher for Windows systems with Java

set SCRIPT_DIR=%~dp0

java -version >nul 2>&1
if %errorlevel% == 0 (
    echo Starting Picture Viewer with system Java...
    java -jar "%SCRIPT_DIR%picture-viewer-fat.jar"
) else (
    echo ERROR: Java not found on system
    echo Install Java or use the native runtime
    pause
    exit /b 1
)
EOF

# Make scripts executable
chmod +x releases/run-picture-viewer-linux.sh
chmod +x releases/run-with-java.sh

# Create README for users
cat > releases/README.txt << 'EOF'
Picture Viewer - Release Package
================================

This package contains the Picture Viewer application in various formats:

CONTENTS:
- picture-viewer-fat.jar: Standalone JAR (requires Java 21+)
- Picture Viewer/: Native Linux application (no Java required)
- run-with-java.sh: Launcher for Linux with Java
- run-with-java.bat: Launcher for Windows with Java

EXECUTION:

For Linux:
1. Native application: ./Picture\ Viewer/bin/Picture\ Viewer
2. With Java: ./run-with-java.sh

For Windows:
1. With Java: run-with-java.bat

NOTES:
- The native Linux application does not require Java installation
- The JAR requires Java 21 or newer
- For Windows native application, Windows build is required

RECOMMENDATIONS:
- The native application is larger (~70MB) but easier for users
- The JAR is smaller (~8MB) but requires Java installation
- For maximum compatibility, distribute both options
EOF

echo ""
echo "SUCCESS: Release build completed!"
echo ""
echo "Files are located in the 'releases/' directory"
echo ""
echo "Next steps:"
echo "1. For Windows runtime: Cross-compilation on Windows machine required"
echo "2. Test the launchers on target system"
echo "3. Create ZIP/TAR archives for distribution"
echo ""
echo "For Windows runtime, run on Windows:"
echo "   mvn javafx:jlink"
echo "   And copy target/picture-viewer as picture-viewer-windows"