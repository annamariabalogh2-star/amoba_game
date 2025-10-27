package hu.amoba.model;

import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tesztek a Game.start() parancságainak és játékmenetének ellenőrzésére.
 * Lefedi:
 *  - az ismert parancsokat ("help", "ment", "betolt", "score", "kilep")
 *  - az ismeretlen parancs esetét
 *  - a döntetlen állapot felismerését üres táblán
 *
 * A tesztek célja, hogy a Game.start() metódus minden ága lefedett legyen
 * hiba dobása nélkül. A Scanner bemenet szimulálva van.
 */
class GameCommandTest {

    /**
     * Teszt: az ismert parancsok helyes kezelése.
     * A start() metódusnak hibamentesen kell lefutnia a megadott inputokra.
     */
    @Test
    void testStartHandlesCommands() {
        // Előre definiált "felhasználói inputok" a parancsokhoz
        String fakeInput = String.join(System.lineSeparator(),
                "TesztJatekos",  // név megadása
                "help",          // súgó kiírása
                "ment",          // mentés parancs
                "betolt",        // betöltés
                "score",         // ponttábla
                "kilep"          // kilépés
        ) + System.lineSeparator();

        // A System.in lecserélése, hogy a Scanner ezeket olvassa be
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(fakeInput.getBytes()));

        // Teszt: a start() ne dobjon hibát és fusson végig
        Game game = new Game();
        assertDoesNotThrow(game::start,
                "A start() metódusnak kezelnie kell az összes ismert parancsot kivétel nélkül.");

        // Input visszaállítása
        System.setIn(originalIn);
    }

    /**
     * Teszt: az ismeretlen parancs hiba nélkül fusson le.
     * Lefedi az "Ismeretlen parancs. Írd be: help" ágat.
     */
    @Test
    void testUnknownCommandHandledGracefully() {
        String fakeInput = String.join(System.lineSeparator(),
                "Player",           // név megadása
                "valamiismeretlen", // hibás parancs
                "kilep"             // kilépés
        ) + System.lineSeparator();

        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(fakeInput.getBytes()));

        Game game = new Game();
        assertDoesNotThrow(game::start,
                "Az ismeretlen parancsot is hiba nélkül kell kezelni.");

        System.setIn(originalIn);
    }

    /**
     * Teszt: döntetlen állapot felismerése.
     * Lefedi azt az ágat, amikor nincs több lehetséges lépés.
     */
    @Test
    void testDetectDrawOnFullBoard() {
        Board board = new Board(3, 3);

        // A táblát feltöltjük 'X'-ekkel → nem marad üres mező
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                board.place(r, c, 'X');
            }
        }

        // Ha nincs több üres mező, az allSpotsTaken() true kell legyen
        assertTrue(board.allSpotsTaken(),
                "A döntetlen felismerésének működnie kell teljes tábla esetén.");
    }

    /**
     * Teszt: mentés és kilépés parancsok hibamentes futása.
     */
    @Test
    void testHandlesSaveAndExit() {
        String fakeInput = String.join(System.lineSeparator(),
                "Tesztelo",   // név megadása
                "ment",       // mentés
                "kilep"       // kilépés
        ) + System.lineSeparator();

        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(fakeInput.getBytes()));

        Game game = new Game();
        assertDoesNotThrow(game::start,
                "A mentés és kilépés parancsoknak hibamentesen kell lefutniuk.");

        System.setIn(originalIn);
    }
}

