package rp.rouletteplugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rp.rouletteplugin.Main;
import rp.rouletteplugin.game.GameManager;
import rp.rouletteplugin.game.PlayerBet;

import java.util.Arrays;

public class RouletteGUI {
    private final Main plugin;
    private final GameManager gameManager;
    public static final int MAIN_GUI_SIZE = 27;
    private static final String MAIN_GUI_TITLE = "§6§l룰렛 게임";
    private static final String AMOUNT_GUI_TITLE = "§e§l베팅 금액 설정";
    private static final String COLOR_GUI_TITLE = "§f§l색상 선택";

    public RouletteGUI(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public void openMainGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, MAIN_GUI_SIZE, MAIN_GUI_TITLE);

        // 베팅 금액 설정 버튼
        ItemStack amountButton = createGuiItem(Material.GOLD_INGOT,
                "§e베팅 금액 설정",
                "§7클릭하여 베팅 금액을 설정하세요",
                "§7현재 보유금액: §e" + String.format("%,d", (long)plugin.getEconomy().getBalance(player)) + "원");

        // 색상 선택 버튼
        PlayerBet currentBet = gameManager.getPlayerBet(player.getUniqueId());
        ItemStack colorButton = createGuiItem(Material.WHITE_WOOL,
                "§f색상 선택",
                "§7클릭하여 베팅할 색상을 선택하세요",
                "§7현재 베팅금액: §e" + (currentBet != null ? String.format("%,d", (long)currentBet.getAmount()) : "설정 필요") + "원");

        // 게임 시작 버튼
        ItemStack startButton = createGuiItem(Material.EMERALD,
                "§a게임 시작",
                "§7클릭하여 게임을 시작하세요");

        // GUI에 아이템 배치
        inv.setItem(11, amountButton);
        inv.setItem(13, colorButton);
        inv.setItem(15, startButton);

        player.openInventory(inv);
    }

    public void openAmountGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, MAIN_GUI_SIZE, AMOUNT_GUI_TITLE);

        // 베팅 금액 옵션들
        long[] amounts = {1000, 5000, 10000, 50000, 100000, 500000};
        int[] slots = {10, 11, 12, 14, 15, 16};

        for (int i = 0; i < amounts.length; i++) {
            ItemStack amountItem = createGuiItem(Material.GOLD_INGOT,
                    "§e" + String.format("%,d", amounts[i]) + "원",
                    "§7클릭하여 베팅",
                    "§7현재 보유금액: §e" + String.format("%,d", (long)plugin.getEconomy().getBalance(player)) + "원");
            inv.setItem(slots[i], amountItem);
        }

        // 돌아가기 버튼
        ItemStack backButton = createGuiItem(Material.BARRIER,
                "§c돌아가기",
                "§7메인 메뉴로 돌아갑니다");
        inv.setItem(22, backButton);

        player.openInventory(inv);
    }

    public void openColorGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, MAIN_GUI_SIZE, COLOR_GUI_TITLE);

        // 빨강 선택
        ItemStack redButton = createGuiItem(Material.RED_WOOL,
                "§c빨강",
                "§7승리시 2배",
                "§7클릭하여 선택");

        // 검정 선택
        ItemStack blackButton = createGuiItem(Material.BLACK_WOOL,
                "§0검정",
                "§7승리시 2배",
                "§7클릭하여 선택");

        // 초록 선택
        ItemStack greenButton = createGuiItem(Material.LIME_WOOL,
                "§a초록",
                "§7승리시 14배",
                "§7클릭하여 선택");

        // 현재 베팅 정보
        PlayerBet currentBet = gameManager.getPlayerBet(player.getUniqueId());
        if (currentBet != null) {
            String betAmount = String.format("%,d", (long)currentBet.getAmount());
            redButton = updateItemLore(redButton, "§7현재 베팅금액: §e" + betAmount + "원");
            blackButton = updateItemLore(blackButton, "§7현재 베팅금액: §e" + betAmount + "원");
            greenButton = updateItemLore(greenButton, "§7현재 베팅금액: §e" + betAmount + "원");
        }

        // GUI에 아이템 배치
        inv.setItem(11, redButton);
        inv.setItem(13, blackButton);
        inv.setItem(15, greenButton);

        // 돌아가기 버튼
        ItemStack backButton = createGuiItem(Material.BARRIER,
                "§c돌아가기",
                "§7메인 메뉴로 돌아갑니다");
        inv.setItem(22, backButton);

        player.openInventory(inv);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack updateItemLore(ItemStack item, String... additionalLore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(additionalLore));
        item.setItemMeta(meta);
        return item;
    }
}