package hu.amoba.io;

import hu.amoba.model.Board;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Ez az osztály felel a tábla fájlba mentéséért és onnan történő betöltéséért. A BoardIO (Board Input/Output) egy egyszerű segédosztály,
 * amely fájlkezelést végez az amőba játék táblájával kapcsolatban. Ha a mentett fájl létezik, onnan tölti be a táblát.
 * Ha a fájl hiányzik vagy hibás, akkor egy új, üres táblát hoz létre.
 * Két formátumot kezelünk: Egyszerű szöveg (board.txt) és XML (board.xml) */

public final class BoardIO {

    private BoardIO() {                                       // utility osztály, ne lehessen példányosítani
    }

    /** Betölti a táblát a megadott fájlból, vagy ha az nem létezik / hibás, akkor létrehoz egy üres táblát az alapértelmezett méretekkel.
     * @param path        a fájl elérési útvonala
     * @param defaultRows alapértelmezett sorok száma, ha nincs fájl
     * @param defaultCols alapértelmezett oszlopok száma, ha nincs fájl
     * @return a betöltött vagy üres Board objektum */

    public static Board loadOrEmpty(Path path, int defaultRows, int defaultCols) {
        if (!Files.exists(path)) {                             // Ha nincs mentett fájl, akkor tábla jön létre.
            return new Board(defaultRows, defaultCols);
        }

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String header = br.readLine();                      // első sor: "sor oszlop"
            if (header == null) {
                return new Board(defaultRows, defaultCols);
            }

            String[] parts = header.trim().split("\\s+");
            int rows = Integer.parseInt(parts[0]);
            int cols = Integer.parseInt(parts[1]);

            Board b = new Board(rows, cols);
            char[][] cells = b.getCells();

            for (int r = 0; r < rows; r++) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }

                for (int c = 0; c < Math.min(cols, line.length()); c++) {
                    char ch = line.charAt(c);
                    if (ch == 'X' || ch == 'O' || ch == '-') {
                        cells[r][c] = ch;
                    }
                }
            }
            return b;

        } catch (Exception e) {
            System.out.println("Hiba a beolvasasnal, ures tabla indul. " + e.getMessage());
            return new Board(defaultRows, defaultCols);
        }
    }

    /** Elmenti a tábla aktuális állapotát egy szövegfájlba. Első sor: sorok és oszlopok száma, utána a tábla tartalma.
     * @param board a mentendő tábla
     * @param path  a célfájl elérési útvonala */
    public static void save(Board board, Path path) {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {   // Első sor: méretek
            bw.write(board.getRows() + " " + board.getCols());
            bw.newLine();

                                                                    // A tábla celláinak kiírása soronként
            char[][] cells = board.getCells();
            for (int r = 0; r < board.getRows(); r++) {
                for (int c = 0; c < board.getCols(); c++) {
                    bw.write(cells[r][c]);
                }
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Hiba a mentesnel: " + e.getMessage());
        }
    }

    /** Elmenti a táblát XML formátumban. Ez egy olvashatóbb mentési forma, ahol a cellák és a méretek is külön tagek között szerepelnek.
     * @param board      a mentendő tábla
     * @param file       a célfájl (pl. board.xml)
     * @param playerName a játékos neve, opcionális */

    public static void saveToXml(Board board, Path file, String playerName) {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
            if (playerName != null && !playerName.isBlank()) {      // Ha van játékosnév, azt kommentként az első sorba írjuk
                out.println("# Player: " + playerName);
            }

                                                                    // XML tag kezdete: a tábla méreteit is eltároljuk
            out.println("<board rows=\"" + board.getRows()
                    + "\" cols=\"" + board.getCols() + "\">");

            for (int r = 0; r < board.getRows(); r++) {
                out.print("<row>");
                for (int c = 0; c < board.getCols(); c++) {
                    char cell = board.getCells()[r][c];              // Üres mezőnél is a '-' jelet használjuk, hogy konzisztens legyen
                    out.print(cell);
                }
                out.println("</row>");
            }
            out.println("</board>");
            System.out.println("XML mentes kesz: " + file.toAbsolutePath());

        } catch (IOException e) {
            System.out.println("Hiba az XML mentes kozben: " + e.getMessage());
        }
    }

    /** Betölti a táblát egy XML fájlból. Ha a fájl hibás vagy nem található, 10x10-es üres tábla készül helyette.
     * @param file a betöltendő XML fájl (pl. board.xml)
     * @return a betöltött Board objektum, vagy hiba esetén új üres tábla */

    public static Board loadFromXml(Path file) {
        try {
            if (!Files.exists(file)) {
                System.out.println("Nincs XML tabla, uj ures tabla indul.");
                return new Board(10, 10);
            }

            List<String> lines = Files.readAllLines(file);
            int rows = 0;
            int cols = 0;
            List<String> content = new ArrayList<>();

            for (String raw : lines) {
                String line = raw.trim();
                if (line.startsWith("<board")) {
                    // <board rows="10" cols="10">
                    String[] parts = line.split("\"");
                    // parts[1] = rows, parts[3] = cols
                    rows = Integer.parseInt(parts[1]);
                    cols = Integer.parseInt(parts[3]);
                } else if (line.startsWith("<row>")) {
                    content.add(line
                            .replace("<row>", "")
                            .replace("</row>", ""));
                }
            }

            if (rows <= 0 || cols <= 0 || content.isEmpty()) {
                System.out.println("Hibas XML, uj ures tabla indul.");
                return new Board(10, 10);
            }

            Board b = new Board(rows, cols);
            for (int r = 0; r < rows; r++) {
                String rowText = content.get(r);
                for (int c = 0; c < Math.min(cols, rowText.length()); c++) {
                    char ch = rowText.charAt(c);
                    if (ch == 'X' || ch == 'O' || ch == '-') {
                        b.place(r, c, ch);
                    }
                }
            }

            System.out.println("XML betoltes kesz: " + file.toAbsolutePath());
            return b;

        } catch (Exception e) {
            System.out.println("Hiba az XML betoltes kozben: " + e.getMessage());
            return new Board(10, 10); // Hiba esetén üres tábla
        }
    }
}
