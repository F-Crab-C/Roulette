package rp.rouletteplugin;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import rp.rouletteplugin.command.RouletteCommand;
import rp.rouletteplugin.game.GameManager;
import rp.rouletteplugin.gui.RouletteGUI;
import rp.rouletteplugin.gui.GUIListener;

public class Main extends JavaPlugin {
    private Economy economy;
    private GameManager gameManager;
    private RouletteGUI rouletteGUI;

    @Override
    public void onEnable() {
        // config.yml 생성
        saveDefaultConfig();

        // Vault 초기화
        if (!setupEconomy()) {
            getLogger().severe("Vault 플러그인을 찾을 수 없습니다!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 매니저 및 GUI 초기화
        this.gameManager = new GameManager(this);
        this.rouletteGUI = new RouletteGUI(this, gameManager);

        // 커맨드 등록
        getCommand("roulette").setExecutor(new RouletteCommand(this));
        getCommand("rl").setExecutor(new RouletteCommand(this));
        getCommand("룰렛").setExecutor(new RouletteCommand(this));

        // 이벤트 리스너 등록
        getServer().getPluginManager().registerEvents(new GUIListener(this, gameManager), this);

        getLogger().info("룰렛 플러그인이 활성화되었습니다!");
    }

    @Override
    public void onDisable() {
        getLogger().info("룰렛 플러그인이 비활성화되었습니다!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    // Getter 메서드들
    public Economy getEconomy() {
        return economy;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public RouletteGUI getRouletteGUI() {
        return rouletteGUI;
    }
}