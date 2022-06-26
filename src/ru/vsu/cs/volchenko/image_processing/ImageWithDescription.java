package ru.vsu.cs.volchenko.image_processing;

import java.awt.image.BufferedImage;

public class ImageWithDescription {
    public BufferedImage img;
    public String description;

    public ImageWithDescription(BufferedImage img, String description) {
        this.img = img;
        this.description = description;
    }
}