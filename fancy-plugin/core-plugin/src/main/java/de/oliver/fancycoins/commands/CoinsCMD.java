package de.oliver.fancycoins.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CoinsCMD extends Command {
    public CoinsCMD(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    //@Override
    //public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    //    if(args.length == 1){
    //        return Stream.of("set", "give", "take", "top").filter(input -> input.startsWith(args[0].toLowerCase())).toList();
    //    }
//
    //    return null;
    //}
//
    //@Override
    //public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    //    // set, give, take only with permission
    //    // no args - show current coins amount
//
    //    return false;
    //}

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        return false;
    }
}
