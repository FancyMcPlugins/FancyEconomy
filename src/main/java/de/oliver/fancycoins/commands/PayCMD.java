package de.oliver.fancycoins.commands;

import de.oliver.fancycoins.FancyCoins;
import de.oliver.fancycoins.currencies.Currency;
import de.oliver.fancycoins.currencies.CurrencyPlayer;
import de.oliver.fancycoins.currencies.CurrencyPlayerManager;
import de.oliver.fancycoins.currencies.CurrencyRegistry;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.UUIDFetcher;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.ADoubleArgument;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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
        MessageHelper.info(player, "/pay <player> <count> - Pay money to a certain player");
    }

    @Default
    public static void pay(
            Player player,
            @AStringArgument String targetName,
            @ADoubleArgument(min = 0.1) double amount
    ) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if(targetPlayer != null){
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if(uuid == null){
            MessageHelper.error(player, "Could not find target player: '" + targetName + "'");
            return;
        }

        if(player.getUniqueId().equals(uuid)){
            MessageHelper.warning(player, "You cannot send money to yourself");
            return;
        }

        Currency currency = CurrencyRegistry.getDefaultCurrency();

        CurrencyPlayer from = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        CurrencyPlayer to = CurrencyPlayerManager.getPlayer(uuid);
        to.setUsername(targetName);

        if(from.getBalance(currency) < amount){
            MessageHelper.error(player, "You don't enough money");
            return;
        }

        from.removeBalance(currency, amount);
        to.addBalance(currency, amount);

        MessageHelper.success(player, "Successfully paid " + currency.format(amount) + " to " + to.getUsername());
        if(targetPlayer != null){
            MessageHelper.info(targetPlayer, "Received " + currency.format(amount) + " from " + from.getUsername());
        }
    }

}
