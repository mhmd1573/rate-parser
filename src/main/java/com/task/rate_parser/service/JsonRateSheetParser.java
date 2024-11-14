package com.task.rate_parser.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.task.rate_parser.model.RateRecord;
import org.springframework.stereotype.Service;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class JsonRateSheetParser implements RateSheetParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonRateSheetParser() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

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
        JsonNode rootNode = objectMapper.readTree(file); // Read the entire JSON

        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                RateRecord rateRecord = new RateRecord();

                rateRecord.setCountry(getFieldValue(node, COUNTRY_COLUMNS));
                rateRecord.setDestinationCode(getFieldValue(node, DESTINATION_CODE_COLUMNS));
                rateRecord.setCarrier(getFieldValue(node, CARRIER_COLUMNS));
                rateRecord.setOperator(getFieldValue(node, OPERATOR_COLUMNS));
                rateRecord.setRateType(getFieldValue(node, RATE_TYPE_COLUMNS));
                rateRecord.setMcc(getFieldValue(node, MCC_COLUMNS));
                rateRecord.setMnc(getFieldValue(node, MNC_COLUMNS));
                rateRecord.setCallType(getFieldValue(node, CALL_TYPE_COLUMNS));
                rateRecord.setSmsType(getFieldValue(node, SMS_TYPE_COLUMNS));
                rateRecord.setDiscountOrSurchargeInfo(getFieldValue(node, DISCOUNT_SURCHARGE_COLUMNS));
                rateRecord.setCurrency(getFieldValue(node, CURRENCY_COLUMNS));
                rateRecord.setEffectiveDate(parseDate(getFieldValue(node, EFFECTIVE_DATE_COLUMNS)));
                rateRecord.setRate(parseRate(getFieldValue(node, RATE_COLUMNS)));

                rateRecords.add(rateRecord);
            }
        }
        return rateRecords;
    }

    // Retrieve the value from the first valid field found in the given list of field names
    private String getFieldValue(JsonNode node, List<String> possibleFields) {
        for (String fieldName : possibleFields) {
            JsonNode fieldNode = node.get(fieldName);
            if (fieldNode != null) {
                return fieldNode.asText().trim(); // Return value if field found
            }
        }
        return ""; // Return empty string if no valid field is found
    }

    private Double parseRate(String rateString) {
        try {
            return Double.parseDouble(rateString);
        } catch (NumberFormatException e) {
            return 0.0; // Default value if parsing fails
        }
    }

    private LocalDate parseDate(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            return LocalDate.now(); // Default date if parsing fails
        }
    }
}
