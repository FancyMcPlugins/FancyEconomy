package de.oliver.fancycoins.commands;

import de.oliver.fancycoins.FancyCoins;
import de.oliver.fancycoins.vaults.FancyVault;
import de.oliver.fancycoins.vaults.VaultRegistry;
import de.oliver.fancylib.MessageHelper;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.ADoubleArgument;
import dev.jorel.commandapi.annotations.arguments.AOfflinePlayerArgument;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
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
    public static void info(CommandSender player) {
        MessageHelper.info(player, " --- FancyCoins Info ---");
        MessageHelper.info(player, "/pay <player> <count> - Send money to certain player (from default vault)");
        MessageHelper.info(player, "/pay <player> <count> <vault> - Send money to certain player");
    }

    @Default
    public static void pay(
            Player player,
            @AOfflinePlayerArgument OfflinePlayer toPlayer,
            @ADoubleArgument(min = 0.1) double count
    ) {
        if (player != toPlayer) {
            List<FancyVault> defaultVaults = VaultRegistry.getDefaultVaultsByPlayer(player);
            List<FancyVault> otherDefaultVaults = VaultRegistry.getDefaultVaultsByPlayer(toPlayer);
            if (!defaultVaults.isEmpty() && !otherDefaultVaults.isEmpty()) {
                defaultVaults.forEach(fancyVault -> otherDefaultVaults
                        .stream()
                        .filter(otherFancyVault -> fancyVault.getName().equals(otherFancyVault.getName()))
                        .findFirst()
                        .ifPresentOrElse(
                                otherVault -> {
                                    otherVault.setBalance(otherVault.getBalance() + count);
                                    fancyVault.setBalance(fancyVault.getBalance() - count);
                                    fancyCoins.getVaultsManager().updateFancyVault(toPlayer.getUniqueId(), otherVault);
                                    fancyCoins.getVaultsManager().updateFancyVault(player.getUniqueId(), fancyVault);
                                    MessageHelper.success(player, "You send " + count + fancyVault.getSymbol() + " to " + toPlayer.getName());
                                    MessageHelper.success(toPlayer.getPlayer(), "You received " + count + fancyVault.getSymbol() + " by " + player.getName());
                                }, () -> MessageHelper.error(player, "Error when you run command")
                        ));
            }
        } else {
            player.sendMessage(Component.text("You cant send it to " + player.getName()));
        }
    }

    @Default
    public static void pay(
            Player player,
            @AOfflinePlayerArgument OfflinePlayer toPlayer,
            @ADoubleArgument(min = 0.1) double count,
            @AStringArgument String vault
    ) {
        if (player != toPlayer) {
            fancyCoins.getVaultsManager().getVaults(player.getUniqueId()).stream().filter(fancyVault -> fancyVault.getName().equals(vault)).findFirst().ifPresentOrElse(
                    fancyVault -> fancyCoins.getVaultsManager().getVaults(toPlayer.getUniqueId()).stream().filter(otherFancyVault -> otherFancyVault.getName().equals(fancyVault.getName())).findFirst().ifPresentOrElse(
                            otherFancyVault -> {
                                if (player.hasPermission("fancycoins.pay." + vault)) {
                                    otherFancyVault.setBalance(otherFancyVault.getBalance() + count);
                                    fancyVault.setBalance(fancyVault.getBalance() - count);
                                    fancyCoins.getVaultsManager().updateFancyVault(toPlayer.getUniqueId(), otherFancyVault);
                                    fancyCoins.getVaultsManager().updateFancyVault(player.getUniqueId(), fancyVault);
                                    MessageHelper.success(player, "You send " + count + fancyVault.getSymbol() + " to " + toPlayer.getName());
                                    MessageHelper.success(toPlayer.getPlayer(), "You received " + count + fancyVault.getSymbol() + " by " + player.getName());
                                }
                            }, () -> MessageHelper.error(player, "Player " + toPlayer.getName() + " dont have " + vault)
                    ), () -> MessageHelper.error(player, "You dont have " + vault)
            );
        } else {
            player.sendMessage(Component.text("You cant send it to " + player.getName()));
        }
    }

}
