package com.task.rate_parser.service;

import com.task.rate_parser.model.RateRecord;

import java.io.File;
import java.util.List;


public interface RateSheetParser {
    List<RateRecord> parse(File file) throws Exception;
}
