package ru.vsu.cs.volchenko.utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

public class ImageUtils {

    private ImageUtils() {}


    public static Scalar[] getHSVGreen() { /*https://russianblogs.com/article/41781040604/*/
        Scalar[] res = new Scalar[2];
        res[0] = new Scalar(36, 202, 59);
        res[1] = new Scalar(71, 255, 255);
        return res;
    }

    public static Scalar[] getHSVYellow() {
        Scalar[] res = new Scalar[2];
        res[0] = new Scalar(18, 0, 196);
        res[1] = new Scalar(36, 255, 255);
        return res;
    }

    public static Scalar[] getHSVBlue() {
        Scalar[] res = new Scalar[2];
        res[0] = new Scalar(89, 0, 0);
        res[1] = new Scalar(125, 255, 255);
        return res;
    }

    public static Scalar[] getHSVRed() {
        Scalar[] res = new Scalar[2];
        res[0] = new Scalar(0, 100, 80);
        res[1] = new Scalar(10, 255, 255);
        return res;
    }

    public static Mat drawColor(Mat sourceMat, Supplier<Scalar[]> supplier) {
        Scalar[] color = supplier.get();
        /*double[] averageColor = {(color[0].val[0] + color[1].val[0]) / 2,
                (color[0].val[1] + color[1].val[1]) / 2,
                (color[0].val[2] + color[1].val[2]) / 2};*/
        double[] averageColor = {color[1].val[0], color[1].val[1], color[1].val[2]};
        double[] black = {0, 0, 0};
        Mat result = new Mat(sourceMat.size(), CvType.CV_8UC3);
        for (int row = 0; row < sourceMat.rows(); row++) {
            for (int col = 0; col < sourceMat.cols(); col++) {
                double val = sourceMat.get(row, col)[0];
                result.put(row, col, val != 0 ? averageColor : black);
            }
        }
        Imgproc.cvtColor(result, result, Imgproc.COLOR_HSV2BGR);
        return result;
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

}
