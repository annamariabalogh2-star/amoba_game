package hu.amoba.vo;

/**
 * A játékos adatait (név, jel) tároló egyszerű value object.
 */
public final class Player {         // Egy játékost leíró egyszerű adatobjektum

    private final String name;      // A játékos neve (pl. "Ancsa" vagy "Gép")
    private final char mark;        // A játékos jele a táblán ('X' vagy 'O')

    public Player(String name, char mark) { // Konstruktor: beállítja a nevet és a jelet
        this.name = name;           // Név eltárolása
        this.mark = mark;           // Jel eltárolása
    }

    public String getName() {       // Visszaadja a játékos nevét
        return name;
    }

    public char getMark() {         // Visszaadja a játékos jelét
        return mark;
    }

    @Override
    public String toString() {      // Szöveges forma (pl. logoláshoz, debughoz)
        return "Player{name='" + name + "', mark=" + mark + "}";
    }

    @Override
    public boolean equals(Object o) { // Két Player akkor egyenlő, ha nevük és jelük is egyezik
        if (this == o) {              // Ha ugyanarra az objektumra mutatnak
            return true;
        }
        if (!(o instanceof Player p)) { // Ha a másik objektum nem Player
            return false;
        }
        return mark == p.mark           // Jel egyezés
                && name.equals(p.name); // Név egyezés
    }

    @Override
    public int hashCode() {                 // Hash-kód számítása (név + jel alapján)
        return 31 * name.hashCode() + mark; // 31-es szorzó: Java-ban bevett gyakorlat
    }
}

