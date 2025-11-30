package hu.amoba.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import hu.amoba.model.Board;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameFlowTest {

    private InputStream eredetiIn;

    @BeforeEach
    void elmentjukEredetiSystemIn() {
        eredetiIn = System.in;
    }

    @AfterEach
    void visszaallitjukSystemIn() {
        System.setIn(eredetiIn);
    }

    @Test
    void egyszeruJatekKilepessel() {
        // Ez a script olyan, mintha a felhasználó beírná ezeket a sorokat:
        String script = String.join(System.lineSeparator(),
                "Ancsa",   // név
                "n",       // nem töltünk be korábbi mentést
                "n",       // nincs automata kezdőlépés
                "kilep",   // rögtön kilépünk
                "n"        // nem kezdünk új játékot
        ) + System.lineSeparator();

        ByteArrayInputStream testInput =
                new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);

        Game game = new Game();

        assertDoesNotThrow(() -> game.start());
    }

    @Test
    void ervenyesLepesTeszt() {
        // Ez a script most egy valódi lépést is tartalmaz:
        String script = String.join(System.lineSeparator(),
                "Ancsa",     // név
                          "n",         // nem töltünk be korábbi mentést
                          "n",         // nincs automata kezdőlépés
                          "lepes 3 c", // itt történik egy VALÓDI lépés
                          "kilep",     // kilépünk
                          "n"          // nincs új játék
        ) + System.lineSeparator();

        ByteArrayInputStream testInput =
                new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);

        Game game = new Game();

        assertDoesNotThrow(() -> game.start());
    }

    @Test
    void automatikusKezdolepesTeszt() {
        // Script: név → nem töltünk → automatikus kezdőlépés IGEN → kilépés → nincs új játék
        String script = String.join(System.lineSeparator(),
                "Ancsa",   // név
                          "n",       // korábbi mentés: nem
                          "i",       // AUTOMATIKUS kezdőlépést kérünk
                          "kilep",   // utána kilépünk
                          "n"        // nem kérünk új játékot
        ) + System.lineSeparator();

        ByteArrayInputStream testInput =
                new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);

        Game game = new Game();

        assertDoesNotThrow(() -> game.start());
    }

    @Test
    void helpEsMentesParancsokTeszt() {
        String script = String.join(System.lineSeparator(),
                "Ancsa",   // név
                          "n",       // korábbi mentés: nem
                          "n",       // automatikus kezdőlépés: nem
                          "help",    // súgó parancs
                          "ment",    // mentés txt-be
                          "kilep",   // kilépünk a játékból
                          "n"        // nem kérünk új játékot
        ) + System.lineSeparator();

        ByteArrayInputStream testInput =
                new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);

        Game game = new Game();

        // Csak azt ellenőrizzük, hogy a folyamat nem száll el.
        assertDoesNotThrow(() -> game.start());
    }

    @Test
    void mentesEsBetoltesTxtTeszt() {
        String script = String.join(System.lineSeparator(),
                "Ancsa",   // név
                          "n",       // korábbi mentés: nem
                          "n",       // automatikus kezdőlépés: nem
                          "ment",    // mentés txt-be (itt létrejön / felülíródik a fájl)
                          "betolt",  // ugyanabban a futásban betöltjük
                          "kilep",   // kilépünk
                          "n"        // nem kérünk új játékot
        ) + System.lineSeparator();

        ByteArrayInputStream testInput =
                new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);

        Game game = new Game();

        assertDoesNotThrow(() -> game.start());
    }

    @Test
    void mentesEsBetoltesXmlTeszt() {
        String script = String.join(System.lineSeparator(),
                "Ancsa",     // név
                          "n",         // korábbi mentés txt-ből: nem
                          "n",         // automatikus kezdőlépés: nem
                          "xmlment",   // táblamentés XML-be
                          "xmlbetolt", // tábla betöltése XML-ből
                          "kilep",     // kilépünk a játékból
                          "n"          // nem kérünk új játékot
        ) + System.lineSeparator();

        ByteArrayInputStream testInput =
                new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);

        Game game = new Game();

        assertDoesNotThrow(() -> game.start());
    }

    @Test
    void scoreParancsTeszt() {
        String script = String.join(System.lineSeparator(),
                "Ancsa",   // név
                          "n",       // korábbi mentés: nem
                          "n",       // automatikus kezdőlépés: nem
                          "score",   // ponttábla kiíratása
                          "kilep",   // kilépés
                          "n"        // nem kérünk új játékot
        ) + System.lineSeparator();

        ByteArrayInputStream testInput =
                new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);

        Game game = new Game();

        assertDoesNotThrow(() -> game.start());
    }

    @Test
    void ervenytelenSorOszlopLepesnel() {
        String script = String.join(System.lineSeparator(),
                "Ancsa",   // név
                "n",       // korábbi mentés: nem
                "n",       // automatikus kezdőlépés: nem
                "lepes 999 z", // teljesen rossz sor/oszlop
                "kilep",   // utána kilépünk
                "n"        // nincs új játék
        ) + System.lineSeparator();

        ByteArrayInputStream testInput =
                new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);

        Game game = new Game();

        assertDoesNotThrow(() -> game.start());
    }

    @Test
    void ervenytelenLepesFoglaltMezore() {
        String script = String.join(System.lineSeparator(),
                "Ancsa",
                "n",
                "n",
                "lepes 3 c", // (3,c) → 2,2 index
                "kilep",
                "n"
        ) + System.lineSeparator();

        ByteArrayInputStream testInput =
                new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);

        Game game = new Game();

        // Előre foglaljuk a (2,2)-t, így a later board.place() false-t ad vissza
        Board board = game.getBoard();
        board.place(2, 2, 'X');

        assertDoesNotThrow(() -> game.start());
    }

    @Test
    void ismeretlenParancsHelpUtasitassal() {
        String script = String.join(System.lineSeparator(),
                "Ancsa",
                "n",
                "n",
                "blabla", // semmilyen ismert parancs
                "kilep",
                "n"
        ) + System.lineSeparator();

        ByteArrayInputStream testInput =
                new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);

        Game game = new Game();

        assertDoesNotThrow(() -> game.start());
    }


}
