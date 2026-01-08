package hu.amoba.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Példa a naplózás (logging) használatára Java-ban.
 */

public class LogExample {

    /** A Logger példány, amelyen keresztül az üzeneteket kiírjuk.
     * A LoggerFactory automatikusan létrehozza a megfelelő logolót az adott osztályhoz tartozó névvel. */
    private static final Logger log = LoggerFactory.getLogger(LogExample.class);

    /** A program belépési pontja. Itt három különböző logolási szintet mutatunk be:
     *  info, warn és error. */
    public static void main(String[] args) {
        log.info("A Logback működik: ez egy információs üzenet.");
        log.warn("Ez egy figyelmeztetés.");
        log.error("Ez egy hibaüzenet például egy kivételhez.");
    }
}
