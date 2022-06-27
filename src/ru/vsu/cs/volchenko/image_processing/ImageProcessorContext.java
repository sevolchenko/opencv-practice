package ru.vsu.cs.volchenko.image_processing;

import java.util.ArrayList;
import java.util.List;

public class ImageProcessorContext {

    public long millisecondsStart;
    public long millisecondsEnd;
    public List<ImageWithDescription> listOfImages;
    public List<ImagesWithColor> listOfColors;

    public ImageProcessorContext() {
        listOfImages = new ArrayList<>();
        listOfColors = new ArrayList<>();
    }

    public long getPassedTime() {
        return millisecondsEnd - millisecondsStart;
    }
}
