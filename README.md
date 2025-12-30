# Online Personal Finance Manager

[![GitHub Repo](https://img.shields.io/badge/repo-Online_Personal_Finance_Manager-blue)](https://github.com/Ayansh-Srivastava-2006/Online_Personal_Finance_Manager) [![License](https://img.shields.io/badge/license-SEE_LICENSE-lightgrey)]()

A full-stack Android application for personal finance management. Track income, expenses, accounts, and budgets with a native Android client and a Java backend server.

## Table of Contents
- [Highlights](#highlights)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [1. Configure the Database](#1-configure-the-database)
  - [2. Build & Run the Backend](#2-build--run-the-backend)
  - [3. Run the Android App](#3-run-the-android-app)
- [API Endpoints](#api-endpoints)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License & Contact](#license--contact)

## Highlights
- 📱 **Native Android Client** - Modern UI with Material Design
- ☕ **Java Backend Server** - Jetty-based REST API with servlet architecture
- 🔐 **Secure Authentication** - User registration and login with password hashing
- 💰 **Financial Management** - Track accounts, transactions, and budgets
- 🔄 **Real-time Sync** - Data synchronization between client and server

## Features
- User registration and login
- Account management (create, update, delete accounts)
- Transaction tracking (income & expenses)
- Budget creation and monitoring
- Category-based organization
- Financial summary and reporting

## Tech Stack

| Component | Technology |
|-----------|------------|
| **Android Client** | Java, Retrofit, Gson, Material Design |
| **Backend Server** | Java 17+, Jetty Server, Jakarta Servlets |
| **Database** | MySQL / PostgreSQL / SQLite |
| **Build Tool** | Gradle (Kotlin DSL) |

## Project Structure
```
Online_Personal_Finance_Manager/
├── app/                          # Android application
│   └── src/main/java/com/example/online_personal_finance_manager/
│       ├── api/                  # Retrofit API client & service
│       ├── backend/              # Data models (User, Account, Transaction, Budget)
│       ├── LoginActivity.java
│       ├── RegisterActivity.java
│       ├── HomeActivity.java
│       └── FinanceManager.java   # Business logic & API calls
├── server/                       # Java backend server
│   └── src/main/java/com/example/finance/backend/
│       ├── ServerMain.java       # Jetty server entry point
│       ├── *Servlet.java         # REST API endpoints
│       ├── FinanceDatabase.java  # Database operations
│       └── *.java                # Data models & utilities
├── DATABASE_SETUP.md             # Database setup instructions
└── README.md
```

## Getting Started

### Prerequisites
- **Java 17+** (for backend server)
- **Android Studio** (latest stable version)
- **MySQL/PostgreSQL** database (or SQLite for development)
- **Gradle 8.x**

### 1. Configure the Database
1. Create a database for the application
2. Update connection settings in `server/src/main/java/com/example/finance/backend/DatabaseConnection.java`

**Recommended: Use environment variables**
```bash
export DB_URL=jdbc:mysql://localhost:3306/finance_db
export DB_USER=your_username
export DB_PASS=your_password
```

For detailed schema setup, see [`DATABASE_SETUP.md`](DATABASE_SETUP.md).

### 2. Build & Run the Backend
```bash
cd server

# Build the project
./gradlew build          # Linux/macOS
gradlew build            # Windows

# Run the server
./gradlew run            # Linux/macOS
gradlew run              # Windows
```
Server starts on **port 8080** by default. Base URL: `http://localhost:8080`

### 3. Run the Android App
1. Open the project in Android Studio: **File > Open** and select the repository root
2. Wait for Gradle sync to complete
3. **Important**: Ensure the backend server is running before launching the app
4. Run the `app` module on an emulator or connected device

> **Note for Emulator Users**: The Android emulator uses `10.0.2.2` to access the host machine's `localhost`. This is already configured in `ApiClient.java`.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/register` | User registration |
| POST | `/login` | User login |
| GET | `/accounts` | Get all accounts |
| POST | `/accounts` | Create new account |
| PUT | `/accounts/{id}` | Update account |
| DELETE | `/accounts/{id}` | Delete account |
| GET | `/transactions` | Get all transactions |
| POST | `/transactions` | Create new transaction |
| GET | `/budgets` | Get all budgets |
| POST | `/budgets` | Create new budget |
| PUT | `/budgets/{id}` | Update budget |
| DELETE | `/budgets/{id}` | Delete budget |

## Troubleshooting

### "CLEARTEXT communication not permitted"
Add `android:usesCleartextTraffic="true"` to `AndroidManifest.xml` (already configured for development).

### "Login Failed: <html>..."
This means the API endpoint URL is incorrect. Ensure:
- Backend server is running on port 8080
- `ApiClient.java` has `BASE_URL = "http://10.0.2.2:8080/"` (no `/api/` suffix)

### Database Connection Failures
- Verify database credentials in `DatabaseConnection.java`
- Ensure database server is running and accepting connections
- Check firewall settings

### Backend Not Reachable from Emulator
- Use `10.0.2.2` (Android emulator) to reach host `localhost`
- For physical devices, use your computer's local IP address

### Gradle Build Errors
```bash
./gradlew build --stacktrace --refresh-dependencies
```

## Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License & Contact
- See [`LICENSE`](LICENSE) at the repo root
- Repository: https://github.com/Ayansh-Srivastava-2006/Online_Personal_Finance_Manager
- For issues and feature requests, use [GitHub Issues](https://github.com/Ayansh-Srivastava-2006/Online_Personal_Finance_Manager/issues)
