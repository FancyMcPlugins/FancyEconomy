package de.oliver.fancycoins.integrations;

import de.oliver.fancycoins.FancyCoins;
import de.oliver.fancycoins.currencies.Currency;
import de.oliver.fancycoins.currencies.CurrencyPlayer;
import de.oliver.fancycoins.currencies.CurrencyPlayerManager;
import de.oliver.fancycoins.currencies.CurrencyRegistry;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FancyCoinsPlaceholderExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "FancyCoins";
    }

    @Override
    public @NotNull String getAuthor() {
        return "OliverHD";
    }

    @Override
    public @NotNull String getVersion() {
        return FancyCoins.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        Currency defaultCurrency = CurrencyRegistry.getDefaultCurrency();

        // %FancyCoins_balance%
        if(params.equalsIgnoreCase("balance")){
            return defaultCurrency.format(currencyPlayer.getBalance(defaultCurrency));
        }

        // %FancyCoins_balance_<currency>%
        for (Currency currency : CurrencyRegistry.CURRENCIES) {
            if(params.equalsIgnoreCase("balance_" + currency.name())){
                return currency.format(currencyPlayer.getBalance(currency));
            }
        }

        return null;
    }
}
