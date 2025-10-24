package hu.amoba.core;

import hu.amoba.db.HighScoreRepository;
import hu.amoba.io.BoardIO;
import hu.amoba.vo.Player;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Scanner;


/**
 * A játék fő vezérlőosztálya.
 *
 * Feladatai:
 * - A játék indítása és leállítása.
 * - A játéktábla betöltése vagy létrehozása.
 * - A játékosok lépéseinek kezelése (ember és gép).
 * - Mentés, betöltés, high score és egyéb parancsok feldolgozása.
 *
 * A módosított változat tesztbarát:
 * - Teszt módban nem indul interaktív játékmenet.
 * - Ha nincs bemenet (pl. CI környezetben), a játék biztonságosan leáll.
 */

public class Game {
    private Board board;
    private final AI ai = new AI();
    private final HighScoreRepository repo = new HighScoreRepository();
    private final Scanner sc = new Scanner(System.in);

    private Player human;
    private final Player cpu = new Player("Gep", 'O');

    private final Path boardFile = Path.of("board.txt");

    public Game() {
        // Alap: 10x10 tábla, de ha van fájl, onnan töltődik be.
        board = BoardIO.loadOrEmpty(boardFile, 10, 10);
    }

    public void start() {
        // Teszt módban normálisan fut, de nem kér interaktív inputot, ha nincs beolvasásra való adat.
        if (Boolean.getBoolean("test.env")) {
            System.out.println("[Teszt mód] A Game.start() interaktív része kihagyva.");
            return;
        }

        // Ha valamilyen előre megadott input (pl. tesztből jön), ne akadjunk be, csak olvassuk be.
        try {
            if (System.in.available() == 0) {
                System.out.println("[Figyelmeztetés] Nincs bemenet, interaktív módba lépne, leállítás.");
                return;
            }
        } catch (IOException e) {
            return;
        }


        System.out.print("Add meg a neved: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) name = "Jatekos";
        human = new Player(name, 'X');

        System.out.println("\n-- Amőba NxM --");
        showHelp();

        // Ha a tábla üres, tegyük le az X-et középre.
        if (!board.hasAnyMark()) {
            int r = board.getRows() / 2;
            int c = board.getCols() / 2;
            board.place(r, c, human.getMark()); // ez az első lépés automatikus
            System.out.println("Automatikus kezdőlépés (X) a középre: sor=" + (r+1) + ", oszlop=" + toCol(c));
        }

        // Fő ciklus: ember (X) –> gép (O) –> ellenőrzések
        while (true) {
            board.print();

            // Parancs bekérés
            System.out.print("\nParancs (pl. 'lepes 3 c' | 'ment' | 'betolt' | 'score' | 'kilep'): ");
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("kilep")) {
                System.out.println("Kilépés. Viszlát!");
                break;
            }
            if (line.equalsIgnoreCase("help")) {
                showHelp();
                continue;
            }
            if (line.equalsIgnoreCase("ment")) {
                BoardIO.save(board, boardFile);
                System.out.println("Tábla elmentve: " + boardFile.toAbsolutePath());
                continue;
            }
            if (line.equalsIgnoreCase("betolt")) {
                board = BoardIO.loadOrEmpty(boardFile, board.getRows(), board.getCols());
                System.out.println("Tábla betöltve (vagy üres, ha nem volt fájl).");
                continue;
            }

            if (line.equalsIgnoreCase("xmlment")) {
                BoardIO.saveToXml(board, Path.of("board.xml"));
                System.out.println("Tábla elmentve XML formátumban.");
                continue;
            }

            if (line.equalsIgnoreCase("xmlbetolt")) {
                board = BoardIO.loadFromXml(Path.of("board.xml"));
                System.out.println("Tábla betöltve XML formátumból.");
                continue;
            }

            if (line.equalsIgnoreCase("score")) {
                printScores();
                continue;
            }

                        // Lépés: "lepes <sor> <oszlopBetu>"
            if (line.toLowerCase().startsWith("lepes")) {
                // parszolás: lepes 3 c  -> sor=2 (0-index), col=2
                String[] parts = line.split("\\s+");
                if (parts.length != 3) {
                    System.out.println("Használat: lepes <sor(1.." + board.getRows() + ")> <oszlopBetu(a.." + toCol(board.getCols()-1) + ")>");
                    continue;
                }
                Integer r = parseRow(parts[1]);
                Integer c = parseCol(parts[2]);
                if (r == null || c == null) {
                    System.out.println("Érvénytelen sor/oszlop!");
                    continue;
                }

                // Ember lépése
                if (!board.place(r, c, human.getMark())) {
                    System.out.println("Érvénytelen lépés (foglaltság / nem szomszédos)!");
                    continue;
                }

                // Ember győzött?
                if (board.hasFiveInARow(human.getMark())) {
                    board.print();
                    System.out.println("🎉 " + human.getName() + " (X) nyert!");
                    // repo.incWin(human.getName());
                    printScores();
                    break;
                }

                // Gép lépése
                int[] m = ai.pickMove(board);
                if (m == null) {
                    board.print();
                    System.out.println("Nincs több lépés. Döntetlen.");
                    break;
                }
                board.place(m[0], m[1], cpu.getMark());
                System.out.println("Gép (O) lép: sor=" + (m[0]+1) + ", oszlop=" + toCol(m[1]));

                // Gép győzött?
                if (board.hasFiveInARow(cpu.getMark())) {
                    board.print();
                    System.out.println("🤖 Gép (O) nyert!");
                    repo.incWin(cpu.getName());
                    printScores();
                    break;
                }

                continue;
            }

            System.out.println("Ismeretlen parancs. Írd be: help");
        }
    }

    public void showHelp() {
        System.out.println("""
            Parancsok:
              help                – súgó
              lepes <sor> <oszlopBetu>  – pl. 'lepes 3 c'  (1-indexelt sor, oszlop: a..)
              ment                – tábla mentése 'board.txt' fájlba
              betolt              – tábla betöltése 'board.txt'-ből (ha nincs, üres indul)
              score               – high score tábla kiírása (név, győzelmek)
              kilep               – kilépés

            Szabályok:
              * X (ember) kezd AUTOMATIKUSAN középen.
              * Csak olyan üres mezőre lehet lépni, amely legalább diagonálisan
                szomszédos egy már lerakott jellel (X vagy O).
              * 5 egymás melletti azonos jel (vízsz., függőlegesen, átlósan) = győzelem.
            """);
    }

    public void printScores() {
        repo.printHighScores();
    }


    /** Sor: 1..rows → 0-indexre alakítjuk. */
    private Integer parseRow(String s) {
        try {
            int r = Integer.parseInt(s);
            if (r < 1 || r > board.getRows()) return null;
            return r - 1;
        } catch (NumberFormatException e) { return null; }
    }

    /** Oszlop: 'a'..  → 0-index (a=0,b=1,...) */
    private Integer parseCol(String s) {
        if (s.length() != 1) return null;
        char ch = Character.toLowerCase(s.charAt(0));
        int c = ch - 'a';
        if (c < 0 || c >= board.getCols()) return null;
        return c;
    }

    /** 0-index oszlop → betű (0→a, 1→b, ...). */
    private String toCol(int c) {
        return String.valueOf((char)('a' + c));
    }
    // Visszaadja a tábla objektumot
    public Board getBoard() {
        return board; // itt a board mező az, amit a Game tárol
    }

    // A gép (O) lépése – egyszerűen meghívja az AI logikáját
    public void computerMove() {
        int[] move = ai.pickMove(board);
        if (move != null) {
            board.place(move[0], move[1], 'O');
        }
    }
}


