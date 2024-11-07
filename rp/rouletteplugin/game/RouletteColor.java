package rp.rouletteplugin.game;

import org.bukkit.ChatColor;

public enum RouletteColor {
    RED("빨강", ChatColor.RED),
    BLACK("검정", ChatColor.BLACK),
    GREEN("초록", ChatColor.GREEN);

    private final String displayName;
    private final ChatColor color;

    RouletteColor(String displayName, ChatColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return this.color + displayName;
    }
}