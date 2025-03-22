package org.example.historalyze.strategies;

import org.example.historalyze.StockPrices;
import org.example.historalyze.Strategy;

import java.util.ArrayList;

public class SMA extends Strategy {
    public static final String name = "SMA";
    private static final String description = "SMA params(format:[Short_MA] [Long_MA])";
    private int shortMA;
    private int longMA;

    private ArrayList<Boolean> buySignals;
    private ArrayList<Boolean> sellSignals;

    public SMA(StockPrices stockPrices) {
        super(stockPrices);
        this.buySignals = new ArrayList<>();
        this.sellSignals = new ArrayList<>();
    }

    @Override
    public float Calculate() {
        if (prices.getSize() < longMA) {
            throw new IllegalArgumentException("Not enough price data for the selected MA periods");
        }

        // Pobierz dane cen zamknięcia
        ArrayList<Float> closePrices = new ArrayList<>();
        for (int i = 0; i < prices.getSize(); i++) {
            closePrices.add(prices.getClose(i));
        }

        // Generuj sygnały na podstawie cen zamknięcia
        generateSignals(closePrices);

        // Oblicz mnożnik zwrotu
        return calculateReturnMultiplier(closePrices);
    }

    private void generateSignals(ArrayList<Float> prices) {
        buySignals.clear();
        sellSignals.clear();

        ArrayList<Float> shortMAValues = calculateMA(prices, shortMA);
        ArrayList<Float> longMAValues = calculateMA(prices, longMA);

        // For the first longMA-1 days, we don't have enough data for both MAs
        for (int i = 0; i < longMA - 1; i++) {
            buySignals.add(false);
            sellSignals.add(false);
        }

        boolean previousCrossAbove = false;

        // Generate signals starting from when we have both MAs
        for (int i = longMA - 1; i < prices.size(); i++) {
            int maIndex = i - longMA + 1;
            float shortValue = shortMAValues.get(maIndex + (shortMA - 1));
            float longValue = longMAValues.get(maIndex);

            boolean crossAbove = shortValue > longValue;

            // Buy signal: short MA crosses above long MA
            buySignals.add(crossAbove && !previousCrossAbove);

            // Sell signal: short MA crosses below long MA
            sellSignals.add(!crossAbove && previousCrossAbove);

            previousCrossAbove = crossAbove;
        }
    }

    private ArrayList<Float> calculateMA(ArrayList<Float> prices, int period) {
        ArrayList<Float> maValues = new ArrayList<>();

        for (int i = 0; i <= prices.size() - period; i++) {
            float sum = 0;
            for (int j = 0; j < period; j++) {
                sum += prices.get(i + j);
            }
            maValues.add(sum / period);
        }

        return maValues;
    }

    private float calculateReturnMultiplier(ArrayList<Float> prices) {
        boolean holding = false;
        float entryPrice = 0;
        float returnMultiplier = 1.0f;

        for (int i = 0; i < prices.size(); i++) {
            if (!holding && buySignals.get(i)) {
                // Buy signal
                holding = true;
                entryPrice = prices.get(i);
            } else if (holding && sellSignals.get(i)) {
                // Sell signal
                holding = false;
                returnMultiplier *= (prices.get(i) / entryPrice);
            }
        }

        // If we're still holding at the end, sell on the last day
        if (holding) {
            returnMultiplier *= (prices.get(prices.size() - 1) / entryPrice);
        }

        return returnMultiplier;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setParams(String params) {
        String[] parameters = params.trim().split("\\s+");
        if (parameters.length != 2) {
            throw new IllegalArgumentException("SMA strategy requires exactly 2 parameters: Short_MA Long_MA");
        }

        try {
            shortMA = Integer.parseInt(parameters[0]);
            longMA = Integer.parseInt(parameters[1]);

            if (shortMA <= 0 || longMA <= 0) {
                throw new IllegalArgumentException("Moving average periods must be positive");
            }

            if (shortMA >= longMA) {
                throw new IllegalArgumentException("Short MA period must be less than Long MA period");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid parameter format. Expected integers for Short_MA and Long_MA");
        }
    }

    @Override
    public String getCustomName() {
        return name;
    }
}
