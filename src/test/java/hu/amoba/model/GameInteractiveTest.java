package hu.amoba.model;

import hu.amoba.ui.Game;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;

/**
 * Teszt a Game.start() fő menüágainak lefedésére.
 * A bemenetet előre megadjuk, így a játék nem akad be.
 */
class GameInteractiveTest {

    @Test
    void testGameStartMenuCommandsRunWithoutError() {
        // "Felhasználó" által beírt parancsokat előre beadjuk:
        String input = String.join(System.lineSeparator(),
                "TesztJatekos",  // név megadása
                "ment",          // mentés
                "betolt",        // betöltés
                "score",         // high score lista
                "kilep"          // kilépés a játékból
        ) + System.lineSeparator();

        // Eredeti System.in elmentése
        InputStream originalIn = System.in;

        try {
            // Beadott input beállítása
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            // Teszt: ne dobjon hibát
            Game game = new Game();
            System.clearProperty("test.env"); // tesztmód ki, hogy lefusson a menü
            assertDoesNotThrow(game::start, "A játék fő menüje hiba nélkül kell lefusson.");

        } finally {
            // Input visszaállítása
            System.setIn(originalIn);
        }
    }
}

