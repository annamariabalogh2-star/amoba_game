package hu.amoba.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import hu.amoba.model.Board;

public class DontetlenEllenorzoTest {

    @Test
    void isDrawIgazHaTeleVanEsNincsNyertes() {
        // 10x10-es tábla – ha nálad más a méret, írd át
        Board board = new Board(10, 10);

        // Kétféle sor-minta: úgy töltjük fel, hogy se vízszintesen,
        // se függőlegesen, se átlósan ne legyen 5 azonos jel egymás után.
        char[] mintaA = {'X','X','O','O','X','X','O','O','X','X'};
        char[] mintaB = {'O','O','X','X','O','O','X','X','O','O'};

        for (int r = 0; r < 10; r++) {
            char[] sorMinta = (r % 2 == 0) ? mintaA : mintaB;
            for (int c = 0; c < 10; c++) {
                board.place(r, c, sorMinta[c]);
            }
        }

        DontetlenEllenorzo ellenorzo = new DontetlenEllenorzo();

        assertTrue(ellenorzo.isDraw(board),
                "Tele táblánál, nyertes nélkül döntetlennek kell lennie.");
    }

    @Test
    void isDrawHamisHaVanUresMezo() {
        Board board = new Board(10, 10);

        // Csak néhány mezőt töltünk ki, maradjon bőven üres hely
        board.place(0, 0, 'X');
        board.place(0, 1, 'O');
        board.place(1, 0, 'X');

        DontetlenEllenorzo ellenorzo = new DontetlenEllenorzo();

        assertFalse(ellenorzo.isDraw(board),
                "Ha van üres mező, nem lehet döntetlen.");
    }
}
