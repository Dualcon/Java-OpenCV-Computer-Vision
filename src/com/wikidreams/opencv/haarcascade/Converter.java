package com.wikidreams.opencv.haarcascade;

import java.awt.AlphaComposite;
import java.awt.Color;
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
import org.opencv.core.Core;
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

	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}


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

	public static BufferedImage toGrayScale(BufferedImage image) {
		for(int i=0; i<image.getHeight(); i++){
			for(int j=0; j<image.getWidth(); j++){
				Color c = new Color(image.getRGB(j, i));
				int red = (int)(c.getRed() * 0.299);
				int green = (int)(c.getGreen() * 0.587);
				int blue = (int)(c.getBlue() *0.114);
				Color newColor = new Color(red+green+blue, red+green+blue,red+green+blue);
				// More methods to color:
				// brighter() It creates a new Color that is a brighter version of this Color.
				// darker() It creates a new Color that is a darker version of this Color.
				image.setRGB(j,i,newColor.getRGB());
			}
		}
		return image;
	}

	public static BufferedImage scaleImageInRatio(Image image, Double width, Double height) {
		BufferedImage originalImage = (BufferedImage) image;
		if ((originalImage.getWidth() <= width) && (originalImage.getWidth() <= height)) {
			return originalImage;
		}

		double windowRatio = width / height;
		double imageRatio = (double) originalImage.getWidth() / originalImage.getHeight();
		double scaleRatio = 0.0;
		if (windowRatio > imageRatio) {
			scaleRatio = height / originalImage.getHeight();
		} else {
			scaleRatio = width / originalImage.getWidth();
		}

		BufferedImage scaledBI = null;
		if(scaleRatio < 1) {
			double wr = originalImage.getWidth() * scaleRatio;
			double hr = originalImage.getHeight() * scaleRatio;
			int w = (int) wr;
			int h = (int) hr;
			System.out.println("New w: " + w + " h: " + h);
			scaledBI = Converter.scaleImage(originalImage, w, h);
		}
		return scaledBI;
	}

	public static BufferedImage scaleImage(BufferedImage image, int w, int h) {
		Boolean preserveAlpha = true;
		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage scaledBI = new BufferedImage(w, h, imageType);
		Graphics2D g = scaledBI.createGraphics();
		if (preserveAlpha) {
			g.setComposite(AlphaComposite.Src);
		}
		g.drawImage(image, 0, 0, w, h, null); 
		g.dispose();
		return scaledBI;
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
						// TODO Auto-generated catch block
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

}
