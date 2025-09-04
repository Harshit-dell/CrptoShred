# CryptoWipe - Secure Shredder

## 1. Project Overview

CryptoWipe is a powerful command-line utility for securely and permanently deleting files and directories. It overwrites data with multiple passes of meaningless patterns before deletion, making data recovery practically impossible.

**Key Features:**

- **Safety Confirmation**: Mandatory interactive prompt warns you before any destructive action, preventing accidental data loss.
- **Detailed Logging**: All operations, configurations, and errors are recorded in a timestamped log file (`CryptoWipe_log.txt`).
- **Recursive Directory Wiping**: Securely delete entire folder structures.
- **Selectable Shredding Standards**: Choose from different methods for a balance of speed and security.
- **Verification**: Confirm a successful wipe by checking the file's final SHA-256 hash.
- **Progress Bar**: Get real-time feedback on the wipe process in your terminal.

---

## 2. How to Package for Distribution

To package CryptoWipe as a standalone tool, compile the source files and create an executable `.jar` file.

### **Steps:**

1. **Open a Terminal/Command Prompt**  
   Navigate to your project root (containing the `src` folder).

2. **Create a Build Directory**
   ```bash
   mkdir bin
   ```

3. **Compile the Source Code**
   ```bash
   javac -d bin src/*.java
   ```

4. **Create the Executable JAR File**
   ```bash
   jar cfe CryptoWipe.jar Main -C bin .
   ```
   The `CryptoWipe.jar` file will appear in your project root.

---

## 3. How to Install and Run as a Command

### For Linux & macOS

1. **Move the JAR file**
   ```bash
   mkdir -p ~/apps/cryptowipe
   mv CryptoWipe.jar ~/apps/cryptowipe/
   ```

2. **Create the runner script**
   ```bash
   touch cryptowipe
   ```
   Edit `cryptowipe` and add:
   ```bash
   #!/bin/bash
   java -jar /home/user/apps/cryptowipe/CryptoWipe.jar "$@"
   ```
   *(Replace `/home/user/apps/cryptowipe/` with your actual path.)*

3. **Make the script executable**
   ```bash
   chmod +x cryptowipe
   ```

4. **Move the script to your PATH**
   ```bash
   sudo mv cryptowipe /usr/local/bin/
   ```
   Now you can run CryptoWipe from anywhere:
   ```bash
   cryptowipe <arguments>
   ```

---

### For Windows

1. **Move the JAR file:**  
   Place `CryptoWipe.jar` in a permanent location, e.g., `C:\Program Files\CryptoWipe\`.

2. **Create the runner script:**  
   Create a file named `cryptowipe.bat` with:
   ```bat
   @echo off
   java -jar "C:\Program Files\CryptoWipe\CryptoWipe.jar" %*
   ```
   *(Replace the path with your actual location.)*

3. **Add script's location to PATH:**
    - Search for "Edit the system environment variables".
    - Click "Environment Variables...".
    - Under "System variables", edit `Path` and add the folder containing `cryptowipe.bat`.
    - Click OK to save.

Now you can run CryptoWipe from any `cmd` or PowerShell:
```shell
cryptowipe <arguments>
```

---

## 4. Command-Line Usage

**Syntax**
```shell
cryptowipe <path> [passes] [--method <name>] [delete] [verify]
```

**Example**
```shell
cryptowipe "C:\Users\MyUser\Desktop\secret-file.txt" --method DOD_5220_22_M delete format this
```

---

## 5. Notes

- Ensure you have Java installed (`java -version`).
- The tool is destructive—use with caution!
- For best security, use a reputable shredding method and verify results.

---