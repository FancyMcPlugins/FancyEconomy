package de.oliver.fancycoins.vaults;

import de.oliver.fancycoins.FancyCoins;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VaultRegistry {

    public static final List<FancyVault> ALL_VAULTS = new ArrayList<>();

    public static void registerVault(FancyVault fancyVault) {
        ALL_VAULTS.add(fancyVault);
    }

    public static FancyVault getFancyVaultByName(String name) {
        for (FancyVault vault : ALL_VAULTS) {
            if (vault.getName().equalsIgnoreCase(name)) {
                return vault;
            }
        }

        return null;
    }

    public static List<FancyVault> getDefaultVaults() {
        return ALL_VAULTS.stream().filter(FancyVault::isDefault_currency).toList();
    }

    public static List<FancyVault> getDefaultVaultsByPlayer(Player player) {
        return FancyCoins.getInstance().getVaultsManager().getVaults(player.getUniqueId()).stream().filter(FancyVault::isDefault_currency).toList();
    }

}
