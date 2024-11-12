package com.task.rate_parser.service;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.task.rate_parser.model.RateRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.Arrays;
import java.util.List;


@Service
public class JsonRateSheetParser implements RateSheetParser {

    // Create ObjectMapper instance
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonRateSheetParser() {
        // Register JavaTimeModule to handle java.time.LocalDate
        objectMapper.registerModule(new JavaTimeModule());
        // Disable writing dates as timestamps, optional if you prefer date strings
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public List<RateRecord> parse(File file) throws Exception {
        return Arrays.asList(objectMapper.readValue(file, RateRecord[].class));
    }
}
