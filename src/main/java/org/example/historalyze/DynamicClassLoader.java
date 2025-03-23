package org.example.historalyze;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DynamicClassLoader {
    /**
     * Dynamically loads investment strategy classes from a specified folder.
     *
     * @param folderPath the path to the folder containing compiled strategy classes (.class files)
     * @param packageName the package name where the classes are located
     * @return a list of loaded Class objects
     */
    public static List<Class<?>> loadClassesFromFolder(String folderPath, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        File folder = new File(folderPath);

        // Validate folder path
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Invalid folder path: " + folderPath);
            return classes;
        }

        // Filter .class files and load classes dynamically
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".class"));
        if (files != null) {
            for (File file : files) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    System.err.println("Could not load class: " + className);
                }
            }
        }
        return classes;
    }
}
