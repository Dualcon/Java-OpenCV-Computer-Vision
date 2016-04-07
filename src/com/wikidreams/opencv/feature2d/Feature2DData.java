package com.wikidreams.opencv.feature2d;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

public class Feature2DData {

	private MatOfKeyPoint keypoints;
	private Mat descriptor;

	public Feature2DData(MatOfKeyPoint keypoints, Mat descriptor) {
		super();
		this.keypoints = keypoints;
		this.descriptor = descriptor;
	}

	public MatOfKeyPoint getKeypoints() {
		return keypoints;
	}

	public void setKeypoints(MatOfKeyPoint keypoints) {
		this.keypoints = keypoints;
	}

	public Mat getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(Mat descriptor) {
		this.descriptor = descriptor;
	}


}
