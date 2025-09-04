#!/bin/bash

# This script compiles the Java source files and packages them into an executable JAR.

echo "--- CryptoWipe Build Script ---"

# Create a build directory if it doesn't exist
echo "[1/4] Creating build directory 'bin'..."
mkdir -p bin

# Compile all .java files from the src directory into the bin directory
echo "[2/4] Compiling Java source files..."
javac -d bin src/*.java
if [ $? -ne 0 ]; then
    echo "Error: Compilation failed."
    exit 1
fi

# Create the executable JAR file
# c - create archive
# f - specify archive file name
# e - specify entry point (main class)
# -C bin . - change to the 'bin' directory and include all its contents
echo "[3/4] Packaging compiled classes into CryptoWipe.jar..."
jar cfe CryptoWipe.jar Main -C bin .
if [ $? -ne 0 ]; then
    echo "Error: Failed to create JAR file."
    exit 1
fi

echo "[4/4] Cleaning up..."
# Optional: remove the bin directory after building the jar
# rm -r bin

echo "--- Build Successful! ---"
echo "Executable JAR created: CryptoWipe.jar"
echo "You can now follow the installation instructions in README.md"

