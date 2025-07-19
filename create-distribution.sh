#!/bin/bash

# Picture Viewer Distribution Creator
# Creates final archives for distribution

echo "Picture Viewer Distribution Creator"
echo "==================================="

if [ ! -d "releases" ]; then
    echo "ERROR: releases directory not found. Run ./build-release.sh first"
    exit 1
fi

cd releases

# Create Linux distribution
echo "Creating Linux distribution..."
if [ -d "Picture Viewer" ]; then
    tar -czf picture-viewer-linux-native-v1.0.tar.gz \
        "Picture Viewer/" \
        run-with-java.sh \
        picture-viewer-fat.jar \
        README.txt
    echo "SUCCESS: picture-viewer-linux-native-v1.0.tar.gz created (with native runtime)"
elif [ -d "picture-viewer-linux" ]; then
    tar -czf picture-viewer-linux-v1.0.tar.gz \
        picture-viewer-linux/ \
        run-picture-viewer-linux.sh \
        run-with-java.sh \
        picture-viewer-fat.jar \
        README.txt
    echo "SUCCESS: picture-viewer-linux-v1.0.tar.gz created"
else
    echo "WARNING: Linux runtime not found, creating JAR-only package..."
    tar -czf picture-viewer-linux-jar-only-v1.0.tar.gz \
        run-with-java.sh \
        picture-viewer-fat.jar \
        README.txt
    echo "SUCCESS: picture-viewer-linux-jar-only-v1.0.tar.gz created"
fi

# Create Windows distribution
echo "Creating Windows distribution..."
if [ -f "Picture Viewer-1.0.exe" ] || [ -f "Picture Viewer-1.0.msi" ]; then
    echo "Creating Windows native installers package..."
    zip -r picture-viewer-windows-native-v1.0.zip \
        *.exe *.msi \
        run-with-java.bat \
        picture-viewer-fat.jar \
        README.txt \
        USAGE.md 2>/dev/null
    echo "SUCCESS: picture-viewer-windows-native-v1.0.zip created (with native installers)"
elif [ -d "picture-viewer-windows" ]; then
    zip -r picture-viewer-windows-v1.0.zip \
        picture-viewer-windows/ \
        run-picture-viewer-windows.bat \
        run-with-java.bat \
        picture-viewer-fat.jar \
        README.txt
    echo "SUCCESS: picture-viewer-windows-v1.0.zip created"
else
    echo "WARNING: Windows runtime not found, creating JAR-only package..."
    zip -r picture-viewer-windows-jar-only-v1.0.zip \
        run-with-java.bat \
        picture-viewer-fat.jar \
        README.txt
    echo "SUCCESS: picture-viewer-windows-jar-only-v1.0.zip created"
fi

# Create universal JAR package
echo "Creating universal JAR package..."
zip -r picture-viewer-universal-jar-v1.0.zip \
    run-with-java.sh \
    run-with-java.bat \
    picture-viewer-fat.jar \
    README.txt
echo "SUCCESS: picture-viewer-universal-jar-v1.0.zip created"

cd ..

echo ""
echo "SUCCESS: Distribution packages created successfully!"
echo ""
echo "Available packages:"
ls -la releases/*.tar.gz releases/*.zip 2>/dev/null | sed 's/^/   /'
echo ""
echo "Distribution guidelines:"
echo "• Linux users: picture-viewer-linux-v1.0.tar.gz (with runtime) or picture-viewer-linux-jar-only-v1.0.tar.gz"
echo "• Windows users: picture-viewer-windows-v1.0.zip (with runtime) or picture-viewer-windows-jar-only-v1.0.zip"
echo "• Universal: picture-viewer-universal-jar-v1.0.zip (requires Java 21+)"