package hu.amoba.model;

import hu.amoba.ui.Game;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A segédfüggvények (showHelp, printScores) tesztje.
 * Privát metódusokat reflexióval hívunk meg biztonságosan.
 */
public class GameUtilityTest {

    @Test
    void testShowHelpAndPrintScores() {
        Game game = new Game();

        assertDoesNotThrow(() -> {
            // Reflexióval meghívjuk a showHelp() privát metódust
            var showHelp = Game.class.getDeclaredMethod("showHelp");
            showHelp.setAccessible(true);
            showHelp.invoke(game);

            // Reflexióval meghívjuk a printScores() privát metódust
            var printScores = Game.class.getDeclaredMethod("printScores");
            printScores.setAccessible(true);
            printScores.invoke(game);
        }, "A showHelp() és printScores() metódusok nem dobhatnak hibát");
    }
}

