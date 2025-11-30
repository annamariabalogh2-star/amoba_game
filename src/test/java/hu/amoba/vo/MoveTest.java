package hu.amoba.vo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MoveTest {

    @Test
    void moveKonstruktorEsGetterek() {
        Move m = new Move(3, 5);
        assertEquals(3, m.row());
        assertEquals(5, m.col());
    }

    @Test
    void toStringTartalmazzaSorEsOszlopot() {
        Move m = new Move(2, 7);
        String s = m.toString();
        assertTrue(s.contains("2"));
        assertTrue(s.contains("7"));
    }
}
