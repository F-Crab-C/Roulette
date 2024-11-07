package rp.rouletteplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ColorGUI {
    private final Main plugin;

    public ColorGUI(Main plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "색상 선택");
        gui.setItem(11, createItem(Material.RED_WOOL, "빨강"));
        gui.setItem(13, createItem(Material.BLACK_WOOL, "검정"));
        gui.setItem(15, createItem(Material.LIME_WOOL, "초록"));
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
        String color = "";
        if (clickedItem.getType() == Material.RED_WOOL) {
            color = "red";
        } else if (clickedItem.getType() == Material.BLACK_WOOL) {
            color = "black";
        } else if (clickedItem.getType() == Material.LIME_WOOL) {
            color = "green";
        }
        plugin.getRouletteManager().setColor(player, color);
        player.sendMessage(color + " 색상을 선택하셨습니다.");
        player.closeInventory();
    }
}
