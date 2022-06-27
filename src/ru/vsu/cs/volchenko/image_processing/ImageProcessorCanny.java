package ru.vsu.cs.volchenko.image_processing;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import ru.vsu.cs.volchenko.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessorCanny {

    public ImageProcessorCanny() {}

    public ImageProcessorContext process(BufferedImage sourceImage,
                                         int kSizeW, int kSizeH, int sigmaX, int sigmaY,
                                         int thresholdMin, int thresholdMax,
                                         int apertureSize, boolean l2Gradient,
                                         int method, int colorThreshold) throws Exception {

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

            Mat greenFilter = ImageUtils.findColor(hsvBoundedMat, ImageUtils::getHSVGreen);
            Mat yellowFilter = ImageUtils.findColor(hsvBoundedMat, ImageUtils::getHSVYellow);
            Mat blueFilter = ImageUtils.findColor(hsvBoundedMat, ImageUtils::getHSVBlue);
            Mat redFilter = ImageUtils.findColor(hsvBoundedMat, ImageUtils::getHSVRed);

            MatWithMatches greenResult = ImageUtils.paintAndCalcMatches(greenFilter, ImageUtils::getHSVGreen);
            MatWithMatches yellowResult = ImageUtils.paintAndCalcMatches(yellowFilter, ImageUtils::getHSVYellow);
            MatWithMatches blueResult = ImageUtils.paintAndCalcMatches(blueFilter, ImageUtils::getHSVBlue);
            MatWithMatches redResult = ImageUtils.paintAndCalcMatches(redFilter, ImageUtils::getHSVRed);

            double sizeOfBoundedMat = boundedMat.cols() * boundedMat.rows();
            ImageUtils.ObjectColor resultColor = ImageUtils.analyzeColor(greenResult.countOfMatches / sizeOfBoundedMat,
                    yellowResult.countOfMatches / sizeOfBoundedMat, blueResult.countOfMatches / sizeOfBoundedMat,
                    redResult.countOfMatches / sizeOfBoundedMat, colorThreshold / 100.0);

            ImagesWithColor allFilters = new ImagesWithColor(ImageUtils.mat2bi(boundedMat), ImageUtils.mat2bi(greenResult.mat),
                    ImageUtils.mat2bi(yellowResult.mat), ImageUtils.mat2bi(blueResult.mat), ImageUtils.mat2bi(redResult.mat),
                    resultColor);

            resultContext.listOfColors.add(allFilters);
        }

        resultContext.listOfImages.add(new ImageWithDescription(ImageUtils.mat2bi(grayMat), "Shades of gray"));
        resultContext.listOfImages.add(new ImageWithDescription(ImageUtils.mat2bi(blurredGrayMat), "Blurred shades of gray"));
        resultContext.listOfImages.add(new ImageWithDescription(ImageUtils.mat2bi(cannyMat), "Canny's borders detecting algorithm"));
        resultContext.listOfImages.add(new ImageWithDescription(ImageUtils.mat2bi(contoursFilledMat), "Found contours are filled with red"));
        resultContext.listOfImages.add(new ImageWithDescription(ImageUtils.mat2bi(contoursBoundedMat), "Found contours are bounded with red"));

        resultContext.millisecondsEnd = System.currentTimeMillis();

        return resultContext;
    }

}
