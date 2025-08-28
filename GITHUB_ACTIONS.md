# GitHub Actions Workflow Documentation

## Overview
This repository now includes a GitHub Actions workflow for automated building and artifact publishing.

## Workflow Features

### Triggers
- **Pull Requests**: Automatically builds and tests PRs targeting the `main` branch
- **Push to Main**: Builds, tests, and publishes artifacts when code is pushed to the `main` branch

### Build Process
1. **Environment Setup**: Uses Ubuntu latest with Java 17 (Temurin distribution)
2. **Dependency Caching**: Caches Maven dependencies to improve build performance
3. **Compilation**: Runs `mvn clean compile` to compile the source code
4. **Testing**: Runs `mvn test` (continues on test failures to allow artifact generation)
5. **Packaging**: Creates the final JAR with `mvn package -DskipTests`

### Artifact Publishing
- **Condition**: Only runs on pushes to the `main` branch
- **Artifact Name**: `FreeMinecraftModels-{commit-sha}`
- **File**: The compiled `FreeMinecraftModels.jar` from the `target/` directory
- **Retention**: Artifacts are kept for 30 days

## File Locations
- Workflow: `.github/workflows/build.yml`
- Gitignore: `.gitignore` (excludes build artifacts and IDE files)

## Benefits
- Automatic validation of pull requests
- Consistent build environment
- Easy access to compiled JAR files for main branch builds
- Cached dependencies for faster subsequent builds