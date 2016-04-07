package com.wikidreams.opencv.advertisement;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

/**
 * @author WIT Software - Daniel Vieira
 * @version 1.0 - 2016
 */

public class RecordManager {

	private Logger logger = Logger.getLogger(RecordManager.class);

	private File originalVideo;
	private File newVideo;
	private ArrayList<Advertisement> objectRanges;

	private FFmpegFrameGrabber grabber;
	private FFmpegFrameRecorder recorder;
	private Frame frame;

	private int currentFrame;

	public RecordManager(File originalVideo, File newVideo, ArrayList<Advertisement> objectRanges) {
		super();
		this.originalVideo = originalVideo;
		this.newVideo = newVideo;
		this.objectRanges = objectRanges;
		this.recordProcess();
	}

	private void recordProcess() {
		this.currentFrame = 0;
		//Import and start the original video.
		try {
			this.grabber = new FFmpegFrameGrabber(this.originalVideo);
			this.grabber.start();
		} catch (Exception e) {
			this.logger.error("Can not load video file.");
			return;
		}

		// Configure and start the recorder.
		try {
			this.recorder = new FFmpegFrameRecorder(this.newVideo, this.grabber.getImageWidth(), this.grabber.getImageHeight());
			this.recorder.setFormat("mp4");  
			this.recorder.setFrameRate(this.grabber.getFrameRate());
			this.recorder.setVideoBitrate(this.grabber.getVideoBitrate());
			this.recorder.start();
		} catch (Exception e) {
			this.logger.error("Can not record video file.");
			return;
		}

		while (true) {
			try {
				this.frame = this.grabber.grabImage();
			} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
				this.logger.error("Can not grab frame.");
			}

			if (this.frame == null) {       
				break;
			}

			if (this.identifyFrame(this.currentFrame)) {
				// Record frame
				try {
					this.recorder.record(frame);
				} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
					this.logger.info(e.getMessage());
				}
			}

			this.currentFrame += 1;
		}

		// Close grabbers
		try {
			this.grabber.stop();
			this.grabber.release();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			this.logger.error(e.getMessage());
		}

		try {
			this.recorder.stop();
			this.recorder.release();
		} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
			this.logger.error(e.getMessage());
		}
	}


	private boolean identifyFrame(int frameNumber) {
		for (Advertisement ad : this.objectRanges) {
			if ((frameNumber >= ad.getStart()) && (frameNumber <= ad.getEnd()) ) {
				return true;
			}
		}
		return false;
	}


}
