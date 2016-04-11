package com.wikidreams.image;

import java.awt.image.BufferedImage;

/**
 * @author WIT Software - Daniel Vieira
 * @version 1.0 - 2016
 * <p>This class is responsible to create some transformations on images Like rotations.</p>
 */

public class ImageRotation {


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

}
