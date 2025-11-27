package hu.amoba.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Ez az osztály egy nagyon egyszerű példa a naplózás (logolás) használatára a Logback könyvtár segítségével.
 * A naplózás célja, hogy a program futása közben fontos eseményeket rögzítsünk:
 *  - mikor indult el egy művelet,
 *  - milyen hibák, figyelmeztetések történtek,
 *  - vagy csak általános információkat (például: "A játék elindult").
 *
 * A Logback az SLF4J (Simple Logging Facade for Java) rendszert használja, ami egységes logolási felületet biztosít
 * különböző naplózó keretrendszerekhez.
 *
 * A naplóüzenetek különböző szinteken adhatók meg:
 *  - **info**  → általános információ (normál működés közbeni események)
 *  - **warn**  → figyelmeztetés (nem kritikus, de gyanús helyzet)
 *  - **error** → hibaüzenet (komoly probléma vagy kivétel)
 *
 * Ez az osztály nem része közvetlenül a játék logikájának, hanem bemutató célból készült, hogy lássuk,
 * hogyan működik a naplózás. */

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
