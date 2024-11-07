package rp.rouletteplugin.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import rp.rouletteplugin.Main;
import rp.rouletteplugin.game.GameManager;
import rp.rouletteplugin.game.RouletteColor;

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
        Inventory inventory = event.getInventory();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null) return;
        event.setCancelled(true);

        // 메인 GUI 처리
        if (inventory.getTitle().equals(MAIN_GUI_TITLE)) {
            handleMainGUIClick(player, clickedItem, event.getSlot());
        }
        // 베팅 금액 GUI 처리
        else if (inventory.getTitle().equals(AMOUNT_GUI_TITLE)) {
            handleAmountGUIClick(player, clickedItem, event.getSlot());
        }
        // 색상 선택 GUI 처리
        else if (inventory.getTitle().equals(COLOR_GUI_TITLE)) {
            handleColorGUIClick(player, clickedItem, event.getSlot());
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
        // 게임 시작 로직
        // 추후 구현
    }

    private void handleAmountGUIClick(Player player, ItemStack clickedItem, int slot) {
        // 베팅 금액 설정 처리
        // 추후 구현
    }

    private void handleColorGUIClick(Player player, ItemStack clickedItem, int slot) {
        // 색상 선택 처리
        // 추후 구현
    }
}