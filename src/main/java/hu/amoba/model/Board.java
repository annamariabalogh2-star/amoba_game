package hu.amoba.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A játéktábla és a szabályok:
 *
 * - 10x10 méretű az alap, de paraméterezhető.
 * - Az üres mező jelölése: '-'
 * - Csak olyan üres mezőre lehet lépni, ami LEGALÁBB diagonálisan szomszédos bármelyik már lerakott jellel (8-irányú szomszédság).
 * - 5 azonos jel egymás mellett (vízsz., függ., átlók) jelenti a győzelmet.
 */
public class Board {
    private final int rows;
    private final int cols;
    private final char[][] cells;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new char[rows][cols];
        // Kezdetben minden üres
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = '-';
            }
        }
    }

    /** Kényelmi konstruktor: négyzetes tábla (alapméret: 10 x 10). */
    public Board() {
        this(10, 10);
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    /** Visszaadja a cellák 2D tömbjét. */
    public char[][] getCells() { return cells; }

    /** Lerak egy jelet, ha a lépés szabályos. */
    public boolean place(int r, int c, char mark) {
        if (!isInside(r, c)) return false;
        if (cells[r][c] != '-') return false;
        // Ha a tábla teljesen üres első lépéskor, bármely üres mező illetve az előírt közép lépést a Game intézi.
        if (!hasAnyMark()) {
            cells[r][c] = mark;
            return true;
        }
        // Máskülönben: csak akkor tehetünk ide, ha szomszédos legalább 1 meglévő jellel
        if (!isAdjacentToAny(r, c)) return false;

        cells[r][c] = mark;
        return true;
    }

    /** Ellenőrzés, hogy van-e már bármilyen jel a táblán? */
    public boolean hasAnyMark() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (cells[r][c] != '-') return true;
        return false;
    }

    /** A (r,c) mező 8-irányban szomszédos-e bármely lerakott X/O mezővel? */
    public boolean isAdjacentToAny(int r, int c) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int rr = r + dr, cc = c + dc;
                if (isInside(rr, cc) && cells[rr][cc] != '-') {
                    return true;
                }
            }
        }
        return false;
    }

    /** A mező a táblán belül helyezkedik-e el? */
    public boolean isInside(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    /** Győzelem-ellenőrzés: van-e 5 egymás mellett ugyanabból a jelből. */
    public boolean hasFiveInARow(char mark) {
        int need = 5;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] != mark) continue;

                // vízszintes
                if (c + need <= cols) {
                    boolean ok = true;
                    for (int k = 0; k < need; k++)
                        if (cells[r][c + k] != mark) { ok = false; break; }
                        if (ok)

                    return true;
                }
                // függőleges
                if (r + need <= rows) {
                    boolean ok = true;
                    for (int k = 0; k < need; k++)
                        if (cells[r + k][c] != mark) { ok = false; break; }
                        if (ok)

                    return true;
                }
                // átló ↘
                if (r + need <= rows && c + need <= cols) {
                    boolean ok = true;
                    for (int k = 0; k < need; k++)
                        if (cells[r + k][c + k] != mark) { ok = false; break; }
                        if (ok)

                    return true;
                }
                // átló ↙
                if (r + need <= rows && c - need + 1 >= 0) {
                    boolean ok = true;
                    for (int k = 0; k < need; k++)
                        if (cells[r + k][c - k] != mark) { ok = false; break; }
                        if (ok)

                    return true;
                }
            }
        }
        return false;
    }

    /** Minden olyan üres mező listája, ahova léphetünk, a szomszédos lépés szabályt alkalmazva. */
    public List<int[]> legalMoves() {
        List<int[]> out = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] == '-' && ( !hasAnyMark() || isAdjacentToAny(r, c) )) {
                    out.add(new int[]{r, c});
                }
            }
        }
        return out;
    }

    /** Konzolra rajzolás (sorszámok 1..N, oszlop betűk a..). */
    public void print() {
        // oszlopfejlécek
        System.out.print("    ");
        for (int c = 0; c < cols; c++) {
            char letter = (char)('a' + c); // a,b,c...
            System.out.print(letter + " ");
        }
        System.out.println();
        // sorok
        for (int r = 0; r < rows; r++) {
            System.out.printf("%2d  ", (r + 1));
            for (int c = 0; c < cols; c++) {
                System.out.print(cells[r][c] + " ");
            }
            System.out.println();
        }
    }
    // Ellenőrzi, hogy a megadott sor és oszlop index a tábla határain belül van-e?
    public boolean isInBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    // Visszaadja a megadott mező tartalmát (X, O vagy '-')
    public char get(int r, int c) {
        if (!isInBounds(r, c)) {
            throw new IllegalArgumentException("A megadott koordináta a tábla határain kívül esik.");
        }
        return cells[r][c];
    }
}
