package rp.rouletteplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BettingGUI {
    private final Main plugin;

    public BettingGUI(Main plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "베팅 설정");
        gui.setItem(11, createItem(Material.GOLD_INGOT, "베팅 금액 설정"));
        gui.setItem(15, createItem(Material.WHITE_WOOL, "색상-선택"));
        gui.setItem(15, createItem(Material.EMERALD, "게임 시작")); // 추가된 부분
        player.openInventory(gui);
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static void handleClick(Main plugin, Player player, ItemStack clickedItem) {
        if (clickedItem.getType() == Material.GOLD_INGOT) {
            new AmountGUI(plugin).openGUI(player);
        } else if (clickedItem.getType() == Material.WHITE_WOOL) {
            new ColorGUI(plugin).openGUI(player);
        }
            else if (clickedItem.getType() == Material.EMERALD) { // 추가된 부분
            plugin.getRouletteManager().startGame(player);
        }
    }
}
