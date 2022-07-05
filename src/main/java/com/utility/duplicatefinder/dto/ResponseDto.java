package com.utility.duplicatefinder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class ResponseDto<T> {
    String message;
    Integer code;
    T data;
}
