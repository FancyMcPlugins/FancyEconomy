package de.oliver.fancycoins;

import de.oliver.fancycoins.commands.CoinsCMD;
import de.oliver.fancycoins.commands.FancyCoinsCMD;
import de.oliver.fancycoins.commands.PayCMD;
import de.oliver.fancycoins.scheduling.PaperFancyScheduler;
import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.VersionFetcher;
import de.oliver.scheduling.FancyScheduler;
import de.oliver.scheduling.FoliaFancyScheduler;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class FancyCoins extends JavaPlugin {

    private static FancyCoins instance;
    private final VersionFetcher versionFetcher;
    private boolean usingVault;
    private FancyScheduler fancyScheduler;

    public FancyCoins() {
        instance = this;
        //loadDependencies(); not need for paper and forks
        versionFetcher = new VersionFetcher("https://api.modrinth.com/v2/project/fancycoins/version", "https://modrinth.com/plugin/fancycoins/versions");
    }

    @Override
    public void onEnable() {
        FancyLib.setPlugin(this);

        if (isFolia()) {
            fancyScheduler = new FoliaFancyScheduler(instance);
        } else {
            fancyScheduler = new PaperFancyScheduler(instance);
        }

        fancyScheduler.runTaskAsynchronously(() -> {
            ComparableVersion newestVersion = versionFetcher.getNewestVersion();
            ComparableVersion currentVersion = new ComparableVersion(getDescription().getVersion());
            if (newestVersion == null) {
                getLogger().warning("Could not fetch latest plugin version");
            } else if (newestVersion.compareTo(currentVersion) > 0) {
                getLogger().warning("-------------------------------------------------------");
                getLogger().warning("You are not using the latest version the FancyPerks plugin.");
                getLogger().warning("Please update to the newest version (" + newestVersion + ").");
                getLogger().warning(versionFetcher.getDownloadUrl());
                getLogger().warning("-------------------------------------------------------");
            }
        });

        PluginManager pluginManager = Bukkit.getPluginManager();

        String serverSoftware = Bukkit.getServer().getName();
        if (serverSoftware.equals("Bukkit") || serverSoftware.equals("Spigot")) {
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Plugin support only Paper and its forks like Purpur or Folia.");
            getLogger().warning("Because you are not using paper, the plugin");
            getLogger().warning("might not work correctly.");
            getLogger().warning("--------------------------------------------------");
        }

        usingVault = pluginManager.getPlugin("Vault") != null;

        getServer().getCommandMap().register("fancycoins", new FancyCoinsCMD("fancycoins", "Command for manage plugin", "/fancycoins <reload, version>", Collections.emptyList()));
        getServer().getCommandMap().register("coins", new CoinsCMD("coins", "Command for check balance", "/coins", List.of("money", "bal", "balance")));
        getServer().getCommandMap().register("pay", new PayCMD("pay", "Command for send money", "/pay <player> <count>", Collections.emptyList()));
        //this.getCommand("FancyCoins").setExecutor(new FancyCoinsCMD());
        //this.getCommand("Coins").setExecutor(new CoinsCMD());
        //this.getCommand("Pay").setExecutor(new PayCMD());

    }

    @Override
    public void onDisable() {

    }

    //private void loadDependencies() {
    //    BukkitLibraryManager paperLibraryManager = new BukkitLibraryManager(instance);
    //    paperLibraryManager.addJitPack();
//
    //    boolean hasFancyLib;
    //    try {
    //        Class.forName("de.oliver.fancylib.FancyLib");
    //        hasFancyLib = true;
    //    } catch (ClassNotFoundException e) {
    //        hasFancyLib = false;
    //    }
//
    //    if (!hasFancyLib) {
    //        getLogger().info("Loading FancyLib");
    //        Library fancyLib = Library.builder()
    //                .groupId("com{}github{}FancyMcPlugins")
    //                .artifactId("FancyLib")
    //                .version("25458c9930")
    //                .build();
    //        paperLibraryManager.loadLibrary(fancyLib);
    //    }
    //}

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static FancyCoins getInstance() {
        return instance;
    }

    public FancyScheduler getFancyScheduler() {
        return fancyScheduler;
    }

    public VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    public boolean isUsingVault() {
        return usingVault;
    }
}
