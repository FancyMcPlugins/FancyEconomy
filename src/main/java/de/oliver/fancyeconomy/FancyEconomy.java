package de.oliver.fancyeconomy;

import de.oliver.fancyeconomy.commands.*;
import de.oliver.fancyeconomy.currencies.Currency;
import de.oliver.fancyeconomy.currencies.CurrencyPlayer;
import de.oliver.fancyeconomy.currencies.CurrencyPlayerManager;
import de.oliver.fancyeconomy.currencies.CurrencyRegistry;
import de.oliver.fancyeconomy.integrations.FancyEconomyPlaceholderExpansion;
import de.oliver.fancyeconomy.integrations.FancyEconomyVault;
import de.oliver.fancyeconomy.listeners.PlayerJoinListener;
import de.oliver.fancylib.*;
import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.serverSoftware.FoliaScheduler;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.serverSoftware.schedulers.BukkitScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.milkbowl.vault.economy.Economy;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


public class FancyEconomy extends JavaPlugin {

    private static FancyEconomy instance;
    private final FancyScheduler scheduler;
    private final VersionFetcher versionFetcher;
    private final LanguageConfig lang;
    private final FancyEconomyConfig config;
    private FancyEconomyVault vaultEconomy;
    private Database database;
    private DistributedWorkload<CurrencyPlayer> saveWorkload;
    private boolean usingVault;
    private boolean usingPlaceholderAPI;

    public FancyEconomy() {
        instance = this;
        this.scheduler = ServerSoftware.isFolia()
                ? new FoliaScheduler(instance)
                : new BukkitScheduler(instance);
        lang = new LanguageConfig(instance);
        config = new FancyEconomyConfig();
        versionFetcher = new VersionFetcher("https://api.modrinth.com/v2/project/fancyeconomy/version", "https://modrinth.com/plugin/fancyeconomy/versions");
        saveWorkload = new DistributedWorkload<>(
                "FancyEconomy_save",
                player -> player.save(false),
                player -> false,
                5,
                true
        );
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
                getLogger().warning("You are not using the latest version the FancyEconomy plugin.");
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

        Metrics metrics = new Metrics(instance, 18569);

        lang.addDefaultLang("your-balance", "Your balance: {balance}");
        lang.load();

        config.reload();

        database = config.getDatabase();
        database.connect();
        createDatabaseTables();

        CurrencyPlayerManager.loadPlayersFromDatabase();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), instance);
        Currency.WithdrawItem.WithdrawItemClick.INSTANCE.register();

        registerCommands();

        usingVault = Bukkit.getPluginManager().isPluginEnabled("Vault");
        if(usingVault){
            vaultEconomy = new FancyEconomyVault(CurrencyRegistry.getDefaultCurrency());
            getServer().getServicesManager().register(Economy.class, vaultEconomy, instance, ServicePriority.Normal);
            getLogger().info("Registered Vault economy");
        }

        usingPlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        if(usingPlaceholderAPI){
            new FancyEconomyPlaceholderExpansion().register();
            getLogger().info("Registered PlaceholoderAPI expansion");
        }

        scheduler.runTaskTimerAsynchronously(60, 60*5, saveWorkload);

//        scheduler.runTaskTimerAsynchronously(60, 60*5, () -> {
//            for (CurrencyPlayer player : CurrencyPlayerManager.getAllPlayers()) {
//                player.save(false);
//            }
//        });
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
        CommandAPI.registerCommand(FancyEconomyCMD.class);
        CommandAPI.registerCommand(PayCMD.class);
        CommandAPI.registerCommand(BalanceCMD.class);
        CommandAPI.registerCommand(WithdrawCMD.class);

        ArgumentSuggestions<CommandSender> allPlayersSuggestion = ArgumentSuggestions.strings(commandSenderSuggestionInfo -> CurrencyPlayerManager.getAllPlayerNames());


        for (Currency currency : CurrencyRegistry.CURRENCIES) {
            CurrencyBaseCMD baseCMD = new CurrencyBaseCMD(currency);

            // info command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name())
                    .executesPlayer((sender, args) -> {
                        baseCMD.info(sender);
                    })
                    .register();

            // balance command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name())
                    .withArguments(
                            new MultiLiteralArgument(null, List.of("bal", "balance"))
                                    .setListed(false)
                    )
                    .executesPlayer((sender, args) -> {
                        baseCMD.balance(sender);
                    })
                    .register();

            // balance others command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name())
                    .withArguments(
                            new MultiLiteralArgument(null, List.of("bal", "balance"))
                                    .setListed(false)
                    )
                    .withArguments(new StringArgument("targetName").includeSuggestions(allPlayersSuggestion))
                    .executesPlayer((sender, args) -> {
                        baseCMD.balance(sender, (String) args.get(0));
                    })
                    .register();

            // pay command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name())
                    .withArguments(
                            new MultiLiteralArgument(null, List.of("pay"))
                                    .setListed(false)
                    )
                    .withArguments(new StringArgument("targetName").includeSuggestions(allPlayersSuggestion), new DoubleArgument("amount", 0.01))
                    .executesPlayer((sender, args) -> {
                        baseCMD.pay(sender, (String) args.get(0), (Double) args.get(1));
                    })
                    .register();

            // withdraw command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name())
                    .withArguments(
                            new MultiLiteralArgument(null, List.of("withdraw"))
                                    .setListed(false)
                    )
                    .withArguments(new DoubleArgument("amount"))
                    .executesPlayer((sender, args) -> {
                        baseCMD.withdraw(sender, (Double) args.get(0));
                    })
                    .register();

            // set command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name() + ".admin")
                    .withArguments(
                            new MultiLiteralArgument(null, List.of("set"))
                                    .setListed(false)
                    )
                    .withArguments(new StringArgument("targetName").includeSuggestions(allPlayersSuggestion), new DoubleArgument("amount", 0.01))
                    .executesPlayer((sender, args) -> {
                        baseCMD.set(sender, (String) args.get(0), (Double) args.get(1));
                    })
                    .register();

            // add command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name() + ".admin")
                    .withArguments(
                            new MultiLiteralArgument(null, List.of("add"))
                                    .setListed(false)
                    )
                    .withArguments(new StringArgument("targetName").includeSuggestions(allPlayersSuggestion), new DoubleArgument("amount", 0.01))
                    .executesPlayer((sender, args) -> {
                        baseCMD.add(sender, (String) args.get(0), (Double) args.get(1));
                    })
                    .register();

            // remove command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name() + ".admin")
                    .withArguments(
                            new MultiLiteralArgument(null, List.of("remove"))
                                    .setListed(false)
                    )
                    .withArguments(new StringArgument("targetName").includeSuggestions(allPlayersSuggestion), new DoubleArgument("amount", 0.01))
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

    public LanguageConfig getLang() {
        return lang;
    }

    public FancyEconomyConfig getFancyEconomyConfig() {
        return config;
    }

    public Database getDatabase() {
        return database;
    }

    public DistributedWorkload<CurrencyPlayer> getSaveWorkload() {
        return saveWorkload;
    }

    public boolean isUsingVault() {
        return usingVault;
    }

    public boolean isUsingPlaceholderAPI() {
        return usingPlaceholderAPI;
    }

    public static FancyEconomy getInstance() {
        return instance;
    }
}
