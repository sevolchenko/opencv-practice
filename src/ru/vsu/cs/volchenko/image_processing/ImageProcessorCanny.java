package ru.vsu.cs.volchenko.image_processing;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import ru.vsu.cs.volchenko.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ImageProcessorCanny {

    public ImageProcessorCanny() {}

    public ImageProcessorContext process(BufferedImage sourceImage,
                                         int kSizeW, int kSizeH, int sigmaX, int sigmaY,
                                         int thresholdMin, int thresholdMax,
                                         int apertureSize, boolean l2Gradient,
                                         int method) throws Exception {

        ImageProcessorContext resultContext = new ImageProcessorContext();
        resultContext.millisecondsStart = System.currentTimeMillis();

        Mat sourceMat = ImageUtils.bi2mat(sourceImage);

        Mat grayMat = new Mat();
        Imgproc.cvtColor(sourceMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        Mat blurredGrayMat = new Mat();
        Imgproc.GaussianBlur(grayMat, blurredGrayMat, new Size(kSizeW,kSizeH), sigmaX, sigmaY);

        Mat cannyMat = new Mat();
        Imgproc.Canny(blurredGrayMat, cannyMat, thresholdMin, thresholdMax, apertureSize, l2Gradient);

        List<MatOfPoint> listOfContours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(cannyMat, listOfContours, hierarchy, Imgproc.RETR_LIST, method);

        Mat contoursFilledMat = new Mat();
        sourceMat.copyTo(contoursFilledMat);
        for (int i = 0; i < listOfContours.size(); i++) {
            Imgproc.drawContours(contoursFilledMat, listOfContours, i, new Scalar(0, 0, 255), -1, 1, hierarchy, 1);
        }

        Mat contoursBoundedMat = new Mat();
        sourceMat.copyTo(contoursBoundedMat);
        List<Mat> listOfBoundedMat = new ArrayList<>();
        for (MatOfPoint contour : listOfContours) {
            Rect boundingRect = Imgproc.boundingRect(new MatOfPoint(contour.toArray()));
            listOfBoundedMat.add(new Mat(sourceMat, boundingRect));
            Imgproc.rectangle(contoursBoundedMat, boundingRect.tl(), boundingRect.br(), new Scalar(0, 0, 255.0), 1);
        }

        for (Mat boundedMat : listOfBoundedMat) {
            Mat hsvBoundedMat = new Mat();
            Imgproc.cvtColor(boundedMat, hsvBoundedMat, Imgproc.COLOR_BGR2HSV);

            boolean isGreen = findColor(hsvBoundedMat, ImageUtils::getHSVGreen);
            boolean isYellow = findColor(hsvBoundedMat, ImageUtils::getHSVYellow);
            boolean isBlue = findColor(hsvBoundedMat, ImageUtils::getHSVBlue);
            boolean isRed = findColor(hsvBoundedMat, ImageUtils::getHSVRed);

            //todo: results masking to context

            if (countOfTrue(isGreen, isYellow, isBlue, isRed) != 1) {
                resultContext.listOfColors.add(new ImagesWithColor(ImageUtils.mat2bi(boundedMat), ImageProcessorContext.ImageColor.UNDEFINED));
            } else {
                if (isGreen) {
                    resultContext.listOfColors.add(new ImagesWithColor(ImageUtils.mat2bi(boundedMat), ImageProcessorContext.ImageColor.GREEN));
                } else if (isYellow) {
                    resultContext.listOfColors.add(new ImagesWithColor(ImageUtils.mat2bi(boundedMat), ImageProcessorContext.ImageColor.YELLOW));
                } else if (isBlue) {
                    resultContext.listOfColors.add(new ImagesWithColor(ImageUtils.mat2bi(boundedMat), ImageProcessorContext.ImageColor.BLUE));
                } else if (isRed) {
                    resultContext.listOfColors.add(new ImagesWithColor(ImageUtils.mat2bi(boundedMat), ImageProcessorContext.ImageColor.RED));
                }
            }
        }

        resultContext.listOfImages.add(new ImageWithDescription(ImageUtils.mat2bi(grayMat), "Shades of gray"));
        resultContext.listOfImages.add(new ImageWithDescription(ImageUtils.mat2bi(blurredGrayMat), "Blurred shades of gray"));
        resultContext.listOfImages.add(new ImageWithDescription(ImageUtils.mat2bi(cannyMat), "Canny's borders detecting algorithm"));
        resultContext.listOfImages.add(new ImageWithDescription(ImageUtils.mat2bi(contoursFilledMat), "Found contours are filled with red"));
        resultContext.listOfImages.add(new ImageWithDescription(ImageUtils.mat2bi(contoursBoundedMat), "Found contours are bounded with red"));

        resultContext.millisecondsEnd = System.currentTimeMillis();
        return resultContext;
    }

    private boolean findColor(Mat src, Supplier<Scalar[]> sup) {
        Mat binaryMat = new Mat(src.size(), src.type());
        Scalar[] color = sup.get();
        Core.inRange(src, color[0], color[1], binaryMat);
        int match = 0;
        for (int row = 0; row < binaryMat.rows(); row++) {
            for (int col = 0; col < binaryMat.cols(); col++) {
                if (binaryMat.get(row, col)[0] != 0.0) {
                    match++;
                }
            }
        }
        return (binaryMat.rows() * binaryMat.cols() * 0.3 < match); //todo
    }

    private int countOfTrue(boolean... args) {
        int count = 0;
        for (boolean arg : args) {
            if (arg) {
                count++;
            }
        }
        return count;
    }

}
