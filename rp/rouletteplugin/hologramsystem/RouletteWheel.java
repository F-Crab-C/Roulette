package rp.rouletteplugin.hologramsystem;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouletteWheel {
    private final HologramManager hologramManager;
    private final Location centerLocation;
    private final List<Integer> numbers;
    private final Map<Integer, String> numberColors;
    private boolean isSpinning;

    private static final double WHEEL_RADIUS = 2.0; // 룰렛 휠의 기본 반지름
    private static final double NUMBER_HEIGHT = 0.25; // 숫자들의 높이 간격

    private static final String GREEN_COLOR = "§2";  // 초록색
    private static final String RED_COLOR = "§c";    // 빨간색
    private static final String BLACK_COLOR = "§8";  // 검은색
    private static final String NUMBER_FORMAT = "【%d】"; // 숫자 포맷

    // 룰렛 번호 배열 (실제 룰렛 순서대로)
    private static final int[] ROULETTE_NUMBERS = {
            0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36,
            11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9,
            22, 18, 29, 7, 28, 12, 35, 3, 26
    };

    public RouletteWheel(Location center) {
        this.hologramManager = new HologramManager();
        this.centerLocation = center;
        this.numbers = new ArrayList<>();
        this.numberColors = new HashMap<>();
        this.isSpinning = false;
        initializeNumbers();
    }

    private void initializeNumbers() {
        for (int number : ROULETTE_NUMBERS) {
            numbers.add(number);
            // 개선된 색상과 심볼 포맷팅
            if (number == 0) {
                numberColors.put(number, GREEN_COLOR + String.format(NUMBER_FORMAT, number));
            } else if (isRedNumber(number)) {
                numberColors.put(number, RED_COLOR + String.format(NUMBER_FORMAT, number));
            } else {
                numberColors.put(number, BLACK_COLOR + String.format(NUMBER_FORMAT, number));
            }
        }
    }

    public class RouletteBall {
        // 공 심볼 상수
        private static final String BALL_SYMBOL = "§f●"; // 흰색 공
        private static final String MOVING_BALL_SYMBOL = "§7◎"; // 회전 중인 공

        public void spawn(Player player) {
            hologramManager.createHologram(player, currentLocation, BALL_SYMBOL);
        }

        public void updatePosition(Player player) {
            if (isMoving) {
                hologramManager.removeHologram(player);
                hologramManager.createHologram(player, currentLocation, MOVING_BALL_SYMBOL);
            } else {
                hologramManager.removeHologram(player);
                hologramManager.createHologram(player, currentLocation, BALL_SYMBOL);
            }
        }
    }

    private boolean isRedNumber(int number) {
        // 빨간색 숫자 정의
        int[] redNumbers = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
        for (int redNumber : redNumbers) {
            if (redNumber == number) return true;
        }
        return false;
    }

    public void createWheel(Player player) {
        // 숫자들의 위치를 더 자연스럽게 배치
        double angleIncrement = 2 * Math.PI / numbers.size();

        for (int i = 0; i < numbers.size(); i++) {
            double angle = i * angleIncrement;

            // 위치 계산 개선
            double x = centerLocation.getX() + WHEEL_RADIUS * Math.cos(angle);
            double y = centerLocation.getY() + NUMBER_HEIGHT; // 높이 조정
            double z = centerLocation.getZ() + WHEEL_RADIUS * Math.sin(angle);

            Location numLocation = new Location(
                    centerLocation.getWorld(),
                    x,
                    y,
                    z
            ).setDirection(new Vector(x - centerLocation.getX(), 0, z - centerLocation.getZ()));

            int number = numbers.get(i);
            String colorCode = numberColors.get(number);
            hologramManager.createHologram(player, numLocation, colorCode + number);
        }
    }

    public Location getCenterLocation() {
        return centerLocation.clone();
    }

    public boolean isSpinning() {
        return isSpinning;
    }

    public void setSpinning(boolean spinning) {
        isSpinning = spinning;
    }

    public List<Integer> getNumbers() {
        return new ArrayList<>(numbers);
    }

    public String getNumberColor(int number) {
        return numberColors.getOrDefault(number, "§f");
    }
    public class RouletteAnimation {
        // 승리/패배 시 효과 심볼
        private static final String WIN_SYMBOL = "§6✦";  // 승리 효과
        private static final String LOSE_SYMBOL = "§7✧"; // 패배 효과

        public void showResult(Player player, boolean isWin) {
            String resultSymbol = isWin ? WIN_SYMBOL : LOSE_SYMBOL;
            // 결과 표시 로직...
        }
    }
}