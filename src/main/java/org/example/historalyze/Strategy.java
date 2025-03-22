package org.example.historalyze;

import java.util.ArrayList;

public abstract class Strategy {
    // Each class extending this class must have public
    protected final StockPrices prices;

    public Strategy(StockPrices prices) {
        this.prices = prices;
    }

    public abstract float Calculate();
    public abstract String getDescription();
    public abstract void setParams(String params);
    public abstract String getCustomName();
}
