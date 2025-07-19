#!/bin/bash

# GitHub Release Creator for Picture Viewer
# This script helps you create a GitHub release

echo "GitHub Release Creator for Picture Viewer v1.0"
echo "==============================================="
echo ""

# Check if release files exist
echo "Checking release files..."
RELEASE_DIR="releases"
REQUIRED_FILES=(
    "picture-viewer_1.0_amd64.deb"
    "picture-viewer-linux-native-v1.0.tar.gz"
    "picture-viewer-linux-jar-only-v1.0.tar.gz"
    "picture-viewer-windows-jar-only-v1.0.zip"
    "picture-viewer-universal-jar-v1.0.zip"
    "USAGE.md"
)

MISSING_FILES=()
for file in "${REQUIRED_FILES[@]}"; do
    if [ ! -f "$RELEASE_DIR/$file" ]; then
        MISSING_FILES+=("$file")
    fi
done

if [ ${#MISSING_FILES[@]} -ne 0 ]; then
    echo "ERROR: Missing files:"
    for file in "${MISSING_FILES[@]}"; do
        echo "  - $file"
    done
    echo ""
    echo "Run first: ./build-release.sh && ./create-distribution.sh"
    exit 1
fi

echo "SUCCESS: All files found!"
echo ""

# Calculate file sizes
echo "File sizes:"
for file in "${REQUIRED_FILES[@]}"; do
    if [ -f "$RELEASE_DIR/$file" ]; then
        size=$(ls -lh "$RELEASE_DIR/$file" | awk '{print $5}')
        echo "  - $file: $size"
    fi
done
echo ""

# GitHub Release instructions
echo "GitHub Release Instructions:"
echo ""
echo "1. Go to your GitHub repository"
echo "2. Click 'Releases' → 'Create a new release'"
echo "3. Use this information:"
echo ""
echo "   Tag version: v1.0"
echo "   Release title: Picture Viewer v1.0"
echo ""
echo "   Description:"
echo "   ============"
cat << 'EOF'
First stable release of Picture Viewer

## Features
- Browse images in directories
- Support for common image formats (JPEG, PNG, GIF, BMP)
- Native Linux installer (.deb)
- Portable applications for Linux
- Cross-platform JAR packages

## Available Packages
- **Linux .deb installer** - For Debian/Ubuntu systems (45MB)
- **Linux portable app** - Works on all Linux distributions (71MB)
- **Linux JAR package** - Requires Java 21+ (8.4MB)
- **Windows JAR package** - Requires Java 21+ (8.4MB)
- **Universal JAR** - Works on all platforms with Java 21+ (8.4MB)

## Installation
See `USAGE.md` for detailed installation instructions for each package type.

## System Requirements
- **Native packages**: No additional requirements
- **JAR packages**: Java 21 or newer

## Notes
- JavaFX warnings in console output are normal and don't affect functionality
- For best user experience, use native packages when available
EOF
echo "   ============"
echo ""
echo "4. Upload these files (drag & drop):"
for file in "${REQUIRED_FILES[@]}"; do
    echo "   - $RELEASE_DIR/$file"
done
echo ""
echo "5. Click 'Publish release'"
echo ""

# Create ZIP with all files for easy upload
echo "Creating release-bundle.zip for easy upload..."
cd "$RELEASE_DIR"
zip -r ../release-bundle-v1.0.zip .
cd ..
echo "SUCCESS: release-bundle-v1.0.zip created"
echo ""
echo "TIP: You can upload release-bundle-v1.0.zip and extract it,"
echo "     or upload files one by one from the releases/ directory"
echo ""
echo "Ready for GitHub Release!"