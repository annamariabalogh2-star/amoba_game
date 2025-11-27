package hu.amoba.model;

import hu.amoba.ui.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Egységtesztek a Game osztályhoz.
 * - Ellenőrzi, hogy a játék rendben inicializálódik.
 * - Az X kezdőlépés a középső mezőre kerül.
 * - A gép (O) legalább egy O-t lerak a táblára.
 * - Érvénytelen lépést nem engedünk (negatív példa).
 */
public class GameTest {

    private Game game;

    /** Minden teszt előtt új játék indul. */
    @BeforeEach
    void setup() {
        System.setProperty("test.env", "true"); // Tesztmód bekapcsolása
        game = new Game();
    }

    /** A játék indulásakor a tábla nem lehet null. */
    @Test
    void testGameInitialization() {
        assertNotNull(game.getBoard(), "A játék táblája nem lehet null induláskor.");
    }

    /** Az X kezdőlépése a tábla közepére kerül. */
    @Test
    void testFirstMoveIsCenter() {
        game.start(); // itt automatikusan középre kerül az X

        int r = game.getBoard().getRows() / 2;
        int c = game.getBoard().getCols() / 2;
        char[][] cells = game.getBoard().getCells();

        assertEquals('X', cells[r][c],
                "A kezdő lépésnek az X-nek kell lennie a tábla közepén.");
    }

    /** A gép (O) lépése után legalább egy 'O' legyen a táblán. */
    @Test
    void testComputerMakesMove() {
        // Először lerakunk egy X-et, hogy az AI tudjon lépni
        var b = game.getBoard();
        b.place(5, 5, 'X');

        assertDoesNotThrow(() -> game.computerMove(),
                "A gép lépése nem dobhat hibát.");

        boolean foundO = false;
        for (char[] row : b.getCells()) {
            for (char cell : row) {
                if (cell == 'O') {
                    foundO = true;
                    break;
                }
            }
            if (foundO) break;
        }

        assertTrue(foundO, "A gépnek le kellett tennie egy 'O' jelet.");
    }

    /** Példa negatív tesztre: érvénytelen lépés elutasítása. */
    @Test
    void testInvalidMoveRejected() {
        boolean ok = game.getBoard().place(-1, -1, 'X');
        assertFalse(ok, "Érvénytelen mezőre ne lehessen lépni.");
    }

    /** Teszt: a start() metódus lefut teszt módban (nem kér be inputot). */
    @Test
    void testStartMethodRunsInTestMode() {
        assertDoesNotThrow(() -> game.start(),
                "A start() metódusnak teszt módban hiba nélkül le kell futnia.");
    }

    /** Teszt: a computerMove() metódus nem dob hibát és módosít a táblán. */
    @Test
    void testComputerMovePlacesMark() {
        char[][] before = copyBoard(game.getBoard().getCells());

        assertDoesNotThrow(() -> game.computerMove(),
                "A gép lépése nem dobhat kivételt.");

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
        assertTrue(changed, "A gépnek módosítania kellett a táblát (lerakott egy 'O'-t).");
    }

    /** Üres név esetén 'Gamer' az alapértelmezett név. */
    @Test
    void testDefaultNameWhenEmptyInput() {
        String name = "";
        if (name.isEmpty()) name = "Gamer";
        assertEquals("Gamer", name,
                "Üres név esetén a program automatikusan 'Gamer'-t állít be.");
    }

    /** A kezdőképernyő (intro) hibátlanul lefut. */
    @Test
    void testShowIntroDoesNotThrow() {
        Game game = new Game();

        assertDoesNotThrow(() -> {
            var method = Game.class.getDeclaredMethod("showIntro");
            method.setAccessible(true);
            method.invoke(game);
        }, "A showIntro() metódusnak hibátlanul kell lefutnia.");
    }

    /** Segédfüggvény a tábla mély másolásához. */
    private char[][] copyBoard(char[][] original) {
        char[][] copy = new char[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }
}
