package de.oliver.fancyeconomy.currencies;

import de.oliver.fancyeconomy.FancyEconomy;
import de.oliver.fancylib.UUIDFetcher;
import de.oliver.fancylib.databases.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CurrencyPlayerManager {

    private static final Map<UUID, CurrencyPlayer> cachedPlayers = new HashMap<>();

    public static CurrencyPlayer getPlayer(UUID uuid){
        if(cachedPlayers.containsKey(uuid)){
            return cachedPlayers.get(uuid);
        }

        CurrencyPlayer player = new CurrencyPlayer(uuid, "N/A");
        cachedPlayers.put(uuid, player);
        return player;
    }

    public static CurrencyPlayer getPlayer(String username){
        for (CurrencyPlayer player : cachedPlayers.values()) {
            if(player.getUsername().equalsIgnoreCase(username)){
                return player;
            }
        }

        UUID uuid = UUIDFetcher.getUUID(username);
        if(uuid == null){
            return null;
        }

        CurrencyPlayer player = new CurrencyPlayer(uuid, username);
        cachedPlayers.put(uuid, player);
        return player;
    }

    public static Collection<CurrencyPlayer> getAllPlayers(){
        return cachedPlayers.values();
    }

    public static void loadPlayersFromDatabase(){
        Database db = FancyEconomy.getInstance().getDatabase();

        cachedPlayers.clear();

        /*
            Load players
         */
        ResultSet rsPlayers = db.executeQuery("SELECT * FROM players");
        try{
            while(rsPlayers.next()){
                String uuidStr = rsPlayers.getString("uuid");
                UUID uuid = UUID.fromString(uuidStr);
                if(uuid == null){
                    continue;
                }

                String username = rsPlayers.getString("username");

                CurrencyPlayer currencyPlayer = new CurrencyPlayer(uuid, username);
                cachedPlayers.put(uuid, currencyPlayer);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        /*
            Load balances
         */
        ResultSet rsBalances = db.executeQuery("SELECT * FROM balances");
        try{
            while(rsBalances.next()){
                String uuidStr = rsBalances.getString("uuid");
                UUID uuid = UUID.fromString(uuidStr);
                if(uuid == null){
                    continue;
                }

                String currencyName = rsBalances.getString("currency");
                Currency currency = CurrencyRegistry.getCurrencyByName(currencyName);
                if(currency == null){
                    continue;
                }

                double balance = rsBalances.getDouble("balance");

                if (cachedPlayers.containsKey(uuid)) {
                    CurrencyPlayer currencyPlayer = cachedPlayers.get(uuid);
                    currencyPlayer.getBalances().put(currency, balance);
                }

            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void saveToDatabase(){
        Database db = FancyEconomy.getInstance().getDatabase();

    }

}