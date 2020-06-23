package com.bookmyride.demo.model;

import java.math.BigDecimal;

/**
 * 
 * @author seeya.wamane
 *
 */
public class CabInfo {
	
	private Long id;
	private CabType type;
	private String cabNumber;
	private String description;
	private BigDecimal distance;
	private User driverInfo;
	
	public CabInfo(Long id, CabType type, String cabNumber, String description, BigDecimal distance, User driverInfo) {
		super();
		this.id = id;
		this.type = type;
		this.cabNumber = cabNumber;
		this.description = description;
		this.distance = distance;
		this.driverInfo = driverInfo;
	}

	public Long getId() {
		return id;
	}

	public CabType getType() {
		return type;
	}

	public String getCabNumber() {
		return cabNumber;
	}

	public String getDescription() {
		return description;
	}

	public BigDecimal getDistance() {
		return distance;
	}

	public User getDriverInfo() {
		return driverInfo;
	}
	
}
