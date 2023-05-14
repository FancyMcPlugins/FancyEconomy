package de.oliver.fancycoins.commands;

import de.oliver.fancylib.MessageHelper;
import dev.jorel.commandapi.annotations.*;
import dev.jorel.commandapi.annotations.arguments.ADoubleArgument;
import dev.jorel.commandapi.annotations.arguments.APlayerArgument;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Command("coins")
@Permission("fancycoins.manage")
public class CoinsCMD {

    static final List<String> availableVaults = new ArrayList<>();

    @Default
    public static void info(Player player) {
        MessageHelper.success(player, " --- FancyCoins Info ---");
        MessageHelper.success(player, "/coins increase <player> <vault_name> <count> - Increase a certain amount to a certain vault for a certain player");
        MessageHelper.success(player, "/coins decrease <player> <vault_name> <count> - Decrease a certain amount to a certain vault for a certain player");
    }

    @Subcommand({"increase", "add"})
    @Permission("fancycoins.manage.increase")
    public static void increase(
            Player player,
            @APlayerArgument Player toPlayer,
            String vault,
            @ADoubleArgument(min = 0.1) double count
    ) {

    }

    @Subcommand({"balance"})
    @Permission("fancycoins.balance.others")
    public static void balanceOthers(
            Player player,
            @APlayerArgument Player toPlayer,
            String vault
    ) {

    }

    @Subcommand({"decrease", "remove"})
    @Permission("fancycoins.manage.decrease")
    public static void decrease(
            Player player,
            @APlayerArgument Player toPlayer,
            String vault,
            @ADoubleArgument(min = 0.1) double count
    ) {

    }
}
