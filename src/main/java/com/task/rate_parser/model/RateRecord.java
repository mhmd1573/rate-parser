package com.task.rate_parser.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "rate_records")
public class RateRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    private String destinationCode;
    private Double rate;
    private String currency;
    private LocalDate effectiveDate;

    // New fields
    private String carrier;
    private String rateType; // e.g., Wholesale, Retail
    private String Mcc; // Mobile Country Code (MCC)
    private String Mnc;  //  Mobile Network Code (MNC)
    private String callType; // e.g., Mobile, Fixed line, VoIP
    private String smsType;
    private String discountOrSurchargeInfo;

    private String operator;


    public void setCountry(String country) {
        this.country = country;
    }

    public void setDestinationCode(String destinationCode) {
        this.destinationCode = destinationCode;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }



    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }



    public void setRateType(String rateType) {
        this.rateType = rateType;
    }



    public void setMcc(String mcc) {
        Mcc = mcc;
    }


    public void setMnc(String mnc) {
        Mnc = mnc;
    }


    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }

    public void setDiscountOrSurchargeInfo(String discountOrSurchargeInfo) {
        this.discountOrSurchargeInfo = discountOrSurchargeInfo;
    }


}
