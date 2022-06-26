package ru.vsu.cs.volchenko.image_processing;

import java.awt.image.BufferedImage;

public class ImagesWithColor {
    public BufferedImage source;
    public BufferedImage greenFilter;
    public BufferedImage yellowFilter;
    public BufferedImage blueFilter;
    public BufferedImage redFilter;
    public ImageProcessorContext.ImageColor color;

    public ImagesWithColor(BufferedImage source,
                           BufferedImage greenFilter, BufferedImage yellowFilter, BufferedImage blueFilter, BufferedImage redFilter,
                           ImageProcessorContext.ImageColor color) {
        this.source = source;
        this.greenFilter = greenFilter;
        this.yellowFilter = yellowFilter;
        this.blueFilter = blueFilter;
        this.redFilter = redFilter;
        this.color = color;
    }

    public ImagesWithColor(BufferedImage source,
                           ImageProcessorContext.ImageColor color) {
        this.source = source;
        this.color = color;
    }
}
