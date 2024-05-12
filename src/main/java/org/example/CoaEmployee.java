package org.example;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class CoaEmployee implements User {
    private static final Scanner scanner = new Scanner(System.in);
    private DatabaseConnection dbConnection;

    public CoaEmployee(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public void showMenu() throws SQLException {
        boolean running = true;
        while (running) {
            System.out.println("\n[COA Employee Menu]");
            System.out.println("1. Register a refugee");
            System.out.println("2. Place or transfer a refugee");
            System.out.println("3. Update a refugee's dossier");
            System.out.println("4. Quit");
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    registerRefugee();
                    break;
                case 2:
                    placeOrTransferRefugee();
                    break;
                case 3:
                    updateRefugeeDossier();
                    break;
                case 4:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void updateRefugeeDossier() throws SQLException {
        System.out.println("Enter refugee ID:");
        int refugeeId = Integer.parseInt(scanner.nextLine());

        System.out.println("Has the refugee shown a passport? (yes/no):");
        boolean hasShownPassport = scanner.nextLine().trim().equalsIgnoreCase("yes");

        System.out.println("Is the asylum application complete? (yes/no):");
        boolean applicationComplete = scanner.nextLine().trim().equalsIgnoreCase("yes");

        System.out.println("Has a judge been assigned? (yes/no):");
        boolean judgeAssigned = scanner.nextLine().trim().equalsIgnoreCase("yes");

        System.out.println("Has the judge made a decision? (yes/no):");
        boolean judgeDecisionMade = scanner.nextLine().trim().equalsIgnoreCase("yes");

        String judgeDecision = null;
        if (judgeDecisionMade) {
            System.out.println("What is the judge's decision? (admitted/rejected):");
            judgeDecision = scanner.nextLine().trim().equalsIgnoreCase("admitted") ? "admitted" : "rejected";
        }

        System.out.println("Has the refugee returned to their country of origin? (yes/no):");
        boolean returnedToOrigin = scanner.nextLine().trim().equalsIgnoreCase("yes");

        String sql = "UPDATE refugees SET has_passport = ?, application_complete = ?, " +
                "judge_assigned = ?, judge_decision_made = ?, judge_decision = ?, returned_to_origin = ? " +
                "WHERE id = ?";

        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            pstmt.setBoolean(1, hasShownPassport);
            pstmt.setBoolean(2, applicationComplete);
            pstmt.setBoolean(3, judgeAssigned);
            pstmt.setBoolean(4, judgeDecisionMade);
            pstmt.setString(5, judgeDecision);
            pstmt.setBoolean(6, returnedToOrigin);
            pstmt.setInt(7, refugeeId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("[DEBUG] Refugee dossier updated successfully.");
            } else {
                System.out.println("[FAILED] Error updating refugee dossier.");
            }
        }
    }

    private void placeOrTransferRefugee() throws SQLException {
        String suggestedMunicipality = dbConnection.findMunicipalityWithMostAvailablePlaces();
        if (suggestedMunicipality == null) {
            suggestedMunicipality = dbConnection.findMunicipalityWithLowestRelativePlaces();
        }
        System.out.println("Suggested municipality for placement: " + suggestedMunicipality);
        System.out.println("Enter refugee ID for placement/transfer:");
        int refugeeId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter destination AZC ID:");
        int azcId = Integer.parseInt(scanner.nextLine());

        transferRefugee(refugeeId, azcId);
    }

    private void transferRefugee(int refugeeId, int azcId) throws SQLException {
        String sql = "UPDATE refugees SET azc_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, azcId);
            pstmt.setInt(2, refugeeId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("[DEBUG] Refugee transferred successfully.");
                updateMunicipalityPlacesAfterPlacement(azcId);
            } else {
                System.out.println("[FAILED] Error transferring refugee.");
            }
        }
    }

    private void updateMunicipalityPlacesAfterPlacement(int azcId) throws SQLException {
        String sql = "UPDATE municipalities SET places_filled = places_filled + 1 " +
                "WHERE id = (SELECT municipality_id FROM azcs WHERE id = ?)";
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, azcId);
            pstmt.executeUpdate();
            System.out.println("[DEBUG] Updated municipality places after placement.");
        }
    }

    private void registerRefugee() throws SQLException {
        System.out.println("Enter refugee name:");
        String name = scanner.nextLine();
        System.out.println("Enter refugee's country of origin:");
        String countryOfOrigin = scanner.nextLine();
        System.out.println("Can the refugee show a passport? (yes/no):");
        boolean hasPassport = scanner.nextLine().trim().equalsIgnoreCase("yes");

        dbConnection.addRefugee(name, countryOfOrigin, hasPassport);
    }
}