package hu.amoba.vo;

/**
 * Egy lépést (sor, oszlop) reprezentáló, nem módosítható értékosztály.
 */
public final class Move {           // Immutable Value Object: egy lépést ír le

    private final int row;          // A lépés sor indexe (0-indexelt)

    private final int col;          // A lépés oszlop indexe (0-indexelt)

    public Move(int row, int col) { // Konstruktor: beállítja a sor és oszlop értékeket
        this.row = row;
        this.col = col;
    }

    public int row() {              // Visszaadja a sor indexét
        return row;
    }

    public int col() {              // Visszaadja az oszlop indexét
        return col;
    }

    @Override
    public String toString() {      // Szöveges forma (pl. logoláshoz, debughoz)
        return "Move(" + row + "," + col + ")";
    }
}

