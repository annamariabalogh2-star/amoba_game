package hu.amoba.vo;

/** A Move (lépés) osztály egy játékos konkrét lépését tárolja.
 * Minden lépés két adatból áll:
 *  - sor (row)
 *  - oszlop (col)
 *
 * Ez az osztály egy egyszerű, - immutable (nem módosítható) - értékosztály (Value Object),
 * ami azt jelenti, hogy a létrehozás után a benne tárolt értékek már nem változnak.
 * Az osztály célja, hogy a lépések kezelését (pl. AI vagy tesztelés során)
 * átláthatóvá és típusbiztossá tegye – így nem kell külön számokat vagy tömböket átadni. */
public final class Move {
    /** A lépés sor indexe (0-tól induló). */
    private final int row;

    /** A lépés oszlop indexe (0-tól induló). */
    private final int col;

    /** Konstruktor, amely létrehoz egy új lépést a megadott sorral és oszloppal.
     * - @param row a lépés sora (0-index)
     * - @param col a lépés oszlopa (0-index) */
    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }
    /** A lépés sor indexének lekérése. */
    public int row() { return row; }

    /** A lépés oszlop indexének lekérése. */
    public int col() { return col; }

    /** Szöveges reprezentáció, pl. "Move(3,4)". Ez főként naplózáshoz vagy hibakereséshez hasznos, hogy
     * lássuk, melyik mezőre lépett a játékos. */
    @Override
    public String toString() {
        return "Move(" + row + "," + col + ")";
    }
}

