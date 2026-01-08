package hu.amoba.ui;

import java.nio.file.Path;
import java.util.Scanner;

import hu.amoba.io.BoardIO;
import hu.amoba.model.Board;
import hu.amoba.service.AI;
import hu.amoba.service.DontetlenEllenorzo;
import hu.amoba.service.PonttablaKezelo;
import hu.amoba.vo.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Az amőba játék fő vezérlő osztálya.
 */

public class Game {                             // A teljes játékmenetet vezérli (konzol parancsokkal)

    private Board board;                        // Az aktuális tábla állapot

    private static final Logger log = LoggerFactory.getLogger(Game.class); // Logger, eseményeket ír (info/warn), “naplózásra”

    private final AI ai = new AI();             // Gép játékos, véletlen szabályos lépést választ

    @SuppressWarnings("FieldCanBeLocal")
    private Scanner sc = new Scanner(System.in); // Konzolos beolvasó (parancsokhoz)

    @SuppressWarnings("FieldCanBeLocal")
    private Player human;                       // Emberi játékos (név + 'X')

    private final Player cpu = new Player("Gep", 'O'); // Gépi játékos (fix név, 'O')
    // A mentett tábla szöveges fájl helye (aktuális mappa + tabla.txt)
    private final Path tablaTxtFajl = Path.of(System.getProperty("user.dir"), "tabla.txt");
    // A mentett tábla XML fájl helye (aktuális mappa + tabla.xml)
    private final Path tablaXmlFajl = Path.of(System.getProperty("user.dir"), "tabla.xml");

    private final KonzolosNezet nezet = new KonzolosNezet(); // “Képernyők”: intro, help, goodbye (külön osztályban)

    private final PonttablaKezelo ponttablaKezelo = new PonttablaKezelo(); // Ponttábla kezelő, ami adatbázist használ

    private final DontetlenEllenorzo dontetlenEllenorzo = new DontetlenEllenorzo(); // Döntetlen ellenőrzés

    public Game() {                                     // Konstruktor: alap tábla létrehozása
        board = new Board(10, 10);           // Kezdéskor 10x10-es üres tábla
    }

    public void start() {                               // A játék elindítása (fő vezérlő metódus)
        sc = new Scanner(System.in);                    // Scanner újra létrehozása (friss beolvasó)
        log.info("A játék elindult.");                  // Log: játék indult

        nezet.showIntro();                              // Kezdőképernyő kiírása

        System.out.print("Kérlek add meg a neved: ");   // Játékosnév bekérése.
        String name = sc.nextLine().trim();             // Beolvassa és levágja a szóközöket
        if (name.isEmpty()) {                           // Ha nem adott meg nevet
            name = "Gamer";                             // Alapnév
            System.out.println("Név nem lett megadva, automatikusan beállitva: " + name); // Info
        }
        human = new Player(name, 'X');            // Emberi játékos létrehozása (X)

        System.out.println("\n-- Amőba Játék --");      // Cím kiírás
        nezet.showHelp();                               // Súgó kiirás

        String answer;
        do {                                            // Addig kérdez, amíg nem üres választ kap
            System.out.print("Szeretnéd betölteni a korábbi mentést? (i/n): ");
            answer = sc.nextLine().trim().toLowerCase(); // beolvas + kisbetű
        } while (answer.isEmpty());

            if (answer.equals("i")) {                     // Betöltés: korábbi játék állása fájlból
                board = BoardIO.loadOrEmpty(tablaTxtFajl, 10, 10); // betöltés (ha hiba: 10x10 üres)

                System.out.println("Korábbi játék betöltve.\n");
                log.info("Korábbi játékállás betöltve a fájlbol.");
        } else {                                          // Új játék: üres tábla + opcionális automatikus kezdőlépés
            board = new Board(10, 10);         // új üres tábla
            System.out.println("Új játék kezdődik!\n");
            log.info("Új játék kezdődik üres táblával.");

            System.out.print("Szeretnél automatikus kezdőlépést középre? (i/n): ");
            String autoValasz = sc.nextLine().trim().toLowerCase(); // választ beolvassa

            if (!autoValasz.equals("n")) {                 // Ha nem “n”, akkor kezdőlépést ad középre
                int kozepSor = centerIndex(board.getRows()); // középső sor index
                int kozepOszlop = centerIndex(board.getCols()); // középső oszlop index

                board.place(kozepSor, kozepOszlop, 'X'); // X-et középre tesz

                System.out.println("Automatikus kezdőlépés (X) középre: sor="
                        + (kozepSor + 1) + ", oszlop=" + (char) ('a' + kozepOszlop)); // Info
                log.info("Automatikus kezdőlépés történt középre.");

                int[] aiMove = ai.pickMove(board);        // AI választ egy lépést

                if (aiMove != null) {                     // Ha van lépés
                    int sor = aiMove[0];                  // sor kiolvasása
                    int oszlop = aiMove[1];               // oszlop kiolvasása

                    board.place(sor, oszlop, 'O');  // O-t lerakja a gép

                    System.out.println("Gép (O) lép: sor=" + (sor + 1)
                            + ", oszlop=" + (char) ('a' + oszlop)); // kiírja a gép lépését
                }
            } else {
                System.out.println("Automatikus kezdőlépés nélkül indul a játék. Te kezdesz a saját lépéseddel.");
                log.info("A játékos kérésére nincs automatikus kezdőlépés."); // Info
            }
        }

        boolean kilepParancs = false;                    // Jelzi, hogy kilépett-e a játékos

        while (true) {                                   // Fő játékkör (parancsfeldolgozás)
            board.print();                               // Tábla kirajzolása
            // Parancs kiírása
            System.out.print("\nParancs (pl. 'lepes 3 c' | 'ment' | 'betolt' | 'xmlment' | 'xmlbetolt' | 'score' | 'kilep'): ");
            String line = sc.nextLine().trim();          // Beolvassa a sort

            if (line.equalsIgnoreCase("kilep")) { // Kilépés parancs
                kilepParancs = true;                     // Jelzi, hogy kilépés történt
                nezet.showGoodbye();                     // Búcsú képernyő
                break;                                   // Kilép a while ciklusból
            }

            if (line.equalsIgnoreCase("help")) { // Súgó parancs
                nezet.showHelp();                        // Súgó kiirás
                continue;                                // Következő kör
            }

            if (line.equalsIgnoreCase("ment")) { // Mentés parancs (txt)
                BoardIO.save(board, tablaTxtFajl);       // Tábla mentés fájlba
                System.out.println("Tábla elmentve: " + tablaTxtFajl.toAbsolutePath());
                continue;
            }

            if (line.equalsIgnoreCase("betolt")) { // Betöltés parancs (txt)
                // Betölti és ha nincs fájl/hiba, akkor a jelenlegi méretű üres táblát ad
                board = BoardIO.loadOrEmpty(tablaTxtFajl, board.getRows(), board.getCols());
                System.out.println("Tábla betöltve (vagy üres, ha nem volt fájl)." + tablaTxtFajl.toAbsolutePath());
                continue;
            }

            if (line.equalsIgnoreCase("xmlment")) { // Mentés XML-be
                BoardIO.saveToXml(board, tablaXmlFajl, human.getName()); // Mentés XML-be + név kommentként
                System.out.println("Tábla elmentve XML formátumban: {}" + tablaXmlFajl.toAbsolutePath());
                continue;
            }

            if (line.equalsIgnoreCase("xmlbetolt")) { // Betöltés XML-ből
                board = BoardIO.loadFromXml(tablaXmlFajl); // Betöltés XML-ből (hiba esetén 10x10)
                System.out.println("Tábla betöltve XML formátumból: {}" + tablaXmlFajl.toAbsolutePath());
                continue;
            }

            if (line.equalsIgnoreCase("score")) { // Ponttábla kiirás parancs
                ponttablaKezelo.kiirEredmenyek(); // Eredmények kiírása
                continue;
            }

            if (line.toLowerCase().startsWith("lepes")) {  // Lépés parancs felismerése
                String[] parts = line.split("\\s+"); // Szétszedi a parancsot részekre
                if (parts.length != 3) {                   // Ha nem 3 rész (lepes, sor, oszlop)
                    System.out.println("Használat: lepes <sor> <oszlopBetu>  pl. 'lepes 3 c'"); // Hibajelzés
                    continue;
                }

                Integer r = parseRow(parts[1]);           // Sor átalakítás (1-index -> 0-index)
                Integer c = parseCol(parts[2]);           // Oszlop betű -> index (a -> 0)
                if (r == null || c == null) {             // Hibás input esetén
                    System.out.println("Érvénytelen sor/oszlop!"); // Info
                    continue;
                }

                if (!board.place(r, c, human.getMark())) { // Ember lépése (ellenőrzi, hogy szabályos-e?)
                    System.out.println("Érvénytelen lépés, figyelj oda! (foglaltság / nem szomszédos)!"); // Info
                    log.warn("Érvénytelen lépés: sor = {}, oszlop = {}", r, c); // Log warning
                    continue;
                }

                if (board.hasFiveInARow(human.getMark())) { // Ember nyert-e?
                    board.print();                          // Tábla kiírás
                    System.out.println(" Gratulálok! " + human.getName() + " (X) nyertél! Szép játék!!!"); // Győzelem üzenet
                    ponttablaKezelo.jatekosNyert(human.getName()); // 3 pont a győztesnek
                    ponttablaKezelo.kiirEredmenyek();       // Ponttábla kiírás
                    log.info("{} Játékos (X) nyert!", human.getName());
                    break;                                  // Kilép a játékkörből
                }

                int[] move = ai.pickMove(board);            // Gép választ lépést
                if (move == null) {                         // Ha nincs lépés (nincs legális)
                    board.print();
                    System.out.println("Nincs több lépés. Döntetlen! Micsoda játszma!");
                    break;                                  // Kilép (vége)
                }

                board.place(move[0], move[1], cpu.getMark()); // Gép lerakja az O-t
                System.out.println("Gep (O) lep: sor=" + (move[0] + 1) + ", oszlop=" + toCol(move[1])); // Kiírja a gép lépését

                if (board.hasFiveInARow(cpu.getMark())) {    // Gép nyert-e?
                    board.print();
                    System.out.println(" Gép (O) nyert!");
                    ponttablaKezelo.jatekosNyert(cpu.getName()); // 3 pont a gépnek
                    ponttablaKezelo.kiirEredmenyek();
                    log.info("A gép (O) nyert!");
                    break;                                   // Kilép (vége)
                }
                continue;
            }

            System.out.println("Ismeretlen parancs. Ird be: help"); // Ismeretlen parancs kezelése
        }

        if (kilepParancs) {                                  // Ha kilépést választott
            return;                                          // Kilép
        }

        if (dontetlenEllenorzo.isDraw(board)) {              // Döntetlen ellenőrzése a játékkör után
            System.out.println("A játék döntetlennel ért véget!");
            log.info("A játék döntetlennel zárult.");

            ponttablaKezelo.dontetlen(human.getName(), cpu.getName()); // 1-1 pont mindkettőnek
            ponttablaKezelo.kiirEredmenyek();                // Ponttábla kiírás
        }

        System.out.print("Szeretnél új játékot kezdeni? (i/n): "); // Új játék kérdés
        String uj = sc.nextLine().trim().toLowerCase();      // Válasz beolvasás

        if ("i".equals(uj)) {
            nezet.showGoodbye();                            // búcsú üzenet
        }

    }

    private int centerIndex(int n) {                        // Kiszámolja a középső indexet
        return (n % 2 == 0) ? (n / 2 - 1) : (n / 2);        // Párosnál bal-közép, páratlannál közép
    }

    private Integer parseRow(String s) {                    // "3" -> 2 (1-index -> 0-index)
        try {
            int r = Integer.parseInt(s);                    // Számmá alakítja
            if (r < 1 || r > board.getRows()) {             // Tartomány ellenőrzés
                return null;                                // Hibás sor
            }
            return r - 1;                                   // 0-indexre váltás
        } catch (NumberFormatException e) {                 // Ha nem szám
            return null;                                    // Hibás input
        }
    }

    private Integer parseCol(String s) {                    // "c" -> 2 (betű -> index)
        if (s.length() != 1) {                              // Csak 1 karakter lehet
            return null;
        }
        char ch = Character.toLowerCase(s.charAt(0));       // Kisbetűsít
        int c = ch - 'a';                                   // 'a'->0, 'b'->1, ...
        if (c < 0 || c >= board.getCols()) {                // Tartomány ellenőrzés
            return null;
        }
        return c;                                           // Visszaadja az indexet
    }

    private String toCol(int c) {                           // 2 -> "c" (index -> betű)
        return String.valueOf((char) ('a' + c));            // 'a'+c karakterből string
    }

    public Board getBoard() {                               // Getter: teszteknek a táblát visszaadja
        return board;
    }

    public void computerMove() {                            // Tesztekhez: a gép csinál egy lépést
        int[] move = ai.pickMove(board);                    // AI választ lépést
        if (move != null) {                                 // Ha van lépés
            board.place(move[0], move[1], 'O');       // Lerak egy O-t
        }
    }

    public void showHelp() {                                // Game-ből hívható súgó
        nezet.showHelp();                                   // Nézet kiírja a helpet
    }

    public void printScores() {                             // Game-ből hívható ponttábla
        ponttablaKezelo.kiirEredmenyek();                   // Ponttábla kiírása
    }
}



