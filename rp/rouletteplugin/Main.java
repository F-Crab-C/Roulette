package rp.rouletteplugin;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import rp.rouletteplugin.game.GameManager;
import rp.rouletteplugin.gui.RouletteGUI;
import rp.rouletteplugin.listener.GUIListener;

public class Main extends JavaPlugin {
    private static Main instance;
    private Economy econ;
    private ProtocolManager protocolManager;
    private GameManager gameManager;
    private RouletteGUI rouletteGUI;

    @Override
    public void onEnable() {
        instance = this;

        // Vault 초기화
        if (!setupEconomy()) {
            getLogger().severe("Vault가 발견되지 않았습니다!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // ProtocolLib 초기화
        protocolManager = ProtocolLibrary.getProtocolManager();

        // 매니저 및 GUI 초기화
        gameManager = new GameManager(this);
        rouletteGUI = new RouletteGUI(this, gameManager);

        // 리스너 등록
        getServer().getPluginManager().registerEvents(new GUIListener(this, gameManager), this);

        // 명령어 등록 (추후 구현)
        getCommand("roulette").setExecutor(new RouletteCommand(this));

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
        econ = rsp.getProvider();
        return econ != null;
    }

    // Getter 메서드들
    public static Main getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return econ;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public RouletteGUI getRouletteGUI() {
        return rouletteGUI;
    }
}