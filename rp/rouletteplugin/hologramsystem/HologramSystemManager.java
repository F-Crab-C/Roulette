package rp.rouletteplugin.hologramsystem;

import org.bukkit.plugin.Plugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HologramSystemManager {
    private RouletteWheel rouletteWheel;
    private RouletteBall rouletteBall;
    private RouletteAnimation rouletteAnimation;
    private static HologramSystemManager instance;
    private final Plugin plugin;

    private HologramSystemManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public static HologramSystemManager getInstance(Plugin plugin) {
        if (instance == null) {
            instance = new HologramSystemManager(plugin);
        }
        return instance;
    }

    public void initializeHologramSystem(Location location) {
        this.rouletteWheel = new RouletteWheel(location);
        this.rouletteBall = new RouletteBall(location.clone().add(0, 0.5, 0));
        this.rouletteAnimation = new RouletteAnimation(plugin, rouletteWheel, rouletteBall);
    }

    // 게임 관련 메서드들
    public void startGame(Player player) {
        rouletteWheel.createWheel(player);
        rouletteBall.spawn(player);
    }

    public void startSpinning(Player player) {
        rouletteAnimation.startSpin(player);
    }

    public void stopSpinning(Player player, int finalNumber) {
        rouletteAnimation.stopSpin(player, finalNumber);
    }
}