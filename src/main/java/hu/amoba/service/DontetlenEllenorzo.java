package hu.amoba.service;

import hu.amoba.model.Board;

public class DontetlenEllenorzo {

    /**
     * Eldönti, hogy a játék döntetlennel ért-e véget.
     * Akkor döntetlen, ha nincs több üres mező és senki sem nyert.
     */
    public boolean isDraw(Board board) {
        // Ha van győztes, akkor nem döntetlen
        if (board.hasFiveInARow('X') || board.hasFiveInARow('O')) {
            return false;
        }

        // Ellenőrizzük, hogy maradt-e üres mező
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.get(r, c) == '-') {
                    return false; // még van üres hely
                }
            }
        }
        return true; // nincs üres mező és nincs győztes → döntetlen
    }
}

