package com.utility.duplicatefinder.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@NoArgsConstructor
@Getter @Setter
public class FileInputDto {
    private String fileUrl;
    private String columnNumbers;
}
