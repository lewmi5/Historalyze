package org.example.historalyze;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
