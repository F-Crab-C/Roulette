package rp.rouletteplugin.game;

public enum RouletteColor {
    RED("§c빨강"),
    BLACK("§0검정"),
    GREEN("§a초록");

    private final String displayName;

    RouletteColor(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}