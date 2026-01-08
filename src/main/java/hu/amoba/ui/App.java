package hu.amoba.ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/** Az alkalmazás belépési pontja. Ez az osztály indítja el az egész amőba játékot.
 * A program innen hívja meg a Game osztályt, amely a teljes játékmenetet kezeli:
 *  - a tábla létrehozását,
 *  - a lépések feldolgozását,
 *  - a mentést és betöltést,
 *  - valamint a győzelem vagy döntetlen megállapítását.
 * Itt tehát csak az indítás történik, maga a játék logika a Game.start() metódusban van megvalósítva.
 * A kód elején a konzol kimenet (System.out) UTF-8 karakterkódolásra van állítva,
 * hogy a magyar ékezetes karakterek helyesen jelenjenek meg a terminálban.
 */

public class App {                           // Az alkalmazás belépési pontja (main osztály)

    public static void main(String[] args) { // A program indulási pontja (JVM ezt hívja meg)
        // A standard kimenetet (System.out) UTF-8 kódolásra állítjuk, hogy a magyar ékezetes karakterek helyesen jelenjenek meg
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        new Game().start();                  // Létrehoz egy Game objektumot, majd elindítja a játék teljes folyamatát
    }
}

