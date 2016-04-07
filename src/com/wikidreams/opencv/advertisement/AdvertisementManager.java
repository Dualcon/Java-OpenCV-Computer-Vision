package com.wikidreams.opencv.advertisement;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * @author WIT Software - Daniel Vieira
 * @version 1.0 - 2016
 */

public class AdvertisementManager {

	private Logger logger = Logger.getLogger(AdvertisementManager.class);

	private int range;
	private ArrayList<Integer> framesWithObject;
	private File originalVideoFile;
	private File newlVideoFile;

	private ArrayList<Advertisement> finalRanges;

	public AdvertisementManager(int range, ArrayList<Integer> framesWithObject, File originalVideoFile, File newVideoFile) {
		super();
		this.range = range;
		this.framesWithObject = framesWithObject;
		this.originalVideoFile = originalVideoFile;
		this.newlVideoFile = newVideoFile;
		this.finalRanges = new ArrayList<>();
	}

	private Boolean generateRanges() {
		this.framesWithObject = new ArrayList<>();
		this.framesWithObject.add(100);
		this.framesWithObject.add(200);
		this.framesWithObject.add(400);
		this.framesWithObject.add(401);
		this.framesWithObject.add(800);
		this.framesWithObject.add(1000);
		this.framesWithObject.add(2000);
		this.framesWithObject.add(2150);
		this.framesWithObject.add(7000);

		if (this.framesWithObject.size() <= 1) {
			this.logger.error("There are no emought frames.");
			return false;
		}

		Boolean foundStart = false;
		int startRange = 0;
		int endRange = 0;

		for (int item : this.framesWithObject) {

			if (foundStart) {

				if (item <= (startRange + this.range)) {
					endRange = item;
					if (this.framesWithObject.get(this.framesWithObject.size() - 1) == item) {
						this.finalRanges.add(new Advertisement(startRange, endRange));
						break;
					}
				} else {

					if (endRange == 0) {
						startRange = item;
					} else {

						if (item <= (endRange + this.range)) {
							endRange = item;
						} else {
							this.finalRanges.add(new Advertisement(startRange, endRange));
							endRange = 0;
							foundStart = false;				
						}
					}
				}
			}

			if (! foundStart) {
				startRange = item;
				foundStart = true;	
			}

		}

		return true;
	}


	public Boolean recordVideoWithoutAdvertisement() {
		Boolean result = this.generateRanges();
		if (result) {
			new RecordManager(this.originalVideoFile, this.newlVideoFile, this.finalRanges);			
			for (Advertisement r : this.finalRanges) {
				System.out.println(r.getStart() + " - " + r.getEnd());
			}
			System.out.println(this.finalRanges.size());	
		}
		return result; 
	}


}
