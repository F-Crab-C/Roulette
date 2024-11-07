package rp.rouletteplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rp.rouletteplugin.gui.RouletteGUI;

public class RouletteCommand implements CommandExecutor {
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

        if (command.getName().equalsIgnoreCase("roulette")) {
            // 권한 체크
            if (!player.hasPermission("roulette.use")) {
                player.sendMessage("§c이 명령어를 사용할 권한이 없습니다.");
                return true;
            }

            // GUI 열기
            plugin.getRouletteGUI().openMainGUI(player);
            return true;
        }

        return false;
    }
}