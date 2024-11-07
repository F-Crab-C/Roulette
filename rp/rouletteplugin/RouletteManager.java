package rp.rouletteplugin;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RouletteManager {
    private final Main plugin;
    private final Random random = new Random();
    private final Map<Player, Integer> bets = new HashMap<>();
    private final Map<Player, String> colors = new HashMap<>();

    public RouletteManager(Main plugin) {
        this.plugin = plugin;
    }

    public void setBet(Player player, int amount) {
        bets.put(player, amount);
    }

    public void setColor(Player player, String color) {
        colors.put(player, color);
    }

    public void startGame(Player player) {
        if (!bets.containsKey(player) || !colors.containsKey(player)) {
            player.sendMessage("베팅 금액과 색상을 먼저 선택해주세요.");
            return;
        }

        int betAmount = bets.get(player);
        String chosenColor = colors.get(player);

        if (!Main.getEconomy().has(player, betAmount)) {
            player.sendMessage("베팅 금액이 부족합니다.");
            return;
        }

        Main.getEconomy().withdrawPlayer(player, betAmount);

        String result = determineResult();
        announceResult(player, result, chosenColor, betAmount);

        bets.remove(player);
        colors.remove(player);
    }

    private String determineResult() {
        return random.nextInt(37) == 0 ? "green" : (random.nextBoolean() ? "red" : "black");
    }

    private void announceResult(Player player, String result, String chosenColor, int betAmount) {
        player.sendMessage("룰렛 결과: " + result);
        if (result.equals(chosenColor)) {
            int winAmount = chosenColor.equals("green") ? betAmount * 35 : betAmount * 2;
            Main.getEconomy().depositPlayer(player, winAmount);
            player.sendMessage("축하합니다! " + winAmount + "원을 획득하셨습니다.");
        } else {
            player.sendMessage("아쉽습니다. 다음 기회에 도전해보세요.");
        }
        bets.remove(player);
        colors.remove(player);
    }
}
