package hu.amoba.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

/** Ez az osztály az amőba játék ponttáblájának (hihg-score) kezeléséért felel.
 * Egy SQLite adatbázisba menti a játékosok nevét és pontszámát. Az adatbázis fájl helye a felhasználó home mappájában
 * lesz:  .../amoba_db, a fájl pedig eredmenyek.db. A használt tábla neve: "highscore".
 * Oszlopai:
 *   - player_name : a játékos neve (PRIMARY KEY, tehát egy név csak egyszer szerepelhet)
 *   - wins        : az adott játékos pontszáma (egész szám)
 * Pontszámok:
 *   - győzelem  = 3 pont
 *   - döntetlen = 1 pont
 *   - vereség   = 0 pont
 */

public class HighScoreRepository {                          // Adatbázis-kezelő osztály a ponttáblához

    private static final String DB_URL;                     // Az SQLite adatbázis elérési útja (jdbc:sqlite:...)

    static {                                                // Statikus blokk: egyszer fut le, amikor az osztály betöltődik
        String home = System.getProperty("user.home");      // Lekéri a felhasználó home mappáját (pl. C:\Users\Név)
        Path dir = Path.of(home, "amoba_db");        // Összerak egy mappautat: .../amoba_db
        try {                                               // Hibakezelés: ha nem sikerül a mappalétrehozás, ne álljon le
            Files.createDirectories(dir);                   // Létrehozza a mappát (ha már létezik, akkor nem gond)
        } catch (IOException e) {                           // Hibakezelés
            System.err.println("Nem sikerült létrehozni a mappát: " + e.getMessage()); // Hibaüzenet kiírása
        }
        DB_URL = "jdbc:sqlite:" + dir.resolve("eredmenyek.db").toString();  // Beállítja az adatbázis URL-t
    }

   public HighScoreRepository() {                           // Konstruktor, osztályok példányosítására
        createTableIfNeeded();                              // Biztosítja, hogy a tábla létezzen.
    }

    private void createTableIfNeeded() {                    // Privát segédfüggvény, osztályon belül használjuk.
        String sql = """
            CREATE TABLE IF NOT EXISTS highscore(
                player_name TEXT PRIMARY KEY,
                wins INTEGER NOT NULL DEFAULT 0
            );
            """;
        // Létrehoz egy "highscore" táblát, ha még nem létezik
        // player_name megadása szövegként, elsődleges kulcsnak beállítva (egyedi)
        // győzelem kiírása egész számmal, nem lehet nulla.
        try (Connection conn = DriverManager.getConnection(DB_URL); // Kapcsolódik az SQLite adatbázishoz
             Statement stmt = conn.createStatement()) {             // Létrehoz egy Statement objektumot az SQL futtatáshoz
            stmt.executeUpdate(sql);                                // Lefuttatja a CREATE TABLE parancsot
        } catch (SQLException e) {                                  // Hibakezelés
            System.err.println("Hiba az adatbázis inicializálásakor: " + e.getMessage()); // Hibaüzenet
        }
    }

    public void addPoints(String playerName, int points) {         // Pontot ad egy játékoshoz (beszúr vagy növel)
        String sql = """
            INSERT INTO highscore(player_name, wins)
            VALUES(?, ?)
            ON CONFLICT(player_name) DO UPDATE SET wins = wins + ?;
            """;
        // Új sor beszúrása (név, pont)
        // Értékek megadás
        // Ha már létezik ez a név (PRIMARY KEY ütközés)
        // Frissítés, a wins értékéhez hozzáadja a megadott pontot
        try (Connection conn = DriverManager.getConnection(DB_URL); // Kapcsolódik az adatbázishoz
             PreparedStatement ps = conn.prepareStatement(sql)) {   // Előkészített SQL, biztonságos paraméterezéssel
            ps.setString(1, playerName);              // Beállítja a játékos nevét
            ps.setInt(2, points);                     // Beállítja a beszúrandó pontot
            ps.setInt(3, points);                     // Beállítja, mennyivel növelje a pontot (UPDATE)
            ps.executeUpdate();                                    // Lefuttatja a beszúrás/frissítés parancsokat
        } catch (SQLException e) {                                 // Hibakezelés
            System.err.println("Hiba a pontok mentésekor: " + e.getMessage());  // Hibaüzenet
        }
    }

    public Map<String, Integer> getAll() {                          // Lekéri az összes játékos pontját rendezve
        Map<String, Integer> results = new LinkedHashMap<>();       // LinkedHashMap: megőrzi a beszúrási sorrendet
        String sql = "SELECT player_name, wins FROM highscore ORDER BY wins DESC, player_name ASC ";
        // Név és pont lekérése
        // Rendezés pont szerint csökkenő sorrendben
        // Ha ugyanannyi pont, név szerint ABC sorban
        try (Connection conn = DriverManager.getConnection(DB_URL); // Kapcsolódik az adatbázishoz
             Statement stmt = conn.createStatement();               // Statement a lekérdezés futtatásához
             ResultSet rs = stmt.executeQuery(sql)) {               // Lefuttatja a SELECT-et, eredmény: ResultSet
            while (rs.next()) {                                     // Soronként beolvassuk az adatokat az eredményhalmazból
                results.put(rs.getString("player_name"), // Kulcs: név oszlop értéke
                        rs.getInt("wins"));              // Érték: pont oszlop értéke
            }
        } catch (SQLException e) {                                  // Hibakezelés
            System.err.println("Hiba az eredmények lekérdezésekor: " + e.getMessage()); // Hibaüzenet
        }
        return results;                                             // Visszaadja a rendezett eredményeket
    }

    public void printHighScores() {                                 // Kiírja a ponttáblát a konzolra
        Map<String, Integer> all = getAll();                        // Lekéri a ponttáblát (név -> pont)

        System.out.println();                                       // üres sor
        System.out.println("-- Ponttábla (3 pont győzelem, 1 pont döntetlen) --");  // Fejléc kiirása

        if (all.isEmpty()) {                                        // Ha nincs adat az adatbázisban
            System.out.println("Még nincs elmentett eredmény.");    // Kiirja
            return;                                                 // Kilép a metódusból
        }

        System.out.printf("%-20s %s%n", "Jatekos", "Pont");         // Oszlopfejléc formázva (20 karakter széles név)
        System.out.println("------------------------------");       // Elválasztó

        for (var entry : all.entrySet()) {                          // Végigmegy minden (név,pont) páron
            System.out.printf("%-20s %d%n",                         // Kiírja a nevet és a pontot formázva
                    entry.getKey(),                                 // Játékos neve
                    entry.getValue());                              // Pontszám
        }
    }
}
