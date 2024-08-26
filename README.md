# Pharmacy Management System

## Overview
This Pharmacy Management System is a Java-based application designed to streamline the operations of a pharmacy. Utilizing JavaFX and FXML, this system enables efficient management of inventory, customer data, and order processing. The application follows the Model-View-Controller (MVC) architecture, ensuring a clear separation of concerns and enhancing maintainability.

## Features
- **Inventory Management**: Add, update, and remove products from the pharmacy's inventory.
- **Customer Management**: Manage customer information, including contact details and purchase history.
- **Order Processing**: Create, view, and process customer orders.
- **Dynamic Reporting**: Generate comprehensive reports using JasperReports for sales, inventory, and customer transactions.
- **Real-Time Input Validation**: Employs regex for validating user inputs, ensuring data integrity and enhancing user experience.

## Technology Stack
- **Java**: The primary programming language used for the application.
- **JavaFX**: A framework for building rich desktop applications with a modern user interface.
- **FXML**: A markup language used to define the user interface.
- **AnimateFX**: Provides animations for a more dynamic user experience.
- **ECJ (Eclipse Compiler for Java)**: An alternative Java compiler for advanced features.
- **FontAwesome**: Icon toolkit for scalable vector icons.
- **Jackson Core**: For processing JSON data.
- **JFoenix**: Material Design components for JavaFX applications.
- **MySQL Connector**: JDBC driver for connecting to MySQL databases.
- **JasperReports**: For generating dynamic reports.

## Architecture
The application follows the **Model-View-Controller (MVC)** architecture:
- **Model**: Manages the data and business logic, including database interactions.
- **View**: Represents the user interface using FXML files.
- **Controller**: Handles user input, interacting with both the model and the view.

## Database Connection
The application utilizes the **Singleton pattern** in `DatabaseConnection.java` to manage database connections efficiently. This approach ensures that only one instance of the database connection is created, optimizing resource usage and performance.

### Sample Code for Database Connection
```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        // Database connection logic
    }

    public static DatabaseConnection getInstance() {
        // Singleton implementation
    }

    public Connection getConnection() {
        return connection;
    }
}
```

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/RaminduA/Pharmacy_Management_System-Fxml.git
   ```
2. Navigate to the project directory:
   ```bash
   cd Pharmacy_Management_System-Fxml
   ```
3. Ensure you have the required dependencies and libraries set up in your Java environment.
4. Run the application using your preferred IDE or JavaFX runtime.

## Usage
- Launch the application.
- Navigate through the various functionalities such as managing inventory, processing orders, and generating reports.
- Utilize the real-time input validation features for a seamless user experience.

## License
This project is licensed under the MIT License, allowing for free use, modification, and distribution.
