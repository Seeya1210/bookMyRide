package com.bookmyride.demo.dao;

import java.util.List;

import com.bookmyride.demo.model.CabInfo;
import com.bookmyride.demo.model.Location;
import com.bookmyride.demo.model.LocationInDTO;
import com.bookmyride.demo.model.RideInDTO;
import com.bookmyride.demo.model.TripInfo;
import com.bookmyride.demo.model.User;

/**
 * 
 * @author seeya.wamane
 *
 */
public interface RidesDAO {
	/**
	 * Searches rides and displays them in ascending order of their distance from user
	 * @param requestedAttributes
	 * @param location
	 * @return
	 */
	List<CabInfo> searchNearestRides(List<String> requestedAttributes,LocationInDTO location);
	
	/**
	 *  Gets all previous rides details for the user
	 * @param userId
	 * @return
	 */
	List<TripInfo> getRidesHistory(Long userId);
	
	/**
	 * Books ride for the user for given source and destination locations
	 * @param rideDetails
	 * @return
	 */
	TripInfo bookRide(RideInDTO rideDetails);

	/**
	 * Gets cab details by combining all cab ids in single query for user's ride history
	 * @param cabIds
	 * @return
	 */
	List<CabInfo> getCabInfo(List<Long> cabIds);

	/**
	 * Gets locations by combining all location ids for user's ride history
	 * @param locationIds
	 * @return
	 */
	List<Location> getlocationsById(List<Long> locationIds);

	/**
	 * Gets drive infos by combining all drives's ids for user's ride history
	 * @param driverIds
	 * @return
	 */
	List<User> getDriverByIds(List<Long> driverIds);

}
