package com.twa.flights.api.clusters.service;

import java.util.List;
import java.util.stream.Collectors;

import com.twa.flights.api.clusters.helper.FlightIdGeneratorHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.twa.flights.api.clusters.dto.ClusterSearchDTO;
import com.twa.flights.api.clusters.dto.request.ClustersAvailabilityRequestDTO;
import com.twa.flights.api.clusters.enums.ExceptionStatus;
import com.twa.flights.api.clusters.exception.APIException;
import com.twa.flights.api.clusters.repository.ClustersRepository;
import com.twa.flights.common.dto.itinerary.ItineraryDTO;

@Service
public class ClustersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClustersService.class);

    private static final String BARRIER_PATH = "/barriers/search";

    private final ItinerariesSearchService itinerariesSearchService;
    private final PricingService pricingService;
    private final ClustersRepository repository;
    private final FlightIdGeneratorHelper flightIdGeneratorHelper;
    private final ZooKeeperService zooKeeperService;

    @Autowired
    public ClustersService(ItinerariesSearchService itinerariesSearchService, PricingService pricingService,
            ClustersRepository repository, FlightIdGeneratorHelper flightIdGeneratorHelper,
            ZooKeeperService zooKeeperService) {
        this.itinerariesSearchService = itinerariesSearchService;
        this.pricingService = pricingService;
        this.repository = repository;
        this.flightIdGeneratorHelper = flightIdGeneratorHelper;
        this.zooKeeperService = zooKeeperService;
    }

    public ClusterSearchDTO availability(ClustersAvailabilityRequestDTO request) {
        LOGGER.debug("begin the search");

        ClusterSearchDTO response = null;

        if (StringUtils.isEmpty(request.getId())) { // New search
            response = repository.get(flightIdGeneratorHelper.generate(request));
            if (response == null) {
                response = availabilityFromBarrierOrProvider(request);
            }
            // Limit the size
            response.setItineraries(
                    response.getItineraries().stream().limit(request.getAmount()).collect(Collectors.toList()));
        } else { // Pagination old search
            response = availabilityFromDatabase(request);
        }

        return response;
    }

    private ClusterSearchDTO availabilityFromBarrierOrProvider(ClustersAvailabilityRequestDTO request) {
        String barrierName = buildBarrierPath(request);

        if (!isBarrierCreated(barrierName)) {
            return availabilityFromProviders(request);
        }

        zooKeeperService.waitOnBarrier(barrierName);

        ClusterSearchDTO result = repository.get(flightIdGeneratorHelper.generate(request));
        if (result == null) {
            return availabilityFromProviders(request);
        }

        return repository.get(flightIdGeneratorHelper.generate(request));
    }

    private ClusterSearchDTO availabilityFromProviders(ClustersAvailabilityRequestDTO request) {
        ClusterSearchDTO response;
        List<ItineraryDTO> itineraries = itinerariesSearchService.availability(request);

        itineraries = pricingService.priceItineraries(itineraries);
        itineraries = itineraries.stream().sorted((itineraryOne, itineraryTwo) -> itineraryOne.getPriceInfo()
                .getTotalAmount().compareTo(itineraryTwo.getPriceInfo().getTotalAmount())).collect(Collectors.toList());

        response = repository.insert(request, itineraries);

        // Limit the size
        response.setItineraries(itineraries.stream().limit(request.getAmount()).collect(Collectors.toList()));
        return response;
    }

    private ClusterSearchDTO availabilityFromDatabase(ClustersAvailabilityRequestDTO request) {
        ClusterSearchDTO response;
        response = repository.get(request.getId());

        if (response == null) {
            throw new APIException(HttpStatus.BAD_GATEWAY, ExceptionStatus.SEARCH_NOT_FOUND_IN_REPOSITORY.getCode(),
                    ExceptionStatus.SEARCH_NOT_FOUND_IN_REPOSITORY.getMessage());
        }

        response.getPagination().setOffset(request.getOffset()); // Update offset

        List<ItineraryDTO> itineraries = response.getItineraries();

        long skip = request.getOffset().longValue() * request.getAmount();

        // Limit the size
        response.setItineraries(
                itineraries.stream().skip(skip).limit(request.getAmount()).collect(Collectors.toList()));
        return response;
    }

    private String buildBarrierPath(ClustersAvailabilityRequestDTO request) {
        return BARRIER_PATH + "/" + flightIdGeneratorHelper.generate(request);
    }

    private synchronized boolean isBarrierCreated(String barrierPath) {
        if (!zooKeeperService.checkIfBarrierExists(barrierPath)) {
            return zooKeeperService.createBarrier(barrierPath);
        }

        return true;
    }
}
