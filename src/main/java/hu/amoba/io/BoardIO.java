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

/**
 * Egy egyszerű fájl alapú be/ki a táblához.
 * Ha a fájl nem létezik: üres tábla (Game oldja meg a default lépést).
 */
public class BoardIO {

    public static Board loadOrEmpty(Path path, int defaultRows, int defaultCols) {
        if (!Files.exists(path)) {
            return new Board(defaultRows, defaultCols);
        }
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String header = br.readLine();
            if (header == null)
                return new Board(defaultRows, defaultCols);

            String[] parts = header.trim().split("\\s+");
            int rows = Integer.parseInt(parts[0]);
            int cols = Integer.parseInt(parts[1]);

            Board b = new Board(rows, cols);
            char[][] cells = b.getCells();
            for (int r = 0; r < rows; r++) {
                String line = br.readLine();
                if (line == null)
                    break;

                for (int c = 0; c < Math.min(cols, line.length()); c++) {
                    char ch = line.charAt(c);
                    if (ch == 'X' || ch == 'O' || ch == '-') {
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

    public static void save(Board b, Path path) {
        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            bw.write(b.getRows() + " " + b.getCols());
            bw.newLine();
            char[][] cells = b.getCells();
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

    // --- XML mentés és betöltés folyamata ---
    public static void saveToXml(Board board, Path file, String playerName) {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(file))) {
            // Ha a játékosnév elérhető, írjuk az első sorba kommentként
            if (playerName != null && !playerName.isBlank()) {
                out.println("# Player: " + playerName);
            }
            out.println("<board rows=\"" + board.getRows() + "\" cols=\"" + board.getCols() + "\">");
            for (int r = 0; r < board.getRows(); r++) {
                out.print("<row>");
                for (int c = 0; c < board.getCols(); c++) {
                    char cell = board.getCells()[r][c];
                    out.print(cell == '-' ? '.' : cell); // üres hely pontként
                }
                out.println("</row>");
            }

            out.println("</board>");
            System.out.println("XML mentés kész: " + file.toAbsolutePath());

        } catch (IOException e) {
            System.out.println("Hiba az XML mentés közben: " + e.getMessage());
        }


    }

    public static Board loadFromXml(Path file) {
        try {
            List<String> lines = Files.readAllLines(file);
            int rows = 0, cols = 0;
            List<String> content = new ArrayList<>();

            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("<board")) {
                    String[] parts = line.split("\""); // helyes split
                    rows = Integer.parseInt(parts[1]);
                    cols = Integer.parseInt(parts[3]);
                } else if (line.startsWith("<row>")) {
                    content.add(line.replace("<row>", "").replace("</row>", ""));
                }
            }

            Board b = new Board(rows, cols);
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
            return new Board(10, 10); // hiba esetén üres tábla
        }
    }
}
