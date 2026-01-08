package hu.amoba.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import hu.amoba.model.Board;

/**
 * Ez az osztály felel a tábla fájlba mentéséért és onnan történő betöltéséért. A BoardIO (Board Input/Output) egy egyszerű segédosztály,
 * amely fájlkezelést végez az amőba játék táblájával kapcsolatban. Ha a mentett fájl létezik, onnan tölti be a táblát.
 * Ha a fájl hiányzik vagy hibás, akkor egy új, üres táblát hoz létre.
 * Két formátumot kezelünk: Egyszerű szöveg (board.txt) és XML (board.xml)
 */

public final class BoardIO {                        // Utility osztály: tábla mentés/betöltés fájlba

    private BoardIO() {                             // Privát konstruktor: ne lehessen példányosítani
    }

    /** Betölti a táblát a megadott fájlból, vagy ha az nem létezik / hibás, akkor létrehoz egy üres táblát az alapértelmezett méretekkel.
     *
     * @param path         a fájl elérési útvonala
     * @param defaultRows  alapértelmezett sorok száma, ha nincs fájl
     * @param defaultCols  alapértelmezett oszlopok száma, ha nincs fájl
     * @return             a betöltött vagy üres Board objektum
     */

    public static Board loadOrEmpty(Path path, int defaultRows, int defaultCols) {
        // Betölti a táblát szövegfájlból; ha nincs/hibás, akkor alapértelmezett méretű üres táblát ad
        if (!Files.exists(path)) {                             // Ha nincs ilyen tábla,
            return new Board(defaultRows, defaultCols);        // akkor új jön létre.
        }

        try (BufferedReader br = Files.newBufferedReader(path)) { // Megnyitja a fájlt olvasásra (automatikusan bezáródik)
            String header = br.readLine();                        // Beolvassa az első sort ("sor oszlop")
            if (header == null) {                                 // Ha üres, akkor
                return new Board(defaultRows, defaultCols);       // új tábla jön létre.
            }

            String[] parts = header.trim().split("\\s+");   // Feldarabolja az első sort szóközök mentén
            int rows = Integer.parseInt(parts[0]);                // Sorok száma
            int cols = Integer.parseInt(parts[1]);                // Oszlopok száma

            Board b = new Board(rows, cols);                      // Létrehoz egy táblát a fájlban tárolt mérettel
            char[][] cells = b.getCells();                        // Lekéri a tábla celláit tartalmazó kétdimenziós tömböt.

            for (int r = 0; r < rows; r++) {                      // Végigmegy soronként
                String line = br.readLine();                      // Beolvassa a következő sort (a tábla egyik sora)
                if (line == null) {                               // Ha elfogyott a fájl,
                    break;                                        // kilép (a maradék sor üres marad)
                }

                for (int c = 0; c < Math.min(cols, line.length()); c++) {
                    // Végigmegy a sor karakterein, de csak addig, amíg van oszlop és van karakter
                    char ch = line.charAt(c);                     // Kiveszi a karaktert (X/O/-)
                    if (ch == 'X' || ch == 'O' || ch == '-') {    // Csak a megengedett jeleket fogadja el
                        cells[r][c] = ch;
                    }
                }
            }
            return b;                                             // Visszaadja a betöltött táblát

        } catch (Exception e) {                                   // Fájlbetöltési hibakezelés
            System.out.println("Hiba a beolvasásnál, üres tábla indul. " + e.getMessage()); // Hibaüzenet
            return new Board(defaultRows, defaultCols);           // Üres táblához tér vissza
        }
    }

    /** Elmenti a tábla aktuális állapotát egy szövegfájlba. Első sor: sorok és oszlopok száma, utána a tábla tartalma.
     *
     * @param board a mentendő tábla
     * @param path  a célfájl elérési útvonala
     */

    public static void save(Board board, Path path) {
        // Elmenti a táblát szövegfájlba: első sor méret, utána a sorok tartalma
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {  // Megnyitja a fájlt írásra
            bw.write(board.getRows() + " " + board.getCols()); // 1. sor: "rows cols"
            bw.newLine();                                          // Új sor


            char[][] cells = board.getCells();                     // Elkéri a tábla celláit
            for (int r = 0; r < board.getRows(); r++) {            // Végigmegy soronként
                for (int c = 0; c < board.getCols(); c++) {        // Végigmegy oszloponként
                    bw.write(cells[r][c]);                         // Kiírja a cella karakterét
                }
                bw.newLine();                                      // Sor vége a fájlban
            }

        } catch (IOException e) {                                  // Fájlírási hibakezelés
            System.out.println("Hiba a mentésnél: " + e.getMessage()); // Hibaüzenet
        }
    }

    /** Elmenti a táblát XML formátumban. Ez egy olvashatóbb mentési forma, ahol a cellák és a méretek is külön tagek között szerepelnek.
     *
     * @param board      a mentendő tábla
     * @param file       a célfájl (pl. board.xml)
     * @param playerName a játékos neve, opcionális
     */

    public static void saveToXml(Board board, Path file, String playerName) {
        // Elmenti a táblát egy egyszerű XML formátumba
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) { // Megnyitja a fájlt írásra
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); // XML fejléc
            if (playerName != null && !playerName.isBlank()) {      // Ha van játékosnév,
                out.println("<!-- Player: " + playerName + " -->"); // XML kommentként kiírjuk.
            }

            out.println("<board rows=\"" + board.getRows()          // Sorok száma attribútumban
                    + "\" cols=\"" + board.getCols() + "\">");      // Oszlopok száma attribútumban

            for (int r = 0; r < board.getRows(); r++) {             // Végigmegy soronként
                out.print("<row>");                                 // Egy sor kezdete
                for (int c = 0; c < board.getCols(); c++) {         // Végigmegy oszloponként
                    char cell = board.getCells()[r][c];             // Kiveszi a cella jelét
                    out.print(cell);                                // Kiírja a jelet a <row> tagbe
                }
                out.println("</row>");                              // Sor vége taggel lezárva
            }
            out.println("</board>");                                // Lezárja a board taget
            System.out.println("XML mentés kész: " + file.toAbsolutePath()); // Info kiírás a konzolra

        } catch (IOException e) {                                   // Fájlírási hibakezelés
            System.out.println("Hiba az XML mentés közben: " + e.getMessage()); // Hibaüzenet
        }
    }

    /** Betölti a táblát egy XML fájlból. Ha a fájl hibás vagy nem található, 10x10-es üres tábla készül helyette.
     *
     * @param file a betöltendő XML fájl (pl. board.xml)
     * @return a betöltött Board objektum, vagy hiba esetén új üres tábla
     */

    public static Board loadFromXml(Path file) {
        // Betölti a táblát az XML fájlból; hiba esetén 10x10 üres táblát ad
        try {
            if (!Files.exists(file)) {                              // Ha a fájl nem létezik
                System.out.println("Nincs XML tábla, új üres tábla indul."); // Új tábla
                return new Board(10, 10);                // Alap üres tábla
            }

            List<String> lines = Files.readAllLines(file);          // Beolvassa az összes sort a fájlból
            int rows = 0;                                           // Sorok száma
            int cols = 0;                                           // Oszlopok száma
            List<String> content = new ArrayList<>();               // Ebbe gyűjti a <row> sorok szövegét

            for (String raw : lines) {                              // Végigmegy a beolvasott sorokon
                String line = raw.trim();                           // Levágja a szóközöket a sor elejéről/végéről
                if (line.startsWith("<board")) {                    // Ha ez a <board ...> sor
                    // <board rows="10" cols="10">
                    String[] parts = line.split("\"");        // Feldarabolja idézőjelek mentén
                    // parts[1] = rows, parts[3] = cols
                    rows = Integer.parseInt(parts[1]);              // rows="10" -> parts[1] = 10
                    cols = Integer.parseInt(parts[3]);              // cols="10" -> parts[3] = 10
                } else if (line.startsWith("<row>")) {              // Ha ez egy <row>....</row> sor
                    content.add(line
                            .replace("<row>", "")  // Kiszedi a nyitó taget
                            .replace("</row>", "")); // Kiszedi a záró taget
                }
            }

            if (rows <= 0 || cols <= 0 || content.isEmpty()) {          // Ha nincs rendes méret vagy nincs sor tartalom
                System.out.println("Hibás XML, új üres tábla indul.");  // Üzenet
                return new Board(10, 10);                    // Üres tábla (fix 10x10)
            }

            Board b = new Board(rows, cols);                            // Létrehozza a táblát a fájl szerinti mérettel
            for (int r = 0; r < Math.min(rows, content.size()); r++) {  // Védekezés hibás XML ellen: nem futunk túl a listán
                String rowText = content.get(r);
                for (int c = 0; c < Math.min(cols, rowText.length()); c++) { // Végigmegy a karaktereken
                    char ch = rowText.charAt(c);                        // Kivesz egy jelet
                    if (ch == 'X' || ch == 'O' || ch == '-') {          // Csak megengedett jeleket enged
                        b.getCells()[r][c] = ch;                        // Beírja a Board-ba (a Board metódusán keresztül)
                    }
                }
            }

            System.out.println("XML betöltés kész: " + file.toAbsolutePath());    // Info kiírás
            return b;                                                             // Visszaadja a betöltött táblát

        } catch (Exception e) {                                                   // Ha bármi hiba van olvasás/parsing közben
            System.out.println("Hiba az XML betöltés közben: " + e.getMessage()); // Hibaüzenet
            return new Board(10, 10);                                  // Üres tábla (fix 10x10)
        }
    }
}
