#!/usr/bin/env bash

set -euo pipefail

# Create lib directory if it doesn't exist
mkdir -p lib

# Download Java 3D libraries from NEA webstart and Maven
echo "Downloading Java 3D libraries..."
curl --fail --location --retry 3 --output lib/j3d-core.jar \
    https://www.oecd-nea.org/webstart/java3d-1.5.2/j3d/1.5.2/j3dcore.jar
curl --fail --location --retry 3 --output lib/j3d-utils.jar \
    https://www.oecd-nea.org/webstart/java3d-1.5.2/j3d/1.5.2/j3dutils.jar
curl --fail --location --retry 3 --output lib/vecmath.jar \
    https://repo1.maven.org/maven2/javax/vecmath/vecmath/1.5.2/vecmath-1.5.2.jar

# Verify downloads
for jar in lib/j3d-core.jar lib/j3d-utils.jar lib/vecmath.jar; do
    if [ ! -s "$jar" ]; then
        echo "Error: $jar is empty or missing"
        exit 1
    fi
    jar tf "$jar" >/dev/null
done

# Download JAI libraries
echo "Downloading JAI libraries..."
curl --fail --location --retry 3 --output jai-1_1_3-lib-linux-amd64.tar.gz \
    https://download.java.net/media/jai/builds/release/1_1_3/jai-1_1_3-lib-linux-amd64.tar.gz
tar -xzf jai-1_1_3-lib-linux-amd64.tar.gz
cp jai-1_1_3/lib/*.jar lib/
rm -rf jai-1_1_3 jai-1_1_3-lib-linux-amd64.tar.gz

# Verify all downloads
for jar in lib/*.jar; do
    if [ ! -s "$jar" ]; then
        echo "Error: $jar is empty or missing"
        exit 1
    fi
    jar tf "$jar" >/dev/null
done

echo "Library setup complete!" 