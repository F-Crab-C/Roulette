package rp.rouletteplugin.game;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Sound;
import rp.rouletteplugin.Main;

import java.util.UUID;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private final Main plugin;
    private final ConcurrentHashMap<UUID, PlayerBet> playerBets;
    private final ConcurrentHashMap<UUID, Boolean> playerGameStatus;
    private final Random random;

    // 배당률 상수
    private static final double NORMAL_MULTIPLIER = 2.0;
    private static final double GREEN_MULTIPLIER = 14.0;

    public GameManager(Main plugin) {
        this.plugin = plugin;
        this.playerBets = new ConcurrentHashMap<>();
        this.playerGameStatus = new ConcurrentHashMap<>();
        this.random = new Random();
    }

    public void setPlayerBetAmount(UUID playerUUID, double amount) {
        try {
            PlayerBet bet = playerBets.computeIfAbsent(playerUUID,
                    k -> new PlayerBet(playerUUID, 0, null));
            bet.setAmount(amount);
        } catch (Exception e) {
            plugin.getLogger().warning("베팅 금액 설정 중 오류 발생: " + e.getMessage());
        }
    }

    public void setPlayerBetColor(UUID playerUUID, RouletteColor color) {
        try {
            PlayerBet bet = playerBets.computeIfAbsent(playerUUID,
                    k -> new PlayerBet(playerUUID, 0, null));
            bet.setColor(color);
        } catch (Exception e) {
            plugin.getLogger().warning("베팅 색상 설정 중 오류 발생: " + e.getMessage());
        }
    }

    public boolean isPlayerBetComplete(UUID playerUUID) {
        PlayerBet bet = playerBets.get(playerUUID);
        return bet != null && bet.getAmount() > 0 && bet.getColor() != null;
    }

    public boolean isPlayerInGame(UUID playerUUID) {
        return playerGameStatus.getOrDefault(playerUUID, false);
    }

    public void startGame(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (isPlayerInGame(playerUUID)) {
            player.sendMessage("§c이미 게임이 진행 중입니다!");
            return;
        }

        try {
            PlayerBet bet = playerBets.get(playerUUID);
            if (bet == null) {
                player.sendMessage("§c베팅 정보가 없습니다!");
                return;
            }

            // 보유 금액 재확인
            if (plugin.getEconomy().getBalance(player) < bet.getAmount()) {
                player.sendMessage("§c보유 금액이 부족합니다!");
                return;
            }

            // 게임 상태 설정
            playerGameStatus.put(playerUUID, true);

            // 베팅 금액 차감
            plugin.getEconomy().withdrawPlayer(player, bet.getAmount());

            // 게임 진행
            new BukkitRunnable() {
                private int ticks = 0;
                private final int ANIMATION_DURATION = 40;

                @Override
                public void run() {
                    if (ticks >= ANIMATION_DURATION) {
                        determineResult(player);
                        playerGameStatus.remove(playerUUID);
                        this.cancel();
                        return;
                    }

                    if (ticks % 2 == 0) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                    }

                    ticks++;
                }
            }.runTaskTimer(plugin, 0L, 1L);

        } catch (Exception e) {
            player.sendMessage("§c게임 진행 중 오류가 발생했습니다.");
            plugin.getLogger().warning("게임 진행 중 오류 발생: " + e.getMessage());
            playerGameStatus.remove(playerUUID);
        }
    }

    public void cleanupPlayerData(UUID playerUUID) {
        playerBets.remove(playerUUID);
        playerGameStatus.remove(playerUUID);
    }

    public PlayerBet getPlayerBet(UUID playerUUID) {
        return playerBets.get(playerUUID);
    }

    private void determineResult(Player player) {
        // ... (기존 결과 처리 코드)
    }
}