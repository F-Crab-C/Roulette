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
    private static final int MAX_POSITION = 8;
    private static final int MIN_POSITION = 0;

    public RouletteAnimation(Main plugin) {
        this.plugin = plugin;
    }

    public void playRouletteAnimation(Player player, RouletteColor resultColor, Runnable onComplete) {
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);
        setupColorPane(inv);
        player.openInventory(inv);

        // 결과 색상에 따른 최종 위치 결정
        final int finalPosition = getFinalPosition(resultColor);

        new BukkitRunnable() {
            private int tick = 0;
            private int position = 0;
            private int direction = 1;
            private boolean isEnding = false;

            @Override
            public void run() {
                if (tick >= 40 || (isEnding && position == finalPosition)) {
                    finishAnimation(player, resultColor, onComplete);
                    this.cancel();
                    return;
                }

                // ... 애니메이션 로직 ...

                // 마지막 몇 틱에서는 결과 위치로 이동
                if (tick >= 35) {
                    isEnding = true;
                    direction = (position < finalPosition) ? 1 : -1;
                }

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private int getFinalPosition(RouletteColor color) {
        switch (color) {
            case RED: return 1;    // 빨간색 구역
            case BLACK: return 3;  // 검은색 구역
            case GREEN: return 4;  // 초록색 구역 (중앙)
            default: return 0;
        }
    }

    private void finishAnimation(Player player, RouletteColor resultColor, Runnable onComplete) {
        PlayerBet bet = plugin.getGameManager().getPlayerBet(player.getUniqueId());
        if (bet != null) {
            double betAmount = bet.getAmount();
            double winAmount = bet.getColor() == resultColor ?
                    betAmount * (resultColor == RouletteColor.GREEN ? 14 : 2) : 0;
            showResult(player, resultColor, betAmount, winAmount);
        }
        onComplete.run();
    }

    private int checkDirection(int position, int direction) {
        if (position >= MAX_POSITION) { // 오른쪽 벽
            return -1;
        } else if (position <= 0) { // 왼쪽 벽
            return 1;
        }
        return direction;
    }

    private void clearPreviousPosition(Inventory inv, int position) {
        if (position >= 0 && position < 9) {
            inv.setItem(position, null);
        }
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
        ItemStack ball = new ItemStack(Material.FIREWORK_STAR, 1);
        ItemMeta meta = ball.getItemMeta();
        meta.setDisplayName("§f⚪");  // 더 잘 보이는 유니코드 문자 사용

        // 아이템 발광 효과 추가
        meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);

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