package com.wikidreams.opencv.haarcascade;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 * @author WIT Software - Daniel Vieira
 * @version 1.0 - 2016
 */

public class Classifier {

	public static MatOfRect classifyImage(CascadeClassifier classifier, Mat image) {
		Mat mRgba = new Mat();  
		Mat mGrey=new Mat();  
		image.copyTo(mRgba);  
		image.copyTo(mGrey);
		// Grayscale conversion
		Imgproc.cvtColor( mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
		// Equalizes the image for better recognition
		Imgproc.equalizeHist( mGrey, mGrey );
		// Detect how many faces are in the loaded image
		MatOfRect detections = new MatOfRect();
		classifier.detectMultiScale(mGrey, detections);
		return detections;
	}


	public static Mat createRectangle(MatOfRect detections, Mat mat) {
		// creates a rectangle into detected informations
		for (Rect rect : detections.toArray()) {
			Imgproc.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 4);
		}
		return mat;
	}

}
