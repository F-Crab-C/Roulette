// GameManager.java
package rp.rouletteplugin.game;

import rp.rouletteplugin.Main;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {
    private final Main plugin;
    private final Map<UUID, PlayerBet> playerBets;
    private boolean isGameRunning;

    public GameManager(Main plugin) {
        this.plugin = plugin;
        this.playerBets = new HashMap<>();
        this.isGameRunning = false;
    }

    public boolean placeBet(Player player, double amount, RouletteColor color) {
        if (isGameRunning) return false;

        UUID playerUUID = player.getUniqueId();
        playerBets.put(playerUUID, new PlayerBet(playerUUID, amount, color));
        return true;
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public void setGameRunning(boolean gameRunning) {
        isGameRunning = gameRunning;
    }

    public PlayerBet getPlayerBet(UUID playerUUID) {
        return playerBets.get(playerUUID);
    }

    public void clearBets() {
        playerBets.clear();
    }
}