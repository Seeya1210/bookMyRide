package com.bookmyride.demo.resolver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bookmyride.demo.dao.RidesDAO;
import com.bookmyride.demo.model.CabInfo;
import com.bookmyride.demo.model.Location;
import com.bookmyride.demo.model.LocationInDTO;
import com.bookmyride.demo.model.RideInDTO;
import com.bookmyride.demo.model.TripInfo;
import com.bookmyride.demo.model.User;

import graphql.execution.ExecutionContext;
import graphql.language.Field;
import graphql.language.Node;
import graphql.schema.DataFetcher;

/**
 * 
 * @author seeya.wamane
 *
 */
@Component
public class GraphQLDataFetchers {

	@Autowired
	RidesDAO ridesDAO;

	public DataFetcher<List<CabInfo>> getSearchRidesDataFetcher() {
		return searchRidesDataFetcher;
	}

	public DataFetcher<List<TripInfo>> getRideHistoryDataFetcher() {
		return rideHistoryDataFetcher;
	}

	public DataFetcher<TripInfo> getBookRideDataFetcher() {
		return bookRideDataFetcher;
	}
	
	public DataFetcher<CompletableFuture<CabInfo>> getCabInfoDataFetcher() {
		return cabInfoDataFetcher;
	}
	
	public DataFetcher<CompletableFuture<Location>> getDestinationLocationDataFetcher() {
		return destiLocationDataFetcher;
	}
	
	public DataFetcher<CompletableFuture<Location>> getSourceLocationDataFetcher() {
		return sourceLoccationDataFetcher;
	}
	
	public DataFetcher<CompletableFuture<User>> getDriverInfoDataFetcher() {
		return driverInfoDataFetcher;
	}
	
	private DataFetcher<CompletableFuture<CabInfo>> cabInfoDataFetcher = environment -> {

		TripInfo tripInfo = environment.getSource();
		Long key = tripInfo.getCabInfo().getId();
		ExecutionContext executionContext = environment.getExecutionContext();
		DataLoaderRegistry dataLoaderRegistry = executionContext.getDataLoaderRegistry();
        DataLoader<Long, CabInfo> countryLoader = dataLoaderRegistry.getDataLoader("cabInfoDataLoader");
		return countryLoader.load(key);

	};
	
	private DataFetcher<CompletableFuture<Location>> destiLocationDataFetcher = environment -> {

		TripInfo tripInfo = environment.getSource();
		Long destinationId = tripInfo.getDestination().getId();
		ExecutionContext executionContext = environment.getExecutionContext();
		DataLoaderRegistry dataLoaderRegistry = executionContext.getDataLoaderRegistry();
        DataLoader<Long, Location> destinationLocationDataLoader = dataLoaderRegistry.getDataLoader("destinationLocationDataLoader");
		return destinationLocationDataLoader.load(destinationId);

	};
	
	private DataFetcher<CompletableFuture<User>> driverInfoDataFetcher = environment -> {

		CabInfo cabInfo = environment.getSource();
		Long driverId = cabInfo.getDriverInfo().getId();
		ExecutionContext executionContext = environment.getExecutionContext();
		DataLoaderRegistry dataLoaderRegistry = executionContext.getDataLoaderRegistry();
        DataLoader<Long, User> driverInfoDataLoader = dataLoaderRegistry.getDataLoader("driverInfoDataLoader");
		return driverInfoDataLoader.load(driverId);
	};
	
	private DataFetcher<CompletableFuture<Location>> sourceLoccationDataFetcher = environment -> {

		TripInfo tripInfo = environment.getSource();
		Long sourceId = tripInfo.getSource().getId();
		ExecutionContext executionContext = environment.getExecutionContext();
		DataLoaderRegistry dataLoaderRegistry = executionContext.getDataLoaderRegistry();
        DataLoader<Long, Location> sourceLocationDataLoader = dataLoaderRegistry.getDataLoader("sourceLocationDataLoader");
		return sourceLocationDataLoader.load(sourceId);
	};
	

	private DataFetcher<List<CabInfo>> searchRidesDataFetcher = environment -> {

		List<String> requestedAttributes = getRequestedAttributes(environment.getFields());
		Map<String, Object> arguments = environment.getArguments();
		@SuppressWarnings("unchecked")
		Map<String, Object> locationInput = (Map<String, Object>) arguments.get("location");
		BigDecimal latitude = (BigDecimal) locationInput.get("latitude");
		BigDecimal longitude = (BigDecimal) locationInput.get("longitude");
		LocationInDTO location = new LocationInDTO(latitude.doubleValue(), longitude.doubleValue(),null);
		List<CabInfo> cabsList = ridesDAO.searchNearestRides(requestedAttributes, location);
		Collections.sort(cabsList, new SortByDistance());
		return cabsList;

	};

	private class SortByDistance implements Comparator<CabInfo> {

		@Override
		public int compare(CabInfo cabInfo1, CabInfo cabInfo2) {
			return cabInfo1.getDistance().compareTo(cabInfo2.getDistance());
		}

	}

	private DataFetcher<List<TripInfo>> rideHistoryDataFetcher = environment -> {
	//	List<String> requestedAttributes = getRequestedAttributes(environment.getFields());
		Map<String, Object> arguments = environment.getArguments();
		String userId = (String) arguments.get("userId");;

		return ridesDAO.getRidesHistory(Long.parseLong(userId));

	};
	
	private DataFetcher<TripInfo> bookRideDataFetcher = environment -> {

		Map<String, Object> arguments = environment.getArguments();

		@SuppressWarnings("unchecked")
		Map<String, Object> tripInfo = (Map<String, Object>) arguments.get("tripInfo");
		Map<String, Object> sourceLocation = (Map<String, Object>) tripInfo.get("source");
		BigDecimal sourceLati = (BigDecimal) sourceLocation.get("latitude");
		BigDecimal sourceLongi = (BigDecimal) sourceLocation.get("longitude");
		String sourcename = (String) sourceLocation.get("name");

		String userId = (String) tripInfo.get("userId");
		String cabId = (String) tripInfo.get("cabId");
		LocationInDTO source = new LocationInDTO(sourceLati.doubleValue(), sourceLongi.doubleValue(),sourcename);
		@SuppressWarnings("unchecked")
		Map<String, Object> destinationLoc = (Map<String, Object>) tripInfo.get("destination");
		BigDecimal destLati = (BigDecimal) destinationLoc.get("latitude");
		BigDecimal destLongi = (BigDecimal) destinationLoc.get("longitude");
		String destiname = (String) destinationLoc.get("name");
		LocationInDTO destination = new LocationInDTO(destLati.doubleValue(), destLongi.doubleValue(),destiname);
		RideInDTO rideDetails = new RideInDTO(Long.parseLong(userId), Long.parseLong(cabId), destination, source);
		return ridesDAO.bookRide(rideDetails);
	};

	@SuppressWarnings("rawtypes")
	private List<String> getRequestedAttributes(List<Field> fields) {
		List<String> argumentsToFetch = new ArrayList<>();
		// Fetch selection fields
		for (Field field : fields) {
			List<Node> selectionField = field.getSelectionSet().getChildren();
			for (Node selection : selectionField) {
				if (selection instanceof Field) {
					Field requestField = (Field) selection;
					argumentsToFetch.add(requestField.getName());
				}
			}
		}
		return argumentsToFetch;
	}

	
	public BatchLoader<Long, CabInfo> cabInfoBatchLoader() {
        return cabIds ->
          CompletableFuture.supplyAsync(() -> {
                List<CabInfo> cabs = ridesDAO.getCabInfo(cabIds);
                return cabs;
            }
          );
    }
	
	public BatchLoader<Long, Location> destinationBatchLoader() {
        return locationIds ->
          CompletableFuture.supplyAsync(() -> {
                List<Location> destinations = ridesDAO.getlocationsById(locationIds);
                return destinations;
            }
          );
    }
	
	public BatchLoader<Long, Location> sourceBatchLoader() {
        return locationIds ->
          CompletableFuture.supplyAsync(() -> {
                List<Location> sources = ridesDAO.getlocationsById(locationIds);
                return sources;
            }
          );
    }
	
	public BatchLoader<Long, User> driverBatchLoader() {
        return driverIds ->
          CompletableFuture.supplyAsync(() -> {
                List<User> driverInfo = ridesDAO.getDriverByIds(driverIds);
                return driverInfo;
            }
          );
    }

	
}
