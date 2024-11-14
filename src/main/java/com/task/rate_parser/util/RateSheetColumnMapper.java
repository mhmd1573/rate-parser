//package com.task.rate_parser.util;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class RateSheetColumnMapper {
//
//    // Define keywords for each expected column type
//    private static final String[] COUNTRY_KEYWORDS = {"country", "nation"};
//    private static final String[] RATE_KEYWORDS = {"rate", "price", "cost"};
//    private static final String[] DESTINATION_KEYWORDS = {"destination", "code", "area"};
//    private static final String[] CURRENCY_KEYWORDS = {"currency", "cur"};
//
//
//    public Map<String, String> detectColumnMappings(List<String[]> rows) {
//        Map<String, String> columnMappings = new HashMap<>();
//        String[] headers = rows.get(0);  // Assume first row contains headers
//
//        for (int i = 0; i < headers.length; i++) {
//            String header = headers[i].toLowerCase();
//            if (matchesKeywords(header, COUNTRY_KEYWORDS)) {
//                columnMappings.put("country", headers[i]);
//            } else if (matchesKeywords(header, RATE_KEYWORDS)) {
//                columnMappings.put("rate", headers[i]);
//            } else if (matchesKeywords(header, DESTINATION_KEYWORDS)) {
//                columnMappings.put("destinationCode", headers[i]);
//            } else if (matchesKeywords(header, CURRENCY_KEYWORDS)) {
//                columnMappings.put("currency", headers[i]);
//            } else if (profileColumnData(rows, i, "rate")) {  // Example data profiling if header lacks keywords
//                columnMappings.put("rate", headers[i]);
//            }
//            // Add more conditions as needed
//        }
//        return columnMappings;
//    }
//
//    private boolean matchesKeywords(String header, String[] keywords) {
//        for (String keyword : keywords) {
//            if (header.contains(keyword)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean profileColumnData(List<String[]> rows, int colIndex, String dataType) {
//        // Basic profiling example based on data type: rate detection by numeric pattern
//        for (int i = 1; i < rows.size(); i++) {  // Skipping header row
//            String cellValue = rows.get(i)[colIndex];
//            if ("rate".equals(dataType)) {
//                try {
//                    Double.parseDouble(cellValue);  // If it parses as a Double, it may be a rate
//                    return true;
//                } catch (NumberFormatException e) {
//                    continue;
//                }
//            }
//            // Additional profiling logic for other data types can be added here
//        }
//        return false;
//    }
//}
