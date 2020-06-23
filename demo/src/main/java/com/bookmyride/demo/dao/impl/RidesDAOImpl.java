package com.bookmyride.demo.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bookmyride.demo.dao.RidesDAO;
import com.bookmyride.demo.exception.SystemFailureException;
import com.bookmyride.demo.exception.TripInfoNotFoundException;
import com.bookmyride.demo.model.CabInfo;
import com.bookmyride.demo.model.CabType;
import com.bookmyride.demo.model.Location;
import com.bookmyride.demo.model.LocationInDTO;
import com.bookmyride.demo.model.RideInDTO;
import com.bookmyride.demo.model.TripInfo;
import com.bookmyride.demo.model.User;
import com.bookmyride.demo.resolver.LocationProvider;

/**
 * 
 * @author seeya.wamane
 *
 */
@Repository
public class RidesDAOImpl implements RidesDAO {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	BookRideStoredProcedure bookRideStoredProcedure;

	@Autowired
	LocationProvider locationProvider;
	
	Logger logger = LoggerFactory.getLogger(RidesDAOImpl.class); 

	private static final String SEARCH_CABS_WITH_DRIVER_INFO_QUERY = "Select cab.id as id, cab.type as type, cab.number as number, cab.description as description,  user.id as driverId, user.first_name as firstName, user.last_name as lastName, user.phone_no as contactNumber, user.email as email from cab_info cab LEFT JOIN User user ON cab.driver_id = user.id";
	private static final String SEARCH_CABS_QUERY = "Select * from cab_info cab";
	private static final String SEARCH_RIDES_BY_USER_ID = "Select trip.id as id, trip.create_ts as createTs, trip.source_id as sourceId, trip.destination_id as destinationId, trip.cab_id as cabId from trip_info trip where trip.user_id = ?";// LEFT
	private static final String SEARCH_CABS_BY_IDS = "Select * from cab_info cab where id in (";
	private static final String SEARCH_LOCATION_BY_IDS = "Select * from location location where id in (";
	private static final String SEARCH_DRIVER_BY_IDS = "Select * from user location where id in (";

	@PostConstruct
	public void postConstruct() {
		bookRideStoredProcedure = new BookRideStoredProcedure(jdbcTemplate);
	}

	@Override
	public List<CabInfo> searchNearestRides(List<String> requestedAttributes, LocationInDTO location) {
		logger.info("Executing RidesDAOImpl.searchNearestRides()");
		String query = null;
		if (requestedAttributes.contains("driverInfo")) {
			query = SEARCH_CABS_WITH_DRIVER_INFO_QUERY;
		} else {
			query = SEARCH_CABS_QUERY;
		}
		List<CabInfo> cabs = null;
		logger.debug("Search nearest ride query : {} ", query);
		try {
			cabs = jdbcTemplate.query(query, new RowMapper<CabInfo>() {
				@Override
				public CabInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
					User driverInfo = null;
					if (requestedAttributes.contains("driverInfo")) {
						driverInfo = new User(rs.getLong("driverId"), rs.getString("firstName"),
								rs.getString("lastName"), rs.getString("contactNumber"), rs.getString("email"));
					}
					Double distance = getCabDistance(location);
					CabInfo cabInfo = new CabInfo(rs.getLong("id"), CabType.valueOf(rs.getString("type")),
							rs.getString("number"), rs.getString("description"),
							new BigDecimal(distance).setScale(2, BigDecimal.ROUND_UP), driverInfo);
					return cabInfo;
				}

			});
		} catch (DataAccessException e) {
			logger.error("Error occured while fetching cab history ");
			throw new SystemFailureException("Error occured while fetching cab history ", e);
		}
		logger.info("Exiting RidesDAOImpl.searchNearestRides()");
		return cabs;
	}

	/**
	 * Dummy method to associate random current location for a cab
	 * 
	 * @param location
	 * @return
	 */
	private Double getCabDistance(LocationInDTO location) {

		Location cabLocation = locationProvider.getCurrentLocation();
		logger.info("Dummy cab Location: latitude {}, logitude {}", cabLocation.getLatitude(),cabLocation.getLongitude());
		Double STATUTE_KM_PER_NAUTICAL_MILE = 0.539957;
		Double lat1 = Math.toRadians(location.getLatitude());
		Double lon1 = Math.toRadians(location.getLongitude());
		Double lat2 = Math.toRadians(cabLocation.getLatitude());
		Double lon2 = Math.toRadians(cabLocation.getLongitude());

		// great circle distance in radians, using law of cosines formula
		Double angle = Math
				.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		// each degree on a great circle of Earth is 60 nautical miles
		Double nauticalMiles = 60 * Math.toDegrees(angle);
		Double disctanceKm = STATUTE_KM_PER_NAUTICAL_MILE * nauticalMiles;

		return disctanceKm;
	}

	@Override
	public List<TripInfo> getRidesHistory(Long userId) {
		logger.info("Executing RidesDAOImpl.getRidesHistory()");
		String query = SEARCH_RIDES_BY_USER_ID;
		List<TripInfo> trips = null;
		logger.debug("Get ride history query : {} ", query);
		try {
			trips = jdbcTemplate.query(query, new Object[] { userId }, new int[] { Types.BIGINT },
					new RowMapper<TripInfo>() {
						@Override
						public TripInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
							User user = new User(userId, null, null, null, null);
							CabInfo cabInfo = new CabInfo(rs.getLong("cabId"), null, null, null, null, null);
							Location destination = new Location();
							destination.setId(rs.getLong("destinationId"));

							Location source = new Location();
							source.setId(rs.getLong("sourceId"));

							TripInfo tripInfo = new TripInfo(rs.getLong("id"), user, cabInfo, destination, source, null,
									null, rs.getTimestamp("createTs").getTime(), null, null);

							return tripInfo;
						}

					});
		} catch (DataAccessException e) {
			logger.error("Error occured while fetching ride history ");
			throw new SystemFailureException("Error occured while fetching ride history ", e);
		}
		logger.info("Exiting RidesDAOImpl.getRidesHistory()");
		return trips;
	}

	@Override
	public TripInfo bookRide(RideInDTO rideDetails) {
		logger.info("Executing RidesDAOImpl.bookRide()");
		Long trip_id = null;
		try {
			Map data = bookRideStoredProcedure.bookRide(rideDetails);
			trip_id = (Long) data.get("t_trip_Id");
		} catch (DataAccessException e) {
			logger.error("Ride not booked. Invaid parameters in ride details");
			throw new TripInfoNotFoundException("Ride not booked. Invaid parameters in ride details for user id :"+rideDetails.getUserId());
		}
		logger.info("Exiting RidesDAOImpl.bookRide()");
		return new TripInfo(trip_id, null, null, null, null, null, null, null, null, null);
	}

	@Override
	public List<CabInfo> getCabInfo(List<Long> cabIds) {
		logger.info("Executing RidesDAOImpl.getCabInfo()");
		QueryParams queryParams = buildQuery(cabIds, SEARCH_CABS_BY_IDS);
		Map<Long, CabInfo> map = new HashMap<>();
		logger.debug("Get cab Info by ids query : {} ", queryParams.getQuery());
		try {
			jdbcTemplate.query(queryParams.getQuery().toString(), queryParams.getParams(), queryParams.getParamType(),
					new RowMapper<CabInfo>() {
						@Override
						public CabInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
							User driverInfo = new User(rs.getLong("driver_id"), null, null, null, null);
							CabInfo cabInfo = new CabInfo(rs.getLong("id"), CabType.valueOf(rs.getString("type")),
									rs.getString("number"), rs.getString("description"), null, driverInfo);
							map.put(cabInfo.getId(), cabInfo);
							return cabInfo;
						}

					});
		} catch (DataAccessException e) {
			logger.error("Error occured while fetching cab Info");
			throw new SystemFailureException("Error occured while fetching cab Info", e);
		}
		@SuppressWarnings("unchecked")
		List<CabInfo> finalList = (List<CabInfo>) sort(cabIds, map);
		logger.info("Exiting RidesDAOImpl.getCabInfo()");
		return finalList;
	}

	@Override
	public List<Location> getlocationsById(List<Long> locationIds) {
		logger.info("Executing RidesDAOImpl.getlocationsById()");
		QueryParams queryParams = buildQuery(locationIds, SEARCH_LOCATION_BY_IDS);
		Map<Long, Location> map = new HashMap<>();
		logger.debug("Get location by ids query : {} ", queryParams.getQuery());
		try {
			jdbcTemplate.query(queryParams.getQuery().toString(), queryParams.getParams(), queryParams.getParamType(),
					new RowMapper<Location>() {
						@Override
						public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
							Location location = new Location(rs.getLong("id"), rs.getString("name"),
									rs.getDouble("latitude"), rs.getDouble("longitude"));
							map.put(location.getId(), location);
							return location;
						}

					});
		} catch (DataAccessException e) {
			logger.error("Error occured while fetching location Info");
			throw new SystemFailureException("Error occured while fetching location Info", e);
		}
		@SuppressWarnings("unchecked")
		List<Location> finalList = (List<Location>) sort(locationIds, map);
		logger.info("Exiting RidesDAOImpl.getlocationsById()");
		return finalList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getDriverByIds(List<Long> driverIds) {
		logger.info("Executing RidesDAOImpl.getDriverByIds()");
		QueryParams queryParams = buildQuery(driverIds, SEARCH_DRIVER_BY_IDS);
		Map<Long, User> map = new HashMap<>();
		logger.debug("Get driver Info by ids query : {} ", queryParams.getQuery());
		try {
			jdbcTemplate.query(queryParams.getQuery().toString(), queryParams.getParams(), queryParams.getParamType(),
					new RowMapper<User>() {
						@Override
						public User mapRow(ResultSet rs, int rowNum) throws SQLException {
							User user = new User(rs.getLong("id"), rs.getString("first_name"),
									rs.getString("last_name"), rs.getString("phone_no"), rs.getString("email"));
							map.put(user.getId(), user);
							return user;
						}

					});
		} catch (DataAccessException e) {
			logger.error("Error occured while fetching driver Info");
			throw new SystemFailureException("Error occured while fetching driver Info", e);
		}
		List<User> finalList = (List<User>) sort(driverIds, map);
		logger.info("Exiting RidesDAOImpl.getDriverByIds()");
		return finalList;
	}

	/**
	 * GraphQL mandates sorting of output lists in order of input keys/ids so that
	 * proper association with parent is guaranteed
	 * 
	 * @param ids
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List sort(List<Long> ids, Map<Long, ?> map) {
		List finalList = new ArrayList<>(ids.size());
		for (Long id : ids) {
			finalList.add(map.get(id));
		}
		return finalList;
	}

	private QueryParams buildQuery(List<Long> ids, String baseQuery) {

		StringBuilder query = new StringBuilder();
		Object[] params = new Object[ids.size()];
		int[] paramType = new int[ids.size()];
		query.append(baseQuery);
		for (int i = 0; i < ids.size(); i++) {
			if (i > 0) {
				query.append(",");
			}
			query.append("?");
			params[i] = ids.get(i);
			paramType[i] = Types.BIGINT;
		}
		query.append(")");
		return new QueryParams(query, params, paramType);
	}

}
