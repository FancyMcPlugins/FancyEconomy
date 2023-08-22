package de.oliver.fancyeconomy.commands;

import de.oliver.fancyeconomy.FancyEconomy;
import de.oliver.fancyeconomy.currencies.Currency;
import de.oliver.fancyeconomy.currencies.CurrencyPlayer;
import de.oliver.fancyeconomy.currencies.CurrencyPlayerManager;
import de.oliver.fancyeconomy.currencies.CurrencyRegistry;
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

@Command("add")
@Permission("fancyeconomy.add")
public class AddCMD {

    @Default
    public static void info(CommandSender player) {
        MessageHelper.info(player, " --- FancyEconomy Info ---");
        MessageHelper.info(player, "/add <player> <count> - add money to a certain player");
    }

    @Default
    public static void add(
            CommandSender player,
            @AStringArgument String targetName,
            @ADoubleArgument(min = 0.1) double amount
    ) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer != null) {
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if (uuid == null) {
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get(
                    "player-not-found",
                    "player", targetName
            ));
            return;
        }

        Currency currency = CurrencyRegistry.getDefaultCurrency();

        CurrencyPlayer to = CurrencyPlayerManager.getPlayer(uuid);

        if (targetPlayer != null) {
            to.setUsername(targetPlayer.getName());
        }

        to.addBalance(currency, amount);

        MessageHelper.success(player, FancyEconomy.getInstance().getLang().get(
                "add-success",
                "amount", currency.format(amount),
                "receiver", to.getUsername()
        ));

        if (targetPlayer != null) {
            MessageHelper.info(player, FancyEconomy.getInstance().getLang().get(
                    "add-receiver",
                    "amount", currency.format(amount)
            ));
        }
    }

}
