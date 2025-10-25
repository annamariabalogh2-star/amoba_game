package hu.amoba.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Egyszerű példa a Mockito használatára.
 * Nem a Board osztályt mockoljuk közvetlenül, hanem egy hasonló viselkedésű interfészt.
 */
class MockTest {

    // Egy egyszerű "dummy" interfész, ami utánozza a Board viselkedését
    interface BoardLike {
        int getRows();
    }

    @Test
    void testMockBoardGetRows() {
        // Létrehozunk egy "kamu" (mockolt) BoardLike objektumot
        BoardLike mockBoard = Mockito.mock(BoardLike.class);

        // Beállítjuk, hogy ha meghívják a getRows() metódust, mindig 10-et adjon vissza
        Mockito.when(mockBoard.getRows()).thenReturn(10);

        // Ellenőrizzük, hogy tényleg 10-et ad-e vissza
        assertEquals(10, mockBoard.getRows(),
                "A mock BoardLike 10-et kell, hogy adjon vissza a getRows() hívásra.");
    }
}

