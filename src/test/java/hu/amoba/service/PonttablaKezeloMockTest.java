package hu.amoba.service;

import hu.amoba.db.HighScoreRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

class PonttablaKezeloMockTest {

    @Test
    void jatekosNyertEkkorAddPointsHivodik() {
        HighScoreRepository repoMock = Mockito.mock(HighScoreRepository.class);

        PonttablaKezelo kezelo = new PonttablaKezelo(repoMock);

        kezelo.jatekosNyert("Ancsa");

        // Győzelem = 3 pont
        verify(repoMock, times(1)).addPoints("Ancsa", 3);
    }

    @Test
    void dontetlenEkkorMindketJatekosKapPontot() {
        HighScoreRepository repoMock = Mockito.mock(HighScoreRepository.class);

        PonttablaKezelo kezelo = new PonttablaKezelo(repoMock);

        kezelo.dontetlen("Alice", "Bob");

        verify(repoMock, times(1)).addPoints("Alice", 1);
        verify(repoMock, times(1)).addPoints("Bob", 1);
    }
}
