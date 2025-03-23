package org.example.historalyze;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
    private boolean listWasRead = false;
    private String[] listContent;
    private String listContentCSV;
    private String stockName;
    private StockPrices prices;
    private String strategyName;
    private String stockPath;

    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getData() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from Spring Backend!");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/names")
    public ResponseEntity<Map<String, Object>> getNames() {
        if(!listWasRead) {
            Path path = Paths.get("stocksdata/list.csv");
            try {
                listContentCSV = Files.readString(path);
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

    @GetMapping("/strategy_names")
    public ResponseEntity<Map<String, Object>> getStrategyNames() {
        String packageName = "org.example.historalyze.strategies";
        String folderPath = "target/classes/org/example/historalyze/strategies";

        List<Class<?>> strategies = new ArrayList<>();
        try {
            strategies = DynamicClassLoader.loadClassesFromFolder(folderPath, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> names = new ArrayList<>();
        for (Class<?> clazz : strategies) {
            // TODO: Repair it by casting clazz to Strategy and create dully instance in order to call .getName()
            try {
                // Sprawdź, czy klasa dziedziczy po Strategy
                if (Strategy.class.isAssignableFrom(clazz)) {
                    // Pobierz statyczne pole "name" z klasy
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

        for (String name : names) {
            System.out.println(name);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("names", names);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    //returns the description for a given strategy
    @PostMapping("/strategy_descritption")
    public ResponseEntity<Map<String, Object>> getStrategyDescriptions(@RequestBody Map<String, String> payload) {
        strategyName = payload.get("name");
        stockPath = "stocksdata/" + stockName + ".csv";
        Strategy temp;
        try {
            prices = new StockPrices(stockPath, stockName);
            temp = createStrategy(strategyName, prices);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> response = new HashMap<>();
        assert temp != null;
        response.put("description", temp.getDescription());
        System.out.println(temp.getDescription());
//        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/prices")
    public ResponseEntity<Map<String, Object>> getStockPrices(@RequestBody Map<String, Object> payload) throws IOException {
        // Process the data received from React
        Map<String, Object> response = new HashMap<>();

        boolean nameInList = false;
        for(String name : listContent) {
            if(name.equals(payload.get("name").toString())) {
                stockName = name;
                stockPath = "stocksdata/" + stockName + ".csv";
                prices = new StockPrices(stockPath, stockName);
                System.out.println("Stock Name: " + stockName);
                nameInList = true;
                break;
            }
        }

        System.out.println(stockName);
        response.put("received", payload);
        response.put("status", "success");
        String stockPath = "stocksdata/" + stockName + ".csv";
        Path path = Paths.get(stockPath);
        if(!Files.exists(path)) {
            StockPriceDownloader.downloadHistoricalData(stockName);
        }


        String content;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            content = "Error reading file: " + e.getMessage();
//            throw new RuntimeException(e);
        }

        response.put("content", content);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> simulateStrategy(@RequestBody Map<String, Object> payload) {
        // Process the data received from React
        Map<String, Object> response = new HashMap<>();
        String params = payload.get("params").toString();
        Strategy currentStrategy = createStrategy(strategyName, prices);

        assert currentStrategy != null;
        float result = currentStrategy.Calculate(params);
        System.out.println(result);
        System.out.println("ds;lakfjf;sjdl");
        response.put("received", payload);
        response.put("result", result);

        System.out.println(payload);
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }
}
