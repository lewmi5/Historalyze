package org.example.historalyze;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.historalyze.StrategyFactory.createStrategy;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class ApiController {
    private final String packageName = "org.example.historalyze.strategies";
    private final String folderPath = "target/classes/org/example/historalyze/strategies";
    private Path stockDataPath = Paths.get("stocksdata/list.csv");
    private boolean listWasRead = false;
    private String[] listContent;
    private String listContentCSV;
    private String stockName;
    private StockPrices prices;
    private String strategyName;
    private String stockPath;


    /**
     * Endpoint which is used to check connection
     * @return ResponseEntity<Map<String, Object>>
     */
    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getData() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from Spring Backend!");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a list of stock names from a CSV file.
     * If the file has not been read yet, it reads the content, splits it into an array,
     * and stores it for future use to avoid redundant file reads.
     * In case of an error while reading the file, an error message is stored instead.
     *
     * @return ResponseEntity containing a map with the stock names and a timestamp.
     */
    @GetMapping("/stock_names")
    public ResponseEntity<Map<String, Object>> getNames() {
        // Check if the stock names list has already been read to avoid redundant file reads.
        if(!listWasRead) {
            try {
                listContentCSV = Files.readString(stockDataPath);
                listContent = listContentCSV.split(",");
                listWasRead = true;
            } catch (IOException e) {
                listContent[0] = "Error reading file: " + e.getMessage();
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("names", listContent);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the names of all strategy classes dynamically loaded from a specified package.
     * It searches for classes in the given folder, loads them, and extracts the strategy names.
     *
     * @return ResponseEntity containing a map with a list of strategy names and a timestamp.
     */
    @GetMapping("/strategy_names")
    public ResponseEntity<Map<String, Object>> getStrategyNames() {
        List<Class<?>> strategies = new ArrayList<>();
        // Load all classes dynamically from specified folder.
        // This allows discovering and processing strategy classes at runtime.
        strategies = DynamicClassLoader.loadClassesFromFolder(folderPath, packageName);

        List<String> names = new ArrayList<>();
        for (Class<?> clazz : strategies) {
            // TODO: Repair it by casting clazz to Strategy and create dully instance in order to call .getName()
            try {
                if (Strategy.class.isAssignableFrom(clazz)) {
                    // Get static attribute "name" from class
                    Field nameField = clazz.getDeclaredField("name");
                    nameField.setAccessible(true);
                    String name = (String) nameField.get(null); // null bo pole jest statyczne
                    names.add(name);
                }
            } catch (Exception e) {
                System.err.println("Error while retrieving the strategy name. " + clazz.getName());
                e.printStackTrace();
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("names", names);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    /**
     * Handles POST requests to retrieve the description of a specified strategy.
     * The strategy name is received in the request body, and the corresponding strategy
     * instance is created to fetch its description.
     *
     * @param payload A map containing the strategy name under the key "name".
     * @return ResponseEntity containing a map with the strategy description.
     */
    @PostMapping("/strategy_descritption")
    public ResponseEntity<Map<String, Object>> getStrategyDescriptions(@RequestBody Map<String, String> payload) {
        strategyName = payload.get("name");
        stockPath = "stocksdata/" + stockName + ".csv";

        Strategy temp;
        prices = new StockPrices(stockPath, stockName, stockDataPath);
        temp = createStrategy(strategyName, prices);

        Map<String, Object> response = new HashMap<>();
        assert temp != null;
        response.put("description", temp.getDescription());
        System.out.println(temp.getDescription());

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves stock prices from a CSV file.
     *
     * @param payload A JSON object containing the stock name. Expected format: {"name": "StockName"}
     * @return ResponseEntity with the CSV content or an error message.
     */
    @PostMapping("/prices")
    public ResponseEntity<Map<String, Object>> getStockPrices(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();

        // Flag to check if the stock name exists in the predefined list
        boolean nameInList = false;

        // Iterate through the list of valid stock names to find a match
        for(String name : listContent) {
            // If given name matches one on the list:
            if(name.equals(payload.get("name").toString())) {
                stockName = name;
                stockPath = "stocksdata/" + stockName + ".csv";
                prices = new StockPrices(stockPath, stockName, stockDataPath);
                System.out.println("Stock Name: " + stockName);
                nameInList = true;
                break;
            }
        }

        // If the stock name is invalid return response error.
        if (!nameInList) {
            response.put("status", "error");
            response.put("message", "Stock name not found in list.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Otherwise extract stock history
        String stockPath = "stocksdata/" + stockName + ".csv";
        Path path = Paths.get(stockPath);
        
        if(!Files.exists(path)) {
            try {
                StockPriceDownloader.downloadHistoricalData(stockName, stockDataPath);
            } catch (IOException e) {
                response.put("status", "error");
                response.put("message", "Error reading file: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }

        // Send back received message.
        response.put("received", payload);
        response.put("status", "success");

        String content;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            content = "Error reading file: " + e.getMessage();
        }

        // and send it back.
        response.put("content", content);
        return ResponseEntity.ok(response);
    }

    /**
     *Calculates the profit based on buy and sell signals generated by a given investment strategy and returns the result.
     *
     * @param payload A JSON request body containing strategy parameters.
     *                Expected format: {"params": "Short_MA Long_MA"} (e.g., {"params": "10 50"}).
     * @return ResponseEntity containing the received parameters, computed result, and status.
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> simulateStrategy(@RequestBody Map<String, Object> payload) {
        // Process the data received from React
        Map<String, Object> response = new HashMap<>();
        String params = payload.get("params").toString();
        Strategy currentStrategy = createStrategy(strategyName, prices);

        assert currentStrategy != null;
        float result = currentStrategy.Calculate(params);
        System.out.println(result);
        response.put("received", payload);
        response.put("result", result);

        System.out.println(payload);
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }
}
