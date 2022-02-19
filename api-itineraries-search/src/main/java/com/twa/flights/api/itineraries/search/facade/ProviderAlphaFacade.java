package com.twa.flights.api.itineraries.search.facade;

import java.util.Collections;
import java.util.List;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.twa.flights.api.itineraries.search.connector.ProviderAlphaConnector;
import com.twa.flights.common.dto.enums.Provider;
import com.twa.flights.common.dto.itinerary.ItineraryDTO;
import com.twa.flights.common.dto.request.AvailabilityRequestDTO;

@Component
public class ProviderAlphaFacade implements ProviderFacade {

    static final Logger LOGGER = LoggerFactory.getLogger(ProviderAlphaFacade.class);

    ProviderAlphaConnector itinerariesSearchConnector;

    @Autowired
    public ProviderAlphaFacade(ProviderAlphaConnector itinerariesSearchConnector) {
        this.itinerariesSearchConnector = itinerariesSearchConnector;
    }

    @CircuitBreaker(name = "provider-alpha")
    @RateLimiter(name = "availability-provider-alpha", fallbackMethod = "fallbackEmptyItineraries")
    public List<ItineraryDTO> availability(AvailabilityRequestDTO request) {
        LOGGER.debug("Obtain the information about the flights");
        return itinerariesSearchConnector.availability(request);
    }

    @Override
    public Provider getProvider() {
        return Provider.ALPHA;
    }

    @SuppressWarnings("unused")
    private List<ItineraryDTO> fallbackEmptyItineraries(AvailabilityRequestDTO request, Throwable ex) {
        return Collections.emptyList();
    }
}
