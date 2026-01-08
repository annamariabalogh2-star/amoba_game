package hu.amoba.service;

import hu.amoba.db.HighScoreRepository;

/**
 * A ponttábla kezeléséért felelős osztály. Meghatározza a pontozás szabályait (győzelem, döntetlen),
 * és az eredményeket az adatbázison keresztül eltárolja.
 */
public class PonttablaKezelo {                          // Ponttábla kezelés, győzelem: 3 pont, döntetlen: 1 pont.

    private HighScoreRepository repo;                   // A repository, amin keresztül az adatbázist elérjük

    public PonttablaKezelo() {                          // Alap konstruktor (program futása közben használjuk)
        this.repo = new HighScoreRepository();          // Létrehoz egy új adatbázis-kezelőt
    }

    public PonttablaKezelo(HighScoreRepository repo) {  // Konstruktor teszteléshez (külső repository is beadható)
        this.repo = repo;                               // A kapott repository-t használja
    }

    public void jatekosNyert(String jatekosNev) {       // Meghívjuk, ha egy játékos megnyerte a meccset
        repo.addPoints(jatekosNev, 3);           // A győztes játékos 3 pontot kap
    }

    public void dontetlen(String jatekos1, String jatekos2) { // Meghívjuk, ha döntetlen lett a játék
        repo.addPoints(jatekos1, 1);              // Az első játékos 1 pontot kap
        repo.addPoints(jatekos2, 1);              // A második játékos is 1 pontot kap
    }

    public void kiirEredmenyek() {                      // Kiírja a ponttáblát a konzolra
        repo.printHighScores();                         // A repository végzi a tényleges kiírást
    }
}




