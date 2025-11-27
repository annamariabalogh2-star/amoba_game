package hu.amoba.db;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

/** Ez az osztály az amőba játék adatbázis-kezeléséért felel.
 * A HighScoreRepository gondoskodik arról, hogy a játékosok győzelmeit (pontszámait) eltároljuk egy SQLite adatbázisban.
 * Az adatbázis neve: amoba.db.
 * Az osztály automatikusan létrehozza a szükséges táblát, ha az még nem létezik.
 * A tábla neve: "highscore", amely két oszlopot tartalmaz:
 *  - player_name: a játékos neve (kulcs)
 *  - wins: hány győzelmet ért el az adott játékos
 * A program így képes megőrizni, hány meccset nyert meg valaki a játék korábbi futtatásai során is.
 * Az osztályhoz nincs szükség külön beállításokra vagy külső fájlokra, minden automatikusan létrejön, ha a játék elindul. */

public class HighScoreRepository {

    /** Az SQLite adatbázis elérési útvonala, a fájl a projekt gyökérkönyvtárában jön létre (amoba.db néven). */
    private static final String DB_URL;

    static {
        // Biztonságos hely: C:\Users\<név>\amoba_db\eredmenyek.db
        String home = System.getProperty("user.home");
        Path dir = Path.of(home, "amoba_db");
        try {
            Files.createDirectories(dir);  // mappa létrehozása, ha nem létezik
        } catch (IOException e) {
            System.err.println("Nem sikerült létrehozni a mappát: " + e.getMessage());
        }
        DB_URL = "jdbc:sqlite:" + dir.resolve("eredmenyek.db").toString();
    }

    /** Konstruktor: létrehozza az adatbázist és a szükséges táblát, ha még nem létezne. */
    public HighScoreRepository() {
        createTableIfNeeded();
    }

    /** Létrehozza a "highscore" nevű táblát, ha az még nem létezik.
     * A tábla szerkezete:
     *  - player_name (szöveg, elsődleges kulcs)
     *  - wins (egész szám, alapértelmezetten 0)
     * Ez a metódus gondoskodik róla, hogy az adatbázis mindig használatra kész legyen. */
    private void createTableIfNeeded() {
        String sql = """
            CREATE TABLE IF NOT EXISTS highscore(
                player_name TEXT PRIMARY KEY,
                wins INTEGER NOT NULL DEFAULT 0
            );
            """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Hiba az adatbázis inicializálásakor: " + e.getMessage());
        }
    }

    /** Növeli a megadott játékos győzelmeinek számát. Ha a játékos még nem szerepel az adatbázisban, automatikusan
     * felkerül 1 győzelemmel. Ha már létezik, a nyert meccsek száma eggyel nő.
     * Példa:
     *  - incWin("Anna") → ha Anna még nincs a táblában, bekerül 1 győzelemmel.
     *  - újabb hívás esetén Anna már 2 győzelemmel fog szerepelni. */
    public void incWin(String playerName) {
        String sql = """
            INSERT INTO highscore(player_name, wins)
            VALUES(?, 1)
            ON CONFLICT(player_name) DO UPDATE SET wins = wins + 1;
            """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Hiba a győzelem mentésekor: " + e.getMessage());
        }
    }

    /** Lekérdezi az összes játékos nevét és győzelmeinek számát.
     * Az eredmény egy rendezett lista (LinkedHashMap formában), amelyet a nyert meccsek száma szerint csökkenő
     * sorrendben ad vissza. Az azonos pontszámú játékosok nevei abc rendben jelennek meg.
     * @return Map, ahol a kulcs a játékos neve, az érték pedig a győzelmek száma. */
    public Map<String, Integer> getAll() {
        Map<String, Integer> results = new LinkedHashMap<>();
        String sql = "SELECT player_name, wins FROM highscore ORDER BY wins DESC, player_name ASC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {                         // Soronként beolvassuk az adatokat az eredményhalmazból
                results.put(rs.getString("player_name"), rs.getInt("wins"));
            }
        } catch (SQLException e) {
            System.err.println("Hiba az eredmények lekérdezésekor: " + e.getMessage());
        }
        return results;
    }
}
