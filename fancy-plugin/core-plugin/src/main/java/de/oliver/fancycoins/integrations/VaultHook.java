package de.oliver.fancycoins.integrations;

import com.google.common.util.concurrent.AtomicDouble;
import de.oliver.fancycoins.FancyCoins;
import de.oliver.fancycoins.vaults.FancyVault;
import de.oliver.fancycoins.vaults.VaultRegistry;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VaultHook implements Economy {

    private final FancyCoins fancyCoins;

    public VaultHook(FancyCoins fancyCoins) {
        this.fancyCoins = fancyCoins;
    }


    @Override
    public boolean isEnabled() {
        return fancyCoins.isEnabled();
    }

    @Override
    public String getName() {
        return "FancyCoins Economy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double v) {
        return v + VaultRegistry.ALL_VAULTS.stream().filter(FancyVault::isDefault_currency).findFirst().get().getSymbol();
    }

    @Override
    public String currencyNamePlural() {
        return currencyNameSingular();
    }

    @Override
    public String currencyNameSingular() {
        return VaultRegistry.ALL_VAULTS.stream().filter(FancyVault::isDefault_currency).findFirst().get().getSymbol();
    }

    @Override
    @Deprecated
    public boolean hasAccount(String s) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return !fancyCoins.getVaultsManager().getVaults(offlinePlayer.getUniqueId()).stream().filter(FancyVault::isDefault_currency).toList().isEmpty();
    }

    @Override
    @Deprecated
    public boolean hasAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return hasAccount(offlinePlayer);
    }

    @Override
    @Deprecated
    public double getBalance(String s) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        AtomicDouble result = new AtomicDouble();
        fancyCoins.getVaultsManager().getVaults(offlinePlayer.getUniqueId()).stream().filter(FancyVault::isDefault_currency).findFirst().ifPresentOrElse(fancyVault -> result.set(fancyVault.getBalance()), () -> {});
        return result.get();
    }

    @Override
    @Deprecated
    public double getBalance(String s, String s1) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }

    @Override
    @Deprecated
    public boolean has(String s, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        AtomicBoolean result = new AtomicBoolean(false);
        fancyCoins.getVaultsManager().getVaults(offlinePlayer.getUniqueId()).stream().filter(FancyVault::isDefault_currency).findFirst().ifPresentOrElse(fancyVault -> result.set(fancyVault.getBalance() >= v), () -> {});
        return result.get();
    }

    @Override
    @Deprecated
    public boolean has(String s, String s1, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return has(offlinePlayer, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        AtomicReference<EconomyResponse> result = new AtomicReference<>(new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "User cannot withdraw!"));
        if (has(offlinePlayer, v)) {
            fancyCoins.getVaultsManager().getVaults(offlinePlayer.getUniqueId()).stream().filter(FancyVault::isDefault_currency).findFirst().ifPresentOrElse(fancyVault -> {
                fancyVault.setBalance(fancyVault.getBalance() - v);
                fancyCoins.getVaultsManager().updateFancyVault(offlinePlayer.getUniqueId(), fancyVault);
                result.set(new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null));
            }, () -> result.set(new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "User cannot withdraw!")));
            return result.get();
        } else {
            return result.get();
        }
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer, v);
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        AtomicReference<EconomyResponse> result = new AtomicReference<>(new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "User cannot withdraw!"));
        fancyCoins.getVaultsManager().getVaults(offlinePlayer.getUniqueId()).stream().filter(FancyVault::isDefault_currency).findFirst().ifPresentOrElse(fancyVault -> {
            fancyVault.setBalance(fancyVault.getBalance() + v);
            fancyCoins.getVaultsManager().updateFancyVault(offlinePlayer.getUniqueId(), fancyVault);
            result.set(new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null));
        }, () -> result.set(new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Deposit error")));
        return result.get();
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer, v);
    }

    @Override
    @Deprecated
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "FancyCoins does not support bank accounts!");
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "FancyCoins does not support bank accounts!");
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "FancyCoins does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "FancyCoins does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "FancyCoins does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "FancyCoins does not support bank accounts!");
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "FancyCoins does not support bank accounts!");
    }

    @Override
    @Deprecated
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "FancyCoins does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "FancyCoins does not support bank accounts!");
    }

    @Override
    @Deprecated
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "FancyCoins does not support bank accounts!");
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "FancyCoins does not support bank accounts!");
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

}
