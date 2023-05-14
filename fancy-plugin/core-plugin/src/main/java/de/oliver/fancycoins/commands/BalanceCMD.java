package de.oliver.fancycoins.commands;

import dev.jorel.commandapi.annotations.Alias;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import org.bukkit.entity.Player;

@Command("balance")
@Alias({"money"})
public class BalanceCMD {

    @Default
    public static void balance(
            Player player
    ) {

    }

}
