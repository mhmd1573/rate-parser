package com.task.rate_parser.service;

import com.task.rate_parser.model.RateRecord;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvRateSheetParser implements RateSheetParser {

    @Override
    public List<RateRecord> parse(File file) throws Exception {
        List<RateRecord> rateRecords = new ArrayList<>();

        // Create CSVReader to read the CSV file
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            String[] line;
            csvReader.skip(1); // Assuming first row is the header and skipping it

            // Define the date format pattern
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");

            // Read the CSV file line by line
            while ((line = csvReader.readNext()) != null) {
                RateRecord rateRecord = new RateRecord();
                // Assuming CSV columns: Country, Destination Code, Rate (per minute), Currency, Effective Date
                rateRecord.setCountry(line[0].trim());
                rateRecord.setDestinationCode(line[1].trim());
                rateRecord.setRate(Double.parseDouble(line[2].trim())); // Convert rate to Double
                rateRecord.setCurrency(line[3].trim());

                // Parse the effective date using the specified formatter
                LocalDate effectiveDate = LocalDate.parse(line[4].trim(), formatter);
                rateRecord.setEffectiveDate(effectiveDate);
                rateRecords.add(rateRecord);
            }
        } catch (IOException e) {
            throw new Exception("Error reading the CSV file", e);
        }
        return rateRecords;
    }
}