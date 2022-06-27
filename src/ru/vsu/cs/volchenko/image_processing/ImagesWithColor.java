package ru.vsu.cs.volchenko.image_processing;

import ru.vsu.cs.volchenko.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class ImagesWithColor {
    public BufferedImage source;
    public BufferedImage greenFilter;
    public BufferedImage yellowFilter;
    public BufferedImage blueFilter;
    public BufferedImage redFilter;
    public ImageUtils.ObjectColor color;

    public ImagesWithColor(BufferedImage source,
                           BufferedImage greenFilter, BufferedImage yellowFilter, BufferedImage blueFilter, BufferedImage redFilter,
                           ImageUtils.ObjectColor color) {
        this.source = source;
        this.greenFilter = greenFilter;
        this.yellowFilter = yellowFilter;
        this.blueFilter = blueFilter;
        this.redFilter = redFilter;
        this.color = color;
    }
}
