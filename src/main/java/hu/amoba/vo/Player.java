package hu.amoba.vo;

/**
 * Egyszerű, immutábilis érték-objektum (VO).
 * - final mezők
 * - nincs setter
 * - equals/hashCode/toString felüldefiniálás: best practice
 */
public final class Player {
    private final String name; // pl. "Ancsa"
    private final char mark;   // 'X' (ember) vagy 'O' (gép)

    public Player(String name, char mark) {
        this.name = name;
        this.mark = mark;
    }

    public String getName() {
        return name;
    }

    public char getMark() {
        return mark;
    }

    @Override
    public String toString() {
        return "Player{name='" + name + "', mark=" + mark + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player p)) return false;
        return mark == p.mark && name.equals(p.name);
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + mark;
    }
}

