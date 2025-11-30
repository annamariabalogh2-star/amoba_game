package hu.amoba.service;

import hu.amoba.db.HighScoreRepository;
import java.util.Map;

public class PonttablaKezelo {

    private final HighScoreRepository repo = new HighScoreRepository();

    /** Jatekos gyozott: kap 3 pontot. */
    public void jatekosNyert(String jatekosNev) {
        repo.addPoints(jatekosNev, 3);
    }

    /** Dontetlen: mindket jatekos kap 1-1 pontot. */
    public void dontetlen(String jatekos1, String jatekos2) {
        repo.addPoints(jatekos1, 1);
        repo.addPoints(jatekos2, 1);
    }

    /** Csak kiirjuk az aktualis ponttabelat. */
    public void kiirEredmenyek() {
        repo.printHighScores();
    }
}




