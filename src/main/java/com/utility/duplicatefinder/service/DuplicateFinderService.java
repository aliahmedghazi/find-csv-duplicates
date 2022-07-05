package com.utility.duplicatefinder.service;

import com.utility.duplicatefinder.dto.FileResponseDto;
import com.utility.duplicatefinder.dto.ResponseDto;
import org.apache.commons.csv.CSVRecord;

import java.util.List;

public interface DuplicateFinderService {
    ResponseDto fetchAndWriteDuplicates(String sourceFileName, String columnNumbers);
    ResponseDto getDuplicateRecord(String sourceFileName, List<Integer> columnNumbers);
    ResponseDto writeDuplicatesToCsv(List<CSVRecord> employeeIdList);
}
