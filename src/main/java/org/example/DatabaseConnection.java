package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private String url = "jdbc:sqlite:src/main/CAO.db";

    public DatabaseConnection() throws SQLException {
        try {
            this.connection = DriverManager.getConnection(url);
            System.out.println("[DEBUG] Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println("[FAILED] Cannot connect to database: " + e.getMessage());
            throw e;
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else if (instance.getConnection().isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void addCountry(String name, boolean isSafe) throws SQLException {
        String sql = "INSERT INTO countries (name, is_safe) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setBoolean(2, isSafe);
            pstmt.executeUpdate();
            System.out.println("[DEBUG] Country added successfully: " + name);
        } catch (SQLException e) {
            System.out.println("[FAILED] Error adding country: " + e.getMessage());
            throw e;
        }
    }

    public void updateCountrySafety(String name, boolean isSafe) throws SQLException {
        String sql = "UPDATE countries SET is_safe = ? WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, isSafe);
            pstmt.setString(2, name);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("[DEBUG] Updated country safety successfully: " + name);
            } else {
                System.out.println("[DEBUG] No such country found: " + name);
            }
        } catch (SQLException e) {
            System.out.println("[FAILED] Error updating country safety: " + e.getMessage());
            throw e;
        }
    }

    public void addMunicipality(String name, int population) throws SQLException {
        String sql = "INSERT INTO municipalities (name, population) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, population);
            pstmt.executeUpdate();
            System.out.println("[DEBUG] Municipality added successfully: " + name);
        } catch (SQLException e) {
            System.out.println("[FAILED] Error adding municipality: " + e.getMessage());
            throw e;
        }
    }

    public void addAZC(String street, int number, String postcode, String municipalityName) throws SQLException {
        String findMunicipalitySql = "SELECT id FROM municipalities WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(findMunicipalitySql)) {
            pstmt.setString(1, municipalityName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int municipalityId = rs.getInt("id");

                String sql = "INSERT INTO azcs (street, number, postcode, municipality_id) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmtAZC = connection.prepareStatement(sql)) {
                    pstmtAZC.setString(1, street);
                    pstmtAZC.setInt(2, number);
                    pstmtAZC.setString(3, postcode);
                    pstmtAZC.setInt(4, municipalityId);
                    pstmtAZC.executeUpdate();
                    System.out.println("[DEBUG] AZC added successfully at " + street);
                }
            } else {
                System.out.println("[FAILED] No such municipality found: " + municipalityName);
            }
        } catch (SQLException e) {
            System.out.println("[FAILED] Error adding AZC: " + e.getMessage());
            throw e;
        }
    }

    public void updateAZC(int azcId, String street, int number, String postcode, String municipalityName) throws SQLException {
        String findMunicipalitySql = "SELECT id FROM municipalities WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(findMunicipalitySql)) {
            pstmt.setString(1, municipalityName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int municipalityId = rs.getInt("id");

                String sql = "UPDATE azcs SET street = ?, number = ?, postcode = ?, municipality_id = ? WHERE id = ?";
                try (PreparedStatement pstmtAZC = connection.prepareStatement(sql)) {
                    pstmtAZC.setString(1, street);
                    pstmtAZC.setInt(2, number);
                    pstmtAZC.setString(3, postcode);
                    pstmtAZC.setInt(4, municipalityId);
                    pstmtAZC.setInt(5, azcId);
                    int affectedRows = pstmtAZC.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("[DEBUG] AZC updated successfully: " + street);
                    } else {
                        System.out.println("[DEBUG] No such AZC found with ID: " + azcId);
                    }
                }
            } else {
                System.out.println("[FAILED] No such municipality found: " + municipalityName);
            }
        } catch (SQLException e) {
            System.out.println("[FAILED] Error updating AZC: " + e.getMessage());
            throw e;
        }
    }

    public void deleteAZC(int azcId) throws SQLException {
        String checkRefugeesSql = "SELECT COUNT(*) as count FROM refugees_in_azc WHERE azc_id = ?";
        try (PreparedStatement pstmtCheck = connection.prepareStatement(checkRefugeesSql)) {
            pstmtCheck.setInt(1, azcId);
            ResultSet rs = pstmtCheck.executeQuery();
            if (rs.next() && rs.getInt("count") == 0) {
                String sql = "DELETE FROM azcs WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, azcId);
                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("[DEBUG] AZC deleted successfully with ID: " + azcId);
                    } else {
                        System.out.println("[DEBUG] No such AZC found with ID: " + azcId);
                    }
                }
            } else {
                System.out.println("[FAILED] AZC cannot be deleted as it has refugees housed.");
            }
        } catch (SQLException e) {
            System.out.println("[FAILED] Error deleting AZC: " + e.getMessage());
            throw e;
        }
    }

    public void updateMunicipalityPlaces(String name, int placesOffered) throws SQLException {
        String sql = "UPDATE municipalities SET places_offered = ? WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, placesOffered);
            pstmt.setString(2, name);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("[DEBUG] Updated places offered successfully for: " + name);
            } else {
                System.out.println("[DEBUG] No such municipality found: " + name);
            }
        } catch (SQLException e) {
            System.out.println("[FAILED] Error updating places offered for municipality: " + e.getMessage());
            throw e;
        }
    }

    public void listMunicipalitiesWithPlaces() throws SQLException {
        String sql = "SELECT name, population, places_offered FROM municipalities";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                int population = rs.getInt("population");
                int placesOffered = rs.getInt("places_offered");
                System.out.println(name + " - Population: " + population + ", Places Offered: " + placesOffered);
            }
        } catch (SQLException e) {
            System.out.println("[FAILED] Error listing municipalities with places offered: " + e.getMessage());
            throw e;
        }
    }

    public List<String> generatePaymentsReport() throws SQLException {
        List<String> reportLines = new ArrayList<>();
        String sql = "SELECT m.id, m.name, m.population, COUNT(r.id) AS hosted_refugees " +
                "FROM municipalities m " +
                "LEFT JOIN refugees r ON m.id = r.municipality_id " +
                "GROUP BY m.id, m.name, m.population";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String municipalityName = rs.getString("name");
                int population = rs.getInt("population");
                int hostedRefugees = rs.getInt("hosted_refugees");

                double requiredRefugees = population * 0.005;
                int payment = 0;

                if (hostedRefugees < requiredRefugees + 100) {
                    payment = 1000 * hostedRefugees;
                } else {
                    payment = 2000 * hostedRefugees;
                }

                String reportLine = String.format("Municipality: %s, Population: %d, Hosted Refugees: %d, Required Refugees: %.2f, Payment: €%,d",
                        municipalityName, population, hostedRefugees, requiredRefugees, payment);
                reportLines.add(reportLine);
            }
        } catch (SQLException e) {
            System.out.println("[FAILED] Error generating payments report: " + e.getMessage());
            throw e;
        }
        return reportLines;
    }


    public void listCountries() throws SQLException {
        String sql = "SELECT name, is_safe FROM countries";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                boolean isSafe = rs.getBoolean("is_safe");
                System.out.println(name + " - " + (isSafe ? "Safe" : "Not Safe"));
            }
        } catch (SQLException e) {
            System.out.println("[FAILED] Error listing countries: " + e.getMessage());
            throw e;
        }
    }

    public void addRefugee(String name, String countryOfOrigin, boolean hasPassport) throws SQLException {
        String sql = "INSERT INTO refugees (name, country_of_origin, has_passport, has_residence_permit) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, countryOfOrigin);
            pstmt.setBoolean(3, hasPassport);
            pstmt.setBoolean(4, false);
            pstmt.executeUpdate();
            System.out.println("[DEBUG] Refugee added successfully: " + name);
        } catch (SQLException e) {
            System.out.println("[FAILED] Error adding refugee: " + e.getMessage());
            throw e;
        }
    }

    public String findMunicipalityWithMostAvailablePlaces() throws SQLException {
        String sql = "SELECT name, (places_offered - places_filled) AS available_places " +
                "FROM municipalities " +
                "ORDER BY available_places DESC, places_offered DESC " +
                "LIMIT 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt("available_places") > 0) {
                return rs.getString("name");
            }
        }
        return null;
    }

    public String findMunicipalityWithLowestRelativePlaces() throws SQLException {
        String sql = "SELECT name, (CAST(places_offered AS REAL) / population) AS relative_places " +
                "FROM municipalities " +
                "ORDER BY relative_places ASC, places_offered ASC " +
                "LIMIT 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return null;
    }
}
