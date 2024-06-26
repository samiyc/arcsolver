package dev.samiyc.bean;

import java.util.List;

public class PixelFilter {
    public Byte val;
    public List<List<Byte>> around;
    public Integer row; // Use Integer instead of Byte to allow for null values
    public Integer col; // Use Integer instead of Byte to allow for null values

    public PixelFilter(Byte val, List<List<Byte>> around, Integer row, Integer col) {
        this.val = val;
        this.around = around;
        this.row = row;
        this.col = col;
    }
}
