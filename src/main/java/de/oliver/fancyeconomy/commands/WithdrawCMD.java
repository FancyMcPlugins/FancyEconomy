package de.oliver.fancyeconomy.commands;

import de.oliver.fancyeconomy.FancyEconomy;
import de.oliver.fancyeconomy.FancyEconomyConfig;
import de.oliver.fancyeconomy.currencies.Currency;
import de.oliver.fancyeconomy.currencies.CurrencyPlayer;
import de.oliver.fancyeconomy.currencies.CurrencyPlayerManager;
import de.oliver.fancyeconomy.currencies.CurrencyRegistry;
import de.oliver.fancylib.MessageHelper;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.arguments.ADoubleArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@Command("withdraw")
@Permission("fancyeconomy.withdraw")
public class WithdrawCMD {

    @Default
    public static void info(CommandSender player) {
        MessageHelper.info(player, " --- FancyEconomy Info ---");
        MessageHelper.info(player, "/withdraw <count> - Withdraw a certain amount of money");
    }

    @Default
    public static void withdraw(
            Player player,
            @ADoubleArgument double amount
    ) {
        Currency currency = CurrencyRegistry.getDefaultCurrency();

        if(!currency.isWithdrawable()){
            MessageHelper.error(player, "This currency is not withdrawable");
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        currencyPlayer.setUsername(player.getName());

        double minWithdrawAmount = FancyEconomy.getInstance().getFancyEconomyConfig().getMinWithdrawAmount();
        double maxWithdrawAmount = FancyEconomy.getInstance().getFancyEconomyConfig().getMaxWithdrawAmount();

        if(amount < minWithdrawAmount){
            MessageHelper.error(player, "The minimum withdraw amount is: " + currency.format(minWithdrawAmount));
            return;
        }

        if(amount > maxWithdrawAmount){
            MessageHelper.error(player, "The maximum withdraw amount is: " + currency.format(maxWithdrawAmount));
            return;
        }

        if(currencyPlayer.getBalance(currency) < amount){
            MessageHelper.error(player, "You don't have enough money");
            return;
        }

        ItemStack withdrawItem = currency.withdrawItem().construct(player, currency, amount);

        HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(withdrawItem);
        if(leftOver.size() > 0){
            MessageHelper.error(player, "You don't have enough space in your inventory");
            return;
        }

        currencyPlayer.removeBalance(currency, amount);

        MessageHelper.success(player, "Successfully withdraw " + currency.format(amount));
    }
}
