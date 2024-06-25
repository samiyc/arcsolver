package dev.samiyc.bean;

import java.util.List;

public class Pixel {
    public Byte val;
    public List<List<Byte>> around;
    public Pixel stepBro; // Output/Input link

    Byte row; // Row location of the pixel
    Byte col; // Column location of the pixel

    public Pixel(Byte val, List<List<Byte>> around, int row, int col) {
        this.val = val;
        this.around = around;
        this.stepBro = null; // Initialized as null, will be linked later
        this.row = (byte) row;
        this.col = (byte) col;
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
