package hu.amoba.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KonzolosNezet {                          // Konzolos „nézet”: csak kiírásokat kezel

    private static final Logger log = LoggerFactory.getLogger(KonzolosNezet.class); // Logger: kilépéskor naplóz egy információs üzenetet

    private static final String BLUE = "\u001B[34m";  // Kék szín ANSI kód (konzol színezéshez)
    private static final String RESET = "\u001B[0m";  // Szín visszaállítása alapértelmezettre

    public void showIntro() {                         // Kezdő képernyő megjelenítése
        System.out.println(BLUE +
                "==============================\n" +
                "                              \n" +
                "        AMŐBA JÁTÉK           \n" +
                "                              \n" +
                "==============================\n" + RESET); // ASCII-szerű keretes cím kiírása kék színnel
    }

    public void showGoodbye() {                       // Kilépéskor megjelenő búcsú üzenet
        System.out.println();
        System.out.println(BLUE +
                "==============================\n" +
                "                              \n" +
                "          VISZLÁT!            \n" +
                "                              \n" +
                "==============================\n" + RESET);
        log.info("A játék vége, köszi a meccset!");   // Naplózza, hogy a játék véget ért
    }

    public void showHelp() {                          // Súgó és parancslista megjelenítése
        System.out.println("""
            Parancsok:
              help                 - súgó
              lepes <sor> <oszlop> - pl. 'lepes 3 c'
              ment                 - tábla mentése
              betolt               - tábla betöltése
              xmlment              - mentés XML-be
              xmlbetolt            - betöltés XML-ből
              score                - high score lista
              kilep                - kilépés

            Szabályok:
              * X (ember) kezd automatikusan közepen vagy kezdőlépés nélkül indul a játék.
              * Csak szomszédos üres mezőre lehet lépni!
              * Ha 5 azonos jel van egymás mellett, győzelem!!!
            """);                                       // A felhasználó számára elérhető parancsok és játékszabályok kiírása
    }
}

