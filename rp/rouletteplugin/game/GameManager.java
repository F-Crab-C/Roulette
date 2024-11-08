package rp.rouletteplugin.game;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rp.rouletteplugin.Main;
import rp.rouletteplugin.animation.RouletteAnimation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {
    private final Main plugin;
    private final Map<UUID, PlayerBet> playerBets;
    private final Map<UUID, Boolean> playerGameStatus;
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
            // 디버그 로그 추가
            plugin.getLogger().info("Setting bet amount for " + playerUUID + ": " + amount);

            // 기존 베팅 정보 확인
            PlayerBet currentBet = playerBets.get(playerUUID);
            if (currentBet == null) {
                currentBet = new PlayerBet(playerUUID, amount, null);
                playerBets.put(playerUUID, currentBet);
            } else {
                currentBet.setAmount(amount);
            }

            // 저장 확인
            plugin.getLogger().info("Bet amount set successfully. Current bet: " + playerBets.get(playerUUID).getAmount());
        } catch (Exception e) {
            plugin.getLogger().warning("베팅 금액 설정 중 오류 발생: " + e.getMessage());
        }
    }

    public void setPlayerBetColor(UUID playerUUID, RouletteColor color) {
        try {
            PlayerBet bet = playerBets.computeIfAbsent(playerUUID,
                    k -> new PlayerBet(playerUUID, 0, null));
            bet.setColor(color);
            plugin.getLogger().info("Bet color set for " + playerUUID + ": " + color);
        } catch (Exception e) {
            plugin.getLogger().warning("베팅 색상 설정 중 오류 발생: " + e.getMessage());
        }
    }

    public boolean isPlayerBetComplete(UUID playerUUID) {
        PlayerBet bet = playerBets.get(playerUUID);
        boolean complete = bet != null && bet.getAmount() > 0;
        plugin.getLogger().info("Checking bet completion for " + playerUUID +
                ": " + complete + " (amount: " + (bet != null ? bet.getAmount() : "null") + ")");
        return complete;
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

            // 결과 미리 결정
            final RouletteColor result;
            int randomNum = random.nextInt(100);
            if (randomNum < 20) {
                result = RouletteColor.GREEN;
            } else if (randomNum < 60) {
                result = RouletteColor.RED;
            } else {
                result = RouletteColor.BLACK;
            }

            // 애니메이션 실행
            RouletteAnimation animation = new RouletteAnimation(plugin);
            animation.playRouletteAnimation(player, result, () -> {
                determineResult(player, result);
                playerGameStatus.remove(playerUUID);
            });

        } catch (Exception e) {
            player.sendMessage("§c게임 진행 중 오류가 발생했습니다.");
            plugin.getLogger().warning("게임 진행 중 오류 발생: " + e.getMessage());
            playerGameStatus.remove(playerUUID);
        }
    }

    private void determineResult(Player player, RouletteColor result) {
        UUID playerUUID = player.getUniqueId();
        PlayerBet bet = playerBets.get(playerUUID);

        try {
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

        } catch (Exception e) {
            player.sendMessage("§c결과 처리 중 오류가 발생했습니다.");
            plugin.getLogger().warning("결과 처리 중 오류 발생: " + e.getMessage());
            // 베팅 금액 환불
            plugin.getEconomy().depositPlayer(player, bet.getAmount());
        } finally {
            // 베팅 정보 초기화
            playerBets.remove(playerUUID);
        }
    }

    public void cleanupPlayerData(UUID playerUUID) {
        playerBets.remove(playerUUID);
        playerGameStatus.remove(playerUUID);
    }

    public PlayerBet getPlayerBet(UUID playerUUID) {
        return playerBets.get(playerUUID);
    }
}