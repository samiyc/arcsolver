package dev.samiyc.bean;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrainTest {
    public List<List<Byte>> input;
    public List<List<Byte>> output;
    public List<List<Byte>> tmp;
    public List<Pixel> inputPixels;
    public List<Pixel> outputPixels;
    public List<Pixel> tmpPixels;

    // Define 8 directions: up, down, left, right, and 4 diagonals
    int[][] ALL_DIRECTIONS = {
            {-1, 0}, // Up
            {1, 0},  // Down
            {0, -1}, // Left
            {0, 1},  // Right
            {-1, -1}, // Up-Left
            {-1, 1},  // Up-Right
            {1, -1},  // Down-Left
            {1, 1}    // Down-Right
    };

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

    public void analyzeAllGrids() {
        this.inputPixels = analyzeGrid(input);
        this.outputPixels = analyzeGrid(output);
        this.tmpPixels = analyzeGrid(tmp);

        // Link input and output pixels
        linkStepBro(tmpPixels, outputPixels);
    }

    public void analyzeTmpGrids() {
        this.tmpPixels = analyzeGrid(tmp);
        linkStepBro(tmpPixels, outputPixels);
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
        int rows = grid.size();
        int cols = grid.get(0).size();

        for (int[] direction : ALL_DIRECTIONS) {
            List<Byte> line = new ArrayList<>();
            int r = row;
            int c = col;

            // Extend in the direction until out of bounds
            while (true) {
                r += direction[0];
                c += direction[1];

                if (r >= 0 && r < rows && c >= 0 && c < cols) {
                    line.add(grid.get(r).get(c));
                } else {
                    line.add((byte) -1); // Outside the grid
                    break;
                }
            }
            around.add(line);
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

    public void logTmp() {
        System.out.println("\nTmp data:");
        printGrid(tmp);
    }
    public void logInOut() {
        System.out.println("\n-------------- LOG IN/OUT --------------");
        System.out.println("\nTrain Input:");
        printGrid(input);
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

    // Method to filter pixels based on the filter criteria
    public List<Pixel> filterPixels(PixelFilter filter, int threshold) {
        return tmpPixels.stream()
                .filter(pixel -> matchesFilter(pixel, filter, threshold)
                && !pixel.val.equals(pixel.stepBro.val))
                .collect(Collectors.toList());
    }

    // Method to check if a pixel matches the filter criteria based on the threshold
    private boolean matchesFilter(Pixel pixel, PixelFilter filter, int threshold) {
        int matchCount = 0;
        int criteriaCount = 0;

        if (filter.val != null) {
            criteriaCount++;
            if (filter.val.equals(pixel.val)) {
                matchCount++;
            }
        }
        if (filter.row != null) {
            criteriaCount++;
            if (filter.row.equals((int) pixel.row)) {
                matchCount++;
            }
        }
        if (filter.col != null) {
            criteriaCount++;
            if (filter.col.equals((int) pixel.col)) {
                matchCount++;
            }
        }
        if (filter.around != null) {
            for (int i = 0; i < Math.min(filter.around.size(), pixel.around.size()); i++) {
                List<Byte> filterLine = filter.around.get(i);
                List<Byte> pixelLine = pixel.around.get(i);
                for (int j = 0; j < Math.min(filterLine.size(), pixelLine.size()); j++) {
                    criteriaCount++;
                    if (filterLine.get(j).equals(pixelLine.get(j))) {
                        matchCount++;
                    }
                }
            }
        }
        return matchCount >= (criteriaCount * threshold / 100);
    }
}
