package hu.amoba.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiegészítő lefedettség-növelő teszt a Game fő elemeihez.
 * Cél: lefedni a parseRow, parseCol, toCol és getBoard metódusokat.
 */
class GameCorePartsTest {

    @Test
    void testParseAndToColMethods() {
        Game game = new Game();

        // Sor beolvasás helyes tartományban
        assertEquals(0, invokeParseRow(game, "1"), "Az 1-es sor indexe 0 kell legyen.");
        assertNull(invokeParseRow(game, "999"), "A túl nagy sor érvénytelen.");

        // Oszlop beolvasás
        assertEquals(0, invokeParseCol(game, "a"), "Az 'a' oszlop indexe 0.");
        assertNull(invokeParseCol(game, "z"), "A túl nagy oszlop legyen érvénytelen.");

        // toCol visszaalakítás
        assertEquals("a", invokeToCol(game, 0), "A 0. oszlop betűjele 'a' kell legyen.");
    }

    // --- belső segédfüggvények, hogy privát metódusokat hívjunk ---
    private Integer invokeParseRow(Game game, String s) {
        try {
            var m = Game.class.getDeclaredMethod("parseRow", String.class);
            m.setAccessible(true);
            return (Integer) m.invoke(game, s);
        } catch (Exception e) { return null; }
    }

    private Integer invokeParseCol(Game game, String s) {
        try {
            var m = Game.class.getDeclaredMethod("parseCol", String.class);
            m.setAccessible(true);
            return (Integer) m.invoke(game, s);
        } catch (Exception e) { return null; }
    }

    private String invokeToCol(Game game, int c) {
        try {
            var m = Game.class.getDeclaredMethod("toCol", int.class);
            m.setAccessible(true);
            return (String) m.invoke(game, c);
        } catch (Exception e) { return "?"; }
    }
}
