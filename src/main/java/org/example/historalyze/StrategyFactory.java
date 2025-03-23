package org.example.historalyze;

import org.example.historalyze.strategies.SMA;

public class StrategyFactory {
    public static Strategy createStrategy(String strategyName, StockPrices stockPrices) {
        switch (strategyName) {
            case SMA.name:
                return new SMA(stockPrices);
        }
        return null;
    }
}
