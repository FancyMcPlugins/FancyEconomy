package de.oliver.fancyeconomy.commands;

import de.oliver.fancyeconomy.currencies.Currency;
import de.oliver.fancyeconomy.currencies.CurrencyPlayer;
import de.oliver.fancyeconomy.currencies.CurrencyPlayerManager;
import de.oliver.fancyeconomy.currencies.CurrencyRegistry;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.UUIDFetcher;
import dev.jorel.commandapi.annotations.*;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Command("balance")
@Permission("fancyeconomy.balance")
@Alias({"bal"})
public class BalanceCMD {

    @Default
    public static void info(Player player) {
        MessageHelper.info(player, " --- FancyEconomy Info ---");
        MessageHelper.info(player, "/balance - Shows your balance");
        MessageHelper.info(player, "/balance <player> - Shows a player's balance");
    }

    @Default
    public static void balance(Player player) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        Currency currency = CurrencyRegistry.getDefaultCurrency();
        double balance = currencyPlayer.getBalance(currency);

        MessageHelper.info(player, "Your balance: " + currency.format(balance));
    }

    @Default
    @Permission("fancyeconomy.balance")
    public static void balance(
            Player player,
            @AStringArgument String targetName
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

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.setUsername(targetName);

        Currency currency = CurrencyRegistry.getDefaultCurrency();
        double balance = currencyPlayer.getBalance(currency);

        MessageHelper.info(player, currencyPlayer.getUsername() + "'s balance: " + currency.format(balance));
    }

}
