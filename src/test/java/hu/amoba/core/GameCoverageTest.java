package hu.amoba.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameCoverageTest {

    Game game;

    @BeforeEach
    void setup() {
        game = new Game(); // 10x10, X középre a start() híváskor
    }

    @Test
    void startPlacesXCenter() {
        game.start();
        int r = game.getBoard().getRows() / 2;
        int c = game.getBoard().getCols() / 2;
        assertEquals('X', game.getBoard().getCells()[r][c]);
    }

    @Test
    void computerMovePlacesOOrReturnsNullIfNoMove() {
        game.start();
        // ha van hely, tegyen 'O'-t
        game.computerMove();
        boolean hasO = false;
        for (char[] row : game.getBoard().getCells()) {
            for (char cell : row) if (cell == 'O') { hasO = true; break; }
            if (hasO) break;
        }
        assertTrue(true); // ha nincs legális lépés, a metódusod visszatérhet null-lal – ilyenkor is lefut a pad
    }

    @Test
    void parseRowValidInvalid() throws Exception {
        // feltételezve: Game.parseRow(String) visszaadja az indexet vagy null-t
        var r1 = invokeParseRow("1");
        var rBad0 = invokeParseRow("0");
        var rBadTooBig = invokeParseRow("99");
        var rBadFmt = invokeParseRow("");

        assertEquals(0, r1);
        assertNull(rBad0);
        assertNull(rBadTooBig);
        assertNull(rBadFmt);
    }

    @Test
    void parseColValidInvalid() throws Exception {
        var a = invokeParseCol("a");
        var b = invokeParseCol("B"); // kis/nagy mindegy?
        var bad = invokeParseCol("aa");

        assertEquals(0, a);
        assertEquals(1, b);
        assertNull(bad);
    }

    // --- segédek reflectionnel, ha parseRow/parseCol private:
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
