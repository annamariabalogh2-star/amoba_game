package hu.amoba;

/**
 * Belépési pont. Csak elindítja a játékot.
 * A játékmenet (input, lépések, mentés) NEM itt történik,
 * hanem a Game.start() metódusban.
 */
public class App {
    public static void main(String[] args) {
        new hu.amoba.core.Game().start();
    }
}

