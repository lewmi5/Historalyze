package org.example.historalyze;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.example.historalyze.ExtractPricesFromCSV.getColumnValues;

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

    public StockPrices(String stockPath, String stockName) throws IOException {
        Path path = Paths.get(stockPath);
        File file = path.toFile();

        if(!file.exists()) {
            StockPriceDownloader.downloadHistoricalData(stockName);
        }

        this.open = getColumnValues(file, 4);
        this.close = getColumnValues(file, 1);
        this.low = getColumnValues(file, 3);
        this.high = getColumnValues(file, 2);
        this.size = open.size();
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
