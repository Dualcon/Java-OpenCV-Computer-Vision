package com.wikidreams.opencv.haarcascade;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

/**
 * @author WIT Software - Daniel Vieira
 * @version 1.0 - 2016
 * <p>This class is responsable for the recognition process using the web cam.</p>
 */

public class CameraManager {

	private Logger logger = Logger.getLogger(CameraManager.class);

	private long startDuration;
	private long endDuration;

	private VideoCapture capture;

	private boolean start;

	private Cascade classifier;

	private int totalFrames;

	public CameraManager(Cascade classifier) {
		this.totalFrames = 0;
		this.startDuration = 0;
		this.endDuration = 0;
		this.classifier = classifier;

		// Create video capture
		this.capture = new VideoCapture(0);
		if (! this.capture.isOpened()) {
			this.logger.error("Can not start webcam.");
			return;
		}

		this.run();
	}

	private void run() {
		this.start = true;
		this.startDuration = System.nanoTime();
		this.process();
		this.endDuration = System.nanoTime();
		this.logger.info("Total duration: " + (this.endDuration - this.startDuration) / 1000000);
		this.logger.info(this.totalFrames + " processed frames.");
	}


	public void process() {

		Mat matImage = new Mat();

		while (this.capture.read(matImage)) {

			if (matImage == null) {
				break;
			}

			new ImageManager(classifier, matImage);

			this.totalFrames += 1;
		}

		this.capture.release();
	}


	public void stopCamera() {
		if (this.start == true && this.capture.isOpened()) {
			this.start = false;
			this.capture.release();
			this.logger.info("Camera stopped by user.");
		}
	}


}
