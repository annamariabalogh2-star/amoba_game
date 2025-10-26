package hu.amoba;

/**
 * Belépési pont,ami csak elindítja a játékot. A játékmenet (input, lépések, mentés)
 * nem itt történik, hanem a Game.start() metódusban.
 */
public class App {
    public static void main(String[] args) {
        new hu.amoba.model.Game().start();
    }
}

