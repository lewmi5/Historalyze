package org.example.historalyze;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class StockPriceDownloader {

//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.print("Enter stock ticker symbol: ");
//        String ticker = scanner.nextLine().toUpperCase();
//        System.out.println(ticker);
//        try {
//            downloadHistoricalData(ticker);
//            System.out.println("Download completed successfully!");
//        } catch (Exception e) {
//            System.err.println("Error downloading stock data: " + e.getMessage());
//        } finally {
//            scanner.close();
//        }
//    }

    public static void downloadHistoricalData(String ticker) throws IOException {
        // Get stock data (default is 1 year of history)
        Stock stock = YahooFinance.get(ticker);

        if (stock == null) {
            throw new IOException("Could not find stock with ticker: " + ticker);
        }

        // Get historical quotes (defaults to 1 year of daily data)
        java.util.List<HistoricalQuote> history = stock.getHistory();

        if (history == null || history.isEmpty()) {
            throw new IOException("No historical data available for: " + ticker);
        }


        // Create CSV filename
        String filename = ticker + ".csv";

        // Write to CSV file
        try (FileWriter csvWriter = new FileWriter(filename)) {
            // Write header
            csvWriter.append("Date,Open,High,Low,Close,Volume,Adj Close\n");

            // Date formatter
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Write data rows
            for (HistoricalQuote quote : history) {
                csvWriter.append(dateFormat.format(quote.getDate().getTime()))
                        .append(",")
                        .append(quote.getOpen().toString())
                        .append(",")
                        .append(quote.getHigh().toString())
                        .append(",")
                        .append(quote.getLow().toString())
                        .append(",")
                        .append(quote.getClose().toString())
                        .append(",")
                        .append(String.valueOf(quote.getVolume()))
                        .append(",")
                        .append(quote.getAdjClose().toString())
                        .append("\n");
            }
        }

        System.out.println("CSV file created: " + filename);
    }
}