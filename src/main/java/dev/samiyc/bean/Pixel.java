package dev.samiyc.bean;

import java.util.List;

public class Pixel {
    public Byte val;
    public List<List<Byte>> around;
    public Pixel stepBro; // Output/Input link

    public Byte row; // Row location of the pixel
    public Byte col; // Column location of the pixel

    public Pixel(Byte val, List<List<Byte>> around, int row, int col) {
        this.val = val;
        this.around = around;
        this.stepBro = null; // Initialized as null, will be linked later
        this.row = (byte) row;
        this.col = (byte) col;
    }

    public Position findValuePosition(Byte value) {
        Position pos = null;
        for (int i = 0; i < around.size(); i++) {
            List<Byte> rowValues = around.get(i);
            for (int j = 0; j < rowValues.size(); j++) {
                if (rowValues.get(j) != null && rowValues.get(j).equals(value)) {
                    if (pos != null) return null; // Already exist return null
                    pos = new Position(i, j);
                }
            }
        }
        return pos; // Return the Value found or null
    }

    // Method to generate a PixelFilter from this Pixel
    public PixelFilter toFilter() {
        return new PixelFilter(this.val, this.around, (int) this.row, (int) this.col);
    }

    @Override
    public String toString() {
        return "Pixel{" +
                "val=" + val +
                ", around=" + around +
                ", stepBro=" + (stepBro != null ? stepBro.val : "null") +
                ", row=" + row +
                ", col=" + col +
                '}';
    }
}
