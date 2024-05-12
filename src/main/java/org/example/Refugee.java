package org.example;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Refugee implements User {
    private int id;
    private String name;
    private String countryOfOrigin;
    private boolean hasPassport;
    private boolean applicationComplete;
    private boolean judgeAssigned;
    private boolean judgeDecisionMade;
    private String judgeDecision;
    private boolean returnedToOrigin;
    private String placementStatus;  // Tracks the placement in own housing
    private String address;          // New address of the refugee

    private DatabaseConnection dbConnection;

    private static final Scanner scanner = new Scanner(System.in);

    public Refugee(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void populateData(int id, String name, String countryOfOrigin, boolean hasPassport,
                             boolean applicationComplete, boolean judgeAssigned,
                             boolean judgeDecisionMade, String judgeDecision,
                             boolean returnedToOrigin, String placementStatus, String address) {
        this.id = id;
        this.name = name;
        this.countryOfOrigin = countryOfOrigin;
        this.hasPassport = hasPassport;
        this.applicationComplete = applicationComplete;
        this.judgeAssigned = judgeAssigned;
        this.judgeDecisionMade = judgeDecisionMade;
        this.judgeDecision = judgeDecision;
        this.returnedToOrigin = returnedToOrigin;
        this.placementStatus = placementStatus;
        this.address = address;
    }

    @Override
    public void showMenu() {
        System.out.println("Enter your Refugee ID:");
        int refugeeId = Integer.parseInt(scanner.nextLine().trim());

        showDetailsById(refugeeId);

        if ("accepted".equalsIgnoreCase(judgeDecision) && "started".equalsIgnoreCase(placementStatus)) {
            System.out.println("Do you want to update your address? (yes/no)");
            String response = scanner.nextLine().trim();
            if ("yes".equalsIgnoreCase(response)) {
                updateAddress(refugeeId);
            }
        }
    }

    public void showDetailsById(int refugeeId) {
        try {
            String sql = "SELECT * FROM refugees WHERE id = ?";
            try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
                pstmt.setInt(1, refugeeId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    populateData(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("country_of_origin"),
                            rs.getBoolean("has_passport"),
                            rs.getBoolean("application_complete"),
                            rs.getBoolean("judge_assigned"),
                            rs.getBoolean("judge_decision_made"),
                            rs.getString("judge_decision"),
                            rs.getBoolean("returned_to_origin"),
                            rs.getString("placement_status"),
                            rs.getString("address")
                    );
                    viewMyDetails();
                } else {
                    System.out.println("No details found for the given ID.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void viewMyDetails() {
        System.out.println("\nYour Registered Details:");
        System.out.println("Name: " + name);
        System.out.println("Country of Origin: " + countryOfOrigin);
        System.out.println("Has Passport: " + (hasPassport ? "Yes" : "No"));
        System.out.println("Application Complete: " + (applicationComplete ? "Yes" : "No"));
        System.out.println("Judge Assigned: " + (judgeAssigned ? "Yes" : "No"));
        System.out.println("Judge Decision Made: " + (judgeDecisionMade ? "Yes" : "No"));
        System.out.println("Judge Decision: " + (judgeDecision != null ? judgeDecision : "N/A"));
        System.out.println("Returned to Origin: " + (returnedToOrigin ? "Yes" : "No"));
        System.out.println("Placement in Own Housing: " + (placementStatus != null ? placementStatus : "N/A"));
        System.out.println("Current Address: " + (address != null && !address.isEmpty() ? address : "Not provided"));
    }

    private void updateAddress(int refugeeId) {
        System.out.println("Enter your new address:");
        String newAddress = scanner.nextLine().trim();

        try {
            String sql = "UPDATE refugees SET address = ? WHERE id = ?";
            try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, newAddress);
                pstmt.setInt(2, refugeeId);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Address updated successfully.");
                    this.address = newAddress;  // Update the current instance's address
                } else {
                    System.out.println("Failed to update the address.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}