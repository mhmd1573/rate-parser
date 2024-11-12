package com.task.rate_parser.repository;


import com.task.rate_parser.model.RateRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateRecordRepository extends JpaRepository<RateRecord, Long> {

}
