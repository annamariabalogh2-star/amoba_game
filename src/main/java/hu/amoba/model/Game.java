package hu.amoba.model;

import hu.amoba.db.HighScoreRepository;
import hu.amoba.io.BoardIO;
import hu.amoba.vo.Player;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * A játék fő vezérlőosztálya.
 * Feladata: a játék indítása, parancsok kezelése, lépések vezérlése és mentés/betöltés kezelése.
 */
public class Game {
    private Board board;
    private final AI ai = new AI();
    private final HighScoreRepository repo = new HighScoreRepository();
    private final Scanner sc = new Scanner(System.in);

    private Player human;
    private final Player cpu = new Player("Gep", 'O');

    private final Path boardFile = Path.of("board.txt");

    /** Konstruktor: alapértelmezett 10x10-es tábla. */
    public Game() {
        board = new Board(10, 10);
    }

    /** A játék indítása és a fő vezérlőciklus. */
    public void start() {

        if (Boolean.getBoolean("test.env")) {
            System.out.println("[Teszt mód] A Game.start() interaktív része kihagyva, középső X lerakva teszteléshez.");
            if (!board.hasAnyMark()) {
                int r = board.getRows() / 2;
                int c = board.getCols() / 2;
                board.place(r, c, 'X');
            }
            return;
        }

        // --- Kezdőképernyő megjelenítése ---
        showIntro();

        // --- Korábbi mentés betöltése, ha van ---
        System.out.print("Szeretnéd betölteni a korábbi mentést? (i/n): ");
        String answer = sc.nextLine().trim().toLowerCase();

        if (answer.equals("i")) {
            board = BoardIO.loadOrEmpty(boardFile, 10, 10);
            System.out.println("Korábbi játék betöltve.\n");
        } else {
            board = new Board(10, 10);
            System.out.println("Új játék kezdődik!\n");
        }

        // --- Név bekérése ---
        System.out.print("Add meg a neved: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            name = "Gamer";
            System.out.println("Név nem lett megadva, automatikusan beállítva: " + name);
        }
        human = new Player(name, 'X');

        System.out.println("\n-- Amőba Játék --");
        showHelp();

        // --- Első lépés automatikusan középre ---
        if (!board.hasAnyMark()) {
            int r = centerIndex(board.getRows());
            int c = centerIndex(board.getCols());
            board.place(r, c, human.getMark());
            System.out.println("Automatikus kezdőlépés (X) a középre: sor=" + (r + 1) + ", oszlop=" + toCol(c));
        }

        // --- Fő játékkör ---
        while (true) {
            board.print();

            System.out.print("\nParancs (pl. 'lepes 3 c' | 'ment' | 'betolt' | 'score' | 'kilep'): ");
            String line = sc.nextLine().trim();

            // --- Kilépés ---
            if (line.equalsIgnoreCase("kilep")) {
                showGoodbye();
                break;
            }

            // --- Súgó ---
            if (line.equalsIgnoreCase("help")) {
                showHelp();
                continue;
            }

            // --- Mentés txt-be ---
            if (line.equalsIgnoreCase("ment")) {
                BoardIO.save(board, boardFile);
                System.out.println("Tábla elmentve: " + boardFile.toAbsolutePath());
                continue;
            }

            // --- Betöltés txt-ből ---
            if (line.equalsIgnoreCase("betolt")) {
                board = BoardIO.loadOrEmpty(boardFile, board.getRows(), board.getCols());
                System.out.println("Tábla betöltve (vagy üres, ha nem volt fájl).");
                continue;
            }

            // --- Mentés XML-be ---
            if (line.equalsIgnoreCase("xmlment")) {
                BoardIO.saveToXml(board, Path.of("board.xml"));
                System.out.println("Tábla elmentve XML formátumban.");
                continue;
            }

            // --- Betöltés XML-ből ---
            if (line.equalsIgnoreCase("xmlbetolt")) {
                board = BoardIO.loadFromXml(Path.of("board.xml"));
                System.out.println("Tábla betöltve XML formátumból.");
                continue;
            }

            // --- High Score megjelenítés ---
            if (line.equalsIgnoreCase("score")) {
                printScores();
                continue;
            }

            // --- Lépés feldolgozása ---
            if (line.toLowerCase().startsWith("lepes")) {
                String[] parts = line.split("\\s+");
                if (parts.length != 3) {
                    System.out.println("Használat: lepes <sor> <oszlopBetu>  pl. 'lepes 3 c'");
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

                // Győzelem ellenőrzése
                if (board.hasFiveInARow(human.getMark())) {
                    board.print();
                    System.out.println("🎉 " + human.getName() + " (X) nyert!");
                    repo.incWin(human.getName());
                    printScores();
                    break;
                }

                // Gép lépése
                int[] move = ai.pickMove(board);
                if (move == null) {
                    board.print();
                    System.out.println("Nincs több lépés. Döntetlen!");
                    break;
                }

                board.place(move[0], move[1], cpu.getMark());
                System.out.println("Gép (O) lép: sor=" + (move[0] + 1) + ", oszlop=" + toCol(move[1]));

                // Gép győzelem
                if (board.hasFiveInARow(cpu.getMark())) {
                    board.print();
                    System.out.println("🤖 Gép (O) nyert!");
                    repo.incWin(cpu.getName());
                    printScores();
                    break;
                }
                continue;
            }

            // Ha semmi sem egyezett:
            System.out.println("Ismeretlen parancs. Írd be: help");
        }
    }

    /** A kezdő képernyő megjelenítése. */
    private void showIntro() {
        final String BLUE = "\u001B[34m";
        final String RESET = "\u001B[0m";

        System.out.println(BLUE + """
        ╔══════════════════════════════╗
        ║                              ║
        ║        A M O B A  JÁTÉK      ║
        ║                              ║
        ╚══════════════════════════════╝
        """ + RESET);
    }

    /** Kilépéskor megjelenő üzenet. */
    private void showGoodbye() {
        final String BLUE = "\u001B[34m";
        final String RESET = "\u001B[0m";

        System.out.println(BLUE + """
        ╔══════════════════════════════╗
        ║                              ║
        ║          V I S Z L Á T !     ║
        ║                              ║
        ╚══════════════════════════════╝
        """ + RESET);
    }

    /** A parancslista és szabályok kiírása. */
    private void showHelp() {
        System.out.println("""
            Parancsok:
              help                 - súgó
              lepes <sor> <oszlop> - pl. 'lepes 3 c'
              ment                 - tábla mentése
              betolt               - tábla betöltése
              xmlment              - mentés XML-be
              xmlbetolt            - betöltés XML-ből
              score                - high score lista
              kilep                - kilépés

            Szabályok:
              * X (ember) kezd AUTOMATIKUSAN középen.
              * Csak szomszédos üres mezőre lehet lépni.
              * 5 azonos jel egymás mellett = győzelem.
            """);
    }

    /** High score kiírása. */
    private void printScores() {
        repo.printHighScores();
    }

    /** Segédfüggvény: kiszámítja a középső indexet páros/páratlan méretre. */
    private int centerIndex(int n) {
        return (n % 2 == 0) ? (n / 2 - 1) : (n / 2);
    }

    /** Sor parszolása (1-index → 0-index). */
    private Integer parseRow(String s) {
        try {
            int r = Integer.parseInt(s);
            if (r < 1 || r > board.getRows()) return null;
            return r - 1;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Oszlop parszolása (a, b, c → 0, 1, 2). */
    private Integer parseCol(String s) {
        if (s.length() != 1) return null;
        char ch = Character.toLowerCase(s.charAt(0));
        int c = ch - 'a';
        if (c < 0 || c >= board.getCols()) return null;
        return c;
    }

    /** 0-index oszlop → betű (0→a, 1→b, ...). */
    private String toCol(int c) {
        return String.valueOf((char) ('a' + c));
    }

    /** Tesztekhez: a tábla lekérése. */
    public Board getBoard() {
        return board;
    }

    /** A gép lépése – egyszerű mesterséges intelligencia. */
    public void computerMove() {
        int[] move = ai.pickMove(board);
        if (move != null) {
            board.place(move[0], move[1], 'O');
        }
    }
}



