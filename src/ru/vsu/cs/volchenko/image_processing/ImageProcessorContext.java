package ru.vsu.cs.volchenko.image_processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImageProcessorContext {
//todo: calculate count of object with single color
    public enum ImageColor {
        UNDEFINED,
        GREEN,
        YELLOW,
        BLUE,
        RED;

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase(Locale.ROOT);
        }
    }

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
