package rp.rouletteplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AmountGUI {
    private final Main plugin;

    public AmountGUI(Main plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "베팅 금액 설정");
        gui.setItem(11, createItem(Material.GOLD_NUGGET, "+10"));
        gui.setItem(13, createItem(Material.GOLD_INGOT, "+100"));
        gui.setItem(15, createItem(Material.GOLD_BLOCK, "+1000"));
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
        int amount = 0;
        if (clickedItem.getType() == Material.GOLD_NUGGET) {
            amount = 10;
        } else if (clickedItem.getType() == Material.GOLD_INGOT) {
            amount = 100;
        } else if (clickedItem.getType() == Material.GOLD_BLOCK) {
            amount = 1000;
        }
        plugin.getRouletteManager().setBet(player, amount);
        player.sendMessage("베팅 금액이 " + amount + "원으로 설정되었습니다.");
        player.closeInventory();
    }
}
