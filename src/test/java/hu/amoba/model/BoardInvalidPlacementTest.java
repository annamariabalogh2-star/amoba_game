package hu.amoba.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Teszt a Board.place() egyik ritka ágára:
 * ha a mező nem szomszédos, a lépés érvénytelen.
 */
class BoardInvalidPlacementTest {

    @Test
    void testNonAdjacentPlacementRejected() {
        Board b = new Board(5, 5);

        // Első lépés középre
        b.place(2, 2, 'X');

        // Próba: távoli helyre (nem szomszédos)
        boolean ok = b.place(0, 0, 'O');

        assertFalse(ok, "A nem szomszédos mezőre lépést el kell utasítani.");
    }
}
