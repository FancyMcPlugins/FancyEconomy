package de.oliver.fancycoins.currencies;

import java.text.DecimalFormat;

public record Currency(String name, String symbol) {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,###,###.##");

    public String format(double amount){
        return symbol + DECIMAL_FORMAT.format(amount) + " " + name;
    }

}
