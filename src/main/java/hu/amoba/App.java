package hu.amoba;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Belépési pont,ami csak elindítja a játékot. A játékmenet (input, lépések, mentés)
 * nem itt történik, hanem a Game.start() metódusban.
 */
public class App {
    public static void main(String[] args) {
        // Konzol kimenet átállítása UTF-8-ra
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        new hu.amoba.model.Game().start();
    }
}

