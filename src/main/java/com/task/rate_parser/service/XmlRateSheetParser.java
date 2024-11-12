package com.task.rate_parser.service;

import com.task.rate_parser.model.RateRecord;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class XmlRateSheetParser implements RateSheetParser {

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
                RateRecord record = new RateRecord();
                record.setCountry(element.getElementsByTagName("Country").item(0).getTextContent());
                record.setDestinationCode(element.getElementsByTagName("DestinationCode").item(0).getTextContent());
                record.setRate(Double.parseDouble(element.getElementsByTagName("Rate").item(0).getTextContent()));
                record.setCurrency(element.getElementsByTagName("Currency").item(0).getTextContent());
                record.setEffectiveDate(LocalDate.parse(element.getElementsByTagName("EffectiveDate").item(0).getTextContent()));
                rateRecords.add(record);
            }
        }
        return rateRecords;
    }
}
