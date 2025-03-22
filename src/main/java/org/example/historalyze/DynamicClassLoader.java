package org.example.historalyze;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DynamicClassLoader {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String packageName = "org.example.historalyze.strategies";
        String folderPath = "target/classes/org/example/historalyze/strategies";

        List<Class<?>> classes = loadClassesFromFolder(folderPath, packageName);

        for (Class<?> clazz : classes) {
            System.out.println(clazz.getName());
        }
    }

    public static List<Class<?>> loadClassesFromFolder(String folderPath, String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Invalid folder path: " + folderPath);
            return classes;
        }

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
