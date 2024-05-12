package org.example;

import java.sql.SQLException;
import java.util.Scanner;

public class Administrator implements User {
    private static final Scanner scanner = new Scanner(System.in);
    private DatabaseConnection dbConnection;

    public Administrator(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public void showMenu() throws SQLException {
        boolean running = true;
        while (running) {
            System.out.println("\n[Administrator Menu]");
            System.out.println("1. Add a new country");
            System.out.println("2. Update country safety");
            System.out.println("3. List countries");
            System.out.println("4. Add a municipality");
            System.out.println("5. Add an AZC");
            System.out.println("6. Update an AZC");
            System.out.println("7. Delete an AZC");
            System.out.println("8. Generate payments report");
            System.out.println("9. Update municipality places offered");
            System.out.println("10. List municipalities with places offered");
            System.out.println("11. Quit");
            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    addCountry();
                    break;
                case 2:
                    updateCountrySafety();
                    break;
                case 3:
                    listCountries();
                    break;
                case 4:
                    addMunicipality();
                    break;
                case 5:
                    addAZC();
                    break;
                case 6:
                    updateAZC();
                    break;
                case 7:
                    deleteAZC();
                    break;
                case 8:
                    generatePaymentsReport();
                    break;
                case 9:
                    updateMunicipalityPlaces();
                    break;
                case 10:
                    listMunicipalitiesWithPlaces();
                    break;
                case 11:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void addCountry() throws SQLException {
        System.out.println("Enter country name:");
        String name = scanner.nextLine();
        System.out.println("Is it a safe country? (true/false):");
        boolean isSafe = Boolean.parseBoolean(scanner.nextLine());

        dbConnection.addCountry(name, isSafe);
    }

    private void updateCountrySafety() throws SQLException {
        System.out.println("Enter country name to update:");
        String name = scanner.nextLine();
        System.out.println("Is it a safe country now? (true/false):");
        boolean isSafe = Boolean.parseBoolean(scanner.nextLine());

        dbConnection.updateCountrySafety(name, isSafe);
    }

    private void listCountries() throws SQLException {
        System.out.println("Countries and their safety status:");
        dbConnection.listCountries();
    }

    private void addMunicipality() throws SQLException {
        System.out.println("Enter municipality name:");
        String name = scanner.nextLine();
        System.out.println("Enter population:");
        int population = Integer.parseInt(scanner.nextLine());

        dbConnection.addMunicipality(name, population);
    }

    private void addAZC() throws SQLException {
        System.out.println("Enter AZC street:");
        String street = scanner.nextLine();
        System.out.println("Enter AZC number:");
        int number = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter AZC postcode:");
        String postcode = scanner.nextLine();
        System.out.println("Enter AZC municipality name:");
        String municipalityName = scanner.nextLine();

        dbConnection.addAZC(street, number, postcode, municipalityName);
    }

    private void updateAZC() throws SQLException {
        System.out.println("Enter AZC ID to update:");
        int azcId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter new AZC street:");
        String street = scanner.nextLine();
        System.out.println("Enter new AZC number:");
        int number = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter new AZC postcode:");
        String postcode = scanner.nextLine();
        System.out.println("Enter new AZC municipality name:");
        String municipalityName = scanner.nextLine();

        dbConnection.updateAZC(azcId, street, number, postcode, municipalityName);
    }

    private void deleteAZC() throws SQLException {
        System.out.println("Enter AZC ID to delete:");
        int azcId = Integer.parseInt(scanner.nextLine());

        dbConnection.deleteAZC(azcId);
    }

    private void generatePaymentsReport() throws SQLException {
        System.out.println("Generating payments report for municipalities...");
        dbConnection.generatePaymentsReport().forEach(System.out::println);
    }

    private void updateMunicipalityPlaces() throws SQLException {
        System.out.println("Enter municipality name:");
        String name = scanner.nextLine();
        System.out.println("Enter number of places offered:");
        int placesOffered = Integer.parseInt(scanner.nextLine());

        dbConnection.updateMunicipalityPlaces(name, placesOffered);
    }

    private void listMunicipalitiesWithPlaces() throws SQLException {
        System.out.println("Municipalities and their offered places:");
        dbConnection.listMunicipalitiesWithPlaces();
    }
}