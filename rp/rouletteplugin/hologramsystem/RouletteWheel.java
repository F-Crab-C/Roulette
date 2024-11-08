package rp.rouletteplugin.hologramsystem;

import org.bukkit.Location;
import org.bukkit.entity.Player;
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
            // 숫자별 색상 설정
            if (number == 0) {
                numberColors.put(number, "§2"); // 초록색
            } else if (isRedNumber(number)) {
                numberColors.put(number, "§c"); // 빨간색
            } else {
                numberColors.put(number, "§8"); // 검은색
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
        double radius = 2.0; // 룰렛의 반지름
        double angleIncrement = 2 * Math.PI / numbers.size();

        for (int i = 0; i < numbers.size(); i++) {
            double angle = i * angleIncrement;
            double x = centerLocation.getX() + radius * Math.cos(angle);
            double z = centerLocation.getZ() + radius * Math.sin(angle);

            Location numLocation = new Location(
                    centerLocation.getWorld(),
                    x,
                    centerLocation.getY(),
                    z
            );

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
}