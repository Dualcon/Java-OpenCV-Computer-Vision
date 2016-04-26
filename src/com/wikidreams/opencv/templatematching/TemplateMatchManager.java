package com.wikidreams.opencv.templatematching;

import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import com.wikidreams.image.ImageTransformation;
import com.wikidreams.opencv.haarcascade.Converter;

/**
 * @author WIT Software - Daniel Vieira
 * @version 1.0 - 2016
 * <p>Template matching recognition process.</p>
 */

public class TemplateMatchManager {

	/**
	 * Template Matching is a method for searching and finding the location of a template image in a larger image. OpenCV comes with a function cv2.matchTemplate() for this purpose. It simply slides the template image over the input image (as in 2D convolution) and compares the template and patch of input image under the template image.
	 * Template matching works by "sliding" the template across the original image. As it slides, it compares or matches the template to the portion of the image directly under it.
	 * It does this matching by calculating a number. This number denotes the extent to which the template and the portion of the original are equal. The actual number depends on the calculation used. Some denote a complete match by a 0 (indicating no difference between the template and the portion of original) or a 1 (indicating a complete match). 
	 * When you perform template matching in OpenCV, you get an image that shows the degree of "equality" or correlation between the template and the portion under the template. 
	 * The template is compared against its background, and the result of the calculation (a number) is stored at the top left pixel.
	 * The NORMED calculations give values upto 1.0... the other ones return huge values.
	 * SQDIFF is a difference based calculation that gives a 0 at a perfect match. The other two (CCORR and CCOEFF) are correlation based, and return a 1.0 for a perfect match.
	 * @param image - Image where the search is running. It must be 8-bit or 32-bit floating-point.
	 * @param template – Searched template. It must be not greater than the source image and have the same data type.
	 * @param match_method
	 * @return
	 */
	public static BufferedImage run(BufferedImage image, BufferedImage template, int match_method) {

		BufferedImage grayImage = ImageTransformation.toGrayScale(image);
		BufferedImage grayTemplate = ImageTransformation.toGrayScale(template); 

		byte[] imageInBytes = Converter.bufferedImageToByte(grayImage);
		byte[] templateInBytes = Converter.bufferedImageToByte(grayTemplate);
		Mat imageMat = Converter.byteToMat(imageInBytes);
		Mat templateMat = Converter.byteToMat(templateInBytes);

		// Choose from the diferent methods type.
		int method;
		switch (match_method) {
		case 1: method = Imgproc.TM_CCOEFF; // Correlation coefficient
		break;
		case 2: method = Imgproc.TM_CCORR; // Cross correlation
		break;
		case 3: method = Imgproc.TM_CCORR_NORMED; // Normalized cross correlation
		break;
		case 4: method = Imgproc.TM_SQDIFF; // Squared difference
		break;
		case 5: method = Imgproc.TM_SQDIFF_NORMED; // Normalized squared difference 
		break;
		default: method = Imgproc.TM_CCOEFF_NORMED; // Normalized correlation coefficient (NCC - Fast Normalized Cross-Correlation)
		break;
		}

		System.out.println("Method: " + method);
		// / Create the result matrix
		int result_cols = imageMat.cols() - templateMat.cols() + 1;
		int result_rows = imageMat.rows() - templateMat.rows() + 1;
		Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

		// / Do the Matching and Normalize
		Imgproc.matchTemplate(imageMat, templateMat, result, method);
		//Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

		// / Localizing the best match with minMaxLoc
		MinMaxLocResult mmr = Core.minMaxLoc(result);
		Point matchLoc;

		if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
			matchLoc = mmr.minLoc;
			System.out.println("Min: " + mmr.minVal);
		} else {
			matchLoc = mmr.maxLoc;
			System.out.println("Max: " + mmr.maxVal);
		}

		// / Draw rectangle.
		Imgproc.rectangle(imageMat, matchLoc, new Point(matchLoc.x + templateMat.cols(),
				matchLoc.y + templateMat.rows()), new Scalar(0, 255, 0));

		byte[] imgDisplayIbBytes = Converter.matToByte(imageMat);
		BufferedImage bi = Converter.byteToBufferedImage(imgDisplayIbBytes);
		return bi;
	}


}
