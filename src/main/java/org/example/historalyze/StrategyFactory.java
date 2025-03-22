package org.example.historalyze;

import org.example.historalyze.strategies.SMA;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StrategyFactory {
    public static Strategy createStrategy(String strategyName, StockPrices stockPrices) {
        switch (strategyName) {
            case SMA.name:
                return new SMA(stockPrices);
        }
        return null;
    }
}
