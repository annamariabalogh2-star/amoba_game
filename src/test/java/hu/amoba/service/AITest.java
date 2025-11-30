package hu.amoba.service;

import hu.amoba.model.Board;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Egyszerű teszt az AI működésének ellenőrzésére. A cél, megbizonyosodunk róla, hogy az AI képes legalább egy érvényes lépést kiválasztani
 * egy olyan táblán, ahol van szabad mező. */

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

    @Test
    void testPickMoveWhenBoardFull() {
        // Teszt: ha a tábla tele van, az AI-nak null-t kell visszaadnia

        Board full = new Board(3, 3);

        // Telepakoljuk a táblát X és O jelekkel
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                full.place(r, c, (r + c) % 2 == 0 ? 'X' : 'O');
            }
        }

        AI ai = new AI();
        int[] move = ai.pickMove(full);

        assertNull(move, "Ha nincs üres mező, az AI-nak null-t kell visszaadnia.");
    }

    /**
     * Teszt: döntetlen helyzet kezelése: Ha a tábla tele van, az AI nem találhat több legális lépést,
     * ezért a pickMove() metódusnak null-t kell visszaadnia.
     */
    @Test
    void testDrawConditionWhenBoardIsFull() {
        // Kis 3x3-as tábla, hogy gyorsan beteljen
        Board board = new Board(3, 3);

        // Feltöltjük váltakozó X és O jelekkel
        char mark = 'X';
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                board.place(r, c, mark);
                mark = (mark == 'X') ? 'O' : 'X';
            }
        }

        // AI példány létrehozása
        AI ai = new AI();

        // A tábla tele van, így nem lehet több lépés — döntetlen helyzet
        int[] move = ai.pickMove(board);

        assertNull(move, "Ha a tábla tele van, az AI-nek null-t kell visszaadnia, vagyis döntetlen állapot van.");
    }
}