package rp.rouletteplugin.animation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import rp.rouletteplugin.Main;
import rp.rouletteplugin.game.RouletteColor;
import rp.rouletteplugin.game.PlayerBet;

import java.util.Random;

public class RouletteAnimation {
    private final Main plugin;
    private static final int GUI_SIZE = 18;
    private static final String GUI_TITLE = "§6§l룰렛 게임";
    private static final int MIN_POSITION = 0;
    private static final int MAX_POSITION = 8;
    private static final int MIN_TICKS = 30; // 최소 진행 시간
    private static final int MAX_TICKS = 50; // 최대 진행 시간

    public RouletteAnimation(Main plugin) {
        this.plugin = plugin;
    }

    public void playRouletteAnimation(Player player, RouletteColor resultColor, Runnable onComplete) {
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);
        setupInitialInventory(inv);
        player.openInventory(inv);

        // 랜덤한 종료 시간 설정
        final int endTick = MIN_TICKS + new Random().nextInt(MAX_TICKS - MIN_TICKS);

        new BukkitRunnable() {
            private int tick = 0;
            private int position = 0;
            private boolean isForward = true;
            private boolean isEnding = false;
            private int finalPosition = -1;

            @Override
            public void run() {
                // 종료 조건 체크
                if (tick >= endTick && finalPosition != -1 && position == finalPosition) {
                    finishAnimation(player, resultColor, onComplete);
                    this.cancel();
                    return;
                }

                // 이전 공 제거
                if (position >= 0 && position < 9) {
                    inv.setItem(position, createEmptyPane());
                }

                // 방향 전환 및 위치 업데이트
                if (position >= 8) {
                    isForward = false;
                } else if (position <= 0) {
                    isForward = true;
                }

                // 종료 시점에 도달하면 결과 위치 결정
                if (tick >= endTick && finalPosition == -1) {
                    finalPosition = calculateFinalPosition(resultColor);
                }

                // 위치 업데이트
                position += isForward ? 1 : -1;

                // 새 위치에 공 배치
                inv.setItem(position, createBall());

                // 효과음
                playSound(player, position);

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void setupInitialInventory(Inventory inv) {
        // 첫 줄 비우기
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, createEmptyPane());
        }
        // 두 번째 줄 색상 설정
        setupColorPane(inv);
    }

    private int calculateFinalPosition(RouletteColor color) {
        Random random = new Random();
        switch (color) {
            case RED:
                return random.nextInt(2) * 2 + 1; // 1, 3, 5, 7 중 랜덤
            case BLACK:
                return random.nextInt(2) * 2 + 2; // 2, 4, 6, 8 중 랜덤
            case GREEN:
                return 4; // 중앙 위치
            default:
                return 0;
        }
    }
    private void setupColorPane(Inventory inv) {
        // 두 번째 줄 색상 배치 (9-17번 슬롯)
        for (int i = 9; i < 18; i++) {
            ItemStack pane;
            if (i == 13) { // 중앙 초록
                pane = createColorPane(Material.LIME_STAINED_GLASS_PANE, "§a초록");
            } else if ((i - 9) % 2 == 0) { // 빨강
                pane = createColorPane(Material.RED_STAINED_GLASS_PANE, "§c빨강");
            } else { // 검정
                pane = createColorPane(Material.BLACK_STAINED_GLASS_PANE, "§0검정");
            }
            inv.setItem(i, pane);
        }
    }

    private void playSound(Player player, int position) {
        if (position == 0 || position == 8) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
    }

    private ItemStack createBall() {
        ItemStack ball = new ItemStack(Material.FIREWORK_STAR, 1);
        ItemMeta meta = ball.getItemMeta();
        meta.setDisplayName("§f⚪");  // 더 잘 보이는 유니코드 문자 사용

        // 아이템 발광 효과 추가
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

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

    private ItemStack createEmptyPane() {
        ItemStack pane = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        pane.setItemMeta(meta);
        return pane;
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