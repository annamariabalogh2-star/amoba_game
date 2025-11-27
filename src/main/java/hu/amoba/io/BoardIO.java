package hu.amoba.io;

import hu.amoba.model.Board;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

/** Ez az osztály felel a tábla fájlba mentéséért és onnan történő betöltéséért.
 * A BoardIO (Board Input/Output) egy egyszerű segédosztály, amely fájlkezelést végez az amőba játék táblájával kapcsolatban.
 * - Ha a mentett fájl létezik, onnan tölti be a táblát.
 * - Ha a fájl hiányzik vagy hibás, akkor egy új, üres táblát hoz létre.
 * A táblát többféle formátumban is tudjuk kezelni:
 * - Egyszerű szöveges formátum (a sorok és oszlopok száma, majd maga a tábla)
 * - XML formátum, amely olvashatóbb és akár későbbi bővítésre is alkalmas.
 * A fájlkezelés minden lépése try-with-resources blokkban történik, így a fájlok automatikusan bezáródnak hiba esetén is. */

public class BoardIO {

    /** Betölti a táblát a megadott fájlból, vagy ha az nem létezik, akkor létrehoz egy üres táblát az alapértelmezett méretekkel.
     *
     * - @param path         a fájl elérési útvonala
     * - @param defaultRows  alapértelmezett sorok száma, ha nincs fájl
     * - @param defaultCols  alapértelmezett oszlopok száma, ha nincs fájl
     * - @return             a betöltött vagy üres Board objektum. */
    public static Board loadOrEmpty(Path path, int defaultRows, int defaultCols) {
        if (!Files.exists(path)) {                                  // Ha nincs mentett fájl, új üres tábla készül
            return new Board(defaultRows, defaultCols);
        }
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String header = br.readLine();                           // Az első sorban a tábla méretei vannak (pl. "10 10")
            if (header == null)
                return new Board(defaultRows, defaultCols);

            String[] parts = header.trim().split("\\s+");
            int rows = Integer.parseInt(parts[0]);
            int cols = Integer.parseInt(parts[1]);

            Board b = new Board(rows, cols);                        // Új tábla létrehozása a fájlban megadott méretekkel
            char[][] cells = b.getCells();
            for (int r = 0; r < rows; r++) {                        // A következő sorok a tábla aktuális állapotát tartalmazzák
                String line = br.readLine();
                if (line == null)
                    break;

                for (int c = 0; c < Math.min(cols, line.length()); c++) {
                    char ch = line.charAt(c);
                    if (ch == 'X' || ch == 'O' || ch == '-') {        // Csak az engedélyezett karaktereket vesszük figyelembe
                        cells[r][c] = ch;
                    }
                }
            }
            return b;

        } catch (Exception e) {
            System.out.println("Hiba a beolvasásnál, üres tábla indul. " + e.getMessage());
            return new Board(defaultRows, defaultCols);
        }
    }

    /** Elmenti a tábla aktuális állapotát egy szövegfájlba. A fájl első sorában a tábla mérete található (sorok és oszlopok száma),
     * utána pedig soronként a tábla tartalma, például:
     * - 10 10
     * - X--O-------
     * - -XO--------
     * ----------, stb.
     * - @param b    a mentendő tábla
     * - @param path a célfájl elérési útvonala */
    public static void save(Board b, Path path) {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            bw.write(b.getRows() + " " + b.getCols());              // Első sor: méretek
            bw.newLine();

            char[][] cells = b.getCells();                              // A tábla celláinak kiírása soronként
            for (int r = 0; r < b.getRows(); r++) {
                for (int c = 0; c < b.getCols(); c++) {
                    bw.write(cells[r][c]);
                }
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Hiba a mentésnél: " + e.getMessage());
        }
    }

    /** Elmenti a táblát XML formátumban. Ez egy olvashatóbb mentési forma, ahol a cellák és a méretek is külön
     * tag-ek között szerepelnek. A fájl elején a játékos neve is megadható.
     * Példa egy mentett fájlra:
     * <pre>
     * # Player: Anna
     * <board rows="10" cols="10">
     * <row>X-O------O</row>
     * <row>--X-------</row>
     * ...
     * </board>
     * </pre>
     *
     * - @param board      a mentendő tábla
     * - @param file       a célfájl elérési útvonala
     * - @param playerName a játékos neve, opcionális */
    public static void saveToXml(Board board, Path file, String playerName) {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
            if (playerName != null && !playerName.isBlank()) {               // Ha van játékosnév, azt kommentként az első sorba írjuk
                out.println("# Player: " + playerName);
            }
            out.println("<board rows=\"" + board.getRows() + "\" cols=\"" + board.getCols() + "\">"); // XML tag kezdete: a tábla méreteit is eltároljuk
            for (int r = 0; r < board.getRows(); r++) {                      // Minden sort külön <row> tag-be írunk
                out.print("<row>");
                for (int c = 0; c < board.getCols(); c++) {
                    char cell = board.getCells()[r][c];
                    out.print(cell == '-' ? '.' : cell);                     // Az üres mezőt pontként jelöljük, hogy olvashatóbb legyen
                }
                out.println("</row>");
            }
            out.println("</board>");
            System.out.println("XML mentés kész: " + file.toAbsolutePath());

        } catch (IOException e) {
            System.out.println("Hiba az XML mentés közben: " + e.getMessage());
        }
    }

    /** Betölti a táblát egy XML fájlból. A metódus az XML tag-eket soronként olvassa be, és azok alapján hozza létre
     * a megfelelő méretű Board objektumot, majd feltölti az X és O jelekkel.
     * Ha a fájl hibás, hiányos, vagy nem található, akkor egy 10x10-es üres tábla készül helyette, hogy a játék folytatható maradjon.
     * - @param file a betöltendő XML fájl
     * - @return a betöltött Board objektum, vagy hiba esetén új üres tábla */
    public static Board loadFromXml(Path file) {
        try {
            List<String> lines = Files.readAllLines(file);
            int rows = 0, cols = 0;
            List<String> content = new ArrayList<>();
            for (String line : lines) {                                     // Az XML sorok feldolgozása
                line = line.trim();
                if (line.startsWith("<board")) {
                    String[] parts = line.split("\"");                // A méretek kiolvasása az attribútumokból
                    rows = Integer.parseInt(parts[1]);
                    cols = Integer.parseInt(parts[3]);
                } else if (line.startsWith("<row>")) {
                    content.add(line.replace("<row>", "").replace("</row>", "")); // A tábla tartalmát gyűjtjük soronként
                }
            }

            Board b = new Board(rows, cols);                                // Tábla újbóli felépítése
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    char ch = content.get(r).charAt(c);
                    if (ch == 'X' || ch == 'O') {
                        b.place(r, c, ch);
                    }
                }
            }

            System.out.println("XML betöltés kész: " + file.toAbsolutePath());
            return b;

        } catch (Exception e) {
            System.out.println("Hiba az XML betöltés közben: " + e.getMessage());
            return new Board(10, 10);                              // Hiba esetén üres tábla
        }
    }
}
