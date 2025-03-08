package org.example.historalyze;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getData() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from Spring Backend!");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitData(@RequestBody Map<String, Object> payload) {
        // Process the data received from React
        Map<String, Object> response = new HashMap<>();
        response.put("received", payload);
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

}
