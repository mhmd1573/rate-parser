package com.task.rate_parser.service;

import com.opencsv.CSVReader;
import com.task.rate_parser.model.RateRecord;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsvRateSheetParser implements RateSheetParser {

    // Define possible column names for each field
    private static final List<String> RATE_COLUMNS = List.of("rate", "price", "cost", "tariff", "allday" , "fee", "charge");
    private static final List<String> COUNTRY_COLUMNS = List.of("country", "nation", "destination", "country_name", "location");
    private static final List<String> DESTINATION_CODE_COLUMNS = List.of("destinationcode", "destination_code", "dest_code", "country_code", "dial_code", "cc", "area_code", "prefix");
    private static final List<String> CURRENCY_COLUMNS = List.of("currency", "curr" , "currency_code", "money_type");
    private static final List<String> EFFECTIVE_DATE_COLUMNS = List.of("effectivedate", "effective_date", "date");

    // New column name lists for added fields
    private static final List<String> OPERATOR_COLUMNS = List.of("operator", "service_operator", "network_operator");
    private static final List<String> CARRIER_COLUMNS = List.of("carrier", "provider", "network" , "telecom");
    private static final List<String> RATE_TYPE_COLUMNS = List.of("rate_type", "rate category", "type", "rate_class", "pricing_type");
    private static final List<String> MCC_COLUMNS = List.of("mcc", "country_code_mcc", "mobile_country_code");
    private static final List<String> MNC_COLUMNS = List.of("mnc", "network_code", "mobile_network_code");
    private static final List<String> CALL_TYPE_COLUMNS = List.of("call_type", "call_category", "type", "service_type", "connection_type");
    private static final List<String> DISCOUNT_SURCHARGE_COLUMNS = List.of("discount", "surcharge", "additional_cost", "extra_charge", "markup", "modifier");
    private static final List<String> SMS_TYPE_COLUMNS = List.of("sms_type", "message_type", "sms_category", "sms_class", "msg_type");


    @Override
    public List<RateRecord> parse(File file) throws Exception {
        List<RateRecord> rateRecords = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            String[] header = csvReader.readNext(); // Read header row
            Map<String, Integer> columnMap = buildColumnMap(header); // Map columns dynamically

            String[] line;
            while ((line = csvReader.readNext()) != null) {

                RateRecord rateRecord = new RateRecord();

                // Use dynamic mapping for each column based on header name
                rateRecord.setCountry(getValueFromColumn(line, columnMap, COUNTRY_COLUMNS));
                rateRecord.setDestinationCode(getValueFromColumn(line, columnMap, DESTINATION_CODE_COLUMNS));
                rateRecord.setCarrier(getValueFromColumn(line, columnMap, CARRIER_COLUMNS));
                rateRecord.setOperator(getValueFromColumn(line, columnMap, OPERATOR_COLUMNS));
                rateRecord.setRateType(getValueFromColumn(line, columnMap, RATE_TYPE_COLUMNS));
                rateRecord.setMcc(getValueFromColumn(line, columnMap, MCC_COLUMNS));
                rateRecord.setMnc(getValueFromColumn(line, columnMap, MNC_COLUMNS));
                rateRecord.setCallType(getValueFromColumn(line, columnMap, CALL_TYPE_COLUMNS));
                rateRecord.setSmsType(getValueFromColumn(line, columnMap, SMS_TYPE_COLUMNS));
                rateRecord.setDiscountOrSurchargeInfo(getValueFromColumn(line, columnMap, DISCOUNT_SURCHARGE_COLUMNS));
                rateRecord.setCurrency(getValueFromColumn(line, columnMap, CURRENCY_COLUMNS));
                rateRecord.setEffectiveDate(parseDate(getValueFromColumn(line, columnMap, EFFECTIVE_DATE_COLUMNS)));

                // Check for rate-related columns (Rate, Price, Cost, etc.)
                Double rate = parseRateFromPossibleColumns(line, columnMap);
                rateRecord.setRate(rate);

                rateRecords.add(rateRecord);
            }
        } catch (IOException e) {
            throw new Exception("Error reading the CSV file", e);
        }
        return rateRecords;
    }



    // Build a map of column names to their index positions
    private Map<String, Integer> buildColumnMap(String[] header) {
        Map<String, Integer> columnMap = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            String normalizedHeader = normalizeColumnName(header[i]);
            columnMap.put(normalizedHeader, i);
        }
        return columnMap;
    }

    // Normalize column name: trim spaces, convert to lowercase, replace spaces with underscores
    private String normalizeColumnName(String columnName) {
        return columnName.trim().toLowerCase().replace(" ", "_");
    }

    // Retrieve the value from the first valid column found in the given list of column names
    private String getValueFromColumn(String[] line, Map<String, Integer> columnMap, List<String> possibleColumns) {
        for (String columnName : possibleColumns) {
            String normalizedColumnName = normalizeColumnName(columnName);
            Integer index = columnMap.get(normalizedColumnName);
            if (index != null && index < line.length) {
                return line[index].trim();
            }
        }
        return "";
    }

    private Double parseRateFromPossibleColumns(String[] line, Map<String, Integer> columnMap) {
        for (String rateColumn : RATE_COLUMNS) {
            String normalizedRateColumn = normalizeColumnName(rateColumn);
            String rateValue = getValueFromColumn(line, columnMap, List.of(normalizedRateColumn));
            if (!rateValue.isEmpty()) {
                return parseRate(rateValue);
            }
        }
        return 0.0;
    }

    private Double parseRate(String rateString) {
        try {
            return Double.parseDouble(rateString);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private LocalDate parseDate(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}

















