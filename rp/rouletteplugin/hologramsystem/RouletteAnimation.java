package rp.rouletteplugin.hologramsystem;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

public class RouletteAnimation {
    private final Plugin plugin;
    private final RouletteWheel rouletteWheel;
    private final RouletteBall rouletteBall;
    private boolean isSpinning;
    private double spinSpeed;
    private double currentAngle;
    private BukkitRunnable animationTask;

    public RouletteAnimation(Plugin plugin, RouletteWheel rouletteWheel, RouletteBall rouletteBall) {
        this.plugin = plugin;
        this.rouletteWheel = rouletteWheel;
        this.rouletteBall = rouletteBall;
        this.isSpinning = false;
        this.spinSpeed = 0.15; // 초기 회전 속도
        this.currentAngle = 0;
    }

    public void startSpin(Player player) {
        if (isSpinning) return;
        isSpinning = true;
        spinSpeed = 0.2; // 초기 속도 설정

        animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isSpinning) {
                    this.cancel();
                    return;
                }
                animateSpin(player);
            }
        };
        animationTask.runTaskTimer(plugin, 0L, 1L); // 매 틱마다 실행
    }

    public void stopSpin(Player player, int finalNumber) {
        isSpinning = false;
        if (animationTask != null) {
            animationTask.cancel();
        }
        // 최종 위치로 공 이동
        Location finalLocation = calculateNumberPosition(finalNumber);
        rouletteBall.move(finalLocation, 0.05);
        rouletteBall.updatePosition(player);
    }

    private void animateSpin(Player player) {
        currentAngle += spinSpeed;
        if (currentAngle >= 2 * Math.PI) {
            currentAngle -= 2 * Math.PI;
        }

        // 룰렛 휠 회전 (숫자 위치 업데이트)
        rouletteWheel.createWheel(player);

        // 공의 움직임
        double radius = 1.8; // 공이 움직이는 반경
        Location centerLocation = rouletteWheel.getCenterLocation();
        double x = centerLocation.getX() + radius * Math.cos(currentAngle);
        double z = centerLocation.getZ() + radius * Math.sin(currentAngle);
        Location ballLocation = new Location(centerLocation.getWorld(), x, centerLocation.getY() + 0.5, z);

        rouletteBall.move(ballLocation, spinSpeed);
        rouletteBall.updatePosition(player);

        // 속도 감소
        spinSpeed *= 0.999; // 매우 천천히 감소
    }

    private Location calculateNumberPosition(int number) {
        // 주어진 숫자의 위치 계산
        int index = rouletteWheel.getNumbers().indexOf(number);
        double angleIncrement = 2 * Math.PI / rouletteWheel.getNumbers().size();
        double angle = index * angleIncrement;

        Location centerLocation = rouletteWheel.getCenterLocation();
        double radius = 2.0;
        double x = centerLocation.getX() + radius * Math.cos(angle);
        double z = centerLocation.getZ() + radius * Math.sin(angle);

        return new Location(centerLocation.getWorld(), x, centerLocation.getY(), z);
    }

    public boolean isSpinning() {
        return isSpinning;
    }
}