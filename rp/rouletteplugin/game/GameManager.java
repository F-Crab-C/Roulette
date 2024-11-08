package rp.rouletteplugin.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import rp.rouletteplugin.hologramsystem.HologramSystemManager;

public class GameManager {
    private static GameManager instance;
    private final Plugin plugin;
    private final HologramSystemManager hologramSystem;
    private final BettingManager bettingManager;
    private boolean isGameRunning;

    private GameManager(Plugin plugin) {
        this.plugin = plugin;
        this.hologramSystem = HologramSystemManager.getInstance(plugin);
        this.bettingManager = BettingManager.getInstance();
        this.isGameRunning = false;
    }

    public static GameManager getInstance(Plugin plugin) {
        if (instance == null) {
            instance = new GameManager(plugin);
        }
        return instance;
    }

    public void startGame(Player player) {
        if (isGameRunning) {
            player.sendMessage("§c게임이 이미 진행 중입니다.");
            return;
        }

        isGameRunning = true;
        Location gameLocation = player.getLocation().add(0, 2, 0);

        // 홀로그램 시스템 초기화
        hologramSystem.initializeHologramSystem(gameLocation);
        hologramSystem.startGame(player);

        // 베팅 GUI 열기
        bettingManager.openBettingGUI(player);
    }

    public void startRouletteAnimation(Player player) {
        if (!isGameRunning) return;

        // 베팅 GUI 닫기
        player.closeInventory();

        // 룰렛 회전 시작
        hologramSystem.startSpinning(player);

        // 일정 시간 후 결과 처리
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            processResult(player);
        }, 200L); // 10초 후
    }

    private void processResult(Player player) {
        if (!isGameRunning) return;

        // 결과 계산
        int result = calculateResult();

        // 애니메이션 정지 및 결과 표시
        hologramSystem.stopSpinning(player, result);

        // 베팅 결과 처리
        bettingManager.processBettingResult(player, result);

        // 게임 상태 초기화
        isGameRunning = false;
    }

    private int calculateResult() {
        // 결과값 계산 로직
        return (int) (Math.random() * 37); // 0-36 사이의 랜덤 값
    }

    public void stopGame(Player player) {
        if (!isGameRunning) return;

        // 홀로그램 제거
        hologramSystem.getHologramManager().removeHologram(player);

        // 게임 상태 초기화
        isGameRunning = false;

        // 베팅 초기화
        bettingManager.clearBetting(player);

        player.sendMessage("§c게임이 중단되었습니다.");
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public void handlePlayerQuit(Player player) {
        if (isGameRunning) {
            stopGame(player);
        }
    }
}