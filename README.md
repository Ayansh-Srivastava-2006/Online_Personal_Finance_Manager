# Online Personal Finance Manager

[![GitHub Repo](https://img.shields.io/badge/repo-Online_Personal_Finance_Manager-blue)](https://github.com/Ayansh-Srivastava-2006/Online_Personal_Finance_Manager) [![License](https://img.shields.io/badge/license-SEE_LICENSE-lightgrey)]()

A polished Android + Java backend app to track income, expenses, accounts, and budgets. This repository contains a native Android client (`app`) and a Java backend server (`server`).

Table of contents
- [Highlights](#highlights)
- [Screenshots / Demo](#screenshots--demo)
- [Features](#features)
- [Tech stack](#tech-stack)
- [Quickstart (3 minutes)](#quickstart-3-minutes)
  - [1) Configure the database](#1-configure-the-database)
  - [2) Build & run the backend](#2-build--run-the-backend)
  - [3) Run the Android app](#3-run-the-android-app)
- [API examples](#api-examples)
- [Development & testing](#development--testing)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License & contact](#license--contact)

Highlights
- Clean separation: Android client + Java server
- Lightweight APIs with token-based auth support
- Extensible: add providers, accounts, budgets, reporting
- Designed for local development and CI

Screenshots / Demo
- Add a screenshot or GIF named `docs/screenshot.png` or `docs/demo.gif` to this repo and it will display here.
- Example:
  ![App screenshot](docs/screenshot.png)

Features
- User sign-up / login
- CRUD for accounts, transactions, categories
- Budget creation and tracking
- Transaction sync between client and backend
- Simple reporting & export

Tech stack
- Android client: Kotlin / Java (Android Studio)
- Backend: Java + Gradle (see `server` module)
- Database: MySQL / Postgres / SQLite (configurable)

Quickstart (3 minutes)

1) Configure the database
- Open `server/src/main/java/com/example/finance/backend/DatabaseConnection.java` (or `DatabaseManager.java` if present) and update the connection string, username and password.
- For secure setups prefer environment variables or a `server/src/main/resources/application.properties` file.
- For full schema and step-by-step DB creation see `DATABASE_SETUP.md`.

Example environment variables (recommended)
- `DB_URL=jdbc:mysql://localhost:3306/yourdbname`
- `DB_USER=yourusername`
- `DB_PASS=yourpassword`

2) Build & run the backend
- Open a terminal in the `server` directory.
- Build the project: `./gradlew build` (Linux/Mac) or `gradlew build` (Windows).
- Run the server: `./gradlew run` or `java -jar build/libs/*.jar`.
- Default server port: `8080` (confirm in startup logs). API base: `http://localhost:8080`.

3) Run the Android app
- Open the project in Android Studio:
  - File > Open > choose repository root
  - Let Gradle sync
- Start an emulator or connect a device, then run the `app` module.
- Ensure backend is reachable from the device/emulator (use `10.0.2.2` for Android emulator to reach host `localhost`).
- Install debug APK to a connected device:

Troubleshooting
- DB connection failures:
  - Verify `DatabaseConnection.java` values or environment variables.
  - Check DB is running and accepting connections on configured port.
- Backend not reachable from emulator:
  - Use `10.0.2.2` (Android emulator) or ensure device is on same network.
- Gradle build errors:
  - Run with `--stacktrace` and `--refresh-dependencies`:

Contributing
- Fork, create a branch, add tests, and open a PR with a clear description.
- Recommended: add `CONTRIBUTING.md` and PR template to the repo.
- Keep secrets out of source control — use environment variables or CI secrets.

License & contact
- See `LICENSE` at the repo root (add one if missing; MIT or Apache-2.0 recommended).
- Repository: https://github.com/Ayansh-Srivastava-2006/Online_Personal_Finance_Manager
- For issues and feature requests use GitHub Issues.

Notes
- Add `docs/` folder with screenshots, API docs (OpenAPI/Swagger) and a `CONTRIBUTING.md` to make the project even more attractive to contributors.



