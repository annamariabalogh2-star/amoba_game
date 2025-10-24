package hu.amoba.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Egységtesztek a Board osztályhoz.
 * Minden teszt egy-egy konkrét funkciót vizsgál:
 * - üres tábla létrehozás
 * - jel lerakása
 * - szomszédos mező szabály
 * - győzelem ellenőrzés
 * - legális lépések
 *
 * Az @Test annotációval jelölt metódusokat automatikusan futtatja a JUnit.
 */
public class BoardTest {

    private Board board;

    /**
     * Minden teszt előtt lefut, új, tiszta táblát hoz létre.
     * Így a tesztek nem zavarják egymást.
     */
    @BeforeEach
    void setup() {
        board = new Board(10, 10);
    }

    /**
     * Ellenőrzi, hogy az üres tábla helyesen jön-e létre:
     * minden cella '-'
     */
    @Test
    void testEmptyBoardCreation() {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                assertEquals('-', board.getCells()[r][c],
                        "Az üres tábla minden mezője '-' kell legyen");
            }
        }
    }

    /**
     * Teszteli, hogy jel lerakása sikeres,
     * és valóban bekerül a táblába.
     */
    @Test
    void testPlaceMarkSuccessfully() {
        boolean ok = board.place(0, 0, 'X');
        assertTrue(ok, "A lépésnek sikeresnek kell lennie");
        assertEquals('X', board.getCells()[0][0], "A mezőn meg kell jelennie az X-nek");
    }

    /**
     * Teszt: nem lehet a táblán kívülre lépni.
     */
    @Test
    void testCannotPlaceOutsideBoard() {
        boolean ok = board.place(10, 10, 'X'); // 0–9 indexek engedettek
        assertFalse(ok, "Nem léphetünk a táblán kívülre");
    }

    /**
     * Teszt: nem lehet egy már foglalt mezőre újra lépni.
     */
    @Test
    void testCannotPlaceOnOccupiedCell() {
        board.place(0, 0, 'X');
        boolean ok = board.place(0, 0, 'O');
        assertFalse(ok, "Nem szabad újra lépni foglalt mezőre");
    }

    /**
     * Teszt: a hasAnyMark() felismeri, ha már van valami a táblán.
     */
    @Test
    void testHasAnyMark() {
        assertFalse(board.hasAnyMark(), "Kezdetben nincs jel");
        board.place(5, 5, 'X');
        assertTrue(board.hasAnyMark(), "Legalább egy jelnek lennie kell");
    }

    /**
     * Teszt: a szomszédos mező felismerése.
     */
    @Test
    void testIsAdjacentToAny() {
        board.place(5, 5, 'X');
        // A (6,6) mező diagonálisan szomszédos
        assertTrue(board.isAdjacentToAny(6, 6),
                "A (6,6) mező diagonálisan szomszédos, tehát igaz kell legyen");

        // A (0,0) messze van → nem szomszédos
        assertFalse(board.isAdjacentToAny(0, 0),
                "A (0,0) messze van, tehát hamis kell legyen");
    }

    /**
     * Teszt: az 5 egymás melletti 'X' vízszintesen győzelmet eredményez.
     */
    @Test
    void testFiveInARowHorizontalWin() {
        for (int c = 0; c < 5; c++) {
            board.place(0, c, 'X');
        }
        assertTrue(board.hasFiveInARow('X'),
                "5 egymás melletti X vízszintesen = győzelem");
    }

    /**
     * Teszt: az 5 egymás melletti 'X' függőlegesen győzelmet eredményez.
     */
    @Test
    void testFiveInARowVerticalWin() {
        for (int r = 0; r < 5; r++) {
            board.place(r, 0, 'X');
        }
        assertTrue(board.hasFiveInARow('X'),
                "5 egymás melletti X függőlegesen = győzelem");
    }

    /**
     * Teszt: az 5 egymás melletti 'X' átlósan (↘) is győzelem.
     */
    @Test
    void testFiveInARowDiagonalRightDown() {
        for (int i = 0; i < 5; i++) {
            board.place(i, i, 'X');
        }
        assertTrue(board.hasFiveInARow('X'),
                "5 egymás melletti X átlósan ↘ = győzelem");
    }

    /**
     * Teszt: az 5 egymás melletti 'X' átlósan (↙) is győzelem.
     */
    @Test
    void testFiveInARowDiagonalLeftDown() {
        int c = 4;
        for (int r = 0; r < 5; r++) {
            board.place(r, c--, 'X');
        }
        assertTrue(board.hasFiveInARow('X'),
                "5 egymás melletti X átlósan ↙ = győzelem");
    }

    /**
     * Teszt: ha kevesebb, mint 5 jel van, nem lehet győzelem.
     */
    @Test
    void testLessThanFiveIsNotWin() {
        for (int i = 0; i < 4; i++) {
            board.place(0, i, 'X');
        }
        assertFalse(board.hasFiveInARow('X'),
                "4 azonos jel még nem győzelem");
    }

    /**
     * Teszt: a legalMoves() visszaad üres és szomszédos helyeket.
     */
    @Test
    void testLegalMoves() {
        // Teljesen üres tábla esetén minden mező engedett (első lépés)
        List<int[]> legal1 = board.legalMoves();
        assertEquals(100, legal1.size(), "Üres tábla esetén 100 legális mező (10x10)");

        // Ha lerakunk egy jelet, csak a szomszédosak lesznek legálisak
        board.place(5, 5, 'X');
        List<int[]> legal2 = board.legalMoves();
        // 3x3 területből 8 szomszédos marad (a középső foglalt)
        assertTrue(legal2.size() < 100, "Nem lehet már akárhova lépni, csak a szomszédos mezőkre");
    }
    /**
     * Teszt: a print() nem dob hibát (nem tartalmaz logikai ellenőrzést, csak kimenet).
     */
    @Test
    void testPrintBoardDoesNotThrow() {
        assertDoesNotThrow(() -> board.print(),
                "A tábla kiíratása nem okozhat kivételt");
    }


}
