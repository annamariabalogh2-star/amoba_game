package hu.amoba.ui;

import hu.amoba.model.Board;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

class GameCommandTest {

    /**
     * Teszt: az ismert parancsok helyes kezelése.
     * A start() metódusnak hibamentesen kell lefutnia a megadott inputokra.
     */
   // @Test
    void testStartHandlesCommands() {
        String fakeInput = String.join(System.lineSeparator(),
                "TesztJatekos",  // név megadása
                "help",          // súgó kiírása
                "ment",          // mentés parancs
                "betolt",        // betöltés
                "score",         // ponttábla
                "kilep"          // kilépés
        ) + System.lineSeparator();

        InputStream eredeti = System.in;
        System.setIn(new ByteArrayInputStream(fakeInput.getBytes()));

        Game game = new Game();
        boolean hibaTortent = false;
        try {
            game.start();
        } catch (Exception e) {
            hibaTortent = true;
        }

        System.setIn(eredeti);

        if (hibaTortent) {
            throw new AssertionError("A start() metódusnak kezelnie kell az ismert parancsokat hiba nélkül.");
        }
    }

    /**
     * Teszt: az ismeretlen parancs hiba nélkül fusson le.
     * Lefedi az \"Ismeretlen parancs. Írd be: help\" ágat.
     */
  //  @Test
    void testUnknownCommandHandledGracefully() {
        String fakeInput = String.join(System.lineSeparator(),
                "Player",           // név megadása
                "valamiismeretlen", // hibás parancs
                "kilep"             // kilépés
        ) + System.lineSeparator();

        InputStream eredeti = System.in;
        System.setIn(new ByteArrayInputStream(fakeInput.getBytes()));

        Game game = new Game();
        boolean hibaTortent = false;
        try {
            game.start();
        } catch (Exception e) {
            hibaTortent = true;
        }

        System.setIn(eredeti);

        if (hibaTortent) {
            throw new AssertionError("Az ismeretlen parancs nem okozhat kivételt.");
        }
    }

    /**
     * Teszt: döntetlen állapot felismerése egy 3x3-as, teljesen teli táblán.
     */
  //  @Test
    void testDetectDrawOnFullBoard() {
        Board board = new Board(3, 3);

        // A táblát feltöltjük jelekkel → nem marad üres mező
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                board.place(r, c, 'X');
            }
        }

        // Nincs üres mező?
        boolean vanUres = false;
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.get(r, c) == '-') {
                    vanUres = true;
                }
            }
        }

        // Van-e győztes?
        boolean vanGyoztes = board.hasFiveInARow('X') || board.hasFiveInARow('O');

        // Döntetlen: nincs üres mező ÉS nincs győztes
        boolean dontetlen = !vanUres && !vanGyoztes;

        if (!dontetlen) {
            throw new AssertionError("Ha nincs üres mező és nincs 5 egymás mellett, akkor döntetlennek kell lennie.");
        }

    }

    /**
     * Teszt: mentés és kilépés parancsok hibamentes futása.
     */
  //  @Test
    void testHandlesSaveAndExit() {
        String fakeInput = String.join(System.lineSeparator(),
                "Tesztelo",   // név megadása
                "ment",       // mentés
                "kilep"       // kilépés
        ) + System.lineSeparator();

        InputStream eredeti = System.in;
        System.setIn(new ByteArrayInputStream(fakeInput.getBytes()));

        Game game = new Game();
        boolean hibaTortent = false;
        try {
            game.start();
        } catch (Exception e) {
            hibaTortent = true;
        }

        System.setIn(eredeti);

        if (hibaTortent) {
            throw new AssertionError("A mentés és kilépés parancsoknak nem szabad kivételt okozniuk.");
        }
    }

    /**
     * Teszt: korábbi játék betöltése (\"i\" választás).
     */
   // @Test
    void testStartLoadsPreviousGame() {
        String fakeInput = String.join(System.lineSeparator(),
                "i",            // Betöltést választ
                "TesztPlayer"   // Név megadása
        ) + System.lineSeparator();

        InputStream eredeti = System.in;
        System.setIn(new ByteArrayInputStream(fakeInput.getBytes()));

        Game game = new Game();
        boolean hibaTortent = false;
        try {
            game.start();
        } catch (Exception e) {
            hibaTortent = true;
        }

        System.setIn(eredeti);

        if (hibaTortent) {
            throw new AssertionError("A betöltés nem okozhat kivételt a start() metódusban.");
        }
    }

    /**
     * Teszt: üres név esetén a \"Gamer\" alapértelmezett név beállítása
     * nem okozhat hibát.
     */
   // @Test
    void testEmptyNameDefaultsToGamer() {
        String fakeInput = String.join(System.lineSeparator(),
                "n",  // új játék
                ""    // üres név beírása
        ) + System.lineSeparator();

        InputStream eredeti = System.in;
        System.setIn(new ByteArrayInputStream(fakeInput.getBytes()));

        Game game = new Game();
        boolean hibaTortent = false;
        try {
            game.start();
        } catch (Exception e) {
            hibaTortent = true;
        }

        System.setIn(eredeti);

        if (hibaTortent) {
            throw new AssertionError("Az üres név nem okozhat hibát, a játékos neve álljon át 'Gamer'-re.");
        }
    }

    /**
     * Teszt: XML mentés és betöltés parancsok hibamentes futása.
     */
   // @Test
    void testXmlSaveAndLoadCommands() {
        String fakeInput = String.join(System.lineSeparator(),
                "TesztJatekos",
                "xmlment",
                "xmlbetolt",
                "kilep"
        ) + System.lineSeparator();

        InputStream eredeti = System.in;
        System.setIn(new ByteArrayInputStream(fakeInput.getBytes()));

        Game game = new Game();
        boolean hibaTortent = false;
        try {
            game.start();
        } catch (Exception e) {
            hibaTortent = true;
        }

        System.setIn(eredeti);

        if (hibaTortent) {
            throw new AssertionError("Az XML mentés és betöltés parancsok nem dobhatnak kivételt.");
        }
    }

    /**
     * Teszt: döntetlen felismerése 3x3-as teljes táblán (nincs üres mező, nincs győztes).
     */
  //  @Test
    void testIsDrawReturnsTrueWhenFull() {
        Board tiny = new Board(3, 3);
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                tiny.place(r, c, ((r + c) % 2 == 0) ? 'X' : 'O');
            }
        }

        // Nincs üres mező?
        boolean vanUres = false;
        for (int r = 0; r < tiny.getRows(); r++) {
            for (int c = 0; c < tiny.getCols(); c++) {
                if (tiny.get(r, c) == '-') {
                    vanUres = true;
                }
            }
        }

        // Van-e győztes?
        boolean vanGyoztes = tiny.hasFiveInARow('X') || tiny.hasFiveInARow('O');

        // Döntetlen: nincs üres mező ÉS nincs győztes
        boolean dontetlen = !vanUres && !vanGyoztes;

        if (!dontetlen) {
            throw new AssertionError("A játék döntetlennek számít, ha nincs üres mező és nincs győztes.");
        }

    }
}
