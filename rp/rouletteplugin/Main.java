package rp.rouletteplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
    private static Economy econ = null;
    private RouletteManager rouletteManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!setupEconomy()) {
            getLogger().severe("Vault 플러그인을 찾을 수 없습니다. 플러그인을 비활성화합니다.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        rouletteManager = new RouletteManager(this);
        PluginCommand command = getCommand("roulette");
        if (command != null) {
            command.setExecutor(new RouletteCommand(this));
        }
        getServer().getPluginManager().registerEvents(new RouletteListener(this), this);
        getLogger().info("룰렛 플러그인이 활성화되었습니다.");
    }

    @Override
    public void onDisable() {
        getLogger().info("룰렛 플러그인이 비활성화되었습니다.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public RouletteManager getRouletteManager() {
        return rouletteManager;
    }
}
