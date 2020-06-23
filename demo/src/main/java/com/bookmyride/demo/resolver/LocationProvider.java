package com.bookmyride.demo.resolver;

import java.util.Random;

import org.springframework.stereotype.Component;

import com.bookmyride.demo.model.Location;

/**
 * 
 * @author seeya.wamane
 *
 */
@Component
public class LocationProvider {

    private static LocationProvider instance = null;

    private LocationProvider() {

    }
    public static LocationProvider getInstance() {
        if (instance == null) {
            instance = new LocationProvider();
        }

        return instance;
    }
    
    public static Location getLocation() {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = 10000 / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(Math.toRadians(20.075588));

        double foundLongitude = new_x + 73.790893;
        double foundLatitude = y + 20.075588;
        return new Location(null,foundLatitude,foundLongitude);
    }
    
    public Location getCurrentLocation() {
        return getLocation();
    }
}
