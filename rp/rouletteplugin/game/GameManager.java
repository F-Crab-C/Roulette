package rp.rouletteplugin.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import rp.rouletteplugin.hologramsystem.HologramSystemManager;

public class GameManager {
    private static GameManager instance;
    private final Plugin plugin;
    private final HologramSystemManager hologramSystem;
    private final BettingManager bettingManager;
    private boolean isGameRunning;

    private GameManager(Plugin plugin) {
        this.plugin = plugin;
        this.hologramSystem = HologramSystemManager.getInstance(plugin);
        this.bettingManager = BettingManager.getInstance();
        this.isGameRunning = false;
    }

    public static GameManager getInstance(Plugin plugin) {
        if (instance == null) {
            instance = new GameManager(plugin);
        }
        return instance;
    }

    public void startGame(Player player) {
        if (isGameRunning) {
            player.sendMessage("§c게임이 이미 진행 중입니다.");
            return;
        }

        isGameRunning = true;
        Location gameLocation = player.getLocation().add(0, 2, 0);
        hologramSystem.initializeHologramSystem(gameLocation);
        hologramSystem.startGame(player);
        bettingManager.openBettingGUI(player);
    }

    public void startRouletteAnimation(Player player) {
        if (!isGameRunning) return;

        player.closeInventory();
        hologramSystem.startSpinning(player);

        // 10초 후 결과 처리
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            processResult(player);
        }, 200L);
    }

    private void processResult(Player player) {
        if (!isGameRunning) return;

        int result = calculateResult();
        hologramSystem.stopSpinning(player, result);
        bettingManager.processBettingResult(player, result);
        isGameRunning = false;
    }

    private int calculateResult() {
        return (int) (Math.random() * 37); // 0-36 사이의 랜덤 값
    }

    public void stopGame(Player player) {
        if (!isGameRunning) return;

        hologramSystem.getHologramManager().removeHologram(player);
        isGameRunning = false;
        bettingManager.clearBetting(player);
        player.sendMessage("§c게임이 중단되었습니다.");
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public void handlePlayerQuit(Player player) {
        if (isGameRunning) {
            stopGame(player);
        }
    }
}