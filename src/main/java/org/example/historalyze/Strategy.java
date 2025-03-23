package org.example.historalyze;

public abstract class Strategy {
    // Each class extending this class must have public
    protected final StockPrices prices;

    public Strategy(StockPrices prices) {
        this.prices = prices;
    }

    public abstract float Calculate(String parameters);
    public abstract String getDescription();
    public abstract String getCustomName();
}
