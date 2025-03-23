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

import static org.example.historalyze.ExtractPricesFromCSV.getColumnValues;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class ApiController {
    private boolean listWasRead = false;
    String[] listContent;
    String listContentCSV;

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
    public ResponseEntity<Map<String, Object>> getStrategyNames() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
//        response.put("names", listContent);
        response.put("names", names);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/prices")
    public ResponseEntity<Map<String, Object>> getStockPrices(@RequestBody Map<String, Object> payload) throws IOException {
        // Process the data received from React
        Map<String, Object> response = new HashMap<>();

        boolean nameInList = false;
        for(String name : listContent) {
            if(name == payload.get("name")) {
                nameInList = true;
                break;
            }
        }

        Object name = payload.get("name");
        System.out.println(name);
        response.put("received", payload);
        response.put("status", "success");
        String stockPath = "stocksdata/" + name.toString() + ".csv";
        Path path = Paths.get(stockPath);
        if(!Files.exists(path)) {
            StockPriceDownloader.downloadHistoricalData(name.toString());
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
    public ResponseEntity<Map<String, Object>> simulateStrategy(@RequestBody Map<String, Object> payload) throws IOException {
        // Process the data received from React
        Map<String, Object> response = new HashMap<>();

        Object strategy = payload.get("strategy");
        System.out.println(strategy);
        response.put("received", payload);
        response.put("status", "success");

        String stockPath = "stocksdata/" + strategy.toString() + ".csv";
        Path path = Paths.get(stockPath);
        File file = path.toFile();

//        if(!Files.exists(path)) {
        if(!file.exists()) {
            StockPriceDownloader.downloadHistoricalData(strategy.toString());
        }


        String content;
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            content = "Error reading file: " + e.getMessage();
//            throw new RuntimeException(e);
        }

        StockPrices prices = new StockPrices(getColumnValues(file, 4), getColumnValues(file, 1), getColumnValues(file, 3), getColumnValues(file, 2));
        ArrayList<Float> pricesClose = getColumnValues(file, 1);

        System.out.println(pricesClose.get(0));

        response.put("content", content);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitData(@RequestBody Map<String, Object> payload) {
        // Process the data received from React
        Map<String, Object> response = new HashMap<>();

        Object firstValue = payload.get("name");
        Object secondValue = payload.get("message");
        System.out.println(firstValue);
        System.out.println(secondValue);
        response.put("received", payload);
        response.put("status", "success");

        Path path = Paths.get("stocksdata/google_stock_history (1).csv");
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

}
