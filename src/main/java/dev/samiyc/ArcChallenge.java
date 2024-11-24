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
        String fileName = "src/main/resources/data/training/855e0971.json";
        /*
            0a938d79 0ca9ddb6 0dfd9992
            98cf29f8 : Rapprochement connected cube. noVlFromOut
            855e0971 : Expand a line of 0, horizontally/vertically
         */
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
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void validateAndProcessTask(Task task) {
        for(TrainTest trainTest : task.train) {
            trainTest.logInOut();
            List<Pixel> matchPxForTrainTest = validateAndProcessTrainTest(trainTest);

            // Check the pixels
            System.out.println("\n# All the filter for this trainTest : "+matchPxForTrainTest.size());
            displayPixelList(matchPxForTrainTest);
        }
    }

    private List<Pixel> validateAndProcessTrainTest(TrainTest trainOne) {
        List<Pixel> matchPxForTrainTest = new ArrayList<>();

        // Example of how to use areEquivalent method
        System.out.println("\n### Comparaisons :");
        boolean inputEq = TrainTest.areEquivalent(trainOne.input, trainOne.tmp);
        System.out.println("Are input and tmp equal ? " + inputEq);
        boolean outputEq = TrainTest.areEquivalent(trainOne.output, trainOne.tmp);
        System.out.println("Are output and tmp equal ? " + outputEq);

        //-------------------------------------------------------------------------------
        System.out.println("\n --- Danger zone ! ---");

        while (true) {
            System.out.println("\n# Output pixels");
            List<Pixel> outSpe = trainOne.outputPixels.stream().filter(p -> p.val != p.stepBro.val).toList();

            System.out.println("outSpe:" + outSpe.size());
            if (outSpe.size() < 1) {
                System.out.println("End of search !!!");
                break;
            }

            //Reduce the threshold until a result is close to the output pixel
            List<Pixel> matchingPixels = new ArrayList<>();
            int threshold = 99;
            for (; threshold > 0 && matchingPixels.isEmpty(); threshold--) {
                for (Pixel p : outSpe) {
                    matchingPixels.addAll(trainOne.filterPixels(p.toFilter(), threshold));
                }
            }
            System.out.println("\n# Filter - " + threshold);
            displayPixelList(matchingPixels);

            //transfert test
            System.out.println("\n# Transfert test");
            matchingPixels.forEach(p -> {
                Position vp = p.findValuePosition(p.stepBro.val);
                if (vp != null) {
                    //updating the tmp grid with on of the around value. doesn't use the output data
                    trainOne.tmp.get(p.row).set(p.col, p.around.get(vp.row).get(vp.col));
                } else {
                    trainOne.tmp.get(p.row).set(p.col, p.stepBro.val);
                    System.out.println("VAL FROM OUTPUT !!!");
                }
            });

            trainOne.logTmp();
            trainOne.analyzeTmpGrids();
            matchPxForTrainTest.addAll(matchingPixels);
        }

        boolean endCheck = TrainTest.areEquivalent(trainOne.output, trainOne.tmp);
        System.out.println("Are output and tmp equal ? " + endCheck);
        return matchPxForTrainTest;
    }

    private static void displayPixelList(List<Pixel> plist) {
        for (Pixel p : plist) System.out.println(p);
    }

}
