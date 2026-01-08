package hu.amoba.service;

import hu.amoba.model.Board;

/**
 * Eldönti, hogy a játék döntetlennel ért-e véget. Akkor döntetlen, ha nincs több üres mező és senki sem nyert.
 */
public class DontetlenEllenorzo {                       // Döntetlen állapot ellenőrzésére szolgáló osztály

    public boolean isDraw(Board board) {                // Eldönti, hogy a játék döntetlen-e
        if (board.hasFiveInARow('X') || board.hasFiveInARow('O')) { // Ha bármelyik játékos már nyert nem döntetlen
            return false;                               // Ha van győztes, akkor nem döntetlen
        }
        // Végignézzük a teljes táblát
        for (int r = 0; r < board.getRows(); r++) {     // Sorokon végigmegy
            for (int c = 0; c < board.getCols(); c++) { // Oszlopokon végigmegy
                if (board.get(r, c) == '-') {           // Ha találunk üres mezőt
                    return false;                       // Akkor még nem döntetlen
                }
            }
        }
        return true;                                    // Nincs üres mező és nincs győztes, döntetlen
    }
}

