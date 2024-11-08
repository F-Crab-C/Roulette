package rp.rouletteplugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import rp.rouletteplugin.Main;

import java.util.ArrayList;
import java.util.List;

public class RouletteCommand implements CommandExecutor, TabCompleter {
    private final Main plugin;

    public RouletteCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        Player player = (Player) sender;

        // 권한 체크
        if (!player.hasPermission("roulette.use")) {
            player.sendMessage("§c이 명령어를 사용할 권한이 없습니다.");
            return true;
        }

        if (args.length == 0) {
            // GUI 열기
            plugin.getRouletteGUI().openMainGUI(player);
            return true;
        }

        // 도움말 명령어
        if (args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(player);
            return true;
        }

        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("§6=== 룰렛 게임 도움말 ===");
        player.sendMessage("§f/룰렛 §7- 룰렛 게임 GUI를 엽니다.");
        player.sendMessage("§f/룰렛 help §7- 도움말을 표시합니다.");
        player.sendMessage("§6=====================");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("help");
        }

        return completions;
    }
}