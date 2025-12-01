package hu.amoba.service;

import hu.amoba.db.HighScoreRepository;

public class PonttablaKezelo {                           // Ponttábla kezelés, győzelem: 3 pont, döntetlen: 1 pont.

    private HighScoreRepository repo;

    public PonttablaKezelo() {                           // Konstruktor, amit a program használ
        this.repo = new HighScoreRepository();
    }

    public PonttablaKezelo(HighScoreRepository repo) {   // Teszteléshez szükséges konstruktor
        this.repo = repo;
    }

    public void jatekosNyert(String jatekosNev) {
        repo.addPoints(jatekosNev, 3);
    } // Ha a játékos győz 3 pontot kap.

    public void dontetlen(String jatekos1, String jatekos2) {   // Döntetlen, mindkét játéko 1-1 pontot kap.
        repo.addPoints(jatekos1, 1);
        repo.addPoints(jatekos2, 1);
    }

    public void kiirEredmenyek() {
        repo.printHighScores();
    }    // Kiírjuk az aktuális pontállást.
}




