package org.example.historalyze;

import java.util.ArrayList;

public class StockPrices {
    private final ArrayList<Float> open;
    private final ArrayList<Float> close;
    private final ArrayList<Float> low;
    private final ArrayList<Float> high;
    private final int size;

    public StockPrices(ArrayList<Float> open, ArrayList<Float> close, ArrayList<Float> low, ArrayList<Float> high) {
        this.open = open;
        this.close = close;
        this.low = low;
        this.high = high;
        size = open.size();
    }

    public StockPrices() {
        this.open = new ArrayList<>();
        this.close = new ArrayList<>();
        this.low = new ArrayList<>();
        this.high = new ArrayList<>();
        size = 0;
    }

    public float getOpen(int i) {
        return open.get(i);
    }

    public float getClose(int i) {
        return close.get(i);
    }

    public float getLow(int i) {
        return low.get(i);
    }

    public float getHigh(int i) {
        return high.get(i);
    }

    public int getSize() {
        return size;
    }
}
