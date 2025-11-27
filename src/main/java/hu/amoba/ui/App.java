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
 * hogy a magyar ékezetes karakterek helyesen jelenjenek meg a terminálban. */

public class App {

    /** A program fő metódusa — innen indul el a játék.
     * @param args parancssori argumentumok (nem használjuk) */

    public static void main(String[] args) {

        // Konzol kimenet átállítása UTF-8-ra – IntelliJ saját konzolja ezt tudja kezelni
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        // A játék indítása: új Game példány létrehozása és a start() metódus meghívása
        new Game().start();
    }
}

