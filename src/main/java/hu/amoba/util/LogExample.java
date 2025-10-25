package hu.amoba.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Egyszerű példa a logolásra Logback segítségével.
 * A Logback egy fejlettebb "System.out.println", amely különböző szinteken logol:
 *  - info: általános információk
 *  - warn: figyelmeztetések
 *  - error: hibák
 */
public class LogExample {

    // Logger példány létrehozása az aktuális osztályhoz
    private static final Logger log = LoggerFactory.getLogger(LogExample.class);

    public static void main(String[] args) {
        log.info("A Logback működik: ez egy információs üzenet.");
        log.warn("Ez egy figyelmeztetés.");
        log.error("Ez egy hibaüzenet például egy kivételhez.");
    }
}
