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
import rp.rouletteplugin.animation.RouletteAnimation;

import java.util.Arrays;

public class RouletteGUI {
    private final Main plugin;
    private final GameManager gameManager;

    // GUI 상수
    private static final String GUI_TITLE = "§6§l룰렛 게임";
    public static final int GUI_SIZE = 18;

    // 아이템 위치 상수
    private static final int AMOUNT_BUTTON_SLOT = 11;
    private static final int COLOR_BUTTON_SLOT = 13;
    private static final int START_BUTTON_SLOT = 15;

    private static final String AMOUNT_GUI_TITLE = "§e§l베팅 금액 설정";
    private static final int[] BETTING_AMOUNTS = {1000, 5000, 10000, 50000, 100000};

    public RouletteGUI(Main plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    private static final long[] BET_AMOUNTS = {
            1000, 5000, 10000,
            50000, 100000, 500000
    };

    public void openMainGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);

        // 베팅 금액 설정 버튼
        ItemStack amountButton = createGuiItem(Material.GOLD_INGOT,
                "§e베팅 금액 설정",
                "§7클릭하여 베팅 금액을 설정하세요");

        // 색상 선택 버튼
        ItemStack colorButton = createGuiItem(Material.WHITE_WOOL,
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

    public void openAmountGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, AMOUNT_GUI_TITLE);

        // 베팅 금액 버튼들 생성
        for (int i = 0; i < BET_AMOUNTS.length; i++) {
            ItemStack amountButton = createGuiItem(
                    Material.GOLD_INGOT,
                    "§e" + String.format("%,d", BET_AMOUNTS[i]) + "원",
                    "§7클릭하여 베팅",
                    "§7현재 보유금액: §e" + String.format("%,d", (long) plugin.getEconomy().getBalance(player)) + "원"
            );
            inv.setItem(10 + i, amountButton);
        }
        ItemStack infoItem = createGuiItem(
                Material.PAPER,
                "§f현재 베팅 정보",
                "§7선택된 금액: §e" + (gameManager.getPlayerBet(player.getUniqueId()) != null ?
                        String.format("%,d", (long)gameManager.getPlayerBet(player.getUniqueId()).getAmount()) : "없음")
        );
        inv.setItem(22, infoItem);

        // 돌아가기 버튼
        ItemStack backButton = createGuiItem(
                Material.BARRIER,
                "§c돌아가기",
                "§7메인 메뉴로 돌아갑니다"
        );
        inv.setItem(26, backButton);

        player.openInventory(inv);
    }

    private static final String COLOR_GUI_TITLE = "§f§l색상 선택";

    public void openColorGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, COLOR_GUI_TITLE);

        // 빨강 선택
        ItemStack redButton = createGuiItem(
                Material.RED_WOOL,
                "§c빨강",
                "§7승리시 2배",
                "§7현재 베팅금액: §e" + (gameManager.getPlayerBet(player.getUniqueId()) != null ?
                        String.format("%,d", (long)gameManager.getPlayerBet(player.getUniqueId()).getAmount()) : "설정 필요")
        );

        // 검정 선택
        ItemStack blackButton = createGuiItem(
                Material.BLACK_WOOL,
                "§0검정",
                "§7승리시 2배",
                "§7현재 베팅금액: §e" + (gameManager.getPlayerBet(player.getUniqueId()) != null ?
                        String.format("%,d", (long)gameManager.getPlayerBet(player.getUniqueId()).getAmount()) : "설정 필요")
        );

        // 초록 선택
        ItemStack greenButton = createGuiItem(
                Material.LIME_WOOL,
                "§a초록",
                "§7승리시 14배",
                "§7현재 베팅금액: §e" + (gameManager.getPlayerBet(player.getUniqueId()) != null ?
                        String.format("%,d", (long)gameManager.getPlayerBet(player.getUniqueId()).getAmount()) : "설정 필요")
        );

        inv.setItem(11, redButton);
        inv.setItem(13, blackButton);
        inv.setItem(15, greenButton);

        // 돌아가기 버튼
        ItemStack backButton = createGuiItem(
                Material.BARRIER,
                "§c돌아가기",
                "§7메인 메뉴로 돌아갑니다"
        );
        inv.setItem(26, backButton);

        player.openInventory(inv);
    }

    public void openRouletteAnimation(Player player, RouletteColor result) {
        // GUI 초기 설정
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, "§6§l룰렛 게임");
        setGUIBorder(inv);
        player.openInventory(inv);

        // 애니메이션 시작
        RouletteAnimation animation = new RouletteAnimation(plugin);
        animation.playRouletteAnimation(player, result, () -> {
            // 애니메이션 완료 후 실행될 코드
            gameManager.determineResult(player, result);
        });
    }
    private void setGUIBorder(Inventory inv) {
        // GUI 테두리 설정
        for(int i = 0; i < GUI_SIZE; i++) {
            if(i < 9 || i >= 36 || i % 9 == 0 || i % 9 == 8) {
                ItemStack borderItem = createBorderItem(i);
                inv.setItem(i, borderItem);
            }
        }
    }
    private ItemStack createBorderItem(int position) {
        Material material;
        String name;
        if(position % 3 == 0) {
            material = Material.RED_STAINED_GLASS_PANE;
            name = "§c빨강";
        } else if(position % 3 == 1) {
            material = Material.BLACK_STAINED_GLASS_PANE;
            name = "§0검정";
        } else {
            material = Material.LIME_STAINED_GLASS_PANE;
            name = "§a초록";
        }
        return createGuiItem(material, name);
    }

    private int getAnimationPosition(int pos) {
        // GUI 테두리를 따라 도는 위치 계산
        int[] path = {1,2,3,4,5,6,7,16,25,34,43,42,41,40,39,38,37,28,19,10};
        return path[pos % path.length];
    }

    private ItemStack createColorItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
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