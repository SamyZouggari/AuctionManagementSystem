# 🛒 Auction Management System

Welcome to our **Auction Management System**, a Java-based application integrated with Oracle SQL. This project allows users to buy and sell products via an auction platform with dynamic features like bidding, stock management, and user authentication.
We were 5 on the project and we did it in one week. I contributed in creating manually the database from a description, then creating it in sql. I also created the interface (TestInterface.java) and coded the auction system (buyer part) with JDBC.
---

## 🚀 Key Features

- **User-Friendly Interface**: Intuitive console-based interaction for buyers and sellers.
- **Dynamic Auctions**: Support for ascending and descending auctions, revocable and non-revocable options.
- **Database Integration**: Uses Oracle SQL for secure and efficient data management.
- **Stock Management**: Tracks product quantities and updates dynamically.
- **User Authentication**: Registers and authenticates users securely.

---

## 🛠️ Technologies Used

- **Programming Language**: Java
- **Database**: Oracle SQL
- **JDBC**: Java Database Connectivity for database interactions

---

## 📂 Project Structure

- **`Interface.java`**: Core functionality for managing auctions, user processes, and database interactions.
- **`TestInterface.java`**: Entry point for testing the system, simulating buyer and seller workflows.
- **`RemplissageBDD.sql`**: SQL script for populating the database with initial data.
- **`baseDeDonnee.sql`**: SQL script for creating the required database schema.

---

Before 

Voici un README complet et adapté pour ton nouveau dépôt basé sur le projet et le Makefile :
🛒 Auction Management System

Welcome to the Auction Management System, a Java-based project integrated with Oracle SQL. This system enables users to buy and sell products through an auction platform featuring dynamic bidding and categorized rooms. It includes user authentication, real-time stock management, and database updates.
🚀 Key Features

    Dynamic Auctions: Supports ascending and descending auctions.
    Database Integration: Efficiently manages users, products, and auctions with Oracle SQL.
    User Roles:
        Buyers: Bid on products dynamically.
        Sellers: List products for sale with auction settings.
    Real-Time Updates: Tracks stock and manages transactions.
    Console-Based Interface: Intuitive and easy to use.

🛠️ Technologies Used

    Java: For application logic and interface.
    Oracle SQL: For database schema and data management.
    JDBC: To connect the Java application with the Oracle database.
    Makefile: For streamlined build and execution processes.

📂 Project Structure

    Interface.java: Handles core auction functionality, user processes, and database interactions.
    TestInterface.java: Entry point for testing the system, simulating buyer and seller workflows.
    baseDeDonnee.sql: SQL script to set up the database schema.
    RemplissageBDD.sql: SQL script to populate the database with sample data.

## 📝 How to Run
First, in the Makefile you have to set the name of the server on DB_CONN, the login into your database by changing DB_USER and DB_PASS. 

### Prerequisites
- Oracle SQL Database instance
- Java Development Kit (JDK)
- Oracle JDBC Driver
- `make` utility installed on your system

### Steps to Run

1. **Initialize and Populate the Database**:
   ```bash
   make db-reset
   ```
   This command sets up the database schema and populates it with initial data using the provided SQL scripts.

2. **Compile the Java Files**:
   ```bash
   make compile
   ```
   This compiles the Java source code files.

3. **Run the Program**:
   ```bash
   make run
   ```
   This executes the main program `TestInterface`.

4. **Clean Compiled Files** (Optional):
   ```bash
   make clean
   ```
   This removes all compiled `.class` files.

### Additional Commands
For further assistance, run:
```bash
make help
```
This will list all available commands.

   
