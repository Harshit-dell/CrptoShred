# CrptoShred - Secure Shredder

## 1. Project Overview

**CrptoShred** is a powerful command-line utility for securely and permanently deleting files and directories. It overwrites data with multiple passes of meaningless patterns before deletion, making data recovery practically impossible.

### Key Features

- **Safety Confirmation:** Mandatory interactive prompt before any destructive action, preventing accidental data loss.
- **Detailed Logging:** All operations, configurations, and errors are recorded in `CrptoShred_log.txt`.
- **Recursive Directory Wiping:** Securely delete entire folder structures.
- **Selectable Shredding Standards:** Choose from different methods for a balance of speed and security.
- **Verification:** Confirm a successful wipe by checking the file's final SHA-256 hash.
- **Progress Bar:** Real-time feedback on the wipe process in your terminal.

---

## 2. How to Build the Application

To compile CrptoShred into a standalone tool:

1. **Make the build script executable:**
    ```bash
    chmod +x build.sh
    ```
2. **Run the build script:**
    ```bash
    ./build.sh
    ```
After building, `CrptoShred.jar` will appear in your project's root directory.

---

## 3. How to Install and Run as a Command

After creating `CrptoShred.jar`, you can set up a script to run it globally.

### Steps:

1. **Move the JAR file to a permanent location:**
    ```bash
    mkdir -p ~/apps/crptoshred
    mv CrptoShred.jar ~/apps/crptoshred/
    ```
2. **Create the runner script:**
    - Create a file named `crptoshred`:
        ```bash
        nano crptoshred
        ```
    - Add the following lines (replace `/home/user/apps/crptoshred/` with the actual path to your JAR):
        ```bash
        #!/bin/bash
        java -jar /home/user/apps/crptoshred/CrptoShred.jar "$@"
        ```
    - Make the script executable:
        ```bash
        chmod +x crptoshred
        ```
3. **Move the script to your PATH:**
    ```bash
    sudo mv crptoshred /usr/local/bin/
    ```
Now you can run the tool from anywhere by typing `crptoshred`.

---

## 4. Command-Line Usage

Once installed, use the command as follows:

### Syntax

```bash
crptoshred <path> [passes] [--method <name>] [delete] [verify]
```

### Example

```bash
crptoshred ./my-secret-folder --method DOD_5220_22_M delete format this
```

---

## 5. Shredding Methods

- **DOD_5220_22_M:** U.S. Department of Defense standard (multiple overwrite passes).
- **Gutmann:** 35-pass overwrite for maximum security.
- **Simple:** Quick single or few-pass overwrite for speed.

---

## 6. Logging

All activities are logged in `CrptoShred_log.txt` in the application folder. Check this file for details about operations, errors, and confirmations.

---

## 7. Safety & Verification

- **Confirmation Prompt:** Prevents accidental deletion by requiring user input before proceeding.
- **Verification:** SHA-256 hash check ensures the wipe is complete.

---

## 8. Troubleshooting

- **Log File:** Review `CrptoShred_log.txt` for errors or issues.
- **Permissions:** Ensure you have proper permissions for the files or directories you wish to shred.
- **Java:** Requires Java to be installed on your system.

---

## 9. License & Contribution

Open source under [your license]. Contributions welcome! Please open issues or pull requests for enhancements and bug fixes.

---