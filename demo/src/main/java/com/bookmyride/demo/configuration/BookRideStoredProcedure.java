package com.bookmyride.demo.configuration;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.bookmyride.demo.model.RideInDTO;

/**
 * Stored procedure configuration
 * @author seeya.wamane
 *
 */
@Configuration
public class BookRideStoredProcedure extends StoredProcedure {
	
	
	public BookRideStoredProcedure(JdbcTemplate jdbcTemplate) {
		 super(jdbcTemplate, "bookRide");
		 
		 declareParameter(new SqlParameter("t_user_id", Types.BIGINT));
		 declareParameter(new SqlParameter("t_cab_id", Types.BIGINT));
		 declareParameter(new SqlParameter("t_s_name", Types.VARCHAR));
		 declareParameter(new SqlParameter("t_s_lati", Types.DOUBLE));
		 declareParameter(new SqlParameter("t_s_longi", Types.DOUBLE));
		 declareParameter(new SqlParameter("t_d_name", Types.VARCHAR));
		 declareParameter(new SqlParameter("t_d_lati", Types.DOUBLE));
		 declareParameter(new SqlParameter("t_d_longi", Types.DOUBLE));
		 declareParameter(new SqlParameter("t_current_ts", Types.TIMESTAMP));
		 
		 declareParameter(new SqlOutParameter("t_trip_Id",
				 Types.BIGINT));

				 
		 compile();
	}

	@SuppressWarnings("unchecked")
	public Map bookRide(RideInDTO rideDetails) {

		 Map inParameters = new HashMap();
		 inParameters.put("t_user_id", rideDetails.getUserId());
		 inParameters.put("t_cab_id", rideDetails.getCabId());
		 inParameters.put("t_s_name", rideDetails.getSource().getName());
		 inParameters.put("t_s_lati", rideDetails.getSource().getLatitude());
		 inParameters.put("t_s_longi", rideDetails.getSource().getLongitude());
		 inParameters.put("t_d_name", rideDetails.getDestination().getName());
		 inParameters.put("t_d_lati", rideDetails.getDestination().getLatitude());
		 inParameters.put("t_d_longi", rideDetails.getDestination().getLongitude());
		 inParameters.put("t_current_ts", new Timestamp(new Date().getTime()));

		 Map out = execute(inParameters); 

		 return out;
		 }
	
}
