package com.utility.duplicatefinder.service;

import com.utility.duplicatefinder.dto.FileResponseDto;
import com.utility.duplicatefinder.dto.ResponseDto;
import com.utility.duplicatefinder.exception.InvalidColumnNumberException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DuplicateFinderServiceImpl implements DuplicateFinderService {

    @Autowired
    RestTemplate restTemplate;

    Logger logger = LoggerFactory.getLogger(DuplicateFinderServiceImpl.class);

    @Override
    public ResponseDto fetchAndWriteDuplicates(String sourceFileName, String columnNumbers) {
        List<Integer> integerColumnNumbers = columnNumbersToInteger(columnNumbers);
        ResponseDto responseDto = getDuplicateRecord(sourceFileName, integerColumnNumbers); //get list of duplicate strings from file
        responseDto = responseDto.getCode()==200 ? writeDuplicatesToCsv((List<CSVRecord>) responseDto.getData()) : responseDto; //write and return the duplicate list onto a new file
        return responseDto;
    }   
    
    @Override
    public ResponseDto getDuplicateRecord(String sourceFileName, List<Integer> columnNumbers) {
        ResponseDto responseDto = new ResponseDto("success",200,new ArrayList<>());
        Set<String> uniqueRecords = new HashSet<>();
        List<CSVRecord> duplicates = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(sourceFileName));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);) {
            List<CSVRecord> csvRecords = csvParser.getRecords();
            duplicates.add(csvRecords.get(0)); // add headers in duplicates list
            validateColumnNumbers(columnNumbers,csvRecords.get(0).size());
            columnNumbers = columnNumbers ==null || columnNumbers.isEmpty() ? getAllColumnNumbers(csvRecords.get(0).size()) : columnNumbers; // add all column numbers if column numbers are not specified
            //parse all records and collect duplicates
            for (CSVRecord csvRecord : csvRecords) {
                //get the given columns for current record
                List<String> columnValues = columnNumbers.stream().map(columnNumber->csvRecord.get(columnNumber-1)).collect(Collectors.toList());
                String duplicateString = columnValues.stream().collect(Collectors.joining());
                if(!uniqueRecords.add(duplicateString)) {
                    duplicates.add(csvRecord);
                }
            }
            responseDto.setData(duplicates);
        } catch (IOException | IndexOutOfBoundsException | InvalidColumnNumberException exception) {
            responseDto = new ResponseDto("failure",500,exception.getMessage());
            logger.error(exception.getLocalizedMessage());
        }
        return responseDto;
    }
    
    @Override
    public ResponseDto writeDuplicatesToCsv(List<CSVRecord> duplicateList) {
        ResponseDto responseDto = null;
        try (BufferedWriter duplicateWriter = Files.newBufferedWriter(Paths.get("duplicates.csv"));
             CSVPrinter csvPrinter = new CSVPrinter(duplicateWriter, CSVFormat.DEFAULT)){
            //write all duplicate records to output stream
            for(CSVRecord duplicateRecord : duplicateList) {
                csvPrinter.printRecord(duplicateRecord);
            }
            csvPrinter.flush();
            responseDto = new ResponseDto("success",200,Paths.get("duplicates.csv"));
        } catch (IOException exception) {
            responseDto = new ResponseDto("failure",500,exception.getMessage());
            logger.error(exception.getMessage());
         }
        return responseDto;
    }
    
    private List<Integer> getAllColumnNumbers(int columnSize) {
        return IntStream.rangeClosed(1, columnSize).boxed().collect(Collectors.toList());
    }

    private void validateColumnNumbers(List<Integer> columnNumbers, int columnSize) throws InvalidColumnNumberException{
        if(columnNumbers.size()>0){
            List<Integer> invalidColumnNumbers = columnNumbers.stream().filter(columnNumber -> columnNumber>columnSize).collect(Collectors.toList());
            if(invalidColumnNumbers.size() > 0){
                throw new InvalidColumnNumberException("Invalid column number(s) "+invalidColumnNumbers.toString());
            }
        }
    }

    private List<Integer> columnNumbersToInteger(String columnNumbers){
        if(columnNumbers!=null && !columnNumbers.isEmpty()){
            return Arrays.asList(columnNumbers.split(",")).stream().map(columnNumber -> Integer.parseInt(columnNumber)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
