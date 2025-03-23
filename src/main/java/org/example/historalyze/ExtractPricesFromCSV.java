package org.example.historalyze;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ExtractPricesFromCSV {
    /**
     * Method to extract column values from a CSV file.
     * @param file The CSV file as a File object.
     * @param columnIndex The column index to extract (0-based index).
     * @return An ArrayList of Float values from the specified column.
     */
    public static ArrayList<Float> getColumnValues(File file, int columnIndex) {
        ArrayList<Float> columnValues = new ArrayList<>();  // Declare an ArrayList to store float values

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            // Read all rows from the CSV
            List<String[]> rows = reader.readAll();

            // Iterate through each row
            for (String[] row : rows) {
                // Check if the row has the expected column
                if (row.length > columnIndex) {
                    try {
                        // Parse the value as a float and add it to the ArrayList
                        columnValues.add(Float.parseFloat(row[columnIndex]));
                    } catch (NumberFormatException e) {
                        // Handle the case where the value can't be parsed as a float
                        System.err.println("Invalid float value: " + row[columnIndex]);
                    }
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return columnValues;  // Return the ArrayList with the extracted float values
    }
}
