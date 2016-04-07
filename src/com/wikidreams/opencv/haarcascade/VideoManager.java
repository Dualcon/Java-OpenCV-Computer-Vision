package com.wikidreams.opencv.haarcascade;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.opencv.core.Core;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @author WIT Software - Daniel Vieira
 * @version 1.0 - 2016
 * <p>This class is responsable for the recognition process on video files.</p>
 */

public class VideoManager {

	private Logger logger = Logger.getLogger(VideoManager.class);

	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private long startDuration;
	private long endDuration;
	private long totalDuration;

	private Cascade classifier;
	private FFmpegFrameGrabber grabber;

	private int totalFrames;
	private ArrayList<Integer> framesWithObject;

	public VideoManager(String filename, Cascade classifier) {
		this.startDuration = 0;
		this.endDuration = 0;
		this.totalDuration = 0;
		this.classifier = classifier;
		this.totalFrames = 0;
		this.framesWithObject = new ArrayList<Integer>();

		// Preload the opencv_objdetect module to work around a known bug.
		Loader.load(opencv_objdetect.class);

		//Declare FrameGrabber to import video from "video.mp4"
		this.grabber = new FFmpegFrameGrabber(new File(filename));
		//this.grabber.setFormat("avi");

		//Start grabber to capture video
		try {
			this.grabber.start();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			this.logger.error("Can not start video.");
			return;
		}

		this.run();
	}


	public void run() {
		this.startDuration = System.nanoTime();
		this.process();
		this.endDuration = System.nanoTime();
		this.totalDuration = (this.endDuration - this.startDuration) / 1000000;
	}


	public void process() {

		// JavaCV converter to BufferedImage
		Java2DFrameConverter converterToBI = new Java2DFrameConverter();

		while (true) {

			try {
				Frame frame = this.grabber.grabImage();

				if (frame == null) {       
					break;
				}

				BufferedImage bufferedImage = null;
				ByteArrayOutputStream baos = null;
				byte[] imageData = null;

				// Converte from JavaCV Frame to BufferedImage
				bufferedImage = converterToBI.convert(frame);

				if (bufferedImage != null) {
					baos = new ByteArrayOutputStream();

					try {
						ImageIO.write(bufferedImage, "jpg", baos );
						baos.flush();
						imageData = baos.toByteArray();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (baos != null) {
					try {
						baos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (imageData != null) {
					org.opencv.core.Mat img = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
					if (! img.empty()) {
						org.opencv.core.Mat mRgba=new org.opencv.core.Mat();  
						org.opencv.core.Mat mGrey=new org.opencv.core.Mat();  
						img.copyTo(mRgba);  
						img.copyTo(mGrey);
						// Grayscale conversion
						Imgproc.cvtColor( mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
						// Equalizes the image for better recognition
						Imgproc.equalizeHist( mGrey, mGrey );
						// Detect how many faces are in the loaded image
						MatOfRect detections = new MatOfRect();
						classifier.getCascadeClassifier().detectMultiScale(mGrey, detections);
						if (detections.toArray().length >= 1) {
							this.framesWithObject.add(this.totalFrames);
						}
					}
				}

				this.totalFrames += 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			this.grabber.release();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			this.logger.error("Can not release the grabber");	
		}

	}


	public ArrayList<Integer> getFramesWithObject() {
		return framesWithObject;
	}


	public long getTotalDuration() {
		return totalDuration;
	}


	public int getTotalFrames() {
		return totalFrames;
	}


}
