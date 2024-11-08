package rp.rouletteplugin.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import rp.rouletteplugin.Main;
import rp.rouletteplugin.game.GameManager;
import rp.rouletteplugin.game.RouletteColor;

public class GUIListener implements Listener {
    private final Main plugin;
    private final GameManager gameManager;

    public GUIListener(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        String title = event.getView().getTitle();

        if (clickedItem == null) return;

        // 디버그 메시지
        plugin.getLogger().info("Inventory clicked: " + title);
        plugin.getLogger().info("Clicked item: " + clickedItem.getType());

        event.setCancelled(true);

        switch(title) {
            case "§6§l룰렛 게임":
                plugin.getLogger().info("Main GUI clicked");
                handleMainGUIClick(player, clickedItem);
                break;
            case "§e§l베팅 금액 설정":
                plugin.getLogger().info("Amount GUI clicked");
                handleAmountGUIClick(player, clickedItem);
                break;
            case "§f§l색상 선택":
                plugin.getLogger().info("Color GUI clicked");
                handleColorGUIClick(player, clickedItem);
                break;
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().contains("룰렛")) {
            event.setCancelled(true);
        }
    }

    private void handleMainGUIClick(Player player, ItemStack clickedItem) {
        plugin.getLogger().info("Handling main GUI click: " + clickedItem.getType());

        switch (clickedItem.getType()) {
            case GOLD_INGOT:
                plugin.getLogger().info("Opening amount GUI");
                plugin.getRouletteGUI().openAmountGUI(player);
                break;
            case WHITE_WOOL:
                plugin.getLogger().info("Checking bet amount before color selection");
                if (!gameManager.isPlayerBetComplete(player.getUniqueId())) {
                    player.sendMessage("§c먼저 베팅 금액을 설정해주세요!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    return;
                }
                plugin.getRouletteGUI().openColorGUI(player);
                break;
            case EMERALD:
                if (!gameManager.isPlayerBetComplete(player.getUniqueId())) {
                    player.sendMessage("§c베팅 금액과 색상을 모두 설정해주세요!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    return;
                }
                gameManager.startGame(player);
                break;
        }
    }

    private void handleAmountGUIClick(Player player, ItemStack clickedItem) {
        if (clickedItem.getType() == Material.BARRIER) {
            plugin.getRouletteGUI().openMainGUI(player);
            return;
        }

        if (clickedItem.getType() == Material.GOLD_INGOT) {
            String amountStr = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName())
                    .replace("원", "").replace(",", "");
            try {
                double amount = Double.parseDouble(amountStr);

                if (plugin.getEconomy().getBalance(player) < amount) {
                    player.sendMessage("§c보유 금액이 부족합니다!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    return;
                }

                gameManager.setPlayerBetAmount(player.getUniqueId(), amount);
                player.sendMessage("§a베팅 금액이 " + String.format("%,d", (long)amount) + "원으로 설정되었습니다.");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                plugin.getRouletteGUI().openMainGUI(player);
            } catch (NumberFormatException e) {
                player.sendMessage("§c오류가 발생했습니다.");
                plugin.getLogger().warning("금액 변환 중 오류 발생: " + e.getMessage());
            }
        }
    }

    private void handleColorGUIClick(Player player, ItemStack clickedItem) {
        plugin.getLogger().info("Color GUI Click - Item: " + clickedItem.getType());

        if (clickedItem.getType() == Material.BARRIER) {
            plugin.getRouletteGUI().openMainGUI(player);
            return;
        }

        RouletteColor selectedColor = null;
        switch (clickedItem.getType()) {
            case RED_WOOL:
                selectedColor = RouletteColor.RED;
                plugin.getLogger().info("Selected RED");
                break;
            case BLACK_WOOL:
                selectedColor = RouletteColor.BLACK;
                plugin.getLogger().info("Selected BLACK");
                break;
            case LIME_WOOL:
                selectedColor = RouletteColor.GREEN;
                plugin.getLogger().info("Selected GREEN");
                break;
        }

        if (selectedColor != null) {
            gameManager.setPlayerBetColor(player.getUniqueId(), selectedColor);
            plugin.getLogger().info("Player " + player.getName() + " bet color set to: " + selectedColor);

            player.sendMessage("§a" + selectedColor.getDisplayName() + " §f색상을 선택했습니다.");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            plugin.getRouletteGUI().openMainGUI(player);
        }
    }
}