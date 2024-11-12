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

}
