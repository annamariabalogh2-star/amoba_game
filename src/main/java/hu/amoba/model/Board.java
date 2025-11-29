package hu.amoba.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A Board (tábla) osztály az amőba játék tábláját kezeli.
 * A tábla egy kétdimenziós karaktertömb, ahol:
 *  'X'  – emberi játékos
 *  'O'  – gép
 *  '-'  – üres mező
 */
public class Board {

    /** Sorok száma. */
    private final int rows;

    /** Oszlopok száma. */
    private final int cols;

    /** A tábla cellái. */
    private final char[][] cells;

    /** Megadott méretű üres tábla létrehozása. */
    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new char[rows][cols];

        // Kezdetben minden mező üres ('-')
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = '-';
            }
        }
    }

    /** Kényelmi konstruktor: alapból 10x10-es tábla. */
    public Board() {
        this(10, 10);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    /** A teljes tábla tömbjét adja vissza (pl. mentéshez). */
    public char[][] getCells() {
        return cells;
    }

    /**
     * Lerak egy jelet a tábla egy mezőjére, ha a lépés szabályos.
     *
     * Szabályok:
     *  - nem léphetünk a tábla határain kívül;
     *  - nem írhatunk felül már foglalt mezőt;
     *  - ha már volt lépés, akkor csak szomszédos mezőre léphetünk.
     *
     * @return true, ha sikerült a lépés, különben false
     */
    public boolean place(int r, int c, char mark) {
        // Tábla határain belül vagyunk?
        if (!isInBounds(r, c)) {
            return false;
        }

        // Üres a mező?
        if (cells[r][c] != '-') {
            return false;
        }

        // Ha még nincs jel a táblán, az első lépés bárhová mehet
        if (!hasAnyMark()) {
            cells[r][c] = mark;
            return true;
        }

        // Későbbi lépések: csak szomszédos mezőre
        if (!isAdjacentMark(r, c)) {
            return false;
        }

        cells[r][c] = mark;
        return true;
    }

    /**
     * Van-e már legalább egy lerakott jel a táblán?
     */
    public boolean hasAnyMark() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] != '-') {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Szomszédos-e (8 irányban) a mező bármely már lerakott jellel?
     * Ez biztosítja, hogy a játék „egy kupacban” maradjon.
     */
    public boolean isAdjacentMark(int r, int c) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                int rr = r + dr;
                int cc = c + dc;
                if (isInBounds(rr, cc) && cells[rr][cc] != '-') {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Ellenőrzi, hogy a megadott sor/oszlop index a tábla határain belül van-e.
     */
    public boolean isInBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    /**
     * Megnézi, hogy van-e 5 egymás melletti azonos jel a táblán.
     * Négy irányban vizsgál:
     *  - vízszintes (jobbra)
     *  - függőleges (lefelé)
     *  - átló (le-jobbra)
     *  - átló (le-balra)
     */
    public boolean hasFiveInARow(char mark) {
        // Négy irány: jobbra, le, le-jobbra, le-balra
        int[][] iranyok = {
                {0, 1},   // vízszintes →
                {1, 0},   // függőleges ↓
                {1, 1},   // átló ↘
                {1, -1}   // átló ↙
        };

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] != mark) {
                    continue;
                }

                // Minden irányt külön megnézünk
                for (int[] dir : iranyok) {
                    int dr = dir[0];
                    int dc = dir[1];
                    boolean ok = true;

                    // Az aktuális mező benne van, ehhez még 4-et vizsgálunk
                    for (int k = 1; k < 5; k++) {
                        int rr = r + dr * k;
                        int cc = c + dc * k;
                        if (!isInBounds(rr, cc) || cells[rr][cc] != mark) {
                            ok = false;
                            break;
                        }
                    }

                    if (ok) {
                        return true; // találtunk 5 egymás mellettit
                    }
                }
            }
        }
        return false;
    }

    /** Az összes olyan lépést visszaadja, ahová szabályosan lehet lépni. Ha még üres a tábla, bárhová lehet; különben csak szomszédos üres mezőre. */
    public List<int[]> legalMoves() {
        List<int[]> out = new ArrayList<>();
        boolean vanJel = hasAnyMark();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] == '-') {
                    if (!vanJel || isAdjacentMark(r, c)) {
                        out.add(new int[]{r, c});
                    }
                }
            }
        }
        return out;
    }

    /** A tábla kirajzolása a konzolra. Oszlopok: betűk (a, b, c, ...), sorok: számok (1, 2, 3, ...). */
    public void print() {
        // Oszlopfejlécek
        System.out.print("    ");
        for (int c = 0; c < cols; c++) {
            char letter = (char) ('a' + c);
            System.out.print(letter + " ");
        }
        System.out.println();

        // Sorok
        for (int r = 0; r < rows; r++) {
            System.out.printf("%2d  ", (r + 1));
            for (int c = 0; c < cols; c++) {
                System.out.print(cells[r][c] + " ");
            }
            System.out.println();
        }
    }

    /** Visszaadja egy mező tartalmát ('X', 'O' vagy '-'). Ha a pozíció a tábla határain kívül esik, kivételt dob. */
    public char get(int r, int c) {
        if (!isInBounds(r, c)) {
            throw new IllegalArgumentException("A megadott koordinata a tábla hatarain kívul esik.");
        }
        return cells[r][c];
    }

    /** Igazat ad vissza, ha a tábla teljesen tele van (nincs több üres mező). */
    public boolean allSpotsTaken() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] == '-') {
                    return false;
                }
            }
        }
        return true;
    }
}
