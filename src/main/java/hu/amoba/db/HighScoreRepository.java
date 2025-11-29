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
            System.err.println("Hiba az adatbazis inicializalasakor: " + e.getMessage());
        }
    }

    /** Növeli egy játékos pontjait a megadott értékkel. A tábla "wins" oszlopa most valójában a pontokat tárolja:
     * győzelem = 3 pont, döntetlen = 1 pont, vereség = 0 pont. */

    public void addPoints(String playerName, int points) {
        String sql = """
            INSERT INTO highscore(player_name, wins)
            VALUES(?, ?)
            ON CONFLICT(player_name) DO UPDATE SET wins = wins + ?;
            """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerName);
            ps.setInt(2, points);
            ps.setInt(3, points);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Hiba a pontok mentesekor: " + e.getMessage());
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
            System.err.println("Hiba az eredmenyek lekerdezesekor: " + e.getMessage());
        }
        return results;
    }

    public void printHighScores() {
        Map<String, Integer> all = getAll();

        System.out.println();
        System.out.println("-- Ponttabla (3 pont gyozelem, 1 pont dontetlen) --");

        if (all.isEmpty()) {
            System.out.println("Meg nincs elmentett eredmeny.");
            return;
        }

        System.out.printf("%-20s %s%n", "Jatekos", "Pont");
        System.out.println("------------------------------");

        for (var entry : all.entrySet()) {
            System.out.printf("%-20s %d%n", entry.getKey(), entry.getValue());
        }
    }
}
