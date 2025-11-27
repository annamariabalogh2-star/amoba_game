package hu.amoba.model;

import hu.amoba.ui.Game;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Egyszerű teszt a középső indexet számoló metódushoz.
 * Itt csak azt nézzük, hogy a számítás helyes páros és páratlan méretekre.
 */
public class GameCenterTest {

    @Test
    void testCenterIndexOdd() {
        Game game = new Game();
        // Páratlan szám: 5 → 2 (0,1,2,3,4) → középső az index 2
        int result = invokeCenterIndex(game, 5);
        assertEquals(2, result, "A 5 méretű tábla középső indexe 2 kell legyen.");
    }

    @Test
    void testCenterIndexEven() {
        Game game = new Game();
        // Páros szám: 10 → 4 (0..9 → közép bal oldali)
        int result = invokeCenterIndex(game, 10);
        assertEquals(4, result, "A 10 méretű tábla középső indexe 4 kell legyen.");
    }

    @Test
    void testCenterIndexSmall() {
        Game game = new Game();
        // 1 → 0 (csak egy elem van)
        int result = invokeCenterIndex(game, 1);
        assertEquals(0, result, "Az 1 méretű tábla középső indexe 0.");
    }

    /**
     * Segéd: a centerIndex privát, ezért reflexióval hívjuk meg.
     * (Ez teljesen megengedett egységtesztben.)
     */
    private int invokeCenterIndex(Game game, int n) {
        try {
            var method = Game.class.getDeclaredMethod("centerIndex", int.class);
            method.setAccessible(true);
            return (int) method.invoke(game, n);
        } catch (Exception e) {
            fail("Nem sikerült meghívni a centerIndex metódust: " + e.getMessage());
            return -1;
        }
    }
}
