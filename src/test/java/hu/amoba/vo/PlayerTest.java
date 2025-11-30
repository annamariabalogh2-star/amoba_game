package hu.amoba.vo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PlayerTest {

    @Test
    void toStringKiirjaNevetEsJeloleset() {
        Player p = new Player("Ancsa", 'X');
        String s = p.toString();
        assertTrue(s.contains("Ancsa"));
        assertTrue(s.contains("X"));
    }

    @Test
    void equalsIgazUgyanarra() {
        Player p1 = new Player("Ancsa", 'X');
        Player p2 = p1;
        assertEquals(p1, p2);
    }

    @Test
    void equalsIgazHaNevEsJelMegegyezik() {
        Player p1 = new Player("Ancsa", 'X');
        Player p2 = new Player("Ancsa", 'X');
        assertEquals(p1, p2);
    }

    @Test
    void equalsHamissalHaKulonbozoNevVagyJel() {
        Player p1 = new Player("Ancsa", 'X');
        Player p2 = new Player("Bela", 'X');
        Player p3 = new Player("Ancsa", 'O');

        assertNotEquals(p1, p2);
        assertNotEquals(p1, p3);
    }

    @Test
    void hashCodeNevMegJelAlapjanSzamol() {
        Player p1 = new Player("Ancsa", 'X');
        Player p2 = new Player("Ancsa", 'X');

        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
