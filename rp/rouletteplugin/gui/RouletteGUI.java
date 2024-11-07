package rp.rouletteplugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rp.rouletteplugin.Main;
import rp.rouletteplugin.game.GameManager;
import rp.rouletteplugin.game.RouletteColor;

import java.util.Arrays;

public class RouletteGUI {
    private final Main plugin;
    private final GameManager gameManager;

    // GUI 상수
    private static final String GUI_TITLE = "§6§l룰렛 게임";
    private static final int GUI_SIZE = 27;

    // 아이템 위치 상수
    private static final int AMOUNT_BUTTON_SLOT = 11;
    private static final int COLOR_BUTTON_SLOT = 13;
    private static final int START_BUTTON_SLOT = 15;

    public RouletteGUI(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    public void openMainGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);

        // 베팅 금액 설정 버튼
        ItemStack amountButton = createGuiItem(Material.GOLD_INGOT,
                "§e베팅 금액 설정",
                "§7클릭하여 베팅 금액을 설정하세요");

        // 색상 선택 버튼
        ItemStack colorButton = createGuiItem(Material.WOOL,
                "§f색상 선택",
                "§7클릭하여 베팅할 색상을 선택하세요");

        // 게임 시작 버튼
        ItemStack startButton = createGuiItem(Material.EMERALD,
                "§a게임 시작",
                "§7클릭하여 게임을 시작하세요");

        // GUI에 아이템 배치
        inv.setItem(AMOUNT_BUTTON_SLOT, amountButton);
        inv.setItem(COLOR_BUTTON_SLOT, colorButton);
        inv.setItem(START_BUTTON_SLOT, startButton);

        // GUI 열기
        player.openInventory(inv);
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}