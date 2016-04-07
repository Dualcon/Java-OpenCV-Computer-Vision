package com.wikidreams.opencv.feature2d;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Feature2DManager {

	private static Logger logger = Logger.getLogger(Feature2DManager.class); 	

	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}


	public static Mat fileToMat(File file) {
		try {
			Mat mat = new Mat();
			BufferedImage image = ImageIO.read(file);	
			byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
			mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
			mat.put(0, 0, data);
			return mat;
		} catch (IOException e) {
			Feature2DManager.logger.error(e.getMessage());			
		}
		return null;
	}

	public static void matToFile(Mat mat, File file) {
		try {
			byte[] data = new byte[mat.rows() * mat.cols() * (int)(mat.elemSize())];
			mat.get(0, 0, data);
			BufferedImage image1 = new BufferedImage(mat.cols(),mat.rows(), BufferedImage.TYPE_3BYTE_BGR);
			image1.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
			ImageIO.write(image1, "jpg", file);
		} catch (IOException e) {
			Feature2DManager.logger.error(e.getMessage());
		}
	}

	public static Mat toGrayScale(Mat mat) {
		Mat matGray = new Mat(); 
		Imgproc.cvtColor(mat, matGray, Imgproc.COLOR_RGB2GRAY);
		return matGray;	
	}

	public static Feature2DData detect(String img) {
		Mat mat = Imgcodecs.imread(img);
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		MatOfKeyPoint keypoints = new MatOfKeyPoint();
		Mat descriptors = new Mat();
		detector.detect(mat, keypoints);
		extractor.compute(mat, keypoints, descriptors);
		System.out.println("keypoints: " + keypoints.toArray().length);
		System.out.println("descriptors:" + descriptors);
		return new Feature2DData(keypoints, descriptors);
	}

	public static MatOfDMatch getMatches(Mat descriptor1, Mat descriptor2) {
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
		MatOfDMatch matches = new MatOfDMatch();
		matcher.match(descriptor1, descriptor2, matches);
		System.out.println("matches.size(): " + matches.size());
		return matches;
	}

	public static MatOfDMatch checkMatchesOfKeyPoints(MatOfDMatch matches) {
		double max_dist = 0; double min_dist = 100;
		List<DMatch> matchesList = matches.toList();
		for (int i = 0; i < matchesList.size(); i++) {
			Double dist = (double) matchesList.get(i).distance;
			if (dist < min_dist && dist != 0) min_dist = dist;
			if (dist > max_dist) max_dist = dist;
		}
		System.out.println("max_dist=" + max_dist + ", min_dist=" + min_dist);

		double threshold = 3 * min_dist;
		double threshold2 = 2 * min_dist;
		if (threshold2 >= max_dist) {
			threshold = min_dist * 1.1;
		} else if (threshold >= max_dist) {
			threshold = threshold2 * 1.4;
		}
		System.out.println("Threshold : "+threshold);

		// Extract good images (distances are under Threshold)
		// But if you try more and more, the value is more precise.
		MatOfDMatch matchesFiltered = new MatOfDMatch();
		List<DMatch> bestMatches= new ArrayList<DMatch>();	    
		for (int i = 0; i < matchesList.size(); i++) {
			Double dist = (double) matchesList.get(i).distance;
			//System.out.println(String.format(i + " match distance best : %s", dist));
			if (dist < threshold) {
				bestMatches.add(matches.toList().get(i));
				System.out.println(String.format(i + " best match added : %s", dist));
			}
		}

		matchesFiltered.fromList(bestMatches);
		System.out.println("matchesFiltered.size() : " + matchesFiltered.size());
		return matchesFiltered;
	}

	public static Mat drawMatches(Mat img1, MatOfKeyPoint keypoints1, Mat img2, MatOfKeyPoint keypoints2, MatOfDMatch matches1to2) {
		Mat outImg = new Mat();
		Features2d.drawMatches(img1, keypoints1, img2, keypoints2, matches1to2, outImg, new Scalar(0, 255, 0), new Scalar(0, 0, 255), new MatOfByte(), Features2d.NOT_DRAW_SINGLE_POINTS);
		return outImg;
	}

	public static Boolean objectDetection(MatOfDMatch bestMatches, int threshold) {
		List<DMatch> bestMatchesList = bestMatches.toList();
		if (bestMatchesList.size() >= threshold) return true;
		return false;
	}

}