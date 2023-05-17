package de.oliver.fancycoins.commands;

import de.oliver.fancycoins.FancyCoins;
import de.oliver.fancycoins.vaults.FancyVault;
import de.oliver.fancycoins.vaults.VaultRegistry;
import de.oliver.fancylib.MessageHelper;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.ADoubleArgument;
import dev.jorel.commandapi.annotations.arguments.APlayerArgument;
import org.bukkit.entity.Player;

import java.util.List;

@Command("pay")
@Permission("fancycoins.pay")
public class PayCMD {

    private static FancyCoins fancyCoins = null;

    public PayCMD(FancyCoins fancyCoins) {
        PayCMD.fancyCoins = fancyCoins;
    }

    @Default
    public static void info(Player player) {
        MessageHelper.info(player, " --- FancyCoins Info ---");
        MessageHelper.info(player, "/pay <player> <count> - Send money to certain player (from default vault)");
    }

    @Default
    public static void pay(
            Player player,
            @APlayerArgument Player toPlayer,
            @ADoubleArgument(min = 0.1) double count
    ) {
        List<FancyVault> defaultVaults = VaultRegistry.getDefaultVaultsByPlayer(player);
        List<FancyVault> otherDefaultVaults = VaultRegistry.getDefaultVaultsByPlayer(toPlayer);
        if (!defaultVaults.isEmpty() && !otherDefaultVaults.isEmpty()) {
            defaultVaults.forEach(fancyVault -> {
                FancyVault otherVault = otherDefaultVaults
                        .stream()
                        .filter(otherFancyVault -> fancyVault.getName().equals(otherFancyVault.getName()))
                        .findFirst()
                        .get();
                otherVault.setBalance(otherVault.getBalance() + count);
                fancyVault.setBalance(fancyVault.getBalance() - count);
            });
            MessageHelper.success(player, "You send " + count + " to " + toPlayer.getName());
            MessageHelper.success(toPlayer, "You received " + count + " by " + player.getName());
        }
    }

}
