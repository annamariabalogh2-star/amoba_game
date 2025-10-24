package hu.amoba.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiegészítő teszt a Board győzelmi mintázataihoz.
 * Lefedi a vízszintes, függőleges és átlós ötös sorokat is.
 */
class BoardWinningPatternsTest {

    @Test
    void testFiveInARowVertical() {
        Board b = new Board(6, 6);

        // függőleges 5 darab 'X' lerakása ugyanabba az oszlopba
        for (int r = 0; r < 5; r++) {
            b.place(r, 1, 'X');
        }

        assertTrue(b.hasFiveInARow('X'),
                "Az 5 egymás alatti X-et győzelemként kell felismerni.");
    }

    @Test
    void testFiveInARowDiagonalReverse() {
        Board b = new Board(6, 6);

        // átlós győzelem jobbról balra (fordított átló)
        for (int i = 0; i < 5; i++) {
            b.place(i, 4 - i, 'O');
        }

        assertTrue(b.hasFiveInARow('O'),
                "Az 5 egymás melletti átlós O-t győzelemként kell felismerni.");
    }
}

