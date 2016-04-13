package com.wikidreams.opencv.haarcascade;

import java.awt.image.BufferedImage;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

/**
 * @author WIT Software - Daniel Vieira
 * @version 1.0 - 2016
 * <p>This class is responsable for the recognition process on image files.</p>
 */

public class ImageManager {

	private long startDuration;
	private long endDuration;
	private long totalDuration;

	private Cascade classifier;

	private String imagePath;
	private Mat originalImage;

	private int totalDetections;
	private MatOfRect detections; 

	public ImageManager(Cascade classifier, String imagePath) {
		this.classifier = classifier;
		this.imagePath = imagePath;
		this.startDuration = 0;
		this.endDuration = 0;
		this.totalDuration = 0;
		this.totalDetections = 0;
		this.originalImage = Converter.fileToMat(this.imagePath);
		this.run();
	}

	public ImageManager(Cascade classifier, Mat image) {
		this.classifier = classifier;
		this.originalImage = image;
		this.startDuration = 0;
		this.endDuration = 0;
		this.totalDuration = 0;
		this.totalDetections = 0;
		this.run();
	}

	public ImageManager(Cascade classifier, BufferedImage image) {
		this.classifier = classifier;
		byte[] imageInBytes = Converter.bufferedImageToByte(image);
		this.originalImage = Converter.byteToMat(imageInBytes);
		this.startDuration = 0;
		this.endDuration = 0;
		this.totalDuration = 0;
		this.totalDetections = 0;
		this.run();
	}

	private void run() {
		this.startDuration = System.nanoTime();
		this.process();
		this.endDuration = System.nanoTime();
		this.totalDuration = (this.endDuration - this.startDuration) / 1000000; 
	}

	private void process() {
		this.detections = Classifier.classifyImage(classifier.getCascadeClassifier(), originalImage); 

		if (detections.toArray().length >= 1) {
			this.totalDetections = detections.toArray().length;
		}
	}

	public long getTotalDuration() {
		return totalDuration;
	}

	public int getTotalDetections() {
		return totalDetections;
	}

	public Mat getOriginalImage() {
		return originalImage;
	}

	public MatOfRect getDetections() {
		return detections;
	}

	public BufferedImage getImageWithDetectedObjects() {
		Mat imageWithRectangle = new Mat();
		imageWithRectangle = Classifier.createRectangle(this.detections, this.originalImage);
		byte[] imageInBytes = Converter.matToByte(imageWithRectangle);
		BufferedImage bi = Converter.byteToBufferedImage(imageInBytes);
		return bi;
	}

}
