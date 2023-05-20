package de.oliver.fancycoins.commands;

import de.oliver.fancycoins.currencies.Currency;
import de.oliver.fancycoins.currencies.CurrencyPlayer;
import de.oliver.fancycoins.currencies.CurrencyPlayerManager;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CurrencyBaseCMD {

    private final Currency currency;

    public CurrencyBaseCMD(Currency currency) {
        this.currency = currency;
    }

    public void info(Player player) {
        MessageHelper.info(player, " --- FancyCoins Info ---");
        MessageHelper.info(player, "/" + currency.name() + " balance - Shows your balance");
        MessageHelper.info(player, "/" + currency.name() + " balance <player> - Shows a player's balance");
        MessageHelper.info(player, "/" + currency.name() + " pay <player> <amount> - Pays money to a certain player");
        MessageHelper.info(player, "/" + currency.name() + " add <player> <amount> - Adds money to a certain player");
        MessageHelper.info(player, "/" + currency.name() + " remove <player> <amount> - Removes money to a certain player");
    }

    public void balance(Player player){
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        double balance = currencyPlayer.getBalance(currency);

        MessageHelper.info(player, "Your balance: " + currency.format(balance));
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
            MessageHelper.error(player, "Could not find target player: '" + targetName + "'");
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.setUsername(targetName);

        double balance = currencyPlayer.getBalance(currency);

        MessageHelper.info(player, currencyPlayer.getUsername() + "'s balance: " + currency.format(balance));
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
            MessageHelper.error(player, "Could not find target player: '" + targetName + "'");
            return;
        }

        if(player.getUniqueId().equals(uuid)){
            MessageHelper.warning(player, "You cannot send money to yourself");
            return;
        }

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
            MessageHelper.error(player, "Could not find target player: '" + targetName + "'");
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.setBalance(currency, amount);

        MessageHelper.success(player, "Successfully set " + currencyPlayer.getUsername() + " balance to " + currency.format(amount));
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
            MessageHelper.error(player, "Could not find target player: '" + targetName + "'");
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.addBalance(currency, amount);

        MessageHelper.success(player, "Successfully added " + currency.format(amount) + " to " + currencyPlayer.getUsername());
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
            MessageHelper.error(player, "Could not find target player: '" + targetName + "'");
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.removeBalance(currency, amount);

        MessageHelper.success(player, "Successfully removed " + currency.format(amount) + " from " + currencyPlayer.getUsername());
    }
}
