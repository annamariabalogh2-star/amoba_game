package hu.amoba.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kombinált teszt: érvényes és érvénytelen lépések vegyesen.
 * Ezzel a Board.place() és hasFiveInARow() maradék ágai is lefedésre kerülnek.
 */
class BoardMixedMovesTest {

    @Test
    void testMixedValidAndInvalidMoves() {
        Board b = new Board(5, 5);

        // Első érvényes lépés középre
        assertTrue(b.place(2, 2, 'X'));

        // Pár szomszédos érvényes lépés
        assertTrue(b.place(2, 3, 'O'));
        assertTrue(b.place(3, 3, 'X'));

        // Egy nem szomszédos érvénytelen lépés
        assertFalse(b.place(0, 0, 'O'));

        // Egy már foglalt mezőre tett lépés is legyen hamis
        assertFalse(b.place(2, 2, 'O'));

        // Ha eddig nem nyert senki, győzelem még ne legyen
        assertFalse(b.hasFiveInARow('X'));
        assertFalse(b.hasFiveInARow('O'));
    }
}

