package com.task.rate_parser.service;

import com.task.rate_parser.model.RateRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExcelRateSheetParser implements RateSheetParser {

    // Define possible column names for each field (Rate, Country, Destination Code, etc.)
    private static final List<String> RATE_COLUMNS = List.of("rate", "price", "cost", "tariff", "allday");
    private static final List<String> COUNTRY_COLUMNS = List.of("country", "nation", "region");
    private static final List<String> DESTINATION_CODE_COLUMNS = List.of("destinationcode", "destination_code", "dest_code");
    private static final List<String> CURRENCY_COLUMNS = List.of("currency", "curr");
    private static final List<String> EFFECTIVE_DATE_COLUMNS = List.of("effectivedate", "effective_date", "date");

    // New column name lists for added fields
    private static final List<String> OPERATOR_COLUMNS = List.of("operator", "service_operator", "network_operator");
    private static final List<String> CARRIER_COLUMNS = List.of("carrier", "provider", "network");
    private static final List<String> RATE_TYPE_COLUMNS = List.of("rate_type", "rate category", "type", "rate_class", "pricing_type");
    private static final List<String> MCC_COLUMNS = List.of("mcc", "country_code_mcc", "mobile_country_code");
    private static final List<String> MNC_COLUMNS = List.of("mnc", "network_code", "mobile_network_code");
    private static final List<String> CALL_TYPE_COLUMNS = List.of("call_type", "call_category", "type", "service_type", "connection_type");
    private static final List<String> SMS_TYPE_COLUMNS = List.of("sms_type", "message_type", "sms_category", "sms_class", "msg_type");
    private static final List<String> DISCOUNT_SURCHARGE_COLUMNS = List.of("discount", "surcharge", "additional_cost", "extra_charge", "markup", "modifier");


    @Override
    public List<RateRecord> parse(File file) throws Exception {
        List<RateRecord> rateRecords = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file)) {
            Workbook workbook = getWorkbook(fis, file.getName());
            Sheet sheet = workbook.getSheetAt(0); // Read the first sheet

            // Get the header row (usually the first row in Excel)
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = buildColumnMap(headerRow); // Map columns dynamically

            // Iterate through each row of the Excel sheet starting from the second row (index 1)
            for (int i = 1; i <= sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {

                    RateRecord rateRecord = new RateRecord();

                    // Use dynamic mapping for each column based on header name
                    rateRecord.setCountry(getValueFromColumn(row, columnMap, COUNTRY_COLUMNS));
                    rateRecord.setDestinationCode(getValueFromColumn(row, columnMap, DESTINATION_CODE_COLUMNS));
                    rateRecord.setCarrier(getValueFromColumn(row, columnMap, CARRIER_COLUMNS));
                    rateRecord.setOperator(getValueFromColumn(row, columnMap, OPERATOR_COLUMNS));
                    rateRecord.setRateType(getValueFromColumn(row, columnMap, RATE_TYPE_COLUMNS));
                    rateRecord.setMcc(getValueFromColumn(row, columnMap, MCC_COLUMNS));
                    rateRecord.setMnc(getValueFromColumn(row, columnMap, MNC_COLUMNS));
                    rateRecord.setCallType(getValueFromColumn(row, columnMap, CALL_TYPE_COLUMNS));
                    rateRecord.setSmsType(getValueFromColumn(row, columnMap, SMS_TYPE_COLUMNS));
                    rateRecord.setDiscountOrSurchargeInfo(getValueFromColumn(row, columnMap, DISCOUNT_SURCHARGE_COLUMNS));
                    rateRecord.setCurrency(getValueFromColumn(row, columnMap, CURRENCY_COLUMNS));
                    rateRecord.setEffectiveDate(parseDate(getValueFromColumn(row, columnMap, EFFECTIVE_DATE_COLUMNS)));

                    // Check for rate-related columns (Rate, Price, Cost, etc.)
                    Double rate = parseRateFromPossibleColumns(row, columnMap);
                    rateRecord.setRate(rate);

                    rateRecords.add(rateRecord);

                }
            }
        } catch (IOException e) {
            throw new Exception("Error reading the Excel file", e);
        }
        return rateRecords;
    }

    // Get the appropriate Workbook (HSSFWorkbook for .xls, XSSFWorkbook for .xlsx)
    private Workbook getWorkbook(FileInputStream fis, String fileName) throws IOException {
        if (fileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(fis); // For .xlsx files
        } else if (fileName.endsWith(".xls")) {
            return new HSSFWorkbook(fis); // For .xls files
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
    }

    // Build a map of column names to their index positions
    private Map<String, Integer> buildColumnMap(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            String columnName = headerRow.getCell(i).getStringCellValue();
            String normalizedHeader = normalizeColumnName(columnName);
            columnMap.put(normalizedHeader, i);
        }
        return columnMap;
    }

    // Normalize column name: trim spaces, convert to lowercase, replace spaces with underscores
    private String normalizeColumnName(String columnName) {
        return columnName.trim().toLowerCase().replace(" ", "_");
    }

    // Retrieve the value from the first valid column found in the given list of column names
    private String getValueFromColumn(Row row, Map<String, Integer> columnMap, List<String> possibleColumns) {
        for (String columnName : possibleColumns) {
            String normalizedColumnName = normalizeColumnName(columnName); // Normalize column name
            Integer index = columnMap.get(normalizedColumnName);
            if (index != null) {
                Cell cell = row.getCell(index);
                if (cell != null) {
                    return cell.toString().trim(); // Return the value if found
                }
            }
        }
        return ""; // Return empty string if no valid column is found
    }

    // Try to parse the rate from multiple possible columns (Rate, Price, Cost, etc.)
    private Double parseRateFromPossibleColumns(Row row, Map<String, Integer> columnMap) {
        for (String rateColumn : RATE_COLUMNS) {
            String normalizedRateColumn = normalizeColumnName(rateColumn); // Normalize rate column name
            String rateValue = getValueFromColumn(row, columnMap, List.of(normalizedRateColumn));
            if (!rateValue.isEmpty()) {
                return parseRate(rateValue); // Return the parsed rate if found
            }
        }
        return 0.0; // Default to 0.0 if no valid rate column found
    }

    // Parse the rate value (e.g., from a string to Double)
    private Double parseRate(String rateString) {
        try {
            return Double.parseDouble(rateString);
        } catch (NumberFormatException e) {
            return 0.0; // Default rate if parsing fails
        }
    }

    // Parse the effective date from a string (formatted as M/d/yyyy)
    private LocalDate parseDate(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            return LocalDate.now(); // Default to current date if parsing fails
        }
    }
}
