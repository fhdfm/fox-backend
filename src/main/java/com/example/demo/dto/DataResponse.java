package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.Date;

import com.example.demo.util.FoxUtils;

public class DataResponse {
    
    private Date inicio;
    private Date fim;

    public DataResponse() {
    }

    public DataResponse(LocalDateTime inicio, LocalDateTime fim) {
        this.inicio = FoxUtils.convertLocalDateTimeToDate(inicio);
        this.fim = FoxUtils.convertLocalDateTimeToDate(fim);
    }

    public Date getInicio() {
        return inicio;
    }

    public Date getFim() {
        return fim;
    }

}
