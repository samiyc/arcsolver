package dev.samiyc.bean;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TrainTest {
    public List<List<Byte>> input;
    public List<List<Byte>> output;
    public List<List<Byte>> tmp;
    public List<Pixel> inputPixels;
    public List<Pixel> outputPixels;
    public List<Pixel> tmpPixels;

    public void initializeTmpGrid() {
        this.tmp = deepCopyGrid(input);
    }

    private List<List<Byte>> deepCopyGrid(List<List<Byte>> grid) {
        List<List<Byte>> copy = new ArrayList<>();
        for (List<Byte> row : grid) {
            List<Byte> rowCopy = new ArrayList<>(row);
            copy.add(rowCopy);
        }
        return copy;
    }

    public void analyzeTmpGrid() {
        this.tmpPixels = analyzeGrid(tmp);
    }

    public void analyzeGrids() {
        this.inputPixels = analyzeGrid(input);
        this.outputPixels = analyzeGrid(output);

        // Link input and output pixels
        linkStepBro(inputPixels, outputPixels);
    }

    private List<Pixel> analyzeGrid(List<List<Byte>> grid) {
        List<Pixel> pixels = new ArrayList<>();
        int rows = grid.size();
        int cols = grid.get(0).size();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Byte val = grid.get(i).get(j);
                List<List<Byte>> around = getAroundValues(grid, i, j);
                Pixel pixel = new Pixel(val, around, i, j); // Pass the location
                pixels.add(pixel);
            }
        }

        return pixels;
    }


    private List<List<Byte>> getAroundValues(List<List<Byte>> grid, int row, int col) {
        List<List<Byte>> around = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            List<Byte> rowValues = new ArrayList<>();
            for (int j = -1; j <= 1; j++) {
                int r = row + i;
                int c = col + j;

                if (i == j && i == 0) {
                    rowValues.add((byte) -2); // CurrentPixel
                } else if (r >= 0 && r < grid.size() && c >= 0 && c < grid.get(0).size()) {
                    rowValues.add(grid.get(r).get(c));
                } else {
                    rowValues.add((byte) -1); // Outside the grid boundaries
                }
            }
            around.add(rowValues);
        }
        return around;
    }

    private void linkStepBro(List<Pixel> inputPixels, List<Pixel> outputPixels) {
        int size = Math.min(inputPixels.size(), outputPixels.size());
        for (int i = 0; i < size; i++) {
            inputPixels.get(i).stepBro = outputPixels.get(i);
            outputPixels.get(i).stepBro = inputPixels.get(i);
        }
    }

    // Deep copy constructor using Gson serialization/deserialization
    public TrainTest deepCopy() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return gson.fromJson(json, TrainTest.class);
    }

    // Method to compare two TrainTest objects
    public static boolean areEquivalent(List<List<Byte>> tt1, List<List<Byte>> tt2) {
        if (tt1 == null || tt2 == null || tt1.size() != tt2.size()) {
            return false;
        }
        for (int i = 0; i < tt1.size(); i++) {
            List<Byte> row1 = tt1.get(i);
            List<Byte> row2 = tt2.get(i);

            if (!row1.equals(row2)) {
                return false;
            }
        }
        return true;
    }

    public void log() {
        System.out.println("\nTrain Input:");
        printGrid(input);
        System.out.println("\nTmp data:");
        printGrid(tmp);
        System.out.println("\nTrain Output:");
        printGrid(output);
    }

    private void printGrid(List<List<Byte>> grid) {
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
}
