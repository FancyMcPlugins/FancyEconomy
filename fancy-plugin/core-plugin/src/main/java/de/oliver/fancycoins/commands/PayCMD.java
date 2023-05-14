package de.oliver.fancycoins.commands;

import de.oliver.fancylib.MessageHelper;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.ADoubleArgument;
import dev.jorel.commandapi.annotations.arguments.APlayerArgument;
import org.bukkit.entity.Player;

@Command("pay")
@Permission("fancycoins.pay")
public class PayCMD {

    @Default
    public static void info(Player player) {
        MessageHelper.success(player, " --- FancyCoins Info ---");
        MessageHelper.success(player, "/pay <player> <count> - Send money to certain player");
    }

    @Default
    public static void pay(
            Player player,
            @APlayerArgument Player toPlayer,
            @ADoubleArgument(min = 0.1) double count
    ) {

    }

}
