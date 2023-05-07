package de.oliver.fancycoins;

import de.oliver.fancycoins.commands.CoinsCMD;
import de.oliver.fancycoins.commands.FancyCoinsCMD;
import de.oliver.fancycoins.commands.PayCMD;
import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.VersionFetcher;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyCoins extends JavaPlugin {

    private static FancyCoins instance;
    private final VersionFetcher versionFetcher;
    private boolean usingVault;

    public FancyCoins(){
        instance = this;
        loadDependencies();
        versionFetcher = new VersionFetcher("https://api.modrinth.com/v2/project/fancycoins/version", "https://modrinth.com/plugin/fancycoins/versions");
    }

    @Override
    public void onEnable() {
        FancyLib.setPlugin(this);

        new Thread(() -> {
            ComparableVersion newestVersion = versionFetcher.getNewestVersion();
            ComparableVersion currentVersion = new ComparableVersion(getDescription().getVersion());
            if(newestVersion == null){
                getLogger().warning("Could not fetch latest plugin version");
            } else if (newestVersion.compareTo(currentVersion) > 0) {
                getLogger().warning("-------------------------------------------------------");
                getLogger().warning("You are not using the latest version the FancyPerks plugin.");
                getLogger().warning("Please update to the newest version (" + newestVersion + ").");
                getLogger().warning(versionFetcher.getDownloadUrl());
                getLogger().warning("-------------------------------------------------------");
            }
        }).start();

        PluginManager pluginManager = Bukkit.getPluginManager();

        String serverSoftware = Bukkit.getServer().getName();
        if(!serverSoftware.equals("Paper")){
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("It is recommended to use Paper as server software.");
            getLogger().warning("Because you are not using paper, the plugin");
            getLogger().warning("might not work correctly.");
            getLogger().warning("--------------------------------------------------");
        }

        usingVault = pluginManager.getPlugin("Vault") != null;

        getCommand("FancyCoins").setExecutor(new FancyCoinsCMD());
        getCommand("Coins").setExecutor(new CoinsCMD());
        getCommand("Pay").setExecutor(new PayCMD());

    }

    @Override
    public void onDisable() {

    }

    private void loadDependencies(){
        BukkitLibraryManager paperLibraryManager = new BukkitLibraryManager(instance);
        paperLibraryManager.addJitPack();

        boolean hasFancyLib;
        try{
            Class.forName("de.oliver.fancylib.FancyLib");
            hasFancyLib = true;
        } catch (ClassNotFoundException e){
            hasFancyLib = false;
        }

        if(!hasFancyLib){
            getLogger().info("Loading FancyLib");
            Library fancyLib = Library.builder()
                    .groupId("com{}github{}FancyMcPlugins")
                    .artifactId("FancyLib")
                    .version("25458c9930")
                    .build();
            paperLibraryManager.loadLibrary(fancyLib);
        }
    }

    public static FancyCoins getInstance() {
        return instance;
    }

    public VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    public boolean isUsingVault() {
        return usingVault;
    }
}
