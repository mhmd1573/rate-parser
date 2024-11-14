package com.task.rate_parser.service;


import com.task.rate_parser.model.RateRecord;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
public class XmlRateSheetParser implements RateSheetParser {

    // Define possible tag names for each field
    private static final List<String> RATE_TAGS = List.of("Rate", "Price", "Cost", "Tariff");
    private static final List<String> COUNTRY_TAGS = List.of("Country", "Nation", "Region");
    private static final List<String> DESTINATION_CODE_TAGS = List.of("DestinationCode", "DestCode", "Destination_Code");
    private static final List<String> CURRENCY_TAGS = List.of("Currency", "Curr");
    private static final List<String> EFFECTIVE_DATE_TAGS = List.of("EffectiveDate", "Date");

    // New column name lists for added fields
    private static final List<String> CARRIER_COLUMNS = List.of("carrier", "provider", "network");
    private static final List<String> RATE_TYPE_COLUMNS = List.of("rate_type", "rate category", "type");
    private static final List<String> MCC_COLUMNS = List.of("mcc");
    private static final List<String> MNC_COLUMNS = List.of("mnc");
    private static final List<String> CALL_TYPE_COLUMNS = List.of("call_type", "call_category", "type");
    private static final List<String> DISCOUNT_SURCHARGE_COLUMNS = List.of("discount", "surcharge", "additional_cost");

    @Override
    public List<RateRecord> parse(File file) throws Exception {
        List<RateRecord> rateRecords = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        NodeList nodeList = document.getElementsByTagName("RateRecord");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                RateRecord rateRecord = new RateRecord();

                rateRecord.setCountry(getElementValue(element, COUNTRY_TAGS));
                rateRecord.setDestinationCode(getElementValue(element, DESTINATION_CODE_TAGS));
                rateRecord.setRate(parseRate(getElementValue(element, RATE_TAGS)));
                rateRecord.setCurrency(getElementValue(element, CURRENCY_TAGS));
                rateRecord.setEffectiveDate(parseDate(getElementValue(element, EFFECTIVE_DATE_TAGS)));

                rateRecords.add(rateRecord);
            }
        }
        return rateRecords;
    }

    // Retrieve the value from the first valid tag found in the given list of tag names
    private String getElementValue(Element element, List<String> possibleTags) {
        for (String tagName : possibleTags) {
            NodeList nodeList = element.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                return nodeList.item(0).getTextContent().trim(); // Return value if tag found
            }
        }
        return ""; // Return empty string if no valid tag is found
    }

    private Double parseRate(String rateString) {
        try {
            return Double.parseDouble(rateString);
        } catch (NumberFormatException e) {
            return 0.0; // Default rate if parsing fails
        }
    }

    private LocalDate parseDate(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            return LocalDate.now(); // Default to current date if parsing fails
        }
    }
}
