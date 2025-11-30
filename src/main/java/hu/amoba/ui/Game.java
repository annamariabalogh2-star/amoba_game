package hu.amoba.ui;

import java.nio.file.Path;
import java.util.Scanner;

import hu.amoba.service.AI;
import hu.amoba.model.Board;
import hu.amoba.io.BoardIO;
import hu.amoba.vo.Player;
import hu.amoba.service.PonttablaKezelo;
import hu.amoba.service.DontetlenEllenorzo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Path tablaTxtFajl = Path.of("board.txt");

    /** A tábla XML mentéséhez használt fájl. */
    private final Path tablaXmlFajl = Path.of("board.xml");

    /** Konzolos nézet (kezdőképernyő, súgó, búcsú). */
    private final KonzolosNezet nezet = new KonzolosNezet();

    /** Ponttábla-kezelő (HighScoreRepository köré). */
    private final PonttablaKezelo ponttablaKezelo = new PonttablaKezelo();

    /** Döntetlen ellenőrzéséért felelős segéd. */
    private final DontetlenEllenorzo dontetlenEllenorzo = new DontetlenEllenorzo();

    /** Létrehoz egy alapértelmezett 10x10-es táblát. */
    public Game() {
        board = new Board(10, 10);
    }

    public void start() {
        sc = new Scanner(System.in);
        log.info("A jatek elindult.");

        if (Boolean.getBoolean("test.env")) {
            System.out.println("[Teszt mód] A Game.start() interaktív része kihagyva, középső X lerakva teszteléshez.");
            if (!board.hasAnyMark()) {
                int r = board.getRows() / 2;
                int c = board.getCols() / 2;
                board.place(r, c, 'X');
            }
            return;
        }

        nezet.showIntro();                                          // Kezdőképernyő betöltése.

        System.out.print("Kerlek add meg a neved: ");               // Játékosnév bekérése.
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            name = "Gamer";
            System.out.println("Nev nem lett megadva, automatikusan beallitva: " + name);
        }
        human = new Player(name, 'X');

        System.out.println("\n-- Amoba Jatek --");
        nezet.showHelp();

        // Korábbi mentés betöltése, ha van.
        System.out.print("Szeretned betolteni a korabbi mentest? (i/n): ");
        String answer = sc.nextLine().trim().toLowerCase();

        if (answer.equals("i")) {
            board = BoardIO.loadOrEmpty(tablaTxtFajl, 10, 10);
            System.out.println("Korabbi jatek betoltve.\n");
            log.info("Korabbi jatekallas betoltve a fajlbol.");
        } else {
            board = new Board(10, 10);
            System.out.println("Uj jatek kezdodik!\n");
            log.info("Uj jatek kezdodik ures tablaval.");
        }

        // Eldöntjük, hogy legyen-e automatikus kezdőlépés.
        System.out.print("Szeretnel automatikus kezdolepest kozepre? (i/n): ");
        String autoValasz = sc.nextLine().trim().toLowerCase();

        // Ha a játékos igent mond (vagy bármi mást, csak nem 'n'), akkor mehet az automatikus lépés.
        if (!autoValasz.equals("n")) {
            int kozepSor = centerIndex(board.getRows());
            int kozepOszlop = centerIndex(board.getCols());

            board.place(kozepSor, kozepOszlop, 'X');

            System.out.println("Automatikus kezdolepes (X) a kozepre: sor="
                    + (kozepSor + 1) + ", oszlop=" + (char) ('a' + kozepOszlop));
            log.info("Automatikus kezdolepes tortent a kozepre.");
        } else {
            System.out.println("Automatikus kezdolepes nelkul indul a jatek. Te kezdesz a sajat lepeseddel.");
            log.info("A jatekos keresere nincs automatikus kezdolepes.");
        }

        boolean kilepParancs = false;                  // Jelzi, hogy a jatekos a 'kilep' parancsot valasztotta-e.

        // Fő játékkör.
        while (true) {
            board.print();

            System.out.print("\nParancs (pl. 'lepes 3 c' | 'ment' | 'betolt' | 'score' | 'kilep'): ");
            String line = sc.nextLine().trim();

            // Kilépés
            if (line.equalsIgnoreCase("kilep")) {
                kilepParancs = true;                    // megjegyezzuk, hogy kilepni szeretne
                nezet.showGoodbye();
                break;                                  // kilep a jatekkorbol
            }

            // Súgó
            if (line.equalsIgnoreCase("help")) {
                nezet.showHelp();
                continue;
            }

            // Mentés txt-be
            if (line.equalsIgnoreCase("ment")) {
                BoardIO.save(board, tablaTxtFajl);
                System.out.println("Tabla elmentve: " + tablaTxtFajl.toAbsolutePath());
                continue;
            }

            // Betöltés txt-ből
            if (line.equalsIgnoreCase("betolt")) {
                board = BoardIO.loadOrEmpty(tablaTxtFajl, board.getRows(), board.getCols());
                System.out.println("Tabla betoltve (vagy ures, ha nem volt fajl)." + tablaTxtFajl.toAbsolutePath());
                continue;
            }

            // Mentés XML-be
            if (line.equalsIgnoreCase("xmlment")) {
                BoardIO.saveToXml(board, tablaXmlFajl, human.getName());
                System.out.println("Tabla elmentve XML formatumban: " + tablaXmlFajl.toAbsolutePath());
                continue;
            }

            // Betöltés XML-ből
            if (line.equalsIgnoreCase("xmlbetolt")) {
                board = BoardIO.loadFromXml(tablaXmlFajl);
                System.out.println("Tabla betoltve XML formatumbol: " + tablaXmlFajl.toAbsolutePath());
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
                    System.out.println("Hasznalat: lepes <sor> <oszlopBetu>  pl. 'lepes 3 c'");
                    continue;
                }

                Integer r = parseRow(parts[1]);
                Integer c = parseCol(parts[2]);
                if (r == null || c == null) {
                    System.out.println("Ervenytelen sor/oszlop!");
                    continue;
                }

                // Ember lépése
                if (!board.place(r, c, human.getMark())) {
                    System.out.println("Ervenytelen lepes, figyelj oda! Mit ittal? :) (foglaltsag / nem szomszedos)!");
                    log.warn("Ervenytelen lepes: sor = {}, oszlop = {}", r, c);
                    continue;
                }

                // Győzelem ellenőrzése
                if (board.hasFiveInARow(human.getMark())) {
                    board.print();
                    System.out.println(" Gratulalok! " + human.getName() + " (X) nyertel! Szep jatek!!!");
                    ponttablaKezelo.jatekosNyert(human.getName());
                    ponttablaKezelo.kiirEredmenyek();
                    log.info("{} jatekos (X) nyert!", human.getName());
                    break;
                }

                // Gép lépése
                int[] move = ai.pickMove(board);
                if (move == null) {
                    board.print();
                    System.out.println("Nincs tobb lepes. Dontetlen! Micsoda jatszma!");
                    break;
                }

                board.place(move[0], move[1], cpu.getMark());
                System.out.println("Gep (O) lep: sor=" + (move[0] + 1) + ", oszlop=" + toCol(move[1]));

                // Gép győzelem
                if (board.hasFiveInARow(cpu.getMark())) {
                    board.print();
                    System.out.println(" Gep (O) nyert!");
                    ponttablaKezelo.jatekosNyert(cpu.getName());
                    ponttablaKezelo.kiirEredmenyek();
                    log.info("A gep (O) nyert!");
                    break;
                }
                continue;
            }

            // Ha semmi sem egyezett:
            System.out.println("Ismeretlen parancs. Ird be: help");
        }

        // Ha a játékos a 'kilep' parancsot irta be, akkor már ki lépünk, nincs értelme új játékot kérni, egyszerűen visszatérünk.
        if (kilepParancs) {
            return;
        }

        // Döntetlen ellenorzése.
        if (dontetlenEllenorzo.isDraw(board)) {
            System.out.println("A jatek dontetlennel ert veget!");
            log.info("A jatek dontetlennel zarult.");

            // dontetlen: mindketten kapnak 1-1 pontot
            ponttablaKezelo.dontetlen(human.getName(), cpu.getName());
            ponttablaKezelo.kiirEredmenyek();
        }

        // Egyszerű fő menü, kérdezze meg, akar-e új játékot.
        System.out.print("Szeretnel uj jatekot kezdeni? (i/n): ");
        String uj = sc.nextLine().trim().toLowerCase();

        if ("i".equals(uj)) {
            // Új, üres tábla – ugyanabban a futásban új meccs indul
            board = new Board(10, 10);
            log.info("Uj jatek indul ugyanabban a futasban.");
            start();   // rekurzivan ujrainditjuk a jatekot
        } else {
            nezet.showGoodbye();
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

    // Egyszerű "továbbhívó" metódusok a tesztek kedvéért.
    /** Kezdő képernyő megjelenítése Game-ből. */
    public void showIntro() {
        nezet.showIntro();
    }

    /** Súgó megjelenítése Game-ből. */
    public void showHelp() {
        nezet.showHelp();
    }

    /** Ponttábla kiíratása Game-ből. */
    public void printScores() {
        ponttablaKezelo.kiirEredmenyek();
    }
}



