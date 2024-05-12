package org.example;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static DatabaseConnection dbConnection;

    public static void main(String[] args) {
        try {
            dbConnection = DatabaseConnection.getInstance();

            System.out.println("Welcome to the Refugee Management System");
            System.out.print("Enter your role (admin, coa, refugee): ");
            String role = scanner.nextLine().trim().toLowerCase();

            switch (role) {
                case "admin":
                    authenticateAdminOrEmployee();
                    break;
                case "coa":
                    authenticateAdminOrEmployee();
                    break;
                case "refugee":
                    new Refugee(dbConnection).showMenu();
                    break;
                default:
                    System.out.println("Invalid role. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void authenticateAdminOrEmployee() throws SQLException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        AuthService authService = new AuthService();

        if (authService.authenticate(username, password)) {
            String role = authService.getUserRole(username);
            switch (role) {
                case "Administrator":
                    new Administrator(dbConnection).showMenu();
                    break;
                case "coa_employee":
                    new CoaEmployee(dbConnection).showMenu();
                    break;
                default:
                    System.out.println("No actions available for this role.");
            }
        } else {
            System.out.println("Authentication failed.");
        }
    }
}