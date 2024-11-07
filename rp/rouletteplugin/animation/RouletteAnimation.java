package rp.rouletteplugin.animation;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import rp.rouletteplugin.Main;
import rp.rouletteplugin.game.RouletteColor;

import static rp.rouletteplugin.gui.RouletteGUI.GUI_SIZE;

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
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, "§6§l룰렛 게임");
        setupColorPane(inv);
        player.openInventory(inv);

        new BukkitRunnable() {
            private int tick = 0;
            private int position = 0;

            @Override
            public void run() {
                if (tick >= 40) {
                    showResult(player, resultColor);
                    onComplete.run();
                    this.cancel();
                    return;
                }

                // 이전 위치의 공 제거 (첫 번째 줄에서만)
                if (position > 0) {
                    inv.setItem(position - 1, null);
                }

                // 새 위치에 공 배치 (첫 번째 줄에서만)
                ItemStack ball = new ItemStack(Material.FIREWORK_STAR);
                ItemMeta meta = ball.getItemMeta();
                meta.setDisplayName("§f●");
                ball.setItemMeta(meta);
                inv.setItem(position % 9, ball);

                position++;
                tick++;

                // 효과음 (속도에 따라 음높이 변경)
                float pitch = 1.0f + (tick / 40.0f);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, pitch);
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void setupColorPane(Inventory inv) {
        // 두 번째 줄에 색상 유리판 배치 (9-17번 슬롯)
        for (int i = 9; i < 18; i++) {
            ItemStack pane;
            if (i == 13) { // 중앙에 초록
                pane = createGuiItem(Material.LIME_STAINED_GLASS_PANE, "§a초록");
            } else if (i % 2 == 0) { // 짝수 위치에 빨강
                pane = createGuiItem(Material.RED_STAINED_GLASS_PANE, "§c빨강");
            } else { // 홀수 위치에 검정
                pane = createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§0검정");
            }
            inv.setItem(i, pane);
        }
    }

    private ItemStack createGuiItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private int getAnimationPosition(int pos) {
        int[] path = {1, 2, 3, 4, 5, 6, 7, 16, 25, 34, 43, 42, 41, 40, 39, 38, 37, 28, 19, 10};
        return path[pos % path.length];
    }

    public void showResult(Player player, RouletteColor resultColor, double betAmount, double winAmount) {
        Location loc = player.getLocation().add(0, 1.5, 0);  // 높이 낮춤
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
            hologram2.setCustomName("§e+" + String.format("%,d", (long) winAmount) + "원");
        } else {
            // 패배
            hologram1.setCustomName("§c패배");
            hologram2.setCustomName("§c-" + String.format("%,d", (long) betAmount) + "원");
        }

        // 3초 후 홀로그램 제거
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            hologram1.remove();
            hologram2.remove();
        }, 60L);
    }
}