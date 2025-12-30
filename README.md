# Online Personal Finance Manager

This is an Android application for managing personal finances. It consists of an Android client and a Java-based backend server.

## Project Structure

- `/app`: Contains the Android application code.
- `/server`: Contains the Java backend server code.

## How to Build and Run

### Backend Server

1.  **Configure the database:** Open `server/src/main/java/com/example/finance/backend/DatabaseConnection.java` and update the database URL, username, and password.
2.  **Build the server:**

    ```bash
    ./gradlew :server:build
    ```

3.  **Run the server:**

    ```bash
    ./gradlew :server:run
    ```

    The server will start on port 8080.

### Android App

1.  **Ensure the backend server is running.**
2.  **Open the project in Android Studio.**
3.  **Build and run the `app` module** on an Android emulator or a physical device.

