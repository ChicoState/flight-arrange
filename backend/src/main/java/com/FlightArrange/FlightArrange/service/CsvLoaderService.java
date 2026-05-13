package com.FlightArrange.FlightArrange.service;

import com.FlightArrange.FlightArrange.model.FlightReliability;
import com.FlightArrange.FlightArrange.repository.FlightReliabilityRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;

@Service
public class CsvLoaderService implements CommandLineRunner {

    private final FlightReliabilityRepo repo;

    // *** SET THIS TO YOUR ACTUAL CSV PATH ***
    private static final String CSV_PATH = "/Users/jeremybertran/Downloads/Airline_Delay_Cause.csv";

    public CsvLoaderService(FlightReliabilityRepo repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only load if table is empty - prevents duplicate loads on restart
        if (repo.count() > 0) {
            System.out.println("Flight reliability data already loaded, skipping CSV import.");
            return;
        }

        System.out.println("Loading flight reliability data from CSV...");
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header

                // Handle quoted fields (airport names have commas inside quotes)
                String[] fields = parseCsvLine(line);
                if (fields.length < 15) continue;

                try {
                    FlightReliability fr = new FlightReliability();
                    fr.setYear(Integer.parseInt(fields[0].trim()));
                    fr.setMonth(Integer.parseInt(fields[1].trim()));
                    fr.setCarrier(fields[2].trim());
                    fr.setCarrierName(fields[3].trim());
                    fr.setAirport(fields[4].trim());
                    fr.setAirportName(fields[5].trim());

                    double arrFlights = Double.parseDouble(fields[6].trim());
                    double arrDel15   = Double.parseDouble(fields[7].trim());
                    double arrCancelled = Double.parseDouble(fields[13].trim());

                    fr.setArrFlights(arrFlights);
                    fr.setArrDel15(arrDel15);
                    fr.setArrCancelled(arrCancelled);

                    // Calculate on-time rate right here on load
                    double onTimeRate = arrFlights > 0
                        ? (arrFlights - arrDel15 - arrCancelled) / arrFlights
                        : 0.0;
                    fr.setOnTimeRate(Math.max(0, onTimeRate));

                    repo.save(fr);
                    count++;
                } catch (NumberFormatException e) {
                    System.out.println("Skipping malformed row: " + line);
                }
            }
        }
        System.out.println("Loaded " + count + " flight reliability records.");
    }

    // Handles CSV lines where fields may be wrapped in quotes containing commas
    private String[] parseCsvLine(String line) {
        java.util.List<String> fields = new java.util.ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }
}