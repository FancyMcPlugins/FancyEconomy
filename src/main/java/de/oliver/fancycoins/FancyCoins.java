package de.oliver.fancycoins;

import de.oliver.fancycoins.commands.BalanceCMD;
import de.oliver.fancycoins.commands.CurrencyBaseCMD;
import de.oliver.fancycoins.commands.FancyCoinsCMD;
import de.oliver.fancycoins.commands.PayCMD;
import de.oliver.fancycoins.currencies.Currency;
import de.oliver.fancycoins.currencies.CurrencyPlayer;
import de.oliver.fancycoins.currencies.CurrencyPlayerManager;
import de.oliver.fancycoins.currencies.CurrencyRegistry;
import de.oliver.fancycoins.integrations.FancyEconomy;
import de.oliver.fancycoins.listeners.PlayerJoinListener;
import de.oliver.fancycoins.utils.FoliaScheduler;
import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.VersionFetcher;
import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.serverSoftware.schedulers.BukkitScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.milkbowl.vault.economy.Economy;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;


public class FancyCoins extends JavaPlugin {

    private static FancyCoins instance;
    private final FancyScheduler scheduler;
    private final VersionFetcher versionFetcher;
    private final FancyCoinsConfig config;
    private FancyEconomy vaultEconomy;
    private Database database;
    private boolean usingVault;

    public FancyCoins() {
        instance = this;
        this.scheduler = ServerSoftware.isFolia()
                ? new FoliaScheduler(instance)
                : new BukkitScheduler(instance);
        config = new FancyCoinsConfig();
        versionFetcher = new VersionFetcher("https://api.modrinth.com/v2/project/fancycoins/version", "https://modrinth.com/plugin/fancycoins/versions");
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        FancyLib.setPlugin(this);

        scheduler.runTaskAsynchronously(() -> {
            ComparableVersion newestVersion = versionFetcher.getNewestVersion();
            ComparableVersion currentVersion = new ComparableVersion(getDescription().getVersion());
            if (newestVersion == null) {
                getLogger().warning("Could not fetch latest plugin version");
            } else if (newestVersion.compareTo(currentVersion) > 0) {
                getLogger().warning("-------------------------------------------------------");
                getLogger().warning("You are not using the latest version the FancyCoins plugin.");
                getLogger().warning("Please update to the newest version (" + newestVersion + ").");
                getLogger().warning(versionFetcher.getDownloadUrl());
                getLogger().warning("-------------------------------------------------------");
            }
        });

        if (!ServerSoftware.isPaper()) {
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Plugin support Paper and its forks like Purpur or Folia.");
            getLogger().warning("Because you are using Bukkit or Spigot,");
            getLogger().warning("the plugin might not work correctly.");
            getLogger().warning("--------------------------------------------------");
        }

        config.reload();

        database = config.getDatabase();
        database.connect();
        createDatabaseTables();

        CurrencyPlayerManager.loadPlayersFromDatabase();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), instance);

        registerCommands();

        usingVault = Bukkit.getPluginManager().isPluginEnabled("Vault");
        if(usingVault){
            vaultEconomy = new FancyEconomy(CurrencyRegistry.getDefaultCurrency());
            getServer().getServicesManager().register(Economy.class, vaultEconomy, instance, ServicePriority.Normal);
        }

        scheduler.runTaskTimerAsynchronously(60, 60*5, () -> {
            for (CurrencyPlayer player : CurrencyPlayerManager.getAllPlayers()) {
                player.save(false);
            }
        });
    }

    @Override
    public void onDisable() {
        for (CurrencyPlayer player : CurrencyPlayerManager.getAllPlayers()) {
            player.save(true);
        }

        database.close();
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(instance).silentLogs(true));
    }

    private void registerCommands(){
        CommandAPI.registerCommand(FancyCoinsCMD.class);
        CommandAPI.registerCommand(PayCMD.class);
        CommandAPI.registerCommand(BalanceCMD.class);

        for (Currency currency : CurrencyRegistry.CURRENCIES) {
            CurrencyBaseCMD baseCMD = new CurrencyBaseCMD(currency);

            // info command
            new CommandAPICommand(currency.name())
                    .withPermission("fancycoins." + currency.name())
                    .executesPlayer((sender, args) -> {
                        baseCMD.info(sender);
                    })
                    .register();

            // balance command
            new CommandAPICommand(currency.name())
                    .withPermission("fancycoins." + currency.name())
                    .withArguments(
                            new MultiLiteralArgument("balance")
                                    .setListed(false)
                    )
                    .executesPlayer((sender, args) -> {
                        baseCMD.balance(sender);
                    })
                    .register();

            // balance others command
            new CommandAPICommand(currency.name())
                    .withPermission("fancycoins." + currency.name())
                    .withArguments(
                            new MultiLiteralArgument("balance")
                                    .setListed(false)
                    )
                    .withArguments(new StringArgument("targetName"))
                    .executesPlayer((sender, args) -> {
                        baseCMD.balance(sender, (String) args.get(0));
                    })
                    .register();

            // pay command
            new CommandAPICommand(currency.name())
                    .withPermission("fancycoins." + currency.name())
                    .withArguments(
                            new MultiLiteralArgument("pay")
                                    .setListed(false)
                    )
                    .withArguments(new StringArgument("targetName"), new DoubleArgument("amount", 0.01))
                    .executesPlayer((sender, args) -> {
                        baseCMD.pay(sender, (String) args.get(0), (Double) args.get(1));
                    })
                    .register();

            // set command
            new CommandAPICommand(currency.name())
                    .withPermission("fancycoins." + currency.name())
                    .withArguments(
                            new MultiLiteralArgument("set")
                                    .setListed(false)
                    )
                    .withArguments(new StringArgument("targetName"), new DoubleArgument("amount", 0.01))
                    .executesPlayer((sender, args) -> {
                        baseCMD.set(sender, (String) args.get(0), (Double) args.get(1));
                    })
                    .register();

            // add command
            new CommandAPICommand(currency.name())
                    .withPermission("fancycoins." + currency.name())
                    .withArguments(
                            new MultiLiteralArgument("add")
                                    .setListed(false)
                    )
                    .withArguments(new StringArgument("targetName"), new DoubleArgument("amount", 0.01))
                    .executesPlayer((sender, args) -> {
                        baseCMD.add(sender, (String) args.get(0), (Double) args.get(1));
                    })
                    .register();

            // remove command
            new CommandAPICommand(currency.name())
                    .withPermission("fancycoins." + currency.name())
                    .withArguments(
                            new MultiLiteralArgument("remove")
                                    .setListed(false)
                    )
                    .withArguments(new StringArgument("targetName"), new DoubleArgument("amount", 0.01))
                    .executesPlayer((sender, args) -> {
                        baseCMD.remove(sender, (String) args.get(0), (Double) args.get(1));
                    })
                    .register();
        }
    }

    private void createDatabaseTables(){
        database.executeNonQuery("""
                CREATE TABLE IF NOT EXISTS players(
                    uuid VARCHAR(255) PRIMARY KEY,
                    username VARCHAR(255)
                )""");

        database.executeNonQuery("""
                CREATE TABLE IF NOT EXISTS balances(
                    uuid VARCHAR(255),
                    currency VARCHAR(255),
                    balance DOUBLE,
                                
                    PRIMARY KEY(uuid, currency),
                    FOREIGN KEY (uuid) REFERENCES players(uuid)
                )""");
    }

    public FancyScheduler getScheduler() {
        return scheduler;
    }

    public VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    public FancyCoinsConfig getFancyCoinsConfig() {
        return config;
    }

    public Database getDatabase() {
        return database;
    }

    public boolean isUsingVault() {
        return usingVault;
    }

    public static FancyCoins getInstance() {
        return instance;
    }
}
