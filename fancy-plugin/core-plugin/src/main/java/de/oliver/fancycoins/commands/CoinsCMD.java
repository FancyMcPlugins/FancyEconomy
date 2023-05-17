package de.oliver.fancycoins.commands;

import de.oliver.fancycoins.FancyCoins;
import de.oliver.fancylib.MessageHelper;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.Subcommand;
import dev.jorel.commandapi.annotations.arguments.ADoubleArgument;
import dev.jorel.commandapi.annotations.arguments.AOfflinePlayerArgument;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

@Command("coins")
@Permission("fancycoins.manage")
public class CoinsCMD {

    private static FancyCoins fancyCoins = null;

    public CoinsCMD(FancyCoins fancyCoins) {
        CoinsCMD.fancyCoins = fancyCoins;
    }

    @Default
    public static void info(CommandSender player) {
        MessageHelper.info(player, " --- FancyCoins Info ---");
        MessageHelper.info(player, "/coins increase <player> <vault_name> <count> - Increase a certain amount to a certain vault for a certain player");
        MessageHelper.info(player, "/coins decrease <player> <vault_name> <count> - Decrease a certain amount to a certain vault for a certain player");
        MessageHelper.info(player, "/coins top <vault_name> - Show top by vault");
        MessageHelper.info(player, "/coins balance <player> <vault_name> - Show player balance");
    }

    @Subcommand({"increase", "add"})
    @Permission("fancycoins.manage.increase")
    public static void increase(
            CommandSender player,
            @AOfflinePlayerArgument OfflinePlayer toPlayer,
            @AStringArgument String vault,
            @ADoubleArgument(min = 0.1) double count
    ) {
        fancyCoins.getVaultsManager().getVaults(toPlayer.getUniqueId())
                .stream()
                .filter(fancyVault -> fancyVault.getName().equals(vault))
                .findFirst()
                .ifPresentOrElse(fancyVault -> {
            fancyVault.setBalance(fancyVault.getBalance() + count);
            fancyCoins.getVaultsManager().updateFancyVault(toPlayer.getUniqueId(), fancyVault);
            MessageHelper.success(player, "Success added " + count);
        }, () -> MessageHelper.error(player, "Error when you run command"));
    }

    @Subcommand({"balance"})
    @Permission("fancycoins.balance.others")
    public static void balanceOthers(
            CommandSender player,
            @AOfflinePlayerArgument OfflinePlayer toPlayer,
            @AStringArgument String vault
    ) {
        fancyCoins.getVaultsManager().getVaults(toPlayer.getUniqueId()).stream().filter(fancyVault -> fancyVault.getName().equals(vault)).findFirst().ifPresentOrElse(fancyVault -> MessageHelper.success(player, toPlayer.getName() + " balance " + fancyVault.getName() + ": " + fancyVault.getBalance() + fancyVault.getSymbol()), () -> MessageHelper.error(player, "Player dont have " + vault));
    }

    @Subcommand({"top"})
    @Permission("fancycoins.top")
    public static void top(
            CommandSender player,
            @AStringArgument String vault
    ) {
        // TODO
    }

    @Subcommand({"decrease", "remove"})
    @Permission("fancycoins.manage.decrease")
    public static void decrease(
            CommandSender player,
            @AOfflinePlayerArgument OfflinePlayer toPlayer,
            @AStringArgument String vault,
            @ADoubleArgument(min = 0.1) double count
    ) {
        fancyCoins.getVaultsManager().getVaults(toPlayer.getUniqueId()).stream().filter(fancyVault -> fancyVault.getName().equals(vault)).findFirst().ifPresentOrElse(fancyVault -> {
            if (fancyVault.getBalance() >= count) {
                fancyVault.setBalance(fancyVault.getBalance() - count);
                fancyCoins.getVaultsManager().updateFancyVault(toPlayer.getUniqueId(), fancyVault);
                MessageHelper.success(player, "Success removed " + count);
            }
        }, () -> MessageHelper.error(player, "Error when you run command"));
    }
}
