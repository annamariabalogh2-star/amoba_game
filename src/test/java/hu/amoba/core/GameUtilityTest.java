package hu.amoba.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiegészítő teszt a Game segédmetódusaihoz.
 * Ezek nem indítanak játékot, csak egyszerű kiírásokat végeznek.
 */
class GameUtilityTest {

    @Test
    void testShowHelpAndPrintScores() {
        Game game = new Game();

        // A showHelp() csak szöveget ír ki, nem dobhat hibát
        assertDoesNotThrow(() -> {
            game.getClass().getDeclaredMethod("showHelp").setAccessible(true);
            game.getClass().getDeclaredMethod("showHelp").invoke(game);
        }, "A showHelp() nem dobhat hibát.");

        // A printScores() sem interaktív, biztonságosan hívható
        assertDoesNotThrow(() -> {
            game.getClass().getDeclaredMethod("printScores").setAccessible(true);
            game.getClass().getDeclaredMethod("printScores").invoke(game);
        }, "A printScores() sem dobhat hibát.");
    }
}
