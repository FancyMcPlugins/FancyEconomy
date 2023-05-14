package de.oliver.fancycoins.commands;

import de.oliver.fancycoins.FancyCoins;
import de.oliver.fancylib.MessageHelper;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.Subcommand;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.entity.Player;

@Command("fancycoins")
@Permission("fancycoins.admin")
public class FancyCoinsCMD {

    @Default
    public static void info(Player player) {
        MessageHelper.success(player, " --- FancyCoins Info ---");
        MessageHelper.success(player, "/fancycoins reload - plugin config reload");
        MessageHelper.success(player, "/fancycoins version - checks for a new version of the plugin");
    }

    @Subcommand("version")
    @Permission("fancycoins.admin.version")
    public static void version(Player player) {
        MessageHelper.info(player, "<i>Checking version, please wait...</i>");
        FancyCoins.getInstance().getScheduler().runTaskAsynchronously(() -> {
            ComparableVersion newestVersion = FancyCoins.getInstance().getVersionFetcher().getNewestVersion();
            ComparableVersion currentVersion = new ComparableVersion(FancyCoins.getInstance().getDescription().getVersion());
            if (newestVersion == null) {
                MessageHelper.error(player, "Could not find latest version");
            } else if (newestVersion.compareTo(currentVersion) > 0) {
                MessageHelper.warning(player, "You are using an outdated version of the FancyCoins Plugin");
                MessageHelper.warning(player, "[!] Please download the newest version (" + newestVersion + "): <click:open_url:'" + FancyCoins.getInstance().getVersionFetcher().getDownloadUrl() + "'><u>click here</u></click>");
            } else {
                MessageHelper.success(player, "You are using the latest version of the FancyCoins Plugin (" + currentVersion + ")");
            }
        });
    }

    @Subcommand("reload")
    @Permission("fancycoins.admin.reload")
    public static void reload(Player player) {
        FancyCoins.getInstance().getFancyCoinsConfig().reload();
        MessageHelper.success(player, "Reloaded the config");
    }
}