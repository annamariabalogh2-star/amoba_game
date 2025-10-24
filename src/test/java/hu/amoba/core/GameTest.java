package hu.amoba.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Egységtesztek a Game osztályhoz.
 * - Ellenőrzi, hogy a játék rendben inicializálódik
 * - Az X kezdőlépés a középső mezőre kerül
 * - A gép (O) legalább egy O-t lerak a táblára, amikor sorra kerül
 * - Érvénytelen lépést nem engedünk (negatív példa)
 */
public class GameTest {

    private Game game;

    /** Minden teszt előtt új játék. */
    @BeforeEach
    void setup() {
        game = new Game();
    }

    /** A játék indulásakor a tábla nem lehet null. */
    @Test
    void testGameInitialization() {
        assertNotNull(game.getBoard(), "A játék táblája nem lehet null induláskor");
    }

    /** Az X kezdő lépése a tábla közepére kerül. */
    @Test
    void testFirstMoveIsCenter() {
       // game.start(); // elindítja a játékot, X automatikus középre lép

        int centerRow = game.getBoard().getRows() / 2;
        int centerCol = game.getBoard().getCols() / 2;

        char[][] cells = game.getBoard().getCells();
        assertEquals('X', cells[centerRow][centerCol],
                "A kezdő lépésnek az X-nek kell lennie a középen");
    }

    /** A gép (O) lépése után legalább egy 'O' legyen a táblán. */
    @Test
    void testComputerMakesMove() {
       // game.start();           // X középre lép
       // game.computerMove();    // gép lép

        boolean foundO = false;
        for (char[] row : game.getBoard().getCells()) {
            for (char cell : row) {
                if (cell == 'O') { foundO = true; break; }
            }
            if (foundO) break;
        }
        assertTrue(foundO, "A gépnek le kell tennie egy 'O' jelet");
    }

    /** Példa negatív tesztre: érvénytelen lépés elutasítása. */
    @Test
    void testInvalidMoveRejected() {
        // feltételezzük, hogy Board.place(r,c,mark) ad vissza booleant
        // és a Game.getBoard() elérhető
        boolean ok = game.getBoard().place(-1, -1, 'X'); // nyilván érvénytelen
        assertFalse(ok, "Érvénytelen mezőre ne lehessen lépni");
    }



    /**
     * Teszt: a start() metódus lefut teszt módban (nem kér be inputot).
     * Ez lefedi a Game.start() logikát, de nem indít interaktív játékot.
     */
    @Test
    void testStartMethodRunsInTestMode() {
        // Teszt mód bekapcsolása
        System.setProperty("test.env", "true");

        // Nem szabad kivételt dobnia
        assertDoesNotThrow(() -> game.start(),
                "A start() metódusnak teszt módban le kell futnia hiba nélkül");

        // Teszt mód visszaállítása
        System.clearProperty("test.env");
    }

    /**
     * Teszt: a computerMove() metódus nem dob hibát és módosít a táblán.
     */
    @Test
    void testComputerMovePlacesMark() {
        char[][] before = copyBoard(game.getBoard().getCells());

        assertDoesNotThrow(() -> game.computerMove(),
                "A gép lépése nem dobhat kivételt");

        char[][] after = game.getBoard().getCells();

        boolean changed = false;
        for (int r = 0; r < before.length; r++) {
            for (int c = 0; c < before[r].length; c++) {
                if (before[r][c] != after[r][c]) {
                    changed = true;
                    break;
                }
            }
        }

        assertTrue(changed, "A gépnek módosítania kellett a táblát (lerakott egy 'O'-t)");
    }

    /**
     * Segédfüggvény a tábla mély másolásához.
     */
    private char[][] copyBoard(char[][] original) {
        char[][] copy = new char[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }

}
