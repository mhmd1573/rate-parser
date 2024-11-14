package com.task.rate_parser.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

// The RateSheetParserFactory Class decides which parser to use based on the file extension (csv, json, xml ,excel)


@Service
public class RateSheetParserFactory {


    private static final Logger logger = LoggerFactory.getLogger(RateSheetParserFactory.class);
    private final Map<String, RateSheetParser> parserMap = new HashMap<>();

    public RateSheetParserFactory(CsvRateSheetParser csvParser, XmlRateSheetParser xmlParser, JsonRateSheetParser jsonParser , ExcelRateSheetParser excelParser) {
        parserMap.put("csv", csvParser);
        parserMap.put("xml", xmlParser);
        parserMap.put("json", jsonParser);
        parserMap.put("xlsx", excelParser);
    }

    public RateSheetParser getParser(File file) {
        String extension = getFileExtension(file);
        logger.info("Attempting to get parser for file extension: {}", extension);
        if (extension.isEmpty() || !parserMap.containsKey(extension)) {
            logger.error("No parser found for file extension: {}", extension);
            return null;
        }
        return parserMap.get(extension);
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf(".");
        return lastIndex == -1 ? "" : name.substring(lastIndex + 1).toLowerCase();
    }
}
