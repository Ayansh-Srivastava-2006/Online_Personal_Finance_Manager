[//]: # (# Database Setup Guide)

[//]: # ()
[//]: # (## Database Configuration)

[//]: # ()
[//]: # (The backend server requires a MySQL database. Update the connection settings in:)

[//]: # ()
[//]: # (`server/src/main/java/com/example/finance/backend/DatabaseManager.java`)

[//]: # ()
[//]: # (```java)

[//]: # (private static final String URL = "jdbc:mysql://localhost:3308/finance_manager?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";)

[//]: # (private static final String USER = "root";)

[//]: # (private static final String PASSWORD = "root";)

[//]: # (```)

[//]: # ()
[//]: # (**⚠️ IMPORTANT**: Change the database credentials before deploying to production!)

[//]: # ()
[//]: # (## Database Schema)

[//]: # ()
[//]: # (### Users Table)

[//]: # (```sql)

[//]: # (CREATE TABLE users &#40;)

[//]: # (    uid VARCHAR&#40;255&#41; PRIMARY KEY,)

[//]: # (    fullName VARCHAR&#40;255&#41; NOT NULL,)

[//]: # (    username VARCHAR&#40;255&#41; NOT NULL UNIQUE,)

[//]: # (    email VARCHAR&#40;255&#41; NOT NULL UNIQUE,)

[//]: # (    password_hash BINARY&#40;32&#41; NOT NULL,)

[//]: # (    salt BINARY&#40;16&#41; NOT NULL,)

[//]: # (    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)

[//]: # (&#41;;)

[//]: # (```)

[//]: # ()
[//]: # (### Accounts Table)

[//]: # (```sql)

[//]: # (CREATE TABLE accounts &#40;)

[//]: # (    accountId VARCHAR&#40;255&#41; PRIMARY KEY,)

[//]: # (    name VARCHAR&#40;255&#41; NOT NULL,)

[//]: # (    type VARCHAR&#40;50&#41; NOT NULL,)

[//]: # (    balance DECIMAL&#40;15, 2&#41; DEFAULT 0.00,)

[//]: # (    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)

[//]: # (&#41;;)

[//]: # (```)

[//]: # ()
[//]: # (### Transactions Table)

[//]: # (```sql)

[//]: # (CREATE TABLE transactions &#40;)

[//]: # (    transactionId VARCHAR&#40;255&#41; PRIMARY KEY,)

[//]: # (    accountId VARCHAR&#40;255&#41; NOT NULL,)

[//]: # (    amount DECIMAL&#40;15, 2&#41; NOT NULL,)

[//]: # (    type ENUM&#40;'INCOME', 'EXPENSE'&#41; NOT NULL,)

[//]: # (    category VARCHAR&#40;100&#41; NOT NULL,)

[//]: # (    date BIGINT NOT NULL,)

[//]: # (    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,)

[//]: # (    FOREIGN KEY &#40;accountId&#41; REFERENCES accounts&#40;accountId&#41; ON DELETE CASCADE)

[//]: # (&#41;;)

[//]: # (```)

[//]: # ()
[//]: # (### Budgets Table)

[//]: # (```sql)

[//]: # (CREATE TABLE budgets &#40;)

[//]: # (    budgetId VARCHAR&#40;255&#41; PRIMARY KEY,)

[//]: # (    category VARCHAR&#40;100&#41; NOT NULL,)

[//]: # (    amount DECIMAL&#40;15, 2&#41; NOT NULL,)

[//]: # (    spent DECIMAL&#40;15, 2&#41; DEFAULT 0.00,)

[//]: # (    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)

[//]: # (&#41;;)

[//]: # (```)

[//]: # ()
[//]: # (## Creating the Database)

[//]: # ()
[//]: # (1. Log into MySQL:)

[//]: # (   ```bash)

[//]: # (   mysql -u root -p)

[//]: # (   ```)

[//]: # ()
[//]: # (2. Create the database:)

[//]: # (   ```sql)

[//]: # (   CREATE DATABASE finance_manager;)

[//]: # (   USE finance_manager;)

[//]: # (   ```)

[//]: # ()
[//]: # (3. Run the CREATE TABLE statements above)

[//]: # ()
[//]: # (4. Verify the tables:)

[//]: # (   ```sql)

[//]: # (   SHOW TABLES;)

[//]: # (   ```)

