package hu.amoba.service;

import hu.amoba.model.Board;

import java.util.List;
import java.util.Random;

/**
 * Egyszerű gépi játékos (AI).
 * A Board osztálytól elkéri az összes szabályos lépést,
 * és ezek közül véletlenszerűen kiválaszt egyet.
 */
public class AI {

    /** Véletlenszám-generátor a lehetséges lépések közül választáshoz. */
    private final Random rnd = new Random();

    /**
     * Kiválaszt egy véletlen érvényes lépést a táblán.
     *
     * @param board az aktuális játék tábla
     * @return egy {sor, oszlop} tömb, vagy null, ha nincs több lépés
     */
    public int[] pickMove(Board board) {
        List<int[]> legal = board.legalMoves();   // szabad, legális mezők
        if (legal.isEmpty()) {
            return null;                          // nincs több lépés → döntetlen
        }
        return legal.get(rnd.nextInt(legal.size()));
    }
}
