package com.wikidreams.image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * @author WIT Software - Daniel Vieira
 * @version 1.0 - 2016
 * <p>This class is responsible to create some transformations on images Like rotations or scales.</p>
 */

public class ImageTransformation {


	public static BufferedImage rotate180( BufferedImage inputImage ) {
		int width = inputImage.getWidth(); //the Width of the original image
		int height = inputImage.getHeight();//the Height of the original image
		BufferedImage returnImage = new BufferedImage( width, height, inputImage.getType()  );
		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				returnImage.setRGB( width - x - 1, height - y - 1, inputImage.getRGB( x, y  )  );
			}
		}
		return returnImage;
	}



	public static BufferedImage rotate90ToLeft( BufferedImage inputImage ){
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		BufferedImage returnImage = new BufferedImage( height, width , inputImage.getType()  );
		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				returnImage.setRGB(y, width - x - 1, inputImage.getRGB( x, y  )  );
			}
		}
		return returnImage;
	}



	public static BufferedImage rotate90ToRight( BufferedImage inputImage ){
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		BufferedImage returnImage = new BufferedImage( height, width , inputImage.getType()  );
		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				returnImage.setRGB( height - y -1, x, inputImage.getRGB( x, y  )  );
			}
		}
		return returnImage;
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



	public static BufferedImage scaleInRatio(BufferedImage originalImage, Double width, Double height) {
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
			scaledBI = ImageTransformation.scaleImage(originalImage, w, h);
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

}
