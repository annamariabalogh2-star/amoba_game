package hu.amoba.model;

import java.util.List;
import java.util.Random;

/**
 * Ez egy nagyon egyszerű AI, amely bekéri a Board-tól a legális lépéseket és véletlenszerűen kiválaszt
 * egyet a szabad lépések közül
 */
public class AI {
    private final Random rnd = new Random();

    public int[] pickMove(Board board) {
        List<int[]> legal = board.legalMoves();
            if (legal.isEmpty())

            return null; // nincs lépés

        return legal.get(rnd.nextInt(legal.size()));
    }
}
