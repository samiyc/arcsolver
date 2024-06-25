package dev.samiyc.bean;

import java.util.List;

public class Task {
    public List<TrainTest> train;
    public List<TrainTest> test;

    public void initializeTmpGrids() {
        for (TrainTest tt : this.train) {
            tt.initializeTmpGrid();
        }
        for (TrainTest tt : this.test) {
            tt.initializeTmpGrid();
        }
    }
}
