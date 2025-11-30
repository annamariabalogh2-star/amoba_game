package hu.amoba.service;

import org.junit.jupiter.api.Test;

public class PonttablaKezeloTest {

    @Test
    void jatekosNyertEsEredmenyekKiirasa() {
        // Ha a PonttablaKezelo-nek van paraméteres konstruktora (pl. fájlnév),
        // akkor itt add meg neki a teszt-fájlt, pl. "pontok_test.txt"
        PonttablaKezelo ponttabla = new PonttablaKezelo();

        // Adjunk hozzá pár nyerést
        ponttabla.jatekosNyert("Ancsa");
        ponttabla.jatekosNyert("Béla");
        ponttabla.jatekosNyert("Ancsa");

        // Majd írassuk ki az eredményeket
        ponttabla.kiirEredmenyek();

        // Itt most nem assertálunk konkrét sorrendet / értéket,
        // a teszt akkor bukik, ha bármelyik hívás kivételt dob.
    }

    @Test
    void dontetlenIsFrissitiAPonttablat() {
        PonttablaKezelo ponttabla = new PonttablaKezelo();

        ponttabla.dontetlen("Ancsa", "CPU");
        ponttabla.dontetlen("Béla", "CPU");

        ponttabla.kiirEredmenyek();
    }
}

