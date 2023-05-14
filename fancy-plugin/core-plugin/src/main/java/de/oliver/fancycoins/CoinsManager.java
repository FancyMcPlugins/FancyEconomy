package de.oliver.fancycoins;

import de.oliver.fancycoins.vaults.FancyVault;
import de.oliver.fancycoins.vaults.VaultRegistry;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class CoinsManager {

    private final File playersConfig = new File(FancyCoins.getInstance().getDataFolder().getAbsolutePath() + "/players.yml");


    public void loadFromConfig() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playersConfig);
        if (!config.isConfigurationSection("vaults")) return;

        for (String uuidStr : config.getConfigurationSection("vaults").getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            for (String vault : config.getConfigurationSection("vaults." + uuidStr).getKeys(false)) {
                FancyVault fancyVault = VaultRegistry.getFancyVaultByName(vault);
                if (fancyVault == null) {
                    continue;
                }
                double balance = config.getDouble("vaults." + uuidStr + "." + vault, 0.0);

            }
        }
    }

}
