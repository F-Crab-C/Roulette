package rp.rouletteplugin.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import rp.rouletteplugin.manager.BettingManager;
import rp.rouletteplugin.manager.GameManager;

public class GUIListener implements Listener {
    private final GameManager gameManager;
    private final BettingManager bettingManager;

    public GUIListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.bettingManager = BettingManager.getInstance();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        // 베팅 GUI 클릭 처리
        if (inventory.getHolder() instanceof BettingGUI) {
            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            // 시작 버튼 클릭
            if (event.getSlot() == 49) { // 시작 버튼 슬롯
                if (bettingManager.hasBetting(player)) {
                    gameManager.startRouletteAnimation(player);
                } else {
                    player.sendMessage("§c베팅을 먼저 해주세요!");
                }
                return;
            }

            // 베팅 처리
            bettingManager.handleBetting(player, event.getSlot(), event.isRightClick());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        // 베팅 GUI가 강제로 닫힌 경우 처리
        if (inventory.getHolder() instanceof BettingGUI) {
            if (gameManager.isGameRunning() && !bettingManager.hasBetting(player)) {
                gameManager.stopGame(player);
            }
        }
    }
}