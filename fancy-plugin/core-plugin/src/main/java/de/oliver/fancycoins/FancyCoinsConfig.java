package de.oliver.fancycoins;

import de.oliver.fancycoins.vaults.FancyVault;
import de.oliver.fancycoins.vaults.VaultRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class FancyCoinsConfig {
    public void reload() {

        FancyCoins.getInstance().reloadConfig();
        FileConfiguration config = FancyCoins.getInstance().getConfig();

        if (!config.isConfigurationSection("vaults")) {
            config.createSection("vault_default", Map.of(
                    "start_balance", 100.0,
                    "symbol", "$",
                    "default_currency", true
            ));
        }

        for (String key : config.getKeys(false)) {
            if (key.startsWith("vault_")) {
                ConfigurationSection section = config.getConfigurationSection(key);
                if (section != null) {
                    double start_balance = section.getDouble("start_balance", 100.0);
                    String symbol = section.getString("symbol", "$");
                    boolean default_currency = section.getBoolean("default_currency", false);
                    VaultRegistry.registerVault(new FancyVault(key, symbol, start_balance, default_currency));
                }
            }
        }

        FancyCoins.getInstance().saveConfig();
    }
}
