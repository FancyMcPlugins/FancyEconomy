package de.oliver.fancycoins.vaults;

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

}
