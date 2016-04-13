package com.wikidreams.opencv.feature2d;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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

/**
 * @author WIT Software - Daniel Vieira
 * @version 1.0 - 2016
 * <p>Feature2D recognition process.</p>
 */

public class Feature2DManager {

	private static Logger logger = Logger.getLogger(Feature2DManager.class); 	

	public static Feature2DData detect(File image) {
		Mat mat = Imgcodecs.imread(image.getAbsolutePath());
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		MatOfKeyPoint keypoints = new MatOfKeyPoint();
		Mat descriptors = new Mat();
		detector.detect(mat, keypoints);
		extractor.compute(mat, keypoints, descriptors);
		System.out.println("Keypoints: " + keypoints.toArray().length + " Descriptors: " + descriptors);
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
				//System.out.println(String.format(i + " best match added : %s", dist));
			}
		}

		matchesFiltered.fromList(bestMatches);
		System.out.println("matchesFiltered.size() : " + matchesFiltered.size());
		return matchesFiltered;
	}

	public static Mat drawMatches(File image1, MatOfKeyPoint keypoints1, File image2, MatOfKeyPoint keypoints2, MatOfDMatch matches1to2) {
		Mat img1 = Imgcodecs.imread(image1.getAbsolutePath());
		Mat img2 = Imgcodecs.imread(image2.getAbsolutePath());
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
