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

    // 룰렛 상수
    private static final double WHEEL_RADIUS = 1.5;
    private static final double NUMBER_HEIGHT = 0.25;

    // 색상 상수
    private static final String GREEN_COLOR = "§2";
    private static final String RED_COLOR = "§c";
    private static final String BLACK_COLOR = "§8";
    private static final String NUMBER_FORMAT = "【%d】";

    // 룰렛 번호 배열
    private static final int[] ROULETTE_NUMBERS = {
            0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36,
            11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9,
            22, 18, 29, 7, 28, 12, 35, 3, 26
    };

    public RouletteWheel(HologramManager hologramManager, Location center) {
        if (center == null || center.getWorld() == null) {
            throw new IllegalArgumentException("Invalid location provided");
        }
        this.hologramManager = hologramManager;
        this.centerLocation = center.clone();
        this.numbers = new ArrayList<>();
        this.numberColors = new HashMap<>();
        this.isSpinning = false;
        initializeNumbers();
    }

    private void initializeNumbers() {
        for (int number : ROULETTE_NUMBERS) {
            numbers.add(number);
            if (number == 0) {
                numberColors.put(number, GREEN_COLOR + String.format(NUMBER_FORMAT, number));
            } else if (isRedNumber(number)) {
                numberColors.put(number, RED_COLOR + String.format(NUMBER_FORMAT, number));
            } else {
                numberColors.put(number, BLACK_COLOR + String.format(NUMBER_FORMAT, number));
            }
        }
    }

    private boolean isRedNumber(int number) {
        int[] redNumbers = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
        for (int redNumber : redNumbers) {
            if (redNumber == number) return true;
        }
        return false;
    }

    public void createWheel(Player player) {
        double angleIncrement = 2 * Math.PI / numbers.size();

        for (int i = 0; i < numbers.size(); i++) {
            double angle = i * angleIncrement;

            double x = centerLocation.getX() + WHEEL_RADIUS * Math.cos(angle);
            double y = centerLocation.getY() + NUMBER_HEIGHT;
            double z = centerLocation.getZ() + WHEEL_RADIUS * Math.sin(angle);

            Location numLocation = new Location(
                    centerLocation.getWorld(),
                    x,
                    y,
                    z
            );

            int number = numbers.get(i);
            String coloredNumber = numberColors.get(number);
            hologramManager.createHologram(player, numLocation, coloredNumber);
        }
    }

    public void removeWheel(Player player) {
        // 모든 홀로그램 제거
        hologramManager.removeHologram(player);
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

    public int getNumberAtAngle(double angle) {
        int index = (int) (((angle + Math.PI) % (2 * Math.PI)) / (2 * Math.PI) * numbers.size());
        return numbers.get(index);
    }
}