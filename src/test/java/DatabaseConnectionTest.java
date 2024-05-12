
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
import org.example.DatabaseConnection;

public class DatabaseConnectionTest {
    private DatabaseConnection db;

    @BeforeEach
    public void setUp() throws SQLException {
        db = new DatabaseConnection();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        db.getConnection().close();
    }


    @Test
    public void testAddCountry() throws SQLException {
        String countryName = "Testland";
        boolean isSafe = true;

        db.addCountry(countryName, isSafe);


        var stmt = db.getConnection().prepareStatement("SELECT name, is_safe FROM countries WHERE name = ?");
        stmt.setString(1, countryName);
        var rs = stmt.executeQuery();

        assertTrue(rs.next());
        assertEquals(countryName, rs.getString("name"));
        assertEquals(isSafe, rs.getBoolean("is_safe"));
    }

    @Test
    public void testUpdateMunicipalityPlaces() throws SQLException {
        String municipalityName = "MuniTest";
        int initialPopulation = 5000;
        db.addMunicipality(municipalityName, initialPopulation);

        int newPlacesOffered = 150;

        db.updateMunicipalityPlaces(municipalityName, newPlacesOffered);

        var stmt = db.getConnection().prepareStatement("SELECT name, places_offered FROM municipalities WHERE name = ?");
        stmt.setString(1, municipalityName);
        var rs = stmt.executeQuery();

        assertTrue(rs.next());
        assertEquals(newPlacesOffered, rs.getInt("places_offered"));
    }

    @Test
    public void testFindMunicipalityWithMostAvailablePlaces() throws SQLException {
        db.addMunicipality("Muni1", 1000);
        db.updateMunicipalityPlaces("Muni1", 250);

        db.addMunicipality("Muni2", 2000);
        db.updateMunicipalityPlaces("Muni2", 300);

        db.addMunicipality("Muni3", 1500);
        db.updateMunicipalityPlaces("Muni3", 350);

        String expected = "Muni3";
        String result = db.findMunicipalityWithMostAvailablePlaces();

        assertEquals(expected, result);
    }
}