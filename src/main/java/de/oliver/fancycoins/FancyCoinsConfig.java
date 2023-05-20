package de.oliver.fancycoins;

import de.oliver.fancycoins.currencies.Currency;
import de.oliver.fancycoins.currencies.CurrencyRegistry;
import de.oliver.fancylib.ConfigHelper;
import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.databases.MySqlDatabase;
import de.oliver.fancylib.databases.SqliteDatabase;
import org.bukkit.configuration.file.FileConfiguration;


public class FancyCoinsConfig {

    private DatabaseType dbType;
    private String dbHost;
    private String dbPort;
    private String dbDatabase;
    private String dbUsername;
    private String dbPassword;
    private String dbFile;

    public void reload() {
        FancyCoins.getInstance().reloadConfig();
        FileConfiguration config = FancyCoins.getInstance().getConfig();

        /*
            Database
         */
        dbType = DatabaseType.getByIdentifier((String) ConfigHelper.getOrDefault(config, "database.type", "sqlite"));
        if(dbType == null){
            FancyCoins.getInstance().getLogger().warning("Invalid database type provided in config");
        }

        dbHost = (String) ConfigHelper.getOrDefault(config, "database.mysql.host", "localhost");
        dbPort = (String) ConfigHelper.getOrDefault(config, "database.mysql.port", "3306");
        dbDatabase = (String) ConfigHelper.getOrDefault(config, "database.mysql.database", "fancycoins");
        dbUsername = (String) ConfigHelper.getOrDefault(config, "database.mysql.username", "root");
        dbPassword = (String) ConfigHelper.getOrDefault(config, "database.mysql.password", "");
        dbFile = (String) ConfigHelper.getOrDefault(config, "database.sqlite.file_path", "database.db");
        dbFile = "plugins/FancyCoins/" + dbFile;


        /*
            Currencies
         */
        String defaultCurrencyName = (String) ConfigHelper.getOrDefault(config, "default_currency", "money");

        if (!config.isConfigurationSection("currencies")) {
            config.set("currencies.money.symbol", "$");
        }

        for (String name : config.getConfigurationSection("currencies").getKeys(false)) {
            String symbol = (String) ConfigHelper.getOrDefault(config, "currencies." + name + ".symbol", "$");
            Currency currency = new Currency(name, symbol);
            CurrencyRegistry.registerCurrency(currency);
        }

        Currency defaultCurrency = CurrencyRegistry.getCurrencyByName(defaultCurrencyName);
        if(defaultCurrency == null){
            FancyCoins.getInstance().getLogger().warning("Could not find default currency: '" + defaultCurrencyName + "'");
        } else {
            CurrencyRegistry.setDefaultCurrency(defaultCurrency);
            FancyCoins.getInstance().getLogger().info("Set default currency to: '" + defaultCurrency.name() + "'");
        }

        FancyCoins.getInstance().saveConfig();
    }

    public Database getDatabase(){
        if(dbType == null){
            return null;
        }

        Database db = null;

        switch (dbType){
            case MYSQL -> db = new MySqlDatabase(dbHost, dbPort, dbDatabase, dbUsername, dbPassword);
            case SQLITE -> db = new SqliteDatabase(dbFile);
        }

        return db;
    }

    enum DatabaseType{
        MYSQL("mysql"),
        SQLITE("sqlite");

        private final String identifier;

        DatabaseType(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }

        public static DatabaseType getByIdentifier(String identifier){
            for (DatabaseType type : values()) {
                if(type.getIdentifier().equalsIgnoreCase(identifier)){
                    return type;
                }
            }

            return null;
        }
    }
}
