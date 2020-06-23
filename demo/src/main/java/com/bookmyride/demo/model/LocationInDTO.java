package com.bookmyride.demo.model;

/**
 * 
 * @author seeya.wamane
 *
 */
public class LocationInDTO {
	
	private Double latitude;
	private Double longitude;
	private String name;
	
	public LocationInDTO(Double latitude, Double longitude,String name) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public String getName() {
		return name;
	}
	
	
	
}
