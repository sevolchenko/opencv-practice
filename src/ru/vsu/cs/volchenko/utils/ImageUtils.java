package ru.vsu.cs.volchenko.utils;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import ru.vsu.cs.volchenko.image_processing.MatWithMatches;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class ImageUtils {

    private ImageUtils() {}

    public enum ObjectColor {
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

    public static Scalar[] getHSVGreen() { /*https://russianblogs.com/article/41781040604/*/
        Scalar[] res = new Scalar[3];
        res[0] = new Scalar(36, 202, 59);
        res[1] = new Scalar(71, 255, 255);
        res[2] = new Scalar(0, 255, 0);
        return res;
    }

    public static Scalar[] getHSVYellow() {
        Scalar[] res = new Scalar[3];
        res[0] = new Scalar(18, 0, 196);
        res[1] = new Scalar(36, 255, 255);
        res[2] = new Scalar(255, 255, 0);
        return res;
    }

    public static Scalar[] getHSVBlue() {
        Scalar[] res = new Scalar[3];
        res[0] = new Scalar(89, 0, 0);
        res[1] = new Scalar(125, 255, 255);
        res[2] = new Scalar(0, 0, 255);
        return res;
    }

    public static Scalar[] getHSVRed() {
        Scalar[] res = new Scalar[3];
        res[0] = new Scalar(0, 100, 80);
        res[1] = new Scalar(10, 255, 255);
        res[2] = new Scalar(255, 0, 0);
        return res;
    }

    public static BufferedImage readBI(String path) throws IOException {
        return ImageIO.read(new File(path));
    }

    public static Mat bi2mat(BufferedImage image) { //https://stackoverflow.com/questions/14958643/converting-bufferedimage-to-mat-in-opencv
        Mat result = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            result = Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        return result;
    }

    public static BufferedImage mat2bi(Mat matrix) {
        BufferedImage result = null;
        try {
            MatOfByte mob=new MatOfByte();
            Imgcodecs.imencode(".jpg", matrix, mob);
            result = ImageIO.read(new ByteArrayInputStream(mob.toArray()));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(2);
        }
        return result;
    }

    public static Mat findColor(Mat src, Supplier<Scalar[]> sup) {
        Mat binaryMat = new Mat(src.size(), src.type());
        Scalar[] color = sup.get();
        Core.inRange(src, color[0], color[1], binaryMat);
        return binaryMat;
    }

    public static MatWithMatches paintAndCalcMatches(Mat maskedMat, Supplier<Scalar[]> sup) {
        Mat coloredMat = new Mat(maskedMat.size(), CvType.CV_8UC3);
        int countOfMatches = 0;
        Scalar[] color = sup.get();
        double[] fillingColor = {color[2].val[0], color[2].val[1], color[2].val[2]};
        double[] black = {0, 0, 0};
        for (int row = 0; row < maskedMat.rows(); row++) {
            for (int col = 0; col < maskedMat.cols(); col++) {
                double val = maskedMat.get(row, col)[0];
                if (val != 0) {
                    countOfMatches++;
                }
                coloredMat.put(row, col, val != 0 ? fillingColor : black);
            }
        }
        Imgproc.cvtColor(coloredMat, coloredMat, Imgproc.COLOR_RGB2BGR);
        return new MatWithMatches(coloredMat, countOfMatches);
    }

    public static ImageUtils.ObjectColor analyzeColor(double greenPercent, double yellowPercent,
                                                double bluePercent, double redPercent,
                                                double colorThreshold) {
        List<Double> percentList = List.of(greenPercent, yellowPercent, bluePercent, redPercent);
        double maxVal = percentList.stream().max(Double::compareTo).get();
        if (maxVal < colorThreshold) {
            return ImageUtils.ObjectColor.UNDEFINED;
        } else {
            int maxIndex = percentList.indexOf(maxVal);
            return ImageUtils.ObjectColor.values()[maxIndex + 1];
        }
    }

}
