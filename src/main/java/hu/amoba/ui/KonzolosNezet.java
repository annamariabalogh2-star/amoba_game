package hu.amoba.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KonzolosNezet {

    private static final Logger log = LoggerFactory.getLogger(KonzolosNezet.class);

    private static final String BLUE = "\u001B[34m";
    private static final String RESET = "\u001B[0m";

    /** A kezdő képernyő megjelenítése. */
    public void showIntro() {
        System.out.println(BLUE +
                "==============================\n" +
                "                              \n" +
                "        AMOBA JATEK           \n" +
                "                              \n" +
                "==============================\n" + RESET);
    }

    /** Kilépéskor megjelenő üzenet. */
    public void showGoodbye() {
        System.out.println(BLUE +
                "==============================\n" +
                "                              \n" +
                "          VISZLAT!            \n" +
                "                              \n" +
                "==============================\n" + RESET);
        log.info("A jatek vege, koszi a meccset!");
    }

    /** A parancslista és szabályok kiírása. */
    public void showHelp() {
        System.out.println("""
            Parancsok:
              help                 - sugo
              lepes <sor> <oszlop> - pl. 'lepes 3 c'
              ment                 - tabla mentese
              betolt               - tabla betoltese
              xmlment              - mentes XML-be
              xmlbetolt            - betoltes XML-bol
              score                - high score lista
              kilep                - kilepes

            Szabalyok:
              * X (ember) kezd AUTOMATIKUSAN kozepen vagy kezdolepes nelkul indul a jatek.
              * Csak szomszedos ures mezore lehet lepni!
              * Ha 5 azonos jel van egymas mellett, gyozelem!!!
            """);
    }
}

