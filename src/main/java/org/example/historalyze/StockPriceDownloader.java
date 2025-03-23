package org.example.historalyze;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

public class StockPriceDownloader {

    /**
     * Downloads historical stock data for a given ticker symbol from Yahoo Finance
     * and saves it as a CSV file in the specified directory.
     * The default period is 1 year of daily stock data.
     *
     * @param ticker The stock ticker symbol for which historical data is to be downloaded.
     * @param stockDataPath The stockDataPath (Path) where the CSV file will be saved.
     * @throws IOException If there is an error fetching the stock data or writing to the file.
     */
    public static void downloadHistoricalData(String ticker, Path stockDataPath) throws IOException {
        // Fetch stock data for the given ticker using YahooFinance API (defaults to 1 year of history)
        Stock stock = YahooFinance.get(ticker);

        // Check if stock data was found, otherwise throw an IOException with a descriptive message
        if (stock == null) {
            throw new IOException("Could not find stock with ticker: " + ticker);
        }

        // Retrieve historical quotes (defaults to 1 year of daily data)
        List<HistoricalQuote> history = stock.getHistory();

        // Check if the history list is empty or null, which indicates no available historical data
        if (history == null || history.isEmpty()) {
            throw new IOException("No historical data available for: " + ticker);
        }

        // Generate the CSV filename based on the stock ticker (e.g., "AAPL.csv")
        String filename = ticker + ".csv";

        // Ensure the directory exists; if not, create it
        if (!Files.exists(stockDataPath)) {
            try {
                Files.createDirectories(stockDataPath); // Create the directory if it doesn't exist
            } catch (IOException e) {
                throw new IOException("Failed to create directory: " + stockDataPath, e);
            }
        }

        // Construct the full file path (e.g., "data/AAPL.csv")
        Path filePath = stockDataPath.resolve(filename);

        // Try-with-resources block to automatically close the CSV writer after use
        try (FileWriter csvWriter = new FileWriter(filePath.toFile())) {
            // Write the header row in the CSV file with appropriate column names
            csvWriter.append("Date,Open,High,Low,Close,Volume,Adj Close\n");

            // Create a SimpleDateFormat object to format dates in "yyyy-MM-dd" format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Write each historical quote data to the CSV
            for (HistoricalQuote quote : history) {
                csvWriter.append(dateFormat.format(quote.getDate().getTime())) // Date
                        .append(",")
                        .append(quote.getOpen().toString()) // Opening price
                        .append(",")
                        .append(quote.getHigh().toString()) // Highest price
                        .append(",")
                        .append(quote.getLow().toString()) // Lowest price
                        .append(",")
                        .append(quote.getClose().toString()) // Closing price
                        .append(",")
                        .append(String.valueOf(quote.getVolume())) // Volume
                        .append(",")
                        .append(quote.getAdjClose().toString()) // Adjusted closing price
                        .append("\n"); // Add newline after each record
            }
        }

        // Log to the console that the CSV file has been created successfully
        System.out.println("CSV file created: " + filePath);
    }
}
