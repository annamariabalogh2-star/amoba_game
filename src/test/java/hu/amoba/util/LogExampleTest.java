package hu.amoba.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

public class LogExampleTest {

    @Test
    void loggerHivasokNemDobnakKivetelt() {
        assertDoesNotThrow(() -> {
            LogExample.main(new String[]{});
        });
    }
}

