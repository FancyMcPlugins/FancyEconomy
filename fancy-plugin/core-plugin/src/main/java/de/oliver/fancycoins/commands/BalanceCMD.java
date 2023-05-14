package de.oliver.fancycoins.commands;

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

    @Default
    public static void balance(
            Player player
    ) {

    }

    @Default
    @Permission("fancycoins.balance.vaults")
    public static void balance(
            Player player,
            @AStringArgument String vault
    ) {

    }

}
