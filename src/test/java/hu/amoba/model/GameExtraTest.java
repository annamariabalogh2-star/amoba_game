package hu.amoba.model;

import hu.amoba.ui.Game;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiegészítő tesztek a Game osztályhoz, hogy lefedjük a
 * mentés, betöltés és győzelmi logikát is.
 */
class GameExtraTest {

    private Game game;

    @BeforeEach
    void setup() {
        game = new Game();
    }

    /** Teszt: a mentés (BoardIO.save) nem dob hibát és létrejön a fájl. */
    @Test
    void testSaveBoardCreatesFile() {
        Path savePath = Path.of("board.txt");
        // Fájl törlése, ha létezett
        try { Files.deleteIfExists(savePath); } catch (Exception ignored) {}

        assertDoesNotThrow(() -> {
            game.getBoard().place(1, 1, 'X');
            // Mentés a játékon keresztül
            hu.amoba.io.BoardIO.save(game.getBoard(), savePath);
        }, "A tábla mentése nem dobhat hibát.");

        assertTrue(Files.exists(savePath), "A mentett board.txt fájlnak léteznie kell.");
    }

    /** Teszt: a betöltés működik, ha a fájl létezik. */
    @Test
    void testLoadBoardDoesNotThrow() {
        Path savePath = Path.of("board.txt");
        assertTrue(Files.exists(savePath), "A mentett fájl hiányzik a betöltési teszthez.");
        assertDoesNotThrow(() ->
                        hu.amoba.io.BoardIO.loadOrEmpty(savePath, 10, 10),
                "A betöltésnek hiba nélkül kell lefutnia.");
    }

    /** Teszt: az X játékos győzelme felismerhető. */
    @Test
    void testPlayerWinDetected() {
        Board b = game.getBoard();

        // Első lépés: kezdő X a középre
        int midRow = b.getRows() / 2;
        int midCol = b.getCols() / 2;
        b.place(midRow, midCol, 'X');

        // Utána további 4 X a középső sorban, középtől jobbra
        for (int c = midCol + 1; c < midCol + 5; c++) {
            assertTrue(b.place(midRow, c, 'X'), "A lépésnek érvényesnek kell lennie: col=" + c);
        }

        assertTrue(b.hasFiveInARow('X'),
                "Az 5 egymás melletti X-et győzelemként kell felismerni.");
    }
}

