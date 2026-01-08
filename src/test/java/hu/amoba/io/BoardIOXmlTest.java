package hu.amoba.io;

import hu.amoba.model.Board;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Teszt a BoardIO XML mentés és betöltés funkciójára. */
class BoardIOXmlTest {

    @Test
    void testSaveAndLoadXml() {
        Path xmlPath = Path.of("test_board.xml");

        Board board = new Board(5, 5);
        board.place(2, 2, 'X');
        board.place(2, 3, 'O');
        board.place(3, 3, 'X');

        assertDoesNotThrow(() -> BoardIO.saveToXml(board, xmlPath, "tesztjátékos"),
                "Az XML mentésnek hiba nélkül le kell futnia.");

        Board loaded = assertDoesNotThrow(() -> BoardIO.loadFromXml(xmlPath),
                "Az XML betöltésnek hiba nélkül le kell futnia.");

        char[][] orig = board.getCells();
        char[][] back = loaded.getCells();

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                assertEquals(orig[r][c], back[r][c],
                        "A betöltött tábla nem egyezik az eredetivel.");
            }
        }

        // takarítás
        try { Files.deleteIfExists(xmlPath); }
        catch (IOException ignored) {}
    }

    @Test
    void testLoadFromXmlWithMissingFile() {
        Path invalidPath = Path.of("nem_letezik.xml");

        Board board = assertDoesNotThrow(() -> BoardIO.loadFromXml(invalidPath),
                "A hiányzó XML fájl nem okozhat hibát.");

        assertNotNull(board, "Hiányzó fájl esetén is vissza kell adni egy üres táblát.");
    }

    @Test
    void testSaveToXmlWithInvalidPath() {
        Path badPath = Path.of("/nem/letezo/mappa/board.xml");

        assertDoesNotThrow(() -> BoardIO.saveToXml(new Board(3, 3), badPath, "tesztjátékos"),
                "A mentésnek hibátlanul kell kezelnie az érvénytelen elérési utat.");
    }

    @Test
    void testSaveToXmlWritesPlayerName() {
        Board board = new Board(3, 3);
        Path file = Path.of("test_player.xml");

        String playerName = "Ancsa";
        BoardIO.saveToXml(board, file, playerName);

        try {
            String content = Files.readString(file);

            // mivel most XML kommentet írsz:
            assertTrue(content.contains("<!-- Player: " + playerName + " -->"),
                    "A fájl tartalmazza a játékos nevét a mentésben.");

        } catch (IOException e) {
            fail("Nem sikerült beolvasni a fájlt: " + e.getMessage());
        }

        // takarítás
        try { Files.deleteIfExists(file); }
        catch (IOException ignored) {}
    }
}
