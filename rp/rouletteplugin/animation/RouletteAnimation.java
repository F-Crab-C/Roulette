package rp.rouletteplugin.animation;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rp.rouletteplugin.Main;
import rp.rouletteplugin.game.RouletteColor;

public class RouletteAnimation {
    private final Main plugin;
    private final ProtocolManager protocolManager;
    private ArmorStand hologram;

    private static final String[] ROULETTE_FRAMES = {
            "§c○ §0● §a■",
            "§a■ §c○ §0●",
            "§0● §a■ §c○"
    };

    public RouletteAnimation(Main plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void playRouletteAnimation(Player player, RouletteColor resultColor, Runnable onComplete) {
        Location center = player.getLocation().add(0, 2, 0);
        createHologram(center);

        new BukkitRunnable() {
            private int tick = 0;
            private int frameIndex = 0;
            private double radius = 1.0;

            @Override
            public void run() {
                if (tick >= 40) { // 2초
                    // 결과 표시
                    showResult(player, resultColor);
                    removeHologram();
                    onComplete.run();
                    this.cancel();
                    return;
                }

                // 회전 속도 조절
                int speed = tick < 20 ? 1 : 2;
                if (tick % speed == 0) {
                    // 홀로그램 텍스트 업데이트
                    frameIndex = (frameIndex + 1) % ROULETTE_FRAMES.length;
                    updateHologramText(ROULETTE_FRAMES[frameIndex]);
                }

                // 원형 파티클 효과
                for (int i = 0; i < 8; i++) {
                    double angle = (i * Math.PI * 2) / 8 + (tick * Math.PI / 10);
                    Location particleLoc = center.clone().add(
                            Math.cos(angle) * radius,
                            0,
                            Math.sin(angle) * radius
                    );
                    player.getWorld().spawnParticle(
                            Particle.SPELL_WITCH,
                            particleLoc,
                            1,
                            0, 0, 0,
                            0
                    );
                }

                // 사운드 효과
                if (tick % 2 == 0) {
                    float pitch = 1.0f + (tick / 40.0f);
                    player.playSound(center, org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1, pitch);
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void showResult(Player player, RouletteColor color) {
        Location loc = player.getLocation().add(0, 2, 0);

        // 결과 색상에 따른 파티클 효과
        Particle.DustOptions dustOptions = null;
        switch (color) {
            case RED:
                dustOptions = new Particle.DustOptions(org.bukkit.Color.RED, 1);
                break;
            case BLACK:
                dustOptions = new Particle.DustOptions(org.bukkit.Color.BLACK, 1);
                break;
            case GREEN:
                dustOptions = new Particle.DustOptions(org.bukkit.Color.GREEN, 1);
                break;
        }

        final Particle.DustOptions finalDustOptions = dustOptions;
        new BukkitRunnable() {
            private int tick = 0;
            private final double radius = 1.5;

            @Override
            public void run() {
                if (tick >= 20) {
                    this.cancel();
                    return;
                }

                // 나선형 파티클 효과
                for (int i = 0; i < 8; i++) {
                    double angle = (i * Math.PI * 2) / 8 + (tick * Math.PI / 5);
                    double height = tick * 0.1;
                    Location particleLoc = loc.clone().add(
                            Math.cos(angle) * radius,
                            height,
                            Math.sin(angle) * radius
                    );
                    player.getWorld().spawnParticle(
                            Particle.REDSTONE,
                            particleLoc,
                            1,
                            0, 0, 0,
                            0,
                            finalDustOptions
                    );
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void createHologram(Location location) {
        hologram = location.getWorld().spawn(location, ArmorStand.class);
        hologram.setVisible(false);
        hologram.setGravity(false);
        hologram.setCustomNameVisible(true);
        hologram.setCustomName("§f룰렛 돌아가는 중...");
    }

    private void updateHologramText(String symbol) {
        hologram.setCustomName("§f" + symbol);
    }

    private void removeHologram() {
        if (hologram != null) {
            hologram.remove();
            hologram = null;
        }
    }

    private void spawnParticles(Location center) {
        center.getWorld().spawnParticle(Particle.SPELL_WITCH, center, 20, 0.5, 0.5, 0.5, 0);
    }

    public void playWinAnimation(Player player, RouletteColor color) {
        Location loc = player.getLocation();

        new BukkitRunnable() {
            private int tick = 0;

            @Override
            public void run() {
                if (tick >= 20) {
                    this.cancel();
                    return;
                }

                // 색상별 파티클 효과
                switch (color) {
                    case RED:
                        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 50, 1, 1, 1, 0);
                        break;
                    case BLACK:
                        loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 50, 1, 1, 1, 0);
                        break;
                    case GREEN:
                        loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 50, 1, 1, 1, 0);
                        break;
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}