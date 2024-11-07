package rp.rouletteplugin.game;

import org.bukkit.entity.Player;

import org.bukkit.Sound;
import rp.rouletteplugin.animation.RouletteAnimation;
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
            animation.playRouletteAnimation(player, result, new Runnable() {
                @Override
                public void run() {
                    determineResult(player, result);
                    playerGameStatus.remove(playerUUID);
                }
            });

        } catch (Exception e) {
            player.sendMessage("§c게임 진행 중 오류가 발생했습니다.");
            plugin.getLogger().warning("게임 진행 중 오류 발생: " + e.getMessage());
            playerGameStatus.remove(playerUUID);
        }
    }

    public void determineResult(Player player, RouletteColor result) {
        UUID playerUUID = player.getUniqueId();
        PlayerBet bet = playerBets.get(playerUUID);
        RouletteAnimation animation = new RouletteAnimation(plugin);

        try {
            // 결과 처리 및 상금 지급
            if (bet.getColor() == result) {
                // 승리한 경우
                double multiplier = (result == RouletteColor.GREEN) ? GREEN_MULTIPLIER : NORMAL_MULTIPLIER;
                double winAmount = bet.getAmount() * multiplier;

                plugin.getEconomy().depositPlayer(player, winAmount);
                player.sendMessage("§a축하합니다! " + result.getDisplayName() + "§a이(가) 나왔습니다!");
                player.sendMessage("§a상금 " + String.format("%,d", (long) winAmount) + "원을 획득하셨습니다!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                // 승리 홀로그램 표시
                animation.showResult(player, result, bet.getAmount(), winAmount);
            } else {
                // 패배한 경우
                player.sendMessage("§c아쉽습니다. " + result.getDisplayName() + "§c이(가) 나왔습니다.");
                player.sendMessage("§c다음 기회를 노려보세요!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);

                // 패배 홀로그램 표시 (winAmount를 0으로 설정하여 패배 표시)
                animation.showResult(player, result, bet.getAmount(), 0);
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

    public PlayerBet getPlayerBet(UUID playerUUID) {
        return playerBets.get(playerUUID);
    }
}