package hu.amoba.model;

import java.util.ArrayList;
import java.util.List;

/** A Board (tábla) osztály az amőba játék tábláját kezeli. A tábla egy kétdimenziós karaktertömb, ahol:
 *  'X'  – emberi játékos
 *  'O'  – gép
 *  '-'  – üres mező
*/

public class Board {                                // A játék tábla (adat + alap műveletek)

    private final int rows;                         // Hány soros a tábla

    private final int cols;                         // Hány oszlopos a tábla

    private final char[][] cells;                   // A tábla mezői: 'X', 'O' vagy '-'

    public Board(int rows, int cols) {              // Konstruktor: beállítja a méretet és üres táblát készít
        this.rows = rows;                           // Elmenti a sorok számát
        this.cols = cols;                           // Elmenti az oszlopok számát
        this.cells = new char[rows][cols];          // Létrehoz egy rows x cols méretű 2D tömböt

        for (int r = 0; r < rows; r++) {            // Végigmegy minden soron
            for (int c = 0; c < cols; c++) {        // Végigmegy minden oszlopon
                cells[r][c] = '-';                  // Minden mezőt '-' jellel feltölt (üres)
            }
        }
    }

    public Board() {                                // Paraméter nélküli konstruktor, alapból 10x10-es tábla
        this(10, 10);                    // Meghívja a másik konstruktort 10x10 mérettel
    }

    public int getRows() {                          // Getter: sorok száma
        return rows;                                // Visszaadja a sorok számát
    }

    public int getCols() {                          // Getter: oszlopok száma
        return cols;                                // Visszaadja az oszlopok számát
    }

    public char[][] getCells() {                    // Getter: cellák tömbje
        return cells;                               // Visszaadja a belső 2D tömböt (cellák) (pl. mentéshez)
    }

    public boolean place(int r, int c, char mark) { // Megpróbál lerakni egy jelet a (r,c) mezőre
        // Tábla határain belül vagyunk?
        if (!isInBounds(r, c)) {                    // Ha kilóg a koordináta a táblából,
            return false;                           // akkor nem enged lépni.
        }

        // Üres a mező?
        if (cells[r][c] != '-') {                   // Ha már nem üres a mező
            return false;                           // akkor nem enged felülírni.
        }

        // Ha még nincs jel a táblán, az első lépés bárhová mehet
        if (!hasAnyMark()) {                        // Ha még nincs egyetlen jel sem a táblán,
            cells[r][c] = mark;                     // lerakja a jelet
            return true;                            // és jelzi, hogy sikerült
        }

        // Későbbi lépések: csak szomszédos mezőre
        if (!isAdjacentMark(r, c)) {                // Ha nem szomszédos egy meglévő jelhez
            return false;                           // nem enged lépni
        }

        cells[r][c] = mark;                         // Lerakja a jelet (szabályos lépés)
        return true;                                // Jelzi, hogy sikerült
    }

    public boolean hasAnyMark() {                   // Megnézi, van-e már jel a táblán
        for (int r = 0; r < rows; r++) {            // Végigmegy soronként
            for (int c = 0; c < cols; c++) {        // Végigmegy oszloponként
                if (cells[r][c] != '-') {           // Ha talál nem üres mezőt,
                    return true;                    // akkor van már jel.
                }
            }
        }
        return false;                               // Ha mind '-' volt, akkor nincs jel
    }

    public boolean isAdjacentMark(int r, int c) {   // Megnézi, van-e a mező körül (8 irányban) bármilyen jel
        for (int dr = -1; dr <= 1; dr++) {          // dr: -1, 0, +1 (sor irány)
            for (int dc = -1; dc <= 1; dc++) {      // dc: -1, 0, +1 (oszlop irány)
                if (dr == 0 && dc == 0) {           // (0,0) az maga a vizsgált mező lenne
                    continue;                       // Azt kihagyjuk
                }
                int rr = r + dr;                    // Szomszédos sor indexe
                int cc = c + dc;                    // Szomszédos oszlop indexe
                if (isInBounds(rr, cc) && cells[rr][cc] != '-') {   // Ha a szomszéd mező a táblán belül van, és nem üres,
                    return true;                    // akkor van szomszédos jel.
                }
            }
        }
        return false;                               // Ha egyik szomszéd sem tartalmaz jelet
    }

    public boolean isInBounds(int r, int c) {       // Ellenőrzi, hogy a (r,c) érvényes koordináta-e
        return r >= 0 && r < rows && c >= 0 && c < cols; // Igaz, ha 0..rows-1 és 0..cols-1 tartományban van
    }

    public boolean hasFiveInARow(char mark) {       // Megnézi, van-e 5 egyforma jel egymás mellett
        int[][] iranyok = {                         // Vizsgált irányok (dr, dc)
                {0, 1},                             // vízszintes
                {1, 0},                             // függőleges
                {1, 1},                             // átló
                {1, -1}                             // átló
        };

        for (int r = 0; r < rows; r++) {            // Minden mezőt végignéz kezdőpontként
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] != mark) {          // Ha itt nem a keresett jel van,
                    continue;                       // nem innen keresünk 5-öt.
                }

                for (int[] dir : iranyok) {         // Minden irányban külön ellenőrzés
                    int dr = dir[0];                // Irány sor lépése
                    int dc = dir[1];                // Irány oszlop lépése
                    boolean ok = true;              // Feltételezzük, hogy megvan az 5-ös sorozat

                    for (int k = 1; k < 5; k++) {   // Még 4 mezőt nézünk az aktuális után (összesen 5)
                        int rr = r + dr * k;        // K-adik lépés sor irányban
                        int cc = c + dc * k;        // K-adik lépés oszlop irányban
                        if (!isInBounds(rr, cc) || cells[rr][cc] != mark) { // Ha kilóg a táblából, vagy nem ugyanaz a jel,
                            ok = false;             // akkor nem jó ez az irány.
                            break;                  // Kilépünk ebből az irányból
                        }
                    }

                    if (ok) {                       // Ha mind az 5 mező jó volt,
                        return true;                // akkor van 5 egymás mellett.
                    }
                }
            }
        }
        return false;                               // Sehol sem találtunk 5-öt
    }

    public List<int[]> legalMoves() {               // Összegyűjti az összes szabályos lépést
        List<int[]> out = new ArrayList<>();        // Ide kerülnek a lépések (r,c) párok
        boolean vanJel = hasAnyMark();              // Van-e már jel a táblán?

        for (int r = 0; r < rows; r++) {            // Végigmegy minden mezőn
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] == '-') {           // Csak üres mezőre lehet lépni
                    if (!vanJel || isAdjacentMark(r, c)) { // Ha nincs még jel: bárhova lehet, ha van, csak a szomszédos üres mezőre lehet.
                        out.add(new int[]{r, c});   // Elmenti a lépést (sor, oszlop)
                    }
                }
            }
        }
        return out;                                 // Visszaadja a lehetséges lépések listáját
    }

    public void print() {                           // Kirajzolja a táblát a konzolra
        System.out.print("    ");                   // Bal oldali behúzás (a sor-számok miatt)
        for (int c = 0; c < cols; c++) {            // Oszlopfejlécek: a, b, c
            char letter = (char) ('a' + c);         // 0 -> 'a', 1 -> 'b' stb...
            System.out.print(letter + " ");         // Kiírja a betűt
        }
        System.out.println();                       // Új sor

        for (int r = 0; r < rows; r++) {            // Sorok kiírása
            System.out.printf("%2d  ", (r + 1));    // Sor száma (1-től), igazítva
            for (int c = 0; c < cols; c++) {        // Mezők kiírása
                System.out.print(cells[r][c] + " "); // Kiírja a cella jelét
            }
            System.out.println();                   // // Sor vége
        }
    }

    public char get(int r, int c) {                 // Visszaadja egy mező jelét
        if (!isInBounds(r, c)) {                    // Ha a koordináta érvénytelen.
            throw new IllegalArgumentException("A megadott koordináta a tábla határain kivül esik."); // Kivételt dob
        }
        return cells[r][c];                         // Visszaadja a cella tartalmát
    }
    /**
     * Előkészített metódus: Segédfüggvény döntetlen ellenőrzésre, jelenleg nincs használatban.
    */

    public boolean allSpotsTaken() {                // Igaz, ha nincs több üres mező
        for (int r = 0; r < rows; r++) {            // Végigmegy minden mezőn
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] == '-') {           // Ha talál üres mezőt,
                    return false;                   // akkor nincs tele a tábla.
                }
            }
        }
        return true;                                // Ha nem talált '-', akkor tele van
    }
}
