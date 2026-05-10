package com.FlightArrange.FlightArrange.service;

import com.FlightArrange.FlightArrange.model.FlightReliability;
import com.FlightArrange.FlightArrange.repository.FlightReliabilityRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightScoreServiceTest {

    @Mock
    private FlightReliabilityRepo reliabilityRepo;

    @Mock
    private SerpApiService serpApiService;

    @InjectMocks
    private FlightScoreService flightScoreService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // --- Test 1: Basic scoring returns results ---
    @Test
    void scoreFlight_returnsResults_whenFlightsExist() throws Exception {
        // Arrange - set up mock Serpapi to return one flight
        List<SerpApiService.SerpFlight> mockFlights = List.of(
            new SerpApiService.SerpFlight(
                "DL401", "Delta Air Lines", "DL",
                187.0, 0,
                "2026-05-04T08:00:00",
                "2026-05-04T09:11:00",
                71
            )
        );
        when(serpApiService.getFlights("ATL", "ORD", "")).thenReturn(mockFlights);

        // Set up mock BTS data for Delta at ATL
        FlightReliability reliability = new FlightReliability();
        reliability.setOnTimeRate(0.83);
        when(reliabilityRepo.findByCarrierAndAirport("DL", "ATL"))
            .thenReturn(List.of(reliability));

        // Act
        String result = flightScoreService.scoreFlight("ATL", "ORD", "");

        // Assert
        assertNotNull(result);
        assertFalse(result.contains("error"));
        JsonNode json = objectMapper.readTree(result);
        assertTrue(json.isArray());
        assertEquals(1, json.size());
        assertEquals("DL401", json.get(0).get("flight").asText());
    }

    // --- Test 2: Score is between 0 and 1 ---
    @Test
    void scoreFlight_scoreIsBetweenZeroAndOne() throws Exception {
        List<SerpApiService.SerpFlight> mockFlights = List.of(
            new SerpApiService.SerpFlight(
                "AA202", "American Airlines", "AA",
                210.0, 0,
                "2026-05-04T09:30:00",
                "2026-05-04T10:45:00",
                75
            )
        );
        when(serpApiService.getFlights("ATL", "ORD", "")).thenReturn(mockFlights);

        FlightReliability reliability = new FlightReliability();
        reliability.setOnTimeRate(0.68);
        when(reliabilityRepo.findByCarrierAndAirport("AA", "ATL"))
            .thenReturn(List.of(reliability));

        String result = flightScoreService.scoreFlight("ATL", "ORD", "");
        JsonNode json = objectMapper.readTree(result);

        double score = json.get(0).get("score").asDouble();
        assertTrue(score >= 0.0 && score <= 1.0,
            "Score should be between 0 and 1 but was: " + score);
    }

    // --- Test 3: Higher reliability = higher score when price is equal ---
    @Test
    void scoreFlight_higherReliabilityScoresHigher_whenPriceEqual() throws Exception {
        List<SerpApiService.SerpFlight> mockFlights = List.of(
            new SerpApiService.SerpFlight(
                "DL401", "Delta Air Lines", "DL",
                200.0, 0,
                "2026-05-04T08:00:00",
                "2026-05-04T09:11:00",
                71
            ),
            new SerpApiService.SerpFlight(
                "AA202", "American Airlines", "AA",
                200.0, 0,
                "2026-05-04T09:30:00",
                "2026-05-04T10:45:00",
                71
            )
        );
        when(serpApiService.getFlights("ATL", "ORD", "")).thenReturn(mockFlights);

        // Delta has better on-time rate
        FlightReliability deltaReliability = new FlightReliability();
        deltaReliability.setOnTimeRate(0.83);
        when(reliabilityRepo.findByCarrierAndAirport("DL", "ATL"))
            .thenReturn(List.of(deltaReliability));

        // American has worse on-time rate
        FlightReliability aaReliability = new FlightReliability();
        aaReliability.setOnTimeRate(0.68);
        when(reliabilityRepo.findByCarrierAndAirport("AA", "ATL"))
            .thenReturn(List.of(aaReliability));

        String result = flightScoreService.scoreFlight("ATL", "ORD", "");
        JsonNode json = objectMapper.readTree(result);

        double deltaScore = json.get(0).get("score").asDouble();
        double aaScore    = json.get(1).get("score").asDouble();

        assertTrue(deltaScore > aaScore,
            "Delta should score higher than American when price is equal");
    }

    // --- Test 4: Results are sorted highest score first ---
    @Test
    void scoreFlight_resultsAreSortedByScoreDescending() throws Exception {
        List<SerpApiService.SerpFlight> mockFlights = List.of(
            new SerpApiService.SerpFlight(
                "AA202", "American Airlines", "AA",
                300.0, 0,
                "2026-05-04T09:30:00",
                "2026-05-04T10:45:00",
                75
            ),
            new SerpApiService.SerpFlight(
                "DL401", "Delta Air Lines", "DL",
                150.0, 0,
                "2026-05-04T08:00:00",
                "2026-05-04T09:11:00",
                71
            )
        );
        when(serpApiService.getFlights("ATL", "ORD", "")).thenReturn(mockFlights);

        FlightReliability reliability = new FlightReliability();
        reliability.setOnTimeRate(0.80);
        when(reliabilityRepo.findByCarrierAndAirport(any(), any()))
            .thenReturn(List.of(reliability));

        String result = flightScoreService.scoreFlight("ATL", "ORD", "");
        JsonNode json = objectMapper.readTree(result);

        double firstScore  = json.get(0).get("score").asDouble();
        double secondScore = json.get(1).get("score").asDouble();

        assertTrue(firstScore >= secondScore,
            "Results should be sorted highest score first");
    }

    // --- Test 5: Default on-time rate when no BTS data exists ---
    @Test
    void scoreFlight_usesDefaultOnTimeRate_whenNoBTSDataFound() throws Exception {
        List<SerpApiService.SerpFlight> mockFlights = List.of(
            new SerpApiService.SerpFlight(
                "WN1234", "Southwest Airlines", "WN",
                156.0, 0,
                "2026-05-04T11:00:00",
                "2026-05-04T12:15:00",
                75
            )
        );
        when(serpApiService.getFlights("ATL", "ORD", "")).thenReturn(mockFlights);

        // No BTS data for this carrier/airport combo
        when(reliabilityRepo.findByCarrierAndAirport("WN", "ATL"))
            .thenReturn(Collections.emptyList());

        String result = flightScoreService.scoreFlight("ATL", "ORD", "");
        JsonNode json = objectMapper.readTree(result);

        double onTimeRate = json.get(0).get("onTimeRate").asDouble();
        assertEquals(0.75, onTimeRate,
            "Should use default 0.75 on-time rate when no BTS data found");
    }

    // --- Test 6: Empty flight list returns error ---
    @Test
    void scoreFlight_returnsError_whenNoFlightsFound() throws Exception {
        when(serpApiService.getFlights("XYZ", "ABC", ""))
            .thenReturn(Collections.emptyList());

        String result = flightScoreService.scoreFlight("XYZ", "ABC", "");

        assertTrue(result.contains("error"),
            "Should return error message when no flights found");
    }

    // --- Test 7: Non-US carriers are filtered out ---
    @Test
    void scoreFlight_filtersOutNonUSCarriers() throws Exception {
        List<SerpApiService.SerpFlight> mockFlights = List.of(
            new SerpApiService.SerpFlight(
                "LH123", "Lufthansa", "LH",  // international - should be filtered
                200.0, 0,
                "2026-05-04T08:00:00",
                "2026-05-04T09:11:00",
                71
            ),
            new SerpApiService.SerpFlight(
                "DL401", "Delta Air Lines", "DL",  // US carrier - should pass
                200.0, 0,
                "2026-05-04T08:00:00",
                "2026-05-04T09:11:00",
                71
            )
        );
        when(serpApiService.getFlights("ATL", "ORD", "")).thenReturn(mockFlights);

        FlightReliability reliability = new FlightReliability();
        reliability.setOnTimeRate(0.83);
        when(reliabilityRepo.findByCarrierAndAirport("DL", "ATL"))
            .thenReturn(List.of(reliability));

        String result = flightScoreService.scoreFlight("ATL", "ORD", "");
        JsonNode json = objectMapper.readTree(result);

        assertEquals(1, json.size(),
            "Should only return 1 flight after filtering out Lufthansa");
        assertEquals("DL401", json.get(0).get("flight").asText());
    }
}