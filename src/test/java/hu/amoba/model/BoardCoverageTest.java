package hu.amoba.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardCoverageTest {

    Board board;

    @BeforeEach
    void setup() {
        board = new Board(10, 10);
    }

    @Test
    void placeRejectsOutsideBoard() {
        assertFalse(board.place(10, 0, 'X'));  // r túl nagy
        assertFalse(board.place(-1, 0, 'X'));  // r negatív
        assertFalse(board.place(0, 10, 'X'));  // c túl nagy
        assertFalse(board.place(0, -1, 'X'));  // c negatív
    }

    @Test
    void cannotPlaceOnOccupiedCell() {
        assertTrue(board.place(0, 0, 'X'));    // első lépés: ok
        assertFalse(board.place(0, 0, 'O'));   // foglalt
    }

    @Test
    void firstMoveAllowedAnywhere() {
        // üres táblán true
        assertTrue(board.place(5, 5, 'X'));
    }

    @Test
    void secondMoveMustBeAdjacent() {
        assertTrue(board.place(5, 5, 'X'));    // első
        assertTrue(board.place(5, 6, 'O'));    // szomszédos: ok
        assertFalse(board.place(0, 0, 'X'));   // messze: nem ok
    }

    @Test
    void hasAnyMarkWorks() {
        assertFalse(board.hasAnyMark());
        board.place(0, 0, 'X');
        assertTrue(board.hasAnyMark());
    }

    @Test
    void detectsWinHorizontal() {
        for (int c = 0; c < 5; c++) board.place(0, c, 'X');
        assertTrue(board.hasFiveInARow('X'));
    }

    @Test
    void detectsWinVertical() {
        for (int r = 0; r < 5; r++) board.place(r, 0, 'O');
        assertTrue(board.hasFiveInARow('O'));
    }

    @Test
    void detectsWinDiagDownRight() {
        for (int i = 0; i < 5; i++) board.place(i, i, 'X');
        assertTrue(board.hasFiveInARow('X'));
    }

    @Test
    void detectsWinDiagDownLeft() {
        // átló ↙ (pl. (0,4)...(4,0))
        for (int i = 0; i < 5; i++) board.place(i, 4 - i, 'O');
        assertTrue(board.hasFiveInARow('O'));
    }
}

