package rp.rouletteplugin.game;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Sound;
import rp.rouletteplugin.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Random;

public class GameManager {
    private final Main plugin;
    private final Map<UUID, PlayerBet> playerBets;
    private final Random random;
    private boolean isGameRunning;

    // 배당률 상수
    private static final double NORMAL_MULTIPLIER = 2.0;  // 빨강, 검정 배당률
    private static final double GREEN_MULTIPLIER = 14.0;  // 초록 배당률

    public GameManager(Main plugin) {
        this.plugin = plugin;
        this.playerBets = new HashMap<>();
        this.random = new Random();
        this.isGameRunning = false;
    }

    public void setPlayerBetAmount(UUID playerUUID, double amount) {
        PlayerBet bet = playerBets.computeIfAbsent(playerUUID, k -> new PlayerBet(playerUUID, 0, null));
        bet.setAmount(amount);
    }

    public void setPlayerBetColor(UUID playerUUID, RouletteColor color) {
        PlayerBet bet = playerBets.computeIfAbsent(playerUUID, k -> new PlayerBet(playerUUID, 0, null));
        bet.setColor(color);
    }

    public boolean isPlayerBetComplete(UUID playerUUID) {
        PlayerBet bet = playerBets.get(playerUUID);
        return bet != null && bet.getAmount() > 0 && bet.getColor() != null;
    }

    public void startGame(Player player) {
        if (isGameRunning) {
            player.sendMessage("§c게임이 이미 진행 중입니다!");
            return;
        }

        UUID playerUUID = player.getUniqueId();
        PlayerBet bet = playerBets.get(playerUUID);

        // 베팅 금액 차감
        plugin.getEconomy().withdrawPlayer(player, bet.getAmount());

        isGameRunning = true;

        // 룰렛 결과 결정
        new BukkitRunnable() {
            private int ticks = 0;
            private final int ANIMATION_DURATION = 40; // 2초

            @Override
            public void run() {
                if (ticks >= ANIMATION_DURATION) {
                    determineResult(player);
                    isGameRunning = false;
                    this.cancel();
                    return;
                }

                // 진행 중 효과음
                if (ticks % 2 == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void determineResult(Player player) {
        UUID playerUUID = player.getUniqueId();
        PlayerBet bet = playerBets.get(playerUUID);

        // 결과 결정 (20% 확률로 초록, 40% 확률로 빨강과 검정)
        int randomNum = random.nextInt(100);
        RouletteColor result;
        if (randomNum < 20) {
            result = RouletteColor.GREEN;
        } else if (randomNum < 60) {
            result = RouletteColor.RED;
        } else {
            result = RouletteColor.BLACK;
        }

        // 결과 처리 및 상금 지급
        if (bet.getColor() == result) {
            double multiplier = (result == RouletteColor.GREEN) ? GREEN_MULTIPLIER : NORMAL_MULTIPLIER;
            double winAmount = bet.getAmount() * multiplier;

            plugin.getEconomy().depositPlayer(player, winAmount);
            player.sendMessage("§a축하합니다! " + result.getDisplayName() + "§a이(가) 나왔습니다!");
            player.sendMessage("§a상금 " + String.format("%,d", (long)winAmount) + "원을 획득하셨습니다!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        } else {
            player.sendMessage("§c아쉽습니다. " + result.getDisplayName() + "§c이(가) 나왔습니다.");
            player.sendMessage("§c다음 기회를 노려보세요!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        }

        // 베팅 정보 초기화
        playerBets.remove(playerUUID);
    }
}