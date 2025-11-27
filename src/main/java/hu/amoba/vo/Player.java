package hu.amoba.vo;

/** A Player (játékos) osztály az amőba játék egyik szereplőjét írja le.
 * Ez egy **egyszerű, immutábilis (nem módosítható) érték-objektum (Value Object)**, ami azt jelenti, hogy a
 * létrehozás után az adatai már nem változnak meg.
 *
 * A játékos két fő tulajdonságot tárol:
 *  - a neve (pl. "Anna" vagy "Gép"),
 *  - és a jele ('X' vagy 'O').
 *
 * Ez az osztály fontos a program tisztasága szempontjából: segít, hogy a játékos adatai egyetlen helyen,
 * biztonságosan legyenek tárolva. A `final` kulcsszóval megakadályozzuk, hogy az osztályból örököljenek,
 * a `final` mezők pedig azt garantálják, hogy a létrehozott példány adatai
 * később nem módosíthatók (ez a **"immutability"** elv). */
public final class Player {
    /** A játékos neve (pl. "Ancsa" vagy "Gép"). */
    private final String name;

    /** A játékos jele a táblán: 'X' (ember) vagy 'O' (gép). */
    private final char mark;

    /** Konstruktor, amely létrehoz egy új játékost névvel és jellel.
     * - @param name a játékos neve
     * - @param mark a játékos jele ('X' vagy 'O') */
    public Player(String name, char mark) {
        this.name = name;
        this.mark = mark;
    }

    /** Visszaadja a játékos nevét. */
    public String getName() {
        return name;
    }

    /** Visszaadja a játékos jelét ('X' vagy 'O'). */
    public char getMark() {
        return mark;
    }

    /** Szöveges reprezentáció a játékosról, pl.: Player{name='Ancsa', mark=X}
     * Hasznos naplózáskor és hibakereséskor. */
    @Override
    public String toString() {
        return "Player{name='" + name + "', mark=" + mark + "}";
    }

    /** Két Player példány akkor egyenlő, ha: a nevük és a jelük is megegyezik.
     * Ez a metódus fontos, ha például a játékosokat listában vagy halmazban hasonlítunk össze. */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Player p))
            return false;
        return mark == p.mark && name.equals(p.name);
    }

    /** Hash-kód számítása a név és a jel alapján. A 31-es szorzó a Java-ban bevett gyakorlat a hashCode
     * képleteknél, mert hatékony és egyenletes eloszlást biztosít. */
    @Override
    public int hashCode() {
        return 31 * name.hashCode() + mark;
    }
}

