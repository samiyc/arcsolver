package dev.samiyc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.samiyc.bean.Pixel;
import dev.samiyc.bean.Task;
import dev.samiyc.bean.TrainTest;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ArcChallenge {

    public static void main(String[] args) {
        ArcChallenge arcc = new ArcChallenge();
        arcc.run();
    }

    public void run() {
        String fileName = "src/main/resources/data/training/0a938d79.json";

        try (FileReader reader = new FileReader(fileName)) {
            // Create a Gson instance
            Gson gson = new Gson();

            // Define the type of the JSON structure
            Type taskType = new TypeToken<Task>() {
            }.getType();

            // Deserialize the JSON into a Task object
            Task task = gson.fromJson(reader, taskType);

            task.initializeTmpGrids();

            // Process the task data
            validateAndProcessTask(task);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void validateAndProcessTask(Task task) {
        System.out.println("Training Data:");
        for (TrainTest tt : task.train) tt.log();
        System.out.println("\nTesting Data:");
        for (TrainTest tt : task.test) tt.log();

        // Example of how to use areEquivalent method
        System.out.println("\n### Comparaisons :");
        boolean inputEq = TrainTest.areEquivalent(task.test.get(0).input, task.test.get(0).tmp);
        System.out.println("Are Test and Computed input equal ? " + inputEq);
        boolean outputEq = TrainTest.areEquivalent(task.test.get(0).output, task.test.get(0).tmp);
        System.out.println("Are Test and Computed output equal ? " + outputEq);

        // Check the pixels
        TrainTest trainOne = task.train.getFirst();
        trainOne.analyzeGrids();
        System.out.println("\n# Input pixels");

        List<Pixel> inSpe = trainOne.inputPixels.stream().filter(p -> p.val != 0).toList();
        displayPixelList(inSpe);
        System.out.println("\n# Output pixels");
        List<Pixel> outSpe = trainOne.outputPixels.stream().filter(p -> p.val != p.stepBro.val).toList();
        displayPixelList(outSpe);
    }

    private static void displayPixelList(List<Pixel> plist) {
        for (Pixel p : plist) System.out.println(p);
    }

}
