package hu.amoba.db;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Az osztály az adatbázisban tárolja a játékosok győzelmeit (amoba.db).
 */
public class HighScoreRepository {
    private static final String DB_URL = "jdbc:sqlite:amoba.db";

    public HighScoreRepository() {
        createTableIfNeeded();
    }

    /** Létrehozza a táblát, ha még nem létezik. */
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

    /** Növeli a megadott játékos győzelmeinek számát (ha nem létezik, létrehozza). */
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

    /** Lekérdezi az összes játékos nevét és győzelmeinek számát. */
    public Map<String, Integer> getAll() {
        Map<String, Integer> results = new LinkedHashMap<>();
        String sql = "SELECT player_name, wins FROM highscore ORDER BY wins DESC, player_name ASC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.put(rs.getString("player_name"), rs.getInt("wins"));
            }
        } catch (SQLException e) {
            System.err.println("Hiba az eredmények lekérdezésekor: " + e.getMessage());
        }
        return results;
    }

    /** High score lista kiíratása a konzolra. */
    public void printHighScores() {
        System.out.println("\n-- High Score --");
        for (var entry : getAll().entrySet()) {
            System.out.printf("%-20s %d%n", entry.getKey(), entry.getValue());
        }
    }
}
