package hu.amoba.ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class KonzolosNezetTest {

    @Test
    void testShowIntroDoesNotThrow() {
        KonzolosNezet nezet = new KonzolosNezet();

        assertDoesNotThrow(nezet::showIntro,
                "A showIntro() metódusnak hiba nélkül kell lefutnia.");
    }
}