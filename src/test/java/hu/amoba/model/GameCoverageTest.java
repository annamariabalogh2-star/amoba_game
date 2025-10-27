package hu.amoba.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lefedettségi (coverage) teszt a Game osztályhoz.
 * Itt alapvető funkciókat ellenőrzünk, hogy minden kódág lefedett legyen.
 */
public class GameCoverageTest {

    private Game game;

    @BeforeEach
    void setup() {
        System.setProperty("test.env", "true"); // Tesztmód bekapcsolása
        game = new Game();
    }

    /** Ellenőrizzük, hogy az X középre kerül a start() hívás után. */
    @Test
    void startPlacesXCenter() {
        game.start(); // teszt módban automatikusan középre kerülhet
        int r = game.getBoard().getRows() / 2;
        int c = game.getBoard().getCols() / 2;
        assertEquals('X', game.getBoard().getCells()[r][c],
                "A tábla közepén X-nek kell lennie a start() után.");
    }

    /** Ellenőrizzük, hogy a gép (O) lépése nem dob hibát, és lerak legalább egy 'O'-t. */
    @Test
    void computerMovePlacesO() {
        // előtte tegyünk le egy X-et, különben az AI nem tud hova lépni
        var b = game.getBoard();
        b.place(5, 5, 'X');

        assertDoesNotThrow(() -> game.computerMove(),
                "A gép lépése nem dobhat hibát.");

        boolean hasO = false;
        for (char[] row : b.getCells()) {
            for (char cell : row) {
                if (cell == 'O') {
                    hasO = true;
                    break;
                }
            }
            if (hasO) break;
        }

        assertTrue(hasO, "A gépnek legalább egy 'O'-t le kellett tennie.");
    }

    /** A parseRow metódus működésének ellenőrzése. */
    @Test
    void parseRowValidInvalid() throws Exception {
        var r1 = invokeParseRow("1");
        var rBad0 = invokeParseRow("0");
        var rBadTooBig = invokeParseRow("11"); // a 10x10 táblán ez túl nagy
        var rBadFmt = invokeParseRow("");

        assertEquals(0, r1);
        assertNull(rBad0);
        assertNull(rBadTooBig);
        assertNull(rBadFmt);
    }

    /** A parseCol metódus működésének ellenőrzése. */
    @Test
    void parseColValidInvalid() throws Exception {
        var a = invokeParseCol("a");
        var b = invokeParseCol("B"); // kis- és nagybetű egyformán működik
        var bad = invokeParseCol("aa"); // túl hosszú bemenet

        assertEquals(0, a);
        assertEquals(1, b);
        assertNull(bad);
    }

    // --- Segédfüggvények (reflexióval hívjuk a privát metódusokat) ---

    private Integer invokeParseRow(String s) throws Exception {
        var m = Game.class.getDeclaredMethod("parseRow", String.class);
        m.setAccessible(true);
        return (Integer) m.invoke(game, s);
    }

    private Integer invokeParseCol(String s) throws Exception {
        var m = Game.class.getDeclaredMethod("parseCol", String.class);
        m.setAccessible(true);
        return (Integer) m.invoke(game, s);
    }
}
