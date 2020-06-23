package com.bookmyride.demo.model;

/**
 * 
 * @author seeya.wamane
 *
 */
public class Location {
	
	private Long id;
	private String name;
	private Double longitude;
	private Double latitude;

	
	public Location() {
		super();
	}

	public Location(Long id, String name, Double longitude, Double latitude) {
		super();
		this.id = id;
		this.name = name;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public Location(String name, Double latitude, Double longitude) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// return string representation of this point
	public String toString() {
		return name + " (" + latitude + ", " + longitude + ")";
	}

	public String getName() {
		return name;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

}
