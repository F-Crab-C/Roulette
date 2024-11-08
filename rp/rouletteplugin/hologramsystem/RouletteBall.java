package rp.rouletteplugin.hologramsystem;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RouletteBall {
    private final HologramManager hologramManager;
    private Location currentLocation;
    private Vector velocity;
    private boolean isMoving;
    private int entityId;
    private final double GRAVITY = 0.05;
    private final double FRICTION = 0.98;

    public RouletteBall(Location startLocation) {
        this.hologramManager = new HologramManager();
        this.currentLocation = startLocation.clone();
        this.velocity = new Vector(0, 0, 0);
        this.isMoving = false;
    }

    public void spawn(Player player) {
        // 공 홀로그램 생성
        hologramManager.createHologram(player, currentLocation, "§f⚪");
    }

    public void move(Location targetLocation, double speed) {
        if (!isMoving) return;

        // 목표 지점까지의 방향 벡터 계산
        Vector direction = targetLocation.toVector().subtract(currentLocation.toVector());

        // 속도 계산
        velocity = direction.normalize().multiply(speed);

        // 중력 효과 적용
        velocity.setY(velocity.getY() - GRAVITY);

        // 마찰 적용
        velocity.multiply(FRICTION);

        // 새로운 위치 계산
        currentLocation.add(velocity);
    }

    public void startMoving() {
        this.isMoving = true;
    }

    public void stopMoving() {
        this.isMoving = false;
        velocity = new Vector(0, 0, 0);
    }

    public void updatePosition(Player player) {
        if (isMoving) {
            hologramManager.removeHologram(player);
            hologramManager.createHologram(player, currentLocation, "§f⚪");
        }
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity.clone();
    }

    public Location getCurrentLocation() {
        return currentLocation.clone();
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void remove(Player player) {
        hologramManager.removeHologram(player);
    }

    // 공의 회전 운동을 위한 메서드
    public void spin(Location center, double radius, double angle, double speed) {
        if (!isMoving) return;

        // 원운동 계산
        double x = center.getX() + radius * Math.cos(angle);
        double z = center.getZ() + radius * Math.sin(angle);

        currentLocation.setX(x);
        currentLocation.setZ(z);

        // Y축 높이는 유지하되, 약간의 흔들림 효과 추가
        double wobble = Math.sin(angle * 2) * 0.05;
        currentLocation.setY(center.getY() + wobble);
    }

    // 공이 특정 위치에 가까운지 확인하는 메서드
    public boolean isNear(Location location, double threshold) {
        return currentLocation.distance(location) < threshold;
    }

    // 공의 움직임에 물리적 효과를 추가하는 메서드
    public void applyPhysics() {
        if (!isMoving) return;

        // 중력 적용
        velocity.setY(velocity.getY() - GRAVITY);

        // 마찰력 적용
        velocity.multiply(FRICTION);

        // 속도가 매우 작아지면 정지
        if (velocity.length() < 0.01) {
            stopMoving();
            return;
        }

        // 새로운 위치 계산
        currentLocation.add(velocity);
    }
}