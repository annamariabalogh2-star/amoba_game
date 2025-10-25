package hu.amoba.model;

import org.junit.jupiter.api.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Teszt a Game.start() parancságainak lefedésére.
 * Lefedi a "ment", "betolt", "score" és "kilep" parancsokat,
 * valamint a help menüt is.
 */
class GameCommandTest {

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
                "A start() metódusnak kezelnie kell az összes parancsot kivétel nélkül.");

        // Input visszaállítása
        System.setIn(originalIn);
    }
}
