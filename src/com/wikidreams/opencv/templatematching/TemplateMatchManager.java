package com.wikidreams.opencv.templatematching;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class TemplateMatchManager {


	public static void run(String inFile, String templateFile, String outFile, int match_method) {
		Mat img = Imgcodecs.imread(inFile, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Mat templ = Imgcodecs.imread(templateFile, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		
		if ((img.cols() != templ.cols()) && (img.rows() != templ.rows())) {
			System.out.println("the images must have the same dimensions.");
			return;
		}
		
		Mat imgDisplay = img.clone();

		// / Create the result matrix
		int result_cols = img.cols() - templ.cols() + 1;
		int result_rows = img.rows() - templ.rows() + 1;
		Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

		// / Do the Matching and Normalize
		Imgproc.matchTemplate(img, templ, result, match_method);
		Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

		// Localizing the best match with minMaxLoc.
		Point matchLoc;
		MinMaxLocResult mmr;
		boolean iterate = true;
		do{
			mmr = Core.minMaxLoc(result);
			matchLoc = mmr.maxLoc;

			if(mmr.maxVal < 0.99) {
				iterate = false;
			}

			if(iterate) {
				// / Draw rectangle.
				Imgproc.rectangle(imgDisplay, matchLoc, new Point(matchLoc.x + templ.cols(),
						matchLoc.y + templ.rows()), new Scalar(0, 255, 0), 2,8,0);
				Imgproc.rectangle(result, new Point(matchLoc.x - templ.cols()/2, matchLoc.y - templ.rows()/2), new Point(matchLoc.x + templ.cols()/2 , matchLoc.y + templ.rows()/2), new Scalar(0, 0, 0), 2,8,0);
				Imgproc.rectangle(result, new Point(matchLoc.x - templ.cols()/2, matchLoc.y - templ.rows()/2), new Point(matchLoc.x + templ.cols()/2 , matchLoc.y + templ.rows()/2), new Scalar(0, 0, 0), -1);
				// Save the visualized detection.
				System.out.println("Writing "+ outFile);
				Imgcodecs.imwrite(outFile, imgDisplay);
			}
		}while(mmr.maxVal > 0.99);

		System.out.println("Max: " + mmr.maxVal + " Min: " + mmr.minVal);
	}


}
