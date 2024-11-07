package rp.rouletteplugin.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.ChatColor;
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
        event.setCancelled(true);

        // 메인 메뉴
        if (title.equals("§6§l룰렛 게임")) {
            handleMainMenu(player, clickedItem);
        }
        // 베팅 금액 설정
        else if (title.equals("§e§l베팅 금액 설정")) {
            handleBettingAmount(player, clickedItem);
        }
        // 색상 선택
        else if (title.equals("§f§l색상 선택")) {
            handleColorSelection(player, clickedItem);
        }
    }

    private void handleMainMenu(Player player, ItemStack clickedItem) {
        switch (clickedItem.getType()) {
            case GOLD_INGOT:
                plugin.getRouletteGUI().openAmountGUI(player);
                break;
            case WHITE_WOOL:
                if (gameManager.getPlayerBet(player.getUniqueId()) == null ||
                        gameManager.getPlayerBet(player.getUniqueId()).getAmount() <= 0) {
                    player.sendMessage("§c먼저 베팅 금액을 설정해주세요!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    return;
                }
                plugin.getRouletteGUI().openColorGUI(player);
                break;
            case EMERALD:
                startGame(player);
                break;
        }
    }

    private void handleBettingAmount(Player player, ItemStack clickedItem) {
        if (clickedItem.getType() == Material.BARRIER) {
            plugin.getRouletteGUI().openMainGUI(player);
            return;
        }

        if (clickedItem.getType() == Material.GOLD_INGOT) {
            String amountStr = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName())
                    .replace("원", "").replace(",", "");
            try {
                double amount = Double.parseDouble(amountStr);

                // 보유 금액 확인
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
            }
        }
    }

    private void handleColorSelection(Player player, ItemStack clickedItem) {
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
            player.sendMessage("§a" + selectedColor.getDisplayName() + " §f색상을 선택했습니다.");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            plugin.getRouletteGUI().openMainGUI(player);
        }
    }

    private void startGame(Player player) {
        if (!gameManager.isPlayerBetComplete(player.getUniqueId())) {
            player.sendMessage("§c베팅 금액과 색상을 모두 설정해주세요!");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        gameManager.startGame(player);
    }
}