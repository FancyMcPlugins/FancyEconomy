package de.oliver.fancycoins.commands;

import de.oliver.fancycoins.FancyCoins;
import de.oliver.fancycoins.vaults.FancyVault;
import de.oliver.fancylib.MessageHelper;
import dev.jorel.commandapi.annotations.Alias;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import org.bukkit.entity.Player;

@Command("balance")
@Permission("fancycoins.balance")
@Alias({"money"})
public class BalanceCMD {

    private static FancyCoins fancyCoins = null;

    public BalanceCMD(FancyCoins fancyCoins) {
        BalanceCMD.fancyCoins = fancyCoins;
    }

    @Default
    public static void info(Player player) {
        MessageHelper.info(player, " --- FancyCoins Info ---");
        MessageHelper.info(player, "/balance - Show default balance");
        MessageHelper.info(player, "/balance <vault_name> - Show balance by vault_name");
    }

    @Default
    public static void balance(Player player) {
       fancyCoins.getVaultsManager().getVaults(player.getUniqueId())
               .stream()
               .filter(FancyVault::isDefault_currency)
               .findFirst()
               .ifPresentOrElse(
                       fancyVault -> MessageHelper.success(player, "Balance: " + fancyVault.getBalance() + fancyVault.getSymbol()),
                       () -> MessageHelper.error(player, "Player not registered"));
    }

    @Default
    @Permission("fancycoins.balance.vaults")
    public static void balance(Player player, @AStringArgument String vault) {
        fancyCoins.getVaultsManager().getVaults(player.getUniqueId())
                .stream()
                .filter(fancyVault -> fancyVault.getName().equals(vault))
                .findFirst()
                .ifPresentOrElse(
                        fancyVault -> MessageHelper.success(player, fancyVault.getName() + " balance: " + fancyVault.getBalance() + fancyVault.getSymbol()),
                        () -> MessageHelper.error(player, "Player dont have " + vault));
    }

}
