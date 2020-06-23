package com.bookmyride.demo.model;

/**
 * 
 * @author seeya.wamane
 *
 */
public class TripInfo {
	
	/*
	 * id:ID! userInfo:User! cabInfo:CabInfo! destination:Location! source:Location!
	 * fare:Int! paymentMode:PaymentMode distanceKm:Int createTs:Long! updateTs:Long
	 */
	
	private Long id;
	private User userInfo;
	private CabInfo cabInfo;
	private Location destination;
	private Location source;
	private Double fare;
	private Double distanceKm;
	private Long createTs;
	private Long updateTs;
	private PaymentMode paymentMode;
	
	public TripInfo(Long id, User userInfo, CabInfo cabInfo, Location destination, Location source, Double fare,
			Double distanceKm, Long createTs, Long updateTs, PaymentMode paymentMode) {
		super();
		this.id = id;
		this.userInfo = userInfo;
		this.cabInfo = cabInfo;
		this.destination = destination;
		this.source = source;
		this.fare = fare;
		this.distanceKm = distanceKm;
		this.createTs = createTs;
		this.updateTs = updateTs;
		this.paymentMode = paymentMode;
	}

	public Long getId() {
		return id;
	}

	public User getUserInfo() {
		return userInfo;
	}

	public CabInfo getCabInfo() {
		return cabInfo;
	}

	public Location getDestination() {
		return destination;
	}

	public Location getSource() {
		return source;
	}

	public Double getFare() {
		return fare;
	}

	public Double getDistanceKm() {
		return distanceKm;
	}

	public Long getCreateTs() {
		return createTs;
	}

	public Long getUpdateTs() {
		return updateTs;
	}

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

}
