package rp.rouletteplugin.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.Sound;
import rp.rouletteplugin.Main;
import rp.rouletteplugin.game.GameManager;
import rp.rouletteplugin.game.RouletteColor;
import org.bukkit.ChatColor;

public class GUIListener implements Listener {
    private final Main plugin;
    private final GameManager gameManager;

    // GUI 식별용 상수
    private static final String MAIN_GUI_TITLE = "§6§l룰렛 게임";
    private static final String AMOUNT_GUI_TITLE = "§e§l베팅 금액 설정";
    private static final String COLOR_GUI_TITLE = "§f§l색상 선택";

    public GUIListener(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null) return;
        event.setCancelled(true);

        String title = event.getView().getTitle();

        if (title.equals(MAIN_GUI_TITLE)) {
            handleMainGUIClick(player, clickedItem, event.getSlot());
        }
        else if (title.equals(AMOUNT_GUI_TITLE)) {
            handleAmountGUIClick(player, clickedItem);
        }
        else if (title.equals(COLOR_GUI_TITLE)) {
            handleColorGUIClick(player, clickedItem);
        }
    }

    private void handleMainGUIClick(Player player, ItemStack clickedItem, int slot) {
        switch (slot) {
            case 11: // 베팅 금액 설정
                openAmountGUI(player);
                break;
            case 13: // 색상 선택
                openColorGUI(player);
                break;
            case 15: // 게임 시작
                startGame(player);
                break;
        }
    }

    private void openAmountGUI(Player player) {
        // 베팅 금액 설정 GUI 열기
        // 추후 구현
    }

    private void openColorGUI(Player player) {
        // 색상 선택 GUI 열기
        // 추후 구현
    }

    private void startGame(Player player) {
        // 베팅 금액과 색상이 설정되었는지 확인
        if (!gameManager.isPlayerBetComplete(player.getUniqueId())) {
            player.sendMessage("§c베팅 금액과 색상을 모두 설정해주세요!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        // 게임 시작
        gameManager.startGame(player);
    }
}

    private void handleAmountGUIClick(Player player, ItemStack clickedItem) {
        if (clickedItem.getType() == Material.BARRIER) {
            plugin.getRouletteGUI().openMainGUI(player);
            return;
        }

        if (clickedItem.getType() == Material.GOLD_INGOT) {
            String amountStr = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName())
                    .replace("원", "");
            try {
                double amount = Double.parseDouble(amountStr);

                // 플레이어의 보유 금액 확인
                if (plugin.getEconomy().getBalance(player) < amount) {
                    player.sendMessage("§c보유 금액이 부족합니다!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    return;
                }

                // 베팅 금액 설정
                gameManager.setPlayerBetAmount(player.getUniqueId(), amount);
                player.sendMessage("§a베팅 금액이 " + amount + "원으로 설정되었습니다.");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                plugin.getRouletteGUI().openMainGUI(player);
            } catch (NumberFormatException e) {
                player.sendMessage("§c오류가 발생했습니다.");
            }
        }
    }

    private void handleColorGUIClick(Player player, ItemStack clickedItem) {
        if (clickedItem.getType() == Material.BARRIER) {
            plugin.getRouletteGUI().openMainGUI(player);
            return;
        }

        RouletteColor selectedColor = null;
        switch (clickedItem.getType()) {
            case RED_WOOL:
                selectedColor = RouletteColor.RED;
                break;
            case BLACK_WOOL:
                selectedColor = RouletteColor.BLACK;
                break;
            case LIME_WOOL:
                selectedColor = RouletteColor.GREEN;
                break;
        }

        if (selectedColor != null) {
            gameManager.setPlayerBetColor(player.getUniqueId(), selectedColor);
            player.sendMessage("§a" + selectedColor.getDisplayName() + "§a색을 선택했습니다.");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            plugin.getRouletteGUI().openMainGUI(player);
        }
    }
}