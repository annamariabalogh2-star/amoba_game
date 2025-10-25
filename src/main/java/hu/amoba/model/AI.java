package hu.amoba.model;

import java.util.List;
import java.util.Random;

/**
 * Rééém egyszerű AI:
 * - bekéri a Board-tól a legális lépéseket
 * - véletlenszerűen kiválaszt egyet
 */
public class AI {
    private final Random rnd = new Random();

    public int[] pickMove(Board board) {
        List<int[]> legal = board.legalMoves();
        if (legal.isEmpty()) return null; // nincs lépés
        return legal.get(rnd.nextInt(legal.size()));
    }
}
