package hu.amoba.service;

import java.util.List;
import java.util.Random;

import hu.amoba.model.Board;

/**
 * Egyszerű gépi játékos (AI). A Board osztálytól elkéri az összes szabályos lépést,
 * és ezek közül véletlenszerűen kiválaszt egyet.
 */
public class AI {                                   // Egyszerű gépi játékos

    private final Random rnd = new Random();        // Véletlenszám-generátor a lépések kiválasztásához

    public int[] pickMove(Board board) {            // Kiválaszt egy lépést a táblán
        List<int[]> legal = board.legalMoves();     // Elkéri a Board-tól az összes szabályos lépést (sor, oszlop párok)
        if (legal.isEmpty()) {                      // Ha nincs egyetlen szabályos lépés sem
            return null;                            // nincs több lépés, döntetlen
        }
        return legal.get(rnd.nextInt(legal.size())); // Véletlenszerűen kiválaszt egyet a lehetséges lépések közül, és visszaadja
    }
}
