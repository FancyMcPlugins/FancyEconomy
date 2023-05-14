package de.oliver.fancycoins;

import de.oliver.fancycoins.vaults.FancyVault;
import de.oliver.fancycoins.vaults.VaultRegistry;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class VaultsManager {

    private final Map<UUID, List<FancyVault>> playerVaults;
    private final File playersConfig = new File(FancyCoins.getInstance().getDataFolder().getAbsolutePath() + "/players.yml");

    public List<FancyVault> getVaults(UUID playerUUID){
        return playerVaults.getOrDefault(playerUUID, new ArrayList<>());
    }

    public VaultsManager() {
        this.playerVaults = new HashMap<>();
    }

    public void loadFromConfig() {
        playerVaults.clear();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playersConfig);
        if (!config.isConfigurationSection("vaults")) return;

        for (String uuidStr : config.getConfigurationSection("vaults").getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            for (String vault : config.getConfigurationSection("vaults." + uuidStr).getKeys(false)) {
                FancyVault fancyVault = VaultRegistry.getFancyVaultByName(vault);
                if (fancyVault == null) {
                    continue;
                }
                double balance = config.getDouble("vaults." + uuidStr + "." + vault, fancyVault.getBalance());
                fancyVault.setBalance(balance);
                List<FancyVault> current = playerVaults.getOrDefault(uuid, new ArrayList<>());
                current.add(fancyVault);
                playerVaults.put(uuid, current);
            }
        }
    }

    public void updateFancyVault(UUID uuid, FancyVault fancyVault){
        List<FancyVault> vaults = getVaults(uuid);
        if(!vaults.contains(fancyVault)){
            vaults.add(fancyVault);
        }

        playerVaults.put(uuid, vaults);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playersConfig);
        config.set("vaults." + uuid + "." + fancyVault.getName(), fancyVault.getBalance());
        try {
            config.save(playersConfig);
        } catch (IOException e) {
            e.printStackTrace();
            MessageHelper.error(Bukkit.getConsoleSender(), "Could not save player config");
        }
    }

}
