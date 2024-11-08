package rp.rouletteplugin.hologramsystem;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RouletteAnimation {
    private final Plugin plugin;
    private final RouletteWheel rouletteWheel;
    private final RouletteBall rouletteBall;
    private boolean isSpinning;
    private double currentAngle;
    private double spinSpeed;
    private BukkitRunnable animationTask;

    // 상수 정의
    private static final double INITIAL_SPEED = 0.15;
    private static final double DECELERATION = 0.995;
    private static final int MAX_TICKS = 200;
    private static final double BALL_ORBIT_RADIUS = 1.5;

    public RouletteAnimation(Plugin plugin, RouletteWheel rouletteWheel, RouletteBall rouletteBall) {
        this.plugin = plugin;
        this.rouletteWheel = rouletteWheel;
        this.rouletteBall = rouletteBall;
        this.isSpinning = false;
        this.currentAngle = 0;
        this.spinSpeed = INITIAL_SPEED;
    }

    public void startSpin(Player player) {
        if (isSpinning) return;
        isSpinning = true;
        spinSpeed = INITIAL_SPEED;

        animationTask = new BukkitRunnable() {
            private int ticks = 0;

            @Override
            public void run() {
                if (!isSpinning || ticks >= MAX_TICKS) {
                    stopSpin(player, calculateFinalNumber());
                    this.cancel();
                    return;
                }

                // 회전 각도 업데이트
                currentAngle += spinSpeed;
                if (currentAngle >= 2 * Math.PI) {
                    currentAngle -= 2 * Math.PI;
                }

                // 공의 위치 업데이트
                updateBallPosition(player);

                // 속도 감소
                spinSpeed *= DECELERATION;
                ticks++;
            }
        };
        animationTask.runTaskTimer(plugin, 0L, 1L);
    }

    public void stopSpin(Player player, int finalNumber) {
        if (!isSpinning) return;
        isSpinning = false;

        if (animationTask != null) {
            animationTask.cancel();
        }

        // 최종 위치로 공 이동
        Location finalLocation = calculateFinalPosition(finalNumber);
        rouletteBall.updatePosition(player, finalLocation);

        // 결과 표시 효과
        showResultEffect(player, finalNumber);
    }

    private void updateBallPosition(Player player) {
        Location center = rouletteWheel.getCenterLocation();
        double x = center.getX() + BALL_ORBIT_RADIUS * Math.cos(currentAngle);
        double z = center.getZ() + BALL_ORBIT_RADIUS * Math.sin(currentAngle);

        // 약간의 상하 움직임 추가
        double y = center.getY() + 0.3 + Math.sin(currentAngle * 2) * 0.05;

        Location newLocation = new Location(center.getWorld(), x, y, z);
        rouletteBall.updatePosition(player, newLocation);
    }

    private Location calculateFinalPosition(int number) {
        Location center = rouletteWheel.getCenterLocation();
        double angle = (2 * Math.PI * rouletteWheel.getNumbers().indexOf(number)) / rouletteWheel.getNumbers().size();

        double x = center.getX() + BALL_ORBIT_RADIUS * Math.cos(angle);
        double z = center.getZ() + BALL_ORBIT_RADIUS * Math.sin(angle);

        return new Location(center.getWorld(), x, center.getY() + 0.3, z);
    }

    private int calculateFinalNumber() {
        // 현재 각도에 따른 번호 계산
        double normalizedAngle = (currentAngle + Math.PI) % (2 * Math.PI);
        int index = (int) ((normalizedAngle / (2 * Math.PI)) * rouletteWheel.getNumbers().size());
        return rouletteWheel.getNumbers().get(index);
    }

    private void showResultEffect(Player player, int number) {
        // 결과 표시 효과 구현
        String numberColor = rouletteWheel.getNumberColor(number);
        String resultText = numberColor + "【" + number + "】";

        // 결과 텍스트를 공의 위치 위에 표시
        Location resultLocation = rouletteBall.getCurrentLocation().add(0, 0.5, 0);
        rouletteBall.getHologramManager().createHologram(player, resultLocation, resultText);
    }

    public boolean isSpinning() {
        return isSpinning;
    }
}