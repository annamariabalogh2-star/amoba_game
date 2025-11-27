package hu.amoba.ui;

import java.nio.file.Path;
import java.util.Scanner;

import hu.amoba.service.AI;
import hu.amoba.model.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import hu.amoba.io.BoardIO;
import hu.amoba.vo.Player;
import hu.amoba.service.PonttablaKezelo;
import hu.amoba.service.DontetlenEllenorzo;

public class Game {

    /** A játék táblája, amely tartalmazza az aktuális állást. */
    private Board board;

    /** Naplózás (logolás) az SLF4J segítségével. */
    private static final Logger log = LoggerFactory.getLogger(Game.class);

    /** Véletlenszerű lépéseket végző AI (mesterséges intelligencia). */
    private final AI ai = new AI();

    /** Konzolos adatbekéréshez szükséges Scanner. */
    private Scanner sc = new Scanner(System.in);

    /** Az emberi játékos objektuma. */
    private Player human;

    /** A gépi játékos (O) objektuma, fix névvel. */
    private final Player cpu = new Player("Gep", 'O');

    /** A tábla mentéséhez használt szöveges fájl. */
    private final Path tablaTxtFajl = Path.of("tabla.txt");

    /** A tábla XML mentéséhez használt fájl. */
    private final Path tablaXmlFajl = Path.of("tabla.xml");

    /** Konzolos nézet (kezdőképernyő, súgó, búcsú). */
    private final KonzolosNezet nezet = new KonzolosNezet();

    /** Ponttábla-kezelő (HighScoreRepository köré). */
    private final PonttablaKezelo ponttablaKezelo = new PonttablaKezelo();

    /** Döntetlen ellenőrzéséért felelős segéd. */
    private final DontetlenEllenorzo dontetlenEllenorzo = new DontetlenEllenorzo();

    /** Konstruktor: létrehoz egy alapértelmezett 10x10-es táblát. */
    public Game() {
        board = new Board(10, 10);
    }

    public void start() {
        sc = new Scanner(System.in);
        log.info("A játék elindult.");

        if (Boolean.getBoolean("test.env")) {
            System.out.println("[Teszt mód] A Game.start() interaktív része kihagyva, középső X lerakva teszteléshez.");
            if (!board.hasAnyMark()) {
                int r = board.getRows() / 2;
                int c = board.getCols() / 2;
                board.place(r, c, 'X');
            }
            return;
        }

        // Kezdőképernyő betöltése.
        nezet.showIntro();

        // Korábbi mentés betöltése, ha van.
        System.out.print("Szeretnéd betölteni a korábbi mentést? (i/n): ");
        String answer = sc.nextLine().trim().toLowerCase();

        if (answer.equals("i")) {
            board = BoardIO.loadOrEmpty(tablaTxtFajl, 10, 10);
            System.out.println("Korábbi játék betöltve.\n");
            log.info("Korábbi játékállás betöltve a fájlból.");
        } else {
            board = new Board(10, 10);
            System.out.println("Új játék kezdődik!\n");
            log.info("Új játék kezdődik üres táblával.");
        }

        // Játékosnév bekérése.
        System.out.print("Kérlek add meg a neved: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            name = "Gamer";
            System.out.println("Név nem lett megadva, automatikusan beállítva: " + name);
        }
        human = new Player(name, 'X');

        System.out.println("\n-- Amőba Játék --");
        nezet.showHelp();

        // Első lépés automatikusan középre.
        if (!board.hasAnyMark()) {
            int r = centerIndex(board.getRows());
            int c = centerIndex(board.getCols());
            board.place(r, c, human.getMark());
            System.out.println("Automatikus kezdőlépés (X) a középre: sor=" + (r + 1) + ", oszlop=" + toCol(c));
        }

        // Fő játékkör.
        while (true) {
            board.print();

            System.out.print("\nParancs (pl. 'lepes 3 c' | 'ment' | 'betolt' | 'score' | 'kilep'): ");
            String line = sc.nextLine().trim();

            // Kilépés
            if (line.equalsIgnoreCase("kilep")) {
                nezet.showGoodbye();
                break;
            }

            // Súgó
            if (line.equalsIgnoreCase("help")) {
                nezet.showHelp();
                continue;
            }

            // Mentés txt-be
            if (line.equalsIgnoreCase("ment")) {
                BoardIO.save(board, tablaTxtFajl);
                System.out.println("Tábla elmentve: " + tablaTxtFajl.toAbsolutePath());
                continue;
            }

            // Betöltés txt-ből
            if (line.equalsIgnoreCase("betolt")) {
                board = BoardIO.loadOrEmpty(tablaTxtFajl, board.getRows(), board.getCols());
                System.out.println("Tábla betöltve (vagy üres, ha nem volt fájl).");
                continue;
            }

            // Mentés XML-be
            if (line.equalsIgnoreCase("xmlment")) {
                BoardIO.saveToXml(board, tablaXmlFajl, human.getName());
                System.out.println("Tábla elmentve XML formátumban: " + tablaXmlFajl.toAbsolutePath());
                continue;
            }

            // Betöltés XML-ből
            if (line.equalsIgnoreCase("xmlbetolt")) {
                board = BoardIO.loadFromXml(tablaXmlFajl);
                System.out.println("Tábla betöltve XML formátumból: " + tablaXmlFajl.toAbsolutePath());
                continue;
            }

            // High Score megjelenítés
            if (line.equalsIgnoreCase("score")) {
                ponttablaKezelo.kiirEredmenyek();
                continue;
            }

            // Lépés feldolgozása
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
                    log.warn("Érvénytelen lépés: sor = {}, oszlop = {}", r, c);
                    continue;
                }

                // Győzelem ellenőrzése
                if (board.hasFiveInARow(human.getMark())) {
                    board.print();
                    System.out.println(" Gratulálok! " + human.getName() + " (X) nyertél!");
                    ponttablaKezelo.jatekosNyert(human.getName());
                    ponttablaKezelo.kiirEredmenyek();
                    log.info("{} játékos (X) nyert!", human.getName());
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
                    System.out.println(" Gép (O) nyert!");
                    ponttablaKezelo.jatekosNyert(cpu.getName());
                    ponttablaKezelo.kiirEredmenyek();
                    log.info("A gép (O) nyert!");
                    break;
                }
                continue;
            }

            // Ha semmi sem egyezett:
            System.out.println("Ismeretlen parancs. Írd be: help");
        }

        // Döntetlen ellenőrzése.

        if (dontetlenEllenorzo.isDraw(board)) {
            System.out.println("A játék döntetlennel ért véget!");
            log.info("A játék döntetlennel zárult.");
        }
    }

    /** Segédfüggvény: kiszámítja a középső indexet (páros/páratlan tábla esetén is). */
    private int centerIndex(int n) {
        return (n % 2 == 0) ? (n / 2 - 1) : (n / 2);
    }

    /** Átalakítja a felhasználó által beírt sort (1-index) 0-indexre. */
    private Integer parseRow(String s) {
        try {
            int r = Integer.parseInt(s);
            if (r < 1 || r > board.getRows()) return null;
            return r - 1;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Átalakítja az oszlopbetűt (a, b, c, ...) számmá. */
    private Integer parseCol(String s) {
        if (s.length() != 1) return null;
        char ch = Character.toLowerCase(s.charAt(0));
        int c = ch - 'a';
        if (c < 0 || c >= board.getCols()) return null;
        return c;
    }

    /** Számindexből visszaalakítja az oszlop betűjelét (0 → a, 1 → b, stb.). */
    private String toCol(int c) {
        return String.valueOf((char) ('a' + c));
    }

    /** A tábla lekérése (teszteléshez). */
    public Board getBoard() {
        return board;
    }

    /** A gép lépése – véletlenszerűen kiválasztott érvényes lépés. */
    public void computerMove() {
        int[] move = ai.pickMove(board);
        if (move != null) {
            board.place(move[0], move[1], 'O');
        }
    }
}



