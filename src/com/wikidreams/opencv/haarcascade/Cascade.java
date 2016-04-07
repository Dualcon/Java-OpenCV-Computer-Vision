package com.wikidreams.opencv.haarcascade;

import org.apache.log4j.Logger;
import org.opencv.core.Core;
import org.opencv.objdetect.CascadeClassifier;

/**
 * @author WIT Software - Daniel Vieira
 * @version 1.0 - 2016
 */

public class Cascade {

	private Logger logger = Logger.getLogger(Cascade.class);

	static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private CascadeClassifier cascadeClassifier; // OpenCV cascade classifier
	private String path;

	public Cascade(String path) {
		// Create cascade classifier
		this.cascadeClassifier = new CascadeClassifier(path);

		// Test if cascade classifier was successfully created
		if (this.cascadeClassifier.empty()) {
			this.logger.error("Can not create classifier.");
			return;
		}

		this.path = path;
	}

	public CascadeClassifier getCascadeClassifier() {
		return cascadeClassifier;
	}

	public String getPath() {
		return path;
	}

}
