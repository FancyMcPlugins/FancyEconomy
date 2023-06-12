package de.oliver.fancyeconomy.commands;

import de.oliver.fancyeconomy.FancyEconomy;
import de.oliver.fancyeconomy.currencies.Currency;
import de.oliver.fancyeconomy.currencies.CurrencyPlayer;
import de.oliver.fancyeconomy.currencies.CurrencyPlayerManager;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class CurrencyBaseCMD {

    private final Currency currency;

    public CurrencyBaseCMD(Currency currency) {
        this.currency = currency;
    }

    public void info(Player player) {
        MessageHelper.info(player, " --- FancyEconomy Info ---");
        MessageHelper.info(player, "/" + currency.name() + " balance - Shows your balance");
        MessageHelper.info(player, "/" + currency.name() + " balance <player> - Shows a player's balance");
        MessageHelper.info(player, "/" + currency.name() + " pay <player> <amount> - Pays money to a certain player");
        MessageHelper.info(player, "/" + currency.name() + " withdraw <amount> - Withdraw a certain amount of money");
        MessageHelper.info(player, "/" + currency.name() + " set <player> <amount> - Sets the balance of a certain player");
        MessageHelper.info(player, "/" + currency.name() + " add <player> <amount> - Adds money to a certain player");
        MessageHelper.info(player, "/" + currency.name() + " remove <player> <amount> - Removes money to a certain player");
    }

    public void balance(Player player){
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        double balance = currencyPlayer.getBalance(currency);

        MessageHelper.info(player, FancyEconomy.getInstance().getLang().get(
                "your-balance",
                "{balance}", currency.format(balance)
        ));
    }

    public void balance(
            Player player,
            String targetName
    ) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if(targetPlayer != null){
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if(uuid == null){
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get(
                    "player-not-found",
                    "{player}", targetName
                    ));
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);

        if(targetPlayer != null){
            currencyPlayer.setUsername(targetPlayer.getName());
        }

        double balance = currencyPlayer.getBalance(currency);

        MessageHelper.info(player, FancyEconomy.getInstance().getLang().get(
                "balance-others",
                "{player}", currencyPlayer.getUsername(),
                "{currency}", currency.format(balance)
        ));
    }

    public void pay(
            Player player,
            String targetName,
            double amount
    ) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if(targetPlayer != null){
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if(uuid == null){
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get(
                    "player-not-found",
                    "{player}", targetName
            ));
            return;
        }

        if(player.getUniqueId().equals(uuid)){
            MessageHelper.warning(player, FancyEconomy.getInstance().getLang().get("cannot-pay-yourself"));
            return;
        }

        CurrencyPlayer from = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        CurrencyPlayer to = CurrencyPlayerManager.getPlayer(uuid);
        from.setUsername(player.getName());

        if(targetPlayer != null){
            to.setUsername(targetPlayer.getName());
        }

        if(from.getBalance(currency) < amount){
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get("not-enough-money"));
            return;
        }

        from.removeBalance(currency, amount);
        to.addBalance(currency, amount);

        MessageHelper.success(player, FancyEconomy.getInstance().getLang().get(
                "paid-sender",
                "{amount}", currency.format(amount),
                "receiver", to.getUsername()
        ));

        if(targetPlayer != null){
            MessageHelper.info(player, FancyEconomy.getInstance().getLang().get(
                    "paid-receiver",
                    "{amount}", currency.format(amount),
                    "sender", from.getUsername()
            ));
        }
    }

    public void withdraw(
            Player player,
            double amount
    ) {
        if(!currency.isWithdrawable()){
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get("not-withdrawable"));
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        currencyPlayer.setUsername(player.getName());

        double minWithdrawAmount = FancyEconomy.getInstance().getFancyEconomyConfig().getMinWithdrawAmount();
        double maxWithdrawAmount = FancyEconomy.getInstance().getFancyEconomyConfig().getMaxWithdrawAmount();

        if(amount < minWithdrawAmount){
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get(
                    "min-withdrawable",
                    "amount", currency.format(minWithdrawAmount)
            ));
            return;
        }

        if(amount > maxWithdrawAmount){
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get(
                    "max-withdrawable",
                    "amount", currency.format(maxWithdrawAmount)
            ));
            return;
        }

        if(currencyPlayer.getBalance(currency) < amount){
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get("not-enough-money"));
            return;
        }

        ItemStack withdrawItem = currency.withdrawItem().construct(player, currency, amount);

        HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(withdrawItem);
        if(leftOver.size() > 0){
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get("no-inventory-space"));
            return;
        }

        currencyPlayer.removeBalance(currency, amount);

        MessageHelper.success(player, FancyEconomy.getInstance().getLang().get(
                "withdraw-success",
                "amount", currency.format(amount)
        ));
    }

    public void set(
            Player player,
            String targetName,
            double amount
    ) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if(targetPlayer != null){
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if(uuid == null){
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get(
                    "player-not-found",
                    "{player}", targetName
            ));
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.setBalance(currency, amount);

        MessageHelper.success(player, FancyEconomy.getInstance().getLang().get(
                "set-success",
                "{player}", currencyPlayer.getUsername(),
                "amount", currency.format(amount)
        ));
    }

    public void add(
            Player player,
            String targetName,
            double amount
    ) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if(targetPlayer != null){
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if(uuid == null){
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get(
                    "player-not-found",
                    "{player}", targetName
            ));
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.addBalance(currency, amount);

        MessageHelper.success(player, FancyEconomy.getInstance().getLang().get(
                "add-success",
                "{player}", currencyPlayer.getUsername(),
                "amount", currency.format(amount)
        ));
    }

    public void remove(
            Player player,
            String targetName,
            double amount
    ) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if(targetPlayer != null){
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if(uuid == null){
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get(
                    "player-not-found",
                    "{player}", targetName
            ));
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.removeBalance(currency, amount);

        MessageHelper.success(player, FancyEconomy.getInstance().getLang().get(
                "remove-success",
                "{player}", currencyPlayer.getUsername(),
                "amount", currency.format(amount)
        ));
    }
}
