package com.bookmyride.demo.model;

/**
 * 
 * @author seeya.wamane
 *
 */
public class RideInDTO {
	
	private Long userId;
	private Long cabId;
	private LocationInDTO destination;
	private LocationInDTO source;
	
	public RideInDTO(Long userId, Long cabId, LocationInDTO destination, LocationInDTO source) {
		super();
		this.userId = userId;
		this.cabId = cabId;
		this.destination = destination;
		this.source = source;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getCabId() {
		return cabId;
	}

	public LocationInDTO getDestination() {
		return destination;
	}

	public LocationInDTO getSource() {
		return source;
	}
	
	
	

}
