package com.utility.duplicatefinder.controller;

import com.utility.duplicatefinder.dto.FileInputDto;
import com.utility.duplicatefinder.dto.ResponseDto;
import com.utility.duplicatefinder.service.DuplicateFinderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DuplicateFinderController {

    @Autowired
    DuplicateFinderService duplicateFinderService;
    
    @GetMapping("/duplicates")
        public ResponseEntity<ResponseDto> getDuplicates(@RequestParam("sourceFileName") String sourceFileName,
                                                         @RequestParam("columnNumbers") String columnNumbers, Model model) {
        model.addAttribute("fileform", new FileInputDto());
        ResponseDto status = duplicateFinderService.fetchAndWriteDuplicates(sourceFileName,columnNumbers);
        ResponseEntity<ResponseDto> response = new ResponseEntity<>(status,HttpStatus.OK);
        response = status.getCode()==200 ? response : new ResponseEntity<>(status,HttpStatus.INTERNAL_SERVER_ERROR);
        return response;
    }

    @GetMapping("/")
    public String greetingForm(Model model) {
        model.addAttribute("fileinput", new FileInputDto());
        return "index";
    }

    @PostMapping("/duplicates")
    public String getDuplicates(@ModelAttribute FileInputDto fileinput,Model model) {
        String response = "error";
        ResponseDto status = duplicateFinderService.fetchAndWriteDuplicates(fileinput.getFileUrl(), fileinput.getColumnNumbers());
        if(status.getCode()==200){
            model.addAttribute("fileOutput", status.getData());
            response = "result";
        }
        return response;
    }

    @RequestMapping(value="/download/duplicate/file", method=RequestMethod.GET)
    @ResponseBody
    public FileSystemResource downloadFile(@RequestParam("fileOutput") String fileOutput) {
        return new  FileSystemResource(new File(fileOutput));
    }
}

