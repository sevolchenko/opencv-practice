package ru.vsu.cs.volchenko.image_processing;

import org.opencv.core.Mat;

public class MatWithMatches {

    public Mat mat;
    public int countOfMatches;

    public MatWithMatches(Mat mat, int countOfMatches) {
        this.mat = mat;
        this.countOfMatches = countOfMatches;
    }

}
