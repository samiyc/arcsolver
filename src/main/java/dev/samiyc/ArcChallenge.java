package dev.samiyc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ArcChallenge {

    static class Task {
        List<TrainTest> train;
        List<TrainTest> test;
        List<TrainTest> computed = new ArrayList<>();
    }

    static class TrainTest {
        List<List<Integer>> input;
        List<List<Integer>> output;

        // Deep copy constructor using Gson serialization/deserialization
        public TrainTest deepCopy() {
            Gson gson = new Gson();
            String json = gson.toJson(this);
            return gson.fromJson(json, TrainTest.class);
        }

        // Method to compare two TrainTest objects
        public static boolean areEquivalent(List<List<Integer>> tt1, List<List<Integer>> tt2) {
            if (tt1 == null || tt2 == null || tt1.size() != tt2.size()) {
                return false;
            }
            for (int i = 0; i < tt1.size(); i++) {
                List<Integer> row1 = tt1.get(i);
                List<Integer> row2 = tt2.get(i);

                if (!row1.equals(row2)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {
        String fileName = "src/main/resources/data/training/0a938d79.json";

        try (FileReader reader = new FileReader(fileName)) {
            // Create a Gson instance
            Gson gson = new Gson();

            // Define the type of the JSON structure
            Type taskType = new TypeToken<Task>() {
            }.getType();

            // Deserialize the JSON into a Task object
            Task task = gson.fromJson(reader, taskType);

            // Copy test data to compute
            copyTrainTestToComputed(task);

            // Process the task data
            validateAndProcessTask(task);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void validateAndProcessTask(Task task) {
        System.out.println("Training Data:");
        for (TrainTest train : task.train) {
            System.out.println("\nTrain Input:");
            printGrid(train.input);
            System.out.println("\nTrain Output:");
            printGrid(train.output);
        }

        System.out.println("\nTesting Data:");
        for (TrainTest test : task.test) {
            System.out.println("\nTest Input:");
            printGrid(test.input);
            System.out.println("\nTest Output:");
            printGrid(test.output);
        }

        System.out.println("\nComputed Data:");
        for (TrainTest test : task.computed) {
            System.out.println("\nComp Input:");
            printGrid(test.input);
            System.out.println("\nComp Output:");
            printGrid(test.output);
        }

        // Example of how to use areEquivalent method
        System.out.println("\n### Comparaisons :");
        boolean inputEq = TrainTest.areEquivalent(task.test.get(0).input, task.computed.get(0).input);
        System.out.println("Are Test and Computed input equal ? " + inputEq);
        boolean outputEq = TrainTest.areEquivalent(task.test.get(0).output, task.computed.get(0).output);
        System.out.println("Are Test and Computed output equal ? " + outputEq);
    }

    private static void printGrid(List<List<Integer>> grid) {
        if (grid == null || grid.isEmpty() || grid.get(0).isEmpty()) {
            System.out.println("Grid is empty.");
            return;
        }

        int rows = grid.size();
        int cols = grid.get(0).size();

        // Print each element in the grid
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(grid.get(i).get(j) + " "); // Use \t for tab separation
            }
            System.out.println(); // Move to the next line after printing each row
        }
    }

    private static void copyTrainTestToComputed(Task task) {
        for (TrainTest tt : task.test) {
            TrainTest dctt = tt.deepCopy();
            dctt.output = null;
            task.computed.add(dctt);
        }
    }
}
