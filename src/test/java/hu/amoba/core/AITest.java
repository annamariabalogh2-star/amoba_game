package hu.amoba.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Egyszerű teszt az AI működésének ellenőrzésére.
 * Cél: megbizonyosodunk róla, hogy az AI képes legalább egy érvényes lépést kiválasztani
 * egy olyan táblán, ahol van szabad mező.
 */
class AITest {

    @Test
    void testPickMoveReturnsValidCoordinates() {
        // 3x3-as tábla létrehozása
        Board board = new Board(3, 3);

        // Egyetlen X-et lehelyezünk középre, hogy legyen érvényes környező mező
        board.place(1, 1, 'X');

        // AI példány létrehozása
        AI ai = new AI();

        // AI megpróbál választani egy érvényes lépést
        int[] move = ai.pickMove(board);

        // Ellenőrzés: a visszaadott lépés nem lehet null (azaz talált helyet)
        assertNotNull(move, "Az AI-nek legalább egy lehetséges lépést vissza kell adnia.");

        // Ellenőrzés: a választott koordináták a tábla határain belül legyenek
        assertTrue(board.isInBounds(move[0], move[1]), "Az AI által választott lépésnek a tábla határain belül kell lennie.");

        // Extra: a kiválasztott mező üres legyen (nem írhat felül másik jelet)
        assertNotEquals('X', board.get(move[0], move[1]), "AI nem választhat foglalt mezőt (X).");
        assertNotEquals('O', board.get(move[0], move[1]), "AI nem választhat foglalt mezőt (O).");

    }
}