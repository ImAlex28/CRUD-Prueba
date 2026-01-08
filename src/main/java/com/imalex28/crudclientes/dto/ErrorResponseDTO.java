package com.imalex28.crudclientes.dto;

public class ErrorResponseDTO {
    public int code;
    public String message;

    public ErrorResponseDTO(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

