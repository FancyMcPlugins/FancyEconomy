package de.oliver.fancycoins.vaults;

public class FancyVault {

    String name;
    String symbol;
    double balance;
    boolean default_currency;

    public FancyVault(String name, String symbol, double balance, boolean default_currency) {
        this.name = name;
        this.symbol = symbol;
        this.balance = balance;
        this.default_currency = default_currency;
    }

    @Override
    public String toString() {
        return "FancyVault{" +
                "name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", balance=" + balance +
                ", default_currency=" + default_currency +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isDefault_currency() {
        return default_currency;
    }

    public void setDefault_currency(boolean default_currency) {
        this.default_currency = default_currency;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getBalance() {
        return balance;
    }
}
