package rp.rouletteplugin.animation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import rp.rouletteplugin.Main;
import rp.rouletteplugin.game.RouletteColor;
import rp.rouletteplugin.game.PlayerBet;

public class RouletteAnimation {
    private final Main plugin;
    private static final int GUI_SIZE = 18;
    private static final String GUI_TITLE = "§6§l룰렛 게임";

    public RouletteAnimation(Main plugin) {
        this.plugin = plugin;
    }

    public void playRouletteAnimation(Player player, RouletteColor resultColor, Runnable onComplete) {
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);
        setupColorPane(inv);
        player.openInventory(inv);

        new BukkitRunnable() {
            private int tick = 0;
            private int position = 0;

            @Override
            public void run() {
                if (tick >= 40) {
                    PlayerBet bet = plugin.getGameManager().getPlayerBet(player.getUniqueId());
                    if (bet != null) {
                        double betAmount = bet.getAmount();
                        double winAmount = bet.getColor() == resultColor ?
                                betAmount * (resultColor == RouletteColor.GREEN ? 14 : 2) : 0;
                        showResult(player, resultColor, betAmount, winAmount);
                    }
                    onComplete.run();
                    this.cancel();
                    return;
                }

                // 이전 위치의 공 제거
                if (position > 0) {
                    inv.setItem(position - 1 % 9, null);
                }

                // 새 위치에 공 배치 (첫 번째 줄에만)
                ItemStack ball = createBall();
                inv.setItem(position % 9, ball);

                // 효과음
                if (tick % 2 == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }

                position++;
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void setupColorPane(Inventory inv) {
        // 두 번째 줄에 색상 패널 배치 (9-17번 슬롯)
        for (int i = 9; i < 18; i++) {
            ItemStack pane;
            if (i == 13) { // 중앙에 초록
                pane = createColorPane(Material.LIME_STAINED_GLASS_PANE, "§a초록");
            } else if (i % 2 == 0) { // 짝수 위치에 빨강
                pane = createColorPane(Material.RED_STAINED_GLASS_PANE, "§c빨강");
            } else { // 홀수 위치에 검정
                pane = createColorPane(Material.BLACK_STAINED_GLASS_PANE, "§0검정");
            }
            inv.setItem(i, pane);
        }
    }

    private ItemStack createBall() {
        ItemStack ball = new ItemStack(Material.FIREWORK_STAR);
        ItemMeta meta = ball.getItemMeta();
        meta.setDisplayName("§f●");
        ball.setItemMeta(meta);
        return ball;
    }

    private ItemStack createColorPane(Material material, String name) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(name);
        pane.setItemMeta(meta);
        return pane;
    }

    private void showResult(Player player, RouletteColor resultColor, double betAmount, double winAmount) {
        Location loc = player.getLocation().add(0, 1.5, 0);
        ArmorStand hologram1 = loc.getWorld().spawn(loc, ArmorStand.class);
        ArmorStand hologram2 = loc.getWorld().spawn(loc.add(0, 0.3, 0), ArmorStand.class);

        hologram1.setVisible(false);
        hologram2.setVisible(false);
        hologram1.setGravity(false);
        hologram2.setGravity(false);
        hologram1.setCustomNameVisible(true);
        hologram2.setCustomNameVisible(true);

        if (winAmount > 0) {
            // 승리
            hologram1.setCustomName("§a우승!");
            hologram2.setCustomName("§e+" + String.format("%,d", (long)winAmount) + "원");
        } else {
            // 패배
            hologram1.setCustomName("§c패배");
            hologram2.setCustomName("§c-" + String.format("%,d", (long)betAmount) + "원");
        }

        // 3초 후 홀로그램 제거
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            hologram1.remove();
            hologram2.remove();
        }, 60L);
    }
}