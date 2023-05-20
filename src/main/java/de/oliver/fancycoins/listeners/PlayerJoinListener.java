package de.oliver.fancycoins.listeners;

import de.oliver.fancycoins.FancyCoins;
import de.oliver.fancycoins.currencies.CurrencyPlayer;
import de.oliver.fancycoins.currencies.CurrencyPlayerManager;
import de.oliver.fancylib.MessageHelper;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        if (event.getPlayer().hasPermission("fancycoins.admin.notification")) {
            FancyCoins.getInstance().getScheduler().runTaskAsynchronously(() -> {
                ComparableVersion newestVersion = FancyCoins.getInstance().getVersionFetcher().getNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyCoins.getInstance().getDescription().getVersion());
                if (newestVersion != null && newestVersion.compareTo(currentVersion) > 0) {
                    MessageHelper.warning(event.getPlayer(), "You are using an outdated version of the FancyCoins Plugin");
                    MessageHelper.warning(event.getPlayer(), "[!] Please download the newest version (" + newestVersion + "): <click:open_url:'" + FancyCoins.getInstance().getVersionFetcher().getDownloadUrl() + "'><u>click here</u></click>");
                }
            });
        }

        // load currency player
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(p.getUniqueId());
        currencyPlayer.setUsername(p.getName());
    }
}
