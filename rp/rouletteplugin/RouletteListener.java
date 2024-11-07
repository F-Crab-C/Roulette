package rp.rouletteplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;

public class RouletteListener implements Listener {
    private final Main plugin;

    public RouletteListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.equals("베팅 설정") || title.equals("베팅 금액 설정") || title.equals("색상 선택")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;

            if (title.equals("베팅 설정")) {
                BettingGUI.handleClick(plugin, player, event.getCurrentItem());
            } else if (title.equals("베팅 금액 설정")) {
                AmountGUI.handleClick(plugin, player, event.getCurrentItem());
            } else if (title.equals("색상 선택")) {
                ColorGUI.handleClick(plugin, player, event.getCurrentItem());
            }
        }
    }
}
