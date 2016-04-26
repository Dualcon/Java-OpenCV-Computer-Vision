package com.wikidreams.opencv.haarcascade;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import com.wikidreams.utils.dates.DateManager;

/**
 * @author WIT Software - Daniel Vieira
 * @version 1.0 - 2016
 */

public class Converter {

	private static Logger logger = Logger.getLogger(Converter.class);

	public static Mat fileToMat(String imageFilePath) {
		Mat image = Imgcodecs.imread(imageFilePath); 
		if (image.empty()) {
			Converter.logger.error("Empty image.");
			return null;
		}
		return image;
	}

	public static void matToFile(Mat image, String path) {
		Imgcodecs.imwrite(path, image);   
	}

	public static Mat byteToMat(byte[] image) {
		Mat mat = Imgcodecs.imdecode(new MatOfByte(image), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
		return mat;    
	}

	public static byte[] matToByte(Mat mat) {
		MatOfByte buf = new MatOfByte();
		Imgcodecs.imencode(".jpg", mat, buf);
		byte[] imageBytes = buf.toArray();
		return imageBytes;
	}

	public static BufferedImage byteToBufferedImage(byte[] image) {
		try {
			InputStream in = new ByteArrayInputStream(image);
			BufferedImage bi = ImageIO.read(in);
			return bi;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] bufferedImageToByte(BufferedImage image) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", baos);
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();
			return imageInByte;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static BufferedImage imageToBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}

	public static void spliteVideoByFrames(File video, File framesDestination, int range) {
		int totalFrames = 1;
		int currentFrame = 1;
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(video);
		try {
			grabber.start();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			e.printStackTrace();
		}

		while (true) {

			try {
				Frame frame = grabber.grabImage();
				if (frame == null) {       
					break;
				}

				// Splite images by range
				if (range == currentFrame) {
					try {
						BufferedImage image = Converter.javacvFrameToBufferedImage(frame);
						ImageIO.write(image, "jpg", new File(framesDestination.getAbsolutePath() + "\\" + DateManager.getDate() + " " + totalFrames + ".jpg"));
						currentFrame = 0;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
				e.printStackTrace();
			}

			totalFrames += 1;
			currentFrame += 1;				
		}

	}

	public static BufferedImage javacvFrameToBufferedImage(Frame frame) {
		// JavaCV converter to BufferedImage
		Java2DFrameConverter converterToBI = new Java2DFrameConverter();
		BufferedImage image = converterToBI.convert(frame);
		return image;
	}

	public static void showImagePixels(String img) {
		Mat image = Imgcodecs.imread(img);
		for (int i=0; i<50; i++) {
			for (int j=0; j<50; j++) {
				System.out.println("Pixel: " + i + " " + j);
				double[] temp = image.get(i, j);
				for (int k=0; k<temp.length; k++) {
					System.out.println(temp[k]);
				}
			}
		}
	}

	public static Mat toGrayscale(String img) {
		Mat image = Imgcodecs.imread(img, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		return image;
	}

}
