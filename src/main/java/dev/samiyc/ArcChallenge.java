package dev.samiyc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.samiyc.bean.Pixel;
import dev.samiyc.bean.Position;
import dev.samiyc.bean.Task;
import dev.samiyc.bean.TrainTest;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
            task.analyzeGrids();


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
        System.out.println("Are input and tmp equal ? " + inputEq);
        boolean outputEq = TrainTest.areEquivalent(task.test.get(0).output, task.test.get(0).tmp);
        System.out.println("Are output and tmp equal ? " + outputEq);

        // Check the pixels
        TrainTest trainOne = task.train.get(2);
        System.out.println("\n# Input pixels");
        List<Pixel> inSpe = trainOne.inputPixels.stream().filter(p -> p.val != 0).toList();
        displayPixelList(inSpe);

        //-------------------------------------------------------------------------------
        System.out.println("\n --- Danger zone ! ---");

        for (int i = 0; i < 10; i++) {

            System.out.println("\n# Output pixels");
            List<Pixel> outSpe = trainOne.outputPixels.stream().filter(p -> p.val != p.stepBro.val).toList();
            //displayPixelList(outSpe);
            System.out.println("outSpe:" + outSpe.size());

            //Reduce the threshold until a result is close to the output pixel
            List<Pixel> matchingPixels = new ArrayList<>();
            int threshold = 99;
            for (; threshold > 12 && matchingPixels.isEmpty(); threshold--)
                for (Pixel p : outSpe) {
                    matchingPixels.addAll(trainOne.filterPixels(p.toFilter(), threshold));
                }
            System.out.println("\n# Filter - " + threshold);
            displayPixelList(matchingPixels);

            //transfert test
            System.out.println("# Transfert test");
            matchingPixels.forEach(p -> {
                Position vp = p.findValuePosition(p.stepBro.val);
                if (vp != null) {
                    //updating the tmp grid with on of the around value. doesn't use the output data
                    trainOne.tmp.get(p.row).set(p.col, p.around.get(vp.row).get(vp.col));
                }
            });
            trainOne.log();
            trainOne.analyzeTmpGrids();
        }
    }

    private static void displayPixelList(List<Pixel> plist) {
        for (Pixel p : plist) System.out.println(p);
    }

}
