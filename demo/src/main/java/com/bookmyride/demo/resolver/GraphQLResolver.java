package com.bookmyride.demo.resolver;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.bookmyride.demo.datafetchers.GraphQLDataFetchers;
import com.bookmyride.demo.model.CabInfo;
import com.bookmyride.demo.model.Location;
import com.bookmyride.demo.model.User;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import graphql.GraphQL;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentationOptions;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

/**
 * 
 * @author seeya.wamane
 *
 */
@Component
public class GraphQLResolver {

	private GraphQL graphQL;

	@Autowired
	GraphQLDataFetchers graphQLDataFetchers;

	private static final String SCHEMA_FILE_NAME = "\\graphql\\rides_schema.graphqls";

	@Bean
	public GraphQL graphQL() {
		return graphQL;
	}

	@PostConstruct
	public void init() throws IOException {
		URL url = Resources.getResource(SCHEMA_FILE_NAME);
		String sdl = Resources.toString(url, Charsets.UTF_8);
		GraphQLSchema schema = buildSchema(sdl);
		DataLoaderDispatcherInstrumentationOptions options = DataLoaderDispatcherInstrumentationOptions.newOptions()
				.includeStatistics(true);
		DataLoaderDispatcherInstrumentation dispatcherInstrumentation = new DataLoaderDispatcherInstrumentation(
				options);
		this.graphQL = GraphQL.newGraphQL(schema).instrumentation(dispatcherInstrumentation).build();
	}

	private GraphQLSchema buildSchema(String sdl) {
		
		TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
		RuntimeWiring runtimeWiring = buildWiring();
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
		
	}

	private RuntimeWiring buildWiring() {
		return RuntimeWiring.newRuntimeWiring()
				.type(newTypeWiring("Query")
						.dataFetcher("searchNearestRides", graphQLDataFetchers.getSearchRidesDataFetcher())
						.dataFetcher("getRideHistory", graphQLDataFetchers.getRideHistoryDataFetcher()))
				.type(newTypeWiring("Mutation").dataFetcher("bookRide", graphQLDataFetchers.getBookRideDataFetcher()))
				.type(newTypeWiring("TripInfo").dataFetcher("cabInfo", graphQLDataFetchers.getCabInfoDataFetcher())
						.dataFetcher("destination", graphQLDataFetchers.getDestinationLocationDataFetcher())
						.dataFetcher("source", graphQLDataFetchers.getSourceLocationDataFetcher()))
				.type(newTypeWiring("CabInfo")
						.dataFetcher("driverInfo", graphQLDataFetchers.getDriverInfoDataFetcher()))
				.build();
	}
	
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	// @Profile("global")
	public DataLoaderRegistry globalDataLoaderRegistry() {
		DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
		DataLoader<Long, CabInfo> cabInfoDataLoader = DataLoader
				.newDataLoader(graphQLDataFetchers.cabInfoBatchLoader());
		DataLoader<Long, Location> destinationLocationDataLoader = DataLoader
				.newDataLoader(graphQLDataFetchers.destinationBatchLoader());
		DataLoader<Long, Location> sourceLocationDataLoader = DataLoader
				.newDataLoader(graphQLDataFetchers.sourceBatchLoader());
		DataLoader<Long, User> driverInfoDataLoader = DataLoader
				.newDataLoader(graphQLDataFetchers.driverBatchLoader());
		dataLoaderRegistry.register("destinationLocationDataLoader", destinationLocationDataLoader);
		dataLoaderRegistry.register("sourceLocationDataLoader", sourceLocationDataLoader);
		dataLoaderRegistry.register("cabInfoDataLoader", cabInfoDataLoader);
		dataLoaderRegistry.register("driverInfoDataLoader", driverInfoDataLoader);
		return dataLoaderRegistry;
	}

}
