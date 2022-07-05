package com.utility.duplicatefinder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class FileResponseDto {
    String fileName;
    String downloadUrl;
    Long size;
}
