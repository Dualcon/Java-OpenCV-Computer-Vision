package com.wikidreams.opencv.videoplayer;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

/**
 * @author WIT Software - Daniel Vieira
 * @version 1.0 - 2016
 * <p>This class allows play video contents.</p>
 */

public class VideoPlayerManager {

	private String title;
	private File file;
	public VideoPlayerManager(String title, File file) {
		super();
		this.title = title;
		this.file = file;

		CanvasFrame canvas = new CanvasFrame(this.title);
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(this.file);
		long frameRate = 33;

		try {
			grabber.start();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			e.printStackTrace();
		}

		TimerTask task = new TimerTask() {
			@Override
			public void run() {

				while (true) {
					try {
						Frame frame = grabber.grabImage();
						if (frame == null) {       
							break;
						}

						canvas.setCanvasSize(frame.imageWidth, frame.imageHeight);
						canvas.showImage(frame);
					} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
						e.printStackTrace();
					}
				}
			}
		};

		Timer timer = new Timer(true);
		timer.schedule(task, 0, frameRate);

	}

}
